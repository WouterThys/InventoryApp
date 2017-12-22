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

    public DistributorPartLink findDistributorPartLinkById(long id) {
        for (DistributorPartLink pn : cache().getDistributorPartLinks()) {
            if (pn.getId() == id) {
                return  pn;
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

    public List<ProjectIDE> findProjectIDEsByType(String type) {
        List<ProjectIDE> projectIDES = new ArrayList<>();
        for (ProjectIDE pi : cache().getProjectIDES()) {
            if (pi.getProjectType().isEmpty() || pi.getProjectType().equals(type)) {
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

    public SetItem findSetItemById(long id) {
        for (SetItem si : cache().getSetItems()) {
            if (si.getId() == id) {
                return si;
            }
        }
        return null;
    }

    public List<SetItem> findSetItemsByItemId(long id) {
        List<SetItem> setItems = new ArrayList<>();
        for (SetItem si : cache().getSetItems()) {
            if (si.getItemId() == id) {
                setItems.add(si);
            }
        }
        return setItems;
    }

    public PcbItem findPcbItemById(long id) {
        for (PcbItem component : cache().getPcbItems()) {
            if (component.getId() == id) {
                return component;
            }
        }
        return null;
    }

    public PcbItem findPcbItem(String footprint, String lib, String part) {
        for (PcbItem component : cache().getPcbItems()) {
            if (component.getFootprint().equals(footprint) &&
                    component.getLibrary().equals(lib) &&
                    component.getPartName().equals(part)) {

                return component;
            }
        }
        return null;
    }

    public PcbItemItemLink findPcbItemLinkWithItem(long itemId, long pcbItemId) {
        for (PcbItemItemLink pcbItemItemLink : cache().getPcbItemItemLinks()) {
            if (!pcbItemItemLink.isSetItem()) {
                if(pcbItemItemLink.getItemId() == itemId && pcbItemItemLink.getPcbItemId() == pcbItemId) {
                    return pcbItemItemLink;
                }
            }
        }
        return null;
    }

    public PcbItemItemLink findPcbItemLinkWithSetItem(long setItemId, long pcbItemId) {
        for (PcbItemItemLink pcbItemItemLink : cache().getPcbItemItemLinks()) {
            if (pcbItemItemLink.isSetItem()) {
                if(pcbItemItemLink.getSetItemId() == setItemId && pcbItemItemLink.getPcbItemId() == pcbItemId) {
                    return pcbItemItemLink;
                }
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
//        for (ProjectOther po : CacheManager.cache().getProjectOthers()) {
//            if (po.getProjectId() == projectId) {
//                projectOthers.add(pc);
//            }
//        }
        return projectOthers;
    }

    public List<PcbItem> findPcbItemsForProjectPcb(long projectPcbId) {
        List<PcbItem> pcbItems = new ArrayList<>();
        if (projectPcbId > 0) {
            for (PcbItemProjectLink link : cache().getPcbItemProjectLinks()) {
                if (link.getProjectPcbId() == projectPcbId) {
                    pcbItems.add(link.getPcbItem());
                }
            }
        }
        return pcbItems;
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

    public PcbItemProjectLink findPcbItemProjectLink(long projectPcbId, long pcbItemId) {
        if (projectPcbId > 0 && pcbItemId > 0) {
            for (PcbItemProjectLink link : cache().getPcbItemProjectLinks()) {
                if (link.getProjectPcbId() == projectPcbId && link.getPcbItemId() == pcbItemId) {
                    return link;
                }
            }
        }
        return null;
    }

    public PcbItemProjectLink findPcbItemLink(PcbItem pcbItem, long projectPcbId, String sheet) {
        if (pcbItem != null && projectPcbId > 0) {
            for (PcbItemProjectLink pil : cache().getPcbItemProjectLinks()) {
                if (pil.getPcbItemId() == pcbItem.getId() &&
                        pil.getProjectPcbId() == projectPcbId &&
                        pil.getValue().equals(pcbItem.getValue()) &&
                        pil.getPcbSheetName().equals(sheet)) {
                    return pil;
                }
            }
        }
        return null;
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

}
