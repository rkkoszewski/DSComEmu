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
package com.robertkoszewski.dsce.client.features;

import java.awt.Color;

/**
 * 
 * @author Robert Koszewski
 */
public class ScreenColor {
	
	public ScreenColor() {
		this.sectors = new byte[36];
	}
	
	public ScreenColor(byte[] sectors) {
		this.sectors = sectors;
	}

	// Variables
	private byte[] sectors;
	
	/**
	 * Get Sector Color
	 * @param sector
	 * @return
	 */
	public Color getColor(int sector) {
		//if(sector < 1) sector = 1;
		//else if(sector > 12) sector = 12;
		return new Color(sectors[sector-1] & 0xFF, sectors[sector] & 0xFF, sectors[sector+1] & 0xFF);
	}
	
	/**
	 * Set Sector Color
	 * @param sector
	 * @param color
	 */
	public void setColor(int sector, Color color) {
		sectors[sector-1] = (byte) (color.getRed()   & 0xFF);
		sectors[sector]   = (byte) (color.getGreen() & 0xFF);
		sectors[sector+1] = (byte) (color.getBlue()  & 0xFF);
	}
	
	/**
	 * Get Average Sector Color
	 * @param sectors
	 * @return
	 */
	public Color getAverageColor(int... sectors) {
		// UNCHECKED: Valid sectors are 1 to 12 and it should not surpass the length of 12 of the array
		
		int r = 0, g = 0, b = 0;
	
		for(int sector : sectors) {
			r += sectors[sector-1] & 0xFF;
			g += sectors[sector] & 0xFF;
			b += sectors[sector+1] & 0xFF;
		}
		
		return new Color(r/sectors.length, g/sectors.length, b/sectors.length);
	}

	/**
	 * Get Sector Payload
	 * @return
	 */
	public byte[] getPayload() {
		return sectors;
	}
}
