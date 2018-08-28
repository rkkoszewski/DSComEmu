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
package com.robertkoszewski.dsce.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.robertkoszewski.dsce.client.devices.DSDevice;
import com.robertkoszewski.dsce.client.devices.NotCurrentStateMessage;
import com.robertkoszewski.dsce.client.server.MessageReceived;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.messages.DSMessage;
import com.robertkoszewski.dsce.utils.DS;

/**
 * DS Client for scanning and connecting to DS devices
 * @author Robert Koszewski
 */
public class DSClient {

	// Constructors
	
	/**
	 * DS Client with Implicit Socket
	 */
	public DSClient() {
		this.socket = new SocketListener(DS.DS_PORT, DS.DS_MAX_BUFFER);
	}
	
	/**
	 * DS Client with Explicit Socket
	 * @param socket
	 */
	public DSClient(SocketListener socket) {
		this.socket = socket;
	}
	
	// Variables
	
	private SocketListener socket;

	/**
	 * Get Client List
	 * @return
	 * @throws IOException
	 */
	public DSDevice[] getClientList() throws IOException {
		final HashMap<String, DSDevice> devices = new HashMap<String, DSDevice>();
		
		// Start Listener
		MessageReceived callback = new MessageReceived() {
			@Override
			public void run(DSMessage message, InetAddress senderIP, int senderPort) {
				// Check if the message is a current state message
				if(message.getCommand() != DSMessage.Command.CURRENT_STATE) return;
				// Parse Device
				try {
					if(!devices.containsKey(senderIP.getHostAddress())) {
						DSDevice device = DSDevice.parseDevice(message, senderIP);
						if(device != null) devices.put(senderIP.getHostAddress(), device);
					}	
				} catch (NotCurrentStateMessage e) {}
			}
		};
		socket.addCallback(callback);

		// Send Query Message
		try {
			int tries = 3;
			while(tries-- > 0) {
				socket.sendMessage(InetAddress.getByName("255.255.255.255"), DSMessage.MESSAGE_READ_CURRENT_STATE);
				Thread.sleep(500); // 0.5 second of timeout
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Remove Callback (After timeout)
		try {
			Thread.sleep(1000); // 1 second of timeout
		} catch (InterruptedException e) {}
		socket.removeCallback(callback);
		
		// Convert to Array
		return devices.values().toArray(new DSDevice[devices.size()]);
	}
	
	/**
	 * Connect to Client
	 * @return
	 * @throws UnknownHostException 
	 * @throws IOException 
	 */
	public DSDevice getClient(String ip) throws UnknownHostException {
		return getClient(InetAddress.getByName(ip));
	}
	
	/**
	 * Connect to Client
	 * @return
	 * @throws IOException
	 */
	public DSDevice getClient(InetAddress ip){
		final Ref<DSDevice> device = new Ref<DSDevice>();
		final Lock lock = new ReentrantLock();
		
		// Start Listener
		MessageReceived callback = new MessageReceived() {
			@Override
			public void run(DSMessage message, InetAddress senderIP, int senderPort) {
				// Check if the message is a current state message
				if(message.getCommand() != DSMessage.Command.CURRENT_STATE) return;
		
				// Parse Device
				try {
					device.var = DSDevice.parseDevice(message, senderIP); // Parse Device
					
					// Release Lock
					synchronized (lock) {
						lock.notifyAll();
					}
				} catch (NotCurrentStateMessage e) {
					e.printStackTrace();
				} 
			}
		};
		socket.addCallback(callback);

		// Send Query Message
		try {
			int tries = 3; // 3 Retries
			while(tries-- > 0) {
				try {
					socket.sendMessage(ip, DSMessage.MESSAGE_READ_CURRENT_STATE);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				synchronized (lock) {
					lock.wait(1000); // Wait up to 1 second
				}

				if(device.var != null) {
					break;
				} 
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Remove Callback
		socket.removeCallback(callback);
		
		return device.var;
	}
	
	/**
	 * Reference Variable
	 * @param <T>
	 */
	private static class Ref<T>{
		T var;
	}
	
}
