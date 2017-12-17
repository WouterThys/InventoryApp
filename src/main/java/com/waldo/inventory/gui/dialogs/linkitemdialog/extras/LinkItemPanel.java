package com.waldo.inventory.gui.dialogs.linkitemdialog.extras;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.DbObjectSearcher;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.PackageType;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
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

import static com.waldo.inventory.managers.CacheManager.cache;

public class LinkItemPanel extends JPanel implements GuiInterface {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILinkItemTableModel tableModel;
    private ITable<PcbItemItemLink> itemTable;
    private IObjectSearchPanel<Item> searchPanel;

    private ITextField descriptionTf;
    private ITextField footprintTf;
    private ITextField setValueTf;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public LinkItemPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void addSearchListener(DbObjectSearcher.SearchListener<Item> listener) {
        searchPanel.addSearchListener(listener);
    }

    public void addListSelectionListener(ListSelectionListener listSelectionListener) {
        itemTable.getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    public void updateTable() {
        tableModel.updateTable();
    }

    public void setItemList(List<PcbItemItemLink> matches) {
        tableModel.setItemList(matches);
        updateSelectedValueData(null);
    }

    public PcbItemItemLink getSelectedItem() {
        int row = itemTable.getSelectedRow();
        if (row >=0) {
            return itemTable.getValueAtRow(row);
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
                if (item.getPackageTypeId() > DbObject.UNKNOWN_ID) {
                    PackageType pt = item.getPackageType();
                    String footprint = pt.getPackage() + "/" + pt;
                    footprintTf.setText(footprint);
                } else {
                    footprintTf.setText("");
                }
                if (item.getValue().hasValue()) {
                    setValueTf.setText(item.getValue().toString());
                } else {
                    setValueTf.setText("");
                }
            } else { // SetItem
                Item item = match.getItem();

                descriptionTf.setText(item.getDescription());
                if (item.getPackageTypeId() > DbObject.UNKNOWN_ID) {
                    PackageType pt = item.getPackageType();
                    String footprint = pt.getPackage() + "/" + pt;
                    footprintTf.setText(footprint);
                } else {
                    footprintTf.setText("");
                }
                if (match.getSetItem().getValue().hasValue()) {
                    setValueTf.setText(match.getSetItem().getValue().toString());
                } else {
                    setValueTf.setText("");
                }
            }
        } else {
            descriptionTf.clearText();
            footprintTf.clearText();
            setValueTf.clearText();
        }
    }

    private JPanel createSouthPanel() {
        JPanel southPanel = new JPanel(new GridBagLayout());

        GuiUtils.GridBagHelper gbh = new GuiUtils.GridBagHelper(southPanel);
        gbh.addLine("Description: ", descriptionTf);
        gbh.addLine("Footprint: ", footprintTf);
        gbh.addLine("Value: ", setValueTf);

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
        searchPanel = new IObjectSearchPanel<>(cache().getItems());
        searchPanel.setSearchList(new ArrayList<>(cache().getItems()));

        // Table
        tableModel = new ILinkItemTableModel();
        itemTable = new ITable<>(tableModel);
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
    public void updateComponents(Object... object) {

    }
}