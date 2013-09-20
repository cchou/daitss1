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

import java.util.zip.*;

public class CompressedMovieAtom  extends Atom {
	public static final int TYPE = 0x636D6F76; // "cmov"
	
	protected byte[] movieResource;	// the uncompressed byte array holding the movie resource information
	protected String resourceStr;
	
	/** 
	 * construct a Compressed Movie Atom using the information in the basic atom object.
	 * @param _atom
	 */
	CompressedMovieAtom(Atom _atom) {
		super(_atom);
		movieResource = null;
	}

	/**
	 * parse the Compressed Movie atom ("cmov").  The "cmov" atom is composed of a data compression
	 * and a compressed movie data atom. The data compression atom identifies the lossless 
	 * data compression algorithm used to compressed the movie.  currently, only zlib compression is supported.
	 * @param _reader
	 */
	public void parse(ByteDecoder _reader, QuickTime qtFile) throws FatalException{
	
		// first read in the data compression atom
		Atom dataCompression = new Atom(_reader);
		byte[] name = new byte[4];
		// read in the name of the algorithm
		_reader.read(name);
		String nameStr = new String(name);
		
		// the compressed movie atom.  The size field of the compressed movie atom
		// represents the compressed size of the movie.  It contains an integer which
		// stores the original uncompressed size of the movie resource.
		Atom compressedMovie = new Atom(_reader);
		long uncompressedDataSize = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		// deduct the header size and the data that already read, uncompressedDataSize(4)
		long compressedDataSize = compressedMovie.getSize() - compressedMovie.headerSize - 4 ; 
		
		// see if it is using zlib to compress the movie.
		if (nameStr.toLowerCase().equals("zlib")) {
			// read in the compressed data.
			byte[] compressedData = new byte[(int)compressedDataSize];
			_reader.read(compressedData);
			// Decompress the bytes
			Inflater decompresser = new Inflater();
			decompresser.setInput(compressedData, 0, (int) compressedDataSize);
			movieResource = new byte[(int)uncompressedDataSize];
			try {
				decompresser.inflate(movieResource);
				decompresser.end();
			} catch (DataFormatException e) {}
		} else {
			// unsupported comrpessed header, record a limitation and downgrade the file to bit-level
			qtFile.addLimitation(qtFile.getLimitationsPossible().getSevereElement(
				QuickTimeLimitations.QUICKTIME_UNSUPPORTED_COMPRESSED_HEADER));
		}
	}
	
	public byte[] getMovieResource() {
		return movieResource;
		}
}
