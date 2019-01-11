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
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.robertkoszewski.dsce.client.features.ScreenColor;
import com.robertkoszewski.dsce.client.server.SocketListener;
import com.robertkoszewski.dsce.emulator.DreamScreenHDEmulator;
import com.robertkoszewski.dsce.emulator.utils.ScreenSampler;

/**
 * DreamScreenHD Emulator with Visual Zone Preview
 * @author Robert Koszewski
 */
public class SwingDreamScreenHDEmulator extends DreamScreenHDEmulator{
	
	public SwingDreamScreenHDEmulator() {
		super();
	}
	
	public SwingDreamScreenHDEmulator(ScreenSampler sampler) {
		super(sampler);
	}
	
	public SwingDreamScreenHDEmulator(ScreenSampler sampler, final SocketListener socket) {
		super(sampler, socket);
	}
	
	public SwingDreamScreenHDEmulator(final SocketListener socket) {
		super(socket);
	}
	
	// Virtual Screen Window
	private JFrame frame;
	// Screen Sectors
	private JPanel s1 = new JPanel();
	private JPanel s2 = new JPanel();
	private JPanel s3 = new JPanel();
	private JPanel s4 = new JPanel();
	private JPanel s5 = new JPanel();
	private JPanel s6 = new JPanel();
	private JPanel s7 = new JPanel();
	private JPanel s8 = new JPanel();
	private JPanel s9 = new JPanel();
	private JPanel s10 = new JPanel();
	private JPanel s11 = new JPanel();
	private JPanel s12 = new JPanel();
	
	private ScreenColor lastColors;

	/**
	 * Start Emulator
	 */
	@Override
	public void start() {
		if(!isRunning()) {
			frame = new JFrame();
			updateTitle();
			frame.setAlwaysOnTop(true);
			frame.setBounds(100, 100, 480, 270);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(new GridLayout(3, 5, 0, 0));
			
			// Top Row
			frame.getContentPane().add(s7);
			frame.getContentPane().add(s6);
			frame.getContentPane().add(s5);
			frame.getContentPane().add(s4);
			frame.getContentPane().add(s3);
			
			
			// Middle Row
			frame.getContentPane().add(s8);
			frame.getContentPane().add(new JPanel());
			frame.getContentPane().add(new JPanel());
			frame.getContentPane().add(new JPanel());
			frame.getContentPane().add(s2);
			
			// Bottom Row
			frame.getContentPane().add(s9);
			frame.getContentPane().add(s10);
			frame.getContentPane().add(s11);
			frame.getContentPane().add(s12);
			frame.getContentPane().add(s1);
			frame.setVisible(true);
		}
		
		
		super.start();
	}
	
	/**
	 * Stop Emulator
	 */
	@Override
	public void stop() {
		if(isRunning()) {
			frame.setVisible(false);
			frame.dispose();
			frame = null;
		}
		super.stop();
	}
	
	/**
	 * Update Window Title
	 */
	private void updateTitle() {
		frame.setTitle(this.name + " - " + this.groupName + " (" + this.groupNumber + ")");
	}
	
	// Callbacks to Update Title
	
	@Override
	public void setName(String name) {
		super.setName(name);
		updateTitle();
	}
	
	@Override
	public void setGroupName(String name) {
		super.setGroupName(name);
		updateTitle();
	}
	
	@Override
	public void setGroupNumber(byte groupNumber) {
		super.setGroupNumber(groupNumber);
		updateTitle();
	}

	// Callbacks to Update Brightness
	
	@Override
	public void setBrightness(int brightness) {
		super.setBrightness(brightness);
		setColors(this.lastColors);
	}
	
	/**
	 * Set Colors of the Virtual Screen
	 * @param scolor
	 */
	@Override
	protected void setColors(ScreenColor scolor) {
		this.lastColors = scolor; // Required for updating brightness
		// TODO: Set Alpha to simulate brightness
		s1.setBackground(processColor(scolor, 1));
		s2.setBackground(processColor(scolor, 2));
		s3.setBackground(processColor(scolor, 3));
		s4.setBackground(processColor(scolor, 4));
		s5.setBackground(processColor(scolor, 5));
		s6.setBackground(processColor(scolor, 6));
		s7.setBackground(processColor(scolor, 7));
		s8.setBackground(processColor(scolor, 8));
		s9.setBackground(processColor(scolor, 9));
		s10.setBackground(processColor(scolor, 10));
		s11.setBackground(processColor(scolor, 11));
		s12.setBackground(processColor(scolor, 12));
	}
	
	/**
	 * Process Color to simulate Brightness
	 * @param scolor
	 * @param sector
	 * @return
	 */
	protected Color processColor(ScreenColor scolor, int sector) {
		Color color = scolor.getColor(sector);
		
		if(this.brightness >= 100)
			return color;
		
		float bfactor = (brightness & 0xFF) / 100f;
		return new Color((int) (color.getRed() * bfactor), (int) (color.getGreen() * bfactor), (int) (color.getBlue() * bfactor));
	}
	
}
