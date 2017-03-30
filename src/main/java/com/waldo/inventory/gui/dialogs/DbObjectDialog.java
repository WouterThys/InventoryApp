package com.waldo.inventory.gui.dialogs;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialogPanel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledEditPanel;
import com.waldo.inventory.gui.dialogs.imagefiledialog.ImageFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;

public class DbObjectDialog<T extends DbObject> extends IDialogPanel {

    private ITextField nameTextField;
    private ITextField iconPathTextField;
    private JButton browseIconButton;

    private T dbObject;
    private Action createAction;

//    public static DbObject showDialog(Application application, String title) {
//        dbObject = null;
//        JDialog dialog = new JDialog(application, title, true);
//        dialog.getContentPane().add(new DbObjectDialog(null, dialog));
//        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//        dialog.setLocationByPlatform(true);
//        dialog.setLocationRelativeTo(null);
//        dialog.setResizable(false);
//        dialog.pack();
//        dialog.setVisible(true);
//        return dbObject;
//    }

    public T getDbObject() {
        return dbObject;
    }

    public static int showDialog(Application application, String title, DbObject object) {
        JDialog dialog = new JDialog(application, title, true);
        dialog.getContentPane().add(new DbObjectDialog<>(application, dialog, object));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(application);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        return returnValue;
    }

    private DbObjectDialog(Application application, JDialog dialog) {
        super(application, dialog, false);
        initComponents();
        initLayouts();
    }

    private DbObjectDialog(Application application, JDialog dialog, T object) {
        this(application, dialog);
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
        browseIconButton = new JButton(resourceManager.readImage("Common.BrowseIcon"));
        browseIconButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = ImageFileChooser.getFileChooser();
                fileChooser.setCurrentDirectory(new File("./Images/"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (fileChooser.showDialog(DbObjectDialog.this, "Open") == JFileChooser.APPROVE_OPTION) {
                    iconPathTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        // Dialog buttons
        createAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (verify()) {
                    // Set values
                    dbObject.setName(nameTextField.getText());
                    dbObject.setIconPath(iconPathTextField.getText());
                    returnValue = OK;
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
