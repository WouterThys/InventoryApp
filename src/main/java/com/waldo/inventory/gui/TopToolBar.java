package com.waldo.inventory.gui;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableToolBar;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TopToolBar extends JPanel {
//        implements
//        IObjectSearchPanel.IObjectSearchListener,
//        IObjectSearchPanel.IObjectSearchBtnListener {

    private final Application application;

    private final IdBToolBar dbToolBar;
    //private IObjectSearchPanel searchPanel;
    private final JPanel contentPane;

    public TopToolBar(Application application, IdBToolBar.IdbToolBarListener toolBarListener){
        this.application = application;

        // Tool bars
        dbToolBar = new IdBToolBar(toolBarListener);

        contentPane = new JPanel();
        contentPane.setOpaque(false);

        // Search stuff: search only for items
//        searchPanel = new IObjectSearchPanel(true, DbObject.TYPE_ITEM);
//        searchPanel.addSearchListener(this);
//        searchPanel.addSearchBtnListener(this);

        setLayout(new BorderLayout());
        add(dbToolBar, BorderLayout.WEST);
        add(contentPane, BorderLayout.CENTER);
//        add(searchPanel, BorderLayout.EAST);
    }

    public TopToolBar(Application application, IdBToolBar.IdbToolBarListener toolBarListener, ITable table) {
        this.application = application;

        // Tool bars
        dbToolBar = new IdBToolBar(toolBarListener);
        ITableToolBar tableToolBar = new ITableToolBar(table, false);

        contentPane = new JPanel();
        contentPane.setOpaque(false);

        JPanel toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.Y_AXIS));
        toolbarPanel.add(dbToolBar);
        toolbarPanel.add(tableToolBar);

        setLayout(new BorderLayout());
        add(dbToolBar, BorderLayout.WEST);
        add(contentPane, BorderLayout.CENTER);
        add(tableToolBar, BorderLayout.EAST);
    }

    public void setDbToolbarVisible(boolean visible) {
        dbToolBar.setVisible(visible);
    }

    public void clearSearch() {
//        if (searchPanel != null) {
//            searchPanel.clearSearch();
//        }
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    private void createLayout() {
        SpringLayout layout = new SpringLayout();

        // Tool bar
        layout.putConstraint(SpringLayout.NORTH, dbToolBar, 5, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.SOUTH, dbToolBar, -5, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.WEST, dbToolBar, -5, SpringLayout.WEST, this);

        // Search panel
//        layout.putConstraint(SpringLayout.NORTH, searchPanel, 5, SpringLayout.NORTH, this);
//        layout.putConstraint(SpringLayout.SOUTH, searchPanel, -5, SpringLayout.SOUTH, this);
//        layout.putConstraint(SpringLayout.EAST, searchPanel, 5, SpringLayout.EAST, this);

        // Content
        layout.putConstraint(SpringLayout.NORTH, contentPane, 5, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.SOUTH, contentPane, -5, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.WEST, contentPane, 5, SpringLayout.EAST, dbToolBar);
        //layout.putConstraint(SpringLayout.EAST, contentPane, 5, SpringLayout.WEST, searchPanel);

        add(dbToolBar);
        add(contentPane);
        //add(searchPanel);
        setPreferredSize(new Dimension(600,  60));
        setLayout(layout);
    }

//    @Override
//    public void onObjectsFound(java.util.List<DbObject> foundObjects) {
//        application.setTableItems(foundObjects);
//        if (foundObjects.size() > 0) {
//            if (foundObjects.get(0) instanceof Item) {
//                application.setSelectedItem((Item) foundObjects.get(0));
//            }
//            if (foundObjects.get(0) instanceof OrderItem) {
//                application.setSelectedOrderItem((OrderItem) foundObjects.get(0));
//            }
//        }
//    }
//
//    @Override
//    public void onSearchCleared() {
//        application.setTableItems(null); // Should set the table to the selected sub category
//        switch (application.getSelectedTab()) {
//            case Application.TAB_ITEMS:
//                application.setSelectedItem(application.getSelectedItem());
//                break;
//            case Application.TAB_ORDERS:
//                application.setSelectedOrderItem(application.getSelectedOrderItem());
//                break;
//        }
//
//    }
//
//    @Override
//    public void onNextSearchObject(DbObject next) {
//        if (next instanceof Item) {
//            try {
//                application.setSelectedItem((Item) next);
//            } catch (Exception e) {
//                Status().setError("Error selecting item.", e);
//            }
//        }
//        if (next instanceof OrderItem) {
//            try {
//                application.setSelectedOrderItem((OrderItem) next);
//            } catch (Exception e) {
//                Status().setError("Error selecting OrderItem.", e);
//            }
//        }
//    }
//
//    @Override
//    public void onPreviousSearchObject(DbObject previous) {
//        if (previous instanceof Item) {
//            try {
//                application.setSelectedItem((Item) previous);
//            } catch (Exception e) {
//                Status().setError("Error selecting item.", e);
//            }
//        }
//        if (previous instanceof OrderItem) {
//            try {
//                application.setSelectedOrderItem((OrderItem) previous);
//            } catch (Exception e) {
//                Status().setError("Error selecting OrderItem.", e);
//            }
//        }
//    }

    public void setRefreshActionEnabled(boolean enabled) {
        dbToolBar.setRefreshActionEnabled(enabled);
    }

    public void setAddActionEnabled(boolean enabled) {
        dbToolBar.setAddActionEnabled(enabled);
    }

    public void setDeleteActionEnabled(boolean enabled) {
        dbToolBar.setDeleteActionEnabled(enabled);
    }

    public void setEditActionEnabled(boolean enabled) {
        dbToolBar.setEditActionEnabled(enabled);
    }

    public void setSearchEnabled(boolean enabled) {
        //searchPanel.setEnabled(enabled);
    }

    public void setSearchList(List<DbObject> searchList) {
//        if (searchPanel != null) {
//            searchPanel.setSearchList(searchList);
//        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setSearchEnabled(enabled);
        dbToolBar.setEnabled(enabled);
    }
}
