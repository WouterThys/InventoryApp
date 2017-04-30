package com.waldo.inventory.gui.dialogs.historydialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class HistoryDialogLayout extends IDialog implements GuiInterface {


    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    HistoryTableModel tableModel;
    ITable historyTable;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Item historyItem;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    HistoryDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<DbObject> findHistoryObjects(Item item) {

        List<DbObject> foundObjects = new ArrayList<>();

        // Find orders
        foundObjects.addAll(DbManager.db().findOrdersForItem(item.getId()));

        // Later: find projects and other stuff??

        return foundObjects;
    }

    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        tableModel = new HistoryTableModel();
        historyTable = new ITable(tableModel);
        historyTable.setRowHeight(50);

        TableColumn tableColumn = historyTable.getColumnModel().getColumn(3);
        tableColumn.setCellRenderer(new ITableEditors.ButtonRenderer());
        tableColumn.setCellEditor(new ITableEditors.ButtonEditor(new JCheckBox()));

    }

    @Override
    public void initializeLayouts() {
        setTitleName(getTitle());

        getContentPanel().add(new JScrollPane(historyTable));
    }

    @Override
    public void updateComponents(Object object) {

        if (object != null) {
            historyItem = (Item) object;
            tableModel.setHistoryObjectList(findHistoryObjects(historyItem));
            setTitleIcon(resourceManager.readImage(historyItem.getIconPath()));
        }

    }
}
