package com.waldo.inventory.gui.dialogs.kicadparserdialog;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.kicad.PcbItem;
import com.waldo.inventory.Utils.parser.KiCad.KiCadParser;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KiCadDialog extends KiCadDialogLayout {

    private KiCadParser kiCadParser = (KiCadParser) Application.getProjectParser("KiCadParser");

    public KiCadDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();

        updateComponents(null);
    }

    private void parseSelected() {
        if (fileToParse.isFile()) {
            if (kiCadParser.isFileValid(fileToParse)) {
                clearComponentTable();
                kiCadParser.parse(fileToParse);
                updateComponentTable(createComponentMap(kiCadParser.sortList(kiCadParser.getParsedData())));
            } else {
                JOptionPane.showMessageDialog(
                        KiCadDialog.this,
                        "The file cannot be parsed with the KiCad parser..",
                        "Invalid file",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            // Search for file
            List<File> actualFiles = FileUtils.findFileInFolder(fileToParse, kiCadParser.getFileExtension(), true);
            if (actualFiles != null && actualFiles.size() == 1) {
                clearComponentTable();
                kiCadParser.parse(actualFiles.get(0));
                updateComponentTable(createComponentMap(kiCadParser.sortList(kiCadParser.getParsedData())));
            } else {
                JOptionPane.showMessageDialog(
                        KiCadDialog.this,
                        "Found no or too many files with extension " + kiCadParser.getFileExtension() + " ..",
                        "File not found",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private HashMap<String, List<PcbItem>> createComponentMap(List<PcbItem> components) {
        HashMap<String, List<PcbItem>> sheetMap = new HashMap<>();
        sheetMap.put("Main", new ArrayList<>());

        for (PcbItem c : components) {
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

    private void openSelected() {
        JFileChooser fileChooser = new JFileChooser("/home");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (fileToParse != null && fileToParse.exists()) {
            fileChooser.setCurrentDirectory(fileToParse);
        }

        if (fileChooser.showDialog(KiCadDialog.this, "Open") == JFileChooser.APPROVE_OPTION) {
            fileToParse = fileChooser.getSelectedFile();
            fileLbl.setText(fileToParse.getAbsolutePath());
            updateComponents(null);
        }
    }


    //
    // Parse button or select file button clicked
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(parseBtn)) {
            parseSelected();
        } else if (source.equals(selectFileBtn)) {
            openSelected();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            int row = getTable().getSelectedRow();
            if (row >= 0) {
                updateComponents(getTable().getValueAtRow(row));
            }
        }
    }

    //
    // Tab changed
    //
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == sheetTabs) {
            updateComponents(null);
        }
    }
}