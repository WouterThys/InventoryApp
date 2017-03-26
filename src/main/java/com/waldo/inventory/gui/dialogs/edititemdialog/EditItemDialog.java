package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.Application;
import sun.awt.WindowClosingListener;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import static com.waldo.inventory.database.DbManager.dbInstance;

public class EditItemDialog extends EditItemDialogLayout {


    public static Item showDialog(Application parent) throws SQLException {
        EditItemDialog.application = parent;
        dialog = new JDialog(parent, "Create new Item", true);
        EditItemDialog layout = new EditItemDialog();
        dialog.getContentPane().add(layout);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        return newItem;
    }

    public static Item showDialog(Application parent, Item item) throws SQLException {
        EditItemDialog.application = parent;
        dialog = new JDialog(parent, "Edit Item", true);
        dialog.getContentPane().add(new EditItemDialog(item));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        return newItem;
    }

    private EditItemDialog(Item item) throws SQLException {
        super();
        newItem = item;
        isNew = false;
        initActions();
        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    private EditItemDialog() throws SQLException {
        this(new Item());
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
                        newItem.setCategory(getCategoryId());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        newItem.setProduct(getProductId());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        newItem.setType(getTypeId());
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
        categoryChangedAction = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Category selectedCategory = (Category) e.getItem();
                    if (productComboBox != null) {
                        productComboBox.setEnabled(selectedCategory.getId() > 1); // Bigger than "UNKNOWN"

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
            }
        };
    }
    private void initProductChangedAction() {
        productChangedAction = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
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
            }
        };
    }

    private boolean verify() {
        boolean ok = true;

        String name = nameTextField.getText();
        if (name.isEmpty()) {
            nameTextField.setError("Name can not be empty");
            ok = false;
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
            return DbObject.UNKNOWN;
        }
    }

    private long getProductId() throws SQLException {
        if (productComboBox.isEnabled()) {
            int ndx = productComboBox.getSelectedIndex();
            if (ndx >= 0) {
                return dbInstance().getProducts().get(ndx).getId();
            } else {
                return DbObject.UNKNOWN;
            }
        } else {
            return DbObject.UNKNOWN;
        }
    }

    private long getTypeId() throws SQLException {
        if (typeComboBox.isEnabled()) {
            int ndx = typeComboBox.getSelectedIndex();
            if (ndx >= 0) {
                return dbInstance().getTypes().get(ndx).getId();
            } else {
                return DbObject.UNKNOWN;
            }
        } else {
            return DbObject.UNKNOWN;
        }
    }

}
