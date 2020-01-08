/*
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

package org.openjump.io;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *  A class that gives most of the functionality of DataInputStream, but is endian aware.
 *  Uses a real java.io.DataInputStream to actually do the writing.
 */
public class EndianAwareInputStream implements DataInput, AutoCloseable {
    private final DataInputStream in;
    private EndianType type;
    private byte[] buf = new byte[8];

    /** Creates new EndianDataInputStream */
    public EndianAwareInputStream(BufferedInputStream bis, EndianType et) {
        this.in = new DataInputStream(bis);
        this.type = et;
    }
    public EndianAwareInputStream(InputStream is) {
        this.in = new DataInputStream(is);
        this.type = EndianType.BIG;
    }
    public void setType(EndianType et) { this.type = et; }
    
    /** close the stream**/
    @Override
    public void close() throws IOException {
        in.close();
    }

    /** When reading a byte, endian doesn't matter */
    @Override
    public byte readByte() throws IOException {
        return in.readByte();
    }
    @Override
    public int readUnsignedByte() throws IOException {
        return in.readUnsignedByte();
    }
    @Override
    public short readShort() throws IOException {
    	if( type.equals(EndianType.BIG)) {
    		return in.readShort();
    	}
    	else {
    		in.readFully(buf, 0, 2);
            return (short) (((buf[1] & 0xff) << 8) | (buf[0] & 0xff));
    	}
    }

    /** read a 32bit int*/
    @Override
    public int readInt() throws IOException {
    	if( type.equals(EndianType.BIG)) {
    		return in.readInt();
    	}
    	else {
    		in.readFully(buf, 0, 4);
            return ((buf[3] & 0xff) << 24) | ((buf[2] & 0xff) << 16) |
            		((buf[1] & 0xff) << 8) | (buf[0] & 0xff);
    	}
    }
    @Override
    public long readLong() throws IOException {
    	if( type.equals(EndianType.BIG)) {
    		return in.readLong();
    	}
    	else {
    		in.readFully(buf, 0, 8);

            return ((long) (buf[7] & 0xff) << 56) |
            		((long) (buf[6] & 0xff) << 48) |
            		((long) (buf[5] & 0xff) << 40) |
            		((long) (buf[4] & 0xff) << 32) |
            		((long) (buf[3] & 0xff) << 24) |
            		((long) (buf[2] & 0xff) << 16) |
            		((long) (buf[1] & 0xff) << 8) | ((long) (buf[0] & 0xff));
    	}
    }
    @Override
    public double readDouble() throws IOException {
    	long l;
    	if( type.equals(EndianType.BIG)) {
    		return in.readDouble();
    	}
    	else {
    		in.readFully(buf, 0, 8);
            l = ((long) (buf[7] & 0xff) << 56) |
                ((long) (buf[6] & 0xff) << 48) |
                ((long) (buf[5] & 0xff) << 40) |
                ((long) (buf[4] & 0xff) << 32) |
                ((long) (buf[3] & 0xff) << 24) |
                ((long) (buf[2] & 0xff) << 16) |
                ((long) (buf[1] & 0xff) << 8) |
                ((long) (buf[0] & 0xff));

            return Double.longBitsToDouble(l);
    	}
    }

    /** skip ahead in the stream
     * @param num number of bytes to read ahead
     */
    @Override
    public int skipBytes(int num) throws IOException {
        return in.skipBytes(num);
    }

	@Override
	public void readFully(byte[] b) throws IOException {
		in.readFully(b);
		
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		in.readFully(b, off, len);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return in.readBoolean();
	}

	@Override
	public int readUnsignedShort() throws IOException {
	    	if( type.equals(EndianType.LITTLE)) {
	    		return in.readUnsignedShort();
	    	}
	    	else {
	    		in.readFully(buf, 0, 2);
	            return (int) (((buf[1] & 0xff) << 8) | (buf[0] & 0xff));
	    	}
	}

	@Override
	public char readChar() throws IOException {
		return in.readChar();
	}

	@Override
	public float readFloat() throws IOException {
		return in.readFloat();  // Not valid for LITTLE endian
	}

	@SuppressWarnings("deprecation")
	@Override
	public String readLine() throws IOException {
		return in.readLine();
	}

	@Override
	public String readUTF() throws IOException {
		return in.readUTF();
	}
	
	/**
	 * Use for debugging.
	 * @return the next n bytes in an array
	 */
	public byte[] readBytes(int count) throws IOException {
		byte[] bytes = new byte[count];
		in.readFully(bytes,0,count);
		return bytes;
	}
}
