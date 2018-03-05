package com.waldo.inventory.gui.dialogs.editcreatedpcbdialog;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.ICreatedPcbTableModel;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.ISpinner;
import com.waldo.utils.icomponents.ITable;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


abstract class EditCreatedPcbDialogLayout extends IDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ICreatedPcbTableModel tableModel;
    private ITable<CreatedPcbLink> createdPcbTable;

    // Created pcb
    private ITextField pcbNameTf;
    private ITextField pcbDateTf;

    // Link panel
    private ITextField projectPcbTf;
    private ITextField pcbItemTf;
    private ITextField linkedItemTf;
    private ITextField usedItemTf;

    private SpinnerNumberModel usedAmountSpModel;
    private ISpinner usedAmountSp;
    private IActions.AutoCalculateUsedAction autoCalculateUsedAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ProjectPcb projectPcb;
    private CreatedPcb createdPcb;
    private List<CreatedPcbLink> displayList = new ArrayList<>();

    private CreatedPcbLink selectedLink;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditCreatedPcbDialogLayout(Window window, String title, ProjectPcb projectPcb, CreatedPcb createdPcb) {
        super(window, title);

        this.projectPcb = projectPcb;
        this.createdPcb = createdPcb;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void initTable() {
        if (projectPcb != null && createdPcb != null) {
            updateDisplayList(projectPcb, createdPcb);
            tableModel.setItemList(displayList);
        }
    }

    private void updateDisplayList(ProjectPcb projectPcb, CreatedPcb createdPcb) {
        displayList.clear();
        List<PcbItemProjectLink> pcbItemList = projectPcb.getPcbItemList();
        List<CreatedPcbLink> createdPcbLinkList = new ArrayList<>(SearchManager.sm().findCreatedPcbLinks(projectPcb.getId(), createdPcb.getId()));

        for (PcbItemProjectLink pipl : pcbItemList) {
            CreatedPcbLink link = findPcbItem(createdPcbLinkList, pipl.getPcbItemId());
            if (link != null) {
                createdPcbLinkList.remove(link);
            } else {
                link = new CreatedPcbLink(pipl.getId(), createdPcb.getId(), 0);
                if (pipl.getPcbItemItemLinkId() > DbObject.UNKNOWN_ID) {
                    link.setUsedItemId(pipl.getPcbItemItemLink().getItemId());
                }
            }
            displayList.add(link);
        }
    }

    private CreatedPcbLink findPcbItem(List<CreatedPcbLink> searchList, long pcbItemId) {
        for (CreatedPcbLink cpl : searchList) {
            if (cpl.getPcbItemProjectLinkId() > DbObject.UNKNOWN_ID) {
                if (cpl.getPcbItemProjectLink().getPcbItemId() == pcbItemId) {
                    return cpl;
                }
            }
        }
        return null;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        tableModel = new ICreatedPcbTableModel();
        createdPcbTable = new ITable<>(tableModel);
        createdPcbTable.setPreferredScrollableViewportSize(createdPcbTable.getPreferredSize());
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().setPreferredSize(new Dimension(600, 400));

        JScrollPane scrollPane = new JScrollPane(createdPcbTable);
        getContentPanel().add(scrollPane, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        initTable();
    }
}