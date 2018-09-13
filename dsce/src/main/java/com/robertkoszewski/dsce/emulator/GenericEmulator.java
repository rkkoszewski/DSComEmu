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
package com.robertkoszewski.dsce.emulator;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

import com.robertkoszewski.dsce.client.devices.DSDevice;
import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientMode;
import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientScene;
import com.robertkoszewski.dsce.client.devices.DSDevice.Mode;
import com.robertkoszewski.dsce.client.server.MessageReceived;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.messages.AmbientColorMessageWrapper;
import com.robertkoszewski.dsce.messages.AmbientModeMessageWrapper;
import com.robertkoszewski.dsce.messages.AmbientSceneMessageWrapper;
import com.robertkoszewski.dsce.messages.BrightnessMessageWrapper;
import com.robertkoszewski.dsce.messages.CurrentStateMessageWrapper;
import com.robertkoszewski.dsce.messages.DSMessage;
import com.robertkoszewski.dsce.messages.DeviceNameMessageWrapper;
import com.robertkoszewski.dsce.messages.GroupNameMessageWrapper;
import com.robertkoszewski.dsce.messages.ModeMessageWrapper;
import com.robertkoszewski.dsce.messages.GroupNumberMessageWrapper;
import com.robertkoszewski.dsce.utils.DS;

/**
 * Generic DS Device Emulator
 * @author Robert Koszewski
 */
public abstract class GenericEmulator implements IGenericEmulator {

	/**
	 * Initialize Generic Emulator
	 */
	public GenericEmulator() {
		this(new SocketListener(DS.DS_PORT, DS.DS_MAX_BUFFER));
	}
	
	/**
	 * Initialize Generic Emulator with Socket
	 * @param socket
	 */
	public GenericEmulator(final SocketListener socket) {
		this.socket = socket;
		final GenericEmulator emulator = this;
		
		// Responses
		callbacks.add(new MessageReceived() {
			@Override
			public void run(DSMessage message, InetAddress senderIP, int senderPort) {
				// Discard message targeted to other group
				byte targetGroup = message.getGroupAddress();
				if(targetGroup != 0 && (targetGroup & 0xFF) != 0xFF && targetGroup != getGroupNumber()) {
					return;
				}
				
				// Supported Commands
				switch(message.getCommand()) {
				case CURRENT_STATE_REQUEST: // Current State Request
					sendMessage(senderIP, emulator.getCurrentStateResponse().getMessage(CurrentStateMessageWrapper.FLAG_STATUS));
					break;
					
				case GROUP_NAME: // Set Group Name
					setGroupName(new GroupNameMessageWrapper(message).getGroupName());
					break;
					
				case GROUP_NUMBER: // Set Group Number
					setGroupNumber(new GroupNumberMessageWrapper(message).getGroupNumber());
					break;
					
				case DEVICE_NAME: // Set Device Name
					setName(new DeviceNameMessageWrapper(message).getDeviceName());
					break;
				
				case AMBIENT_COLOR: // Set Ambient Color
					setAmbientColor(new AmbientColorMessageWrapper(message).getAmbientColor(), 
							message.getFlags() == AmbientColorMessageWrapper.FLAG_UNICAST_GROUP ? true : false);
					break;

				case AMBIENT_MODE: // Set Ambient Mode Type
					setAmbientMode(new AmbientModeMessageWrapper(message).getAmbientMode());
					break;
					
				case AMBIENT_SCENE: // Set Ambient Scene
					setAmbientScene(new AmbientSceneMessageWrapper(message).getAmbientScene());
					break;
					
				case BRIGHTNESS: // Set Brightness
					setBrightness(new BrightnessMessageWrapper(message).getBrightness());
					break;

				case MODE: // Set Device Mode
					setMode(new ModeMessageWrapper(message).getMode()); 
					break;
					
				// Ignore any other commands
				default: break; 
				}
			}
		});
		
		// Default Settings
		name = "DSEmulator";
		groupName = "undefined";
		groupNumber = 0;
		mode = Mode.SLEEP;
		brightness = 100;
		ambientColor = new Color(0,0,0);
		ambientScene = AmbientScene.FIRESIDE;
	}
	
	// Device Status
	protected String name;
	protected String groupName;
	protected byte groupNumber;
	protected Mode mode;
	protected byte brightness;
	protected Color ambientColor; 
	protected AmbientScene ambientScene;
	protected AmbientMode ambientMode;
	
	protected final SocketListener socket;
	private boolean running = false;
	protected ArrayList<MessageReceived> callbacks = new ArrayList<MessageReceived>();
	
	/**
	 * Start Device Emulation
	 */
	public void start() { // TODO: Make thread safe
		if(running) return; // Abort if already running
		Iterator<MessageReceived> it = callbacks.iterator();
		while(it.hasNext())
			socket.addCallback(it.next());
		running = true;	
	}
	
	/**
	 * Stop Device Emulation
	 */
	public void stop() { // TODO: Make thread safe
		if(!running) return; // Abort if already stopped
		Iterator<MessageReceived> it = callbacks.iterator();
		while(it.hasNext())
			socket.removeCallback(it.next());
		running = false;	
	}
	
	/**
	 * Is Device Running?
	 * @return
	 */
	protected boolean isRunning() {
		return running;
	}

	// Messages
	
	/**
	 * Get Current State Response
	 * @return
	 */
	abstract CurrentStateMessageWrapper getCurrentStateResponse();
	
	// Device Controls
	
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
	 */
	public void setName(String name) {
		this.name = name;
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
	public void setGroupName(String name) {
		this.groupName = name;
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
	public void setGroupNumber(byte groupNumber) {
		this.groupNumber = groupNumber;
	}
	
	/**
	 * Get Mode
	 * @return
	 */
	public Mode getMode() {
		return this.mode;
	}

	/**
	 * Set Mode
	 * @param groupNumber
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
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
	 */
	public void setBrightness(int brightness) {
		if(brightness > 100 || brightness < 0) 
			throw new NumberFormatException("Value can only be between 0 and 100");
		this.brightness = (byte) (brightness & 0xFF);
	}
	
	/**
	 * Set Ambient Mode
	 * @param ambientMode
	 */
	public void setAmbientMode(AmbientMode ambientMode) {
		this.ambientMode = ambientMode;
	}
	
	/**
	 * Get Ambient Mode
	 * @return
	 */
	public AmbientMode getAmbientMode() {
		return this.ambientMode;
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
	 */
	public void setAmbientColor(Color color, boolean broadcastToGroup) {
		this.ambientColor = color;
	}
	
	/**
	 * Set  Ambient Color
	 * @param groupNumber
	 */
	public void setAmbientColor(byte r, byte g, byte b, boolean broadcastToGroup) {
		this.ambientColor = new Color(r, g, b);
	}

	/**
	 * Set Ambient Scene
	 * @return
	 */
	public AmbientScene getAmbientScene() {
		return this.ambientScene;
	}
	
	/**
	 * Set Ambient Scene
	 * @param groupNumber
	 */
	public void setAmbientScene(AmbientScene ambientScene) {
		this.ambientScene = ambientScene;
	}
	
	// Utility Functions
	
	/**
	 * Replicate Device Settings
	 * @param device
	 */
	public void replicate(DSDevice device) {
		this.name = device.getName();
		this.ambientScene = device.getAmbientScene();
		this.brightness = (byte) device.getBrightness();
		this.groupName = device.getGroupName();
		this.groupNumber = device.getGroupNumber();
		this.mode = device.getMode();
		this.ambientColor = device.getAmbientColor();
	}
	
	// Helpers
	
	/**
	 * Send Message
	 * @param senderIP
	 * @param dsMessage
	 */
	protected void sendMessage(InetAddress senderIP, DSMessage dsMessage) {
		try {
			socket.sendMessage(senderIP, dsMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
