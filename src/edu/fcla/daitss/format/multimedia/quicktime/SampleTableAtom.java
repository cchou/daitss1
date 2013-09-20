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

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ByteDecoder;

/**
 * Sample Table Atom contains child atoms that can be used for interpreting samples in a media and
 * converting media time into sample number to sample location. 
 * Atom Hierarchy: Movie -> Track -> Media -> Media Information -> Sample Table
 * @author carol
 */
public class SampleTableAtom extends Atom {
	public static final int TYPE = 0x7374626C; // "stbl"

	/** 
	 * construct a Sample Table Atom using the information in the basic atom object.
	 * @param _atom
	 */
	SampleTableAtom(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract the Sample Table metadata  
	 * @param _reader
	 * @param qtFile
	 * @return
	 */
	void parse(ByteDecoder _reader, QuickTime qtFile, Bitstream currentStream) throws FatalException {
		long bytesRead = headerSize;
		
		// make sure there is enough bytes to read in the next child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			// prefetch the next atom.
			Atom childAtom = new Atom(_reader);
			// check what kind of atom
			if (childAtom.isType(SampleDescriptionAtom.TYPE)) {
				if (currentStream != null) {
					SampleDescriptionAtom stsdAtom = new SampleDescriptionAtom(childAtom);
					stsdAtom.parse(_reader, qtFile, currentStream);
				} else
					// cannot parse the sample description if there is no known information about the stream
					childAtom.skip(_reader);
			} else if (childAtom.isType(TimeToSampleAtom.TYPE)) {
				TimeToSampleAtom sttsAtom = new TimeToSampleAtom(childAtom);
				sttsAtom.parse(_reader, qtFile, currentStream);
			} else {
				childAtom.skip(_reader);
			}
			bytesRead += childAtom.size;
		}
		
	    // skip the rest of unidentified data in this atom.
	    int bytesToSkip = (int) (size - bytesRead);
	    if (bytesToSkip > 0)
	    	_reader.skipBytes(bytesToSkip);
	    
	}
}
