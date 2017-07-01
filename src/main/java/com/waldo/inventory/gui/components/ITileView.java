package com.waldo.inventory.gui.components;

import com.waldo.inventory.gui.GuiInterface;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Comparator;

import static com.waldo.inventory.gui.Application.imageResource;

public class ITileView extends JPanel implements GuiInterface {

    private JButton iconBtn;
    private JTextPane nameTp;;

    private ImageIcon icon;
    private String name;

    public ITileView(String iconPath, String name) {
        this.icon = imageResource.readImage(iconPath, 64, 64);
        this.name = name;

        initializeComponents();
        initializeLayouts();

        updateComponents(null);
    }

    public ITileView(ImageIcon icon, String name) {
        this.icon = icon;
        this.name = name;

        initializeComponents();
        initializeLayouts();

        updateComponents(null);
    }

    private String createName(String text) {
        String result = text;

//        // Remove extension
//        if (result.contains(".")) {
//            int ndx = text.lastIndexOf(".");
//            result = result.substring(0, ndx);
//        }

        // Not too long
        result = formatString(result);


        return result;
    }

    private String formatString(String text) {
        String result = text;
        if (result.length() > 12) {
            String[] split = result.split("(?=\\p{Lu})|(?=\\.)|(?<=\\_)|(?<=\\-)");
            if (split.length > 1) {
                int middle = split.length / 2;
                String first = "", second = "";
                for (int i = 0; i < middle; i++) {
                    first += split[i];
                }
                for (int i = middle; i < split.length; i++) {
                    second += split[i];
                }

                if (first.length() > 12) {
                    first = formatString(first);
                }

                if (second.length() > 12) {
                    second = formatString(second);
                }

                result = first + "\n" + second;
            }
        }

        return result;
    }

    @Override
    public void initializeComponents() {
        iconBtn = new JButton();
        iconBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconBtn.setAlignmentY(Component.CENTER_ALIGNMENT);

        nameTp = new JTextPane();

        StyledDocument doc = nameTp.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        nameTp.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameTp.setAlignmentY(Component.CENTER_ALIGNMENT);
        nameTp.setFocusable(false);
        nameTp.setOpaque(false);
        nameTp.setBackground(new Color(0,0,0,0));
        nameTp.setBorder(null);
        nameTp.setEditable(false);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(iconBtn);
        add(nameTp);

        setPreferredSize(new Dimension(128, 128));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    }

    @Override
    public void updateComponents(Object object) {
        iconBtn.setIcon(icon);
        nameTp.setText(createName(name));
    }

    public static class ITileViewComparator implements Comparator<ITileView> {

        @Override
        public int compare(ITileView o1, ITileView o2) {
            return o1.name.compareToIgnoreCase(o2.name);
        }
    }

}

