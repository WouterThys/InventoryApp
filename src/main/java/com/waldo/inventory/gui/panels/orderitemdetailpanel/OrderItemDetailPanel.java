package com.waldo.inventory.gui.panels.orderitemdetailpanel;

import com.waldo.inventory.classes.PartNumber;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;

public class OrderItemDetailPanel extends OrderItemDetailPanelLayout {

    public OrderItemDetailPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initActions();
        updateComponents(null);
    }

    private void initActions() {
        addBtn.addActionListener(e -> incrementAmount());
        minBtn.addActionListener(e -> decrementAmount());
        saveBtn.addActionListener(e -> saveOrderItem());

    }

    private void incrementAmount() {
        orderItem.setAmount(orderItem.getAmount() + 1);
        amountTf.setValue(orderItem.getAmount());
    }

    private void decrementAmount() {
        if (orderItem.getAmount() > 0) {
            orderItem.setAmount(orderItem.getAmount() - 1);
            amountTf.setValue(orderItem.getAmount());
        }
    }

    private void saveOrderItem() {
        if (orderItem != null) {
            String itemRef = itemRefTf.getText().trim();
            // Order item
            int amount = ((Number)amountTf.getValue()).intValue();
            orderItem.setAmount(amount);
            orderItem.setItemRef(itemRef);
            orderItem.save();
            // Reference tabel
            PartNumber number = DbManager.db().findPartNumber(orderItem.getOrder().getDistributor().getId(), orderItem.getItemId());
            if (number == null) {
                if (!itemRef.isEmpty()) {
                    number = new PartNumber(orderItem.getOrder().getDistributor().getId(), orderItem.getItemId());
                    number.setItemRef(itemRef);
                    number.save();
                }
            } else {
                number.setItemRef(itemRef);
                number.save();
            }
        }
    }
}
