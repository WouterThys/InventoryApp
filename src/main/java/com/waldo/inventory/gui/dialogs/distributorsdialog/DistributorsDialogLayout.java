package com.waldo.inventory.gui.dialogs.distributorsdialog;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.OrderFileFormat;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.editorderfileformatdialog.EditOrderFileFormatDialog;

import javax.swing.*;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;

import static com.waldo.inventory.gui.Application.imageResource;
import static javax.swing.SpringLayout.*;

public abstract class DistributorsDialogLayout extends IDialog implements
        ListSelectionListener,
        DbObjectChangedListener<Distributor>,
        IObjectSearchPanel.IObjectSearchListener,
        IObjectSearchPanel.IObjectSearchBtnListener,
        IdBToolBar.IdbToolBarListener,
        IEditedListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<Distributor> distributorList;
    private DefaultListModel<Distributor> distributorDefaultListModel;
    private IdBToolBar toolBar;
    private IObjectSearchPanel searchPanel;

    ITextField detailName;
    ITextField detailWebsite;
    private JButton detailsBrowseButton;
    ILabel detailLogo;

    ITextField detailOrderLink;
    private JButton detailOrderLinkBtn;
    IComboBox<OrderFileFormat> detailOrderFileFormatCb;
    private DefaultComboBoxModel<OrderFileFormat> detailOrderFileFormatModel;
    private IdBToolBar detailOrderFileFormatTb;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Distributor selectedDistributor;
    Distributor originalDistributor;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    DistributorsDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected void updateEnabledComponents() {
        toolBar.setEditActionEnabled((selectedDistributor != null));
        toolBar.setDeleteActionEnabled((selectedDistributor != null));

        if (selectedDistributor != null) {
            detailOrderFileFormatTb.setRefreshActionEnabled(true);
            detailOrderFileFormatTb.setAddActionEnabled(true);

            OrderFileFormat off = selectedDistributor.getOrderFileFormat();
            if (off != null && !off.isUnknown()) {
                detailOrderFileFormatTb.setDeleteActionEnabled(true);
                detailOrderFileFormatTb.setEditActionEnabled(true);
            } else {
                detailOrderFileFormatTb.setDeleteActionEnabled(false);
                detailOrderFileFormatTb.setEditActionEnabled(false);
            }
        } else {
            detailOrderFileFormatTb.setRefreshActionEnabled(false);
            detailOrderFileFormatTb.setAddActionEnabled(false);
            detailOrderFileFormatTb.setDeleteActionEnabled(false);
            detailOrderFileFormatTb.setEditActionEnabled(false);
        }
    }

    private void updateOrderFileFormatComboBox() {
        detailOrderFileFormatModel.removeAllElements();
        for (OrderFileFormat off : DbManager.db().getOrderFileFormats()) {
            detailOrderFileFormatModel.addElement(off);
        }
    }

    private JPanel createWestPanel() {
        TitledBorder titledBorder = PanelUtils.createTitleBorder("Distributors");

        JPanel westPanel = new JPanel();
        JScrollPane list = new JScrollPane(distributorList);

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

    private JPanel createDistributorsDetailPanel() {
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

        // Order stuff
        JPanel orderPanel = new JPanel(new GridBagLayout());

        // - Link
        ILabel linkLabel = new ILabel("Order link: ");
        linkLabel.setHorizontalAlignment(ILabel.RIGHT);
        linkLabel.setVerticalAlignment(ILabel.CENTER);

        // - Format
        ILabel formatLabel = new ILabel("File format: ");
        formatLabel.setHorizontalAlignment(ILabel.RIGHT);
        formatLabel.setVerticalAlignment(ILabel.CENTER);

        // - Browse panel
        JPanel bPanel = PanelUtils.createBrowsePanel(detailOrderLink, detailOrderLinkBtn);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        orderPanel.add(linkLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        orderPanel.add(bPanel, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        orderPanel.add(formatLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        orderPanel.add(detailOrderFileFormatCb, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        orderPanel.add(detailOrderFileFormatTb, gbc);

        // Add all
        panel.add(textFieldPanel, BorderLayout.CENTER);
        panel.add(orderPanel, BorderLayout.SOUTH);
        panel.setBorder(titledBorder);

        return panel;
    }

    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title
        setTitleIcon(imageResource.readImage("DistributorsDialog.TitleIcon"));
        setTitleName("Distributors");
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Search
        searchPanel = new IObjectSearchPanel(false, DbObject.TYPE_DISTRIBUTOR);
        searchPanel.addSearchListener(this);
        searchPanel.addSearchBtnListener(this);

        // Distributor list
        distributorDefaultListModel = new DefaultListModel<>();
        distributorList = new JList<>(distributorDefaultListModel);
        distributorList.addListSelectionListener(this);

        toolBar = new IdBToolBar(this);

        // Details
        detailName = new ITextField("Name");
        detailName.setEnabled(false);
        detailWebsite = new ITextField("Web site");
        detailWebsite.addEditedListener(this, "website");
        detailLogo = new ILabel();
        detailLogo.setHorizontalAlignment(SwingConstants.RIGHT);
        detailsBrowseButton = new JButton(imageResource.readImage("Common.BrowseWebSiteIcon"));
        detailsBrowseButton.addActionListener(e -> {
            if (!detailWebsite.getText().isEmpty()) {
                try {
                    OpenUtils.browseLink(detailWebsite.getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(DistributorsDialogLayout.this, "Unable to browse: " + detailWebsite.getText(), "Browse error", JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        });

        detailOrderLink = new ITextField("Order link");
        detailOrderLink.addEditedListener(this, "orderLink");
        detailOrderLinkBtn = new JButton(imageResource.readImage("Common.BrowseWebSiteIcon"));
        detailOrderLinkBtn.addActionListener(e -> {
            if (!detailOrderLink.getText().isEmpty()) {
                try {
                    OpenUtils.browseLink(detailOrderLink.getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(DistributorsDialogLayout.this, "Unable to browse: " + detailOrderLink.getText(), "Browse error", JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        });

        detailOrderFileFormatModel = new DefaultComboBoxModel<>();
        detailOrderFileFormatCb = new IComboBox<>(detailOrderFileFormatModel);
        detailOrderFileFormatCb.addEditedListener(this, "orderFileFormatId");
        detailOrderFileFormatTb = new IdBToolBar(new IdBToolBar.IdbToolBarListener() {
            @Override
            public void onToolBarRefresh(IdBToolBar source) {
                updateOrderFileFormatComboBox();
            }

            @Override
            public void onToolBarAdd(IdBToolBar source) {
                EditOrderFileFormatDialog dialog = new EditOrderFileFormatDialog(application, "Add format", new OrderFileFormat());
                if (dialog.showDialog() == IDialog.OK) {
                    OrderFileFormat off = dialog.getOrderFileFormat();
                    off.save();
                }
            }

            @Override
            public void onToolBarDelete(IdBToolBar source) {
                OrderFileFormat off = (OrderFileFormat) detailOrderFileFormatCb.getSelectedItem();
                if (off != null) {
                    int result = JOptionPane.showConfirmDialog(DistributorsDialogLayout.this,
                            "Are you sure you want to delete " + off.getName(),
                            "Delete format",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        off.delete();
                    }
                }

            }

            @Override
            public void onToolBarEdit(IdBToolBar source) {
                OrderFileFormat off = (OrderFileFormat) detailOrderFileFormatCb.getSelectedItem();
                if (off != null) {
                    EditOrderFileFormatDialog dialog = new EditOrderFileFormatDialog(application, "Edit format", off);
                    if (dialog.showDialog() == IDialog.OK) {
                        off.save();
                    }
                }
            }
        });
        detailOrderFileFormatTb.setAlignmentX(RIGHT_ALIGNMENT);
        updateOrderFileFormatComboBox();

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(createWestPanel(), BorderLayout.WEST);

        getContentPanel().add(createDistributorsDetailPanel(), BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (application.isUpdating()) {
            return;
        }
        application.beginWait();
        try {
            // Get all
            distributorDefaultListModel.removeAllElements();
            for (Distributor d : DbManager.db().getDistributors()) {
                if (!d.isUnknown()) {
                    distributorDefaultListModel.addElement(d);
                }
            }

            selectedDistributor = (Distributor) object;
            updateEnabledComponents();

            if (selectedDistributor != null) {
                originalDistributor = selectedDistributor.createCopy();
                distributorList.setSelectedValue(selectedDistributor, true);
            } else {
                selectedDistributor = null;
            }
        } finally {
            application.endWait();
        }

    }
}
