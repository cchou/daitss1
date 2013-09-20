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
import edu.fcla.daitss.format.video.Video;
import edu.fcla.daitss.format.video.VideoCompression;
import edu.fcla.daitss.util.Informer;

/**
 * represents: 
 *
 */
public class QuickTimeVideo extends Video{
    /**
     * class constant
     */
    // Normalized format, Lossless Motion JPEG video stream, for the video streams wrapped in QuickTime files.
	public static final String QUICKTIME_VIDEO_NORM = "mjpa";
	private String normalizedVideo = QUICKTIME_VIDEO_NORM;
	
	// video encoding (the fourcc code for quicktime video stream)
	protected static final String FORMAT_RAWRGB = "RAW ";	// uncompressed RGB
	protected static final String FORMAT_YUV422 = "YUV2";	// uncompressed YUV 422
	protected static final String FORMAT_CVID   = "CVID";	// Cinepak 
	protected static final String FORMAT_MJPGA  = "MJPA";	// Motion JPEG -A 
	protected static final String FORMAT_MJPGB  = "MJPB";	// Motion JPEG -B
	protected static final String FORMAT_AVC1  = "AVC1";	// Advance Video Coding (H.264)
	protected static final String FORMAT_SMC  = "SMC ";		// Graphics
	
	protected long temporalQuality = -1;		// the degree of temporal compression, 0 - 1023
	protected long spatialQuality = -1;			// the degree of spatial compression, 0 - 1024
	
    // constants used for determining the minimum and maximum value of class members. 
	private final long MIN_TEMPORAL_QUALITY = 0;
	// shall be 1023 (quicktime spec. p97) but many quicktimes files apppear to use 1024 as the maximum
	private final long MAX_TEMPORAL_QUALITY = 1024; 
	private final long MIN_SPATIAL_QUALITY = 0;
	private final long MAX_SPATIAL_QUALITY = 1024;
	
	// a hashtable to store the mapping between the fourcc encoding code to the string
	// representation of the fourcc.  
	private static Hashtable<String, String> encodingTable = new Hashtable<String, String>();
	 	static {
			encodingTable.put(FORMAT_RAWRGB, ENCODE_RGB);
			encodingTable.put(FORMAT_YUV422, ENCODE_YUV422);
			encodingTable.put(FORMAT_CVID, ENCODE_CVID);
			encodingTable.put(FORMAT_MJPGA, ENCODE_MJPG);
			encodingTable.put(FORMAT_MJPGB, ENCODE_MJPG);
			encodingTable.put(FORMAT_AVC1, ENCODE_AVC1);
			encodingTable.put(FORMAT_SMC, ENCODE_GRAPHICS);
	 	}
	 	
	// a hashtable to map between the fourcc encoding code to its applicable compression
	private static Hashtable<String, String> compressionTable = new Hashtable<String, String>();
	 	static {
	 		compressionTable.put(FORMAT_RAWRGB, Compression.COMP_NONE);
	 		compressionTable.put(FORMAT_YUV422,  Compression.COMP_NONE);
	 		compressionTable.put(FORMAT_CVID,  VideoCompression.COMP_CVID);
	 		compressionTable.put(FORMAT_MJPGA,  VideoCompression.COMP_MJPG);
	 		compressionTable.put(FORMAT_MJPGB,  VideoCompression.COMP_MJPG);
	 		compressionTable.put(FORMAT_SMC, VideoCompression.COMP_SMC);
	 		compressionTable.put(FORMAT_AVC1, VideoCompression.COMP_AVC1);
	 	}
	 	
	/**
	 * Construct a video bitstream retrieved from a quicktime file 
	 * 
	 * @param df the file containing this video bitstream
	 * @throws FatalException
	 */
	public QuickTimeVideo(DataFile df) throws FatalException {
		super(df);
		
		// quicktime use meter as the resolution unit.
		setResUnit(RES_UNIT_INCH);
		// use MOTION JPEG A as the normlization format
		normalizationFormat = ENCODE_MJPG;
	}
	
	/**
	 * Set the encoding used for this video stream.  This method will check whether it is a
	 *  valid video encoding for quicktime video before changing the encoding string.
	 * @param _encoding		- the video encoding
	 * @return	<code>true</code> - valid encoding, encoding string is changed
	 * 			<code>false</code> - invalid encoding, the encoding string is not changed.
	 * @throws FatalException
	 */
	public boolean setEncoding(String _encoding) throws FatalException {
		boolean changed = false;
		// translate this fourcc into a string representation of the encoding format
		String strEncoding = (String) encodingTable.get(_encoding.toUpperCase());
	
		if (strEncoding != null) 
			changed = super.setEncoding(strEncoding);
		else {
			// unsupported encoding, report as a limitation and downgrade it to bit-level
			this.getDf().addLimitation(this.getDf().getLimitationsPossible().getSevereElement(
				QuickTimeLimitations.QUICKTIME_UNSUPPORTED_VIDEO_ENCODING));
			// store its original FOURCC such that we know what encoding need to
			// support in the future.
			changed = super.setEncoding(_encoding.toUpperCase());
		}			
		
		return changed;
	}
	
	/**
	 * Set the compression used for this video stream.  
	 * @param _compression		- the compression code
	 * @throws FatalException
	 */
	public void setCompression(String _compression) throws FatalException {

		// translate this compression code into a string representation of the compression
		String strCompression = (String) compressionTable.get(_compression.toUpperCase());
	
		if (strCompression != null) 
			super.getCompression().setCompression(strCompression);
	}
	
	/**
	 * Sets the temporal quality
	 * 
	 * @param _temporalQuality	-  temporal quality
	 * @throws FatalException
	 */
	public void setTemporalQuality(long _temporalQuality) throws FatalException {
		String methodName = "setTemporalQuality(long)";
		
		if (_temporalQuality < MIN_TEMPORAL_QUALITY || _temporalQuality > MAX_TEMPORAL_QUALITY) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_temporalQuality: " + _temporalQuality,
				new FatalException("Not a valid number for temporalQuality "));
		}
		temporalQuality = _temporalQuality;
	}
	
	/**
	 * Sets the spatial quality
	 * 
	 * @param _spatialQuality	-  spatial quality
	 * @throws FatalException
	 */
	public void setSpatialQuality(long _spatialQuality) throws FatalException {
		String methodName = "setSpatialQuality(long)";
		
		if (_spatialQuality < MIN_SPATIAL_QUALITY || _spatialQuality > MAX_SPATIAL_QUALITY) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_spatialQuality: " + _spatialQuality,
				new FatalException("Not a valid number for spatialQuality "));
		}
		spatialQuality = _spatialQuality;
	}
	
	/**
	 * @return String object
	 */
	public String getNormalizedVideo() {return normalizedVideo;}
}
