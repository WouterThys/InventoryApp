package com.waldo.inventory.gui.dialogs.subdivisionsdialog;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.classes.Type;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialogPanel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledEditPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;
import static com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialogLayout.CATEGORIES;
import static com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialogLayout.PRODUCTS;
import static com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialogLayout.TYPES;

public class AddNewSubDivisionDialog extends IDialogPanel {

    private ITextField nameTextField;
    private ITextField iconPathTextField;
    private JButton browseIconButton;
    private JFileChooser iconPathChooser;

    private static DbObject dbObject;
    private int objectType;

    private Action createAction;

    static DbObject showDialog(Application application, int type) {
        String title;
        switch (type) {
            case CATEGORIES: title = "Add category"; break;
            case PRODUCTS: title = "Add product"; break;
            case TYPES: title = "Add type"; break;
            default: title = "Add";
        }
        dbObject = null;
        JDialog dialog = new JDialog(application, title, true);
        dialog.getContentPane().add(new AddNewSubDivisionDialog(application, dialog, type));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(application);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        return dbObject;
    }

    static DbObject showDialog(Application application, int type, DbObject object) {
        String title;
        switch (type) {
            case CATEGORIES: title = "Edit " + object.getName(); break;
            case PRODUCTS: title = "Edit " + object.getName(); break;
            case TYPES: title = "Edit " + object.getName(); break;
            default: title = "Edit";
        }
        dbObject = null;
        JDialog dialog = new JDialog(application, title, true);
        dialog.getContentPane().add(new AddNewSubDivisionDialog(application, dialog, type, object));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(application);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        return dbObject;
    }

    private AddNewSubDivisionDialog(Application application, JDialog dialog, int type) {
        super(application, dialog);
        objectType = type;
        initComponents();
        initLayouts();
    }

    private AddNewSubDivisionDialog(Application application, JDialog dialog, int type, DbObject object) {
        this(application, dialog, type);
        dbObject = object;
        updateComponents();
    }

    private void updateComponents() {
        if (dbObject != null) {
            nameTextField.setText(dbObject.getName());
            iconPathTextField.setText(dbObject.getIconPath());
            positiveButton.setText("Update");
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
        iconPathChooser = new JFileChooser();
        browseIconButton = new JButton(resourceManager.readImage("Common.BrowseIcon"));
        browseIconButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "png", "jpeg");
                iconPathChooser.setCurrentDirectory(new File("."));
                iconPathChooser.setDialogTitle("Select the data sheet");
                iconPathChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (iconPathChooser.showOpenDialog(AddNewSubDivisionDialog.this) == JFileChooser.APPROVE_OPTION) {
                    iconPathTextField.setText(iconPathChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        // Dialog buttons
        createAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (verify()) {
                    // Set values
                    if (dbObject == null) {
                        switch (objectType) {
                            case 0:
                                dbObject = new Category();
                                break;
                            case 1:
                                dbObject = new Product();
                                break;
                            case 2:
                                dbObject = new Type();
                                break;
                        }
                    }
                    dbObject.setName(nameTextField.getText());
                    dbObject.setIconPath(iconPathTextField.getText());
                    // Close dialog
                    close();
                }
            }
        };
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

        // Dialog buttons
        setNegativeButton("Cancel");
        setPositiveButton("Create").addActionListener(createAction);
    }
}
