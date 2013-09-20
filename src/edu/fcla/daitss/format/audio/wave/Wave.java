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

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.bitstream.Bitstream;
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
import edu.fcla.daitss.format.audio.Audio;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.ExternalProgram;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.Procedure;

/**
 * Wave represents a Microsoft Audio for Windows (WAVE) file format.
 * 
 * @author carol
 */
public class Wave  extends DataFile {
	// Fully-qualified name for this class. To be used for Informer calls from static methods.
    private static String CLASSNAME = "edu.fcla.daitss.format.audio.wave.Wave";

    // Version information of the wave file.  Not applied.
    /**
     * Class constant for version information
     */
    public static final String VERSION_NA = "NA";
    
    // Whether this wave file contains a fact chunk.
    private boolean hasFactChunk = false;
    
    // Whether this wave file contains a play-list chunk.
    private boolean hasPlayListChunk = false;
    
    // Whether this wave file contains lable chunks
    private boolean hasLabelChunk = false;
    
    // Whether this wave file contains Note chunks.
    private boolean hasNoteChunk = false;
    
    // Whether this wave file contains a "ltxt" (Text associated with a data segment) chunks.
    private boolean hasLtxtChunk = false;
    
    // Whether this wave file contains a "file" (Embeded file) chunks.
    private boolean hasFileChunk = false;
    
    // Whether this wave file contains a "smpl" (Sample) chunks.
    private boolean hasSampleChunk = false;
    
    // Whether this wave file contains an "inst" (Instrument) chunks.
    private boolean hasInstrumentChunk = false;
    
    // Whether this wave file contains a cue-points chunk.
    private boolean hasCueChunk = false;
    
    // the list of cue points in this wave file
	Vector<String> cuePoints = new Vector<String>();
	
	// copywrite statement
	String copyright = null;
	
	/**
     * Determines whether or not the file is a WAVE file when metadata about this
     * file is not available.
     * 
     * @param filePath - absolute path to an existing readable file
     * @return whether or not its a WAVE file
     * @throws FatalException
     */
    public static boolean isType(String filePath) throws FatalException {
        return isType(filePath, null);
    }
    
    /**
     * Determines if this file is a WAVE file when metadata about this file is
     * available.
     * 
     * @return whether or not its a WAVE file
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
        
        // the size of WAVE RIFF chunk
        long chunkSize = breader.readBytes(4, DataFile.BYTE_ORDER_LE);
		
        // read in the WAVE fourcc (the next four bytes)
        breader.read(data); 
        String fourCC = new String(data);
        if (!fourCC.equals("WAVE"))
        	isType = false;
       
        breader.close();
        breader = null;
        theFile = null;
		
		return isType;
	}
	
	/**
	 * Main method for testing purposes
	 * 
	 * @param args 
	 * @throws PackageException 
	 * @throws FatalException 
	 * @see edu.fcla.daitss.file.DataFile#main(java.lang.String[])
	 */
	public static void main(String[] args) throws PackageException, FatalException
	{
		String testFile = "/work/audio/obj1.wav";
		if (Wave.isType(testFile, null)) {
			System.out.println(testFile + " is a WAVE file");
			Wave wave = new Wave(testFile, new SIP("/daitss/dev/AA"));
			wave.setOid("F20050926_AAAAAA");
			wave.extractMetadata();
			System.out.println(wave);
		} else {
			System.out.println("Is not a WAVE file");
		}
	}

	/**
     * The constructor to call for an existing WAVE file when metadata about this
     * file is not available.
     * 
     * @param path 	- the absolute path to an existing readable file
     * @param ip	- the Information Package that this file is part of
     * @throws FatalException
     */
    public Wave(String path, InformationPackage ip) throws FatalException {
        super(path, ip);

        this.setByteOrder(DataFile.BYTE_ORDER_NA);
        this.setMediaType(MimeMediaType.MIME_AUDIO_WAVE);
		this.setMediaTypeVersion(VERSION_NA);
        this.setFileExt(Extension.EXT_WAVE);

        // build the list of potential anomalies
        this.anomsPossible = null;
        this.anomsPossible = new WaveAnomalies();
       
        // list of potential limitations
        limitationsPossible = new WaveLimitations();
    }

    /**
	 * The constructor to call for an existing WAVE file when metadata about
	 * this file is available.
	 * 
	 * @param path	- the absolute path to an existing readable file
	 * @param ip 	- the Information Package that this file is part of
	 * @param _metadata - a METSDocument containing metadata about this DataFile
	 * @throws FatalException
	 */
	public Wave(String path, InformationPackage ip, METSDocument _metadata)
		throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}
	
	/**
	 * Read the file and retrieve the attributes of the Wave files
	 * 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		byte[] data = new byte[4];
		long chunkSize = -1;

		// skip the first WAVE_RIFF_HEADER_SIZE (12) bytes, we already check it in isType()
		reader.skipBytes(4);	// skip RIFF heade 
		
	     // the size of WAVE RIFF chunk
        long length = reader.readBytes(4, DataFile.BYTE_ORDER_LE);
		reader.skipBytes(4);	// skip WAVE heade r
	
		// skip all the chunks until the reader reaches the wave format chunk.
		reader.read(data);	// chunk id
		String fourCC = new String(data);
		chunkSize = reader.readBytes(4, DataFile.BYTE_ORDER_LE);
		
		while (reader.bytesLeft() > 8) {
			if (fourCC.equals("fmt "))
				// parse the format chunk
				parseFormat(chunkSize);
			else if (fourCC.equals("data"))
				// parse the data chunk
				parseData(chunkSize);
			else if (fourCC.equals("cue "))
				//parse the cue list chunk
				parseCue(chunkSize);
			else if (fourCC.equals("plst"))
				//parse the play list chunk
				parsePlaylist(chunkSize);
			else if (fourCC.equals("fact")) {		
				hasFactChunk = true;
				// skip the rest of the fact chunk
				reader.skipBytes((int) chunkSize);
			} else if (fourCC.equals("LIST")) {
				parseList(chunkSize);
			} else if (fourCC.equals("smpl")) {
				hasSampleChunk = true;
				// skip the rest of the sample chunk
				reader.skipBytes((int) chunkSize);
			} else if (fourCC.equals("inst")) {
				hasInstrumentChunk = true;
				// skip the rest of the instrument chunk
				reader.skipBytes((int) chunkSize);
			}
			else {
				// skip this chunk if we don't have to process it.
				reader.skipBytes((int) chunkSize);
			}
			
			// skip the extra padding if this chunk is not byte aligned.
			if ((chunkSize % 2)  != 0) {
				reader.skipBytes(1);
			}
			
			reader.read(data);	// next chunk id
			fourCC = new String(data);
			chunkSize = reader.readBytes(4, DataFile.BYTE_ORDER_LE);
		}
		
		// perform additional checking at the end of parsing
		if (this.getBitstreams().size() > 0) {			
			if (this.getBitstreams().firstElement() instanceof WaveAudio) {
				WaveAudio audioStream = (WaveAudio) this.getBitstreams().firstElement();
				// a fact chunk is required if the wave data is in compressed format
				if (audioStream.isCompressed() && !hasFactChunk)  {
					SevereElement ja = this.getAnomsPossible().
					getSevereElement(WaveAnomalies.WAVE_NO_FACT_FOR_COMPRESSED_DATA);
				this.addAnomaly(ja);
				}
			}
		}
	}

	/**
	 * parse and retrieve the value from Wave Format Chunk
	 * @param chunkSize 
	 * 
	 * @throws FatalException
	 */
	private void parseFormat(long chunkSize) throws FatalException{
		long bytesRead = 0;
		
		Long streamOffset = new Long((long) reader.getFilePointer());
		if (chunkSize < WaveAudio.WAVEFORMAT_SIZE) {
			SevereElement ja = this.getAnomsPossible().
				getSevereElement(WaveAnomalies.WAVE_FMT_CHUNK_TOO_SMALL);
			this.addAnomaly(ja);
		}
		else {
			// Make sure there is only one audio stream, if more than one audio stream format chunk
			// contained in wave files, ignore the rest of them.  
			int numStream = this.getBitstreams().size();
			if (numStream <= 0) {
				// create an audio stream to hold the information
		    	WaveAudio audioStream = new WaveAudio(this);
		    	bytesRead = audioStream.readFrom(reader, chunkSize);
			   	// add the audio stream to the bitstream collection of this data file
		    	this.addBitstream(audioStream);
		    	audioStream.setLocation(streamOffset.toString());
		    	audioStream.setLocationType(Bitstream.LOC_FILE_BYTE_OFFSET);
			}
		}
		
		// skip the remaining bytes that is not read.
		int remainingBytes = (int) (chunkSize  - bytesRead);
		if (remainingBytes > 0)
			reader.skipBytes(remainingBytes);
	}
	
	/**
	 * parse and retrieve the value from Wave Data Chunk
	 * @param chunkSize 
	 * 
	 * @throws FatalException
	 */
	private void parseData(long chunkSize) throws FatalException {
		// retrieve the audio stream.  Since the data chunk should appear after the format chunk
		// there should have an audio stream created for it already.
		int numStream = this.getBitstreams().size();
		if (numStream > 0) {			
			if (this.getBitstreams().firstElement() instanceof WaveAudio) {
				WaveAudio audioStream = (WaveAudio) this.getBitstreams().firstElement();
				audioStream.setLength(chunkSize);
			}
		} else {
			SevereElement ja = this.getAnomsPossible().
				getSevereElement(WaveAnomalies.WAVE_NO_FMT_BEFORE_DATA);
			this.addAnomaly(ja);
		}
		
		// skip the rest of the data chunk.
		reader.skipBytes((int) chunkSize);
	}
	
	/**
	 * parse, retrieve and validate the value in the optional Wave Cue list Chunk
	 * @param chunkSize 
	 * 
	 * @throws FatalException
	 */
	private void parseCue(long chunkSize) throws FatalException {
		long numCuePoints =  reader.readBytes(4, DataFile.BYTE_ORDER_LE); // number of cue points;
		long bytesRead = 4; // note: already read in the numCuePoints above
		byte[] dwName = new byte[4];

		if (hasCueChunk) {
			//we already seen a cue-points chunk.  only one cue-points chunk is allowed in a wave file.
			//ignore this chunk and record an anomaly.
			SevereElement ja = this.getAnomsPossible().
			getSevereElement(WaveAnomalies.WAVE_MULTIPLE_CUE_POINTS_CHUNK);
			this.addAnomaly(ja);
		}
		else {
			// go through each cue point and verify its name for uniqueness
			for (int i =0; i < numCuePoints; i++) {
				// read in the dwName filed
				reader.read(dwName);
				String name = new String(dwName);
				if (cuePoints.contains(name)) {
					SevereElement ja = this.getAnomsPossible().
						getSevereElement(WaveAnomalies.WAVE_NON_UNIQUE_CUE_POINT_ID);
					this.addAnomaly(ja);
				}
				else
					cuePoints.add(name);
				
				// skip the rest of cue point fields, i.e., Position, Data Chunk ID, Chunk Start
				// Block Start and Sample Offset, as we don't validate them.
				reader.skipBytes(20);
				bytesRead += 24;
			}
			
			hasCueChunk = true;
		}
		
		// skip the rest of the unread chunk data.
		int remainingBytes = (int) (chunkSize - bytesRead);
		if (remainingBytes > 0)
			reader.skipBytes(remainingBytes);
	}
	
	/**
	 * parse, retrieve and validate the value in the optional Wave Cue list Chunk
	 * @param chunkSize 
	 * 
	 * @throws FatalException
	 */
	private void parsePlaylist(long chunkSize) throws FatalException {
		long numSegments =  reader.readBytes(4, DataFile.BYTE_ORDER_LE); // number of segments;
		long bytesRead = 4; // note: already read in the numSegments above
		byte[] dwName = new byte[4];

		hasPlayListChunk = true;
		// go through each segment and make sure the cue-point name in the segment match
		// a name defined in the cue-point chunk
		for (int i =0; i < numSegments; i++) {
			// read in the dwName filed
			reader.read(dwName);
			String name = new String(dwName);
			//record an anomaly is it is not matched.
			if (!cuePoints.contains(name)) {
				SevereElement ja = this.getAnomsPossible().
					getSevereElement(WaveAnomalies.WAVE_UNDEFINED_CUE_POINT_IN_PLAYLIST);
				this.addAnomaly(ja);
			}
			// skip the rest of the segment fields, i.e., dwLength and dwLoops, as we don't validate them.
			reader.skipBytes(8);
			bytesRead += 12;
		}
		
		// skip the rest of the unread chunk data.
		int remainingBytes = (int) (chunkSize - bytesRead);
		if (remainingBytes > 0)
			reader.skipBytes(remainingBytes);
	}
	
	/**
	 * parse the LIST chunk
	 * @param chunkSize
	 * @throws FatalException
	 */
	private void parseList(long chunkSize) throws FatalException {
		long bytesRead = 4; // note: always read in the typeName above
		byte[] data = new byte[4];

		reader.read(data);
		String typeName = new String(data);
		int remainingBytes = (int) (chunkSize - bytesRead);
		
		if (typeName.equals("adtl")) {
			parseAssociatedData(remainingBytes);
		} else if (typeName.equals("INFO")) {
			parseInfo(remainingBytes);
		} else {
			SevereElement ja = this.getAnomsPossible().
			getSevereElement(WaveAnomalies.WAVE_BAD_ASSOCIATED_DATA_CHUNK);
			this.addAnomaly(ja);
			// skip the rest of the unread chunk data.
			if (remainingBytes > 0)
				reader.skipBytes(remainingBytes);
			return;
		}
		
	}
	/**
	 * parse, retrieve and validate the associated data chunk.  The associate data list can contains
	 * "labl"(LABEL), "note"(NOTE), "ltxt" (TEXT), "file"(FILE) subchunks, as defined in the 
	 * Waveform Audio File Format of the Multimedia Programming Interface and Data Spec. 1.0, August 1991 
	 * @param chunkSize 
	 * 
	 * @throws FatalException
	 */
	private void parseAssociatedData(long chunkSize) throws FatalException {
		long bytesRead = 0;
		byte[] data = new byte[4];
		
		while (bytesRead < chunkSize) {
			//read in the sub chunk name
			reader.read(data);
			String subChunkName = new String(data);
			long subChunkSize = reader.readBytes(4, DataFile.BYTE_ORDER_LE);
			bytesRead += 8;
			if (subChunkName.equals("labl") || subChunkName.equals("note") || 
					subChunkName.equals("ltxt") || subChunkName.equals("file")) {	
				// read in the dwName field
				reader.read(data);
				String name = new String(data);
				// make sure the cue-point name in the subchunk match a name defined in the cue-point chunk
				if (!cuePoints.contains(name)) {
					SevereElement ja = this.getAnomsPossible().
						getSevereElement(WaveAnomalies.WAVE_UNDEFINED_CUE_POINT_IN_ASSOCIATED_DATA_LIST);
					this.addAnomaly(ja);
				}
				
				// set the flag for the kind of chunk we seen.
				if (subChunkName.equals("labl"))
					hasLabelChunk = true;
				else if (subChunkName.equals("note"))
					hasNoteChunk = true;
				else if (subChunkName.equals("ltxt"))
					hasLtxtChunk = true;
				else if (subChunkName.equals("file"))
					hasFileChunk = true;
				
				// ignore the rest of subChunk, mainly the text field
				if (subChunkSize > 0) {
					// the name field (4 bytes) has already been read in this subchunk
					reader.skipBytes((int) (subChunkSize - 4));
					bytesRead += subChunkSize;
				}
			} else {
				// unrecognizable subchunk, record an anomaly
				SevereElement ja = this.getAnomsPossible().
					getSevereElement(WaveAnomalies.WAVE_BAD_SUBCHUNK_IN_ASSOCIATED_DATA_LIST);
				this.addAnomaly(ja);
				// ignore the rest of subChunk, 
				if (subChunkSize > 0) {
					reader.skipBytes((int) subChunkSize);
					bytesRead += subChunkSize;
				}
			}
				
			// skip the extra padding if this sub chunk is not byte aligned.
			if ((subChunkSize % 2)  != 0) {
				reader.skipBytes(1);
				bytesRead++;
			}
		}
	}
	/**
	 * parse and retrieve the metadata in the INFO chunk.
	 * @param chunkSize
	 * @throws FatalException
	 */
	private void parseInfo(long chunkSize) throws FatalException {
		long bytesRead = 0;
		byte[] data = new byte[4];
		
		while (bytesRead < chunkSize && (chunkSize - bytesRead) >= 8) {
			//read in the sub chunk name
			reader.read(data);
			String subChunkName = new String(data);
			long subChunkSize = reader.readBytes(4, DataFile.BYTE_ORDER_LE);
			bytesRead += 8;
			byte[] content = new byte[(int) subChunkSize];
			reader.read(content);
			bytesRead += subChunkSize;
			String contentStr = new String(content);
			
			if (subChunkName.equals("ICOP")) {	
				// copyright statement
				copyright = contentStr;
			} else if (subChunkName.equals("ICRD")) { 
				// creation date is stored in YYYY-MM-DD format in wave
				String dateStr = DateTimeUtil.Wave2Arch(contentStr);
				super.setCreateDate(dateStr);
			} else if (subChunkName.equals("ISFT")) {	
				// software used to create the wave file 
				super.setCreatorProg(contentStr);
			} // ignore the other subchunk
				
			// skip the extra padding if this sub chunk is not byte aligned.
			if ((subChunkSize % 2)  != 0) {
				reader.skipBytes(1);
				bytesRead++;
			}
		}
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

        // evaluate the members for each stream found in this WAVE file
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
	
		columns.add(ArchiveDatabase.COL_WAVE_FILE_DFID);
		values.add(this.getOid());
		columns.add(ArchiveDatabase.COL_WAVE_FILE_HAS_FACT_CHUNK);
		values.add(hasFactChunk);
		columns.add(ArchiveDatabase.COL_WAVE_FILE_HAS_PLAYLIST_CHUNK);
		values.add(hasPlayListChunk);
		columns.add(ArchiveDatabase.COL_WAVE_FILE_HAS_LABEL_CHUNK);
		values.add(hasLabelChunk);
		columns.add(ArchiveDatabase.COL_WAVE_FILE_HAS_NOTE_CHUNK);
		values.add( hasNoteChunk);
		columns.add(ArchiveDatabase.COL_WAVE_FILE_HAS_LTXT_CHUNK);
		values.add( hasLtxtChunk);
		columns.add(ArchiveDatabase.COL_WAVE_FILE_HAS_FILE_CHUNK);
		values.add(hasFileChunk);
		columns.add(ArchiveDatabase.COL_WAVE_FILE_HAS_SAMPLE_CHUNK);
		values.add(hasSampleChunk);
		columns.add(ArchiveDatabase.COL_WAVE_FILE_HAS_INSTRUMENT_CHUNK);
		values.add(hasInstrumentChunk);
		columns.add(ArchiveDatabase.COL_WAVE_FILE_HAS_CUE_CHUNK);
		values.add(hasCueChunk);
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
		
		return tcon.insert(ArchiveDatabase.TABLE_WAVE_FILE, columns, colValues);
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
				ArchiveDatabase.COL_WAVE_FILE_DFID, SqlQuote.escapeString(this.getOid()));
		return tcon.update(ArchiveDatabase.TABLE_WAVE_FILE, columns, colValues, whereClause);
	}
	
	 /**
     * Puts all the WAVE file's members and their values in a String.
     * 
     * @return the members of this class, including bitStreams, as a String
     */
    public String toString() {
        StringBuffer strBuffer = new StringBuffer("");
        
        strBuffer.append(super.toString() + "\n");
        
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
	    
        // Bs WAVE File
	    Element bsWaveFileElement = doc.createElementNS(namespace, "WAVE_FILE");
	    rootElement.appendChild(bsWaveFileElement);

	    // Dfid 
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getOid() != null ? this.getOid() : "" );
	    Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsWaveFileElement.appendChild(dfidElement);

	    // HAS_FACT_CHUNK
	    Element hasFactChunkElement = doc.createElementNS(namespace, "HAS_FACT_CHUNK");
	    Text hasFactChunkText = doc.createTextNode(Boolean.toString(hasFactChunk).toUpperCase());
	    hasFactChunkElement.appendChild(hasFactChunkText);
	    bsWaveFileElement.appendChild(hasFactChunkElement);
	    
	    // HAS_PLAYLIST_CHUNK
	    Element hasPlaylistChunkElement = doc.createElementNS(namespace, "HAS_PLAYLIST_CHUNK");
	    Text hasPlaylistChunkText = doc.createTextNode(Boolean.toString(hasPlayListChunk).toUpperCase());
	    hasPlaylistChunkElement.appendChild(hasPlaylistChunkText);
	    bsWaveFileElement.appendChild(hasPlaylistChunkElement);
  
	    // HAS_LABEL_CHUNK
	    Element hasLabelChunkElement = doc.createElementNS(namespace, "HAS_LABEL_CHUNK");
	    Text hasLabelChunkText = doc.createTextNode(Boolean.toString(hasLabelChunk).toUpperCase());
	    hasLabelChunkElement.appendChild(hasLabelChunkText);
	    bsWaveFileElement.appendChild(hasLabelChunkElement);
	    
	    // HAS_NOTE_CHUNK
	    Element hasNoteChunkElement = doc.createElementNS(namespace, "HAS_NOTE_CHUNK");
	    Text hasNoteChunkText = doc.createTextNode(Boolean.toString(hasNoteChunk).toUpperCase());
	    hasNoteChunkElement.appendChild(hasNoteChunkText);
	    bsWaveFileElement.appendChild(hasNoteChunkElement);
	    
	    // HAS_LTXT_CHUNK
	    Element hasLtxtChunkElement = doc.createElementNS(namespace, "HAS_LTXT_CHUNK");
	    Text hasLtxtChunkText = doc.createTextNode(Boolean.toString(hasLtxtChunk).toUpperCase());
	    hasLtxtChunkElement.appendChild(hasLtxtChunkText);
	    bsWaveFileElement.appendChild(hasLtxtChunkElement);
	    
	    // HAS_FILE_CHUNK
	    Element hasFileChunkElement = doc.createElementNS(namespace, "HAS_FILE_CHUNK");
	    Text hasFileChunkText = doc.createTextNode(Boolean.toString(hasFileChunk).toUpperCase());
	    hasFileChunkElement.appendChild(hasFileChunkText);
	    bsWaveFileElement.appendChild(hasFileChunkElement);
	    
	    // HAS_SAMPLE_CHUNK
	    Element hasSampleChunkElement = doc.createElementNS(namespace, "HAS_SAMPLE_CHUNK");
	    Text hasSampleChunkText = doc.createTextNode(Boolean.toString(hasSampleChunk).toUpperCase());
	    hasSampleChunkElement.appendChild(hasSampleChunkText);
	    bsWaveFileElement.appendChild(hasSampleChunkElement);
	    
	    // HAS_INSTRUMENT_CHUNK
	    Element hasInstrumentChunkElement = doc.createElementNS(namespace, "HAS_INSTRUMENT_CHUNK");
	    Text hasInstrumentChunkText = doc.createTextNode(Boolean.toString(hasInstrumentChunk).toUpperCase());
	    hasInstrumentChunkElement.appendChild(hasInstrumentChunkText);
	    bsWaveFileElement.appendChild(hasInstrumentChunkElement);
	    
	    // HAS_CUE_CHUNK
	    Element hasCueChunkElement = doc.createElementNS(namespace, "HAS_CUE_CHUNK");
	    Text hasCueChunkText = doc.createTextNode(Boolean.toString(hasCueChunk).toUpperCase());
	    hasCueChunkElement.appendChild(hasCueChunkText);
	    bsWaveFileElement.appendChild(hasCueChunkElement);
        return doc;
	}

	/**
	 * determine if normalization should be perform.  this method perform additional
	 * checking for normalizing wave file. 
	 * @return boolean 
	 */
	protected boolean normalizeSetup(){
		
	    boolean continueNormalization = super.normalizeSetup();
	    
	    if (continueNormalization) {
		    // evaluate the audio stream in this WAVE file, there should be only one audio stream any way
	        Bitstream bs = (Bitstream) this.getBitstreams().firstElement();
	        if ( (bs != null) && (bs instanceof Audio))  {
	            Audio bsAudio = (Audio) bs;
	            continueNormalization = bsAudio.isNormalizationRequired();
	        }
	        else
	        	continueNormalization = false;
	    }
	    return continueNormalization;
	}
	
	/**
     * normalize the audio bitstream in this WAVE file
     * @throws PackageException
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

        // get the normalization form from the configuration files
        if (keepNormalizing){
            String formProp = "NORM_" + this.getFormatCode() + "_FORMAT";
            normForm = ArchiveProperties.getInstance().getArchProperty(formProp);
        }

        // make sure that there are bitstreams to normalize
        if (keepNormalizing && (this.getBitstreams() == null
        		|| this.getBitstreams().size() <= 0)) {
            Informer.getInstance().warning(this, methodName,
				"No bitstreams to normalize", "WAVE file: " + this.getPath());                
            // done normalizing; exit
            keepNormalizing = false;
        }
        
        if (keepNormalizing && normForm.equals(TransformationFormat.WAVE_NORM_1)) {
            // set the event procedure
            specProc = Procedure.PROC_WAVE_NORM_1;
               
            // create a wave file that contains the normalized audio bitstream formats.
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
            File outputFile = new File(normDirPath, "norm.wav");

            String outputPath = outputFile.getAbsolutePath();
            String fileToNorm = this.getPath();
            
            // tell DataFileFactory to treat these files as created by the archive
            DataFileFactory.getInstance().addFutureOriginArchivePath(outputFile.getAbsolutePath());
  
            // construct command to create a wave file with a normalized audio stream.
            String cmdProperty = ArchiveProperties.getInstance().getArchProperty("WAVE_NORM_CMD");
            cmdProperty = cmdProperty.replaceFirst("%INPUT_FILE%", fileToNorm);
            cmdProperty = cmdProperty.replaceFirst("%OUTPUT_FILE%", outputPath);
            cmdProperty = cmdProperty.replaceFirst("%WAVE_AUDIO_NORM%", WaveAudio.WAVE_AUDIO_NORM);
            String[] command = cmdProperty.split("\\s");                

            // create the wave file with a normalized bitstream.
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
}
