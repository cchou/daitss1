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
-- Table structure for table `ACCOUNT`
--

DROP TABLE IF EXISTS `ACCOUNT`;
CREATE TABLE `ACCOUNT` (
  `CODE` varchar(16) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A unique code identifying an account (i.e.''UF'')',
  `NAME` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The full name of the organization or individual holding this account',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A description of the organization or individual holding this account',
  `ADMIN_CONTACT` int(8) unsigned NOT NULL default '0' COMMENT 'Reference to the contact responsible for administration and billing for this account',
  `TECH_CONTACT` int(8) unsigned NOT NULL default '0' COMMENT 'Reference to the contact responsibile for technical issues for this account',
  `REPORT_EMAIL` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The email address to which all reports will be sent for items associated with this account.',
  PRIMARY KEY  (`CODE`),
  KEY `ADMIN_CONTACT` (`ADMIN_CONTACT`),
  KEY `TECH_CONTACT` (`TECH_CONTACT`),
  CONSTRAINT `ACCOUNT_ibfk_3` FOREIGN KEY (`ADMIN_CONTACT`) REFERENCES `CONTACT` (`ID`),
  CONSTRAINT `ACCOUNT_ibfk_4` FOREIGN KEY (`TECH_CONTACT`) REFERENCES `CONTACT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `ACCOUNT_PROJECT`
--

DROP TABLE IF EXISTS `ACCOUNT_PROJECT`;
CREATE TABLE `ACCOUNT_PROJECT` (
  `ID` int(10) unsigned NOT NULL auto_increment COMMENT 'A unique code identifying an account and project combination',
  `ACCOUNT` varchar(16) collate utf8_unicode_ci NOT NULL default '' COMMENT 'An reference to an account code',
  `PROJECT` varchar(32) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A reference to a project code',
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `ACCOUNT` (`ACCOUNT`,`PROJECT`),
  KEY `PROJECT` (`PROJECT`),
  CONSTRAINT `ACCOUNT_PROJECT_ibfk_1` FOREIGN KEY (`ACCOUNT`) REFERENCES `ACCOUNT` (`CODE`),
  CONSTRAINT `ACCOUNT_PROJECT_ibfk_2` FOREIGN KEY (`PROJECT`) REFERENCES `PROJECT` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `ACTION_PLAN`
--

DROP TABLE IF EXISTS `ACTION_PLAN`;
CREATE TABLE `ACTION_PLAN` (
  `ID` int(10) unsigned NOT NULL auto_increment COMMENT 'A unique id for the action plan',
  `LOCATION` varchar(64) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The location of the action plan, whether it be physical or digital',
  `BACKGROUND_DOC` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The name/title of the document containing the background information that was used in creating the action plan. The background document is a summary of pertinent information regarding the format for which the action plan was created. In essence it is a li',
  `BACKGROUND_DOC_LOCATION` varchar(64) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The location of the background document whether it be physical or digital',
  `CREATE_DATE` date NOT NULL default '0000-00-00' COMMENT 'The date that the action plan was finalized',
  `REVIEW_DATE` date NOT NULL default '0000-00-00' COMMENT 'The future date when the action plan will be reviewed to determine if modification is necessary',
  `LAST_REVISION_DATE` date default NULL COMMENT 'The date that the action plan was last modified',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A short, general description of the action plan and the formats to which it applies',
  PRIMARY KEY  (`ID`),
  KEY `LOCATION` (`LOCATION`),
  KEY `BACKGROUND_DOC_LOCATION` (`BACKGROUND_DOC_LOCATION`),
  CONSTRAINT `ACTION_PLAN_ibfk_4` FOREIGN KEY (`LOCATION`) REFERENCES `DOCUMENT_LOCATION` (`CODE`),
  CONSTRAINT `ACTION_PLAN_ibfk_5` FOREIGN KEY (`BACKGROUND_DOC_LOCATION`) REFERENCES `DOCUMENT_LOCATION` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `ADMIN`
--

DROP TABLE IF EXISTS `ADMIN`;
CREATE TABLE `ADMIN` (
  `OID` char(16) collate utf8_unicode_ci NOT NULL default '',
  `INGEST_TIME` datetime NOT NULL default '0000-00-00 00:00:00',
  `ACCOUNT_PROJECT` int(10) unsigned NOT NULL default '0',
  `SUB_ACCOUNT` int(8) unsigned default '0',
  PRIMARY KEY  (`OID`),
  KEY `ACCOUNT_PROJECT` (`ACCOUNT_PROJECT`),
  KEY `SUB_ACCOUNT` (`SUB_ACCOUNT`),
  CONSTRAINT `ADMIN_ibfk_1` FOREIGN KEY (`ACCOUNT_PROJECT`) REFERENCES `ACCOUNT_PROJECT` (`ID`),
  CONSTRAINT `ADMIN_ibfk_2` FOREIGN KEY (`SUB_ACCOUNT`) REFERENCES `SUB_ACCOUNT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `ARCHIVE_LOGIC`
--

DROP TABLE IF EXISTS `ARCHIVE_LOGIC`;
CREATE TABLE `ARCHIVE_LOGIC` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `ACCOUNT_PROJECT` int(10) unsigned NOT NULL default '0',
  `START_DATE` date NOT NULL default '0000-00-00',
  `END_DATE` date default NULL,
  `MEDIA_TYPE` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `PRES_LEVEL` enum('NONE','BIT','FULL') collate utf8_unicode_ci NOT NULL default 'BIT',
  PRIMARY KEY  (`ID`),
  KEY `MEDIA_TYPE` (`MEDIA_TYPE`),
  KEY `ACCOUNT_PROJECT` (`ACCOUNT_PROJECT`),
  CONSTRAINT `ARCHIVE_LOGIC_ibfk_17` FOREIGN KEY (`ACCOUNT_PROJECT`) REFERENCES `ACCOUNT_PROJECT` (`ID`),
  CONSTRAINT `ARCHIVE_LOGIC_ibfk_18` FOREIGN KEY (`MEDIA_TYPE`) REFERENCES `MEDIA_TYPE` (`TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `AVI_FILE`
--

DROP TABLE IF EXISTS `AVI_FILE`;
CREATE TABLE `AVI_FILE` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The DataFile identifier',
  `MAX_DATA_RATE` int(10) unsigned NOT NULL default '0' COMMENT 'The maximum number of bytes per second that the system need to handle to render the data in the AVI files.',
  `TOTAL_FRAMES` int(10) unsigned NOT NULL default '0' COMMENT 'Assigned by the ingest program for original and normalized objects; assigned by the migration program for migrated objects. ',
  `NO_OF_STREAMS` int(10) unsigned NOT NULL default '0' COMMENT 'The number of streams (both video and audio) contained in AVI files.',
  `INITIAL_FRAMES` int(10) unsigned NOT NULL default '0' COMMENT 'The number of frames in the file prior to the initial frame of the AVI sequence.',
  `DURATION` time NOT NULL default '00:00:00' COMMENT 'The total duration to play back the data in the AVI file.',
  `HAS_INDEX` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE' COMMENT 'Indicates whether the AVI file contains an index chunk.',
  `MUST_USE_INDEX` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE' COMMENT 'Indicates whether the index chunk should be used to determine the presentation order.',
  `IS_INTERLEAVED` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE' COMMENT 'Indicates whether the AVI file is interleaved.',
  `WAS_CAPTURED` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE' COMMENT 'Indicates whether the AVI file is a special allocated file used for capturing real-time video.',
  `COPYRIGHTED` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE' COMMENT 'Indicates whether the AVI file contains copyrighted data.',
  PRIMARY KEY  (`DFID`),
  CONSTRAINT `AVI_FILE_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Used to store information pertaining to AVI files.';

--
-- Table structure for table `BILLING`
--

DROP TABLE IF EXISTS `BILLING`;
CREATE TABLE `BILLING` (
  `CODE` int(8) unsigned zerofill NOT NULL auto_increment,
  `ACCOUNT_PROJECT` int(10) unsigned NOT NULL default '0',
  `START_DATE` date NOT NULL default '0000-00-00',
  `END_DATE` date default NULL,
  `RATE_1` char(8) collate utf8_unicode_ci NOT NULL default '00000000',
  `RATE_2` char(8) collate utf8_unicode_ci NOT NULL default '00000000',
  PRIMARY KEY  (`CODE`),
  KEY `ACCOUNT_PROJECT` (`ACCOUNT_PROJECT`),
  CONSTRAINT `BILLING_ibfk_1` FOREIGN KEY (`ACCOUNT_PROJECT`) REFERENCES `ACCOUNT_PROJECT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BITSTREAM`
--

DROP TABLE IF EXISTS `BITSTREAM`;
CREATE TABLE `BITSTREAM` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `SEQUENCE` mediumint(7) unsigned NOT NULL default '0',
  `LOCATION` varchar(255) collate utf8_unicode_ci default NULL,
  `LOCATION_TYPE` enum('FILE_BYTE_OFFSET','UNCOMPRESSED_HEADER_BYTE_OFFSET','N/A','UNKNOWN') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `COMPRESSION` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `BS_TABLE` varchar(64) collate utf8_unicode_ci default NULL,
  `ROLE` enum('AUDIO_MAIN','AUDIO_PREVIEW','AUDIO_MOVIE','AUDIO_POSTER','DOC_MAIN','IMG_LOW_RES','IMG_MAIN','IMG_PAGE','IMG_THUMBNAIL','IMG_TRANSPARENCY','TXT_METADATA','UNKNOWN','VIDEO_MAIN','VIDEO_PREVIEW','VIDEO_MOVIE','VIDEO_POSTER') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  PRIMARY KEY  (`BSID`),
  KEY `COMPRESSION` (`COMPRESSION`),
  KEY `BS_TABLE` (`BS_TABLE`),
  KEY `DFID` (`DFID`),
  CONSTRAINT `BITSTREAM_ibfk_26` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE,
  CONSTRAINT `BITSTREAM_ibfk_27` FOREIGN KEY (`COMPRESSION`) REFERENCES `COMPRESSION` (`CODE`),
  CONSTRAINT `BITSTREAM_ibfk_28` FOREIGN KEY (`BS_TABLE`) REFERENCES `BS_TABLE` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BITSTREAM_BS_PROFILE`
--

DROP TABLE IF EXISTS `BITSTREAM_BS_PROFILE`;
CREATE TABLE `BITSTREAM_BS_PROFILE` (
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `BS_PROFILE` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`BSID`,`BS_PROFILE`),
  KEY `BS_PROFILE` (`BS_PROFILE`),
  CONSTRAINT `BITSTREAM_BS_PROFILE_ibfk_3` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE,
  CONSTRAINT `BITSTREAM_BS_PROFILE_ibfk_4` FOREIGN KEY (`BS_PROFILE`) REFERENCES `BS_PROFILE` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Associates bitsreams with the profiles to which they conform';

--
-- Table structure for table `BS_AUDIO`
--

DROP TABLE IF EXISTS `BS_AUDIO`;
CREATE TABLE `BS_AUDIO` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The Object ID of the archival (physical) object.',
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The Object ID of the bitstream.',
  `ENCODING` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The encoding scheme for the audio data',
  `SAMPLE_RATE` float NOT NULL default '0' COMMENT 'The number of samples to be processed in one second.',
  `SAMPLE_SIZE` int(10) unsigned NOT NULL default '0' COMMENT 'The number of bits used to represent each sample.  For audio data that are stored in variable bit rate format, this value would be 0.',
  `NO_OF_CHANNELS` int(10) unsigned NOT NULL default '0' COMMENT 'The number of individual channels in the audio stream.',
  `FRAME_RATE` float NOT NULL default '0' COMMENT 'The number of samples to be processed in one second for each audio frame.  This value is calculated by multiplying the sample rate by the number of audio channels.',
  `FRAME_SIZE` int(10) unsigned NOT NULL default '0' COMMENT 'The number of bits used to store each audio frame.  This value is calculated by multiplying the sample size by the number of audio channels.',
  `LENGTH` bigint(20) NOT NULL default '0' COMMENT 'The length of the audio stream (in samples).',
  `DATA_QUALITY` smallint(5) NOT NULL default '-1' COMMENT 'The quality of the data in the audio stream.  Defined by codec.',
  `DATA_RATE` float NOT NULL default '0' COMMENT 'The data rate of the audio stream expressed in bps(bits per second).',
  `BITRATE_MODE` enum('UNKNOWN','CBR','VBR') collate utf8_unicode_ci NOT NULL default 'UNKNOWN' COMMENT 'This describes the mode in which the audio data were encoded. It could be either constant Bitrate (CBR) or variable Bitrate (VBR). VBR means that the bitrate varies during the audio sequence, whereas CBR means that the bitrate does not vary but is the same through the entire audio sequence. ',
  `ENABLED` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE' COMMENT 'Indicates whether the audio stream is enabled by default.',
  `BS_TABLE` varchar(64) collate utf8_unicode_ci default NULL,
  PRIMARY KEY  (`BSID`),
  KEY `DFID` (`DFID`),
  KEY `BS_TABLE` (`BS_TABLE`),
  CONSTRAINT `BS_AUDIO_ibfk_6` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`),
  CONSTRAINT `BS_AUDIO_ibfk_7` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE,
  CONSTRAINT `BS_AUDIO_ibfk_8` FOREIGN KEY (`BS_TABLE`) REFERENCES `BS_TABLE` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='This table is used to store general information regarding an';

--
-- Table structure for table `BS_AUDIO_WAVE`
--

DROP TABLE IF EXISTS `BS_AUDIO_WAVE`;
CREATE TABLE `BS_AUDIO_WAVE` (
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `VALID_BITS_PER_SAMPLE` int(10) unsigned default NULL,
  `SAMPLES_PER_BLOCK` int(10) unsigned default NULL,
  PRIMARY KEY  (`BSID`),
  KEY `DFID` (`DFID`),
  CONSTRAINT `BS_AUDIO_WAVE_ibfk_1` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE,
  CONSTRAINT `BS_AUDIO_WAVE_ibfk_2` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Bitstream WAVE audio table';

--
-- Table structure for table `BS_IMAGE`
--

DROP TABLE IF EXISTS `BS_IMAGE`;
CREATE TABLE `BS_IMAGE` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `BITS_PER_SAMPLE` varchar(255) collate utf8_unicode_ci default NULL,
  `HAS_INTERNAL_CLUT` enum('TRUE','FALSE','UNKNOWN') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `EXTRA_SAMPLES` varchar(255) collate utf8_unicode_ci default NULL,
  `ORIENTATION` enum('BL','BR','LB','LT','RB','RT','TL','TR','UNKNOWN') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `COLOR_SPACE` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `IMAGE_WIDTH` int(10) unsigned NOT NULL default '0',
  `IMAGE_LENGTH` int(10) unsigned NOT NULL default '0',
  `HAS_ICC_PROFILE` enum('TRUE','FALSE','UNKNOWN') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `NUM_COMPONENTS` smallint(5) NOT NULL default '0',
  `RES_UNIT` enum('UNKNOWN','NONE','IN','CM','M') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `RES_HORZ` float NOT NULL default '0',
  `RES_VERT` float NOT NULL default '0',
  `SAMPLING_HOR` varchar(32) collate utf8_unicode_ci NOT NULL default '',
  `SAMPLING_VER` varchar(32) collate utf8_unicode_ci NOT NULL default '',
  `BS_TABLE` varchar(64) collate utf8_unicode_ci default NULL,
  PRIMARY KEY  (`BSID`),
  KEY `DFID` (`DFID`),
  KEY `BS_TABLE` (`BS_TABLE`),
  CONSTRAINT `BS_IMAGE_ibfk_4` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`),
  CONSTRAINT `BS_IMAGE_ibfk_5` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE,
  CONSTRAINT `BS_IMAGE_ibfk_6` FOREIGN KEY (`BS_TABLE`) REFERENCES `BS_TABLE` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BS_IMAGE_JPEG`
--

DROP TABLE IF EXISTS `BS_IMAGE_JPEG`;
CREATE TABLE `BS_IMAGE_JPEG` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `JPEG_PROCESS` enum('UNKNOWN','BASELINE','DIFF_LOSSLESS','DIFF_LOSSLESS_ARITH','DIFF_PROG','DIFF_PROG_ARITH','DIFF_SEQ','DIFF_SEQ_ARITH','EXT_SEQ','EXT_SEQ_ARITH','LOSSLESS','LOSSLESS_ARITH','PROG','PROG_ARITH') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `PIXEL_ASPECT_RATIO` float NOT NULL default '0',
  PRIMARY KEY  (`BSID`),
  KEY `DFID` (`DFID`),
  CONSTRAINT `BS_IMAGE_JPEG_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`),
  CONSTRAINT `BS_IMAGE_JPEG_ibfk_2` FOREIGN KEY (`BSID`) REFERENCES `BS_IMAGE` (`BSID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BS_IMAGE_JPEG2000`
--

DROP TABLE IF EXISTS `BS_IMAGE_JPEG2000`;
CREATE TABLE `BS_IMAGE_JPEG2000` (
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `HAS_ROI` enum('UNKNOWN','TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `PROG_ORDER` varchar(255) collate utf8_unicode_ci NOT NULL default '''UNKNOWN''',
  `NUM_TILES` int(10) unsigned default NULL,
  `TILE_LENGTH` int(10) unsigned default NULL,
  `TILE_WIDTH` int(10) unsigned default NULL,
  `WAVELET_TRANF_TYPE` varchar(255) collate utf8_unicode_ci NOT NULL default '''UNKNOWN''',
  PRIMARY KEY  (`BSID`),
  KEY `DFID` (`DFID`),
  CONSTRAINT `BS_IMAGE_JPEG2000_ibfk_3` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE,
  CONSTRAINT `BS_IMAGE_JPEG2000_ibfk_4` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BS_IMAGE_TIFF`
--

DROP TABLE IF EXISTS `BS_IMAGE_TIFF`;
CREATE TABLE `BS_IMAGE_TIFF` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `HAS_CHROMATICITIES` enum('UNKNOWN','TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `NUM_STRIPS` int(10) unsigned default NULL,
  `NUM_TILES` int(10) unsigned default NULL,
  `PLANAR_CONFIG` enum('UNKNOWN','CHUNKY','PLANAR') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `STORAGE_SEGMENT` enum('UNKNOWN','STRIP','TILE') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `MAX_STRIP_BYTES` int(10) unsigned default NULL,
  `ROWS_PER_STRIP` int(10) unsigned default NULL,
  `MAX_TILE_BYTES` int(10) unsigned default NULL,
  `TILE_LENGTH` int(10) unsigned default NULL,
  `TILE_WIDTH` int(10) unsigned default NULL,
  PRIMARY KEY  (`BSID`),
  KEY `DFID` (`DFID`),
  CONSTRAINT `BS_IMAGE_TIFF_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`),
  CONSTRAINT `BS_IMAGE_TIFF_ibfk_2` FOREIGN KEY (`BSID`) REFERENCES `BS_IMAGE` (`BSID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BS_MARKUP`
--

DROP TABLE IF EXISTS `BS_MARKUP`;
CREATE TABLE `BS_MARKUP` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `MARKUP_BASIS` enum('UNKNOWN','HTML','SGML','XML','N/A') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `SCHEMA_TYPE` enum('UNKNOWN','DTD','N/A','W3C_XML_SCHEMA') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `MARKUP_LANGUAGE` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `SCHEMA_DFID` varchar(16) collate utf8_unicode_ci default NULL,
  `VALID` enum('UNCHECKED','TRUE','FALSE','N/A') collate utf8_unicode_ci NOT NULL default 'UNCHECKED',
  `CHARSET` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `CHARSET_ORIGIN` enum('ARCHIVE','DEPOSITOR','N/A') collate utf8_unicode_ci NOT NULL default 'N/A',
  PRIMARY KEY  (`BSID`),
  KEY `SCHEMA_DFID` (`SCHEMA_DFID`),
  KEY `DFID` (`DFID`),
  CONSTRAINT `BS_MARKUP_ibfk_3` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`),
  CONSTRAINT `BS_MARKUP_ibfk_4` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BS_PDF`
--

DROP TABLE IF EXISTS `BS_PDF`;
CREATE TABLE `BS_PDF` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `NATL_LANG` varchar(128) collate utf8_unicode_ci NOT NULL default '',
  `NUM_PAGES` int(11) NOT NULL default '-1',
  `HAS_OUTLINE` enum('FALSE','TRUE','UNKNOWN') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `HAS_IMAGES` enum('FALSE','TRUE','UNKNOWN') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `HAS_METADATA` enum('FALSE','TRUE','UNKNOWN') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `NONSTANDARD_EMBEDDED_FONTS` text collate utf8_unicode_ci COMMENT 'A comma delimited list of alll nonstandard embedded fonts used in the PDF document.',
  `NONSTANDARD_UNEMBEDDED_FONTS` text collate utf8_unicode_ci COMMENT 'A comma delimited list of alll nonstandard unembedded fonts used in the PDF document.',
  PRIMARY KEY  (`BSID`),
  KEY `DFID` (`DFID`),
  CONSTRAINT `BS_PDF_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`),
  CONSTRAINT `BS_PDF_ibfk_2` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BS_PDF_ACTION`
--

DROP TABLE IF EXISTS `BS_PDF_ACTION`;
CREATE TABLE `BS_PDF_ACTION` (
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `PDF_ACTION` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`BSID`,`PDF_ACTION`),
  KEY `PDF_ACTION` (`PDF_ACTION`),
  CONSTRAINT `BS_PDF_ACTION_ibfk_3` FOREIGN KEY (`BSID`) REFERENCES `BS_PDF` (`BSID`) ON DELETE CASCADE,
  CONSTRAINT `BS_PDF_ACTION_ibfk_4` FOREIGN KEY (`PDF_ACTION`) REFERENCES `PDF_ACTION` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BS_PDF_ANNOTATION`
--

DROP TABLE IF EXISTS `BS_PDF_ANNOTATION`;
CREATE TABLE `BS_PDF_ANNOTATION` (
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `PDF_ANNOTATION` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`BSID`,`PDF_ANNOTATION`),
  KEY `PDF_ANNOTATION` (`PDF_ANNOTATION`),
  CONSTRAINT `BS_PDF_ANNOTATION_ibfk_3` FOREIGN KEY (`BSID`) REFERENCES `BS_PDF` (`BSID`) ON DELETE CASCADE,
  CONSTRAINT `BS_PDF_ANNOTATION_ibfk_4` FOREIGN KEY (`PDF_ANNOTATION`) REFERENCES `PDF_ANNOTATION` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BS_PDF_FILTER`
--

DROP TABLE IF EXISTS `BS_PDF_FILTER`;
CREATE TABLE `BS_PDF_FILTER` (
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `PDF_FILTER` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`BSID`,`PDF_FILTER`),
  KEY `PDF_FILTER` (`PDF_FILTER`),
  CONSTRAINT `BS_PDF_FILTER_ibfk_5` FOREIGN KEY (`BSID`) REFERENCES `BS_PDF` (`BSID`) ON DELETE CASCADE,
  CONSTRAINT `BS_PDF_FILTER_ibfk_6` FOREIGN KEY (`PDF_FILTER`) REFERENCES `PDF_FILTER` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BS_PROFILE`
--

DROP TABLE IF EXISTS `BS_PROFILE`;
CREATE TABLE `BS_PROFILE` (
  `NAME` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `SPECIFICATION` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`NAME`),
  KEY `SPECIFICATION` (`SPECIFICATION`),
  CONSTRAINT `BS_PROFILE_ibfk_1` FOREIGN KEY (`SPECIFICATION`) REFERENCES `SPECIFICATION` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Stores information about all recognized bitsream profiles re';

--
-- Table structure for table `BS_TABLE`
--

DROP TABLE IF EXISTS `BS_TABLE`;
CREATE TABLE `BS_TABLE` (
  `NAME` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BS_TEXT`
--

DROP TABLE IF EXISTS `BS_TEXT`;
CREATE TABLE `BS_TEXT` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `CHARSET` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `CHARSET_ORIGIN` enum('ARCHIVE','DEPOSITOR','N/A') collate utf8_unicode_ci NOT NULL default 'N/A',
  `LINE_BREAK` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `NATL_LANG` varchar(128) collate utf8_unicode_ci NOT NULL default '',
  `NUM_LINES` bigint(20) NOT NULL default '-1',
  PRIMARY KEY  (`BSID`),
  KEY `DFID` (`DFID`),
  CONSTRAINT `BS_TEXT_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`),
  CONSTRAINT `BS_TEXT_ibfk_2` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `BS_VIDEO`
--

DROP TABLE IF EXISTS `BS_VIDEO`;
CREATE TABLE `BS_VIDEO` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The Object ID of the archival (physical) object.',
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The Object ID of the bitstream.',
  `ENCODING` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The encoding scheme for the video data.',
  `FRAME_RATE` float NOT NULL default '0' COMMENT 'The number of frames per second.',
  `BITS_PER_PIXEL` smallint(5) unsigned NOT NULL default '0' COMMENT 'The number of bits used to store the data for each pixel.',
  `FRAME_HEIGHT` int(10) unsigned NOT NULL default '0' COMMENT 'The height in pixels for the video frames in this video stream.',
  `FRAME_WIDTH` int(10) unsigned NOT NULL default '0' COMMENT 'The width in pixels for the video frames in this video stream.',
  `RES_UNIT` enum('UNKNOWN','NONE','IN','CM','M') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `RES_HORZ` float NOT NULL default '0',
  `RES_VERT` float NOT NULL default '0',
  `LENGTH` bigint(20) NOT NULL default '0' COMMENT 'The length of the video stream (in frames).',
  `DATA_QUALITY` smallint(5) NOT NULL default '-1' COMMENT 'The quality of the data in the video stream.  Defined by codec.',
  `DATA_RATE` float NOT NULL default '0' COMMENT 'The data rate of the video stream expressed in bps(bits per second).',
  `BITRATE_MODE` enum('UNKNOWN','CBR','VBR') collate utf8_unicode_ci NOT NULL default 'UNKNOWN' COMMENT 'This described the mode in which the video data were encoded. It could be either constant Bitrate (CBR) or variable Bitrate (VBR). VBR means that the bitrate varies during the video clip, whereas CBR means that the bitrate does not vary during the video but is the same through the video clip.',
  `ENABLED` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE' COMMENT 'Indicates whether the video stream is enabled by default.',
  `BS_TABLE` varchar(64) collate utf8_unicode_ci default NULL,
  PRIMARY KEY  (`BSID`),
  KEY `DFID` (`DFID`),
  KEY `BS_TABLE` (`BS_TABLE`),
  CONSTRAINT `BS_VIDEO_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`),
  CONSTRAINT `BS_VIDEO_ibfk_2` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='This table is used to store general information regarding a ';

--
-- Table structure for table `COMPRESSION`
--

DROP TABLE IF EXISTS `COMPRESSION`;
CREATE TABLE `COMPRESSION` (
  `CODE` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `CONTACT`
--

DROP TABLE IF EXISTS `CONTACT`;
CREATE TABLE `CONTACT` (
  `ID` int(8) unsigned NOT NULL auto_increment,
  `NAME` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The full name of the contact, First name [middle name/initial] Last name',
  `ADDR_L1` varchar(128) collate utf8_unicode_ci NOT NULL default '',
  `ADDR_L2` varchar(128) collate utf8_unicode_ci NOT NULL default '',
  `ADDR_L3` varchar(128) collate utf8_unicode_ci default NULL,
  `ADDR_L4` varchar(128) collate utf8_unicode_ci default NULL,
  `ADDR_L5` varchar(128) collate utf8_unicode_ci default NULL,
  `EMAIL` varchar(128) collate utf8_unicode_ci NOT NULL default '',
  `PHONE` varchar(32) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `DATA_FILE`
--

DROP TABLE IF EXISTS `DATA_FILE`;
CREATE TABLE `DATA_FILE` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `IEID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `CREATE_DATE` datetime NOT NULL default '0000-00-00 00:00:00',
  `FILE_COPY_DATE` datetime NOT NULL default '0000-00-00 00:00:00',
  `DIP_VERSION` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `ORIGIN` enum('ARCHIVE','DEPOSITOR','INTERNET','UNKNOWN') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `ORIG_URI` varchar(255) collate utf8_unicode_ci default NULL,
  `PACKAGE_PATH` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `FILE_TITLE` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `FILE_EXT` varchar(8) collate utf8_unicode_ci NOT NULL default '',
  `FORMAT` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `CONTENT_TYPE` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `CREATOR_PROG` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `SIZE` bigint(20) NOT NULL default '-1',
  `BYTE_ORDER` enum('UNKNOWN','LE','BE','N/A') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `IS_ROOT` enum('TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `IS_GLOBAL` enum('TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `IS_OBSOLETE` enum('TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `CAN_DELETE` enum('TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `ROLE` enum('DESCRIPTOR_AIP','DESCRIPTOR_DIP','DESCRIPTOR_GFP','DESCRIPTOR_SIP','SCHEMA','CONTENT_FILE','UNKNOWN') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `PRES_LEVEL` enum('BIT','FULL') collate utf8_unicode_ci NOT NULL default 'BIT',
  PRIMARY KEY  (`DFID`),
  KEY `IEID` (`IEID`),
  KEY `FORMAT_CODE` (`FORMAT`),
  CONSTRAINT `DATA_FILE_ibfk_45` FOREIGN KEY (`DFID`) REFERENCES `ADMIN` (`OID`),
  CONSTRAINT `DATA_FILE_ibfk_46` FOREIGN KEY (`IEID`) REFERENCES `INT_ENTITY` (`IEID`),
  CONSTRAINT `DATA_FILE_ibfk_47` FOREIGN KEY (`FORMAT`) REFERENCES `FORMAT` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `DATA_FILE_FORMAT_ATTRIBUTE`
--

DROP TABLE IF EXISTS `DATA_FILE_FORMAT_ATTRIBUTE`;
CREATE TABLE `DATA_FILE_FORMAT_ATTRIBUTE` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `FORMAT_ATTRIBUTE` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`DFID`,`FORMAT_ATTRIBUTE`),
  KEY `FORMAT_ATTRIBUTE` (`FORMAT_ATTRIBUTE`),
  CONSTRAINT `DATA_FILE_FORMAT_ATTRIBUTE_ibfk_3` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE,
  CONSTRAINT `DATA_FILE_FORMAT_ATTRIBUTE_ibfk_4` FOREIGN KEY (`FORMAT_ATTRIBUTE`) REFERENCES `FORMAT_ATTRIBUTE` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Associates data files with format-specific attributes that t';

--
-- Table structure for table `DATA_FILE_SEVERE_ELEMENT`
--

DROP TABLE IF EXISTS `DATA_FILE_SEVERE_ELEMENT`;
CREATE TABLE `DATA_FILE_SEVERE_ELEMENT` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `SEVERE_ELEMENT` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`DFID`,`SEVERE_ELEMENT`),
  KEY `SEVERE_ELEMENT` (`SEVERE_ELEMENT`),
  CONSTRAINT `DATA_FILE_SEVERE_ELEMENT_ibfk_3` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE,
  CONSTRAINT `DATA_FILE_SEVERE_ELEMENT_ibfk_4` FOREIGN KEY (`SEVERE_ELEMENT`) REFERENCES `SEVERE_ELEMENT` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `DATA_VERIFICATION`
--

DROP TABLE IF EXISTS `DATA_VERIFICATION`;
CREATE TABLE `DATA_VERIFICATION` (
  `ID` int(11) unsigned NOT NULL auto_increment COMMENT 'Identifier for the verification test',
  `TYPE` enum('FIXITY','VALIDITY','FRESHNESS','INTEGRITY','GLOBAL_FILE') collate utf8_unicode_ci NOT NULL default 'FIXITY' COMMENT 'The type of verification test that was executed',
  `OUTCOME` enum('PASS','FAIL') collate utf8_unicode_ci NOT NULL default 'FAIL' COMMENT 'Whether or not the test case was passed',
  `DATE_TIME` datetime NOT NULL default '0000-00-00 00:00:00' COMMENT 'The time that the verification test ended',
  `OID` char(16) collate utf8_unicode_ci default NULL COMMENT 'The identifier (if any) of the archive entity against which the test was run',
  PRIMARY KEY  (`ID`),
  KEY `OID` (`OID`),
  CONSTRAINT `DATA_VERIFICATION_ibfk_1` FOREIGN KEY (`OID`) REFERENCES `ADMIN` (`OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Data verification tests including storage management check';

--
-- Table structure for table `DISTRIBUTED`
--

DROP TABLE IF EXISTS `DISTRIBUTED`;
CREATE TABLE `DISTRIBUTED` (
  `PARENT` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `LINKS` text collate utf8_unicode_ci NOT NULL,
  `BROKEN_LINKS` text collate utf8_unicode_ci NOT NULL,
  `IGNORED_LINKS` text collate utf8_unicode_ci NOT NULL,
  PRIMARY KEY  (`PARENT`),
  CONSTRAINT `DISTRIBUTED_ibfk_1` FOREIGN KEY (`PARENT`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `DOCUMENT_LOCATION`
--

DROP TABLE IF EXISTS `DOCUMENT_LOCATION`;
CREATE TABLE `DOCUMENT_LOCATION` (
  `CODE` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `TYPE` enum('PHYSICAL','URL') collate utf8_unicode_ci NOT NULL default 'PHYSICAL',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='For FDA-created documents only(act. plan),not archived docs.';

--
-- Table structure for table `EVENT`
--

DROP TABLE IF EXISTS `EVENT`;
CREATE TABLE `EVENT` (
  `ID` bigint(20) NOT NULL default '0',
  `OID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `EVENT_TYPE` enum('CPD','CPU','CV','D','DEL','DLK','I','L','M','N','RM','UNKNOWN','VC','WA','WO') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `DATE_TIME` datetime NOT NULL default '0000-00-00 00:00:00',
  `EVENT_PROCEDURE` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `OUTCOME` enum('UNKNOWN','N/A','SUCCESS','P_SUCCESS','FAIL') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `NOTE` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `REL_OID` varchar(16) collate utf8_unicode_ci default NULL,
  PRIMARY KEY  (`ID`),
  KEY `REL_OID` (`REL_OID`),
  KEY `OID` (`OID`),
  CONSTRAINT `EVENT_ibfk_1` FOREIGN KEY (`OID`) REFERENCES `ADMIN` (`OID`),
  CONSTRAINT `EVENT_ibfk_2` FOREIGN KEY (`REL_OID`) REFERENCES `ADMIN` (`OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `FORMAT`
--

DROP TABLE IF EXISTS `FORMAT`;
CREATE TABLE `FORMAT` (
  `CODE` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `MEDIA_TYPE` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `MEDIA_TYPE_VERSION` varchar(32) collate utf8_unicode_ci NOT NULL default '',
  `VARIATION` varchar(20) collate utf8_unicode_ci NOT NULL default '',
  `IS_SUPPORTED` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `ACTION_PLAN` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`CODE`),
  UNIQUE KEY `MEDIA_TYPE` (`MEDIA_TYPE`,`MEDIA_TYPE_VERSION`,`VARIATION`),
  KEY `ACTION_PLAN` (`ACTION_PLAN`),
  CONSTRAINT `FORMAT_ibfk_2` FOREIGN KEY (`MEDIA_TYPE`) REFERENCES `MEDIA_TYPE` (`TYPE`),
  CONSTRAINT `FORMAT_ibfk_3` FOREIGN KEY (`ACTION_PLAN`) REFERENCES `ACTION_PLAN` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `FORMAT_ATTRIBUTE`
--

DROP TABLE IF EXISTS `FORMAT_ATTRIBUTE`;
CREATE TABLE `FORMAT_ATTRIBUTE` (
  `NAME` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `FORMAT` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`NAME`,`FORMAT`),
  KEY `FORMAT_CODE` (`FORMAT`),
  CONSTRAINT `FORMAT_ATTRIBUTE_ibfk_1` FOREIGN KEY (`FORMAT`) REFERENCES `FORMAT` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `FORMAT_SPECIFICATION`
--

DROP TABLE IF EXISTS `FORMAT_SPECIFICATION`;
CREATE TABLE `FORMAT_SPECIFICATION` (
  `FORMAT` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `SPECIFICATION` int(10) unsigned NOT NULL default '0',
  `AUTHORITY_LEVEL` enum('INFORMATIONAL','SUPPLEMENTAL','PRIMARY') collate utf8_unicode_ci NOT NULL default 'INFORMATIONAL' COMMENT 'An indicator of the authoritativeness of the specification with respect to the format it describes. Since some specifications may apply to more than one format, the authority level is variable depending on context.',
  PRIMARY KEY  (`FORMAT`,`SPECIFICATION`),
  KEY `SPECIFICATION` (`SPECIFICATION`),
  CONSTRAINT `FORMAT_SPECIFICATION_ibfk_1` FOREIGN KEY (`FORMAT`) REFERENCES `FORMAT` (`CODE`),
  CONSTRAINT `FORMAT_SPECIFICATION_ibfk_2` FOREIGN KEY (`SPECIFICATION`) REFERENCES `SPECIFICATION` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `GLOBAL_FILE`
--

DROP TABLE IF EXISTS `GLOBAL_FILE`;
CREATE TABLE `GLOBAL_FILE` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `USE_COUNT` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`DFID`),
  CONSTRAINT `0_923` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `INT_ENTITY`
--

DROP TABLE IF EXISTS `INT_ENTITY`;
CREATE TABLE `INT_ENTITY` (
  `IEID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `PACKAGE_NAME` varchar(32) collate utf8_unicode_ci NOT NULL default '',
  `EXT_REC` varchar(64) collate utf8_unicode_ci default NULL,
  `EXT_REC_TYPE` varchar(64) collate utf8_unicode_ci default NULL,
  `ENTITY_ID` varchar(16) collate utf8_unicode_ci default NULL,
  `VOL` varchar(4) collate utf8_unicode_ci default NULL,
  `ISSUE` varchar(3) collate utf8_unicode_ci default NULL,
  `TITLE` varchar(255) collate utf8_unicode_ci default NULL,
  PRIMARY KEY  (`IEID`),
  KEY `ENTITY_ID` (`ENTITY_ID`),
  CONSTRAINT `0_929` FOREIGN KEY (`IEID`) REFERENCES `ADMIN` (`OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `INT_ENTITY_GLOBAL_FILE`
--

DROP TABLE IF EXISTS `INT_ENTITY_GLOBAL_FILE`;
CREATE TABLE `INT_ENTITY_GLOBAL_FILE` (
  `IEID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`IEID`,`DFID`),
  KEY `DFID` (`DFID`),
  CONSTRAINT `INT_ENTITY_GLOBAL_FILE_ibfk_1` FOREIGN KEY (`IEID`) REFERENCES `INT_ENTITY` (`IEID`),
  CONSTRAINT `INT_ENTITY_GLOBAL_FILE_ibfk_2` FOREIGN KEY (`DFID`) REFERENCES `GLOBAL_FILE` (`DFID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `MEDIA_TYPE`
--

DROP TABLE IF EXISTS `MEDIA_TYPE`;
CREATE TABLE `MEDIA_TYPE` (
  `TYPE` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `MESSAGE_DIGEST`
--

DROP TABLE IF EXISTS `MESSAGE_DIGEST`;
CREATE TABLE `MESSAGE_DIGEST` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `CODE` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `VALUE` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `ORIGIN` enum('UNKNOWN','ARCHIVE','DEPOSITOR') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  PRIMARY KEY  (`DFID`,`CODE`),
  KEY `CODE` (`CODE`),
  CONSTRAINT `MESSAGE_DIGEST_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE,
  CONSTRAINT `MESSAGE_DIGEST_ibfk_2` FOREIGN KEY (`CODE`) REFERENCES `MESSAGE_DIGEST_TYPE` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `MESSAGE_DIGEST_TYPE`
--

DROP TABLE IF EXISTS `MESSAGE_DIGEST_TYPE`;
CREATE TABLE `MESSAGE_DIGEST_TYPE` (
  `CODE` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `OUTPUT_REQUEST`
--

DROP TABLE IF EXISTS `OUTPUT_REQUEST`;
CREATE TABLE `OUTPUT_REQUEST` (
  `ID` int(8) unsigned NOT NULL auto_increment COMMENT 'A unique identifier for this account/contact combination',
  `ACCOUNT` varchar(16) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A reference to the account for which a contact is allowed to request output services',
  `CONTACT` int(8) unsigned NOT NULL default '0' COMMENT 'A reference  to the contact allowed to request output services',
  `CAN_REQUEST_REPORT` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE' COMMENT 'Specifies whether this contact can request reports on behalf of this account',
  `CAN_REQUEST_DISSEMINATION` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE' COMMENT 'Specifies whether this contact can request dissemination of archive entities on behalf of this account',
  `CAN_REQUEST_WITHDRAWAL` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE' COMMENT 'Specifies whether this contact can request withdrawal of intellectual entites on behalf of this account',
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `ACCOUNT_2` (`ACCOUNT`,`CONTACT`),
  KEY `ACCOUNT` (`ACCOUNT`),
  KEY `CONTACT` (`CONTACT`),
  CONSTRAINT `OUTPUT_REQUEST_ibfk_1` FOREIGN KEY (`ACCOUNT`) REFERENCES `ACCOUNT` (`CODE`),
  CONSTRAINT `OUTPUT_REQUEST_ibfk_2` FOREIGN KEY (`CONTACT`) REFERENCES `CONTACT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `PDF_ACTION`
--

DROP TABLE IF EXISTS `PDF_ACTION`;
CREATE TABLE `PDF_ACTION` (
  `NAME` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `TYPE` enum('ACTIVATION','ANNOTATION','DOCUMENT','FORM_FIELD','PAGE') collate utf8_unicode_ci NOT NULL default 'PAGE',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `PDF_ANNOTATION`
--

DROP TABLE IF EXISTS `PDF_ANNOTATION`;
CREATE TABLE `PDF_ANNOTATION` (
  `NAME` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `PDF_FILTER`
--

DROP TABLE IF EXISTS `PDF_FILTER`;
CREATE TABLE `PDF_FILTER` (
  `NAME` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `PROGRAM`
--

DROP TABLE IF EXISTS `PROGRAM`;
CREATE TABLE `PROGRAM` (
  `CODE` int(10) unsigned NOT NULL auto_increment,
  `NAME` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `START_DATE` datetime NOT NULL default '0000-00-00 00:00:00',
  `END_DATE` datetime default NULL,
  `EXT_NAMES` text collate utf8_unicode_ci,
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `PROJECT`
--

DROP TABLE IF EXISTS `PROJECT`;
CREATE TABLE `PROJECT` (
  `CODE` varchar(32) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A meaningful, unique code for this project',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A meaningful description of this project that may be used for reporting',
  PRIMARY KEY  (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `QUICKTIME_FILE`
--

DROP TABLE IF EXISTS `QUICKTIME_FILE`;
CREATE TABLE `QUICKTIME_FILE` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '''''',
  `DURATION` time NOT NULL default '00:00:00',
  `HAS_DISPLAY_MATRIX` enum('TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `HAS_USERDATA` enum('TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `HAS_COLORTABLE` enum('TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `HAS_COMPRESSED_RESOURCE` enum('TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  PRIMARY KEY  (`DFID`),
  CONSTRAINT `QUICKTIME_FILE_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='information pertaining to QuickTime files';

--
-- Table structure for table `RELATIONSHIP`
--

DROP TABLE IF EXISTS `RELATIONSHIP`;
CREATE TABLE `RELATIONSHIP` (
  `DFID_1` char(16) collate utf8_unicode_ci NOT NULL default '',
  `REL_TYPE` enum('CHILD_OF','LOCALIZED_TO','MIGRATED_TO','NORM_TO','UNKNOWN') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `DFID_2` char(16) collate utf8_unicode_ci NOT NULL default '',
  `EVENT_ID` bigint(20) default NULL,
  KEY `DFID_2` (`DFID_2`),
  KEY `DFID_1` (`DFID_1`),
  KEY `EVENT_ID` (`EVENT_ID`),
  CONSTRAINT `RELATIONSHIP_ibfk_11` FOREIGN KEY (`DFID_1`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE,
  CONSTRAINT `RELATIONSHIP_ibfk_12` FOREIGN KEY (`DFID_2`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE,
  CONSTRAINT `RELATIONSHIP_ibfk_13` FOREIGN KEY (`EVENT_ID`) REFERENCES `EVENT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `REPORT`
--

DROP TABLE IF EXISTS `REPORT`;
CREATE TABLE `REPORT` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `TYPE` enum('UNKNOWN','ERROR','INGEST','WITHDRAWAL','DISSEMINATE') collate utf8_unicode_ci NOT NULL default 'UNKNOWN',
  `DATE` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `REPORT_INT_ENTITY`
--

DROP TABLE IF EXISTS `REPORT_INT_ENTITY`;
CREATE TABLE `REPORT_INT_ENTITY` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `REPORT` int(10) unsigned NOT NULL default '0',
  `INT_ENTITY` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`ID`),
  KEY `REPORT` (`REPORT`),
  KEY `IEID` (`INT_ENTITY`),
  CONSTRAINT `REPORT_INT_ENTITY_ibfk_1` FOREIGN KEY (`REPORT`) REFERENCES `REPORT` (`ID`),
  CONSTRAINT `REPORT_INT_ENTITY_ibfk_2` FOREIGN KEY (`INT_ENTITY`) REFERENCES `INT_ENTITY` (`IEID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Report-Service relation';

--
-- Table structure for table `SEVERE_ELEMENT`
--

DROP TABLE IF EXISTS `SEVERE_ELEMENT`;
CREATE TABLE `SEVERE_ELEMENT` (
  `CODE` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `TYPE` enum('ANOMALY','INHIBITOR','LIMITATION','METADATA_CONFLICT','PRES_LEVEL_CONFLICT','QUIRK') collate utf8_unicode_ci NOT NULL default 'ANOMALY',
  `SEVERITY` varchar(32) collate utf8_unicode_ci NOT NULL default '',
  `REPORT` enum('TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`CODE`),
  KEY `SEVERITY` (`SEVERITY`),
  CONSTRAINT `SEVERE_ELEMENT_ibfk_1` FOREIGN KEY (`SEVERITY`) REFERENCES `SEVERITY` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `SEVERITY`
--

DROP TABLE IF EXISTS `SEVERITY`;
CREATE TABLE `SEVERITY` (
  `CODE` varchar(32) collate utf8_unicode_ci NOT NULL default '',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `SPECIFICATION`
--

DROP TABLE IF EXISTS `SPECIFICATION`;
CREATE TABLE `SPECIFICATION` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `NUMBER` varchar(64) collate utf8_unicode_ci default NULL COMMENT 'The alpha-numeric code assigned to the document by its publisher/issuing body. For example, "ISO-8859-15"',
  `NAME` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A human-readable name of the specification. For specifications with titles, name will take the form title:subtitle.',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A short description of the specification, including information such as notes (i.e. â€œCD incuded in back, inside coverâ€?).',
  `DATE` date default NULL,
  `MEDIUM` enum('PAPER-BOUND','PAPER-UNBOUND','BINDER','BOOK','CD','DVD') collate utf8_unicode_ci NOT NULL default 'PAPER-BOUND',
  `SUBJECT` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `AUTHOR_LIST` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `PUBLISHER_LIST` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `CALL_NUMBER` varchar(64) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The document''s location in the specification library',
  `PAGE_COUNT` smallint(5) unsigned default NULL,
  PRIMARY KEY  (`ID`),
  KEY `LOCATION` (`CALL_NUMBER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `STORAGE_DESC`
--

DROP TABLE IF EXISTS `STORAGE_DESC`;
CREATE TABLE `STORAGE_DESC` (
  `ID` bigint(20) unsigned NOT NULL auto_increment COMMENT 'A unique id representing a storage description.',
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The DataFile whose description is recorded.',
  `STORAGE_INSTANCE` int(10) unsigned NOT NULL default '0' COMMENT 'The storage instance (device) on which this description is stored.',
  `IDENTIFIER` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A string that uniquely identifies a item within a storage instance. This corresponds to the identifier portion of a URL.',
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `STORAGE_INSTANCE` (`STORAGE_INSTANCE`,`IDENTIFIER`),
  KEY `DFID` (`DFID`),
  CONSTRAINT `STORAGE_DESC_ibfk_3` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE,
  CONSTRAINT `STORAGE_DESC_ibfk_4` FOREIGN KEY (`STORAGE_INSTANCE`) REFERENCES `STORAGE_INSTANCE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Used to store information about how and where DataFiles are ';

--
-- Table structure for table `STORAGE_DESC_PREP`
--

DROP TABLE IF EXISTS `STORAGE_DESC_PREP`;
CREATE TABLE `STORAGE_DESC_PREP` (
  `ID` bigint(20) unsigned NOT NULL auto_increment COMMENT 'A unique id for this combination of storage description and storage preparation process.',
  `STORAGE_DESC` bigint(20) unsigned NOT NULL default '0' COMMENT 'The storage description to which associated storage processes were applied.',
  `STORAGE_PREP` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A process applied to the data file in preparation for storage.',
  `SEQUENCE` tinyint(3) unsigned NOT NULL default '0' COMMENT 'This order of this storage process in a list of preparation processes.',
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `STORAGE_DESC` (`STORAGE_DESC`,`SEQUENCE`),
  KEY `STORAGE_PREP` (`STORAGE_PREP`),
  CONSTRAINT `STORAGE_DESC_PREP_ibfk_3` FOREIGN KEY (`STORAGE_DESC`) REFERENCES `STORAGE_DESC` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `STORAGE_DESC_PREP_ibfk_4` FOREIGN KEY (`STORAGE_PREP`) REFERENCES `STORAGE_PREP` (`PROCESS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Associates a storage description (which records information ';

--
-- Table structure for table `STORAGE_INSTANCE`
--

DROP TABLE IF EXISTS `STORAGE_INSTANCE`;
CREATE TABLE `STORAGE_INSTANCE` (
  `ID` int(10) unsigned NOT NULL auto_increment COMMENT 'A unique identifier for the storage device. Each ID value must be associated with a storage device inside the properties file to facilitate database inserts',
  `METHOD` enum('TIVOLI') collate utf8_unicode_ci NOT NULL default 'TIVOLI' COMMENT 'The top-level identifier for an storage device. This corresponds to the machine-type or possibly to a storage management system. Although there is currently only one type of storage device used, this is expected to grow in the future. This corresponds to the ''scheme'' portion of a URI, or ''protocol'' portion of a URL.',
  `INSTANCE` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A string that uniquely identifies storage devices of the same type. For Tivoli Storage Manager, this implies inclusion of the management class name. This corresponds to the ''instance'' portion of a URI, or the ''domain'' portion of a URL. Other information that might  be included in this field is from_node as needed by Tivoli.',
  `ENABLED` enum('TRUE','FALSE') collate utf8_unicode_ci NOT NULL default 'TRUE' COMMENT 'A boolean representing whether this storage instance is one that is currently used.',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'General description of the storage instance.',
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `unique_instance` (`METHOD`,`INSTANCE`),
  KEY `ENABLED` (`ENABLED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Tuples stored in this table represent a back-end storage dev';

--
-- Table structure for table `STORAGE_PREP`
--

DROP TABLE IF EXISTS `STORAGE_PREP`;
CREATE TABLE `STORAGE_PREP` (
  `PROCESS` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `INVERSE` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`PROCESS`),
  KEY `INVERSE` (`INVERSE`),
  CONSTRAINT `STORAGE_PREP_ibfk_1` FOREIGN KEY (`INVERSE`) REFERENCES `STORAGE_PREP` (`PROCESS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `SUB_ACCOUNT`
--

DROP TABLE IF EXISTS `SUB_ACCOUNT`;
CREATE TABLE `SUB_ACCOUNT` (
  `ID` int(8) unsigned NOT NULL auto_increment,
  `CODE` varchar(32) collate utf8_unicode_ci NOT NULL default '',
  `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `ACCOUNT` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`ID`),
  KEY `ACCOUNT` (`ACCOUNT`),
  CONSTRAINT `SUB_ACCOUNT_ibfk_1` FOREIGN KEY (`ACCOUNT`) REFERENCES `ACCOUNT` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `SUPPORTING_SPECIFICATION`
--

DROP TABLE IF EXISTS `SUPPORTING_SPECIFICATION`;
CREATE TABLE `SUPPORTING_SPECIFICATION` (
  `SPECIFICATION` int(10) unsigned NOT NULL default '0' COMMENT 'The referencing specification',
  `SUPPORTING_SPECIFICATION` int(10) unsigned NOT NULL default '0' COMMENT 'The referenced specification',
  PRIMARY KEY  (`SPECIFICATION`,`SUPPORTING_SPECIFICATION`),
  KEY `SUPPORTING_SPECIFICATION` (`SUPPORTING_SPECIFICATION`),
  CONSTRAINT `SUPPORTING_SPECIFICATION_ibfk_1` FOREIGN KEY (`SPECIFICATION`) REFERENCES `SPECIFICATION` (`ID`),
  CONSTRAINT `SUPPORTING_SPECIFICATION_ibfk_2` FOREIGN KEY (`SUPPORTING_SPECIFICATION`) REFERENCES `SPECIFICATION` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `WAVE_FILE`
--

DROP TABLE IF EXISTS `WAVE_FILE`;
CREATE TABLE `WAVE_FILE` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `HAS_FACT_CHUNK` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `HAS_PLAYLIST_CHUNK` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `HAS_LABEL_CHUNK` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `HAS_NOTE_CHUNK` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `HAS_LTXT_CHUNK` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `HAS_FILE_CHUNK` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `HAS_SAMPLE_CHUNK` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `HAS_INSTRUMENT_CHUNK` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  `HAS_CUE_CHUNK` enum('FALSE','TRUE') collate utf8_unicode_ci NOT NULL default 'FALSE',
  PRIMARY KEY  (`DFID`),
  CONSTRAINT `WAVE_FILE_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='information pertaining to WAVE files';
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2007-02-20 19:19:22
