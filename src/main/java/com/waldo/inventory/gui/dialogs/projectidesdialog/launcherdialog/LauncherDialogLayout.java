package com.waldo.inventory.gui.dialogs.projectidesdialog.launcherdialog;


import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ICheckBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.dialogs.filechooserdialog.ShellFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class LauncherDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ICheckBox useDefaultLauncherCb;
    ITextField launcherPathTf;
    private JButton launcherFileBtn;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     private boolean useDefaultLauncher;
     private String launcherPath;

      /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LauncherDialogLayout(Application application, String title, boolean useDefaultLauncher, String launcherPath) {
        super(application, title);
        showTitlePanel(false);

        this.useDefaultLauncher = useDefaultLauncher;
        this.launcherPath = launcherPath;

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        useDefaultLauncherCb = new ICheckBox("Use default launcher", true);
        useDefaultLauncherCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED || e.getStateChange() == ItemEvent.DESELECTED) {
                launcherPathTf.setEnabled(!useDefaultLauncherCb.isSelected());
                launcherFileBtn.setEnabled(!useDefaultLauncherCb.isSelected());
            }
        });

        launcherPathTf = new ITextField("Launcher");
        launcherPathTf.setEnabled(false);

        launcherFileBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        launcherFileBtn.addActionListener(e -> {
            JFileChooser fileChooser = ShellFileChooser.getFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fileChooser.showDialog(LauncherDialogLayout.this, "Open") == JFileChooser.APPROVE_OPTION) {
                launcherPathTf.setText(fileChooser.getSelectedFile().getPath());
                launcherPathTf.fireValueChanged();
            }
        });
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new GridBagLayout());
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        // - Browse panel
        JPanel launcherPanel = PanelUtils.createFileOpenPanel(launcherPathTf, launcherFileBtn);

        // - Launcher path
        ILabel launcherLabel = new ILabel("Launcher: ");
        launcherLabel.setHorizontalAlignment(ILabel.RIGHT);
        launcherLabel.setVerticalAlignment(ILabel.CENTER);

        // - Add to panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(useDefaultLauncherCb, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        getContentPanel().add(launcherLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        getContentPanel().add(launcherPanel, gbc);

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        useDefaultLauncherCb.setSelected(useDefaultLauncher);
        launcherPathTf.setText(launcherPath);
    }
}
