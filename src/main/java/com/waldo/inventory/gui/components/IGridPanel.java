package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.GuiInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IGridPanel<K extends DbObject, T, V extends ArrayList<T>> extends JPanel implements GuiInterface, ITileView.TileClickListener {


    public interface GridComponentClicked<K> {
        void onGridComponentClick(K clickedObject, File file);
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<ITileView> tileViews;
    private HashMap<K, V> map;

    private GridComponentClicked<K> listener;

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

    public void addOnGridComponentClickedListener(GridComponentClicked<K> listener) {
        this.listener = listener;
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
         // TODO : not always delete all?
        tileViews.clear();
        removeAll();
        for (K k : map.keySet()) {
            for (Object o : map.get(k)) {
                String name;
                File file = null;
                if (o instanceof File) {
                    name = getFileName(((File)o).getAbsolutePath());
                    file = (File) o;
                } else {
                    name = o.getClass().getSimpleName();
                }
                Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgIdesPath(), k.getIconPath());
                ITileView view = new ITileView(path.toString(), name, k.getId());
                if (file != null) {
                    view.setFile(file);
                }
                view.addTileClickListener(this);
                tileViews.add(view);
            }
        }

        //tileViews.sort(new ITileView.ITileViewComparator());
        for (ITileView view : tileViews) {
            add(view);
        }

        revalidate();
        repaint();
    }

    @Override
    public void onTileClick(ITileView view) {
        if (listener != null) {
            for (K k : map.keySet()) {
                if (k.getId() == view.getTileId()) {
                    listener.onGridComponentClick(k, view.getFile());
                }
            }
        }
    }
}

