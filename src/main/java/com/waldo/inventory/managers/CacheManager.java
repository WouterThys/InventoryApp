package com.waldo.inventory.managers;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.classes.dbclasses.Package;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.CacheChangedListener;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class CacheManager {

    private static final LogManager LOG = LogManager.LOG(CacheManager.class);

    private static final CacheManager INSTANCE = new CacheManager();
    public static CacheManager cache() {
        return INSTANCE;
    }

    private CacheManager() {}




    public List<CacheChangedListener<Item>> onItemsChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Category>> onCategoriesChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Product>> onProductsChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Type>> onTypesChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Manufacturer>> onManufacturerChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Order>> onOrdersChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Location>> onLocationsChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<LocationType>> onLocationTYpeChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<OrderItem>> onOrderItemsChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Distributor>> onDistributorsChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<DistributorPartLink>> onPartNumbersChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<PackageType>> onPackageTypesChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Project>> onProjectChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<ProjectIDE>> onProjectIDEChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<OrderFileFormat>> onOrderFileFormatChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Package>> onPackageChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<SetItem>> onSetItemChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<PcbItem>> onPcbItemChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<PcbItemItemLink>> onPcbItemItemLinkChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<ProjectCode>> onProjectCodeChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<ProjectPcb>> onProjectPcbChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<ProjectOther>> onProjectOtherChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<PcbItemProjectLink>> onPcbItemProjectLinkChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<ParserItemLink>> onParserItemLinkChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<DistributorPartLink>> onDistributorPartLinkChangedListenerList = new ArrayList<>();

    // Part numbers...

    // Cached lists
    private List<Item> items;
    private List<Category> categories;
    private List<Product> products;
    private List<Type> types;
    private List<Manufacturer> manufacturers;
    private List<Location> locations;
    private List<LocationType> locationTypes;
    private List<Order> orders;
    private List<OrderItem> orderItems;
    private List<Distributor> distributors;
    private List<DistributorPartLink> distributorPartLinks;
    private List<PackageType> packageTypes;
    private List<Project> projects;
    private List<ProjectIDE> projectIDES;
    private List<OrderFileFormat> orderFileFormats;
    private List<Package> packages;
    private List<SetItem> setItems;
    private List<PcbItem> pcbItems;
    private List<PcbItemItemLink> pcbItemItemLinks;
    private List<PcbItemProjectLink> pcbItemProjectLinks;
    private List<Log> logs;
    private List<DbHistory> dbHistoryList;
    private List<ProjectCode> projectCodes;
    private List<ProjectPcb> projectPcbs;
    private List<ProjectOther> projectOthers;
    private List<ParserItemLink> parserItemLinks;


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void addOnItemsChangedListener(CacheChangedListener<Item> cacheChangedListener) {
        if (!onItemsChangedListenerList.contains(cacheChangedListener)) {
            onItemsChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnCategoriesChangedListener(CacheChangedListener<Category> cacheChangedListener) {
        if (!onCategoriesChangedListenerList.contains(cacheChangedListener)) {
            onCategoriesChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnProductsChangedListener(CacheChangedListener<Product> cacheChangedListener) {
        if (!onProductsChangedListenerList.contains(cacheChangedListener)) {
            onProductsChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnTypesChangedListener(CacheChangedListener<Type> cacheChangedListener) {
        if (!onTypesChangedListenerList.contains(cacheChangedListener)) {
            onTypesChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnManufacturerChangedListener(CacheChangedListener<Manufacturer> cacheChangedListener) {
        if (!onManufacturerChangedListenerList.contains(cacheChangedListener)) {
            onManufacturerChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnLocationsChangedListener(CacheChangedListener<Location> cacheChangedListener) {
        if (!onLocationsChangedListenerList.contains(cacheChangedListener)) {
            onLocationsChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnLocationTypeChangedListener(CacheChangedListener<LocationType> cacheChangedListener) {
        if (!onLocationTYpeChangedListenerList.contains(cacheChangedListener)) {
            onLocationTYpeChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnOrdersChangedListener(CacheChangedListener<Order> cacheChangedListener) {
        if (!onOrdersChangedListenerList.contains(cacheChangedListener)) {
            onOrdersChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnOrderItemsChangedListener(CacheChangedListener<OrderItem> cacheChangedListener) {
        if (!onOrderItemsChangedListenerList.contains(cacheChangedListener)) {
            onOrderItemsChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnDistributorChangedListener(CacheChangedListener<Distributor> cacheChangedListener) {
        if (!onDistributorsChangedListenerList.contains(cacheChangedListener)) {
            onDistributorsChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnPartNumbersChangedListener(CacheChangedListener<DistributorPartLink> cacheChangedListener) {
        if (!onPartNumbersChangedListenerList.contains(cacheChangedListener)) {
            onPartNumbersChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnProjectChangedListener(CacheChangedListener<Project> cacheChangedListener) {
        if (!onProjectChangedListenerList.contains(cacheChangedListener)) {
            onProjectChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnPackageChangedListener(CacheChangedListener<Package> cacheChangedListener) {
        if (!onPackageChangedListenerList.contains(cacheChangedListener)) {
            onPackageChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnPackageTypeChangedListener(CacheChangedListener<PackageType> cacheChangedListener) {
        if (!onPackageTypesChangedListenerList.contains(cacheChangedListener)) {
            onPackageTypesChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnProjectTypeChangedListener(CacheChangedListener<ProjectIDE> cacheChangedListener) {
        if (!onProjectIDEChangedListenerList.contains(cacheChangedListener)) {
            onProjectIDEChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnOrderFileFormatChangedListener(CacheChangedListener<OrderFileFormat> cacheChangedListener) {
        if (!onOrderFileFormatChangedListenerList.contains(cacheChangedListener)) {
            onOrderFileFormatChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnKcComponentChangedListener(CacheChangedListener<PcbItem> cacheChangedListener) {
        if (!onPcbItemChangedListenerList.contains(cacheChangedListener)) {
            onPcbItemChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnPcbItemItemLinkChangedListener(CacheChangedListener<PcbItemItemLink> cacheChangedListener) {
        if (!onPcbItemItemLinkChangedListenerList.contains(cacheChangedListener)) {
            onPcbItemItemLinkChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnProjectCodeChangedListener(CacheChangedListener<ProjectCode> cacheChangedListener) {
        if (!onProjectCodeChangedListenerList.contains(cacheChangedListener)) {
            onProjectCodeChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnProjectPcbChangedListener(CacheChangedListener<ProjectPcb> cacheChangedListener) {
        if (!onProjectPcbChangedListenerList.contains(cacheChangedListener)) {
            onProjectPcbChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnPcbItemLinkChangedListener(CacheChangedListener<PcbItemProjectLink> cacheChangedListener) {
        if (!onPcbItemProjectLinkChangedListenerList.contains(cacheChangedListener)) {
            onPcbItemProjectLinkChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnProjectOtherChangedListener(CacheChangedListener<ProjectOther> cacheChangedListener) {
        if (!onProjectOtherChangedListenerList.contains(cacheChangedListener)) {
            onProjectOtherChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnParserItemLinkChangedListener(CacheChangedListener<ParserItemLink> cacheChangedListener) {
        if (!onParserItemLinkChangedListenerList.contains(cacheChangedListener)) {
            onParserItemLinkChangedListenerList.add(cacheChangedListener);
        }
    }

    public void addOnDistributorPartLinkChangedListener(CacheChangedListener<DistributorPartLink> cacheChangedListener) {
        if (!onDistributorPartLinkChangedListenerList.contains(cacheChangedListener)) {
            onDistributorPartLinkChangedListenerList.add(cacheChangedListener);
        }
    }


    public void removeOnCategoriesChangedListener(CacheChangedListener<Category> cacheChangedListener) {
        if (onCategoriesChangedListenerList != null) {
            if (onCategoriesChangedListenerList.contains(cacheChangedListener)) {
                onCategoriesChangedListenerList.remove(cacheChangedListener);
            }
        }
    }

    public void removeOnProductsChangedListener(CacheChangedListener<Product> cacheChangedListener) {
        if (onProductsChangedListenerList != null) {
            if (onProductsChangedListenerList.contains(cacheChangedListener)) {
                onProductsChangedListenerList.remove(cacheChangedListener);
            }
        }
    }

    public void removeOnTypesChangedListener(CacheChangedListener<Type> cacheChangedListener) {
        if (onTypesChangedListenerList != null) {
            if (onTypesChangedListenerList.contains(cacheChangedListener)) {
                onTypesChangedListenerList.remove(cacheChangedListener);
            }
        }
    }

    public void removeOnOrdersChangedListener(CacheChangedListener<Order> cacheChangedListener) {
        if (onOrdersChangedListenerList != null) {
            if (onOrdersChangedListenerList.contains(cacheChangedListener)) {
                onOrdersChangedListenerList.remove(cacheChangedListener);
            }
        }
    }

    public <T extends DbObject> void notifyListeners(int changedHow, T object, List<CacheChangedListener<T>> listeners) {
        for (CacheChangedListener<T> l : listeners) {
            switch (changedHow) {
                case DbManager.OBJECT_INSERT:
                    try {
                        SwingUtilities.invokeLater(() -> l.onInserted(object));
                    } catch (Exception e) {
                        LOG.error("Error after insert of " + object.getName(), e);
                    }
                    break;
                case DbManager.OBJECT_UPDATE:
                    try {
                        SwingUtilities.invokeLater(() -> l.onUpdated(object));
                    } catch (Exception e) {
                        LOG.error("Error after update of " + object.getName(), e);
                    }
                    break;
                case DbManager.OBJECT_DELETE:
                    try {
                        SwingUtilities.invokeLater(() -> l.onDeleted(object));
                    } catch (Exception e) {
                        LOG.error("Error after delete of " + object.getName(), e);
                    }
                    break;
                case DbManager.OBJECT_CACHE_CLEAR:
                    try {
                        SwingUtilities.invokeLater(l::onCacheCleared);
                    } catch (Exception e) {
                        LOG.error("Error after clearing cache", e);
                    }
            }
        }
    }

    public void clearCache() {
//        items = null; db().notifyListeners(OBJECT_CACHE_CLEAR, null, onItemsChangedListenerList);
//        categories = null; notifyListeners(OBJECT_CACHE_CLEAR, null, onCategoriesChangedListenerList);
//        products = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onProductsChangedListenerList);
//        types = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onTypesChangedListenerList);
//        manufacturers = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onManufacturerChangedListenerList);
//        locations = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onLocationsChangedListenerList);
//        locationTypes = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onLocationTYpeChangedListenerList);
//        orders = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onOrdersChangedListenerList);
//        orderItems = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onOrderItemsChangedListenerList);
//        distributors = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onDistributorsChangedListenerList);
//        distributorPartLinks = null;
//        packageTypes = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onPackageTypesChangedListenerList);
//        projects = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onProjectChangedListenerList);
//        projectIDES = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onProjectIDEChangedListenerList);
//        orderFileFormats = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onOrderFileFormatChangedListenerList);
//        packages = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onPackageChangedListenerList);
//        setItems = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onSetItemChangedListenerList);
//        pcbItems = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onPcbItemChangedListenerList);
//        pcbItemItemLinks = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onPcbItemItemLinkChangedListenerList);
//        logs = null;
    }



    public List<Item> getItems() {
        if (items == null) {
            items = db().updateItems();
        }
        return items;
    }

    public List<Category> getCategories() {
        if (categories == null) {
            categories = db().updateCategories();
        }
        return categories;
    }

    public List<Product> getProducts() {
        if (products == null) {
            products = db().updateProducts();
        }
        return products;
    }

    public List<Type> getTypes() {
        if (types == null) {
            types = db().updateTypes();
        }
        return types;
    }

    public List<Manufacturer> getManufacturers() {
        if (manufacturers == null) {
            manufacturers = db().updateManufacturers();
        }
        return manufacturers;
    }

    public synchronized List<Location> getLocations()   {
        if (locations == null) {
            locations = db().updateLocations();
        }
        return locations;
    }

    public List<LocationType> getLocationTypes()   {
        if (locationTypes == null) {
            locationTypes = db().updateLocationTypes();
        }
        return locationTypes;
    }

    public List<Order> getOrders()    {
        if (orders == null) {
           orders = db().updateOrders();
        }
        return orders;
    }

    public List<OrderItem> getOrderItems()    {
        if (orderItems == null) {
            orderItems = db().updateOrderItems();
        }
        return orderItems;
    }

    public List<OrderItem> getOrderedItems(long orderId) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItem i : getOrderItems()) {
            if (i.getOrderId() == orderId || orderId == -1) {
                items.add(i);
            }
        }
        return items;
    }

    public List<Distributor> getDistributors()    {
        if (distributors == null) {
            distributors = db().updateDistributors();
        }
        return distributors;
    }

    public List<DistributorPartLink> getDistributorPartLinks()    {
        if (distributorPartLinks == null) {
            distributorPartLinks = db().updateDistributorParts();
        }
        return distributorPartLinks;
    }

    public List<Package> getPackages()    {
        if (packages == null) {
            packages = db().updatePackages();
        }
        return packages;
    }

    public List<PackageType> getPackageTypes()    {
        if (packageTypes == null) {
            packageTypes = db().updatePackageTypes();
        }
        return packageTypes;
    }

    public List<Project> getProjects()    {
        if (projects == null) {
            projects = db().updateProjects();
        }
        return projects;
    }

    public List<ProjectCode> getProjectCodes()    {
        if (projectCodes == null) {
            projectCodes = db().updateProjectCodes();
        }
        return projectCodes;
    }

    public List<ProjectPcb> getProjectPcbs()    {
        if (projectPcbs == null) {
            projectPcbs = db().updateProjectPcbs();
        }
        return projectPcbs;
    }

    public List<PcbItemProjectLink> getPcbItemProjectLinks()    {
        if (pcbItemProjectLinks == null) {
            pcbItemProjectLinks = db().updatePcbItemLinks();
        }
        return pcbItemProjectLinks;
    }

    public List<ProjectIDE> getProjectIDES()    {
        if (projectIDES == null) {
            projectIDES = db().updateProjectIDEs();
        }
        return projectIDES;
    }

    public List<ParserItemLink> getParserItemLinks()    {
        if (parserItemLinks == null) {
            parserItemLinks = db().updateParserItemLinks();
        }
        return parserItemLinks;
    }

    public List<OrderFileFormat> getOrderFileFormats()    {
        if (orderFileFormats == null) {
            orderFileFormats = db().updateOrderFileFormats();
        }
        return orderFileFormats;
    }

    public List<SetItem> getSetItems()    {
        if (setItems == null) {
            setItems = db().updateSetItems();
        }
        return setItems;
    }

    public List<PcbItem> getPcbItems()    {
        if (pcbItems == null) {
            pcbItems = db().updatePcbItems();
        }
        return pcbItems;
    }

    public List<PcbItemItemLink> getPcbItemItemLinks()    {
        if (pcbItemItemLinks == null) {
            pcbItemItemLinks = db().updateKcItemLinks();
        }
        return pcbItemItemLinks;
    }

    public List<Log> getLogs()    {
        if (logs == null) {
            logs = db().updateLogs();
        }
        return logs;
    }

    public List<Log> getLogsByType(boolean info, boolean debug, boolean warn, boolean error) {
        List<Log> logList = new ArrayList<>();
        for (Log log : getLogs()) {
            switch (log.getLogType()) {
                case Statics.LogTypes.INFO:
                    if (info) logList.add(log);
                    break;
                case Statics.LogTypes.DEBUG:
                    if (debug) logList.add(log);
                    break;
                case Statics.LogTypes.WARN:
                    if (warn) logList.add(log);
                    break;
                case Statics.LogTypes.ERROR:
                    if (error) logList.add(log);
                    break;
            }
        }
        return logList;
    }

    public List<DbHistory> getDbHistory() {
        if (dbHistoryList == null) {
            dbHistoryList = db().updateDbHistoryList();
        }
        return dbHistoryList;
    }

    public List<Order> getOrdersForManufacturer(long manufacturerId) {
        return null;
    }

    public List<Item> getItemsForManufacturer(long manufacturerId)    {
        List<Item> items = new ArrayList<>();
        for (Item item : getItems()) {
            if (item.getManufacturerId() == manufacturerId) {
                items.add(item);
            }
        }
        return items;
    }

    public List<Item> getItemsForCategory(long categoryId)    {
        List<Item> items = new ArrayList<>();
        for (Item item : getItems()) {
            if (item.getCategoryId() == categoryId) {
                items.add(item);
            }
        }
        return items;
    }

    public boolean isItemInCurrentOrders(long itemId) {
        for (OrderItem oi : getOrderItems()) {
            if (!oi.getOrder().isOrdered()) {
                if (oi.getItemId() == itemId) {
                    return true;
                }
            }
        }
        return false;
    }

}
