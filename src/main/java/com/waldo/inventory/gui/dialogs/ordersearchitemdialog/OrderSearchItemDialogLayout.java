package com.waldo.inventory.gui.dialogs.ordersearchitemdialog;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IObjectSearchPanel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.IItemTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static com.waldo.inventory.managers.CacheManager.cache;

abstract class OrderSearchItemDialogLayout extends IDialog implements
        IObjectSearchPanel.IObjectSearchListener<Item>,
        ListSelectionListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IObjectSearchPanel<Item> searchPanel;
    ITable resultTable;
    IItemTableModel resultTableModel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderSearchItemDialogLayout(Application application, String title) {
        super(application, title);
        showTitlePanel(false);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Search panel
        searchPanel = new IObjectSearchPanel<>(cache().getItems());
        searchPanel.addSearchListener(this);

        // Item table
        resultTableModel = new IItemTableModel();
        resultTable = new ITable<>(resultTableModel);
        resultTable.getSelectionModel().addListSelectionListener(this);
        resultTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JScrollPane pane = new JScrollPane(resultTable);
        pane.setPreferredSize(new Dimension(600,400));

        getContentPanel().add(searchPanel, BorderLayout.NORTH);
        getContentPanel().add(pane, BorderLayout.CENTER);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {

    }
}
