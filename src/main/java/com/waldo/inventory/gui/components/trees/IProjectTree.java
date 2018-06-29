package com.waldo.inventory.gui.components.trees;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.ITree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class IProjectTree extends ITree<Project> {

    private final ImageIcon codeIcon = imageResource.readIcon("Code.S");
    private final ImageIcon pcbIcon = imageResource.readIcon("Pcb.S");
    private final ImageIcon otherIcon = imageResource.readIcon("Other.S");

    public IProjectTree(Project root, boolean showRoot, boolean allowMultiSelect) {
        super(root, showRoot, allowMultiSelect);

        setRenderer();
    }

    @Override
    protected DefaultTreeModel createModel(Project root) {
        rootNode = new DefaultMutableTreeNode(root);

        for (Project p : cache().getProjects()) {
            if (!p.isUnknown()) {
                rootNode.add(createProjectNode(p));
            }
        }

        return new DefaultTreeModel(rootNode);
    }

    private DefaultMutableTreeNode createProjectNode(Project project) {
        ProjectCode code = new ProjectCode("Code  ");
        ProjectPcb pcb = new ProjectPcb("Pcbs  ");
        ProjectOther other = new ProjectOther("Other  ");

        code.setCanBeSaved(false);
        pcb.setCanBeSaved(false);
        other.setCanBeSaved(false);

        DefaultMutableTreeNode codeNode = new DefaultMutableTreeNode(code, false);
        DefaultMutableTreeNode pcbNode = new DefaultMutableTreeNode(pcb, false);
        DefaultMutableTreeNode otherNode = new DefaultMutableTreeNode(other, false);
        DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project);

        projectNode.add(codeNode);
        projectNode.add(pcbNode);
        projectNode.add(otherNode);

        return projectNode;
    }

    public void removeProject(Project project) {
        super.removeItem(root, project);
    }

    public void addProject(Project project) {
        if (project != null) {
            DefaultMutableTreeNode parentNode = rootNode;
            DefaultMutableTreeNode childNode = createProjectNode(project);
            treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            scrollPathToVisible(new TreePath(childNode.getPath()));
        }
    }

    public void collapseAll() {
        expandAllNodes(0, getRowCount());
    }

    public void expandAll() {
        expandAllNodes(0, getRowCount());
    }

    private void setRenderer() {
        setCellRenderer(new DefaultTreeCellRenderer() {
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
        });
    }
}
