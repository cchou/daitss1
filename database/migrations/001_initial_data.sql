-- MySQL dump 10.10
--
-- Host: darchive    Database: daitss_prod
-- ------------------------------------------------------
-- Server version	4.1.20-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `ACTION_PLAN`
--

LOCK TABLES `ACTION_PLAN` WRITE;
INSERT INTO `ACTION_PLAN` VALUES (1, 'PLACE_HOLDER', 'The background document', 'PLACE_HOLDER', '2005-02-15', '2006-02-20', NULL, 'This is only a placeholder action plan used for testing');
UNLOCK TABLES;

--
-- Dumping data for table `DOCUMENT_LOCATION`
--

LOCK TABLES `DOCUMENT_LOCATION` WRITE;
INSERT INTO `DOCUMENT_LOCATION` VALUES ('C1_D1', 'PHYSICAL', 'cabinet one, drawer one, Room 40, FCLA HQ');
INSERT INTO `DOCUMENT_LOCATION` VALUES ('C1_D2', 'PHYSICAL', 'cabinet one, drawer two, Room 40, FCLA HQ');
INSERT INTO `DOCUMENT_LOCATION` VALUES ('C1_D3', 'PHYSICAL', 'cabinet one, drawer three, Room 40, FCLA HQ');
INSERT INTO `DOCUMENT_LOCATION` VALUES ('C1_D4', 'PHYSICAL', 'cabinet one, drawer four, Room 40, FCLA HQ');
INSERT INTO `DOCUMENT_LOCATION` VALUES ('PLACE_HOLDER', 'PHYSICAL', 'Not a real location, used for testing only');
INSERT INTO `DOCUMENT_LOCATION` VALUES ('S1_S1', 'PHYSICAL', 'Shelving Unit one, Shelf one, Room 40, FCLA HQ');
INSERT INTO `DOCUMENT_LOCATION` VALUES ('S1_S2', 'PHYSICAL', 'Shelving Unit one, Shelf two, Room 40, FCLA HQ');
INSERT INTO `DOCUMENT_LOCATION` VALUES ('S1_S3', 'PHYSICAL', 'Shelving Unit one, Shelf three, Room 40, FCLA HQ');
INSERT INTO `DOCUMENT_LOCATION` VALUES ('S1_S4', 'PHYSICAL', 'Shelving Unit one, Shelf four, Room 40, FCLA HQ');
UNLOCK TABLES;

--
-- Dumping data for table `BS_PROFILE`
--

LOCK TABLES `BS_PROFILE` WRITE;
/*!40000 ALTER TABLE `BS_PROFILE` DISABLE KEYS */;
INSERT INTO `BS_PROFILE` VALUES ('PDF_TAGGED','Tagged PDF',1),('TIFF_BL_BILEVEL','Tiff Baseline image in bilevel colorspace (aka Tiff Class B)',1),('TIFF_BL_EXT_CIELAB','CIE L*a*b* Images (Tiff 6.0 spec)',1),('TIFF_BL_EXT_CMYK','CMYK Images (Tiff 6.0 spec)',1),('TIFF_BL_EXT_G3G4','CCITT Bilevel Encodings (Tiff 6.0 spec)',1),('TIFF_BL_EXT_JPEG','JPEG Compression (Tiff 6.0 spec)',1),('TIFF_BL_EXT_LZW','LZW Compression (Tiff 6.0 spec)',1),('TIFF_BL_EXT_TILED','Tiled Images (Tiff 6.0 spec)',1),('TIFF_BL_EXT_YCBCR','YCbCr Images (Tiff 6.0 spec)',1),('TIFF_BL_GRAYSCALE','Tiff Baseline image in grayscale colorspace (aka Tiff Class G)',1),('TIFF_BL_PALETTE','Tiff Baseline image in palette colorspace (aka Tiff Class P)',1),('TIFF_BL_RGB','Tiff Baseline image in RGB colorspace (aka Tiff Class R)',1),('TIFF_CLASS_F','A subclass of BASELINE_BILEVEL for fax images',1),('TIFF_EP','Tiff/EP',1),('TIFF_EXIF','Exif (Exchangeable Image File Format)',1),('TIFF_GEOTIFF','GeoTiff Revision 1.0 - a tiff having geographic information',1),('TIFF_IT','Tiff/IT (Transport Independent File Format for Image Technology)',1);
/*!40000 ALTER TABLE `BS_PROFILE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `COMPRESSION`
--

LOCK TABLES `COMPRESSION` WRITE;
/*!40000 ALTER TABLE `COMPRESSION` DISABLE KEYS */;
INSERT INTO `COMPRESSION` VALUES ('CCITT_ID','1-dimensional modified Huffman run length encoding'),('CVID','CINEPAK VIDEO'),('DEFLATE','Deflate compression uses the zlib compressed data format'),('DEFLATE_UNOFFICIAL','PKZIP-style Deflate encoding (experimental)'),('GROUP_3_FAX','T4-encoding: CCITT T.4 bi-level encoding'),('GROUP_4_FAX','T6-encoding: CCITT T.6 bi-level encoding'),('IT8_BINARY_LINE','RLE for binary line art (Source: JHOVE, JIU documentation)'),('IT8_CT_PADDING','RasterPaddding in CT (continuous tone) or \r\nRasterPaddding in CT (continuous tone) or MP'),('IT8_LINEWORK','RLE for LW (linework)'),('IT8_MONOCHROME','RLE for Monochrome images'),('IV32','INDEO VIDEO 3.2'),('IV50','INDEO VIDEO 5.0'),('JBIG','JBIG'),('JBIG2','JBIG2'),('JPEG','JPEG (Joint Photographic Experts Group) compression'),('JPEG_NEW','JPEG compression as described in Adobe Photoshop'),('JPEG2000','JPEG 2000 compression as defined by ISO/IEC 15444'),('KODAK_DCS','Kodak DCS'),('LINEAR','LINEAR PCM (AUDIO)'),('LZW','LZW (Lempel-Ziv & Welsh) compression'),('MJPG','MOTION JPEG VIDEO'),('NEXT','NeXT 2-bit encoding'),('NONE','No compression'),('NONE_WORD_ALIGNED','No compression, word-aligned'),('NOT_APPLICABLE','Not applicable'),('PACKBITS','Packbits compression'),('PEGASUS_IMJ','Pegasus IMJ'),('PIXAR10','Pixar compounded 10-bit ZIP encoding'),('PIXAR11','Pixar compounded 11-bit ZIP encoding'),('SGI_LOG_24_PACKED','SGI 24-bit Log Luminance encoding (experimental)'),('SGI_LOG_RLE','SGI 32-bit Log Luminance encoding (experimental)'),('THUNDERSCAN','ThunderScan 4-bit encoding'),('UNKNOWN','Not known if there is any compression');
/*!40000 ALTER TABLE `COMPRESSION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `FORMAT`
--

LOCK TABLES `FORMAT` WRITE;
/*!40000 ALTER TABLE `FORMAT` DISABLE KEYS */;
INSERT INTO `FORMAT` VALUES ('APP_PDF_1_0','application/pdf','1.0','','TRUE',1),('APP_PDF_1_1','application/pdf','1.1','','TRUE',1),('APP_PDF_1_2','application/pdf','1.2','','TRUE',1),('APP_PDF_1_3','application/pdf','1.3','','TRUE',1),('APP_PDF_1_4','application/pdf','1.4','','TRUE',1),('APP_PDF_1_5','application/pdf','1.5','','TRUE',1),('APP_PDF_1_6','application/pdf','1.6','','TRUE',1),('APP_UNK','application/octet-stream','','','FALSE',1),('APP_XML_1_0','application/xml','1.0','','TRUE',1),('APP_XMLDTD_1_0','application/xml-dtd','1.0','','TRUE',1),('AUDIO_WAVE','audio/x-wave','N/A','','TRUE',1),('IMG_JP2_1_0','image/jp2','1.0','','TRUE',1),('IMG_JPEG_ADOBE','image/jpeg','','ADOBE','TRUE',1),('IMG_JPEG_JFIF','image/jpeg','','JFIF','TRUE',1),('IMG_JPEG_UNKNOWN','image/jpeg','UNKNOWN','','TRUE',1),('IMG_JPX_1_0','image/jpx','1.0','','TRUE',1),('IMG_TIFF_4_0','image/tiff','4.0','','TRUE',1),('IMG_TIFF_5_0','image/tiff','5.0','','TRUE',1),('IMG_TIFF_6_0','image/tiff','6.0','','TRUE',1),('TXT_PLAIN','text/plain','','','TRUE',1),('VIDEO_AVI_1_0','video/avi','1.0','','TRUE',1);
/*!40000 ALTER TABLE `FORMAT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `FORMAT_ATTRIBUTE`
--

LOCK TABLES `FORMAT_ATTRIBUTE` WRITE;
/*!40000 ALTER TABLE `FORMAT_ATTRIBUTE` DISABLE KEYS */;
INSERT INTO `FORMAT_ATTRIBUTE` VALUES ('APP_PDF_HAS_BIN_HDR','APP_PDF_1_0','Has the recommended binary characters in its header'),('APP_PDF_HAS_BIN_HDR','APP_PDF_1_1','Has the recommended binary characters in its header'),('APP_PDF_HAS_BIN_HDR','APP_PDF_1_2','Has the recommended binary characters in its header'),('APP_PDF_HAS_BIN_HDR','APP_PDF_1_3','Has the recommended binary characters in its header'),('APP_PDF_HAS_BIN_HDR','APP_PDF_1_4','Has the recommended binary characters in its header'),('APP_PDF_HAS_BIN_HDR','APP_PDF_1_5','Has the recommended binary characters in its header'),('APP_PDF_IS_LIN','APP_PDF_1_2','Is linearized'),('APP_PDF_IS_LIN','APP_PDF_1_3','Is linearized'),('APP_PDF_IS_LIN','APP_PDF_1_4','Is linearized'),('APP_PDF_IS_LIN','APP_PDF_1_5','Is linearized'),('APP_PDF_PDFA_1A_CONF','APP_PDF_1_0','Conformant with PDF/A-1a specification'),('APP_PDF_PDFA_1A_CONF','APP_PDF_1_1','Conformant with PDF/A-1a specification'),('APP_PDF_PDFA_1A_CONF','APP_PDF_1_2','Conformant with PDF/A-1a specification'),('APP_PDF_PDFA_1A_CONF','APP_PDF_1_3','Conformant with PDF/A-1a specification'),('APP_PDF_PDFA_1A_CONF','APP_PDF_1_4','Conformant with PDF/A-1a specification'),('APP_PDF_PDFA_1A_CONF','APP_PDF_1_5','Conformant with PDF/A-1a specification'),('APP_PDF_PDFA_1B_CONF','APP_PDF_1_0','Conformant with PDF/A-1b specification'),('APP_PDF_PDFA_1B_CONF','APP_PDF_1_1','Conformant with PDF/A-1b specification'),('APP_PDF_PDFA_1B_CONF','APP_PDF_1_2','Conformant with PDF/A-1b specification'),('APP_PDF_PDFA_1B_CONF','APP_PDF_1_3','Conformant with PDF/A-1b specification'),('APP_PDF_PDFA_1B_CONF','APP_PDF_1_4','Conformant with PDF/A-1b specification'),('APP_PDF_PDFA_1B_CONF','APP_PDF_1_5','Conformant with PDF/A-1b specification'),('APP_XML_HAS_DTD','APP_XML_1_0','Uses a DTD'),('APP_XML_HAS_W3D_SCHEMA','APP_XML_1_0','Uses a W3D Xml schema'),('IMAGE_JP2_HAS_IPR_MD','IMG_JP2_1_0','File contains intellectual property rights metadata'),('IMAGE_JP2_HAS_XML_MD','IMG_JP2_1_0','File contains at least 1 XML box'),('IMAGE_JPEG_IS_ADOBE_PS_3','IMG_JPEG_ADOBE','File is a JPEG written by Adobe Photoshop or Illustrator'),('IMAGE_JPEG_IS_JFIF','IMG_JPEG_JFIF','File is a JFIF file'),('IMAGE_JPEG_IS_UNKNOWN_VAR','IMG_JPEG_UNKNOWN','The JPEG file variation is unknown'),('TEXT_PLAIN_HAS_BOM','APP_XMLDTD_1_0','File begins with the BOM (byte order mark)'),('TEXT_PLAIN_HAS_BOM','TXT_PLAIN','File begins with the BOM (byte order mark)');
/*!40000 ALTER TABLE `FORMAT_ATTRIBUTE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `MEDIA_TYPE`
--

LOCK TABLES `MEDIA_TYPE` WRITE;
/*!40000 ALTER TABLE `MEDIA_TYPE` DISABLE KEYS */;
INSERT INTO `MEDIA_TYPE` VALUES ('application/msword','MSword Document'),('application/octet-stream','unknown bitstream'),('application/pdf','RFC3778. PDF, the \'Portable Document Format\', is a general document representation language that has been in use for document exchange on\r\n the Internet since 1993.'),('application/postscript',''),('application/vnd.ms-excel',''),('application/x-bzip2',''),('application/x-dosexec',''),('application/x-gzip',''),('application/x-rar-compressed',''),('application/xml','RFC3023.'),('application/xml-dtd','RFC 3023'),('application/zip',''),('audio/mpeg',''),('audio/unknown',''),('audio/x-wave','RFC2361, RFC2626, audio wave format'),('default','This is not a real media type, it is used as a placeholder to be referenced from the ARCHIVE_LOGIC table. All partners must have a default agreement to cover formats for which no preservation level (i.e. agreement) has been specified.'),('image/bmp',''),('image/gif',''),('image/jp2','RFC3745. The image/jp2 content-type refers to all of the profiles and extensions that build on JPEG 2000 [ISO-JPEG2000-1] encoded image data. The file format is also defined in [ISO-JPEG2000-1], Annex I. The recommended file suffix is \"jp2\".'),('image/jpeg','RFC2045,RFC2046.'),('image/jpx','RFC3745. The image/jpx content-type refers to all of the profiles and extensions that build on JPEG 2000 [ISO-JPEG2000-2] encoded image data. The file format is also defined in [ISO-JPEG2000-2], Annex M. The recommended file suffix is \"jpf\".'),('image/png',''),('image/tiff','RFC3302. Tag Image File Format.'),('image/x-portable-bitmap',''),('image/x-portable-greymap',''),('image/x-portable-pixmap',''),('text/html',''),('text/plain','RFC2046, RFC3676.'),('text/rtf',''),('text/sgml','[RFC 1874] SGML Media Types'),('video/avi','RFC2361, Audio/Video Interleaved format'),('video/mpeg',''),('video/quicktime',''),('video/x-msvideo','');
/*!40000 ALTER TABLE `MEDIA_TYPE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `MESSAGE_DIGEST_TYPE`
--

LOCK TABLES `MESSAGE_DIGEST_TYPE` WRITE;
/*!40000 ALTER TABLE `MESSAGE_DIGEST_TYPE` DISABLE KEYS */;
INSERT INTO `MESSAGE_DIGEST_TYPE` VALUES ('0','unknown signature type'),('md5','MD5 message digest algorithm, 128 bits'),('sha-1','Secure Hash Algorithm 1, 160 bits');
/*!40000 ALTER TABLE `MESSAGE_DIGEST_TYPE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `PDF_ACTION`
--

LOCK TABLES `PDF_ACTION` WRITE;
/*!40000 ALTER TABLE `PDF_ACTION` DISABLE KEYS */;
INSERT INTO `PDF_ACTION` VALUES ('A_GOTO','ACTIVATION','Go to a destination in the current document'),('A_GOTO3DVIEW','ACTIVATION','Set the current view of a 3D annotation (PDF 1.6)'),('A_GOTOE','ACTIVATION','Go to a destination in an embedded file (PDF 1.6)'),('A_GOTOR','ACTIVATION','Go to a destination in another document'),('A_HIDE','ACTIVATION','Set an annotation\'s hidden flag. (PDF 1.2)'),('A_IMPORTDATA','ACTIVATION','Import field values from a file. (PDF 1.2)'),('A_JAVASCRIPT','ACTIVATION','Execute a JavaScript script. (PDF 1.3)'),('A_LAUNCH','ACTIVATION','Launch an application, usually to open a file'),('A_MOVIE','ACTIVATION','Play a movie. (PDF 1.2)'),('A_NAMED','ACTIVATION','Execute an action predifined by the viewer app. (PDF 1.2)'),('A_RENDITION','ACTIVATION','Control the playing of multimedia content (PDF 1.5)'),('A_RESETFORM','ACTIVATION','Set fields to their default values. (PDF 1.2)'),('A_SETOCGSTATE','ACTIVATION','Set the state of optional content group (PDF 1.5)'),('A_SOUND','ACTIVATION','Play a sound. (PDF 1.2)'),('A_SUBMITFORM','ACTIVATION','Send data to a URL. (PDF 1.2)'),('A_THREAD','ACTIVATION','Begin reading an article thread'),('A_TRANS','ACTIVATION','Update the display of a document, using a transition dictionary (PDF 1.5)'),('A_URI','ACTIVATION','Resolve a uniform resource identifier'),('AN_BL','ANNOTATION','Annotation action on blur. (1.2)'),('AN_D','ANNOTATION','Annotation action on mouse down. (1.2)'),('AN_E','ANNOTATION','Annotation action on cursor entry. (1.2)'),('AN_FO','ANNOTATION','Annotation action on focus. (1.2)'),('AN_PC','ANNOTATION','Annotation action to be performed when the page containing the annotation is closed. (1.5)'),('AN_PI','ANNOTATION','Annotation action to be performed when the page containing the annotation is no longer visible. (1.5)'),('AN_PO','ANNOTATION','Annotation action to be performed when the page containing the annotation is opened. (1.5)'),('AN_PV','ANNOTATION','Annotation action to be performed when the page containing the annotation becomes visible. (1.5)'),('AN_U','ANNOTATION','Annotation action on mouse up. (1.2)'),('AN_X','ANNOTATION','Annotation action on cursor exit. (1.2)'),('D_DC','DOCUMENT','A JavaScript action to be performed before closing a document. (1.4)'),('D_DP','DOCUMENT','A JavaScript action to be performed after printing a document. (1.4)'),('D_DS','DOCUMENT','A JavaScript action to be performed after saving a document. (1.4)'),('D_WP','DOCUMENT','A JavaScript action to be performed before printing a document. (1.4)'),('D_WS','DOCUMENT','A JavaScript action to be performed before saving a document. (1.4)'),('F_C','FORM_FIELD','Form field action on recalculation of this field because another field changed. (1.3)'),('F_F','FORM_FIELD','Form field action before formatting field display. (1.3)'),('F_K','FORM_FIELD','Form field action on key type or scollable list selection modification. (1.3)'),('F_V','FORM_FIELD','Form field action on field value change. (1.3)'),('P_C','PAGE','Page action on close. (1.2)'),('P_O','PAGE','Page action on open. (1.2)');
/*!40000 ALTER TABLE `PDF_ACTION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `PDF_ANNOTATION`
--

LOCK TABLES `PDF_ANNOTATION` WRITE;
/*!40000 ALTER TABLE `PDF_ANNOTATION` DISABLE KEYS */;
INSERT INTO `PDF_ANNOTATION` VALUES ('3D','3D annotation (PDF 1.6)'),('Caret','Caret annotation (PDF 1.5)'),('Circle','Circle annotation (PDF 1.3)'),('FileAttachment','File attachment annotation (PDF 1.3)'),('FreeText','Free text annotation (PDF 1.3)'),('Highlight','Highlight annotation (PDF 1.3)'),('Ink','Ink annotation (PDF 1.3)'),('Line','Line annotation (PDF 1.3)'),('Link','Link annotation'),('Movie','Movie annotation (PDF 1.2)'),('Polygon','Polygon annotation (PDF 1.5)'),('PolyLine','PolyLine annotation (PDF 1.5)'),('Popup','Popup annotation (PDF 1.3)'),('PrinterMark','Printer mark annotation (PDF 1.4)'),('Screen','Screen annotation (PDF 1.5)'),('Sound','Sound annotation (PDF 1.2)'),('Square','Square annotation (PDF 1.3)'),('Squiggly','Squiggly annotation (PDF 1.4)'),('Stamp','Stamp annotation (PDF 1.3)'),('StrikeOut','Strike out annotation (PDF 1.3)'),('Text','Text annotation'),('TrapNet','Trap net annotation (PDF 1.3)'),('Underline','Underline annotation (PDF 1.3)'),('Watermark','Watermark annotation (PDF 1.6)'),('Widget','Widget annotation (PDF 1.2)');
/*!40000 ALTER TABLE `PDF_ANNOTATION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `PDF_FILTER`
--

LOCK TABLES `PDF_FILTER` WRITE;
/*!40000 ALTER TABLE `PDF_FILTER` DISABLE KEYS */;
INSERT INTO `PDF_FILTER` VALUES ('ASCII85Decode',''),('ASCIIHexDecode',''),('CCITTFaxDecode',''),('DCTDecode',''),('FlateDecode',''),('JBIG2Decode',''),('LZWDecode',''),('RunLengthDecode','');
/*!40000 ALTER TABLE `PDF_FILTER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `SEVERE_ELEMENT`
--

LOCK TABLES `SEVERE_ELEMENT` WRITE;
/*!40000 ALTER TABLE `SEVERE_ELEMENT` DISABLE KEYS */;
INSERT INTO `SEVERE_ELEMENT` VALUES ('A_AVI_BAD_MAIN_HEADER','ANOMALY','BIT','TRUE','he AVI header does not start with \"avih\"'),('A_AVI_BAD_STREAM_HEADER','ANOMALY','BIT','TRUE','the AVI stream header does not start with \"strh\"'),('A_AVI_HDRL_MISSING','ANOMALY','BIT','TRUE','missing the required hdrl chunk'),('A_AVI_MAIN_HEADER_TOO_SMALL','ANOMALY','BIT','TRUE','the main header (\"avih\") does not have sufficient size'),('A_AVI_MOVI_MISSING','ANOMALY','BIT','TRUE','missing the required movi chunk'),('A_AVI_NO_HDRL_BEFORE_MOVI','ANOMALY','BIT','TRUE','No hdrl chunk before movi chunk'),('A_AVI_STRH_MISSING','ANOMALY','BIT','TRUE','missing the required STRH header.'),('A_AVI_STRH_TOO_SMALL','ANOMALY','BIT','TRUE','the STRH header does not has sufficient size'),('A_AVI_UNIDENTIFIABLE_STREAM_HEADER','ANOMALY','BIT','TRUE','unidentifiable stream header.'),('A_DTD_ATT_FOR_MISSING_ELEM','ANOMALY','NOTE','FALSE','Attribute list for a non-existent element'),('A_DTD_ATT_FOR_NO_ELEM','ANOMALY','BIT','TRUE','Attribute list that doesn\'t specify an element'),('A_DTD_BAD_CHARREF','ANOMALY','BIT','TRUE','Badly formed character reference'),('A_DTD_BAD_ENT_FILE','ANOMALY','BIT','TRUE','Can\'t read a file that the DTD references for entities'),('A_DTD_BAD_SYNTAX','ANOMALY','BIT','TRUE','Bad syntax'),('A_DTD_MISSING_ENT_FILE','ANOMALY','BIT','TRUE','Can\'t find a file that the DTD references for entities'),('A_DTD_MULT_ATTLIST','ANOMALY','NOTE','FALSE','Multiple attribute lists for the same element type'),('A_DTD_MULT_ELEM_DECL','ANOMALY','BIT','TRUE','Multiple declarations of the same element type'),('A_DTD_MULT_ID_ATT','ANOMALY','BIT','TRUE','multiple ID attributes for the same element type'),('A_DTD_MULT_NOT_ATT','ANOMALY','BIT','TRUE','multiple notation attributes for the same element type'),('A_DTD_MULT_NOT_DECL','ANOMALY','BIT','TRUE','Multiple declarations of the same notation name'),('A_DTD_NOT_ATT_EMPTY_ELEM','ANOMALY','BIT','TRUE','Notation attribute on an empty element'),('A_FILE_LINK_BAD_URI','ANOMALY','NOTE','TRUE','Bad URI syntax for a link within the file'),('A_FILE_NO_BITSTREAM','ANOMALY','BIT','TRUE','Cannot find any valid bitstream'),('A_FILE_OVRLMT_CREATOR_PROG','ANOMALY','NOTE','FALSE','Exceeds data size limit for CREATOR_PROG'),('A_FILE_OVRLMT_FILE_TITLE','ANOMALY','NOTE','FALSE','Exceeds data size limit for FILE_TITLE'),('A_FILE_OVRLMT_MEDIA_TYPE_VERSION','ANOMALY','NOTE','FALSE','Exceeds data size limit for MEDIA_TYPE_VERSION'),('A_FILE_OVRLMT_NUM_BITSTREAMS','ANOMALY','NOTE','FALSE','Exceeds data size limit for NUM_BITSTREAMS'),('A_FILE_OVRLMT_ORIG_URI','ANOMALY','NOTE','FALSE','Exceeds data size limit for ORIG_URI'),('A_FILE_OVRLMT_SIZE','ANOMALY','NOTE','FALSE','Exceeds data size limit for SIZE'),('A_FILE_TOO_MANY_ANOMS','ANOMALY','BIT','TRUE','Has too many anomalies'),('A_FILE_UNKNOWN_COMPRESSION','ANOMALY','BIT','TRUE','Uses an unknown compression scheme'),('A_FILE_UNKNOWN_MEDIATYPE','ANOMALY','BIT','TRUE','Can not recognize the file format'),('A_IMG_OVRLMT_BITS_PER_SAMPLE','ANOMALY','NOTE','FALSE','Exceeds data size limit for BITS_PER_SAMPLE'),('A_IMG_OVRLMT_EXTRA_SAMPLES','ANOMALY','NOTE','FALSE','Exceeds data size limit for EXTRA_SAMPLES'),('A_IMG_OVRLMT_IMAGE_HEIGHT','ANOMALY','NOTE','FALSE','Exceeds data size limit for IMAGE_HEIGHT'),('A_IMG_OVRLMT_IMAGE_WIDTH','ANOMALY','NOTE','FALSE','Exceeds data size limit for IMAGE_WIDTH'),('A_IMG_OVRLMT_NUM_COMPONENTS','ANOMALY','NOTE','FALSE','Exceeds data size limit for NUM_COMPONENTS'),('A_IMG_OVRLMT_PIXEL_ASPECT_RATIO','ANOMALY','NOTE','FALSE','Exceeds data size limit for PIXEL_ASPECT_RATIO'),('A_IMG_OVRLMT_RES_HORZ','ANOMALY','NOTE','FALSE','Exceeds data size limit for RES_HORZ'),('A_IMG_OVRLMT_RES_VERT','ANOMALY','NOTE','FALSE','Exceeds data size limit for RES_VERT'),('A_IMG_UNKNOWN_COLORSPACE','ANOMALY','BIT','TRUE','Uses an unknown color space'),('A_IMG_UNKNOWN_EXTRASAMPLES','ANOMALY','NOTE','FALSE','Has extra data per pixel for which the purpose is unknown'),('A_IMG_UNKNOWN_ORIENTATION','ANOMALY','NOTE','FALSE','Has an unknown orientation'),('A_IMG_UNKNOWN_RESUNIT','ANOMALY','NOTE','FALSE','Uses an unknown resolution unit'),('A_JP2_EXTRA_DECODE_REQS','ANOMALY','BIT','TRUE','codestream has decode requirements not described in the ISO Jp2 spec'),('A_JP2_NO_JP2CBOX','ANOMALY','BIT','TRUE','No image data (contiguous codestream box)'),('A_JP2_NO_JP2HBOX','ANOMALY','BIT','TRUE','No image metadata (JP2 Header box)'),('A_JP2_NONCOMPAT_JP2','ANOMALY','BIT','TRUE','file not compatible with the ISO Jp2 spec'),('A_JP2_NONCONTIG_COLRBOX','ANOMALY','NOTE','FALSE','Non-contiguous color specification boxes in the JP2 Header box'),('A_JPEG_BAD_DHT','ANOMALY','BIT','TRUE','DHT marker segment is malformed'),('A_JPEG_BAD_DQT','ANOMALY','BIT','TRUE','DQT marker segment is malformed'),('A_JPEG_BAD_DRI','ANOMALY','BIT','TRUE','DRI marker segment is malformed'),('A_JPEG_BAD_SOF','ANOMALY','BIT','TRUE','SOF marker segment is malformed'),('A_JPEG_BAD_SOS','ANOMALY','BIT','TRUE','SOS marker segment is malformed'),('A_JPEG_INCOMPLETE_APP0','ANOMALY','BIT','TRUE','The JFIF APP0 marker segment is incomplete'),('A_JPEG_MISSING_DQT','ANOMALY','BIT','TRUE','SOF marker segment refers to an unknown DQT'),('A_JPEG_UNKNOWN_JFIFEXT','ANOMALY','BIT','TRUE','Uses an unknown JFIF extension'),('A_JPEG_UNKNOWN_MARKER','ANOMALY','BIT','TRUE','Contains an unknown marker - can\'t interpret file'),('A_JPEG_UNKNOWN_VARIATION','ANOMALY','BIT','TRUE','Do not know this JPEG variation'),('A_JPEG2K_BAD_BPCBOX','ANOMALY','BIT','TRUE','Bits Per Component box not formatted correctly'),('A_JPEG2K_BAD_CDEFBOX','ANOMALY','BIT','TRUE','Channel definition box not formatted correctly'),('A_JPEG2K_BAD_CMAPBOX','ANOMALY','BIT','TRUE','Component mapping box not formatted correctly'),('A_JPEG2K_BAD_FTYPBOX','ANOMALY','BIT','TRUE','Malformed file type box'),('A_JPEG2K_BAD_IHDRBOX','ANOMALY','BIT','TRUE','Malformed image header box'),('A_JPEG2K_BAD_JP2CBOX','ANOMALY','BIT','TRUE','Malformed codestream'),('A_JPEG2K_BAD_JP2HBOX','ANOMALY','BIT','TRUE','JP2 header box not formatted correctly'),('A_JPEG2K_BAD_LBOX','ANOMALY','BIT','TRUE','Invalid box length value'),('A_JPEG2K_BAD_LOC_BOX','ANOMALY','BIT','TRUE','A box is not in the correct location within the file'),('A_JPEG2K_BAD_LOC_JP2H','ANOMALY','BIT','TRUE','JP2 Header box is not in the correct location within the file'),('A_JPEG2K_BAD_PCLRBOX','ANOMALY','BIT','TRUE','Palette box not formatted correctly.'),('A_JPEG2K_BAD_RES0BOX','ANOMALY','BIT','TRUE','Resolution box not formatted correctly.'),('A_JPEG2K_BAD_XLBOX','ANOMALY','BIT','TRUE','Invalid extended box length value'),('A_JPEG2K_EARLY_BOX_END','ANOMALY','NOTE','FALSE','End of box reached before specified box length reached'),('A_JPEG2K_NO_BPCBOX','ANOMALY','BIT','FALSE','No bits per component info'),('A_JPEG2K_NO_CMAPBOX','ANOMALY','BIT','TRUE','No component mapping info'),('A_JPEG2K_NO_COLRBOX','ANOMALY','BIT','TRUE','No color specification box'),('A_JPEG2K_OVRLMT_PROG_ORDER','ANOMALY','NOTE','FALSE','Exceeds data size limit for PROG_ORDER'),('A_JPEG2K_OVRLMT_WAVELET_TRANF_TYPE','ANOMALY','NOTE','FALSE','Exceeds data size limit for WAVELET_TRANF_TYPE'),('A_JPEG2K_UNK_BOXTYPE','ANOMALY','BIT','TRUE','Unrecognized (unknown) box type'),('A_JPEG2K_UNK_CS_METH','ANOMALY','BIT','TRUE','Unknown color specification method (METH field) in the Color Specification box'),('A_JPEG2K_UNK_MCT','ANOMALY','BIT','TRUE','Unknown multiple component transformation in the COD box'),('A_JPEG2K_UNK_MIN_VERSION','ANOMALY','BIT','TRUE','Unrecognized (unknown) File Type box, MinV field value'),('A_JPEG2K_UNK_PROG_ORDER','ANOMALY','BIT','TRUE','Unrecognized progression order'),('A_JPEG2K_UNK_TBOX','ANOMALY','BIT','TRUE','Unrecognized (unknown) box type value'),('A_JPEG2K_UNK_UNKC','ANOMALY','BIT','TRUE','Unrecognized (unknown) Image Header box, UnkC field value'),('A_ML_OVRLMT_CHARSET','ANOMALY','NOTE','FALSE','Exceeds string length for CHARSET'),('A_ML_OVRLMT_MARKUP_LANGUAGE','ANOMALY','NOTE','FALSE','Exceeds data size limit for MARKUP_LANGUAGE'),('A_PDF_BAD_ACTION_DICT','ANOMALY','NOTE','TRUE','Badly formed action dictionary'),('A_PDF_BAD_CATALOG_DICT','ANOMALY','BIT','TRUE','Badly formed catalog dictionary'),('A_PDF_BAD_DATE_OBJECT','ANOMALY','NOTE','TRUE','Illegal date object'),('A_PDF_BAD_FORMAT','ANOMALY','BIT','TRUE','Badly formatted'),('A_PDF_BAD_MARKINFO_DICT','ANOMALY','NOTE','TRUE','Badly formed mark info dictionary'),('A_PDF_BAD_NAME_OBJECT','ANOMALY','BIT','TRUE','Illegal name object'),('A_PDF_BAD_PAGELAYOUT','ANOMALY','NOTE','TRUE','Not a valid page layout option'),('A_PDF_BAD_PAGEMODE','ANOMALY','NOTE','TRUE','Not a valid page mode option'),('A_PDF_BAD_PAGETREE','ANOMALY','BIT','TRUE','Malformed or cyclical page tree'),('A_PDF_BAD_PREV_VALUE','ANOMALY','BIT','TRUE','Invalid Prev value in the trailer dictionary'),('A_PDF_BAD_SIZE_VALUE','ANOMALY','BIT','TRUE','Invalid Size value in trailer dictionary'),('A_PDF_BAD_STREAM_LENGTH','ANOMALY','BIT','TRUE','Invalid Length value in stream dictionary'),('A_PDF_BAD_STRING_OBJECT','ANOMALY','NOTE','TRUE','Illegal string object'),('A_PDF_NO_OBJECT','ANOMALY','BIT','TRUE','An object not found'),('A_PDF_NO_PAGE','ANOMALY','NOTE','TRUE','There is no page contained in this PDF file.  Normalization will not be performed if applicable.'),('A_PDF_NO_ROOT','ANOMALY','BIT','TRUE','No Root keyword in trailer dictionary'),('A_PDF_NO_STARTXREF','ANOMALY','BIT','TRUE','No startxref keyword'),('A_PDF_NO_TRAILER','ANOMALY','BIT','TRUE','No trailer in last 1024 bytes'),('A_PDF_NO_XREF','ANOMALY','BIT','TRUE','Cross-reference dictionary not found'),('A_PDF_NO_XREF_OR_TRAILER','ANOMALY','BIT','TRUE','The cross-reference and/or trailer dictionary not found'),('A_PDF_NO_XREF_SUBSECTION','ANOMALY','BIT','TRUE','A cross-reference sub-section not found'),('A_PDF_OVRLMT_NUM_PAGES','ANOMALY','NOTE','FALSE','Exceeds data size limit for NUM_PAGES'),('A_PDF_UNKNOWN_ACTION','ANOMALY','NOTE','FALSE','Lists an unknown action type'),('A_PDF_UNKNOWN_ANNOTATION','ANOMALY','NOTE','FALSE','Lists an unknown annotation type'),('A_PDF_UNKNOWN_FILTER','ANOMALY','NOTE','FALSE','Lists an unknown filter type'),('A_PDF_UNKNOWN_OBJECT','ANOMALY','BIT','TRUE','Not a known PDF object type'),('A_PDF_WRONG_TYPE','ANOMALY','NOTE','FALSE','Not the right data type for that key/object'),('A_QUICKTIME_ATOM_TOO_SMALL','ANOMALY','BIT','TRUE','insufficient atom size'),('A_QUICKTIME_BAD_MDIA','ANOMALY','BIT','TRUE','malform Media atom'),('A_QUICKTIME_BAD_MINF','ANOMALY','BIT','TRUE','malform Media Information atom'),('A_QUICKTIME_BAD_MOOV','ANOMALY','BIT','TRUE','malform Movie atom'),('A_QUICKTIME_BAD_TRAK','ANOMALY','BIT','TRUE','malform Track atom'),('A_QUICKTIME_NO_MDAT','ANOMALY','BIT','TRUE','missing the required Movie Data atom'),('A_QUICKTIME_NO_MOOV','ANOMALY','BIT','TRUE','missing the required Movie atom'),('A_TIFF_BAD_DATETIME','ANOMALY','NOTE','TRUE','Doesn\'t use the correct format for date/time'),('A_TIFF_NEG_FIELD_COUNT','ANOMALY','BIT','TRUE','Says it has a negative number of values for a field - impossible'),('A_TIFF_NEG_FILE_OFFSET','ANOMALY','BIT','TRUE','Has a negative offset into the file - impossible'),('A_TIFF_NEG_TAG','ANOMALY','BIT','TRUE','Has a negative tag number'),('A_TIFF_NO_IFD','ANOMALY','BIT','TRUE','Does not have any of the most basic internal metadata (IFDs)'),('A_TIFF_OFFSET_TOO_BIG','ANOMALY','BIT','TRUE','Offset data exceeds file size'),('A_TIFF_OVRLMT_MAX_STRIP_BYTES','ANOMALY','NOTE','FALSE','Exceeds data size limit for MAX_STRIP_BYTES'),('A_TIFF_OVRLMT_MAX_TILE_BYTES','ANOMALY','NOTE','FALSE','Exceeds data size limit for MAX_TILE_BYTES'),('A_TIFF_OVRLMT_NUM_STRIPS','ANOMALY','NOTE','FALSE','Exceeds data size limit for NUM_STRIPS'),('A_TIFF_OVRLMT_NUM_TILES','ANOMALY','NOTE','FALSE','Exceeds data size limit for NUM_TILES'),('A_TIFF_OVRLMT_ROWS_PER_STRIP','ANOMALY','NOTE','FALSE','Exceeds data size limit for ROWS_PER_STRIP'),('A_TIFF_OVRLMT_TILE_LENGTH','ANOMALY','NOTE','FALSE','Exceeds data size limit for TILE_LENGTH'),('A_TIFF_OVRLMT_TILE_WIDTH','ANOMALY','NOTE','FALSE','Exceeds data size limit for TILE_WIDTH'),('A_TIFF_TAGLESS_IFD','ANOMALY','BIT','TRUE','Contains metadata sections (IFDs) without any metadata (fields)'),('A_TIFF_UNKNOWN_GRUNIT','ANOMALY','BIT','TRUE','Doesn\'t use a valid value for the GrayResponseUnit field'),('A_TIFF_UNKNOWN_PLANCONFIG','ANOMALY','BIT','TRUE','Doesn\'t use a valid value for the PlanarConfiguration field'),('A_TIFF_UNKNOWN_TAG','ANOMALY','NOTE','TRUE','Contains a metadata field (tag) not found in the tiff specification'),('A_TIFF_UNKNOWN_TYPE','ANOMALY','NOTE','TRUE','Uses a data type that isn\'t part of the tiff specification'),('A_TIFF_UNKNOWN_YCBCRPOSITIONING','ANOMALY','BIT','TRUE','Doesn\'t use a valid value for the YCbCRPositioning field'),('A_TIFF_UNKNOWN_YCBCRSUBSAMPLING','ANOMALY','BIT','TRUE','Doesn\'t use a valid value for the YCbCRSubsampling field'),('A_TIFF_UNSORTED_TAGS','ANOMALY','NOTE','TRUE','Metadata fields are not in the order they are supposed to be'),('A_TIFF_WRONG_COUNT','ANOMALY','BIT','TRUE','Has a metadata field (tag) with the wrong number of values'),('A_TIFF_WRONG_TYPE','ANOMALY','BIT','TRUE','Uses the wrong data type for a metadata field (tag)'),('A_TXT_MULT_LINEEND','ANOMALY','NOTE','TRUE','Uses multiple types of line ending characters'),('A_TXT_OVRLMT_CHARSET','ANOMALY','NOTE','FALSE','Exceeds string length for CHARSET'),('A_TXT_OVRLMT_NUM_LINES','ANOMALY','NOTE','FALSE','Exceeds data size limit for NUM_LINES'),('A_TXT_UNKNOWN_CHARENC','ANOMALY','BIT','TRUE','Can not determine the character encoding'),('A_WAVE_BAD_ASSOCIATED_DATA_CHUNK','ANOMALY','NOTE','FALSE','bad associated data list chunk'),('A_WAVE_BAD_SUBCHUNK_IN_ASSOCIATED_DATA_LIST','ANOMALY','NOTE','FALSE','bad subchunk in the associated data list chunk'),('A_WAVE_FMT_CHUNK_TOO_SMALL','ANOMALY','BIT','TRUE','Insufficient size for the format (\"fmt \") chunk'),('A_WAVE_NO_FACT_FOR_COMPRESSED_DATA','ANOMALY','BIT','TRUE','lack of fact chunk for compressed data'),('A_WAVE_NO_FMT_BEFORE_DATA','ANOMALY','BIT','TRUE','lack of format chunk before data chunk'),('A_WAVE_NON_UNIQUE_CUE_POINT_ID','ANOMALY','NOTE','FALSE','non-unique cue point id'),('A_WAVE_UNDEFINED_CUE_POINT_IN_ASSOCIATED_DATA_LIST','ANOMALY','NOTE','FALSE','undefined cue point name is used in the associated data list'),('A_WAVE_UNDEFINED_CUE_POINT_IN_PLAYLIST','ANOMALY','NOTE','FALSE','undefined cue point name are used in the play list chunk'),('A_WAVE_UNRECOGNIZABLE_SPEAKER_POSITION','ANOMALY','NOTE','FALSE','use of unrecognizable speaker position in the channel mask'),('A_XML_BAD_FORMAT','ANOMALY','BIT','TRUE','Unknown format exception (parse error)'),('A_XML_BAD_SCHEMALOCATION','ANOMALY','BIT','TRUE','Badly formatted schema location value'),('A_XML_MULT_SCHEMA_TYPES','ANOMALY','NOTE','FALSE','Uses multiple schema types'),('A_XML_UNKNOWN_VERSION','ANOMALY','NOTE','FALSE','Unknown XML version'),('A_XML_VALIDATION_WARNING','ANOMALY','NOTE','TRUE','XML Validation Warning'),('I_PDF_ENCRYPTION','INHIBITOR','BIT','TRUE','Encrypts Strings and Streams'),('L_AVI_UNSUPPORTED_AUDIO_ENCODING','LIMITATION','BIT','TRUE','unsupported audio encoding'),('L_AVI_UNSUPPORTED_VIDEO_ENCODING','LIMITATION','BIT','TRUE','unsupported video encoding'),('L_PDF_UNSUPPORTED_RESOURCE','LIMITATION','BIT','TRUE','unsupported resource in the PDF file'),('L_QUICKTIME_UNSUPPORTED_AUDIO_ENCODING','LIMITATION','BIT','TRUE','unsupported audio encoding in QuickTime'),('L_QUICKTIME_UNSUPPORTED_COMPRESSED_HEADER','LIMITATION','BIT','TRUE','unsupported comrpessed header in the QuickTime file'),('L_QUICKTIME_UNSUPPORTED_VIDEO_ENCODING','LIMITATION','BIT','TRUE','unsupported video encoding in QuickTime'),('L_WAVE_UNSUPPORTED_ENCODING','LIMITATION','BIT','TRUE','unsupported wave audio encoding.'),('M_COLORSPACE_MISMATCH','METADATA_CONFLICT','NOTE','TRUE','metadata conflict in image colorspace'),('M_COMPRESSION_MISMATCH','METADATA_CONFLICT','NOTE','TRUE','metadata conflict in compression'),('M_CREATOR_PROGRAM_MISMATCH','METADATA_CONFLICT','NOTE','TRUE','conflict in creator program metadata'),('M_DEPTH_MISMATCH','METADATA_CONFLICT','NOTE','TRUE','metadata conflict in image depth'),('M_FILE_SIZE_MISMATCH','METADATA_CONFLICT','NOTE','TRUE','conflict in file size metadata'),('M_MIME_TYPE_MISMATCH','METADATA_CONFLICT','NOTE','TRUE','conflict in mime type metadata'),('M_ORIENTATION_MISMATCH','METADATA_CONFLICT','NOTE','TRUE','metadata conflict in image orientation'),('M_XDIMENSION_MISMATCH','METADATA_CONFLICT','NOTE','TRUE','metadata conflict in image X dimension'),('M_YDIMENSION_MISMATCH','METADATA_CONFLICT','NOTE','TRUE','metadata conflict in image Y dimension'),('P_PARENT_HAS_DIFFERENT_PRES_LEVEL','PRES_LEVEL_CONFLICT','NOTE','FALSE','For distributed objects, this severe element is recorded for children that have a different preservation level from the parent'),('Q_NORM_DATA_QUALITY_RESET','QUIRK','NOTE','FALSE','the data quality reset after normalization');
/*!40000 ALTER TABLE `SEVERE_ELEMENT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `SEVERITY`
--

LOCK TABLES `SEVERITY` WRITE;
/*!40000 ALTER TABLE `SEVERITY` DISABLE KEYS */;
INSERT INTO `SEVERITY` VALUES ('BIT','Archive with bitlevel preservation.'),('NOTE','Archive with depositor-specified preservation'),('REJECT','Reject the package'),('UNKNOWN','UNKNOWN');
/*!40000 ALTER TABLE `SEVERITY` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2007-02-20 19:21:13
