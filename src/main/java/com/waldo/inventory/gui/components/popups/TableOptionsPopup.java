package com.waldo.inventory.gui.components.popups;

import javax.swing.*;

import java.util.Comparator;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class TableOptionsPopup extends JPopupMenu {

    private final JMenu sortMenu = new JMenu("Sort");
    private final JCheckBoxMenuItem showSetsMenu = new JCheckBoxMenuItem("Show sets", imageResource.readImage("Sets.Small"), true);
    private final JCheckBoxMenuItem showSetItemsMenu =  new JCheckBoxMenuItem("Show set items", imageResource.readImage("Items.Tree.Item"), false);

    public interface TableOptionsListener {
        void onShowSets(boolean show);
        void onShowSetItems(boolean show);
    }

    private TableOptionsListener tableOptionsListener;

    protected TableOptionsPopup() {
        super();
        init();
    }

    public void addTableOptionsListener(TableOptionsListener tableOptionsListener) {
        this.tableOptionsListener = tableOptionsListener;
    }

    public abstract void onSortBy(Comparator comparator);

    public void addSortOption(final Comparator comparator, String name, ImageIcon icon) {
        JMenuItem menuItem = new JMenuItem(name, icon);
        menuItem.addActionListener(e -> onSortBy(comparator));

        sortMenu.add(menuItem);
    }

    private void init() {

        showSetsMenu.addActionListener(e -> {
            if (tableOptionsListener != null) {
                tableOptionsListener.onShowSets(showSetsMenu.isSelected());
            }
        });
        showSetItemsMenu.addActionListener(e -> {
            if (tableOptionsListener != null) {
                tableOptionsListener.onShowSetItems(showSetItemsMenu.isSelected());
            }
        });

        // Add
        add(sortMenu);
        addSeparator();
        add(showSetsMenu);
        add(showSetItemsMenu);

    }
}
