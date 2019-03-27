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
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

import com.robertkoszewski.dsce.client.devices.DSDevice;
import com.robertkoszewski.dsce.client.devices.DreamScreenHD;
import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientMode;
import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientScene;
import com.robertkoszewski.dsce.client.devices.DSDevice.Device;
import com.robertkoszewski.dsce.client.devices.DSDevice.Mode;
import com.robertkoszewski.dsce.client.features.ScreenColor;
import com.robertkoszewski.dsce.client.server.MessageReceived;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.emulator.utils.NoopSampler;
import com.robertkoszewski.dsce.emulator.utils.ScreenSampler;
import com.robertkoszewski.dsce.messages.CurrentStateMessageWrapper;
import com.robertkoszewski.dsce.messages.DSMessage;
import com.robertkoszewski.dsce.messages.HDMIInputMessageWrapper;
import com.robertkoszewski.dsce.messages.HDMINameMessageWrapper;
import com.robertkoszewski.dsce.utils.DS;
import com.robertkoszewski.dsce.utils.NetworkInterface;

/**
 * DreamScreen HD Emulator
 * @author Robert Koszewski
 */
public class DreamScreenHDEmulator extends GenericEmulator {
	
	/**
	 * Initialize DreamScreen HD Emulator
	 */
	public DreamScreenHDEmulator() {
		super();
	}

	/**
	 * Initialize DreamScreen HD Emulator with Screen Sampler
	 * @param sampler
	 */
	public DreamScreenHDEmulator(ScreenSampler sampler) {
		this(sampler, new SocketListener(DS.DS_PORT, DS.DS_MAX_BUFFER));
	}
	
	/**
	 * Initialize DreamScreen HD Emulator with Network Interface
	 * @param networkInterface
	 */
	public DreamScreenHDEmulator(NetworkInterface networkInterface) {
		super(networkInterface);
	}
	
	/**
	 * Initialize DreamScreen HD Emulator with Network Interface and Screen Sampler
	 * @param sampler
	 * @param networkInterface
	 */
	public DreamScreenHDEmulator(ScreenSampler sampler, NetworkInterface networkInterface) {
		super(networkInterface);
	}
	
	/**
	 * Initialize DreamScreen HD Emulator with Screen Sampler and Socket
	 * @param socket
	 */
	public DreamScreenHDEmulator(SocketListener socket) {
		this(new NoopSampler(), socket);
	}
	
	/**
	 * Initialize DreamScreen HD Emulator with Screen Sampler and Socket
	 * @param sampler
	 * @param socket
	 */
	public DreamScreenHDEmulator(ScreenSampler sampler, final SocketListener socket) {
		super(socket);
		
		// Set Sampler
		this.sampler = sampler;
		sampler.init(this);

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
				case HDMI_ACTIVE_CHANNELS: // Get List of Active HDMI Channels
					// TODO: Implement this
					break;
				
				case HDMI_INPUT_STATUS: // Probably means restore to previous state
					// TODO: Implement this
					break;
				
				case HDMI_INPUT: // Set HDMI Input
					setHDMIInput(new HDMIInputMessageWrapper(message).getHDMIInput() & 0xFF);
					break;
					
				case HDMI_NAME_1: // Set HDMI 1 Input Name
					setHDMIInput1Name(new HDMINameMessageWrapper(message).getInputName());
					break;
					
				case HDMI_NAME_2: // Set HDMI 2 Input Name
					setHDMIInput2Name(new HDMINameMessageWrapper(message).getInputName());
					break;
					
				case HDMI_NAME_3: // Set HDMI 3 Input Name
					setHDMIInput3Name(new HDMINameMessageWrapper(message).getInputName());
					break;

				// Ignore any other commands
				default: break; 
				}
			}
		});
	}

	// Variables
	
	protected byte hdmiInput = 0;
	protected String inputName1 = "unassigned";
	protected String inputName2 = "unassigned";
	protected String inputName3 = "unassigned";
	protected byte hdmiActiveChannels = 1;
	
	protected ScreenSampler sampler;
	
	// Methods
	
	@Override
	public Device getDeviceType() {
		return Device.DREAMSCREENHD;
	}
	
	@Override
	protected CurrentStateMessageWrapper getCurrentStateResponse() {
		CurrentStateMessageWrapper message = new CurrentStateMessageWrapper(getDeviceType());
		message.setName(name);
		message.setGroupNumber(groupNumber);
		message.setGroupName(groupName);
		message.setMode(mode);
		message.setBrightness(brightness);
		message.setAmbientColor(ambientColor);
		message.setAmbientScene(ambientScene);
		message.setHDMIInput(hdmiInput);
		message.setHDMIInput1Name(inputName1);
		message.setHDMIInput2Name(inputName2);
		message.setHDMIInput3Name(inputName3);
		message.setActiveChannels(hdmiActiveChannels);
		
		// Message Details
		DSMessage llmessage = message.getMessage((byte) 0x60);
		llmessage.setGroupAddress((byte) 0xFF);
		
		return message;
	}
	
	// Device Controls
	
	@Override
	public void setAmbientColor(Color color, boolean broadcastToGroup) {
		super.setAmbientColor(color, broadcastToGroup);
		if(mode == Mode.AMBIENT && ambientMode == AmbientMode.RGB) fillColors(color);
	}
	
	@Override
	public void setAmbientMode(AmbientMode ambientMode) {
		if(this.ambientMode == ambientMode) return; // No changes
		super.setAmbientMode(ambientMode);
		// Switch Ambient Mode
		switch(ambientMode) {
		case RGB:
			fillColors(ambientColor);
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
				fillColors(Color.GREEN);
				break;
			case FIRESIDE:
				fillColors(Color.ORANGE);
				break;
			case HOLIDAY:
				fillColors(Color.GREEN.darker());
				break;
			case JULY4TH:
				fillColors(Color.BLUE);
				break;
			case OCEAN:
				fillColors(Color.CYAN);
				break;
			case POP:
				fillColors(Color.MAGENTA);
				break;
			case RAINBOW:
				fillColors(Color.YELLOW);
				break;
			case RANDOMCOLOR:
				Random rand = new Random();
				fillColors(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
				break;
			case TWINKLE:
				fillColors(Color.GRAY);
				break;
			}
		}
	}
	
	@Override
	public void setMode(Mode mode) {
		if(this.mode == mode) return; // Ignore setting same modes

		// Switch Modes
		switch(mode) {
			case AMBIENT:
				fillColors(ambientColor);
				break;
			case MUSIC: // TODO: May be implemented in the future
			case SLEEP:
				byte[] black = new byte[36]; Arrays.fill(black, (byte) 0);
				setColors(new ScreenColor(black));
				break;
			case VIDEO: 
				sampler.start();
				break;
		}
		
		// Stop Sampler
		if(this.mode == Mode.VIDEO && mode != Mode.VIDEO) {
			sampler.stop();
		}
			
		super.setMode(mode);
	}

	@Override
	public void setGroupNumber(byte groupNumber) {
		super.setGroupNumber(groupNumber);
		
		if(groupNumber == 0) {
			stopSubscriptionThread();
		}else {
			startSubscriptionThread();
		}
	}
	
	/**
	 * Get HDMI Input
	 * @return
	 */
	public int getHDMIInput() {
		return (hdmiInput & 0xFF) + 1;
	}
	
	/**
	 * Set HDMI Input
	 * @param hdmiInput
	 */
	public void setHDMIInput(int hdmiInput) {
		if(hdmiInput < 1) hdmiInput = 1;
		else if(hdmiInput > 3) hdmiInput = 3;
		this.hdmiInput = (byte) ((hdmiInput - 1) & 0xFF);
	}
	
	/**
	 * Get HDMI Input 1 Name
	 * @return
	 */
	public String getHDMIInput1Name() {
		return this.inputName1;
	}
	
	/**
	 * Get HDMI Input 1 Name
	 * @return
	 */
	public void setHDMIInput1Name(String name) {
		this.inputName1 = name;
	}
	
	/**
	 * Get HDMI Input 2 Name
	 * @return
	 */
	public String getHDMIInput2Name() {
		return this.inputName2;
	}
	
	/**
	 * Get HDMI Input 2 Name
	 * @return
	 */
	public void setHDMIInput2Name(String name) {
		this.inputName2 = name;
	}
	
	/**
	 * Get HDMI Input 3 Name
	 * @return
	 */
	public String getHDMIInput3Name() {
		return this.inputName3;
	}
	
	/**
	 * Get HDMI Input 3 Name
	 * @return
	 */
	public void setHDMIInput3Name(String name) {
		this.inputName3 = name;
	}
	
	/**
	 * Get HDMI Active Channels
	 * @return
	 */
	public byte getHDMIActiveChannels() {
		return this.hdmiActiveChannels;
	}
	
	/**
	 * Set HDMI Active Channels
	 * @param hdmiInput
	 */
	public void setHDMIActiveChannels(byte hdmiActiveChannels) {
		this.hdmiActiveChannels = hdmiActiveChannels;
	}
	
	/**
	 * Set the Virtual Screen Sector Colors (This is to be called by the Screen Grabber, etc)
	 * @param scolor
	 */
	public void setScreenColors(ScreenColor scolor) {
		setColors(scolor);
		if(mode == Mode.VIDEO || mode == Mode.MUSIC)
			sendScreenColors(scolor);
	}
	
	/**
	 * Send Screen Colors to all Subscribed Devices
	 * @param scolor
	 */
	protected void sendScreenColors(ScreenColor scolor) {
		// TODO: TO BE IMPLEMENTED. Send out the colors to the subscribers
		System.out.println("SEDING COLORS!!!!");
	}
	
	/**
	 * Fill all sectors with same color
	 * @param color
	 */
	protected void fillColors(Color color) {
		Color[] colors = new Color[12];
		Arrays.fill(colors, color);
		setColors(new ScreenColor(colors));
	}
	
	/**
	 * Sets the Final Screen Sector Colors (HDMI, Colors, Ambient, etc)
	 * @param scolor
	 */
	protected void setColors(ScreenColor scolor) {
		// Method to be overridden
	}
	
	// Subscription Thread
	private SubscriptionThread subscription_thread;
	
	/**
	 * Start Subscription Thread
	 */
	private void startSubscriptionThread() {
		if(subscription_thread == null) {
			try {
				subscription_thread = new SubscriptionThread(this);
			} catch (UnknownHostException e) {
				e.printStackTrace(); // Should not happen, but in case it does it will show up in the console. (Caused probably due to OS limits)
			}
		}
	}
	
	/**
	 * Stop Subscription Thread
	 */
	private void stopSubscriptionThread() {
		if(subscription_thread != null) {
			subscription_thread.interrupt();
			subscription_thread = null;
		}
	}

	// Subscription Thread
	private class SubscriptionThread extends Thread {
		
		public SubscriptionThread(DreamScreenHDEmulator dsemulator) throws UnknownHostException {
			this.dsemulator = dsemulator;
			
			// Get Broadcast IP
			this.broadcast = InetAddress.getByName("255.255.255.255");
			
			// Build Message
			this.subscription_request = new DSMessage();
			subscription_request.setCommandLower(DSMessage.COMMAND_LOWER_SUBSCRIPTION_REQUEST);
			subscription_request.setCommandUpper(DSMessage.COMMAND_UPPER_SUBSCRIPTION_REQUEST);
			subscription_request.setFlags((byte) 0xFF); // TODO: FIND OUT WHAT FLAG
			subscription_request.setPayload(new byte[0]); // TODO: IS THIS THE CORRECT PAYLOAD?
		}
		
		private final DreamScreenHDEmulator dsemulator;
		private final DSMessage subscription_request;
		private final InetAddress broadcast;
		
		@Override
		public void run() {
			super.run();
			
			while(!this.isInterrupted()) {
				
				System.out.println("SENDING SUBSCRIPTION REQUEST TO GROUP: " + dsemulator.getGroupNumber());
				
				subscription_request.setGroupAddress(dsemulator.getGroupNumber());
				sendMessage(broadcast, subscription_request);
				
				try {
					Thread.sleep(5000); // Sleep 5 seconds
				} catch (InterruptedException e) {} 
			}
			
	
			
		}
	}
	
	// Utility Functions
	
	/**
	 * Replicate Device
	 */
	@Override
	public void replicate(DSDevice device) {
		super.replicate(device);
		
		// DreamScreen HD
		if(device instanceof DreamScreenHD) {
			DreamScreenHD dsdevice = (DreamScreenHD) device;
			this.hdmiActiveChannels = dsdevice.getHDMIActiveChannels();
			this.hdmiInput = dsdevice.getHDMIInput();
			this.inputName1 = dsdevice.getHDMIInput1Name();
			this.inputName2 = dsdevice.getHDMIInput2Name();
			this.inputName3 = dsdevice.getHDMIInput3Name();
		}
	}
}
