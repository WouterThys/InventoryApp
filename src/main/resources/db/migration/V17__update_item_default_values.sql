PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM items;

DROP TABLE items;

CREATE TABLE items (
    id              INTEGER        PRIMARY KEY AUTOINCREMENT,
    name            VARCHAR (128)  NOT NULL,
    description     VARCHAR (1023),
    price           DOUBLE,
    categoryid      INTEGER        CONSTRAINT [Category Convtraint] REFERENCES categories (id)
                                   DEFAULT (1),
    productId       INTEGER        CONSTRAINT [Product Convtraint] REFERENCES products (id)
                                   DEFAULT (1),
    typeId          INTEGER        CONSTRAINT [Type Convtraint] REFERENCES types (id)
                                   DEFAULT (1),
    localdatasheet  VARCHAR (1024),
    onlinedatasheet VARCHAR (1024),
    iconpath        VARCHAR (1024),
    manufacturerid  INTEGER        CONSTRAINT [Manufacturer Constraint] REFERENCES manufacturers (id)
                                   DEFAULT (1),
    locationid      INTEGER        CONSTRAINT [Location Constraint] REFERENCES locations (id)
                                   DEFAULT (1),
    amount          INTEGER,
    amounttype      INTEGER,
    orderstate      INTEGER,
    packagetypeid   INTEGER        REFERENCES package (id)
                                   DEFAULT (1),
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
                      packagetypeid,
                      pins,
                      width,
                      height
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
                         packagetypeid,
                         pins,
                         width,
                         height
                    FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;
