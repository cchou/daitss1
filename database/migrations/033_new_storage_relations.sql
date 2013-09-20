DROP TABLE IF EXISTS SILO, COPY, OLD_TSM_FILE_COPIES;

create table SILO (
       ID int(10) primary key auto_increment not null,
       URL varchar(255) unique not null,
       COHORT int(10) not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

create table COPY (
      ID int(10) primary key auto_increment not null,
      IEID varchar(16) not null,
      SILO int(10) not null,
      PATH varchar(255) not null,
      unique (IEID,SILO,PATH)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

create table OLD_TSM_FILE_COPIES (
      ID int(10) primary key auto_increment not null,
      IEID_URI int not null,
      DFID int not null,
      IDENTIFIER varchar(256) not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
UPDATE DATABASE_VERSION SET MIGRATION_NUMBER=33;