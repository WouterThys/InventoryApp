package com.waldo.inventory.gui.components.trees;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.ITree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class IProjectTree extends ITree<Project> {

    public IProjectTree(Project root, boolean showRoot, boolean allowMultiSelect) {
        super(root, showRoot, allowMultiSelect);
    }

    @Override
    protected DefaultTreeModel createModel(Project root) {
        return null;
    }



    private void treeInitializeTree(DefaultMutableTreeNode rootNode) {
        for (Project p : cache().getProjects()) {
            if (!p.isUnknown()) {
                ProjectCode code = new ProjectCode("Code  ");
                ProjectPcb pcb = new ProjectPcb("Pcbs  ");
                ProjectOther other = new ProjectOther("Other  ");

                code.setCanBeSaved(false);
                pcb.setCanBeSaved(false);
                other.setCanBeSaved(false);

                DefaultMutableTreeNode codeNode = new DefaultMutableTreeNode(code, false);
                DefaultMutableTreeNode pcbNode = new DefaultMutableTreeNode(pcb, false);
                DefaultMutableTreeNode otherNode = new DefaultMutableTreeNode(other, false);
                DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(p);

                projectNode.add(codeNode);
                projectNode.add(pcbNode);
                projectNode.add(otherNode);

                rootNode.add(projectNode);
            }
        }
    }

    public void removeProject(Project project) {

    }

    @Override
    public void removeItem(Project parent, Project item) {
        super.removeItem(parent, item);
    }



    public static DefaultTreeCellRenderer getProjectsRenderer() {
        return new DefaultTreeCellRenderer() {
            private final ImageIcon codeIcon = imageResource.readIcon("Projects.Tree.Code");
            private final ImageIcon pcbIcon = imageResource.readIcon("Projects.Tree.Pcb");
            private final ImageIcon otherIcon = imageResource.readIcon("Projects.Tree.Other");

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof DefaultMutableTreeNode) {
                    DbObject object = (DbObject) ((DefaultMutableTreeNode) value).getUserObject();
                    if (!object.canBeSaved()) {
                        switch (DbObject.getType(object)) {
                            case DbObject.TYPE_PROJECT_CODE:
                                setIcon(codeIcon);
                                break;
                            case DbObject.TYPE_PROJECT_PCB:
                                setIcon(pcbIcon);
                                break;
                            case DbObject.TYPE_PROJECT_OTHER:
                                setIcon(otherIcon);
                                break;
                            default:
                                break;
                        }
                    }
                }

                return c;
            }
        };
    }
}
