-- modify comments of rows
ALTER TABLE `DOCUMENT_LOCATION` MODIFY `CODE` varchar(64) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A unique alphanumeric code for the location';
ALTER TABLE `DOCUMENT_LOCATION` MODIFY `TYPE` enum('PHYSICAL','URL') collate utf8_unicode_ci NOT NULL default 'PHYSICAL' COMMENT 'The type of location, new types can be added as they become necessary';
ALTER TABLE `DOCUMENT_LOCATION` MODIFY `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A description for the location';
ALTER TABLE `SUB_ACCOUNT` MODIFY `ID` int(8) unsigned NOT NULL auto_increment COMMENT 'A unique identifier for this sub_account';
ALTER TABLE `SUB_ACCOUNT` MODIFY `CODE` varchar(32) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A meaningful code for this sub_account';
ALTER TABLE `SUB_ACCOUNT` MODIFY `DESCRIPTION` varchar(255) collate utf8_unicode_ci NOT NULL default '' COMMENT 'A textual description for this sub_account to be used during reporting';
ALTER TABLE `SUB_ACCOUNT` MODIFY `ACCOUNT` varchar(16) collate utf8_unicode_ci NOT NULL default '' COMMENT 'The account to which this subaccount is linked';
