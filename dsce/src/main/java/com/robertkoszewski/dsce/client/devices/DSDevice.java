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
package com.robertkoszewski.dsce.client.devices;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;

import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.messages.AmbientColorMessageWrapper;
import com.robertkoszewski.dsce.messages.AmbientModeMessageWrapper;
import com.robertkoszewski.dsce.messages.AmbientSceneMessageWrapper;
import com.robertkoszewski.dsce.messages.BrightnessMessageWrapper;
import com.robertkoszewski.dsce.messages.CurrentStateMessageWrapper;
import com.robertkoszewski.dsce.messages.DSMessage;
import com.robertkoszewski.dsce.messages.DeviceNameMessageWrapper;
import com.robertkoszewski.dsce.messages.InvalidMessageException;
import com.robertkoszewski.dsce.messages.ModeMessageWrapper;
import com.robertkoszewski.dsce.messages.GroupNumberMessageWrapper;
import com.robertkoszewski.dsce.utils.DS;

/**
 * DreamScreen Device Class
 * @author Robert Koszewski
 */
public abstract class DSDevice implements IDSDevice{
	
	// Constants
	public static final int MAX_STR_LENGTH = 16;
	
	/**
	 * Default Settings Device
	 */
	public DSDevice() {
		this(new SocketListener(DS.DS_PORT, DS.DS_MAX_BUFFER));
	}
	
	/**
	 * Default Settings Device
	 * @param socket
	 */
	public DSDevice(SocketListener socket) {
		this.socket = socket;
	}
	
	/**
	 * Device from DSMessage
	 * @param message
	 * @param ip
	 * @throws NotCurrentStateMessage
	 */
	public DSDevice(DSMessage message, InetAddress ip) throws NotCurrentStateMessage {
		this(new CurrentStateMessageWrapper(message), ip);
	}
	
	/**
	 * Device from DSMessage
	 * @param socket
	 * @param message
	 * @param ip
	 * @throws NotCurrentStateMessage
	 */
	public DSDevice(SocketListener socket, DSMessage message, InetAddress ip) throws NotCurrentStateMessage {
		this(socket, new CurrentStateMessageWrapper(message), ip);
	}
	
	/**
	 * Device from CurrentStateMessage Wrapper
	 * @param csmessage
	 * @param ip
	 */
	public DSDevice(CurrentStateMessageWrapper csmessage, InetAddress ip) {
		this(new SocketListener(DS.DS_PORT, DS.DS_MAX_BUFFER), csmessage, ip);
		// TODO: Initialize change listener
	}
	
	/**
	 * Device from CurrentStateMessage Wrapper
	 * @param socket
	 * @param csmessage
	 * @param ip
	 */
	public DSDevice(SocketListener socket, CurrentStateMessageWrapper csmessage, InetAddress ip) {
		updateState(csmessage);
		this.ip = ip;
		this.socket = socket;
	}

	// Variables
	private InetAddress ip;
	private String name;
	private String groupName;
	protected byte groupNumber;
	private byte mode;
	private byte brightness;
	private Color ambientColor;
	private byte ambientScene;
	
	// Socket
	protected SocketListener socket;
	
	// Methods
	
	/**
	 * Update Device State
	 * @param csmessage
	 */
	protected void updateState(CurrentStateMessageWrapper csmessage) {
		name = csmessage.getName();
		groupName = csmessage.getGroupName();
		groupNumber = csmessage.getGroupNumber();
		mode = csmessage.getMode();
		brightness = csmessage.getBrightness();
		ambientColor = csmessage.getAmbientColor();
		ambientScene = csmessage.getAmbientScene();
	}
	
	/**
	 * Get Device IP
	 * @return
	 */
	public InetAddress getIP() {
		return this.ip;
	}
	
	/**
	 * Get Device Name
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Set Name
	 * @param name
	 * @throws IOException 
	 */
	public void setName(String name) throws IOException {
		this.name = name == null ? "" : name.length() > MAX_STR_LENGTH ? name.substring(0, MAX_STR_LENGTH) : name;
		socket.sendStaticMessage(getIP(), new DeviceNameMessageWrapper(groupNumber, name).getMessage(DeviceNameMessageWrapper.FLAG_UNICAST));
	}

	/**
	 * Get Group Name
	 * @return
	 */
	public String getGroupName() {
		return this.groupName;
	}
	
	/**
	 * Set Group Name
	 * @param name
	 */
	public void setGroupName(String name) throws IOException{
		this.groupName = name == null ? "" : name.length() > MAX_STR_LENGTH ? name.substring(0, MAX_STR_LENGTH) : name;
	}
	
	/**
	 * Get Group Number
	 * @return
	 */
	public byte getGroupNumber() {
		return this.groupNumber;
	}

	/**
	 * Set Group Number
	 * @param groupNumber
	 */
	public void setGroupNumber(byte groupNumber) throws IOException {
		socket.sendStaticMessage(getIP(), new GroupNumberMessageWrapper(this.groupNumber, groupNumber).getMessage(GroupNumberMessageWrapper.FLAG_UNICAST));
		this.groupNumber = groupNumber;
	}
	
	/**
	 * Get Mode
	 * @return
	 */
	public Mode getMode() {
		return Mode.valueOf(this.mode);
	}

	/**
	 * Set Mode
	 * @param groupNumber
	 * @throws IOException 
	 */
	public void setMode(Mode mode) throws IOException {
		this.mode = mode.getByte();
		socket.sendStaticMessage(getIP(), new ModeMessageWrapper(groupNumber, mode).getMessage(ModeMessageWrapper.FLAG_UNICAST));
	}
	
	/**
	 * Get Brightness
	 * @return
	 */
	public int getBrightness() {
		return this.brightness;
	}

	/**
	 * Set Mode
	 * @param groupNumber
	 * @throws IOException 
	 */
	public void setBrightness(int brightness) throws IOException {
		if(brightness > 100 || brightness < 0) 
			throw new NumberFormatException("Value can only be between 0 and 100");
		this.brightness = (byte) (brightness & 0xFF);
		socket.sendStaticMessage(getIP(), new BrightnessMessageWrapper(groupNumber, brightness).getMessage(BrightnessMessageWrapper.FLAG_UNICAST));
	}
	
	/**
	 * Get Ambient Color
	 * @return
	 */
	public Color getAmbientColor() {
		return this.ambientColor;
	}

	/**
	 * Set  Ambient Color
	 * @param groupNumber
	 * @throws IOException 
	 */
	public void setAmbientColor(Color color, boolean broadcastToGroup) throws IOException {
		this.ambientColor = color;
		socket.sendStaticMessage(getIP(), new AmbientColorMessageWrapper(groupNumber, color).getMessage(
				broadcastToGroup ? AmbientColorMessageWrapper.FLAG_UNICAST_GROUP : AmbientColorMessageWrapper.FLAG_UNICAST_LOCAL));
	}
	
	/**
	 * Set  Ambient Color
	 * @param groupNumber
	 * @throws IOException 
	 */
	public void setAmbientColor(byte r, byte g, byte b, boolean broadcastToGroup) throws IOException {
		this.ambientColor = new Color(r, g, b);
		socket.sendStaticMessage(getIP(), new AmbientColorMessageWrapper(groupNumber, this.ambientColor).getMessage(
				broadcastToGroup ? AmbientColorMessageWrapper.FLAG_UNICAST_GROUP : AmbientColorMessageWrapper.FLAG_UNICAST_LOCAL));
	}

	/**
	 * Set Ambient Scene
	 * @return
	 */
	public AmbientScene getAmbientScene() {
		return AmbientScene.valueOf(this.ambientScene);
	}
	
	/**
	 * Set Ambient Scene
	 * @param groupNumber
	 * @throws IOException 
	 */
	public void setAmbientScene(AmbientScene ambientScene) throws IOException {
		this.ambientScene = ambientScene.getByte();
		socket.sendStaticMessage(getIP(), new AmbientSceneMessageWrapper(groupNumber, ambientScene).getMessage(AmbientSceneMessageWrapper.FLAG_UNICAST));
	}

	/**
	 * Set Ambient Mode (Write Only)
	 * @param groupNumber
	 * @throws IOException 
	 */
	public void setAmbientMode(AmbientMode mode) throws IOException {
		socket.sendStaticMessage(getIP(), new AmbientModeMessageWrapper(groupNumber, mode).getMessage(AmbientModeMessageWrapper.FLAG_UNICAST));
	}

	// Static Methods
	
	/**
	 * Parse Device
	 * @param message
	 * @param deviceIP
	 * @return
	 * @throws InvalidMessageException
	 * @throws NotCurrentStateMessage 
	 */
	public static DSDevice parseDevice(DSMessage message, InetAddress deviceIP) throws NotCurrentStateMessage {
		// Check if the message is a current state message
		if(message.getCommand() != DSMessage.Command.CURRENT_STATE) {
			throw new NotCurrentStateMessage();
		}
		
		// Identify Device
		CurrentStateMessageWrapper currentState = new CurrentStateMessageWrapper(message);

		switch(currentState.getDevice()) {
		case DREAMSCREEN4K:
			return new DreamScreen4K(currentState, deviceIP);
		case DREAMSCREENHD:
			return new DreamScreenHD(currentState, deviceIP);
		case SIDEKICK:
			return new SideKick(currentState, deviceIP);
		default:
			System.err.println("UNKNOWN DEVICE TYPE: " + currentState.getDevice().name()); // TODO: Maybe throw an exception?
			return null;
		}
	}
	
	/**
	 * Device Type
	 */
	public static enum Device{
		DREAMSCREENHD,
		DREAMSCREEN4K,
		SIDEKICK;
		
		public byte getByte() {
			switch(this) {
			case DREAMSCREENHD: return 0x01;
			case DREAMSCREEN4K: return 0x02;
			case SIDEKICK: return 0x03;
			default:
				return (byte) 0xFF;
			}
		}
		
		public static Device valueOf(byte productID) {
			switch(productID) {
			case 0x01:
				return Device.DREAMSCREENHD;
			case 0x02:
				return Device.DREAMSCREEN4K;
			case 0x03:
				return Device.SIDEKICK;
			default:
				return null;
			}
		}
	}
	
	/**
	 * Device Mode
	 */
	public enum Mode{
		SLEEP 	(DSMessage.MODE_SLEEP_PAYLOAD),
		VIDEO	(DSMessage.MODE_VIDEO_PAYLOAD),
		MUSIC	(DSMessage.MODE_MUSIC_PAYLOAD),
		AMBIENT	(DSMessage.MODE_AMBIENT_PAYLOAD);
		
		private final byte value;
		
		Mode(byte value) {
			this.value = value;
		}
		
		public byte getByte() {
			return this.value;
		}
		
		public static Mode valueOf(byte modeByte) {
			switch(modeByte) {
			case DSMessage.MODE_SLEEP_PAYLOAD: return SLEEP;
			case DSMessage.MODE_VIDEO_PAYLOAD: return VIDEO;
			case DSMessage.MODE_MUSIC_PAYLOAD: return MUSIC;
			case DSMessage.MODE_AMBIENT_PAYLOAD: return AMBIENT;
			default:
				return null;
			}
		}
	}
	
	/**
	 * Ambient Scene
	 */
	public enum AmbientScene{
		RANDOMCOLOR	(DSMessage.AMBIENT_SCENE_RANDOM_COLOR),
		FIRESIDE	(DSMessage.AMBIENT_SCENE_FIRESIDE_PAYLOAD),
		TWINKLE		(DSMessage.AMBIENT_SCENE_TWINKLE_PAYLOAD),
		OCEAN		(DSMessage.AMBIENT_SCENE_OCEAN_PAYLOAD),
		RAINBOW		(DSMessage.AMBIENT_SCENE_RAINBOW_PAYLOAD),
		JULY4TH		(DSMessage.AMBIENT_SCENE_JULY4TH_PAYLOAD),
		HOLIDAY		(DSMessage.AMBIENT_SCENE_HOLIDAY_PAYLOAD),
		POP			(DSMessage.AMBIENT_SCENE_POP_PAYLOAD),
		ENCHANTEDFOREST(DSMessage.AMBIENT_SCENE_ENCHANTED_FOREST_PAYLOAD);
		
		private final byte value;
		
		AmbientScene(byte value) {
			this.value = value;
		}
		
		public byte getByte() {
			return this.value;
		}
		
		public static AmbientScene valueOf(byte ambientSceneByte) {
			switch(ambientSceneByte) {
			case DSMessage.AMBIENT_SCENE_RANDOM_COLOR: return RANDOMCOLOR;
			case DSMessage.AMBIENT_SCENE_FIRESIDE_PAYLOAD: return FIRESIDE;
			case DSMessage.AMBIENT_SCENE_TWINKLE_PAYLOAD: return TWINKLE;
			case DSMessage.AMBIENT_SCENE_OCEAN_PAYLOAD: return OCEAN;
			case DSMessage.AMBIENT_SCENE_RAINBOW_PAYLOAD: return RAINBOW;
			case DSMessage.AMBIENT_SCENE_JULY4TH_PAYLOAD: return JULY4TH;
			case DSMessage.AMBIENT_SCENE_HOLIDAY_PAYLOAD: return HOLIDAY;
			case DSMessage.AMBIENT_SCENE_POP_PAYLOAD: return POP;
			case DSMessage.AMBIENT_SCENE_ENCHANTED_FOREST_PAYLOAD: return ENCHANTEDFOREST;
			default:
				return null;
			}
		}
	}
	
	/**
	 * Undocumented: Ambient Mode
	 */
	public enum AmbientMode{
		RGB	(DSMessage.AMBIENT_MODE_TYPE_RGB_COLOR_PAYLOAD),
		SCENE	(DSMessage.AMBIENT_MODE_TYPE_SCENE_PAYLOAD);

		private final byte value;
		
		AmbientMode(byte value) {
			this.value = value;
		}
		
		public byte getByte() {
			return this.value;
		}
		
		public static AmbientMode valueOf(byte ambientModeByte) {
			switch(ambientModeByte) {
			case DSMessage.AMBIENT_MODE_TYPE_RGB_COLOR_PAYLOAD: return RGB;
			case DSMessage.AMBIENT_MODE_TYPE_SCENE_PAYLOAD: return SCENE;
			default:
				return null;
			}
		}
	}
}
