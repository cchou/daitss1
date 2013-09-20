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
import edu.fcla.daitss.bitstream.BitstreamRole;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteDecoder;
import edu.fcla.daitss.util.ByteReader;
/**
 * Track Atom contains information related to a track in a quicktime movie.
 * Both the Track Header Atom ("mdhd") and Media Atom ("mdia") are required
 * Atom Hierarchy: Movie -> Track
 * @author carol
 */
public class TrackAtom extends Atom {
	public static final int TYPE = 0x7472616B; // "trak"
	
	/** 
	 * construct a Track Atom using the information in the basic atom object.
	 * @param _atom
	 */
	TrackAtom(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract Track metadata
	 * @param _reader
	 * @param qtFile
	 * @return
	 */
	void parse(ByteDecoder _reader, QuickTime qtFile) throws FatalException {
		long bytesRead = headerSize;
		MediaAtom mediaAtom = null;
		TrackHeaderAtom trackHeaderAtom = null;
		
		// make sure there is enough bytes to read in the next child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			Atom childAtom = new Atom(_reader);
			if (childAtom.isType(TrackHeaderAtom.TYPE)) {
				// found the required track header
				trackHeaderAtom = new TrackHeaderAtom(childAtom);
				trackHeaderAtom.parse(_reader, qtFile);
			} else if (childAtom.isType(MediaAtom.TYPE)) {
				// found a media atom
				mediaAtom = new MediaAtom(childAtom);
				mediaAtom.parse(_reader, qtFile);
			} else if (childAtom.isType(UserDataAtom.TYPE)) {
	        	// User Data Atom
	        	qtFile.SetHasUserData(true);
	        	UserDataAtom userDataAtom = new UserDataAtom(childAtom);
	        	userDataAtom.parse(_reader, qtFile);
	        } else {
	        	childAtom.skip(_reader);
			}
			bytesRead += childAtom.size;
		}
		
		// setting the bistream metadata in this track.
		if (mediaAtom != null && trackHeaderAtom != null) {
			Bitstream bitstream = mediaAtom.getBitstream();
			boolean enabled = trackHeaderAtom.getEnable();
			boolean inPreview = trackHeaderAtom.getInPreview();
			boolean inMovie = trackHeaderAtom.getInMovie();
			boolean inPoster = trackHeaderAtom.getInPoster();

			if (bitstream instanceof QuickTimeVideo) {
				QuickTimeVideo video = (QuickTimeVideo) bitstream;
				video.setEnabled(enabled);
				// set the bistream role for the video
				if (inMovie)
					video.setRole(BitstreamRole.VIDEO_MOVIE);
				else if (inPreview)
					video.setRole(BitstreamRole.VIDEO_PREVIEW);
				else if (inPoster)
					video.setRole(BitstreamRole.VIDEO_POSTER);
				else
					video.setRole(BitstreamRole.VIDEO_MAIN);
			}
			else if (bitstream instanceof QuickTimeAudio) {
				QuickTimeAudio audio = (QuickTimeAudio) bitstream;
				audio.setEnabled(enabled);
				// set the bistream role for the audio
				if (inMovie)
					audio.setRole(BitstreamRole.AUDIO_MOVIE);
				else if (inPreview)
					audio.setRole(BitstreamRole.AUDIO_PREVIEW);
				else if (inPoster)
					audio.setRole(BitstreamRole.AUDIO_POSTER);
				else
					audio.setRole(BitstreamRole.AUDIO_MAIN);
			}
		}

		// skip the rest of unidentified data in this atom.
	    int bytesToSkip = (int) (size - bytesRead);
	    if (bytesToSkip > 0)
	    	_reader.skipBytes(bytesToSkip);
	    
		// check if all the required atoms are present
		if (trackHeaderAtom == null || mediaAtom == null) {
			// record an anomaly
	      	SevereElement ja = qtFile.getAnomsPossible().
			getSevereElement(QuickTimeAnomalies.QUICKTIME_BAD_TRAK);
        	qtFile.addAnomaly(ja);
		}
	}
	
	/**
	 * localize the information in the Track Atom
	 * @param _reader
	 * @param qtFile
	 * @throws FatalException
	 */
	public void localize(ByteReader _reader, QuickTime qtFile) 
	throws FatalException {
		long bytesRead = headerSize;
		MediaAtom mediaAtom = null;
		
		int newSize = headerSize;
		// make sure there is enough bytes to read in the next child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			Atom childAtom = new Atom(_reader);
			if (childAtom.isType(MediaAtom.TYPE)) {
				// localize the media atom
				mediaAtom = new MediaAtom(childAtom);
				mediaAtom.localize(_reader, qtFile);
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
	 * Write the localized information in the TrackAtom.  For those child atoms that do not
	 * contains localized information, copy the information from the original quicktime file (_reader)
	 * @param _reader
	 * @param _writer
	 * @throws FatalException
	 */
	public void write( ByteReader _reader, RandomAccessFile _writer) 
	throws FatalException {
		long bytesRead = headerSize;
		MediaAtom mediaAtom = null;
		
		super.writeHeader(_writer);
		// make sure there is enough bytes to read in the next child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			Atom childAtom = new Atom(_reader);
			if (childAtom.isType(MediaAtom.TYPE)) {
				// localize the media atom
				mediaAtom = new MediaAtom(childAtom);
				mediaAtom.write( _reader, _writer);
			} else {
		    	// copy other atoms
	        	childAtom.copy(_reader, _writer);
			}
			bytesRead += childAtom.size;
		}
	}
}
