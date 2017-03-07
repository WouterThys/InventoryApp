CREATE TABLE categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE types (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(128) NOT NULL
);

INSERT INTO categories (name) VALUES ('Unknown');
INSERT INTO categories (name) VALUES ('<New>');

INSERT INTO products (name) VALUES ('Unknown');
INSERT INTO products (name) VALUES ('<New>');

INSERT INTO types (name) VALUES ('Unknown');
INSERT INTO types (name) VALUES ('<New>');