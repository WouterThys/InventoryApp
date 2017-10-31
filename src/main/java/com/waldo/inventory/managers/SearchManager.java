package com.waldo.inventory.managers;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.classes.Package;

import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class SearchManager {

    private static final SearchManager INSTANCE = new SearchManager();

    public static SearchManager sm() {
        return INSTANCE;
    }

    private boolean searched = false;
    private boolean inAdvanced = false;
    private boolean hasAdvancedSearchOption;
    private int[] searchOptions;
    private List<DbObject> searchList;
    private List<DbObject> resultList = new ArrayList<>();
    private int currentResultNdx = 0;

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

        currentResultNdx = 0;
        resultList = foundObjects;
        return foundObjects;
    }

    public void clearSearch() {
        resultList.clear();
        currentResultNdx = 0;
    }

    public DbObject getNextFoundObject() {
        currentResultNdx++;
        if (currentResultNdx >= resultList.size()) {
            currentResultNdx = 0;
        }
        return resultList.get(currentResultNdx);
    }

    public DbObject getPreviousFoundObject() {
        currentResultNdx--;
        if (currentResultNdx < 0) {
            currentResultNdx = resultList.size()-1;
        }
        return resultList.get(currentResultNdx);
    }


    private List<DbObject> searchAllKnownObjects(String searchWord) {
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

        // Projects
        Status().setMessage("Searching for: Projects");
        foundList.addAll(searchForObject(new ArrayList<>(db().getProjects()), searchWord));

        // Project directories
        //Status().setMessage("Searching for: Project directories");
        //foundList.addAll(searchForObject(new ArrayList<>(db().getProjectDirectories()), searchWord));

        // Project types
        Status().setMessage("Searching for: Project types");
        foundList.addAll(searchForObject(new ArrayList<>(db().getProjectIDES()), searchWord));

        // Package types
        Status().setMessage("Searching for: Package types");
        foundList.addAll(searchForObject(new ArrayList<>(db().getPackageTypes()), searchWord));

        // Logs
        Status().setMessage("Searching for: Logs");
        foundList.addAll(searchForObject(new ArrayList<>(db().getLogs()), searchWord));

        return foundList;
    }

    private List<DbObject> searchSpecific(int[] searchOptions, String searchWord) {
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
                case DbObject.TYPE_LOCATION_TYPE:
                    Status().setMessage("Searching for: Location types");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getLocationTypes()), searchWord));
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
                case DbObject.TYPE_PROJECT:
                    Status().setMessage("Searching for: Projects");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getProjects()), searchWord));
                    break;
                case DbObject.TYPE_PROJECT_TYPE:
                    Status().setMessage("Searching for: Project types");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getProjectIDES()), searchWord));
                    break;
                case DbObject.TYPE_PACKAGE:
                    Status().setMessage("Searching for: Package types");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getPackageTypes()), searchWord));
                    break;
                case DbObject.TYPE_LOG:
                    Status().setMessage("Searching for: Logs");
                    foundList.addAll(searchForObject(new ArrayList<>(db().getLogs()), searchWord));
                default:
                    break;
            }
        }
        return foundList;
    }

    private List<DbObject> searchSpecific(List<DbObject> searchList, int[] searchOptions, String searchWord) {
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
                case DbObject.TYPE_LOCATION_TYPE:
                    Status().setMessage("Searching for: Location types");
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
                case DbObject.TYPE_PROJECT:
                    Status().setMessage("Searching for: Projects");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                case DbObject.TYPE_PROJECT_TYPE:
                    Status().setMessage("Searching for: Project types");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                case DbObject.TYPE_LOG:
                    Status().setMessage("Searching for: Logs");
                    foundList.addAll(searchForObject(searchList, searchWord));
                    break;
                default:
                    break;
            }
        }
        return foundList;
    }

    private List<DbObject> searchForObject(List<DbObject> listToSearch, String searchWord) {
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

    public void setSearchOptions(int[] searchOptions) {
        this.searchOptions = searchOptions;
    }

    public void setSearchList(List<DbObject> searchList) {
        this.searchList = searchList;
    }

    public List<DbObject> getResultList() {
        return resultList;
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

    public Product findProductById(long id) {
        for (Product p : db().getProducts()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public Type findTypeById(long id) {
        for (Type t : db().getTypes()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public List<Product> findProductListForCategory(long categoryId)    {
        List<Product> products = new ArrayList<>();
        for (Product p : db().getProducts()) {
            if (p.getCategoryId() == categoryId) {
                products.add(p);
            }
        }
        return products;
    }

    public List<Type> findTypeListForProduct(long productId)    {
        List<com.waldo.inventory.classes.Type> types = new ArrayList<>();
        for (Type t : db().getTypes()) {
            if (t.getProductId() == productId) {
                types.add(t);
            }
        }
        return types;
    }

    public List<Item> findItemListForCategory(Category c)    {
        List<Item> items = new ArrayList<>();
        for (Item i : db().getItems()) {
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
        for (Manufacturer m : db().getManufacturers()) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public Location findLocationById(long id) {
        if (id >= DbObject.UNKNOWN_ID) {
            for (Location t : db().getLocations()) {
                if (t.getId() == id) {
                    return t;
                }
            }
        }
        return null;
    }

    synchronized public Location findLocation(long locationTypeId, int row, int column) {
        for (Location l : db().getLocations()) {
            if (l.getLocationTypeId() == locationTypeId && l.getRow() == row && l.getCol() == column) {
                return l;
            }
        }
        return null;
    }

    public Order findOrderById(long id) {
        for (Order t : db().getOrders()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public OrderItem findOrderItemById(long id) {
        for (OrderItem t : db().getOrderItems()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Distributor findDistributorById(long distributorId) {
        for (Distributor d : db().getDistributors()) {
            if (d.getId() == distributorId) {
                return d;
            }
        }
        return null;
    }

    public DistributorPartLink findDistributorPartLink(long distributorId, long itemId) {
        for (DistributorPartLink pn : db().getDistributorPartLinks()) {
            if (pn.getDistributorId() == distributorId && pn.getItemId() == itemId) {
                return pn;
            }
        }
        return null;
    }

    public DistributorPartLink findDistributorPartLinkById(long id) {
        for (DistributorPartLink pn : db().getDistributorPartLinks()) {
            if (pn.getId() == id) {
                return  pn;
            }
        }
        return null;
    }

    public List<DistributorPartLink> findDistributorPartLinksForItem(long itemId) {
        List<DistributorPartLink> linkList = new ArrayList<>();
        if (itemId > DbObject.UNKNOWN_ID) {
            for (DistributorPartLink link : db().getDistributorPartLinks()) {
                if (link.getItemId() == itemId) {
                    linkList.add(link);
                }
            }
        }
        return linkList;
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

    public List<Order> findPlannedOrders() {
        List<Order> orders = new ArrayList<>();
        for (Order o : db().getOrders()) {
            if (!o.isUnknown() && !o.isOrdered()) {
                orders.add(o);
            }
        }
        return orders;
    }

    public List<ProjectPcb> findPcbsForItem(long itemId) {
        List<ProjectPcb> projects = new ArrayList<>();
        if (itemId > DbObject.UNKNOWN_ID) {
            for (ProjectPcb pcb : db().getProjectPcbs()) {
                for (Item item : pcb.getLinkedItems()) {
                    if (item.getId() == itemId) {
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
        for (PackageType pt : db().getPackageTypes()) {
            if (pt.getId() == id) {
                return pt;
            }
        }
        return null;
    }

    public List<PackageType> findPackageTypesByPackageId(long packageId) {
        List<PackageType> types = new ArrayList<>();
        if (packageId > 0) {
            for (PackageType type : db().getPackageTypes()) {
                if (type.getPackageId() == packageId) {
                    types.add(type);
                }
            }
        }
        return types;
    }

    public Project findProjectById(long id) {
        for (Project p : db().getProjects()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public ProjectIDE findProjectIDEById(long id) {
        for (ProjectIDE p : db().getProjectIDES()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<ProjectIDE> findProjectIDEsByType(String type) {
        List<ProjectIDE> projectIDES = new ArrayList<>();
        for (ProjectIDE pi : db().getProjectIDES()) {
            if (pi.getProjectType().isEmpty() || pi.getProjectType().equals(type)) {
                projectIDES.add(pi);
            }
        }
        return projectIDES;
    }

    public OrderFileFormat findOrderFileFormatById(long id) {
        for (OrderFileFormat of : db().getOrderFileFormats()) {
            if (of.getId() == id) {
                return of;
            }
        }
        return null;
    }

    public OrderFileFormat findOrderFileFormatByName(String name) {
        for (OrderFileFormat of : db().getOrderFileFormats()) {
            if (of.getName().equals(name)) {
                return of;
            }
        }
        return null;
    }

    public Package findPackageById(long id) {
        for (Package pa : db().getPackages()) {
            if (pa.getId() == id) {
                return  pa;
            }
        }
        return null;
    }

    public SetItem findSetItemById(long id) {
        for (SetItem si : db().getSetItems()) {
            if (si.getId() == id) {
                return si;
            }
        }
        return null;
    }

    public List<SetItem> findSetItemsByItemId(long id) {
        List<SetItem> setItems = new ArrayList<>();
        for (SetItem si : db().getSetItems()) {
            if (si.getItemId() == id) {
                setItems.add(si);
            }
        }
        return setItems;
    }

    public PcbItem findPcbItemById(long id) {
        for (PcbItem component : db().getPcbItems()) {
            if (component.getId() == id) {
                return component;
            }
        }
        return null;
    }

    public PcbItem findPcbItem(String value, String footprint, String lib, String part) {
        for (PcbItem component : db().getPcbItems()) {
            if (component.getValue().equals(value) &&
                    component.getFootprint().equals(footprint) &&
                    component.getLibrary().equals(lib) &&
                    component.getPartName().equals(part)) {

                return component;
            }
        }
        return null;
    }

    public PcbItemItemLink findPcbItemLinkWithItem(long itemId, long pcbItemId) {
        for (PcbItemItemLink pcbItemItemLink : db().getPcbItemItemLinks()) {
            if (!pcbItemItemLink.isSetItem()) {
                if(pcbItemItemLink.getItemId() == itemId && pcbItemItemLink.getPcbItemId() == pcbItemId) {
                    return pcbItemItemLink;
                }
            }
        }
        return null;
    }

    public PcbItemItemLink findPcbItemLinkWithSetItem(long setItemId, long pcbItemId) {
        for (PcbItemItemLink pcbItemItemLink : db().getPcbItemItemLinks()) {
            if (pcbItemItemLink.isSetItem()) {
                if(pcbItemItemLink.getSetItemId() == setItemId && pcbItemItemLink.getPcbItemId() == pcbItemId) {
                    return pcbItemItemLink;
                }
            }
        }
        return null;
    }

    public PcbItemItemLink findPcbItemLinkForPcbItem(long pcbItemId) {
        if (pcbItemId > 0) {
            for (PcbItemItemLink pcbItemItemLink : db().getPcbItemItemLinks()) {
                if (pcbItemItemLink.getPcbItemId() == pcbItemId) {
                    return pcbItemItemLink;
                }
            }
        }
        return null;
    }

    public LocationType findLocationTypeById(long locationTypeId) {
        for (LocationType lt : db().getLocationTypes()) {
            if (lt.getId() == locationTypeId) {
                return lt;
            }
        }
        return null;
    }

    public List<Location> findLocationsByTypeId(long locationTypeId) {
        List<Location> locations = new ArrayList<>();
        for (Location location : db().getLocations()) {
            if (location.getLocationTypeId() == locationTypeId) {
                locations.add(location);
            }
        }
        return locations;
    }

    public List<ProjectCode> findProjectCodesByProjectId(long projectId) {
        List<ProjectCode> projectCodes = new ArrayList<>();
        for (ProjectCode pc : db().getProjectCodes()) {
            if (pc.getProjectId() == projectId) {
                projectCodes.add(pc);
            }
        }
        return projectCodes;
    }

    public List<ProjectPcb> findProjectPcbsByProjectId(long projectId) {
        List<ProjectPcb> projectPcbs = new ArrayList<>();
        if (projectId > 0) {
            for (ProjectPcb pp : db().getProjectPcbs()) {
                if (pp.getProjectId() == projectId) {
                    projectPcbs.add(pp);
                }
            }
        }
        return projectPcbs;
    }

    public ProjectPcb findProjectPcbById(long id) {
        if (id > 0) {
            for (ProjectPcb pp : db().getProjectPcbs()) {
                if (pp.getId() == id) {
                    return pp;
                }
            }
        }
        return null;
    }

    public List<ProjectOther> findProjectOthersByProjectId(long projectId) {
        List<ProjectOther> projectOthers = new ArrayList<>();
//        for (ProjectOther po : db().getProjectOthers()) {
//            if (po.getProjectId() == projectId) {
//                projectOthers.add(pc);
//            }
//        }
        return projectOthers;
    }

    public List<PcbItem> findPcbItemsForProjectPcb(long projectPcbId) {
        List<PcbItem> pcbItems = new ArrayList<>();
        if (projectPcbId > 0) {
            for (PcbItemProjectLink link : db().getPcbItemProjectLinks()) {
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
            for (PcbItemProjectLink link : db().getPcbItemProjectLinks()) {
                if (link.getProjectPcbId() == projectPcbId) {
                    links.add(link);
                }
            }
        }
        return links;
    }

    public PcbItemProjectLink findPcbItemLink(long pcbItemId, long projectPcbId, String sheet) {
        if (pcbItemId > 0 && projectPcbId > 0) {
            for (PcbItemProjectLink pil : db().getPcbItemProjectLinks()) {
                if (pil.getPcbItemId() == pcbItemId && pil.getProjectPcbId() == projectPcbId && pil.getSheetName().equals(sheet)) {
                    return pil;
                }
            }
        }
        return null;
    }

    public List<ParserItemLink> findParserItemLinksByParserName(String parserName) {
        List<ParserItemLink> parserItemLinks = new ArrayList<>();
        if (parserName != null && !parserName.isEmpty()) {
            for (ParserItemLink link : db().getParserItemLinks()) {
                if (link.getParserName().equals(parserName)) {
                    parserItemLinks.add(link);
                }
            }
        }
        return parserItemLinks;
    }

    public ParserItemLink findParserItemLinkByPcbItemName(String name) {
        if (name != null && !name.isEmpty()) {
            for (ParserItemLink link : db().getParserItemLinks()) {
                if (link.getPcbItemName().equals(name)) {
                    return link;
                }
            }
        }
        return null;
    }

}
