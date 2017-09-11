package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ILocationButton extends JButton {

    private List<DbObject> items;
    private JPopupMenu popupMenu;

    private int r;
    private int c;
    private int w;
    private int h;

    public ILocationButton(int row, int col) {
        super();

        this.r = row;
        this.c = col;

        w = 1;
        h = 1;

        items = new ArrayList<>();
        popupMenu = new JPopupMenu();
        updateName();
    }

    @Override
    public String toString() {
        return getName() + "(" + r +"," + c + ")";
    }

    public List<DbObject> getItems() {
        return items;
    }

    public void addItem(Application application, Item item) {
        items.add(item);
        JMenuItem menu = new JMenuItem(item.getName());
        menu.addActionListener(e -> {
            EditItemDialog dialog = new EditItemDialog(application, "Item", item);
            dialog.showDialog();
        });
        popupMenu.add(menu);
    }

    public void addItem(Application application, SetItem setItem) {
        items.add(setItem);
        JMenuItem menu = new JMenuItem(setItem.toString());
        menu.addActionListener(e -> {
            EditItemDialog dialog = new EditItemDialog(application, "Item", setItem.getItem());
            dialog.showDialog();
        });
        popupMenu.add(menu);
    }

    public void showPopup(MouseEvent e) {
        if (items.size() > 0) {
            Component component = e.getComponent();
            popupMenu.show(component, 0, component.getHeight());
        }
    }

    public int getRow() {
        return r;
    }

    public void setRow(int row) {
        this.r = row;
        updateName();
    }

    public int getCol() {
        return c;
    }

    public void setCol(int col) {
        this.c = col;
    }

    public int getW() {
        return w;
    }

    public void setW(int width) {
        this.w = width;
    }

    public int getH() {
        return h;
    }

    public void setH(int height) {
        this.h = height;
    }

    private void updateName() {
        String name = Statics.Alphabet[r] + String.valueOf(c);
        setName(name);
        setText(name);
    }
}