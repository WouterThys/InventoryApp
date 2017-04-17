CREATE TABLE distributors (
    id           INTEGER PRIMARY KEY autoincrement NOT NULL,
    name         VARCHAR (128)  NOT NULL,
    iconpath     VARCHAR (1024),
    website      VARCHAR (1024)
)