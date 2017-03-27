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
        dialog.setResizable(false);
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
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        return newItem;
    }

    private EditItemDialog(Application application, JDialog dialog, Item item) throws SQLException {
        super(application, dialog);
        newItem = item;
        isNew = false;
        initActions();
        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    private EditItemDialog(Application application, JDialog dialog) throws SQLException {
        this(application, dialog, new Item());
        isNew = false;
    }

    private void initActions() {
        initIconDoubleClicked();
        initLocalDataSheetAction();
        initCreateAction();
        initCancelAction();
        initCategoryChangedAction();
        initProductChangedAction();
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
    private void initLocalDataSheetAction() {
        localDataSheetAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                localDataSheetFileChooser.setCurrentDirectory(new File("."));
                localDataSheetFileChooser.setDialogTitle("Select the data sheet");
                localDataSheetFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (localDataSheetFileChooser.showOpenDialog(EditItemDialog.this) == JFileChooser.APPROVE_OPTION) {
                    localDataSheetTextField.setText(localDataSheetFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        };
    }
    private void initCreateAction() {
        createAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (verify()) {
                    // Create item
                    newItem.setName(nameTextField.getText());
                    newItem.setDescription(descriptionTextArea.getText());
                    String priceTxt = priceTextField.getText();
                    if (!priceTxt.isEmpty()) {
                        newItem.setPrice(Double.valueOf(priceTxt));
                    }

                    try {
                        newItem.setCategoryId(getCategoryId());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        newItem.setProductId(getProductId());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        newItem.setTypeId(getTypeId());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                    newItem.setLocalDataSheet(localDataSheetTextField.getText());
                    newItem.setOnlineDataSheet(onlineDataSheetTextField.getText());

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
    private void initCategoryChangedAction() {
        categoryChangedAction = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Category selectedCategory = (Category) e.getItem();
                if (productComboBox != null) {
                    productComboBox.setEnabled(selectedCategory.getId() > UNKNOWN_ID); // Bigger than "UNKNOWN"

                    productCbModel.removeAllElements();
                    try {
                        productCbModel.addElement(dbInstance().getProducts().get(0)); // Add unknown
                        for (Product p : dbInstance().getProductListForCategory(selectedCategory.getId())) {
                            productCbModel.addElement(p);
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                }
                if (typeComboBox != null) {
                    typeComboBox.setEnabled(selectedCategory.getId() > 1);
                }
            }
        };
    }
    private void initProductChangedAction() {
        productChangedAction = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Product selectedProduct = (Product) e.getItem();
                if (typeComboBox != null) {
                    typeComboBox.setEnabled(selectedProduct.getId() > 1); // Bigger than "UNKNOWN"

                    typeCbModel.removeAllElements();
                    try {
                        typeCbModel.addElement(dbInstance().getTypes().get(0)); // Add unknown
                        for (Type t : dbInstance().getTypeListForProduct(selectedProduct.getId())) {
                            typeCbModel.addElement(t);
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
    }

    private boolean verify() {
        boolean ok = true;

        String name = nameTextField.getText();
        if (name.isEmpty()) {
            nameTextField.setError("Name can not be empty");
            ok = false;
        } else {
            try {
                Item check = dbInstance().findItemByName(name);
                if (check != null) {
                    nameTextField.setError("Name already exists in items");
                    ok = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String price = priceTextField.getText();
        try {
            Double.valueOf(price);
        } catch (Exception e) {
            priceTextField.setError("This should be a number");
            ok = false;
        }
        return ok;
    }

    private long getCategoryId() throws SQLException {
        int ndx = categoryComboBox.getSelectedIndex();
        if (ndx >= 0) {
            return dbInstance().getCategories().get(ndx).getId();
        } else {
            return UNKNOWN_ID;
        }
    }

    private long getProductId() throws SQLException {
        if (productComboBox.isEnabled()) {
            int ndx = productComboBox.getSelectedIndex();
            if (ndx >= 0) {
                return dbInstance().getProducts().get(ndx).getId();
            } else {
                return UNKNOWN_ID;
            }
        } else {
            return UNKNOWN_ID;
        }
    }

    private long getTypeId() throws SQLException {
        if (typeComboBox.isEnabled()) {
            int ndx = typeComboBox.getSelectedIndex();
            if (ndx >= 0) {
                return dbInstance().getTypes().get(ndx).getId();
            } else {
                return UNKNOWN_ID;
            }
        } else {
            return UNKNOWN_ID;
        }
    }

}
