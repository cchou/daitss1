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

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteDecoder;
import edu.fcla.daitss.util.ByteReader;

/**
 * Media Atom describe and define a track's media type and sample data.
 * Atom Hierarchy: Movie -> Track -> Media
 * @author carol
 */
public class MediaAtom extends Atom {
	public static final int TYPE = 0x6D646961; // "mdia"
	
	// the bitstream in this media atom
	Bitstream		bitstream = null;
	/** 
	 * construct a Media Atom using the information in the basic atom object.
	 * @param _atom
	 */
	MediaAtom(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract Media metadata
	 * @param _reader
	 * @param qtFile
	 * @return
	 */
	void parse(ByteDecoder _reader, QuickTime qtFile) throws FatalException {
		MediaHeaderAtom mdhdAtom = null;
		long bytesRead = headerSize;
		
		Long mediaOffset = new Long((long) _reader.getCurrentPointer());
		
		// make sure there is enough space for the next child atom header
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			// prefetch the next atom.
			Atom childAtom = new Atom(_reader);
			// check what kind of atom
			if (childAtom.isType(MediaHeaderAtom.TYPE)) {
				// media header atom
				mdhdAtom = new MediaHeaderAtom(childAtom);
				mdhdAtom.parse(_reader, qtFile);
			} else if (childAtom.isType(HandlerReferenceAtom.TYPE)) {
				// handler reference atom
				HandlerReferenceAtom hdlrAtom = new HandlerReferenceAtom(childAtom);
				bitstream = hdlrAtom.parse(_reader, qtFile);
				qtFile.addBitstream(bitstream);
			} else if (childAtom.isType(MediaInfoAtom.TYPE)) {
				// media information atom
				MediaInfoAtom minfAtom = new MediaInfoAtom(childAtom);
				minfAtom.parse(_reader, qtFile, bitstream);
			} else {
				childAtom.skip(_reader);
			}
			bytesRead += childAtom.size;
		}
		
		if (bitstream != null) {
			// set the bitstream location (or contentLocation in PREMIS term)
			bitstream.setLocation(mediaOffset.toString());
			bitstream.setLocationType(Bitstream.LOC_UNCOMPRESSED_HEADER_BYTE_OFFSET);
			// determine and set the frame rate if it is a video stream
			if (mdhdAtom != null && bitstream instanceof QuickTimeVideo) {
				QuickTimeVideo video = ((QuickTimeVideo) bitstream);
				float duration = mdhdAtom.getDuration();
				float totalFrames = video.getLength();
				// calculate frame rate (frames/second)
				float frameRate = totalFrames/duration;
				video.setFrameRate(frameRate);
				// calculate data rate (bits/second)
				float dataRate = frameRate * video.getFrameWidth() * 
					video.getFrameHeight() * video.getBitsPerPixel();
				video.setDataRate(dataRate);
			}
		}
		
		// check if all the required atoms are present
		if (mdhdAtom == null) {
			// record an anomaly
	      	SevereElement ja = qtFile.getAnomsPossible().
			getSevereElement(QuickTimeAnomalies.QUICKTIME_BAD_MDIA);
        	qtFile.addAnomaly(ja);
		}
		
	    // skip the rest of unidentified data in this atom.
	    int bytesToSkip = (int) (size - bytesRead);
	    if (bytesToSkip > 0)
	    	_reader.skipBytes(bytesToSkip);
	}
	
	public void localize( ByteReader _reader, QuickTime qtFile) 
	throws FatalException {
		long bytesRead = headerSize;
		
		int newSize = headerSize;
		// make sure there is enough space for the next child atom header
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			// prefetch the next atom.
			Atom childAtom = new Atom(_reader);
			// check what kind of atom
			if (childAtom.isType(MediaInfoAtom.TYPE)) {
				// media information atom
				MediaInfoAtom minfAtom = new MediaInfoAtom(childAtom);
				minfAtom.localize(_reader, qtFile);
			} else {
				childAtom.skip(_reader);
			}
			bytesRead += childAtom.size;
			newSize += childAtom.size;
		}
		// recalculate the atom size after the child atoms have been localized.
		size = newSize;
	}
	
	public void write(ByteReader _reader, RandomAccessFile _writer) 
	throws FatalException {
		long bytesRead = headerSize;
		
		super.writeHeader(_writer);
		// make sure there is enough space for the next child atom header
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			// prefetch the next atom.
			Atom childAtom = new Atom(_reader);
			// check what kind of atom
			if (childAtom.isType(MediaInfoAtom.TYPE)) {
				// media information atom
				MediaInfoAtom minfAtom = new MediaInfoAtom(childAtom);
				minfAtom.write( _reader, _writer);
			} else {
				childAtom.copy(_reader, _writer);
			}
			bytesRead += childAtom.size;
		}
	}
	
	public Bitstream getBitstream() {
		return bitstream;
	}
}
