CREATE TABLE partnumbers (
    id                 INTEGER        PRIMARY KEY AUTOINCREMENT,
    name               VARCHAR (128),
    iconpath           VARCHAR (1024),
    distributorid      INTEGER        REFERENCES distributors (id),
    itemid             INTEGER        REFERENCES items (id),
    distributoritemref VARCHAR (128)
);
