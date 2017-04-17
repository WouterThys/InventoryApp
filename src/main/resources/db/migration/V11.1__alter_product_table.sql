PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM products;

DROP TABLE products;

CREATE TABLE products (
    id         INTEGER        PRIMARY KEY AUTOINCREMENT,
    name       VARCHAR (128)  NOT NULL,
    categoryid INTEGER        REFERENCES categories (id)
                              NOT NULL ON CONFLICT ABORT,
    iconpath   VARCHAR (1024)
);

INSERT INTO products (
                         id,
                         name,
                         categoryid,
                         iconpath
                     )
                     SELECT id,
                            name,
                            categoryid,
                            iconpath
                       FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;
