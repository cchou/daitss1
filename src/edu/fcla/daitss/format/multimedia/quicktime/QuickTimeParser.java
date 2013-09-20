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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteReader;

/**
 * @author ?
 *
 */
public class QuickTimeParser {
    // some top level atoms in quicktime files that could be handled.
    protected static final int MDAT = 0x6D646174; // "mdat" atom 
    
    /**
     * check if the file is a QuickTime file
     * @param _reader
     * @return boolean
     * @throws FatalException 
     */
    public boolean isType(ByteReader _reader) throws FatalException {
    	boolean isType = false;
		boolean seenMOOV = false;
		boolean seenMDAT = false;
		
		while (_reader.bytesLeft() >= Atom.MIN_HEADER_SIZE) {
			// get the next top-level atom 
			Atom nextAtom = new Atom(_reader);
			
	        if (nextAtom.isType(MovieAtom.TYPE))
	        	seenMOOV = true;
	        else if (nextAtom.isType(MDAT))
	        	seenMDAT = true;
	        	
	        // skip the atom
	        nextAtom.skip(_reader);
		}
		
        if (seenMOOV && seenMDAT) 
        	isType = true;
        
        return isType;
    }
    
    /**
     * parse and extract metadata in a quicktime file.  Record anomaly
     * @param _reader
     * @param qtFile 
     * @throws FatalException 
     */
	public void parse(ByteReader _reader, QuickTime qtFile) throws FatalException {
		boolean seenMOOV = false;
		boolean seenMDAT = false;
		
		while (_reader.bytesLeft() > 0) {
			// parse and get the next atom 
			Atom nextAtom = new Atom(_reader);
			
	        if (nextAtom.isType(MovieAtom.TYPE)) {
	        	seenMOOV = true;
	        	MovieAtom moovAtom = new MovieAtom(nextAtom);
	        	// flatten the Movie atom if it's compressed
	        	MovieAtom flattenMoov = moovAtom.flatten(_reader, qtFile);
	        	flattenMoov.parse(qtFile);
	        } else if (nextAtom.isType(MDAT)) {
	        	seenMDAT = true;
	        	// skip Movie Data atom ("mdat"), as there is nothing to record in "mdat" atom.
	        	nextAtom.skip(_reader);
	        } else {
	        	// unknown atom, skip it.
	        	nextAtom.skip(_reader);
	        }
		}
		
        if (!seenMOOV) {
        	//If the required Movie atom is not seen, record an anomaly
        	SevereElement ja = qtFile.getAnomsPossible().
			getSevereElement(QuickTimeAnomalies.QUICKTIME_NO_MOOV);
        	qtFile.addAnomaly(ja);
        }
        
        if (!seenMDAT) {
        	// If the required Movie Data atom is not seen, record an anomaly
        	SevereElement ja = qtFile.getAnomsPossible().
			getSevereElement(QuickTimeAnomalies.QUICKTIME_NO_MDAT);
        	qtFile.addAnomaly(ja);
        }
	}
	
	/**
	 * create a localized copy of the quicktime file
	 * @param localizedFileName
	 * @param qtFile
	 * @return boolean
	 * @throws FatalException
	 */
	public boolean localize(String localizedFileName, QuickTime qtFile) throws FatalException {	
	    File theFile = new File(qtFile.getPath());
	    ByteReader _reader = new ByteReader(theFile, "r");
		boolean success = true;
		long readPtr = 0;
	    try {
		    RandomAccessFile _writer = new RandomAccessFile(localizedFileName, "rw");
		      
			while (_reader.bytesLeft() > 0) {
				readPtr = _reader.getFilePointer();
				// parse and get the next atom 
				Atom nextAtom = new Atom(_reader);
				
		        if (nextAtom.isType(MovieAtom.TYPE)) {
		        	MovieAtom moovAtom = new MovieAtom(nextAtom);
		        	// read and localize the information in the quicktime file (_reader).
		        	moovAtom.localize(_reader, qtFile);
		        	// reset the _reader pointer back to the begining of the MOOV.
		        	_reader.seek(readPtr);
		        	// write the localized information to output file (_writer)
		        	moovAtom.write(_reader, _writer);
		        } else {
		        	// copy the content of all other atoms to the new output file.
		        	nextAtom.copy(_reader, _writer);
		        	//nextAtom.skip(_reader);
		        }
			}
	    } catch (FileNotFoundException e) {
	    	success = false;
	    	throw new FatalException(e);
	    }
	    return success;
	}
}
