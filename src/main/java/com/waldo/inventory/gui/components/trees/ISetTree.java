package com.waldo.inventory.gui.components.trees;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.components.ITree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import static com.waldo.inventory.gui.Application.imageResource;

public class ISetTree extends ITree<Set> {

    public ISetTree(Set rootSet, boolean showRoot, boolean allowMultiSelect) {
        super(rootSet, showRoot, allowMultiSelect);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(imageResource.readIcon("Items.Tree.Set"));
        setCellRenderer(renderer);
    }

    @Override
    protected DefaultTreeModel createModel(Set set) {
        rootNode = new DefaultMutableTreeNode(set);

        for(Item item : set.getSetItems()) {
            rootNode.add(new DefaultMutableTreeNode(item, false));
        }

        return new DefaultTreeModel(rootNode);
    }

    public Set getRootSet() {
        return root;
    }


}