package com.waldo.inventory.gui;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.gui.components.IObjectSearchPanel;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class TopToolBar extends JPanel implements
        IObjectSearchPanel.IObjectSearchListener,
        IObjectSearchPanel.IObjectSearchBtnListener{

    private Application application;

    private IdBToolBar mainViewToolBar;
    private IObjectSearchPanel searchPanel;
    private JPanel contentPane;

    public TopToolBar(Application application, IdBToolBar.IdbToolBarListener toolBarListener){
        this.application = application;

        // Tool bars
        mainViewToolBar = new IdBToolBar(toolBarListener);
        mainViewToolBar.setFloatable(false);
        contentPane = new JPanel();
        contentPane.setOpaque(false);

        // Search stuff: search only for items
        searchPanel = new IObjectSearchPanel(true, DbObject.TYPE_ITEM);
        searchPanel.addSearchListener(this);
        searchPanel.addSearchBtnListener(this);

        //createLayout();
        setLayout(new BorderLayout());
        add(mainViewToolBar, BorderLayout.WEST);
        add(contentPane, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.EAST);
    }

    public void setDbToolbarVisible(boolean visible) {
        mainViewToolBar.setVisible(visible);
    }

    public void setAdvancedSearchVisible(boolean visible) {

    }

    public void clearSearch() {
        searchPanel.clearSearch();
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    private void createLayout() {
        SpringLayout layout = new SpringLayout();

        // Tool bar
        layout.putConstraint(SpringLayout.NORTH, mainViewToolBar, 5, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.SOUTH, mainViewToolBar, -5, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.WEST, mainViewToolBar, -5, SpringLayout.WEST, this);

        // Search panel
        layout.putConstraint(SpringLayout.NORTH, searchPanel, 5, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.SOUTH, searchPanel, -5, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.EAST, searchPanel, 5, SpringLayout.EAST, this);

        // Content
        layout.putConstraint(SpringLayout.NORTH, contentPane, 5, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.SOUTH, contentPane, -5, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.WEST, contentPane, 5, SpringLayout.EAST, mainViewToolBar);
        layout.putConstraint(SpringLayout.EAST, contentPane, 5, SpringLayout.WEST, searchPanel);

        add(mainViewToolBar);
        add(contentPane);
        add(searchPanel);
        setPreferredSize(new Dimension(600,  60));
        setLayout(layout);
    }

    @Override
    public void onDbObjectFound(java.util.List<DbObject> foundObjects) {
        application.setTableItems(foundObjects);
        if (foundObjects.size() > 0) {
            if (foundObjects.get(0) instanceof Item) {
                application.setSelectedItem((Item) foundObjects.get(0));
            }
            if (foundObjects.get(0) instanceof OrderItem) {
                application.setSelectedOrderItem((OrderItem) foundObjects.get(0));
            }
        }
    }

    @Override
    public void onSearchCleared() {
        application.setTableItems(null); // Should set the table to the selected sub category
        switch (application.getSelectedTab()) {
            case Application.TAB_ITEMS:
                application.setSelectedItem(application.getSelectedItem());
                break;
            case Application.TAB_ORDERS:
                application.setSelectedOrderItem(application.getSelectedOrderItem());
                break;
        }

    }

    @Override
    public void nextSearchObject(DbObject next) {
        if (next instanceof Item) {
            try {
                application.setSelectedItem((Item) next);
            } catch (Exception e) {
                Status().setError("Error selecting item.", e);
            }
        }
        if (next instanceof OrderItem) {
            try {
                application.setSelectedOrderItem((OrderItem) next);
            } catch (Exception e) {
                Status().setError("Error selecting OrderItem.", e);
            }
        }
    }

    @Override
    public void previousSearchObject(DbObject previous) {
        if (previous instanceof Item) {
            try {
                application.setSelectedItem((Item) previous);
            } catch (Exception e) {
                Status().setError("Error selecting item.", e);
            }
        }
        if (previous instanceof OrderItem) {
            try {
                application.setSelectedOrderItem((OrderItem) previous);
            } catch (Exception e) {
                Status().setError("Error selecting OrderItem.", e);
            }
        }
    }

    public void setRefreshActionEnabled(boolean enabled) {
        mainViewToolBar.setRefreshActionEnabled(enabled);
    }

    public void setAddActionEnabled(boolean enabled) {
        mainViewToolBar.setAddActionEnabled(enabled);
    }

    public void setDeleteActionEnabled(boolean enabled) {
        mainViewToolBar.setDeleteActionEnabled(enabled);
    }

    public void setEditActionEnabled(boolean enabled) {
        mainViewToolBar.setEditActionEnabled(enabled);
    }

    public void setSearchEnabled(boolean enabled) {
        searchPanel.setEnabled(enabled);
    }

    public void setSearchList(List<DbObject> searchList) {
        if (searchPanel != null) {
            searchPanel.setSearchList(searchList);
        }
    }
}
