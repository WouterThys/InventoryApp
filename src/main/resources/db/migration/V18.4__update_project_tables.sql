PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM projects;

DROP TABLE projects;

CREATE TABLE projects (
    id       INTEGER        PRIMARY KEY AUTOINCREMENT,
    name     VARCHAR (128)  NOT NULL,
    iconpath VARCHAR (1024)
);

INSERT INTO projects (
                         id,
                         name,
                         iconpath
                     )
                     SELECT id,
                            name,
                            iconpath
                       FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;


PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM projecttypes;

DROP TABLE projecttypes;

CREATE TABLE projecttypes (
    id        INTEGER        PRIMARY KEY AUTOINCREMENT,
    name      VARCHAR (128)  NOT NULL,
    iconpath  VARCHAR (1024),
    extension VARCHAR (8)
);

INSERT INTO projecttypes (
                             id,
                             name,
                             iconpath,
                             extension
                         )
                         SELECT id,
                                name,
                                iconpath,
                                extension
                           FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;

PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM projectdirectories;

DROP TABLE projectdirectories;

CREATE TABLE projectdirectories (
    id        INTEGER       PRIMARY KEY AUTOINCREMENT,
    name      VARCHAR (128) NOT NULL,
    projectid INTEGER       REFERENCES projects (id)
);

INSERT INTO projectdirectories (
                                   id,
                                   name,
                                   projectid
                               )
                               SELECT id,
                                      name,
                                      projectid
                                 FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;


