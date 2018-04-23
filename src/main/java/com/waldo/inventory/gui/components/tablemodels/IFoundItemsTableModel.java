package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.classes.dbclasses.PackageType;
import com.waldo.inventory.classes.search.ObjectMatch;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IFoundItemsTableModel extends IAbstractTableModel<ObjectMatch<Item>> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Value", "Footprint", "Manufacturer", "Match"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class, String.class, ILabel.class};

    public IFoundItemsTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ObjectMatch<Item> objectMatch = getItemAt(rowIndex);
        if (objectMatch != null) {
            Item foundItem = objectMatch.getFoundObject();
            switch (columnIndex) {
                case -1:
                    return objectMatch;
                case 0: // Amount label
                    return foundItem;
                case 1: // Name
                    return foundItem.toString();
                case 2: // Value
                    String result;
                    if (foundItem.getValue().hasValue()) {
                        result = foundItem.getValue().toString();
                    } else {
                        result = foundItem.getAlias();
                    }
                    return result;
                case 3: // Footprint
                    PackageType packageType = foundItem.getPackageType();
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
                    Manufacturer m = foundItem.getManufacturer();
                    if (m != null && !m.isUnknown()) {
                        return m.toString();
                    }
                    return "";
                case 5: // Match
                    return objectMatch;
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

        private static final ImageIcon greenBall = imageResource.readIcon("Ball.green");
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
            else if (value instanceof ObjectMatch) {
                return new MatchValue(((ObjectMatch)value));
            }
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

        MatchValue(ObjectMatch match) {
            if (match != null) {
                this.val = match.getPercent();
                this.setToolTipText(match.getMatchString());
            }
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
