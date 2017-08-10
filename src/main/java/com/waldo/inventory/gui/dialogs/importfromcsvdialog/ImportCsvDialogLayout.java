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
import java.awt.event.MouseListener;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ImportCsvDialogLayout extends IDialog implements
        GuiInterface,
        ListSelectionListener,
        TableObjectPanel.IItemSelectedListener,
        MouseListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    TableModel tableModel;
    ITable objectTable;

    TableObjectPanel tableObjectPanel; // Bottom panel with list of found objects and stuff

    /*
    *                  VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    String[] tableColumnNames;
    List<TableObject> tableObjects;

    TableObject selectedTableObject;

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
        setTitleIcon(imageResource.readImage("ReadCsvDialog.TitleIcon"));
        getButtonOK().setText("Import");

        // Table
        if (tableColumnNames != null) {
            tableModel = new TableModel(tableColumnNames);
        } else {
            tableModel = new TableModel();
        }
//        objectTable = new ITable();
//        objectTable.getSelectionModel().addListSelectionListener(this);
//        objectTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);
//        objectTable.setDefaultRenderer(ILabel.class, new ITableEditors.CheckRenderer());
//        objectTable.setOpaque(true);
//        objectTable.addMouseListener(this);

        // Object panel
        tableObjectPanel = new TableObjectPanel(this, application);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(new JScrollPane(objectTable), BorderLayout.CENTER);
        getContentPanel().add(tableObjectPanel, BorderLayout.SOUTH);
        setMinimumSize(new Dimension(1200,600));

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (tableObjects != null) {

            if (!tableModel.hasData()) {
                tableModel.setObjectList(tableObjects);
            }

            selectedTableObject = (TableObject) object;
            tableObjectPanel.updateComponents(selectedTableObject);
        }


    }
}
