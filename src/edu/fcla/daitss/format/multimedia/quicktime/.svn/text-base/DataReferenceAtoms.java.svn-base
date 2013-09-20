/*
 * DAITSS Copyright (C) 2007 University of Florida
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package edu.fcla.daitss.format.multimedia.quicktime;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.util.ByteDecoder;
import edu.fcla.daitss.util.ByteReader;

/**
 * represents
 *
 */
public class DataReferenceAtoms  extends Atom {
	/**
	 * class constant
	 */
	public static final int TYPE = 0x64726566; 	// "dref"
	/**
	 * class constant
	 */
	public static final int DATA_REFERENCE_HEADER_SIZE = 16;
	
	byte[]		version = new byte[1];
	byte[] 		flags = new byte[3];
	
	int 		noOfEntries = 0;

	Vector<DataReference> dataReference = new Vector<DataReference>();
	
	/** 
	 * construct a Data Reference Atom using the information in the basic atom object.
	 * @param _atom
	 */
	DataReferenceAtoms(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract Data Reference metadata.  
	 * @param _reader
	 * @param qtFile
	 * @throws FatalException 
	 */
	void parse(ByteDecoder _reader, QuickTime qtFile) throws FatalException {
		long bytesRead = headerSize;  
		
		// read version(1), flags(3)
		_reader.read(version);
		_reader.read(flags);
		bytesRead += 4;
		
		// read in the number of table entries.
		noOfEntries = (int) _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		bytesRead += 4;
		
		for (int i = 0; i < noOfEntries; i++) {
			DataReference dataRF = new DataReference(_reader);
			dataRF.processURL(qtFile);
			dataReference.add(dataRF);
		}
	}
	
	/**
	 * localize the information in the data referenc atom
	 * @param _reader
	 * @param qtFile
	 * @throws FatalException 
	 */
	public void localize(ByteReader _reader, QuickTime qtFile) 
	throws FatalException {
	    
		// read version(1), flags(3)
		_reader.read(version);
		_reader.read(flags);
			
		// read in the number of table entries.
		noOfEntries = (int) _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
	
		size = DATA_REFERENCE_HEADER_SIZE;
		for (int i = 0; i < noOfEntries; i++) {
			DataReference dataRF = new DataReference(_reader);
			dataRF.localize(qtFile);
			dataReference.add(dataRF);
			size += dataRF.dfSize;
		}
	}
	
	/**
	 * write the information in the data reference atoms to a file
	 * @param _writer
	 * @throws FatalException
	 */
	public void write( RandomAccessFile _writer) throws FatalException {
		try {
			super.writeHeader(_writer);
			_writer.write(version);
			_writer.write(flags);
			_writer.writeInt(noOfEntries);
			
			for (Iterator iter = dataReference.iterator(); iter.hasNext();){								
				DataReference df = (DataReference) iter.next();
				df.write(_writer);
			}
		} catch (IOException e) {throw new FatalException(e); }
	}
}

