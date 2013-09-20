-- silos
-- INSERT INTO `SILO` VALUES (1, 'http://127.0.0.1:5000/silo-1', true, "", "silo1");
-- INSERT INTO `SILO` VALUES (2, 'http://127.0.0.1:5000/silo-2', true, "", "silo2");
-- INSERT INTO `SILO` VALUES (3, 'http://127.0.0.1:5000/silo-3', true, "", "silo3");
INSERT INTO `SILO` VALUES (4, 'http://localhost:3000/local1/data', true, "", "silo1");
INSERT INTO `SILO` VALUES (5, 'http://localhost:3000/local2/data', true, "", "silo2");

-- account info
INSERT INTO `CONTACT` VALUES (1,'daitss operator','number street','city, state','','','','daitss@localhost','555-5555');
INSERT INTO `ACCOUNT` VALUES ('FDA', 'Florida Digital Archive', 'Account for the archive itself, needed for archive-owned entities', 1, 1, 'daitss@localhost');
INSERT INTO `PROJECT` VALUES ('FDA', 'Project code used for entities owned by the archive');
INSERT INTO `ACCOUNT_PROJECT` VALUES (1, 'FDA', 'FDA');
INSERT INTO `ARCHIVE_LOGIC` VALUES (1, 1, '2004-10-22', NULL, 'default', 'FULL');
INSERT INTO `OUTPUT_REQUEST` VALUES (1,'FDA',1,'TRUE','TRUE','TRUE');

