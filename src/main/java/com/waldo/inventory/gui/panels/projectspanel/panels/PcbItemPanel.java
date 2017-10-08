package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.IPcbItemModel;
import com.waldo.inventory.gui.dialogs.pcbitemorderdialog.PcbItemOrderDialog;
import com.waldo.inventory.gui.dialogs.kicadparserdialog.PcbItemSheetTab;
import com.waldo.inventory.gui.dialogs.linkitemdialog.LinkPcbItemDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class PcbItemPanel extends JPanel implements GuiInterface, ListSelectionListener, ChangeListener, ActionListener {

    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JTabbedPane sheetTabs;

    private JButton linkBtn;
    private JButton orderBtn;
    private JButton parseBtn;

    private JPanel buttonPanel;

    private ILabel lastParsedLbl;
    private ILabel parsedHowLbl;
    private ILabel itemAmountLbl;

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

    public JPanel getToolbarPanel() {
        return buttonPanel;
    }

    private void updateEnabledComponents() {
        boolean enable = projectPcb != null;
        linkBtn.setEnabled(enable);
        parseBtn.setEnabled(enable);
        orderBtn.setEnabled(enable);
    }

    private void updateComponentTable(HashMap<String, List<PcbItem>> pcbItemMap) {
        for (String sheet : pcbItemMap.keySet()) {
            PcbItemSheetTab tab = new PcbItemSheetTab(application, this);
            tab.updateComponents(pcbItemMap.get(sheet));
            sheetTabs.addTab(sheet, tab);
        }
        repaint();
    }

    private void clearComponentTable() {
        sheetTabs.removeAll();
    }

    private JPanel createDetailPanel() {
        JPanel detailPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Labels
        ILabel lp = new ILabel("Last parsed: ", ILabel.RIGHT);
        lp.setForeground(Color.gray);
        ILabel ni = new ILabel("# items: ", ILabel.RIGHT);
        ni.setForeground(Color.gray);

        // - Parsed how
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailPanel.add(parsedHowLbl, gbc);

        // - Last parsed
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        detailPanel.add(lp, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailPanel.add(lastParsedLbl, gbc);

        // - Amount
        gbc.gridx = 3; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        detailPanel.add(ni, gbc);

        gbc.gridx = 4; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailPanel.add(itemAmountLbl, gbc);

        // - Border
        detailPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));

        return detailPanel;

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

        // Buttons
        linkBtn = new JButton(imageResource.readImage("Projects.Pcb.LinkBtn"));
        orderBtn = new JButton(imageResource.readImage("Projects.Pcb.OrderBtn"));
        parseBtn = new JButton(imageResource.readImage("Projects.Pcb.ParseBtn"));

        linkBtn.addActionListener(this);
        orderBtn.addActionListener(this);
        parseBtn.addActionListener(this);

        linkBtn.setToolTipText("Link to known items");
        orderBtn.setToolTipText("Order linked");
        parseBtn.setToolTipText("Parse again");

        buttonPanel = new JPanel();

        // Labels
        lastParsedLbl = new ILabel();
        parsedHowLbl = new ILabel();
        itemAmountLbl = new ILabel();
        lastParsedLbl.setForeground(Color.gray);
        parsedHowLbl.setForeground(Color.gray);
        itemAmountLbl.setForeground(Color.gray);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        buttonPanel.add(linkBtn);
        buttonPanel.add(orderBtn);
        buttonPanel.add(parseBtn);

        // Add
        add(sheetTabs, BorderLayout.CENTER);
        add(createDetailPanel(), BorderLayout.SOUTH);

    }

    @Override
    public void updateComponents(Object object) {
        if (object != null && object instanceof ProjectPcb) {
            setVisible(true);
            projectPcb = (ProjectPcb) object;

            application.beginWait();
            try {
                clearComponentTable();
                updateComponentTable(projectPcb.getPcbItemMap());

                parsedHowLbl.setText(projectPcb.hasParsed() ? "Items from file" : "Items from database");
                lastParsedLbl.setText(sdf.format(projectPcb.getLastParsedDate()));
                itemAmountLbl.setText("45");

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

    //
    // Buttons pressed
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(linkBtn)) {
            // Show dialog to link items
            LinkPcbItemDialog dialog = new LinkPcbItemDialog(application, "Link items", projectPcb);
            dialog.showDialog();
        } else if (source.equals(orderBtn)) {
            // Order known items
            List<PcbItem> linkedItems = getLinkedPcbItems(projectPcb.getPcbItemMap().get(getSelectedSheet()));
            if (linkedItems.size() > 0) {
                PcbItemOrderDialog orderDialog = new PcbItemOrderDialog(
                        application,
                        "Order items",
                        linkedItems);
                orderDialog.showDialog();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Items need to be linked with known item..",
                        "No linked items",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else if (source.equals(parseBtn)) {
            try {
                if (projectPcb.parseAgain()) {
                    clearComponentTable();
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
        updateEnabledComponents();
    }

}