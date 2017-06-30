PRAGMA foreign_keys = 0;

CREATE TABLE sqlitestudio_temp_table AS SELECT *
                                          FROM projecttypelink;

DROP TABLE projecttypelink;

CREATE TABLE projecttypelink (
    id                 INTEGER PRIMARY KEY,
    projectdirectoryid INTEGER REFERENCES projectdirectories (id) ON DELETE CASCADE
                               NOT NULL,
    projecttypeid      INTEGER REFERENCES projecttypes (id) ON DELETE CASCADE
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
