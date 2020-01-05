package org.openjump.util;


import java.io.UnsupportedEncodingException;

public final class HexDump {
	public static String dump(byte[] array, int offset, int length) {
		final int width = 16;

		StringBuilder builder = new StringBuilder("\n");
		for (int rowOffset = offset; rowOffset < offset + length; rowOffset += width) {
			builder.append(String.format("%06d:  ", rowOffset));

			for (int index = 0; index < width; index++) {
				if (rowOffset + index < array.length) {
					builder.append(String.format("%02x ", array[rowOffset + index]));
				} 
				else {
					builder.append("   ");
				}
			}

			if (rowOffset < array.length) {
				int asciiWidth = Math.min(width, array.length - rowOffset);
				builder.append("  |  ");
				try {
					builder.append(new String(array, rowOffset, asciiWidth, "UTF-8").replaceAll("\r\n", " ").replaceAll("\n", " "));
				} 
				catch (UnsupportedEncodingException ignored) {
					//If UTF-8 isn't available as an encoding then what can we do?!
				}
			}
			builder.append("\n");
		}
		return builder.toString();
	}
}

