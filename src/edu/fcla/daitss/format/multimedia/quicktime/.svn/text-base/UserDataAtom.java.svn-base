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

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ByteDecoder;
/**
 * User Data Atom specifies custom application data for a quicktime movie, track or media. (p37, qt file spec)
 * Atom Hierarchy: Movie -> User Data
 *                 Movie -> Track -> User Data
 *                 Movie -> Track -> Media -> User Data
 * @author carol
 *
 */
public class UserDataAtom  extends Atom {
	/**
	 * constant
	 */
	public static final int TYPE = 0x75647461; 	// "udta"
	
	private final int COPYRIGHT = 0xA9637079;  	// "(c)cpy";
	private final int WRITER = 0xA9777274;  	// "(c)wrt";
	
	private String copyrightStatement = null;
	private String writerName = null;
	
	/** 
	 * construct a User Data Atom using the information in the basic atom object.
	 * @param _atom
	 */
	UserDataAtom(Atom _atom) {
		super(_atom);
	}
	/**
	 * parse and extract User Data metadata 
	 * @param _reader
	 * @param qtFile
	 * @throws FatalException 
	 */
	public void parse(ByteDecoder _reader, QuickTime qtFile) throws FatalException {
	    long bytesRead = headerSize;  
	    int bytesToSkip = 0;
	    
	    // parse through the user data list (in atom format)
	    while ( (size - bytesRead) > Atom.MIN_HEADER_SIZE) {
	    	// get the next child atom
	    	Atom childAtom = new Atom(_reader);
	    	// does it contain copyright statement
	    	if (childAtom.isType(COPYRIGHT)) {
	    		// retrieve the copyright statement
	    		copyrightStatement = retrieveUserData(_reader, childAtom); 
	    	}
	    	else if (childAtom.isType(WRITER)) {
	    		// retrieve the name of the movie's writer
	    		writerName = retrieveUserData(_reader, childAtom); 
	    		qtFile.setWriter(writerName);
	    	} else {
	    		bytesToSkip = (int) childAtom.size - childAtom.headerSize;
	    		_reader.skipBytes(bytesToSkip);
	    	}
	    	bytesRead += childAtom.size;
	    }
	    
	    // skip the rest of unidentified data in this atom.
	    bytesToSkip = (int) (size - bytesRead);
	    if (bytesToSkip > 0)
	    	_reader.skipBytes(bytesToSkip);
	}
	/**
	 * retrieve the data in the user data list
	 * @param _reader
	 * @param atom
	 * @return String
	 * @throws FatalException
	 */
	protected String retrieveUserData(ByteDecoder _reader, Atom atom)  throws FatalException {
		long dataLenght = atom.size - atom.headerSize;
		byte[] data = new byte[(int)dataLenght];
		_reader.read(data);
		return new String(data);
	}
}
