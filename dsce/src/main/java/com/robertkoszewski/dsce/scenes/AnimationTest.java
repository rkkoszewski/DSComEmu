/*******************************************************************************
 * Copyright (c) 2019 Robert Koszewski
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
package com.robertkoszewski.dsce.scenes;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

import javax.swing.JFrame;

import com.robertkoszewski.dsce.emulator.DreamScreenHDEmulator;
import com.robertkoszewski.dsce.emulator.utils.ColorSampler;
import com.robertkoszewski.dsce.emulator.variant.SwingDreamScreenHDEmulator;
import com.robertkoszewski.dsce.features.ScreenColor;

public class AnimationTest implements ColorSampler {
	public static void main(String[] args) {
		
		
		DreamScreenHDEmulator ds = new SwingDreamScreenHDEmulator(new AnimationTest());
		
		
		ds.start();
	}
	
	DreamScreenHDEmulator dsemulator;

	@Override
	public void init(DreamScreenHDEmulator dsemulator) {
		this.dsemulator = dsemulator;
	}
	
	Thread thread;

	@Override
	public void start() {
		
		System.out.println("STARING FIRE ANIMATION");
		// TODO Auto-generated method stub
		
		if(thread != null) return;
		
		thread = new Thread() {
			@Override
			public void run() {
				timestamp_animation_start = System.currentTimeMillis();
				
				while(!thread.isInterrupted()) {
					
					update();
					
					dsemulator.setScreenColors(color);
					
					try {
						Thread.sleep(35);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		};
		
		thread.start();
	}

	@Override
	public void stop() {
		if(thread == null) return;
		thread.interrupt();
		thread = null;
	}
	
	
	
	public static void update() {
		
		
		/*
		int r = random.nextInt((max - min) + 1) + min;
		int g = random.nextInt((max - min) + 1) + min;
		int b = random.nextInt((max - min) + 1) + min;
		*/
		
		
		float time = System.currentTimeMillis() - timestamp_animation_start;
		if(time > 500) {
			time = 0;
			timestamp_animation_start = System.currentTimeMillis();
			
			last_target = target;
			target = random.nextInt((max - min) + 1) + min;
			System.out.println("-- NEW TARGET: " + target);
			
			target_diff = target - last_target;
			
			transition_time = random.nextInt((max_time - min_time) + 1) + min_time;
		}
		
		
		int r = 255;
		int g = (int) easeNone(time, last_target, target_diff,  500);
		int b = 0;
		
		System.out.println(g);

		color = new ScreenColor(new Color(r,g,b));
		
	}
	
	public static float easeNone (float time, float start_value, float change_value, float duration) {
		return change_value * time / duration + start_value;
	}
	
	static int target = 100;
	static int target_diff = 100;
	static int last_target = 0;
	
	static int transition_time = 100;
	static int max_time = 70;
	static int min_time = 10;
	
	static int max = 170;
	static int min = 90;
	
	static ScreenColor color;
	
	static long timestamp_animation_start;
	
	static Random random = new Random();
}
