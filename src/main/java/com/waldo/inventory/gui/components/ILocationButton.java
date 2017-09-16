package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Location;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class ILocationButton extends JButton {

    private Location location;

    public ILocationButton(Location location) {
        super();

        this.location = location;
        setText(location.getName());
        setName(location.getName());
    }

    @Override
    public String toString() {
        return getName() + "(" + location.getRow() +"," + location.getCol() + ")";
    }

    public Location getTheLocation() {
        return location;
    }

    public List<DbObject> getItems() {
        return location.getItems();
    }

    public void showPopup(MouseEvent e, Application application) {
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
        for (DbObject item : location.getItems()) {

            JMenuItem menu;
            if (item instanceof Item) {
                menu = new JMenuItem(item.getName());
            } else if (item instanceof SetItem) {
                menu = new JMenuItem(item.getName() + " - " +((SetItem) item).getValue());
            } else {
                menu = null;
            }
            if (menu != null) {
                menu.addActionListener(e -> {
                    if (item instanceof Item) {
                        EditItemDialog dialog = new EditItemDialog(application, "Item", (Item) item);
                        dialog.showDialog();
                    } else {
                        EditItemDialog dialog = new EditItemDialog(application, "Item", ((SetItem) item).getItem());
                        dialog.showDialog();
                    }
                });
                popupMenu.add(menu);
            }
        }
    }

    public int getRow() {
        return location.getRow();
    }

    public int getCol() {
        return location.getCol();
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