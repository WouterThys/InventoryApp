package com.waldo.inventory.gui.panels.mainpanel.preview.itemdetailpanel;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.ItemOrderLine;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.panels.mainpanel.AbstractDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.ItemDetailListener;
import com.waldo.inventory.gui.panels.mainpanel.OrderDetailListener;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ItemDetailPanelLayout extends AbstractDetailPanel {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
   // Item
    private ILabel iconLbl;
    ITextField nameTf;
    ITextField descriptionTa;
    private ITextField categoryTf;
    private ITextField productTf;
    private ITextField typeTf;
    ITextField manufacturerTf;
    ITextField footprintTf;
    ITextField locationTf;
    IStarRater starRater;
    ICheckBox discourageOrderCb;
    ITextPane remarksTp;

    // ItemOrder
    ITextField amountTf;
    ITextField priceTf;
    ITextField referenceTf;

    IActions.EditAction editPriceAction;
    IActions.PlusOneAction plusOneAction;
    IActions.MinOneAction minOneAction;
    IActions.EditAction editReferenceAction;

    JButton dataSheetBtn;
    private JButton orderBtn;
    private JButton historyBtn;
    private JPanel remarksPnl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Item selectedItem;
    ItemOrderLine selectedItemOrderLine;

    private final ItemDetailListener itemDetailListener;
    private final OrderDetailListener orderDetailListener;
    final boolean isOrderType;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ItemDetailPanelLayout(ItemDetailListener itemDetailListener, OrderDetailListener orderDetailListener) {
        this.itemDetailListener = itemDetailListener;
        this.orderDetailListener = orderDetailListener;
        this.isOrderType = orderDetailListener != null;

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private JPanel createIconPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        iconLbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(2,2,2,2)
        ));

        panel.add(iconLbl, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        return panel;
    }

    private JPanel createIdentificationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(descriptionTa);
        scrollPane.setBorder(null);

        panel.add(nameTf, BorderLayout.NORTH);
        panel.add(descriptionTa, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createItemDetailPanel() {
        JPanel divisionPanel = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(divisionPanel, 0);
        gbc.addLine("Category", imageResource.readIcon("Component.Green.SS"), categoryTf);
        gbc.addLine("Product", imageResource.readIcon("Component.Yellow.SS"), productTf);
        gbc.addLine("Type", imageResource.readIcon("Component.Red.SS"), typeTf);

        return divisionPanel;
    }

    private JPanel createOrderDetailPanel() {
        JPanel infoPnl = new JPanel();

        JPanel amountPnl = GuiUtils.createComponentWithActions(amountTf, plusOneAction, minOneAction);
        JPanel refPnl = GuiUtils.createComponentWithActions(referenceTf, editReferenceAction);
        JPanel pricePnl = GuiUtils.createComponentWithActions(priceTf, editPriceAction);

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("Amount", imageResource.readIcon("Amount.S"), amountPnl);
        gbc.addLine("Price", imageResource.readIcon("Value.S"), pricePnl);
        gbc.addLine("Reference", imageResource.readIcon("Distributor.SS"), refPnl);

        return infoPnl;
    }

    private JPanel createDataPanel() {
        JPanel dataPnl = new JPanel();
        dataPnl.setLayout(new BoxLayout(dataPnl, BoxLayout.X_AXIS));

        GuiUtils.GridBagHelper gbc;
        JPanel sharePnl = new JPanel();
        gbc = new GuiUtils.GridBagHelper(sharePnl);
        gbc.addLine("Manufacturers", imageResource.readIcon("Factory.SS"), manufacturerTf);
        gbc.addLine("Footprint", imageResource.readIcon("Chip.SS"), footprintTf);
        gbc.addLine("Location", imageResource.readIcon("Location.SS"), locationTf);

        JPanel infoPnl;
        if (isOrderType) {
            infoPnl = createOrderDetailPanel();
        } else {
            infoPnl = createItemDetailPanel();
        }
        infoPnl.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));

        //dataPnl.add(createDivisionPanel());
        dataPnl.add(sharePnl);
        dataPnl.add(infoPnl);

        return dataPnl;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        buttonsPanel.add(dataSheetBtn, gbc);

        gbc.gridy++;
        buttonsPanel.add(orderBtn, gbc);

        gbc.gridy++;
        buttonsPanel.add(historyBtn, gbc);

        return buttonsPanel;
    }

    private JPanel createRemarksPanel() {
        remarksPnl = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel(new BorderLayout());

        northPanel.add(starRater, BorderLayout.WEST);
        northPanel.add(discourageOrderCb, BorderLayout.EAST);

        remarksPnl.add(northPanel, BorderLayout.NORTH);
        remarksPnl.add(new JScrollPane(remarksTp), BorderLayout.CENTER);
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
        iconLbl.setBackground(Color.WHITE);
        iconLbl.setOpaque(true);
        iconLbl.setHorizontalAlignment(ILabel.CENTER);
        iconLbl.setVerticalAlignment(ILabel.CENTER);
        iconLbl.setPreferredSize(new Dimension(150,150));
        iconLbl.setMaximumSize(new Dimension(150,150));
        iconLbl.setMinimumSize(new Dimension(150,150));

        nameTf = new ITextField(false);
        manufacturerTf = new ITextField(false);
        footprintTf = new ITextField(false);
        locationTf = new ITextField(false);
        categoryTf = new ITextField(false);
        productTf = new ITextField(false);
        typeTf = new ITextField(false);

        amountTf = new ITextField(false);
        priceTf = new ITextField(false, 6);
        referenceTf = new ITextField(false);

        descriptionTa = new ITextField(false);

        starRater = new IStarRater(5);
        starRater.setEnabled(false);
        discourageOrderCb = new ICheckBox("Discourage order ");
        discourageOrderCb.setEnabled(false);
        discourageOrderCb.setHorizontalAlignment(SwingConstants.RIGHT);
        remarksTp = new ITextPane();
        remarksTp.setEditable(false);
        //remarksTp.setEnabled(false);

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

        dataSheetBtn = new JButton(imageResource.readIcon("Datasheet.M"));
        orderBtn = new JButton(imageResource.readIcon("Order.M"));
        historyBtn = new JButton(imageResource.readIcon("History.M"));

        dataSheetBtn.addActionListener(e -> itemDetailListener.onShowDataSheet(selectedItem));
        orderBtn.addActionListener(e -> itemDetailListener.onOrderItem(selectedItem));
        historyBtn.addActionListener(e -> itemDetailListener.onShowHistory(selectedItem));

        dataSheetBtn.setToolTipText("Data sheets");
        orderBtn.setToolTipText("ItemOrder");
        historyBtn.setToolTipText("History");

        remarksPnl = new JPanel(new BorderLayout());
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel idPanel = createIdentificationPanel();
        JPanel infoPanel = createDataPanel();
        JPanel remarksPanel = createRemarksPanel();

        JPanel panel1 = new JPanel(new BorderLayout());
        JPanel panel2 = new JPanel(new BorderLayout());

        panel1.add(idPanel, BorderLayout.NORTH);
        panel1.add(infoPanel, BorderLayout.CENTER);

        panel2.add(panel1, BorderLayout.CENTER);
        panel2.add(remarksPanel, BorderLayout.EAST);

        add(createIconPanel(), BorderLayout.WEST);
        add(panel2, BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.EAST);
    }
}
