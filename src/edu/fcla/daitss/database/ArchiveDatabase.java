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
 * Created on Oct 5, 2004
 */
package edu.fcla.daitss.database;

import java.lang.reflect.Field;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

/**
 * ArchiveDatabase stores the database table and field names. The naming
 * conventions for this class are important because some methods in this class
 * rely on this naming scheme. The naming conventions are (substituting the
 * database table name for <TABLE>and the database field name for <FIELD>):
 * 
 * Table name variable prefix: TABLE_<TABLE>
 * Column name variable prefix: COL_<TABLE>_<FIELD>
 * 
 * Note that some of the variable names were shortened because they were
 * too long so they don't follow the convention described above exactly.
 * For ex: 
 * COL_DATA_FILE_FORMAT_ATT_FORMAT_ATT
 * instead of:
 * COL_DATA_FILE_FORMAT_ATTRIBUTE_FORMAT_ATTRIBUTE
 * 
 */
public class ArchiveDatabase {

    /**
     * Fully-qualified name for this class. To be used for Informer calls from
     * within static methods.
     */
    private static String CLASSNAME = "edu.fcla.daitss.database.ArchiveDatabase";

    /**
     * ACCOUNT.CODE
     */
    public static final String COL_ACCOUNT_CODE = "CODE";
    
    /**
     * ACCOUNT_PROJECT.ACCOUNT
     */
    public static final String COL_ACCOUNT_PROJECT_ACCOUNT = "ACCOUNT";
    
    /**
     * ACCOUNT_PROJECT.ID
     */
    public static final String COL_ACCOUNT_PROJECT_ID = "ID";
    
    /**
     * ACCOUNT_PROJECT.PROJECT
     */
    public static final String COL_ACCOUNT_PROJECT_PROJECT = "PROJECT";

    /**
     * ACCOUNT.REPORT_EMAIL
     */
    public static final String COL_ACCOUNT_REPORT_EMAIL = "REPORT_EMAIL";
    
    /**
	 * ACCOUNT.TECH_CONTACT
	 */
	public static final String COL_ACCOUNT_TECH_CONTACT = "TECH_CONTACT";

    /**
     * ADMIN.ACCOUNT_PROJECT
     */
    public static final String COL_ADMIN_ACCOUNT_PROJECT = "ACCOUNT_PROJECT";

    /**
     * ADMIN.INGEST_TIME
     */
    public static final String COL_ADMIN_INGEST_TIME = "INGEST_TIME";

    /**
     * ADMIN.OID
     */
    public static final String COL_ADMIN_OID = "OID";

    /**
     * ADMIN.ACCOUNT_PROJECT
     */
    public static final String COL_ADMIN_SUB_ACCOUNT = "SUB_ACCOUNT";
    
    /**
     * ARCHIVE_LOGIC.ACCOUNT_PROJECT
     */
    public static final String COL_ARCHIVE_LOGIC_ACCOUNT_PROJECT = "ACCOUNT_PROJECT";
    
    /**
     * ARCHIVE_LOGIC.END_DATE
     */
    public static final String COL_ARCHIVE_LOGIC_END_DATE = "END_DATE";
        
    /**
     * ARCHIVE_LOGIC.MEDIA_TYPE
     */
    public static final String COL_ARCHIVE_LOGIC_MEDIA_TYPE = "MEDIA_TYPE";
    
    /**
     * ARCHIVE_LOGIC.PRES_LEVEL
     */
    public static final String COL_ARCHIVE_LOGIC_PRES_LEVEL = "PRES_LEVEL";
    
    /**
     * ARCHIVE_LOGIC.START_DATE
     */
    public static final String COL_ARCHIVE_LOGIC_START_DATE = "START_DATE";
    /**
     * AVI_FILE.COPYRIGHTED
     */
    public static final String COL_AVI_FILE_COPYRIGHTED = "COPYRIGHTED";  

    /**
     * AVI_FILE.DFID
     */
    public static final String COL_AVI_FILE_DFID = "DFID";
    /**
     * AVI_FILE.DURATION
     */
    public static final String COL_AVI_FILE_DURATION = "DURATION";   
    /**
     * AVI_FILE.HAS_INDEX
     */
    public static final String COL_AVI_FILE_HAS_INDEX = "HAS_INDEX";   
    
    /**
     * AVI_FILE.INITIAL_FRAMES
     */
    public static final String COL_AVI_FILE_INITIAL_FRAMES = "INITIAL_FRAMES";   
    
    /**
     * AVI_FILE.IS_INTERLEAVED
     */
    public static final String COL_AVI_FILE_IS_INTERLEAVED = "IS_INTERLEAVED";  
    
    /**
     * AVI_FILE.MAX_DATA_RATE
     */
    public static final String COL_AVI_FILE_MAX_DATA_RATE = "MAX_DATA_RATE";    
    /**
     * AVI_FILE.MUST_USE_INDEX
     */
    public static final String COL_AVI_FILE_MUST_USE_INDEX = "MUST_USE_INDEX";   
    
    /**
     * AVI_FILE.NO_OF_STREAMS
     */
    public static final String COL_AVI_FILE_NO_OF_STREAMS = "NO_OF_STREAMS";    
    
    /**
     * AVI_FILE.TOTAL_FRAMES
     */
    public static final String COL_AVI_FILE_TOTAL_FRAMES = "TOTAL_FRAMES";    
    /**
     * AVI_FILE.WAS_CAPTURED
     */
    public static final String COL_AVI_FILE_WAS_CAPTURED = "WAS_CAPTURED";  
    
    /**
     * BITSTEAM_BS_PROFILE.BS_PROFILE
     */
    public static final String COL_BITSTEAM_BS_PROFILE_BS_PROFILE = "BS_PROFILE";
    
    /**
     * BITSTEAM_BS_PROFILE.BSID
     */
    public static final String COL_BITSTEAM_BS_PROFILE_BSID = "BSID";
    
    /**
     * BITSTREAM.BS_TABLE
     */
    public static final String COL_BITSTREAM_BS_TABLE = "BS_TABLE";

    /**
     * BITSTREAM.BSID
     */
    public static final String COL_BITSTREAM_BSID = "BSID";

    /**
     * BITSTREAM.COMPRESSION
     */
    public static final String COL_BITSTREAM_COMPRESSION = "COMPRESSION";

    /**
     * BITSTREAM.DFID
     */
    public static final String COL_BITSTREAM_DFID = "DFID";

    /**
     * BITSTREAM.LOCATION
     */
    public static final String COL_BITSTREAM_LOCATION = "LOCATION";

    /**
     * BITSTREAM.LOCATION_TYPE
     */
    public static final String COL_BITSTREAM_LOCATION_TYPE = "LOCATION_TYPE";

    /**
     * BITSTREAM.ROLE
     */
    public static final String COL_BITSTREAM_ROLE = "ROLE";

    /**
     * BITSTREAM.SEQUENCE
     */
    public static final String COL_BITSTREAM_SEQUENCE = "SEQUENCE";
    
    /**
     * BS_AUDIO.BSID
     */
    public static final String COL_BS_AUDIO_BSID = "BSID";
 
    /**
     * BS_AUDIO.DFID
     */
    public static final String COL_BS_AUDIO_DFID = "DFID";
    
    /**
     * BS_AUDIO.DATA_QUALITY
     */
    public static final String COL_BS_AUDIO_DATA_QUALITY = "DATA_QUALITY";

    /**
     * BS_AUDIO.DATA_RATE
     */
    public static final String COL_BS_AUDIO_DATA_RATE = "DATA_RATE";

    /**
     * BS_AUDIO.ENABLED
     */
    public static final String COL_BS_AUDIO_ENABLED = "ENABLED";

    /**
     * BS_AUDIO.ENCODING
     */
    public static final String COL_BS_AUDIO_ENCODING = "ENCODING";
    
    /**
     * BS_AUDIO.FRAME_RATE
     */
    public static final String COL_BS_AUDIO_FRAME_RATE = "FRAME_RATE";
    
    /**
     * BS_AUDIO.FRAME_SIZE
     */
    public static final String COL_BS_AUDIO_FRAME_SIZE = "FRAME_SIZE";
    
    /**
     * BS_AUDIO.LENGTH
     */
    public static final String COL_BS_AUDIO_LENGTH = "LENGTH";
    
    /**
     * BS_AUDIO.NO_OF_CHANNELS
     */
    public static final String COL_BS_AUDIO_NO_OF_CHANNELS = "NO_OF_CHANNELS";
    
    /**
     * BS_AUDIO.BITRATE_MODE
     */
    public static final String COL_BS_AUDIO_BITRATE_MODE = "BITRATE_MODE";

    /**
     * BS_AUDIO.SAMPLE_RATE
     */
    public static final String COL_BS_AUDIO_SAMPLE_RATE = "SAMPLE_RATE";
    
    /**
     * BS_AUDIO.SAMPLE_SIZE
     */
    public static final String COL_BS_AUDIO_SAMPLE_SIZE = "SAMPLE_SIZE";

    /**
     * BS_AUDIO.BS_TABLE
     */
    public static final String COL_BS_AUDIO_BS_TABLE = "BS_TABLE";
    
    
    /**
     * BS_AUDIO_WAVE.BSID
     */
    public static final String COL_BS_AUDIO_WAVE_BSID = "BSID";
 
    /**
     * BS_AUDIO_WAVE.DFID
     */
    public static final String COL_BS_AUDIO_WAVE_DFID = "DFID";
    
    /**
     * BS_AUDIO_WAVE.AVE_BYTES_PER_SEC
     */
    public static final String COL_BS_AUDIO_WAVE_AVE_BYTES_PER_SEC = "AVE_BYTES_PER_SEC";

    /**
     * BS_AUDIO_WAVE.BLOCK_ALIGN
     */
    public static final String COL_BS_AUDIO_WAVE_BLOCK_ALIGN = "BLOCK_ALIGN";

    /**
     * BS_AUDIO_WAVE.VALID_BITS_PER_SAMPLE
     */
    public static final String COL_BS_AUDIO_WAVE_VALID_BITS_PER_SAMPLE = "VALID_BITS_PER_SAMPLE";

    /**
     * BS_AUDIO_WAVE.SAMPLES_PER_BLOCK
     */
    public static final String COL_BS_AUDIO_WAVE_SAMPLES_PER_BLOCK = "SAMPLES_PER_BLOCK";

    /**
     * BS_IMAGE.BITS_PER_SAMPLE
     */
    public static final String COL_BS_IMAGE_BITS_PER_SAMPLE = "BITS_PER_SAMPLE";

    /**
     * BS_IMAGE.BS_TABLE
     */
    public static final String COL_BS_IMAGE_BS_TABLE = "BS_TABLE";

    /**
     * BS_IMAGE.BSID
     */
    public static final String COL_BS_IMAGE_BSID = "BSID";

    /**
     * BS_IMAGE.COLOR_SPACE
     */
    public static final String COL_BS_IMAGE_COLOR_SPACE = "COLOR_SPACE";

    /**
     * BS_IMAGE.DFID
     */
    public static final String COL_BS_IMAGE_DFID = "DFID";

    /**
     * BS_IMAGE.EXTRA_SAMPLES
     */
    public static final String COL_BS_IMAGE_EXTRA_SAMPLES = "EXTRA_SAMPLES";

    /**
     * BS_IMAGE.HAS_ICC_PROFILE
     */
    public static final String COL_BS_IMAGE_HAS_ICC_PROFILE = "HAS_ICC_PROFILE";

    /**
     * BS_IMAGE.HAS_INTERNAL_CLUT
     */
    public static final String COL_BS_IMAGE_HAS_INTERNAL_CLUT = "HAS_INTERNAL_CLUT";

    /**
     * BS_IMAGE.IMAGE_LENGTH
     */
    public static final String COL_BS_IMAGE_IMAGE_LENGTH = "IMAGE_LENGTH";

    /**
     * BS_IMAGE.IMAGE_WIDTH
     */
    public static final String COL_BS_IMAGE_IMAGE_WIDTH = "IMAGE_WIDTH";

    /**
     * BS_IMAGE_JPEG.BSID
     */
    public static final String COL_BS_IMAGE_JPEG_BSID = "BSID";

    /**
     * BS_IMAGE_JPEG.DFID
     */
    public static final String COL_BS_IMAGE_JPEG_DFID = "DFID";

    /**
     * BS_IMAGE_JPEG.JPEG_PROCESS
     */
    public static final String COL_BS_IMAGE_JPEG_JPEG_PROCESS = "JPEG_PROCESS";

    /**
     * BS_IMAGE_JPEG.PIXEL_ASPECT_RATIO
     */
    public static final String COL_BS_IMAGE_JPEG_PIXEL_ASPECT_RATIO = "PIXEL_ASPECT_RATIO";

    /**
     * BS_IMAGE_JPEG2000.BSID
     */
    public static final String COL_BS_IMAGE_JPEG2000_BSID = "BSID";

    /**
     * BS_IMAGE_JPEG2000.DFID
     */
    public static final String COL_BS_IMAGE_JPEG2000_DFID = "DFID";

    /**
     * BS_IMAGE_JPEG2000.HAS_ROI
     */
    public static final String COL_BS_IMAGE_JPEG2000_HAS_ROI = "HAS_ROI";
    
    /**
     * BS_IMAGE_JPEG2000.PROG_ORDER
     */
    public static final String COL_BS_IMAGE_JPEG2000_PROG_ORDER = "PROG_ORDER";
    
    /**
     * BS_IMAGE_JPEG2000.NUM_TILES
     */
    public static final String COL_BS_IMAGE_JPEG2000_NUM_TILES = "NUM_TILES";
    
    /**
     * BS_IMAGE_JPEG2000.TILE_LENGTH
     */
    public static final String COL_BS_IMAGE_JPEG2000_TILE_LENGTH = "TILE_LENGTH";
    
    /**
     * BS_IMAGE_JPEG2000.TILE_WIDTH
     */
    public static final String COL_BS_IMAGE_JPEG2000_TILE_WIDTH = "TILE_WIDTH";
    
    /**
     * BS_IMAGE_JPEG2000.WAVELET_TRANF_TYPE
     */
    public static final String COL_BS_IMAGE_JPEG2000_WAVELET_TRANF_TYPE = "WAVELET_TRANF_TYPE";
    
    /**
     * BS_IMAGE.NUM_COMPONENTS
     */
    public static final String COL_BS_IMAGE_NUM_COMPONENTS = "NUM_COMPONENTS";

    /**
     * BS_IMAGE.ORIENTATION
     */
    public static final String COL_BS_IMAGE_ORIENTATION = "ORIENTATION";

    /**
     * BS_IMAGE.RES_HORZ
     */
    public static final String COL_BS_IMAGE_RES_HORZ = "RES_HORZ";

    /**
     * BS_IMAGE.RES_UNIT
     */
    public static final String COL_BS_IMAGE_RES_UNIT = "RES_UNIT";

    /**
     * BS_IMAGE.RES_VERT
     */
    public static final String COL_BS_IMAGE_RES_VERT = "RES_VERT";

    /**
     * BS_IMAGE.SAMPLING_HOR
     */
    public static final String COL_BS_IMAGE_SAMPLING_HOR = "SAMPLING_HOR";

    /**
     * BS_IMAGE.SAMPLING_VER
     */
    public static final String COL_BS_IMAGE_SAMPLING_VER = "SAMPLING_VER";

    /**
     * BS_IMAGE_TIFF.BSID
     */
    public static final String COL_BS_IMAGE_TIFF_BSID = "BSID";

    /**
     * BS_IMAGE_TIFF.DFID
     */
    public static final String COL_BS_IMAGE_TIFF_DFID = "DFID";

    /**
     * BS_IMAGE_TIFF.HAS_CHROMATICITIES
     */
    public static final String COL_BS_IMAGE_TIFF_HAS_CHROMS = "HAS_CHROMATICITIES";

    /**
     * BS_IMAGE_TIFF.MAX_STRIP_BYTES
     */
    public static final String COL_BS_IMAGE_TIFF_MAX_STRIP_BYTES = "MAX_STRIP_BYTES";

    /**
     * BS_IMAGE_TIFF.MAX_TILE_BYTES
     */
    public static final String COL_BS_IMAGE_TIFF_MAX_TILE_BYTES = "MAX_TILE_BYTES";

    /**
     * BS_IMAGE_TIFF.NUM_STRIPS
     */
    public static final String COL_BS_IMAGE_TIFF_NUM_STRIPS = "NUM_STRIPS";

    /**
     * BS_IMAGE_TIFF.NUM_TILES
     */
    public static final String COL_BS_IMAGE_TIFF_NUM_TILES = "NUM_TILES";

    /**
     * BS_IMAGE_TIFF.PLANAR_CONFIG
     */
    public static final String COL_BS_IMAGE_TIFF_PLANAR_CONFIG = "PLANAR_CONFIG";

    /**
     * BS_IMAGE_TIFF.ROWS_PER_STRIP
     */
    public static final String COL_BS_IMAGE_TIFF_ROWS_PER_STRIP = "ROWS_PER_STRIP";

    /**
     * BS_IMAGE_TIFF.STORAGE_SEGMENT
     */
    public static final String COL_BS_IMAGE_TIFF_STOR_SEGMENT = "STORAGE_SEGMENT";

    /**
     * BS_IMAGE_TIFF.TILE_LENGTH
     */
    public static final String COL_BS_IMAGE_TIFF_TILE_LENGTH = "TILE_LENGTH";

 
    /**
     * BS_IMAGE_TIFF.TILE_WIDTH
     */
    public static final String COL_BS_IMAGE_TIFF_TILE_WIDTH = "TILE_WIDTH";

    /**
     * BS_MARKUP.BSID
     */
    public static final String COL_BS_MARKUP_BSID = "BSID";

    /**
     * BS_MARKUP.CHARSET
     */
    public static final String COL_BS_MARKUP_CHARSET = "CHARSET";

    /**
     * BS_MARKUP.CHARSET_ORIGIN
     */
    public static final String COL_BS_MARKUP_CHARSET_ORIGIN = "CHARSET_ORIGIN";

    /**
     * BS_MARKUP.DFID
     */
    public static final String COL_BS_MARKUP_DFID = "DFID";

    /**
     * BS_MARKUP.MARKUP_BASIS
     */
    public static final String COL_BS_MARKUP_MARKUP_BASIS = "MARKUP_BASIS";

    /**
     * BS_MARKUP.MARKUP_LANGUAGE
     */
    public static final String COL_BS_MARKUP_MARKUP_LANG = "MARKUP_LANGUAGE";

    /**
     * BS_MARKUP.SCHEMA_DFID
     */
    public static final String COL_BS_MARKUP_SCHEMA_DFID = "SCHEMA_DFID";

    /**
     * BS_MARKUP.SCHEMA_TYPE
     */
    public static final String COL_BS_MARKUP_SCHEMA_TYPE = "SCHEMA_TYPE";

    /**
     * BS_MARKUP.VALID
     */
    public static final String COL_BS_MARKUP_VALID = "VALID";

    /**
     * BS_PDF_ACTION.BSID
     */
    public static final String COL_BS_PDF_ACTION_BSID = "BSID";

    /**
     * BS_PDF_ACTION.PDF_ACTION
     */
    public static final String COL_BS_PDF_ACTION_PDF_ACTION = "PDF_ACTION";

    /**
     * BS_PDF_ANNOTATION.PDF_ANNOTATION
     */
    public static final String COL_BS_PDF_ANNOT_PDF_ANNOT = "PDF_ANNOTATION";

    /**
     * BS_PDF_ANNOTATION.BSID
     */
    public static final String COL_BS_PDF_ANNOTATION_BSID = "BSID";

    /**
     * BS_PDF.BSID
     */
    public static final String COL_BS_PDF_BSID = "BSID";

    /**
     * BS_PDF.DFID
     */
    public static final String COL_BS_PDF_DFID = "DFID";

    /**
     * BS_PDF_ANNOTATION.BSID
     */
    public static final String COL_BS_PDF_FILTER_BSID = "BSID";

    /**
     * BS_PDF_FILTER.PDF_FILTER
     */
    public static final String COL_BS_PDF_FILTER_PDF_FILTER = "PDF_FILTER";

    /**
     * BS_PDF.HAS_IMAGES
     */
    public static final String COL_BS_PDF_HAS_IMAGES = "HAS_IMAGES";
    
    /**
     * BS_PDF.HAS_METADATA
     */
    public static final String COL_BS_PDF_HAS_METADATA = "HAS_METADATA";
    
    /**
     * BS_PDF.HAS_OUTLINE
     */
    public static final String COL_BS_PDF_HAS_OUTLINE = "HAS_OUTLINE";
    
    /**
     * BS_PDF.NATL_LANG
     */
    public static final String COL_BS_PDF_NATL_LANG = "NATL_LANG";
    
    /**
     * BS_PDF.NUM_PAGES
     */
    public static final String COL_BS_PDF_NUM_PAGES = "NUM_PAGES";
    
    /**
     * BS_PDF.NONSTANDARD_EMBEDDED_FONTS
     */
    public static final String COL_BS_PDF_NONSTANDARD_EMBEDDED_FONTS = "NONSTANDARD_EMBEDDED_FONTS";
    
    /**
     * BS_PDF.NONSTANDARD_UNEMBEDDED_FONTS
     */
    public static final String COL_BS_PDF_NONSTANDARD_UNEMBEDDED_FONTS = "NONSTANDARD_UNEMBEDDED_FONTS";
    
    /**
     * BS_TEXT.BSID
     */
    public static final String COL_BS_TEXT_BSID = "BSID";

    /**
     * BS_TEXT.CHARSET
     */
    public static final String COL_BS_TEXT_CHARSET = "CHARSET";

    /**
     * BS_TEXT.CHARSET_ORIGIN
     */
    public static final String COL_BS_TEXT_CHARSET_ORIGIN = "CHARSET_ORIGIN";
    
    /**
     * BS_TEXT.DFID
     */
    public static final String COL_BS_TEXT_DFID = "DFID";
    
    /**
     * BS_TEXT.LINE_BREAK
     */
    public static final String COL_BS_TEXT_LINE_BREAK = "LINE_BREAK";

    /**
     * BS_TEXT.NATL_LANG
     */
    public static final String COL_BS_TEXT_NATL_LANG = "NATL_LANG";

    /**
     * BS_TEXT.NUM_LINES
     */
    public static final String COL_BS_TEXT_NUM_LINES = "NUM_LINES";
    
    /**
     * BS_CSV.BSID
     */
    public static final String COL_BS_CSV_BSID = "BSID";
    
    /**
     * BS_CSV.DFID
     */
    public static final String COL_BS_CSV_DFID = "DFID";
    
    /**
     * BS_CSV.NUM_ROWS
     */
    public static final String COL_BS_CSV_NUM_ROWS = "NUM_ROWS";
    
    /**
     * BS_CSV.NUM_COLUMNS
     */
    public static final String COL_BS_CSV_NUM_COLUMNS = "NUM_COLUMNS";
    
    /**
     * BS_VIDEO.BITRATE_MODE
     */
    public static final String COL_BS_VIDEO_BITRATE_MODE = "BITRATE_MODE";

    /**
     * BS_VIDEO.BITS_PER_PIXEL
     */
    public static final String COL_BS_VIDEO_BITS_PER_PIXEL = "BITS_PER_PIXEL";
    
    /**
     * BS_VIDEO.BSID
     */
    public static final String COL_BS_VIDEO_BSID = "BSID";
    
    /**
     * BS_VIDEO.DATA_QUALITY
     */
    public static final String COL_BS_VIDEO_DATA_QUALITY = "DATA_QUALITY";
    
    /**
     * BS_VIDEO.DATA_RATE
     */
    public static final String COL_BS_VIDEO_DATA_RATE = "DATA_RATE";

    /**
     * BS_VIDEO.DFID
     */
    public static final String COL_BS_VIDEO_DFID = "DFID";
    
    /**
     * BS_VIDEO.ENABLED
     */
    public static final String COL_BS_VIDEO_ENABLED = "ENABLED";

    /**
     * BS_VIDEO.ENCODING
     */
    public static final String COL_BS_VIDEO_ENCODING = "ENCODING";
    /**
     * BS_VIDEO.FRAME_HEIGHT
     */
    public static final String COL_BS_VIDEO_FRAME_HEIGHT = "FRAME_HEIGHT";
    
    /**
     * BS_VIDEO.FRAME_RATE
     */
    public static final String COL_BS_VIDEO_FRAME_RATE = "FRAME_RATE";
    /**
     * BS_VIDEO.FRAME_WIDTH 
     */
    public static final String COL_BS_VIDEO_FRAME_WIDTH = "FRAME_WIDTH";
    
    /**
     * BS_VIDEO.RES_UNIT
     */
    public static final String COL_BS_VIDEO_RES_UNIT = "RES_UNIT";
    
    /**
     * BS_VIDEO.RES_HORZ
     */
    public static final String COL_BS_VIDEO_RES_HORZ = "RES_HORZ";
    /**
     * BS_VIDEO.RES_VERT
     */
    public static final String COL_BS_VIDEO_RES_VERT = "RES_VERT";
    /**
     * BS_VIDEO.LENGTH
     */
    public static final String COL_BS_VIDEO_LENGTH = "LENGTH";

    /**
     * BS_VIDEO.BS_TABLE
     */
    public static final String COL_BS_VIDEO_BS_TABLE = "BS_TABLE";
    
    /**
	 * CONTACT.EMAIL
	 */
	public static final String COL_CONTACT_EMAIL = "EMAIL";

    /**
	 * CONTACT.ID
	 */
	public static final String COL_CONTACT_ID = "ID";

    /**
     * DATA_FILE.BYTE_ORDER
     */
    public static final String COL_DATA_FILE_BYTE_ORDER = "BYTE_ORDER";

    /**
     * DATA_FILE.CAN_DELETE
     */
    public static final String COL_DATA_FILE_CAN_DELETE = "CAN_DELETE";

    /**
     * DATA_FILE.CONTENT_TYPE
     */
    public static final String COL_DATA_FILE_CONTENT_TYPE = "CONTENT_TYPE";

    /**
     * DATA_FILE.CREATE_DATE
     */
    public static final String COL_DATA_FILE_CREATE_DATE = "CREATE_DATE";

    /**
     * DATA_FILE.CREATOR_PROG
     */
    public static final String COL_DATA_FILE_CREATOR_PROG = "CREATOR_PROG";

    /**
     * DATA_FILE.DFID
     */
    public static final String COL_DATA_FILE_DFID = "DFID";

    /**
     * DATA_FILE.DIP_VERSION
     */
    public static final String COL_DATA_FILE_DIP_VERSION = "DIP_VERSION";

	/**
     * DATA_FILE.FILE_COPY_DATE
     */
    public static final String COL_DATA_FILE_FILE_COPY_DATE = "FILE_COPY_DATE";

	/**
     * DATA_FILE.FILE_EXT
     */
    public static final String COL_DATA_FILE_FILE_EXT = "FILE_EXT";
    
    /**
     * DATA_FILE.FILE_TITLE
     */
    public static final String COL_DATA_FILE_FILE_TITLE = "FILE_TITLE";

    /**
     * DATA_FILE.FORMAT
     */
    public static final String COL_DATA_FILE_FORMAT = "FORMAT";

    /**
     * DATA_FILE_FORMAT_ATTRIBUTE.DFID
     */
    public static final String COL_DATA_FILE_FORMAT_ATT_DFID = "DFID";

    /**
     * DATA_FILE_FORMAT_ATTRIBUTE.FORMAT_ATTRIBUTE
     */
    public static final String COL_DATA_FILE_FORMAT_ATT_FORMAT_ATT = "FORMAT_ATTRIBUTE";

    /**
     * DATA_FILE.IEID
     */
    public static final String COL_DATA_FILE_IEID = "IEID";

    /**
     * DATA_FILE.IS_GLOBAL
     */
    public static final String COL_DATA_FILE_IS_GLOBAL = "IS_GLOBAL";

    /**
     * DATA_FILE.IS_OBSOLETE
     */
    public static final String COL_DATA_FILE_IS_OBSOLETE = "IS_OBSOLETE";

    /**
     * DATA_FILE.IS_ROOT
     */
    public static final String COL_DATA_FILE_IS_ROOT = "IS_ROOT";

    /**
     * DATA_FILE.ORIG_URI
     */
    public static final String COL_DATA_FILE_ORIG_URI = "ORIG_URI";

    /**
     * DATA_FILE.ORIGIN
     */
    public static final String COL_DATA_FILE_ORIGIN = "ORIGIN";
    
    /**
     * DATA_FILE.PACKAGE_PATH
     */
    public static final String COL_DATA_FILE_PACKAGE_PATH = "PACKAGE_PATH";
    
    /**
     * DATA_FILE.PRES_LEVEL
     */
    public static final String COL_DATA_FILE_PRES_LEVEL = "PRES_LEVEL";
    
    /**
     * DATA_FILE.ROLE
     */
    public static final String COL_DATA_FILE_ROLE = "ROLE";

    /**
     * DATA_FILE_SEVERE_ELEMENT.DFID
     */
    public static final String COL_DATA_FILE_SEV_ELEM_DFID = "DFID";

    /**
     * DATA_FILE_SEVERE_ELEMENT.SEVERE_ELEMENT
     */
    public static final String COL_DATA_FILE_SEV_ELEM_SEV_ELEM = "SEVERE_ELEMENT";

    /**
     * DATA_FILE.SIZE
     */
    public static final String COL_DATA_FILE_SIZE = "SIZE";
    
	/**
     * DATA_VERIFICATION.DATE_TIME
     */
    public static final String COL_DATA_VERIFICATION_DATE_TIME = "DATE_TIME";

	/**
     * DATA_VERIFICATION.ID
     */
    public static final String COL_DATA_VERIFICATION_ID = "ID";

	/**
     * DATA_VERIFICATION.OID
     */
    public static final String COL_DATA_VERIFICATION_OID = "OID";

    /**
     * DATA_VERIFICATION.OUTCOME
     */
    public static final String COL_DATA_VERIFICATION_OUTCOME = "OUTCOME";

    
    /**
     * DATA_VERIFICATION.TYPE
     */
    public static final String COL_DATA_VERIFICATION_TYPE = "TYPE";

    /**
     * DISTRIBUTED.BROKEN_LINKS
     */
    public static final String COL_DISTRIBUTED_BROKEN_LINKS = "BROKEN_LINKS";

    /**
     * DISTRIBUTED.IGNORED_LINKS
     */
    public static final String COL_DISTRIBUTED_IGNORED_LINKS = "IGNORED_LINKS";

    /**
     * DISTRIBUTED.LINKS
     */
    public static final String COL_DISTRIBUTED_LINKS = "LINKS";
    
    /**
     * DISTRIBUTED.PARENT
     */
    public static final String COL_DISTRIBUTED_PARENT = "PARENT";

    /**
     * EVENT.DATE_TIME
     */
    public static final String COL_EVENT_DATE_TIME = "DATE_TIME";
   
    /**
     * EVENT.EVENT_PROCEDURE
     */
    public static final String COL_EVENT_EVENT_PROCEDURE = "EVENT_PROCEDURE";

    /**
     * EVENT.EVENT_TYPE
     */
    public static final String COL_EVENT_EVENT_TYPE = "EVENT_TYPE";
    
    /**
     * EVENT.ID
     */
    public static final String COL_EVENT_ID = "ID";

    /**
     * EVENT.NOTE
     */
    public static final String COL_EVENT_NOTE = "NOTE";
    
    /**
     * EVENT.OID
     */
    public static final String COL_EVENT_OID = "OID";
    
    /**
     * EVENT.OUTCOME
     */
    public static final String COL_EVENT_OUTCOME = "OUTCOME";
    
    /**
     * EVENT.REL_OID
     */
    public static final String COL_EVENT_REL_OID = "REL_OID";
    
    /**
     * GLOBAL_FILE.DFID
     */
    public static final String COL_GLOBAL_FILE_DFID = "DFID";
    
    /**
     * GLOBAL_FILE.USE_COUNT
     */
    public static final String COL_GLOBAL_FILE_USE_COUNT = "USE_COUNT";
    
    /**
     * INT_ENTITY.ENTITY_ID
     */
    public static final String COL_INT_ENTITY_ENTITY_ID = "ENTITY_ID";
    
    /**
     * INT_ENTITY.EXT_REC
     */
    public static final String COL_INT_ENTITY_EXT_REC = "EXT_REC";
    
    /**
     * INT_ENTITY.EXT_REC_TYPE
     */
    public static final String COL_INT_ENTITY_EXT_REC_TYPE = 
        "EXT_REC_TYPE";
    
    /**
     * INT_ENTITY_GLOBAL_FILE.DFID
     */
    public static final String COL_INT_ENTITY_GLOBAL_FILE_DFID = "DFID";
    
    /**
     * INT_ENTITY_GLOBAL_FILE.IEID
     */
    public static final String COL_INT_ENTITY_GLOBAL_FILE_IEID = "IEID";
    
    /**
     * INT_ENTITY.IEID
     */
    public static final String COL_INT_ENTITY_IEID = "IEID";
    
    /**
     * INT_ENTITY.ISSUE
     */
    public static final String COL_INT_ENTITY_ISSUE = "ISSUE";

    /**
     * INT_ENTITY.PACKAGE_NAME
     */
    public static final String COL_INT_ENTITY_PACKAGE_NAME = "PACKAGE_NAME";

    /**
     * INT_ENTITY.TITLE
     */
    public static final String COL_INT_ENTITY_TITLE = "TITLE";
    
    
    /**
     * INT_ENTITY.VOL
     */
    public static final String COL_INT_ENTITY_VOL = "VOL";
    
    /**
     * MESSAGE_DIGEST.CODE
     */
    public static final String COL_MESSAGE_DIGEST_CODE = "CODE";
    
    /**
     * MESSAGE_DIGEST.DFID
     */
    public static final String COL_MESSAGE_DIGEST_DFID = "DFID";
    
    /**
     * MESSAGE_DIGEST.ORIGIN
     */
    public static final String COL_MESSAGE_DIGEST_ORIGIN = "ORIGIN";
    
    /**
     * MESSAGE_DIGEST.VALUE
     */
    public static final String COL_MESSAGE_DIGEST_VALUE = "VALUE";
    
    /**
     * PROJECT.CODE
     */
    public static final String COL_PROJECT_CODE = "CODE";
    
    /**
     * QUICKTIME_FILE.DFID
     */
    public static final String COL_QUICKTIME_FILE_DFID = "DFID";
    /**
     * QUICKTIME_FILE.DURATION
     */
    public static final String COL_QUICKTIME_DURATION = "DURATION";
    /**
     * QUICKTIME_FILE.HAS_DISPLAY_MATRIX
     */
    public static final String COL_QUICKTIME_HAS_DISPLAY_MATRIX = "HAS_DISPLAY_MATRIX";
    /**
     * QUICKTIME_FILE.HAS_USERDATA
     */
    public static final String COL_QUICKTIME_HAS_USERDATA = "HAS_USERDATA";
    /**
     * QUICKTIME_FILE.HAS_COLORTABLE
     */
    public static final String COL_QUICKTIME_HAS_COLORTABLE = "HAS_COLORTABLE";
    /**
     * QUICKTIME_FILE.HAS_COMPRESSED_RESOURCE
     */
    public static final String COL_QUICKTIME_HAS_COMPRESSED_RESOURCE = "HAS_COMPRESSED_RESOURCE";
    
    /**
     * RELATIONSHIP.DFID_1
     */
    public static final String COL_RELATIONSHIP_DFID_1 = "DFID_1";
    
    /**
     * RELATIONSHIP.DFID_2
     */
    public static final String COL_RELATIONSHIP_DFID_2 = "DFID_2";
   
    /**
     * RELATIONSHIP.EVENT_ID
     */
    public static final String COL_RELATIONSHIP_EVENT_ID = "EVENT_ID";
    
    /**
     * RELATIONSHIP.REL_TYPE
     */
    public static final String COL_RELATIONSHIP_REL_TYPE = "REL_TYPE";

    /**
	 * REPORT.DATE
	 */
	public static final String COL_REPORT_DATE = "DATE";

    /**
	 * REPORT.DESCRIPTION
	 */
	public static final String COL_REPORT_DESCRIPTION = "DESCRIPTION";

    /**
	 * REPORT.ID
	 */
	public static final String COL_REPORT_ID = "ID";

    /**
     * REPORT_INT_ENTITY.ID
     */
    public static final String COL_REPORT_INT_ENTITY_ID = "ID";

    /**
     * REPORT_INT_ENTITY.INT_ENTITY 
     */
    public static final String COL_REPORT_INT_ENTITY_INT_ENTITY = "INT_ENTITY";
    
    /**
	 * REPORT_INT_ENTITY.REPORT
	 */
	public static final String COL_REPORT_INT_ENTITY_REPORT = "REPORT";
    
    /**
     * REPORT.TYPE
     */
    public static final String COL_REPORT_TYPE = "TYPE";

	/**
     * SUB_ACCOUNT.ACCOUNT
     */
    public static final String COL_SUB_ACCOUNT_ACCOUNT = "ACCOUNT";

    /**
     * SUB_ACCOUNT.CODE
     */
    public static final String COL_SUB_ACCOUNT_CODE = "CODE";

    /**
     * SUB_ACCOUNT.DESCRIPTION
     */
    public static final String COL_SUB_ACCOUNT_DESCRIPTION = "DESCRIPTION";    

    /**
     * SUB_ACCOUNT.ID
     */
    public static final String COL_SUB_ACCOUNT_ID = "ID";
    
    /**
     * TSM_STORAGE_DESC.FROM_NODE
     */
    //public static final String COL_STORAGE_DESC_FROM_NODE = "FROM_NODE";

    /**
     * WAVE_FILE.DFID
     */
    public static final String COL_WAVE_FILE_DFID = "DFID";
    /**
     * WAVE_FILE.HAS_FACT_CHUNK
     */
    public static final String COL_WAVE_FILE_HAS_FACT_CHUNK = "HAS_FACT_CHUNK";
    /**
     * WAVE_FILE.HAS_PLAYLIST_CHUNK
     */
    public static final String COL_WAVE_FILE_HAS_PLAYLIST_CHUNK = "HAS_PLAYLIST_CHUNK";
    /**
     * WAVE_FILE.HAS_LABEL_CHUNK
     */
    public static final String COL_WAVE_FILE_HAS_LABEL_CHUNK = "HAS_LABEL_CHUNK";
    /**
     * WAVE_FILE.HAS_NOTE_CHUNK
     */
    public static final String COL_WAVE_FILE_HAS_NOTE_CHUNK = "HAS_NOTE_CHUNK";
    /**
     * WAVE_FILE.HAS_LTXT_CHUNK
     */
    public static final String COL_WAVE_FILE_HAS_LTXT_CHUNK = "HAS_LTXT_CHUNK";
    /**
     * WAVE_FILE.HAS_FILE_CHUNK
     */
    public static final String COL_WAVE_FILE_HAS_FILE_CHUNK = "HAS_FILE_CHUNK";
    /**
     * WAVE_FILE.HAS_SAMPLE_CHUNK
     */
    public static final String COL_WAVE_FILE_HAS_SAMPLE_CHUNK = "HAS_SAMPLE_CHUNK";
    /**
     * WAVE_FILE.HAS_INSTRUMENT_CHUNK
     */
    public static final String COL_WAVE_FILE_HAS_INSTRUMENT_CHUNK = "HAS_INSTRUMENT_CHUNK";
    /**
     * WAVE_FILE.HAS_CUE_CHUNK
     */
    public static final String COL_WAVE_FILE_HAS_CUE_CHUNK = "HAS_CUE_CHUNK";
    /**
     * Account table
     */
    public static final String TABLE_ACCOUNT = "ACCOUNT";
    
    /**
     * Account_Project table
     */
    public static final String TABLE_ACCOUNT_PROJECT = "ACCOUNT_PROJECT";

    /**
     * Admin table
     */
    public static final String TABLE_ADMIN = "ADMIN";

    /**
     * Preservation Agreements table
     */
    public static final String TABLE_ARCHIVE_LOGIC = "ARCHIVE_LOGIC";

    /**
     * AVI_FILE table
     */
    public static final String TABLE_AVI_FILE = "AVI_FILE";
    
    /**
     * Bitstream Profile table
     */
    public static final String TABLE_BITSTEAM_BS_PROFILE = "BITSTREAM_BS_PROFILE";

    /**
     * Bitstream table
     */
    public static final String TABLE_BS = "BITSTREAM";
    
    /**
     * Bitstream audio table (applicable to all audio bitstreams)
     */
    public static final String TABLE_BS_AUDIO = "BS_AUDIO";
    
    /**
     * Bitstream wave audio table
     */
    public static final String TABLE_BS_AUDIO_WAVE = "BS_AUDIO_WAVE";
    
    /**
     * Bitstream image table (applicable to all raster image bitstreams)
     */
    public static final String TABLE_BS_IMAGE = "BS_IMAGE";
    
    /**
     * Bitstream image jpeg table
     */
    public static final String TABLE_BS_IMAGE_JPEG = "BS_IMAGE_JPEG";

    /**
     * Bitstream image jpeg 2000 table
     */
    public static final String TABLE_BS_IMAGE_JPEG2000 = "BS_IMAGE_JPEG2000";
    
    /**
     * Bitstream image tiff table
     */
    public static final String TABLE_BS_IMAGE_TIFF = "BS_IMAGE_TIFF";

    /**
     * Bitstream markup table
     */
    public static final String TABLE_BS_MARKUP = "BS_MARKUP";

    /**
     * Bitstream PDF document table
     */
    public static final String TABLE_BS_PDF = "BS_PDF";

    /**
     * Bitstream PDF document action table
     */
    public static final String TABLE_BS_PDF_ACTION = "BS_PDF_ACTION";

    /**
     * Bitstream PDF document annotation table
     */
    public static final String TABLE_BS_PDF_ANNOTATION = "BS_PDF_ANNOTATION";
    
    /**
     * Bitstream PDF document filter table
     */
    public static final String TABLE_BS_PDF_FILTER = "BS_PDF_FILTER";
    
    /**
     * Bitstream text table
     */
    public static final String TABLE_BS_TEXT = "BS_TEXT";

    /**
     * Bitstream CSV table
     */
    public static final String TABLE_BS_CSV = "BS_TEXT_CSV";
    
    /**
     * Bitstream video table (applicable to all video bitstreams)
     */
    public static final String TABLE_BS_VIDEO = "BS_VIDEO";
    
    /**
	 * Contacts table
	 */
	public static final String TABLE_CONTACT = "CONTACT";

    /**
     * File table (applicable to all physical files)
     */
    public static final String TABLE_DATA_FILE = "DATA_FILE";
	
	/**
     * Data file format attribute table
     */
    public static final String TABLE_DATA_FILE_FORMAT_ATT = 
        "DATA_FILE_FORMAT_ATTRIBUTE";

    /**
     * Data file severe elements table
     */
    public static final String TABLE_DATA_FILE_SEV_ELEM = "DATA_FILE_SEVERE_ELEMENT";

	/**
     * Data Verification table.
     */
    public static final String TABLE_DATA_VERIFICATION = "DATA_VERIFICATION";
    
    /**
     * Distributed table
     */
    public static final String TABLE_DISTRIBUTED = "DISTRIBUTED";
    
    /**
     * Events table
     */
    public static final String TABLE_EVENT = "EVENT";
    
    /**
     * AVI FILE table
     */
    public static final String TABLE_FILE_AVI = "FILE_AVI";
    
    /**
     * Global File table
     */
    public static final String TABLE_GLOBAL_FILE = "GLOBAL_FILE";
    
    /**
     * Intellectual Entity Table 
     */
    public static final String TABLE_INT_ENTITY = "INT_ENTITY";

    /**
     * Intellectual Entity Global File Table 
     */
    public static final String TABLE_INT_ENTITY_GLOBAL_FILE = "INT_ENTITY_GLOBAL_FILE";
    
    
    /**
     * Message_digest table
     */
    public static final String TABLE_MESSAGE_DIGEST = "MESSAGE_DIGEST";
    
    /**
     * Output request table
     */
    public static final String TABLE_OUTPUT_REQUEST = "OUTPUT_REQUEST";
    
    /**
     * Project table
     */
    public static final String TABLE_PROJECT = "PROJECT";

    /**
     * QuickTime file table
     */
    public static final String TABLE_QUICKTIME_FILE = "QUICKTIME_FILE";
    /**
     * Relationships table
     */
    public static final String TABLE_RELATIONSHIP = "RELATIONSHIP";

    /**
	 * Report table.
	 */
	public static final String TABLE_REPORT = "REPORT";

    /**
	 * Report Int Entity Relationship.
	 */
	public static final String TABLE_REPORT_INT_ENTITY = "REPORT_INT_ENTITY";



    /**
     * Sub Account table
     */
    public static final String TABLE_SUB_ACCOUNT = "SUB_ACCOUNT";
           
    // FOR ENUMERATED VALUES AS NEEDED
    /**
     * TSM storage description prep table
     */
    public static final String VALUE_RELATIONSHIP_REL_TYPE_CHILD_OF = 
        "CHILD_OF";

	/**
	 * Table SEVERE_ELEMENT
	 */
	public static final String TABLE_SEV_ELEM = "SEVERE_ELEMENT";

	/**
	 * SEVERE_ELEMENT.TYPE
	 */
	public static final String COL_SEV_ELEM_TYPE = "TYPE";

	/**
	 * SEVERE_ELEMENT.CODE
	 */
	public static final String COL_SEV_ELEM_CODE = "CODE";

	/**
	 * SEVERE_ELEMENT.DESCRIPTION
	 */
	public static final String COL_SEV_ELEM_DESCRIPTION = "DESCRIPTION";
    
	/**
	 * SEVERE_ELEMENT.REPORT
	 */
	public static final String COL_SEV_ELEM_REPORT = "REPORT";

	/**
     * Wave File table
     */
    public static final String TABLE_WAVE_FILE = "WAVE_FILE";

	/**
	 * OUTPUT_REQUEST.CAN_REQUEST_WITHDRAWAL
	 */
	public static final String COL_OUTPUT_REQUEST_CAN_REQUEST_WITHDRAWAL = "CAN_REQUEST_WITHDRAWAL";

	/**
	 * OUTPUT_REQUEST.CONTACT
	 */
	public static final String COL_OUTPUT_REQUEST_CONTACT = "CONTACT";

	/**
	 * OUTPUT_REQUEST.ACCOUNT
	 */
	public static final String COL_OUTPUT_REQUEST_ACCOUNT = "ACCOUNT";

	/**
	 * CONTACT.NAME
	 */
	public static final String COL_CONTACT_NAME = "NAME";

	/**
	 * OUTPUT_REQUEST.CAN_REQUEST_DISSEMINATION
	 */
	public static final String COL_OUTPUT_REQUEST_CAN_REQUEST_DISSEMINATION = "CAN_REQUEST_DISSEMINATION";

    public static final String TABLE_COPY = "COPY";

    public static final String COL_COPY_IEID = "IEID";

    public static final String COL_COPY_PATH = "PATH";

    public static final String TABLE_SILO = "SILO";

    public static final String COL_SILO_ID = "ID";

    public static final String COL_COPY_SILO = "SILO";

    public static final String COL_SILO_URL = "URL";

	public static final String COL_COPY_MD5 = "MD5";

	public static final String COL_SILO_ACTIVE = "ACTIVE";

	public static final String COL_SILO_RETIRED = "RETIRED";

	public static final String COL_COPY_ID = "ID";

	
    /**
     * Determines if a table name is a valid bitstream table name in this
     * archive's database. Does this by looking for this table as the value of a
     * member starting with "TABLE_BS_".
     * 
     * @param table
     *            database table name
     * @return whether this is a valid bitstream table name
     * @throws FatalException
     */
    public static boolean isValidBitstreamTable(String table)
            throws FatalException {
        String methodName = "isValidBitstreamTable(String)";

        boolean isValid = false, foundIt = false;

        if (table != null && !table.equals("")) {
            // dynamically make a list of all the fileds listed
            // in this class
            Field[] fields = new ArchiveDatabase().getClass().getFields();
            int i = 0;
            while (!foundIt && i < fields.length) {
                try {
                    // only want to consider the members that start with
                    // "TABLE_BS_"
                    if (fields[i].getName().startsWith("TABLE_BS_")
                            && ((String) fields[i].get(fields[i]))
                                    .equals(table)) {
                        foundIt = true;
                        isValid = true;
                    }
                    i++;
                } catch (IllegalAccessException e) {
                    Informer.getInstance().fail(CLASSNAME, methodName,
                            "Illegal access", "field",
                            new FatalException(e.getMessage()));
                }
            }

        }

        return isValid;
    }

    /**
     * Determines if a table name is a valid image bitstream table name in this
     * archive's database. Does this by looking for this table as the value of a
     * member starting with "TABLE_BS_IMAGE_".
     * 
     * @param table
     *            database table name
     * @return whether this is a valid bitstream table name
     * @throws FatalException
     */
    public static boolean isValidImageBitstreamTable(String table)
            throws FatalException {
        String methodName = "isValidImageBitstreamTable(String)";

        boolean isValid = false, foundIt = false;

        if (table != null && !table.equals("")) {
            // dynamically make a list of all the fields listed
            // in this class
            Field[] fields = new ArchiveDatabase().getClass().getFields();
            int i = 0;
            while (!foundIt && i < fields.length) {
                try {
                    // only want to consider the members that start with
                    // "TABLE_BS_IMAGE_"
                    if (fields[i].getName().startsWith("TABLE_BS_IMAGE_")
                            && ((String) fields[i].get(fields[i]))
                                    .equals(table)) {
                        foundIt = true;
                        isValid = true;
                    }
                    i++;
                } catch (IllegalAccessException e) {
                    Informer.getInstance().fail(CLASSNAME, methodName,
                            "Illegal access", "field",
                            new FatalException(e.getMessage()));
                }
            }
        }

        return isValid;
    }

    /**
     * Determines if a table name is a valid audio bitstream table name in this
     * archive's database. Does this by looking for this table as the value of a
     * member starting with "TABLE_BS_AUDIO_".
     * 
     * @param table
     *            database table name
     * @return whether this is a valid bitstream table name
     * @throws FatalException
     */
    public static boolean isValidAudioBitstreamTable(String table)
            throws FatalException {
        String methodName = "isValidAudioBitstreamTable(String)";

        boolean isValid = false, foundIt = false;

        if (table != null && !table.equals("")) {
            // dynamically make a list of all the fields listed
            // in this class
            Field[] fields = new ArchiveDatabase().getClass().getFields();
            int i = 0;
            while (!foundIt && i < fields.length) {
                try {
                    // only want to consider the members that start with
                    // "TABLE_BS_AUDIO_"
                    if (fields[i].getName().startsWith("TABLE_BS_AUDIO_")
                            && ((String) fields[i].get(fields[i]))
                                    .equals(table)) {
                        foundIt = true;
                        isValid = true;
                    }
                    i++;
                } catch (IllegalAccessException e) {
                    Informer.getInstance().fail(CLASSNAME, methodName,
                            "Illegal access", "field",
                            new FatalException(e.getMessage()));
                }
            }
        }

        return isValid;
    }

    /**
     * Determines if a table name is a valid video bitstream table name in this
     * archive's database. Does this by looking for this table as the value of a
     * member starting with "TABLE_BS_VIDEO_".
     * 
     * @param table
     *            database table name
     * @return whether this is a valid bitstream table name
     * @throws FatalException
     */
    public static boolean isValidVideoBitstreamTable(String table)
            throws FatalException {
        String methodName = "isValidVideoBitstreamTable(String)";

        boolean isValid = false, foundIt = false;

        if (table != null && !table.equals("")) {
            // dynamically make a list of all the fields listed
            // in this class
            Field[] fields = new ArchiveDatabase().getClass().getFields();
            int i = 0;
            while (!foundIt && i < fields.length) {
                try {
                    // only want to consider the members that start with
                    // "TABLE_BS_AUDIO_"
                    if (fields[i].getName().startsWith("TABLE_BS_VIDEO_")
                            && ((String) fields[i].get(fields[i]))
                                    .equals(table)) {
                        foundIt = true;
                        isValid = true;
                    }
                    i++;
                } catch (IllegalAccessException e) {
                    Informer.getInstance().fail(CLASSNAME, methodName,
                            "Illegal access", "field",
                            new FatalException(e.getMessage()));
                }
            }
        }

        return isValid;
    }
}
