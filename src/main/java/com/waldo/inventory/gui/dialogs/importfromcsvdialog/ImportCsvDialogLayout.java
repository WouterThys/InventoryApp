package com.waldo.inventory.gui.dialogs.importfromcsvdialog;

import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

public abstract class ImportCsvDialogLayout extends IDialog implements
        GuiInterface,
        ListSelectionListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    TableModel tableModel;
    ITable table;

    /*
    *                  VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    String[] tableColumnNames;
    List<TableObject> tableObjects;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ImportCsvDialogLayout(Application application, String title) {
        super(application, title);
    }

     /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
   *                  LISTENERS
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title
        setTitleName(getTitle());
        setTitleIcon(resourceManager.readImage("ReadCsvDialog.TitleIcon"));
        getButtonOK().setText("Import");

        // Table
        if (tableColumnNames != null) {
            tableModel = new TableModel(tableColumnNames);
        } else {
            tableModel = new TableModel();
        }
        table = new ITable(tableModel);
        table.getSelectionModel().addListSelectionListener(this);
        table.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);
        table.setDefaultRenderer(ILabel.class, new ITableEditors.CheckRenderer());
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(new JScrollPane(table), BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (tableObjects != null) {
            tableModel.setObjectList(tableObjects);
        }
    }
}
