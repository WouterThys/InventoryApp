package com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel2;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.classes.Package;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ICheckBox;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.IStarRater;
import com.waldo.inventory.gui.components.ITextArea;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;
import com.waldo.inventory.gui.dialogs.historydialog.HistoryDialog;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ItemDetailPanel2 extends JPanel implements GuiInterface {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel iconLbl;

    private ILabel nameLbl;
    private JTree divisionTr;
    private ILabel manufacturerLbl;
    private ILabel descriptionLbl;
    private ILabel footprintLbl;
    private ILabel priceLbl;
    private ILabel locationLbl;

    private IStarRater starRater;
    private ICheckBox discourageOrderCb;
    private ITextArea  remarksTa;

    private AbstractAction dataSheetAa;
    private AbstractAction orderAa;
    private AbstractAction historyAa;

    private JPanel remarksPnl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;
    Item selectedItem;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ItemDetailPanel2(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private JPanel createIconPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(iconLbl, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }

    private void updateHeader(Item item) {
        // Icon
        try {
            Path path = Paths.get(settings().getFileSettings().getImgItemsPath(), item.getIconPath());
            iconLbl.setIcon(path.toString());
        } catch (Exception e) {
            Status().setError("Failed to set item icon");
        }

        nameLbl.setText(item.getName());
        updateTree(item);

        dataSheetAa.setEnabled(!item.getLocalDataSheet().isEmpty() || !item.getOnlineDataSheet().isEmpty());
    }

    private void updateData(Item item) {
        String description = "<html>" + item.getDescription() + "</html>";
        descriptionLbl.setText(description);

        if (item.getManufacturerId() > DbObject.UNKNOWN_ID) {
            manufacturerLbl.setText(item.getManufacturer().toString());
        } else {
            manufacturerLbl.setText("");
        }

        if (item.getPackageTypeId() > DbObject.UNKNOWN_ID) {
            PackageType packageType = item.getPackageType();
            Package itemPackage = packageType.getPackage();
            if (itemPackage != null) {
                footprintLbl.setText(packageType.toString() + " - " + itemPackage.toString());
            } else {
                footprintLbl.setText(packageType.toString());
            }
        } else {
            footprintLbl.setText("");
        }

        priceLbl.setText(String.valueOf(item.getPrice()));
        starRater.setRating(item.getRating());
        discourageOrderCb.setSelected(item.isDiscourageOrder());
        remarksTa.setText(item.getRemarks());

        if (item.getLocationId() > DbObject.UNKNOWN_ID) {
            Location l = SearchManager.sm().findLocationById(item.getLocationId());
            if (l != null && !l.isUnknown()) {
                if (item.isSet()) {
                    locationLbl.setText(l.getLocationType().getName().substring(0,3));
                } else {
                    locationLbl.setText(l.getPrettyString());
                }
            }
        } else {
            locationLbl.setText("");
        }

//        if (item.isSet()) {
//            setItemModel.setItemList(SearchManager.sm().findSetItemsByItemId(item.getId()));
//            setItemPanel.setVisible(true);
//        } else {
//            setItemPanel.setVisible(false);
//        }
    }

    private void updateTree(Item item) {
        DefaultMutableTreeNode root;
        if (item.getCategoryId() > DbObject.UNKNOWN_ID) {
            root = new DefaultMutableTreeNode(item.getCategory(), true);
            if (item.getProductId() > DbObject.UNKNOWN_ID) {
                DefaultMutableTreeNode pNode = new DefaultMutableTreeNode(item.getProduct(), true);
                if (item.getTypeId() > DbObject.UNKNOWN_ID) {
                    DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(item.getType(), false);
                    pNode.add(tNode);
                }
                root.add(pNode);
            }
        } else {
            root = new DefaultMutableTreeNode();
        }
        DefaultTreeModel model = new DefaultTreeModel(root);
        divisionTr.setModel(model);

        for(int i=0;i<divisionTr.getRowCount();++i){
            divisionTr.expandRow(i);
        }
    }

    private void openDataSheet(Item item) {
        if (item != null) {
            String local = item.getLocalDataSheet();
            String online = item.getOnlineDataSheet();
            if (local != null && !local.isEmpty() && online != null && !online.isEmpty()) {
                SelectDataSheetDialog.showDialog(application, online, local);
            } else if (local != null && !local.isEmpty()) {
                try {
                    OpenUtils.openPdf(local);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(application,
                            "Error opening the file: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else if (online != null && !online.isEmpty()) {
                try {
                    OpenUtils.browseLink(online);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(application,
                            "Error opening the file: " + e1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        }
    }

    private void orderItem(Item item) {
        int result = JOptionPane.YES_OPTION;
        if (item.isDiscourageOrder()) {
            result = JOptionPane.showConfirmDialog(
                    application,
                    "This item is marked to discourage new orders, \n do you really want to order it?",
                    "Discouraged to order",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
        }
        if (result == JOptionPane.YES_OPTION) {
            OrderItemDialog dialog = new OrderItemDialog(application, "Order " + item.getName(), item, true);
            dialog.showDialog();
        }
    }

    private void showHistory(Item item) {
        HistoryDialog dialog = new HistoryDialog(application, item);
        dialog.showDialog();
    }

    private JPanel createComponentInfoPanel() {
        JPanel componentPanel = new JPanel();
        JPanel leftDataRowsPnl = new JPanel();
        JPanel rightDataRowsPnl = new JPanel();

        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(leftDataRowsPnl);
        gbc.addLine("", nameLbl);
        gbc.addLine("", descriptionLbl);
        gbc.addLine(imageResource.readImage(""), divisionTr);

        gbc = new PanelUtils.GridBagHelper(rightDataRowsPnl);
        gbc.addLine(imageResource.readImage("Items.Preview.Manufacturer"), manufacturerLbl);
        gbc.addLine(imageResource.readImage("Items.Preview.Footprint"), footprintLbl);
        gbc.addLine(imageResource.readImage("Items.Preview.Price"), priceLbl);
        gbc.addLine(imageResource.readImage("Items.Preview.Location"), locationLbl);

        componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.X_AXIS));
        componentPanel.add(leftDataRowsPnl);
        componentPanel.add(rightDataRowsPnl);

        return componentPanel;
    }

    private JToolBar createToolbar() {
        JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);

        toolBar.add(dataSheetAa);
        toolBar.add(orderAa);
        toolBar.add(historyAa);

        toolBar.setBorderPainted(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

        return toolBar;
    }

    private JPanel createRemarksPanel() {
        remarksPnl = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel(new BorderLayout());

        northPanel.add(starRater, BorderLayout.WEST);
        northPanel.add(discourageOrderCb, BorderLayout.EAST);

        remarksPnl.add(northPanel, BorderLayout.NORTH);
        remarksPnl.add(new JScrollPane(remarksTa), BorderLayout.CENTER);
        remarksPnl.setBorder(BorderFactory.createEmptyBorder(5,10,2,10));

        return remarksPnl;

    }


     /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        // Label
        iconLbl = new ILabel();
        iconLbl.setHorizontalAlignment(ILabel.CENTER);
        iconLbl.setVerticalAlignment(ILabel.CENTER);
        iconLbl.setPreferredSize(new Dimension(150,150));

        // Data
        nameLbl = new ILabel("", ILabel.CENTER);
        nameLbl.setFont(20, Font.BOLD);

        divisionTr = new JTree();
        divisionTr.setEnabled(false);
        divisionTr.setOpaque(false);

        descriptionLbl = new ILabel();
        manufacturerLbl = new ILabel();
        footprintLbl = new ILabel();
        priceLbl = new ILabel();
        locationLbl = new ILabel();
        starRater = new IStarRater();
        starRater.setEnabled(false);
        discourageOrderCb = new ICheckBox("Discourage order ");
        discourageOrderCb.setEnabled(false);
        remarksTa = new ITextArea(false);
        remarksTa.setLineWrap(true);
        remarksTa.setWrapStyleWord(true);
        remarksTa.setOpaque(false);

        // Actions
        dataSheetAa = new AbstractAction("Datasheet", imageResource.readImage("Items.Buttons.Datasheet")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null) {
                    openDataSheet(selectedItem);
                }
            }
        };
        dataSheetAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Data sheet");
        orderAa = new AbstractAction("Order", imageResource.readImage("Items.Buttons.Order")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null) {
                    orderItem(selectedItem);
                }
            }
        };
        orderAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Order");
        historyAa = new AbstractAction("History", imageResource.readImage("Items.Buttons.History")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null) {
                    showHistory(selectedItem);
                }
            }
        };
        historyAa.putValue(AbstractAction.SHORT_DESCRIPTION, "History");
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.add(createComponentInfoPanel(), BorderLayout.CENTER);
        //dataPanel.add(createRemarksPanel(), BorderLayout.EAST);

        add(createIconPanel(), BorderLayout.WEST);
        add(dataPanel, BorderLayout.CENTER);
        add(createToolbar(), BorderLayout.EAST);
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length == 0 || object[0] == null) {
            setVisible(false);
            selectedItem = null;
        } else {

                setVisible(true);

                selectedItem = (Item) object[0];

                updateHeader(selectedItem);
                updateData(selectedItem);

        }
    }
}
