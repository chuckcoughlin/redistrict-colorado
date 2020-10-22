/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.geotools.data.wkt;

/**
 * This is an bean-type object that is a marker for the current parse position
 * relative to the string fragment presented to the parser. The text returned
 * is trimmed and the length is the character count including the delimiter.
 */
public class ParseStatus {
	private char delimiter; // The next delimiter
	private String text;
	private int length;     // Number of characters of text to return.
	
	public ParseStatus() {
		this.length = 0;
	}
	
	public char getDelimiter() { return this.delimiter; }
	public int getLength()  { return this.length; }
	public String getText() { return this.text; }
	
	public void setDelimiter(char c) { this.delimiter=c; }
	public void setLength(int len)   { this.length=len;  }
	public void setText(String txt)  { this.text=txt; }
}
