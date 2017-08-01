package com.waldo.inventory.gui.dialogs.kicadparserdialog;

import com.waldo.inventory.classes.kicad.KcComponent;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;
import com.waldo.inventory.gui.components.tablemodels.IKiCadParserModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class KiCadSheetTab extends JPanel implements GuiInterface {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IKiCadParserModel componentTableModel;
    private ITable componentTable;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;
    private ListSelectionListener listSelectionListener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public KiCadSheetTab(Application application, ListSelectionListener listSelectionListener) {
        this.application = application;
        this.listSelectionListener = listSelectionListener;
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public IKiCadParserModel getTableModel() {
        return componentTableModel;
    }

    public ITable getTable() {
        return componentTable;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        componentTableModel = new IKiCadParserModel();
        componentTable = new ITable(componentTableModel);
        componentTable.getSelectionModel().addListSelectionListener(listSelectionListener);
        componentTable.setDefaultRenderer(ILabel.class, new ITableEditors.KcMatchRenderer());
        componentTable.getColumnModel().getColumn(0).setMaxWidth(30);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        JScrollPane pane = new JScrollPane(componentTable);
        pane.setPreferredSize(new Dimension(600, 400));
        add(pane);
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            java.util.List<KcComponent> components = (java.util.List<KcComponent>) object;
            componentTableModel.setItemList(components);
        }
    }
}