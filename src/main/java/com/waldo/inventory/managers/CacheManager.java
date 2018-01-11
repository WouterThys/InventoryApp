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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        cacheLogList.add(new CacheLog("Items", items));
        cacheLogList.add(new CacheLog("Categories", categories));
        cacheLogList.add(new CacheLog("Products", products));
        cacheLogList.add(new CacheLog("Types", types));
        cacheLogList.add(new CacheLog("Manufacturers", manufacturers));
        cacheLogList.add(new CacheLog("Locations", locations));
        cacheLogList.add(new CacheLog("Location types", locationTypes));
        cacheLogList.add(new CacheLog("Orders", orders));
        cacheLogList.add(new CacheLog("Order items", orderItems));
        cacheLogList.add(new CacheLog("Distributors", distributors));
        cacheLogList.add(new CacheLog("Distributor part links", distributorPartLinks));
        cacheLogList.add(new CacheLog("Packages", packages));
        cacheLogList.add(new CacheLog("Package types", packageTypes));
        cacheLogList.add(new CacheLog("Projects", projects));
        cacheLogList.add(new CacheLog("Project IDEs", projectIDES));
        cacheLogList.add(new CacheLog("Order file formats", orderFileFormats));
        cacheLogList.add(new CacheLog("PCB items", pcbItems));
        cacheLogList.add(new CacheLog("PCB item links", pcbItemItemLinks));
        cacheLogList.add(new CacheLog("PCB item project links", pcbItemProjectLinks));
        cacheLogList.add(new CacheLog("Database history", dbHistoryList));
        cacheLogList.add(new CacheLog("Project codes", projectCodes));
        cacheLogList.add(new CacheLog("Project PCBs", projectPcbs));
        cacheLogList.add(new CacheLog("Project others", projectOthers));
        cacheLogList.add(new CacheLog("Project item links", pcbItemProjectLinks));
        cacheLogList.add(new CacheLog("Sets", sets));
        cacheLogList.add(new CacheLog("Set item links", setItemLinks));
        initTime = DateUtils.now();
    }

    // Cached lists
    private final CacheList<Item> items = new CacheList<>();
    private final CacheList<Category> categories = new CacheList<>();
    private final CacheList<Product> products = new CacheList<>();
    private final CacheList<Type> types = new CacheList<>();
    private final CacheList<Manufacturer> manufacturers = new CacheList<>();
    private final CacheList<Location> locations = new CacheList<>();
    private final CacheList<LocationType> locationTypes = new CacheList<>();
    private final CacheList<Order> orders = new CacheList<>();
    private final CacheList<OrderItem> orderItems = new CacheList<>();
    private final CacheList<Distributor> distributors = new CacheList<>();
    private final CacheList<DistributorPartLink> distributorPartLinks = new CacheList<>();
    private final CacheList<PackageType> packageTypes = new CacheList<>();
    private final CacheList<Project> projects = new CacheList<>();
    private final CacheList<ProjectIDE> projectIDES = new CacheList<>();
    private final CacheList<OrderFileFormat> orderFileFormats = new CacheList<>();
    private final CacheList<Package> packages = new CacheList<>();
    private final CacheList<PcbItem> pcbItems = new CacheList<>();
    private final CacheList<PcbItemItemLink> pcbItemItemLinks = new CacheList<>();
    private final CacheList<PcbItemProjectLink> pcbItemProjectLinks = new CacheList<>();
    private final CacheList<Log> logs = new CacheList<>();
    private final CacheList<DbHistory> dbHistoryList = new CacheList<>();
    private final CacheList<ProjectCode> projectCodes = new CacheList<>();
    private final CacheList<ProjectPcb> projectPcbs = new CacheList<>();
    private final CacheList<ProjectOther> projectOthers = new CacheList<>();
    private final CacheList<ParserItemLink> parserItemLinks = new CacheList<>();
    private final CacheList<Set> sets = new CacheList<>();
    private final CacheList<SetItemLink> setItemLinks = new CacheList<>();

    // Listeners
    private final Map<Class<? extends DbObject>, List<CacheChangedListener<? extends DbObject>>> changedListenerMap = new HashMap<>();


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public <T extends DbObject> void addListener(Class<T> c, CacheChangedListener<T> listener) {
        if (!changedListenerMap.containsKey(c)) {
            changedListenerMap.put(c, new ArrayList<>());
            changedListenerMap.get(c).add(listener);
        } else {
            if (!changedListenerMap.get(c).contains(listener)) {
                changedListenerMap.get(c).add(listener);
            }
        }
    }

    public void removeListener(CacheChangedListener listener) {
        for (List l : changedListenerMap.values()) {
            l.remove(listener);
        }
    }

    public <T extends DbObject> void notifyListeners(int changedHow, T object) {
        Class t = object.getClass();
        if (changedListenerMap.containsKey(t)) {
            for (CacheChangedListener l : changedListenerMap.get(t)) {
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
    }

    public List<CacheLog> getCacheLogList() {
        return cacheLogList;
    }

    public Date getInitTime() {
        return initTime;
    }

    public void clearCache() {
        items.clear();
        categories.clear();
        products.clear();
        types.clear();
        manufacturers.clear();
        locations.clear();
        locationTypes.clear();
        orders.clear();
        orderItems.clear();
        distributors.clear();
        distributorPartLinks.clear();
        packageTypes.clear();
        projects.clear();
        projectIDES.clear();
        orderFileFormats.clear();
        packages.clear();
        pcbItems.clear();
        pcbItemItemLinks.clear();
        pcbItemProjectLinks.clear();
        logs.clear();
        dbHistoryList.clear();
        projectCodes.clear();
        projectPcbs.clear();
        projectOthers.clear();
        parserItemLinks.clear();
        sets.clear();
        setItemLinks.clear();
    }


    public synchronized CacheList<Item> getItems() {
        if (!items.isFetched()) {
            long start = System.nanoTime();
            items.setList(db().updateItems(), (System.nanoTime() - start));
        }
        return items;
    }

    public synchronized CacheList<Category> getCategories() {
        if (!categories.isFetched()) {
            long start = System.nanoTime();
            categories.setList(db().updateCategories(), (System.nanoTime() - start));
        }
        return categories;
    }

    public synchronized CacheList<Product> getProducts() {
        if (!products.isFetched()) {
            long start = System.nanoTime();
            products.setList(db().updateProducts(), (System.nanoTime() - start));
        }
        return products;
    }

    public synchronized CacheList<Type> getTypes() {
        if (!types.isFetched()) {
            long start = System.nanoTime();
            types.setList(db().updateTypes(), (System.nanoTime() - start));
        }
        return types;
    }

    public synchronized CacheList<Manufacturer> getManufacturers() {
        if (!manufacturers.isFetched()) {
            long start = System.nanoTime();
            manufacturers.setList(db().updateManufacturers(), (System.nanoTime() - start));
        }
        return manufacturers;
    }

    public synchronized CacheList<Location> getLocations() {
        if (!locations.isFetched()) {
            long start = System.nanoTime();
            locations.setList(db().updateLocations(), (System.nanoTime() - start));
        }
        return locations;
    }

    public synchronized CacheList<LocationType> getLocationTypes() {
        if (!locationTypes.isFetched()) {
            long start = System.nanoTime();
            locationTypes.setList(db().updateLocationTypes(), (System.nanoTime() - start));
        }
        return locationTypes;
    }

    public synchronized CacheList<Order> getOrders() {
        if (!orders.isFetched()) {
            long start = System.nanoTime();
            orders.setList(db().updateOrders(), (System.nanoTime() - start));
        }
        return orders;
    }

    public synchronized CacheList<OrderItem> getOrderItems() {
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

    public synchronized CacheList<Distributor> getDistributors() {
        if (!distributors.isFetched()) {
            long start = System.nanoTime();
            distributors.setList(db().updateDistributors(), (System.nanoTime() - start));
        }
        return distributors;
    }

    public synchronized CacheList<DistributorPartLink> getDistributorPartLinks() {
        if (!distributorPartLinks.isFetched()) {
            long start = System.nanoTime();
            distributorPartLinks.setList(db().updateDistributorParts(), (System.nanoTime() - start));
        }
        return distributorPartLinks;
    }

    public synchronized CacheList<Package> getPackages() {
        if (!packages.isFetched()) {
            long start = System.nanoTime();
            packages.setList(db().updatePackages(), (System.nanoTime() - start));
        }
        return packages;
    }

    public synchronized CacheList<PackageType> getPackageTypes() {
        if (!packageTypes.isFetched()) {
            long start = System.nanoTime();
            packageTypes.setList(db().updatePackageTypes(), (System.nanoTime() - start));
        }
        return packageTypes;
    }

    public synchronized CacheList<Project> getProjects() {
        if (!projects.isFetched()) {
            long start = System.nanoTime();
            projects.setList(db().updateProjects(), (System.nanoTime() - start));
        }
        return projects;
    }

    public synchronized CacheList<ProjectCode> getProjectCodes() {
        if (!projectCodes.isFetched()) {
            long start = System.nanoTime();
            projectCodes.setList(db().updateProjectCodes(), (System.nanoTime() - start));
        }
        return projectCodes;
    }

    public synchronized CacheList<ProjectPcb> getProjectPcbs() {
        if (!projectPcbs.isFetched()) {
            long start = System.nanoTime();
            projectPcbs.setList(db().updateProjectPcbs(), (System.nanoTime() - start));
        }
        return projectPcbs;
    }

    public synchronized CacheList<PcbItemProjectLink> getPcbItemProjectLinks() {
        if (!pcbItemProjectLinks.isFetched()) {
            long start = System.nanoTime();
            pcbItemProjectLinks.setList(db().updatePcbItemLinks(), (System.nanoTime() - start));
        }
        return pcbItemProjectLinks;
    }

    public synchronized CacheList<ProjectIDE> getProjectIDES() {
        if (!projectIDES.isFetched()) {
            long start = System.nanoTime();
            projectIDES.setList(db().updateProjectIDEs(), (System.nanoTime() - start));
        }
        return projectIDES;
    }

    public synchronized CacheList<ParserItemLink> getParserItemLinks() {
        if (!parserItemLinks.isFetched()) {
            long start = System.nanoTime();
            parserItemLinks.setList(db().updateParserItemLinks(), (System.nanoTime() - start));
        }
        return parserItemLinks;
    }

    public synchronized CacheList<OrderFileFormat> getOrderFileFormats() {
        if (!orderFileFormats.isFetched()) {
            long start = System.nanoTime();
            orderFileFormats.setList(db().updateOrderFileFormats(), (System.nanoTime() - start));
        }
        return orderFileFormats;
    }

    public synchronized CacheList<PcbItem> getPcbItems() {
        if (!pcbItems.isFetched()) {
            long start = System.nanoTime();
            pcbItems.setList(db().updatePcbItems(), (System.nanoTime() - start));
        }
        return pcbItems;
    }

    public synchronized CacheList<PcbItemItemLink> getPcbItemItemLinks() {
        if (!pcbItemItemLinks.isFetched()) {
            long start = System.nanoTime();
            pcbItemItemLinks.setList(db().updateKcItemLinks(), (System.nanoTime() - start));
        }
        return pcbItemItemLinks;
    }

    public synchronized CacheList<Log> getLogs() {
        if (!logs.isFetched()) {
            long start = System.nanoTime();
            logs.setList(db().updateLogs(), (System.nanoTime() - start));
        }
        return logs;
    }

    public synchronized CacheList<DbHistory> getDbHistory() {
        if (!dbHistoryList.isFetched()) {
            long start = System.nanoTime();
            dbHistoryList.setList(db().updateDbHistoryList(), (System.nanoTime() - start));
        }
        return dbHistoryList;
    }

    public synchronized CacheList<Set> getSets() {
        if (!sets.isFetched()) {
            long start = System.nanoTime();
            sets.setList(db().updateSets(), (System.nanoTime() - start));
        }
        return sets;
    }

    public synchronized CacheList<SetItemLink> getSetItemLinks() {
        if (!setItemLinks.isFetched()) {
            long start = System.nanoTime();
            setItemLinks.setList(db().updateSetItemLinks(), (System.nanoTime() - start));
        }
        return setItemLinks;
    }
}
