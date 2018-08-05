package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private long locationLabelId;
    private LocationLabel locationLabel;

    public LocationType() {
        super(TABLE_NAME);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LocationType that = (LocationType) o;
        return getLocationLabelId() == that.getLocationLabelId() &&
                Objects.equals(getLayoutDefinition(), that.getLayoutDefinition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLayoutDefinition(), getLocationLabelId());
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        statement.setString(ndx++, getLayoutDefinition());
        statement.setLong(ndx++, getLocationLabelId());

        return ndx;
    }

    @Override
    public LocationType createCopy(DbObject copyInto) {
        LocationType cpy = (LocationType) copyInto;
        copyBaseFields(cpy);

        cpy.setLayoutDefinition(getLayoutDefinition());
        cpy.setLocationLabelId(getLocationLabelId());

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
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                cache().add(this);
                break;
            }
            case Delete: {
                cache().remove(this);
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

            int maxRowsForThisCol = getNumberOfRowsInCol(newCol) - 1;
            int maxCol = getNumberOfCols();

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
                    newRow--;
                    if (newRow < 0) {
                        newRow = maxRowsForThisCol;
                        if (leftToRight) {
                            newCol++;
                            if (newCol > maxCol) {
                                newCol = 0;
                                newRow = maxRowsForThisCol;
                            }
                        } else {
                            newCol--;
                            if (newCol < 0) {
                                newCol = maxCol;
                            }
                        }
                    }
                    break;

                case Lower:
                    newRow++;
                    if (newRow > maxRowsForThisCol) {
                        newRow = 0;
                        if (leftToRight) {
                            newCol++;
                            if (newCol > maxCol) {
                                newRow = 0;
                                newCol = 0;
                            }
                        } else {
                            newCol--;
                            if (newCol < 0) {
                                newCol = maxCol;
                            }
                        }
                    }
                    break;
            }

            neighbour = SearchManager.sm().findLocation(getId(), newRow, newCol);
        }
        return neighbour;
    }

    private int getNumberOfColumnsInRow(int row) {
        return getLocationsInRow(row).size();
    }

    private int getNumberOfRowsInCol(int col) {
        return getLocationsInCol(col).size();
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

    private int getNumberOfCols() {
        int maxCol = 0;
        for (Location location : getLocations()) {
            if (location.getCol() > maxCol) {
                maxCol = location.getCol();
            }
        }
        return maxCol;
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

    private List<Location> getLocationsInCol(int col) {
        List<Location> colLocations = new ArrayList<>();
        if (col >= 0) {
            for (Location location : getLocations()) {
                if (location.getCol() == col) {
                    colLocations.add(location);
                }
            }
        }
        return colLocations;
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


    public long getLocationLabelId() {
        if (locationLabelId < DbObject.UNKNOWN_ID) {
            locationLabelId = UNKNOWN_ID;
        }
        return locationLabelId;
    }

    public void setLocationLabelId(long locationLabelId) {
        if (locationLabel != null && locationLabel.getId() != locationLabelId) {
            locationLabel = null;
        }
        this.locationLabelId = locationLabelId;
    }

    public LocationLabel getLocationLabel() {
        if (locationLabel == null && getLocationLabelId() > UNKNOWN_ID) {
            locationLabel = SearchManager.sm().findLocationLabelById(locationLabelId);
        }
        return locationLabel;
    }
}