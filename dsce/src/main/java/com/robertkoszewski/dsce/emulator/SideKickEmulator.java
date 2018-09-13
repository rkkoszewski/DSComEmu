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

import java.awt.Color;
import java.net.InetAddress;
import java.util.Random;

import com.robertkoszewski.dsce.client.devices.DSDevice;
import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientMode;
import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientScene;
import com.robertkoszewski.dsce.client.devices.DSDevice.Device;
import com.robertkoszewski.dsce.client.devices.DSDevice.Mode;
import com.robertkoszewski.dsce.client.features.ScreenColor;
import com.robertkoszewski.dsce.client.server.MessageReceived;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.messages.CurrentStateMessageWrapper;
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
					return;
				}
				
				// Supported Commands
				switch(message.getCommand()) {
				case SECTOR_SETTING: // Set Sector Settings
					screenSectors = new SectorSettingsMessageWrapper(message).getSectorSettings();
					break;
					
				case SUBSCRIPTION_REQUEST: // Subscription Request
					if(mode != Mode.VIDEO && mode != Mode.MUSIC) break; 
					message.setPayload(DSMessage.SUBSCRIPTION_REQUEST_ACK_PAYLOAD); // Acknowledge Subscription
					sendMessage(senderIP, message);
					break;
					
				case SCREEN_SECTOR_DATA:
					if(mode != Mode.VIDEO && mode != Mode.MUSIC) break; 
					setScreenColors(new ScreenColor(message.getPayload()));
					break;

				// Ignore any other commands
				default: break; 
				}
			}
		});
	}
	
	// Variables
	private int[] screenSectors = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
	
	// Methods - Overrides
	
	@Override
	public void setAmbientColor(Color color, boolean broadcastToGroup) {
		super.setAmbientColor(color, broadcastToGroup);
		if(mode == Mode.AMBIENT && ambientMode == AmbientMode.RGB) setColor(color);
	}
	
	@Override
	public void setAmbientMode(AmbientMode ambientMode) {
		if(this.ambientMode == ambientMode) return; // No changes
		super.setAmbientMode(ambientMode);
		// Switch Ambient Mode
		switch(ambientMode) {
		case RGB:
			setColor(ambientColor);
			break;
		case SCENE:
			runAmbientScene(ambientScene);
			break;
		}
	}
	
	@Override
	public void setAmbientScene(AmbientScene ambientScene) {
		if(this.ambientScene == ambientScene) return; // No changes
		super.setAmbientScene(ambientScene);
		runAmbientScene(ambientScene);
	}
	
	/**
	 * Run Ambient Scene (Is called when Mode=Ambient and AmbientMode=Ambient)
	 * @param ambientScene
	 */
	public void runAmbientScene(AmbientScene ambientScene) {
		if(mode == Mode.AMBIENT && ambientMode == AmbientMode.SCENE) {
			// Ambient Scenes (Static Implementation)
			switch(ambientScene) {
			case ENCHANTEDFOREST:
				setColor(Color.GREEN);
				break;
			case FIRESIDE:
				setColor(Color.ORANGE);
				break;
			case HOLIDAY:
				setColor(Color.GREEN.darker());
				break;
			case JULY4TH:
				setColor(Color.BLUE);
				break;
			case OCEAN:
				setColor(Color.CYAN);
				break;
			case POP:
				setColor(Color.MAGENTA);
				break;
			case RAINBOW:
				setColor(Color.YELLOW);
				break;
			case RANDOMCOLOR:
				Random rand = new Random();
				setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
				break;
			case TWINKLE:
				setColor(Color.GRAY);
				break;
			}
		}
	}
	
	// Methods
	
	/**
	 * Set Screen Colors (Independent of configured screen sectors)
	 * @param scolor
	 */
	public void setScreenColors(ScreenColor scolor) {
		if(mode == Mode.VIDEO || mode == Mode.MUSIC)
			setColor(scolor.getAverageColor(screenSectors));
	}
	
	/**
	 * Set Color (For the final light device, applies for screen and ambient also)
	 * @param color
	 */
	public void setColor(Color color) {
		// Method to be overriden
	}
	
	@Override
	public void setMode(Mode mode) {
		if(this.mode == mode) return; // Ignore setting same modes
		super.setMode(mode);

		// Perform Changes for transitioning
		switch(mode) {
		case AMBIENT:
			setColor(ambientColor);
			break;
		case SLEEP:
		case MUSIC:
		case VIDEO:
			setColor(Color.BLACK);
			break;
		}
	}
	
	/**
	 * Current State Message
	 */
	protected CurrentStateMessageWrapper getCurrentStateResponse() {
		CurrentStateMessageWrapper message = new CurrentStateMessageWrapper(getDeviceType());
		message.setName(name);
		message.setGroupNumber(groupNumber);
		message.setGroupName(groupName);
		message.setMode(mode);
		message.setBrightness(brightness);
		message.setAmbientColor(ambientColor);
		message.setAmbientScene(ambientScene);
		message.setActiveSectors(screenSectors);
		
		// Message Details
		DSMessage llmessage = message.getMessage((byte) 0x60);
		llmessage.setGroupAddress((byte) 0xFF);

		return message;
	}

	@Override
	public Device getDeviceType() {
		return DSDevice.Device.SIDEKICK;
	}

}
