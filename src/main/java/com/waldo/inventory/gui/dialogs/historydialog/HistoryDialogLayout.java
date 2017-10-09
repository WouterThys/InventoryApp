package com.waldo.inventory.gui.dialogs.historydialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.IHistoryTableModel;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class HistoryDialogLayout extends IDialog implements GuiInterface {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IHistoryTableModel tableModel;
    private ITable historyTable;

    private ILabel insertedByLbl;
    private ILabel insertedWhenLbl;

    private ILabel updatedByLbl;
    private ILabel updatedWhenLbl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Item historyItem;

    private List<Order> orderHistoryList = new ArrayList<>();
    private List<ProjectPcb> pcbHistoryList = new ArrayList<>();

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    HistoryDialogLayout(Application application) {
        super(application, "History");
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateHistoryViews(Item item) {
        // Find orders
        orderHistoryList.addAll(SearchManager.sm().findOrdersForItem(item.getId()));

        // Find projects
        pcbHistoryList.addAll(SearchManager.sm().findPcbsForItem(item.getId()));
    }

    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        tableModel = new IHistoryTableModel();
        historyTable = new ITable<>(tableModel);
        historyTable.setRowHeight(50);

//        Action go = new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                int modelRow = Integer.valueOf(e.getActionCommand());
//                Order order = (Order) tableModel.getValueAt(modelRow, 1);
//                // Go to orders tab
//                application.setSelectedTab(Application.TAB_ORDERS);
//                // Select order
//                application.getOrderPanel().treeSelectOrder(order);
//                // Select order item
//                application.getOrderPanel().tableSelectOrderItem(order.findOrderItemInOrder(historyItem.getId()));
//            }
//        };
//
//        ITableEditors.ButtonEditor buttonEditor = new ITableEditors.ButtonEditor(historyTable, go, 3);
//        buttonEditor.setMnemonic(KeyEvent.VK_ENTER);

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

            if (!historyItem.getIconPath().isEmpty()) {
                try {
                    Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgItemsPath(), historyItem.getIconPath());
                    URL url = path.toUri().toURL();
                    setTitleIcon(imageResource.readImage(url, 64, 64));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            updateHistoryViews(historyItem);
        }

    }
}
