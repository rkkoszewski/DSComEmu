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
package com.robertkoszewski.dsce.utils;

/**
 * Arrays related utilities
 * @author Robert Koszewski
 */
public class ArrayUtils {
	
	/**
	 * Fill array with other array (Overwriting)
	 * @param array
	 * @param item
	 * @param index
	 */
	public static <E> void fillInArray(E[] array, E[] items, int index) {
		int ilen = items.length;
		int alen = array.length;
		for(int i = 0; i <= ilen && i + index <= alen; i++) {
			array[i + index] = items[i];
		}
	}
	
	/**
	 * Fill array with other array with length and blank fill (Overwriting)
	 * @param array
	 * @param items
	 * @param index
	 * @param length
	 * @param fill
	 */
	public static <E> void fillInArray(E[] array, E[] items, int index, int length, E fill) {
		int ilen = items.length;
		int alen = array.length;
		for(int i = 0; i + index <= alen && i < length; i++) {
			if(i <= ilen)
				array[i + index] = items[i]; // Put Item
			else
				array[i + index] = fill; // Fill blank
		}
	}
	
	/**
	 * Fill array with other array with length and blank fill (Overwriting)
	 * @param array
	 * @param items
	 * @param index
	 * @param length
	 * @param fill
	 */
	public static <E> void fillInArray(byte[] array, byte[] items, int index, int length, byte fill) {
		int ilen = items.length;
		int alen = array.length;
		for(int i = 0; i + index <= alen && i < length; i++) {
			if(i < ilen)
				array[i + index] = items[i]; // Put Item
			else
				array[i + index] = fill; // Fill blank
		}
	}
}
