package com.waldo.inventory.gui.panels.mainpanel.preview.itemdetailpanel;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.OrderLine;
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
            selectedOrderLine = null;
        } else {
            setVisible(true);
            if (object[0] instanceof Item) {
                selectedItem = (Item) object[0];
                selectedOrderLine = null;
            } else {
                selectedOrderLine = (OrderLine) object[0];
                selectedItem = selectedOrderLine.getItem();
            }
            updateHeader(selectedItem);
            updateData(selectedItem, selectedOrderLine);
            updateRemarks(selectedItem);
            updateButtons(selectedItem);
        }
    }

    private void updateHeader(Item item) {
        try {
            iconLbl.setIcon(item.getItemIcon());
        } catch (Exception e) {
            Status().setError("Failed to set item icon");
        }
        nameTf.setText(item.toString());
        descriptionTa.setText(item.getDescription());
        starRater.setRating(item.getRating());
    }

    private void updateData(Item item, OrderLine orderLine) {
        if (isOrderType) {
            amountTf.setText(String.valueOf(orderLine.getAmount()));
            if (orderLine.getDistributorPartId() > DbObject.UNKNOWN_ID) {
                priceTf.setText(orderLine.getPrice().toString());
                referenceTf.setText(orderLine.getDistributorPartLink().getReference());
            }
            boolean locked = orderLine.isLocked();
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
