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
import edu.fcla.daitss.util.DateTimeUtil;

/**
 * Movie Header Atom specifies the characteristic of a quicktime movie
 * Atom Hierarchy: Movie -> Movie Header
 * @author carol
 */
public class MovieHeaderAtom extends Atom {
	public static final int TYPE = 0x6D766864; // "mvhd"  
	public final long SIZE = 108;  // Movie Header size in bytes
	
	/** 
	 * construct a Movie Header Atom using the information in the basic atom object.
	 * @param _atom
	 */
	MovieHeaderAtom(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract Movie Header metadata
	 * @param _reader
	 * @param qtFile
	 * @return
	 */
	void parse(ByteDecoder _reader, QuickTime qtFile) throws FatalException {
		
		_reader.skipBytes(4); // skip version(1), flag(3)
		
		// creation date, in seconds since midnight 1904
		long creationDate = _reader.readBytes(4, DataFile.BYTE_ORDER_BE); 
		qtFile.SetCreationDate(DateTimeUtil.Mac2Arch(creationDate));
		
		// modified date, in seconds since midnight 1904
		long modifiedDate = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		qtFile.SetModifiedDate(DateTimeUtil.Mac2Arch(modifiedDate));
		
		long timeScale = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		
		// Since the duration is stored in movie time scale unit, convert it to seconds 
		double duration = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		duration = duration / timeScale;
		qtFile.SetDuration(duration);
		
		// preffered movie playback rate
		float prefferedRate = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		qtFile.SetPrefferedRate(prefferedRate);
		
		// preffered movie volume
		float prefferredVolume = _reader.readBytes(2, DataFile.BYTE_ORDER_BE);
		qtFile.SetPrefferedVolume(prefferredVolume);
		
	    _reader.skipBytes(10); // skip reserved
	    
	    byte[] displayMatrix = new byte[36];
	    _reader.read(displayMatrix); // Display Matrix structure
		qtFile.SetHasDisplayMatrix(true);
		
	    // preview start time
		long prevStart = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		qtFile.SetPreviewStart(prevStart);
		
		// preview duration
		long prevDuration = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		qtFile.SetPreviewDuration(prevDuration);
		
		// skip poster time, selection time, selection duration, current time, next track ID
		_reader.skipBytes(20); 
		
	}
}
