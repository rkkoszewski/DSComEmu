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

/**
 * Ambient Color Message
 * @author Robert Koszewski
 */
public class AmbientColorMessageWrapper extends DSMessageWrapper{

	// Constructor
	
	public AmbientColorMessageWrapper(byte group) {
		super(new DSMessage(group, DSMessage.FLAG_BROADCAST_TO_GROUP, DSMessage.COMMAND_UPPER_AMBIENT_COLOR, DSMessage.COMMAND_LOWER_AMBIENT_COLOR, new byte[3]));
	}
	
	public AmbientColorMessageWrapper(byte group, Color ambientColor) {
		this(group);
		setAmbientColor(ambientColor);
	}
	
	public AmbientColorMessageWrapper(DSMessage message) {
		super(message);
	}
	
	// Methods
	
	/**
	 * Get Ambient Color
	 * @return
	 */
	public Color getAmbientColor() {
		byte[] payload = message.getPayload();
		return new Color(payload[0] & 0xFF, payload[1] & 0xFF, payload[2] & 0xFF);
	}

	/**
	 * Set Ambient Color
	 * @param mode
	 */
	public void setAmbientColor(Color color) {
		byte[] payload = message.getPayload();
		payload[0] = (byte) ((byte) color.getRed() & 0xFF);
		payload[1] = (byte) ((byte) color.getGreen() & 0xFF);
		payload[2] = (byte) ((byte) color.getBlue() & 0xFF);
	}
	
	// Flags
	public static final byte FLAG_UNICAST_GROUP = DSMessage.FLAG_UNICAST;
	public static final byte FLAG_UNICAST_LOCAL = 0x01;
	
}
