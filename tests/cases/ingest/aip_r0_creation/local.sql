SET FOREIGN_KEY_CHECKS = 0;
update ACCOUNT set CODE='UF' where CODE='FDA';
update PROJECT set CODE='UFDC' where CODE='FDA';
update ACCOUNT_PROJECT set ACCOUNT='UF', PROJECT='UFDC' where ACCOUNT= 'FDA'and PROJECT='FDA';
SET FOREIGN_KEY_CHECKS = 1;