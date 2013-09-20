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
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.audio.Audio;
import edu.fcla.daitss.format.video.Video;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteDecoder;

/**
 * Time To Sample Atom contains duration information for samples in a media. 
 * Atom Hierarchy: Movie -> Track -> Media -> Media Information -> Sample Table -> Time To Sample 
 * @author carol
 */
public class TimeToSampleAtom extends Atom {
	/**
	 * class constant
	 */
	public static final int TYPE = 0x73747473; // "stts"
	/** 
	 * construct a Time To Sample Atom using the information in the basic atom object.
	 * @param _atom
	 */
	TimeToSampleAtom(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract Time To Sample metadata
	 * @param _reader
	 * @param qtFile
	 * @param currentStream 
	 * @throws FatalException 
	 */
	public void parse(ByteDecoder _reader, QuickTime qtFile, Bitstream currentStream) throws FatalException {
	       long bytesRead = headerSize;  
	       long totalSamples = 0;
	       String bitrateMode = Bitstream.UNKNOWN;
	       
	       // skip version(1), flags(3)
			_reader.skipBytes(4);
			bytesRead += 4;
			
			// read in the number of table entries.
			long noOfEntries = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
			bytesRead += 4;
			
			if (noOfEntries == 1)
				bitrateMode = Bitstream.CBR;
			else if (noOfEntries > 1)
				bitrateMode = Bitstream.VBR;
				
		    // check the minimum size
		    long sizeRequired = headerSize + 8 + noOfEntries * 8;
		    if (size < sizeRequired) {
		    	//record an anomaly
		      	SevereElement ja = qtFile.getAnomsPossible().
				getSevereElement(QuickTimeAnomalies.QUICKTIME_ATOM_TOO_SMALL);
	        	qtFile.addAnomaly(ja);
	        	
		    	_reader.skipBytes((int) (size - headerSize - 8));
		    	return;
		    }
		    
			for (int i = 0; i < noOfEntries; i++) {
				// read in the sample count
				long sampleCount = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
				// skip the sample duration (4)
				_reader.skipBytes(4);
				totalSamples += sampleCount;
			}
			
			if (currentStream instanceof QuickTimeVideo) {
				Video video = (Video) currentStream;
				video.setLength(totalSamples);
				video.setBitrateMode(bitrateMode);
			}
			else if (currentStream instanceof QuickTimeAudio) {
				Audio audio = (Audio) currentStream;
				audio.setLength(totalSamples);
				audio.setBitrateMode(bitrateMode);
			} 
	}
}
