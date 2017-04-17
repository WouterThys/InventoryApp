PRAGMA foreign_keys = 0;

DROP TABLE orders;

CREATE TABLE orders (
    id            INTEGER        PRIMARY KEY AUTOINCREMENT,
    name          VARCHAR (128)  NOT NULL,
    iconpath      VARCHAR (1024),
    dateordered   DATETIME,
    datemodified  DATETIME,
    datereceived  DATETIME,
    distributorid INTEGER        REFERENCES distributors (id)
                                 NOT NULL ON CONFLICT ABORT
);

PRAGMA foreign_keys = 1;
