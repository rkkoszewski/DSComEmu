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
		if(sectors.length != 36) {
			System.err.println("ERROR: Expected 36 bytes of sector data but got " + sectors.length);
			this.sectors = new byte[36];
		}else {
			this.sectors = sectors;
		}
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
		int isector = sector * 3;
		this.sectors[--isector] = (byte) (color.getBlue()  & 0xFF);
		this.sectors[--isector] = (byte) (color.getGreen() & 0xFF);
		this.sectors[--isector] = (byte) (color.getRed()   & 0xFF);
	}
	
	/**
	 * Get Average Sector Color
	 * @param sectors
	 * @return
	 */
	public Color getAverageColor(int... isectors) {
		// UNCHECKED: Valid sectors are 1 to 12 and it should not surpass the length of 12 of the array
		if(isectors.length == 0) return Color.BLACK;
		
		int r = 0, g = 0, b = 0;
	
		for(int sector : isectors) {
			int isector = sector * 3;
			b += this.sectors[--isector] & 0xFF;
			g += this.sectors[--isector] & 0xFF;
			r += this.sectors[--isector] & 0xFF;
		}
		
		return new Color(r/isectors.length, g/isectors.length, b/isectors.length);
	}

	/**
	 * Get Sector Payload
	 * @return
	 */
	public byte[] getPayload() {
		return this.sectors;
	}
}
