package com.waldo.inventory.gui.dialogs.historydialog;

import com.waldo.inventory.Utils.DateUtils;
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

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class HistoryDialogLayout extends IDialog implements GuiInterface {

    private JScrollPane orderPane;
    private JScrollPane pcbPane;

    private IOrderHistoryTableModel orderHistoryModel;
    private ITable<Order> orderHistoryTable;

    private IPcbHistoryTableModel pcbHistoryModel;
    private ITable<ProjectPcb> pcbHistoryTable;

    private ILabel insertedByLbl;
    private ILabel insertedWhenLbl;

    private ILabel updatedByLbl;
    private ILabel updatedWhenLbl;

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
        orderHistoryModel.clearItemList();
        pcbHistoryModel.clearItemList();



        //orderHistoryModel.addItems(toAdd);

//        // Find orders


        // Find projects
//        pcbHistoryList.addAll(SearchManager.sm().findPcbsForItem(item.getId()));
//        pcbHistoryModel.setItemList(pcbHistoryList);
//        pcbPane.setVisible(pcbHistoryList.size() > 0);

        // Labels
        insertedByLbl.setText(item.getAud().getInsertedBy());
        insertedWhenLbl.setText(DateUtils.formatDateTime(item.getAud().getInsertedDate()));
        updatedByLbl.setText(item.getAud().getUpdatedBy());
        updatedWhenLbl.setText(DateUtils.formatDateTime(item.getAud().getUpdatedDate()));
    }

    private JPanel createLabelPanel() {
        JPanel panel = new JPanel();
        JPanel left = new JPanel();
        JPanel right = new JPanel();

        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(left);
        gbc.addLine("Inserted by: ", insertedByLbl);
        gbc.addLine("Date: ", insertedWhenLbl);

        gbc = new PanelUtils.GridBagHelper(right);
        gbc.addLine("Updated by: ", updatedByLbl);
        gbc.addLine("Date: ", updatedWhenLbl);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(left);
        panel.add(right);

        return panel;
    }

    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("History.Title"));
        setTitleName(getTitle());
        setResizable(true);


        Order o1 = new Order("Order 1 ");
        Order o2 = new Order("Order 2 ");
        Order o3 = new Order("Order 3 ");

        List<Order> toAdd = new ArrayList<>();
        toAdd.add(o1);
        toAdd.add(o2);
        toAdd.add(o3);

        // This
        orderHistoryModel = new IOrderHistoryTableModel(toAdd);
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
        JPanel labelPanel = createLabelPanel();
        JPanel tablePanel = new JPanel(new BorderLayout());
        orderPane = new JScrollPane(orderHistoryTable);
        pcbPane = new JScrollPane(pcbHistoryTable);

        labelPanel.setBorder(PanelUtils.createTitleBorder("AUD"));
        orderPane.setBorder(PanelUtils.createTitleBorder("Order history"));
        pcbPane.setBorder(PanelUtils.createTitleBorder("Pcb history"));

        tablePanel.add(orderPane, BorderLayout.CENTER);
        tablePanel.add(pcbPane, BorderLayout.SOUTH);

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(labelPanel, BorderLayout.NORTH);
        getContentPanel().add(tablePanel, BorderLayout.CENTER);

        pack();

    }

    @Override
    public void updateComponents(Object object) {

        if (object != null) {
            Item historyItem = (Item) object;

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
