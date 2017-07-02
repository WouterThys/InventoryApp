CREATE TABLE log (
    id           INTEGER       PRIMARY KEY AUTOINCREMENT,
    logtype      INTEGER,
    logtime      DATE,
    logclass     VARCHAR (128),
    logmessage   TEXT,
    logexception TEXT
);
