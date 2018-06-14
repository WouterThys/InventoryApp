package com.waldo.inventory.gui.panels.orderpanel.preview;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.IImagePanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.panels.mainpanel.AbstractDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.ItemDetailListener;
import com.waldo.inventory.gui.panels.mainpanel.OrderDetailListener;
import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OrderItemPreviewPanel extends AbstractDetailPanel implements IdBToolBar.IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IImagePanel imagePanel;
    private ITextField nameTf;
    private ILabel aliasLbl;
    private ITextArea descriptionTa;
    private ITextField manufacturerTf;
    private ITextField footprintTf;
    private ITextField locationTf;
    private IStarRater starRater;
    private ITextPane remarksTp;

    private AbstractAction dataSheetAa;
    private AbstractAction orderAa;
    private AbstractAction historyAa;

    // ItemOrder
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
    private ItemOrderLine selectedItemOrderLine;
    private final ItemDetailListener itemDetailListener;
    private final OrderDetailListener orderDetailListener;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected OrderItemPreviewPanel(OrderDetailListener orderDetailListener, ItemDetailListener itemDetailListener) {
        this.orderDetailListener = orderDetailListener;
        this.itemDetailListener = itemDetailListener;
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateToolbar(DbObject object) {
        if (object != null) {
            if (object instanceof Item) {
                Item item = (Item) object;
                aliasLbl.setText(item.getAlias());

                if (item.getOnlineDataSheet().isEmpty() && item.getLocalDataSheet().isEmpty()) {
                    dataSheetAa.setEnabled(false);
                } else {
                    dataSheetAa.setEnabled(true);
                }
            } else if (object instanceof ItemOrderLine) {
                aliasLbl.setText("");
                dataSheetAa.setEnabled(false);
            }
        }
    }

    private void updateHeader(AbstractOrderLine orderLine) {
        if (orderLine != null) {
                Orderable line = orderLine.getLine();
                if (line != null && line instanceof Item) {
                    Item item = (Item) line;
                        if (item.getIconPath().isEmpty()) {
                            imagePanel.setImage(imageResource.getDefaultImage(ImageType.ItemImage));
                        } else {
                            imagePanel.setImage(item.getIconPath());
                        }
                        nameTf.setText(item.toString());
                        descriptionTa.setText(item.getDescription());
                        starRater.setRating(item.getRating());

                } else {
                    imagePanel.setImage(imageResource.getDefaultImage(ImageType.ItemImage));
                    nameTf.setText("");
                    descriptionTa.setText("");
                    starRater.setRating(0);
                }

        }
    }

    private void updateData(AbstractOrderLine orderLine) {
        if (orderLine != null) {
            amountTf.setText(String.valueOf(orderLine.getAmount()));
            if (orderLine.getDistributorPartId() > DbObject.UNKNOWN_ID) {
                priceTf.setText(orderLine.getPrice().toString());
                referenceTf.setText(orderLine.getDistributorPartLink().getReference());
            } else {
                priceTf.setText("");
                referenceTf.setText("");
            }
            boolean locked = orderLine.isLocked();
            editPriceAction.setEnabled(!locked);
            editReferenceAction.setEnabled(!locked);
            plusOneAction.setEnabled(!locked);
            minOneAction.setEnabled(!locked);

            Orderable line = orderLine.getLine(); // TODO: same for PCB
            if (line != null && line instanceof Item) {
                Item item = (Item) line;
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
            } else {
                manufacturerTf.setText("");
                footprintTf.setText("");
                locationTf.setText("");
            }
        } else {
            imagePanel.setImage(imageResource.getDefaultImage(ImageType.ItemImage));
            nameTf.setText("");
            aliasLbl.setText("");
            descriptionTa.setText("");
            manufacturerTf.setText("");
            footprintTf.setText("");
            locationTf.setText("");
            starRater.setRating(0);
            remarksTp.setText("");
            amountTf.setText("");
            priceTf.setText("");
            referenceTf.setText("");
        }

    }

    private void updateRemarks(AbstractOrderLine orderLine) {
        if (orderLine != null) {
                Orderable item = orderLine.getLine();
                if (item != null) {
                    remarksTp.setFile(item.getRemarksFile());
                } else {
                    remarksTp.setFile(null);
                }
        } else {
            remarksTp.setFile(null);
        }
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

        JPanel raterPnl = new JPanel();
        raterPnl.add(starRater);

        JScrollPane scrollPane = new JScrollPane(descriptionTa);
        scrollPane.setBorder(null);

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(headerPnl);
        // Label
        gbc.gridx = 0;gbc.weightx = 0;
        gbc.gridy = 0;gbc.weighty = 0;
        gbc.gridwidth = 1;gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        headerPnl.add(imagePanel, gbc);

        // Name
        gbc.gridx = 1;gbc.weightx = 1;
        gbc.gridy = 0;gbc.weighty = 0;
        gbc.gridwidth = 1;gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPnl.add(nameTf, gbc);

        // Name
        gbc.gridx = 1;gbc.weightx = 1;
        gbc.gridy = 1;gbc.weighty = 1;
        gbc.gridwidth = 1;gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        headerPnl.add(scrollPane, gbc);

        // Rater
        gbc.gridx = 0;gbc.weightx = 1;
        gbc.gridy = 2;gbc.weighty = 0;
        gbc.gridwidth = 2;gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPnl.add(raterPnl, gbc);

        return headerPnl;
    }

    private JPanel createDataPanel() {
        JPanel dataPnl = new JPanel();
        dataPnl.setLayout(new BoxLayout(dataPnl, BoxLayout.Y_AXIS));

        GuiUtils.GridBagHelper gbc;
        JPanel orderInfoPnl = new JPanel();

        gbc = new GuiUtils.GridBagHelper(orderInfoPnl, 0);
        JPanel amountPnl = GuiUtils.createComponentWithActions(amountTf, plusOneAction, minOneAction);
        JPanel refPnl = GuiUtils.createComponentWithActions(referenceTf, editReferenceAction);
        JPanel pricePnl = GuiUtils.createComponentWithActions(priceTf, editPriceAction);
        gbc.addLine("Amount", imageResource.readIcon("Preview.Amount"), amountPnl);
        gbc.addLine("Price", imageResource.readIcon("Preview.Price"), pricePnl);
        gbc.addLine("Reference", imageResource.readIcon("Actions.OrderReference"), refPnl);

        JPanel infoPnl = new JPanel();
        infoPnl.setBorder(BorderFactory.createEmptyBorder(8, 1, 1, 1));
        gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("Manufacturers", imageResource.readIcon("Manufacturers.Menu"), manufacturerTf);
        gbc.addLine("Footprint", imageResource.readIcon("Packages.Menu"), footprintTf);
        gbc.addLine("Location", imageResource.readIcon("Locations.Menu"), locationTf);

        dataPnl.add(orderInfoPnl);
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
        // Image
        imagePanel = new IImagePanel(ImageType.ItemImage, new Dimension(150, 150));

        // Data
        nameTf = new ITextField(false);
        aliasLbl = new ILabel();
        aliasLbl.setHorizontalAlignment(ILabel.CENTER);
        aliasLbl.setVerticalAlignment(ILabel.CENTER);
        aliasLbl.setFont(20, Font.BOLD);
        manufacturerTf = new ITextField(false);
        footprintTf = new ITextField(false);
        locationTf = new ITextField(false);
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
        dataSheetAa = new AbstractAction("Datasheet", imageResource.readIcon("Items.Buttons.Datasheet")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItemOrderLine != null && itemDetailListener != null) {
                    if (selectedItemOrderLine.getLine() != null) {
                        // TODO itemDetailListener.onShowDataSheet(selectedItemOrderLine.getItem());
                    }
                }
            }
        };
        dataSheetAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Data sheet");
        orderAa = new AbstractAction("ItemOrder", imageResource.readIcon("Items.Buttons.Order")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItemOrderLine != null && itemDetailListener != null) {
                    if (selectedItemOrderLine.getLine() != null) {
                        // TODO itemDetailListener.onOrderItem(selectedItemOrderLine.getItem());
                    }
                }
            }
        };
        orderAa.putValue(AbstractAction.SHORT_DESCRIPTION, "ItemOrder");
        historyAa = new AbstractAction("History", imageResource.readIcon("Items.Buttons.History")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItemOrderLine != null && itemDetailListener != null) {
                    if (selectedItemOrderLine.getLine() != null) {
                        // TODO itemDetailListener.onShowHistory(selectedItemOrderLine.getItem());
                    }
                }
            }
        };
        historyAa.putValue(AbstractAction.SHORT_DESCRIPTION, "History");


        // ItemOrder
        plusOneAction = new IActions.PlusOneAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItemOrderLine != null && orderDetailListener != null) {
                    int currentAmount = selectedItemOrderLine.getAmount();
                    orderDetailListener.onSetOrderItemAmount(selectedItemOrderLine, currentAmount + 1);
                    updateComponents(selectedItemOrderLine);
                }
            }
        };
        minOneAction = new IActions.MinOneAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItemOrderLine != null && orderDetailListener != null) {
                    int currentAmount = selectedItemOrderLine.getAmount();
                    orderDetailListener.onSetOrderItemAmount(selectedItemOrderLine, currentAmount - 1);
                    updateComponents(selectedItemOrderLine);
                }
            }
        };
        editReferenceAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItemOrderLine != null && orderDetailListener != null) {
                    orderDetailListener.onEditReference(selectedItemOrderLine);
                    updateComponents(selectedItemOrderLine);
                }
            }
        };
        editPriceAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItemOrderLine != null && orderDetailListener != null) {
                    orderDetailListener.onEditPrice(selectedItemOrderLine);
                    updateComponents(selectedItemOrderLine);
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
            selectedItemOrderLine = null;
        } else {
            setVisible(true);

            selectedItemOrderLine = (ItemOrderLine) args[0];
            updateToolbar(selectedItemOrderLine);
            updateHeader(selectedItemOrderLine);
            updateRemarks(selectedItemOrderLine);

            updateData(selectedItemOrderLine);
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

