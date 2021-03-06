package com.waldo.inventory.gui.dialogs;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.gui.components.ITitledEditPanel;
import com.waldo.inventory.gui.dialogs.filechooserdialog.ImageFileChooser;
import com.waldo.utils.FileUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.waldo.inventory.gui.Application.imageResource;

public class DbObjectDialog<T extends DbObject> extends IDialog {

    private ITextField nameTextField;
    private ITextField iconPathTextField;
    private JButton browseIconButton;

    private String initialPath;

    private T dbObject;

    public T getDbObject() {
        return dbObject;
    }

    public int showDialog() {
        setLocationRelativeTo(getParent());
        pack();
        setVisible(true);
        return dialogResult;
    }

    private DbObjectDialog(Window parent, String title) {
        super(parent, title);

        setResizable(false);
        showTitlePanel(false);

        initComponents();
        initLayouts();
    }

    public DbObjectDialog(Window parent, String title, T object) {
        this(parent, title);
        dbObject = object;
        determineInitialPath();
        updateComponents();
    }

    private void determineInitialPath() {
//        if (T.getType(dbObject) == DbObject.TYPE_DISTRIBUTOR) initialPath = settings().getFileSettings().getImgDistributorsPath();
//        else if (T.getType(dbObject) == DbObject.TYPE_DIVISION) initialPath = settings().getFileSettings().getImgDivisionsPath();
//        else if (T.getType(dbObject) == DbObject.TYPE_PROJECT_TYPE) initialPath = settings().getFileSettings().getImgIdesPath();
//        else if (T.getType(dbObject) == DbObject.TYPE_ITEM) initialPath = settings().getFileSettings().getImgItemsPath();
//        else if (T.getType(dbObject) == DbObject.TYPE_MANUFACTURER) initialPath = settings().getFileSettings().getImgManufacturersPath();
//        else if (T.getType(dbObject) == DbObject.TYPE_PROJECT) initialPath = settings().getFileSettings().getImgProjectsPath();
//        else if (T.getType(dbObject) == DbObject.TYPE_ORDER) initialPath = settings().getFileSettings().getFileOrdersPath();
//        else {
//            initialPath = "home/";
//        }


        initialPath = "home/";

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
        browseIconButton = new JButton(imageResource.readIcon("Browse.Folder.SS"));
        browseIconButton.addActionListener(e -> {
            JFileChooser fileChooser = ImageFileChooser.getFileChooser();
            fileChooser.setCurrentDirectory(new File(initialPath));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fileChooser.showDialog(DbObjectDialog.this, "Open") == JFileChooser.APPROVE_OPTION) {
                iconPathTextField.setText(fileChooser.getSelectedFile().getName());
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
                dbObject.setIconPath(FileUtils.createIconPath(initialPath, iconPath));
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
        JPanel iconPathPanel = GuiUtils.createFileOpenPanel(iconPathTextField, browseIconButton);

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
    public void updateComponents(Object... object) {

    }
}
