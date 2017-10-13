package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.ProjectObject;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ITileView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectGridPanel<P extends ProjectObject> extends JPanel implements
        GuiInterface,
        ITileView.TileClickListener<P> {

    private int rows = -1;
    private int cols = -1;


    @Override
    public void onTileClick(P projectObject) {
        if (gridComponentListener != null) {
            gridComponentListener.onGridComponentClick(projectObject);
        }
    }

    public interface GridComponentClicked<PO extends ProjectObject> {
        void onGridComponentClick(PO projectObject);
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<ITileView<P>> tileViews = new ArrayList<>();


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private GridComponentClicked<P> gridComponentListener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectGridPanel(GridComponentClicked<P> gridComponentListener) {
        this.gridComponentListener = gridComponentListener;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    public ProjectGridPanel(GridComponentClicked<P> gridComponentListener, int rows, int cols) {
        this.gridComponentListener = gridComponentListener;

        this.rows = rows;
        this.cols = cols;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
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

    public void redrawTiles() {
        removeAll();
        for (ITileView view : tileViews) {
            add(view);
        }
        //revalidate();
        repaint();
    }

    public void updateTiles() {
        for (ITileView view : tileViews) {
            //view.revalidate();
            view.repaint();
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
    public void updateComponents(Object object) {
        if (object != null) {
            redrawTiles();
        } else {
            tileViews.clear();
            removeAll();
        }
    }

}
