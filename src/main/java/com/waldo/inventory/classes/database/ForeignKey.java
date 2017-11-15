package com.waldo.inventory.classes.database;

public class ForeignKey {

    private String constraintName;

    private DbTable fromTable;
    private String fromColumn;

    private DbTable referenceTable;
    private String referenceColumn;

    public ForeignKey(DbTable fromTable, String fromColumn, String constraintName, DbTable referenceTable, String referenceColumn) {
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
        if (obj instanceof ForeignKey) {
            ForeignKey fk = (ForeignKey) obj;

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
