DROP TABLE orderitems;

CREATE TABLE orderitems (
    id      INTEGER        PRIMARY KEY autoincrement NOT NULL,
    name    VARCHAR (128)  NOT NULL,
    orderid REFERENCES orders (id)
            NOT NULL ON CONFLICT ABORT,
    itemid  REFERENCES items (id)
            NOT NULL ON CONFLICT ABORT
);