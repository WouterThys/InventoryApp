CREATE TABLE orderitems (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(128) NOT NULL,
    iconpath VARCHAR(1024),
    itemid INTEGER,
    lastmodifieddate DATE,
    orderdate DATE,
    receivedate DATE,
    isordered INTEGER
);

INSERT INTO orderitems (name, iconpath) VALUES ('Unknown', 'icons/order/shopping_cart24.png');