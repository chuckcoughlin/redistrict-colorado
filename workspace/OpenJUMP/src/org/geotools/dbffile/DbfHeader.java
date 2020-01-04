package org.geotools.dbffile;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openjump.io.EndianAwareInputStream;
import org.openjump.io.EndianType;


/**
 * This class holds information contained in the header of a DBF (or DBase) file.
 * Construct it with a filename (including the .dbf)
 * this causes the header and field definitions to be read.<p>
 * Later queries return rows or columns of the database.
 * <hr>
 * @author <a href="mailto:ian@geog.leeds.ac.uk">Ian Turton</a> Centre for
 * Computational Geography, University of Leeds, LS2 9JT, 1998.
 */
public class DbfHeader  {
	private static final String CLSS = "DbfHeader";
	private static final Logger LOGGER = Logger.getLogger(CLSS); 
	private static final boolean DEBUG = false;
	private int id = 0;
	private int dataOffset = 0;
	private int fieldCount = 0;
	private int fileSize = 0;
	private int lastRecord = 0;
	private int recordSize = 0;
	private int updateDay = 0;
	private int updateMonth = 0;
	private int updateYear = 0;

	/**
	 * Reads the header of a dbf file.
	 * @param file file Stream attached to the input file
	 * @exception IOException read error.
	 */
	public void load(EndianAwareInputStream eastream) throws IOException {
		eastream.setType(EndianType.LITTLE);
		id = eastream.readUnsignedByte();
		if(DEBUG) LOGGER.info("DbfHeader id: " + id);

		updateYear = eastream.readUnsignedByte() + DbfConstants.DBF_CENTURY;
		updateMonth = eastream.readUnsignedByte();
		updateDay = eastream.readUnsignedByte();
		if(DEBUG) LOGGER.info(String.format("DbfHeader last update: %d/%d/%d", updateDay, updateMonth, updateYear));

		lastRecord = eastream.readInt();
		if(DEBUG) LOGGER.info("DbfHeader last record: " + lastRecord);

		dataOffset = (char)eastream.readShort();
		if(DEBUG) LOGGER.info("DbfHeader data offset: " + dataOffset);

		recordSize = (char)eastream.readShort();
		if(DEBUG) LOGGER.info("DbfHeader record size: " + recordSize);

		fileSize = (recordSize * lastRecord) + dataOffset + 1;
		if(DEBUG) LOGGER.info("DbfHeader file size :" + fileSize);

		fieldCount = (int)((dataOffset - DbfConstants.DBF_BUFFSIZE - 1) / DbfConstants.DBF_BUFFSIZE);
		if(DEBUG) LOGGER.info("DbfHeader number of fields :" + fieldCount);

		eastream.skipBytes(20);
	}

	public int getId() { return id; }
	public int getDataOffset() { return dataOffset; }
	public int getFieldCount() { return fieldCount; }
	public int getFileSize() { return fileSize; }
	public int getLastRecord() { return lastRecord; }
	public int getRecordSize() { return recordSize; }
	public int getUpdateDay() { return updateDay; }
	public int getUpdateMonth() { return updateMonth; }
	public int getUpdateYear() { return updateYear; }

}
