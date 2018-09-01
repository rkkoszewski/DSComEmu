/*******************************************************************************
 * Copyright (c) 2018 Robert Koszewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package com.robertkoszewski.dsce.messages;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import com.robertkoszewski.dsce.utils.DSUtils;
import com.robertkoszewski.dsce.utils.StringUtils;

/**
 * DS Low-Level Message Class
 * @author Robert Koszewski
 */
public class DSMessage {
	
	// Message Data
	// Start of Packet - is used to provide synchronization when parsing packets. Always 0xFC
	// Packet Length - Packet Length from Group Address (inclusive) to CRC (inclusive)
	private byte group_address = 0xF; // Group Address - the group number which the device belongs. 0x00 indicates �No specified Group�, 0x01 indicates group 1, 0x02 indicates group 2, etc. If the Group Address is incorrect, DreamScreen will discard the message.
	private byte flags = FLAG_BROADCAST_TO_ALL; // Flags - provides context for handling the message
	private byte command_upper; // Command Upper - specifies command namespace
	private byte command_lower; // Command Lower - specifies individual command within namespace
	private byte[] payload; // Payload - variable length, depending upon the context of the command
	// CRC - 8 bit CRC for error detection. If incorrect, DreamScreen will discard the message.
	
	// Cache
	private Command command;
	
	// Constructors
	
	/**
	 * Empty Message
	 */
	public DSMessage() {}
	
	/**
	 * Self Defined Message without Payload
	 * @param group_address
	 * @param flags
	 * @param command_upper
	 * @param command_lower
	 */
	public DSMessage(byte group_address, byte flags, byte command_upper, byte command_lower) {
		this(group_address, flags, command_upper, command_lower, null);
	}
	
	/**
	 * Self Defined Message
	 * @param group_address
	 * @param flags
	 * @param command_upper
	 * @param command_lower
	 * @param payload
	 */
	public DSMessage(byte group_address, byte flags, byte command_upper, byte command_lower, byte[] payload) {
		this.group_address = group_address;
		this.flags = flags;
		this.command_upper = command_upper;
		this.command_lower = command_lower;
		this.payload = payload;
	}
	
	/**
	 * Parse Message Data
	 * @param message
	 * @throws InvalidMessageException
	 */
	public DSMessage(byte[] message) throws InvalidMessageException {
		this(message, true);
	}

	/**
	 * Parse Message Data
	 * @param message
	 * @param validateCRC
	 * @throws InvalidMessageException
	 */
	public DSMessage(byte[] message, boolean validateCRC) throws InvalidMessageException {
		// Validate Message
		if(message.length < 7) { // Message has a minimum of 7 bytes of size
			throw new InvalidMessageException("Message length is under the minimum of 7 bytes");
		}
		
		// Validate Start of Packet
		if(message[0] != 0xFFFFFFFC) {
			throw new InvalidMessageException("Invalid start of packet. Expected was 0xFC but got 0x" + Integer.toHexString(message[0]).toUpperCase());
		}
		
		// Validate Length
		if((message[1] & 0xFF) != message.length - 2) {
			throw new InvalidMessageException("Invalid message length. Expected was " + (message[1] & 0xFF) + " bytes but got " + (message.length - 2) + " bytes");
		}
		
		// Validate CRC
		byte crc = DSUtils.calculate_crc8(message, message.length - 1);
		if(validateCRC == true && crc != message[message.length - 1]) {
			throw new InvalidMessageException("Message has invalid CRC. Expected 0x" + StringUtils.bytesToHex(message[message.length - 1]) + " but got 0x" + StringUtils.bytesToHex(crc));
		}
		
		// Process Message
		this.group_address = message[2];
		this.flags = message[3];
		this.command_upper = message[4];
		this.command_lower = message[5];
		if(message.length != 7)
			this.payload = Arrays.copyOfRange(message, 6, message.length - 1); // - 1 = Ignore CRC
	}
	
	// Methods
	
	/**
	 * Get Group Address
	 * @return
	 */
	public byte getGroupAddress() {
		return this.group_address;
	}
	
	/**
	 * Set Group Address
	 * @param group_address
	 */
	public void setGroupAddress(byte group_address) {
		this.group_address = group_address;
	}
	
	/**
	 * Get Flags
	 * @return
	 */
	public byte getFlags() {
		return this.flags;
	}
	
	/**
	 * Set Flags
	 * @param flags
	 */
	public void setFlags(byte flags) {
		this.flags = flags;
	}
	
	/**
	 * Get Command Upper
	 * @return
	 */
	public byte getCommandUpper() {
		return this.command_upper;
	}
	
	/**
	 * Set Command Upper
	 * @param command_upper
	 */
	public void setCommandUpper(byte command_upper) {
		this.command_upper = command_upper;
		this.command = null;
	}
	
	/**
	 * Get Command Lower
	 * @return
	 */
	public byte getCommandLower() {
		return this.command_lower;
	}
	
	/**
	 * Set Command Lower
	 * @param command_lower
	 */
	public void setCommandLower(byte command_lower) {
		this.command_lower = command_lower;
		this.command = null;
	}
	
	/**
	 * Get Payload
	 * @return
	 */
	public byte[] getPayload() {
		if(this.payload == null) return new byte[0];
		return this.payload;
	}
	
	/**
	 * Set Payload
	 * @param payload
	 */
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	/**
	 * Get Message
	 * @return
	 */
	public byte[] getMessage() {
		ByteArrayOutputStream message = new ByteArrayOutputStream() ;
		message.write(0xFC);
		message.write((byte) (0x05 + (this.payload == null ? 0 : this.payload.length))) ;
		message.write(this.group_address);
		message.write(this.flags);
		message.write(this.command_upper);
		message.write(this.command_lower);
		if(this.payload != null)
			for(byte b: this.payload) message.write(b) ;
		message.write(DSUtils.calculate_crc8(message.toByteArray(), message.size())) ;
		return message.toByteArray();
	}
	
	// Helpers
	
	/**
	 * Get Command
	 * @return
	 */
	public Command getCommand() {
		if(command == null) {
			// Parse Command
			command = Command.UNKNOWN;
			switch(this.command_upper) {
			
			case 0x01: // Discovery?
				switch(this.command_lower) {
					case COMMAND_LOWER_CURRENT_STATE:
						if(payload == null || payload.length == 0)
							command = Command.CURRENT_STATE_REQUEST;
						else
							command = Command.CURRENT_STATE;
						break;
				}
				break;
				
			case 0x03: // Device Commands?
				switch(this.command_lower) {
					case COMMAND_LOWER_MODE:
						command = Command.MODE;
						break;
					case COMMAND_LOWER_BRIGHTNESS:
						command = Command.BRIGHTNESS;
						break;
					case COMMAND_LOWER_AMBIENT_COLOR:
						command = Command.AMBIENT_COLOR;
						break;
					case COMMAND_LOWER_AMBIENT_MODE_TYPE:
						command = Command.AMBIENT_MODE_TYPE;
						break;
					case COMMAND_LOWER_AMBIENT_SCENE:
						command = Command.AMBIENT_SCENE;
						break;
					case COMMAND_LOWER_HDMI_INPUT:
						command = Command.HDMI_INPUT;
						break;
				}
				
				break;
			}
		}
		
		return command;
	}
	
	@Override
	public String toString() {
		return StringUtils.bytesToHex(getMessage());
	}
	
	// Static Methods
	
	/**
	 * Build direct Message
	 * @param group_address
	 * @param flags
	 * @param command_upper
	 * @param command_lower
	 * @param payload
	 * @return
	 */
	public static byte[] buildMessage(byte group_address, byte flags, byte command_upper, byte command_lower, byte[] payload) {
		ByteArrayOutputStream message = new ByteArrayOutputStream() ;
		message.write(0xFC);
		message.write((byte) (0x05 + (payload == null ? 0 : payload.length))) ;
		message.write(group_address);
		message.write(flags);
		message.write(command_upper);
		message.write(command_lower);
		for(byte b: payload) message.write(b) ;
		message.write(DSUtils.calculate_crc8(message.toByteArray(), message.size())) ;
		return message.toByteArray();
	}
	
	// Static Messages
	public static final byte[] MESSAGE_READ_CURRENT_STATE = new byte[]{(byte) 0xFC, 0x05, (byte) 0xFF, 0x30, 0x01, 0x0A, 0x2A};
	
	// Flags
	public static final byte FLAG_BROADCAST_TO_GROUP = 0b00100001;
	public static final byte FLAG_BROADCAST_TO_ALL = 0b00010001;
	
	// Current State
	public static final byte COMMAND_UPPER_CURRENT_STATE = 0x01;
	public static final byte COMMAND_LOWER_CURRENT_STATE = 0x0A;
	
	// Mode
	public static final byte COMMAND_UPPER_MODE = 0x03;
	public static final byte COMMAND_LOWER_MODE = 0x01;
	public static final byte MODE_SLEEP_PAYLOAD = 0x00;
	public static final byte MODE_VIDEO_PAYLOAD = 0x01;
	public static final byte MODE_MUSIC_PAYLOAD = 0x02;
	public static final byte MODE_AMBIENT_PAYLOAD = 0x03;
	
	// Brightness
	public static final byte COMMAND_UPPER_BRIGHTNESS = 0x03;
	public static final byte COMMAND_LOWER_BRIGHTNESS = 0x02;
	
	// Ambient Color
	public static final byte COMMAND_UPPER_AMBIENT_COLOR = 0x03;
	public static final byte COMMAND_LOWER_AMBIENT_COLOR = 0x05;

	// Ambient Mode Type
	public static final byte COMMAND_UPPER_AMBIENT_MODE_TYPE = 0x03;
	public static final byte COMMAND_LOWER_AMBIENT_MODE_TYPE = 0x08;
	public static final byte AMBIENT_MODE_TYPE_RGB_COLOR_PAYLOAD = 0x00;
	public static final byte AMBIENT_MODE_TYPE_SCENE_PAYLOAD = 0x01;
	
	// Ambient Scene
	public static final byte COMMAND_UPPER_AMBIENT_SCENE = 0x03;
	public static final byte COMMAND_LOWER_AMBIENT_SCENE = 0x0D;
	public static final byte AMBIENT_SCENE_RANDOM_COLOR = 0x00;
	public static final byte AMBIENT_SCENE_FIRESIDE_PAYLOAD = 0x01;
	public static final byte AMBIENT_SCENE_TWINKLE_PAYLOAD = 0x02;
	public static final byte AMBIENT_SCENE_OCEAN_PAYLOAD = 0x03;
	public static final byte AMBIENT_SCENE_RAINBOW_PAYLOAD = 0x04;
	public static final byte AMBIENT_SCENE_JULY4TH_PAYLOAD = 0x05;
	public static final byte AMBIENT_SCENE_HOLIDAY_PAYLOAD = 0x06;
	public static final byte AMBIENT_SCENE_POP_PAYLOAD = 0x07;
	public static final byte AMBIENT_SCENE_ENCHANTED_FOREST_PAYLOAD = 0x08;
	
	// HDMI Input
	public static final byte COMMAND_UPPER_HDMI_INPUT = 0x03;
	public static final byte COMMAND_LOWER_HDMI_INPUT = 0x20;
	public static final byte HDMI_INPUT_CHANNEL_1_PAYLOAD = 0x00;
	public static final byte HDMI_INPUT_CHANNEL_2_PAYLOAD = 0x01;
	public static final byte HDMI_INPUT_CHANNEL_3_PAYLOAD = 0x02;
	
	// Change Device Name
	public static final byte COMMAND_UPPER_DEVICE_NAME = 0x01;
	public static final byte COMMAND_LOWER_DEVICE_NAME = 0x07;
	
	// Enums
	
	/**
	 * Commands
	 * @author Robert Koszewski
	 */
	public static enum Command {
		MODE,
		BRIGHTNESS,
		AMBIENT_COLOR,
		AMBIENT_MODE_COLOR,
		AMBIENT_MODE_TYPE,
		AMBIENT_SCENE,
		HDMI_INPUT,
		CURRENT_STATE,
		CURRENT_STATE_REQUEST,
		UNKNOWN
	}
}
