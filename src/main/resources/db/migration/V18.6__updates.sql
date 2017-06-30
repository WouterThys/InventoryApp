PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM projectdirectories;

DROP TABLE projectdirectories;

CREATE TABLE projectdirectories (
    id        INTEGER        PRIMARY KEY AUTOINCREMENT,
    name      VARCHAR (128)  NOT NULL,
    projectid INTEGER        REFERENCES projects (id) ON DELETE CASCADE,
    directory VARCHAR (1024)
);

INSERT INTO projectdirectories (
                                   id,
                                   name,
                                   projectid,
                                   directory
                               )
                               SELECT id,
                                      name,
                                      projectid,
                                      directory
                                 FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;


PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM projecttypelink;

DROP TABLE projecttypelink;

CREATE TABLE projecttypelink (
    id                 INTEGER PRIMARY KEY,
    projectdirectoryid INTEGER REFERENCES projectdirectories (id) ON DELETE CASCADE
                               NOT NULL,
    projecttypeid      INTEGER REFERENCES packagetypes (id) ON DELETE CASCADE
                               NOT NULL
);

INSERT INTO projecttypelink (
                                id,
                                projectdirectoryid,
                                projecttypeid
                            )
                            SELECT id,
                                   projectdirectoryid,
                                   projecttypeid
                              FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;

PRAGMA foreign_keys = 1;

