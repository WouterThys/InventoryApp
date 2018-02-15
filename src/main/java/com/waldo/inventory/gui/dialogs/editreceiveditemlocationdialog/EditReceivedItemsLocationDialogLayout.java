package com.waldo.inventory.gui.dialogs.editreceiveditemlocationdialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.components.ILocationMapPanel;
import com.waldo.inventory.managers.CacheManager;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemListener;
import java.util.List;

abstract class EditReceivedItemsLocationDialogLayout extends IDialog implements
        ItemListener, ListSelectionListener, ILocationMapPanel.LocationClickListener {

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
    EditReceivedItemsLocationDialogLayout(Window parent, String title) {
        super(parent, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Item getItem(long id) {
        for (Item item : itemsWithoutLocation) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        showTitlePanel(false);
        setResizable(true);
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setEnabled(false);

        // List
        DefaultListModel<Item> listModel = new DefaultListModel<>();
        for (Item item : itemsWithoutLocation) {
            listModel.addElement(item);
        }
        itemList = new JList<>(listModel);
        itemList.addListSelectionListener(this);

        // Location
        locationMapPanel = new ILocationMapPanel(this, null,this , true);

        locationTypeCb = new IComboBox<>(CacheManager.cache().getLocationTypes(), new DbObjectNameComparator<>(), true);
        locationTypeCb.addItemListener(this);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.X_AXIS));

        JPanel locationPanel = new JPanel(new BorderLayout());
        locationPanel.add(locationTypeCb, BorderLayout.NORTH);
        locationPanel.add(locationMapPanel, BorderLayout.CENTER);

        JScrollPane pane = new JScrollPane(itemList);
        pane.setPreferredSize(new Dimension(120,200));

        getContentPanel().add(pane);
        getContentPanel().add(locationPanel);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (itemsWithoutLocation != null && itemsWithoutLocation.size() > 0) {
            itemList.setSelectedIndex(0);
        }
    }
}