package com.waldo.inventory.gui.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ITableProgressCellRenderer extends JPanel implements TableCellRenderer {

    public int val = 0;
    private int column ;

    public ITableProgressCellRenderer(int column) {
        this.column = column;
    }

    private static Paint generatePaint(Color c, int height) {
        return new LinearGradientPaint(0.0f, 0.0f, 0.0f, (float)height, new float[]{0.0f, 0.5f, 1.0f}, new Color[]{c.darker(), c.brighter(), c.darker()}, MultipleGradientPaint.CycleMethod.REFLECT);
    }

    private static Paint greenPaint = generatePaint(Color.GREEN, 3);
    private static Paint orangePaint = generatePaint(Color.ORANGE, 3);
    private static Paint redPaint = generatePaint(Color.RED, 4);

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        int x = 1;
        int y = 1;
        int w = getWidth()-2;
        int h = getHeight()-2;
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
        int wd = (int)Math.round(w * val / 100.0);
        g2d.fillRect(x, y, wd, h);
        g2d.draw3DRect(x, y, wd, h, true);
        // Draw some text here if you want
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (this.column == column && value instanceof Integer) {
            val = (int)value;
            return this;
        }
        return null;
    }
}
