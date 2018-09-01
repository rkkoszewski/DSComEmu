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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Shell Context
 * @author Robert Koszewski
 */
public class ShellContext {
	
	// Constructors
	
	public ShellContext(String prompt) {
		this(prompt, true);
	}
	
	public ShellContext(String prompt, boolean useCommandMatching) {
		this.prompt = prompt;
		this.useCommandMatching = useCommandMatching;
		
		// Base Commands
		// - Help Command
		addCommand("help", new Command() {

			@Override
			public void run(ShellContext context, String args) {
				if(args == null) {
					// Show all commands
					System.out.println("====================================================================");
					System.out.println("Showing all possible commands:");
					System.out.println("====================================================================");
					showCommandList();
					System.out.println("====================================================================");
					
				}else {
					// Show a specific command
					context.showHelp(args);
				}
			}

			@Override
			public String help() {
				return "Here you can find information about the available commands. "
						+ "By running 'help' you will see a list of available commands. "
						+ "You can view a extended description by running 'help <command>'.";
			}

			@Override
			public String description() {
				return "Here you can find information about the available commands";
			}
		});
		
		// - Exit Command
		addCommand("exit", new Command() {

			@Override
			public void run(ShellContext context, String args) {
				context.exit();
			}

			@Override
			public String help() {
				return "Returns to previous menu or exits the application when no more menus are left.";
			}

			@Override
			public String description() {
				return "Returns to previous menu or exits the application";
			}
		});
	}
	
	// Variables
	private String prompt = "Prompt> ";
	private boolean exitContext = false;
	private boolean useCommandMatching = false;
	private CommandMap commandMap = new CommandMap();
	
	// Methods
	
	/**
	 * Add Command
	 * @param command
	 * @param callback
	 */
	public void addCommand(String command, Command callback) {
		commandMap.put(command.toLowerCase(), callback);
	}
	
	/**
	 * Remove Command
	 * @param command
	 */
	public void removeCommand(String command) {
		commandMap.remove(command.toLowerCase());
	}
	
	/**
	 * Execute Command
	 * @param line
	 */
	public void executeCommand(String line) {
		String[] sline = line.trim().split(" ", 2);
		if(sline.length == 0 || line.trim().equals("")) {
			// Show available commands
			// showCommandList();
			
		}else {
			SortedMap<String, Command> commands = commandMap.getBestMatch(sline[0].toLowerCase());
			if(commands.isEmpty()) {
				// No command found -> show command list
				System.out.print(sline[0] + " is a unknown command. Possible commands are: ");
				Iterator<Entry<String, Command>> cit = commandMap.entrySet().iterator();
				boolean first = true;
				while(cit.hasNext()) {
					Entry<String, Command> command = cit.next();
					if(first) {
						System.out.print(command.getKey());
						first = false;
					}else {
						System.out.print(", " + command.getKey());
					}
				}
				System.out.println(""); // Add a new line
				
			}else{
				Iterator<Entry<String, Command>> cit = commands.entrySet().iterator();
				Entry<String, Command> command = cit.next();
				
				if(command.getKey().equals(sline[0].toLowerCase())) {
					// Match! Execute command
					command.getValue().run(this, (sline.length > 1 ? sline[1] : null));
					
				}else{
					if(useCommandMatching) {
						// Run closest similar command
						command.getValue().run(this, (sline.length > 1 ? sline[1] : null));
						
					}else {
						// Show possible similar commands
						System.out.print(sline[0] + " is a unknown command. Similar commands are: " + command.getKey());
						while(cit.hasNext()) {
							command = cit.next();
							System.out.print(", " + command.getKey());
						}
						System.out.println(""); // Add a new line
						
					}
				}
			}
			
		}
	}
	
	/**
	 * Shows all available Commands with descriptions
	 */
	public void showCommandList() {
		Iterator<Entry<String, Command>> cit = commandMap.entrySet().iterator();
		while(cit.hasNext()) {
			Entry<String, Command> command = cit.next();
			System.out.println(command.getKey() + ": " + command.getValue().description());
		}
	}
	
	/**
	 * Show Command help
	 * @param args
	 */
	public void showHelp(String command) {
		command = command.toLowerCase();
		Command cobj = commandMap.get(command);
		if(cobj == null) {
			System.out.println("The command '" + command + "' could not be found");
			
		}else {
			System.out.println(cobj.help());
			
		}
	}

	/**
	 * Exit the Context
	 */
	public void exit() {
		this.exitContext = true;
	}
	
	/**
	 * Set Prompt String
	 * @param prompt
	 */
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
	/**
	 * Get Prompt String
	 * @return
	 */
	public String getPrompt() {
		return this.prompt;
	}

	/**
	 * Run Shell Context
	 */
	public void run() {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		// Command Loop
		while(!this.exitContext) {
			try {
				System.out.print(this.prompt + " ");
				String line = in.readLine();
				executeCommand(line);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Command Map
	 * @author Robert Koszewski
	 */
	class CommandMap extends TreeMap<String, Command>{
		private static final long serialVersionUID = 1L;
		
		/**
		 * Find Best Match
		 * @param command
		 * @return
		 */
		public SortedMap<String, Command> getBestMatch(String command) {
			 if(command.length() > 0) {
				 char nextLetter = (char) (command.charAt(command.length()-1) + 1);
				 String end = command.substring(0, command.length()-1) + nextLetter;
				 return this.subMap(command, end);
			}
			return this;
		}
	}
}
