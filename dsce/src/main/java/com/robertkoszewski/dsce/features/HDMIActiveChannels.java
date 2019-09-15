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
package com.robertkoszewski.dsce.features;

/**
 * HDMI Active Channels
 * @author Robert Koszewski
 */
public class HDMIActiveChannels {

	/**
	 * Active HDMI Channels Constructor
	 */
	public HDMIActiveChannels() {
		 this.hdmi1Active = this.hdmi2Active = this.hdmi3Active = false;
	}
	
	/**
	 * Active HDMI Channels Constructor
	 * @param hdmi1Status
	 * @param hdmi2Status
	 * @param hdmi3Status
	 */
	public HDMIActiveChannels(boolean hdmi1Active, boolean hdmi2Active, boolean hdmi3Active) {
		this.hdmi1Active = hdmi1Active;
		this.hdmi2Active = hdmi2Active;
		this.hdmi3Active = hdmi3Active;
	}
	
	/**
	 * Active HDMI Channels Constructor from Byte
	 * @param activeHDMIByte
	 */
	public HDMIActiveChannels(byte activeHDMIByte) {
		hdmi1Active = (activeHDMIByte & 1) == 1;
		hdmi1Active = ((activeHDMIByte >> 1) & 1) == 1;
		hdmi1Active = ((activeHDMIByte >> 2) & 1) == 1;
	}

	// Variables
	private boolean hdmi1Active;
	private boolean hdmi2Active;
	private boolean hdmi3Active;

	// Methods
	
	public boolean isHDMI1Active() {
		return hdmi1Active;
	}
	
	public void setHDMI1Active(boolean active) {
		hdmi1Active = active; 
	}
	
	public boolean isHDMI2Active() {
		return hdmi2Active;
	}
	
	public void setHDMI2Active(boolean active) {
		hdmi2Active = active; 
	}
	
	public boolean isHDMI3Active() {
		return hdmi3Active;
	}
	
	public void setHDMI3Active(boolean active) {
		hdmi3Active = active; 
	}
	
	// Helper Methods
	
	/**
	 * Get Byte Representation
	 * @return
	 */
	public byte getByte() {
		return (byte) (0x00 + (
				(hdmi3Active ? 1 : 0) + 
				((hdmi2Active ? 1 : 0) << 1) + 
				((hdmi1Active ? 1 : 0) << 2)));
	}
}
