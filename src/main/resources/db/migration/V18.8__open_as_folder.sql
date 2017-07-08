PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM projecttypes;

DROP TABLE projecttypes;

CREATE TABLE projecttypes (
    id           INTEGER        PRIMARY KEY AUTOINCREMENT,
    name         VARCHAR (128)  NOT NULL,
    iconpath     VARCHAR (1024),
    extension    VARCHAR (8),
    openasfolder BOOLEAN
);

INSERT INTO projecttypes (
                             id,
                             name,
                             iconpath,
                             extension
                         )
                         SELECT id,
                                name,
                                iconpath,
                                extension
                           FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;