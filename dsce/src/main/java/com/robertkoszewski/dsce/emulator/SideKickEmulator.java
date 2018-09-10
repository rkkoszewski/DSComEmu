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

import java.io.IOException;
import java.net.InetAddress;

import com.robertkoszewski.dsce.client.devices.DSDevice;
import com.robertkoszewski.dsce.client.devices.DSDevice.Device;
import com.robertkoszewski.dsce.client.server.MessageReceived;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.messages.DSMessage;
import com.robertkoszewski.dsce.messages.SectorSettingsMessageWrapper;

/**
 * SideKick Emulator
 * @author Robert Koszewski
 */
public class SideKickEmulator extends GenericEmulator {

	public SideKickEmulator() {
		super();
	}
	
	public SideKickEmulator(final SocketListener socket) {
		super(socket);

		// Responses
		callbacks.add(new MessageReceived() {
			@Override
			public void run(DSMessage message, InetAddress senderIP, int senderPort) {
				// Discard message targeted to other group
				byte targetGroup = message.getGroupAddress();
				if(targetGroup != 0 && (targetGroup & 0xFF) != 0xFF && targetGroup != getGroupNumber()) {
					System.out.println("IGNORING MESSAGE TARGETED FOR OTHER GROUP");
					return;
				}
				
				// Supported Commands
				switch(message.getCommand()) {
				case SECTOR_SETTING: // Set Sector Settings
					sectorSettings = new SectorSettingsMessageWrapper(message).getSectorSettings();
					break;
					
				case UNKNOWN_DS_PING:
					// TODO: Find out what this message means. A DreamScreen device sends this out to a SideKick in a Group. Is probably eider a ping or a request to know the sector settings of the device.
					
					System.out.println("Sending Test Message");
					DSMessage test_message = new DSMessage(getGroupNumber(), 
							(byte) 0x30, // Flags
							DSMessage.COMMAND_UPPER_SECTOR_SETTING, // Command Upper
							DSMessage.COMMAND_LOWER_SECTOR_SETTING, // Command Lower
							sectorSettings); // Payload
					
					/*
					DSMessage test_message = new DSMessage(getGroupNumber(), 
							(byte) 0x33, // Flags
							DSMessage.COMMAND_UPPER_UNKNOWN_DS_PING, // Command Upper
							DSMessage.COMMAND_LOWER_UNKNOWN_DS_PING); // Command Lower
					*/

					try {
						socket.sendStaticMessage(senderIP, test_message.getMessage());
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;

				// Ignore any other commands
				default: break; 
				}
			}
		});
	}
	
	// Variables
	private byte[] sectorSettings = new byte[30];
	
	// Methods
	
	// TODO: Research how Sector Settings are working (How to parse and generate data)

	@Override
	public Device getDeviceType() {
		return DSDevice.Device.SIDEKICK;
	}

}
