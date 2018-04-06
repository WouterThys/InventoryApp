package com.waldo.inventory.gui.panels.projectspanel.preview;

import com.waldo.inventory.classes.dbclasses.ProjectOther;
import com.waldo.inventory.gui.Application;

import javax.swing.*;

public abstract class ProjectOtherPreviewPanel extends ProjectPreviewPanel<ProjectOther> {


    public ProjectOtherPreviewPanel(Application application) {
        super(application);
    }


    @Override
    void initializeInfoComponents() {

    }

    @Override
    JPanel createInfoPanel() {
        return new JPanel();
    }

    @Override
    void updateInfoPanel(ProjectOther projectObject) {

    }

}
