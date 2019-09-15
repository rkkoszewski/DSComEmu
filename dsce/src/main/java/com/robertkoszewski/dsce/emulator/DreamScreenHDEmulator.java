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
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.robertkoszewski.dsce.client.devices.DSDevice;
import com.robertkoszewski.dsce.client.devices.DreamScreenHD;
import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientMode;
import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientScene;
import com.robertkoszewski.dsce.client.devices.DSDevice.Device;
import com.robertkoszewski.dsce.client.devices.DSDevice.Mode;
import com.robertkoszewski.dsce.client.server.MessageReceived;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.emulator.utils.NoopSampler;
import com.robertkoszewski.dsce.emulator.utils.ColorSampler;
import com.robertkoszewski.dsce.emulator.utils.SubscribedDevice;
import com.robertkoszewski.dsce.features.HDMIActiveChannels;
import com.robertkoszewski.dsce.features.ScreenColor;
import com.robertkoszewski.dsce.messages.CurrentStateMessageWrapper;
import com.robertkoszewski.dsce.messages.DSMessage;
import com.robertkoszewski.dsce.messages.HDMIActiveChannelMessageWrapper;
import com.robertkoszewski.dsce.messages.HDMIInputMessageWrapper;
import com.robertkoszewski.dsce.messages.HDMINameMessageWrapper;
import com.robertkoszewski.dsce.utils.DS;
import com.robertkoszewski.dsce.utils.NetworkInterface;

/**
 * DreamScreen HD Emulator
 * @author Robert Koszewski
 */
public class DreamScreenHDEmulator extends GenericEmulator {
	
	// Constructors
	
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
	public DreamScreenHDEmulator(ColorSampler sampler) {
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
	public DreamScreenHDEmulator(ColorSampler sampler, NetworkInterface networkInterface) {
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
	public DreamScreenHDEmulator(ColorSampler sampler, final SocketListener socket) {
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
					setHDMIActiveChannels(new HDMIActiveChannelMessageWrapper(message).getHDMIActiveChannels());
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
					
				case SUBSCRIPTION_REQUEST: // Subscription Request
					byte[] payload = message.getPayload();
					if(payload == null) break;
					if (Arrays.equals(payload, DSMessage.SUBSCRIPTION_REQUEST_ACK_PAYLOAD)) {
						
						String senderIPAddress = senderIP.getHostAddress();
						synchronized(subscriptions) {
							SubscribedDevice subscription = subscriptions.get(senderIPAddress);
							if(subscription != null) {
								// Device Exists
								subscription.tock();
							} else {
								// Device Doesn't Exist
								subscriptions.put(senderIPAddress, new SubscribedDevice(senderIP));
							}
						}
					}
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
	
	protected ColorSampler sampler;
	protected HashMap<String, SubscribedDevice> subscriptions = new HashMap<String, SubscribedDevice>();
	
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
		message.setColorSaturation(saturationR, saturationG, saturationB);
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
		
		System.out.println("SETTING GROUP NUMBER");
		
		if(groupNumber == 0x00) {
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
		sendUpdateMessage(new HDMIInputMessageWrapper(this.groupNumber, this.hdmiInput)); // Update Message
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
		sendUpdateMessage(new HDMINameMessageWrapper(this.groupNumber, 1, name)); // Update Message
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
		sendUpdateMessage(new HDMINameMessageWrapper(this.groupNumber, 2, name)); // Update Message
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
		sendUpdateMessage(new HDMINameMessageWrapper(this.groupNumber, 2, name)); // Update Message
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
	public void setHDMIActiveChannels(HDMIActiveChannels hdmiActiveChannels) {
		this.hdmiActiveChannels = hdmiActiveChannels.getByte();
		sendUpdateMessage(new HDMIActiveChannelMessageWrapper(this.groupNumber, hdmiActiveChannels)); // Update Message
	}
	
	/**
	 * Set the Virtual Screen Sector Colors (This is to be called by the Screen Grabber, etc)
	 * @param scolor
	 */
	public void setScreenColors(ScreenColor scolor) {
		if(mode == Mode.VIDEO || mode == Mode.MUSIC) {
			setColors(scolor);
			if(groupNumber != 0x00) sendScreenColors(scolor);
		}
	}
	
	
	long lastFrameTime = 0;
	
	/**
	 * Send Screen Colors to all Subscribed Devices
	 * @param scolor
	 */
	protected void sendScreenColors(ScreenColor scolor) {
		// TODO: TO BE IMPLEMENTED. Send out the colors to the subscribers
//		System.out.println("SEDING COLORS!!!! " + subscriptions.size());
		
		// Color Message
		DSMessage message = new DSMessage(groupNumber, DSMessage.FLAG_SCREEN_SECTOR_DATA, DSMessage.COMMAND_UPPER_SCREEN_SECTOR_DATA, DSMessage.COMMAND_LOWER_SCREEN_SECTOR_DATA);
		message.setPayload(scolor.getPayload());
		
		//System.out.println(message.toDebugString());

		System.currentTimeMillis();
		
		// Send to Subscriptors
		synchronized(subscriptions) {
			Iterator<SubscribedDevice> sit = subscriptions.values().iterator();
			while(sit.hasNext()) {
				SubscribedDevice subscription = sit.next();
				//sendMessage(subscription.ip, message);
//				System.out.println("SENDING TO: " + subscription.ip.getHostAddress());
				try {
					socket.sendStaticMessage(subscription.ip, message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// Force 60FPS to avoid saturating network
		long currentFrameTime = System.currentTimeMillis();
		long timeDiff = currentFrameTime - lastFrameTime;
		if(timeDiff < 16) {
			try {
				Thread.sleep(16 - timeDiff); 
			} catch (InterruptedException e) {} 
			lastFrameTime = System.currentTimeMillis();
		}else {
			lastFrameTime = currentFrameTime;
		}
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
		System.out.println("+ START SUBSCRIPTION THREAD");
		if(subscription_thread == null) {
			System.out.println("+ START SUBSCRIPTION START");
			try {
				subscription_thread = new SubscriptionThread(this);
				subscription_thread.start();
			} catch (UnknownHostException e) {
				e.printStackTrace(); // Should not happen, but in case it does it will show up in the console. (Caused probably due to OS limits)
			}
		}
	}
	
	/**
	 * Stop Subscription Thread
	 */
	private void stopSubscriptionThread() {
		System.out.println("STOP SUBSCRIPTION THREAD");
		if(subscription_thread != null) {
			System.out.println("STOP SUBSCRIPTION STOP");
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
			subscription_request.setFlags(DSMessage.FLAG_SUBSCRIPTION_REQUEST);
			subscription_request.setPayload(new byte[0]);
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
	
	/**
	 * Active HDMI Input
	 */
	public enum ActiveHDMIInput{
		HDMI1,
		HDMI2,
		HDMI3;
		
		public byte toByte(ActiveHDMIInput activeInput) {
			switch(activeInput) {
			case HDMI1:
				return 0x00;
			case HDMI2:
				return 0x01;
			case HDMI3:
				return 0x02;
			}
			return 0x00;
		} 
		
		public ActiveHDMIInput fromByte(byte activeInputByte) {
			switch(activeInputByte) {
				case 0x00: return HDMI1;
				case 0x01: return HDMI2;
				case 0x02: return HDMI3;
			}
			return HDMI1;
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
