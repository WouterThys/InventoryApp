package com.waldo.inventory.gui.dialogs;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledEditPanel;
import com.waldo.inventory.gui.dialogs.imagefiledialog.ImageFileChooser;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;

public class DbObjectDialog<T extends DbObject> extends IDialog {

    private ITextField nameTextField;
    private ITextField iconPathTextField;
    private JButton browseIconButton;

    private T dbObject;

    public T getDbObject() {
        return dbObject;
    }

    public int showDialog() {
        setLocationRelativeTo(application);
        pack();
        setVisible(true);
        return dialogResult;
    }

    public DbObjectDialog(Application application, String title) {
        super(application, title);

        setResizable(false);
        showTitlePanel(false);

        initComponents();
        initLayouts();
    }

    public DbObjectDialog(Application application, String title,  T object) {
        this(application, title);
        dbObject = object;
        updateComponents();
    }

    private void updateComponents() {
        if (dbObject != null) {
            nameTextField.setText(dbObject.getName());
            iconPathTextField.setText(dbObject.getIconPath());
            buttonOK.setText("Update");
        }
    }

    private Boolean verify() {
        String name = nameTextField.getText();
        if (name == null || name.isEmpty()) {
            nameTextField.setError("Name can not be empty");
            return false;
        }
        return true;
    }

    private void initComponents() {
        // Text fields
        nameTextField = new ITextField();
        iconPathTextField = new ITextField();

        // File chooser
        browseIconButton = new JButton(resourceManager.readImage("Common.BrowseIcon"));
        browseIconButton.addActionListener(e -> {
            JFileChooser fileChooser = ImageFileChooser.getFileChooser();
            fileChooser.setCurrentDirectory(new File("./Images/"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fileChooser.showDialog(DbObjectDialog.this, "Open") == JFileChooser.APPROVE_OPTION) {
                iconPathTextField.setText(fileChooser.getSelectedFile().getPath());
            }
        });
    }

    @Override
    protected void onOK() {
        if (verify()) {
            // Set values
            dbObject.setName(nameTextField.getText());
            String iconPath = iconPathTextField.getText();
            if (iconPath != null && !iconPath.isEmpty()) {
                dbObject.setIconPath(iconPath);
            } else {
                dbObject.setIconPath("");
            }
            dialogResult = OK;
            // Close dialog
            dispose();
        }
    }

    private void initLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));

        // Additional stuff
        JPanel iconPathPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = createFieldConstraints(0,0);
        constraints.gridwidth = 1;
        iconPathPanel.add(iconPathTextField, constraints);
        constraints = createFieldConstraints(1,0);
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        iconPathPanel.add(browseIconButton, constraints);

        // Add all
        getContentPanel().add(new ITitledEditPanel(
                "New",
                new String[] {"Name: ", "Icon path: "},
                new JComponent[] {nameTextField, iconPathPanel}
        ));
    }
}
