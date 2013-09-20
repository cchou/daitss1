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
 * Created on Mar 25, 2004
 *
 */
package edu.fcla.daitss.file;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.StringUtil;

/**
 * 
 * Extension represent file extensions.
 * It is used to store extensions recognized by the archive and
 * to associate mime media types with these extensions.
 * 
 * Extension strings must start with "EXT_" and only extension strings 
 * may start with "EXT_".
 * 
 * When a new extension (EXT_*) is added:
 * Note: for items 1 + 2, see notes in MimeMediaType.java 
 * 
 * 1. In MimeMediaType.java make sure there is an associated mime type for the new extension.
 *    In other words, add a static variable of the form:
 * 	  public static final String MIME_? = "?"
 * 
 * 2. In MimeMediaType.java make sure there is a class list (of DataFile subclasses) that 
 *    corresponds to the mime type of the extension being added.
 *    In other words, add a static varible of the form:
 *    public static final String[] CLASS_? = {?};
 * 
 * 3. its mime media type priority list must be created (MIME_EXT_?)
 *    in this class
 * 
 * 4. both must be added to the mimeLookup table in buildMimeLookup()
 *    in this class
 * 
 * 5. include its corresponding mime type from MimeMediaType in MIME_EXT_NONE if and
 * 	  only if its mime media priority list is not already represented by 
 * 	  another one in the list. For example, MIME_EXT_XSD is not in MIME_EXT_NONE's
 * 	  list because MIME_EXT_XSD's mime media priority list is the same
 * 	  as MIME_EXT_XML's list. 
 * 
 * Note that the mapping of extension to mime type is many to many.
 * 
 * 
 * @author Chris Vicary, FCLA
 * @author Andrea Goethals, FCLA		
 *
 */
public class Extension {
	
	private static String CLASSNAME = "edu.fcla.daitss.file.util.Extension";
	
	/**
	 * XML DTD file (containing entitiy declarations) extension
	 */
	public static final String EXT_ENT = "ent";
	
	/**
	 * AVI preferred file extension
	 */
	public static final String EXT_AVI = "avi";
	
	/**
	 * QuickTime preferred file extension
	 */
	public static final String EXT_QUICKTIME = "mov";
	
	/**
	 * JPEG2000's JP2 preferred file extension
	 */
	public static final String EXT_JP2 = "jp2";
	
	/**
	 * JPEG file extension
	 */
	public static final String EXT_JPEG = "jpeg";
	
	/**
	 * JPEG2000's JPX alternate file extension
	 */
	public static final String EXT_JPF = "jpf";
	
	/**
	 * JPEG file extension
	 */
	public static final String EXT_JPG = "jpg";
	
	/**
	 * JPEG2000's JP2 alternate registered file extension
	 */
	public static final String EXT_JPG2 = "jpg2";
	
	/**
	 * JPEG2000's JPX file extension
	 */
	public static final String EXT_JPX = "jpx";

	/**
	 * MXF (XML) file extension
	 */
	public static final String EXT_MXF = "mxf";	
	
	/**
	 * No extension
	 */
	public static String EXT_NONE = "";
	
	/**
	 * PDF file extension
	 */
	public static String EXT_PDF = "pdf";
	
	/**
	 * Tiff file extension
	 */
	public static final String EXT_TIF = "tif";
	
	/**
	 * Tiff file extension
	 */
	public static final String EXT_TIFF = "tiff";
	
	/**
	 * General text file extension
	 */
	public static final String EXT_TEXT = "txt";
	
	/**
	 * General pro file extension
	 */
	public static final String EXT_PRO = "pro";
	
	/**
	 * General csv file extension
	 */
	public static final String EXT_CSV = "csv";
	
	/**
	 * WAVE preferred file extension
	 */
	public static final String EXT_WAVE = "wav";
	
	/**
	 * XML file extension
	 */
	public static final String EXT_XML = "xml";
	
	/**
	 * XML DTD file extension
	 */
	public static final String EXT_XMLDTD = "dtd";
	
	/**
	 * XSD (XML Schema) file extension
	 */
	public static final String EXT_XSD = "xsd";
	
	/**
	 * XSL (XML Stylesheet) file extension
	 */
	public static final String EXT_XSL = "xsl";

	/**
	 * Set of recognized file extensions
	 */
	private static Vector extensions = null;
	
	/**
	 * Mime media types that correspond to the 
	 * AVI file extension
	 */
	public static final String[] MIME_EXT_AVI =  {
		MimeMediaType.MIME_VIDEO_AVI
	};
	
	/**
	 * Mime media types that correspond to the 
	 * QUICKTIME file extension
	 */
	public static final String[] MIME_EXT_QUICKTIME =  {
		MimeMediaType.MIME_VIDEO_QUICKTIME
	};
	
	/**
	 * Mime media types that correspond to the 
	 * XML DTD file (containing entity declarations) extension
	 */
	public static final String[] MIME_EXT_ENT =  {
		MimeMediaType.MIME_APP_XMLDTD
	};
	
	/**
	 * Mime media types that correspond to the
	 * JP2 file extension
	 */
	public static final String[] MIME_EXT_JP2 = {
		MimeMediaType.MIME_IMG_JP2,
		MimeMediaType.MIME_IMG_JPX
	};
	
	/**
	 * Mime media types that correspond to the
	 * JPF file extension
	 */
	public static final String[] MIME_EXT_JPF = {
		MimeMediaType.MIME_IMG_JPX
	};
	
	/**
	 * Mime media types that correspond to the 
	 * JPEG file extension
	 */
	public static final String[] MIME_EXT_JPEG =  {
		MimeMediaType.MIME_IMG_JPEG
	};
	
	/**
	 * Mime media types that correspond to the 
	 * JPEG file extension
	 */
	public static final String[] MIME_EXT_JPG =  {
		MimeMediaType.MIME_IMG_JPEG
	};
	
	/**
	 * Mime media types that correspond to the
	 * JPX file extension
	 */
	public static final String[] MIME_EXT_JPX = {
		MimeMediaType.MIME_IMG_JPX
	};
	
	/**
	 * Mime media types that correspond to the
	 * JPX file extension
	 */
	public static final String[] MIME_EXT_MXF = {
		MimeMediaType.MIME_APP_XML
	};
	
	/**
	 * Mime media types that correspond to the 
	 * WAVE file extension
	 */
	public static final String[] MIME_EXT_WAVE =  {
		MimeMediaType.MIME_AUDIO_WAVE
	};
	/**
	 * Mime media types that correspond to having no file extension.
	 * Note: this array must contain ALL possible mime types in prioritized
	 * order (in the order of most specific to least specific, in other words
	 * if there is an overlap in file formats the more specific format should 
	 * listed first)
	 * 
	 */
	public static String[] MIME_EXT_NONE =  {
		MimeMediaType.MIME_IMG_TIFF,
		MimeMediaType.MIME_IMG_JPEG,
		MimeMediaType.MIME_IMG_JP2,
		MimeMediaType.MIME_IMG_JPX,
		MimeMediaType.MIME_APP_PDF,
		MimeMediaType.MIME_APP_XML,
		MimeMediaType.MIME_APP_XMLDTD,
		MimeMediaType.MIME_TXT_PLAIN,
		MimeMediaType.MIME_APP_UNK
	};
	
	/**
	 * Mime media types that correspond to the 
	 * PDF file extension
	 */
	public static String[] MIME_EXT_PDF =  {
		MimeMediaType.MIME_APP_PDF
	};
	
	/**
	 * Mime media types that correspond to the 
	 * Tiff file extension
	 */
	public static final String[] MIME_EXT_TIF =  {
		MimeMediaType.MIME_IMG_TIFF
	};
	
	/**
	 * Mime media types that correspond to the 
	 * Tiff file extension
	 */
	public static final String[] MIME_EXT_TIFF = {
		MimeMediaType.MIME_IMG_TIFF	
	};

	public static final String[] MIME_EXT_TEXT = {
	    MimeMediaType.MIME_TXT_PLAIN
	};
	
	public static final String[] MIME_EXT_CSV = {
	    MimeMediaType.MIME_TXT_CSV
	};
	
	/**
	 * Mime media types that correspond to the 
	 * XML file extension
	 */
	public static final String[] MIME_EXT_XML =  {
		MimeMediaType.MIME_APP_XML
	};
	
	/**
	 * Mime media types that correspond to the 
	 * XML DTD file extension
	 */
	public static final String[] MIME_EXT_XMLDTD =  {
		MimeMediaType.MIME_APP_XMLDTD
	};

	/**
	 * Mime media types that correspond to the 
	 * XSD file extension
	 */
	public static final String[] MIME_EXT_XSD =  {
		MimeMediaType.MIME_APP_XML
	};
	
	/**
	 * Mime media types that correspond to the 
	 * XSL file extension
	 */
	public static final String[] MIME_EXT_XSL =  {
		MimeMediaType.MIME_APP_XML
	};

	/**
	 * Mime media types that correspond to the 
	 * XSL file extension
	 */
	public static final String[] MIME_EXT_PRO =  {
		MimeMediaType.MIME_APP_PRO
	};
	
	/**
	 * The set of recognized file extensions and
	 * their associated mime media types.
	 */
	private static Hashtable<String, String[]> mimeLookup = null;

	/**
	 * Method to populate the recognizedExtensions Vector based on fields
	 * present in the Extension class.
	 * 
	 * @throws FatalException
	 */
	private static void buildExtensions() throws FatalException {
		String methodName = "buildExtensions()";
		
		if (extensions == null) {
			
			extensions = new Vector();
			
			// get members from MimeMediaType and put in Vector
			Field[] fields = new Extension().getClass().getFields();
			for (int i=0;i<fields.length;i++) {
				if(fields[i].getName().matches("EXT_.*")) {
					try {
						extensions.add(fields[i].get(fields[i]));
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
				}// end if
			}// end for
		}// end if
	}// end build extensions

	/**
	 * Build the set of recognized file extensions and
	 * their associated prioritized list of mime media types.
	 * 
	 */
	private static void buildMimeLookup() {
		mimeLookup = new Hashtable<String, String[]>();
		mimeLookup.put(EXT_ENT, MIME_EXT_ENT);
		mimeLookup.put(EXT_QUICKTIME, MIME_EXT_QUICKTIME);
		mimeLookup.put(EXT_AVI, MIME_EXT_AVI);
		mimeLookup.put(EXT_WAVE, MIME_EXT_WAVE);
		mimeLookup.put(EXT_JP2, MIME_EXT_JP2);
		mimeLookup.put(EXT_JPF, MIME_EXT_JPF);
		mimeLookup.put(EXT_JPG, MIME_EXT_JPG);
		mimeLookup.put(EXT_JPG2, MIME_EXT_JP2);
		mimeLookup.put(EXT_JPEG, MIME_EXT_JPEG);
		mimeLookup.put(EXT_JPX, MIME_EXT_JPX);
		mimeLookup.put(EXT_MXF, MIME_EXT_MXF);
		mimeLookup.put(EXT_NONE, MIME_EXT_NONE);
		mimeLookup.put(EXT_PDF, MIME_EXT_PDF);
		mimeLookup.put(EXT_TIF, MIME_EXT_TIF);
		mimeLookup.put(EXT_TIFF, MIME_EXT_TIFF);
		mimeLookup.put(EXT_CSV, MIME_EXT_CSV);
		mimeLookup.put(EXT_XML, MIME_EXT_XML);
		mimeLookup.put(EXT_XMLDTD, MIME_EXT_XMLDTD);
		mimeLookup.put(EXT_XSD, MIME_EXT_XSD);
		mimeLookup.put(EXT_XSL, MIME_EXT_XSL);
		mimeLookup.put(EXT_PRO, MIME_EXT_PRO);
	}

	/**
	 * @return the recognized extensions
	 * @throws FatalException
	 */
	private static Vector getExtensions() throws FatalException {
		init();
		return extensions;
	}

	/**
	 * 
	 * @return the recognized mime media types
	 * @throws FatalException
	 */
	public static Hashtable getMimeLookup() throws FatalException {
		init();
		return mimeLookup;
	}

	/**
	 * Returns a String array of mimetypes associated with <code>extension</code>. Internally extensions
	 * are stored as lowercase characters, but this method is case-insensitive, so any extension may
	 * be passed as an argument. The extension should not include the stopgap ("."). 
	 * 
	 * @param extension
	 * @return If <code>extension</code> is recognized, an array of Strings 
	 * 		representing supported mimetypes for <code>extension</code>.
	 * 		If extension is unrecognized then null is returned.
	 * @throws FatalException
	 *  
	 */
	public static String[] getMimeType(String extension) 
		throws FatalException {		
		init();
		return  (String [])mimeLookup.get(extension.toLowerCase());
	}

	/**
	 * Build the set of known extensions and mime media tpyes.
	 * 
	 * @throws FatalException
	 */
	public static void init() throws FatalException {
		if (extensions == null) {
			buildExtensions();
		}
		if (mimeLookup == null) {
			buildMimeLookup();
		}		
	}

	/**
	 * Determine whether or not a file extension is a 
	 * recognized one.
	 * 
	 * @param ext file extension
	 * @return whether or not the extension is recognized
	 * @throws FatalException
	 */
	public static boolean isRecognizedExt(String ext) throws FatalException {
		String methodName = "isRecognizedExt(String)";
		// check input
		if (ext == null) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Illegal argument",
				"ext: " + ext,
				new IllegalArgumentException());		
		}
		Iterator iter = getExtensions().iterator();
		while (iter.hasNext()) {
			if(ext.equalsIgnoreCase((String) iter.next())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws PackageException
	 * @throws FatalException
	 */
	public static void main(String[] args)
		throws PackageException, FatalException {
		String ext = "xml";
		System.out.println(ext + ":\n" + StringUtil.getString(getMimeType(ext)));
		ext = "tiff";
		System.out.println(ext + ":\n" + StringUtil.getString(getMimeType(ext)));
		//ext = "tip";
		//System.out.println(ext + ":\n" + StringUtil.getString(getMimeType(ext)));
		ext = "";
		System.out.println(ext + ":\n" + StringUtil.getString(getMimeType(ext)));
		ext = "mxf";
		System.out.println(ext + ":\n" + StringUtil.getString(getMimeType(ext)));		
		
	}

	/**
	 * 
	 */
	public Extension() {
	}

}
