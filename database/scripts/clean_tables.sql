DELETE FROM DATA_VERIFICATION WHERE ID > -1;
DELETE FROM BS_PDF_FILTER WHERE BSID LIKE 'F%';
DELETE FROM BS_PDF_ANNOTATION WHERE BSID LIKE 'F%';
DELETE FROM BS_PDF_ACTION WHERE BSID LIKE 'F%';
DELETE FROM BS_PDF WHERE BSID LIKE 'F%';
DELETE FROM BS_TEXT WHERE BSID LIKE 'F%';
DELETE FROM BS_MARKUP WHERE BSID LIKE 'F%';
DELETE FROM BS_IMAGE_TIFF WHERE BSID LIKE 'F%';
DELETE FROM BS_IMAGE_JPEG WHERE BSID LIKE 'F%';
DELETE FROM BS_IMAGE_JPEG2000 WHERE BSID LIKE 'F%';
DELETE FROM BS_IMAGE WHERE BSID LIKE 'F%';
DELETE FROM BS_AUDIO_WAVE WHERE BSID LIKE 'F%';
DELETE FROM BS_AUDIO WHERE BSID LIKE 'F%';
DELETE FROM BS_VIDEO WHERE BSID LIKE 'F%';
DELETE FROM BITSTREAM_BS_PROFILE WHERE BSID LIKE 'F%';
DELETE FROM BITSTREAM WHERE BSID LIKE 'F%';
DELETE FROM RELATIONSHIP WHERE DFID_1 LIKE 'F%';
DELETE FROM EVENT WHERE ID >= 0;
DELETE FROM STORAGE_DESC WHERE ID >= 0;
DELETE FROM AVI_FILE WHERE DFID LIKE 'F%';
DELETE FROM QUICKTIME_FILE WHERE DFID LIKE 'F%';
DELETE FROM WAVE_FILE WHERE DFID LIKE 'F%';
DELETE FROM DATA_FILE_SEVERE_ELEMENT WHERE DFID LIKE 'F%';
DELETE FROM DATA_FILE_FORMAT_ATTRIBUTE WHERE DFID LIKE 'F%';
DELETE FROM MESSAGE_DIGEST WHERE DFID LIKE 'F%';
DELETE FROM DISTRIBUTED WHERE PARENT LIKE 'F%';
DELETE FROM INT_ENTITY_GLOBAL_FILE WHERE IEID LIKE 'E%';
DELETE FROM GLOBAL_FILE WHERE DFID LIKE 'F%';
DELETE FROM DATA_FILE WHERE DFID LIKE 'F%';
DELETE FROM REPORT_INT_ENTITY WHERE ID >= 0;
DELETE FROM REPORT WHERE ID >= 0;
DELETE FROM INT_ENTITY WHERE IEID LIKE 'E%';
DELETE FROM ADMIN WHERE OID LIKE 'F%' OR OID LIKE 'E%';
