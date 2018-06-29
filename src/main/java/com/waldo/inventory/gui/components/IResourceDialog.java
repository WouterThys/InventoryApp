package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class IResourceDialog<R extends DbObject> extends IObjectDialog<R> implements
        ListSelectionListener,
        IObjectSearchPanel.SearchListener<R>  {

    private DefaultListModel<R> resourceListModel;
    private JList<R> resourceList;
    private IdBToolBar resourceTb;
    private IObjectSearchPanel<R> resourceSearchPnl;

    public IResourceDialog(Window window, String title, Class<R> c) {
        super(window, title, null, c);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    protected void updateEnabledComponents() {
        boolean enabled = getObject() != null;

        resourceTb.setDeleteActionEnabled(enabled);
        resourceTb.setEditActionEnabled(enabled);
    }

    protected void setResourceList(List<R> resourceList) {
        if (resourceList != null) {
            resourceListModel.removeAllElements();
            for (R r : resourceList) {
                if (!r.isUnknown()) {
                    resourceListModel.addElement(r);
                }
            }
        }
    }

    protected void selectResource(R resource) {
        if (hasChanged()) {
            int res = JOptionPane.showConfirmDialog(
                    IResourceDialog.this,
                    getObject().toString() + " has changed, do you want to save it?",
                    "Changed",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            switch (res) {
                case JOptionPane.YES_OPTION: save(false); break;
                case JOptionPane.NO_OPTION: break;
                case JOptionPane.CANCEL_OPTION: return;
            }
        }

        setOriginalObject(resource);

        if (resource != null) {
            setDetails(resource);
        } else {
            clearDetails();
        }
        updateEnabledComponents();
    }

    protected R getSelectedResource() {
        return getObject();
    }

    private JPanel createWestPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        TitledBorder titledBorder = GuiUtils.createTitleBorder(getTitle());

        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel southPanel = new JPanel(new BorderLayout());

        northPanel.add(resourceSearchPnl, BorderLayout.CENTER);
        southPanel.add(resourceTb, BorderLayout.WEST);

        JScrollPane scrollPane = new JScrollPane(resourceList);
        scrollPane.setPreferredSize(new Dimension(100, 400));

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);
        panel.setBorder(titledBorder);

        return panel;
    }

    protected abstract List<R> getAllResources();
    protected abstract R getNewResource();

    protected abstract void initializeDetailComponents();
    protected abstract JPanel createDetailPanel();
    protected abstract void setDetails(R resource);
    protected abstract void clearDetails();


    //
    // Search
    //
    @Override
    public void onObjectsFound(List<R> foundObjects) {
        if (foundObjects != null && foundObjects.size() > 0) {
            beginWait();
            try {
                setResourceList(foundObjects);
                R r = foundObjects.get(0);
                resourceList.setSelectedValue(r, true);
                resourceSearchPnl.setCurrentObject(r);
            } finally {
                endWait();
            }
        } else {
            setResourceList(new ArrayList<>());
        }
    }

    @Override
    public void onNextObjectSelected(R next) {
        resourceList.setSelectedValue(next, true);
    }

    @Override
    public void onPreviousObjectSelected(R previous) {
        resourceList.setSelectedValue(previous, true);
    }

    @Override
    public void onSearchCleared() {
        updateComponents(getObject());
    }

    //
    // Gui
    //
    @Override
    public void initializeComponents() {
       resourceSearchPnl = new IObjectSearchPanel<>(getAllResources(), this, true);
       resourceListModel = new DefaultListModel<>();
       resourceList = new JList<>(resourceListModel);
       resourceList.addListSelectionListener(this);

       resourceTb = new IdBToolBar(createToolBarListener());

       initializeDetailComponents();
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel detailPanel = createDetailPanel();
        detailPanel.setBorder(GuiUtils.createTitleBorder("Info"));

        getContentPanel().add(createWestPanel(), BorderLayout.WEST);
        getContentPanel().add(detailPanel, BorderLayout.CENTER);
        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if(isUpdating()) {
            return;
        }
        beginWait();
        try {
            List<R> resources = getAllResources();
            resources.sort(new ComparatorUtils.DbObjectNameComparator<>());
            setResourceList(resources);

            R r = null;
            if (args.length > 0) {
                try {
                    r = (R) args[0];
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
            if (r == null) {
                if (resources.size() > 0) r = resources.get(0);
                if (r != null && r.isUnknown() && resources.size() > 1) r = resources.get(1);

                resourceList.setSelectedValue(r, true);
                selectResource(r);
            }

        } finally {
            endWait();
        }
    }

    //
    // Cache changed
    //
    @Override
    public void onInserted(R object) {
        super.onInserted(object);
        beginWait();
        try {
            setResourceList(getAllResources());
        } finally {
            endWait();
        }
        resourceList.setSelectedValue(object, true);
    }

    @Override
    public void onUpdated(R object) {
        super.onUpdated(object);
        beginWait();
        try {
            setResourceList(getAllResources());
        } finally {
            endWait();
        }
        resourceList.setSelectedValue(object, true);
    }

    @Override
    public void onDeleted(R object) {
        super.onDeleted(object);
        beginWait();
        try {
            setResourceList(getAllResources());
        } finally {
            endWait();
        }
        clearDetails();
        resourceList.setSelectedIndex(0);

    }

    @Override
    public void onCacheCleared() {
        // Don't care
    }

    //
    // List selection listener
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !isUpdating()) {
            JList list = (JList) e.getSource();
            Object selected = list.getSelectedValue();

            selectResource((R) selected);
        }
    }

    protected void onTbAddResource() {
        DbObjectDialog<R> dialog = new DbObjectDialog<>(IResourceDialog.this, "New", getNewResource());
        if (dialog.showDialog() == DbObjectDialog.OK) {
            R r = dialog.getDbObject();
            r.save();
        }
    }

    protected void onTbEditResource() {
        if (getObject() != null) {
            DbObjectDialog<R> dialog = new DbObjectDialog<>(IResourceDialog.this, "Update " + getObject().getName(), getObject());
            if (dialog.showDialog() == DbObjectDialog.OK) {
                doSave();
            }
        }
    }

    protected void onTbDeleteResource() {
        if (getObject() != null) {
            int res = JOptionPane.showConfirmDialog(IResourceDialog.this, "Are you sure you want to delete \"" + getObject().getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                doDelete();
            }
        }
    }

    //
    // Tool bar
    //
    private IdBToolBar.IdbToolBarListener createToolBarListener() {
        return new IdBToolBar.IdbToolBarListener() {
            @Override
            public void onToolBarRefresh(IdBToolBar source) {
                updateComponents(getObject());
            }

            @Override
            public void onToolBarAdd(IdBToolBar source) {
                onTbAddResource();
            }

            @Override
            public void onToolBarDelete(IdBToolBar source) {
                onTbDeleteResource();
            }

            @Override
            public void onToolBarEdit(IdBToolBar source) {
                onTbEditResource();
            }
        };
    }
}
