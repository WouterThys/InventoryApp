package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialogPanel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledPanel;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static javax.swing.SpringLayout.*;

public abstract class ManufacturersDialogLayout extends IDialogPanel
        implements GuiInterface, DbObjectChangedListener<Manufacturer> {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<Manufacturer> manufacturerList;
    private DefaultListModel<Manufacturer> manufacturerDefaultListModel;
    IdBToolBar toolBar;
    ITextField searchField;

    Action searchAction;
    ListSelectionListener manufacturerChanged;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    ManufacturersDialogLayout(Application application, JDialog dialog) {
        super(application, dialog, true);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel createListPanelLayout() {
        JPanel panel = new JPanel();
        SpringLayout layout = new SpringLayout();
        JScrollPane scrollPane = new JScrollPane(manufacturerList);

        // Search
        layout.putConstraint(WEST, searchField, 5, WEST, panel);
        layout.putConstraint(EAST, searchField, -5, EAST, panel);
        layout.putConstraint(NORTH, searchField, 5, NORTH, panel);

        // List
        layout.putConstraint(WEST, scrollPane, 5, WEST, panel);
        layout.putConstraint(EAST, scrollPane, -5, EAST, panel);
        layout.putConstraint(NORTH, scrollPane, 5, SOUTH, searchField);
        layout.putConstraint(SOUTH, scrollPane, 5, NORTH, toolBar);

        // Toolbar
        layout.putConstraint(WEST, toolBar, 5, WEST, panel);
        layout.putConstraint(EAST, toolBar, -5, EAST, panel);
        layout.putConstraint(SOUTH, toolBar, 5, SOUTH, panel);

        // Add stuff
        panel.add(searchField);
        panel.add(scrollPane);
        panel.add(toolBar);
        panel.setPreferredSize(new Dimension(100, 500));
        panel.setLayout(layout);

        return panel;
    }

    /*
    *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title
        setTitleIcon(resourceManager.readImage("ManufacturersDialog.TitleIcon"));
        setTitleName("Manufacturers");

        // Search
        searchField = new ITextField("Search");
        searchField.setMaximumSize(new Dimension(100,30));
        searchField.addActionListener(searchAction);

        // Manufacturers list
        manufacturerDefaultListModel = new DefaultListModel<>();
        manufacturerList = new JList<>(manufacturerDefaultListModel);
        manufacturerList.addListSelectionListener(manufacturerChanged);


        toolBar = new IdBToolBar(IdBToolBar.HORIZONTAL) {
            @Override
            protected void refresh() {

            }

            @Override
            protected void add() {

            }

            @Override
            protected void delete() {

            }

            @Override
            protected void update() {

            }
        };
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(new ITitledPanel("Manufacturers",
                new JComponent[] {searchField, new JScrollPane(manufacturerList), toolBar}), BorderLayout.WEST);
//        getContentPanel().add(new ITitledPanel("Manufacturers",
//                new JComponent[] {createListPanelLayout()}), BorderLayout.WEST);

        setPositiveButton("Ok");
    }

    @Override
    public void updateComponents(Object object) {

    }


    @Override
    public void onAdded(Manufacturer manufacturer) {

    }

    @Override
    public void onUpdated(Manufacturer manufacturer) {

    }

    @Override
    public void onDeleted(Manufacturer manufacturer) {

    }
}
