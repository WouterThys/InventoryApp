package com.waldo.inventory.gui.panels.projectpanel.extras;

import com.waldo.inventory.Utils.parser.KiCad.KiCadParser;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.classes.kicad.KcComponent;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.IKiCadParserModel;
import com.waldo.inventory.gui.dialogs.kccomponentorderdialog.KcComponentOrderDialog;
import com.waldo.inventory.gui.dialogs.kicadparserdialog.KiCadSheetTab;
import com.waldo.inventory.gui.dialogs.linkitemdialog.LinkItemDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class PcbItemPanel extends JPanel implements GuiInterface, ListSelectionListener, ChangeListener, ActionListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JTabbedPane sheetTabs;

    private JButton linkBtn;
    private JButton orderBtn;
    private JButton parseBtn;
    private JButton saveToDbBtn;

    private JPanel buttonPanel;

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
    IKiCadParserModel getTableModel() {
        KiCadSheetTab panel = (KiCadSheetTab) sheetTabs.getSelectedComponent();
        return panel.getTableModel();
    }

    ITable getTable() {
        KiCadSheetTab panel = (KiCadSheetTab) sheetTabs.getSelectedComponent();
        return panel.getTable();
    }

    public JPanel getToolbarPanel() {
        return buttonPanel;
    }

    private void updateEnabledComponents() {
        //saveToDbBtn.setEnabled(!projectPcb.getParser().allComponentsInDb());
        //orderBtn.setEnabled(projectPcb.getParser().hasLinkedItems());
    }

    private void updateComponentTable(HashMap<String, List<KcComponent>> componentMap) {
        for (String sheet : componentMap.keySet()) {
            KiCadSheetTab tab = new KiCadSheetTab(application, this);
            tab.updateComponents(componentMap.get(sheet));
            sheetTabs.addTab(sheet, tab);
        }
        repaint();
    }

    private void clearComponentTable() {
        sheetTabs.removeAll();
    }

//    private void reParse(File fileToParse) {
//        hasParsed = false;
//        parseFile(fileToParse);
//    }

//    private void matchItems() {
//        if (!hasMatched) {
//            application.beginWait();
//            try {
//                SwingUtilities.invokeLater(() -> {
//                    for (KcComponent component : getTableModel().getItemList()) {
//                        component.findMatchingItems();
//                        getTableModel().updateItem(component);
//                    }
//                    updateEnabledComponents();
//                });
//            } finally {
//                application.endWait();
//            }
//        }
//    }
//
//    private void parseFile(File fileToParse) {
//        if (!hasParsed) {
//            application.beginWait();
//            try {
//                if (fileToParse.isFile()) {
//                    if (kiCadParser.isFileValid(fileToParse)) {
//                        clearComponentTable();
//                        kiCadParser.parse(fileToParse);
//                        updateComponentTable(createComponentMap(kiCadParser.sortList(kiCadParser.getParsedData())));
//                        hasParsed = true;
//                        matchItems();
//                    } else {
//                        if (FileUtils.getExtension(fileToParse).equals("pro")) {
//                            parseFile(fileToParse.getParentFile());
//                        } else {
//                            JOptionPane.showMessageDialog(
//                                    PcbItemPanel.this,
//                                    "The file cannot be parsed with the KiCad parser..",
//                                    "Invalid file",
//                                    JOptionPane.ERROR_MESSAGE
//                            );
//                        }
//                    }
//                } else {
//                    // Search for file
//                    List<File> actualFiles = FileUtils.findFileInFolder(fileToParse, kiCadParser.getFileExtension(), true);
//                    if (actualFiles != null && actualFiles.size() == 1) {
//                        clearComponentTable();
//                        kiCadParser.parse(actualFiles.get(0));
//                        updateComponentTable(createComponentMap(kiCadParser.sortList(kiCadParser.getParsedData())));
//                        hasParsed = true;
//                        matchItems();
//                    } else {
//                        JOptionPane.showMessageDialog(
//                                PcbItemPanel.this,
//                                "Found no or too many files with extension " + kiCadParser.getFileExtension() + " ..",
//                                "File not found",
//                                JOptionPane.ERROR_MESSAGE
//                        );
//                    }
//                }
//            } finally {
//                application.endWait();
//            }
//        }
//    }

    private HashMap<String, List<KcComponent>> createComponentMap(List<KcComponent> components) {
        HashMap<String, List<KcComponent>> sheetMap = new HashMap<>();
        sheetMap.put("Main", new ArrayList<>());

        for (KcComponent c : components) {
            List<String> sheetNames = c.getSheetPath().getNames();
            if (sheetNames.size() == 0) {
                sheetMap.get("Main").add(c);
            } else {
                for (String name : sheetNames) {
                    if (!sheetMap.containsKey(name)) {
                        sheetMap.put(name, new ArrayList<>());
                    }
                    sheetMap.get(name).add(c);
                }
            }
        }
        return sheetMap;
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

        linkBtn = new JButton(imageResource.readImage("Common.NewLink", 24));
        orderBtn = new JButton(imageResource.readImage("Common.Order", 24));
        parseBtn = new JButton(imageResource.readImage("Common.Parse", 24));
        saveToDbBtn = new JButton(imageResource.readImage("Common.DbSave", 24));

        linkBtn.addActionListener(this);
        orderBtn.addActionListener(this);
        parseBtn.addActionListener(this);
        saveToDbBtn.addActionListener(this);

        linkBtn.setToolTipText("Link to known items");
        orderBtn.setToolTipText("Order linked");
        parseBtn.setToolTipText("Parse again");
        saveToDbBtn.setToolTipText("Save to database");

        buttonPanel = new JPanel();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        buttonPanel.add(linkBtn);
        buttonPanel.add(orderBtn);
        buttonPanel.add(parseBtn);
        buttonPanel.add(saveToDbBtn);

        // Add
        add(sheetTabs, BorderLayout.CENTER);
        //add(buttonPanel, BorderLayout.SOUTH);

    }

    @Override
    public void updateComponents(Object object) {
        if (object != null && object instanceof ProjectPcb) {
            setVisible(true);
            projectPcb = (ProjectPcb) object;

            application.beginWait();
            try {
                clearComponentTable();
                updateComponentTable(createComponentMap(projectPcb.getPcbItemList()));
            } finally {
                application.endWait();
            }

            updateEnabledComponents();
        } else {
            setVisible(false);
        }
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

    }

    //
    // Buttons pressed
    //

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(linkBtn)) {
            // Show dialog to link items
            LinkItemDialog dialog = new LinkItemDialog(application, "Link items", projectPcb.getParser());
            dialog.showDialog();
        } else if (source.equals(orderBtn)) {
            // Order known items
            KcComponentOrderDialog orderDialog = new KcComponentOrderDialog(
                    application,
                    "Order items",
                    projectPcb.getParser().getLinkedItems());
            orderDialog.showDialog();
        } else if (source.equals(parseBtn)) {
            try {
                projectPcb.reParse();
            } catch (Exception ex) {
                Status().setError("Error parsing", ex);
                JOptionPane.showMessageDialog(
                        PcbItemPanel.this,
                        "Error parsing: " + ex,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else if (source.equals(saveToDbBtn)) {
            if (projectPcb.getParser() != null) {
                saveKcComponents(projectPcb.getParser());
            }
            saveToDbBtn.setEnabled(false);
        }
        updateEnabledComponents();
    }

    private void saveKcComponents(KiCadParser parser) {
        List<KcComponent> toSaveList = parser.createUniqueList(parser.getParsedData());
        int cnt = 0;
        for (KcComponent component : toSaveList) {
            KcComponent dbComponent = SearchManager.sm().findKcComponent(component.getValue(), component.getFootprint(), component.getLibSource().getLib(), component.getLibSource().getPart());
            if (dbComponent == null) {
                component.save();
                cnt++;
            }
        }

        String msg = "Saved " + cnt + " new components, " + (toSaveList.size() - cnt) + " where already saved in the database!";

        JOptionPane.showMessageDialog(
                application,
                msg,
                "Saved components",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}