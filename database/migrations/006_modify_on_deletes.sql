-- dropping ON DELETE CASCADE
ALTER TABLE `BS_VIDEO` DROP FOREIGN KEY `BS_VIDEO_ibfk_2`;
ALTER TABLE `BS_VIDEO` ADD CONSTRAINT `BS_VIDEO_ibfk_2` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`);
ALTER TABLE `BS_AUDIO` DROP FOREIGN KEY `BS_AUDIO_ibfk_7`;
ALTER TABLE `BS_AUDIO` ADD CONSTRAINT `BS_AUDIO_ibfk_7` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`);

-- adding ON DELETE CASCADE
ALTER TABLE `BS_TEXT` DROP FOREIGN KEY `BS_TEXT_ibfk_1`;
ALTER TABLE `BS_TEXT` ADD CONSTRAINT `BS_TEXT_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE;
ALTER TABLE `BS_PDF` DROP FOREIGN KEY `BS_PDF_ibfk_1`;
ALTER TABLE `BS_PDF` ADD CONSTRAINT `BS_PDF_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE;
ALTER TABLE `BS_MARKUP` DROP FOREIGN KEY `BS_MARKUP_ibfk_3`;
ALTER TABLE `BS_MARKUP` ADD CONSTRAINT `BS_MARKUP_ibfk_3` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE;
ALTER TABLE `BS_IMAGE_TIFF` DROP FOREIGN KEY `BS_IMAGE_TIFF_ibfk_1`;
ALTER TABLE `BS_IMAGE_TIFF` ADD CONSTRAINT `BS_IMAGE_TIFF_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE;
ALTER TABLE `BS_IMAGE_JPEG2000` DROP FOREIGN KEY `BS_IMAGE_JPEG2000_ibfk_4`;
ALTER TABLE `BS_IMAGE_JPEG2000` ADD CONSTRAINT `BS_IMAGE_JPEG2000_ibfk_4` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE;
ALTER TABLE `BS_IMAGE_JPEG` DROP FOREIGN KEY `BS_IMAGE_JPEG_ibfk_1`;
ALTER TABLE `BS_IMAGE_JPEG` ADD CONSTRAINT `BS_IMAGE_JPEG_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE;
ALTER TABLE `BS_IMAGE` DROP FOREIGN KEY `BS_IMAGE_ibfk_4`;
ALTER TABLE `BS_IMAGE` ADD CONSTRAINT `BS_IMAGE_ibfk_4` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE;
ALTER TABLE `BS_AUDIO_WAVE` DROP FOREIGN KEY `BS_AUDIO_WAVE_ibfk_2`;
ALTER TABLE `BS_AUDIO_WAVE` ADD CONSTRAINT `BS_AUDIO_WAVE_ibfk_2` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE;
ALTER TABLE `BS_AUDIO` DROP FOREIGN KEY `BS_AUDIO_ibfk_6`;
ALTER TABLE `BS_AUDIO` ADD CONSTRAINT `BS_AUDIO_ibfk_6` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE;

-- addding FK CONSTRAINT
ALTER TABLE `BS_VIDEO` ADD CONSTRAINT `BS_VIDEO_ibfk_9` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`) ON DELETE CASCADE;
ALTER TABLE `BS_VIDEO` ADD CONSTRAINT `BS_VIDEO_ibfk_11` FOREIGN KEY (`BS_TABLE`) REFERENCES `BS_TABLE` (`NAME`);
ALTER TABLE `BS_VIDEO` ADD CONSTRAINT `BS_VIDEO_ibfk_10` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE;
ALTER TABLE `BS_AUDIO` ADD CONSTRAINT `BS_AUDIO_ibfk_10` FOREIGN KEY (`BSID`) REFERENCES `BITSTREAM` (`BSID`) ON DELETE CASCADE;
ALTER TABLE `BS_AUDIO` ADD CONSTRAINT `BS_AUDIO_ibfk_1` FOREIGN KEY (`DFID`) REFERENCES `DATA_FILE` (`DFID`);

