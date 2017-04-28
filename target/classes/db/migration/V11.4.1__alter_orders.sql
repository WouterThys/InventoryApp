PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM orders;

DROP TABLE orders;

CREATE TABLE orders (
    id           INTEGER        PRIMARY KEY AUTOINCREMENT,
    name         VARCHAR (128)  NOT NULL,
    iconpath     VARCHAR (1024),
    dateordered  DATETIME,
    datemodified DATETIME,
    datereceived DATETIME
);

INSERT INTO orders (
                       id,
                       name,
                       iconpath
                   )
                   SELECT id,
                          name,
                          iconpath
                     FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;
