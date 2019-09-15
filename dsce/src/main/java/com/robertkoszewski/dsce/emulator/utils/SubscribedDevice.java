/*******************************************************************************
 * Copyright (c) 2019 Robert Koszewski
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
package com.robertkoszewski.dsce.emulator.utils;

import java.net.InetAddress;

/**
 * Subscribed Device
 * @author Robert Koszewski
 */
public class SubscribedDevice {
	
	public SubscribedDevice(InetAddress ip) {
		this.ip = ip;
	}
	
	// Variables
	
	public final InetAddress ip;

	// Tick Tock Counter
	
	private int ticks = 0;
	
	/**
	 * Method to be executed when a request is dispatched
	 * @return TRUE if ticks are under the limit of 3, FALSE otherwise
	 */
	public boolean tick() { 
		synchronized(this) {
			if(ticks > 3) return false;
			ticks++;
			return true; 
		}
	}
	
	/**
	 * Method to be executed when a Subscription answer has been received
	 */
	public void tock() {
		synchronized(this) {
			ticks = 0;
		}
	}
}