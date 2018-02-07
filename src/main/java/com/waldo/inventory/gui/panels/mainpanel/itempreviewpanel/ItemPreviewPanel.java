package com.waldo.inventory.gui.panels.mainpanel.itempreviewpanel;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.panels.mainpanel.AbstractDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.itemlisteners.ItemDetailListener;
import com.waldo.inventory.gui.panels.mainpanel.itemlisteners.OrderDetailListener;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public abstract class ItemPreviewPanel extends AbstractDetailPanel implements IdBToolBar.IdbToolBarListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel iconLbl;

    private ITextField nameTf;
    private ILabel aliasLbl;
    private ITextArea descriptionTa;
    private ITextField manufacturerTf;
    private ITextField footprintTf;
    private ITextField locationTf;
    private ITextField categoryTf;
    private ITextField productTf;
    private ITextField typeTf;
    private IStarRater starRater;
    private ITextPane remarksTp;

    private AbstractAction dataSheetAa;
    private AbstractAction orderAa;
    private AbstractAction historyAa;

    // Order
    private ITextField amountTf;
    private ITextField priceTf;
    private ITextField referenceTf;

    private IActions.EditAction editPriceAction;
    private IActions.PlusOneAction plusOneAction;
    private IActions.MinOneAction minOneAction;
    private IActions.EditAction editReferenceAction;

    private IdBToolBar dbToolbar;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Item selectedItem;
    private OrderItem selectedOrderItem;

    private final ItemDetailListener itemDetailListener;
    private final OrderDetailListener orderDetailListener;
    private final boolean isOrderType;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ItemPreviewPanel(ItemDetailListener itemDetailListener, OrderDetailListener orderDetailListener) {
        this.itemDetailListener = itemDetailListener;
        this.orderDetailListener = orderDetailListener;
        this.isOrderType = orderDetailListener != null;
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateToolbar(Item item) {
        aliasLbl.setText(item.getAlias());

        if (item.getOnlineDataSheet().isEmpty() && item.getLocalDataSheet().isEmpty()) {
            dataSheetAa.setEnabled(false);
        } else {
            dataSheetAa.setEnabled(true);
        }
    }

    private void updateHeader(Item item) {
        try {
            if (!item.getIconPath().isEmpty()) {
                Path path = Paths.get(settings().getFileSettings().getImgItemsPath(), item.getIconPath());
                iconLbl.setIcon(imageResource.readImage(path));
            } else {
                iconLbl.setIcon(imageResource.readImage("Items.Edit.Title"));
            }
        } catch (Exception e) {
            Status().setError("Failed to set item icon");
        }
        nameTf.setText(item.toString());
        descriptionTa.setText(item.getDescription());
        starRater.setRating(item.getRating());
    }

    private void updateData(Item item, OrderItem orderItem) {
        if (!isOrderType) {
            if (item.getCategoryId() > DbObject.UNKNOWN_ID) {
                categoryTf.setText(item.getCategory().toString());
            } else {
                categoryTf.setText("");
            }

            if (item.getProductId() > DbObject.UNKNOWN_ID) {
                productTf.setText(item.getProduct().toString());
            } else {
                productTf.setText("");
            }

            if (item.getTypeId() > DbObject.UNKNOWN_ID) {
                typeTf.setText(item.getType().toString());
            } else {
                typeTf.setText("");
            }
        } else {
            amountTf.setText(String.valueOf(orderItem.getAmount()));
            if (orderItem.getDistributorPartId() > DbObject.UNKNOWN_ID) {
                priceTf.setText(orderItem.getPrice().toString());
                referenceTf.setText(orderItem.getDistributorPartLink().getItemRef());
            }
            boolean locked = orderItem.isLocked();
            editPriceAction.setEnabled(!locked);
            editReferenceAction.setEnabled(!locked);
            plusOneAction.setEnabled(!locked);
            minOneAction.setEnabled(!locked);
        }

        if (item.getManufacturerId() > DbObject.UNKNOWN_ID) {
            manufacturerTf.setText(item.getManufacturer().toString());
        } else {
            manufacturerTf.setText("");
        }

        if (item.getPackageTypeId() > DbObject.UNKNOWN_ID) {
            footprintTf.setText(item.getPackageType().getPrettyString());
        } else {
            footprintTf.setText("");
        }

        if (item.getLocationId() > DbObject.UNKNOWN_ID) {
            locationTf.setText(item.getLocation().getPrettyString());
        } else {
            locationTf.setText("");
        }
    }

    private void updateRemarks(Item item) {
        remarksTp.setFile(item.getRemarksFile());
    }

    private JPanel createToolBarPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JToolBar eastTb = GuiUtils.createNewToolbar(dataSheetAa, orderAa, historyAa);

        panel.add(dbToolbar, BorderLayout.WEST);
        panel.add(aliasLbl, BorderLayout.CENTER);
        panel.add(eastTb, BorderLayout.EAST);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPnl = new JPanel(new BorderLayout());

        iconLbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(2,2,2,2)
        ));

        JPanel raterPnl = new JPanel();
        raterPnl.add(starRater);

        JScrollPane scrollPane = new JScrollPane(descriptionTa);
        scrollPane.setBorder(null);

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(headerPnl);
        // Label
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        headerPnl.add(iconLbl, gbc);

        // Name
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPnl.add(nameTf, gbc);

        // Name
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        headerPnl.add(scrollPane, gbc);

        // Rater
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPnl.add(raterPnl, gbc);

        return headerPnl;
    }

    private JPanel createDataPanel() {
        JPanel dataPnl = new JPanel();
        dataPnl.setLayout(new BoxLayout(dataPnl, BoxLayout.Y_AXIS));

        GuiUtils.GridBagHelper gbc;

        JPanel divisionPanel = new JPanel();
        divisionPanel.setBorder(BorderFactory.createEmptyBorder(1,1,8,1));
        gbc = new GuiUtils.GridBagHelper(divisionPanel, 0);
        if (!isOrderType) {
            gbc.addLine("Category", imageResource.readImage("Items.Tree.Category"), categoryTf);
            gbc.addLine("Product", imageResource.readImage("Items.Tree.Product"), productTf);
            gbc.addLine("Type", imageResource.readImage("Items.Tree.Type"), typeTf);
        } else {
            JPanel amountPnl = GuiUtils.createComponentWithActions(amountTf, plusOneAction, minOneAction);
            JPanel refPnl = GuiUtils.createComponentWithActions(referenceTf, editReferenceAction);
            JPanel pricePnl = GuiUtils.createComponentWithActions(priceTf, editPriceAction);
            gbc.addLine("Amount", imageResource.readImage("Preview.Amount"), amountPnl);
            gbc.addLine("Price", imageResource.readImage("Preview.Price"), pricePnl);
            gbc.addLine("Reference", imageResource.readImage("Actions.OrderReference"), refPnl);
        }

        JPanel infoPnl = new JPanel();
        infoPnl.setBorder(BorderFactory.createEmptyBorder(8,1,1,1));
        gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("Manufacturers", imageResource.readImage("Manufacturers.Menu"), manufacturerTf);
        gbc.addLine("Footprint", imageResource.readImage("Packages.Menu"), footprintTf);
        gbc.addLine("Location", imageResource.readImage("Locations.Menu"), locationTf);

        //dataPnl.add(createDivisionPanel());
        dataPnl.add(divisionPanel);
        dataPnl.add(infoPnl);

        return dataPnl;
    }

    private JPanel createRemarksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(remarksTp);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

     /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        // Label
        iconLbl = new ILabel();
        iconLbl.setBackground(Color.WHITE);
        iconLbl.setOpaque(true);
        iconLbl.setHorizontalAlignment(ILabel.CENTER);
        iconLbl.setVerticalAlignment(ILabel.CENTER);
        iconLbl.setPreferredSize(new Dimension(150,150));
        iconLbl.setMaximumSize(new Dimension(150,150));
        iconLbl.setMinimumSize(new Dimension(150,150));

        // Data
        nameTf = new ITextField(false);
        aliasLbl = new ILabel();
        aliasLbl.setHorizontalAlignment(ILabel.CENTER);
        aliasLbl.setVerticalAlignment(ILabel.CENTER);
        aliasLbl.setFont(20, Font.BOLD);
        manufacturerTf = new ITextField(false);
        footprintTf = new ITextField(false);
        locationTf = new ITextField(false);
        categoryTf = new ITextField(false);
        productTf = new ITextField(false);
        typeTf = new ITextField(false);
        descriptionTa = new ITextArea(false);
        descriptionTa.setBorder(nameTf.getBorder());
        descriptionTa.setEnabled(false);
        descriptionTa.setLineWrap(true);
        descriptionTa.setWrapStyleWord(true);

        amountTf = new ITextField(false);
        priceTf = new ITextField(false, 6);
        referenceTf = new ITextField(false);

        starRater = new IStarRater();
        starRater.setEnabled(false);

        remarksTp = new ITextPane();
        remarksTp.setEditable(false);
        remarksTp.setEnabled(false);

        dbToolbar = new IdBToolBar(this, false, false, true, true);

        // Actions
        dataSheetAa = new AbstractAction("Datasheet", imageResource.readImage("Items.Buttons.Datasheet")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null && itemDetailListener != null) {
                    itemDetailListener.onShowDataSheet(selectedItem);
                }
            }
        };
        dataSheetAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Data sheet");
        orderAa = new AbstractAction("Order", imageResource.readImage("Items.Buttons.Order")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null && itemDetailListener != null) {
                    itemDetailListener.onOrderItem(selectedItem);
                }
            }
        };
        orderAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Order");
        historyAa = new AbstractAction("History", imageResource.readImage("Items.Buttons.History")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null && itemDetailListener != null) {
                    itemDetailListener.onShowHistory(selectedItem);
                }
            }
        };
        historyAa.putValue(AbstractAction.SHORT_DESCRIPTION, "History");


        // Order
        plusOneAction = new IActions.PlusOneAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedOrderItem != null && orderDetailListener != null) {
                    int currentAmount = selectedOrderItem.getAmount();
                    orderDetailListener.onSetOrderItemAmount(selectedOrderItem, currentAmount + 1);
                    updateComponents(selectedOrderItem);
                }
            }
        };
        minOneAction = new IActions.MinOneAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedOrderItem != null && orderDetailListener != null) {
                    int currentAmount = selectedOrderItem.getAmount();
                    orderDetailListener.onSetOrderItemAmount(selectedOrderItem, currentAmount - 1);
                    updateComponents(selectedOrderItem);
                }
            }
        };
        editReferenceAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedOrderItem != null && orderDetailListener != null) {
                    orderDetailListener.onEditReference(selectedOrderItem);
                    updateComponents(selectedOrderItem);
                }
            }
        };
        editPriceAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedOrderItem != null && orderDetailListener != null) {
                    orderDetailListener.onEditPrice(selectedOrderItem);
                    updateComponents(selectedOrderItem);
                }
            }
        };
    }

    @Override
    public void initializeLayouts() {
        JPanel panel1 = new JPanel(new BorderLayout());
        JPanel panel2 = new JPanel(new BorderLayout());

        JPanel toolbarsPanel = createToolBarPanel();
        JPanel headerPanel = createHeaderPanel();
        JPanel dataPanel = createDataPanel();
        JPanel remarksPanel = createRemarksPanel();

        setLayout(new BorderLayout());

        panel1.add(headerPanel, BorderLayout.NORTH);
        panel1.add(dataPanel, BorderLayout.CENTER);

        panel2.add(toolbarsPanel, BorderLayout.PAGE_START);
        panel2.add(panel1, BorderLayout.CENTER);

        add(panel2, BorderLayout.NORTH);
        add(remarksPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length == 0 || args[0] == null) {
            setVisible(false);
            selectedItem = null;
            selectedOrderItem = null;
        } else {
            setVisible(true);
            if (args[0] instanceof Item) {
                selectedItem = (Item) args[0];
                selectedOrderItem = null;
            } else {
                selectedOrderItem = (OrderItem) args[0];
                selectedItem = selectedOrderItem.getItem();
            }
            updateToolbar(selectedItem);
            updateHeader(selectedItem);
            updateData(selectedItem, selectedOrderItem);
            updateRemarks(selectedItem);
        }
    }

    //
    // Toolbar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {

    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {

    }
}
