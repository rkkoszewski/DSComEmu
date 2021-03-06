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

import java.awt.Color;
import java.nio.charset.Charset;
import java.util.Arrays;
import com.robertkoszewski.dsce.client.devices.DSDevice;
import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientScene;
import com.robertkoszewski.dsce.client.devices.DSDevice.Device;
import com.robertkoszewski.dsce.client.devices.DSDevice.Mode;
import com.robertkoszewski.dsce.utils.ArrayUtils;

/**
 * Current State Message
 * @author Robert Koszewski
 */
public class CurrentStateMessageWrapper extends DSMessageWrapper{

	// Constructor
	
	public CurrentStateMessageWrapper(Device deviceType) {
		super(new DSMessage((byte) 0xFF, DSMessage.FLAG_STATUS, DSMessage.COMMAND_UPPER_CURRENT_STATE, DSMessage.COMMAND_LOWER_CURRENT_STATE, 
				deviceType == Device.SIDEKICK ? 
						new byte[SIDEKICK_PAYLOAD_SIZE] : 
							new byte[DREAMSCREEN_PAYLOAD_SIZE]));
		// Create Empty Current State Message
		this.deviceType = deviceType;
		setDevice(deviceType);
	}
	
	public CurrentStateMessageWrapper(DSMessage message) {
		super(message);
		this.deviceType = getDevice();
	}

	
	// Variables
	private final Device deviceType;
	
	// Methods
	
	/**
	 * Get Device Type
	 * @return
	 */
	public Device getDevice() {
		byte[] payload = message.getPayload();
		byte productID = payload[payload.length-1];
		return DSDevice.Device.valueOf(productID);
	}
	
	/**
	 * Set Device Type
	 * @param device
	 * @throws MalformedPayloadState 
	 */
	private void setDevice(Device device) {
		byte[] payload = message.getPayload();
		payload[payload.length-1] = device.getByte();
	}
	
	/**
	 * Get Device Name
	 * @return
	 */
	public String getName() {
		return new String(Arrays.copyOfRange(message.getPayload(), 0, 15)).trim(); // 0-15 (UTF-8) - Name
	}
	
	/**
	 * Set Name
	 * @param name
	 */
	public void setName(String name) {
		byte[] bname = name.getBytes(Charset.forName("UTF-8"));
		ArrayUtils.fillInArray(message.getPayload(), bname, 0, 16, (byte) 0x00);
	}

	/**
	 * Get Group Name
	 * @return
	 */
	public String getGroupName() {
		return new String(Arrays.copyOfRange(message.getPayload(), 16, 31)).trim(); // 16-31 (UTF-8) - Group Name
	}
	
	/**
	 * Set Group Name
	 * @param name
	 */
	public void setGroupName(String name) {
		byte[] bname = name.getBytes(Charset.forName("UTF-8"));
		ArrayUtils.fillInArray(message.getPayload(), bname, 16, 15, (byte) 0x00);
	}
	
	/**
	 * Get Group Number
	 * @return
	 */
	public byte getGroupNumber() {
		byte[] payload = message.getPayload();
		return payload[32];
	}

	/**
	 * Set Group Number
	 * @param groupNumber
	 */
	public void setGroupNumber(byte groupNumber) {
		byte[] payload = message.getPayload();
		payload[32] = groupNumber;
	}
	
	/**
	 * Get Mode
	 * @return
	 */
	public byte getMode() {
		byte[] payload = message.getPayload();
		return payload[33];
	}

	/**
	 * Set Mode
	 * @param mode
	 */
	public void setMode(byte mode) {
		byte[] payload = message.getPayload();
		payload[33] = mode;
	}
	
	/**
	 * Set Mode
	 * @param mode
	 */
	public void setMode(Mode mode) {
		setMode(mode.getByte());
	}
	
	/**
	 * Get Brightness
	 * @return
	 */
	public byte getBrightness() {
		byte[] payload = message.getPayload();
		return payload[34];
	}

	/**
	 * Set Brightness
	 * @param brightness
	 */
	public void setBrightness(byte brightness) {
		byte[] payload = message.getPayload();
		payload[34] = brightness;
	}
	
	/**
	 * Set Brightness
	 * @param brightness
	 */
	public void setBrigthness(int brightness) {
		if(brightness > 100 || brightness < 0) 
			throw new NumberFormatException("Value can only be between 0 and 100");
		setBrightness((byte) (brightness & 0xFF));
	}
	
	/**
	 * Get Ambient Color
	 * @return
	 */
	public Color getAmbientColor() {
		byte[] payload = message.getPayload();
		if(this.deviceType == Device.SIDEKICK) {
			return new Color(payload[35] & 0xFF, payload[36] & 0xFF, payload[37] & 0xFF);
		}else {
			// DreamScreen HD and 4K
			return new Color(payload[40] & 0xFF, payload[41] & 0xFF, payload[42] & 0xFF);
		}
	}

	/**
	 * Set Ambient Color
	 * @param color
	 */
	public void setAmbientColor(Color color) {
		setAmbientColor((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue());
	}
	
	/**
	 * Set Ambient Color
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setAmbientColor(byte r, byte g, byte b) {
		byte[] payload = message.getPayload();
		if(this.deviceType == Device.SIDEKICK) {
			payload[35] = r;
			payload[36] = g;
			payload[37] = b;
		}else {
			// DreamScreen HD and 4K
			payload[40] = r;
			payload[41] = g;
			payload[42] = b;
		}
	}
	
	/**
	 * Get Saturation Color
	 * @return
	 */
	public Color getColorSaturation() {
		byte[] payload = message.getPayload();
		if(this.deviceType == Device.SIDEKICK) {
			return new Color(payload[38] & 0xFF, payload[39] & 0xFF, payload[40] & 0xFF);
		}else {
			// DreamScreen HD and 4K
			return new Color(payload[43] & 0xFF, payload[44] & 0xFF, payload[45] & 0xFF);
		}
	}

	/**
	 * Set Saturation Color
	 * @param color
	 */
	public void setColorSaturation(Color color) {
		setColorSaturation((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue());
	}
	
	/**
	 * Set Saturation Color
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setColorSaturation(byte r, byte g, byte b) {
		byte[] payload = message.getPayload();
		if(this.deviceType == Device.SIDEKICK) {
			payload[38] = r;
			payload[39] = g;
			payload[40] = b;
		}else {
			// DreamScreen HD and 4K
			payload[43] = r;
			payload[44] = g;
			payload[45] = b;
		}
	}

	/**
	 * Set Ambient Scene
	 * @return
	 */
	public byte getAmbientScene() {
		byte[] payload = message.getPayload();
		if(this.deviceType == Device.SIDEKICK) {
			return payload[60];
		}else {
			// DreamScreen HD and 4K
			return payload[62];
		}
	}
	
	/**
	 * Set Ambient Scene
	 * @param ambientSceneID
	 */
	public void setAmbientScene(byte ambientSceneID) {
		byte[] payload = message.getPayload();
		if(this.deviceType == Device.SIDEKICK) {
			payload[60] = ambientSceneID;
		}else {
			// DreamScreen HD and 4K
			payload[62] = ambientSceneID;
		}
	}
	
	/**
	 * Set Ambient Scene
	 * @param ambientScene
	 */
	public void setAmbientScene(AmbientScene ambientScene) {
		setAmbientScene(ambientScene.getByte());
	}
	
	/**
	 * Set HDMI Input
	 * @return
	 */
	public byte getHDMIInput() {
		if(this.deviceType == Device.SIDEKICK) return 0;
		byte[] payload = message.getPayload();
		if(payload.length < 73) return 0;
		return payload[73];
	}
	
	/**
	 * Set HDMI Input
	 * @param HDMIInput
	 */
	public void setHDMIInput(byte HDMIInput) {
		if(this.deviceType == Device.SIDEKICK) return;
		byte[] payload = message.getPayload();
		if(payload.length < 73) return;
		payload[73] = HDMIInput;
	}
	
	/**
	 * Get HDMI 1 Input Name
	 * @return
	 */
	public String getHDMIInput1Name() {
		if(this.deviceType == Device.SIDEKICK) return null;
		byte[] payload = message.getPayload();
		if(payload.length < 90) return "";
		return new String(Arrays.copyOfRange(payload, 75, 90)).trim(); // 16-31 (UTF-8) - Group Name
	}
	
	/**
	 * Set HDMI 1 Input Name
	 * @param name
	 */
	public void setHDMIInput1Name(String name) {
		if(this.deviceType == Device.SIDEKICK) return;
		byte[] bname = name.getBytes(Charset.forName("UTF-8"));
		ArrayUtils.fillInArray(message.getPayload(), bname, 75, 15, (byte) 0x00);
	}
	
	/**
	 * Get HDMI 2 Input Name
	 * @return
	 */
	public String getHDMIInput2Name() {
		if(this.deviceType == Device.SIDEKICK) return null;
		byte[] payload = message.getPayload();
		if(payload.length < 106) return "";
		return new String(Arrays.copyOfRange(payload, 91, 106)).trim(); // 16-31 (UTF-8) - Group Name
	}
	
	/**
	 * Set HDMI 2 Input Name
	 * @param name
	 */
	public void setHDMIInput2Name(String name) {
		if(this.deviceType == Device.SIDEKICK) return;
		byte[] bname = name.getBytes(Charset.forName("UTF-8"));
		ArrayUtils.fillInArray(message.getPayload(), bname, 91, 15, (byte) 0x00);
	}
	
	/**
	 * Get HDMI 3 Input Name
	 * @return
	 */
	public String getHDMIInput3Name() {
		if(this.deviceType == Device.SIDEKICK) return null;
		byte[] payload = message.getPayload();
		if(payload.length < 122) return "";
		return new String(Arrays.copyOfRange(payload, 107, 122)).trim(); // 16-31 (UTF-8) - Group Name
	}
	
	/**
	 * Set HDMI 3 Input Name
	 * @param name
	 */
	public void setHDMIInput3Name(String name) {
		if(this.deviceType == Device.SIDEKICK) return;
		byte[] bname = name.getBytes(Charset.forName("UTF-8"));
		ArrayUtils.fillInArray(message.getPayload(), bname, 107, 15, (byte) 0x00);
	}
	
	/**
	 * Set HDMI Input
	 * @return
	 */
	public byte getActiveChannels() {
		if(this.deviceType == Device.SIDEKICK) return 0;
		byte[] payload = message.getPayload();
		if(payload.length < 129) return 0;
		return payload[129];
	}
	
	/**
	 * Set HDMI Input
	 * @param activeChannels
	 */
	public void setActiveChannels(byte activeChannels) {
		if(this.deviceType == Device.SIDEKICK) return;
		byte[] payload = message.getPayload();
		if(payload.length < 129) return;
		payload[129] = activeChannels;
	}
	
	/**
	 * Set Active Screen Sectors
	 * @param screenSectors
	 */
	public void setActiveSectors(int[] screenSectors) {
		if(this.deviceType != Device.SIDEKICK) return;
		byte[] sectors = new byte[15];
		
		for(int i = 0; i < 15 && i < screenSectors.length ; i++) {
			sectors[i] = (byte) (screenSectors[i] & 0xFF);
		}
		
		ArrayUtils.fillInArray(message.getPayload(), sectors, 42, 15, (byte) 0x00); // Found by brute force. 42 + 15 = Sector Settings
	}
	
	// Static Variables
	public static final int SIDEKICK_PAYLOAD_SIZE = 63;
	public static final int DREAMSCREEN_PAYLOAD_SIZE = 141;
	
	// Flags
	public static final byte FLAG_STATUS = DSMessage.FLAG_STATUS;

}
