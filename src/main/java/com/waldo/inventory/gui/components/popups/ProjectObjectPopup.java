package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.ProjectObject;
import com.waldo.inventory.gui.components.actions.IActions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ProjectObjectPopup<P extends ProjectObject> extends JPopupMenu {

    protected ProjectObjectPopup(P projectObject) {
        super();

        init(projectObject);
    }

    public abstract void onEditObject(P projectObject);
    public abstract void onDeleteObject(P projectObject);
    public abstract void onRunIde(P projectObject);
    public abstract void onBrowseProjectObject(P projectObject);

    private void init(final P projectObject) {

        // Actions
        IActions.EditAction editAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditObject(projectObject);
            }
        };
        editAction.setName("Edit");

        IActions.DeleteAction deleteAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteObject(projectObject);
            }
        };
        deleteAction.setName("Delete");

        IActions.DoItAction runIdeAction = new IActions.DoItAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRunIde(projectObject);
            }
        };
        runIdeAction.setIcon(imageResource.readIcon("Actions.Execute"));
        String name = "Run ";
        if (projectObject != null && projectObject.getProjectIDE() != null) {
            name += projectObject.getProjectIDE().toString();
        }
        runIdeAction.setName(name);

        IActions.BrowseFileAction openProjectFolderAction = new IActions.BrowseFileAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBrowseProjectObject(projectObject);
            }
        };
        openProjectFolderAction.setIcon(imageResource.readIcon("Actions.BrowseFile"));

        boolean enabled = projectObject != null && projectObject.isValid();
        runIdeAction.setEnabled(enabled);
        openProjectFolderAction.setEnabled(enabled);

        add(editAction);
        add(deleteAction);
        addSeparator();
        add(runIdeAction);
        add(openProjectFolderAction);
    }
}
