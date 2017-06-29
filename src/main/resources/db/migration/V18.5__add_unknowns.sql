PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM projectdirectories;

DROP TABLE projectdirectories;

CREATE TABLE projectdirectories (
    id        INTEGER        PRIMARY KEY AUTOINCREMENT,
    name      VARCHAR (128)  NOT NULL,
    projectid INTEGER        REFERENCES projects (id),
    directory VARCHAR (1024)
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



INSERT INTO projects (name) VALUES ('UNKNOWN');
INSERT INTO projectdirectories (name, projectid, directory) VALUES ('UNKNOWN', 1, '');
INSERT INTO projecttypes (name, iconpath, extension) VALUES ('UNKNOWN', '', '');