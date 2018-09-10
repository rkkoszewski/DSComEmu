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

import com.robertkoszewski.dsce.client.devices.DSDevice;

/**
 * HDMI Name Message
 * @author Robert Koszewski
 */
public class HDMINameMessageWrapper extends DSMessageWrapper{

	// Constructor
	
	public HDMINameMessageWrapper(byte group, int hdmiInput) {
		super(new DSMessage(group, DSMessage.FLAG_UNICAST, DSMessage.COMMAND_UPPER_HDMI_NAME, DSMessage.COMMAND_LOWER_HDMI_NAME_1, new byte[0]));
		setInputNumber(hdmiInput);
	}
	
	public HDMINameMessageWrapper(byte group, int hdmiInput, String inputName) {
		this(group, hdmiInput);
		setInputName(inputName);
	}
	
	public HDMINameMessageWrapper(DSMessage message) {
		super(message);
	}

	// Methods
	
	/**
	 * Get Device Name
	 * @return
	 */
	public String getInputName() {
		return new String(message.getPayload());
	}

	/**
	 * Set Device Name
	 * @param mode
	 */
	public void setInputName(String deviceName) {
		deviceName = deviceName == null ? "" : 
			deviceName.length() > DSDevice.MAX_STR_LENGTH ? 
					deviceName.substring(0, DSDevice.MAX_STR_LENGTH) : 
						deviceName;
		message.setPayload(deviceName.getBytes());		
	}
	
	/**
	 * Get HDMI Input Index
	 * @return
	 */
	public int getInputNumber() {
		switch(message.getCommandLower()) {
		case DSMessage.COMMAND_LOWER_HDMI_NAME_1:
			return  1;
		case DSMessage.COMMAND_LOWER_HDMI_NAME_2:
			return 2;
		default: 
			return 0;
		}
	}
	
	
	/**
	 * Set HDMI Input Index
	 * @param inputIndex
	 */
	public void setInputNumber(int inputIndex) {
		if(inputIndex < 0) inputIndex = 0;
		else if(inputIndex > 2) inputIndex = 2;
		switch(inputIndex) {
		case 0:
			message.setCommandLower(DSMessage.COMMAND_LOWER_HDMI_NAME_1);
			break;
		case 1: 
			message.setCommandLower(DSMessage.COMMAND_LOWER_HDMI_NAME_2);
			break;
		case 2: 
			message.setCommandLower(DSMessage.COMMAND_LOWER_HDMI_NAME_3);
			break;
		}
	}

	// Flags
	public static final byte FLAG_UNICAST = DSMessage.FLAG_UNICAST;
}
