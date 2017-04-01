package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.SQLException;

import static com.waldo.inventory.database.DbManager.dbInstance;
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

    ITextField detailName;
    ITextField detailWebsite;

    Action searchAction;
    ListSelectionListener manufacturerChanged;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Manufacturer selectedManufacturer;


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
                updateComponents(null);
            }

            @Override
            protected void add() {
                DbObjectDialog<Manufacturer> dialog = new DbObjectDialog<>(application, "New Manufacturer", new Manufacturer());
                if (dialog.showDialog() == DbObjectDialog.OK) {
                    Manufacturer m = dialog.getDbObject();
                    m.save();
                }

            }

            @Override
            protected void delete() {
                if (selectedManufacturer != null) {
                    int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete \"" + selectedManufacturer.getName() + "\"?");
                    if (res == JOptionPane.OK_OPTION) {
                        selectedManufacturer.delete();
                        selectedManufacturer = null;
                    }
                }
            }

            @Override
            protected void update() {
                if (selectedManufacturer != null) {
                    DbObjectDialog<Manufacturer> dialog = new DbObjectDialog<>(application, "New Manufacturer", selectedManufacturer);
                    if (dialog.showDialog() == DbObjectDialog.OK) {
                        selectedManufacturer.save();
                    }
                }
            }
        };
        toolBar.setFloatable(false);

        // Details
        detailName = new ITextField("Name");
        detailWebsite = new ITextField("Web stie");
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(new ITitledPanel("Manufacturers",
                new JComponent[] {searchField, new JScrollPane(manufacturerList), toolBar}), BorderLayout.WEST);
//        getContentPanel().add(new ITitledPanel("Manufacturers",
//                new JComponent[] {createListPanelLayout()}), BorderLayout.WEST);

        // New JPanel with box layout for details?

        getContentPanel().add(new ITitledEditPanel("Details",
                new String[] {"Name: ", "Web site: "},
                new JComponent[] {detailName, detailWebsite}), BorderLayout.CENTER);

        setPositiveButton("Ok");

        setPreferredSize(getPreferredSize());
        validate();
    }

    @Override
    public void updateComponents(Object object) {
        // Get all manus
        manufacturerDefaultListModel.removeAllElements();
        try {
            for(Manufacturer m : dbInstance().getManufacturers()) {
                manufacturerDefaultListModel.addElement(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAdded(Manufacturer manufacturer) {
        updateComponents(null);
    }

    @Override
    public void onUpdated(Manufacturer manufacturer) {
        updateComponents(null);
    }

    @Override
    public void onDeleted(Manufacturer manufacturer) {
        updateComponents(null);
    }
}
