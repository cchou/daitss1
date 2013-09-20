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
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.util.ByteDecoder;

public class TrackHeaderAtom  extends Atom {
	public static final int TYPE = 0x746B6864; // "tkhd"

	private static final int TRACK_ENABLED = 0x0001;
	private static final int TRACK_INMOVIE = 0x0002;
	private static final int TRACK_INPREVIEW = 0x0004;
	private static final int TRACK_INPOSTER = 0x0008;
		
	boolean enabled = false;	// whether the track is enabled
	boolean inPreview = false;	// whether the track is used in the movie preview
	boolean inMovie = false;	// whether the track is used in the movie
	boolean inPoster = false;	// whether the track is used in movie poster
	
	/** 
	 * construct a Track Header Atom using the information in the basic atom object.
	 * @param _atom
	 */
	TrackHeaderAtom(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract Tracker Header metadata
	 * @param _reader
	 * @param qtFile
	 * @return
	 */
	void parse(ByteDecoder _reader, QuickTime qtFile) throws FatalException {
		int bytesRead = headerSize;
		
		_reader.skipBytes(1); // skip version(1)
		bytesRead += 1;
		
		long flags = _reader.readBytes(3, DataFile.BYTE_ORDER_BE); 
		bytesRead += 3;
		
		// check if the track is enabled
	    if ( (flags & TRACK_ENABLED) > 0) 
	    	enabled = true;	
		// check if the track is used in preview
	    else if ( (flags & TRACK_INPREVIEW) > 0) 
	    	inPreview = true;	
		// check if the track is used in movie
	    else if ( (flags & TRACK_INMOVIE) > 0) 
	    	inMovie = true;	
		// check if the track is used in poster
	    else if ( (flags & TRACK_INPOSTER) > 0) 
	    	inPoster = true;	
		int bytesToSkip = (int) size - bytesRead;
		if (bytesToSkip > 0)
			_reader.skipBytes(bytesToSkip);
	}
	
	boolean getEnable() { return enabled; }
	boolean getInPreview() { return inPreview; }
	boolean getInMovie() { return inMovie; }
	boolean getInPoster() { return inPoster; }
}
