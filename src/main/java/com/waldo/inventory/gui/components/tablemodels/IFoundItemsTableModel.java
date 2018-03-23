package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.classes.dbclasses.PackageType;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialogLayout.SearchType;

public class IFoundItemsTableModel extends IAbstractTableModel<Item> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Value", "Footprint", "Manufacturer", "Match"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class, String.class, ILabel.class};

    private SearchType searchType;

    public IFoundItemsTableModel(SearchType searchType, Comparator<DbObject> comparator) {
        super(COLUMN_NAMES, COLUMN_CLASSES, comparator);
        this.searchType = searchType;
    }

    public void setItemList(List<Item> itemList) {
        super.setItemList(itemList);
    }

    @Override
    public int getColumnCount() {
        switch (searchType) {
            case SearchWord: return 5;
            case PcbItem: return 6;
        }
        return super.getColumnCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item setItem = getItemAt(rowIndex);

        if (setItem != null) {
            switch (columnIndex) {
                case -1:
                case 0: // Amount label
                    return setItem;
                case 1: // Name
                    return setItem.toString();
                case 2: // Value
                    String result;
                    if (setItem.getValue().hasValue()) {
                        result = setItem.getValue().toString();
                    } else {
                        result = setItem.getAlias();
                    }
                    return result;
                case 3: // Footprint
                    PackageType packageType = setItem.getPackageType();
                    if (packageType != null) {
                        if (packageType.getPackageId() > DbObject.UNKNOWN_ID) {
                            return packageType.getPackage().toString() + " - " + packageType.toString();
                        } else {
                            return packageType.toString();
                        }
                    } else {
                        return "";
                    }
                case 4: // Manufacturer
                    Manufacturer m = setItem.getManufacturer();
                    if (m != null && !m.isUnknown()) {
                        return m.toString();
                    }
                    return "";
                case 5: // Match
                    // TODO #1
                    return 0;//setItem.getObjectMatch();
            }
        }
        return null;
    }


    @Override
    public boolean hasTableCellRenderer() {
        return true;
    }

    private static final IRenderer renderer = new IRenderer();
    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return renderer;
    }

    private static class IRenderer extends DefaultTableCellRenderer {

        private static final ImageIcon greenBall = imageResource.readImage("Ball.green");
        private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, greenBall, "");

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
                label.updateBackground(component.getBackground(), row, isSelected);
                label.setText(txt);

                return label;
            }
//            else if (value instanceof Match) {
//                return 0;// TODO #1 new MatchValue(((Match)value).getMatchPercent());
//            }
            return component;
        }
    }

    private static class MatchValue extends ILabel {

        private static Paint generatePaint(Color c, int height) {
            return new LinearGradientPaint(0.0f, 0.0f, 0.0f, (float)height, new float[]{0.0f, 0.5f, 1.0f}, new Color[]{c.darker(), c.brighter(), c.darker()}, MultipleGradientPaint.CycleMethod.REFLECT);
        }

        private static Paint greenPaint = generatePaint(Color.GREEN, 25);
        private static Paint orangePaint = generatePaint(Color.ORANGE, 24);
        private static Paint redPaint = generatePaint(Color.RED, 23);

        private int val;

        MatchValue(int value) {
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
