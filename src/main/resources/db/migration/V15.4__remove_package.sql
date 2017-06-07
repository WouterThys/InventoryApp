PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM items;

DROP TABLE items;

CREATE TABLE items (
    id              INTEGER        PRIMARY KEY AUTOINCREMENT,
    name            VARCHAR (128)  NOT NULL,
    description     VARCHAR (1023),
    price           DOUBLE,
    categoryid      INTEGER        CONSTRAINT [Category Convtraint] REFERENCES categories (id),
    productId       INTEGER        CONSTRAINT [Product Convtraint] REFERENCES products (id),
    typeId          INTEGER        CONSTRAINT [Type Convtraint] REFERENCES types (id),
    localdatasheet  VARCHAR (1024),
    onlinedatasheet VARCHAR (1024),
    iconpath        VARCHAR (1024),
    manufacturerid  INTEGER        CONSTRAINT [Manufacturer Constraint] REFERENCES manufacturers (id),
    locationid      INTEGER        CONSTRAINT [Location Constraint] REFERENCES locations (id),
    amount          INTEGER,
    amounttype      INTEGER,
    orderstate      INTEGER,
    packagetypeid   INTEGER        REFERENCES package (id),
    pins            INTEGER,
    width           DOUBLE,
    height          DOUBLE
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
                      locationid,
                      amount,
                      amounttype,
                      orderstate,
                      packagetypeid
                  )
                  SELECT id,
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
                         locationid,
                         amount,
                         amounttype,
                         orderstate,
                         packageid
                    FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;
