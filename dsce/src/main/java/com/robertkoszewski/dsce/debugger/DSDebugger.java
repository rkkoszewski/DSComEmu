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
package com.robertkoszewski.dsce.debugger;

import java.net.InetAddress;

import com.robertkoszewski.dsce.client.server.MessageReceived;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.messages.DSMessage;
import com.robertkoszewski.dsce.utils.DS;
import com.robertkoszewski.dsce.utils.StringUtils;

/**
 * DS Network Debugger
 * @author Robert Koszewski
 */
public class DSDebugger {
	
	private final SocketListener socket;
	private boolean running = false;
	private boolean showHex = true;
	
	private MessageReceived DEBUG_CALLBACK = new MessageReceived() {
		public void run(DSMessage message, InetAddress senderIP, int senderPort) {
			byte[] payload = message.getPayload();
			System.out.println("DEBUG: " + senderIP.getHostAddress() +":"+senderPort + " -> " +
					"GrAddr: 0x" + StringUtils.bytesToHex(message.getGroupAddress()) +
					" | CUpper: 0x" + StringUtils.bytesToHex(message.getCommandUpper()) + 
					" | CLower: 0x" + StringUtils.bytesToHex(message.getCommandLower()) + 
					" | Flags: 0x" + StringUtils.bytesToHex(message.getFlags()) +
					(payload.length != 0 ? 
							" | Payload: 0x" + StringUtils.bytesToHex(message.getPayload()) +
							" | Payload (char): " + new String(message.getPayload())
							: "") +
					" | Command: " + message.getCommand().name() + 
					(showHex ? " | Hex: 0x" + StringUtils.bytesToHex(message.getMessage()) : ""));
		}
	};
	
	public DSDebugger() {
		this(new SocketListener(DS.DS_PORT, DS.DS_MAX_BUFFER));
	}
	
	public DSDebugger(boolean showHex) {
		this(new SocketListener(DS.DS_PORT, DS.DS_MAX_BUFFER));
		this.showHex = showHex;
	}
	
	public DSDebugger(SocketListener socket) {
		this.socket = socket;
	}
	
	public DSDebugger(SocketListener socket, boolean showHex) {
		this.socket = socket;
		this.showHex = showHex;
	}


	/**
	 * Start Debugger
	 */
	public void start() {
		if(running) return;
		running = true;
		// Add Callback
		socket.addCallback(DEBUG_CALLBACK);
		socket.setEnableAutoStartClose(false);
		socket.start();
	}
	
	/**
	 * Stop Debugger
	 */
	public void stop() {
		if(!running) return;
		
		// Remove Callback
		socket.removeCallback(DEBUG_CALLBACK);
		socket.setEnableAutoStartClose(true);
		running = false;
	}
}
