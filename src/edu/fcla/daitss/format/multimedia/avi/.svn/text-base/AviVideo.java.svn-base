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

import java.util.Hashtable;

import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.video.Video;
import edu.fcla.daitss.util.ByteReader;
/**
 * AviVideo represents a video stream in an AVI file
 */
public class AviVideo extends Video{

	private final static int AVIS_DISABLED 			= 0x00000001;
	private final static int AVIS_VIDEO_PALCHANGES 	= 0x00010000;
	    
	/**
	 * Normalized format, Motion JPEG video stream,
	 * for the video streams wrapped in AVI files.
	 */
	public static final String AVI_VIDEO_NORM = "mjpeg";
	
	/**
	 * is the palette changes embedded in the files
	 */
	protected boolean isPaletteChanged = false;	

	/**
	 * video encoding (the fourcc code for avi video stream)
	 */
	public static final String FORMAT_IV32 = "IV32";		// Indeo Video 3.2 
	public static final String FORMAT_IV50 = "IV50";		// Indeo Video 5.0 
	public static final String FORMAT_CVID = "CVID";		// Cinepak 
	public static final String FORMAT_MJPG = "MJPG";		// Motion JPEG 
	public static final String FORMAT_NONE = "";			// none specified. 

	/**
	 * a hashtable to store the mapping between the fourcc encoding code to the string
	 * representation of the fourcc.  
	 */
	private static Hashtable<String, String> encodingTable = new Hashtable<String, String>();
	 	static {
	 		// video encoding formats supported by ffmpeg
			encodingTable.put(FORMAT_IV32, ENCODE_IV32);
			encodingTable.put(FORMAT_CVID, ENCODE_CVID);
			encodingTable.put(FORMAT_MJPG, ENCODE_MJPG);
			// video encoding format supported by mencoder
			encodingTable.put(FORMAT_IV50, ENCODE_IV50);
			encodingTable.put(FORMAT_NONE, ENCODE_UNCOMPRESSED);
	 	}
	 	
	/**
	 * Construct a video bitstream that is retrieved from an AVI file 
	 * 
	 * @param df the file containing this video bitstream
	 * @throws FatalException
	 */
	public AviVideo(DataFile df) throws FatalException {
		super(df);
	}
	
	public boolean getPaletteChanged() {
		return isPaletteChanged;
	}

	/** 
	 * Initialize this video stream with the data read from the stream header 
	 * chunk (STRH) in the aviFile
	 * @param aviFile	- the aviFile to read from.  The current file pointer shall be
	 * 					at the start of the STRH chunk. 
	 * @return byteRead	- the number of bytes read from the aviFile by this video stream.
	 * @throws FatalException
	 */
	public long readFromSTRH(ByteReader aviFile) throws FatalException {
		byte[] data = new byte[4];
		long bytesRead = 52;	// the total number of bytes read 
		
		aviFile.read(data);
		String emptyStr = new String(new char[4]);
    	String handler = new String(data);
		// set the compression handler to none if no handler is used.
		if (handler.equals(emptyStr)) {
			handler = Compression.COMP_NONE;
		}
		
		try {
			super.getCompression().setCompression(handler);
		} catch (FatalException e) {
			handler = Compression.COMP_UNKNOWN;
		}
 
    	long flags = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);
       	// disable the stream if the AVIS_DISABLED is defined.
    	if ( (flags & AVIS_DISABLED) > 0) 
    		isEnabled = false;
    	else
    		isEnabled = true;
    	
    	// is the video stream contains palette changes?  Set the value accordingly.
    	if ( (flags & AVIS_VIDEO_PALCHANGES) > 0) 
    		isPaletteChanged = true;
    	else
    		isPaletteChanged = false;
    	
    	aviFile.skipBytes(4); 	//skip dwReserved
    	aviFile.skipBytes(4); 	//skip dwInitialFrame
    	long scale = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);
       	long rate = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);
       	frameRate = rate / scale;
    	long start = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);
    	long strmLength = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);
    	length = strmLength - start;
    	aviFile.skipBytes(4); 	// skip dwSuggestedBufferSize
    	dataQuality = (int) aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);
       	long sampleSize = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE);
       	aviFile.skipBytes(8); 	// skip Padding
       	
       	return bytesRead;
	}
	
	/** 
	 * set this video stream with the data read from the stream format 
	 * chunk (STRF) in the aviFile
	 * @param aviFile	- the aviFile to read from.  The current file pointer shall be
	 * 					at the start of the STRF chunk. 
	 * @return byteRead	- the number of bytes read from this video stream.
	 * @throws FatalException
	 */
	public long readFromSTRF(ByteReader aviFile) throws FatalException {
		byte[] data = new byte[4];
		long bytesRead = 32;	// total number of bytes read 
		String strEncoding = null; 
		
		// tagBITMAPINFOHEADER block expected
		long value = aviFile.readBytes(4, DataFile.BYTE_ORDER_LE); 			//biSize; 
		setFrameWidth(aviFile.readBytes(4, DataFile.BYTE_ORDER_LE)); 		//biWidth;
		setFrameHeight(aviFile.readBytes(4, DataFile.BYTE_ORDER_LE));		// biHeight;
		value = aviFile.readBytes(2, DataFile.BYTE_ORDER_LE);				// biPlanes;
		setBitsPerPixel((int) aviFile.readBytes(2, DataFile.BYTE_ORDER_LE));// biBitCount;
		aviFile.read(data);													// biCompression;
		aviFile.skipBytes(4);// skip biSizeImage;
		setHorizontalResolution(aviFile.readBytes(4, DataFile.BYTE_ORDER_LE));	// biXPelsPerMeter;
		setVerticalResolution(aviFile.readBytes(4, DataFile.BYTE_ORDER_LE));	// biYPelsPerMeter;
		setResUnit(RES_UNIT_METER);
		
		String strFormat = new String(data);
		String emptyStr = new String(new char[4]);
		
		// set the encoding to uncompressed if no encoding format is specified.
		if (strFormat.equals(emptyStr)) {
			strFormat = ENCODE_UNCOMPRESSED;
			setEncoding(strFormat);
		}
		else {
			// translate this formatTags into a string representation of the encoding format
			strEncoding = (String) encodingTable.get(strFormat.toUpperCase());
		
			if (strEncoding != null) 
				setEncoding(strEncoding);
			else {
				// unsupported encoding, report as a limitation and downgrade it to bit-level
				this.getDf().addLimitation(this.getDf().getLimitationsPossible().getSevereElement(
					AviLimitations.AVI_UNSUPPORTED_VIDEO_ENCODING));
				// store its original FOURCC such that we know what encoding to need to
				// support in the future.
				setEncoding(strFormat.toUpperCase());
			}			
		}
		
		// calculate the dataRate
		dataRate = frameRate * frameWidth * frameHeight * bitsPerPixel;
		
		// for Motion JPEG video encoding, the bitrate is constant (CBR)
		if (encoding.equals(ENCODE_MJPG))
			setBitrateMode(CBR);
		else 
			setBitrateMode(UNKNOWN);
		
		return bytesRead;
	}
	
	 /**
     * Puts all the avi video stream members and their values in a String.
     * 
     * @return the members of this class as a String
     */
    public String toString() {
        StringBuffer strBuffer = new StringBuffer("");
        
        strBuffer.append(super.toString() + "\n");
        strBuffer.append("isPaletteChanged: " + isPaletteChanged + "\n");	
 
        return strBuffer.toString();
    }
}
