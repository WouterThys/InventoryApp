package com.waldo.inventory.gui.dialogs.historydialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class HistoryDialogLayout extends IDialog implements GuiInterface {

    private static final SimpleDateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final String[] columnNames = {"", "Name", "Date", "Go"};

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private DefaultTableModel tableModel;
    private ITable historyTable;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Item historyItem;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    HistoryDialogLayout(Application application) {
        super(application, "History");
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<DbObject> findHistoryObjects(Item item) {

        List<DbObject> foundObjects = new ArrayList<>();

        // Find orders
        foundObjects.addAll(SearchManager.sm().findOrdersForItem(item.getId()));

        // Later: find projects and other stuff??

        return foundObjects;
    }

    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(columnNames);
        historyTable = new ITable(tableModel) {
            @Override
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        historyTable.setRowHeight(50);

        Action go = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int modelRow = Integer.valueOf(e.getActionCommand());
                Order order = (Order) tableModel.getValueAt(modelRow, 1);
                // Go to orders tab
                application.setSelectedTab(Application.TAB_ORDERS);
                // Select order
                application.getOrderPanel().treeSelectOrder(order);
                // Select order item
                application.getOrderPanel().tableSelectOrderItem(order.findOrderItemInOrder(historyItem.getId()));
            }
        };

        ITableEditors.ButtonEditor buttonEditor = new ITableEditors.ButtonEditor(historyTable, go, 3);
        buttonEditor.setMnemonic(KeyEvent.VK_ENTER);

    }

    @Override
    public void initializeLayouts() {
        JScrollPane pane = new JScrollPane(historyTable);
        pane.setPreferredSize(new Dimension(600,400));

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(pane, BorderLayout.CENTER);
        pack();
    }

    @Override
    public void updateComponents(Object object) {

        if (object != null) {
            historyItem = (Item) object;
            //tableModel.setHistoryObjectList(findHistoryObjects(historyItem));

            for (DbObject o : findHistoryObjects(historyItem)) {
                switch (DbObject.getType(o)) {
                    case DbObject.TYPE_ORDER:
                        Order order = (Order) o;
                        tableModel.addRow(new Object[] {
                                imageResource.readImage("HistoryDialog.OrderIcon"),
                                order,
                                dateFormatLong.format(order.getDateModified()),
                                "Go"
                        });
                        break;
                    default:
                        break;
                }
            }
            try {
                setTitleName(historyItem.getName());
                URL url = new File(historyItem.getIconPath()).toURI().toURL();
                setTitleIcon(imageResource.readImage(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
