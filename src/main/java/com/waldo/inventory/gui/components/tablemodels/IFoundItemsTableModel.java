package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.classes.dbclasses.SetItem;
import com.waldo.inventory.classes.search.DbObjectMatch;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITableIcon;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialogLayout.SearchType;

public class IFoundItemsTableModel extends IAbstractTableModel<DbObject> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Description", "Manufacturer", "Match"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class, ILabel.class};

    private static final ImageIcon greenBall = imageResource.readImage("Ball.green");
    private static final ImageIcon blueBall = imageResource.readImage("Ball.blue");

    private SearchType searchType;

    public IFoundItemsTableModel(SearchType searchType, Comparator<DbObject> comparator) {
        super(COLUMN_NAMES, COLUMN_CLASSES, comparator);
        this.searchType = searchType;
    }

    public void setItemList(List<DbObject> itemList) {
        super.setItemList(itemList);
    }

    @Override
    public int getColumnCount() {
        switch (searchType) {
            case SearchWord: return 4;
            case PcbItem: return 5;
        }
        return super.getColumnCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DbObject obj = getItemAt(rowIndex);

        if (obj != null) {
            switch (columnIndex) {
                case -1:
                case 0: // Amount label
                    return obj;
                case 1: // Name
                    return obj.toString();
                case 2: // Description
                    if (obj instanceof Item) {
                        return ((Item)obj).getDescription();
                    } else {
                        return ((SetItem)obj).getItem().getDescription();
                    }
                case 3: // Manufacturer
                    Manufacturer m;
                    if (obj instanceof Item) {
                        m = SearchManager.sm().findManufacturerById(((Item)obj).getManufacturerId());
                    } else {
                        m = SearchManager.sm().findManufacturerById(((SetItem)obj).getItem().getManufacturerId());
                    }

                    if (m != null && !m.isUnknown()) {
                        return m.toString();
                    }
                    return "";
                case 4: // Match
                    return obj.getObjectMatch();
            }
        }
        return null;
    }


    @Override
    public boolean hasTableCellRenderer() {
        return true;
    }

    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Item) {
                    if (row == 0) {
                        TableColumn tableColumn = table.getColumnModel().getColumn(column);
                        tableColumn.setMaxWidth(32);
                        tableColumn.setMinWidth(32);
                    }

                    Item item = (Item) value;
                    String txt = String.valueOf(item.getAmount());

                    return new ITableIcon(component.getBackground(), row, isSelected, greenBall, txt);
                } else if (value instanceof SetItem) {
                    if (row == 0) {
                        TableColumn tableColumn = table.getColumnModel().getColumn(column);
                        tableColumn.setMaxWidth(32);
                        tableColumn.setMinWidth(32);
                    }
                    String txt = String.valueOf(((SetItem)value).getAmount());
                    return new ITableIcon(component.getBackground(), row, isSelected, blueBall, txt);

                } else if (value instanceof DbObjectMatch) {
                    return new MatchValue(((DbObjectMatch)value).getMatchPercent());
                }
                return component;
            }
        };
    }

    private static class MatchValue extends ILabel {

        private static Paint generatePaint(Color c, int height) {
            return new LinearGradientPaint(0.0f, 0.0f, 0.0f, (float)height, new float[]{0.0f, 0.5f, 1.0f}, new Color[]{c.darker(), c.brighter(), c.darker()}, MultipleGradientPaint.CycleMethod.REFLECT);
        }

        private static Paint greenPaint = generatePaint(Color.GREEN, 25);
        private static Paint orangePaint = generatePaint(Color.ORANGE, 24);
        private static Paint redPaint = generatePaint(Color.RED, 23);

        private int val;

        public MatchValue(int value) {
            this.val = value;
            setPreferredSize(new Dimension(100, 25));
            setMaximumSize(getPreferredSize());
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            int x = 1;
            int y = 1;
            int w = getWidth() - 2;
            int h = getHeight() - 2;
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(x, y, w, h);
            Paint backPaint;
            if (val >= 75)
                backPaint = greenPaint;
            else if (val >= 50)
                backPaint = orangePaint;
            else
                backPaint = redPaint;
            g2d.setPaint(backPaint);
            int wd = (int) Math.round(w * val / 100.0);
            g2d.fillRect(x, y, wd, h);
            g2d.draw3DRect(x, y, wd, h, true);
            // Draw some text here if you want
        }
    }


}
