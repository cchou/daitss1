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
 * Media Header Atom specifies the characteristic of a media 
 * Atom Hierarchy: Movie -> Track -> Media -> Media Header
 * @author carol
 */
public class MediaHeaderAtom extends Atom {
	public static final int TYPE = 0x6D646864; // "mdhd"
	public final long SIZE = 24;  // Media Header size in bytes
	
	private float duration = -1;   // the duration of the media in seconds
	/** 
	 * construct a Media Header Atom using the information in the basic atom object.
	 * @param _atom
	 */
	MediaHeaderAtom(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract Media Header metadata.  
	 * @param _reader
	 * @param qtFile
	 * @return
	 */
	void parse(ByteDecoder _reader, QuickTime qtFile) throws FatalException {
		_reader.skipBytes(4); // skip version(1), flag(3)
		
		// creation date, in seconds since midnight 1904
		long creationDate = _reader.readBytes(4, DataFile.BYTE_ORDER_BE); 
		// qtFile.SetCreationDate(DateTimeUtil.Mac2Arch(creationDate));
		
		// modified date, in seconds since midnight 1904
		long modifiedDate = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		qtFile.SetModifiedDate(DateTimeUtil.Mac2Arch(modifiedDate));
		
		// media time scale.  Number of media unit per second
		long timeScale = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		
		// Since the duration is stored in media time scale unit, convert it to seconds 
		duration = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		duration = duration / timeScale;
		
		//skip language code(2), quality(2)
		_reader.skipBytes(4);
	}
	
	float getDuration() {return duration;}
}
