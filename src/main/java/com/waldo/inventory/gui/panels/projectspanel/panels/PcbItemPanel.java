package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.classes.PcbItemProjectLink;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.IPcbItemModel;
import com.waldo.inventory.gui.dialogs.kicadparserdialog.PcbItemSheetTab;
import com.waldo.inventory.gui.dialogs.linkitemdialog.LinkPcbItemDialog;
import com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog.OrderPcbItemDialog;
import com.waldo.inventory.gui.dialogs.projectusedpcbitemsdialog.UsedPcbItemsDialog;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class PcbItemPanel extends JPanel implements
        GuiInterface, ListSelectionListener, ChangeListener, IPcbItemModel.PcbItemListener {


    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JTabbedPane sheetTabs;

    private AbstractAction linkAa;
    private AbstractAction orderAa;
    private AbstractAction usedAa;
    private AbstractAction parseAa;

    private JToolBar buttonPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;
    private ProjectPcb projectPcb;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public PcbItemPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
        setVisible(false);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    IPcbItemModel getTableModel() {
        PcbItemSheetTab panel = (PcbItemSheetTab) sheetTabs.getSelectedComponent();
        return panel.getTableModel();
    }

    ITable getTable() {
        PcbItemSheetTab panel = (PcbItemSheetTab) sheetTabs.getSelectedComponent();
        return panel.getTable();
    }

    JToolBar getToolbarPanel() {
        return buttonPanel;
    }

    private void updateEnabledComponents() {
        boolean enable = projectPcb != null;
        linkAa.setEnabled(enable);
        parseAa.setEnabled(enable);
        orderAa.setEnabled(enable);
        usedAa.setEnabled(enable && projectPcb.isValid());
    }

    private void updateComponentTable(HashMap<String, List<PcbItem>> pcbItemMap) {
        for (String sheet : pcbItemMap.keySet()) {
            PcbItemSheetTab tab = new PcbItemSheetTab(this, this);
            tab.updateComponents(pcbItemMap.get(sheet));
            sheetTabs.addTab(sheet, tab);
        }
    }

    private void clearComponentTable() {
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

        // Actions
        linkAa = new AbstractAction("Link", imageResource.readImage("Projects.Pcb.LinkBtn")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onLink());
            }
        };
        linkAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Link to known items");

        orderAa = new AbstractAction("Order", imageResource.readImage("Projects.Pcb.OrderBtn")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onOrder());
            }
        };
        orderAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Order linked items");

        usedAa = new AbstractAction("Used", imageResource.readImage("Projects.Pcb.UsedBtn")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onUsed());
            }
        };
        usedAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Used items");

        parseAa = new AbstractAction("Parse", imageResource.readImage("Projects.Pcb.ParseBtn")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onParse());
            }
        };
        parseAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Parse again");

        buttonPanel = new JToolBar(JToolBar.HORIZONTAL);
        buttonPanel.setOpaque(false); buttonPanel.setFloatable(false);
        buttonPanel.setBorder(new EmptyBorder(2,2,2,2));
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        buttonPanel.add(linkAa);
        buttonPanel.add(orderAa);
        buttonPanel.add(usedAa);
        buttonPanel.addSeparator();
        buttonPanel.add(parseAa);

        // Add
        add(sheetTabs, BorderLayout.CENTER);

    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null && object[0] instanceof ProjectPcb) {
            setVisible(true);
            projectPcb = (ProjectPcb) object[0];

            application.beginWait();
            try {
                clearComponentTable();
                updateComponentTable(projectPcb.getPcbItemMap());

                //setDetails();

            } finally {
                application.endWait();
            }

        } else {
            projectPcb = null;
            setVisible(false);
        }
        updateEnabledComponents();
    }

    private String getSelectedSheet() {
        return sheetTabs.getTitleAt(sheetTabs.getSelectedIndex());
    }

    private List<PcbItem> getLinkedPcbItems(List<PcbItem> allItems) {
        List<PcbItem> linkedItems = new ArrayList<>();

        for (PcbItem pcbItem : allItems) {
            if (pcbItem.hasMatch()) {
                linkedItems.add(pcbItem);
            }
        }

        return linkedItems;
    }


    //
    // One of the list values in the sheet tabs selected
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    //
    // Tab changed
    //
    @Override
    public void stateChanged(ChangeEvent e) {
        // change amount
    }

    private void onLink() {
        LinkPcbItemDialog dialog = new LinkPcbItemDialog(application, "Link items", projectPcb);
        dialog.showDialog();
    }

    private void onOrder() {
        if (projectPcb.hasLinkedItems()) {
            OrderPcbItemDialog orderDialog = new OrderPcbItemDialog(
                    application,
                    "Order items",
                    projectPcb);
            orderDialog.showDialog();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Items need to be linked with known item..",
                    "No linked items",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onUsed() {
        if (projectPcb.hasLinkedItems()) {
            // Used dialog
            UsedPcbItemsDialog dialog = new UsedPcbItemsDialog(application, "Set used", projectPcb);
            dialog.showDialog();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Items need to be linked with known item..",
                    "No linked items",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onParse() {
        try {
            if (projectPcb.parseAgain()) {
                clearComponentTable();
                //setDetails();
                updateComponentTable(projectPcb.getPcbItemMap());
            }
        } catch (Exception ex) {
            Status().setError("Error parsing", ex);
            JOptionPane.showMessageDialog(
                    PcbItemPanel.this,
                    "Error parsing: " + ex,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    //
    // Table model listener
    //

    @Override
    public PcbItemProjectLink onGetProjectLink(PcbItem pcbItem) {
        if (projectPcb != null && pcbItem != null) {
            return SearchManager.sm().findPcbItemProjectLink(projectPcb.getId(), pcbItem.getId());
        }
        return null;
    }
}