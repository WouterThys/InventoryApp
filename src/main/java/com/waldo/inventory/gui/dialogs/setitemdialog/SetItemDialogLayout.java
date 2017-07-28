package com.waldo.inventory.gui.dialogs.setitemdialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.database.SearchManager;
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
        tableModel.setItemList(SearchManager.sm().findSetItemsByItemId(item.getId()));
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("SetItemDialog.TitleIcon", 48));
        setTitleName("Set items");
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Table
        tableModel = new ISetItemTableModel();
        setItemTable = new ITable(tableModel);
        setItemTable.getSelectionModel().addListSelectionListener(this);
        setItemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);
        setItemTable.setDefaultRenderer(ILabel.class, new ITableEditors.AmountRenderer());

        // Tool bar
        toolBar = new IdBToolBar(this, IdBToolBar.VERTICAL);

        // Button
        useKnownBtn = new JButton("Add special");
        useKnownBtn.addActionListener(this);

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JScrollPane pane = new JScrollPane(setItemTable);
        pane.setPreferredSize(new Dimension(600, 400));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(useKnownBtn, BorderLayout.EAST);

        getContentPanel().add(northPanel, BorderLayout.NORTH);
        getContentPanel().add(pane, BorderLayout.CENTER);
        getContentPanel().add(toolBar, BorderLayout.EAST);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            if (object instanceof Item) {
                item = (Item) object;

                updateTable();
                updateEnabledComponents();
            }
        } else {
            item = null;
        }
    }
}