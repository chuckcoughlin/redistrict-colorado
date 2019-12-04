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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.BufferedInputStream;

/**
 *  A class that gives most of the functionality of DataInputStream, but is endian aware.
 *  Uses a real java.io.DataInputStream to actually do the writing.
 */
public class EndianAwareDataInputStream  {
    private final DataInputStream in;
    private final EndianType type;
    private byte[] buf = new byte[8];

    /** Creates new EndianDataInputStream */
    public EndianAwareDataInputStream(BufferedInputStream bis, EndianType et) {
        this.in = new DataInputStream(bis);
        this.type = et;
    }

    /** close the stream**/
    public void close() throws IOException {
        in.close();
    }

    /** When reading a byte, endian doesn't matter */
    public byte readByte() throws IOException {
        return in.readByte();
    }

     /** Endian won't matter in this case either */
    public void readByteLEnum(byte[] b) throws IOException {
        in.readFully(b);
    }
    
    public int readUnsignedByte() throws IOException {
        return in.readUnsignedByte();
    }

    public short readShort() throws IOException {
    	if( type.equals(EndianType.BIG)) {
    		return in.readShort();
    	}
    	else {
    		in.readFully(buf, 0, 2);
            return (short) (((buf[1] & 0xff) << 8) | (buf[0] & 0xff));
    	}
    }

    /** read a 32bit int in BE*/
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
    public int skipBytes(int num) throws IOException {
        return in.skipBytes(num);
    }
}
