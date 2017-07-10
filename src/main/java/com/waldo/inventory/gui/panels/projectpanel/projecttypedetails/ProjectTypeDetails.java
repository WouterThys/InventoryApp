package com.waldo.inventory.gui.panels.projectpanel.projecttypedetails;

import com.waldo.inventory.classes.ProjectType;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;

public class ProjectTypeDetails extends JPanel implements GuiInterface, ActionListener {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel  typeIconLbl;
    private JButton openProjectBtn;
    private JButton parseBtn;

    private ITextField nameTf;
    private ITextField launchPathTf;
    private ITextField parserNameTf;

    private ILabel launcherPathLbl;
    private ILabel parserNameLbl;

    private ActionListener actionListener;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;
    private ProjectType projectType;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectTypeDetails(Application application, ActionListener actionListener) {
        this.application = application;
        this.actionListener = actionListener;

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        parseBtn.setEnabled((projectType != null) && (!projectType.getParserName().isEmpty()));
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        typeIconLbl = new ILabel();
        openProjectBtn = new JButton(imageResource.readImage("Common.RArrow", 48));
        parseBtn = new JButton();

        openProjectBtn.addActionListener(actionListener);
        parseBtn.addActionListener(this);

        nameTf = new ITextField();
        launchPathTf = new ITextField();
        parserNameTf = new ITextField();

        nameTf.setEnabled(false);
        launchPathTf.setEnabled(false);
        parserNameTf.setEnabled(false);

        launcherPathLbl = new ILabel("Launcher path: ", ILabel.RIGHT);
        parserNameLbl = new ILabel("Parser name: ", ILabel.RIGHT);
    }

    @Override
    public void initializeLayouts() {

        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        buttonsPanel.add(typeIconLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        buttonsPanel.add(openProjectBtn, gbc);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        JComponent[] jComponents = new JComponent[] { nameTf, launchPathTf, parserNameTf};
        ILabel[] iLabels = new ILabel[] {
                new ILabel("Name: ", ILabel.RIGHT) ,
                launcherPathLbl,
                parserNameLbl
        };

        for (int i = 0; i < jComponents.length; i++) {
            // Label
            gbc.gridx = 0; gbc.weightx = 1;
            gbc.gridy = i; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.EAST;
            detailsPanel.add(iLabels[i], gbc);
            // Component
            gbc.gridx = 1; gbc.weightx = 1;
            gbc.gridy = i; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            detailsPanel.add(jComponents[i], gbc);
        }

        // Add
        setLayout(new BorderLayout());
        add(buttonsPanel, BorderLayout.NORTH);
        add(detailsPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        if (object == null) {
            setVisible(false);
        } else {
            if (object instanceof ProjectType) {
                projectType = (ProjectType) object;

                typeIconLbl.setIcon(projectType.getIconPath(), 48,48);
                nameTf.setText("name"); //FileUtils.formatFileNameString(projectType.get)

                if (projectType.isUseDefaultLauncher()) {
                    launchPathTf.setText("Default");
                } else {
                    launchPathTf.setText(projectType.getLauncherPath());
                }

                if (projectType.getParserName().isEmpty()) {
                    parserNameLbl.setVisible(false);
                    parserNameTf.setVisible(false);
                } else {
                    parserNameLbl.setVisible(true);
                    parserNameTf.setVisible(true);
                    parserNameTf.setText(projectType.getParserName());
                }

                updateEnabledComponents();

                setVisible(true);
            }
        }
    }

    //
    // Button clicks
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(parseBtn)) {

        }
    }
}