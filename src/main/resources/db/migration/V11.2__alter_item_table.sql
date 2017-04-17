PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM items;

DROP TABLE items;

CREATE TABLE items (
    id              INTEGER        PRIMARY KEY AUTOINCREMENT,
    name            VARCHAR (128)  NOT NULL,
    description     VARCHAR (1023),
    price           DOUBLE,
    categoryid      INTEGER        REFERENCES categories (id),
    productId       INTEGER        REFERENCES products (id),
    typeId          INTEGER        REFERENCES types (id),
    localdatasheet  VARCHAR (1024),
    onlinedatasheet VARCHAR (1024),
    iconpath        VARCHAR (1024),
    manufacturerid  INTEGER        REFERENCES manufacturers (id),
    locationid      INTEGER        REFERENCES locations (id)
);

INSERT INTO items (
                      id,
                      name,
                      description,
                      price,
                      categoryid,
                      productId,
                      typeId,
                      localdatasheet,
                      onlinedatasheet,
                      iconpath,
                      manufacturerid,
                      locationid
                  )
                  SELECT id,
                         name,
                         description,
                         price,
                         categoryId,
                         productId,
                         typeId,
                         localdatasheet,
                         onlinedatasheet,
                         iconpath,
                         manufacturerid,
                         locationid
                    FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;
