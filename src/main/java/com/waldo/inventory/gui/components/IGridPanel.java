package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.gui.GuiInterface;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class IGridPanel<K extends DbObject, V extends ArrayList> extends JPanel implements GuiInterface {


    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<ITileView> tileViews;
    private HashMap<K, V> map;

    /*
      *                  CONSTRUCTOR
      * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public IGridPanel() {
        this(null);
    }

    public IGridPanel(HashMap<K, V> map) {
        initializeComponents();
        initializeLayouts();

        setMap(map);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void setMap(HashMap<K,V> map) {
        if (map == null) {
            this.map = new HashMap<>();
        } else {
            this.map = map;
        }
        updateComponents(null);
    }

    public void clearMap() {
        this.map.clear();
        updateComponents(null);
    }

    private String getFileName(String filePath) {
        if (filePath.contains("/")) {
            int ndx = filePath.lastIndexOf("/");
            return filePath.substring(ndx + 1, filePath.length());
        }
        return filePath;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        tileViews = new ArrayList<>();
    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object object) {
        tileViews.clear();
        removeAll();
        for (K k : map.keySet()) {
            for (Object o : map.get(k)) {
                String name;
                if (o instanceof File) {
                    name = getFileName(((File)o).getAbsolutePath());
                } else {
                    name = o.getClass().getSimpleName();
                }
                ITileView view = new ITileView(k.getIconPath(), name);
                tileViews.add(view);

            }
        }

        for (ITileView view : tileViews) {
            add(view);
        }

        revalidate();
        repaint();
    }

}

