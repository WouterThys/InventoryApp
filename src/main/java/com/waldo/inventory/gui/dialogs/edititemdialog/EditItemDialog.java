package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import static com.waldo.inventory.classes.DbObject.UNKNOWN_ID;
import static com.waldo.inventory.database.DbManager.dbInstance;

public class EditItemDialog extends EditItemDialogLayout {

    public static Item showDialog(Application parent) throws SQLException {
        newItem = null;
        JDialog dialog = new JDialog(parent, "Create new Item", true);
        EditItemDialog layout = new EditItemDialog(parent, dialog);
        dialog.getContentPane().add(layout);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                newItem = null;
                super.windowClosing(e);
            }
        });
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(parent);
        dialog.pack();
        dialog.setVisible(true);
        return newItem;
    }

    public static Item showDialog(Application parent, Item item) throws SQLException {
        newItem = null;
        JDialog dialog = new JDialog(parent, "Edit Item", true);
        dialog.getContentPane().add(new EditItemDialog(parent, dialog, item));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                newItem = null;
                super.windowClosing(e);
            }
        });
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(parent);
        dialog.pack();
        dialog.setVisible(true);
        return newItem;
    }

    private EditItemDialog(Application application, JDialog dialog, Item item) throws SQLException {
        super(application, dialog);
        newItem = item;
        isNew = false;
        initializeComponents();
        initActions();
        initializeLayouts();
        updateComponents(null);
    }

    private EditItemDialog(Application application, JDialog dialog) throws SQLException {
        this(application, dialog, new Item());
        isNew = false;
    }

    private void initActions() {
        // Component panel actions
        initIconDoubleClicked();
        initCreateAction();
        initCancelAction();
        initCategoryChangedAction();
        initProductChangedAction();
    }

    private void initCreateAction() {
        createAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (verify()) {
                    // Create item
                    newItem.setName(componentPanel.getNameFieldValue());
                    newItem.setDescription(componentPanel.getDescriptionFieldValue());
                    String priceTxt = componentPanel.getPriceFieldValue();
                    if (!priceTxt.isEmpty()) {
                        newItem.setPrice(Double.valueOf(priceTxt));
                    }

                    try {
                        newItem.setCategoryId(componentPanel.getCbCategoryId());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        newItem.setProductId(componentPanel.getCbProductId());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        newItem.setTypeId(componentPanel.getCbTypeId());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                    newItem.setLocalDataSheet(componentPanel.getLocalDataSheetFieldValue());
                    newItem.setOnlineDataSheet(componentPanel.getOnlineDataSheetFieldValue());

                    // Close dialog
                    close();
                }
            }
        };
    }
    private void initCancelAction() {
        cancelAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newItem = null;
                close();
            }
        };
    }

    private void initIconDoubleClicked() {
        titleIconDoubleClicked = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JLabel lbl = (JLabel)e.getSource();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "png", "jpeg");
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(filter);
                    fileChooser.setCurrentDirectory(new File("./Images/ItemImages/"));
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    if (fileChooser.showOpenDialog(EditItemDialog.this) == JFileChooser.APPROVE_OPTION) {
                        newItem.setIconPath(fileChooser.getSelectedFile().getAbsolutePath());
                        try {
                            URL url = fileChooser.getSelectedFile().toURI().toURL();
                            lbl.setIcon(resourceManager.readImage(url, 48,48));
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    private void initCategoryChangedAction() {
        componentPanel.setCategoryChangedAction(
                e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        Category selectedCategory = (Category) e.getItem();
                        if (componentPanel.getProductComboBox() != null) {
                            componentPanel.getProductComboBox().setEnabled(selectedCategory.getId() > UNKNOWN_ID); // Bigger than "UNKNOWN"
                            componentPanel.updateProductCbValues(selectedCategory.getId());
                        }
                        if (componentPanel.getTypeComboBox() != null) {
                            componentPanel.getTypeComboBox().setEnabled(selectedCategory.getId() > 1);
                        }
                    }
                });
    }
    private void initProductChangedAction() {
        componentPanel.setProductChangedAction(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Product selectedProduct = (Product) e.getItem();
                if (componentPanel.getTypeComboBox() != null) {
                    componentPanel.getTypeComboBox().setEnabled(selectedProduct.getId() > 1); // Bigger than "UNKNOWN"
                    componentPanel.updateTypeCbValues(selectedProduct.getId());
                }
            }
        });
    }

    private boolean verify() {
        boolean ok = true;

        String name = componentPanel.getNameFieldValue();
        if (name.isEmpty()) {
            componentPanel.setNameFieldError("Name can not be empty");
            ok = false;
        } else {
            try {
                Item check = dbInstance().findItemByName(name);
                if (check != null) {
                    componentPanel.setNameFieldError("Name already exists in items");
                    ok = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String price = componentPanel.getPriceFieldValue();
        try {
            Double.valueOf(price);
        } catch (Exception e) {
            componentPanel.setNameFieldError("This should be a number");
            ok = false;
        }
        return ok;
    }



}
