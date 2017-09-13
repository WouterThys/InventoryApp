package com.waldo.inventory.gui.dialogs.customlocationdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILocationButton;
import com.waldo.inventory.gui.components.ILocationMapPanel;
import com.waldo.inventory.gui.components.ITextArea;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public abstract class CustomLocationDialogLayout extends IDialog implements
        ILocationMapPanel.LocationClickListener,
        ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILocationMapPanel locationMapPanel;
    ITextArea inputTa;
    JButton convertBtn;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    List<ILocationButton> locationButtonList = new ArrayList<>();

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    CustomLocationDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel createEastPanel() {
        JPanel eastPanel = new JPanel(new BorderLayout());

        eastPanel.add(new JScrollPane(inputTa), BorderLayout.CENTER);
        eastPanel.add(convertBtn, BorderLayout.SOUTH);

        TitledBorder titledBorder = PanelUtils.createTitleBorder("Edit");
        eastPanel.setBorder(titledBorder);

        return eastPanel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        showTitlePanel(false);
        setResizable(true);

        // Components
        locationMapPanel = new ILocationMapPanel(application, this);
        locationMapPanel.setPreferredSize(new Dimension(300,300));
        inputTa = new ITextArea();
        convertBtn = new JButton("Convert");
        convertBtn.addActionListener(this);

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(locationMapPanel, BorderLayout.CENTER);
        getContentPanel().add(createEastPanel(), BorderLayout.EAST);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object object) {

    }
}