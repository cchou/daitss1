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
package edu.fcla.daitss.format.audio.wave;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.bitstream.BitstreamRole;
import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.audio.Audio;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.Informer;
/**
 * WaveAudio represents an audio stream in a wave file
 * @author carol
 */
public class WaveAudio extends Audio {
	/**
	 * constants used in the format tage of wave audio stream format chunk
	 * from http://hul.harvard.edu/jhove/wave-hul.html, or http://msdn2.microsoft
	 * .com/en-us/library/ms867195.aspx. Only those audio bitstream formats that 
	 * are current supported or plan to be supported in the near future are listed here.
	 */
	protected final static int FORMAT_UNKNOWN			= 0x0000;	// unknown
	protected final static int FORMAT_PCM 				= 0x0001;	// PCM audio in integer format
	protected final static int FORMAT_ADPCM 			= 0x0002;	// ADPCM, Microsoft adaptive PCM
	protected final static int FORMAT_PCM_IEEE_FLOAT	= 0x0003;	// PCM audio in IEEE floating-point format
	protected final static int FORMAT_MP3	 			= 0x0055;	// MPEG layer 3
    
	/**
	 * constants related to wave format header information 
	 */
	public final static int WAVEFORMAT_SIZE 			= 14;  	// bytes
	/**
	 * constants related to wave format header information  
	 */
	public final static int PCMWAVEFORMATEX_SIZE 		= 16;  	// bytes
	/**
	 * constants related to wave format header information 
	 */
	public final static int WAVEFORMATEX_SIZE 			= 18;  	// bytes
	/**
	 * constants related to wave format header information 
	 */
	public final static int WAVEFORMATEXTENSIBLE_SIZE 	= 28;  	// bytes + GUID
    
    /**
     * speaker position
     * http://www.microsoft.com/whdc/device/audio/multichaud.mspx
     */
    protected final static int SPEAKER_FRONT_LEFT = 0x1;
    protected final static int SPEAKER_FRONT_RIGHT = 0x2;
    protected final static int SPEAKER_FRONT_CENTER = 0x4;
    protected final static int SPEAKER_LOW_FREQUENCY = 0x8;
    protected final static int SPEAKER_BACK_LEFT = 0x10;
    protected final static int SPEAKER_BACK_RIGHT = 0x20;
    protected final static int SPEAKER_FRONT_LEFT_OF_CENTER = 0x40;
    protected final static int SPEAKER_FRONT_RIGHT_OF_CENTER = 0x80;
    protected final static int SPEAKER_BACK_CENTER = 0x100;
    protected final static int SPEAKER_SIDE_LEFT = 0x200;
    protected final static int SPEAKER_SIDE_RIGHT = 0x400;
    protected final static int SPEAKER_TOP_CENTER = 0x800;
    protected final static int SPEAKER_TOP_FRONT_LEFT = 0x1000;
    protected final static int SPEAKER_TOP_FRONT_CENTER = 0x2000;
    protected final static int SPEAKER_TOP_FRONT_RIGHT = 0x4000;
    protected final static int SPEAKER_TOP_BACK_LEFT = 0x8000;
    protected final static int SPEAKER_TOP_BACK_CENTER = 0x10000;
    protected final static int SPEAKER_TOP_BACK_RIGHT = 0x20000;

	/**
	 * constants used for determining the minimum and maximum value of class members. 
	 */
	private static final long MAX_VALID_BITS_PER_SAMPLE 		= 4294967295L;	// max. valid bits per sample
	private static final long MIN_VALID_BITS_PER_SAMPLE 		= 0L;			// min. valid bits per sample
	private static final long MAX_SAMPLES_PER_BLOCK 			= 4294967295L;	// max. samples per block
	private static final long MIN_SAMPLES_PER_BLOCK  			= 0L;			// min.  samples per block
	
	/**
	 * A string representing the Normalized format for the audio stream in the wave files.
	 * It is stored as PCM data with 16 bits/sample, signed, LE, 
	 */
	public static final String WAVE_AUDIO_NORM = "pcm_s16le";
    
    /**
     * This value indicates how many bytes of wave data must be streamed to a D/A converter 
     * per second in order to play the wave file. This information is useful when determining 
     * if data can be streamed from the source fast enough to keep up with playback.  This value
     * is stored in each wave file.  However, it can also be easily calculated with the formula:
     * AvgBytesPerSec = SampleRate * BlockAlign
     */ 
    private long aveBytesPerSec = -1;
    
    /**
     * The number of bytes per sample (specified in wave file).  
     * This value is not affected by the number of channels 
     * and can be calculated with the formula:
     * BlockAlign = BitsPerSample / 8 * NumChannels
     */ 
    private int blockAlign = -1;
    
    /**
     * Size, in bytes, of extra format information appended to the 
     * end of the WAVEFORMATEX structure. 
     */
    private int cbSize = -1;
    
    /**
     * actual number of bits containing the sound data per sample, excluding padding
     */ 
    private int validBitsPerSample = -1;
    
    /**
     * Number of samples contained in one compressed block of audio data.
     */ 
    private int samplesPerBlock = -1;
    
    /**
	 * a collection of the string representation of the bitmask specifying the assignment 
	 * of channels in the stream to speaker positions
	 */
    private Vector<String> channels = new Vector<String>();

    boolean isWaveFormatExtensible = false;
   
    /**
	 * a hashtable to store the mapping between the 2 byte format tag encoding code to the string
	 * representation of the format tag.  It is statically initialized to make sure it can be used 
	 * at AviAudio class 
	 */
	public static Hashtable<Integer, String> encodingTable = new Hashtable<Integer, String>();
	 	static {
			encodingTable.put(new Integer(FORMAT_UNKNOWN), ENCODE_UNKNOWN);
			encodingTable.put(new Integer(FORMAT_PCM), ENCODE_LINEAR);
			encodingTable.put(new Integer(FORMAT_ADPCM), ENCODE_LINEAR);
			encodingTable.put(new Integer(FORMAT_PCM_IEEE_FLOAT), ENCODE_LINEAR);
			//not yet, encodingTable.put(new Integer(FORMAT_MP3), ENCODE_MP3);
	 	}
	 	
	/**
	 * Construct a audio bitstream that is retrieved from a wave file
	 * @param df the file containing this video bitstream
	 * @throws FatalException
	 */
	public WaveAudio(DataFile df) throws FatalException {
		super(df);
		this.setBsAudioTable(ArchiveDatabase.TABLE_BS_AUDIO_WAVE);
		setRole(BitstreamRole.AUDIO_MAIN);
	}

	  /**
     * @return average bytes per second.
     */
    public long getAveBytesPerSec() {
    	return aveBytesPerSec;
    }
    
    /**
     * @return blockAlign
     */
    public int getBlockAlign() {
    	return blockAlign;
    }
    
    /**
     * @return validBitsPerSample
     */
    public int getValidBitsPerSample() {
    	return validBitsPerSample;
    }
    
    /**
     * @return samplesPerBlock
     */
    public int getSamplesPerBlock() {
    	return samplesPerBlock;
    }
    
    /**
     * @return audio channels
     */
    public Vector getChannels() {
    	return channels;
    }
	/**
	 * Set the encoding used for this audio stream. 
	 * @param formatTag		- a wave format tag for the audio encoding
	 * @return	<code>true</code> -  valid encoding.
	 * 			<code>false</code> - invalid encoding, encoding string is not changed.
	 * @throws FatalException
	 */
	public boolean setEncoding(int formatTag) throws FatalException {
		boolean found = false;
		
		// translate this formatTags into a string representation of the encoding format
	    String strEncoding = (String) encodingTable.get(new Integer(formatTag));
		
	    if (strEncoding != null) {
	    	found = super.setEncoding(strEncoding);
	    	// wave format does not store compression info.  The linear encoding indicates there is no compression.
	    	if (strEncoding.equals(ENCODE_LINEAR))
	    		compression.setCompression(Compression.COMP_NONE);
	    } else {
			// unsupported encoding, report as a limitation and downgrade it to bit-level
			this.getDf().addLimitation(this.getDf().getLimitationsPossible().getSevereElement(
				WaveLimitations.WAVE_UNSUPPORTED_ENCODING));
	    	// store its original formatTags such that we know what encoding to need to
			// support in the future.
			setEncoding(new Integer(formatTag).toString());
			
			found = false;
	    }

		return found;
	}
	
	/**
	 * Sets the number of valid bits per sample -1 is not allowed
	 * 
	 * @param _validBitsPerSample	-  number of valid bits per sample
	 * @throws FatalException
	 */
	public void setValidBitsPerSample(int _validBitsPerSample) throws FatalException {
		String methodName = "setValidBitsPerSample(long)";
		
		if (_validBitsPerSample < MIN_VALID_BITS_PER_SAMPLE || _validBitsPerSample > MAX_VALID_BITS_PER_SAMPLE) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_validBitsPerSample: " + _validBitsPerSample,
				new FatalException("Not a valid number for validBitsPerSample "));
		}
		validBitsPerSample = _validBitsPerSample;
	}
	
	/**
	 * Sets the number of samples per block, -1 is not allowed
	 * 
	 * @param _samplesPerBlock	-  number of of samples per block
	 * @throws FatalException
	 */
	public void setSamplesPerBlock(int _samplesPerBlock) throws FatalException {
		String methodName = "setSamplesPerBlock(long)";
		
		if (_samplesPerBlock < MIN_SAMPLES_PER_BLOCK || _samplesPerBlock > MAX_SAMPLES_PER_BLOCK) {
			Informer.getInstance().fail(this, methodName,
				"Illegal argument", "_setSamplesPerBlock: " + _samplesPerBlock,
				new FatalException("Not a valid number for samplesPerBlock"));
		}
		samplesPerBlock = _samplesPerBlock;
	}
	/**
	 * Set the channel information of this audio stream.
	 *  as described in http://www.microsoft.com/whdc/device/audio/multichaud.mspx
	 * @param channelMask - an integer that contains the bitMask of the channels used in this audio
	 * @throws FatalException
	 */
	public void setChannels(long channelMask)  throws FatalException {
       	if ( (channelMask & SPEAKER_FRONT_LEFT) > 0) { 
       		channels.add("SPEAKER_FRONT_LEFT");
       	}
       	
       	else if ( (channelMask & SPEAKER_FRONT_RIGHT) > 0) {
       		channels.add("SPEAKER_FRONT_RIGHT");
       	}
       	
       	if ( (channelMask & SPEAKER_FRONT_CENTER) > 0) { 
       		channels.add("SPEAKER_FRONT_CENTER");
    	}
    	
       	else if ( (channelMask & SPEAKER_LOW_FREQUENCY) > 0) { 
       		channels.add("SPEAKER_LOW_FREQUENCY");
    	}
       	
       	else if ( (channelMask & SPEAKER_BACK_LEFT) > 0) { 
       		channels.add("SPEAKER_BACK_LEFT");
    	}
  
       	else if ( (channelMask & SPEAKER_BACK_RIGHT) > 0) { 
       		channels.add("SPEAKER_BACK_RIGHT");
    	}
     	
       	else if ( (channelMask & SPEAKER_FRONT_LEFT_OF_CENTER) > 0) { 
       		channels.add("SPEAKER_FRONT_LEFT_OF_CENTER");
    	}
            
       	else if ( (channelMask & SPEAKER_FRONT_RIGHT_OF_CENTER) > 0) { 
       		channels.add("SPEAKER_FRONT_RIGHT_OF_CENTER");
    	}
       	
       	else if ( (channelMask & SPEAKER_BACK_CENTER) > 0) { 
       		channels.add("SPEAKER_BACK_CENTER");
    	}
       	
       	else if ( (channelMask & SPEAKER_SIDE_LEFT) > 0) { 
       		channels.add("SPEAKER_SIDE_LEFT");
    	}
      	
       	else if ( (channelMask & SPEAKER_SIDE_RIGHT) > 0) { 
       		channels.add("SPEAKER_SIDE_RIGHT");
    	}
      	
       	else if ( (channelMask & SPEAKER_TOP_CENTER) > 0) { 
       		channels.add("SPEAKER_TOP_CENTER");
    	}
      	
       	else if ( (channelMask & SPEAKER_TOP_FRONT_LEFT) > 0) { 
       		channels.add("SPEAKER_TOP_FRONT_LEFT");
    	}
      	
       	else if ( (channelMask & SPEAKER_TOP_FRONT_CENTER) > 0) { 
       		channels.add("SPEAKER_TOP_FRONT_CENTER");
    	}
      	
       	else if ( (channelMask & SPEAKER_TOP_FRONT_RIGHT) > 0) { 
       		channels.add("SPEAKER_TOP_FRONT_RIGHT");
    	}
      	
       	else if ( (channelMask & SPEAKER_TOP_BACK_LEFT) > 0) { 
       		channels.add("SPEAKER_TOP_BACK_LEFT");
    	}
     	
       	else if ( (channelMask & SPEAKER_TOP_BACK_CENTER) > 0) { 
       		channels.add("SPEAKER_TOP_BACK_CENTER");
    	}
     	
       	else if ( (channelMask & SPEAKER_TOP_BACK_RIGHT) > 0) { 
       		channels.add("SPEAKER_TOP_BACK_RIGHT");
    	}
       	else {
			// unsupported channel position, report as an anomoly
			SevereElement ta = this.getDf().getAnomsPossible().getSevereElement(
				WaveAnomalies.WAVE_UNRECOGNIZABLE_SPEAKER_POSITION);
			this.getDf().addAnomaly(ta);
       	}
	}
	
	/** 
	 * setup this audio stream with the data read from the stream format 
	 * chunk in the wave file
	 * @param reader	- the wave file to read from.  The current file pointer shall be
	 * 					at the start of the stream format chunk. 
	 * @param chunkSize 
	 * @return byteRead	- the number of bytes read from the wave file by this audio stream.
	 * @throws FatalException
	 */
	public long readFrom(ByteReader reader, long chunkSize) throws FatalException{
		long bytesRead = 0;
		
		// read in the 2 bytes format tag
		int formatTag = (int) reader.readBytes(2, DataFile.BYTE_ORDER_LE); // wFormatTag
		
		setEncoding(formatTag);

		if (formatTag == FORMAT_PCM || formatTag == FORMAT_ADPCM )
			// Constant bitrate mode for linear PCM and ADPCM format
			bitrateMode = CBR;
		else  {
			// TODO: If the format is MP3, parse throught the entire MP3 
			// bitstream to determine if it is VBR
			bitrateMode = UNKNOWN;
		}
		
		setNoOfChannels(reader.readBytes(2, DataFile.BYTE_ORDER_LE)); // nChannels;
		sampleRate=  reader.readBytes(4, DataFile.BYTE_ORDER_LE); // nSamplesPerSec;
	    aveBytesPerSec =  reader.readBytes(4, DataFile.BYTE_ORDER_LE); // nAvgBytesPerSec;
	    blockAlign =  (int) reader.readBytes(2, DataFile.BYTE_ORDER_LE); // nBlockAlign;
	    bytesRead = WAVEFORMAT_SIZE;

	    // expect PCMWAVEFORMAT data
	    if (chunkSize >= PCMWAVEFORMATEX_SIZE) {
	    	setSampleSize(reader.readBytes(2, DataFile.BYTE_ORDER_LE)); // wBitsPerSample;
	    	bytesRead = PCMWAVEFORMATEX_SIZE;
	    }
	    
	    // expect WAVEFORMATEX data
	    if (chunkSize >= WAVEFORMATEX_SIZE) {
	    	cbSize = (int) reader.readBytes(2, DataFile.BYTE_ORDER_LE); // cbSize;
	    	bytesRead = WAVEFORMATEX_SIZE;
	    }
	    
	    // expect WAVEFORMATEXTENSIBLE data
	    if (chunkSize >= WAVEFORMATEXTENSIBLE_SIZE) {
	    	isWaveFormatExtensible = true;
	    	setValidBitsPerSample((int) reader.readBytes(2, DataFile.BYTE_ORDER_LE)); // wValidBitsPerSample;
	    	setSamplesPerBlock((int) reader.readBytes(2, DataFile.BYTE_ORDER_LE)); 	// wSamplesPerBlock;
	    	reader.skipBytes(2); 	// wReserved, should be 0.  Skip it.
	    	setChannels(reader.readBytes(4, DataFile.BYTE_ORDER_LE)); // dwChannelMask;
	    	bytesRead = WAVEFORMATEXTENSIBLE_SIZE;
	    }

	    // calcuate the frame rate, frame size and the data rate based on the information
	    // read from the format chunk
		frameRate = sampleRate * numOfChannels;
		setFrameSize(sampleSize * numOfChannels);
		dataRate = frameRate * sampleSize;
		return bytesRead;
	}
	
	 /**
     * Puts all the audio stream members and their values in a String.
     * 
     * @return the members of this class as a String
     */
    public String toString() {
        StringBuffer strBuffer = new StringBuffer("");
        
        strBuffer.append(super.toString() + "\n");
        
        strBuffer.append("Average Bytes Per Second: " + aveBytesPerSec + "\n"); 
        strBuffer.append("Block Align: " + blockAlign + "\n");
        
        // the following attributes are only valid if this audio stream is in 
        // WaveFormatExtensible format
        if (isWaveFormatExtensible) {
	        strBuffer.append("Valid Bits Per Sample: " + validBitsPerSample + "\n"); 
	        strBuffer.append("Samples Per Block: " + samplesPerBlock + "\n");
	     
	        // iterate through all the channel mappings
	        strBuffer.append("Channels: ");
	        Iterator itr = this.channels.iterator();
	        while (itr.hasNext()) {
	            strBuffer.append(itr.next() + ", ");
	        }
	        strBuffer.append("\n");
        }
        return strBuffer.toString();        
    }
    
	/**
	 * fill in the database column-value pairs for this audio
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();
		
		columns.add(ArchiveDatabase.COL_BS_AUDIO_WAVE_BSID);
		values.add(this.getBsid());
		columns.add(ArchiveDatabase.COL_BS_AUDIO_WAVE_DFID);
		values.add(this.getDfid());
		
		// Only store those WaveFormatExtensible information to the database if this audio 
		// stream contains WaveFormatExtensible information.
		if (isWaveFormatExtensible) {
			columns.add(ArchiveDatabase.COL_BS_AUDIO_WAVE_VALID_BITS_PER_SAMPLE);
			values.add(this.getValidBitsPerSample());
			columns.add(ArchiveDatabase.COL_BS_AUDIO_WAVE_SAMPLES_PER_BLOCK);
			values.add(this.getSamplesPerBlock());
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
		
		return tcon.insert(ArchiveDatabase.TABLE_BS_AUDIO_WAVE, columns, colValues);
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
				ArchiveDatabase.COL_BS_AUDIO_WAVE_BSID, SqlQuote.escapeString(this.getBsid()));
		return tcon.update(ArchiveDatabase.TABLE_BS_AUDIO_WAVE, columns, colValues, whereClause);
	}
	

	/** 
	 * dump the content of this object into a XML document.
	 * @return 
	 * @throws FatalException 
	 */
	public Document toXML() throws FatalException {
	    // Daitss Document
	    Document doc = super.toXML();
	    
	    // Daitss Element 
	    Element rootElement = doc.getDocumentElement();
	    
	    // Daitss Namespace
	    String namespace = rootElement.getNamespaceURI();
	    
	    // Bs Audio Wave
	    Element bsAudioWaveElement = doc.createElementNS(namespace, "BS_AUDIO_WAVE");
	    rootElement.appendChild(bsAudioWaveElement);

	    // Dfid 
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
	    Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsAudioWaveElement.appendChild(dfidElement);

	    // Bsid 
	    Element bsidElement = doc.createElementNS(namespace, "BSID");
	    String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
	    Text bsidText = doc.createTextNode(bsidValue);
	    bsidElement.appendChild(bsidText);
	    bsAudioWaveElement.appendChild(bsidElement);
        /*
	    // Average Bytes Per Second 
		Element pAveBytesPerSecondElement = doc.createElementNS(namespace, "AVERAGE_BYTES_PER_SECOND");
        Text pAveBytesPerSecondText = doc.createTextNode(Long.toString(this.getAveBytesPerSec()));
        pAveBytesPerSecondElement.appendChild(pAveBytesPerSecondText);
        bsAudioWaveElement.appendChild(pAveBytesPerSecondElement);
	    
	    // Block Align
		Element pBlockAlignElement = doc.createElementNS(namespace, "BLOCK_ALIGN");
        Text pBlockAlignText = doc.createTextNode(Integer.toString(this.getBlockAlign()));
        pBlockAlignElement.appendChild(pBlockAlignText);
        bsAudioWaveElement.appendChild(pBlockAlignElement);
        */
	    
		// Only store those WaveFormatExtensible information to XML if this audio 
		// stream contains WaveFormatExtensible information.
		if (isWaveFormatExtensible) {
		    // Valid Bits Per Sample
			Element pValidBitsPerSampleElement = doc.createElementNS(namespace, "VALID_BITS_PER_SAMPLE");
	        Text pValidBitsPerSampleText = doc.createTextNode(Integer.toString(this.getValidBitsPerSample()));
	        pValidBitsPerSampleElement.appendChild(pValidBitsPerSampleText);
	        bsAudioWaveElement.appendChild(pValidBitsPerSampleElement);
	        
		    // Samples Per Block
			Element pSamplesPerBlockElement = doc.createElementNS(namespace, "SAMPLES_PER_BLOCK");
	        Text pSamplePerBlockText = doc.createTextNode(Integer.toString(this.getSamplesPerBlock()));
	        pSamplesPerBlockElement.appendChild(pSamplePerBlockText);
	        bsAudioWaveElement.appendChild(pSamplesPerBlockElement);
		}
        return doc;
	}	
}
