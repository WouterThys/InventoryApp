package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;
import static javax.swing.SpringLayout.*;

abstract class ManufacturersDialogLayout extends IDialog implements
        ListSelectionListener,
        CacheChangedListener<Manufacturer>,
        IObjectSearchPanel.IObjectSearchListener,
        IObjectSearchPanel.IObjectSearchBtnListener,
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
    PanelUtils.IBrowseWebPanel browsePanel;
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
     *                  METHODS
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

        // Panels
        JPanel textFieldPanel = new JPanel(new GridBagLayout());
        JPanel listPanel = new JPanel(new GridBagLayout());
        JPanel panel = new JPanel(new BorderLayout(5,5));

        // - Text fields
        PanelUtils.GridBagHelper gbh = new PanelUtils.GridBagHelper(textFieldPanel);
        gbh.addLine("Name: ", detailName);
        gbh.addLine("Web site: ", browsePanel);
        gbh.add(detailLogo, 1, 2, 1, 1);

        // Item list
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        listPanel.add(new JLabel("Items: "), gbc);

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
        setTitleIcon(imageResource.readImage("Manufacturers.Title"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Search
        searchPanel = new IObjectSearchPanel(false, DbObject.TYPE_MANUFACTURER);
        searchPanel.addSearchListener(this);
        searchPanel.addSearchBtnListener(this);

        // Manufacturers list
        manufacturerDefaultListModel = new DefaultListModel<>();
        manufacturerList = new JList<>(manufacturerDefaultListModel);
        manufacturerList.addListSelectionListener(this);

        toolBar = new IdBToolBar(this, IdBToolBar.HORIZONTAL);
        toolBar.setFloatable(false);

        // Details
        detailName = new ITextField("Name");
        detailName.setEnabled(false);
        detailLogo = new ILabel();
        detailLogo.setHorizontalAlignment(SwingConstants.RIGHT);
        browsePanel = new PanelUtils.IBrowseWebPanel("Web site", "website", this);

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
