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
 * Handler Reference Atom contains information that can be used to interpret the media data.
 * Atom Hierarchy: Movie -> Track -> Media -> Handler Reference
 *                 Movie -> Track -> Media -> Media Information -> Handler Reference
 * @author carol
 */
public class HandlerReferenceAtom 	extends Atom {
	public static final int TYPE = 0x68646C72; 	// "hdlr"
	
	protected final String VIDEO = "vide"; 		// for video
	protected final String AUDIO = "soun"; 		// for sound
	
	String componentSubtype = null;

	/** 
	 * construct a Handler Reference Atom using the information in the basic atom object.
	 * @param _atom
	 */
		HandlerReferenceAtom(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract Handler Reference metadata.  
	 * @param _reader
	 * @param qtFile
	 * @return a bitstream (Video/Audio) that this handler describes.
	 */
	Bitstream parse(ByteDecoder _reader, QuickTime qtFile) throws FatalException {
        byte[] data = new byte[4];
		long bytesRead = headerSize;  
		Bitstream currentStream = null;
		
		// skip version(1), flags(3)
		_reader.skipBytes(4);
		bytesRead += 4;
		
		// skip component type
		_reader.skipBytes(4);
		bytesRead += 4;
		
		// read in the component subtype
		_reader.read(data);
		componentSubtype = new String(data);
		bytesRead += 4;
		
		if (componentSubtype.equals(VIDEO))
			currentStream = new QuickTimeVideo(qtFile);
		else if (componentSubtype.equals(AUDIO))
			currentStream = new QuickTimeAudio(qtFile);
		
		// skip the rest of the fields
		long bytesToSkip = size - bytesRead;
		_reader.skipBytes((int) bytesToSkip);
		
		return currentStream;
	}
}
