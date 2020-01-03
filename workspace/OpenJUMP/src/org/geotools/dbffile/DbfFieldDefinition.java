package org.geotools.dbffile;

import java.io.IOException;
import java.nio.charset.Charset;

import org.openjump.io.EndianAwareInputStream;


/**
 * Class to hold information about a field in the file
 */
public class DbfFieldDefinition implements DbfConstants{

    static final boolean DEBUG=false;

    public StringBuffer fieldname = new StringBuffer(DBF_NAMELEN);
    public char fieldtype;
    public int  fieldstart;
    public int  fieldlen;
    public int  fieldnumdec;

    public DbfFieldDefinition(){ /* do nothing*/ }

    public DbfFieldDefinition(String fieldname, char fieldtype, int fieldlen, int fieldnumdec){
		this.fieldname = new StringBuffer(fieldname);
		this.fieldname.setLength(DBF_NAMELEN);
		this.fieldtype = fieldtype;
		this.fieldlen = fieldlen;
		this.fieldnumdec = fieldnumdec;
	}

	public String toString(){
		return new String(""+fieldname+" "+fieldtype+" "+fieldlen+
			"."+fieldnumdec);
	}

  // [Matthias Scholz 04.Sept.2010] Charset changes
  /**
   * Sets up the Dbf field definition. For compatibilty reasons, this method is
   * is now a wrapper for the changed/new one with Charset functions.
   *
   * @see #setup(int pos, EndianDataInputStream dFile, Charset charset)
   * 
   * @param pos
   * @param dFile
   * @throws IOException
   */
    public void setup(int pos, EndianAwareInputStream dFile) throws IOException {
	    setup(pos, dFile, Charset.defaultCharset());
    }

  /**
   * Sets up the Dbf field definition with a specified Charset for the fieldnames.
   *
   * @param pos
   * @param dFile
   * @param charset
   * @throws IOException
   */
    public void setup(int pos, EndianAwareInputStream dFile, Charset charset) throws IOException {

        //two byte character modification thanks to Hisaji ONO
        byte[] strbuf = new byte[DBF_NAMELEN]; // <---- byte array buffer for storing string's byte data
	    int j = -1;
	    int term = -1;
        for(int i = 0 ; i < DBF_NAMELEN ; i++){
            byte b = dFile.readByte();
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

        if(DEBUG) System.out.println("Fieldname " + fieldname);
        fieldtype=(char)dFile.readUnsignedByte();
        fieldstart=pos;
        dFile.skipBytes(4);
        switch(fieldtype){
            case 'C':
            case 'c':
            case 'D':
            case 'L':
            case 'M':
            case 'G':
                fieldlen = dFile.readUnsignedByte();
                fieldnumdec = dFile.readUnsignedByte();
                fieldnumdec = 0;
                break;
		    case 'N':
		    case 'n':
            case 'F':
            case 'f':
                fieldlen = dFile.readUnsignedByte();
                fieldnumdec = dFile.readUnsignedByte();
                break;
            default:
                System.out.println("Help - wrong field type: "+fieldtype);
        }
        if(DEBUG) System.out.println("Fieldtype "+fieldtype+" width "+fieldlen+
            "."+fieldnumdec);

        dFile.skipBytes(14);

    }
}
