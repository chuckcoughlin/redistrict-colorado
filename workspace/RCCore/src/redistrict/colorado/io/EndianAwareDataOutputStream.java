/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package redistrict.colorado.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *  A class that gives most of the functionality of DataOutputStream, but is endian aware.
 *  Uses a real java.io.DataOutputStream to actually do the writing.
 *  Bigendian is what OutputStream does. A DataoutPutStream has final methods
 *  so we are forced to use a wrapper.
 */
public class EndianAwareDataOutputStream  {
	private EndianType type;
	private DataOutputStream out;

    /** Creates new EndianDataOutputStream */
    public EndianAwareDataOutputStream(OutputStream os, EndianType et) {
        this.out = new DataOutputStream(os);
        this.type = et;
    }
	
	public void close() throws IOException {
		out.close();
	}
	
	public void flush() throws IOException {
		out.flush();
	}
	/** Write a byte. For a single byte Endian doesn't matter.*/
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }
    
    /** Write a byte. For a single byte Endian doesn't matter.*/
    public void writeByte(byte b) throws IOException {
        out.writeByte(b);
    }

    /** Write a byte array. This is another case where Endian doesn't matter.*/
    public void writeBytes(String s) throws IOException {
        out.writeBytes(s);
    }

    public void writeShort(int s) throws IOException {
    	if( type.equals(EndianType.BIG)) {
    		out.writeShort(s);
    	}
    	else {
            out.writeByte(s);
            out.writeByte(s >> 8);
    	}
    }

    public void writeInt(int i) throws IOException {
    	if( type.equals(EndianType.BIG)) {
    		out.writeInt(i);
    	}
    	else {
            out.writeByte(i);
            out.writeByte(i >> 8);
            out.writeByte(i >> 16);
            out.writeByte(i >> 24);
    	}
    }


    /** write a 64bit long in BigEndian*/
    public void writeLong(long l) throws IOException {
    	if( type.equals(EndianType.BIG)) {
    		out.writeLong(l);
    	}
    	else {
    		out.writeByte((byte) (l));
    		out.writeByte((byte) (l >> 8));
    		out.writeByte((byte) (l >> 16));
    		out.writeByte((byte) (l >> 24));
    		out.writeByte((byte) (l >> 32));
    		out.writeByte((byte) (l >> 40));
    		out.writeByte((byte) (l >> 48));
    		out.writeByte((byte) (l >> 56));
    	};
    }

    /** write a 64bit double in BigEndian*/
    public void writeDouble(double d) throws IOException {
    	out.writeLong(Double.doubleToLongBits(d));
    }
}
