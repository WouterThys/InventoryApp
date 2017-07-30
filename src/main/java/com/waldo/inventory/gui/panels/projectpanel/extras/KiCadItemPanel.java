package com.waldo.inventory.gui.panels.projectpanel.extras;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.Utils.parser.KiCad.KcComponent;
import com.waldo.inventory.Utils.parser.KiCad.KiCadParser;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.IKiCadParserModel;
import com.waldo.inventory.gui.dialogs.kicadparserdialog.KiCadSheetTab;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KiCadItemPanel extends JPanel implements GuiInterface, ListSelectionListener, ChangeListener {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JTabbedPane sheetTabs;

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
            for (KcComponent component : getTableModel().getItemList()) {
                SwingUtilities.invokeLater(() -> {
                    component.findMatchingItems();
                    getTableModel().updateItem(component);
                });
            }
        }
    }

    public void parseFile(File fileToParse) {
        if (!hasParsed) {
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
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Details panel

        add(sheetTabs, BorderLayout.CENTER);

    }

    @Override
    public void updateComponents(Object object) {
        if (object != null && object instanceof File) {
            setVisible(true);
            if (parseFile == null || !parseFile.equals(object)) {
                parseFile = (File) object;
                parseFile(parseFile);
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
}