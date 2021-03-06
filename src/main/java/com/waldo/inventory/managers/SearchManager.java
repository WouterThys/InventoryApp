package com.waldo.inventory.managers;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.Utils.Statics.OrderStates;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.classes.dbclasses.Package;
import com.waldo.utils.DateUtils;

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

    public List<Item> findItemsToOrder() {
        List<Item> itemsToOrder = new ArrayList<>();
        for (Item item : cache().getItems()) {
            if (item.getOrderState().equals(OrderStates.NoOrder) &&
                    !item.isDiscourageOrder() &&
                    item.getAmount() < item.getMinimum()) {
                itemsToOrder.add(item);
            }
        }
        return itemsToOrder;
    }

    public Division findDivisionById(long id) {
        if (id > DbObject.UNKNOWN_ID) {
            for (Division d : cache().getDivisions()) {
                if (d.getId() == id) {
                    return d;
                }
            }
        }
        return null;
    }

    public Division findDivisionByName(String name) {
        if (name != null && !name.isEmpty()) {
            for (Division d : cache().getDivisions()) {
                if (d.getName().equals(name)) {
                    return d;
                }
            }
        }
        return null;
    }

    public List<Division> findDivisionsWithoutParent() {
        List<Division> divisionsWithoutParent = new ArrayList<>();
        for (Division d : cache().getDivisions()) {
            if (!d.isUnknown() && d.getParentDivisionId() <= DbObject.UNKNOWN_ID) {
                divisionsWithoutParent.add(d);
            }
        }
        return divisionsWithoutParent;
    }

    public List<Division> findDivisionsWithParent(long parentDivisionId) {
        List<Division> divisionsWithoutParent = new ArrayList<>();
        for (Division d : cache().getDivisions()) {
            if (d.getParentDivisionId() == parentDivisionId) {
                divisionsWithoutParent.add(d);
            }
        }
        return divisionsWithoutParent;
    }

    public List<Item> findItemsForDivision(Division d) {
        List<Item> items = new ArrayList<>();
        if (d != null) {
            for (Item item : cache().getItems()) {
                if (item.getDivisionId() == d.getId()) {
                    items.add(item);
                }
            }
            for (Division subDivision : d.getSubDivisions()) {
                items.addAll(subDivision.getItemList());
            }
        }
        return items;
    }

    public List<Item> findItemsForManufacturer(Manufacturer m) {
        List<Item> items = new ArrayList<>();
        if (m != null && !m.isUnknown()) {
            for (Item item : cache().getItems()) {
                if (item.getManufacturerId() == m.getId()) {
                    items.add(item);
                }
            }
        }
        return items;
    }

    public List<Item> findItemsForLocation(Location l) {
        List<Item> items = new ArrayList<>();
        if (l != null && !l.isUnknown()) {
            for (Item item : cache().getItems()) {
                if (item.getLocationId() == l.getId()) {
                    items.add(item);
                }
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

    public Manufacturer findManufacturerByName(String name) {
        if (name != null && !name.isEmpty()) {
            for (Manufacturer m : cache().getManufacturers()) {
                if (m.getName().equals(name)) {
                    return m;
                }
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

    public ItemOrder findItemOrderById(long id) {
        for (ItemOrder t : cache().getItemOrders()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public PcbOrder findPcbOrderById(long id) {
        for (PcbOrder t : cache().getPcbOrders()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public ItemOrder findItemOrderByName(String name) {
        if (name != null && !name.isEmpty()) {
            for (ItemOrder o : cache().getItemOrders()) {
                if (o.getName().equals(name)) {
                    return o;
                }
            }
        }
        return null;
    }

    public PcbOrder findPcbOrderByName(String name) {
        if (name != null && !name.isEmpty()) {
            for (PcbOrder o : cache().getPcbOrders()) {
                if (o.getName().equals(name)) {
                    return o;
                }
            }
        }
        return null;
    }

    public List<ItemOrder> findAutoOrdersByState(OrderStates orderState) {
        List<ItemOrder> autoItemOrderList = new ArrayList<>();

        for (ItemOrder itemOrder : cache().getItemOrders()) {
            if (itemOrder.isAutoOrder()) {
                if (orderState == null || itemOrder.getOrderState().equals(orderState)) {
                    autoItemOrderList.add(itemOrder);
                }
            }
        }

        return autoItemOrderList;
    }

    public List<ItemOrderLine> findItemOrderLinesForOrder(long orderId) {
        List<ItemOrderLine> lines = new ArrayList<>();
        if (orderId > DbObject.UNKNOWN_ID) {
            for (ItemOrderLine i : cache().getItemOrderLines()) {
                if (i.getOrderId() == orderId) {
                    lines.add(i);
                }
            }
        }
        return lines;
    }

    public List<PcbOrderLine> findPcbOrderLinesForOrder(long orderId) {
        List<PcbOrderLine> lines = new ArrayList<>();
        if (orderId > DbObject.UNKNOWN_ID) {
            for (PcbOrderLine i : cache().getPcbOrderLines()) {
                if (i.getOrderId() == orderId) {
                    lines.add(i);
                }
            }
        }
        return lines;
    }

    public List<PcbOrderLine> findPcbOrderLinesForPcb(long pcbId) {
        List<PcbOrderLine> lines = new ArrayList<>();
        if (pcbId > DbObject.UNKNOWN_ID) {
            for (PcbOrderLine ol : cache().getPcbOrderLines()) {
                if (ol.getLineId() == pcbId) {
                    lines.add(ol);
                }
            }
        }
        return lines;
    }

    public List<ItemOrderLine> findItemOrderLinesForItem(long itemId) {
        List<ItemOrderLine> lines = new ArrayList<>();
        if (itemId > DbObject.UNKNOWN_ID) {
            for (ItemOrderLine ol : cache().getItemOrderLines()) {
                if (ol.getLineId() == itemId) {
                    lines.add(ol);
                }
            }
        }
        return lines;
    }

    public ItemOrderLine findItemOrderLineById(long id) {
        for (ItemOrderLine t : cache().getItemOrderLines()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public PcbOrderLine findPcbOrderLineById(long id) {
        for (PcbOrderLine t : cache().getPcbOrderLines()) {
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

    public Distributor findDistributorByName(String name) {
        if (name != null && !name.isEmpty()) {
            for (Distributor d : cache().getDistributors()) {
                if (d.getName().equals(name)) {
                    return d;
                }
            }
        }
        return null;
    }

    public List<Distributor> findDistributorsByType(DistributorType type) {
        List<Distributor> distributorList = new ArrayList<>();
        if (type != null) {
            for (Distributor distributor : cache().getDistributors()) {
                if (distributor.getDistributorType().equals(type)) {
                    distributorList.add(distributor);
                }
            }
        } else {
            distributorList.addAll(cache().getDistributors());
        }
        return distributorList;
    }

    public DistributorPartLink findDistributorPartLink(long distributorId, Item item) {
        if (distributorId > DbObject.UNKNOWN_ID && item != null) {
            for (DistributorPartLink pn : cache().getDistributorPartLinks()) {
                if ((pn.getDistributorId() == distributorId) && (pn.getItemId() == item.getId())) {
                    return pn;
                }
            }
        }
        return null;
    }

    public DistributorPartLink findDistributorPartLink(long distributorId, ProjectPcb pcb) {
        if (distributorId > DbObject.UNKNOWN_ID && pcb != null) {
            for (DistributorPartLink pn : cache().getDistributorPartLinks()) {
                if ((pn.getDistributorId() == distributorId) && (pn.getPcbId() == pcb.getId())) {
                    return pn;
                }
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

    public List<DistributorOrderFlow> findOrderFlowTemplateForDistributor(long distributorId) {
        List<DistributorOrderFlow> distributorOrderFlows = new ArrayList<>();
        if (distributorId > DbObject.UNKNOWN_ID) {
            for (DistributorOrderFlow dof : cache().getDistributorOrderFlows()) {
                if (dof.getDistributorId() == distributorId) {
                    distributorOrderFlows.add(dof);
                }
            }
        }
        return distributorOrderFlows;
    }

    public List<ItemOrder> findOrdersForItem(long itemId) {
        List<ItemOrder> itemOrders = new ArrayList<>();
        if (itemId > DbObject.UNKNOWN_ID) {
            for (ItemOrder o : cache().getItemOrders()) {
                    for (AbstractOrderLine oi : o.getOrderLines()) {
                        if (oi.getLineId() == itemId) {
                            itemOrders.add(o);
                            break;
                        }
                    }

            }
        }
        return itemOrders;
    }

    public List<AbstractOrder> findOrdersForStateAndYear(OrderStates states, int year) {
        List<AbstractOrder> orderList = new ArrayList<>();

        for (ItemOrder order : cache().getItemOrders()) {
            if (!order.isUnknown()) {
                OrderStates state = order.getOrderState();
                if (state.equals(states)) {
                    if (year < 1) {
                        orderList.add(order);
                    } else {
                        int y;
                        switch (state) {
                            case Planned:
                                y = DateUtils.getYear(order.getDateModified());
                                if (y == year) orderList.add(order);
                                break;
                            case Ordered:
                                y = DateUtils.getYear(order.getDateOrdered());
                                if (y == year) orderList.add(order);
                                break;
                            case Received:
                                y = DateUtils.getYear(order.getDateReceived());
                                if (y == year) orderList.add(order);
                                break;
                        }
                    }
                }
            }
        }

        for (PcbOrder order : cache().getPcbOrders()) {
            if (!order.isUnknown()) {
                OrderStates state = order.getOrderState();
                if (state.equals(states)) {
                    if (year < 1) {
                        orderList.add(order);
                    } else {
                        int y;
                        switch (state) {
                            case Planned:
                                y = DateUtils.getYear(order.getDateModified());
                                if (y == year) orderList.add(order);
                                break;
                            case Ordered:
                                y = DateUtils.getYear(order.getDateOrdered());
                                if (y == year) orderList.add(order);
                                break;
                            case Received:
                                y = DateUtils.getYear(order.getDateReceived());
                                if (y == year) orderList.add(order);
                                break;
                        }
                    }
                }
            }
        }

        return orderList;
    }

    public List<ItemOrder> findPlannedItemOrders() {
        List<ItemOrder> itemOrders = new ArrayList<>();
        for (ItemOrder o : cache().getItemOrders()) {
            if (!o.isUnknown() && !o.isOrdered()) {
                itemOrders.add(o);
            }
        }
        return itemOrders;
    }

    public List<PcbOrder> findPlannedPcbOrders() {
        List<PcbOrder> pcbOrders = new ArrayList<>();
        for (PcbOrder o : cache().getPcbOrders()) {
            if (!o.isUnknown() && !o.isOrdered()) {
                pcbOrders.add(o);
            }
        }
        return pcbOrders;
    }

    public List<ItemOrder> findPlannedOrders(DistributorType distributorType) {
        List<ItemOrder> itemOrders = new ArrayList<>();
        for (ItemOrder o : cache().getItemOrders()) {
            if (!o.isUnknown() && !o.isOrdered() && o.getDistributorType().equals(distributorType)) {
                itemOrders.add(o);
            }
        }
        return itemOrders;
    }

    public List<ProjectPcb> findPcbsForItem(long itemId) {
        List<ProjectPcb> projects = new ArrayList<>();
        if (itemId > DbObject.UNKNOWN_ID) {
            for (ProjectPcb pcb : cache().getProjectPcbs()) {
                for (PcbItemProjectLink projectLink : pcb.getPcbItemList()) {
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
    public ItemOrder findLastOrderForItem(long itemId) {
        List<ItemOrder> itemOrders = findOrdersForItem(itemId);
        if (itemOrders.size() == 0) return null;
        else if (itemOrders.size() == 1) return itemOrders.get(0);
        else {
            //itemOrders.sort(new ItemOrder.SortAllOrders());
            return itemOrders.get(itemOrders.size()-1); // Return the last one
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

    public LocationType findLocationTypeByName(String name) {
        if (name != null && !name.isEmpty()) {
            for (LocationType lt : cache().getLocationTypes()) {
                if (lt.getName().equals(name)) {
                    return lt;
                }
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

    public CreatedPcb findCreatedPcbById(long id) {
        if (id > 0) {
            for (CreatedPcb cp : cache().getCreatedPcbs()) {
                if (cp.getId() == id) {
                    return cp;
                }
            }
        }
        return null;
    }

    public List<CreatedPcb> findCreatedPcbsForProjectPcb(long projectPcbId) {
        List<CreatedPcb> createdPcbList = new ArrayList<>();
        if (projectPcbId > 0) {
            for (CreatedPcb cp : cache().getCreatedPcbs()) {
                if (cp.getProjectPcbId() == projectPcbId) {
                    createdPcbList.add(cp);
                }
            }
        }
        return createdPcbList;
    }

    public List<CreatedPcb> findCreatedPcbsByPcbAndOrder(long pcbId, long orderId) {
        List<CreatedPcb> createdPcbList = new ArrayList<>();
        if (pcbId > DbObject.UNKNOWN_ID && orderId > DbObject.UNKNOWN_ID) {
            for (CreatedPcb cp : cache().getCreatedPcbs()) {
                if (cp.getProjectPcbId() == pcbId && cp.getOrderId() == orderId) {
                    createdPcbList.add(cp);
                }
            }
        }
        return createdPcbList;
    }

    public CreatedPcbLink findCreatedPcbLinkById(long linkId) {
        if (linkId > DbObject.UNKNOWN_ID) {
            for (CreatedPcbLink cpl : cache().getCreatedPcbLinks()) {
                if (cpl.getId() == linkId) {
                    return cpl;
                }
            }
        }
        return null;
    }

    public List<CreatedPcbLink> findCreatedPcbLinksByProjectPcbId(long projectPcbId) {
        List<CreatedPcbLink> createdPcbLinks = new ArrayList<>();
        if (projectPcbId > 0) {
            for (CreatedPcbLink cpl : cache().getCreatedPcbLinks()) {
                if (cpl.getPcbItemProjectLinkId() > DbObject.UNKNOWN_ID && cpl.getPcbItemProjectLink().getProjectPcbId() == projectPcbId) {
                    createdPcbLinks.add(cpl);
                }
            }
        }
        return createdPcbLinks;
    }

    public List<CreatedPcbLink> findCreatedPcbLinks(long projectPcbId, long createdPcbId) {
        List<CreatedPcbLink> createdPcbLinks = new ArrayList<>();
        if (projectPcbId > 0 && createdPcbId > 0) {
            for(CreatedPcbLink cpl : findCreatedPcbLinksByProjectPcbId(projectPcbId)) {
                if (cpl.getCreatedPcbId() == createdPcbId) {
                    createdPcbLinks.add(cpl);
                }
            }
        }
        return createdPcbLinks;
    }

    public SolderItem findSolderItemById(long solderItemId) {
        if (solderItemId > DbObject.UNKNOWN_ID) {
            for (SolderItem solderItem : cache().getSolderItems()) {
                if (solderItem.getId() == solderItemId) {
                    return solderItem;
                }
            }
        }
        return null;
    }

    public List<SolderItem> findSolderItemsForCreatedPcbLinkId(long createdPcbLinkId) {
        List<SolderItem> solderItems = new ArrayList<>();
        if (createdPcbLinkId > DbObject.UNKNOWN_ID) {
            for (SolderItem solderItem : cache().getSolderItems()) {
                if (solderItem.getCreatedPcbLinkId() == createdPcbLinkId) {
                    solderItems.add(solderItem);
                }
            }
        }
        return solderItems;
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

    public PcbItemProjectLink findPcbItemProjectLinkById(long pcbItemProjectLinkId) {
        if (pcbItemProjectLinkId > 0) {
            for (PcbItemProjectLink pipl : cache().getPcbItemProjectLinks()) {
                if (pipl.getId() == pcbItemProjectLinkId) {
                    return pipl;
                }
            }
        }
        return null;
    }

    public PcbItemProjectLink findPcbItemProjectLink(long projectPcbId, long pcbItemId) {
        if (projectPcbId > 0 && pcbItemId > 0) {
            for (PcbItemProjectLink pipl : findPcbItemLinksWithProjectPcb(projectPcbId)) {
                if (pipl.getPcbItemId() == pcbItemId) {
                    return pipl;
                }
            }
        }
        return null;
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

    public ParserItemLink findParserItemLink(PcbItem pcbItem) {
        if (pcbItem != null) {
            for (ParserItemLink link : cache().getParserItemLinks()) {
                String linkName = link.getPcbItemName().toUpperCase();
                String name = pcbItem.getPartName().toUpperCase();
                if (!name.isEmpty() && !linkName.isEmpty()) {
                    if (linkName.equals(name)) {
                        return link;
                    }
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

    public PendingOrder findPendingOrderById(long id) {
        if (id > DbObject.UNKNOWN_ID) {
            for (PendingOrder po : cache().getPendingOrders()) {
                if (po.getId() == id) {
                    return po;
                }
            }
        }
        return null;
    }

    public LocationLabel findLocationLabelById(long id) {
        if (id > DbObject.UNKNOWN_ID) {
            for (LocationLabel ll : cache().getLocationLabels()) {
                if (ll.getId() == id) {
                    return ll;
                }
            }
        }
        return null;
    }

    public LocationLabel findLocationLabelByName(String name) {
        if (name != null && !name.isEmpty()) {
            for (LocationLabel ll : cache().getLocationLabels()) {
                if (ll.getName().equalsIgnoreCase(name)) {
                    return ll;
                }
            }
        }
        return null;
    }

    public LabelAnnotation findLabelAnnotationById(long id) {
        if (id > DbObject.UNKNOWN_ID) {
            for (LabelAnnotation la : cache().getLabelAnnotations()) {
                if (la.getId() == id) {
                    return la;
                }
            }
        }
        return null;
    }

    public List<LabelAnnotation> findLabelAnnotationsForLocation(long locationLabelId) {
        List<LabelAnnotation> annotations = new ArrayList<>();
        if (locationLabelId > DbObject.UNKNOWN_ID) {
            for (LabelAnnotation la : cache().getLabelAnnotations()) {
                if (la.getLocationLabelId() == locationLabelId) {
                    annotations.add(la);
                }
            }
        }
        return annotations;
    }
}
