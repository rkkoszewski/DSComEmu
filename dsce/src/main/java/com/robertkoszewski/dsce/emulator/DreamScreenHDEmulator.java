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

import java.net.InetAddress;

import com.robertkoszewski.dsce.client.devices.DSDevice;
import com.robertkoszewski.dsce.client.devices.DreamScreenHD;
import com.robertkoszewski.dsce.client.devices.DSDevice.Device;
import com.robertkoszewski.dsce.client.server.MessageReceived;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.messages.CurrentStateMessageWrapper;
import com.robertkoszewski.dsce.messages.DSMessage;
import com.robertkoszewski.dsce.messages.HDMIInputMessageWrapper;
import com.robertkoszewski.dsce.messages.HDMINameMessageWrapper;

/**
 * DreamScreen HD Emulator
 * @author Robert Koszewski
 */
public class DreamScreenHDEmulator extends GenericEmulator {
	
	/**
	 * Initialize DreamScreen HD Emulator
	 */
	public DreamScreenHDEmulator() {
		super();
	}
	
	/**
	 * Initialize DreamScreen HD Emulator with Socket
	 * @param socket
	 */
	public DreamScreenHDEmulator(final SocketListener socket) {
		super(socket);
		
		// Defaults
		hdmiInput = 0;
		inputName1 = "unassigned";
		inputName2 = "unassigned";
		inputName3 = "unassigned";
		hdmiActiveChannels = 1;

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
				case HDMI_ACTIVE_CHANNELS: // Get List of Active HDMI Channels
					// TODO: Implement this
					break;
				
				case HDMI_INPUT_STATUS: // Probably means restore to previous state
					// TODO: Implement this
					break;
				
				case HDMI_INPUT: // Set HDMI Input
					setHDMIInput(new HDMIInputMessageWrapper(message).getHDMIInput() & 0xFF);
					break;
					
				case HDMI_NAME_1: // Set HDMI 1 Input Name
					setHDMIInput1Name(new HDMINameMessageWrapper(message).getInputName());
					break;
					
				case HDMI_NAME_2: // Set HDMI 2 Input Name
					setHDMIInput2Name(new HDMINameMessageWrapper(message).getInputName());
					break;
					
				case HDMI_NAME_3: // Set HDMI 3 Input Name
					setHDMIInput3Name(new HDMINameMessageWrapper(message).getInputName());
					break;

				// Ignore any other commands
				default: break; 
				}
			}
		});
	}

	// Variables
	
	protected byte hdmiInput;
	protected String inputName1;
	protected String inputName2;
	protected String inputName3;
	protected byte hdmiActiveChannels;
	
	// Methods
	
	@Override
	public Device getDeviceType() {
		return Device.DREAMSCREENHD;
	}
	
	@Override
	protected CurrentStateMessageWrapper getCurrentStateResponse() {
		CurrentStateMessageWrapper message = new CurrentStateMessageWrapper(getDeviceType());
		message.setName(name);
		message.setGroupNumber(groupNumber);
		message.setGroupName(groupName);
		message.setMode(mode);
		message.setBrightness(brightness);
		message.setAmbientColor(ambientColor);
		message.setAmbientScene(ambientScene);
		message.setHDMIInput(hdmiInput);
		message.setHDMIInput1Name(inputName1);
		message.setHDMIInput2Name(inputName2);
		message.setHDMIInput3Name(inputName3);
		message.setActiveChannels(hdmiActiveChannels);
		
		// Message Details
		DSMessage llmessage = message.getMessage((byte) 0x60);
		llmessage.setGroupAddress((byte) 0xFF);
		
		return message;
	}
	
	// Device Controls
	
	/**
	 * Get HDMI Input
	 * @return
	 */
	public int getHDMIInput() {
		return (hdmiInput & 0xFF) + 1;
	}
	
	/**
	 * Set HDMI Input
	 * @param hdmiInput
	 */
	public void setHDMIInput(int hdmiInput) {
		if(hdmiInput < 1) hdmiInput = 1;
		else if(hdmiInput > 3) hdmiInput = 3;
		this.hdmiInput = (byte) ((hdmiInput - 1) & 0xFF);
	}
	
	/**
	 * Get HDMI Input 1 Name
	 * @return
	 */
	public String getHDMIInput1Name() {
		return this.inputName1;
	}
	
	/**
	 * Get HDMI Input 1 Name
	 * @return
	 */
	public void setHDMIInput1Name(String name) {
		this.inputName1 = name;
	}
	
	/**
	 * Get HDMI Input 2 Name
	 * @return
	 */
	public String getHDMIInput2Name() {
		return this.inputName2;
	}
	
	/**
	 * Get HDMI Input 2 Name
	 * @return
	 */
	public void setHDMIInput2Name(String name) {
		this.inputName2 = name;
	}
	
	/**
	 * Get HDMI Input 3 Name
	 * @return
	 */
	public String getHDMIInput3Name() {
		return this.inputName3;
	}
	
	/**
	 * Get HDMI Input 3 Name
	 * @return
	 */
	public void setHDMIInput3Name(String name) {
		this.inputName3 = name;
	}
	
	/**
	 * Get HDMI Active Channels
	 * @return
	 */
	public byte getHDMIActiveChannels() {
		return this.hdmiActiveChannels;
	}
	
	/**
	 * Set HDMI Active Channels
	 * @param hdmiInput
	 */
	public void setHDMIActiveChannels(byte hdmiActiveChannels) {
		this.hdmiActiveChannels = hdmiActiveChannels;
	}
	
	// Utility Functions
	
	/**
	 * Replicate Device
	 */
	@Override
	public void replicate(DSDevice device) {
		super.replicate(device);
		
		// DreamScreen HD
		if(device instanceof DreamScreenHD) {
			DreamScreenHD dsdevice = (DreamScreenHD) device;
			this.hdmiActiveChannels = dsdevice.getHDMIActiveChannels();
			this.hdmiInput = dsdevice.getHDMIInput();
			this.inputName1 = dsdevice.getHDMIInput1Name();
			this.inputName2 = dsdevice.getHDMIInput2Name();
			this.inputName3 = dsdevice.getHDMIInput3Name();
		}
	}
}
