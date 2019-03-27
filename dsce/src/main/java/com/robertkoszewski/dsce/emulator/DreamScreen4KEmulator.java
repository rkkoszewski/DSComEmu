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

import com.robertkoszewski.dsce.client.devices.DSDevice.Device;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.emulator.utils.NoopSampler;
import com.robertkoszewski.dsce.emulator.utils.ScreenSampler;
import com.robertkoszewski.dsce.utils.DS;
import com.robertkoszewski.dsce.utils.NetworkInterface;

/**
 * DreamScreen 4K Emulator 
 * @author Robert Koszewski
 */
public class DreamScreen4KEmulator extends DreamScreenHDEmulator {
	
	/**
	 * Initialize DreamScreen 4K Emulator
	 */
	public DreamScreen4KEmulator() {
		super();
	}
	
	/**
	 * Initialize DreamScreen 4K Emulator with Screen Sampler
	 * @param sampler
	 */
	public DreamScreen4KEmulator(ScreenSampler sampler) {
		this(sampler, new SocketListener(DS.DS_PORT, DS.DS_MAX_BUFFER));
	}
	
	/**
	 * Initialize DreamScreen 4K Emulator with Network Interface
	 * @param networkInterface
	 */
	public DreamScreen4KEmulator(NetworkInterface networkInterface) {
		super(networkInterface);
	}
	
	/**
	 * Initialize DreamScreen 4K Emulator with Network Interface and Screen Sampler
	 * @param sampler
	 * @param networkInterface
	 */
	public DreamScreen4KEmulator(ScreenSampler sampler, NetworkInterface networkInterface) {
		super(networkInterface);
	}
	
	/**
	 * Initialize DreamScreen 4K Emulator with Socket
	 * @param sampler
	 */
	public DreamScreen4KEmulator(SocketListener socket) {
		super(new NoopSampler(), socket);
	}
	
	/**
	 * Initialize DreamScreen 4K Emulator with Screen Sampler and Socket
	 * @param sampler
	 * @param socket
	 */
	public DreamScreen4KEmulator(ScreenSampler sampler, final SocketListener socket) {
		super(socket);
	}


	@Override
	public Device getDeviceType() {
		return Device.DREAMSCREEN4K;
	}

}
