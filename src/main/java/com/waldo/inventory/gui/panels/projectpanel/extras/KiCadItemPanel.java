package com.waldo.inventory.gui.panels.projectpanel.extras;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.kicad.KcComponent;
import com.waldo.inventory.Utils.parser.KiCad.KiCadParser;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.IKiCadParserModel;
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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class KiCadItemPanel extends JPanel implements GuiInterface, ListSelectionListener, ChangeListener, ActionListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JTabbedPane sheetTabs;

    private ILabel titleLbl;

    private JButton linkBtn;
    private JButton orderBtn;
    private JButton parseBtn;
    private JButton saveToDbBtn;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;
    private File parseFile;
    private KiCadParser kiCadParser = (com.waldo.inventory.Utils.parser.KiCad.KiCadParser) Application.getProjectParser("KiCadParser");

    private boolean hasParsed = false;
    private boolean hasMatched = false;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public KiCadItemPanel(Application application) {
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

    void updateComponentTable(HashMap<String, List<KcComponent>> componentMap) {
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

    void reParse(File fileToParse) {
        hasParsed = false;
        parseFile(fileToParse);
    }

    void reMatch() {
        hasMatched = false;
        matchItems();
    }

    public void matchItems() {
        if (!hasMatched) {
            application.beginWait();
            try {
                for (KcComponent component : getTableModel().getItemList()) {
                    SwingUtilities.invokeLater(() -> {
                        component.findMatchingItems();
                        getTableModel().updateItem(component);
                    });
                }
            } finally {
                application.endWait();
            }
        }
    }

    public void parseFile(File fileToParse) {
        if (!hasParsed) {
            application.beginWait();
            try {
                if (fileToParse.isFile()) {
                    if (kiCadParser.isFileValid(fileToParse)) {
                        clearComponentTable();
                        kiCadParser.parse(fileToParse);
                        updateComponentTable(createComponentMap(kiCadParser.sortList(kiCadParser.getParsedData())));
                        hasParsed = true;
                        matchItems();
                    } else {
                        if (FileUtils.getExtension(fileToParse).equals("pro")) {
                            parseFile(fileToParse.getParentFile());
                        } else {
                            JOptionPane.showMessageDialog(
                                    KiCadItemPanel.this,
                                    "The file cannot be parsed with the KiCad parser..",
                                    "Invalid file",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                } else {
                    // Search for file
                    List<File> actualFiles = FileUtils.findFileInFolder(fileToParse, kiCadParser.getFileExtension(), true);
                    if (actualFiles != null && actualFiles.size() == 1) {
                        clearComponentTable();
                        kiCadParser.parse(actualFiles.get(0));
                        updateComponentTable(createComponentMap(kiCadParser.sortList(kiCadParser.getParsedData())));
                        hasParsed = true;
                        matchItems();
                    } else {
                        JOptionPane.showMessageDialog(
                                KiCadItemPanel.this,
                                "Found no or too many files with extension " + kiCadParser.getFileExtension() + " ..",
                                "File not found",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            } finally {
                application.endWait();
            }
        }
    }

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
        titleLbl = new ILabel("Items");
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
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLbl, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(linkBtn);
        buttonPanel.add(orderBtn);
        buttonPanel.add(parseBtn);
        buttonPanel.add(saveToDbBtn);

        // Add
        add(sheetTabs, BorderLayout.CENTER);
        //add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    @Override
    public void updateComponents(Object object) {
        if (object != null && object instanceof File) {
            setVisible(true);
            if (parseFile == null || !parseFile.equals(object)) {
                application.beginWait();
                try {
                    parseFile = (File) object;
                    parseFile(parseFile);
                } finally {
                    application.endWait();
                }
            }
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
            LinkItemDialog dialog = new LinkItemDialog(application, "Link items", kiCadParser);
            dialog.showDialog();
        } else if (source.equals(orderBtn)) {
            // Order known items
        } else if (source.equals(parseBtn)) {
            reParse(parseFile);
        } else if (source.equals(saveToDbBtn)) {
            if (kiCadParser != null) {
                saveKcComponents(kiCadParser);
            }
        }
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