package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.ProjectIDE;
import com.waldo.inventory.classes.ProjectObject;
import com.waldo.inventory.gui.GuiInterface;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;

public class ITileView<IT extends ProjectObject> extends JPanel implements GuiInterface, ActionListener {

    public interface TileClickListener<IT extends ProjectObject> {
        void onTileClick(IT projectObject);
    }

    private JButton iconBtn;
    private JTextPane nameTp;
    private String name;
    private Path path;
    private IT projectObject;
    private TileClickListener<IT> listener;

    public ITileView(IT projectObject) {
        this.projectObject = projectObject;
        this.name = projectObject.getDirectory();

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    public void addTileClickListener(TileClickListener<IT> listener) {
        this.listener = listener;
    }

    public IT getProjectObject() {
        return projectObject;
    }

    private String createName(String text) {
        return FileUtils.formatFileNameString(text);
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
        ProjectIDE ide = projectObject.getProjectIDE();
        if (ide != null) {
            path = Paths.get(settings().getFileSettings().getImgIdesPath(), projectObject.getProjectIDE().getIconPath());
            setIcon(path.toString());
        } else {
            setIcon("");
        }


        nameTp.setText(createName(name));
    }

    private void setIcon(String path) {
        if (!path.isEmpty()) {
            URL url;
            try {
                url = new File(path).toURI().toURL();
                iconBtn.setIcon(imageResource.readImage(url, 48, 48));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            iconBtn.setIcon(imageResource.readImage("Common.UnknownIcon48"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (listener != null) {
            listener.onTileClick(projectObject);
        }
    }

    @Override
    public String getName() {
        return name;
    }
}

