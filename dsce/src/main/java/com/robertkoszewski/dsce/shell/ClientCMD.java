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
package com.robertkoszewski.dsce.shell;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import com.robertkoszewski.dsce.client.DSClient;
import com.robertkoszewski.dsce.client.devices.DSDevice;
import com.robertkoszewski.dsce.client.devices.DreamScreenHD;
import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientMode;
import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientScene;
import com.robertkoszewski.dsce.client.devices.DSDevice.Device;
import com.robertkoszewski.dsce.client.devices.DSDevice.Mode;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.debugger.DSDebugger;
import com.robertkoszewski.dsce.emulator.DreamScreen4KEmulator;
import com.robertkoszewski.dsce.emulator.DreamScreenHDEmulator;
import com.robertkoszewski.dsce.emulator.GenericEmulator;
import com.robertkoszewski.dsce.emulator.variant.SwingSideKickEmulator;
import com.robertkoszewski.dsce.utils.DS;
import com.robertkoszewski.dsce.utils.StringUtils;

/**
 * DS Client Shell
 * @author Robert Koszewski
 */
public class ClientCMD {

	/**
	 * Entry point for the CLI
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		final SocketListener socket = new SocketListener(DS.DS_PORT, DS.DS_MAX_BUFFER);
		final DSClient client = new DSClient(socket);
		final Services services = new Services();

		// Shell Instance
		final ShellContext shell = new ShellContext("DSce CLI>");
		
		// Command: List Devices
		shell.addCommand("list", new Command() {

			@Override
			public void run(ShellContext context, String args) {
				DSDevice[] list;
				try {
					System.out.print("Querying devices.. Please wait.\r");
					list = client.getClientList();
					System.out.println("Devices found:");
					for(DSDevice device: list)
						System.out.println("-> (" + device.getDeviceType().name() + ") " + device.getName() + " - " + device.getIP().getHostAddress());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// Help
			@Override public String help() { return "Shows a list of available devices on your network. No parameters are used."; }
			
			// Description
			@Override public String description() { return "Shows a list of available devices on your network"; }
		});
		
		// Command: Connect to device
		shell.addCommand("connect", new Command() {

			@Override
			public void run(ShellContext context, String args) {
				
				DSDevice sdevice = null;
				if(args == null) {
					// Query Devices
					System.out.print("Querying devices.. Please wait.\r");
					DSDevice[] list;
					try {
						list = client.getClientList();
						System.out.println("Devices found:");
						int c = 1;
						for(DSDevice dev: list)
							System.out.println("-> " + c++ + ". (" + dev.getDeviceType().name() + ") " + dev.getName() + " - " + dev.getIP().getHostAddress());
						
						boolean selected = false;
						
						while(!selected) {
							System.out.print("Select your device number. 0 will exit: ");
							String line = (new BufferedReader(new InputStreamReader(System.in))).readLine();
							try {
								int num = Integer.parseInt(line);
								if(num == 0) return;
								if(num > list.length) {
									System.out.println("That device doesn't exists.");
								}else{
									sdevice = list[num - 1];
									selected = true;
								}
								
							}catch(Exception e) {
								System.err.println("Wrong number. Try again.");
							}
						}
						
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					
					
				}else {
					// Connect to Single Device
					String[] arg = args.split(" ");
					try {
						sdevice = client.getClient(arg[0]);
					} catch (UnknownHostException e) {
						// e.printStackTrace();
						System.err.println("ERROR: The host you specified is unknown or invalid.");
						return;
					}
					
				}
				
				// Check if a device has been found
				if(sdevice == null) {
					System.err.println("ERROR: Could not find or connect to the device.");
					return; 
				} 
				final DSDevice device = sdevice;
				
				// Create a new Shell Context
				ShellContext shell_client = new ShellContext("Control>" + device.getName() + "@" + device.getIP().getHostAddress() + ">");

				// COMMAND: name
				shell_client.addCommand("name", new Command() {
					@Override public void run(ShellContext context, String args) { 
						if(args == null) {
							System.out.println(device.getName()); 
						}else {
							try {
								device.setName(args);
							} catch (IOException e) {
								System.err.println("ERROR: Could not change device name. Reason: " + e.getMessage());
							}
						}
							
					}
					@Override public String help() { return "Get/Set Name. With no parameters, "
							+ "the value will be returned. With parameters, the parameter value will be set on the device,"; }
					@Override public String description() { return "Get/Set Device Name"; }
				});
				
				// COMMAND: mode
				shell_client.addCommand("mode", new Command() {
					@Override public void run(ShellContext context, String args) { 
						if(args == null) {
							System.out.println(device.getMode().name()); 
						} else {
								try {
									device.setMode(DSDevice.Mode.valueOf(args.trim().toUpperCase()));
								} catch (IOException e) {
									e.printStackTrace();
								} catch(Exception e) {
									System.err.println("ERROR: Unknown mode. Valid modes are: " + listAllEnums(DSDevice.Mode.values()));
								}
						}
					}
					@Override public String help() { return "Get/Set Mode. With no parameters, "
							+ "the value will be returned. With parameters, the parameter value will be set on the device,"; }
					@Override public String description() { return "Get/Set Device Mode"; }
				});
				
				// COMMAND: brightness
				shell_client.addCommand("brightness", new Command() {
					@Override public void run(ShellContext context, String args) { 
						if(args == null)
							System.out.println(device.getBrightness()); 
						else {
							try {
								int brightness = Integer.parseInt(args);
								device.setBrightness(brightness);
							}catch(Exception e) {
								System.err.println("ERROR: Not a valid value. Only a value from 0 to 100 is allowed representing the brightness in percentage");
							}
						}	
					}
					@Override public String help() { return "Get/Set Brightness. With no parameters, "
							+ "the value will be returned. With parameters, the parameter value will be set on the device,"; }
					@Override public String description() { return "Get/Set Brightness"; }
				});
				
				// COMMAND: ambientscene
				shell_client.addCommand("ambientscene", new Command() {
					@Override public void run(ShellContext context, String args) { 
						if(args == null) {
							System.out.println(device.getAmbientScene().name()); 
						} else {
							try {
								AmbientScene scene = DSDevice.AmbientScene.valueOf(args.trim().toUpperCase());
								device.setMode(Mode.AMBIENT);
								device.setAmbientMode(AmbientMode.SCENE);
								device.setAmbientScene(scene);
							}catch(Exception e) {
								System.err.println("ERROR: Unknown mode. Valid modes are: " + listAllEnums(DSDevice.AmbientScene.values()));
							}
						}
					}
					@Override public String help() { return "Get/Set Device Ambient Scene. With no parameters, "
							+ "the value will be returned. With parameters, the parameter value will be set on the device."; }
					@Override public String description() { return "Get/Set Ambient Scene"; }
				});
				
				// COMMAND: ambientcolor
				shell_client.addCommand("ambientcolor", new Command() {
					@Override public void run(ShellContext context, String args) { 
						if(args == null) {
							System.out.println(device.getAmbientColor().toString()); 
						} else {
							try {
								Color color = Color.decode(args);
								device.setMode(Mode.AMBIENT);
								device.setAmbientMode(AmbientMode.RGB);
								device.setAmbientColor(color, true);
							}catch(Exception e) {
								System.err.println("ERROR: Unknown color. " + e.getMessage());
							}
						}
					}
					@Override public String help() { return "Get/Set Device Ambient Color. With no parameters, "
							+ "the value will be returned. In order to set the color the first parameter must be a hex encoded color ex. #FF0033"; }
					@Override public String description() { return "Get/Set Device Ambient Color"; }
				});

				// Dream Screen HD and Dream Screen 4K settings
				if(device instanceof DreamScreenHD) {
					final DreamScreenHD hddevice = (DreamScreenHD) device;
					
					// COMMAND: HDMI Name 1
					shell_client.addCommand("hdminame1", new Command() {
						@Override
						public void run(ShellContext context, String args) {
							if(args == null) {
								System.out.println(hddevice.getHDMIInput1Name()); 
							} else {
								try {
									hddevice.setHDMIInput1Name(args);
								}catch(Exception e) {
									System.err.println("ERROR: Could not update input Name.");
								}
							}
						}
						@Override public String help() { return description() + ". No parameters shows the current input name. With parameter sets the name of the input."; }
						@Override public String description() { return "Shows the name of the HDMI Input 1"; }
					});
					
					// COMMAND: HDMI Name 2
					shell_client.addCommand("hdminame2", new Command() {
						@Override
						public void run(ShellContext context, String args) {
							if(args == null) {
								System.out.println(hddevice.getHDMIInput2Name()); 
							} else {
								try {
									hddevice.setHDMIInput2Name(args);
								}catch(Exception e) {
									System.err.println("ERROR: Could not update input Name.");
								}
							}
						}
						@Override public String help() { return description() + ". No parameters shows the current input name. With parameter sets the name of the input."; }
						@Override public String description() { return "Shows the name of the HDMI Input 2"; }
					});
					
					// COMMAND: HDMI Name 3
					shell_client.addCommand("hdminame3", new Command() {
						@Override
						public void run(ShellContext context, String args) {
							if(args == null) {
								System.out.println(hddevice.getHDMIInput3Name()); 
							} else {
								try {
									hddevice.setHDMIInput3Name(args);
								}catch(Exception e) {
									System.err.println("ERROR: Could not update input Name.");
								}
							}
						}
						@Override public String help() { return description() + ". No parameters shows the current input name. With parameter sets the name of the input."; }
						@Override public String description() { return "Shows the name of the HDMI Input 3"; }
					});
					
					// COMMAND: HDMI Input
					shell_client.addCommand("hdmiinput", new Command() {
						@Override
						public void run(ShellContext context, String args) {
							if(args == null) {
								System.out.println(hddevice.getHDMIInput()); 
							} else {
								try {
									hddevice.setHDMIInput(Integer.parseInt(args));
								}catch(Exception e) {
									System.err.println("ERROR: Could not update input Name. Allowed input numbers are 1 to 3.");
								}
							}
						}
						@Override public String help() { return description() + ". No parameters shows the current input name. With parameter sets the name of the input."; }
						@Override public String description() { return "Shows and sets the current HDMI Input"; }
					});
					
					// COMMAND: HDMI Active Channels
					shell_client.addCommand("hdmiactivechannels", new Command() {
						@Override
						public void run(ShellContext context, String args) {
							System.out.println("0x"+StringUtils.bytesToHex(hddevice.getHDMIActiveChannels())); 
						}
						@Override public String help() { return description() + ". No parameters are required."; }
						@Override public String description() { return "Shows the HDMI Active Channels"; }
					});

				}
				
				// COMMAND: info
				shell_client.addCommand("info", new Command() {
					@Override
					public void run(ShellContext context, String args) {
						System.out.println("DEVICE TYPE: " + device.getDeviceType().name());
						System.out.println("getName: " + device.getName());
						System.out.println("getAmbientScene: " + device.getAmbientScene());
						System.out.println("getBrightness: " + device.getBrightness());
						System.out.println("getGroupName: " + device.getGroupName());
						System.out.println("getGroupNumber: " + device.getGroupNumber());
						System.out.println("getMode: " + device.getMode());
						System.out.println("getAmbientColor: " + device.getAmbientColor().toString());
						
						if(device instanceof DreamScreenHD) {
							DreamScreenHD dsdevice = (DreamScreenHD) device;
							System.out.println("getHDMIActiveChannels: " + dsdevice.getHDMIActiveChannels());
							System.out.println("getHDMIInput: " + dsdevice.getHDMIInput());
							System.out.println("getHDMIInput1Name: " + dsdevice.getHDMIInput1Name());
							System.out.println("getHDMIInput2Name: " + dsdevice.getHDMIInput2Name());
							System.out.println("getHDMIInput3Name: " + dsdevice.getHDMIInput3Name());
						}
					}
					@Override public String help() { return "Lists all known parameters of the device. No parameters required"; }
					@Override public String description() { return "Lists all known parameters of the device"; }
				});
				
				shell_client.executeCommand("help");
				shell_client.run();
			}

			@Override
			public String help() {
				return "Connect and control a DreamScreen Device. Without parameters a list of available DreamScreen"
						+ " devices will be queried. Running 'connect <ip>' connects directly to the device.";
			}

			@Override
			public String description() {
				return "Connect and control a DreamScreen Device";
			}
		});
		
		// COMMAND: emulator
		shell.addCommand("emulator", new Command() {

			@Override
			public void run(ShellContext context, String args) {
				
				// Create a new Shell Context
				ShellContext shell_emu = new ShellContext("Emulator>");
				
				// COMMAND: start
				shell_emu.addCommand("start", new Command() {

					@Override
					public void run(ShellContext context, String args) {
						if(services.emu == null) {
							
							try {
								Device device = (DSDevice.Device.valueOf(args.trim().toUpperCase()));
								System.out.print("Starting emulator...\r");
								
								switch(device) {
								case DREAMSCREEN4K:
									services.emu = new DreamScreen4KEmulator(socket);
									break;
								case DREAMSCREENHD:
									services.emu = new DreamScreenHDEmulator(socket);
									break;
								case SIDEKICK:
									services.emu = new SwingSideKickEmulator(socket);
									break;
								default:
									System.err.println("ERROR: Cannot start an unknown device.");
									return;
								}

								services.emu.start();
								System.out.println("Emulator Started");
								
							}catch(Exception e) {
								System.err.println("ERROR: Unknown Device Type. Valid Devices are: " + listAllEnums(DSDevice.Device.values()));
							}
						}else {
							System.err.println("Emulator is already running. Stop it before starting a new one.");
						}
					}
					
					@Override public String help() { return description() + ". As parameter you need to specify the device type you want to emulate."; }
					@Override public String description() { return "Starts a DreamScreen emulator instance"; }
				});
				
				// COMMAND: stop
				shell_emu.addCommand("stop", new Command() {

					@Override
					public void run(ShellContext context, String args) {
						if(services.emu != null) {
							System.out.print("Stopping emulator...\r");
							services.emu.stop();
							services.emu = null;
							System.out.println("Emulator Stopped");
						}else {
							System.err.println("Emulator is not running.");
						}
					}
					
					@Override public String help() { return description() + ". No arguments required."; }
					@Override public String description() { return "Stops the simulated device."; }
				});
				
				// COMMAND: replicate
				shell_emu.addCommand("replicate", new Command() {

					@Override
					public void run(ShellContext context, String args) {
						if(services.emu == null) {
							System.err.println("ERROR: No emulator is currently running.");
							return;
						} 
						
						if(args == null) {
							System.err.println("ERROR: You have to specify the replicate device IP.");
							return;
						}

						DSDevice cdevice;
						try {
							System.out.println("Connecting to external device...");
							cdevice = client.getClient(args);
							if(cdevice == null) {
								System.err.println("ERROR: Device not found");
							}else {
								services.emu.replicate(cdevice);
								System.out.println("Replicated sucessfully");
							}
						} catch (UnknownHostException e) {
							System.err.println("ERROR: Host could not be found. Replication failed.");
						}
					}
					
					@Override public String help() { return description() + ". The IP of the device is required as argument."; }
					@Override public String description() { return "Replicates the settings of an existing DreamScreen device."; }
				});
				
				// COMMAND: connect
				shell_emu.addCommand("connect", new Command() {

					@Override
					public void run(ShellContext context, String args) {
						System.out.println("Connecting to Emulator...");
						shell.executeCommand("connect localhost");
					}
					
					@Override public String help() { return description() + ". No arguments required."; }
					@Override public String description() { return "Connects to the emulated device."; }
				});
				
				// Start Context
				shell_emu.executeCommand("help");
				shell_emu.run();
			}

			@Override
			public String help() {
				return "Opens the emulator console";
			}

			@Override
			public String description() {
				return "Opens the emulator console";
			}
		});
		
		// COMMAND: debug
		shell.addCommand("debug", new Command() {

			@Override
			public void run(ShellContext context, String args) {
				System.out.println("Starting Network Debugger");
				if(services.dbg == null) services.dbg = new DSDebugger(socket);
				services.dbg.start();
			}

			@Override
			public String help() {
				return "Running the command starts the network debugger to "
						+ "display messages received from DreamSreen devices";
			}

			@Override
			public String description() {
				return "Display messages received from DreamSreen devices";
			}
		});

		// Run Shell
		System.out.println("====================================================================");
		System.out.println("Welcome to the DS Control and Emulation CLI. Available commands are:");
		System.out.println("====================================================================");
		shell.showCommandList();
		System.out.println("====================================================================");
		shell.run();
	}
	
	/**
	 * List All Enums
	 * @param values
	 * @return
	 */
	private static <E extends Enum<E>> String listAllEnums(Enum<E>[] values) {
		String out = "";
		boolean first = true;
		for(Enum<E> value: values) {
			if(first) {
				out += value.name();
				first = false;
			}else {
				out += ", " + value.name();
			}
		}
		return out;
	}
	
	/**
	 * Service References
	 */
	private static class Services {
		public GenericEmulator emu = null;
		public DSDebugger dbg = null;
	}
}
