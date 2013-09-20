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
package edu.fcla.daitss.format.multimedia.avi;

import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.audio.Audio;
import edu.fcla.daitss.format.audio.wave.WaveAudio;
import edu.fcla.daitss.util.ByteReader;

/**
 * AviAudio represents an audio stream in an AVI file.  
 * @author carol
 */
public class AviAudio extends Audio {
	/**
	 * constants used for processing audio header chunk in an avi file
	 */
	private final static int STRF_AUDIO_HEADER_SIZE = 16;		// bytes
	private final static int STRH_AUDIO_HEADER_SIZE = 52;		// bytes
	private final static int AVIS_DISABLED 			= 0x00000001;
	   
	/**
	 * A string representing the Normalized format for the audio stream in AVI files.
	 * It is stored as PCM data with 16 bits/sample, signed, LE, 
	 */
	public final static String AVI_AUDIO_NORM = "pcm";//"pcm_s16le";
	
	/**
	 * Construct a audio bitstream that is retrieved from an AVI file
	 * @param df the file containing this audio bitstream
	 * @throws FatalException
	 */
	public AviAudio(DataFile df) throws FatalException {
		super(df);
		
	}
	
	/** 
	 * Initialize this audio stream with the data read from the stream header 
	 * chunk (STRH) in the aviFile
	 * @param aviFile	- the aviFile to read from.  The current file pointer shall be
	 * 					at the start of the STRH chunk. 
	 * @return byteRead	- the number of bytes read from this audio stream.
	 * @throws FatalException
	 */
	public long readFromSTRH(ByteReader aviFile) throws FatalException {
		byte[] data = new byte[4];
		long bytesRead = STRH_AUDIO_HEADER_SIZE;	// the total number of bytes read 
		
		// read in the fourcc of the bitstream handler (codec)
		aviFile.read(data);

		String emptyStr = new String(new char[4]);
		String handler = new String(data);

		// set the compression handler to none if no handler is used.
		if (handler.equals(emptyStr)) {
			handler = Compression.COMP_NONE;
		}
		try {
			compression.setCompression(handler);
		} catch (FatalException e) {
			// set the compression type to unknow if a fatal exception is thrown from the setCompression method
			handler = Compression.COMP_UNKNOWN;
		}
		
		// read in the flags used for this audio stream
    	long flags = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);
       	// disable the stream if the AVIS_DISABLED is defined.
    	if ( (flags & AVIS_DISABLED) > 0) 
    		isEnabled = false;
    	else
    		isEnabled = true;
    	
    	// read in the information of this audio stream 
    	aviFile.skipBytes(4); 										//skip dwReserved
    	aviFile.skipBytes(4); 										//skip dwInitialFrame
    	long value = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE); 	// dwScale
    	value = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);		// dwRate
    	long start = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);	// dwStart
    	long strmLength = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE); // dwStrmLength
    	setLength(strmLength - start);
    	aviFile.skipBytes(4); 											// skip dwSuggestedBufferSize
    	dataQuality = (int) aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);	// dwDataQuality
    	value = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);	// dwSampleSize
       	aviFile.skipBytes(8); 									// skip Padding
       	
       	return bytesRead;
	}
	
	/** 
	 * setup this audio stream with the data read from the stream format 
	 * chunk (STRF) in the aviFile
	 * @param aviFile	- the aviFile to read from.  The current file pointer shall be
	 * 					at the start of the stream format chunk. 
	 * @return byteRead	- the number of bytes read from the aviFile by this audio stream.
	 * @throws FatalException
	 */
	public long readFromSTRF(ByteReader aviFile) throws FatalException {
		long bytesRead = STRF_AUDIO_HEADER_SIZE;	// the total number of bytes read 
		
		// expect WAVEFORMATEX structure as required by MS DirectX 9.0, DirectShow interface
		// http://msdn.microsoft.com/library/default.asp?url=/library/en-us/directshow/htm/avirifffilereference.asp
		int formatTag = (int) aviFile.readBytes(2, DataFile.BYTE_ORDER_LE); // wFormatTag;
		
		// translate this formatTags into a string representation of the encoding format
	    String strEncoding = (String) WaveAudio.encodingTable.get(new Integer(formatTag));
	   
	    if (strEncoding != null) {
	    	setEncoding(strEncoding);
	    	if (strEncoding.equals(Audio.ENCODE_LINEAR))
				// linear PCM is always in Constant Bitrate mode
				bitrateMode = CBR;
			else 
				// TODO: If the format is MP3, parse throught the entire MP3 
				// bitstream to determine if it is VBR
				bitrateMode = UNKNOWN;
	    }
	    else  { 
			// unsupported encoding, report as a limitation and downgrade it to bit-level
	    	this.getDf().addLimitation(this.getDf().getLimitationsPossible().getSevereElement(
						AviLimitations.AVI_UNSUPPORTED_AUDIO_ENCODING));
	    	// store its original formatTags such that we know what encoding to need to
			// support in the future.
			setEncoding(new Integer(formatTag).toString());
		}
	
		
		setNoOfChannels(aviFile.readBytes(2, DataFile.BYTE_ORDER_LE)); 	// nChannels;
		sampleRate=  aviFile.readBytes(4, DataFile.BYTE_ORDER_LE); 		// nSamplesPerSec;
        long value =  aviFile.readBytes(4, DataFile.BYTE_ORDER_LE); 	// nAvgBytesPerSec;
        value =  aviFile.readBytes(2, DataFile.BYTE_ORDER_LE); 			// nBlockAlign;
        setSampleSize(aviFile.readBytes(2, DataFile.BYTE_ORDER_LE)); 	// wBitsPerSample;
       
        // calcuate the frame rate, frame size and the data rate based on the information
        // read from the format chunk
		frameRate = sampleRate * numOfChannels;
		setFrameSize(sampleSize * numOfChannels);
		dataRate = frameRate * sampleSize;
		
       	return bytesRead;
	}
}
