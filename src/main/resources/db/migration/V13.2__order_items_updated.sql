PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM orderitems;

DROP TABLE orderitems;

CREATE TABLE orderitems (
    id      INTEGER       PRIMARY KEY AUTOINCREMENT
                          NOT NULL,
    name    VARCHAR (128) NOT NULL,
    orderid INTEGER       REFERENCES orders (id)
                          NOT NULL ON CONFLICT ABORT,
    itemid  INTEGER       REFERENCES items (id)
                          NOT NULL ON CONFLICT ABORT,
    amount  INTEGER,
    itemref VARCHAR (128)
);

INSERT INTO orderitems (
                           id,
                           name,
                           orderid,
                           itemid
                       )
                       SELECT id,
                              name,
                              orderid,
                              itemid
                         FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;
