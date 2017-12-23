package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class LocationType extends DbObject {

    public static final String TABLE_NAME = "locationtypes";

    public enum LocationNeighbour {
        Left,
        Right,
        Upper,
        Lower
    }

    private String layoutDefinition;
    private List<Location> locations;

    public LocationType() {
        super(TABLE_NAME);
    }


    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        return result && obj instanceof LocationType && ((LocationType) obj).getLayoutDefinition().equals(getLayoutDefinition());
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        statement.setString(ndx++, getLayoutDefinition());

        return ndx;
    }

    @Override
    public LocationType createCopy(DbObject copyInto) {
        LocationType cpy = (LocationType) copyInto;
        copyBaseFields(cpy);

        cpy.setLayoutDefinition(getLayoutDefinition());

        return cpy;
    }

    @Override
    public LocationType createCopy() {
        return createCopy(new LocationType());
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<LocationType> list = cache().getLocationTypes();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<LocationType> list = cache().getLocationTypes();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    public Location getNeighbourOfLocation(Location location, LocationNeighbour direction, boolean leftToRight, boolean downwards) {
        Location neighbour = null;
        if (location != null) {
            int newRow = location.getRow();
            int newCol = location.getCol();

            int maxColsForThisRow = getNumberOfColumnsInRow(newRow)-1;
            int maxRow = getNumberOfRows()-1;

            switch (direction) {
                case Right:
                    newCol++;
                    if (newCol > maxColsForThisRow) {
                        newCol = 0;
                        if (downwards) {
                            newRow++;
                            if (newRow > maxRow) {
                                newRow = 0;
                                newCol = 0;
                            }
                        } else {
                            newRow--;
                            if (newRow < 0) {
                                newRow = maxRow;
                            }
                        }
                    }
                    break;

                case Left:
                    newCol--;
                    if (newCol < 0) {
                        newCol = maxColsForThisRow;
                        if (downwards) {
                            newRow++;
                            if (newRow > maxRow) {
                                newRow = 0;
                                newCol = maxColsForThisRow;
                            }
                        } else {
                            newRow--;
                            if (newRow < 0) {
                                newRow = maxRow;
                            }
                        }
                    }
                    break;

                case Upper:
                    break;

                case Lower:
                    break;
            }

            neighbour = SearchManager.sm().findLocation(getId(), newRow, newCol);
        }
        return neighbour;
    }

    private int getNumberOfColumnsInRow(int row) {
        return getLocationsInRow(row).size();
    }

    private int getNumberOfRows() {
        int maxRow = 0;
        for (Location location : getLocations()) {
            if (location.getRow() > maxRow) {
                maxRow = location.getRow();
            }
        }
        return maxRow;
    }

    private List<Location> getLocationsInRow(int row) {
        List<Location> rowLocations = new ArrayList<>();
        if (row >= 0) {
            for (Location location : getLocations()) {
                if (location.getRow() == row) {
                    rowLocations.add(location);
                }
            }
        }
        return rowLocations;
    }

    public List<Location> getLocations() {
        if (locations == null) {
            locations = SearchManager.sm().findLocationsByTypeId(getId());
        }
        return locations;
    }

    public void updateLocations() {
        locations = null;
    }

    public String getLayoutDefinition() {
        if (layoutDefinition == null) {
            layoutDefinition = "";
        }
        return layoutDefinition;
    }

    public void setLayoutDefinition(String layoutDefinition) {
        this.layoutDefinition = layoutDefinition;
    }

    public boolean hasLayoutDefinition() {
        return !getLayoutDefinition().isEmpty();
    }
}