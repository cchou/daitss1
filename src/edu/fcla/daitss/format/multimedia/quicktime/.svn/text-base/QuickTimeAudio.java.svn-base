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

import java.util.Hashtable;

import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.audio.Audio;
import edu.fcla.daitss.format.audio.AudioCompression;

/**
 * represents: 
 *
 */
public class QuickTimeAudio extends Audio {

	/**
	 * A string representing the Normalized format for the audio stream in QuickTime files.
	 * It is stored as uncompressed 
	 */
	public static final String QUICKTIME_AUDIO_NORM = "rawaudio";
	private String normalizedAudio = QUICKTIME_AUDIO_NORM;
	
	/**
	 * constants used in the fourcc code of the quicktime audio stream.  Only those
	 * audio bitstream formats that are current supported or plan to be supported
	 * in the near future are listed here.
	 */
	protected final static String FORMAT_NONE		= "NONE";	// uncompressed
	protected final static String FORMAT_RAW		= "RAW ";	// uncompressed in 8-bit offset-binary format
	protected final static String FORMAT_TWOS		= "TWOS";	// unompressed in two's-complement, 16-bit, big-endian
	protected final static String FORMAT_SOWT		= "SOWT";	// unompressed in two's-complement, 16-bit, little-endian
	protected final static String FORMAT_QDMC 		= "QDMC";	// QDesign music
	protected final static String FORMAT_QDM2 		= "QDM2";	// QDesign music, version 2
	
	// a hashtable to store the mapping between the fourcc encoding code to a string representation.
	private static Hashtable<String, String> encodingTable = new Hashtable<String, String>();
	 	static {
			encodingTable.put(FORMAT_NONE, ENCODE_LINEAR);
			encodingTable.put(FORMAT_RAW, ENCODE_8BITOFFSET);
			encodingTable.put(FORMAT_TWOS, ENCODE_16BITBE);
			encodingTable.put(FORMAT_SOWT, ENCODE_16BITLE);
			encodingTable.put(FORMAT_QDMC, ENCODE_QDESIGN);
			encodingTable.put(FORMAT_QDM2, ENCODE_QDESIGN2);
	 	}
	// a hashtable to map between the fourcc encoding code to its applicable compression
	private static Hashtable<String, String> compressionTable = new Hashtable<String, String>();
	 	static {
	 		compressionTable.put(FORMAT_NONE, Compression.COMP_NONE);
	 		compressionTable.put(FORMAT_RAW,  Compression.COMP_NONE);
	 		compressionTable.put(FORMAT_TWOS,  Compression.COMP_NONE);
	 		compressionTable.put(FORMAT_SOWT,  Compression.COMP_NONE);
	 		compressionTable.put(FORMAT_QDMC,  AudioCompression.COMP_QDESIGN);
	 		compressionTable.put(FORMAT_QDM2, AudioCompression.COMP_QDESIGN);
	 	}
		 	
	/**
	 * Construct a audio bitstream that is retrieved from a QuickTime file
	 * @param df the file containing this video bitstream
	 * @throws FatalException
	 */
	public QuickTimeAudio(DataFile df) throws FatalException {
			super(df);
		}
	/**
	 * Set the encoding used for this audio stream.  This method will check whether it is a
	 *  valid video encoding for quicktime video before changing the encoding string.
	 * @param _encoding		- the audio encoding
	 * @return	<code>true</code> - valid encoding, encoding string is changed
	 * 			<code>false</code> - invalid encoding, the encoding string is not changed.
	 * @throws FatalException
	 */
	public boolean setEncoding(String _encoding) throws FatalException {
		boolean changed = false;
		// translate this fourcc into a string representation of the encoding format
		String strEncoding = (String) encodingTable.get(_encoding.toUpperCase());
	
		if (strEncoding != null) { 
			changed = super.setEncoding(strEncoding);
			// set the encoding format for the normalized audio.  This is a workaround for 
			// libquicktime's lqt_transcode as converting the audio from bit-endian format ("twos")
			// to little-endian formats ("sowt") creates noises for the normalized audio.
			if (_encoding.toUpperCase().equals(FORMAT_RAW)) 
				normalizedAudio = QUICKTIME_AUDIO_NORM;
			else if (_encoding.toUpperCase().equals(FORMAT_TWOS)) 
				normalizedAudio = FORMAT_TWOS.toLowerCase();
			else
				normalizedAudio = FORMAT_SOWT.toLowerCase();
		}
		else {
			// unsupported encoding, report as a limitation and downgrade it to bit-level
			this.getDf().addLimitation(this.getDf().getLimitationsPossible().getSevereElement(
				QuickTimeLimitations.QUICKTIME_UNSUPPORTED_AUDIO_ENCODING));
			// store its original FOURCC such that we know what encoding need to
			// support in the future.
			changed = super.setEncoding(_encoding.toUpperCase());
		}			
		
		return changed;
	}
	/**
	 * Set the compression used for this audio stream.  
	 * @param _compression		- the compression code
	 * @throws FatalException
	 */
	public void setCompression(String _compression) throws FatalException {

		// translate this compression code into a string representation of the compression
		String strCompression = (String) compressionTable.get(_compression.toUpperCase());
	
		if (strCompression != null) 
			compression.setCompression(strCompression);
	}
	
	/**
	 * @return String
	 */
	public String getNormalizedAudio() {return normalizedAudio;}
}
