package com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.Application;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;
import static com.waldo.inventory.managers.SearchManager.sm;

public class ItemDetailPanel extends ItemDetailPanelLayout {

    public ItemDetailPanel(Application application, OnItemDetailListener detailListener) {
        super(application, detailListener);
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length == 0 || object[0] == null) {
            setVisible(false);
            selectedItem = null;
        } else {
            if (object[0] instanceof Item) {
                setVisible(true);

                selectedItem = (Item) object[0];

                updateIcon(selectedItem);
                updateTextFields(selectedItem);
                updateButtons(selectedItem);
            }
        }
    }

    public void setRemarksPanelVisible(boolean visible) {
        remarksPnl.setVisible(visible);
    }

    private void updateIcon(Item item) {
        try {
            Path path = Paths.get(settings().getFileSettings().getImgItemsPath(), item.getIconPath());
            iconLbl.setIcon(path.toString());
        } catch (Exception e) {
            Status().setError("Failed to set item icon");
        }
    }

    private void updateTextFields(Item item) {
        if (item != null) {
            nameTf.setText(item.getName());
            StringBuilder builder = new StringBuilder();

            if (item.getCategoryId() > DbObject.UNKNOWN_ID) {
                builder.append(" / ").append(sm().findCategoryById(item.getCategoryId()).getName());
                if (item.getProductId() > DbObject.UNKNOWN_ID) {
                    builder.append(" / ").append(sm().findProductById(item.getProductId()).getName());
                    if (item.getTypeId() > DbObject.UNKNOWN_ID) {
                        builder.append(" / ").append(sm().findTypeById(item.getTypeId()).getName());
                    }
                }
            }
            divisionTa.setText(builder.toString());

            if (item.getManufacturerId() > DbObject.UNKNOWN_ID) {
                manufacturerTf.setText(sm().findManufacturerById(item.getManufacturerId()).getName());
            } else {
                manufacturerTf.setText("");
            }

            descriptionTa.setText(item.getDescription());

            starRater.setRating(item.getRating());
            discourageOrderCb.setSelected(item.isDiscourageOrder());
            remarksTa.setText(item.getRemarks());
        }
    }

    private void updateButtons(Item item) {
        if (item != null) {
            dataSheetBtn.setEnabled(!item.getLocalDataSheet().isEmpty() || !item.getOnlineDataSheet().isEmpty());
        }
    }

}
