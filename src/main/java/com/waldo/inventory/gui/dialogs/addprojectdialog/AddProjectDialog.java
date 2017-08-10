package com.waldo.inventory.gui.dialogs.addprojectdialog;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.editdirectorydialog.EditDirectoryDialog;
import com.waldo.inventory.gui.dialogs.filechooserdialog.ImageFileChooser;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

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

        initActions();

        updateComponents(project);

        setPreferredSize(new Dimension(600, 400));
        pack();

        if (project.getId() <= DbObject.UNKNOWN_ID) {
            isNew = true;
        } else {
            isNew = false;
        }
    }

    public Project getProject() {
        return project;
    }

    private void initActions() {
        getTitleIconLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JLabel lbl = (JLabel)e.getSource();

                    String initialPath = SettingsManager.settings().getFileSettings().getImgProjectsPath();

                    JFileChooser fileChooser = ImageFileChooser.getFileChooser();
                    fileChooser.setCurrentDirectory(new File(initialPath));
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    if (fileChooser.showDialog(AddProjectDialog.this, "Open") == JFileChooser.APPROVE_OPTION) {
                        project.setIconPath(FileUtils.createIconPath(initialPath, fileChooser.getSelectedFile().getAbsolutePath()));
                        try {
                            URL url = fileChooser.getSelectedFile().toURI().toURL();
                            lbl.setIcon(imageResource.readImage(url, 48,48));
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        });
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

            super.onOK();
        }
    }

    //
    // Toolbar for directories listener
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        //
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        EditDirectoryDialog directoryDialog = new EditDirectoryDialog(AddProjectDialog.this, "Add directory", new ProjectDirectory());
        if (directoryDialog.showDialog() == IDialog.OK) {
            application.beginWait();
            try {
                project.addDirectory(directoryDialog.getDirectory(), directoryDialog.getSelectedTypes());
            } finally {
                application.endWait();
            }
            updateComponents(project);
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
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
                    project.removeDirectory(dir);
                    directoryModel.removeElement(dir);
                }
            }
            selectedDirectory = null;
            updateComponents(null);
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedDirectory != null) {
//            JFileChooser fileChooser = new JFileChooser(selectedDirectory.getDirectory());
//            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//            if (fileChooser.showDialog(AddProjectDialog.this, "Select project folder") == JFileChooser.APPROVE_OPTION) {
//                File dir = fileChooser.getSelectedFile();
//                if (dir.isDirectory()) {
//                    selectedDirectory.setDirectory(dir.getAbsolutePath());
//                    project.updateDirectory(selectedDirectory);
//                    updateComponents(project);
//                }
//            }
            EditDirectoryDialog directoryDialog = new EditDirectoryDialog(AddProjectDialog.this, "Edit directory", selectedDirectory);
            if (directoryDialog.showDialog() == IDialog.OK) {
                application.beginWait();
                try {
                    selectedDirectory.setDirectory(directoryDialog.getDirectory());
                    project.updateDirectory(selectedDirectory, directoryDialog.getSelectedTypes());
                } finally {
                    application.endWait();
                }
                updateComponents(project);
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
                directoryInfoLbl.setText("Found " + directory.getProjectTypeMap().size() + " projects in folder");
            } else {
                directoryInfoLbl.setText("");
            }
            selectedDirectory = directory;
            updateEnabledComponents();
        }
    }

    //
    // Name changed
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        if (component.equals(nameField)) {
            setTitleName(nameField.getText());
        }
    }

    @Override
    public DbObject getGuiObject() {
        return project;
    }
}
