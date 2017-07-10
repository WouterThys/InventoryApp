package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.gui.GuiInterface;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Comparator;

import static com.waldo.inventory.gui.Application.imageResource;

public class ITileView extends JPanel implements GuiInterface, ActionListener {

    public interface TileClickListener {
        void onTileClick(ITileView view);
    }

    private long tileId;
    private JButton iconBtn;
    private JTextPane nameTp;;

    private File file; // Should get rid of this

    private ImageIcon icon;
    private String name;

    private TileClickListener listener;

    public ITileView(String iconPath, String name, long id) {
        this.icon = imageResource.readImage(iconPath, 64, 64);
        this.name = name;
        this.tileId = id;

        initializeComponents();
        initializeLayouts();

        updateComponents(null);
    }

    public ITileView(String iconPath, String name) {
        this (iconPath, name, -1);
    }

    public ITileView(ImageIcon icon, String name) {
        this.icon = icon;
        this.name = name;

        initializeComponents();
        initializeLayouts();

        updateComponents(null);
    }

    public void addTileClickListener(TileClickListener listener) {
        this.listener = listener;
    }

    private String createName(String text) {
        return FileUtils.formatFileNameString(text);
    }

    public long getTileId() {
        return tileId;
    }

    public void setTileId(long tileId) {
        this.tileId = tileId;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void initializeComponents() {
        iconBtn = new JButton();
        iconBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
        iconBtn.addActionListener(this);

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


    @Override
    public void actionPerformed(ActionEvent e) {
        if (listener != null) {
            listener.onTileClick(this);
        }
    }
}

