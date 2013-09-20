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
import edu.fcla.daitss.format.audio.Audio;
import edu.fcla.daitss.format.video.Video;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteDecoder;
import edu.fcla.daitss.util.ByteReader;

/**
 * Media Information Atom contains handler-specific information for a track's media data
 * Atom Hierarchy: Movie -> Track -> Media -> Media Information
 * @author carol
 */
public class MediaInfoAtom extends Atom {
	public static final int TYPE = 0x6D696E66; // "minf"

	// type of some children atoms 
	protected static final int VMHD = 0x766D6864; // "vmhd"
	protected static final int SMHD = 0x736D6864; // "smhd"
	
	/** 
	 * construct a Media Information Atom using the information in the basic atom object.
	 * @param _atom
	 */
	MediaInfoAtom(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract Media Information metadata
	 * @param _reader
	 * @param qtFile
	 * @return
	 */
	void parse(ByteDecoder _reader, QuickTime qtFile, Bitstream currentStream) throws FatalException {
		boolean sawHeader = false;
		boolean sawHDLR = false;
		long bytesRead = headerSize;
		
		// make sure there is enough room to read in the header of the next child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			// prefetch the next atom.
			Atom childAtom = new Atom(_reader);
			// check what kind of atom
			// TODO : may need to handle base media information atom
			if (childAtom.isType(VMHD)) {
				// video media information header atom
				childAtom.skip(_reader);
				if (currentStream instanceof Video)
					sawHeader = true;
			} else if (childAtom.isType(SMHD)) {
				// audio media information header atom
				childAtom.skip(_reader);
				if (currentStream instanceof Audio)
					sawHeader = true;
			} else if (childAtom.isType(HandlerReferenceAtom.TYPE)) {
				HandlerReferenceAtom hdlrAtom = new HandlerReferenceAtom(childAtom);
				sawHDLR = true;
				
				if (currentStream == null) {
					currentStream = hdlrAtom.parse(_reader, qtFile);
					qtFile.addBitstream(currentStream);
				} else {
					// if we have already seen the handler reference before, skip it
					hdlrAtom.skip(_reader);
				}
			} else if (childAtom.isType(SampleTableAtom.TYPE)) {
				// sample table atom
				SampleTableAtom stblAtom = new SampleTableAtom(childAtom);
				stblAtom.parse(_reader, qtFile, currentStream);
			} else if (childAtom.isType(DataInformationAtom.TYPE)) {
				// data information atom
				DataInformationAtom dinfAtom = new DataInformationAtom(childAtom);
				dinfAtom.parse(_reader, qtFile);
			} else {
				childAtom.skip(_reader);
			}
			bytesRead += childAtom.size;
		}
		
		// check if all the required atoms are present
		if (!sawHeader || !sawHDLR) {
			// record an anomaly
	      	SevereElement ja = qtFile.getAnomsPossible().
			getSevereElement(QuickTimeAnomalies.QUICKTIME_BAD_MINF);
        	qtFile.addAnomaly(ja);
		}
		
	    // skip the rest of unidentified data in this atom.
	    int bytesToSkip = (int) (size - bytesRead);
	    if (bytesToSkip > 0)
	    	_reader.skipBytes(bytesToSkip);
	}
	
	/**
	 * localize the information in the Media Info Atom
	 * @param _reader
	 * @param qtFile
	 * @throws FatalException
	 */
	public void localize(ByteReader _reader, QuickTime qtFile) 
	throws FatalException {
		long bytesRead = headerSize;
		
		int newSize = headerSize;
		
		// make sure there is enough room to read in the header of the next child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			// prefetch the next atom.
			Atom childAtom = new Atom(_reader);
			// check what kind of atom
			if (childAtom.isType(DataInformationAtom.TYPE)) {
				// data information atom
				DataInformationAtom dinfAtom = new DataInformationAtom(childAtom);
				dinfAtom.localize( _reader, qtFile);
			} else {    	
				// copy other atoms
				childAtom.skip(_reader);
			}
			bytesRead += childAtom.size;
			newSize += childAtom.size;
		}
		
		// recalculate the atom size after the child atoms have been localized.
		size = newSize;
	}
	/**
	 * Write the localized information in the MediaInfo Atom.  For those child atoms that do not
	 * contains localized information, copy the information from the original quicktime file (_reader)
	 * @param _reader
	 * @param _writer
	 * @throws FatalException
	 */
	public void write(ByteReader _reader, RandomAccessFile _writer) 
	throws FatalException {
		long bytesRead = headerSize;
		
		super.writeHeader(_writer);
		
		// make sure there is enough room to read in the header of the next child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			// prefetch the next atom.
			Atom childAtom = new Atom(_reader);
			// check what kind of atom
			if (childAtom.isType(DataInformationAtom.TYPE)) {
				// data information atom
				DataInformationAtom dinfAtom = new DataInformationAtom(childAtom);
				dinfAtom.write(_reader, _writer);
			} else {    	
				// copy other atoms
				childAtom.copy(_reader, _writer);
			}
			bytesRead += childAtom.size;
		}
	}
}
