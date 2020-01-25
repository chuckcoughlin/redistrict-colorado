package org.geotools.data.dbf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openjump.feature.AttributeType;
import org.openjump.feature.BasicFeature;
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureDataset;
import org.openjump.feature.FeatureSchema;
import org.openjump.io.EndianAwareInputStream;


/**
 * This class represents a DBF (or DBase) file.<p>
 * Construct it with a filename (including the .dbf)
 * this causes the header and field definitions to be read.<p>
 * Later queries return rows or columns of the database.
 * <hr>
 * @author <a href="mailto:ian@geog.leeds.ac.uk">Ian Turton</a> Centre for
 * Computational Geography, University of Leeds, LS2 9JT, 1998.
 */
public class DbaseFile  {
	private static final String CLSS = "DbfFile";
	private static final Logger LOGGER = Logger.getLogger(CLSS); 
    private SimpleDateFormat simpleDataParser = new SimpleDateFormat("yyyyMMdd");
	private final Charset charset;
    private final DbaseHeader header;
    private Map<String,String> uniqueStrings;
    private DbaseFieldDefinition[] fielddef;
    private FeatureDataset features = null;
    private FeatureSchema schema = null;

    /**
     * Constructor. 
     * @param charset name of the character set.
     */
    public DbaseFile(Charset charset) {
    	this.charset = charset;
    	this.simpleDataParser.setLenient(true);
    	this.header = new DbaseHeader();
    	this.schema = new FeatureSchema();
    }
    
    public FeatureSchema getFeatureSchema() { return this.schema; }
    /**
     * Initializer: Read the open stream and populate the Dbfile. This
     * loads the header and field definitions, but not the records.
     * @param in InputStream ready for reading
     * @exception IOException If the file can't be opened.
     */
    public void load(EndianAwareInputStream instream) throws Exception {
    	header.load(instream);
    	// A map to store a unique reference for identical field value
    	uniqueStrings = new HashMap<>();
    	int widthsofar;
    	fielddef = new DbaseFieldDefinition[header.getFieldCount()];
    	widthsofar = 1;

    	for (int index = 0; index < header.getFieldCount(); index++) {
    		fielddef[index] = new DbaseFieldDefinition();
    		fielddef[index].load(widthsofar, instream, charset);
    		widthsofar += fielddef[index].fieldlen;
    	}

    	instream.skipBytes(1); // end of field defs marker
    	LOGGER.fine("Dbf file initialized");
    }
    
    /**
     * Load the data records. Read until EOF. Each record is a Feature with 
     * attributes per the definition. The features do not yet have a geometry.
     * @param in InputStream.
     * @return the number of records read.
     */
    public int loadFeatures(EndianAwareInputStream in) {
    	this.schema = new FeatureSchema();
    	this.features = new FeatureDataset(schema);
        
        schema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
        int numfields = header.getFieldCount();
        for (int j = 0; j < numfields; j++) {
            AttributeType type = AttributeType.valueOf((getFieldType(j).toUpperCase()));
            schema.addAttribute( getFieldName(j), type );
        }
        
    	int count = 0;
    	try {
    		while(count<header.getLastRecord()) {
    			Feature feature = new BasicFeature(schema);
    			byte[] bytes = getNextRecord(in);
    			for (int y = 0; y < numfields; y++) {
                    feature.setAttribute(y + 1, ParseRecordColumn(bytes, y));
                }
    			features.add(feature);
    			count++;
    		}
    		LOGGER.info(String.format("%s: Successfully loaded %d features (%d attributes)", CLSS,count,numfields));
    	}
    	catch(Exception ex) {
    		LOGGER.warning(String.format("%s: Error parsing record %d (%s)", CLSS,count,ex.getLocalizedMessage()));
    	}
    	return count;
    }
	
    /**
     * Returns the header.
     */
    public DbaseHeader getHeader() {
        return this.header;
    }

    public FeatureDataset getFeatureDataset() { return this.features; }
    
    public String getFieldName(int col) {
    	return fielddef[col].fieldname.toString();
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
     * Parses the next record from the open stream. 
     * @param row - the row to fetch
     * @exception IOException on read error.
     */
    public byte[] getNextRecord(EndianAwareInputStream in) throws IOException { 
        byte[] strbuf = new byte[header.getRecordSize()]; // <---- byte array buffer fo storing string's byte data
        in.readFully(strbuf);
        return strbuf;
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


    private DateFormat lastFormat = simpleDataParser;
    private Date parseDate(String s) throws ParseException {

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
