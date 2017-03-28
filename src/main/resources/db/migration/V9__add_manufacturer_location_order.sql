CREATE TABLE manufacturers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(128) NOT NULL,
    iconpath VARCHAR(1024)
);

CREATE TABLE locations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(128) NOT NULL,
    iconpath VARCHAR(1024)
);

CREATE TABLE orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(128) NOT NULL,
    iconpath VARCHAR(1024)
);

INSERT INTO manufacturers (name, iconpath) VALUES ('Unknown', 'icons/factory/factory24.png');

INSERT INTO locations (name, iconpath) VALUES ('Unknown', 'icons/order/shopping_cart24.png');

INSERT INTO orders (name, iconpath) VALUES ('Unknown', 'icons/location/location_pin24.png');