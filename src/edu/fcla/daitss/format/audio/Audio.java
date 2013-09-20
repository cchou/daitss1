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
 * Audio.java		 08/19/05
 *
 */

package edu.fcla.daitss.format.audio;

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
 * Audio represents an audio stream
 * @author carol
 */
public class Audio extends Bitstream {
	/**
	 * encoding format of this audio stream
	 */
	protected String encoding = ENCODE_UNKNOWN;
	
	/**
	 * number of samples per second
	 */
	protected float sampleRate = -1;	
	/** 
	 * number of bits per sample
	 */
	protected long sampleSize = 0;	
	/**
	 * number of channels
	 */
	protected long numOfChannels = 0;	

	/**
	 * number of samples per second per frame
	 */
	protected float frameRate = -1;	
	/** 
	 * number of bits per sample per frame
	 */
	protected long frameSize = 0;	
	
	/**
	 * length (total number of samples in this audio stream)
	 */
	protected long length = -1;
	/**
	 * data quality, codec specific, range: 0 - 10000
	 */
	protected int dataQuality = -1; // use the driver's default quality	

	/**
	 * number of bits per second required for transmitting the audio.
	 */
	protected float dataRate = -1;	
	
	/**
	 * the mode of the bit rate (CBR or VBR)
	 */
	protected String bitrateMode = UNKNOWN;
	
	/**
	 * is the stream enabled by default?
	 */
	protected boolean isEnabled = true;
	
	/**
	 * The name of a database table containing information specific to
	 * this audio type.
	 */
	private String bsAudioTable = null;
	
	/**
	 * audio encoding
	 */
	protected static final String ENCODE_MP3		= "MPEG Layer 3";	// MPEG 1/2 layer 3 encoding
	protected static final String ENCODE_LINEAR		= "Linear";			// linear PCM encoding
	protected static final String ENCODE_8BITOFFSET	= "8-bit Offset";	//uncompressed, 8-bit offset binary
	//	uncompressed, 16-bit big-endian, two's-compement
	protected static final String ENCODE_16BITBE	= "16-bit Big Endian";
	//	uncompressed, 16-bit little-endian, two's-compement
	protected static final String ENCODE_16BITLE	= "16-bit Little Endian";
	protected static final String ENCODE_QDESIGN	= "QDesign music";
	protected static final String ENCODE_QDESIGN2	= "QDesign music version 2";
	protected static final String ENCODE_MSADPCM 	= "Microsoft ADPCM";// MS. ADPCM encoding
	protected static final String ENCODE_UNKNOWN	= "Unknown";		// unknown audio encoding

	/**
	 * constants used for determining the minimum and maximum value of class members. 
	 */
	private static final long MAX_SAMPLE_SIZE 		= 4294967295L;	// max. sample size
	private static final long MIN_SAMPLE_SIZE 		= 0L;			// min. sample size
	private static final long MAX_FRAME_SIZE 		= 4294967295L;	// max. frame size.
	private static final long MIN_FRAME_SIZE 		= 0L;			// min. frame size
	private static final long MAX_NO_OF_CHANNELS 	= 4294967295L;	// max number of channels
	private static final long MIN_NO_OF_CHANNELS 	= 0L;			// min. number of channels
	private static final long MAX_LENGTH 			= 4294967295L;	// max. data length
	private static final long MIN_LENGTH 			= 0L;			// min. data length

	private static final double MAX_SAMPLE_RATE 	= 1.844674407e+19;	// max. sample rate
	private static final double MIN_SAMPLE_RATE		= 0L;			// min. sample rate
	private static final double MAX_FRAME_RATE 		= 1.844674407e+19;	// max. frame rate
	private static final double MIN_FRAME_RATE		= 0L;			// min. frame rate
	private static final double MAX_DATA_RATE 		= 1.844674407e+19;	// max. data rate
	private static final double MIN_DATA_RATE		= 0L;			// min. data rate

	/**
	 * Construct an Audio bitstream
	 * 
	 * @param df the file containing this audio bitstream
	 * @throws FatalException
	 */
	public Audio(DataFile df) throws FatalException {
		super(df);
		this.setBsTable(ArchiveDatabase.TABLE_BS_AUDIO);
		encoding = ENCODE_UNKNOWN;
		// create audio compression
		compression = null;
		compression = new AudioCompression();
	}

	/**
	 * @return dataQuality
	 */
	public long getDataQuality() {
		return dataQuality;
	}
	
	/**
	 * @return sampleRate
	 */
	public float getSampleRate() {
		return sampleRate;
	}
	
	/**
	 * @return sampleSize
	 */
	public long getSampleSize() {
		return sampleSize;
	}
	
	/**
	 * @return numOfChannels
	 */
	public long getNumOfChannels() {
		return numOfChannels;
	}
	
	/**
	 * @return length
	 */
	public long getLength() {
		return length;
	}
	
	/**
	 * Set the encoding used for this audio stream. 
	 * @param _encoding		- the encoding
	 * @return	<code>true</code> -  encoding string is changed
	 * 			<code>false</code> - null value, encoding string is not changed.
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
	 * Sets the sample size of the audio stream, -1 is not allowed
	 * 
	 * @param _sampleSize	- the size of the audio sample
	 * @throws FatalException
	 */
	public void setSampleSize(long _sampleSize) throws FatalException {
		String methodName = "setSampleSize(long)";
		
		if (_sampleSize < MIN_SAMPLE_SIZE || _sampleSize > MAX_SAMPLE_SIZE) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_sampleSize: " + _sampleSize,
				new FatalException("Not a valid sample size")
			);
		}
		sampleSize= _sampleSize;
	}
	
	/**
	 * Sets the frame size of the audio stream, -1 is not allowed
	 * 
	 * @param _frameSize	- the audio frame size
	 * @throws FatalException
	 */
	public void setFrameSize(long _frameSize) throws FatalException {
		String methodName = "setFrameSize(long)";
		
		if (_frameSize < MIN_FRAME_SIZE || _frameSize > MAX_FRAME_SIZE) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_frameSize: " + _frameSize,
				new FatalException("Not a valid frame size "));
		}
		frameSize= _frameSize;
	}
	
	/**
	 * Sets the number of channels used by the audio stream, -1 is not allowed
	 * 
	 * @param _numOfChannels	- no of channels
	 * @throws FatalException
	 */
	public void setNoOfChannels(long _numOfChannels) throws FatalException {
		String methodName = "setNoOfChannels(long)";
		
		if (_numOfChannels < MIN_NO_OF_CHANNELS || _numOfChannels > MAX_NO_OF_CHANNELS) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_numOfChannels: " + _numOfChannels,
				new FatalException("Not a valid no_of_channels"));
		}
		numOfChannels= _numOfChannels;
	}
	
	/**
	 * Sets the sample rate (samples/seconds) of the audio stream
	 * 
	 * @param _sampleRate	- sample rate
	 * @throws FatalException
	 */
	public void setSampleRate(float _sampleRate) throws FatalException {
		String methodName = "setSampleRate(float)";
		
		if (_sampleRate < MIN_SAMPLE_RATE || _sampleRate > MAX_SAMPLE_RATE) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_sampleRate: " + _sampleRate,
				new FatalException("Not a valid sampleRate"));
		}
		sampleRate = _sampleRate;
	}
	
	/**
	 * Sets the frames rate (samples per second per frame of the audio stream
	 * 
	 * @param _frameRate	- samples per second per frame 
	 * @throws FatalException
	 */
	public void setFrameRate(float _frameRate) throws FatalException {
		String methodName = "setFrameRate(float)";
		
		if (_frameRate < MIN_FRAME_RATE || _frameRate > MAX_FRAME_RATE) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_frameRate: " + _frameRate,
				new FatalException("Not a valid frameRate"));
		}
		frameRate = _frameRate;
	}
	
	/**
	 * Sets the data rate (bits per second) of the audio stream
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
	 * Sets the length of the audio stream, -1 is not allowed
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
	 * Returns the name of a database table containing information specific to
	 * this audio type.
	 * 
	 * @return the name of a database table containing information specific to
	 * this audio type
	 */
	public String getBsAudioTable() {
		return this.bsAudioTable;
	}      
	
	/**
	 * setting the audio stream to be enabled or not
	 * @param _enabled
	 */
	public void setEnabled(boolean _enabled) {
		isEnabled = _enabled;
	}
	
	/**
	 * Sets the name of a database table containing information specific to
	 * this audio type.
	 * 
	 * @param _bsTable the name of a database table containing information 
	 * specific to this audio type
	 * @throws FatalException
	 */
	public void setBsAudioTable(String _bsTable) throws FatalException {
		//	check that the argument is not null
		if (_bsTable == null || 
			_bsTable.length() > MAX_BSTABLE_LENGTH ||
			!ArchiveDatabase.isValidAudioBitstreamTable(_bsTable)){
				// illegal bitstream image table name
				Informer.getInstance().fail(this, "setBsAudioTable(String)",
				"Illegal argument",
				"_bsTable: " + _bsTable,
				new FatalException("Not a valid bitstream audio table name"));			
		}		
		bsAudioTable = _bsTable;
	}
	
	/**
	 * check to see if the audio data is in compressed format
	 * @return compressed - true (compressed), false(uncompressed)
	 */
	public boolean isCompressed() {
		boolean compressed = false;
		
		// if the data in non-linear(PCM), then it is compressed
		if (!encoding.equalsIgnoreCase(ENCODE_LINEAR))
			compressed = true;
		
		return compressed;
	}
	/**
	 * check if the normalization process is required for this audio stream
	 * 
	 * @return normalizationRequired
	 */
	public boolean isNormalizationRequired() {
		boolean normalizationRequired = false;
		
		// if this audio stream is compressed, need to normalize it.
		if (isCompressed())
			normalizationRequired = true;
		
		return normalizationRequired;
	}
	
	/**
	 * fill in the database column-value pairs for this audio
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();
		
		columns.add(ArchiveDatabase.COL_BS_AUDIO_BSID);
		values.add(this.getBsid());
		columns.add(ArchiveDatabase.COL_BS_AUDIO_DFID);
		values.add(this.getDfid());
		columns.add(ArchiveDatabase.COL_BS_AUDIO_ENCODING);
		values.add(encoding);
		columns.add(ArchiveDatabase.COL_BS_AUDIO_SAMPLE_RATE);
		values.add(this.getSampleRate());
		columns.add(ArchiveDatabase.COL_BS_AUDIO_SAMPLE_SIZE);
		values.add(this.getSampleSize());
		columns.add(ArchiveDatabase.COL_BS_AUDIO_NO_OF_CHANNELS);
		values.add(this.getNumOfChannels());
		columns.add(ArchiveDatabase.COL_BS_AUDIO_FRAME_RATE);
		values.add( frameRate);
		columns.add(ArchiveDatabase.COL_BS_AUDIO_FRAME_SIZE);
		values.add( frameSize);
		columns.add(ArchiveDatabase.COL_BS_AUDIO_LENGTH);
		values.add(this.getLength());
		columns.add(ArchiveDatabase.COL_BS_AUDIO_DATA_QUALITY);
		values.add(this.getDataQuality());
		columns.add(ArchiveDatabase.COL_BS_AUDIO_DATA_RATE);
		values.add( dataRate);
		columns.add(ArchiveDatabase.COL_BS_AUDIO_BITRATE_MODE);
		values.add( bitrateMode);
		columns.add(ArchiveDatabase.COL_BS_AUDIO_ENABLED);
		values.add(isEnabled);
		if (this.getBsAudioTable() != null) {
			columns.add(ArchiveDatabase.COL_BS_AUDIO_BS_TABLE);
			values.add(this.getBsAudioTable());
		}
	}
	
	/**
	 * Insert this audio into the database
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbInsert() throws FatalException{
		super.dbInsert();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		return tcon.insert(ArchiveDatabase.TABLE_BS_AUDIO, columns, colValues);
	}
	
	/**
	 * update the entry in the database for this audio
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
				ArchiveDatabase.COL_BS_AUDIO_BSID, SqlQuote.escapeString(this.getBsid()));
		return tcon.update(ArchiveDatabase.TABLE_BS_AUDIO, columns, colValues, whereClause);
	}
	
	

	 /**
     * Puts all the audio stream members and their values in a String.
     * 
     * @return the members of this class as a String
     */
    public String toString() {
        StringBuffer strBuffer = new StringBuffer("");
    	
        strBuffer.append(super.toString() + "\n");
        strBuffer.append("sampleRate: " + sampleRate + "\n"); 
        strBuffer.append("sampleSize: " + sampleSize + "\n");	
        strBuffer.append("numOfChannels: " + numOfChannels + "\n");
        strBuffer.append("frameRate: " + frameRate + "\n"); 
        strBuffer.append("frameSize: " + frameSize + "\n");	
        strBuffer.append("length: " + length + "\n");
        strBuffer.append("dataRate: " + dataRate + "\n"); 
        strBuffer.append("bitrateMode: " + bitrateMode + "\n"); 
        strBuffer.append("dataQuality: " + dataQuality + "\n");
        strBuffer.append("isEnabled: " + isEnabled + "\n"); 
        return strBuffer.toString();
    }
    
	/** 
	 * dump the content of this object into a XML document.
	 * @return Document object
	 * @throws FatalException 
	 */
	public Document toXML() throws FatalException {
        // Document.
        Document doc = super.toXML();

        // Root element.
        Element rootElement = doc.getDocumentElement();

        // Namespace. 
        String namespace = rootElement.getNamespaceURI();
        
	    /* Bs Audio */
	    Element bsAudioElement = doc.createElementNS(namespace, "BS_AUDIO");
	    rootElement.appendChild(bsAudioElement);

	    /* Dfid */
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
	    Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsAudioElement.appendChild(dfidElement);

	    /* Bsid */
	    Element bsidElement = doc.createElementNS(namespace, "BSID");
	    String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
	    Text bsidText = doc.createTextNode(bsidValue);
	    bsidElement.appendChild(bsidText);
	    bsAudioElement.appendChild(bsidElement);
	    
	    /* ENCODING */
	    Element encodingElement = doc.createElementNS(namespace, "ENCODING");
	    Text encodingText = doc.createTextNode(encoding); 
	    encodingElement.appendChild(encodingText);
	    bsAudioElement.appendChild(encodingElement);

	    /* SAMPLE_RATE */
	    Element sampleRateElement = doc.createElementNS(namespace, "SAMPLE_RATE");
	    Text sampleRateText = doc.createTextNode(Float.toString(sampleRate));
	    sampleRateElement.appendChild(sampleRateText);
	    bsAudioElement.appendChild(sampleRateElement);
	    
	    /* SAMPLE_SIZE */
	    Element sampleSizeElement = doc.createElementNS(namespace, "SAMPLE_SIZE");
	    Text sampleSizeText = doc.createTextNode(Long.toString(sampleSize));
	    sampleSizeElement.appendChild(sampleSizeText);
	    bsAudioElement.appendChild(sampleSizeElement);
	    
	    /* NO_OF_CHANNELS */
	    Element numOfChannelsElement = doc.createElementNS(namespace, "NO_OF_CHANNELS");
	    Text numOfChannelsText = doc.createTextNode(Long.toString(numOfChannels));
	    numOfChannelsElement.appendChild(numOfChannelsText);
	    bsAudioElement.appendChild(numOfChannelsElement);
	    
	    /* FRAME_RATE */
	    Element frameRateElement = doc.createElementNS(namespace, "FRAME_RATE");
	    Text frameRateText = doc.createTextNode(Float.toString(frameRate));
	    frameRateElement.appendChild(frameRateText);
	    bsAudioElement.appendChild(frameRateElement);

	    /* FRAME_SIZE */
	    Element frameSizeElement = doc.createElementNS(namespace, "FRAME_SIZE");
	    Text frameSizeText = doc.createTextNode(Long.toString(frameSize));
	    frameSizeElement.appendChild(frameSizeText);
	    bsAudioElement.appendChild(frameSizeElement);
	    
	    /* LENGTH */
	    Element lengthElement = doc.createElementNS(namespace, "LENGTH");
	    Text lengthText = doc.createTextNode(Long.toString(length));
	    lengthElement.appendChild(lengthText);
	    bsAudioElement.appendChild(lengthElement);
	    
	    /* DATA_QUALITY */
	    Element dataQualityElement = doc.createElementNS(namespace, "DATA_QUALITY");
	    Text dataQualityText = doc.createTextNode(Long.toString(dataQuality));
	    dataQualityElement.appendChild(dataQualityText);
	    bsAudioElement.appendChild(dataQualityElement);

	    /* DATA_RATE */
	    Element dataRateElement = doc.createElementNS(namespace, "DATA_RATE");
	    Text dataRateText = doc.createTextNode(Float.toString(dataRate));
	    dataRateElement.appendChild(dataRateText);
	    bsAudioElement.appendChild(dataRateElement);
	    
	    /* BITRATE_MODE */
	    Element bitrateModeElement = doc.createElementNS(namespace, "BITRATE_MODE");
	    Text bitrateModeText = doc.createTextNode(bitrateMode);
	    bitrateModeElement.appendChild(bitrateModeText);
	    bsAudioElement.appendChild(bitrateModeElement);
	    
	    /* ENABLED */
	    Element isEnabledElement = doc.createElementNS(namespace, "ENABLED");
	    Text isEnabledText = doc.createTextNode(Boolean.toString(isEnabled).toUpperCase());
	    isEnabledElement.appendChild(isEnabledText);
	    bsAudioElement.appendChild(isEnabledElement);
	
	    /* Bs Table */
	    Element bsTableElement = doc.createElementNS(namespace, "BS_TABLE");
	    String bsTableValue = (this.getBsAudioTable() != null ? this.getBsAudioTable() : "" );
	    Text bsTableText = doc.createTextNode(bsTableValue);
	    bsTableElement.appendChild(bsTableText);
	    bsAudioElement.appendChild(bsTableElement);
	    
        return doc;
	}
}
