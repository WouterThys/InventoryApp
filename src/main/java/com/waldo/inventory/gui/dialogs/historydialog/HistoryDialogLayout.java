package com.waldo.inventory.gui.dialogs.historydialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.IOrderHistoryTableModel;
import com.waldo.inventory.gui.components.tablemodels.IPcbHistoryTableModel;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
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
    private IOrderHistoryTableModel orderHistoryModel;
    private ITable<Order> orderHistoryTable;

    private IPcbHistoryTableModel pcbHistoryModel;
    private ITable<ProjectPcb> pcbHistoryTable;

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
        orderHistoryModel.setItemList(orderHistoryList);

        // Find projects
        pcbHistoryList.addAll(SearchManager.sm().findPcbsForItem(item.getId()));
        pcbHistoryModel.setItemList(pcbHistoryList);
    }

    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        orderHistoryModel = new IOrderHistoryTableModel();
        orderHistoryTable = new ITable<>(orderHistoryModel);

        pcbHistoryModel = new IPcbHistoryTableModel();
        pcbHistoryTable = new ITable<>(pcbHistoryModel);

        insertedByLbl = new ILabel();
        insertedWhenLbl = new ILabel();
        updatedByLbl = new ILabel();
        updatedWhenLbl = new ILabel();

//        Action go = new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                int modelRow = Integer.valueOf(e.getActionCommand());
//                Order order = (Order) orderHistoryModel.getValueAt(modelRow, 1);
//                // Go to orders tab
//                application.setSelectedTab(Application.TAB_ORDERS);
//                // Select order
//                application.getOrderPanel().treeSelectOrder(order);
//                // Select order item
//                application.getOrderPanel().tableSelectOrderItem(order.findOrderItemInOrder(historyItem.getId()));
//            }
//        };
//
//        ITableEditors.ButtonEditor buttonEditor = new ITableEditors.ButtonEditor(orderHistoryTable, go, 3);
//        buttonEditor.setMnemonic(KeyEvent.VK_ENTER);

    }

    @Override
    public void initializeLayouts() {
        JScrollPane orderPane = new JScrollPane(orderHistoryTable);
        JScrollPane pcbPane = new JScrollPane(pcbHistoryTable);

        orderPane.setBorder(PanelUtils.createTitleBorder("Order history"));
        pcbPane.setBorder(PanelUtils.createTitleBorder("Pcb history"));

        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));

        // TODO: aud fields

        getContentPanel().add(orderPane);
        getContentPanel().add(pcbPane);

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
