package com.waldo.inventory.gui.components.trees;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.gui.components.ITree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IOrderTree extends ITree<Order> {


    public IOrderTree(Order root, boolean showRoot, boolean allowMultiSelect) {
        super(root, showRoot, allowMultiSelect);
    }

    @Override
    protected DefaultTreeModel createModel(Order root) {
        return null;
    }


    public void removeOrder(Order order) {

    }

    @Override
    public void removeItem(Order parent, Order item) {
        super.removeItem(parent, item);
    }



    public static DefaultTreeCellRenderer getOrdersRenderer() {
        return new DefaultTreeCellRenderer() {
            private final ImageIcon receivedIcon = imageResource.readIcon("Orders.Tree.Received");
            private final ImageIcon orderedIcon = imageResource.readIcon("Orders.Tree.Ordered");
            private final ImageIcon plannedIcon = imageResource.readIcon("Orders.Tree.Planned");

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
                                break;
                        }
                    } else {
                        setIcon(null);
                    }
                }

                return c;
            }
        };
    }
}
