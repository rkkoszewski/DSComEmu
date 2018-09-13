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
package com.robertkoszewski.dsce.emulator.variant;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import com.robertkoszewski.dsce.client.devices.DSDevice.Mode;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.emulator.SideKickEmulator;

/**
 * Side Kick Emulator with AWT Based Color Preview
 * @author Robert Koszewski
 */
public class SwingSideKickEmulator extends SideKickEmulator {

	
	public SwingSideKickEmulator() {
		super();
	}
	
	public SwingSideKickEmulator(SocketListener socket) {
		super(socket);
	}
	
	private JFrame frame; // Window
	
	/**
	 * Set Window Title
	 */
	private void setTitle() {
		if(frame != null)
			frame.setTitle(getName() + ": " + getGroupName() + "(" + (getGroupNumber() & 0xFF) + ") : " + getMode().name());
	}

	@Override
	public void start() {
		if(!isRunning()) {
			frame = new JFrame(getName() + ": " + getGroupName() + "(" + (getGroupNumber() & 0xFF) + ") : " + getMode().name());
			frame.setSize(new Dimension(450, 350));
			frame.getContentPane().setBackground(getAmbientColor());
			frame.setVisible(true);
		}
		super.start();
	}
	
	@Override
	public void stop() {
		if(isRunning()) {
			frame.setVisible(false);
			frame.dispose();
			frame = null;
		}
		super.stop();
	}
	
	private Color lastColor = Color.BLACK;

	@Override
	public void setColor(Color color) {
		this.lastColor = color;
		float bfactor = (brightness & 0xFF) / 100f;
		color = new Color(Math.round(color.getRed() * bfactor), Math.round(color.getGreen() * bfactor), Math.round(color.getBlue() * bfactor)); // Simulate Brightness Regulation
		frame.getContentPane().setBackground(color); // Update GUI Color
	}
	
	@Override
	public void setBrightness(int brightness) {
		super.setBrightness(brightness);
		setColor(lastColor); // Update Color
	}

	@Override
	public void setGroupName(String name) {
		super.setGroupName(name);
		setTitle();
	}
	
	@Override
	public void setGroupNumber(byte groupNumber) {
		super.setGroupNumber(groupNumber);
		setTitle();
	}
	
	@Override
	public void setName(String name) {
		super.setName(name);
		setTitle();
	}
	
	@Override
	public void setMode(Mode mode) {
		super.setMode(mode);
		setTitle();
	}
}
