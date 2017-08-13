package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.classes.Package;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.filechooserdialog.ImageFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

import static com.waldo.inventory.classes.DbObject.UNKNOWN_ID;
import static com.waldo.inventory.database.SearchManager.sm;
import static com.waldo.inventory.gui.Application.imageResource;

public class EditItemDialog extends EditItemDialogLayout {

    private int currentTabIndex = 0;
    private boolean canClose = true;
    private boolean partNumberChanged = false;

    public EditItemDialog(Application application, String title, Item item)  {
        super(application, title);
        if (application != null) {
            setLocationRelativeTo(application);
        } else {
            setLocationByPlatform(true);
        }
        setValues(item);
        isNew = false;
        initializeComponents();
        initializeLayouts();
        initListeners();
        initActions();
        updateComponents(null);
    }

    public EditItemDialog(Application application, String title) {
        this(application, title, new Item());
        isNew = true;
    }

    private void setValues(Item item) {
        newItem = item;
        originalItem = newItem.createCopy();

        newPackage = item.getPackage();
        if (newPackage != null)  {
            originalPackage = newPackage.createCopy();
        } else {
            originalPackage = null;
        }
    }

    private void initActions() {
        // Top Panel
        initIconDoubleClicked();
        initTabChangedAction();

        // Component panel actions
        initCategoryChangedAction();
        initProductChangedAction();
    }

    private void initListeners() {
        DbManager.db().addOnPackageChangedListener(new DbObjectChangedListener<Package>() {
            @Override
            public void onInserted(Package p) {
                newItem.setPackageId(p.getId());
                newItem.save();
                originalItem = newItem.createCopy();
            }

            @Override
            public void onUpdated(Package p) {
                componentPanel.updateComponents(null);
            }

            @Override
            public void onDeleted(Package p) {
                componentPanel.updateComponents(null);
            }
        });
    }

    @Override
    protected void onOK() {
        if (checkChange()) {
            canClose = false;
            showSaveDialog(true);
        }

        if (canClose) {
            dialogResult = OK;
            dispose();
        }
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            // Part number
            if (partNumberChanged) {
                editItemOrderPanel.setPartNumber();
                partNumberChanged = false;
            }
            // Package
            if (newPackage != null) {
                newPackage.save();
                originalPackage = newPackage.createCopy();
            }
            // Location
            newItem.setLocationId(editItemStockPanel.getLocationId());

            newItem.save();
            originalItem = newItem.createCopy();

            getButtonNeutral().setEnabled(false);
            // Don't call update for just one component
            componentPanel.updateRating(newItem.getRating());
        }
    }

    @Override
    protected void onCancel() {
        originalItem.createCopy(newItem);
        newItem.setCanBeSaved(true);
        super.onCancel();
    }

    private boolean checkChange() {
        boolean itemChange = (newItem != null) && !(newItem.equals(originalItem));
        boolean packageChange = (newPackage != null) && !(newPackage.equals(originalPackage));
        return itemChange || packageChange;
    }

    private void showSaveDialog(boolean closeAfter) {
        if (newItem != null) {
            String msg = newItem.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    newItem.save();
                    originalItem = newItem.createCopy();
                    if (closeAfter) {
                        dispose();
                    }
                }
            }
        } else {
            if (closeAfter) {
                dialogResult = OK;
                dispose();
            }
        }
        canClose = true;
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
                            lbl.setIcon(imageResource.readImage(url, 48,48));
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    private void initTabChangedAction() {
        tabbedPane.addChangeListener(e -> {

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
                Item check = sm().findItemByName(name);
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


    //
    // Values changed
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        if (fieldName.equals("Name")) {
            getTitleNameLabel().setText(String.valueOf(newValue));
        }
        // Package
        if (newPackage == null &&
                (fieldName.equals("PackageTypeId") || fieldName.equals("Pins") || fieldName.equals("Height") || fieldName.equals("Width"))) {
            newPackage = newItem.getPackage();
        }
            // Distributor part
            if (editItemOrderPanel.getItemRefField().equals(component)) {
                partNumberChanged = editItemOrderPanel.checkChange();
                getButtonNeutral().setEnabled(partNumberChanged);
            } else {
                getButtonNeutral().setEnabled(checkChange());
            }

            // Dimensions
            if (componentPanel.getPackageTypeCb().equals(component)) {
                componentPanel.updateDimensionPanel();
            }

    }

    @Override
    public DbObject getGuiObject() {
        if (initialized) {
            return newItem;
        } else {
            return null;
        }
    }
}
