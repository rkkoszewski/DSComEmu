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

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Java Robot Screen Capture based Screen Grabber
 * @author Robert Koszewski
 */
public class RobotScreenGrabber extends Thread implements ScreenGrabber {
	
	private boolean hasFrame = false;
	private BufferedImage screen;
	
	// Locks
	private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private ReentrantLock screenLock = new ReentrantLock();
	
	@Override
	public synchronized void start() {
		super.start();
	}
	
	@Override
	public void run() {
		super.run();
		
		Robot robot;
		try {
			robot = new Robot();
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Rectangle screenRectangle;

			while(!isInterrupted()) {

				screenRectangle = new Rectangle(toolkit.getScreenSize());
				screen = robot.createScreenCapture(screenRectangle); // Seems that what takes most of the time is storing the buffered image. Maybe multithread two Robot instances?

				synchronized(screenLock) {
					screenLock.notifyAll();
				}

				rwLock.writeLock().lock();
				hasFrame = true;
				rwLock.writeLock().unlock();

			}
		
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean hasFrame() {
		rwLock.readLock().lock();
		boolean retHasFrame = hasFrame;
		rwLock.readLock().unlock();
		return retHasFrame;
	}
	
	public BufferedImage getFrame() {
		rwLock.readLock().lock();
		if(!hasFrame) {
			// Wait for frame
			synchronized(screenLock){
				try {
					screenLock.wait();
				} catch (InterruptedException e) {
					rwLock.readLock().unlock();
					return null;
				}
			}
		}
		rwLock.readLock().unlock();
		
		BufferedImage tscreen = screen;
		
		rwLock.writeLock().lock();
		hasFrame = false;
		rwLock.writeLock().unlock();
		
		return tscreen;
	}
}
