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
package com.robertkoszewski.dsce.emulator.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.robertkoszewski.dsce.client.features.ScreenColor;
import com.robertkoszewski.dsce.emulator.DreamScreenHDEmulator;

// TODO: Brainstorm options:
/*
 * 1. Reduce refresh rate
 * 2. Reduce sample distance (Reduces quality)
 * 3. Temporal Sampling (Reduce sampling by 2, blend the 2 frames filling the sampling gaps)
 * 4. Allow previous options to eider be set manually, or to be adjusted automatically to keep low CPU usage
 * 
// TODO: Get the right screen: https://stackoverflow.com/questions/18301429/java-print-screen-two-monitors
// TODO: Get Screen Refresh Rate: http://www.java2s.com/Code/Java/2D-Graphics-GUI/GettingtheCurrentScreenRefreshRateandNumberofColors.htm
 * 
 */

/**
 * Dynamic Average Sampler with custom Screen Grabber
 * @author Robert Koszewski
 */
public class DynamicAverageSampler implements ScreenSampler {
	
	// Constructors
	
	public DynamicAverageSampler(ScreenGrabber sgrabber) {
		this.sgrabber = sgrabber;
	}
	
	// Variables
	
	protected DreamScreenHDEmulator dsemulator;
	protected final ScreenGrabber sgrabber;
	protected SimpleAverageSamplerThread gthread;

	@Override
	public void init(DreamScreenHDEmulator dsemulator) {
		this.dsemulator = dsemulator;
	}

	@Override
	public void start() {
		if(gthread == null) {
			gthread = new SimpleAverageSamplerThread(this.dsemulator, this.sgrabber);
			gthread.start();
		}
	}

	@Override
	public void stop() {
		if(gthread != null) {
			gthread.interrupt();
			gthread = null;
		}
	}
	
	/**
	 * Simple Average Sampler Thread
	 * @author Robert Koszewski
	 */
	private class SimpleAverageSamplerThread extends Thread{
		
		public SimpleAverageSamplerThread(DreamScreenHDEmulator dsemulator, ScreenGrabber sgrabber) {
			this.sgrabber = sgrabber;
			this.dsemulator = dsemulator;
		}
		
		private final DreamScreenHDEmulator dsemulator;
		private final ScreenGrabber sgrabber;
		
		@Override
		public void run() {
			super.run();
			
			// Start Screen Grabber Thread
			if(sgrabber instanceof Thread) {
				if(!((Thread) sgrabber).isAlive()) // In case its already running
					((Thread) sgrabber).start(); // Start Grabber Thread
			}

			byte[] bscolor = new byte[36];
			ScreenColor scolor;
			long curTimestamp, timestamp = System.currentTimeMillis();
			int x0 = 0, y0 = 0, frames = 0, tileWidth, tileHeight;
			BufferedImage screen;

			while(!isInterrupted()) {

				screen = sgrabber.getFrame();
				tileWidth = screen.getWidth() / 5;
				tileHeight = screen.getHeight() / 3;
				scolor = new ScreenColor(bscolor);

				for(int i = 1; i <= 12 ; i++) {

					// Sectors:
					// 7  6  5  4  3
					// 8           2
					// 9 10 11 12  1
					switch(i) {
						case 1:  x0 = 4 * tileWidth; y0 = 2 * tileHeight; break;
						case 2:  x0 = 4 * tileWidth; y0 = tileHeight; 	  break;
						case 3:  x0 = 4 * tileWidth; y0 = 0; 			  break;
						case 4:  x0 = 3 * tileWidth; y0 = 0; 			  break;
						case 5:  x0 = 2 * tileWidth; y0 = 0; 			  break;
						case 6:  x0 = tileWidth; 	 y0 = 0; 			  break;
						case 7:  x0 = 0; 			 y0 = 0; 			  break;
						case 8:  x0 = 0; 			 y0 = tileHeight; 	  break;
						case 9:  x0 = 0; 			 y0 = 2 * tileHeight; break;
						case 10: x0 = tileWidth;     y0 = 2 * tileHeight; break;
						case 11: x0 = 2 * tileWidth; y0 = 2 * tileHeight; break;
						case 12: x0 = 3 * tileWidth; y0 = 2 * tileHeight; break;
					}
					
					// Simple Average Algorithm
					int x1 = x0 + tileWidth;
				    int y1 = y0 + tileHeight;
				    int num = tileWidth * tileHeight, pixel;
				    long sumr = 0, sumg = 0, sumb = 0;
				    for (int x = x0; x < x1; x+=1) {
				        for (int y = y0; y < y1; y+=1) {
				        	// Direct Way
				        	pixel = screen.getRGB(x, y);
				        	sumb += pixel & 0xFF;
				        	sumg += (pixel >> 8) & 0xFF;
				        	sumr += (pixel >> 16) & 0xFF;
				        }
				    }

					scolor.setColor(i, new Color((int) sumr / num,(int)  sumg / num,(int)  sumb / num));
				}

				dsemulator.setScreenColors(new ScreenColor(bscolor));
				frames++;
				
				curTimestamp = System.currentTimeMillis();
				if(curTimestamp - timestamp > 1000) {
					System.out.println("FPS: " + frames);
					frames = 0;
					timestamp = curTimestamp;
				}
			}
		}
		
		@Override
		public void interrupt() {
			super.interrupt();
			
			// Interrupt Grabber Thread
			if(sgrabber instanceof Thread) {
				Thread tsgrabber = (Thread) sgrabber;
				if(tsgrabber.isAlive() && tsgrabber.isInterrupted())
					((Thread) sgrabber).interrupt(); 
			}
		}
	}

}
