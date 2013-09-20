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

import java.io.RandomAccessFile;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteDecoder;
import edu.fcla.daitss.util.ByteReader;

/**
 * Movie Atom specify the information that can be used to understand the data that is
 * stored in the Movie Data Atom.
 * Atom Hierarchy: Movie (Top-level)
 * @author carol
 */
public class MovieAtom extends Atom {
	/**
	 * type
	 */
	public static final int TYPE = 0x6D6F6F76;	// "moov" atom
	
	// type of some children atoms which may eventually be put into a class if we are 
	// going to parse its content.  Don't see the point of creating a class just to 
	// hold a constant.
	protected static final int RMRA = 0x726D7261; // "rmra"
	protected static final int CLIP = 0x636C6970; // "clip"
	protected static final int CTAB = 0x63746162; // "ctab"
	
	// the compressed movie atom which may be contained in the MovieAtom
	private CompressedMovieAtom cmovAtom = null;
	private ByteDecoder decoder = null;
	
	/** 
	 * construct a Movie Atom using the information in the basic atom object.
	 * @param _atom
	 */
	MovieAtom(Atom _atom) {
		super(_atom);
	}

	/**
	 * flatten the movie atom
	 * @param _reader
	 * @param qtFile
	 * @return MovieAtom object
	 * @throws FatalException
	 */
	public MovieAtom flatten(ByteReader _reader, QuickTime qtFile) throws FatalException {
		MovieAtom flattenMoov = this;
		byte[] moovData = new byte[(int) (super.size - super.headerSize)];
		_reader.read(moovData);
		decoder = new ByteDecoder(moovData);
		
		Atom childAtom = new Atom(decoder);
		
		if (childAtom.isType(CompressedMovieAtom.TYPE)) {
        	// Compressed Movie Atom
        	qtFile.SetHasCompressedMovie(true);
        	cmovAtom = new CompressedMovieAtom(childAtom);
        	cmovAtom.parse(decoder, qtFile);
        	// create a new decoder based on the uncompressed movie resource
        	decoder = null;
        	decoder = new ByteDecoder(cmovAtom.getMovieResource());
           	// extract the movie atom embedded inside the compressed movie atom
        	Atom nextAtom = new Atom(decoder);
        	if (nextAtom.isType(MovieAtom.TYPE)) {
        		flattenMoov = new MovieAtom(nextAtom);
        		flattenMoov.decoder = decoder;
        		flattenMoov.cmovAtom = cmovAtom;
        	} else {
        		// TODO: record anomaly.  Shall we also stop the parsing?
        	}
		} else {
			decoder.reset();
		}
		return flattenMoov;
	}
	
	/**
	 * parse the Movie atom ("moov"), this is one of the top-level atoms
	 * @param qtFile 
	 * @throws FatalException 
	 */
	public void parse(QuickTime qtFile) throws FatalException{
		MovieHeaderAtom mvhdAtom = null;
		boolean seenRMRA = false;
		long bytesRead = headerSize;
		
		// make sure there is enough room to read in the header of the child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE && !decoder.isEnd()) {
			// read in the next atom 
			Atom childAtom = new Atom(decoder);
			
	        if (childAtom.isType(MovieHeaderAtom.TYPE)) {
	        	// Movie Heade Atom
	        	mvhdAtom = new MovieHeaderAtom(childAtom);
	        	mvhdAtom.parse(decoder, qtFile);
	        } else if (childAtom.isType(RMRA)) {
	        	// Reference Movie Atom
	        	seenRMRA = true;
	        	qtFile.SetHasReferences(true);
	        	childAtom.skip(decoder);
	        } else if (childAtom.isType(TrackAtom.TYPE)) {
	        	// Track Atom
	        	TrackAtom trackAtom = new TrackAtom(childAtom);
	        	trackAtom.parse(decoder, qtFile);
	        } else if (childAtom.isType(UserDataAtom.TYPE)) {
	        	// User Data Atom
	        	qtFile.SetHasUserData(true);
	        	UserDataAtom userDataAtom = new UserDataAtom(childAtom);
	        	userDataAtom.parse(decoder, qtFile);
	        } else if (childAtom.isType(CLIP)) {
	        	// Clipping atom
	        	qtFile.SetHasClippingRegion(true);
	        	childAtom.skip(decoder);
	        } else if (childAtom.isType(CTAB)) {
	        	// color table atom
	        	qtFile.SetHasColorTable(true);
	        	childAtom.skip(decoder);
	        }
	        else {
	        	// skip other optional atoms
	        	childAtom.skip(decoder);
	        }
			bytesRead += childAtom.size;
		}
		
		if ((mvhdAtom == null) && (cmovAtom == null) && !seenRMRA) {
			// at least one of the atoms ("mvhd", "cmov", "rmra") must exist, QT Fil Spec., p31
			// record an anomaly 
	      	SevereElement ja = qtFile.getAnomsPossible().
			getSevereElement(QuickTimeAnomalies.QUICKTIME_BAD_MOOV);
        	qtFile.addAnomaly(ja);
		}
		
	    // skip the rest of unidentified data in this atom.
	    int bytesToSkip = (int) (size - bytesRead);
	    if (bytesToSkip > 0)
	    	decoder.skipBytes(bytesToSkip);
	}
	
	/**
	 * localize the information in the Movie Atom
	 * @param _reader
	 * @param qtFile
	 * @throws FatalException
	 */
	public void localize(ByteReader _reader, QuickTime qtFile) 
	throws FatalException {
		long bytesRead = headerSize;
		
		int newSize = headerSize;
		// make sure there is enough bytes to read in the next child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			Atom childAtom = new Atom(_reader);
	        if (childAtom.isType(TrackAtom.TYPE)) {
	        	// Track Atom
	        	TrackAtom trackAtom = new TrackAtom(childAtom);
	        	trackAtom.localize(_reader, qtFile);
	        } else {
	        	// copy other atoms
	        	childAtom.skip(_reader);
	        }
			bytesRead += childAtom.size;
			newSize += childAtom.size;
		}
		
		size = newSize;
	}
	
	/**
	 * Write the localized information in the Movie Atom.  For those child atoms that do not
	 * contains localized information, copy the information from the original quicktime file (_reader)
	 * @param _reader
	 * @param _writer
	 * @throws FatalException
	 */
	public void write( ByteReader _reader, RandomAccessFile _writer) 
	throws FatalException {
		long bytesRead = headerSize;
		
		super.writeHeader(_writer);
		// make sure there is enough bytes to read in the next child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			Atom childAtom = new Atom(_reader);
	        if (childAtom.isType(TrackAtom.TYPE)) {
	        	// Track Atom
	        	TrackAtom trackAtom = new TrackAtom(childAtom);
	        	trackAtom.write(_reader, _writer);
	        } else {
	        	// copy other atoms
	        	childAtom.copy(_reader, _writer);
	        }
			bytesRead += childAtom.size;
		}
	}
}
