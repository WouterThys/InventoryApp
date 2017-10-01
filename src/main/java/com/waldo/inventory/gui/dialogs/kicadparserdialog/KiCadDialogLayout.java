package com.waldo.inventory.gui.dialogs.kicadparserdialog;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.IKiCadParserModel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class KiCadDialogLayout extends IDialog implements ActionListener, ListSelectionListener, ChangeListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JButton parseBtn;
    JButton selectFileBtn;
    ILabel  fileLbl;

//    IKiCadParserModel componentTableModel;
//    ITable componentTable;

    JTabbedPane sheetTabs;

    KiCadDetailPanel detailPanel;


     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    File fileToParse;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    KiCadDialogLayout(Application application, String title) {
        super(application, title);
        setResizable(true);
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        if (fileToParse != null && fileToParse.exists()) {
            parseBtn.setEnabled(true);
        } else {
            parseBtn.setEnabled(false);
        }
    }


     IKiCadParserModel getTableModel() {
        KiCadSheetTab panel = (KiCadSheetTab) sheetTabs.getSelectedComponent();
        return panel.getTableModel();
    }

    ITable getTable() {
        KiCadSheetTab panel = (KiCadSheetTab) sheetTabs.getSelectedComponent();
        return panel.getTable();
    }

    void updateComponentTable(HashMap<String, List<PcbItem>> componentMap) {
        for (String sheet : componentMap.keySet()) {
            KiCadSheetTab tab = new KiCadSheetTab(application, this);
            tab.updateComponents(componentMap.get(sheet));
            sheetTabs.addTab(sheet, tab);
        }
        repaint();
    }

    void clearComponentTable() {
        sheetTabs.removeAll();
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10,2));

        panel.add(fileLbl);
        panel.add(selectFileBtn);
        panel.add(parseBtn);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 1),
                BorderFactory.createEmptyBorder(10,20,10,20)
        ));

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title
        setTitleIcon(imageResource.readImage("Parser.KiCad", 48));
        setTitleName(getTitle());

        // Stuff
        parseBtn = new JButton("Parse");
        parseBtn.addActionListener(this);
        selectFileBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        selectFileBtn.addActionListener(this);
        fileLbl = new ILabel();

        // Tabs
        sheetTabs = new JTabbedPane();
        sheetTabs.addChangeListener(this);

        // Table
//        componentTableModel = new IKiCadParserModel();
//        componentTable = new ITable(componentTableModel);
//        componentTable.getSelectionModel().addListSelectionListener(this);
//        componentTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);

        // Details panel
        detailPanel = new KiCadDetailPanel(application);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());
//        JScrollPane pane = new JScrollPane(componentTable);
//        pane.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        // Details panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(detailPanel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        getContentPanel().add(sheetTabs, BorderLayout.CENTER);
        getContentPanel().add(createBottomPanel(), BorderLayout.NORTH);
        getContentPanel().add(panel, BorderLayout.EAST);

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        application.beginWait();
        try {
            PcbItem selectedComponent;
            if (object != null) {
                selectedComponent = (PcbItem) object;
            } else {
                selectedComponent = null;
            }

            // File
            updateEnabledComponents();

            // Details
            detailPanel.updateComponents(selectedComponent);
        } finally {
            application.endWait();
        }
    }
}