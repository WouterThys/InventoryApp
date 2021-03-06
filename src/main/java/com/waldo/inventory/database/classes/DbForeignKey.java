package com.waldo.inventory.database.classes;

public class DbForeignKey {

    private String constraintName;

    private final DbTable fromTable;
    private String fromColumn;

    private final DbTable referenceTable;
    private String referenceColumn;

    public DbForeignKey(DbTable fromTable, String fromColumn, String constraintName, DbTable referenceTable, String referenceColumn) {
        this.fromTable = fromTable;
        this.fromColumn = fromColumn;
        this.constraintName = constraintName;
        this.referenceTable = referenceTable;
        this.referenceColumn = referenceColumn;
    }

    @Override
    public String toString() {
        return getConstraintName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DbForeignKey) {
            DbForeignKey fk = (DbForeignKey) obj;

            if ((fk.getFromColumn().equals(getFromColumn())) &&
                    (fk.getConstraintName().equals(getConstraintName())) &&
                    (fk.getReferenceTable().equals(getReferenceTable())) &&
                    (fk.getReferenceColumn().equals(getReferenceColumn()))) {
                return true;
            }
        }
        return false;
    }

    public String getFromColumn() {
        if (fromColumn == null) {
            fromColumn = "";
        }
        return fromColumn;
    }

    public String getConstraintName() {
        if (constraintName == null) {
            constraintName = "";
        }
        return constraintName;
    }

    public DbTable getReferenceTable() {
        return referenceTable;
    }

    public String getReferenceColumn() {
        if (referenceColumn == null) {
            referenceColumn = "";
        }
        return referenceColumn;
    }

    public DbTable getFromTable() {
        return fromTable;
    }
}
