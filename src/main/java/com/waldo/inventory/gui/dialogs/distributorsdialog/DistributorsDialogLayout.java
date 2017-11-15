package com.waldo.inventory.gui.dialogs.distributorsdialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.OrderFileFormat;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.editorderfileformatdialog.EditOrderFileFormatDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;
import static javax.swing.SpringLayout.*;

public abstract class DistributorsDialogLayout extends IDialog implements
        ListSelectionListener,
        CacheChangedListener<Distributor>,
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
    PanelUtils.IBrowseWebPanel browseDistributorPanel;
    PanelUtils.IBrowseWebPanel browseOrderLinkPanel;
    ILabel detailLogo;
    IComboBox<OrderFileFormat> detailOrderFileFormatCb;
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
        detailOrderFileFormatCb.updateList();
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
        TitledBorder titledBorder = PanelUtils.createTitleBorder("Info");
        JPanel panel = new JPanel(new BorderLayout(5,5));

        // Panels
        JPanel textFieldPanel = new JPanel();
        JPanel orderPanel = new JPanel(new GridBagLayout());

        // - Text fields
        PanelUtils.GridBagHelper gbh = new PanelUtils.GridBagHelper(textFieldPanel);
        gbh.addLine("Name: ", detailName);
        gbh.addLine("Web site: ", browseDistributorPanel);
        gbh.add(detailLogo, 1, 2, 1, 1);

        // - Order stuff
        gbh = new PanelUtils.GridBagHelper(orderPanel);
        gbh.addLine("Order link: ", browseOrderLinkPanel);
        gbh.gridwidth = 2;
        gbh.addLine("File format: ", detailOrderFileFormatCb, GridBagConstraints.BOTH);
        gbh.gridwidth = 1;
        gbh.add(detailOrderFileFormatTb, 1, 2);

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
        // Dialog
        setTitleIcon(imageResource.readImage("Distributors.Title"));
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
        detailLogo = new ILabel();
        detailLogo.setHorizontalAlignment(SwingConstants.RIGHT);

        browseDistributorPanel = new PanelUtils.IBrowseWebPanel("Web site", "website", this);
        browseOrderLinkPanel = new PanelUtils.IBrowseWebPanel("Order link", "orderLink", this);

        detailOrderFileFormatCb = new IComboBox<>(cache().getOrderFileFormats(), new DbObjectNameComparator<>(), true);
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
    public void updateComponents(Object... object) {
        if (application.isUpdating()) {
            return;
        }
        application.beginWait();
        try {
            // Get all
            distributorDefaultListModel.removeAllElements();
            for (Distributor d : cache().getDistributors()) {
                if (!d.isUnknown()) {
                    distributorDefaultListModel.addElement(d);
                }
            }

            selectedDistributor = (Distributor) object[0];
            updateEnabledComponents();

            if (selectedDistributor != null) {
                originalDistributor = selectedDistributor.createCopy();
                distributorList.setSelectedValue(selectedDistributor, true);
            } else {
                originalDistributor = null;
            }
        } finally {
            application.endWait();
        }

    }
}
