
insert into SEVERE_ELEMENT set CODE = 'L_FILE_UNKNOWN', TYPE = 'LIMITATION', SEVERITY = 'NOTE', REPORT = 'TRUE', DESCRIPTION = 'This file is an unknown format to the archive';

UPDATE DATABASE_VERSION SET MIGRATION_NUMBER=28;
