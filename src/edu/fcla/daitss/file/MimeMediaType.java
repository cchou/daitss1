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
 * Created on Mar 23, 2004
 *
 */
package edu.fcla.daitss.file;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

/**
 * MimeMediaType
 * 
 * When a new mime type is added:
 * 1) Add a static variable representing the mime type in the form:
 * 	  public static final String MIME_? = "?/?"; (Note that there is a
 *    maximum allowed length for this - see DataFile.MAX_MTYPE_LENGTH.
 * 
 * 2) Add a prioritized class list for the mime type in the form:
 *    public static final String[] CLASS_? = {?,?,...};
 * 	  
 * 	  a) for every class that is entered in the CLASS_? array, make sure the 
 *  	 class is also added to the CLASS_APP_UNKNOWN array. 
 * 
 * 3) Map the MIME_? constant to the CLASS_? constant in the buildClassLookup() 
 *    method. 
 * 
 * 4) Also remember that an extension->mimetype mapping must be added to the 
 *    Extension class - see Extension.java for more notes.
 * 
 * @author Chris Vicary, FCLA
 *
 */
public class MimeMediaType {	
	/**
	 * class for PDF files
	 */
	public static final String[] CLASS_APP_PDF = {
		"edu.fcla.daitss.format.miscfile.pdf.Pdf"	
	};
	
	/**
	 * this must map unknown mime types to all possible classes 
	 * known to DAITSS.
	 */
	public static final String[] CLASS_APP_UNK = {
		"edu.fcla.daitss.format.audio.wave.Wave",
		"edu.fcla.daitss.format.multimedia.avi.AviFile",
		"edu.fcla.daitss.format.multimedia.quicktime.QuickTime",
		"edu.fcla.daitss.format.image.tiff.Tiff",
		"edu.fcla.daitss.format.image.jpeg.Jpeg",
		"edu.fcla.daitss.format.image.jpeg2000.Jp2",
		"edu.fcla.daitss.format.image.jpeg2000.Jpx",
		"edu.fcla.daitss.format.miscfile.pdf.Pdf",
		"edu.fcla.daitss.format.pro.ProFile",
		"edu.fcla.daitss.format.text.csv.CSVFile",
		"edu.fcla.daitss.format.markup.XML",
		"edu.fcla.daitss.format.text.dtd.DTD",
		"edu.fcla.daitss.format.text.TextFile",
		"edu.fcla.daitss.file.DataFile"	
	};
	
	/**
	 * XML file class
	 */
	public static final String[] CLASS_APP_XML = {
		"edu.fcla.daitss.format.markup.XML"
	};
	
	/**
	 * XML DTD file class
	 */
	public static final String[] CLASS_APP_XMLDTD = {
		"edu.fcla.daitss.format.text.dtd.DTD"
	};
	
	/**
	 * WAVE class
	 */
	public static final String[] CLASS_AUDIO_WAVE = {
		"edu.fcla.daitss.format.audio.wave.Wave"	
	};
	
	/**
	 * JPEG2000's JP2 class
	 */
	public static final String[] CLASS_IMG_JP2 = {
		"edu.fcla.daitss.format.image.jpeg2000.Jp2"
	};
	
	/**
	 * JPEG class
	 */
	public static final String[] CLASS_IMG_JPEG = {
		"edu.fcla.daitss.format.image.jpeg.Jpeg"	
	};
	
	/**
	 * JPEG2000's JPX class
	 */
	public static final String[] CLASS_IMG_JPX = {
		"edu.fcla.daitss.format.image.jpeg2000.Jpx"
	};
	
	/**
	 * Tiff class
	 */
	public static final String[] CLASS_IMG_TIFF = {
		"edu.fcla.daitss.format.image.tiff.Tiff"	
	};
	
	/**
	 * Avi class
	 */
	public static final String[] CLASS_VIDEO_AVI = {
		"edu.fcla.daitss.format.multimedia.avi.AviFile"	
	};
	
	/**
	 * QUICKTIME class
	 */
	public static final String[] CLASS_VIDEO_QUICKTIME = {
		"edu.fcla.daitss.format.multimedia.quicktime.QuickTime"	
	};
	
	/**
	 * Plain text class
	 */
	public static final String[] CLASS_TXT_PLAIN = {
		"edu.fcla.daitss.format.text.TextFile"
	};
	
	/**
	 * PRO class
	 */
	public static final String[] CLASS_APP_PRO = {
		"edu.fcla.daitss.format.pro.ProFile"
	};
	
	/**
	 * CSV text class
	 */
	public static final String[] CLASS_TXT_CSV = {
		"edu.fcla.daitss.format.text.csv.CSVFile"
	};

	/**
	 * Contains the set of supported mime media types and
	 * their associated daitss classes
	 */
	private static Hashtable <String, String[]> classLookup = null;
	
	/**
	 * Used for <code>Informer</code> calls within
	 * static methods
	 */
	private static String CLASSNAME = "edu.fcla.daitss.file.util.MimeMediaType";

    /**
     * Bzip2 file mime media type
     */ 
    public static final String MIME_APP_BZIP2 = "application/x-bzip2";
    
    /**
     * DOS executable file mime media type
     */ 
    public static final String MIME_APP_DOS_EXEC = "application/x-dosexec";    
        
    /**
     * MS Excel file mime media type
     */ 
    public static final String MIME_APP_MS_EXCEL = "application/vnd.ms-excel";
    
    /**
     * MS Word file mime media type
     */ 
    public static final String MIME_APP_MS_WORD = "application/msword";

    
    /**
     * GZIP executable file mime media type
     */ 
    public static final String MIME_APP_GZIP = "application/x-gzip";        
    
    /**
     * Postscript file mime media type
     */ 
    public static final String MIME_APP_POSTSCRIPT = "application/postscript";            
    
    /**
	 * PDF file mime media type
	 */	
	public static final String MIME_APP_PDF = "application/pdf";
	
    /**
     * Compressed RAR file mime media type
     */ 
    public static final String MIME_APP_RAR_COMP = "application/x-rar-compressed";

    
    /**
	 * Unknown file format mime media type
	 */
	public static final String MIME_APP_UNK = "application/octet-stream";
	
	/**
	 * XML file mime media type
	 */
	public static final String MIME_APP_XML = "application/xml";

	/**
	 * XML DTD file mime media type
	 */	
	public static final String MIME_APP_XMLDTD = "application/xml-dtd";
	
    /**
     * ZIP file mime media type
     */ 
    public static final String MIME_APP_ZIP = "application/zip";

    /**
     * MPEG mime media type
     */
    public static final String MIME_AUDIO_MPEG = "audio/mpeg";    
    
    /**
     * Unknown audio mime media type
     */
    public static final String MIME_AUDIO_UNK = "audio/unknown";    
    
    /**
	 * WAVE mime media type
	 */
	public static final String MIME_AUDIO_WAVE = "audio/x-wav";
	
    /**
     * BMP image mime media type
     */
    public static final String MIME_IMG_BMP = "image/bmp";        
    
    /**
     * GIF image mime media type
     */
    public static final String MIME_IMG_GIF = "image/gif";        
    
    /**
	 * JPEG2000's JP2 mime media type
	 */
	public static final String MIME_IMG_JP2 = "image/jp2";
	
	/**
	 * JPEG file mime media type
	 */
	public static final String MIME_IMG_JPEG = "image/jpeg";
	
	/**
	 * JPEG2000's JPX mime media type
	 */
	public static final String MIME_IMG_JPX = "image/jpx";
	
    /**
     * PNG image mime media type
     */
    public static final String MIME_IMG_PNG = "image/png";            
    
    /**
     * Portable bitmap image mime media type
     */
    public static final String MIME_IMG_PORT_BMP = "image/x-portable-bitmap";            

    /**
     * Portable greymap image mime media type
     */
    public static final String MIME_IMG_PORT_GMP = "image/x-portable-greymap";            

    /**
     * Portable pixelmap image mime media type
     */
    public static final String MIME_IMG_PORT_PXMP = "image/x-portable-pixmap";            
    
    /**
	 * Tiff file mime media type
	 */
	public static final String MIME_IMG_TIFF = "image/tiff";		
	
    /**
     * HTML 4.0
     */
    public static final String MIME_TXT_HTML_4_0 = "text/html";

    /**
     * HTML 4.0.1
     */
    public static final String MIME_TXT_HTML_4_0_1 = "text/html";
    
    /**
	 * Plain text file mime media type
	 */
	public static final String MIME_TXT_PLAIN = "text/plain";
	
	/**
	 * CSV text file mime media type
	 */
	public static final String MIME_TXT_CSV = "text/csv";
	
	/**
	 * Plain text file mime media type for PRO (output generated by PrimeOCR)
	 */
	public static final String MIME_APP_PRO = "application/x-pro";
	
    /**
     * Rich text file mime media type
     */
    public static final String MIME_TXT_RTF = "text/rtf";
    
    /**
     * SGML
     */
    public static final String MIME_TXT_SGML = "text/sgml";

    
    /**
	 * xml text file mime media type
	 */
	public static final String MIME_TXT_XML = "text/xml";
	
	/**
	 * AVI mime media type
	 */
	public static final String MIME_VIDEO_AVI = "video/avi";
	
    /**
     * Video MPEG mime media type
     */
    public static final String MIME_VIDEO_MPEG = "video/mpeg";    
    
    /**
     * MS Video MPEG mime media type
     */
    public static final String MIME_VIDEO_MS_VIDEO = "video/x-msvideo";    

    
    /**
	 * QuickTime mime media type
	 */
	public static final String MIME_VIDEO_QUICKTIME = "video/quicktime";
	
	/**
	 * Set of supported mime media types
	 */
	private static Vector <String> supportedMimeTypes = null;
	
	/**
	 * set of valid (recognized, but not necessary supported) mime type.
	 */
	private static Vector <String> validMimeTypes = null;
	/**
	 * Build the set of SUPPORTED mime media types and
	 * their associated daitss classes
	 */
	private static void buildClassLookup() {
		classLookup = new Hashtable <String, String[]> ();
		classLookup.put(MIME_APP_PDF, CLASS_APP_PDF);
		classLookup.put(MIME_APP_XML, CLASS_APP_XML);
		classLookup.put(MIME_APP_XMLDTD, CLASS_APP_XMLDTD);
		classLookup.put(MIME_APP_UNK, CLASS_APP_UNK);
		classLookup.put(MIME_IMG_JP2, CLASS_IMG_JP2);
		classLookup.put(MIME_IMG_JPEG, CLASS_IMG_JPEG);
		classLookup.put(MIME_IMG_JPX, CLASS_IMG_JPX);
		classLookup.put(MIME_IMG_TIFF, CLASS_IMG_TIFF);	
		classLookup.put(MIME_VIDEO_QUICKTIME, CLASS_VIDEO_QUICKTIME);	
		classLookup.put(MIME_VIDEO_AVI, CLASS_VIDEO_AVI);	
		classLookup.put(MIME_AUDIO_WAVE, CLASS_AUDIO_WAVE);
		classLookup.put(MIME_TXT_CSV, CLASS_TXT_CSV);	
		classLookup.put(MIME_APP_PRO, CLASS_APP_PRO);	
		classLookup.put(MIME_TXT_PLAIN, CLASS_TXT_PLAIN);		
	}
	
	/**
	 * Method to populate the supportedMimeTypes Vector based on fields
	 * present in the MimeMediaType class.
	 * 
	 * @throws FatalException
	 */
	private static void buildMimeTypes() throws FatalException {
		String methodName = "buildMimeTypes()";
		
		// build the list of supported mime type
		supportedMimeTypes = new Vector <String> ();
		supportedMimeTypes.add(MIME_APP_PDF);
		supportedMimeTypes.add(MIME_APP_XML);
		supportedMimeTypes.add(MIME_APP_XMLDTD);
		supportedMimeTypes.add(MIME_APP_UNK);
		supportedMimeTypes.add(MIME_IMG_JP2);
		supportedMimeTypes.add(MIME_IMG_JPEG);
		supportedMimeTypes.add(MIME_IMG_JPX);
		supportedMimeTypes.add(MIME_IMG_TIFF);	
		supportedMimeTypes.add(MIME_VIDEO_QUICKTIME);	
		supportedMimeTypes.add(MIME_VIDEO_AVI);	
		supportedMimeTypes.add(MIME_AUDIO_WAVE);	
		supportedMimeTypes.add(MIME_TXT_CSV);	
		supportedMimeTypes.add(MIME_APP_PRO);
		supportedMimeTypes.add(MIME_TXT_PLAIN);		
		
		validMimeTypes = new Vector <String> ();
		
		// get members from MimeMediaType and use them to build the list of valid mime type
		Field[] fields = new MimeMediaType().getClass().getFields();
		for (int i=0;i<fields.length;i++) {
			if(fields[i].getName().matches("MIME_.*")) {
				try {
					validMimeTypes.add((String)fields[i].get(fields[i]));
				} 
				catch (IllegalArgumentException e) {
					Informer.getInstance().fail(
						CLASSNAME,
						methodName,
						"Illegal argument",
						"field: " + fields[i].getName(),
						e);
				} 
				catch (IllegalAccessException e) {
					Informer.getInstance().fail(
						CLASSNAME,
						methodName,
						"Illegal access of MimeMediaType",
						"field: " + fields[i].getName(),
						e);
				}
			}
		}
	}
	
	/** 
	 * @return the classes associated with file formats
	 * @throws FatalException
	 */
	public static Hashtable getClassLookup() throws FatalException {
		init();
		return classLookup;
	}
	
	/**
	 * 
	 * @return the supported mime media types
	 * @throws FatalException
	 */
	public static Vector getSupportedMimeTypes() throws FatalException {
		init();
		return supportedMimeTypes;
	}

	public static Vector getValidMimeTypes() throws FatalException {
		init();
		return validMimeTypes;
	}
	/**
	 * Build the known classes ans mime media types.
	 * @throws FatalException
	 */
	public static void init() throws FatalException {
		if (classLookup == null) {
			buildClassLookup();
		}
		if (supportedMimeTypes == null) {
			buildMimeTypes();
		}		
	}
	
	/**
	 * Returns true if <code>type</code> is a MIME type supported by the 
	 * archive.
	 * 
	 * @param type
	 * @return boolean
	 * @throws FatalException
	 */
	public static boolean isSupportedType(String type) throws FatalException {
		String methodName = "isSupportedType(String)";
		init();
		// check input
		if (type == null) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Illegal argument",
				"type: " +type,
				new IllegalArgumentException());
		}
		Iterator iter = getSupportedMimeTypes().iterator();
		while (iter.hasNext()) {
			if (type.equalsIgnoreCase((String) iter.next())) {
				return true;
			}			
		}
		// if control made it here, then type is not a supportedType
		Informer.getInstance().warning(
			CLASSNAME,
			methodName,
			"Encountered unsupported MIME type",
			"mimeType: "+type);
			
		return false;
	}
	
	/**
	 * Returns true if <code>type</code> is a valid MIME type 
	 * @param type
	 * @return boolean
	 * @throws FatalException
	 */
	public static boolean isValidType(String type) throws FatalException {
		String methodName = "isValidType(String)";
		init();
		// check input
		if (type != null) {
			Iterator iter = getValidMimeTypes().iterator();
			while (iter.hasNext()) {
				if (type.equalsIgnoreCase((String) iter.next())) 
					return true;
			}			
		}
			
		// if control made it here, then type is not a validType
		Informer.getInstance().warning(
			CLASSNAME,
			methodName,
			"Encountered invalid MIME type",
			"mimeType: "+type);
			
		return false;
	}
}
