package com.waldo.inventory.gui.components.trees;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.utils.DateUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class IOrderTree extends ITree<ItemOrder> {

    private final ImageIcon receivedIcon = imageResource.readIcon("Orders.Tree.Received");
    private final ImageIcon orderedIcon = imageResource.readIcon("Orders.Tree.Ordered");
    private final ImageIcon plannedIcon = imageResource.readIcon("Orders.Tree.Planned");

    private final ImageIcon itemIcon = imageResource.readIcon("Items.Tree.Set");
    private final ImageIcon pcbIcon = imageResource.readIcon("Projects.Details.Pcb");

    private DefaultMutableTreeNode orderedNode;
    private DefaultMutableTreeNode plannedNode;
    private DefaultMutableTreeNode receivedNode;

    public IOrderTree(ItemOrder root, boolean showRoot, boolean allowMultiSelect) {
        super(root, showRoot, allowMultiSelect);

        setRenderer();
    }

    @Override
    protected DefaultTreeModel createModel(ItemOrder root) {
        rootNode = new DefaultMutableTreeNode(root);

        ItemOrder ordered = ItemOrder.createDummyOrder("Ordered");
        ItemOrder planned = ItemOrder.createDummyOrder("Planned");
        ItemOrder received = ItemOrder.createDummyOrder("Received");

        orderedNode = new DefaultMutableTreeNode(ordered);
        plannedNode = new DefaultMutableTreeNode(planned);
        receivedNode = new DefaultMutableTreeNode(received);

        rootNode.add(receivedNode);
        rootNode.add(orderedNode);
        rootNode.add(plannedNode);

        createTreeNodes();

        return new DefaultTreeModel(rootNode);
    }

    private void createTreeNodes() {
        java.util.List<ItemOrder> plannedList = new ArrayList<>();
        java.util.List<ItemOrder> orderedList = new ArrayList<>();
        Map<Integer, List<ItemOrder>> receivedList = new TreeMap<>();

        for (ItemOrder itemOrder : cache().getItemOrders()) {
            if (!itemOrder.isUnknown()) {
                switch (itemOrder.getOrderState()) {
                    case Planned:
                        plannedList.add(itemOrder);
                        break;
                    case Ordered:
                        orderedList.add(itemOrder);
                        break;
                    case Received:
                        int year = DateUtils.getYear(itemOrder.getDateReceived());
                        if (!receivedList.containsKey(year)) {
                            receivedList.put(year, new ArrayList<>());
                        }
                        receivedList.get(year).add(itemOrder);
                        break;

                    default:
                        break;
                }
            }
        }

        for (int year : receivedList.keySet()) {
            receivedList.get(year).sort(new ComparatorUtils.ReceivedOrderComparator());
        }

        plannedNode.removeAllChildren();
        for (ItemOrder o : plannedList) {
            plannedNode.add(new DefaultMutableTreeNode(o, false));
        }

        orderedNode.removeAllChildren();
        for (ItemOrder o : orderedList) {
            orderedNode.add(new DefaultMutableTreeNode(o, false));
        }

        receivedNode.removeAllChildren();
        for (int year : receivedList.keySet()) {
            ItemOrder dateItemOrder = ItemOrder.createDummyOrder(String.valueOf(year));
            DefaultMutableTreeNode rNode = new DefaultMutableTreeNode(dateItemOrder);
            for (ItemOrder o : receivedList.get(year)) {
                rNode.add(new DefaultMutableTreeNode(o, false));
            }
            receivedNode.add(rNode);
        }
    }

    public void collapseAll() {
        TreePath path;
        path = new TreePath(orderedNode.getPath());
        collapsePath(path);
        path = new TreePath(plannedNode.getPath());
        collapsePath(path);
        path = new TreePath(receivedNode.getPath());
        collapsePath(path);
    }

    public void expandAll() {
        TreePath path;
        path = new TreePath(orderedNode.getPath());
        expandPath(path);
        path = new TreePath(plannedNode.getPath());
        expandPath(path);
        path = new TreePath(receivedNode.getPath());
        expandPath(path);
    }

    @Override
    public void addItem(ItemOrder itemOrder) {
        if (itemOrder != null) {
            DefaultMutableTreeNode parentNode = null;
            switch (itemOrder.getOrderState()) {
                case Planned:
                    parentNode = plannedNode;
                    break;
                case Ordered:
                    parentNode = orderedNode;
                    break;
                case Received:
                    String year = String.valueOf(DateUtils.getYear(itemOrder.getDateReceived()));
                    // Find node of year to add new itemOrder to
                    Enumeration e = receivedNode.depthFirstEnumeration();
                    while (e.hasMoreElements()) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
                        if (node.toString().equals(year)) {
                            parentNode = node;
                        }
                    }
                    // Create new node
                    if (parentNode == null) {
                        ItemOrder dateItemOrder = ItemOrder.createDummyOrder(year);
                        DefaultMutableTreeNode rNode = new DefaultMutableTreeNode(dateItemOrder);
                        receivedNode.add(rNode);
                        parentNode = rNode;
                    }
                    break;
                    default:
                        return;
            }
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(itemOrder);

            treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            scrollPathToVisible(new TreePath(childNode.getPath()));
        }
    }

    public void removeOrder(ItemOrder itemOrder) {
        if (itemOrder != null) {
            DefaultMutableTreeNode parentNode = null;
            switch (itemOrder.getOrderState()) {
                case Planned:
                    parentNode = plannedNode;
                    break;
                case Ordered:
                    parentNode = orderedNode;
                    break;
                case Received:
                    String year = String.valueOf(DateUtils.getYear(itemOrder.getDateReceived()));
                    // Find node of year to add new itemOrder to
                    Enumeration e = receivedNode.depthFirstEnumeration();
                    while (e.hasMoreElements()) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
                        if (node.toString().equals(year)) {
                            parentNode = node;
                        }
                    }
                    break;
                default:
                    return;
            }
            if (parentNode != null) {
                super.removeItem((ItemOrder) parentNode.getUserObject(), itemOrder);
            }
        }
    }

    public void structureChanged() {
        SwingUtilities.invokeLater(() -> {
            createTreeNodes();
            treeModel.nodeStructureChanged(rootNode);
        });
    }

    private void setRenderer() {
        setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof DefaultMutableTreeNode) {
                    ItemOrder itemOrder = (ItemOrder) ((DefaultMutableTreeNode) value).getUserObject();
                    Font font = getFont();
                    setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));
                    if (!itemOrder.canBeSaved()) {
                        switch (itemOrder.getName()) {
                            case "Planned":
                                setIcon(plannedIcon);
                                break;
                            case "Ordered":
                                setIcon(orderedIcon);
                                break;
                            case "Received":
                                setIcon(receivedIcon);
                                break;
                            default:
                                setIcon(null);
                                break;
                        }
                    } else {
                        switch (itemOrder.getDistributorType()) {
                            default:
                            case Items: setIcon(itemIcon); break;
                            case Pcbs: setIcon(pcbIcon); break;
                        }
                        if (itemOrder.isAutoOrder()) {
                            setFont(new Font(font.getName(), Font.ITALIC, font.getSize()));
                        }
                    }
                }

                return c;
            }
        });
    }


}
