package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class ILocationButton extends JButton {

    private final Location location;

    ILocationButton(Location location) {
        super();

        this.location = location;
        setText(location.getName());
        setName(location.getName());

        if (!location.getAlias().isEmpty()) {
            setToolTipText(location.getAlias());
        }
    }

    @Override
    public String toString() {
        return getName() + "(" + location.getRow() +"," + location.getCol() + ")";
    }

    public Location getTheLocation() {
        return location;
    }

    public List<Item> getItems() {
        return location.getItems();
    }

    void showPopup(MouseEvent e, Application application) {
        if (location.getItems().size() > 0) {
            Component component = e.getComponent();
            JPopupMenu popupMenu = new JPopupMenu();

            if (!location.getAlias().isEmpty()) {
                JMenuItem menu = new JMenuItem(location.getAlias());
                menu.setEnabled(false);
                popupMenu.add(menu);
                popupMenu.addSeparator();
            }

            addItemsToPopup(popupMenu, application);
            popupMenu.show(component, 0, component.getHeight());
        }
    }

    private void addItemsToPopup(JPopupMenu popupMenu, Application application) {
        for (Item item : location.getItems()) {

            JMenuItem menu = new JMenuItem(item.getName());
                menu.addActionListener(e -> {
                        EditItemDialog dialog = new EditItemDialog<>(application, "Item", item);
                        dialog.showDialog();

                });
                popupMenu.add(menu);

        }
    }

    public int getRow() {
        return location.getRow();
    }

    public int getCol() {
        return location.getCol();
    }

    Location.LocationLayout getBtnLayout() {
        return location.getLayout();
    }

    @Override
    public String getName() {
        if (location != null) {
            return location.getName();
        } else {
            return super.getName();
        }
    }

    @Override
    public String getText() {
        return getName();
    }
}