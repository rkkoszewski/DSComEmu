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
package com.robertkoszewski.dsce.client.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.robertkoszewski.dsce.messages.DSMessage;
import com.robertkoszewski.dsce.messages.InvalidMessageException;
import com.robertkoszewski.dsce.utils.StringUtils;

/**
 * Socket Listener
 * @author Robert Koszewski
 */
public class SocketListener {
	
	private final int buffer_size;
	private final int port;
	private final ArrayList<MessageReceived> messageReceivedCallbacks;
	private ServerThread serverThread = null;
	private boolean autoStartClose = true;
	private boolean debugMode = false;
	
	public SocketListener(int port, int buffer_size, boolean autoStartClose) {
		this(port, buffer_size);
		this.autoStartClose = autoStartClose;
	}
	
	public SocketListener(int port, int buffer_size) {
		this.port = port;
		this.buffer_size = buffer_size;
		this.messageReceivedCallbacks = new ArrayList<MessageReceived>();
	}
	
	/**
	 * Start Server Thread
	 */
	public void start() {
		if(serverThread == null || !serverThread.isAlive()) {
			serverThread = new ServerThread();
			serverThread.start();
			if(debugMode) System.out.println("Server Thread Started");
		}
	}
	
	/**
	 * Stop Server Thread
	 */
	public void stop() {
		if(serverThread != null && serverThread.isAlive()) {
			serverThread.interrupt();
			if(debugMode) System.out.println("Stopping Server Thread");
		}
	}
	
	/**
	 * Enable/Disable Debug Mode
	 * @param enable
	 */
	public void setEnableDebugMode(boolean enable) {
		this.debugMode = enable;
	}
	
	/**
	 * Is Debug Mode Enabled?
	 * @return
	 */
	public boolean isDebugModeEnabled() {
		return this.debugMode;
	}
	
	/**
	 * Enable/Disable Auto Start Close
	 * @param enable
	 */
	public void setEnableAutoStartClose(boolean enable) {
		this.autoStartClose = enable;
	}
	
	/**
	 * Add Message Received Callbacks
	 * @param messageReceived
	 */
	public void addCallback(MessageReceived... messageReceived) {	// TODO: Make this thread safe
		// Register Callbacks
		for(MessageReceived callback: messageReceived)
			messageReceivedCallbacks.add(callback);
		// Auto Start Server
		if(autoStartClose) 
			if(serverThread == null || !serverThread.isAlive())
				start();
	}
	
	/**
	 * Remove Message Received Callback
	 * @param messageReceived
	 */
	public void removeCallback(MessageReceived... messageReceived) {	// TODO: Make this thread safe
		// Remove Callback
		for(MessageReceived callback: messageReceived)
			messageReceivedCallbacks.remove(callback);
		// Auto Stop Server
		if(autoStartClose && messageReceivedCallbacks.size() == 0) 
			if(serverThread != null && serverThread.isAlive())
				stop();
	}
	
	/**
	 * Clear all Message Received Callbacks
	 */
	public void clearCallbacks() {
		// Clear all Callbacks
		messageReceivedCallbacks.clear();
		// Auto Stop Server
		if(autoStartClose && messageReceivedCallbacks.size() == 0) 
			if(serverThread != null && serverThread.isAlive())
				stop();
	}

	/**
	 * Server Thread
	 */
	private class ServerThread extends Thread {
		private DatagramSocket serverSocket = null;

		/**
		 * Run Thread
		 */
		public void run() {
			try {
				serverSocket = new DatagramSocket(port);
				serverSocket.setReuseAddress(true);
			
				DatagramPacket receivePacket = new DatagramPacket(new byte[buffer_size], buffer_size);
				while(!this.isInterrupted()) {
					try {
						// Receive Packet
						serverSocket.receive(receivePacket);
						
						// Parse Packet
						byte[] data = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
						
						try {
							// Parse DS Message
							InetAddress srcIP = receivePacket.getAddress();
							int srcPort = receivePacket.getPort();
							if(debugMode) System.out.println("+Message Received: (" + srcIP.getHostAddress() + ":" + srcPort + ") - 0x" + StringUtils.bytesToHex(data));
							DSMessage message = new DSMessage(data);
							
							// Run Callbacks
							for(Iterator<MessageReceived> it = messageReceivedCallbacks.iterator(); it.hasNext(); ) {
								it.next().run(message, srcIP, srcPort);
							}
						} catch (InvalidMessageException e) {
							System.err.println("Detected invalid message - Reason: " + e.getMessage());
							// Discard Unsupported Messages
						}
						
					} catch (IOException e) {
						if(this.isInterrupted()) return;
						e.printStackTrace();
					} 
				}
				
			} catch (SocketException e) {
				e.printStackTrace();
				
			} finally{
				// Close Server Socket
				if(serverSocket != null && !serverSocket.isClosed()) {
					serverSocket.close();
					if(debugMode) System.out.println("Server closed successfully");
				}
			}
		};
		
		/**
		 * Send Message using Server Socket
		 * @param dest_ip
		 * @param message
		 * @throws IOException 
		 */
		public void send(InetAddress dest_ip, byte[] message) throws IOException {
			if(serverSocket != null) {
				DatagramPacket packet = new DatagramPacket(message, message.length, dest_ip, port);
				serverSocket.send(packet);
			}
		}
		
		@Override
		public void interrupt() {
			super.interrupt();
			if(serverSocket != null && !serverSocket.isClosed()) 
				serverSocket.close();
		}
	};

	
	/**
	 * Send UDP Message (Requires server port to be available)
	 * @param dest_ip
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(InetAddress dest_ip, DSMessage message) throws IOException {
		sendMessage(dest_ip, message.getMessage());
	}
	
	/**
	 * Send UDP Message (Requires server port to be available)
	 * @param dest_ip
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(InetAddress dest_ip, byte[] message) throws IOException {
		// TODO: Fix threading issues here
		if(serverThread != null) {
			serverThread.send(dest_ip, message);
		}else {
			// Manually send
			DatagramSocket socket = new DatagramSocket(port);
			socket.setReuseAddress(true);
			DatagramPacket packet = new DatagramPacket(message, message.length, dest_ip, port);
			socket.send(packet);
			socket.close();
		}
	}
	
	/**
	 * Send Static UDP Message (Any open port)
	 * @param dest_ip
	 * @param message
	 * @throws IOException
	 */
	public void sendStaticMessage(InetAddress dest_ip, byte[] message) throws IOException {
		
	}
	
	/**
	 * Send a Static Message
	 * @param dest_ip
	 * @param message
	 * @throws IOException 
	 */
	public static void sendStaticMessage(InetAddress dest_ip, byte[] message, int port) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		socket.setBroadcast(true);
		socket.setReuseAddress(true);
		//System.out.println("SENDING PACKET: " + StringUtils.bytesToHex(message) + " to " + ip.toString());
		DatagramPacket packet = new DatagramPacket(message, message.length, dest_ip, port);
		socket.send(packet);
		socket.close();
	}
}
