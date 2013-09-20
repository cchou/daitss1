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
/*
 * Video.java		 08/19/05
 *
 */
package edu.fcla.daitss.format.video;



import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.util.Informer;

/**
* Video represents a video stream
*/
public class Video extends Bitstream {

	// video encodings
	protected static final String ENCODE_IV32 = "INDEO VIDEO 32";	   // Indeo Video 3.2 
	protected static final String ENCODE_IV50 = "INDEO VIDEO 50";	   // Indeo Video 5.0 
	protected static final String ENCODE_CVID = "CINEPAK VIDEO";	   //  Cinepak 
	protected static final String ENCODE_MJPG = "MOTION JPEG";		   //  Motion JPEG 
	protected static final String ENCODE_MJPGA = "MOTION JPEG A";	   //  Motion JPEG - A
	protected static final String ENCODE_MJPGB = "MOTION JPEG B";	   //  Motion JPEG - B
	protected static final String ENCODE_AVC1 = "Advance Video Coding";// Advance Video Coding (H.264)
	protected static final String ENCODE_GRAPHICS = "GRAPHICS";		   // Graphics
	protected static final String ENCODE_UNCOMPRESSED = "UNCOMPRESSED";// uncompressed video
	protected static final String ENCODE_RGB = "RAW RGB";			   // uncompressed RGB
	protected static final String ENCODE_YUV422 = "YUV422";			   // uncompressed YUV422
	protected static final String ENCODE_UNKNOWN = "UNKNOWN";		   // unknown
	
	// resolution unit
	/**
	 * Class constant
	 */
	public static final String RES_UNIT_CM = "CM"; 		// Measurement in centimeters
	/**
	 * Class constant
	 */
	public static final String RES_UNIT_INCH = "IN"; 	// Measurement in inches
    /**
     * Class constant
     */
    public static final String RES_UNIT_METER = "M";	// Measurement in meters
	/**
	 * Class constant
	 */
	public static final String RES_UNIT_NONE = "NONE"; 	// No absolute unit of measurement
	/**
	 * Class constant
	 */
	public static final String RES_UNIT_UNKNOWN = "UNKNOWN"; // Unknown absolute unit of measurement

	// constants used for determining the minimum and maximum value of class members. 
	private static final long MAX_FRAME_HEIGHT 	= 4294967295L;	// max. frame height
	private static final long MIN_FRAME_HEIGHT 	= 0L;			// min. frame height
	private static final long MAX_FRAME_WIDTH 	= 4294967295L;	// max. frame width
	private static final long MIN_FRAME_WIDTH	= 0L;			// min. frame width
	private static final int MAX_BITS_PER_PIXEL = 65536;		// max. bits/pixel
	private static final int MIN_BITS_PER_PIXEL = 0;			// min. bits/pixel
	private static final long MAX_LENGTH 		= 4294967295L;	// max. data length
	private static final long MIN_LENGTH 		= 0L;			// min. data length
	private static final double MAX_FRAME_RATE 	= 1.844674407e+19;	// max. frame rate
	private static final double MIN_FRAME_RATE 	= 0L;			// min. frame rate
	private static final double MAX_DATA_RATE 	= 1.844674407e+19;	// max. data rate
	private static final double MIN_DATA_RATE	= 0L;			// min. data rate
	private static final double MAX_RES_HORZ = 1.844674407e+19;	//  Max. horizontal resolution.
	private static final double MAX_RES_VERT = 1.844674407e+19;// Max. vertical resolution.

	// video metadata
	protected String encoding;	// encoding format of this video stream
	protected float frameRate = -1;	// number of frames per second
	protected int bitsPerPixel = 0;	// number of bits per pixel
	protected long frameHeight = 0;	// frame height in pixels
	protected long frameWidth = 0;	// frame width in pixels
	private String resUnit = RES_UNIT_UNKNOWN; // resolution unit
	protected float verticalResolution = 0;		// pixels per inch
	protected float horizontalResolution = 0;	// pixel per inch
	protected long length = -1;	//  length (frames)
	protected int dataQuality = -1; // (0 - 10000), -1 for driver's default quality	
	protected float dataRate = -1;	//  number of bits per second required
	protected String bitrateMode = UNKNOWN; //the mode of the bit rate (CBR or VBR)
	protected boolean isEnabled = false; // is the stream enabled by default?
	private String bsVideoTable = null; //The name of a database table containing 
										// information specific to this video type.
	
	// the format that this video will be normalized to.  Can be set by the subclass
	// to set to different normalization format.
	protected String normalizationFormat = ENCODE_MJPG; // default to Motion JPEG
	/**
	 * Construct a Video bitstream
	 * 
	 * @param df the file containing this video bitstream
	 * @throws FatalException
	 */
	public Video(DataFile df) throws FatalException {
		super(df);
		this.setBsTable(ArchiveDatabase.TABLE_BS_VIDEO);
		encoding = ENCODE_UNKNOWN;
		
		// create video compression
		compression = null;
		compression = new VideoCompression();
	}
	
	/**
	 * @return float primitive representing video frame rate
	 */
	public float getFrameRate() {
		return frameRate;
	}
	
	/**
	 * @return long primitive representing bits per pixel
	 */
	public long getBitsPerPixel() {
		return bitsPerPixel;
	}
	
	/**
	 * @return long primitive representing frame height
	 */
	public long getFrameHeight() {
		return frameHeight;
	}
	
	/**
	 * @return long primitive representing frame width
	 */
	public long getFrameWidth() {
		return frameWidth;
	}
	
	/**
	 * @return long primitive representing length
	 */
	public long getLength() {
		return length;
	}
	
	/**
	 * @return long primitive representing data quality
	 */
	public long getDataQuality() {
		return dataQuality;
	}

	/**
	 * Set the encoding used for this video stream.  This method will check whether it is a valid video encoding
	 * before changing the encoding string.
	 * @param _encoding  the video encoding
	 * @return	<code>true</code> - valid encoding, encoding string is changed
	 * 			<code>false</code> - invalid encoding, the encoding string is not changed.
	 * @throws FatalException
	 */
	protected boolean setEncoding(String _encoding) throws FatalException {
		boolean found = false;
		
		if (_encoding != null) {
			found = true;
			encoding = _encoding.toUpperCase();
		}
		
		return found;
	}
	
	/**
	 * Sets the height of the video frame in the video stream, -1 is not allowed
	 * 
	 * @param _frameHeight	- the frame height
	 * @throws FatalException
	 */
	public void setFrameHeight(long _frameHeight) throws FatalException {
		String methodName = "_frameHeight(long)";
		
		if (_frameHeight < MIN_FRAME_HEIGHT || _frameHeight > MAX_FRAME_HEIGHT) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_frameHeight: " + _frameHeight,
				new FatalException("Not a valid frame height "));
		}
		frameHeight= _frameHeight;
	}
	
	/**
	 * Sets the width of the video frame in the video stream, -1 is not allowed
	 * 
	 * @param _frameWidth	- the frame height
	 * @throws FatalException
	 */
	public void setFrameWidth(long _frameWidth) throws FatalException {
		String methodName = "_frameWidth(long)";
		
		if (_frameWidth < MIN_FRAME_WIDTH || _frameWidth > MAX_FRAME_WIDTH) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_frameWidth: " + _frameWidth,
				new FatalException("Not a valid frame width "));
		}
		frameWidth= _frameWidth;
	}
	
	/**
	 * Sets the unit of measurement for horizontal and vertical resolution. Valid values are 
	 * <code>RES_UNIT_NONE</code>, <code>RES_UNIT_INCH</code>, <code>RES_UNIT_CM</code> or
	 * <code>RES_UNIT_UNKNOWN</code.
	 * 
	 * @param _resUnit	the unit of measurement for <code>horizontalResolution</code>
	 * 												and <code>verticalResolution</code>
	 * @see "Image#xResolution"
	 * @see "Image#yResolution"
	 * @throws FatalException
	 */
	public void setResUnit(String _resUnit) throws FatalException {
		if (_resUnit == null || (!_resUnit.equals(RES_UNIT_CM) &&
			!_resUnit.equals(RES_UNIT_INCH) && !_resUnit.equals(RES_UNIT_METER) &&
			!_resUnit.equals(RES_UNIT_NONE) && !_resUnit.equals(RES_UNIT_UNKNOWN))) {
			Informer.getInstance().fail(this, "setResUnit(String)", "Illegal argument",
					"_resUnit: " + _resUnit, new FatalException("Not a valid resolution unit value"));				
			}
		this.resUnit = _resUnit;
	}
	
	/**
	 * Sets the horizontal resolution (pixels/inch)
	 * 
	 * @param _horizontalResolution	-  horizontal resolution
	 * @throws FatalException
	 */
	public void setHorizontalResolution(float _horizontalResolution) throws FatalException {
		if (_horizontalResolution > MAX_RES_HORZ) {
			Informer.getInstance().fail(this, "setHorizontalResolution",
				"Illegal argument", "_horizontalResolution: " + _horizontalResolution,
				new FatalException("Not a valid horizontal resolution "));
		}
		horizontalResolution = _horizontalResolution;
	}
	
	/**
	 * Sets the vertical resolution (pixels/inch)
	 * 
	 * @param _verticalResolution	-  vertical resolution
	 * @throws FatalException
	 */
	public void setVerticalResolution(float _verticalResolution) throws FatalException {
		if (_verticalResolution > MAX_RES_VERT) {
			Informer.getInstance().fail(this, "setVerticalResolution",
				"Illegal argument", "_horizontalResolution: " + _verticalResolution,
				new FatalException("Not a valid vertical resolution "));
		}
		verticalResolution = _verticalResolution;
	}
	
	/**
	 * Sets the bits/pixel used in the video stream, -1 is not allowed
	 * 
	 * @param _bitsPerPixel	- number of bits used per pixel
	 * @throws FatalException
	 */
	public void setBitsPerPixel(int _bitsPerPixel) throws FatalException {
		String methodName = "_bitsPerPixel(long)";
		
		if (_bitsPerPixel < MIN_BITS_PER_PIXEL || _bitsPerPixel > MAX_BITS_PER_PIXEL ) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_bitsPerPixel: " + _bitsPerPixel,
				new FatalException("Not a valid bits per pixel "));
		}
		bitsPerPixel= _bitsPerPixel;
	}
	
	/**
	 * Sets the length of the video stream, -1 is not allowed
	 * 
	 * @param _length	- the total number of samples
	 * @throws FatalException
	 */
	public void setLength(long _length) throws FatalException {
		String methodName = "setLength(long)";
		
		if (_length < MIN_LENGTH || _length > MAX_LENGTH) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_length: " + _length,
				new FatalException("Not a valid length")
			);
		}
		length = _length;
	}
	
	/**
	 * set the bitrate mode of this video stream
	 * @param _bitrateMode
	 * @throws FatalException
	 */
	public void setBitrateMode(String _bitrateMode) throws FatalException {
		String methodName = "setBitrateMode(long)";
		
		if (!_bitrateMode.equals(CBR) && !_bitrateMode.equals(VBR) && !_bitrateMode.equals(UNKNOWN)) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_bitrateMode: " + _bitrateMode,
				new FatalException("invalid bitrate mode")
			);
		}
		bitrateMode = _bitrateMode;
	}
	
	/**
	 * setting the video stream to be enabled or not
	 * @param _enabled
	 */
	public void setEnabled(boolean _enabled) {
		isEnabled = _enabled;
	}
	
	/**
	 * Sets the frame rate of this video stream
	 * 
	 * @param _frameRate	- the frame rate
	 * @throws FatalException
	 */
	public void setFrameRate(float _frameRate) throws FatalException {
		if (_frameRate < MIN_FRAME_RATE || _frameRate > MAX_FRAME_RATE) {
			Informer.getInstance().fail(this, "setFrameRate",
				"Illegal argument", "_frameRate: " + _frameRate,
				new FatalException("Not a valid frame rate")
			);
		}
		frameRate = _frameRate;
	}
	
	/**
	 * Sets the data rate (bits per second) of the video stream
	 * 
	 * @param _dataRate	- number of bits per second
	 * @throws FatalException
	 */
	public void setDataRate(float _dataRate) throws FatalException {
		String methodName = "setDataRate(float)";
		
		if (_dataRate < MIN_DATA_RATE || _dataRate > MAX_DATA_RATE) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_dataRate: " + _dataRate,
				new FatalException("Not a valid dataRate"));
		}
		dataRate = _dataRate;
	}
	
	/**
	 * Returns the name of a database table containing information specific to
	 * this video type.
	 * 
	 * @return the name of a database table containing information specific to
	 * this video type
	 */
	public String getBsVideoTable() {
		return this.bsVideoTable;
	}      
	
	/**
	 * Sets the name of a database table containing information specific to
	 * this audio type.
	 * 
	 * @param _bsTable the name of a database table containing information 
	 * specific to this audio type
	 * @throws FatalException
	 */
	public void setBsVideoTable(String _bsTable) throws FatalException {
		//	check that the argument is not null
		if (_bsTable == null || 
			_bsTable.length() > MAX_BSTABLE_LENGTH ||
			!ArchiveDatabase.isValidVideoBitstreamTable(_bsTable)){
				// illegal bitstream image table name
				Informer.getInstance().fail(this, "setBsAudioTable(String)",
				"Illegal argument",
				"_bsTable: " + _bsTable,
				new FatalException("Not a valid bitstream audio table name"));			
		}		
		bsVideoTable = _bsTable;
	}
	
	
	/**
	 * check if the normalization process is required for this video stream
	 * 
	 * @return normalizationRequired
	 */
	public boolean isNormalizationRequired() {
		boolean normalizationRequired = true;
		
		// if this video stream is already in normalized format, no normalization is required.
		if (encoding.equals(normalizationFormat))
			normalizationRequired = false;
		
		return normalizationRequired;
	}
	
	/**
	 * fill in the database column-value pairs for this video
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();
		
		columns.add(ArchiveDatabase.COL_BS_VIDEO_BSID);
		values.add(this.getBsid());
		columns.add(ArchiveDatabase.COL_BS_VIDEO_DFID);
		values.add(this.getDfid());
		columns.add(ArchiveDatabase.COL_BS_VIDEO_ENCODING);
		values.add(encoding);
		columns.add(ArchiveDatabase.COL_BS_VIDEO_FRAME_RATE);
		values.add(this.getFrameRate());
		columns.add(ArchiveDatabase.COL_BS_VIDEO_BITS_PER_PIXEL);
		values.add(this.getBitsPerPixel());
		columns.add(ArchiveDatabase.COL_BS_VIDEO_FRAME_HEIGHT);
		values.add(this.getFrameHeight());
		columns.add(ArchiveDatabase.COL_BS_VIDEO_FRAME_WIDTH);
		values.add(this.getFrameWidth());
		columns.add(ArchiveDatabase.COL_BS_VIDEO_RES_UNIT);
		values.add(resUnit);
		columns.add(ArchiveDatabase.COL_BS_VIDEO_RES_HORZ);
		values.add(horizontalResolution);
		columns.add(ArchiveDatabase.COL_BS_VIDEO_RES_VERT);
		values.add(verticalResolution);
		columns.add(ArchiveDatabase.COL_BS_VIDEO_LENGTH);
		values.add(length);
		columns.add(ArchiveDatabase.COL_BS_VIDEO_DATA_QUALITY);
		values.add(getDataQuality());
		columns.add(ArchiveDatabase.COL_BS_VIDEO_DATA_RATE);
		values.add(dataRate);
		columns.add(ArchiveDatabase.COL_BS_VIDEO_BITRATE_MODE);
		values.add(bitrateMode);
		columns.add(ArchiveDatabase.COL_BS_VIDEO_ENABLED);
		values.add(isEnabled);

		if (this.getBsVideoTable() != null) {
			columns.add(ArchiveDatabase.COL_BS_VIDEO_BS_TABLE);
			values.add(this.getBsVideoTable());
		}
	}
	
	/**
	 * Insert this video into the database
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbInsert() throws FatalException{
		super.dbInsert();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		return tcon.insert(ArchiveDatabase.TABLE_BS_VIDEO, columns, colValues);
	}
	
	/**
	 * update the entry in the database for this video
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbUpdate() throws FatalException{
		super.dbUpdate();

		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		String whereClause = String.format("%s = %s",
				ArchiveDatabase.COL_BS_VIDEO_BSID, SqlQuote.escapeString(this.getBsid()));
		return tcon.update(ArchiveDatabase.TABLE_BS_VIDEO, columns, colValues, whereClause);
	}
	
	

	 /**
     * Puts all the video stream members and their values in a String.
     * 
     * @return the members of this class as a String
     */
    public String toString() {
        StringBuffer strBuffer = new StringBuffer("");
        
        strBuffer.append(super.toString() + "\n");
        strBuffer.append("frameRate: " + frameRate + "\n"); 
        strBuffer.append("bitsPerPixel: " + bitsPerPixel + "\n");	
        strBuffer.append("frameHeight: " + frameHeight + "\n");
        strBuffer.append("frameWidth: " + frameWidth + "\n");
        strBuffer.append("length: " + length + "\n");
        strBuffer.append("dataQuality: " + dataQuality + "\n");
        strBuffer.append("dataRate: " + dataRate + "\n"); 
        strBuffer.append("bitrateMode: " + bitrateMode + "\n"); 
        strBuffer.append("isEnabled: " + isEnabled + "\n"); 
        return strBuffer.toString();
    }
    
	/** (non-Javadoc)
	 * @see edu.fcla.daitss.bitstream.Bitstream#toXML()
	 */
	
	public Document toXML() throws FatalException {
        // Document.
        Document doc = super.toXML();

        // Root element.
        Element rootElement = doc.getDocumentElement();

        // Namespace. 
        String namespace = rootElement.getNamespaceURI();
        
	    // Bs Video 
	    Element bsVideoElement = doc.createElementNS(namespace, "BS_VIDEO");
	    rootElement.appendChild(bsVideoElement);

	    // Dfid 
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
	    Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsVideoElement.appendChild(dfidElement);

	    // Bsid
	    Element bsidElement = doc.createElementNS(namespace, "BSID");
	    String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
	    Text bsidText = doc.createTextNode(bsidValue);
	    bsidElement.appendChild(bsidText);
	    bsVideoElement.appendChild(bsidElement);
	    
	    // ENCODING
	    Element encodingElement = doc.createElementNS(namespace, "ENCODING");
	    Text encodingText = doc.createTextNode(encoding); 
	    encodingElement.appendChild(encodingText);
	    bsVideoElement.appendChild(encodingElement);

	    // FRAME_RATE 
	    Element frameRateElement = doc.createElementNS(namespace, "FRAME_RATE");
	    Text frameRateText = doc.createTextNode(Float.toString(frameRate));
	    frameRateElement.appendChild(frameRateText);
	    bsVideoElement.appendChild(frameRateElement);
	    
	    // BITS_PER_PIXEL
	    Element bitsPerPixelElement = doc.createElementNS(namespace, "BITS_PER_PIXEL");
	    Text bitsPerPixelText = doc.createTextNode(Long.toString(bitsPerPixel));
	    bitsPerPixelElement.appendChild(bitsPerPixelText);
	    bsVideoElement.appendChild(bitsPerPixelElement);
	    
	    // FRAME_HEIGHT 
	    Element frameHeightElement = doc.createElementNS(namespace, "FRAME_HEIGHT");
	    Text frameHeightText = doc.createTextNode(Long.toString(frameHeight));
	    frameHeightElement.appendChild(frameHeightText);
	    bsVideoElement.appendChild(frameHeightElement);
	    
	    // FRAME_WIDTH 
	    Element frameWidthElement = doc.createElementNS(namespace, "FRAME_WIDTH");
	    Text frameWidthText = doc.createTextNode(Long.toString(frameWidth));
	    frameWidthElement.appendChild(frameWidthText);
	    bsVideoElement.appendChild(frameWidthElement);
	   
	    // RES_UNIT
	    Element resUnitElement = doc.createElementNS(namespace, "RES_UNIT");
	    Text resUnitText = doc.createTextNode(resUnit);
	    resUnitElement.appendChild(resUnitText);
	    bsVideoElement.appendChild(resUnitElement);
	    
	    // RES_HORZ 
	    Element resHorzElement = doc.createElementNS(namespace, "RES_HORZ");
	    Text resHorzText = doc.createTextNode(Float.toString(horizontalResolution));
	    resHorzElement.appendChild(resHorzText);
	    bsVideoElement.appendChild(resHorzElement);
	    
	    // RES_VERT
	    Element resVertElement = doc.createElementNS(namespace, "RES_VERT");
	    Text resVertText = doc.createTextNode(Float.toString(verticalResolution));
	    resVertElement.appendChild(resVertText);
	    bsVideoElement.appendChild(resVertElement);
	    
	    // LENGTH
	    Element lengthElement = doc.createElementNS(namespace, "LENGTH");
	    Text lengthText = doc.createTextNode(Long.toString(length));
	    lengthElement.appendChild(lengthText);
	    bsVideoElement.appendChild(lengthElement);
	    
	    // DATA_QUALITY 
	    Element dataQualityElement = doc.createElementNS(namespace, "DATA_QUALITY");
	    Text dataQualityText = doc.createTextNode(Integer.toString(dataQuality));
	    dataQualityElement.appendChild(dataQualityText);
	    bsVideoElement.appendChild(dataQualityElement);

	    // DATA_RATE 
	    Element dataRateElement = doc.createElementNS(namespace, "DATA_RATE");
	    Text dataRateText = doc.createTextNode(Float.toString(dataRate));
	    dataRateElement.appendChild(dataRateText);
	    bsVideoElement.appendChild(dataRateElement);
	    
	    // BITRATE_MODE 
	    Element bitrateModeElement = doc.createElementNS(namespace, "BITRATE_MODE");
	    Text bitrateModeText = doc.createTextNode(bitrateMode);
	    bitrateModeElement.appendChild(bitrateModeText);
	    bsVideoElement.appendChild(bitrateModeElement);
	    
	    // ENABLED 
	    Element isEnabledElement = doc.createElementNS(namespace, "ENABLED");
	    Text isEnabledText = doc.createTextNode(Boolean.toString(isEnabled).toUpperCase());
	    isEnabledElement.appendChild(isEnabledText);
	    bsVideoElement.appendChild(isEnabledElement);
	
	    /* Bs Table */
	    Element bsTableElement = doc.createElementNS(namespace, "BS_TABLE");
	    String bsTableValue = (this.getBsVideoTable() != null ? this.getBsVideoTable() : "" );
	    Text bsTableText = doc.createTextNode(bsTableValue);
	    bsTableElement.appendChild(bsTableText);
	    bsVideoElement.appendChild(bsTableElement);
	    
        return doc;
	}
}
