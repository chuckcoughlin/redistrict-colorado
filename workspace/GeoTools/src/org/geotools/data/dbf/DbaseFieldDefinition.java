package org.geotools.data.dbf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.openjump.io.EndianAwareInputStream;


/**
 * Class to hold information about a field in the file
 */
public class DbaseFieldDefinition implements DbaseConstants{

	private static final String CLSS = "DbfFieldDefinition";
	private static final Logger LOGGER = Logger.getLogger(CLSS); 
	private static final boolean DEBUG = true;

    public StringBuffer fieldname = new StringBuffer(DBF_NAMELEN);
    public char fieldtype;
    public int  fieldstart;
    public int  fieldlen;
    public int  fieldnumdec;

    public DbaseFieldDefinition() {
    }
    
    public DbaseFieldDefinition(String fieldname, char fieldtype, int fieldlen, int fieldnumdec){
		this.fieldname = new StringBuffer(fieldname);
		this.fieldname.setLength(DBF_NAMELEN);
		this.fieldtype = fieldtype;
		this.fieldlen = fieldlen;
		this.fieldnumdec = fieldnumdec;
	}

	public String toString(){
		return String.format("%s(%d), type %d, %d bytes",fieldname,fieldnumdec,fieldtype,fieldlen);
	}

  /**
   * Reads the Dbf field definition with a specified Charset.
   *
   * @param pos
   * @param instream
   * @param charset
   * @throws IOException
   */
    public void load(int pos, EndianAwareInputStream instream, Charset charset) throws IOException {
        byte[] strbuf = new byte[DBF_NAMELEN]; // <---- byte array buffer for storing string's byte data
	    int j = -1;
	    int term = -1;
        for(int i = 0 ; i < DBF_NAMELEN ; i++){
            byte b = instream.readByte();
            if(b == 0){
                if(term == -1 ) {
                    term = j;
                }
                continue;
            }
            j++;
            strbuf[j] = b; // <---- read string's byte data
        }
        if(term == -1) term = j;
        String name = new String(strbuf, 0, term + 1, charset.name());

        fieldname.append(name.trim()); // <- append byte array to String Buffer
        fieldtype=(char)instream.readUnsignedByte();
        fieldstart=pos;
        instream.skipBytes(4);
        switch(fieldtype){
            case 'C':
            case 'c':
            case 'D':
            case 'L':
            case 'M':
            case 'G':
                fieldlen = instream.readUnsignedByte();
                fieldnumdec = instream.readUnsignedByte();
                fieldnumdec = 0;
                break;
		    case 'N':
		    case 'n':
            case 'F':
            case 'f':
                fieldlen = instream.readUnsignedByte();
                fieldnumdec = instream.readUnsignedByte();
                break;
            default:
                LOGGER.warning(String.format("%s.load: Help - wrong field type (%d)",CLSS,fieldtype));
        }
        if(DEBUG) LOGGER.info(String.format("%s: %s type %s, fmt %d.%d",CLSS,name,fieldtype,fieldlen,fieldnumdec));

        instream.skipBytes(14);

    }
}
