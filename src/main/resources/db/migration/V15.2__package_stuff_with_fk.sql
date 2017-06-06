PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM package;

DROP TABLE package;

CREATE TABLE package (
    id     INTEGER       PRIMARY KEY ASC AUTOINCREMENT
                         UNIQUE,
    name   VARCHAR (128) UNIQUE,
    typeid INTEGER       REFERENCES packagetypes (id),
    pins   INTEGER,
    width  INTEGER,
    height INTEGER
);

INSERT INTO package (
                        id,
                        name,
                        typeid,
                        pins,
                        width,
                        height
                    )
                    SELECT id,
                           name,
                           typeid,
                           pins,
                           width,
                           height
                      FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;
