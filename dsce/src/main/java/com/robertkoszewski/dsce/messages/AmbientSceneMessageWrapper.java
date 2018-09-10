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
package com.robertkoszewski.dsce.messages;

import com.robertkoszewski.dsce.client.devices.DSDevice.AmbientScene;

/**
 * Ambient Scene Message
 * @author Robert Koszewski
 */
public class AmbientSceneMessageWrapper extends DSMessageWrapper{

	// Constructor
	
	public AmbientSceneMessageWrapper(byte group) {
		// Create Empty Current State Message
		super(new DSMessage(group, DSMessage.FLAG_BROADCAST_TO_GROUP, DSMessage.COMMAND_UPPER_AMBIENT_SCENE, DSMessage.COMMAND_LOWER_AMBIENT_SCENE, new byte[1]));
	}
	
	public AmbientSceneMessageWrapper(byte group, AmbientScene ambientScene) {
		this(group);
		setAmbientScene(ambientScene);
	}
	
	public AmbientSceneMessageWrapper(DSMessage message) {
		super(message);
	}

	// Methods
	
	/**
	 * Get Ambient Scene
	 * @return
	 */
	public AmbientScene getAmbientScene() {
		return AmbientScene.valueOf(message.getPayload()[0]);
	}

	/**
	 * Set Ambient Scene
	 * @param mode
	 */
	public void setAmbientScene(AmbientScene mode) {
		message.getPayload()[0] = mode.getByte();
	}
	
	// Flags
	public static final byte FLAG_UNICAST = DSMessage.FLAG_UNICAST;
	
}
