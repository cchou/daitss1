
-- insert the required format information for CSV
INSERT into MEDIA_TYPE set TYPE = 'text/csv', DESCRIPTION = 'Comma Seperated Value file, RFC 4180';
INSERT into FORMAT set CODE = 'TXT_CSV', MEDIA_TYPE = 'text/csv', MEDIA_TYPE_VERSION = 'N/A', SUPPORT_LEVEL = 'FULLY_SUPPORTED', ACTION_PLAN = 1;
insert into SEVERE_ELEMENT set CODE = 'A_CSV_VARIABLE_COLUMNS', TYPE = 'ANOMALY', SEVERITY = 'NOTE', REPORT = 'TRUE', DESCRIPTION = 'CSV contains variable columns in each row';
insert into SEVERE_ELEMENT set CODE = 'A_CSV_BAD_ESCAPE_CHAR', TYPE = 'ANOMALY', SEVERITY = 'NOTE', REPORT = 'TRUE', DESCRIPTION = 'use non-conformed escape character for doube quote in quoted string';
insert into SEVERE_ELEMENT set CODE = 'A_CSV_ROW_ENDED_WITH_COMMA', TYPE = 'ANOMALY', SEVERITY = 'NOTE', REPORT = 'TRUE', DESCRIPTION = 'a row was ended with comma (not conformed to RFC 4180)';

DROP TABLE IF EXISTS `BS_TEXT_CSV`;

-- create BS_TEXT_CSV table
CREATE TABLE `BS_TEXT_CSV` (
  `DFID` varchar(16) collate utf8_unicode_ci NOT NULL default '',
  `BSID` varchar(24) collate utf8_unicode_ci NOT NULL default '',
  `NUM_ROWS` bigint(20) unsigned NOT NULL default '0',
  `NUM_COLUMNS` varchar(255) collate utf8_unicode_ci default NULL,
  PRIMARY KEY  (`BSID`),
  KEY `DFID` (`DFID`),
  CONSTRAINT `BS_TEXT_CSV_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`),
  CONSTRAINT `BS_TEXT_CSV_ibfk_2` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

insert into BS_TABLE set NAME = 'BS_TEXT_CSV', DESCRIPTION = 'bitstream table for Text CSV';

UPDATE DATABASE_VERSION SET MIGRATION_NUMBER=27;
