package com.waldo.inventory;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;

public class TablePartitions {
    private JScrollPane getContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIManager.getColor("Table.background"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        String[][] s = {{"A", "B", "C"}};
        String[] colIds = {"Jan", "Feb", "Mar"};
        JTable t = new JTable(new Object[0][3], colIds);
        panel.add(wrap(t), gbc);

        t = new JTable(new Object[0][1], new String[]{"2008"});
        panel.add(wrap(t), gbc);

        t = new JTable(s, colIds);
        panel.add(pad(t), gbc);

        t = new JTable(new Object[0][1], new String[]{"2007"});
        panel.add(wrap(t), gbc);

        t = new JTable(s, colIds);
        panel.add(new PadIt(t), gbc);

        t = new JTable(new Object[0][1], new String[]{"2006"});
        panel.add(wrap(t), gbc);

        t = new JTable(s, colIds);
        panel.add(new PadIt(t), gbc);

        gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);
        return new JScrollPane(panel);
    }

    private JScrollPane wrap(JTable table) {
        Dimension d = table.getPreferredSize();
        table.setPreferredScrollableViewportSize(d);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        return scrollPane;
    }

    private class PadIt extends JComponent {
        public PadIt(JTable table) {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(table, gbc);
            setBorder(new PadItBorder());
        }
    }

    private class PadItBorder extends AbstractBorder {
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 1, 0, 2);
        }
    }

    private Container pad(JTable table) {
        Container c = new Container() {
            public void paint(Graphics g) {
                super.paint(g);
                int w = getWidth();
                int h = getHeight();
                Color color = UIManager.getColor("ScrollPane.background");
                g.setColor(color);
                g.fillRect(0, 0, w, h);
                super.paint(g);
            }

            public Insets getInsets() {
                return new Insets(0, 1, 0, 2);
            }
        };
        c.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        c.add(table, gbc);
        return c;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new TablePartitions().getContent());
        f.setSize(400, 275);
        f.setLocation(100, 200);
        f.setVisible(true);
    }
}

