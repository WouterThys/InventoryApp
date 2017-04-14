package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;

import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;
import static com.waldo.inventory.database.DbManager.dbInstance;
import static javax.swing.SpringLayout.*;

public abstract class ManufacturersDialogLayout extends IDialog
        implements GuiInterface, DbObjectChangedListener<Manufacturer>, IObjectSearchPanel.IObjectSearchListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<Manufacturer> manufacturerList;
    private DefaultListModel<Manufacturer> manufacturerDefaultListModel;
    private IdBToolBar toolBar;
    private IObjectSearchPanel searchPanel;

    ITextField detailName;
    ITextField detailWebsite;
    private JButton detailsBroweButton;
    ILabel detailLogo;

    private JList<Item> detailItemList;
    DefaultListModel<Item> detailItemDefaultListModel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Manufacturer selectedManufacturer;


    ManufacturersDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel createWestPanel() {
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Manufacturers");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        JPanel westPanel = new JPanel();
        JScrollPane list = new JScrollPane(manufacturerList);

        SpringLayout layout = new SpringLayout();
        // Search panel
        layout.putConstraint(NORTH, searchPanel, 5, NORTH, westPanel);
        layout.putConstraint(EAST, searchPanel, -5, EAST, westPanel);
        layout.putConstraint(WEST, searchPanel, 5, WEST, westPanel);

        // Sub division list
        layout.putConstraint(EAST, list, -5, EAST, westPanel);
        layout.putConstraint(WEST, list, 5, WEST, westPanel);
        layout.putConstraint(SOUTH, list, -5, NORTH, toolBar);
        layout.putConstraint(NORTH, list, 2, SOUTH, searchPanel);

        // Tool bar
        layout.putConstraint(EAST, toolBar, -5, EAST, westPanel);
        layout.putConstraint(SOUTH, toolBar, -5, SOUTH, westPanel);
        layout.putConstraint(WEST, toolBar, 5, WEST, westPanel);

        // Add stuff
        westPanel.add(searchPanel);
        westPanel.add(list);
        westPanel.add(toolBar);
        westPanel.setLayout(layout);
        westPanel.setPreferredSize(new Dimension(300, 500));
        westPanel.setBorder(titledBorder);

        return westPanel;
    }

    private JPanel createManufacturerDetailsPanel(JPanel browsePanel) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Info");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        JPanel panel = new JPanel();
        SpringLayout layout = new SpringLayout();
        JScrollPane list = new JScrollPane(detailItemList);

        // Name
        ILabel nameLabel = new ILabel("Name: ");
        nameLabel.setHorizontalAlignment(ILabel.RIGHT);
        nameLabel.setVerticalAlignment(ILabel.CENTER);
        layout.putConstraint(NORTH, nameLabel, 5, NORTH, panel);
        layout.putConstraint(WEST, nameLabel, 5, WEST, panel);
        layout.putConstraint(SOUTH, nameLabel, 0, SOUTH, detailName);

        layout.putConstraint(NORTH, detailName, 5, NORTH, panel);
        layout.putConstraint(EAST, detailName, -5, EAST, panel);
        layout.putConstraint(WEST, detailName, 2, EAST, nameLabel);
        layout.putConstraint(WEST, detailName, 0, WEST, browsePanel);

        // Browse
        ILabel browseLabel = new ILabel("Web site: ");
        browseLabel.setHorizontalAlignment(ILabel.RIGHT);
        browseLabel.setVerticalAlignment(ILabel.CENTER);
        layout.putConstraint(NORTH, browseLabel, 0, NORTH, browsePanel);
        layout.putConstraint(WEST, browseLabel, 5, WEST, panel);
        layout.putConstraint(SOUTH, browseLabel, 0, SOUTH, browsePanel);

        layout.putConstraint(NORTH, browsePanel, 5, SOUTH, detailName);
        layout.putConstraint(EAST, browsePanel, -5, EAST, panel);
        layout.putConstraint(WEST, browsePanel, 5, EAST, browseLabel);

        // Logo
        layout.putConstraint(EAST, detailLogo, -5, EAST, panel);
        layout.putConstraint(NORTH, detailLogo, 5, SOUTH, browsePanel);

        // Item list
        ILabel itemLabel = new ILabel("Manufacturer items: ");
        itemLabel.setHorizontalAlignment(ILabel.RIGHT);
        itemLabel.setVerticalAlignment(ILabel.CENTER);
        layout.putConstraint(NORTH, itemLabel, 5, SOUTH, detailLogo);
        layout.putConstraint(WEST, itemLabel, 5, WEST, panel);

        layout.putConstraint(NORTH, list, 2, SOUTH, itemLabel);
        layout.putConstraint(EAST, list, -5, EAST, panel);
        layout.putConstraint(WEST, list, 5, WEST, panel);
        layout.putConstraint(SOUTH, list, -5, SOUTH, panel);


        // Add stuff
        panel.add(nameLabel);
        panel.add(detailName);

        panel.add(browseLabel);
        panel.add(browsePanel);

        panel.add(detailLogo);

        panel.add(itemLabel);
        panel.add(list);

        panel.setLayout(layout);
        panel.setPreferredSize(new Dimension(400, 500));
        panel.setBorder(titledBorder);

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
        searchPanel = new IObjectSearchPanel(false, this, DbObject.TYPE_MANUFACTURER);

        // Manufacturers list
        manufacturerDefaultListModel = new DefaultListModel<>();
        manufacturerList = new JList<>(manufacturerDefaultListModel);

        toolBar = new IdBToolBar(IdBToolBar.HORIZONTAL) {
            @Override
            protected void refresh() {
                updateComponents(null);
            }

            @Override
            protected void add() {
                DbObjectDialog<Manufacturer> dialog = new DbObjectDialog<>(application, "New Manufacturer");
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
                    DbObjectDialog<Manufacturer> dialog = new DbObjectDialog<>(application, "Update " + selectedManufacturer.getName(), selectedManufacturer);
                    if (dialog.showDialog() == DbObjectDialog.OK) {
                        selectedManufacturer.save();
                    }
                }
            }
        };
        toolBar.setFloatable(false);

        // Details
        detailName = new ITextField("Name");
        detailName.setEnabled(false);
        detailWebsite = new ITextField("Web site");
        detailLogo = new ILabel();
        detailLogo.setPreferredSize(new Dimension(48,48));
        detailLogo.setHorizontalAlignment(SwingConstants.RIGHT);
        detailsBroweButton = new JButton(resourceManager.readImage("Common.BrowseWebSiteIcon"));
        detailsBroweButton.addActionListener(e -> {
            if (!detailWebsite.getText().isEmpty())
                try {
                    OpenUtils.browseLink(detailWebsite.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        });

        detailItemDefaultListModel = new DefaultListModel<>();
        detailItemList = new JList<>(detailItemDefaultListModel);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(createWestPanel(), BorderLayout.WEST);

        // Additional stuff
        JPanel browsePanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = createFieldConstraints(0,0);
        constraints.gridwidth = 1;
        browsePanel.add(detailWebsite, constraints);
        constraints = createFieldConstraints(1,0);
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        browsePanel.add(detailsBroweButton, constraints);

        getContentPanel().add(createManufacturerDetailsPanel(browsePanel), BorderLayout.CENTER);

//        details.add(new ITitledPanel("Items",
//                new JComponent[] {new JScrollPane(detailItemList)}));
    }

    @Override
    public void updateComponents(Object object) {
        // Get all menus
        manufacturerDefaultListModel.removeAllElements();
        for(Manufacturer m : dbInstance().getManufacturers()) {
            manufacturerDefaultListModel.addElement(m);
        }
    }
}
