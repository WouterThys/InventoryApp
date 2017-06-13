package com.waldo.inventory.gui.dialogs.importfromcsvdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ISpinner;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.dialogs.filechooserdialog.CsvFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;

public abstract class ReadCsvDialogLayout extends IDialog implements GuiInterface {



    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField fileNameTf;
    JButton fileNameOpenBtn;

    JCheckBox useHeaderCb;
    ISpinner headerRowSp;

    ISpinner componentNameColSp;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ReadCsvDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title
        setTitleName(getTitle());
        setTitleIcon(resourceManager.readImage("ReadCsvDialog.TitleIcon"));
        getButtonOK().setText("Import");

        // File
        fileNameTf = new ITextField("File path");
        fileNameOpenBtn = new JButton(resourceManager.readImage("Common.BrowseIcon"));
        fileNameOpenBtn.addActionListener(e -> {
            JFileChooser fileChooser = CsvFileChooser.getFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fileChooser.showDialog(ReadCsvDialogLayout.this, "Open") == JFileChooser.APPROVE_OPTION) {
                fileNameTf.setText(fileChooser.getSelectedFile().getPath());
            }
        });

        // Header
        useHeaderCb = new JCheckBox("Use header", false);
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        headerRowSp = new ISpinner(spinnerModel);

        // Value column
        spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        componentNameColSp = new ISpinner(spinnerModel);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridBagLayout());

        // Labels and other
        ILabel fileLabel = new ILabel("File: ");
        fileLabel.setHorizontalAlignment(ILabel.RIGHT);
        fileLabel.setVerticalAlignment(ILabel.CENTER);

        ILabel headerRowLabel = new ILabel("First row: ");
        headerRowLabel.setHorizontalAlignment(ILabel.RIGHT);
        headerRowLabel.setVerticalAlignment(ILabel.CENTER);

        ILabel componentRowLabel = new ILabel("Reference col.: ");
        componentRowLabel.setHorizontalAlignment(ILabel.RIGHT);
        componentRowLabel.setVerticalAlignment(ILabel.CENTER);

        JPanel openFilePanel = PanelUtils.createFileOpenPanel(fileNameTf, fileNameOpenBtn);

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        content.add(fileLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        content.add(openFilePanel, gbc);

        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        content.add(useHeaderCb, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        content.add(headerRowLabel, gbc);

        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        content.add(headerRowSp, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        content.add(componentRowLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        content.add(componentNameColSp, gbc);


        // Border
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,2,5,2));

        // Add
        getContentPanel().add(content, BorderLayout.CENTER);

        // Pack
        pack();
    }

    @Override
    public void updateComponents(Object object) {

    }
}
