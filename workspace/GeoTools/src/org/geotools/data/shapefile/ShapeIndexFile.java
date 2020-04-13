package org.geotools.data.shapefile;

import java.util.logging.Logger;

import org.openjump.io.EndianAwareInputStream;
import org.openjump.io.EndianType;


/**
 * This class represents a ShapeIndex file. It holds a offset/length
 * for all the records in the accompanying .shp file.
 * Construct it with a filename (including the .shx)
 * this causes the header and field definitions to be read.<p>
 * Later queries return rows or columns of the database.
 */
public class ShapeIndexFile {
	private static final String CLSS = "ShapeIndexFile";
	private static final Logger LOGGER = Logger.getLogger(CLSS); 
	private final RecordReference[] records;
	private final ShapefileHeader header;  // Same header as Shpefile

	/**
	 * @param file file name
	 */
    public ShapeIndexFile(int count) {
		this.records = new RecordReference[count];
		this.header = new ShapefileHeader();
	}

    /**
     * Initializer: Read the open stream and construct the object.
     * Entries are 4-byte integers: offset, length.
     * True offset and length in bytes are 2x the number given.
     * Lengths do not include an 8 byte header (record number, length)
     * @param in InputStream ready for reading
     * @exception IOException If the file can't be opened.
     */
    public void load(EndianAwareInputStream instream) throws Exception {
    	header.load(instream);
    	instream.setType(EndianType.BIG);
    	for (int index = 0; index < records.length; index++) {
    		int offset = instream.readInt();
    		int length = instream.readInt();
    		records[index] = new RecordReference(offset,length);
    		//if( index<20 )LOGGER.info(String.format("%s(%d): %d %d",CLSS,index,offset,length));	
    	}
    	LOGGER.info(String.format("%s: Successfully initialized type %d, %d records",CLSS,header.getShapeType(),records.length));
    }
}
