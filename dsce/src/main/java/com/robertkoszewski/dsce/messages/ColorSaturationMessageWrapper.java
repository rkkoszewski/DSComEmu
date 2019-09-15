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
 * Saturation Settings Message
 * @author Robert Koszewski
 */
public class ColorSaturationMessageWrapper extends DSMessageWrapper{

	// Constructor
	
	public ColorSaturationMessageWrapper(byte group) {
		super(new DSMessage(group, DSMessage.FLAG_BROADCAST_TO_GROUP, DSMessage.COMMAND_UPPER_SECTOR_SETTING, DSMessage.COMMAND_LOWER_SECTOR_SETTING, new byte[3]));
	}
	
	public ColorSaturationMessageWrapper(byte group, Color saturation) {
		this(group);
		setColorSaturation(saturation);
	}
	
	public ColorSaturationMessageWrapper(byte group, byte r, byte g, byte b) {
		this(group);
		setColorSaturation(r, g, b);
	}
	
	public ColorSaturationMessageWrapper(DSMessage message) {
		super(message);
	}

	// Methods
	
	/**
	 * Get Color Saturation
	 * @return
	 */
	public Color getColorSaturation() {
		byte[] sbyte = message.getPayload();
		System.out.println(sbyte[0] & 0xFF);
		System.out.println(sbyte[1] & 0xFF);
		System.out.println(sbyte[2] & 0xFF);
		return new Color(sbyte[0] & 0xFF, sbyte[1] & 0xFF, sbyte[2]  & 0xFF);
	}

	/**
	 * Get Color Saturation
	 * @param mode
	 */
	public void setColorSaturation(Color saturation) {
		byte[] sbyte = message.getPayload();
		sbyte[0] = (byte) (saturation.getRed() & 0xFF);
		sbyte[1] = (byte) (saturation.getRed() & 0xFF);
		sbyte[2] = (byte) (saturation.getRed() & 0xFF);	
	}
	
	/**
	 * Set Color Saturation
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setColorSaturation(byte r, byte g, byte b) {
		byte[] sbyte = message.getPayload();
		sbyte[0] = r;
		sbyte[1] = g;
		sbyte[2] = b;	
	}
	
	// Flags
	public static final byte FLAG_UNICAST = DSMessage.FLAG_UNICAST;
}
