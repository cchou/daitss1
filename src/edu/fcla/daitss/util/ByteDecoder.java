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
package edu.fcla.daitss.util;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;

/**
 * Byte Decoder class provide general decoding mechanisms for parsing of a byte array
 * @author carol
 */
public class ByteDecoder {
	private byte[] data = null;	 // the byte array which hold the data (shall be uncompressed)
	private int currentIndex = 0;// the current decoding index of the byte array
	
	/**
	 * constructor
	 * @param _data
	 */
	public ByteDecoder(byte[] _data) {
		data = _data.clone();
		// start from index 0;
		currentIndex = 0;
	}
	
	/**
	 * Whether the decoder reaches the end of the byte buffer
	 * @return boolean
	 */
	public boolean isEnd() {
		if (currentIndex >= data.length)
			return true;
		else
			return false;
	}
	
	/**
	 * convert a byte value to an integer.  The byte value is a signed value in the range -2^7 - 2^7-1 
	 * (this is java default).  The return integer will be in the value of (0 - 255)
	 * @param b
	 * @return integer
	 */
	private int byteToInt(byte b) {
	    return (int) b & 0xFF;
	}
	
	/**
	 * get the next byte in the byte array
	 * @return byte
	 * @throws FatalException
	 */
	private byte getNextByte() throws FatalException{
		byte byteData;
		
		if (isEnd()) {
			currentIndex = data.length - 1;
			throw new FatalException("passing the end of byte buffer");
		} else {
			byteData = data[currentIndex];
			// increase our byte pointer
			currentIndex++;
		}
		
		return byteData;
	}
	
	/**
	 * @return integer
	 */
	public int getCurrentPointer() {
		return currentIndex;
	}
	
	/**
	 * get the next fews bytes (depends on the size of array _buffer) from the byte array
	 * @param _buffer
	 * @return integer
	 * @throws FatalException
	 */
	public int read(byte[] _buffer) throws FatalException {
		int byteRead = -1;
		
		if (_buffer != null) {
			for (int i = 0; i < _buffer.length; i++) {
				_buffer[i] = getNextByte();
				byteRead++;
			}
		} 
		return byteRead;
	}
	
	/** 
	 * get the next fews bytes from the byte array and convert them into requested endian-ness
	 * @param numBytes
	 * @param endian
	 * @return long
	 * @throws FatalException
	 */
	public long readBytes(int numBytes, String endian) throws FatalException {

		// get the first byte
		long dataValue = byteToInt(getNextByte());
			
		// get the rest of bytes and shift if necessary 
		for (int i = 1; i < numBytes; i++) {
			long byteValue = byteToInt(getNextByte());
			if (endian.equals(DataFile.BYTE_ORDER_LE)) {
				dataValue = dataValue + (byteValue << 8 * i);
			} else if (	endian.equals(DataFile.BYTE_ORDER_BE) || 
					endian.equals(DataFile.BYTE_ORDER_UNKNOWN) ||
					endian.equals(DataFile.BYTE_ORDER_NA)) {
				dataValue = (dataValue << 8) + byteValue;
			}
		}
		return dataValue;
	}

	/**
	 * skip the next fews bytes in the byte array
	 * @param numBytes
	 * @throws FatalException
	 */
	public void skipBytes(int numBytes) throws FatalException {
		currentIndex += numBytes;
		if (isEnd()) {
			currentIndex = data.length;
		}			
	}
	
	/**
	 * how many more bytes are left in the byte array
	 * @return integer
	 */
	public int bytesLeft() {
		int bytesLeft = data.length - currentIndex;
		return bytesLeft;
	}
	
	/**
	 * reset the byte decoder pointer
	 */
	public void reset() {
		currentIndex = 0;
	}
}
