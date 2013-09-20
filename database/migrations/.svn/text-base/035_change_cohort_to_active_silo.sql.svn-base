-- change the cohort field to the active silo field
start transaction;
alter table SILO drop COHORT;
alter table SILO add ACTIVE boolean not null;
commit;
UPDATE DATABASE_VERSION SET MIGRATION_NUMBER=35;