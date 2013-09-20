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

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.util.ByteDecoder;
import edu.fcla.daitss.util.ByteReader;

/**
 * @author ?
 *
 */
public class Atom {
	static protected final int MIN_HEADER_SIZE = 8; // minimum size for the atom header
	
	protected int		type = -1;		// code describing the atom type, normally fourcc
	protected String 	typeStr = null; // for debugging purpose
	protected long 		size;			// atom size , including both the atom header and content.
	
	protected int		headerSize; 	// atom header size, this could either be 8 or 16 bytes.
	
	/**
	 * copy constructor
	 * @param _atom
	 */
	Atom(Atom _atom) {
		type = _atom.type;
		size = _atom.size;
		typeStr = _atom.typeStr;
		headerSize = _atom.headerSize;
	}
	
	/**
	 * construct an atom by reading the header information from the file
	 * @param _reader
	 * @throws FatalException 
	 */
	Atom(ByteReader _reader) throws FatalException {
		// read in the atom header.
		size = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);  // atom size

		type = (int) _reader.readBytes(4, DataFile.BYTE_ORDER_BE);  // atom type
		String tempStr = Integer.toHexString(type);
		
		if (typeStr == null )
			typeStr = new String("");
		
		// convert from HEX ASCII CODE to string
		for (int i=0; i< (tempStr.length() - 1); i+=2) {
			int n = Integer.parseInt(tempStr.substring(i, i+2), 16);
			n &= 0xFF; // in case of n < 0
			char ch = (char)n;
			typeStr += ch;
		}
		
		headerSize = MIN_HEADER_SIZE;
		
		if (size == 1) { 
        	// if size = 1, read the next 8 bytes which contains the actual atom size.
			size = _reader.readBytes(8, DataFile.BYTE_ORDER_BE);
			headerSize += 8;
        } else if (size == 0) {
        	// this shall be last top-level atom of the file.  In this case,
        	// the size should be the remaining bytes plus the atom header.
        	size = _reader.bytesLeft() + headerSize;
        }
	}
	
	/**
	 * construct an atom by reading the header information from the file
	 * @param _decoder 
	 * @throws FatalException 
	 */
	Atom(ByteDecoder _decoder) throws FatalException {
		// read in the atom header.
		size = _decoder.readBytes(4, DataFile.BYTE_ORDER_BE);  // atom size

		type = (int) _decoder.readBytes(4, DataFile.BYTE_ORDER_BE);  // atom type
		String tempStr = Integer.toHexString(type);
		
		if (typeStr == null )
			typeStr = new String("");
		
		// convert from HEX ASCII CODE to string
		for (int i=0; i< (tempStr.length() - 1); i+=2) {
			int n = Integer.parseInt(tempStr.substring(i, i+2), 16);
			n &= 0xFF; // in case of n < 0
			char ch = (char)n;
			typeStr += ch;
		}
		
		headerSize = MIN_HEADER_SIZE;
		
		if (size == 1) { 
        	// if size = 1, read the next 8 bytes which contains the actual atom size.
			size = _decoder.readBytes(8, DataFile.BYTE_ORDER_BE);
			headerSize += 8;
        } else if (size == 0) {
        	// this shall be last top-level atom of the file.  In this case,
        	// the size should be the remaining bytes plus the atom header.
        	size = _decoder.bytesLeft() + headerSize;
        }
	}
	
	/**
	 * @return long primitive
	 */
	public long getSize() {return size;}
	/**
	 * @return long primitive
	 */
	public long getType() {return type;}
	
	/**
	 * Check to see if this atom is _type type of atom.
	 * @param _type
	 * @return boolean 
	 */
	public boolean isType(int _type) {
		boolean yes = false;
	
		if (type == _type)
			yes = true;
		
		return yes;
	}
	
	/**
	 * Check to see if this atom is _type type of atom.
	 * @param _type
	 * @return boolean
	 */
	public boolean isType(String _type) {
		boolean yes = false;
	
		if (typeStr.toUpperCase().equals(_type.toUpperCase()))
			yes = true;
		
		return yes;
	}

	/**
	 * write the atom header (no data)
	 * @param _writer
	 * @throws FatalException
	 */
	public void writeHeader(RandomAccessFile _writer) throws FatalException {
		if (_writer != null) {
			try {
				_writer.writeInt((int) size); // 4 bytes Int
				_writer.writeInt((int) type); // 4 bytes Int
			} catch (IOException e) {}
		}
	}
	
	/**
	 * copy the atom content from the _reader to the _writer
	 * @param _reader
	 * @param _writer
	 * @throws FatalException
	 */
	public void copy(ByteReader _reader, RandomAccessFile _writer) throws FatalException {
		if (_writer != null) {
			try {
				_writer.writeInt((int) size); // 4 bytes Int
				_writer.writeInt((int) type); // 4 bytes Int
				// copy the atom data from _reader to _writer
				int dataSize = (int) size - headerSize;
				if (dataSize <= 1024) {
					byte[] data = new byte[dataSize];
					_reader.read(data);
					_writer.write(data);
				} else {
					byte[] data = new byte[1024];
			        int byteRead = 0;
			        // to prevent overflowing the heap space, max the buffer to 1024 bytes
			        while (dataSize - byteRead > 0) {
			        	if (dataSize - byteRead < 1024) {
			        		data = null;
			        		data = new byte[dataSize - byteRead];
			        	}
			        	_reader.read(data);
			            _writer.write(data);
			            byteRead += data.length;
			        }
				}
			} catch (IOException e) {}
		}
	}
	

	/**
	 * skip this atom
	 * @param _reader
	 * @throws FatalException
	 */
	protected void skip(ByteReader _reader) throws FatalException {
		// deduct the size of the atom header since its already read
		int bytesToSkip = (int) (size - headerSize);
		// skip the parsing of this atom
		_reader.skipBytes(bytesToSkip);
	}
	
	protected void skip(ByteDecoder _decoder) throws FatalException {
		// deduct the size of the atom header since its already read
		int bytesToSkip = (int) (size - headerSize);
		// skip the parsing of this atom
		_decoder.skipBytes(bytesToSkip);
	}
}
