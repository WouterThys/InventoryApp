package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.kicadparserdialog.PcbItemSheetTab;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PcbItemPanel extends JPanel implements
        GuiUtils.GuiInterface, ChangeListener {


    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JTabbedPane sheetTabs;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private ProjectPcb projectPcb;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PcbItemPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
        setVisible(false);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void updateComponentTable(List<PcbItemProjectLink> pcbItemProjectLinks) {
        HashMap<String, List<PcbItemProjectLink>> map = new HashMap<>();

        for (PcbItemProjectLink link : pcbItemProjectLinks) {
            String sheet = link.getPcbSheetName();
            if (!map.containsKey(sheet)) {
                map.put(sheet, new ArrayList<>());
            }

            map.get(sheet).add(link);
        }

        for (String sheet : map.keySet()) {
            PcbItemSheetTab tab = new PcbItemSheetTab(application);
            tab.updateComponents(map.get(sheet).toArray());
            sheetTabs.add(sheet, tab);
        }
    }

    public void clearComponentTable() {
        sheetTabs.removeAll();
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Tabs
        sheetTabs = new JTabbedPane();
        sheetTabs.addChangeListener(this);

        // Title
        ILabel titleLbl = new ILabel("Items");
        titleLbl.setFont(20, Font.BOLD);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Add
        add(sheetTabs, BorderLayout.CENTER);

    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null && object[0] instanceof ProjectPcb) {
            setVisible(true);
            projectPcb = (ProjectPcb) object[0];

            Application.beginWait(PcbItemPanel.this);
            try {
                clearComponentTable();
                updateComponentTable(projectPcb.getPcbItemList());
            } finally {
                Application.endWait(PcbItemPanel.this);
            }

        } else {
            projectPcb = null;
            setVisible(false);
        }
    }


    //
    // Tab changed
    //
    @Override
    public void stateChanged(ChangeEvent e) {
        // change amount
    }
}