/*
 * CodePage.java
 *
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package org.geotools.data.dbf;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The CodePage is derived from the .cpg file in the Shapefile bundle.
 * It is used when reading the .dbf file.
 */
public class CodePage  {
	private static final String CLSS = "CodePage";
	private static final Logger LOGGER = Logger.getLogger(CLSS); 
	private static final Pattern CODE_PAGE = Pattern.compile(".*?(\\d\\d\\d++)");
	
	/**
	 * Convert the raw code page string into a character set.
	 * 
	 * @param cp
	 * @return
	 */
	public static String getCharSet(String code) { 
		String charset = Charset.defaultCharset().name();
		try {
			if(Charset.isSupported(code)) {
				charset = codeToPage(code);
			}
		}
		catch(IllegalCharsetNameException icne) {
			LOGGER.warning(String.format("%s: Could not interpret charset name %s - use default (%s)", CLSS,code,icne.getLocalizedMessage()));
		}
		return charset;
	}

	/**
	 * Convert the raw code into a neumonic.
	 * @param raw
	 * @return
	 */
    private static String codeToPage(String raw) {
        Matcher matcher = CODE_PAGE.matcher(raw);
        if (matcher.matches() && matcher.groupCount() == 1) {
            String code = matcher.group(1);
            if (code.length() == 3) {
                if (code.equals("708")) return "ISO-8859-6";
                else if (code.equals("932")) return "Shift_JIS";
                else if (code.equals("936")) return "GBK";
                else return "IBM"+code;
            }
            else if (code.length() == 4) {
                return "windows-"+code;
            } 
            else if (code.startsWith("8859")) {
                return "ISO-8859-"+code.substring(4);
            } 
            else return raw.replaceAll(" ","-");
        } 
        else return raw.replaceAll(" ","-");
    }

}
