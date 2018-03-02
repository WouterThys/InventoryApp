package com.waldo.inventory.gui.dialogs.editpendingorderdialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.PendingOrder;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import static com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialogLayout.SearchType.SearchWord;
import static com.waldo.inventory.managers.CacheManager.cache;

abstract class EditPendingOrderDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField itemNameTf;
    private IActions.SearchAction searchAction;
    private IComboBox<Distributor> distributorCb;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PendingOrder pendingOrder;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditPendingOrderDialogLayout(Window window, String title, PendingOrder pendingOrder) {
        super(window, title);

        this.pendingOrder = pendingOrder;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        showTitlePanel(false);

        itemNameTf = new ITextField(false);
        searchAction = new IActions.SearchAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                        EditPendingOrderDialogLayout.this,
                        "Find item",
                        SearchWord);
                if (dialog.showDialog() == IDialog.OK) {
                    Item item = dialog.getSelectedItem();
                    if (item == null) {
                        pendingOrder.setItemId(DbObject.UNKNOWN_ID);
                        itemNameTf.setText("");
                    } else {
                        pendingOrder.setItemId(item.getId());
                        itemNameTf.setText(item.toString());
                    }
                }
            }
        };


        distributorCb = new IComboBox<>(cache().getDistributors(), new DbObjectNameComparator<>(), true);
        distributorCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Distributor distributor = (Distributor) distributorCb.getSelectedItem();
                if (distributor == null) {
                    pendingOrder.setDistributorId(DbObject.UNKNOWN_ID);
                } else {
                    pendingOrder.setDistributorId(distributor.getId());
                }
            }
        });
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel);

        gbc.addLineVertical("Item", GuiUtils.createComponentWithActions(itemNameTf, searchAction));
        gbc.addLineVertical("Distributor", distributorCb);

        panel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        getContentPanel().add(panel);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (pendingOrder != null) {
            if (pendingOrder.getItemId() > DbObject.UNKNOWN_ID) {
                itemNameTf.setText(pendingOrder.getItem().toString());
            }

            distributorCb.setSelectedItem(pendingOrder.getDistributor());
        }
    }
}