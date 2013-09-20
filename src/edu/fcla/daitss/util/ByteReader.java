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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;

/**
 * ByteReader represents a random-access reader of byte data
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class ByteReader {
	
	/**
	 * If the data is little-endian it flips the bits (highest-order bit becomes 
	 * the lowest-order bit) etc.
	 * 
	 * @param data	the bytes that will be flipped
	 * @param numBytes	the <code>data</code>'s number of bytes
	 * @param endian	the byte order
	 * @return the bytes flipped
	 */
	public static int flipLeBytes(int data, int numBytes, String endian){
		int flipped = 0;
		if (endian.equals(DataFile.BYTE_ORDER_LE)) {
			int tempVal = data;
			for (int i=0; i<numBytes; i++) {
					tempVal = data << (8 * i); // move byte to 0th byte
					tempVal = tempVal >>> 24; // move byte to 3rd byte
					flipped = flipped + (tempVal << (8 * i));
			}
		} else {
			flipped = data;
		}
		return flipped;
	}
	
	/**
	 * Test driver.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		byte data =0x09;
		byte[] splitData = splitByteInTwo(data);
		System.out.println("Orig: " + Integer.toHexString(data) + 
			"\tsplit[0]: " + Integer.toHexString(splitData[0]) + 
			"\tsplit[1]: " + Integer.toHexString(splitData[1]));
	}
	
	/**
	 * This splits a byte (8 bits) into 2 bits numbers in big-endian fashion 
	 * (the first resulting number comes from the 4 high-order bits) and
	 * then zero pads them into 8-bit bytes
	 * 
	 * @param data the byte to split
	 * @return two 4-bit values zero-padded into 2 bytes
	 */
	public static byte[] splitByteInTwo(byte data) {
		byte[] result = new byte[2];
		result[0] = (byte)(data >> 4);
		byte temp = (byte) (data << 4);
		result[1] = (byte)( temp >> 4);
		return result;
	}
	
	/**
	 * This 'splits' data of 4 bytes into shorter size data types.
	 * For example, given a long (4 bytes) it can split it into 2 shorts (each
	 * with 2 bytes). Can split the long into 4 1-byte values, 3 1-byte values,
	 * 2 1-bytes values, 1 1-byte value, 2 2-byte values, or 1 2-byte value
	 * of both little-endian and big-endian data. Note that this is only
	 * applicable when the data is left-justified in the long value. For
	 * example, the first short value in the long in big-endian data would
	 * be byte0,byte1. The first short value in the long in little-endian
	 * data would be byte1,byte0. The first byte value in the long in both
	 * big-endian and little-endian data would be byte0.
	 * <BR>
	 * There's 2 cases handled here:<BR>
	 * 1. Split big-endian data into 1-byte or 2-byte segments or
	 * 	little-endian data into 1-byte segments. Because tiffs store values
	 *		left-justified (in the lower-numbered bytes) in the offset, this just entails
	 *		extracting 1 or 2-byte segments from the left.<BR>
	 * 2. Split little-endian into 2-byte segments. This entails taking 2
	 * 	bytes at a time from the left and switching the bytes.
	 * 
	 * @param numWanted	number of new (smaller) data values wanted
	 * @param	smallerSize	the size in bytes of the new smaller data values
	 * @param data	the data to split into the smaller size data types
	 * @param endian	the byte order
	 * @return	an array of the new smaller data values
	 * @throws FatalException
	 */
	public static int[] splitLeftPackedData(int numWanted, int smallerSize, 
			long data, String endian) 
		throws FatalException {
		int[] newData = new int[numWanted];
		if ((numWanted * smallerSize) > 4 ||
			(numWanted * smallerSize) < 1) {
				Informer.getInstance().fail("edu.fcla.daitss.file.util.ByteReader",
					"splitData(int,int,long,String)",
					"Illegal arguments",
					"numWanted: " + numWanted + "\tsmallerSize: " + smallerSize,
					new FatalException("numWanted*smallerSize must equal 1, 2, 3 or 4"));
		}
		int tempVal = (int) data;
		if ((endian.equals(DataFile.BYTE_ORDER_BE) && 
				(smallerSize == 1 || smallerSize == 2)) || 
			smallerSize == 1){
		// CASE 1
			for (int i=0; i<numWanted; i++){
				newData[i] = (int)(tempVal >>> (32 - (8*smallerSize)));
				tempVal = tempVal << (8*smallerSize);
			}
		} else if (endian.equals(DataFile.BYTE_ORDER_LE) && smallerSize == 2){
		// CASE 2
			// isolate the bytes
			int[] theBytes = new int[4];
			for (int i=0; i<theBytes.length; i++) {
				theBytes[i] = tempVal << (8 * i); // move byte to 0th byte
				theBytes[i] = theBytes[i] >>> 24; // move byte to 3rd byte
			}
			// recombine the bytes
			for (int i=0, b=0; i<numWanted; i++, b+=2) {
				newData[i] = (theBytes[(b+1)] << 8) + theBytes[b];
			}
		} else {
			// asked for the data to be split into <1 bytes or >2 bytes - illegal
			Informer.getInstance().fail("edu.fcla.daitss.file.util.ByteReader",
				"splitData(int,int,long,String)",
				"Illegal arguments",
				"smallerSize: " + smallerSize,
				new FatalException("smallerSize can be 1 or 2"));
		}
		
		return newData;
	}
	
	/**
	 * The last reading location within a file
	 */
	private long lastLocation = 0L;
	
	/**
	 * The file reader
	 */
	private RandomAccessFile reader = null;
	
	/**
	 * Instantiates a <code>RandomAccessFile</code> reader for a file
	 * 
	 * @param file	a file to read
	 * @param mode	the mode in which to read the file
	 * @throws FatalException
	 */
	public ByteReader(File file, String mode) throws FatalException {
		try {
			reader = new RandomAccessFile(file, mode);
		} catch (FileNotFoundException e) {
			Informer.getInstance().fail(this, "ByteReader(File, String)",
				"I/O error",
				"Could not read the file",
				e);
		}
	}
	
	/**
	 * Returns the number of bytes left to read in the file.
	 * Assumes that the bytes are being read sequentially starting
	 * from the file reader's current file pointer until the
	 * end of the file.
	 * 
	 * @return number of bytes left to read
	 * @throws FatalException
	 */
	public long bytesLeft() throws FatalException {
		long bytesLeft = 0;
		try {
			bytesLeft =  reader.length() - reader.getFilePointer();
		} catch (IOException e) {
			Informer.getInstance().fail(this, "goBack()",
				"I/O error",
				"Could not get the file length or pointer",
				e);
		}
		return bytesLeft;
	}
	
	/**
	 * Closes a file and releases system resources used to read it.
	 * 
	 * @throws FatalException
	 */
	public void close() throws FatalException {
		try {
			this.reader.close();
		} catch (IOException e) {
			Informer.getInstance().fail(this, "close()",
				"I/O error", "Could not close file", e);
		}
	}
	
	/**
	 * Returns the current offset in this file (must already be reading a file)
	 * 
	 * @return the current offset in this file
	 */
	public long getFilePointer() {
		long position =  -1;
		try {
			position = this.reader.getFilePointer();
		} catch (IOException e) {
			// do nothing - just return -1
		}
		return position;
	}
	
	/**
	 * Moves the file reading pointer back to where it was before calling
	 * <code>seek(long)</code>.
	 * 
	 * @throws FatalException
	 *
	 */
	public void goBack() throws FatalException {
		try {
			reader.seek(lastLocation);
		} catch (IOException e) {
			Informer.getInstance().fail(this, "goBack()",
				"I/O error",
				"Could not go back to the last location in the file",
				e);
		}
	}
	
	/**
	 * Reads a certain number of bytes (the size of the <code>data</code> array) and 
	 * puts them into an array of bytes. Reads from the beginning of the file.
	 * 
	 * @param data		where to put the data read
	 * @return	the number of bytes read or <code>-1</code> if the end of 
	 * 					the file was reached before reading all the bytes
	 * @throws FatalException
	 */
	public int read(byte[] data) throws FatalException {
		int result = -1;
		if (data != null) {
			try {
				result = reader.read(data);
			} catch (IOException e) {
				Informer.getInstance().fail(this, "read(byte[])",
					"I/O error",
					"Could not read " + data.length + " bytes at offset "
					+ this.getFilePointer() + " in file",
					e);
			}
		} 
		return result;
	}

	/**
	 * Reads <code>numBytes</code> bytes in the file starting from the
	 * file pointer of the <code>RandomAccessFile</code>. It shifts
	 * the bytes depending on the file's endian order. Valid endian orders
	 * are <code>DataFile.BYTE_ORDER_LE</code>,
	 * <code>DataFile.BYTE_ORDER_BE</code>, 
	 * <code>DataFile.BYTE_ORDER_NA</code> or
	 * <code>DataFile.BYTE_ORDER_UNKNOWN</code>. When the
	 * byte order is <code>DataFile.BYTE_ORDER_UNKNOWN</code> or
	 * <code>DataFile.BYTE_ORDER_NA</code>
	 * this method reads the bytes in big-endian order.
	 * 
	 * @param numBytes number of bytes to read
	 * @param endian endian order
	 * @return the data read
	 * @throws FatalException
	 */
	public long readBytes(int numBytes, String endian) throws FatalException {
		long data = 0;
		try {
			data = reader.read(); // read first byte
			// read rest of bytes and shift if necessary 
			for (int i = 1; i < numBytes; i++) {
				if (endian.equals(DataFile.BYTE_ORDER_LE)) {
					//System.out.println("data: " + data + " i: " + i);
					data = data + ((long) reader.read() << 8 * i);
				} else if (	endian.equals(DataFile.BYTE_ORDER_BE) ||
								endian.equals(DataFile.BYTE_ORDER_UNKNOWN) ||
								endian.equals(DataFile.BYTE_ORDER_NA)) {
					//System.out.println("data: " + data + " i: " + i);
					data = (data << 8) + reader.read();
				} else {
					// not a valid endian order
					Informer.getInstance().fail(
						this,
						"readBytes(int, String)",
						"Illegal argument",
						"endian: " + endian,
						new FatalException("Not a valid endian order"));
				}
			}
		} catch (IOException e) {
			Informer.getInstance().fail(
				this,
				"readBytes(int, String)",
				"Error reading bytes",
				"data read: " + data,
				new FatalException(e.getMessage()));
		}
		return data;
	}
	
	/**
	 * Seeks to a particular byte offset within a file. Saves the previous reading
	 * location to go back to by using <code>goBack()</code>.
	 * 
	 * @param offset	byte offset from the beginning of the file
	 * @throws FatalException
	 */
	public void seek(long offset) throws FatalException {
		try {
			setBookmark();
			reader.seek(offset);
		} catch (IOException e) {
			Informer.getInstance().fail(this, "seek(long)",
				"I/O error",
				"Could not go to the byte at offset " + offset + "into the file",
				e);
		}
	}
	
	/**
	 * Stores a file pointer location that could be gotten back
	 * to by calling the <code>goBack</code> method. If 
	 * the <code>seek</code> method is called after this than
	 * this location will be lost. 
	 * 
	 * @throws FatalException
	 */
	public void setBookmark() throws FatalException {
		try {
			lastLocation = reader.getFilePointer();
		} catch (IOException e) {
			Informer.getInstance().fail(this, "seek(long)",
				"I/O error",
				"Could not get the file reader pointer",
				e);
		}
	}
	
	/**
	 * Attempts to read and discard bytes starting from an existing file
	 * pointer
	 * 
	 * @param numBytes	number of bytes to skip
	 * @throws FatalException
	 */
	public void skipBytes(int numBytes) throws FatalException {
		try {
			this.reader.skipBytes(numBytes);
		} catch (IOException e){
			Informer.getInstance().fail(
				this,
				"skipBytes(int)",
				"IO Exception",
				"RandomAccessFile reader",
				new FatalException(e.getMessage()));
		}
		
	}
}
