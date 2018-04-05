package com.waldo.inventory.gui.components.trees;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Order;
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

public class IOrderTree extends ITree<Order> {

    private final ImageIcon receivedIcon = imageResource.readIcon("Orders.Tree.Received");
    private final ImageIcon orderedIcon = imageResource.readIcon("Orders.Tree.Ordered");
    private final ImageIcon plannedIcon = imageResource.readIcon("Orders.Tree.Planned");

    private DefaultMutableTreeNode orderedNode;
    private DefaultMutableTreeNode plannedNode;
    private DefaultMutableTreeNode receivedNode;

    public IOrderTree(Order root, boolean showRoot, boolean allowMultiSelect) {
        super(root, showRoot, allowMultiSelect);

        setRenderer();
    }

    @Override
    protected DefaultTreeModel createModel(Order root) {
        rootNode = new DefaultMutableTreeNode(root);

        Order ordered = Order.createDummyOrder("Ordered");
        Order planned = Order.createDummyOrder("Planned");
        Order received = Order.createDummyOrder("Received");

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
        java.util.List<Order> plannedList = new ArrayList<>();
        java.util.List<Order> orderedList = new ArrayList<>();
        Map<Integer, List<Order>> receivedList = new TreeMap<>();

        for (Order order : cache().getOrders()) {
            if (!order.isUnknown()) {
                switch (order.getOrderState()) {
                    case Planned:
                        plannedList.add(order);
                        break;
                    case Ordered:
                        orderedList.add(order);
                        break;
                    case Received:
                        int year = DateUtils.getYear(order.getDateReceived());
                        if (!receivedList.containsKey(year)) {
                            receivedList.put(year, new ArrayList<>());
                        }
                        receivedList.get(year).add(order);
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
        for (Order o : plannedList) {
            plannedNode.add(new DefaultMutableTreeNode(o, false));
        }

        orderedNode.removeAllChildren();
        for (Order o : orderedList) {
            orderedNode.add(new DefaultMutableTreeNode(o, false));
        }

        receivedNode.removeAllChildren();
        for (int year : receivedList.keySet()) {
            Order dateOrder = Order.createDummyOrder(String.valueOf(year));
            DefaultMutableTreeNode rNode = new DefaultMutableTreeNode(dateOrder);
            for (Order o : receivedList.get(year)) {
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
    public void addItem(Order order) {
        if (order != null) {
            DefaultMutableTreeNode parentNode = null;
            switch (order.getOrderState()) {
                case Planned:
                    parentNode = plannedNode;
                    break;
                case Ordered:
                    parentNode = orderedNode;
                    break;
                case Received:
                    String year = String.valueOf(DateUtils.getYear(order.getDateReceived()));
                    // Find node of year to add new order to
                    Enumeration e = receivedNode.depthFirstEnumeration();
                    while (e.hasMoreElements()) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
                        if (node.toString().equals(year)) {
                            parentNode = node;
                        }
                    }
                    // Create new node
                    if (parentNode == null) {
                        Order dateOrder = Order.createDummyOrder(year);
                        DefaultMutableTreeNode rNode = new DefaultMutableTreeNode(dateOrder);
                        receivedNode.add(rNode);
                        parentNode = rNode;
                    }
                    break;
                    default:
                        return;
            }
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(order);

            treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            scrollPathToVisible(new TreePath(childNode.getPath()));
        }
    }

    public void removeOrder(Order order) {
        if (order != null) {
            DefaultMutableTreeNode parentNode = null;
            switch (order.getOrderState()) {
                case Planned:
                    parentNode = plannedNode;
                    break;
                case Ordered:
                    parentNode = orderedNode;
                    break;
                case Received:
                    String year = String.valueOf(DateUtils.getYear(order.getDateReceived()));
                    // Find node of year to add new order to
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
                super.removeItem((Order) parentNode.getUserObject(), order);
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
                    DbObject object = (DbObject) ((DefaultMutableTreeNode) value).getUserObject();
                    if (!object.canBeSaved()) {
                        switch (object.getName()) {
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
                        setIcon(null);
                    }
                }

                return c;
            }
        });
    }


}
