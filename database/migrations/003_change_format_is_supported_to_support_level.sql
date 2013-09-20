-- rename IS_SUPPORTED to SUPPORT_LEVEL
-- migrate TRUE -> FULLY_SUPPORTED or RECOGNIZED
-- migrate FALSE -> UNSUPPORTED

-- make a unified SUPPORT_LEVEL
ALTER TABLE `FORMAT` CHANGE `IS_SUPPORTED` `SUPPORT_LEVEL` enum('FULLY_SUPPORTED','RECOGNIZED','UNSUPPORTED','FALSE','TRUE') NOT NULL;

-- updates for FORMAT
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'APP_PDF_1_0', `MEDIA_TYPE_VERSION` = '1.0', `MEDIA_TYPE` = 'application/pdf' WHERE `CODE` = 'APP_PDF_1_0';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'APP_PDF_1_1', `MEDIA_TYPE_VERSION` = '1.1', `MEDIA_TYPE` = 'application/pdf' WHERE `CODE` = 'APP_PDF_1_1';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'APP_PDF_1_2', `MEDIA_TYPE_VERSION` = '1.2', `MEDIA_TYPE` = 'application/pdf' WHERE `CODE` = 'APP_PDF_1_2';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'APP_PDF_1_3', `MEDIA_TYPE_VERSION` = '1.3', `MEDIA_TYPE` = 'application/pdf' WHERE `CODE` = 'APP_PDF_1_3';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'APP_PDF_1_4', `MEDIA_TYPE_VERSION` = '1.4', `MEDIA_TYPE` = 'application/pdf' WHERE `CODE` = 'APP_PDF_1_4';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'APP_PDF_1_5', `MEDIA_TYPE_VERSION` = '1.5', `MEDIA_TYPE` = 'application/pdf' WHERE `CODE` = 'APP_PDF_1_5';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'APP_PDF_1_6', `MEDIA_TYPE_VERSION` = '1.6', `MEDIA_TYPE` = 'application/pdf' WHERE `CODE` = 'APP_PDF_1_6';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'RECOGNIZED', `CODE` = 'APP_UNK', `MEDIA_TYPE_VERSION` = '', `MEDIA_TYPE` = 'application/octet-stream' WHERE `CODE` = 'APP_UNK';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'APP_XML_1_0', `MEDIA_TYPE_VERSION` = '1.0', `MEDIA_TYPE` = 'application/xml' WHERE `CODE` = 'APP_XML_1_0';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'APP_XMLDTD_1_0', `MEDIA_TYPE_VERSION` = '1.0', `MEDIA_TYPE` = 'application/xml-dtd' WHERE `CODE` = 'APP_XMLDTD_1_0';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'AUDIO_WAVE', `MEDIA_TYPE_VERSION` = 'N/A', `MEDIA_TYPE` = 'audio/x-wave' WHERE `CODE` = 'AUDIO_WAVE';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'IMG_JP2_1_0', `MEDIA_TYPE_VERSION` = '1.0', `MEDIA_TYPE` = 'image/jp2' WHERE `CODE` = 'IMG_JP2_1_0';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = 'ADOBE', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'IMG_JPEG_ADOBE', `MEDIA_TYPE_VERSION` = '', `MEDIA_TYPE` = 'image/jpeg' WHERE `CODE` = 'IMG_JPEG_ADOBE';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = 'JFIF', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'IMG_JPEG_JFIF', `MEDIA_TYPE_VERSION` = '', `MEDIA_TYPE` = 'image/jpeg' WHERE `CODE` = 'IMG_JPEG_JFIF';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'IMG_JPEG_UNKNOWN', `MEDIA_TYPE_VERSION` = 'UNKNOWN', `MEDIA_TYPE` = 'image/jpeg' WHERE `CODE` = 'IMG_JPEG_UNKNOWN';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'IMG_JPX_1_0', `MEDIA_TYPE_VERSION` = '1.0', `MEDIA_TYPE` = 'image/jpx' WHERE `CODE` = 'IMG_JPX_1_0';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'IMG_TIFF_4_0', `MEDIA_TYPE_VERSION` = '4.0', `MEDIA_TYPE` = 'image/tiff' WHERE `CODE` = 'IMG_TIFF_4_0';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'IMG_TIFF_5_0', `MEDIA_TYPE_VERSION` = '5.0', `MEDIA_TYPE` = 'image/tiff' WHERE `CODE` = 'IMG_TIFF_5_0';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'IMG_TIFF_6_0', `MEDIA_TYPE_VERSION` = '6.0', `MEDIA_TYPE` = 'image/tiff' WHERE `CODE` = 'IMG_TIFF_6_0';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'TXT_PLAIN', `MEDIA_TYPE_VERSION` = '', `MEDIA_TYPE` = 'text/plain' WHERE `CODE` = 'TXT_PLAIN';
UPDATE `FORMAT` SET `ACTION_PLAN` = '1', `VARIATION` = '', `SUPPORT_LEVEL` = 'FULLY_SUPPORTED', `CODE` = 'VIDEO_AVI_1_0', `MEDIA_TYPE_VERSION` = '1.0', `MEDIA_TYPE` = 'video/avi' WHERE `CODE` = 'VIDEO_AVI_1_0';

-- remove IS_SUPPORTED posibilities
ALTER TABLE `FORMAT` MODIFY `SUPPORT_LEVEL`enum('FULLY_SUPPORTED','RECOGNIZED','UNSUPPORTED') NOT NULL;