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

import com.robertkoszewski.dsce.features.HDMIActiveChannels;

/**
 * HDMI Active Channel Message
 * @author Robert Koszewski
 */
public class HDMIActiveChannelMessageWrapper extends DSMessageWrapper{

	// Constructor
	
	public HDMIActiveChannelMessageWrapper(byte group) {
		super(new DSMessage(group, DSMessage.FLAG_BROADCAST_TO_GROUP, DSMessage.COMMAND_UPPER_HDMI_ACTIVE_CHANNELS, DSMessage.COMMAND_LOWER_HDMI_ACTIVE_CHANNELS, new byte[1]));
	}
	
	public HDMIActiveChannelMessageWrapper(byte group, HDMIActiveChannels activeChannels) {
		this(group);
		setHDMIActiveChannels(activeChannels);
	}
	
	public HDMIActiveChannelMessageWrapper(DSMessage message) {
		super(message);
	}

	// Methods
	
	/**
	 * Get Ambient Mode
	 * @return
	 */
	public HDMIActiveChannels getHDMIActiveChannels() {
		return new HDMIActiveChannels(message.getPayload()[0]);
	}

	/**
	 * Set Ambient Mode
	 * @param mode
	 */
	public void setHDMIActiveChannels(HDMIActiveChannels activeChannels) {
		message.getPayload()[0] = activeChannels.getByte();
	}
	
	// Flags	
	public static final byte FLAG_UNICAST = DSMessage.FLAG_UNICAST;
	
}
