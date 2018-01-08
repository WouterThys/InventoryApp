package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.dbclasses.*;
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

    private boolean canClose = true;

    public EditItemDialog(Application application, String title, Item item)  {
        this(application, title, item, null);
    }

    public EditItemDialog(Application application, String title, Item item, Set set)  {
        super(application, title);
        if (application != null) {
            setLocationRelativeTo(application);
        } else {
            setLocationByPlatform(true);
        }
        setValues(item, set);
        initializeComponents();
        initializeLayouts();
        initActions();
        updateComponents();
    }

    private void setValues(Item item, Set set) {
        selectedItem = item;
        originalItem = selectedItem.createCopy();
        selectedSet = set;
    }

    private void initActions() {
        // Top Panel
        initIconDoubleClicked();
        initTabChangedAction();

        // Component panel actions
        initCategoryChangedAction();
        initProductChangedAction();
    }

    private void save() {
        selectedItem.save();
        originalItem = selectedItem.createCopy();

        if (selectedSet != null) {
            selectedSet.addSetItem(selectedItem);
        }
    }

    @Override
    protected void onOK() {
        if (verify()) {
            if (checkChange()) {
                canClose = false;
                showSaveDialog();
            }

            if (canClose) {
                dialogResult = OK;
                dispose();
            }
        }
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            componentPanel.updateRemarks();
            save();

            getButtonNeutral().setEnabled(false);
            // Don't call update for just one component
            componentPanel.updateRating(selectedItem.getRating());
        }
    }

    @Override
    protected void onCancel() {
        originalItem.createCopy(selectedItem);
        selectedItem.setCanBeSaved(true);
        super.onCancel();
    }

    private boolean checkChange() {
        return (selectedItem != null) && (componentPanel.updateRemarks() || !(selectedItem.equals(originalItem)));
    }

    private void showSaveDialog() {
        if (selectedItem != null) {
            String msg = selectedItem.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    save();
                    super.onOK();
                }
            }
        } else {
            super.onOK();
        }
        canClose = true;
    }

    public Item getItem() {
        return selectedItem;
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
                            selectedItem.setIconPath(FileUtils.createIconPath(initialPath, iconPath));
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
        tabbedPane.addChangeListener(this::updateComponents);
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
            if (selectedItem.getId() < DbObject.UNKNOWN_ID) {
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
            return selectedItem;
        } else {
            return null;
        }
    }
}
