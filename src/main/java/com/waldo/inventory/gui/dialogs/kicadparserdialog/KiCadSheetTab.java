package com.waldo.inventory.gui.dialogs.kicadparserdialog;

import com.waldo.inventory.Utils.parser.KiCad.KcComponent;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ITable;
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
        componentTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        add(new JScrollPane(componentTable));
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