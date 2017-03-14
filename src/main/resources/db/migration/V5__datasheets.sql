/* Add the datasheet links to the table */
ALTER TABLE items ADD COLUMN localdatasheet VARCHAR(1024);
ALTER TABLE items ADD COLUMN onlinedatasheet VARCHAR(1024);