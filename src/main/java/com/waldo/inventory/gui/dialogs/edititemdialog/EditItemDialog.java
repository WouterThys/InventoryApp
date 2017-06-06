package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.imagefiledialog.ImageFileChooser;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.waldo.inventory.classes.DbObject.UNKNOWN_ID;
import static com.waldo.inventory.database.DbManager.db;

public class EditItemDialog extends EditItemDialogLayout {

    private static final int COMPONENT_TAB = 0;
    private static final int STOCK_TAB = 1;
    private static final int MANUFACTURER_TAB = 2;
    private static final int ORDER_TAB = 3;

    private int currentTabIndex = 0;

    public EditItemDialog(Application application, String title, Item item)  {
        super(application, title);
        if (application != null) {
            setLocationRelativeTo(application);
        } else {
            setLocationByPlatform(true);
        }
        newItem = item;
        isNew = false;
        initializeComponents();
        initializeLayouts();
        initActions();
        updateComponents(null);
    }

    public EditItemDialog(Application application, String title) {
        this(application, title, new Item());
        isNew = true;
    }

    private void initActions() {
        // Top Panel
        initIconDoubleClicked();
        initTabChangedAction();

        // Component panel actions
        initCategoryChangedAction();
        initProductChangedAction();
    }

    @Override
    protected void onOK() {
        if (verify()) {
            componentPanel.setComponentValues();
            editItemManufacturerPanel.setComponentValues();
            editItemStockPanel.setComponentValues();
            editItemOrderPanel.setComponentValues();

            // Close dialog
            dialogResult = OK;
            dispose();
        }
    }

    @Override
    protected void onCancel() {
        newItem = null;
        dialogResult = CANCEL;
        dispose();
    }

    public Item getItem() {
        return newItem;
    }

    private void initIconDoubleClicked() {
        getTitleIconLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JLabel lbl = (JLabel)e.getSource();

                    JFileChooser fileChooser = ImageFileChooser.getFileChooser();
                    fileChooser.setCurrentDirectory(new File("./Images/ItemImages/"));
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    if (fileChooser.showDialog(EditItemDialog.this, "Open") == JFileChooser.APPROVE_OPTION) {
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
        });
    }
    private void initTabChangedAction() {
        tabbedPane.addChangeListener(e -> {
            // Save values depending on tab
            switch (currentTabIndex) {
                case COMPONENT_TAB:
                    componentPanel.setComponentValues();
                    break;
                case STOCK_TAB:
                    editItemManufacturerPanel.setComponentValues();
                    break;
                case MANUFACTURER_TAB:
                    editItemStockPanel.setComponentValues();
                    break;
                case ORDER_TAB:
                    editItemOrderPanel.setComponentValues();
                    break;
            }

            currentTabIndex = tabbedPane.getSelectedIndex();

            updateComponents(null);
        });
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

        String name = componentPanel.getNameFieldValue().trim();
        if (name.isEmpty()) {
            componentPanel.setNameFieldError("Name can not be empty");
            ok = false;
        } else {
            if (isNew) {
                Item check = db().findItemByName(name);
                if (check != null) {
                    componentPanel.setNameFieldError("Name already exists in items");
                    ok = false;
                }
            }
        }

        String price = componentPanel.getPriceFieldValue();
        try {
            Double.valueOf(price);
        } catch (Exception e) {
            componentPanel.setPriceFieldError("This should be a number");
            ok = false;
        }
        return ok;
    }



}
