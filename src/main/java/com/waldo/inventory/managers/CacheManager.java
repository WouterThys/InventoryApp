package com.waldo.inventory.managers;

import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.classes.CacheLog;
import com.waldo.inventory.classes.cache.CacheList;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.classes.dbclasses.Package;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.database.interfaces.CacheChangedListener;

import javax.swing.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DatabaseAccess.db;

public class CacheManager {

    private static final LogManager LOG = LogManager.LOG(CacheManager.class);
    private final List<CacheLog> cacheLogList;
    private final Date initTime;

    private static final CacheManager INSTANCE = new CacheManager();
    public static CacheManager cache() {
        return INSTANCE;
    }

    private CacheManager() {
        cacheLogList = new ArrayList<>();
        cacheLogList.add(new CacheLog("Items") {
            @Override
            public CacheList getCacheList() {
                return items;
            }
        });
        cacheLogList.add(new CacheLog("Categories"){
            @Override
            public CacheList getCacheList() {
                return categories;
            }
        });
        cacheLogList.add(new CacheLog("Products") {
            @Override
            public CacheList getCacheList() {
                return products;
            }
        });
        cacheLogList.add(new CacheLog("Types") {
            @Override
            public CacheList getCacheList() {
                return types;
            }
        });
        cacheLogList.add(new CacheLog("Manufacturers") {
            @Override
            public CacheList getCacheList() {
                return manufacturers;
            }
        });
        cacheLogList.add(new CacheLog("Locations") {
            @Override
            public CacheList getCacheList() {
                return locations;
            }
        });
        cacheLogList.add(new CacheLog("Location types") {
            @Override
            public CacheList getCacheList() {
                return locationTypes;
            }
        });
        cacheLogList.add(new CacheLog("Orders"){
            @Override
            public CacheList getCacheList() {
                return orders;
            }
        });
        cacheLogList.add(new CacheLog("Order items") {
            @Override
            public CacheList getCacheList() {
                return orderItems;
            }
        });
        cacheLogList.add(new CacheLog("Distributors") {
            @Override
            public CacheList getCacheList() {
                return distributors;
            }
        });
        cacheLogList.add(new CacheLog("Distributor part links") {
            @Override
            public CacheList getCacheList() {
                return distributorPartLinks;
            }
        });
        cacheLogList.add(new CacheLog("Packages") {
            @Override
            public CacheList getCacheList() {
                return packages;
            }
        });
        cacheLogList.add(new CacheLog("Package types") {
            @Override
            public CacheList getCacheList() {
                return packageTypes;
            }
        });
        cacheLogList.add(new CacheLog("Projects"){
            @Override
            public CacheList getCacheList() {
                return projects;
            }
        });
        cacheLogList.add(new CacheLog("Project IDEs") {
            @Override
            public CacheList getCacheList() {
                return projectIDES;
            }
        });
        cacheLogList.add(new CacheLog("Order file formats") {
            @Override
            public CacheList getCacheList() {
                return orderFileFormats;
            }
        });
        cacheLogList.add(new CacheLog("Set items") {
            @Override
            public CacheList getCacheList() {
                return setItems;
            }
        });
        cacheLogList.add(new CacheLog("PCB items") {
            @Override
            public CacheList getCacheList() {
                return pcbItems;
            }
        });
        cacheLogList.add(new CacheLog("PCB item links") {
            @Override
            public CacheList getCacheList() {
                return pcbItemItemLinks;
            }
        });
        cacheLogList.add(new CacheLog("PCB item project links") {
            @Override
            public CacheList getCacheList() {
                return pcbItemProjectLinks;
            }
        });
        cacheLogList.add(new CacheLog("Logs") {
            @Override
            public CacheList getCacheList() {
                return logs;
            }
        });
        cacheLogList.add(new CacheLog("Database history") {
            @Override
            public CacheList getCacheList() {
                return dbHistoryList;
            }
        });
        cacheLogList.add(new CacheLog("Project codes") {
            @Override
            public CacheList getCacheList() {
                return projectCodes;
            }
        });
        cacheLogList.add(new CacheLog("Project PCBs") {
            @Override
            public CacheList getCacheList() {
                return projectPcbs;
            }
        });
        cacheLogList.add(new CacheLog("Project others") {
            @Override
            public CacheList getCacheList() {
                return projectOthers;
            }
        });
        cacheLogList.add(new CacheLog("Project item links") {
            @Override
            public CacheList getCacheList() {
                return pcbItemProjectLinks;
            }
        });
        initTime = DateUtils.now();
    }

    //public List<CacheChangedListener<Item>> onItemsChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Category>> onCategoriesChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Product>> onProductsChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Type>> onTypesChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Manufacturer>> onManufacturerChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Order>> onOrdersChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<Location>> onLocationsChangedListenerList = new ArrayList<>();
    public List<CacheChangedListener<LocationType>> onLocationTYpeChangedListenerList = new ArrayList<>();
    //public List<CacheChangedListener<OrderItem>> onOrderItemsChangedListenerList = new ArrayList<>();
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

    // Cached lists
    private CacheList<Item> items = null;
    private CacheList<Category> categories = null;
    private CacheList<Product> products = null;
    private CacheList<Type> types = null;
    private CacheList<Manufacturer> manufacturers = null;
    private CacheList<Location> locations = null;
    private CacheList<LocationType> locationTypes = null;
    private CacheList<Order> orders = null;
    private CacheList<OrderItem> orderItems = new CacheList<>();
    private CacheList<Distributor> distributors = null;
    private CacheList<DistributorPartLink> distributorPartLinks = null;
    private CacheList<PackageType> packageTypes = null;
    private CacheList<Project> projects = null;
    private CacheList<ProjectIDE> projectIDES = null;
    private CacheList<OrderFileFormat> orderFileFormats = null;
    private CacheList<Package> packages = null;
    private CacheList<SetItem> setItems = null;
    private CacheList<PcbItem> pcbItems = null;
    private CacheList<PcbItemItemLink> pcbItemItemLinks = null;
    private CacheList<PcbItemProjectLink> pcbItemProjectLinks = null;
    private CacheList<Log> logs = null;
    private CacheList<DbHistory> dbHistoryList = null;
    private CacheList<ProjectCode> projectCodes = null;
    private CacheList<ProjectPcb> projectPcbs = null;
    private CacheList<ProjectOther> projectOthers = null;
    private CacheList<ParserItemLink> parserItemLinks = null;


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//    public void addOnItemsChangedListener(CacheChangedListener<Item> cacheChangedListener) {
//        if (!onItemsChangedListenerList.contains(cacheChangedListener)) {
//            onItemsChangedListenerList.add(cacheChangedListener);
//        }
//    }

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

//    public void addOnOrderItemsChangedListener(CacheChangedListener<OrderItem> cacheChangedListener) {
//        if (!onOrderItemsChangedListenerList.contains(cacheChangedListener)) {
//            onOrderItemsChangedListenerList.add(cacheChangedListener);
//        }
//    }
    public void addOrderItemListeners(CacheChangedListener<OrderItem> listener) {
        orderItems.addChangedListener(listener);
    }

    public List<CacheChangedListener<OrderItem>> getOrderItemListeners() {
        return orderItems.getChangedListeners();
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

    public void removeOnOrdersChangedListener(CacheChangedListener<Order> cacheChangedListener) {
        if (onOrdersChangedListenerList.contains(cacheChangedListener)) {
            onOrdersChangedListenerList.remove(cacheChangedListener);
        }
    }

    public <T extends DbObject> void notifyListeners(int changedHow, T object, List<CacheChangedListener<T>> listeners) {
        for (CacheChangedListener<T> l : listeners) {
            switch (changedHow) {
                case DatabaseAccess.OBJECT_INSERT:
                    try {
                        SwingUtilities.invokeLater(() -> l.onInserted(object));
                    } catch (Exception e) {
                        LOG.error("Error after insert of " + object.getName(), e);
                    }
                    break;
                case DatabaseAccess.OBJECT_UPDATE:
                    try {
                        SwingUtilities.invokeLater(() -> l.onUpdated(object));
                    } catch (Exception e) {
                        LOG.error("Error after update of " + object.getName(), e);
                    }
                    break;
                case DatabaseAccess.OBJECT_DELETE:
                    try {
                        SwingUtilities.invokeLater(() -> l.onDeleted(object));
                    } catch (Exception e) {
                        LOG.error("Error after delete of " + object.getName(), e);
                    }
                    break;
                case DatabaseAccess.OBJECT_CACHE_CLEAR:
                    try {
                        SwingUtilities.invokeLater(l::onCacheCleared);
                    } catch (Exception e) {
                        LOG.error("Error after clearing cache", e);
                    }
            }
        }
    }

    public List<CacheLog> getCacheLogList() {
        return cacheLogList;
    }

    public Date getInitTime() {
        return initTime;
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



    public CacheList<Item> getItems() {
        if (items == null) {
            long start = System.nanoTime();
            items = new CacheList<>(db().updateItems(), (System.nanoTime() - start));
        }
        return items;
    }

    public CacheList<Category> getCategories() {
        if (categories == null) {
            long start = System.nanoTime();
            categories = new CacheList<>(db().updateCategories(), (System.nanoTime() - start));
        }
        return categories;
    }

    public CacheList<Product> getProducts() {
        if (products == null) {
            long start = System.nanoTime();
            products = new CacheList<>(db().updateProducts(), (System.nanoTime() - start));
        }
        return products;
    }

    public CacheList<Type> getTypes() {
        if (types == null) {
            long start = System.nanoTime();
            types = new CacheList<>(db().updateTypes(), (System.nanoTime() - start));
        }
        return types;
    }

    public CacheList<Manufacturer> getManufacturers() {
        if (manufacturers == null) {
            long start = System.nanoTime();
            manufacturers = new CacheList<>(db().updateManufacturers(), (System.nanoTime() - start));
        }
        return manufacturers;
    }

    public synchronized CacheList<Location> getLocations()   {
        if (locations == null) {
            long start = System.nanoTime();
            locations = new CacheList<>(db().updateLocations(), (System.nanoTime() - start));
        }
        return locations;
    }

    public CacheList<LocationType> getLocationTypes()   {
        if (locationTypes == null) {
            long start = System.nanoTime();
            locationTypes = new CacheList<>(db().updateLocationTypes(), (System.nanoTime() - start));
        }
        return locationTypes;
    }

    public CacheList<Order> getOrders()    {
        if (orders == null) {
            long start = System.nanoTime();
           orders = new CacheList<>(db().updateOrders(), (System.nanoTime() - start));
        }
        return orders;
    }

    public CacheList<OrderItem> getOrderItems()    {
        if (!orderItems.isFetched()) {
            long start = System.nanoTime();
            orderItems.setList(db().updateOrderItems(), (System.nanoTime() - start));
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

    public CacheList<Distributor> getDistributors()    {
        if (distributors == null) {
            long start = System.nanoTime();
            distributors = new CacheList<>(db().updateDistributors(), (System.nanoTime() - start));
        }
        return distributors;
    }

    public CacheList<DistributorPartLink> getDistributorPartLinks()    {
        if (distributorPartLinks == null) {
            long start = System.nanoTime();
            distributorPartLinks = new CacheList<>(db().updateDistributorParts(), (System.nanoTime() - start));
        }
        return distributorPartLinks;
    }

    public CacheList<Package> getPackages()    {
        if (packages == null) {
            long start = System.nanoTime();
            packages = new CacheList<>(db().updatePackages(), (System.nanoTime() - start));
        }
        return packages;
    }

    public CacheList<PackageType> getPackageTypes()    {
        if (packageTypes == null) {
            long start = System.nanoTime();
            packageTypes = new CacheList<>(db().updatePackageTypes(), (System.nanoTime() - start));
        }
        return packageTypes;
    }

    public CacheList<Project> getProjects()    {
        if (projects == null) {
            long start = System.nanoTime();
            projects = new CacheList<>(db().updateProjects(), (System.nanoTime() - start));
        }
        return projects;
    }

    public CacheList<ProjectCode> getProjectCodes()    {
        if (projectCodes == null) {
            long start = System.nanoTime();
            projectCodes = new CacheList<>(db().updateProjectCodes(), (System.nanoTime() - start));
        }
        return projectCodes;
    }

    public CacheList<ProjectPcb> getProjectPcbs()    {
        if (projectPcbs == null) {
            long start = System.nanoTime();
            projectPcbs = new CacheList<>(db().updateProjectPcbs(), (System.nanoTime() - start));
        }
        return projectPcbs;
    }

    public CacheList<PcbItemProjectLink> getPcbItemProjectLinks()    {
        if (pcbItemProjectLinks == null) {
            long start = System.nanoTime();
            pcbItemProjectLinks = new CacheList<>(db().updatePcbItemLinks(), (System.nanoTime() - start));
        }
        return pcbItemProjectLinks;
    }

    public CacheList<ProjectIDE> getProjectIDES()    {
        if (projectIDES == null) {
            long start = System.nanoTime();
            projectIDES = new CacheList<>(db().updateProjectIDEs(), (System.nanoTime() - start));
        }
        return projectIDES;
    }

    public CacheList<ParserItemLink> getParserItemLinks()    {
        if (parserItemLinks == null) {
            long start = System.nanoTime();
            parserItemLinks = new CacheList<>(db().updateParserItemLinks(), (System.nanoTime() - start));
        }
        return parserItemLinks;
    }

    public CacheList<OrderFileFormat> getOrderFileFormats()    {
        if (orderFileFormats == null) {
            long start = System.nanoTime();
            orderFileFormats = new CacheList<>(db().updateOrderFileFormats(), (System.nanoTime() - start));
        }
        return orderFileFormats;
    }

    public CacheList<SetItem> getSetItems()    {
        if (setItems == null) {
            long start = System.nanoTime();
            setItems = new CacheList<>(db().updateSetItems(), (System.nanoTime() - start));
        }
        return setItems;
    }

    public CacheList<PcbItem> getPcbItems()    {
        if (pcbItems == null) {
            long start = System.nanoTime();
            pcbItems = new CacheList<>(db().updatePcbItems(), (System.nanoTime() - start));
        }
        return pcbItems;
    }

    public CacheList<PcbItemItemLink> getPcbItemItemLinks()    {
        if (pcbItemItemLinks == null) {
            long start = System.nanoTime();
            pcbItemItemLinks = new CacheList<>(db().updateKcItemLinks(), (System.nanoTime() - start));
        }
        return pcbItemItemLinks;
    }

    public CacheList<Log> getLogs()    {
        if (logs == null) {
            long start = System.nanoTime();
            logs = new CacheList<>(db().updateLogs(), (System.nanoTime() - start));
        }
        return logs;
    }

    public CacheList<DbHistory> getDbHistory() {
        if (dbHistoryList == null) {
            long start = System.nanoTime();
            dbHistoryList = new CacheList<>(db().updateDbHistoryList(), (System.nanoTime() - start));
        }
        return dbHistoryList;
    }

}
