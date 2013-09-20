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
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteDecoder;

/**
 * Sample Description Atom contains information for decoding samples in a media.  The content of the
 * atom depends on the type of media that it described 
 * Atom Hierarchy: Movie -> Track -> Media -> Media Information -> Sample Table -> Sample Description
 * @author carol
 */
public class SampleDescriptionAtom extends Atom {
	/**
	 * class constant
	 */
	public static final int TYPE = 0x73747364; // "stsd"
	
	// minimum size in bytes for the video sample description data
	private final long MIN_VIDEO_SAMPLE_SIZE = 84;
	
	// minimum size in bytes for the audio sample description data
	private final long MIN_AUDIO_SAMPLE_SIZE = 36;
	
	/** 
	 * construct a Sample Description Atom using the information in the basic atom object.
	 * @param _atom
	 */
	SampleDescriptionAtom(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract Sample Description metadata
	 * @param _reader
	 * @param qtFile
	 * @param currentStream 
	 * @throws FatalException 
	 */
	public void parse(ByteDecoder _reader, QuickTime qtFile, Bitstream currentStream) throws FatalException {
	       byte[] data = new byte[4];
			long bytesRead = headerSize;  

			// skip version(1), flags(3)
			_reader.skipBytes(4);
			bytesRead += 4;
			
			// read in the number of table entries.
			long noOfEntries = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
			bytesRead += 4;
			
			for (int i = 0; i < noOfEntries; i++) {
				// read in the sample description size
				long sampleDescriptionSize = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
				
				// read in the data format (encoding)
				_reader.read(data);
				String encoding = new String(data);
				
				// skip the reserved (6 bytes) and data reference index (2 bytes)
				_reader.skipBytes(8);
				
				if (currentStream instanceof QuickTimeVideo) {
					QuickTimeVideo video = (QuickTimeVideo) currentStream;
					video.setEncoding(encoding);
					// use the encoding format to determine the compression scheme as quicktime
					// does not store the compression scheme else where
					video.setCompression(encoding);
					parseVideoSampleTable(_reader, video, qtFile, sampleDescriptionSize);
				}
				else if (currentStream instanceof QuickTimeAudio) {
					QuickTimeAudio audio = (QuickTimeAudio) currentStream;
					audio.setEncoding(encoding);
					// use the encoding format to determine the compression scheme as quicktime
					// does not store the compression scheme else where
					audio.setCompression(encoding);
					parseAudioSampleTable(_reader, audio, qtFile, sampleDescriptionSize);
				} else { // unsupported sample description, skip it
					_reader.skipBytes((int) sampleDescriptionSize - 16);
				}
				
				bytesRead += sampleDescriptionSize;
			}
			
			// skip the rest of the fields
			long bytesToSkip = size - bytesRead;
			_reader.skipBytes((int) bytesToSkip);
			
	}

	/**
	 * parse the video sample description table and extract the video metadata
	 * @param _reader
	 * @param video
	 * @param qtFile 
	 * @param sampleDescriptionSize 
	 * @throws FatalException 
	 */
	public void parseVideoSampleTable(ByteDecoder _reader, QuickTimeVideo video, QuickTime qtFile, long sampleDescriptionSize) 
	throws FatalException {

		if (sampleDescriptionSize < MIN_VIDEO_SAMPLE_SIZE) {
			// record an anomaly
			SevereElement ja = qtFile.getAnomsPossible().
			getSevereElement(QuickTimeAnomalies.QUICKTIME_ATOM_TOO_SMALL);
        	qtFile.addAnomaly(ja);
        	
			// deduct those 16 bytes that were already read in parse() 
			_reader.skipBytes((int) sampleDescriptionSize - 16);
			return;
		}

		// skip versiom(2), Revision Level (2), Vendor(4),
		_reader.skipBytes(8);
		
		long temporalQuality = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		video.setTemporalQuality(temporalQuality);
		
		long spatialQuality = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		video.setSpatialQuality(spatialQuality);
		
		int width = (int) _reader.readBytes(2, DataFile.BYTE_ORDER_BE);
		video.setFrameWidth(width);
		
		int height = (int) _reader.readBytes(2, DataFile.BYTE_ORDER_BE);
		video.setFrameHeight(height);
		
		float verticalResolution = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		video.setVerticalResolution(verticalResolution);
		
		float horizontalResolution = _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		video.setHorizontalResolution(horizontalResolution);
		
		// skip data size(4), frame count(2)
		_reader.skipBytes(6);

		byte[] compressor = new byte[32];
		_reader.read(compressor);
		String compressorName = new String(compressor);
		
		int bitsPerPixel = (int) _reader.readBytes(2, DataFile.BYTE_ORDER_BE);
		video.setBitsPerPixel(bitsPerPixel);
		
		// skip the rest of the video sample description, i.e., color table
		long bytesToSkip = sampleDescriptionSize - MIN_VIDEO_SAMPLE_SIZE;
		_reader.skipBytes((int) bytesToSkip);
	}
	
	/**
	 * parse the audio sample description table and extract the audio metadata
	 * @param _reader
	 * @param audio
	 * @param qtFile 
	 * @param sampleDescriptionSize 
	 * @throws FatalException 
	 */
	public void parseAudioSampleTable(ByteDecoder _reader, QuickTimeAudio audio, QuickTime qtFile, long sampleDescriptionSize) 
	throws FatalException { 
		if (sampleDescriptionSize < MIN_AUDIO_SAMPLE_SIZE) {
			// record an anomaly
			SevereElement ja = qtFile.getAnomsPossible().
			getSevereElement(QuickTimeAnomalies.QUICKTIME_ATOM_TOO_SMALL);
        	qtFile.addAnomaly(ja);
        	
			// deduct those 16 bytes that were already read in parse() 
			_reader.skipBytes((int) sampleDescriptionSize - 16); 
		}
		// version
		int version = (int) _reader.readBytes(2, DataFile.BYTE_ORDER_BE);
		
		// Revision Level (2), Vendor(4),
		_reader.skipBytes(6);
		
		// number of channels;
		int noOfChannels = (int) _reader.readBytes(2, DataFile.BYTE_ORDER_BE);
		audio.setNoOfChannels(noOfChannels);
		
		// sample size
		int sampleSize = (int) _reader.readBytes(2, DataFile.BYTE_ORDER_BE);
		audio.setSampleSize(sampleSize);
		
		// skip compression ID(2) and PacketSize(2)
		_reader.skipBytes(4);
		
		// sample rate - somehow this number is stored as shift left by 16 bits (<< 16)
		float sampleRate = (float) _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		// shift the value back.  Optionally, we can also use the timescale in the media header
		// the spec said (p104) it should match
		sampleRate = (int) sampleRate >> 16;
		audio.setSampleRate(sampleRate);
		
	    // calcuate the frame rate, frame size and the data rate based on the information read
		float frameRate = sampleRate * noOfChannels;
		audio.setFrameRate(frameRate);
		audio.setFrameSize(sampleSize * noOfChannels);
		float dataRate = frameRate * sampleSize;
		audio.setDataRate(dataRate);
		
		// for version 1 of the sound description, 4 extra fields with type long were added
		// (p105 of quicktime file format), skip those fields for now.
		if (version == 1)
			// skip samplesPerPacket(4), bytesPerPackage(4), bytesPerFrame(4), and bytesPerSample(4)
			_reader.skipBytes(16);
	}
}
