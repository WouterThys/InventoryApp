package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.classes.Package;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.dialogs.filechooserdialog.ImageFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import static com.waldo.inventory.managers.SearchManager.sm;

public class EditItemDialog extends EditItemDialogLayout {

    private int currentTabIndex = 0;
    private boolean canClose = true;
    private boolean partNumberChanged = false;
    private boolean locationChanged = false;

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
        updateComponents();
    }

    public EditItemDialog(Application application, String title) {
        this(application, title, new Item());
        isNew = true;
    }

    private void setValues(Item item) {
        newItem = item;
        originalItem = newItem.createCopy();
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
                newItem.setPackageTypeId(p.getId());
                newItem.save();
                originalItem = newItem.createCopy();
            }

            @Override
            public void onUpdated(Package p) {
                //componentPanel.updateComponents(null);
            }

            @Override
            public void onDeleted(Package p) {
                componentPanel.updateComponents();
            }

            @Override
            public void onCacheCleared() {}
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
        return (newItem != null) && !(newItem.equals(originalItem));
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
                    ILabel lbl = (ILabel)e.getSource();

                    String initialPath = SettingsManager.settings().getFileSettings().getImgItemsPath();

                    JFileChooser fileChooser = ImageFileChooser.getFileChooser();
                    fileChooser.setCurrentDirectory(new File(initialPath));
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    if (fileChooser.showDialog(EditItemDialog.this, "Open") == JFileChooser.APPROVE_OPTION) {
                        String iconPath = fileChooser.getSelectedFile().getPath();
                        if (!iconPath.isEmpty()) {
                            newItem.setIconPath(FileUtils.createIconPath(initialPath, iconPath));
                            try {
                                lbl.setIcon(iconPath, 48,48);
                                onValueChanged(lbl, "iconPath", "", iconPath);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }
    private void initTabChangedAction() {
        tabbedPane.addChangeListener(e -> {

            currentTabIndex = tabbedPane.getSelectedIndex();

            updateComponents();
        });
    }

    private void initCategoryChangedAction() {
        componentPanel.setCategoryChangedAction(
                e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        Category selectedCategory = (Category) e.getItem();
                        componentPanel.getProductComboBox().setEnabled(!selectedCategory.isUnknown());
                        componentPanel.updateProductCbValues(selectedCategory.getId());
                        componentPanel.getTypeComboBox().setEnabled(false);
                    }
                });
    }
    private void initProductChangedAction() {
        componentPanel.setProductChangedAction(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Product selectedProduct = (Product) e.getItem();
                componentPanel.getTypeComboBox().setEnabled(!selectedProduct.isUnknown());
                componentPanel.updateTypeCbValues(selectedProduct.getId());
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
        getButtonNeutral().setEnabled(checkChange());
    }

    @Override
    public DbObject getGuiObject() {
        if (isShown) {
            return newItem;
        } else {
            return null;
        }
    }
}
