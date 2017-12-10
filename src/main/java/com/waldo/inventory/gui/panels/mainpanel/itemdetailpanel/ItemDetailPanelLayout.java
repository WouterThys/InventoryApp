package com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ItemDetailPanelLayout extends JPanel implements GuiInterface {

    public interface OnItemDetailListener {
        void onShowDataSheet(Item item);
        void onShowDataSheet(Item item, boolean online);
        void onOrderItem(Item item);
        void onShowHistory(Item item);
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILabel iconLbl;

    ITextField nameTf;
    ITextArea divisionTa;
    ITextField manufacturerTf;
    ITextArea descriptionTa;

    IStarRater starRater;
    ICheckBox discourageOrderCb;
    ITextPane remarksTp;

    JButton dataSheetBtn;
    private JButton orderBtn;
    private JButton historyBtn;

    JPanel remarksPnl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;
    Item selectedItem;
    private OnItemDetailListener detailListener;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ItemDetailPanelLayout(Application application, OnItemDetailListener detailListener) {
        this.application = application;
        this.detailListener = detailListener;

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

    private JPanel createComponentInfoPanel() {
        JPanel componentPanel = new JPanel(new GridBagLayout());

        ILabel nameLabel = new ILabel("Name: ", ILabel.RIGHT);
        nameLabel.setStatusInfo("Item name");
        ILabel divisionLabel = new ILabel("Division: ", ILabel.RIGHT);
        divisionLabel.setStatusInfo("Item division");
        ILabel manufacturerLabel = new ILabel("Manufacturer: ", ILabel.RIGHT);
        manufacturerLabel.setStatusInfo("Item manufacturer");
        ILabel descriptionLabel = new ILabel("Description: ", ILabel.RIGHT);
        descriptionLabel.setStatusInfo("Item description");

        // Helping lists
        JComponent[] labels = new JComponent[] {nameLabel, divisionLabel, manufacturerLabel};
        JComponent[] fields = new JComponent[] {nameTf, new JScrollPane(divisionTa), manufacturerTf};

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        for (int i = 0; i < labels.length; i++) {
            //  - Label
            gbc.gridx = 0; gbc.weightx = 0;
            gbc.gridy = i; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            componentPanel.add(labels[i], gbc);
            //  - Field
            gbc.gridx = 1; gbc.weightx = 1;
            gbc.gridy = i; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            componentPanel.add(fields[i], gbc);
        }

        // Description
        //  - Label
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy++; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        componentPanel.add(descriptionLabel, gbc);
        //  - Field
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        componentPanel.add(new JScrollPane(descriptionTa), gbc);

        return componentPanel;
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
        iconLbl = new ILabel();
        iconLbl.setHorizontalAlignment(ILabel.CENTER);
        iconLbl.setVerticalAlignment(ILabel.CENTER);
        iconLbl.setPreferredSize(new Dimension(150,150));

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    EditItemDialog dialog = new EditItemDialog(application, "Edit item", selectedItem);
                    Object source = e.getSource();
                    if (source.equals(remarksTp)) {
                        dialog.showDialog(EditItemDialog.TAB_COMP_DETAILS, EditItemDialog.COMP_REMARK);
                    } else if (source.equals(descriptionTa)) {
                        dialog.showDialog(EditItemDialog.TAB_COMPONENTS, EditItemDialog.COMP_DESCRIPTION);
                    } else if (source.equals(nameTf)) {
                        dialog.showDialog(EditItemDialog.TAB_COMPONENTS, EditItemDialog.COMP_NAME);
                    } else if (source.equals(divisionTa)) {
                        dialog.showDialog(EditItemDialog.TAB_COMPONENTS, EditItemDialog.COMP_DIVISION);
                    } else if (source.equals(manufacturerTf)) {
                        dialog.showDialog(EditItemDialog.TAB_COMP_DETAILS, EditItemDialog.COMP_MANUFACTURER);
                    } else if (source.equals(starRater)) {
                        dialog.showDialog(EditItemDialog.TAB_COMP_DETAILS, EditItemDialog.COMP_RATING);
                    } else if (source.equals(discourageOrderCb)) {
                        dialog.showDialog(EditItemDialog.TAB_COMP_DETAILS, EditItemDialog.COMP_DISCOURAGE);
                    } else {
                        dialog.showDialog();
                    }
                }
            }
        };

        nameTf = new ITextField(false);
        nameTf.addMouseListener(mouseAdapter);
        divisionTa = new ITextArea(false);
        divisionTa.setLineWrap(true);
        divisionTa.setWrapStyleWord(true);
        divisionTa.addMouseListener(mouseAdapter);
        manufacturerTf = new ITextField(false);
        manufacturerTf.addMouseListener(mouseAdapter);
        descriptionTa = new ITextArea(false);
        descriptionTa.setLineWrap(true);
        descriptionTa.setWrapStyleWord(true);
        descriptionTa.addMouseListener(mouseAdapter);

        starRater = new IStarRater(5);
        starRater.setEnabled(false);
        starRater.addMouseListener(mouseAdapter);
        discourageOrderCb = new ICheckBox("Discourage order ");
        discourageOrderCb.setEnabled(false);
        discourageOrderCb.setHorizontalAlignment(SwingConstants.RIGHT);
        discourageOrderCb.addMouseListener(mouseAdapter);
        remarksTp = new ITextPane();
        remarksTp.setEditable(false);

        dataSheetBtn = new JButton(imageResource.readImage("Items.Buttons.Datasheet"));
        orderBtn = new JButton(imageResource.readImage("Items.Buttons.Order"));
        historyBtn = new JButton(imageResource.readImage("Items.Buttons.History"));

        dataSheetBtn.addActionListener(e -> detailListener.onShowDataSheet(selectedItem));
        orderBtn.addActionListener(e -> detailListener.onOrderItem(selectedItem));
        historyBtn.addActionListener(e -> detailListener.onShowHistory(selectedItem));

        dataSheetBtn.setToolTipText("Data sheets");
        orderBtn.setToolTipText("Order");
        historyBtn.setToolTipText("History");

        remarksPnl = new JPanel(new BorderLayout());
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel helper = new JPanel(new BorderLayout());
        helper.add(createComponentInfoPanel(), BorderLayout.CENTER);
        helper.add(createRemarksPanel(), BorderLayout.EAST);


        add(createIconPanel(), BorderLayout.WEST);
        add(helper, BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.EAST);
    }
}
