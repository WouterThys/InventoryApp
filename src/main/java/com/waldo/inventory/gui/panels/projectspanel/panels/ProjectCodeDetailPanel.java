package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.ProjectCode;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextArea;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class ProjectCodeDetailPanel extends JPanel implements GuiInterface {    
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel directoryLbl;
    private ILabel ideLbl;
    private ILabel ideTypeLbl;
    private JButton runIdeBtn;

    private ITextArea remarksTa;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectCodeDetailPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        directoryLbl = new ILabel();
        ideLbl = new ILabel();
        ideTypeLbl = new ILabel();

        directoryLbl.setForeground(Color.gray);
        ideLbl.setForeground(Color.gray);
        ideTypeLbl.setForeground(Color.gray);

        runIdeBtn = new JButton(imageResource.readImage("Common.Execute", 24));
        remarksTa = new ITextArea("", 10,10);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel detailPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Directory
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailPanel.add(new ILabel("Directory: ", ILabel.RIGHT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailPanel.add(directoryLbl, gbc);

        // IDE
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailPanel.add(new ILabel("IDE: ", ILabel.RIGHT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailPanel.add(ideLbl, gbc);

        // Type
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailPanel.add(new ILabel("Type: ", ILabel.RIGHT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailPanel.add(ideTypeLbl, gbc);

        // Button
        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.NONE;
        detailPanel.add(runIdeBtn, gbc);

        // Border
        detailPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Remarks
        JScrollPane scrollPane = new JScrollPane(remarksTa);

        // Add
        add(detailPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {

            ProjectCode code = (ProjectCode) object;

            directoryLbl.setText(code.getName());
            directoryLbl.setToolTipText(code.getDirectory());
            ideLbl.setText(code.getProjectIDE().getName());
            ideTypeLbl.setText(code.getProjectIDE().getProjectType());
            remarksTa.setText(code.getRemarks());

            setVisible(true);
        } else {
            setVisible(false);
        }
    }
}