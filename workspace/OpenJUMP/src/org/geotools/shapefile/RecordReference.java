/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.geotools.shapefile;

public class RecordReference implements Comparable<RecordReference> {
	private final int offset;
	private final int length;
	
	public RecordReference(int off,int len) {
		this.offset = off;
		this.length = len;
	}
	
	public int getOffset() { return this.offset; }
	public int getLength() { return this.length; }

	@Override
	public int compareTo(RecordReference rr) {
		return this.offset - rr.getOffset();
	}
}
