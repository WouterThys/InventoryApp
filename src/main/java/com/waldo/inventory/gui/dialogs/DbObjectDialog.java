package com.waldo.inventory.gui.dialogs;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledEditPanel;
import com.waldo.inventory.gui.dialogs.filechooserdialog.ImageFileChooser;

import javax.swing.*;
import java.io.File;

import static com.waldo.inventory.gui.Application.imageResource;

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
            if (dbObject.getId() < DbObject.UNKNOWN_ID) {
                buttonOK.setText("Save");
            } else {
                buttonOK.setText("Update");
            }
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
        browseIconButton = new JButton(imageResource.readImage("Common.BrowseIcon"));
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
        JPanel iconPathPanel = PanelUtils.createFileOpenPanel(iconPathTextField, browseIconButton);

        // Add all
        getContentPanel().add(new ITitledEditPanel(
                "New",
                new String[] {"Name: ", "Icon path: "},
                new JComponent[] {nameTextField, iconPathPanel}
        ));
    }

    @Override
    public void initializeComponents() {

    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object object) {

    }
}
