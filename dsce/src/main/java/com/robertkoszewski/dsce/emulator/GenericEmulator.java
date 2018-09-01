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
import com.robertkoszewski.dsce.client.server.MessageReceived;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.messages.CurrentStateMessageWrapper;
import com.robertkoszewski.dsce.messages.DSMessage;
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
		
		// Respond to Current State Request
		callbacks.add(new MessageReceived() {
			@Override
			public void run(DSMessage message, InetAddress senderIP, int senderPort) {
				// System.out.println("MESSAGE RECEIVED: (" +  message.getCommand().name() + ") 0x" + StringUtils.bytesToHex(message.getMessage()));
				if(DSMessage.Command.CURRENT_STATE_REQUEST == message.getCommand()) {
					try {
						System.out.println("EMULATOR: Responding to State Request");
						//System.out.println("SENDING: " + StringUtils.bytesToHex(emulator.getCurrentStateResponse().getMessage().getMessage()));
						socket.sendMessage(senderIP, emulator.getCurrentStateResponse().getMessage());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		// Default Settings
		name = "unassigned";
		groupName = "unassigned";
		groupNumber = 0;
		mode = DSMessage.MODE_MUSIC_PAYLOAD;
		brightness = 0;
		ambientColor = new Color(0,0,0);
		ambientScene = DSMessage.AMBIENT_SCENE_FIRESIDE_PAYLOAD;
	}
	
	// Device Status
	protected String name;
	protected String groupName;
	protected byte groupNumber;
	protected byte mode;
	protected byte brightness;
	protected Color ambientColor; 
	protected byte ambientScene;
	
	protected final SocketListener socket;
	private boolean running = false;
	private ArrayList<MessageReceived> callbacks = new ArrayList<MessageReceived>();
	
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
	 * Stop Devie Emulation
	 */
	public void stop() { // TODO: Make thread safe
		if(!running) return; // Abort if already stopped
		Iterator<MessageReceived> it = callbacks.iterator();
		while(it.hasNext())
			socket.removeCallback(it.next());
		running = false;	
	}

	// Messages
	
	/**
	 * Get Current State Response
	 * @return
	 */
	protected CurrentStateMessageWrapper getCurrentStateResponse() {
		CurrentStateMessageWrapper message = new CurrentStateMessageWrapper(CurrentStateMessageWrapper.SIDEKICK_PAYLOAD_SIZE);
		message.setName(name);
		message.setGroupNumber(groupNumber);
		message.setGroupName(groupName);
		message.setMode(mode);
		message.setBrightness(brightness);
		message.setAmbientColor(ambientColor);
		message.setAmbientScene(ambientScene);
		message.setDevice(getDeviceType());
		
		// Message Details
		DSMessage llmessage = message.getMessage();
		llmessage.setGroupAddress((byte) 0xFF);
		llmessage.setFlags((byte) 0x60);
		
		return message;
	}
	
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
	public byte getMode() {
		return this.mode;
	}

	/**
	 * Set Mode
	 * @param groupNumber
	 */
	public void setMode(byte mode) {
		this.mode = mode;
	}
	
	/**
	 * Get Brightness
	 * @return
	 */
	public byte getBrightness() {
		return this.brightness;
	}

	/**
	 * Set Mode
	 * @param groupNumber
	 */
	public void setBrightness(byte brightness) {
		this.brightness = brightness;
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
	public void setAmbientColor(Color color) {
		this.ambientColor = color;
	}
	
	/**
	 * Set  Ambient Color
	 * @param groupNumber
	 */
	public void setAmbientColor(byte r, byte g, byte b) {
		this.ambientColor = new Color(r, g, b);
	}

	/**
	 * Set Ambient Scene
	 * @return
	 */
	public byte getAmbientScene() {
		return this.ambientScene;
	}
	
	/**
	 * Set Ambient Scene
	 * @param groupNumber
	 */
	public void setAmbientScene(byte ambientSceneID) {
		this.ambientScene = ambientSceneID;
	}
	
	// Utility Functions
	
	/**
	 * Replicate Device Settings
	 * @param device
	 */
	public void replicate(DSDevice device) {
		this.name = device.getName();
		this.ambientScene = device.getAmbientScene().getByte();
		this.brightness = (byte) device.getBrightness();
		this.groupName = device.getGroupName();
		this.groupNumber = device.getGroupNumber();
		this.mode = device.getMode().getByte();
		this.ambientColor = device.getAmbientColor();
	}
}
