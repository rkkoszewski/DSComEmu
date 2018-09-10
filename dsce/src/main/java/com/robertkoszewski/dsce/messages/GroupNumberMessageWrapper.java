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

/**
 * Set Group Number Message
 * @author Robert Koszewski
 */
public class GroupNumberMessageWrapper extends DSMessageWrapper{

	// Constructor
	
	public GroupNumberMessageWrapper(byte group) {
		super(new DSMessage(group, DSMessage.FLAG_BROADCAST_TO_GROUP, DSMessage.COMMAND_UPPER_GROUP_NUMBER, DSMessage.COMMAND_LOWER_GROUP_NUMBER, new byte[1]));
	}
	
	public GroupNumberMessageWrapper(byte group, byte groupNumber) {
		this(group);
		setGroupNumber(groupNumber);
	}
	
	public GroupNumberMessageWrapper(DSMessage message) {
		super(message);
	}

	// Methods
	
	/**
	 * Get Mode
	 * @return
	 */
	public byte getGroupNumber() {
		return message.getPayload()[0];
	}

	/**
	 * Set Mode
	 * @param mode
	 */
	public void setGroupNumber(byte groupNumber) {
		message.getPayload()[0] = groupNumber;
	}
	
	// Flags
	public static final byte FLAG_UNICAST = DSMessage.FLAG_UNICAST;
}
