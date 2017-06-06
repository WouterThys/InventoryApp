CREATE TABLE packagetypes (
    id   INTEGER       PRIMARY KEY ASC AUTOINCREMENT
                       UNIQUE,
    name VARCHAR (128) UNIQUE
);

CREATE TABLE package (
    id     INTEGER       PRIMARY KEY ASC AUTOINCREMENT
                         UNIQUE,
    name   VARCHAR (128) UNIQUE,
    typeid INTEGER,
    pins   INTEGER,
    width  INTEGER,
    height INTEGER
);