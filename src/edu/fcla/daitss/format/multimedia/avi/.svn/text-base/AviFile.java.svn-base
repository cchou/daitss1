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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.file.TransformationFormat;
import edu.fcla.daitss.format.audio.*;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.format.video.*;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.ExternalProgram;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.Procedure;
import edu.fcla.daitss.bitstream.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * AviFile represents a Microsoft Audio Video Interleaved file 
 * @author carol
 */

public class AviFile extends DataFile {

	/**
	 * constants related to header chunk size in AVI
	 */
    private final static int AVI_HEADER_SIZE 		= 56;  		// bytes
    private final static int AVI_RIFF_HEADER_SIZE 	= 12;  		// bytes
    private final static int STRH_HEADER_SIZE 		= 56;  		// bytes
 
    /** 
     * Flags defined in the AVI header chunk
     */
    private final static int AVI_HASINDEX 			= 0x00000010;	// contains INDEX chunk
    private final static int AVI_USEINDEX			= 0x00000020;	// must use INDEX chunk
    private final static int AVI_ISINTERLEAVED 		= 0x00000100;	// interleaved file
    private final static int AVI_ISCAPTUREDFILE		= 0x00010000;	// special captured file format
    private final static int AVI_ISCOPYRIGHTED		= 0x00020000;	// copy right info.
    
	 /**
     * Fully-qualified name for this class. To be used for Informer calls from static methods.
     */
    private static String CLASSNAME = "edu.fcla.daitss.format.multimedia.avi";
    
    /**
     * AVI version 1.0
     */
    public static final String VERSION_1_0 = "1.0";
    
    /**
     * Micro Seconds Per frame
     */
    private int microSecPerFrame = 0;
    
    /**
     * Maximum Bytes Per Second
     */
    private int maxBytesPerSec;

    /**
     * Total Number of Frames
     */
    private int totalFrames = 0;
    
    /**
     * Initial Number of Frames in the AVI file prior to the intial frame sequence
     */
    private int initialFrames;

    /**
     * Number of Streams, including video and audio streams.
     */
    private int numOfStreams = 0;
    
    /**
     * Total Duration of this AVI file in seconds
     */
    private double duration = 0;
    
    /**
     * Whether or not the AVI file contains an index chunk
     */
    private boolean hasIndex = false;
    
    /**
     * Whether the order of presentation of data is determined by the index
     */
    private boolean useIndex = false;
    
    /**
     * Whether the streams in the AVI file is interleaved
     */
    private boolean isInterleaved = false;
    
    /**
     * Whether this file contains copyrighted data
     */
    private boolean isCopyrighted = false;
    
    /**
     * If the AVI file is a specially allocated file used for capturing real-time video
     */
    private boolean isCapturedFile = false;
  
    /**
     * This list of quicks that can be introduced during AVI normalization/migration process
     */
    private AviQuirks aviQuirks = null;
    /**
     * This list of encountered archival limitation during during processing the AVI file
     */
    private AviLimitations aviLimitations = null;
    
    /**
     * Determines whether or not the file is an AVI file when metadata about this
     * file is not available.
     * 
     * @param filePath - absolute path to an existing readable file
     * @return whether or not its a AVI file
     * @throws FatalException
     */
    public static boolean isType(String filePath) throws FatalException {
        return isType(filePath, null);
    }
    
    /**
     * Determines if this file is an AVI file when metadata about this file is
     * available.
     * 
     * @return whether or not its an AVI file
     * @param _filePath 	- absolute path to an existing readable file
     * @param _metadata - metadata about this DataFile
     * @throws FatalException
     */
	public static boolean isType(String _filePath, METSDocument _metadata) 
	throws FatalException {
		boolean isType = true;
	    String methodName = "isType(String, METSDocument)";
	    
		// check that filePath is != null and points to an existing file
        if (!FileUtil.isGoodFile(_filePath)) {
            Informer.getInstance().fail(CLASSNAME, methodName, "Illegal argument", 
            	"filePath: " + _filePath, new FatalException("invalid absolute path to a file"));
        }

        File theFile = new File(_filePath);
        ByteReader breader = new ByteReader(theFile, "r");
        byte[] data = new byte[4];
        
        // read in the RIFF fourcc (the first 4 bytes)
		breader.read(data);
        String riffFourCC = new String(data);
        if (!riffFourCC.equals("RIFF"))
        	isType = false;
        
        // the size of AVI RIFF chunk
        breader.readBytes(4, DataFile.BYTE_ORDER_LE);
		
        // read in the AVI fourcc (the next four bytes)
        breader.read(data);
        String aviFourCC = new String(data);
        if (!aviFourCC.equals("AVI "))
        	isType = false;
       
        breader.close();
        breader = null;
        theFile = null;
		
		return isType;
	}
	
	/**
	 * main method used for testing purposes
	 * @param args 
	 * @throws PackageException 
	 * @throws FatalException 
	 */
	public static void main(String[] args) throws PackageException, FatalException {
		String testFile = "/work/video/avi/ETD/Krishnan_Vinu_B_200405_MS_sma_switch_with_load.avi";
		if (AviFile.isType(testFile, null)) {
			System.out.println(testFile + " is an AVI file");
			AviFile avi = new AviFile(testFile, new SIP("/daitss/dev/AA00000000"));
			avi.setOid("F20090101_AAAAAA");
			avi.extractMetadata();
			System.out.println(avi);
		} else {
			System.out.println("Is not a Avi");
		}
	}
	  /**
     * The constructor to call for an existing AVI file when metadata about this
     * file is not available.
     * 
     * @param path 	- the absolute path to an existing readable file
     * @param ip	- the Information Package that this file is part of
     * @throws FatalException
     */
    public AviFile(String path, InformationPackage ip) throws FatalException {
        super(path, ip);

        this.setByteOrder(DataFile.BYTE_ORDER_NA);
        this.setMediaType(MimeMediaType.MIME_VIDEO_AVI);
		this.setMediaTypeVersion(VERSION_1_0);
        this.setFileExt(Extension.EXT_AVI);

        // build lists of potential anomalies
        this.anomsPossible = null;
        this.anomsPossible = new AviAnomalies();
        
        // build the list of possible avi quirks
        aviQuirks = new AviQuirks();
        
        // build the list of possible avi limitations
        limitationsPossible = new AviLimitations();
    }
	/**
	 * The constructor to call for an existing AVI file when metadata about
	 * this file is available.
	 * 
	 * @param path	- the absolute path to an existing readable file
	 * @param ip 	- the Information Package that this file is part of
	 * @param _metadata - a METSDocument containing metadata about this DataFile
	 * @throws FatalException
	 */
	public AviFile(String path, InformationPackage ip, METSDocument _metadata)
		throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}
    
	/**
	 * get string representing the duration of the AVI file in the format of HH:MM:SS
	 * @return String
	 */
	public String getDuration() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		// need to convert to milliseconds for Date object
		Date newDate = new Date((int) duration * 1000); 
		
		return sdf.format(newDate);
	}
	
	/**
	 * Read the file and retrieve the attributes of the AVI files
	 * 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		byte[] data = new byte[4];
		long chunkSize = -1;
		boolean sawMoviChunk = false;
		boolean sawHdrlChunk = false;
		boolean sawIdx1Chunk = false;
		
		// skip the first AVI_RIFF_HEADER_SIZE (12) bytes, we already check it in isType()
		reader.skipBytes(AVI_RIFF_HEADER_SIZE);

		while (reader.bytesLeft() > 12 ) {	
			reader.read(data); 
			String fourCC = new String(data);
			chunkSize = reader.readBytes(4, DataFile.BYTE_ORDER_LE);  
			
			if (fourCC.equals("LIST")) { 
				// LIST chunk
				reader.read(data);
				String chunkId = new String(data);
				if (chunkId.equals("hdrl")) {
					// process the main AVI header
					parseMainAVIHeader();
					sawHdrlChunk = true;
				}
				else if (chunkId.equals("strl")) {
					parseStreamChunk(chunkSize);
				} else if (chunkId.equals("movi")) {
					if (!sawHdrlChunk) {
						SevereElement ja = this.getAnomsPossible().
						getSevereElement(AviAnomalies.AVI_NO_HDRL_BEFORE_MOVI);
						this.addAnomaly(ja);
					}
					sawMoviChunk = true;
					// skip the rest of movi chunk, need to deduct the length of the subchunk ID
					reader.skipBytes((int) chunkSize-4);
				}
				else // unknown subchunk, skip it (deduct the length of the subchunk id)
					reader.skipBytes((int) chunkSize-4);
			} else if (fourCC.equals("idx1")){ 
				// index chunk "idx1"
				sawIdx1Chunk = true;
				reader.skipBytes((int) chunkSize);
			}	
			else {	// unknown chunk, skip it
				// skip the extra padding if this chunk is not byte aligned.
				if ((chunkSize % 2)  != 0)
					chunkSize++;
				
				if (chunkSize > 0)
				reader.skipBytes((int) chunkSize);
				else {
					// if there is a zero or negative value in the chunkSize, 
					// let's skip to the end of the files to to get us out of this infinite loop.
					reader.skipBytes((int) reader.bytesLeft());
				}
			}
		}
		
		// check if any required chunks is missing.
		if (!sawHdrlChunk) {
			SevereElement ja = this.getAnomsPossible().
			getSevereElement(AviAnomalies.AVI_HDRL_MISSING);
			this.addAnomaly(ja);
		}
	
		if (!sawMoviChunk) {
			SevereElement ja = this.getAnomsPossible().
			getSevereElement(AviAnomalies.AVI_MOVI_MISSING);
			this.addAnomaly(ja);
		}
	}
	
	/**
	 * parse and retrieve the value from the main AVI Header Chunk
	 * @return long
	 * 
	 * @throws FatalException
	 */
	private long parseMainAVIHeader() throws FatalException{
		byte[] data = new byte[4];
		long length = 0;
		
		reader.read(data);
		String avih = new String(data);
		
		// read avih chunk id
		if (!avih.equals("avih")) {
			SevereElement ja = this.getAnomsPossible().
				getSevereElement(AviAnomalies.AVI_BAD_MAIN_HEADER);
			this.addAnomaly(ja);
			return length;
		}
		
		// read in avih chunk length
		length = reader.readBytes(4, DataFile.BYTE_ORDER_LE);
	    if (length < AVI_HEADER_SIZE) {
			SevereElement se = this.getAnomsPossible().
				getSevereElement(AviAnomalies.AVI_MAIN_HEADER_TOO_SMALL);
			this.addAnomaly(se);
			return length;
	    }

	    microSecPerFrame = (int) reader.readBytes(4, DataFile.BYTE_ORDER_LE);
		maxBytesPerSec = (int) reader.readBytes(4, DataFile.BYTE_ORDER_LE);
		reader.skipBytes(4);  // skip the dwReserved1
		
		int flags = (int) reader.readBytes(4, DataFile.BYTE_ORDER_LE);
		// retrieve the flag and set members in this AVI file object accordingly
	    if ( (flags & AVI_HASINDEX) > 0) 
	    	hasIndex = true;
	    if ( (flags & AVI_USEINDEX) > 0)
	    	useIndex = true;
	    if ( (flags & AVI_ISINTERLEAVED) > 0) 
	    	isInterleaved = true;
	    if ( (flags & AVI_ISCAPTUREDFILE) > 0) 
	    	isCapturedFile = true;
	    if ( (flags & AVI_ISCOPYRIGHTED) > 0) 
	    	isCopyrighted = true;
	   
		totalFrames = (int) reader.readBytes(4, DataFile.BYTE_ORDER_LE);
		initialFrames = (int) reader.readBytes(4, DataFile.BYTE_ORDER_LE);
		numOfStreams = (int) reader.readBytes(4, DataFile.BYTE_ORDER_LE);
		reader.skipBytes(4);	// skip dwSuggestedBufferSize;
		reader.skipBytes(4);	// skip dwWidth;
		reader.skipBytes(4);	// skip dwHeight;
		reader.skipBytes(4);	// skip dwScale;
		reader.skipBytes(4);	// skip dwRate;
		reader.skipBytes(4);	// skip dwStart;
		reader.skipBytes(4);	// skip dwLength;

		// skip the rest of the header
		if (length > AVI_HEADER_SIZE)
			 reader.skipBytes((int) (length - AVI_HEADER_SIZE));
		
		// calculate the total duration (in seconds) of this AVI file
		if ( (totalFrames != 0) && (microSecPerFrame != 0) ) {
		    duration = ((microSecPerFrame * totalFrames) / 1000000) ;
		}
		return length;
	}

	/**
	 * parse and the stream chunk
	 * @param streamChunkSize 
	 * 
	 * @throws FatalException
	 */
	private void parseStreamChunk(long streamChunkSize) throws FatalException{
		byte[] data = new byte[4];
		// since "strl" (4cc) has been read in, we need to set the pointer to the 5th byte
		int currentPos = 4;			
		Bitstream currentStream = null;
		
		Long streamOffset = new Long((long) reader.getFilePointer());
	    while (currentPos < streamChunkSize) {
			reader.read(data);
			String fourCC = new String(data);
			long subChunkSize = reader.readBytes(4, DataFile.BYTE_ORDER_LE);
	
			if (fourCC.equals("strh")) {
				// stream header chunk
			    if (subChunkSize < STRH_HEADER_SIZE) {
					SevereElement ele = this.getAnomsPossible().
						getSevereElement(AviAnomalies.AVI_STRH_TOO_SMALL);
					this.addAnomaly(ele);
			    }
			    else
			    	currentStream = parseStreamHeader(subChunkSize);
			}
			else if (fourCC.equals("strf")) {
				// make sure there is a strh chunk preceeds this strf chunk,
				if (currentStream == null) {
					SevereElement ele = this.getAnomsPossible().
					getSevereElement(AviAnomalies.AVI_STRH_MISSING);
					this.addAnomaly(ele);
					//advance the file pointer or the rest of the file will be parsed incorrectly
					 reader.skipBytes((int) subChunkSize);	
				}
				else parseStreamFormat(currentStream, subChunkSize);
			}
			else {
			    // other chunk types
				// skip the extra padding if this chunk is not byte aligned.
				if ((subChunkSize % 2)  != 0)
					subChunkSize++;
				
			    reader.skipBytes((int)subChunkSize);
			}
			currentPos += subChunkSize + 8; // 8 bytes for fourCC and subChunkSize
		} 
	    
		// set the bitstream location (or contentLocation in PREMIS term)
		if (currentStream != null) {
			currentStream.setLocation(streamOffset.toString());
			currentStream.setLocationType(Bitstream.LOC_FILE_BYTE_OFFSET);
			// set the role of the bitstream
			if (currentStream instanceof AviVideo)
				((AviVideo) currentStream).setRole(BitstreamRole.VIDEO_MAIN);
			else 	if (currentStream instanceof AviAudio) 
				((AviAudio) currentStream).setRole(BitstreamRole.AUDIO_MAIN);
		}
	}
	/**
	 * parse and retrieve the value from the stream header chunk
	 * @param subChunkSize 
	 * @return Bitstream object 
	 * 
	 * @throws FatalException
	 */
	private Bitstream parseStreamHeader(long subChunkSize) throws FatalException{
		byte[] data = new byte[4];
		Bitstream currentStream = null;
		long bytesRead = 0;
		
	    // read the stream type
	    reader.read(data);
	    String streamType = new String(data);
	    bytesRead += 4;
	    		
	    if (streamType.equals("vids")) {
		    // found a video stream
	    	AviVideo videoStream = new AviVideo(this);
	    	bytesRead += videoStream.readFromSTRH(reader);
	    	this.addBitstream(videoStream);
	    	currentStream = videoStream;
	    }
	    else if (streamType.equals("auds")) {
		    // found an audio stream
	    	AviAudio audioStream = new AviAudio(this);
	    	bytesRead += audioStream.readFromSTRH(reader);
	    	this.addBitstream(audioStream);
	    	currentStream = audioStream;
	    }
	    else {
	    	// unidentifiable stream header, record it as an anomaly
			SevereElement ele = this.getAnomsPossible().
			getSevereElement(AviAnomalies.AVI_UNIDENTIFIABLE_STREAM_HEADER);
			this.addAnomaly(ele);
	    }
		// skip the rest of the stream header
		if (subChunkSize > bytesRead)
			 reader.skipBytes((int) (subChunkSize - bytesRead));
		return currentStream;
	}
	
	/**
	 * parse and retrieve values from the stream format chunk
	 * @param currentStream 
	 * @param subChunkSize 
	 * 
	 * @throws FatalException
	 */
	private void parseStreamFormat(Bitstream currentStream, long subChunkSize) throws FatalException{
		long bytesRead = 0;
		
		if (currentStream instanceof AviVideo) {
			// video stream
			AviVideo videoStream = (AviVideo) currentStream;
			bytesRead = videoStream.readFromSTRF(reader);
		} else 	if (currentStream instanceof AviAudio) {
			// audio stream
			AviAudio audioStream = (AviAudio) currentStream;
			bytesRead = audioStream.readFromSTRF(reader);
		}
		
        // skip the rest of Stream Format chunk 
		if (subChunkSize > bytesRead)
		 reader.skipBytes((int) (subChunkSize - bytesRead));	
	}
	
	/**
     * Sets the members for which their value depends on the value of the
     * members that were set by parsing this file. In other words, this is the
     * 'second phase' of setting this file's members. The first was the parsing
     * phase.
     * 
     * @throws FatalException
     */
    protected void evalMembers() throws FatalException {
        super.evalMembers();

        // evaluate the members for each stream found in this AVI file
        Iterator bsIter = this.getBitstreams().iterator();
        while (bsIter.hasNext()) {
            Bitstream bs = (Bitstream) bsIter.next();
            //bs.evalMembers();
        }
    }
    
	/**
	 * Creates a file or group of files that is the migrated version of
	 * this file. 
	 * 
	 * @throws PackageException
     * @throws FatalException
	 */
	public void migrate() throws PackageException, FatalException{
	    
       boolean migrate = migrateSetup();
        
        // exit early if we already migrated or this file
        // should not be migrated.
        if (!migrate){
            return;
        }
	}       
	
	/**
	 * fill in the database column-value pairs for the avi file
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();
		
		columns.add(ArchiveDatabase.COL_AVI_FILE_DFID);
		values.add(this.getOid());
		columns.add(ArchiveDatabase.COL_AVI_FILE_MAX_DATA_RATE);
		values.add(maxBytesPerSec);
		columns.add(ArchiveDatabase.COL_AVI_FILE_TOTAL_FRAMES);
		values.add(totalFrames);
		columns.add(ArchiveDatabase.COL_AVI_FILE_INITIAL_FRAMES);
		values.add(initialFrames);
		columns.add(ArchiveDatabase.COL_AVI_FILE_NO_OF_STREAMS);
		values.add( numOfStreams);
		columns.add(ArchiveDatabase.COL_AVI_FILE_DURATION);
		values.add( getDuration());
		columns.add(ArchiveDatabase.COL_AVI_FILE_HAS_INDEX);
		values.add(hasIndex);
		columns.add(ArchiveDatabase.COL_AVI_FILE_MUST_USE_INDEX);
		values.add(useIndex);
		columns.add(ArchiveDatabase.COL_AVI_FILE_IS_INTERLEAVED);
		values.add(isInterleaved);
		columns.add(ArchiveDatabase.COL_AVI_FILE_COPYRIGHTED);
		values.add(isCopyrighted);
		columns.add(ArchiveDatabase.COL_AVI_FILE_WAS_CAPTURED);
		values.add(isCapturedFile);
	}
	
	/**
	 * Insert this avi file into the database
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbInsert() throws FatalException{
		super.dbInsert();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		return tcon.insert(ArchiveDatabase.TABLE_AVI_FILE, columns, colValues);
	}
	
	/**
	 * update the entry in the database for this avi file
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
				ArchiveDatabase.COL_AVI_FILE_DFID, SqlQuote.escapeString(this.getOid()));
		return tcon.update(ArchiveDatabase.TABLE_AVI_FILE, columns, colValues, whereClause);
	}
	

	 /**
     * Puts all the Avi file's members and their values in a String.
     * 
     * @return the members of this class, including bitStreams, as a String
     */
    public String toString() {
        StringBuffer strBuffer = new StringBuffer("");
        
        strBuffer.append(super.toString() + "\n");

        strBuffer.append("Micro Sec. Per frame: " + microSecPerFrame + "\n"); 
        strBuffer.append("Max Bytes Per Sec. : " + maxBytesPerSec + "\n"); 
        strBuffer.append("Total Frames : " + totalFrames + "\n"); 
        strBuffer.append("Initial Frames : " + initialFrames + "\n"); 
        strBuffer.append("Number of Streams : " + numOfStreams + "\n"); 
        strBuffer.append("Duration (s) : " + duration + "\n"); 
        strBuffer.append("hasIndex : " + hasIndex + "\n"); 
        strBuffer.append("useIndex : " + useIndex + "\n"); 
        strBuffer.append("isInterleaved : " + isInterleaved + "\n"); 
        strBuffer.append("isCopyrighted : " + isCopyrighted + "\n"); 
        strBuffer.append("isCapturedFile : " + isCapturedFile + "\n"); 
  
        return strBuffer.toString();
        
    }
    
	/**
	 * @return Document object 
	 * @throws FatalException  
	 * 
	 */
	public Document toXML() throws FatalException {

        // Document.
        Document doc = super.toXML();

        // Root element.
        Element rootElement = doc.getDocumentElement();

        // Namespace. 
        String namespace = rootElement.getNamespaceURI();
	    
        /* Bs AviFile */
	    Element bsAviFileElement = doc.createElementNS(namespace, "AVI_FILE");
	    rootElement.appendChild(bsAviFileElement);

	    /* Dfid */
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getOid() != null ? this.getOid() : "" );
	    Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsAviFileElement.appendChild(dfidElement);

	    /* MAX_DATA_RATE */
	    Element maxDataRateElement = doc.createElementNS(namespace, "MAX_DATA_RATE");
	    Text maxDataRateText = doc.createTextNode(Integer.toString(maxBytesPerSec));
	    maxDataRateElement.appendChild(maxDataRateText);
	    bsAviFileElement.appendChild(maxDataRateElement);

	    /* TOTAL_FRAMES */
	    Element totalFramesElement = doc.createElementNS(namespace, "TOTAL_FRAMES");
	    Text totalFramesText = doc.createTextNode(Integer.toString(totalFrames));
	    totalFramesElement.appendChild(totalFramesText);
	    bsAviFileElement.appendChild(totalFramesElement);
	    
	    /* NO_OF_STREAMS */
	    Element numOfStreamsElement = doc.createElementNS(namespace, "NO_OF_STREAMS");
	    Text numOfStreamsText = doc.createTextNode(Integer.toString(numOfStreams));
	    numOfStreamsElement.appendChild(numOfStreamsText);
	    bsAviFileElement.appendChild(numOfStreamsElement);
	    
	    /* INTIAL_FRAMES */
	    Element initialFramesElement = doc.createElementNS(namespace, "INTIAL_FRAMES");
	    Text initialFramesText = doc.createTextNode(Integer.toString(initialFrames));
	    initialFramesElement.appendChild(initialFramesText);
	    bsAviFileElement.appendChild(initialFramesElement);
	    
	    /* DURATION */
	    Element durationElement = doc.createElementNS(namespace, "DURATION");
	    Text durationText = doc.createTextNode(getDuration());
	    durationElement.appendChild(durationText);
	    bsAviFileElement.appendChild(durationElement);

	    /* HAS_INDEX */
	    Element hasIndexElement = doc.createElementNS(namespace, "HAS_INDEX");
	    Text hasIndexText = doc.createTextNode(Boolean.toString(hasIndex).toUpperCase());
	    hasIndexElement.appendChild(hasIndexText);
	    bsAviFileElement.appendChild(hasIndexElement);
	    
	    /* MUST_USE_INDEX */
	    Element useIndexElement = doc.createElementNS(namespace, "MUST_USE_INDEX");
	    Text useIndexText = doc.createTextNode(Boolean.toString(useIndex).toUpperCase());
	    useIndexElement.appendChild(useIndexText);
	    bsAviFileElement.appendChild(useIndexElement);
	    
	    /* IS_INTERLEAVED */
	    Element isInterleavedElement = doc.createElementNS(namespace, "IS_INTERLEAVED");
	    Text isInterleavedText = doc.createTextNode(Boolean.toString(isInterleaved).toUpperCase());
	    isInterleavedElement.appendChild(isInterleavedText);
	    bsAviFileElement.appendChild(isInterleavedElement);
	    
	    /* WAS_CAPTURED */
	    Element isCapturedFileElement = doc.createElementNS(namespace, "WAS_CAPTURED");
	    Text isCapturedFileText = doc.createTextNode(Boolean.toString(isCapturedFile).toUpperCase());
	    isCapturedFileElement.appendChild(isCapturedFileText);
	    bsAviFileElement.appendChild(isCapturedFileElement);
	    
	    /* COPYRIGHTED */
	    Element isCopyrightedElement = doc.createElementNS(namespace, "COPYRIGHTED");
	    Text isCopyrightedText = doc.createTextNode(Boolean.toString(isCopyrighted).toUpperCase());
	    isCopyrightedElement.appendChild(isCopyrightedText);
	    bsAviFileElement.appendChild(isCopyrightedElement);
	    
        return doc;
	}

	/**
	 * determine if normalization should be perform.  this method perform additional
	 * checking for normalizing wave file. 
	 * @return boolean primitive
	 */
	protected boolean normalizeSetup(){
		
	    boolean continueNormalization = super.normalizeSetup();
	    
	    if (continueNormalization) {
		    // loop through all audio and video streams in this avie file
	    	// to decide if normalization process should be performed.
	    	boolean bitstreamNormalization = false;
	    	
	        Iterator bsItr = this.getBitstreams().iterator();
	        while (bsItr.hasNext()) {
	        	Bitstream bs = (Bitstream) bsItr.next();
	        	
	        	if (bs instanceof Audio) {
	        		// check if normalization is required for this audio stream
		            Audio bsAudio = (Audio) bs;
		            if (bsAudio.isNormalizationRequired())
		            	bitstreamNormalization = true;
	        	}
	        	if (bs instanceof Video) {
	        		// check if normalization is required for this video stream
	        		Video bsVideo = (Video) bs;
	        		if (bsVideo.isNormalizationRequired())
		            	bitstreamNormalization = true;
	        	}
	        }
	        // if one of the stream should require normalization,
        	// this avi file will be normalized.
	        if (bitstreamNormalization)
	        	continueNormalization = true;
	        else 
	        	continueNormalization = false;
	    }
	    return continueNormalization;
	}
	
	/**
     * normalize the bitstreams in this AVI file and then multiplex them back into an AVI file
	 * @throws PackageException 
     * geException
     * @throws FatalException
     */
    public void normalize() throws PackageException, FatalException {
    	String methodName = "normalize()";
        boolean keepNormalizing = normalizeSetup();
 
        // exit early if we already normalized or this file should not be normalized.
        if (!keepNormalizing){
            return;
        }
         
        String normForm = null; 	// normalization format
        String specProc = null; 	// specific transformation procedure for event reporting
        String genProc = Procedure.TRANSFORM_NORM; // general transformation procedure
        
        // put in separate norm dir
        keepNormalizing = transformStart(genProc, this.getFormatCode(), true); 

        // get the normalization from the configuration files
        if (keepNormalizing){
            String formProp = "NORM_" + this.getFormatCode() + "_FORMAT";
            normForm = ArchiveProperties.getInstance().getArchProperty(formProp);
        }

        // make sure that there are bitstreams to normalize
        if (keepNormalizing && (this.getBitstreams() == null
        		|| this.getBitstreams().size() <= 0)) {
            Informer.getInstance().warning(this, methodName,
				"No bitstreams to normalize", "AVI file: " + this.getPath());                
            // done normalizing; exit
            keepNormalizing = false;
        }
        
        if (keepNormalizing && normForm.equals(TransformationFormat.AVI_NORM_1)) {
            // set the event procedure
            specProc = Procedure.PROC_AVI_NORM_1;
               
            // create a AVI files that contains the normalized audio and video bitstream formats.
            ExternalProgram prog = new ExternalProgram();

            // construct the ideal dir name
            String dirName = this.getOid() + "_" + normForm;

            File normDirPath = new File(this.getIp().getNormalizedDir(),dirName);
            
            // find an unique directory name that is not exist.
            int i = 1;
            while (normDirPath.exists()){
                dirName = this.getOid() + "_" + normForm + "_" + i;
                normDirPath = new File(this.getIp().getNormalizedDir(), dirName);
                i++;
            }
            
            // make directory within normalization directory
            normDirPath.mkdirs();
            File outputFile = new File(normDirPath, "norm.avi");

            String outputPath = outputFile.getAbsolutePath();
            String fileToNorm = this.getPath();
            
            // tell DataFileFactory to treat these files as created by the archive
            DataFileFactory.getInstance().addFutureOriginArchivePath(outputFile.getAbsolutePath());
  
            // construct command to create an AVI files with normalized video stream and audio stream.
            String cmdProperty = ArchiveProperties.getInstance().getArchProperty("AVI_NORM_CMD");
            cmdProperty = cmdProperty.replaceFirst("%INPUT_FILE%", fileToNorm);
            cmdProperty = cmdProperty.replaceFirst("%OUTPUT_FILE%", outputPath);
            cmdProperty = cmdProperty.replaceFirst("%AVI_VIDEO_NORM%", AviVideo.AVI_VIDEO_NORM);
            cmdProperty = cmdProperty.replaceFirst("%AVI_AUDIO_NORM%", AviAudio.AVI_AUDIO_NORM);
            String[] command = cmdProperty.split("\\s");                

            // create the AVI files with normalized bitstreams.
            prog.executeCommand(command);
            
            if (prog.getExitValue() == 0) // success, no error
            {
	            // create DataFile, add events and relationships
	            transformEnd(genProc, outputPath, specProc);
            } else { // encounter error
                Informer.getInstance().error(this, methodName,
                    "Encounter problems during " + cmdProperty,
                    " Error Code: " + prog.getExitValue() + " File:" + this.getPath(),                                
                    new PackageException("Normalization Problem"));
            }
            // clean up file references
            normDirPath = null;
            outputFile = null;
            
        } else if (keepNormalizing) {
            // end of normalization for this file
            Informer.getInstance().info(this, methodName, "Done " + genProc 
            	+ " at " + DateTimeUtil.now(), "File: " + this.getPath(), false);
	    }  
    }
    
    /**
	 * Record those quirks which were introduced by the AVI normalization process.  
	 * This method is called by transformEnd method after the normalization is completed.
     * @throws FatalException 
	 */
	public void recordNormQuirks() throws FatalException {
		DataFile origFile = this.getDerivedFrom();
		// get the data file that this file is normalized from
		if (origFile != null && origFile instanceof AviFile) {
			AviFile origAvi = (AviFile) origFile;
			
		    // evaluate each stream found in this AVI file with the streams in the _aviFile
	        Iterator bsIter1 = this.getBitstreams().iterator();
	        Iterator bsIter2 = origAvi.getBitstreams().iterator();
	        
	        while (bsIter1.hasNext() && bsIter2.hasNext()) {
	        	// loops through each stream in both Avifile objects
	            Bitstream bs1 = (Bitstream) bsIter1.next();
	            Bitstream bs2 = (Bitstream) bsIter2.next();
	            // record any known quirk introduced in the video stream.
	            if (bs1 instanceof Video && bs2 instanceof Video) {
	            	Video vBs1 = (Video) bs1;
	            	Video vBs2 = (Video) bs2;
	            	// data quality attribute in the video stream was reset after normalization
	            	if (vBs1.getDataQuality() != vBs2.getDataQuality())
	            		this.addQuirk(AviQuirks.getSevereElement(AviQuirks.NORM_DATA_QUALITY_RESET));
	            }
	            // record any known quirk introduced in the audio stream.
	            if (bs1 instanceof Audio && bs2 instanceof Audio) {
	            	Audio aBs1 = (Audio) bs1;
	            	Audio aBs2 = (Audio) bs2;
	            	// data quality attribute in the audio stream was reset after normalization
	            	if (aBs1.getDataQuality() != aBs2.getDataQuality())
	            		this.addQuirk(AviQuirks.getSevereElement(AviQuirks.NORM_DATA_QUALITY_RESET));
	            }
	        }	
		}
	}
}
