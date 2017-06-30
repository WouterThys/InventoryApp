package com.waldo.inventory.gui.dialogs.addprojectdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.io.File;
import java.util.List;

public class AddProjectDialog extends AddProjectDialogLayout {

    private boolean isNew;

    // New project
    public AddProjectDialog(Application application, String title) {
        this(application, title, new Project());
    }

    public AddProjectDialog(Application application, String title, Project project) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(project);

        if (project.getId() <= DbObject.UNKNOWN_ID) {
            isNew = true;
        } else {
            isNew = false;
        }
    }

    public Project getProject() {
        return project;
    }

    private boolean verify() {
        boolean ok = true;

        String name = nameField.getText();
        if (name == null || name.isEmpty()) {
            nameField.setError("Name can't be empty..");
            ok = false;
        }

        return ok;
    }

    @Override
    protected void onOK() {
        if (verify()) {
            project.setName(nameField.getText());

            // Get all directories
            for (int i = 0; i < directoryModel.getSize(); i++) {
                project.getProjectDirectories().add(directoryModel.getElementAt(i));
            }

            super.onOK();
        }
    }

    //
    // Toolbar for directories listener
    //
    @Override
    public void onToolBarRefresh() {
        //
    }

    @Override
    public void onToolBarAdd() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showDialog(AddProjectDialog.this, "Select project folder") == JFileChooser.APPROVE_OPTION) {
            File dir = fileChooser.getSelectedFile();
            if (dir.isDirectory()) {
                project.addDirectory(dir.getAbsolutePath());
                updateComponents(project);
            }
        }
    }

    @Override
    public void onToolBarDelete() {
        List<ProjectDirectory> selected = directoryList.getSelectedValuesList();
        if (selected != null && selected.size() > 0) {
            int result;
            if (selected.size() == 1) {
                result = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete: " + selected.get(0) + "?",
                        "Delete directory",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
            } else {
                result = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete " + String.valueOf(selected.size()) + " selected objects?",
                        "Delete directories",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
            }

            if (result == JOptionPane.OK_OPTION) {
                for(ProjectDirectory dir : selected) {
                    dir.delete();
                    directoryModel.removeElement(dir);
                }
            }
        }
    }

    @Override
    public void onToolBarEdit() {
        if (selectedDirectory != null) {
            JFileChooser fileChooser = new JFileChooser(selectedDirectory.getDirectory());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showDialog(AddProjectDialog.this, "Select project folder") == JFileChooser.APPROVE_OPTION) {
                File dir = fileChooser.getSelectedFile();
                if (dir.isDirectory()) {
                    selectedDirectory.setDirectory(dir.getAbsolutePath());
                    project.updateDirectory(selectedDirectory);
                    updateComponents(project);
                }
            }
        }
    }

    //
    // Directory list selection changed
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            ProjectDirectory directory = directoryList.getSelectedValue();
            // TODO: dialog displaying all project types and linked files (with icons and stuff..)
            if (directory != null) {
                directoryInfoLbl.setText("Found " + directory.getProjectTypes().size() + " projects in folder");
            } else {
                directoryInfoLbl.setText("");
            }
            selectedDirectory = directory;
            updateEnabledComponents();
        }
    }
}
