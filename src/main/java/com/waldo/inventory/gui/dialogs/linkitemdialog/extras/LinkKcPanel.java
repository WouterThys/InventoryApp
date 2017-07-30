package com.waldo.inventory.gui.dialogs.linkitemdialog.extras;

import com.waldo.inventory.Utils.parser.KiCad.KcComponent;
import com.waldo.inventory.Utils.parser.KiCad.KiCadParser;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.ILinkKiCadTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class LinkKcPanel extends JPanel implements GuiInterface {    
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILinkKiCadTableModel tableModel;
    private ITable itemTable;

    private ITextField referencesTf;
    private ITextField footprintTf;

    private JToggleButton sortByRefBtn;
    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;
    private KiCadParser kiCadParser;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public LinkKcPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void addListSelectionListener(ListSelectionListener listSelectionListener) {
        itemTable.getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    public void updateSelectedValueData(KcComponent component) {
        if (component != null) {
            referencesTf.setText(component.getReferenceString());
            footprintTf.setText(component.getFootprint());
        } else {
            referencesTf.clearText();
            footprintTf.clearText();
        }
    }

    public KcComponent getSelectedComponent() {
        int row = itemTable.getSelectedRow();
        if (row >=0) {
            return (KcComponent) itemTable.getValueAtRow(row);
        }
        return null;
    }

    public void updateTable() {
        tableModel.updateTable();
    }

    private JPanel createSouthPanel() {
        JPanel southPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // - Description
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.EAST;
        southPanel.add(new ILabel("References: ", ILabel.RIGHT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        southPanel.add(referencesTf, gbc);

        // - FootPrint
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.EAST;
        southPanel.add(new ILabel("Foot print: ", ILabel.RIGHT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        southPanel.add(footprintTf, gbc);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 1),
                BorderFactory.createEmptyBorder(2,5,2,5)
        ));

        return southPanel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        tableModel = new ILinkKiCadTableModel();
        itemTable = new ITable(tableModel);
        itemTable.setDefaultRenderer(ILabel.class, new ITableEditors.KcMatchRenderer());
        itemTable.getColumnModel().getColumn(0).setMaxWidth(30);
        itemTable.getColumnModel().getColumn(3).setMaxWidth(30);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Text fields
        referencesTf = new ITextField();
        footprintTf = new ITextField();

        referencesTf.setEnabled(false);
        footprintTf.setEnabled(false);

        // Button
        sortByRefBtn = new JToggleButton("Order by reference ");
        sortByRefBtn.setSelected(true);
        sortByRefBtn.addActionListener(e -> {
            if (kiCadParser != null) {
                if (sortByRefBtn.isSelected()) {
                    tableModel.setItemList(kiCadParser.sortList(kiCadParser.getParsedData()));
                } else {
                    tableModel.setItemList(kiCadParser.getParsedData());
                }
            }
        });
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Extras
        JScrollPane pane = new JScrollPane(itemTable);
        pane.setPreferredSize(new Dimension(400, 300));
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(sortByRefBtn, BorderLayout.WEST);

        // Add
        add(northPanel, BorderLayout.NORTH);
        add(pane, BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            kiCadParser = (KiCadParser) object;
            tableModel.setItemList(kiCadParser.sortList(kiCadParser.getParsedData()));
        } else {
            tableModel.clearItemList();
        }
    }
}