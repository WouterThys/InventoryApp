PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM packagetypes;

DROP TABLE packagetypes;

CREATE TABLE packagetypes (
    id          INTEGER       PRIMARY KEY ASC AUTOINCREMENT
                              UNIQUE,
    name        VARCHAR (128) UNIQUE,
    description TEXT
);

INSERT INTO packagetypes (
                             id,
                             name
                         )
                         SELECT id,
                                name
                           FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;
