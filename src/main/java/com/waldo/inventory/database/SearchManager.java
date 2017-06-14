package com.waldo.inventory.database;

import com.waldo.inventory.classes.*;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.*;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class SearchManager {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(SearchManager.class);

    private static final SearchManager INSTANCE = new SearchManager();

    public static SearchManager sm() {
        return INSTANCE;
    }

    private boolean searched = false;
    private boolean inAdvanced = false;
    private boolean hasAdvancedSearchOption;
    private int[] searchOptions;
    private List<DbObject> searchList;

    public SearchManager() {
    }

    public SearchManager(int ... searchOptions) {
        this.searchOptions = searchOptions;
    }

    public SearchManager(List<DbObject> searchList) {
        this.searchList = searchList;
    }

    public SearchManager(List<DbObject> searchList, int ... searchOptions) {
        this.searchList = searchList;
        this.searchOptions = searchOptions;
    }


    public List<DbObject> search(String searchWord) {
        List<DbObject> foundObjects;

        // Search list
        if (searchOptions == null || searchOptions.length == 0) {
            if (searchList == null) {
                foundObjects = searchAllKnownObjects(searchWord);
            } else {
                foundObjects = searchForObject(searchList, searchWord);
            }
        } else {
            // Should work with search options -> more specific search
            if (searchList == null) {
                foundObjects = searchSpecific(searchOptions, searchWord);
            } else {
                foundObjects = searchSpecific(searchList, searchOptions, searchWord);
            }
        }

        return foundObjects;
    }


    public List<DbObject> searchAllKnownObjects(String searchWord) {
        List<DbObject> foundList = new ArrayList<>();

        // Categories
        Status().setMessage("Searching for: Categories");
        foundList.addAll(searchForObject(new ArrayList<>(db().getCategories()), searchWord));

        // Products
        Status().setMessage("Searching for: Products");
        foundList.addAll(searchForObject(new ArrayList<>(db().getProducts()), searchWord));

        // Types
        Status().setMessage("Searching for: Types");
        foundList.addAll(searchForObject(new ArrayList<>(db().getTypes()), searchWord));

        // Orders
        Status().setMessage("Searching for: Orders");
        foundList.addAll(searchForObject(new ArrayList<>(db().getOrders()), searchWord));

        // Locations
        Status().setMessage("Searching for: Locations");
        foundList.addAll(searchForObject(new ArrayList<>(db().getLocations()), searchWord));

        // Manufacturers
        Status().setMessage("Searching for: Manufacturers");
        foundList.addAll(searchForObject(new ArrayList<>(db().getManufacturers()), searchWord));

        // Distributors
        Status().setMessage("Searching for: Distributors");
        foundList.addAll(searchForObject(new ArrayList<>(db().getDistributors()), searchWord));

        // Items
        Status().setMessage("Searching for: Items");
        foundList.addAll(searchForObject(new ArrayList<>(db().getItems()), searchWord));

        // Package types
        Status().setMessage("Searching for: Package types");
        foundList.addAll(searchForObject(new ArrayList<>(db().getPackageTypes()), searchWord));

        return foundList;
    }

    public List<DbObject> searchSpecificAnd(int[] searchResultType, String... args) {
        List<DbObject> foundList = new ArrayList<>();
        if (args.length > 0) {
            // First search
            foundList = searchSpecific(searchResultType, args[0]);
            if (args.length > 1) {
                for (int i = 1; i < args.length; i++) {
                    foundList.addAll(searchSpecific(foundList, searchResultType, args[i]));
                }
            }
        }
        return foundList;
    }

    public List<DbObject> searchSpecific(int[] searchOptions, String searchWord) {
        List<DbObject> foundList = new ArrayList<>();
        for (int type : searchOptions) {
            switch (type) {
                case DbObject.TYPE_CATEGORY:
                    Status().setMessage("Searching for: Categories");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getCategories()), searchWord));
                    break;
                case DbObject.TYPE_PRODUCT:
                    Status().setMessage("Searching for: Products");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getProducts()), searchWord));
                    break;
                case DbObject.TYPE_TYPE:
                    Status().setMessage("Searching for: Types");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getTypes()), searchWord));
                    break;
                case DbObject.TYPE_ORDER:
                    Status().setMessage("Searching for: Orders");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getOrders()), searchWord));
                    break;
                case DbObject.TYPE_LOCATION:
                    Status().setMessage("Searching for: Locations");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getLocations()), searchWord));
                    break;
                case DbObject.TYPE_MANUFACTURER:
                    Status().setMessage("Searching for: Manufacturers");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getManufacturers()), searchWord));
                    break;
                case DbObject.TYPE_DISTRIBUTOR:
                    Status().setMessage("Searching for: Distributors");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getDistributors()), searchWord));
                    break;
                case DbObject.TYPE_ITEM:
                    Status().setMessage("Searching for: Items");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getItems()), searchWord));
                    break;
                case DbObject.TYPE_PACKAGE_TYPE:
                    Status().setMessage("Searching for: PackageType");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getPackageTypes()), searchWord));
                    break;
                default:
                    break;
            }
        }
        return foundList;
    }

    public List<DbObject> searchSpecific(List<DbObject> searchList, int[] searchOptions, String searchWord) {
        List<DbObject> foundList = new ArrayList<>();
        for (int type : searchOptions) {
            switch (type) {
                case DbObject.TYPE_CATEGORY:
                    Status().setMessage("Searching for: Categories");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                case DbObject.TYPE_PRODUCT:
                    Status().setMessage("Searching for: Products");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                case DbObject.TYPE_TYPE:
                    Status().setMessage("Searching for: Types");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                case DbObject.TYPE_ORDER:
                    Status().setMessage("Searching for: Orders");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                case DbObject.TYPE_LOCATION:
                    Status().setMessage("Searching for: Locations");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                case DbObject.TYPE_MANUFACTURER:
                    Status().setMessage("Searching for: Manufacturers");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                case DbObject.TYPE_DISTRIBUTOR:
                    Status().setMessage("Searching for: Distributors");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                case DbObject.TYPE_ITEM:
                    Status().setMessage("Searching for: Items");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                case DbObject.TYPE_PACKAGE_TYPE:
                    Status().setMessage("Searching for: PackageTypes");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                case DbObject.TYPE_PACKAGE:
                    Status().setMessage("Searching for: Packages");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                default:
                    break;
            }
        }
        return foundList;
    }

    public List<DbObject> searchForObject(List<DbObject> listToSearch, String searchWord) {
        List<DbObject> foundList = new ArrayList<>();
        if (listToSearch == null || listToSearch.size() == 0) {
            return foundList;
        }

        searchWord = searchWord.toUpperCase();

        for (DbObject dbo : listToSearch) {
            if (dbo.hasMatch(searchWord)) {
                foundList.add(dbo);
            }
        }

        return foundList;
    }

    public boolean isSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public boolean isInAdvanced() {
        return inAdvanced;
    }

    public void setInAdvanced(boolean inAdvanced) {
        this.inAdvanced = inAdvanced;
    }

    public boolean isHasAdvancedSearchOption() {
        return hasAdvancedSearchOption;
    }

    public void setHasAdvancedSearchOption(boolean hasAdvancedSearchOption) {
        this.hasAdvancedSearchOption = hasAdvancedSearchOption;
    }

    public int[] getSearchOptions() {
        return searchOptions;
    }

    public void setSearchOptions(int[] searchOptions) {
        this.searchOptions = searchOptions;
    }

    public List<DbObject> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<DbObject> searchList) {
        this.searchList = searchList;
    }

    /*
        *                  FINDERS
        * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public Item findItemById(long id) {
        for (Item i : db().getItems()) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    public Item findItemByName(String name) {
        for (Item i : db().getItems()) {
            if (i.getName().toUpperCase().equals(name.toUpperCase())) {
                return i;
            }
        }
        return null;
    }

    public Category findCategoryById(long id) {
        for (Category c : db().getCategories()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public Category findCategoryByName(String name) {
        for (Category c : db().getCategories()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public int findCategoryIndex(long categoryId) {
        for (int i = 0; i < db().getCategories().size(); i++) {
            if (db().getCategories().get(i).getId() == categoryId) {
                return i;
            }
        }
        return -1;
    }

    public Product findProductById(long id) {
        for (Product p : db().getProducts()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public Product findProductByName(String name) {
        for (Product p : db().getProducts()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public int findProductIndex(long productNdx) {
        for (int i = 0; i < db().getProducts().size(); i++) {
            if (db().getProducts().get(i).getId() == productNdx) {
                return i;
            }
        }
        return -1;
    }

    public Type findTypeById(long id) {
        for (Type t : db().getTypes()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Type findTypeByName(String name) {
        for (Type t : db().getTypes()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int findTypeIndex(long typeNdx) {
        for (int i = 0; i < db().getTypes().size(); i++) {
            if (db().getTypes().get(i).getId() == typeNdx) {
                return i;
            }
        }
        return -1;
    }

    public Manufacturer findManufacturerById(long id) {
        for (Manufacturer m : db().getManufacturers()) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public Manufacturer findManufacturerByName(String name) {
        for (Manufacturer m : db().getManufacturers()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    public int findManufacturerIndex(long manufacturerId) {
        for (int i = 0; i < db().getManufacturers().size(); i++) {
            if (db().getManufacturers().get(i).getId() == manufacturerId) {
                return i;
            }
        }
        return -1;
    }

    public Location findLocationById(long id) {
        for (Location t : db().getLocations()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Location findLocationByName(String name) {
        for (Location t : db().getLocations()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int findLocationIndex(long typeNdx) {
        for (int i = 0; i < db().getLocations().size(); i++) {
            if (db().getLocations().get(i).getId() == typeNdx) {
                return i;
            }
        }
        return -1;
    }

    public Order findOrderById(long id) {
        for (Order t : db().getOrders()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Order findOrderByName(String name) {
        for (Order t : db().getOrders()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int findOrderIndex(long typeNdx) {
        for (int i = 0; i < db().getOrders().size(); i++) {
            if (db().getOrders().get(i).getId() == typeNdx) {
                return i;
            }
        }
        return -1;
    }

    public OrderItem findOrderItemById(long id) {
        for (OrderItem t : db().getOrderItems()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public OrderItem findOrderItemByName(String name) {
        for (OrderItem t : db().getOrderItems()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int findOrderItemIndex(long orderItemId) {
        for (int i = 0; i < db().getOrders().size(); i++) {
            if (db().getOrderItems().get(i).getId() == orderItemId) {
                return i;
            }
        }
        return -1;
    }

    public Distributor findDistributorById(long distributorId) {
        for (Distributor d : db().getDistributors()) {
            if (d.getId() == distributorId) {
                return d;
            }
        }
        return null;
    }

    public PartNumber findPartNumber(long distributorId, long itemId) {
        for (PartNumber pn : db().getPartNumbers()) {
            if (pn.getDistributorId() == distributorId && pn.getItemId() == itemId) {
                return pn;
            }
        }
        return null;
    }

    public List<Order> findOrdersForItem(long itemId) {
        List<Order> orders = new ArrayList<>();
        for (OrderItem oi : db().getOrderItems()) {
            if (oi.getItemId() == itemId) {
                orders.add(oi.getOrder());
            }
        }
        return orders;
    }

    public PackageType findPackageTypeById(long id) {
        for (PackageType pt : db().getPackageTypes()) {
            if (pt.getId() == id) {
                return pt;
            }
        }
        return null;
    }

    public PackageType findPackageTypeByName(String name) {
        for (PackageType pt : db().getPackageTypes()) {
            if (pt.getName().equals(name)) {
                return pt;
            }
        }
        return null;
    }

}
