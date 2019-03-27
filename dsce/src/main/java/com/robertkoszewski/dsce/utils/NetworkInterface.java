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
package com.robertkoszewski.dsce.utils;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Custom Network Interface Selector
 * @author Robert Koszewski
 */
public class NetworkInterface {
	
	// Constructors
	
	public NetworkInterface(InetAddress hostIP) {
		this.hostIP = hostIP;
	}
	
	public NetworkInterface(String networkInterfaceName) {
		this.networkInterfaceName = networkInterfaceName;
	}
	
	// Variables
	
	private InetAddress hostIP = null;
	private String networkInterfaceName = null;
	
	// Methods
	
	/**
	 * Build DatagramSocket
	 * @param port
	 * @return
	 * @throws SocketException 
	 */
	public DatagramSocket newDatagramSocket(int port) throws SocketException {
		if(this.hostIP != null) return newDatagramSocket(hostIP, port);
		if(this.networkInterfaceName != null) return newDatagramSocket(networkInterfaceName, port);
		return new DatagramSocket(port);
	}
	
	/**
	 * Host IP Based DatagramSocket
	 * @param hostIP
	 * @return
	 * @throws SocketException
	 */
	private static DatagramSocket newDatagramSocket(InetAddress hostIP, int port) throws SocketException {
		DatagramSocket dsocket = new DatagramSocket(null);
		dsocket.bind(new InetSocketAddress(hostIP, port));
		return dsocket;
	}
	
	/**
	 * Network Interface Name Based DatagramSocket
	 * @param networkIF
	 * @return
	 * @throws SocketException
	 */
	private static DatagramSocket newDatagramSocket(String networkIF, int port) throws SocketException {
		DatagramSocket dsocket = new DatagramSocket(null);
		java.net.NetworkInterface nif = java.net.NetworkInterface.getByName(networkIF);
		if(nif == null)
			throw new SocketException("ERROR: Interface '" + networkIF + "' not found.");
		
		Enumeration<InetAddress> nifAddresses = nif.getInetAddresses();
		if(!nifAddresses.hasMoreElements())
			throw new SocketException("ERROR: Interface '" + networkIF + "' IP not found.");
		
		InetAddress ip = nifAddresses.nextElement();
		// System.out.println("CONNECTING TO: " + networkIF +" @ " + ip.getHostAddress() + " : " + port);
		dsocket.bind(new InetSocketAddress(ip, port));
		return dsocket;
	}
}
