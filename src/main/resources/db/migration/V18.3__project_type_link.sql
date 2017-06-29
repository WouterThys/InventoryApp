CREATE TABLE projecttypelink (
    id                 INTEGER PRIMARY KEY,
    projectdirectoryid INTEGER REFERENCES projectdirectories (id)
                               NOT NULL,
    projecttypeid      INTEGER REFERENCES packagetypes (id)
                               NOT NULL
);
