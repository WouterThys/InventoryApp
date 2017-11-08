package com.waldo.inventory.gui.dialogs.editreceiveditemlocationdialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILocationMapPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemListener;
import java.util.List;

public abstract class EditReceivedItemsLocationDialogLayout extends IDialog implements ItemListener, ListSelectionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    DefaultListModel<Item> listModel;
    JList<Item> itemList;

    ILocationMapPanel locationMapPanel;
    IComboBox<LocationType> locationTypeCb;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     List<Item> itemsWithoutLocation;

     Item selectedItem;



    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditReceivedItemsLocationDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        showTitlePanel(false);
        setResizable(true);

        // List
        listModel = new DefaultListModel<>();
        for (Item item : itemsWithoutLocation) {
            listModel.addElement(item);
        }
        itemList = new JList<>(listModel);
        itemList.addListSelectionListener(this);

        // Location
        locationMapPanel = new ILocationMapPanel(application, null, (e, location) -> {
            locationMapPanel.clearHighlights();
            locationMapPanel.setHighlighted(location, ILocationMapPanel.GREEN);
            if (selectedItem != null && location != null) {
                selectedItem.setLocationId(location.getId());
                SwingUtilities.invokeLater(() -> selectedItem.save());
            }
        }, true);

        locationTypeCb = new IComboBox<>(DbManager.db().getLocationTypes(), new DbObjectNameComparator<>(), true);
        locationTypeCb.addItemListener(this);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.X_AXIS));

        JPanel locationPanel = new JPanel(new BorderLayout());
        locationPanel.add(locationTypeCb, BorderLayout.NORTH);
        locationPanel.add(locationMapPanel, BorderLayout.CENTER);

        getContentPanel().add(new JScrollPane(itemList));
        getContentPanel().add(locationPanel);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {

    }
}