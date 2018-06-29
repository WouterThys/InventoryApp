package com.waldo.inventory.gui.components.trees;

import com.waldo.inventory.Utils.Statics.OrderStates;
import com.waldo.inventory.classes.dbclasses.AbstractOrder;
import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.inventory.classes.dbclasses.PcbOrder;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.utils.DateUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class IOrdersTree extends ITree<IOrdersTree.OrderTreeNode> {

    public static class OrderTreeNode {

        private OrderStates orderState;
        private String name;
        private Date date;
        private ImageIcon icon;

        OrderTreeNode(OrderStates orderState, String name, Date date, ImageIcon icon) {
            this.orderState = orderState;
            this.date = date;
            this.name = name;
            this.icon = icon;
        }

        OrderTreeNode(AbstractOrder order) {
            this.orderState = order.getOrderState();
            this.name = "";
            switch (orderState) {
                case Planned:
                    date = order.getDateModified();
                    break;
                case Ordered:
                    date = order.getDateOrdered();
                    break;
                case Received:
                    date = order.getDateReceived();
                    break;

                    default:
                        break;
            }

            if (order.getDistributor() != null) {
                switch (order.getDistributor().getDistributorType()) {
                    case Items:
                        icon = itemIcon;
                        break;
                    case Pcbs:
                        icon = pcbIcon;
                        break;
                }
            }
        }

        @Override
        public String toString() {
            return "OrderTreeNode{" +
                    "orderState=" + orderState +
                    ", date=" + getYear() +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OrderTreeNode)) return false;
            OrderTreeNode that = (OrderTreeNode) o;
            return getYear() == that.getYear() &&
                    orderState == that.orderState;
        }

        @Override
        public int hashCode() {
            return Objects.hash(orderState, getYear());
        }

        public OrderStates getOrderState() {
            return orderState;
        }

        public String getName() {
            return name;
        }

        public Date getDate() {
            if (date == null) {
                return DateUtils.minDate();
            }
            return date;
        }

        public int getYear() {
            if (date == null) {
                return 0;
            } else {
                return DateUtils.getYear(date);
            }
        }
    }

    public static final OrderTreeNode defaultRoot = new OrderTreeNode(OrderStates.NoOrder, "", null, null);

    private static final ImageIcon receivedIcon = imageResource.readIcon("Received.S");
    private static final ImageIcon orderedIcon = imageResource.readIcon("Truck.S");
    private static final ImageIcon plannedIcon = imageResource.readIcon("Calendar.S");

    private static final ImageIcon itemIcon = imageResource.readIcon("Components.SS");
    private static final ImageIcon pcbIcon = imageResource.readIcon("Pcb.SS");

    private DefaultMutableTreeNode orderedNode;
    private DefaultMutableTreeNode plannedNode;
    private DefaultMutableTreeNode receivedNode;

    public IOrdersTree(OrderTreeNode rootNode) {
        super(rootNode, false, true);

        setRenderer();
    }

    @Override
    protected DefaultTreeModel createModel(OrderTreeNode root) {
        rootNode = new DefaultMutableTreeNode(root);

        OrderTreeNode ordered = new OrderTreeNode(OrderStates.Ordered, "Ordered", null, orderedIcon);
        OrderTreeNode planned = new OrderTreeNode(OrderStates.Planned, "Planned", null, plannedIcon);
        OrderTreeNode received = new OrderTreeNode(OrderStates.Received, "Received", null, receivedIcon);

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

        List<OrderTreeNode> plannedList = new ArrayList<>();
        List<OrderTreeNode> orderedList = new ArrayList<>();
        List<OrderTreeNode> receivedList = new ArrayList<>();

        for (ItemOrder order : cache().getItemOrders()) {
            if (!order.isUnknown()) {
                OrderStates state = order.getOrderState();
                OrderTreeNode node = new OrderTreeNode(order);
                switch (state) {
                    case Planned:
                        if (!plannedList.contains(node)) plannedList.add(node);
                        break;
                    case Ordered:
                        if (!orderedList.contains(node)) orderedList.add(node);
                        break;
                    case Received:
                        if (!receivedList.contains(node)) receivedList.add(node);
                        break;

                    default:
                        break;
                }
            }
        }

        for (PcbOrder order : cache().getPcbOrders()) {
            if (!order.isUnknown()) {
                OrderStates state = order.getOrderState();
                OrderTreeNode node = new OrderTreeNode(order);
                switch (state) {
                    case Planned:
                        if (!plannedList.contains(node)) plannedList.add(node);
                        break;
                    case Ordered:
                        if (!orderedList.contains(node)) orderedList.add(node);
                        break;
                    case Received:
                        if (!receivedList.contains(node)) receivedList.add(node);
                        break;

                    default:
                        break;
                }
            }
        }

        plannedList.sort(Comparator.comparing(OrderTreeNode::getDate));
        orderedList.sort(Comparator.comparing(OrderTreeNode::getDate));
        receivedList.sort(Comparator.comparing(OrderTreeNode::getDate));

        plannedNode.removeAllChildren();
        for (OrderTreeNode o : plannedList) {
            plannedNode.add(new DefaultMutableTreeNode(o, false));
        }

        orderedNode.removeAllChildren();
        for (OrderTreeNode o : orderedList) {
            orderedNode.add(new DefaultMutableTreeNode(o, false));
        }

        receivedNode.removeAllChildren();
        for (OrderTreeNode o : receivedList) {
            receivedNode.add(new DefaultMutableTreeNode(o, false));
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

    public void addItem(AbstractOrder order) {
        if (order != null) {
            OrderTreeNode node = new OrderTreeNode(order);
            addItem(node);
        }
    }

    @Override
    public void addItem(OrderTreeNode order) {
        if (order != null) {
            DefaultMutableTreeNode parentNode = null;
            switch (order.orderState) {
                case Planned:
                    parentNode = plannedNode;
                    break;
                case Ordered:
                    parentNode = orderedNode;
                    break;
                case Received:
                    parentNode = receivedNode;
                    break;
                default:
                    return;
            }
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(order);

            treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            scrollPathToVisible(new TreePath(childNode.getPath()));
        }
    }

    public void removeOrder(AbstractOrder order) {
        if (order != null) {
            DefaultMutableTreeNode parentNode;
            switch (order.getOrderState()) {
                case Planned:
                    parentNode = plannedNode;
                    break;
                case Ordered:
                    parentNode = orderedNode;
                    break;
                case Received:
                    parentNode = receivedNode;
                    break;
                default:
                    return;
            }
            if (parentNode != null) {
                super.removeItem((OrderTreeNode) parentNode.getUserObject(), new OrderTreeNode(order));
            }
        }
    }

    public void setSelectedItem(AbstractOrder order) {
        if (order != null) {
            setSelectedItem(new OrderTreeNode(order));
        } else {
            setSelectedItem((OrderTreeNode) null);
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
                    OrderTreeNode order = (OrderTreeNode) ((DefaultMutableTreeNode) value).getUserObject();
                    if (order.getYear() < 1) {
                        setText(order.name);
                    } else {
                        setText(String.valueOf(order.getYear()));
                    }

                    setIcon(order.icon);
                }

                return c;
            }
        });
    }


}
