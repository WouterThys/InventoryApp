package com.waldo.inventory.gui.panels.mainpanel.preview.itemdetailpanel;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.ItemOrderLine;
import com.waldo.inventory.gui.panels.mainpanel.ItemDetailListener;
import com.waldo.inventory.gui.panels.mainpanel.OrderDetailListener;

import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ItemDetailPanel extends ItemDetailPanelLayout {

    public ItemDetailPanel(ItemDetailListener detailListener) {
        super(detailListener, null);
    }

    public ItemDetailPanel(ItemDetailListener itemDetailListener, OrderDetailListener orderDetailListener) {
        super(itemDetailListener, orderDetailListener);
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length == 0 || object[0] == null) {
            setVisible(false);
            selectedItem = null;
            selectedItemOrderLine = null;
        } else {
            setVisible(true);
            if (object[0] instanceof Item) {
                selectedItem = (Item) object[0];
                selectedItemOrderLine = null;
            } else {
//                selectedItemOrderLine = (ItemOrderLine) object[0];
//                selectedItem = selectedItemOrderLine.getItem();
            }
            updateHeader(selectedItem);
            updateData(selectedItem, selectedItemOrderLine);
            updateRemarks(selectedItem);
            updateButtons(selectedItem);
        }
    }

    private void updateHeader(Item item) {
        try {
            //iconLbl.setIcon(item.getItemIcon());
        } catch (Exception e) {
            Status().setError("Failed to set item icon");
        }
        nameTf.setText(item.toString());
        descriptionTa.setText(item.getDescription());
        starRater.setRating(item.getRating());
    }

    private void updateData(Item item, ItemOrderLine itemOrderLine) {
        if (isOrderType) {
            amountTf.setText(String.valueOf(itemOrderLine.getAmount()));
            if (itemOrderLine.getDistributorPartId() > DbObject.UNKNOWN_ID) {
                priceTf.setText(itemOrderLine.getPrice().toString());
                referenceTf.setText(itemOrderLine.getDistributorPartLink().getReference());
            }
            boolean locked = itemOrderLine.isLocked();
            editPriceAction.setEnabled(!locked);
            editReferenceAction.setEnabled(!locked);
            plusOneAction.setEnabled(!locked);
            minOneAction.setEnabled(!locked);
        } else {
//            if (item.getCategoryId() > DbObject.UNKNOWN_ID) {
//                categoryTf.setText(item.getCategory().toString());
//            } else {
//                categoryTf.setText("");
//            }
//
//            if (item.getProductId() > DbObject.UNKNOWN_ID) {
//                productTf.setText(item.getProduct().toString());
//            } else {
//                productTf.setText("");
//            }
//
//            if (item.getTypeId() > DbObject.UNKNOWN_ID) {
//                typeTf.setText(item.getDivision().toString());
//            } else {
//                typeTf.setText("");
//            }
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
        discourageOrderCb.setSelected(item.isDiscourageOrder());
    }

    private void updateButtons(Item item) {
        if (item != null) {
            dataSheetBtn.setEnabled(!item.getLocalDataSheet().isEmpty() || !item.getOnlineDataSheet().isEmpty());
        }
    }

}
