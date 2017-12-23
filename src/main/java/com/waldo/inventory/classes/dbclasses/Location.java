package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Location extends DbObject {

    public static final String TABLE_NAME = "locations";

    // private String name; -> in DbObject
    private long locationTypeId;
    private LocationType locationType;
    private int row = 0;
    private int col = 0;
    private String alias;

    // For layout
    private LocationLayout layout = new LocationLayout();


    // Items or SetItems for this location
    private List<DbObject> items;

    public Location() {
        super(TABLE_NAME);
        matchCount = 7;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getPrettyString() {
        if (getLocationType() != null && getLocationTypeId() > UNKNOWN_ID) {
            String locType = getLocationType().getName();
            if (locType.length() > 3) {
                locType = locType.substring(0,3);
            }
            return locType + "/"+ Statics.Alphabet[row]+"/"+col;
        }
        return "";
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        statement.setString(ndx++, getName());
        statement.setLong(ndx++, getLocationTypeId());
        statement.setInt(ndx++, getRow());
        statement.setInt(ndx++, getCol());
        statement.setString(ndx++, getAlias());

        return ndx;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof Location)) {
                return false;
            }
            if (!(((Location)obj).getLocationTypeId() == getLocationTypeId())) {
                System.out.println("location type differs");
                return false;
            }
            if (!(((Location)obj).getRow() == getRow())) {
                System.out.println("row differs");
                return false;
            }
            if (!(((Location)obj).getCol() == getCol())) {
                System.out.println("col differs");
                return false;
            }
            if (!(((Location)obj).getAlias().equals(getAlias()))) {
                System.out.println("alias differs");
                return false;
            }
        }
        return result;
    }

    public static Location createLocation(String name, int r, int c, long locationTypeId) {
        Location location = SearchManager.sm().findLocation(locationTypeId, r, c);
        if (location == null) {
            location = new Location();
        } else {
            location = location.createCopy();
        }
        if (name != null && !name.isEmpty()) {
            location.setName(name);
        } else {
            location.setName("(" + Statics.Alphabet[r] + "," + c + ")");
        }
        location.setCol(c);
        location.setRow(r);
        location.setLocationTypeId(locationTypeId);

        return location;
    }


    @Override
    protected int findMatch(String searchTerm) {
        int match = super.findMatch(searchTerm);

        // Local objects
        if (getLocationType() != null && getLocationType().getName().toUpperCase().contains(searchTerm)) match++;
        if (Statics.Alphabet[row].toUpperCase().contains(searchTerm)) match++;
        if (String.valueOf(col).toUpperCase().contains(searchTerm)) match++;
        if (getPrettyString().toUpperCase().contains(searchTerm)) match++;
        if (getAlias().toUpperCase().contains(searchTerm)) match++;

        return match;
    }

    @Override
    public Location createCopy(DbObject copyInto) {
        Location cpy = (Location) copyInto;
        copyBaseFields(cpy);

        cpy.setLocationTypeId(getLocationTypeId());
        cpy.setRow(getRow());
        cpy.setCol(getCol());
        cpy.setAlias(getAlias());

        return cpy;
    }

    @Override
    public Location createCopy() {
        return createCopy(new Location());
    }


    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<Location> list = cache().getLocations();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {

                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<Location> list = cache().getLocations();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    public boolean hasItems() {
        return getItems().size() > 0;
    }

    public List<DbObject> getItems() {
        if (items == null) {
            items = new ArrayList<>();
            List<Item> itemList = cache().getItems();
            for (Item item : itemList) {
                // If locationId < UNKNOWN_ID, the item has no location or the location is derived from the SetItems
                if (item.getLocationId() == getId()) {
                    items.add(item);
                } else {
                    if (item.isSet()) {
                        for (SetItem setItem : item.getSetItems()) {
                            if (setItem.getLocationId() == getId()) {
                                items.add(setItem);
                            }
                        }
                    }
                }
            }
        }

        return items;
    }

    public void updateItems() {
        items = null;
    }

    public long getLocationTypeId() {
        return locationTypeId;
    }

    public void setLocationTypeId(long locationTypeId) {
        if (locationType != null && locationType.getId() != locationTypeId) {
            locationType = null;
        }
        this.locationTypeId = locationTypeId;
    }

    public LocationType getLocationType() {
        if (locationType == null) {
            locationType = SearchManager.sm().findLocationTypeById(locationTypeId);
        }
        return locationType;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
        this.layout.y = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int column) {
        this.col = column;
        this.layout.x = column;
    }

    public String getAlias() {
        if (alias == null) {
            alias = "";
        }
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setLayout(int x, int y, int w, int h) {
        layout.set(x, y, w, h);
    }

    public void setLayout(int x, int y, int w, int h, int wx, int wy) {
        layout.set(x, y, w, h, wx, wy);
    }

    public LocationLayout getLayout() {
        return layout;
    }

    //
    // Helper class for layout buttons
    //
    public static class LocationLayout {
        public int x = 0; // X coordinate
        public int y = 0; // Y coordinate
        public int w = 1; // Width
        public int h = 1; // Height
        public int wx = 0; // Weight x
        public int wy = 0; // Weight y

        LocationLayout() {

        }

        public void set(int x, int y, int w, int h) {
            set(x, y, w, h, 0, 0);
        }

        public void set(int x, int y, int w, int h, int wx, int wy) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.wx = wx;
            this.wy = wy;
        }
    }
}
