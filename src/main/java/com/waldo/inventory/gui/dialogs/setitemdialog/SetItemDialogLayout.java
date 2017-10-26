package com.waldo.inventory.gui.dialogs.setitemdialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.ISetItemTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class SetItemDialogLayout extends IDialog implements
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ISetItemTableModel tableModel;
    ITable setItemTable;

    private IdBToolBar toolBar;
    JButton useKnownBtn;
    private JButton locateBtn;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Item item;

    SetItem selectedSetItem;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SetItemDialogLayout(Application application, String title, Item item) {
        super(application, title);
        setResizable(true);
        this.item = item;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        if (selectedSetItem == null) {
            toolBar.setEditActionEnabled(false);
            toolBar.setDeleteActionEnabled(false);
        } else {
            toolBar.setEditActionEnabled(true);
            toolBar.setDeleteActionEnabled(true);
        }
    }

    void updateTable() {
        java.util.List<SetItem> list = SearchManager.sm().findSetItemsByItemId(item.getId());
        list.sort(new SetItem.SetItemComparator());
        tableModel.setItemList(list);
    }

    java.util.List<SetItem> getSetItems() {
        return tableModel.getItemList();
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("SetItem.Title"));
        setTitleName("Set items");
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Table
        tableModel = new ISetItemTableModel(new SetItem.SetItemComparator());
        setItemTable = new ITable<>(tableModel);
        setItemTable.getSelectionModel().addListSelectionListener(this);
        setItemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);
        setItemTable.setDefaultRenderer(ILabel.class, new ITableEditors.AmountRenderer());

        // Tool bar
        toolBar = new IdBToolBar(this, IdBToolBar.VERTICAL);

        // Buttons
        useKnownBtn = new JButton("Add series");
        useKnownBtn.addActionListener(this);

        locateBtn = new JButton("Locations");
        locateBtn.addActionListener(this);

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JScrollPane pane = new JScrollPane(setItemTable);
        pane.setPreferredSize(new Dimension(600, 400));

        JPanel northPanel = new JPanel();
        northPanel.add(useKnownBtn);
        northPanel.add(locateBtn);

        getContentPanel().add(northPanel, BorderLayout.NORTH);
        getContentPanel().add(pane, BorderLayout.CENTER);
        getContentPanel().add(toolBar, BorderLayout.EAST);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            if (object[0] instanceof Item) {
                item = (Item) object[0];

                updateTable();
                updateEnabledComponents();
            }
        } else {
            item = null;
        }
    }
}