package com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel2;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ItemDetailPanelLayout2 extends JPanel implements GuiInterface {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILabel iconLbl;

    ILabel nameTf;
    JTree divisionTr;
    ILabel manufacturerTf;
    ILabel descriptionTa;

    IStarRater starRater;
    ICheckBox discourageOrderCb;
    ITextArea  remarksTa;

    JButton dataSheetBtn;
    JButton orderBtn;
    JButton historyBtn;

    JPanel remarksPnl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;
    Item selectedItem;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ItemDetailPanelLayout2(Application application) {
        this.application = application;
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

        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(componentPanel);
        gbc.addLine("", nameTf);
        gbc.addLine("", divisionTr);
        gbc.addLine(imageResource.readImage("Items.Preview.Manufacturer"), manufacturerTf);
        gbc.addLine("", descriptionTa, PanelUtils.GridBagHelper.BOTH);

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
        remarksPnl.add(new JScrollPane(remarksTa), BorderLayout.CENTER);
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

        nameTf = new ILabel();
        nameTf.setFont(20, Font.BOLD);
        divisionTr = new JTree();
        divisionTr.setEnabled(false);
        divisionTr.setOpaque(false);
        manufacturerTf = new ILabel();
        descriptionTa = new ILabel();

        starRater = new IStarRater(5);
        starRater.setEnabled(false);
        discourageOrderCb = new ICheckBox("Discourage order ");
        discourageOrderCb.setEnabled(false);
        discourageOrderCb.setHorizontalAlignment(SwingConstants.RIGHT);
        remarksTa = new ITextArea(false);
        remarksTa.setLineWrap(true);
        remarksTa.setWrapStyleWord(true);

        dataSheetBtn = new JButton(imageResource.readImage("Items.Buttons.Datasheet"));
        orderBtn = new JButton(imageResource.readImage("Items.Buttons.Order"));
        historyBtn = new JButton(imageResource.readImage("Items.Buttons.History"));

        dataSheetBtn.setToolTipText("Data sheets");
        orderBtn.setToolTipText("Order");
        historyBtn.setToolTipText("History");

        remarksPnl = new JPanel(new BorderLayout());
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel comp = new JPanel(new BorderLayout());
        comp.add(createComponentInfoPanel(), BorderLayout.NORTH);

        JPanel helper = new JPanel(new BorderLayout());
        helper.add(comp, BorderLayout.CENTER);
        helper.add(createRemarksPanel(), BorderLayout.EAST);

        add(createIconPanel(), BorderLayout.WEST);
        add(helper, BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.EAST);
    }
}
