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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.bitstream.Link;
import edu.fcla.daitss.file.distributed.Distributed;
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
import edu.fcla.daitss.format.video.Video;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.ExternalProgram;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.Procedure;

/**
 * represents QuickTime bitstream
 *
 */
public class QuickTime extends DataFile  implements Distributed {
	// fully-qualitifed class name
    private static String CLASSNAME = "edu.fcla.daitss.format.multimedia.quicktime"; 
    /**
     * class constant
     */
    public static final String VERSION_NA = "NA";	// QuickTime version, not applied
    
    private String	modifiedDate;				// when the movie was last modified
    private double  duration;					// duration of the movie, in seconds
    private float 	preferredRate;				// preffered playback rate
    private  float 	preferredVolume;			// preffered playback volume
    private long	timeScale;					// movie time scale,i.e., the number of time units per second.
    private long    previewStart;				// the time unit when the preview start
    private long    previewDuration;			// the duration of the preview, in movie time unit
    
    private boolean hasDisplayMatrix = false; 	// whether the movie contains a display matrix.
    private boolean hasUserData = false;		// whether the movie contains user data
    private boolean hasColorTable = false;		// whether the movie contains color table
    private boolean hasClippingRegion = false;	// whether the movie contains clipping regions
    private boolean hasCompressedMovie = false;	// whether the movie contains compressed movie header
    private boolean hasReferences = false; 		// whether the movie contains references to alternative movie
    
    private QuickTimeLimitations quicktimeLimitations = null;
    // normalized audio encoding format
    private String normalizedAudio = QuickTimeAudio.QUICKTIME_AUDIO_NORM;				
    // normalized video encoding format
    private String normalizedVideo = QuickTimeVideo.QUICKTIME_VIDEO_NORM;
    /**
     * Determines whether or not the file is a quicktime movie file when metadata about this
     * file is not available.
     * 
     * @param filePath - absolute path to an existing readable file
     * @return whether or not its a quicktime movie file
     * @throws FatalException
     */
    public static boolean isType(String filePath) throws FatalException {
        return isType(filePath, null);
    }
    
    /**
     * Determines if this file is a quicktime movie file file when metadata about this file is
     * available.
     * 
     * @return whether or not its a quicktime movie file
     * @param _filePath 	- absolute path to an existing readable file
     * @param _metadata - metadata about this DataFile
     * @throws FatalException
     */
	public static boolean isType(String _filePath, METSDocument _metadata) 
	throws FatalException {
		boolean isType = false;
	    String methodName = "isType(String, METSDocument)";
	    
		// check that filePath is != null and points to an existing file
        if (!FileUtil.isGoodFile(_filePath)) {
            Informer.getInstance().fail(CLASSNAME, methodName, "Illegal argument", 
            	"filePath: " + _filePath, new FatalException("invalid absolute path to a file"));
        }

        File theFile = new File(_filePath);
        ByteReader breader = new ByteReader(theFile, "r");
        
        QuickTimeParser parser = new QuickTimeParser();
        isType = parser.isType(breader);
 
        breader.close();
        breader = null;
        theFile = null;
		
		return isType;
	}
	
	/**
	 * testing routine
	 * @param args 
	 * @throws PackageException 
	 * @throws FatalException 
	 */
	public static void main(String[] args) throws PackageException, FatalException {
		String testFile = "/work/testdata/video/mov/rama.mov";
		if (QuickTime.isType(testFile, null)) {
			System.out.println(testFile + " is a QuickTime file");
			QuickTime file = new QuickTime(testFile, new SIP("/daitss/dev/AA00000000"));
			file.setOid("F20090101_AAAAAA");
			file.extractMetadata();
			System.out.println(file);
		} else {
			System.out.println("Is not a QuickTime");
		}
	}
	/**
     * The constructor to call for an existing QuickTime file when metadata about this
     * file is not available.
     * 
     * @param path 	- the absolute path to an existing readable file
     * @param ip	- the Information Package that this file is part of
     * @throws FatalException
     */
    public QuickTime(String path, InformationPackage ip) throws FatalException {
        super(path, ip);

        this.setByteOrder(DataFile.BYTE_ORDER_NA);
        this.setMediaType(MimeMediaType.MIME_VIDEO_QUICKTIME);
		this.setMediaTypeVersion(VERSION_NA);
        this.setFileExt(Extension.EXT_QUICKTIME);

        // build lists of potential anomalies
        this.anomsPossible = new QuickTimeAnomalies();
        
        // build the list of possible limitations
        limitationsPossible = new QuickTimeLimitations();
    }
    
	/**
	 * The constructor to call for an existing QuickTime file when metadata about
	 * this file is available.
	 * 
	 * @param path	- the absolute path to an existing readable file
	 * @param ip 	- the Information Package that this file is part of
	 * @param _metadata - a METSDocument containing metadata about this DataFile
	 * @throws FatalException
	 */
	public QuickTime(String path, InformationPackage ip, METSDocument _metadata)
		throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}
	
	/**
	 * Adds a Video or Audio bitstream to the list of bitstreams found
	 * in quicktime. 
	 * @param stream	represents an audio or video stream 
	 * @throws FatalException
	 */
	public void addBitstream(Bitstream stream) throws FatalException {
		// only video and audio bitstream are recognized for QuickTime files
		if ((stream instanceof QuickTimeVideo) || (stream instanceof QuickTimeAudio))
			super.addBitstream(stream);
	}
	
	/**
	 * set the name of the quicktime movie writer
	 * @param writerName - the name of the movie writer
	 * @throws FatalException
	 */
	public void setWriter(String writerName) throws FatalException {
		
		super.setCreatorProg(writerName);
	}
	
	/**
	 * Read the file and retrieve the attributes of the QuickTime files
	 * 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		QuickTimeParser parser = new QuickTimeParser();
	    parser.parse(reader, this);
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

        // evaluate the members for each stream found in this QuickTime file
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
	 * fill in the database column-value pairs for the mov file
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();
		
		columns.add(ArchiveDatabase.COL_QUICKTIME_FILE_DFID);
		values.add(this.getOid());
		columns.add(ArchiveDatabase.COL_QUICKTIME_DURATION);
		values.add(getDuration());
		columns.add(ArchiveDatabase.COL_QUICKTIME_HAS_DISPLAY_MATRIX);
		values.add(hasDisplayMatrix);
		columns.add(ArchiveDatabase.COL_QUICKTIME_HAS_USERDATA);
		values.add(hasUserData);
		columns.add(ArchiveDatabase.COL_QUICKTIME_HAS_COLORTABLE);
		values.add( hasColorTable);
		columns.add(ArchiveDatabase.COL_QUICKTIME_HAS_COMPRESSED_RESOURCE);
		values.add( hasCompressedMovie);
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
		
		return tcon.insert(ArchiveDatabase.TABLE_QUICKTIME_FILE, columns, colValues);
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
				ArchiveDatabase.COL_QUICKTIME_FILE_DFID, SqlQuote.escapeString(this.getOid()));
		return tcon.update(ArchiveDatabase.TABLE_QUICKTIME_FILE, columns, colValues, whereClause);
	}
	

    /**
     * write the metadata in this quicktime file to the XML file for QuickTime 
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
	    
        // Bs QuickTimeFile 
	    Element bsQTFileElement = doc.createElementNS(namespace, "QUICKTIME_FILE");
	    rootElement.appendChild(bsQTFileElement);

	    // Dfid 
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getOid() != null ? this.getOid() : "" );
	    dfidElement.appendChild(doc.createTextNode(dfidValue));
	    bsQTFileElement.appendChild(dfidElement);

	    /* DURATION */
	    Element durationElement = doc.createElementNS(namespace, "DURATION");
	    durationElement.appendChild(doc.createTextNode(getDuration()));
	    bsQTFileElement.appendChild(durationElement);
	    
	    // HAS_DISPLAY_MATRIX
	    Element hasDPElement = doc.createElementNS(namespace, "HAS_DISPLAY_MATRIX");
	    hasDPElement.appendChild(doc.createTextNode(Boolean.toString(hasDisplayMatrix).toUpperCase()));
	    bsQTFileElement.appendChild(hasDPElement);
	    
	    // HAS_USER_DATA
	    Element hasUserDataElement = doc.createElementNS(namespace, "HAS_USER_DATA");
	    hasUserDataElement.appendChild(doc.createTextNode(Boolean.toString(hasUserData).toUpperCase()));
	    bsQTFileElement.appendChild(hasUserDataElement);
	    
	    // HAS_COLORTABLE
	    Element hasColortableElement = doc.createElementNS(namespace, "HAS_COLORTABLE");
	    hasColortableElement.appendChild(doc.createTextNode(Boolean.toString(hasColorTable).toUpperCase()));
	    bsQTFileElement.appendChild(hasColortableElement);
	    
	    // HAS_COMPRESSED_RESOURCE
	    Element hasCompressedResourceElement = doc.createElementNS(namespace, "HAS_COMPRESSED_RESOURCE");
	    hasCompressedResourceElement.appendChild(doc.createTextNode(Boolean.toString(hasCompressedMovie).toUpperCase()));
	    bsQTFileElement.appendChild(hasCompressedResourceElement);
	    
	    
        return doc;
	}

	/**
	 * determine if normalization should be perform.  This method performs additional
	 * checking for a normalizing quicktime file. 
	 * @return whether to continue to normalize or not.
	 */
	protected boolean normalizeSetup(){
		
	    boolean continueNormalization = super.normalizeSetup();
	    
	    if (continueNormalization) {
		    // loop through all audio and video streams in this quicktime file
	    	// to decide if normalization process should be performed.
	    	boolean bitstreamNormalization = false;
	    	
	        Iterator bsItr = this.getBitstreams().iterator();
	        while (bsItr.hasNext()) {
	        	Bitstream bs = (Bitstream) bsItr.next();
	        	
	        	if (bs instanceof Audio) {
	        		// check if normalization is required for this audio stream
		            QuickTimeAudio bsAudio = (QuickTimeAudio) bs;
		            if (bsAudio.isNormalizationRequired()) {
			            normalizedAudio = bsAudio.getNormalizedAudio();
		            	bitstreamNormalization = true;
		            }
	        	}
	        	
	        	if (bs instanceof Video) {
	        		// check if normalization is required for this video stream
	        		QuickTimeVideo bsVideo = (QuickTimeVideo) bs;
	        		if (bsVideo.isNormalizationRequired()) {
		            	bitstreamNormalization = true;
			            normalizedVideo = bsVideo.getNormalizedVideo();
	        		}
	        	}
	        }
	        // if one of the stream should require normalization,
        	// this quicktime file will be normalized.
	        if (bitstreamNormalization)
	        	continueNormalization = true;
	        else 
	        	continueNormalization = false;
	    }
	    return continueNormalization;
	}
	
	/**
	 * Perform localization if the Quciktime file contains links to external file 
	 * reference and at least of of the link was successfully resolved.  
	 * Phase 1 or 2 is performed, depending on the <code>locPhase</code>
	 * of the DataFile.
	 * 
	 * Phase 1 of localizing a set of files:
	 * replace the links in the localized copy of this file with relative paths 
	 * to other files in this package. These othe files may not be localized yet and
	 * thus may contain non-relative paths to remote files outside the archive.
	 *
	 * Phase 2 of localizing a set of files:
	 * delete the file created in phase 1 and then recreate the localized copy of 
	 * this file by replacing the external link with other localized file.  These 
	 * other localized files contain relative paths to files contained
	 * by the archive, at least for those links that were resolved or retrieved.
	 * 
	 * The purpose of 2-phase localization is to make sure that the external link of
	 * the localized copy of this data file will point to the "localized" copy of other
	 * files.  For example, if A.pdf points to B.xml which point to other remote URL,
	 * the phase 1 of localization A.pdf will make A_loc.pdf point to B.xml(since B 
	 * is not localized yet), and B_loc.xml points to downloaded link such as 
	 * ./link/....html.  The phase 2 of localization will make A_loc.pdf points 
	 * to B_loc.xml and B_loc.xml points to ./link/...html.  Ingest will make 
	 * phase 1 call for all data files in the packages and then relocalizing it 
	 * again with phase 2.  
	 * 
	 * Making the 2 phase localization will also prevent the infinite loop of cycle
	 * references such as (A -> B -> C -> A) as might happen with other scheme 
	 * in rearranging the calling sequence of data files. 
	 *  
	 * @throws FatalException
	 * @throws PackageException
	 */
	public void localize() throws FatalException, PackageException {
	    boolean continueLocalizing = localizeSetup(); // sets the loc. call state
	       
	    // exit early if this file should not be localized
	    if (!continueLocalizing){
	        return;
	    }
	}
	
	/**
     * normalize the bitstreams in this QuickTime file and then multiplex them back into a QuickTime file
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

        // get the normalization from the configuration files
        if (keepNormalizing){
            String formProp = "NORM_" + this.getFormatCode() + "_FORMAT";
            normForm = ArchiveProperties.getInstance().getArchProperty(formProp);
        }

        // make sure that there are bitstreams to normalize
        if (keepNormalizing && (this.getBitstreams() == null
        		|| this.getBitstreams().size() <= 0)) {
            Informer.getInstance().warning(this, methodName,
				"No bitstreams to normalize", "file: " + this.getPath());                
            // done normalizing; exit
            keepNormalizing = false;
        }
        
        if (keepNormalizing && normForm.equals(TransformationFormat.QUICKTIME_NORM_1)) {
            // set the event procedure
            specProc = Procedure.PROC_QUICKTIME_NORM_1;
               
            // create a QuickTime files that contains the normalized audio and video bitstream formats.
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
            File outputFile = new File(normDirPath, "norm.mov");

            String outputPath = outputFile.getAbsolutePath();
            String fileToNorm = this.getPath();
            
            // tell DataFileFactory to treat these files as created by the archive
            DataFileFactory.getInstance().addFutureOriginArchivePath(outputFile.getAbsolutePath());
  
            // construct command to create a QuickTime files with normalized video stream and audio stream.
            String cmdProperty = ArchiveProperties.getInstance().getArchProperty("QUICKTIME_NORM_CMD");
            cmdProperty = cmdProperty.replaceFirst("%INPUT_FILE%", fileToNorm);
            cmdProperty = cmdProperty.replaceFirst("%OUTPUT_FILE%", outputPath);
            cmdProperty = cmdProperty.replaceFirst("%QUICKTIME_VIDEO_NORM%", normalizedVideo);
            cmdProperty = cmdProperty.replaceFirst("%QUICKTIME_AUDIO_NORM%", normalizedAudio);
            String[] command = cmdProperty.split("\\s");                

            // create the QuickTime files with normalized bitstreams.
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
     * @param _creationDate
     * @throws FatalException
     */
    public void SetCreationDate(String _creationDate) throws FatalException{
    	if (_creationDate != null) {
    		super.setCreateDate(_creationDate);
    	}
    }
    
    /**
     * @param _modDate
     */
    public void SetModifiedDate(String _modDate) {
    	if (_modDate != null) {
    		modifiedDate = _modDate;
    	}
    }
    
    /**
     * @param _duration
     */
    public void SetDuration(double _duration) {
    	duration = _duration;
    }
    
    /**
     * @param _preferredRate
     */
    public void SetPrefferedRate(float _preferredRate) {
    	preferredRate = _preferredRate;
    }
    
    /**
     * @param _preferredVolume
     */
    public void SetPrefferedVolume(float _preferredVolume) {
    	preferredVolume = _preferredVolume;
    }
    
    /**
     * @param _timeScale
     */
    public void SetTimeScale(long _timeScale) {
    	timeScale = _timeScale;
    }
   
    /**
     * @param _previewStart
     */
    public void SetPreviewStart(long _previewStart) {
    	previewStart = _previewStart;
    }
    
    /**
     * @param _previewDuration
     */
    public void SetPreviewDuration(long _previewDuration) {
    	previewDuration = _previewDuration;
    }
    
    /**
     * @param _hasDisplayMatrix
     */
    public void SetHasDisplayMatrix(boolean _hasDisplayMatrix) {
    	hasDisplayMatrix = _hasDisplayMatrix;
    }
    
    /**
     * @param _hasUserData
     */
    public void SetHasUserData(boolean _hasUserData) {
    	hasUserData = _hasUserData;
    }
 
    /**
     * @param _hasColorTable
     */
    public void SetHasColorTable(boolean _hasColorTable) {
    	hasColorTable = _hasColorTable;
    }
  
    /**
     * @param _hasClippingRegion
     */
    public void SetHasClippingRegion(boolean _hasClippingRegion) {
    	hasClippingRegion = _hasClippingRegion;
    }
   
    /**
     * @param _hasCompressedMovie
     */
    public void SetHasCompressedMovie(boolean _hasCompressedMovie) {
    	hasCompressedMovie = _hasCompressedMovie;
    }
  
    /**
     * @param _hasReferences
     */
    public void SetHasReferences(boolean _hasReferences) {
    	hasReferences = _hasReferences;
    }
    
	/**
	 * get string representing the duration of the movie in the format of HH:MM:SS
	 * @return String object
	 */
	public String getDuration() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		// need to convert from seconds to milliseconds for Date object
		Date newDate = new Date((int) duration * 1000); 
		
		return sdf.format(newDate);
	}
}
