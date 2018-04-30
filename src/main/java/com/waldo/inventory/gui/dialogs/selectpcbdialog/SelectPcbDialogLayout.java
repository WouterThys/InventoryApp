package com.waldo.inventory.gui.dialogs.selectpcbdialog;

import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.inventory.gui.components.tablemodels.IProjectPcbTableModel;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

abstract class SelectPcbDialogLayout extends iDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IProjectPcbTableModel tableModel;
    private ITable<ProjectPcb> pcbTable;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SelectPcbDialogLayout(Window window) {
        super(window, "");

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean hasSelected = getSelectedPcbs().size() > 0;
        getButtonOK().setEnabled(hasSelected);
    }

    public List<ProjectPcb> getSelectedPcbs() {
        return pcbTable.getSelectedItems();
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        showTitlePanel(false);
        setResizable(true);

        getButtonOK().setText("Select");
        getButtonOK().setEnabled(false);

        tableModel = new IProjectPcbTableModel();
        pcbTable = new ITable<>(tableModel);
        pcbTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        pcbTable.getSelectionModel().addListSelectionListener(e -> {
            updateEnabledComponents();
        });
    }

    @Override
    public void initializeLayouts() {

        JPanel mainPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(pcbTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        mainPanel.add(scrollPane);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(mainPanel);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        tableModel.setItemList(cache().getProjectPcbs());
        updateEnabledComponents();
    }
}