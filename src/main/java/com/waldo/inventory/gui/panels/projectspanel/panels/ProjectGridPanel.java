package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.ProjectObject;
import com.waldo.inventory.gui.components.ITileView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ProjectGridPanel<P extends ProjectObject> extends JPanel implements
        GuiUtils.GuiInterface,
        ITileView.TileClickListener<P> {

    private int rows = -1;
    private int cols = -1;

    private ITileView<P> selectedTile;


    @Override
    public void onTileClick(MouseEvent e, P projectObject) {
        selectTile(projectObject);
        if (gridComponentListener != null) {
            gridComponentListener.onGridComponentClick(e, projectObject);
        }
    }

    @Override
    public void onTileRightClick(MouseEvent e, P projectObject) {
        selectTile(projectObject);
        if (gridComponentListener != null) {
            gridComponentListener.onGridComponentRightClick(e, projectObject);
        }
    }

    public interface GridComponentClicked<PO extends ProjectObject> {
        void onGridComponentClick(MouseEvent e, PO projectObject);
        void onGridComponentRightClick(MouseEvent e, PO projectObject);
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final List<ITileView<P>> tileViews = new ArrayList<>();


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final GridComponentClicked<P> gridComponentListener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectGridPanel(GridComponentClicked<P> gridComponentListener) {
        this.gridComponentListener = gridComponentListener;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITileView<P> createTile(P projectObject) {
        ITileView<P> tileView = new ITileView<>(projectObject);
        tileView.addTileClickListener(this);
        return  tileView;
    }

    public void drawTiles(List<P> projectObjects) {
        tileViews.clear();
        for (P po : projectObjects) {
            tileViews.add(createTile(po));
        }
        redrawTiles();
    }

    private void redrawTiles() {
        removeAll();
        for (ITileView view : tileViews) {
            add(view);
        }
        revalidate();
        repaint();
    }

    private void selectTile(P projectObject) {
        if (selectedTile != null) {
            selectedTile.setSelected(false);
        }

        selectedTile = getTile(projectObject);
        if (selectedTile != null) {
            selectedTile.setSelected(true);
        }
    }

    public void addTile(P projectObject) {
        tileViews.add(createTile(projectObject));
        redrawTiles();
    }

    public void removeTile(P projectObject) {
        for (int i = tileViews.size()-1; i >= 0; i--) {
            ITileView<P> tileView = tileViews.get(i);
            if (tileView.getProjectObject().equals(projectObject)) {
                tileViews.remove(tileView);
                break;
            }
        }
        redrawTiles();
    }

    public ITileView<P> getTile(P projectObject) {
        for (ITileView<P> tv : tileViews) {
            if (tv.getProjectObject().equals(projectObject)) {
                return tv;
            }
        }
        return null;
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        if (rows > 0 && cols > 0) {
            setLayout(new GridLayout(rows, cols));
        }
    }

    @Override
    public void initializeLayouts() {
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            redrawTiles();
        } else {
            tileViews.clear();
            removeAll();
        }
    }

}
