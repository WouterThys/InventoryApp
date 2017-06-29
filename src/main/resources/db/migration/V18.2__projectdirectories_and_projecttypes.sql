CREATE TABLE projectdirectories (
    id        INTEGER       PRIMARY KEY,
    name      VARCHAR (128) NOT NULL,
    projectid INTEGER       REFERENCES projects (id)
);

CREATE TABLE projecttypes (
    id        INTEGER        PRIMARY KEY,
    name      VARCHAR (128)  NOT NULL,
    iconpath  VARCHAR (1024),
    extension VARCHAR (8)
);