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
 * Created on Oct 11, 2004
 *
 */
package edu.fcla.daitss.file;

import java.util.Enumeration;
import java.util.Hashtable;

import edu.fcla.daitss.exception.FatalException;

import edu.fcla.daitss.format.audio.wave.*;
import edu.fcla.daitss.format.image.jpeg.Jpeg;
import edu.fcla.daitss.format.image.jpeg2000.Jp2;
import edu.fcla.daitss.format.image.jpeg2000.Jpx;
import edu.fcla.daitss.format.image.tiff.TiffRevisions;
import edu.fcla.daitss.format.markup.XML;
import edu.fcla.daitss.format.miscfile.pdf.Pdf;
import edu.fcla.daitss.format.multimedia.avi.AviFile;
import edu.fcla.daitss.format.multimedia.quicktime.QuickTime;
import edu.fcla.daitss.format.text.dtd.DTD;
import edu.fcla.daitss.util.Informer;

/**
 * Format represents a file's format. This is a
 * combination of its media type, media type version,
 * and variation on the media type. The media type is
 * always required, media type version and media type
 * variation may not apply to a particular file.
 * 
 * Note that each format value must be less than or 
 * equla to 255 characters.
 * 
 * @author Andrea Goethals, Chris Vicary FCLA
 *
 */
public class Format {
    
    /** 
     * Bzip 2 format
     */
    public static final String APP_BZIP2 = "APP_BZIP2";    
    
    /**
     * DOS executable format
     */
    public static final String APP_DOS_EXEC = "APP_DOS_EXEC";    
    
    /**
     * GZIP format
     */
    public static final String APP_GZIP = "APP_GZIP";
    
    /**
     * Microsoft Excel format
     */
    public static final String APP_MS_EXCEL = "APP_MS_EXCEL";
    
    /**
     * Microsoft Word format
     */
    public static final String APP_MS_WORD = "APP_MS_WORD";    
    
    /**
     * Pdf version 1.0
     */
    public static final String APP_PDF_1_0 = "APP_PDF_1_0";
    
    /**
     * Pdf version 1.1
     */
    public static final String APP_PDF_1_1 = "APP_PDF_1_1";
    
    /**
     * Pdf version 1.2
     */
    public static final String APP_PDF_1_2 = "APP_PDF_1_2";
    
    /**
     * Pdf version 1.3
     */
    public static final String APP_PDF_1_3 = "APP_PDF_1_3";
    
    /**
     * Pdf version 1.4
     */
    public static final String APP_PDF_1_4 = "APP_PDF_1_4";
    
    /**
     * Pdf version 1.5
     */
    public static final String APP_PDF_1_5 = "APP_PDF_1_5";
 
    /**
     * Pdf version 1.6
     */
    public static final String APP_PDF_1_6 = "APP_PDF_1_6";
    
    /**
     * Pdf version 1.7
     */
    public static final String APP_PDF_1_7 = "APP_PDF_1_7";
    /**
     * PS format
     */
    public static final String APP_POSTSCRIPT = "APP_POSTSCRIPT";
    
    /**
     * Compressed RAR format
     */
    public static final String APP_RAR_COMP = "APP_RAR_COMP";
    
    /**
     * Unknown format
     */
    public static final String APP_UNK = "APP_UNK";
    
    /**
     * XML version 1.0
     */
    public static final String APP_XML_1_0 = "APP_XML_1_0";
    
    /**
     * XML DTD version 1.0
     */
    public static final String APP_XMLDTD_1_0 = "APP_XMLDTD_1_0";
    
    /**
     * ZIP format
     */
    public static final String APP_ZIP = "APP_ZIP";
    
    /**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = "edu.fcla.daitss.file.Format";
    
    /**
     * Stores all the unique media type/subtype, version, variation
     * combinations and their associated format code.
     */
    private static Hashtable <String, String[]> formats = null; 
    
    /**
     * MPEG audio format
     */
    public static final String AUDIO_MPEG = "AUDIO_MPEG";
    
    /**
     * Unknown audio format. Identified by FFIdent.
     */
    public static final String AUDIO_UNK = "AUDIO_UNK";
    
    /**
     * WAVE
     */
    public static final String AUDIO_WAVE = "AUDIO_WAVE";
    
    /**
     * Bitmap image format
     */
    public static final String IMG_BMP = "IMG_BMP";
    
    /**
     * GIF image format
     */
    public static final String IMG_GIF = "IMG_GIF";
    
    /**
     * JP2 version 1.0
     */
    public static final String IMG_JP2_1_0 = "IMG_JP2_1_0";
    
    /**
     * Adobe version of JPEG
     */
    public static final String IMG_JPEG_ADOBE = "IMG_JPEG_ADOBE";
    
    /**
     * JFIF version of JPEG
     */
    public static final String IMG_JPEG_JFIF = "IMG_JPEG_JFIF";
    
    /**
     * JPEG file - unknown variation
     */
    public static final String IMG_JPEG_UNKNOWN = "IMG_JPEG_UNKNOWN";
    
    /**
     * JPX version 1.0
     */
    public static final String IMG_JPX_1_0 = "IMG_JPX_1_0";
    
    /**
     * PNG image format
     */
    public static final String IMG_PNG = "IMG_PNG";
    
    /**
     * Portable bitmap image format
     */
    public static final String IMG_PORT_BMP = "IMG_PORT_BMP";
    
    /**
     * Portable greymap image format
     */
    public static final String IMG_PORT_GMP = "IMG_PORT_GMP";
    
    /**
     * Portable pixelmap image format
     */
    public static final String IMG_PORT_PXMP = "IMG_PORT_PXMP";
    
    
    /**
     * Tiff revision 4.0
     */
    public static final String IMG_TIFF_4_0 = "IMG_TIFF_4_0";
    
    /**
     * Tiff revision 5.0
     */
    public static final String IMG_TIFF_5_0 = "IMG_TIFF_5_0";
    
    /**
     * Tiff revision 6.0
     */
    public static final String IMG_TIFF_6_0 = "IMG_TIFF_6_0";
    
    /**
     * Avi version 1.0
     */
    public static final String VIDEO_AVI_1_0 = "VIDEO_AVI_1_0";
    
    /**
     * MPEG
     */
    public static final String VIDEO_MPEG = "VIDEO_MPEG";
    
    /**
     * MS video format
     */
    public static final String VIDEO_MS_VIDEO = "VIDEO_MS_VIDEO";    
    
    /**
     * QuickTime
     */
    public static final String VIDEO_QUICKTIME = "VIDEO_QUICKTIME";
    
    /**
     * HMTL 4.0 format
     */
    public static final String TXT_HTML_4_0 = "TXT_HTML_4_0";    

    /**
     * HMTL 4.0.1 format
     */
    public static final String TXT_HTML_4_0_1 = "TXT_HTML_4_0_1";        
    
    /**
     * Plain text
     */
    public static final String TXT_PLAIN = "TXT_PLAIN";
    
    /**
     * PRO text format
     */
    public static final String APP_PRO = "APP_PRO";    
    
    /**
     * CSV text format
     */
    public static final String TXT_CSV = "TXT_CSV";    
    
    /**
     * Rich text format
     */
    public static final String TXT_RTF = "TXT_RTF";    
    
    /**
     * SGML format
     */
    public static final String TXT_SGML = "TXT_SGML";    
    
    
    /**
     * Construct the set of all the archive-recognized unique media
     * type/subtype, version, variation combinations and their associated format
     * code.
     */
    private static void buildFormats() {
        formats = new Hashtable <String, String[]> ();

        String[] list_APP_BZIP2 = { MimeMediaType.MIME_APP_BZIP2 , "", ""};
        formats.put(APP_BZIP2, list_APP_BZIP2);        
        
        String[] list_APP_DOS_EXEC = { MimeMediaType.MIME_APP_DOS_EXEC , "", ""};
        formats.put(APP_DOS_EXEC, list_APP_DOS_EXEC);
        
        String[] list_APP_GZIP = { MimeMediaType.MIME_APP_GZIP , "", ""};
        formats.put(APP_GZIP, list_APP_GZIP);
        
        String[] list_APP_MS_EXCEL = { MimeMediaType.MIME_APP_MS_EXCEL , "", ""};
        formats.put(APP_MS_EXCEL, list_APP_MS_EXCEL);
        
        String[] list_APP_MS_WORD = { MimeMediaType.MIME_APP_MS_WORD , "", ""};
        formats.put(APP_MS_WORD, list_APP_MS_WORD);
        
        String[] list_APP_PDF_1_0 = { MimeMediaType.MIME_APP_PDF,
                Pdf.VERSION_1_0, "" };
        formats.put(APP_PDF_1_0, list_APP_PDF_1_0);

        String[] list_APP_PDF_1_1 = { MimeMediaType.MIME_APP_PDF,
                Pdf.VERSION_1_1, "" };
        formats.put(APP_PDF_1_1, list_APP_PDF_1_1);

        String[] list_APP_PDF_1_2 = { MimeMediaType.MIME_APP_PDF,
                Pdf.VERSION_1_2, "" };
        formats.put(APP_PDF_1_2, list_APP_PDF_1_2);

        String[] list_APP_PDF_1_3 = { MimeMediaType.MIME_APP_PDF,
                Pdf.VERSION_1_3, "" };
        formats.put(APP_PDF_1_3, list_APP_PDF_1_3);

        String[] list_APP_PDF_1_4 = { MimeMediaType.MIME_APP_PDF,
                Pdf.VERSION_1_4, "" };
        formats.put(APP_PDF_1_4, list_APP_PDF_1_4);

        String[] list_APP_PDF_1_5 = { MimeMediaType.MIME_APP_PDF,
                Pdf.VERSION_1_5, "" };
        formats.put(APP_PDF_1_5, list_APP_PDF_1_5);
        
        String[] list_APP_PDF_1_6 = { MimeMediaType.MIME_APP_PDF,
                Pdf.VERSION_1_6, "" };
        formats.put(APP_PDF_1_6, list_APP_PDF_1_6);
        
        String[] list_APP_PDF_1_7 = { MimeMediaType.MIME_APP_PDF,
                Pdf.VERSION_1_7, "" };
        formats.put(APP_PDF_1_7, list_APP_PDF_1_7);
        
        String[] list_APP_POSTSCRIPT = { MimeMediaType.MIME_APP_POSTSCRIPT , "", ""};
        formats.put(APP_POSTSCRIPT, list_APP_POSTSCRIPT);
        
        String[] list_APP_RAR_COMP = { MimeMediaType.MIME_APP_RAR_COMP , "", ""};
        formats.put(APP_RAR_COMP, list_APP_RAR_COMP);        
        
        String[] list_APP_UNK = { MimeMediaType.MIME_APP_UNK,
                "", "" };
        formats.put(APP_UNK, list_APP_UNK);
        
        String[] list_APP_XML_1_0 = { MimeMediaType.MIME_APP_XML,
                XML.VERSION_1_0, "" };
        formats.put(APP_XML_1_0, list_APP_XML_1_0);

        String[] list_APP_XMLDTD_1_0 = { MimeMediaType.MIME_APP_XMLDTD,
                DTD.VERSION_1_0, "" };
        formats.put(APP_XMLDTD_1_0, list_APP_XMLDTD_1_0);
        
        String[] list_APP_ZIP = { MimeMediaType.MIME_APP_ZIP , "", ""};
        formats.put(APP_ZIP, list_APP_ZIP);

        String[] list_AUDIO_MPEG = { MimeMediaType.MIME_AUDIO_MPEG , "", ""};
        formats.put(AUDIO_MPEG, list_AUDIO_MPEG);
        
        String[] list_AUDIO_UNK = { MimeMediaType.MIME_AUDIO_UNK , "", ""};
        formats.put(AUDIO_UNK, list_AUDIO_UNK);
        
        String[] list_AUDIO_WAVE = { MimeMediaType.MIME_AUDIO_WAVE,
        		Wave.VERSION_NA, "" };
        formats.put(AUDIO_WAVE, list_AUDIO_WAVE);
        
        String[] list_IMG_BMP = { MimeMediaType.MIME_IMG_BMP , "", ""};
        formats.put(IMG_BMP, list_IMG_BMP);
        
        String[] list_IMG_GIF = { MimeMediaType.MIME_IMG_GIF , "", ""};
        formats.put(IMG_GIF, list_IMG_GIF);               
        
        String[] list_IMG_JP2_1_0 = { MimeMediaType.MIME_IMG_JP2,
                Jp2.VERSION_1_0, "" };
        formats.put(IMG_JP2_1_0, list_IMG_JP2_1_0);
        
        String[] list_IMG_JPEG_ADOBE = { MimeMediaType.MIME_IMG_JPEG,
                "", Jpeg.VAR_ADOBE };
        formats.put(IMG_JPEG_ADOBE, list_IMG_JPEG_ADOBE);
        
        String[] list_IMG_JPEG_JFIF = { MimeMediaType.MIME_IMG_JPEG,
                "", Jpeg.VAR_JFIF };
        formats.put(IMG_JPEG_JFIF, list_IMG_JPEG_JFIF);
        
        String[] list_IMG_JPEG_UNKNOWN = { MimeMediaType.MIME_IMG_JPEG,
                "", Jpeg.VAR_UNKNOWN };
        formats.put(IMG_JPEG_UNKNOWN, list_IMG_JPEG_UNKNOWN);
        
        String[] list_IMG_JPX_1_0 = { MimeMediaType.MIME_IMG_JPX,
                Jpx.VERSION_1_0, "" };
        formats.put(IMG_JPX_1_0, list_IMG_JPX_1_0);
        
        String[] list_IMG_PNG = { MimeMediaType.MIME_IMG_PNG , "", ""};
        formats.put(IMG_PNG, list_IMG_PNG);
        
        String[] list_IMG_PORT_BMP = { MimeMediaType.MIME_IMG_PORT_BMP , "", ""};
        formats.put(IMG_PORT_BMP, list_IMG_PORT_BMP);
        
        String[] list_IMG_PORT_GMP = { MimeMediaType.MIME_IMG_PORT_GMP , "", ""};
        formats.put(IMG_PORT_GMP, list_IMG_PORT_GMP);
        
        String[] list_IMG_PORT_PXMP = { MimeMediaType.MIME_IMG_PORT_PXMP , "", ""};
        formats.put(IMG_PORT_PXMP, list_IMG_PORT_PXMP);               
        
        String[] list_IMG_TIFF_4_0 = { MimeMediaType.MIME_IMG_TIFF,
                TiffRevisions.REVISION_4, "" };
        formats.put(IMG_TIFF_4_0, list_IMG_TIFF_4_0);
        
        String[] list_IMG_TIFF_5_0 = { MimeMediaType.MIME_IMG_TIFF,
                TiffRevisions.REVISION_5, "" };
        formats.put(IMG_TIFF_5_0, list_IMG_TIFF_5_0);
        
        String[] list_IMG_TIFF_6_0 = { MimeMediaType.MIME_IMG_TIFF,
                TiffRevisions.REVISION_6, "" };
        formats.put(IMG_TIFF_6_0, list_IMG_TIFF_6_0);
        
        String[] list_TXT_HTML_4_0 = { MimeMediaType.MIME_TXT_HTML_4_0 , "", ""};
        formats.put(TXT_HTML_4_0, list_TXT_HTML_4_0);
        
        String[] list_TXT_HTML_4_0_1 = { MimeMediaType.MIME_TXT_HTML_4_0_1 , "", ""};
        formats.put(TXT_HTML_4_0_1, list_TXT_HTML_4_0_1);
        
        String[] list_TXT_PLAIN = { MimeMediaType.MIME_TXT_PLAIN,
                "", "" };
        formats.put(TXT_PLAIN, list_TXT_PLAIN);
        
        String[] list_TXT_PRO = { MimeMediaType.MIME_APP_PRO , "", ""};
        formats.put(APP_PRO, list_TXT_PRO);
        
        String[] list_TXT_CSV = { MimeMediaType.MIME_TXT_CSV , "", ""};
        formats.put(TXT_CSV, list_TXT_CSV);
        
        String[] list_TXT_RTF = { MimeMediaType.MIME_TXT_RTF , "", ""};
        formats.put(TXT_RTF, list_TXT_RTF);
        
        String[] list_TXT_SGML = { MimeMediaType.MIME_TXT_SGML , "", ""};
        formats.put(TXT_SGML, list_TXT_SGML);
        
        String[] list_VIDEO_AVI_1_0 = { MimeMediaType.MIME_VIDEO_AVI,
        		AviFile.VERSION_1_0, "" };
        formats.put(VIDEO_AVI_1_0, list_VIDEO_AVI_1_0);
        
        String[] list_VIDEO_MPEG = { MimeMediaType.MIME_VIDEO_MPEG , "", ""};
        formats.put(VIDEO_MPEG, list_VIDEO_MPEG);
        
        String[] list_VIDEO_MS_VIDEO = { MimeMediaType.MIME_VIDEO_MS_VIDEO , "", ""};
        formats.put(VIDEO_MS_VIDEO, list_VIDEO_MS_VIDEO);
        
        String[] list_VIDEO_QUICKTIME = { MimeMediaType.MIME_VIDEO_QUICKTIME,
        		QuickTime.VERSION_NA, "" };
        formats.put(VIDEO_QUICKTIME, list_VIDEO_QUICKTIME);
                
    }
    
    /**
     * Determines the archive's code for a format, given
     * its media type and optionally a media type version
     * and/or media type variation. None of these arguments
     * can be null (empty strings are fine) or a 
     * <code>FatalException</code> will be thrown.
     * 
     * @param mType media type
     * @param mVersion media type version
     * @param variation media type variation
     * @throws FatalException
     * @return the format code
     */
    public static String getFormatCode(String mType,
            String mVersion, String variation) 
    	throws FatalException {
        
        String methodName = "getFormatCode(String)";
        
        // see that the args are non-null
        if (mType == null || mVersion == null || variation == null){
            Informer.getInstance().fail(CLASSNAME,
                    methodName,
                    "illegal argument",
                    "mType: " + mType + " mVersion: " + mVersion +
                    " variation: " + variation,
                    new FatalException("Must be non-null."));
        }
        
        // construct the format codes set if not already
        // done
        if (formats == null) {
            buildFormats();
        }
        
        String code = null;
        
        for (Enumeration e = formats.keys(); e.hasMoreElements();){
            String nextCode = (String) e.nextElement();
            String[] chars = (String[]) formats.get(nextCode);
            
            String mediaTandS = chars[0];
            String vers = chars[1];
            String var = chars[2];
            
            if (mediaTandS.equals(mType) && vers.equals(mVersion) &&
                    var.equals(variation)) {
                // we've found the correct format code
                code = nextCode;
                break;
            }
        }
        
        // fail if we don't have a format code for this combination
        if (code == null) {
            Informer.getInstance().fail(CLASSNAME,
                    methodName,
                    "Format codes error",
                    "mType: " + mType + " mVersion: " + mVersion +
                    " variation: " + variation,
                    new FatalException("No format code for this combination"));
        }
        
        return code;
    }
    /**
     * get the associated mime type for the format code.
     * @param format
     * @return mime type
     */
    public static String getMimeTypeInFormat(String format) {
    	String mType = MimeMediaType.MIME_APP_UNK;
        // construct the format codes set if not already
        // done
        if (formats == null) {
            buildFormats();
        }
        
        String[] chars = formats.get(format);
    
        if (chars != null) {// an indentifiable format
        	mType = chars[0];
        }
        return mType;
    }
    
    /**
     * Determines if the format code is valid.
     * 
     * @param format format code
     * @return whether or not the code is valid
     */
    public static boolean isValidFormat(String format) {
    	boolean isValid = false;
    	
    	if (format == null || format.equals("")){
    		return false;
    	}
    	
        // construct the format codes set if not already
        // done
        if (formats == null) {
            buildFormats();
        }
        
        for (Enumeration e = formats.keys(); e.hasMoreElements();){
            String nextCode = (String) e.nextElement();
            if (format.equals(nextCode)){
            	isValid = true;
            	break;
            }
        }
    	
    	return isValid;
    }
    
    /**
     * Test driver.
     * 
     * @param args not used
     * @throws FatalException
     */
    public static void main(String[] args) throws FatalException {
        System.out.println("Format code for a file with mime type " +
                MimeMediaType.MIME_APP_PDF + " and version " + Pdf.VERSION_1_2 +
                ": " + getFormatCode(MimeMediaType.MIME_APP_PDF,
                        Pdf.VERSION_1_2, ""));
        
        System.out.println("Is valid (APP_PDF_1.0): " + 
        		isValidFormat(APP_PDF_1_0));
    }
}
