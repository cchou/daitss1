DROP TABLE IF EXISTS `DATABASE_VERSION`;

CREATE TABLE `DATABASE_VERSION` (
  `MIGRATION_NUMBER` INT UNSIGNED NOT NULL DEFAULT '0' COMMENT 'An sequential number referring to the last SQL script run when creating this database',
   KEY `MIGRATION_NUMBER` (`MIGRATION_NUMBER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


INSERT DATABASE_VERSION(MIGRATION_NUMBER) VALUES(21);

-- all scripts should now end with an sql statement like:
-- for example, the next migration script that starts with
-- 022_something.sql would have to have
--
-- UPDATE DATABASE_VERSION SET MIGRATION_NUMBER = 22;
