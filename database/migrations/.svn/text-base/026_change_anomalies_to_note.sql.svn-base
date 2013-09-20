-- change all anomalies to 'note', except for too many anomalies which stays 'bit'

update SEVERE_ELEMENT SET SEVERITY = 'NOTE' where TYPE = 'ANOMALY';
update SEVERE_ELEMENT SET SEVERITY = 'BIT' WHERE CODE = 'A_FILE_TOO_MANY_ANOMS';

UPDATE DATABASE_VERSION SET MIGRATION_NUMBER=26;

