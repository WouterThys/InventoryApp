package com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ItemDetailPanel extends ItemDetailPanelLayout {

    public ItemDetailPanel(OnItemDetailListener detailListener) {
        super(detailListener);
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

                updateHeader(selectedItem);
                updateData(selectedItem);
                updateRemarks(selectedItem);
                updateButtons(selectedItem);
            }
        }
    }

    private void updateHeader(Item item) {
        try {
            if (!item.getIconPath().isEmpty()) {
                Path path = Paths.get(settings().getFileSettings().getImgItemsPath(), item.getIconPath());
                iconLbl.setIcon(path.toString());
            } else {
                iconLbl.setIcon(imageResource.readImage("Items.Edit.Title"));
            }
        } catch (Exception e) {
            Status().setError("Failed to set item icon");
        }
        nameTf.setText(item.toString());
        descriptionTa.setText(item.getDescription());
        starRater.setRating(item.getRating());
    }

    private void updateData(Item item) {
        if (item.getCategoryId() > DbObject.UNKNOWN_ID) {
            categoryTf.setText(item.getCategory().toString());
        } else {
            categoryTf.setText("");
        }

        if (item.getProductId() > DbObject.UNKNOWN_ID) {
            productTf.setText(item.getProduct().toString());
        } else {
            productTf.setText("");
        }

        if (item.getTypeId() > DbObject.UNKNOWN_ID) {
            typeTf.setText(item.getType().toString());
        } else {
            typeTf.setText("");
        }

        if (item.getManufacturerId() > DbObject.UNKNOWN_ID) {
            manufacturerTf.setText(item.getManufacturer().toString());
        } else {
            manufacturerTf.setText("");
        }

        if (item.getPackageTypeId() > DbObject.UNKNOWN_ID) {
            footprintTf.setText(item.getPackageType().getPrettyString());
        } else {
            footprintTf.setText("");
        }

        if (item.getLocationId() > DbObject.UNKNOWN_ID) {
            locationTf.setText(item.getLocation().getPrettyString());
        } else {
            locationTf.setText("");
        }
    }

    private void updateRemarks(Item item) {
        remarksTp.setFile(item.getRemarksFile());
    }

    private void updateButtons(Item item) {
        if (item != null) {
            dataSheetBtn.setEnabled(!item.getLocalDataSheet().isEmpty() || !item.getOnlineDataSheet().isEmpty());
        }
    }

}
