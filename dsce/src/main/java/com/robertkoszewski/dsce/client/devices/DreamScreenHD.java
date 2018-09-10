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

import java.io.IOException;
import java.net.InetAddress;

import com.robertkoszewski.dsce.messages.*;

/**
 * DreamScreen HD Device
 * @author Robert Koszewski
 */
public class DreamScreenHD extends DSDevice {

	/**
	 * Default Settings Device
	 */
	public DreamScreenHD() {
		super();
		
		// Default Settings
		this.hdmiInput = 0;
		this.inputName1 = "";
		this.inputName2 = "";
		this.inputName3 = "";
		this.hdmiActiveChannels = 0;
	}
	
	/**
	 * Device from DSMessage
	 * @param message
	 * @throws NotCurrentStateMessage 
	 */
	public DreamScreenHD(DSMessage message, InetAddress ip) throws NotCurrentStateMessage {
		super(message, ip);
	}
	
	/**
	 * Device from CurrentStateMessage Wrapper
	 * @param csmessage
	 */
	public DreamScreenHD(CurrentStateMessageWrapper csmessage, InetAddress ip) {
		super(csmessage, ip);
	}
	
	// Variables
	private byte hdmiInput;
	private String inputName1;
	private String inputName2;
	private String inputName3;
	private byte hdmiActiveChannels;

	// Device Type
	
	public Device getDeviceType() {
		return Device.DREAMSCREENHD;
	}

	@Override
	protected void updateState(CurrentStateMessageWrapper csmessage) {
		super.updateState(csmessage); // Update State
		// Update DreamScreen HD related properties
		hdmiInput = csmessage.getHDMIInput();
		inputName1 = csmessage.getHDMIInput1Name();
		inputName2 = csmessage.getHDMIInput2Name();
		inputName3 = csmessage.getHDMIInput3Name();
		hdmiActiveChannels = csmessage.getActiveChannels();
	}
	
	// Methods
	
	/**
	 * Get HDMI Input
	 * @return
	 */
	public byte getHDMIInput() {
		return hdmiInput;
	}
	
	/**
	 * Set HDMI Input
	 * @param hdmiInput
	 * @throws IOException 
	 */
	public void setHDMIInput(int hdmiInput) throws IOException {
		if(hdmiInput < 1) hdmiInput = 1;
		else if(hdmiInput > 3) hdmiInput = 3;
		this.hdmiInput = (byte) ((hdmiInput - 1) & 0xFF);
		socket.sendStaticMessage(getIP(), new HDMIInputMessageWrapper(groupNumber, this.hdmiInput).getMessage(HDMIInputMessageWrapper.FLAG_UNICAST));
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
	 * @throws IOException 
	 */
	public void setHDMIInput1Name(String name) throws IOException {
		this.inputName1 = name;
		socket.sendStaticMessage(getIP(), new HDMINameMessageWrapper(groupNumber, 1, name).getMessage(HDMINameMessageWrapper.FLAG_UNICAST));
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
	 * @throws IOException 
	 */
	public void setHDMIInput2Name(String name) throws IOException {
		this.inputName2 = name;
		socket.sendStaticMessage(getIP(), new HDMINameMessageWrapper(groupNumber, 2, name).getMessage(HDMINameMessageWrapper.FLAG_UNICAST));
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
	 * @throws IOException 
	 */
	public void setHDMIInput3Name(String name) throws IOException {
		this.inputName3 = name;
		socket.sendStaticMessage(getIP(), new HDMINameMessageWrapper(groupNumber, 3, name).getMessage(HDMINameMessageWrapper.FLAG_UNICAST));
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
		// TODO: Finish this
	}
	
}
