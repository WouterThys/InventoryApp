package com.waldo.inventory.gui.dialogs.editdirectorydialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.classes.ProjectIDE;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IComboCheckBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.dialogs.filechooserdialog.ImageFileChooser;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.waldo.inventory.gui.Application.imageResource;

public class EditDirectoryDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField directoryTf;
    private JButton directoryBtn;
    IComboCheckBox useTypeCb;

    java.util.List<ProjectIDE> projectIDEList;

    /*
    *                  CONSTRUCTORS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public EditDirectoryDialogLayout(Application application, String title) {
        super(application, title);
        showTitlePanel(false);
    }

    public EditDirectoryDialogLayout(Dialog dialog, String title) {
        super(dialog, title);
        showTitlePanel(false);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        directoryTf = new ITextField("Directory");
        directoryBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        directoryBtn.addActionListener(e -> {
            JFileChooser fileChooser = ImageFileChooser.getFileChooser();
            fileChooser.setCurrentDirectory(new File("home/"));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (fileChooser.showDialog(EditDirectoryDialogLayout.this, "Open") == JFileChooser.APPROVE_OPTION) {
                directoryTf.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        projectIDEList = DbManager.db().getProjectIDES();
        JCheckBox[] boxes = new JCheckBox[projectIDEList.size() + 1];

        boxes[0] = new JCheckBox("All", true);
        for (int i = 1; i < boxes.length; i++) {
            boxes[i] = new JCheckBox(projectIDEList.get(i-1).getName());
        }

        useTypeCb = new IComboCheckBox(boxes);
        useTypeCb.setPreferredSize(new Dimension(120, 30));
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel browsePanel = PanelUtils.createBrowsePanel(directoryTf, directoryBtn);
        JPanel cbPanel = new JPanel();
        cbPanel.add(new ILabel("Project types to search for: "));
        cbPanel.add(useTypeCb);

        getContentPanel().add(browsePanel, BorderLayout.CENTER);
        getContentPanel().add(cbPanel, BorderLayout.SOUTH);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null && object instanceof ProjectDirectory) {
            ProjectDirectory projectDirectory = (ProjectDirectory) object;
            directoryTf.setText(projectDirectory.getDirectory());
        }
    }
}
