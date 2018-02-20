package com.waldo.inventory.managers;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.classes.dbclasses.Package;

import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class SearchManager {

    private static final SearchManager INSTANCE = new SearchManager();

    public static SearchManager sm() {
        return INSTANCE;
    }

    /*
     *                  FINDERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public Item findItemById(long id) {
        for (Item i : cache().getItems()) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    public Item findItemByName(String name) {
        for (Item i : cache().getItems()) {
            if (i.getName().toUpperCase().equals(name.toUpperCase())) {
                return i;
            }
        }
        return null;
    }

    public Category findCategoryById(long id) {
        for (Category c : cache().getCategories()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public Product findProductById(long id) {
        for (Product p : cache().getProducts()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public Type findTypeById(long id) {
        for (Type t : cache().getTypes()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public List<Product> findProductListForCategory(long categoryId)    {
        List<Product> products = new ArrayList<>();
        for (Product p : cache().getProducts()) {
            if (p.getCategoryId() == categoryId) {
                products.add(p);
            }
        }
        return products;
    }

    public List<Type> findTypeListForProduct(long productId)    {
        List<Type> types = new ArrayList<>();
        for (Type t : cache().getTypes()) {
            if (t.getProductId() == productId) {
                types.add(t);
            }
        }
        return types;
    }

    public List<Item> findItemListForCategory(Category c)    {
        List<Item> items = new ArrayList<>();
        for (Item i : cache().getItems()) {
            if (i.getCategoryId() == c.getId()) {
                items.add(i);
            }
        }
        return items;
    }

    public List<Item> findItemListForProduct(Product p)    {
        List<Item> items = new ArrayList<>();
        Category c = sm().findCategoryById(p.getCategoryId());
        for (Item i : findItemListForCategory(c)) {
            if (i.getProductId() == p.getId()) {
                items.add(i);
            }
        }
        return items;
    }

    public List<Item> findItemListForType(Type t)    {
        List<Item> items = new ArrayList<>();
        Product p = sm().findProductById(t.getProductId());
        for (Item i : findItemListForProduct(p)) {
            if (i.getTypeId() == t.getId()) {
                items.add(i);
            }
        }
        return items;
    }

    public Manufacturer findManufacturerById(long id) {
        for (Manufacturer m : cache().getManufacturers()) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public List<Item> getItemsForManufacturer(long manufacturerId)    {
        List<Item> items = new ArrayList<>();
        for (Item item : cache().getItems()) {
            if (item.getManufacturerId() == manufacturerId) {
                items.add(item);
            }
        }
        return items;
    }

    public Location findLocationById(long id) {
        if (id >= DbObject.UNKNOWN_ID) {
            for (Location t : cache().getLocations()) {
                if (t.getId() == id) {
                    return t;
                }
            }
        }
        return null;
    }

    synchronized public Location findLocation(long locationTypeId, int row, int column) {
        for (Location l : cache().getLocations()) {
            if (l.getLocationTypeId() == locationTypeId && l.getRow() == row && l.getCol() == column) {
                return l;
            }
        }
        return null;
    }

    public Order findOrderById(long id) {
        for (Order t : cache().getOrders()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public OrderItem findOrderItemById(long id) {
        for (OrderItem t : cache().getOrderItems()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Distributor findDistributorById(long distributorId) {
        for (Distributor d : cache().getDistributors()) {
            if (d.getId() == distributorId) {
                return d;
            }
        }
        return null;
    }

    public DistributorPartLink findDistributorPartLink(long distributorId, long itemId) {
        for (DistributorPartLink pn : cache().getDistributorPartLinks()) {
            if (pn.getDistributorId() == distributorId && pn.getItemId() == itemId) {
                return pn;
            }
        }
        return null;
    }

    public List<DistributorPartLink> findDistributorPartLinksForItem(long itemId) {
        List<DistributorPartLink> linkList = new ArrayList<>();
        if (itemId > DbObject.UNKNOWN_ID) {
            for (DistributorPartLink link : cache().getDistributorPartLinks()) {
                if (link.getItemId() == itemId) {
                    linkList.add(link);
                }
            }
        }
        return linkList;
    }

    public List<Order> findOrdersForItem(long itemId) {
        List<Order> orders = new ArrayList<>();
        for (OrderItem oi : cache().getOrderItems()) {
            if (oi.getItemId() == itemId) {
                orders.add(oi.getOrder());
            }
        }
        return orders;
    }

    public List<Order> findPlannedOrders() {
        List<Order> orders = new ArrayList<>();
        for (Order o : cache().getOrders()) {
            if (!o.isUnknown() && !o.isOrdered()) {
                orders.add(o);
            }
        }
        return orders;
    }

    public List<ProjectPcb> findPcbsForItem(long itemId) {
        List<ProjectPcb> projects = new ArrayList<>();
        if (itemId > DbObject.UNKNOWN_ID) {
            for (ProjectPcb pcb : cache().getProjectPcbs()) {
                for (PcbItemProjectLink projectLink : pcb.getPcbItemMap()) {
                    if (projectLink.getPcbItemItemLinkId() > DbObject.UNKNOWN_ID && projectLink.getPcbItemItemLink().getItemId() == itemId) {
                        projects.add(pcb);
                        break;
                    }
                }
            }
        }
        return projects;
    }

    /**
     * Last order where the item in appeared
     * @param itemId: Id of the item
     * @return last order
     */
    public Order findLastOrderForItem(long itemId) {
        List<Order> orders = findOrdersForItem(itemId);
        if (orders.size() == 0) return null;
        else if (orders.size() == 1) return orders.get(0);
        else {
            orders.sort(new Order.SortAllOrders());
            return orders.get(orders.size()-1); // Return the last one
        }
    }

    public PackageType findPackageTypeById(long id) {
        for (PackageType pt : cache().getPackageTypes()) {
            if (pt.getId() == id) {
                return pt;
            }
        }
        return null;
    }

    public List<PackageType> findPackageTypesByPackageId(long packageId) {
        List<PackageType> types = new ArrayList<>();
        if (packageId > 0) {
            for (PackageType type : cache().getPackageTypes()) {
                if (type.getPackageId() == packageId) {
                    types.add(type);
                }
            }
        }
        return types;
    }

    public Project findProjectById(long id) {
        for (Project p : cache().getProjects()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public ProjectIDE findProjectIDEById(long id) {
        for (ProjectIDE p : cache().getProjectIDES()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<ProjectIDE> findProjectIDEsByType(Statics.ProjectTypes type) {
        List<ProjectIDE> projectIDES = new ArrayList<>();
        for (ProjectIDE pi : cache().getProjectIDES()) {
            if (pi.getProjectType().equals(type)) {
                projectIDES.add(pi);
            }
        }
        return projectIDES;
    }

    public List<Project> findProjectsWithIde(long ideId) {
        List<Project> projects = new ArrayList<>();
        if (ideId > DbObject.UNKNOWN_ID) {
            for (Project project : cache().getProjects()) {
                for (ProjectObject object : project.getAllProjectObjects()) {
                    if (object.getProjectIDEId() == ideId) {
                        projects.add(project);
                        break;
                    }
                }
            }
        }
        return projects;
    }

    public OrderFileFormat findOrderFileFormatById(long id) {
        for (OrderFileFormat of : cache().getOrderFileFormats()) {
            if (of.getId() == id) {
                return of;
            }
        }
        return null;
    }

    public OrderFileFormat findOrderFileFormatByName(String name) {
        for (OrderFileFormat of : cache().getOrderFileFormats()) {
            if (of.getName().equals(name)) {
                return of;
            }
        }
        return null;
    }

    public Package findPackageById(long id) {
        for (Package pa : cache().getPackages()) {
            if (pa.getId() == id) {
                return  pa;
            }
        }
        return null;
    }

    public PcbItem findPcbItemById(long id) {
        for (PcbItem component : cache().getPcbItems()) {
            if (component.getId() == id) {
                return component;
            }
        }
        return null;
    }

    public PcbItem findPcbItem(String footprint, String part) {
        for (PcbItem component : cache().getPcbItems()) {
            if (component.getFootprint().equals(footprint) &&
                    component.getPartName().equals(part)) {

                return component;
            }
        }
        return null;
    }

    public PcbItemItemLink findPcbItemItemLinkById(long id) {
        if (id > DbObject.UNKNOWN_ID) {
            for (PcbItemItemLink link : cache().getPcbItemItemLinks()) {
                if (link.getId() == id) {
                    return link;
                }
            }
        }
        return null;
    }

    public LocationType findLocationTypeById(long locationTypeId) {
        for (LocationType lt : cache().getLocationTypes()) {
            if (lt.getId() == locationTypeId) {
                return lt;
            }
        }
        return null;
    }

    public List<Location> findLocationsByTypeId(long locationTypeId) {
        List<Location> locations = new ArrayList<>();
        for (Location location : cache().getLocations()) {
            if (location.getLocationTypeId() == locationTypeId) {
                locations.add(location);
            }
        }
        return locations;
    }

    public List<ProjectCode> findProjectCodesByProjectId(long projectId) {
        List<ProjectCode> projectCodes = new ArrayList<>();
        for (ProjectCode pc : cache().getProjectCodes()) {
            if (pc.getProjectId() == projectId) {
                projectCodes.add(pc);
            }
        }
        return projectCodes;
    }

    public List<ProjectPcb> findProjectPcbsByProjectId(long projectId) {
        List<ProjectPcb> projectPcbs = new ArrayList<>();
        if (projectId > 0) {
            for (ProjectPcb pp : cache().getProjectPcbs()) {
                if (pp.getProjectId() == projectId) {
                    projectPcbs.add(pp);
                }
            }
        }
        return projectPcbs;
    }

    public ProjectPcb findProjectPcbById(long id) {
        if (id > 0) {
            for (ProjectPcb pp : cache().getProjectPcbs()) {
                if (pp.getId() == id) {
                    return pp;
                }
            }
        }
        return null;
    }

    public List<ProjectOther> findProjectOthersByProjectId(long projectId) {
        List<ProjectOther> projectOthers = new ArrayList<>();
        for (ProjectOther po : cache().getProjectOthers()) {
            if (po.getProjectId() == projectId) {
                projectOthers.add(po);
            }
        }
        return projectOthers;
    }

    public List<PcbItemProjectLink> findPcbItemProjectLinksWithPcbItem(long pcbItemId) {
        List<PcbItemProjectLink> linkList = new ArrayList<>();
        if (pcbItemId > DbObject.UNKNOWN_ID) {
            for (PcbItemProjectLink projectLink : cache().getPcbItemProjectLinks()) {
                if (projectLink.getPcbItemId() == pcbItemId) {
                    linkList.add(projectLink);
                }
            }
        }
        return linkList;
    }

    public List<PcbItemProjectLink> findPcbItemLinksWithProjectPcb(long projectPcbId) {
        List<PcbItemProjectLink> links = new ArrayList<>();
        if (projectPcbId > 0) {
            for (PcbItemProjectLink link : cache().getPcbItemProjectLinks()) {
                if (link.getProjectPcbId() == projectPcbId) {
                    links.add(link);
                }
            }
        }
        return links;
    }

    public List<PcbItemItemLink> findPcbItemItemLinksForPcbItem(long pcbItemId) {
        List<PcbItemItemLink> links = new ArrayList<>();
        if (pcbItemId > DbObject.UNKNOWN_ID) {
            for (PcbItemItemLink itemItemLink : cache().getPcbItemItemLinks()) {
                if (itemItemLink.getPcbItemId() == pcbItemId) {
                    links.add(itemItemLink);
                }
            }
        }
        return links;
    }

    public List<ParserItemLink> findParserItemLinksByParserName(String parserName) {
        List<ParserItemLink> parserItemLinks = new ArrayList<>();
        if (parserName != null && !parserName.isEmpty()) {
            for (ParserItemLink link : cache().getParserItemLinks()) {
                if (link.getParserName().equals(parserName)) {
                    parserItemLinks.add(link);
                }
            }
        }
        return parserItemLinks;
    }

    public ParserItemLink findParserItemLinkByPcbItemName(String name) {
        if (name != null && !name.isEmpty()) {
            for (ParserItemLink link : cache().getParserItemLinks()) {
                if (link.getPcbItemName().equals(name)) {
                    return link;
                }
            }
        }
        return null;
    }

    public List<Log> getLogsByType(boolean info, boolean debug, boolean warn, boolean error) {
        List<Log> logList = new ArrayList<>();
        for (Log log : cache().getLogs()) {
            switch (log.getLogType()) {
                case Info:
                    if (info) logList.add(log);
                    break;
                case Debug:
                    if (debug) logList.add(log);
                    break;
                case Warn:
                    if (warn) logList.add(log);
                    break;
                case Error:
                    if (error) logList.add(log);
                    break;
            }
        }
        return logList;
    }

    public Set findSetById(long setId) {
        if (setId > 0) {
            for (Set set : cache().getSets()) {
                if (set.getId() == setId) {
                    return set;
                }
            }
        }
        return null;
    }

    public List<Set> findSetsByItemId(long itemId) {
        List<Set> setList = new ArrayList<>();
        if (itemId > DbObject.UNKNOWN_ID) {
            for (Set set : cache().getSets()) {
                for (Item item : findSetItemsBySetId(set.getId())) {
                    if (item.getId() == itemId) {
                        setList.add(set);
                        break;
                    }
                }
            }
        }
        return setList;
    }

    public List<Item> findSetItemsBySetId(long setId) {
        List<Item> setItems = new ArrayList<>();
        if (setId > DbObject.UNKNOWN_ID) {
            for (SetItemLink sil : cache().getSetItemLinks()) {
                if (sil.getSetId() == setId) {
                    setItems.add(sil.getItem());
                }
            }
        }
        return setItems;
    }

    public SetItemLink findSetItemLinkBySetAndItem(long setId, long itemId) {
        if (setId > DbObject.UNKNOWN_ID && itemId > DbObject.UNKNOWN_ID) {
            for (SetItemLink link : cache().getSetItemLinks()) {
                if (link.getSetId() == setId && link.getItemId() == itemId) {
                    return link;
                }
            }
        }
        return null;
    }

    public List<SetItemLink> findSetItemLinksByItemId(long itemId) {
        List<SetItemLink> setItemLinks = new ArrayList<>();
        if (itemId > DbObject.UNKNOWN_ID) {
            for (SetItemLink link : cache().getSetItemLinks()) {
                if (link.getItemId() == itemId) {
                    setItemLinks.add(link);
                }
            }
        }
        return setItemLinks;
    }

    public List<PendingOrder> findPendingOrdersByDistributorId(long distributorId) {
        List<PendingOrder> pendingOrders = new ArrayList<>();
        if (distributorId > DbObject.UNKNOWN_ID) {
            for (PendingOrder po : cache().getPendingOrders()) {
                if (po.getDistributorId() == distributorId) {
                    pendingOrders.add(po);
                }
            }
        }
        return pendingOrders;
    }
}
