package org.geotools.dbffile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openjump.io.EndianAwareInputStream;
import org.openjump.io.EndianType;


/**
 * This class represents a DBF (or DBase) file.<p>
 * Construct it with a filename (including the .dbf)
 * this causes the header and field definitions to be read.<p>
 * Later queries return rows or columns of the database.
 * <hr>
 * @author <a href="mailto:ian@geog.leeds.ac.uk">Ian Turton</a> Centre for
 * Computational Geography, University of Leeds, LS2 9JT, 1998.
 */
public class DbfFile  {
	private static final String CLSS = "DbfFile";
	private static final Logger LOGGER = Logger.getLogger(CLSS); 
    private SimpleDateFormat simpleDataParser = new SimpleDateFormat("yyyyMMdd");
	private final Charset charset;
    private int dbf_id;
    private int last_update_d;
    private int last_update_m;
    private int last_update_y;
    private int last_rec;
    private int rec_size;
    private long data_offset;
    private long filesize;
    private int numfields;
    private Map<String,String> uniqueStrings;

    public DbfFieldDefinition[] fielddef;

    /**
     * Constructor. 
     * @param charset name of the character set.
     */
    public DbfFile(Charset charset) {
    	this.charset = charset;
    	this.simpleDataParser.setLenient(true);
    }
    
    /**
     * Initializer: Read the open stream and construct the object. 
     * @param file The file to be opened, includes path and .dbf
     * @exception IOException If the file can't be opened.
     */
    public void load(InputStream in) throws Exception {
    	try (EndianAwareInputStream instream = new EndianAwareInputStream(in)) {
    		new DbfFileHeader(instream);
    		// A map to store a unique reference for identical field value
    		uniqueStrings = new HashMap<>();
    		int widthsofar;
    		fielddef = new DbfFieldDefinition[numfields];
    		widthsofar = 1;

    		for (int index = 0; index < numfields; index++) {
    			fielddef[index] = new DbfFieldDefinition();
    			fielddef[index].setup(widthsofar, instream, charset);
    			widthsofar += fielddef[index].fieldlen;
    		}

    		instream.skipBytes(1); // end of field defs marker
    	}
    	catch(Exception ex) { LOGGER.severe(ex.getLocalizedMessage());}
    	LOGGER.fine("Dbf file initialized");
    }

	
    /**
     * Returns the date of the last update of the file as a string.
     */
    public String getLastUpdate() {
        return last_update_d + "/" + last_update_m + "/" + last_update_y;
    }

    /**
     * Returns the number of records in the database file.
     */
    public int getLastRec() {
        return last_rec;
    }

    /**
     * Returns the size of the records in the database file.
     */
    public int getRecSize() {
        return rec_size;
    }

    /**
     * Returns the number of fields in the records in the database file.
     */
    public int getNumFields() {
        return numfields;
    }

    public String getFieldName(int col) {
        return (fielddef[col].fieldname).toString();
    }

    public String getFieldType(int col) {
        char type = fielddef[col].fieldtype;
        String realtype;

        switch (type) {
            case 'C':
                realtype = "STRING";
                break;

            case 'N':
                if (fielddef[col].fieldnumdec == 0) {
                    if (fielddef[col].fieldlen > 9) {
                        realtype = "LONG";
                    } 
                    else {
                        realtype = "INTEGER";
                    }
                }
                else {
                    realtype = "DOUBLE";
                }
                break;

            case 'F':
                realtype = "DOUBLE";
                break;

            case 'D': //Added by [Jon Aquino]
                realtype = "DATE";
                break;

            case 'L': //Added by [Jon Aquino]
                realtype = "BOOLEAN";
                break;

            default:
                realtype = "STRING";
                break;
        }

        return realtype;
    }

    /**
     * Returns the size  of the database file.
     */
    public long getFileSize() {
        return filesize;
    }



    /**
     * fetches the <i>row</i>th row of the file
     * @param row - the row to fetch
     * @exception java.io.IOException on read error.
     */
    public byte[] GetDbfRec(long row) throws java.io.IOException {  //[sstein 9.Sept.08]
    	
        //rFile.seek(data_offset + (rec_size * row));

        //Multi byte character modification thanks to Hisaji ONO
        byte[] strbuf = new byte[rec_size]; // <---- byte array buffer fo storing string's byte data
        //dFile.readFully(strbuf);
        return strbuf;		 //[sstein 9.Sept.08]
    }


    /**
     * Get a field value from the dbf record data (byte[]) and the field index
     * @param rec the byte array representing the record
     * @param wantedCol the wanted column
     * @return an object representing the field
     * @throws Exception
     */
    public Object ParseRecordColumn(byte[] rec, int wantedCol) throws Exception {
        int start;
        int end;
        start = fielddef[wantedCol].fieldstart;
        int len = fielddef[wantedCol].fieldlen;		 //[sstein 9.Sept.08]
        end = start + len;
        String s;
        String masterString;

        switch (fielddef[wantedCol].fieldtype) {
            
            case 'C': //character
                while ((start < end) &&
                       (rec[end-1] == ' ' ||    //[sstein 9.Sept.08]
                        rec[end-1] == 0))       //[mmichaud 16 june 2010]
                        end--;  //trim trailing spaces
                //[sstein 9.Sept.08] + [Matthias Scholz 3. Sept.10] Charset added
                s = new String(rec, start, end - start, charset.name());
                masterString = uniqueStrings.get(s);
                if (masterString != null) {
                    return masterString;
                } else {
                    uniqueStrings.put(s,s);
                    return s;
                }

            case 'F': //same as numeric, more or less

            case 'N': //numeric

                // fields of type 'F' are always represented as Doubles
                boolean isInteger = fielddef[wantedCol].fieldnumdec == 0
                    && fielddef[wantedCol].fieldtype == 'N';
                boolean isLong = isInteger && fielddef[wantedCol].fieldlen > 9;

                // The number field should be trimmed from the start AND the end.
                // Added .trim() to 'String numb = rec.substring(start, end)' instead. [Kevin Neufeld]
                // while ((start < end) && (rec.charAt(start) == ' '))
                // 	start++;

                String numb = new String(rec, start, len).trim();  //[sstein 9.Sept.08]
                if (isLong) { //its an int
                    try {
                        return Long.parseLong(numb);
                    } catch (java.lang.NumberFormatException e) {
                        return null;
                    }
                }
                else if (isInteger) { //its an int
                    try {
                        return Integer.parseInt(numb);
                    } catch (java.lang.NumberFormatException e) {
                        return null;
                    }
                }
                else { //its a float
                    try {
                        return Double.parseDouble(numb);
                    } catch (java.lang.NumberFormatException e) {
                        // dBase can have numbers that look like '********' !! This isn't ideal but at least reads them
                        return null;
                    }
                }

            case 'L': //boolean added by mmichaud
                String bool = new String(rec, start, len).trim().toLowerCase();
                if (bool.equals("?")) return null;
                else if (bool.equals("t") || bool.equals("y") || bool.equals("1")) return Boolean.TRUE;
                else return Boolean.FALSE;

            case 'D': //date. Added by [Jon Aquino]
                return parseDate(new String(rec, start, len));  //[sstein 9.Sept.08]

            default:
           	    s = new String(rec, start, len);  //[sstein 9.Sept.08]
                masterString = uniqueStrings.get(s);
                if (masterString!=null) {
                    return masterString;
                } else {
                    uniqueStrings.put(s,s);
                    return s;
                }
        }
    }

    /**
     * Internal Class to hold information from the header of the file
     */
    private class DbfFileHeader {
        /**
         * Reads the header of a dbf file.
         * @param file file Stream attached to the input file
         * @exception IOException read error.
         */
        public DbfFileHeader(EndianAwareInputStream instream)
            throws IOException {
            getDbfFileHeader(instream);
        }

        private void getDbfFileHeader(EndianAwareInputStream eastream) throws IOException {
        	eastream.setType(EndianType.LITTLE);
            dbf_id = eastream.readUnsignedByte();
            LOGGER.fine("Dbf header id: " + dbf_id);

            last_update_y = eastream.readUnsignedByte() + DbfConstants.DBF_CENTURY;
            last_update_m = eastream.readUnsignedByte();
            last_update_d = eastream.readUnsignedByte();
            LOGGER.fine(String.format("Dbf last update: %d/%d/%d", last_update_d, last_update_m, last_update_y));

            last_rec = eastream.readInt();
            LOGGER.fine("Dbf las record: " + last_rec);

            data_offset = (char)eastream.readShort();
            LOGGER.fine("Dbf data offset: " + data_offset);

            rec_size = (char)eastream.readShort();
            LOGGER.fine("Dbf rec size: " + rec_size);

            filesize = (rec_size * last_rec) + data_offset + 1;
            LOGGER.fine("Dbf file size :" + filesize);

            numfields = (int)((data_offset - DbfConstants.DBF_BUFFSIZE - 1) / DbfConstants.DBF_BUFFSIZE);
            LOGGER.fine("Dbf number of fields :" + numfields);

            eastream.skipBytes(20);
        }
    }

    private DateFormat lastFormat = simpleDataParser;

    protected Date parseDate(String s) throws ParseException {

        Date date = null;

        if (s.trim().length() != 0 && !s.equals("00000000")) {
            try {
                date = lastFormat.parse(s);
            } catch (ParseException e) {
                String[] patterns = new String[]{"yyyyMMdd", "yy/mm/dd"};
                for (int i = 0; i < patterns.length; i++) {
                    DateFormat df = new SimpleDateFormat(patterns[i]);
                    df.setLenient(true);
                    try {
                        date = df.parse(s);
                        lastFormat = df;
                        break;
                    } catch (ParseException pe) {
                        date = null;
                    }
                }
            }
        }
        return date;
    }


}
