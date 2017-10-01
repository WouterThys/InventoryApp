package com.waldo.inventory.gui.dialogs.linkitemdialog.extras;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.PcbItemItemLink;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.ILinkItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LinkItemPanel extends JPanel implements GuiInterface {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILinkItemTableModel tableModel;
    private ITable<PcbItemItemLink> itemTable;
    private IObjectSearchPanel searchPanel;

    private ITextField descriptionTf;
    private ITextField footprintTf;
    private ITextField setValueTf;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public LinkItemPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void addSearchListener(IObjectSearchPanel.IObjectSearchListener listener) {
        searchPanel.addSearchListener(listener);
    }

    public void addListSelectionListener(ListSelectionListener listSelectionListener) {
        itemTable.getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    public void setItemList(List<PcbItemItemLink> matches) {
        tableModel.setItemList(matches);
        updateSelectedValueData(null);
    }

    public PcbItemItemLink getSelectedItem() {
        int row = itemTable.getSelectedRow();
        if (row >=0) {
            return (PcbItemItemLink) itemTable.getValueAtRow(row);
        }
        return null;
    }

    public void clearItemList() {
        tableModel.clearItemList();
    }

    public void selectMatchItem(PcbItemItemLink matchedItem) {
        itemTable.selectItem(matchedItem);
    }

    public void updateSelectedValueData(PcbItemItemLink match) {
        if (match != null) {
            if (!match.isSetItem()) {
                Item item = match.getItem();

                descriptionTf.setText(item.getDescription());
                if (item.getDimensionType() != null) {
                    footprintTf.setText(item.getDimensionType().getName());
                } else {
                    if (item.getPackage() != null && item.getPackage().getPackageType() != null) {
                        footprintTf.setText(item.getPackage().getPackageType().getName());
                    } else {
                        footprintTf.setText("");
                    }
                }
                setValueTf.setText("");
            } else { // SetItem
                Item item = match.getItem();

                descriptionTf.setText(item.getDescription());
                if (item.getDimensionType() != null) {
                    footprintTf.setText(item.getDimensionType().getName());
                } else {
                    if (item.getPackage() != null && item.getPackage().getPackageType() != null) {
                        footprintTf.setText(item.getPackage().getPackageType().getName());
                    } else {
                        footprintTf.setText("");
                    }
                }
                setValueTf.setText(match.getSetItem().getValue().toString());
            }
        } else {
            descriptionTf.clearText();
            footprintTf.clearText();
            setValueTf.clearText();
        }
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
        southPanel.add(new ILabel("Description: ", ILabel.RIGHT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        southPanel.add(descriptionTf, gbc);

        // - FootPrint
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.EAST;
        southPanel.add(new ILabel("Foot print: ", ILabel.RIGHT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        southPanel.add(footprintTf, gbc);

        // - Value
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.EAST;
        southPanel.add(new ILabel("Value: ", ILabel.RIGHT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        southPanel.add(setValueTf, gbc);

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
        // Search panel
        searchPanel = new IObjectSearchPanel(false);
        searchPanel.setSearchList(new ArrayList<>(DbManager.db().getItems()));

        // Table
        tableModel = new ILinkItemTableModel();
        itemTable = new ITable<>(tableModel);
        itemTable.setDefaultRenderer(ILabel.class, new ITableEditors.AmountRenderer());
        itemTable.getColumnModel().getColumn(0).setMaxWidth(30);
        itemTable.getColumnModel().getColumn(2).setMaxWidth(30);
        itemTable.getColumnModel().getColumn(3).setMaxWidth(30);
        itemTable.getColumnModel().getColumn(4).setMaxWidth(30);
        itemTable.setOpaque(true);
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Item item = getSelectedItem().getItem();
                    if (item != null) {
                        EditItemDialog itemDialog = new EditItemDialog(application, "Edit " + item.getName(), item);
                        if (itemDialog.showDialog() == IDialog.OK) {
                            tableModel.updateTable();
                        }
                    }
                }
            }
        });

        // Text fields
        descriptionTf = new ITextField();
        footprintTf = new ITextField();
        setValueTf = new ITextField();

        descriptionTf.setEnabled(false);
        footprintTf.setEnabled(false);
        setValueTf.setEnabled(false);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Extras
        JPanel searchPnl = new JPanel(new BorderLayout());
        searchPnl.add(searchPanel, BorderLayout.EAST);

        JScrollPane pane = new JScrollPane(itemTable);
        pane.setPreferredSize(new Dimension(300, 300));

        // Add
        add(searchPnl, BorderLayout.NORTH);
        add(pane, BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object object) {

    }
}