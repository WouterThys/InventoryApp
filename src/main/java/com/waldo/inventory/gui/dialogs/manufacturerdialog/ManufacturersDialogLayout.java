package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;

import static com.waldo.inventory.database.DbManager.db;
import static javax.swing.SpringLayout.*;

public abstract class ManufacturersDialogLayout extends IDialog implements
        GuiInterface,
        ListSelectionListener,
        DbObjectChangedListener<Manufacturer>,
        IObjectSearchPanel.IObjectSearchListener,
        IdBToolBar.IdbToolBarListener,
        IEditedListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<Manufacturer> manufacturerList;
    DefaultListModel<Manufacturer> manufacturerDefaultListModel;
    private IdBToolBar toolBar;
    private IObjectSearchPanel searchPanel;

    ITextField detailName;
    ITextField detailWebsite;
    private JButton detailsBrowseButton;
    ILabel detailLogo;

    private JList<Item> detailItemList;
    DefaultListModel<Item> detailItemDefaultListModel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Manufacturer selectedManufacturer;
    Manufacturer originalManufacturer;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ManufacturersDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        if (selectedManufacturer == null || selectedManufacturer.isUnknown()) {
            toolBar.setDeleteActionEnabled(false);
            toolBar.setEditActionEnabled(false);
        } else {
            toolBar.setDeleteActionEnabled(true);
            toolBar.setEditActionEnabled(true);
        }
    }


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

    private JPanel createManufacturerDetailsPanel() {

        TitledBorder titledBorder = BorderFactory.createTitledBorder("Info");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        JPanel panel = new JPanel(new BorderLayout(5,5));

        // Text fields
        JPanel textFieldPanel = new JPanel(new GridBagLayout());

        // - Name
        ILabel nameLabel = new ILabel("Name: ");
        nameLabel.setHorizontalAlignment(ILabel.RIGHT);
        nameLabel.setVerticalAlignment(ILabel.CENTER);

        // - Browse
        ILabel browseLabel = new ILabel("Web site: ");
        browseLabel.setHorizontalAlignment(ILabel.RIGHT);
        browseLabel.setVerticalAlignment(ILabel.CENTER);

        JPanel browsePanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0; constraints.weightx = 1;
        constraints.gridy = 0; constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        browsePanel.add(detailWebsite, constraints);
        constraints.gridx = 1; constraints.weightx = 0;
        constraints.gridy = 0; constraints.weighty = 0;
        constraints.fill = GridBagConstraints.VERTICAL;
        detailsBrowseButton.setSize(new Dimension(detailsBrowseButton.getWidth(), detailWebsite.getHeight()));
        browsePanel.add(detailsBrowseButton, constraints);

        // - Add to panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        textFieldPanel.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 3;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        textFieldPanel.add(detailName, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        textFieldPanel.add(browseLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 3;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        textFieldPanel.add(browsePanel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.NONE;
        textFieldPanel.add(detailLogo, gbc);

        // Item list
        JPanel listPanel = new JPanel(new GridBagLayout());

        JLabel itemLabel = new JLabel("Items: ");

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        listPanel.add(itemLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        listPanel.add(new JScrollPane(detailItemList), gbc);

        // Add all
        panel.add(textFieldPanel, BorderLayout.NORTH);
        panel.add(listPanel, BorderLayout.CENTER);
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
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Search
        searchPanel = new IObjectSearchPanel(false, DbObject.TYPE_MANUFACTURER);
        searchPanel.addSearchListener(this);

        // Manufacturers list
        manufacturerDefaultListModel = new DefaultListModel<>();
        manufacturerList = new JList<>(manufacturerDefaultListModel);
        manufacturerList.addListSelectionListener(this);

        toolBar = new IdBToolBar(this, IdBToolBar.HORIZONTAL);
        toolBar.setFloatable(false);

        // Details
        detailName = new ITextField("Name");
        detailName.setEnabled(false);
        detailWebsite = new ITextField("Web site");
        detailWebsite.addEditedListener(this, "website");
        detailLogo = new ILabel();
        //detailLogo.setPreferredSize(new Dimension(48,48));
        detailLogo.setHorizontalAlignment(SwingConstants.RIGHT);
        detailsBrowseButton = new JButton(resourceManager.readImage("Common.BrowseWebSiteIcon"));
        detailsBrowseButton.addActionListener(e -> {
            if (!detailWebsite.getText().isEmpty())
                try {
                    OpenUtils.browseLink(detailWebsite.getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(ManufacturersDialogLayout.this, "Unable to browse: " + detailWebsite.getText(), "Browse error", JOptionPane.ERROR_MESSAGE);
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

        getContentPanel().add(createManufacturerDetailsPanel(), BorderLayout.CENTER);

        pack();
    }
}
