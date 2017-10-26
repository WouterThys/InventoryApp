package com.waldo.inventory.gui.dialogs.projectidesdialog.detectiondialog;

import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class DetectionDialogLayout extends IDialog implements ActionListener {

    private static final String[] oafcb = {"Open as folder", "Open as file"};
    private static final String[] emcb = {"Directory matches extension", "Directory contains extension"};
    private static final String[] eucb = {"Use directory folder", "Use matching extension file"};


    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField extensionTf;
    JComboBox<String> openAsFolderCb; // As folder of as file
    JComboBox<String> extensionMatchCb; // Directory match extension or directory contains extension
    JComboBox<String> extensionUsingCb; // Use parent folder or use matching extension file (only when directory contains option)

    private ILabel openAsFolderHelpLbl;
    private ILabel extensionMatchHelpLbl;
    private ILabel extensionUsingHelpLbl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private String extension;
    private boolean openAsFolder;
    private boolean matchExtension;
    private boolean useParentFolder;


     /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     DetectionDialogLayout(Application application, String title,
                           String extension,
                           boolean openAsFolder,
                           boolean matchExtension,
                           boolean useParentFolder) {
         super(application, title);
         showTitlePanel(false);

         this.extension = extension;
         this.openAsFolder = openAsFolder;
         this.matchExtension = matchExtension;
         this.useParentFolder = useParentFolder;
     }

    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {

        extensionTf = new ITextField("Extension");

        openAsFolderCb = new JComboBox<>(oafcb);
        openAsFolderCb.addActionListener(this);
        extensionMatchCb = new JComboBox<>(emcb);
        //extensionMatchCb.addActionListener(this);
        extensionUsingCb = new JComboBox<>(eucb);
        //extensionUsingCb.addActionListener(this);

        openAsFolderHelpLbl = new ILabel(imageResource.readImage("Common.HelpIcon"));
        openAsFolderHelpLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        DetectionDialogLayout.this,
                        "Open as folder",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        extensionMatchHelpLbl = new ILabel(imageResource.readImage("Common.HelpIcon"));
        extensionMatchHelpLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        DetectionDialogLayout.this,
                        "Info info",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        extensionUsingHelpLbl = new ILabel(imageResource.readImage("Common.HelpIcon"));
        extensionUsingHelpLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        DetectionDialogLayout.this,
                        "Pandas are not real",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new GridBagLayout());
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        // - Extension
        ILabel extensionLabel = new ILabel("Extension: ");
        extensionLabel.setHorizontalAlignment(ILabel.RIGHT);
        extensionLabel.setVerticalAlignment(ILabel.CENTER);

        // - Add to panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        getContentPanel().add(extensionLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(extensionTf, gbc);


        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(openAsFolderCb, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        getContentPanel().add(openAsFolderHelpLbl, gbc);


        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(extensionMatchCb, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        getContentPanel().add(extensionMatchHelpLbl, gbc);


        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(extensionUsingCb, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        getContentPanel().add(extensionUsingHelpLbl, gbc);

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        extensionTf.setText(extension);

        if (openAsFolder) {
            openAsFolderCb.setSelectedIndex(0);
        } else {
            openAsFolderCb.setSelectedIndex(1);
        }

        if (matchExtension) {
            extensionMatchCb.setSelectedIndex(0);
        } else {
            extensionMatchCb.setSelectedIndex(1);
        }

        if (useParentFolder) {
            extensionUsingCb.setSelectedIndex(0);
        } else {
            extensionUsingCb.setSelectedIndex(1);
        }
    }
}
