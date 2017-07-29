package com.waldo.inventory.gui.panels.projectpanel.extras;

import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.classes.ProjectType;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ITileView;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProjectGirdPanel extends JPanel implements GuiInterface, ITileView.TileClickListener {

    public interface GridComponentClicked {
        void onGridComponentClick(String name, ProjectType type, File file);
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<ITileView> tileViews = new ArrayList<>();


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;
    private Project project;

    private GridComponentClicked gridComponentListener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectGirdPanel(Application application, Project project, GridComponentClicked gridComponentListener) {
        this.application = application;
        this.project = project;
        this.gridComponentListener = gridComponentListener;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void initializeTiles() {
        if (project != null) {
            tileViews.clear();
            for (ProjectDirectory directory : project.getProjectDirectories()) {
                for (ProjectType type : directory.getProjectTypes()) {
                    for (File file : directory.getProjectFilesForType(type)) {
                        ITileView tileView = new ITileView(file, type, directory);
                        tileView.addTileClickListener(this);
                        tileViews.add(tileView);
                    }
                }
            }
        }
    }

    public void redrawTiles() {
        removeAll();
        for (ITileView view : tileViews) {
            add(view);
        }
        revalidate();
        repaint();
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {

    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object object) {
        if (object != null && object instanceof Project) {
            if (!object.equals(project)) {

                project = (Project) object;

                initializeTiles();
                redrawTiles();
            }
        } else {
            tileViews.clear();
            removeAll();
        }
    }

    //
    // Tile clicked
    //
    @Override
    public void onTileClick(ITileView view) {
        if (gridComponentListener != null) {
            gridComponentListener.onGridComponentClick(view.getName(), view.getProjectType(), view.getFile());
        }
    }
}