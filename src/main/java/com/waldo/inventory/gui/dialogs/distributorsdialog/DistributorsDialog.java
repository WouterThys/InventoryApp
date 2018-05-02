package com.waldo.inventory.gui.dialogs.distributorsdialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.DistributorOrderFlow;
import com.waldo.inventory.classes.dbclasses.OrderFileFormat;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.components.IImagePanel;
import com.waldo.inventory.gui.components.IResourceDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.IOrderFlowTableModel;
import com.waldo.inventory.gui.dialogs.editdistributororderflowdialog.EditDistributorOrderflowDialog;
import com.waldo.inventory.gui.dialogs.editorderfileformatdialog.EditOrderFileFormatDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ITable;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class DistributorsDialog extends IResourceDialog<Distributor> implements IdBToolBar.IdbToolBarListener {

    private static final ImageIcon icon = imageResource.readIcon("Distributors.Title");

    private ITextField detailName;
    private GuiUtils.IBrowseWebPanel browseDistributorPanel;
    private GuiUtils.IBrowseWebPanel browseOrderLinkPanel;
    private IComboBox<DistributorType> distributorTypeCb;
    private IImagePanel detailLogo;
    private IComboBox<OrderFileFormat> detailOrderFileFormatCb;
    //private IdBToolBar detailOrderFileFormatTb;
    private IActions.EditAction editOrderFileAa;
    private IActions.DeleteAction deleteOrderFileAa;

    private IOrderFlowTableModel tableModel;
    private ITable<DistributorOrderFlow> orderFlowTable;

    private IActions.EditAction editOrderFlowAa;

    public DistributorsDialog(Window window) {
        super(window, "Distributors", Distributor.class);
    }

    @Override
    protected List<Distributor> getAllResources() {
        return cache().getDistributors();
    }

    @Override
    protected Distributor getNewResource() {
        return new Distributor();
    }

    @Override
    protected void initializeDetailComponents() {
        setTitleIcon(icon);
        setResizable(true);

        detailName = new ITextField("Name");
        detailName.setEnabled(false);
        detailLogo = new IImagePanel(this, ImageType.DistributorImage, "", this, new Dimension(128, 128));

        distributorTypeCb = new IComboBox<>(DistributorType.values());
        distributorTypeCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                getObject().setDistributorType((DistributorType) distributorTypeCb.getSelectedItem());
                onValueChanged(distributorTypeCb, "distributorType", null, null);
            }
        });

        browseDistributorPanel = new GuiUtils.IBrowseWebPanel("Web site", "website", this);
        browseOrderLinkPanel = new GuiUtils.IBrowseWebPanel("Order link", "orderLink", this);

        tableModel = new IOrderFlowTableModel();
        orderFlowTable = new ITable<>(tableModel);

        editOrderFlowAa = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditDistributorOrderflowDialog dialog = new EditDistributorOrderflowDialog(DistributorsDialog.this, getSelectedResource());
                dialog.showDialog();

                getSelectedResource().updateOrderFlowTemplate();
                tableModel.setItemList(getSelectedResource().getOrderFlowTemplate());
            }
        };

        detailOrderFileFormatCb = new IComboBox<>(cache().getOrderFileFormats(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        detailOrderFileFormatCb.addEditedListener(this, "orderFileFormatId");
        detailOrderFileFormatCb.updateList();

        editOrderFileAa = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderFileFormat off = (OrderFileFormat) detailOrderFileFormatCb.getSelectedItem();
                editOrderFile(off);
            }
        };

        deleteOrderFileAa = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderFileFormat off = (OrderFileFormat) detailOrderFileFormatCb.getSelectedItem();
                deleteOrderFile(off);
            }
        };
    }

    @Override
    protected JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));

        // Panels
        JPanel textFieldPanel = new JPanel();
        JPanel orderPanel = new JPanel(new GridBagLayout());
        JPanel orderFlowPanel = new JPanel(new BorderLayout());

        JPanel orderFlowTbPnl = new JPanel(new BorderLayout());
        JPanel orderFileTbPnl = new JPanel(new BorderLayout());

        orderFlowTbPnl.add(GuiUtils.createNewToolbar(editOrderFlowAa), BorderLayout.EAST);
        orderFileTbPnl.add(GuiUtils.createNewToolbar(deleteOrderFileAa, editOrderFileAa), BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(orderFlowTable);
        orderFlowPanel.add(orderFlowTbPnl, BorderLayout.SOUTH);
        orderFlowPanel.add(scrollPane, BorderLayout.CENTER);
        orderFlowPanel.setBorder(GuiUtils.createInlineTitleBorder("Order flow"));

        // - Text fields
        GuiUtils.GridBagHelper gbh = new GuiUtils.GridBagHelper(textFieldPanel);
        gbh.addLine("Name: ", detailName);
        gbh.addLine("Web site: ", browseDistributorPanel);
        gbh.addLine("Type: ", distributorTypeCb);
        gbh.add(detailLogo, 1, 3, 1, 1);
        textFieldPanel.setBorder(GuiUtils.createInlineTitleBorder("Details"));

        // - Order stuff
        gbh = new GuiUtils.GridBagHelper(orderPanel);
        gbh.addLine("Order link: ", browseOrderLinkPanel);
        gbh.gridwidth = 2;
        gbh.addLine("File format: ", detailOrderFileFormatCb, GridBagConstraints.BOTH);
        gbh.gridwidth = 1;
        gbh.anchor = GridBagConstraints.EAST;
        gbh.add(orderFileTbPnl, 1, 2);
        orderPanel.setBorder(GuiUtils.createInlineTitleBorder("Order file"));

        // Add all
        panel.add(textFieldPanel, BorderLayout.NORTH);
        panel.add(orderFlowPanel, BorderLayout.CENTER);
        panel.add(orderPanel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void updateEnabledComponents() {
        super.updateEnabledComponents();

        if (getObject() != null) {
            editOrderFileAa.setEnabled(true);
            deleteOrderFileAa.setEnabled(true);
            distributorTypeCb.setEnabled(true);
            editOrderFlowAa.setEnabled(true);

            OrderFileFormat off = getObject().getOrderFileFormat();
            if (off != null && !off.isUnknown()) {
                deleteOrderFileAa.setEnabled(true);
            } else {
                deleteOrderFileAa.setEnabled(false);
            }
        } else {
            distributorTypeCb.setEnabled(false);
            editOrderFlowAa.setEnabled(false);
            editOrderFileAa.setEnabled(false);
            deleteOrderFileAa.setEnabled(false);
        }
    }

    @Override
    protected void setDetails(Distributor distributor) {
        if (distributor != null) {
            detailName.setText(distributor.getName());
            browseDistributorPanel.setText(distributor.getWebsite());
            detailLogo.setImage(distributor.getIconPath());
            distributorTypeCb.setSelectedItem(distributor.getDistributorType());

            // Order flow
            tableModel.setItemList(distributor.getOrderFlowTemplate());

            // Orders
            browseOrderLinkPanel.setText(distributor.getOrderLink());
            detailOrderFileFormatCb.setSelectedItem(distributor.getOrderFileFormat());
        } else {
            detailName.clearText();
            browseDistributorPanel.clearText();
            detailLogo.setImage(imageResource.getDefaultImage(ImageType.DistributorImage));
            distributorTypeCb.setSelectedItem(null);
            tableModel.clearItemList();
            browseOrderLinkPanel.clearText();
            detailOrderFileFormatCb.setSelectedItem(null);
        }
    }

    @Override
    public VerifyState verify(Distributor d) {
        VerifyState ok = VerifyState.Ok;
        if (detailName.getText().isEmpty()) {
            detailName.setError("Name can't be empty");
            ok = VerifyState.Error;
        } else {
            if (d.getId() < DbObject.UNKNOWN_ID) {
                if (SearchManager.sm().findDistributorByName(detailName.getText()) != null) {
                    detailName.setError("Name already exists..");
                    ok = VerifyState.Error;
                }
            }
        }

        return ok;
    }

    @Override
    public void clearDetails() {
        detailName.setText("");
        browseDistributorPanel.clearText();
        detailLogo.setImage(imageResource.getDefaultImage(ImageType.DistributorImage));

        // Order flow
        //..

        // List
        browseOrderLinkPanel.clearText();
        detailOrderFileFormatCb.setSelectedItem(null);
    }

    //
    // Tool bars
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        detailOrderFileFormatCb.updateList();
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        EditOrderFileFormatDialog dialog = new EditOrderFileFormatDialog(DistributorsDialog.this, "Add format", new OrderFileFormat());
        if (dialog.showDialog() == ICacheDialog.OK) {
            OrderFileFormat off = dialog.getOrderFileFormat();
            off.save();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        OrderFileFormat off = (OrderFileFormat) detailOrderFileFormatCb.getSelectedItem();
        if (off != null) {
            int result = JOptionPane.showConfirmDialog(DistributorsDialog.this,
                    "Are you sure you want to delete " + off.getName(),
                    "Delete format",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                off.delete();
            }
        }

    }

    private void deleteOrderFile(OrderFileFormat off) {
        if (off != null) {
            int result = JOptionPane.showConfirmDialog(DistributorsDialog.this,
                    "Are you sure you want to delete " + off.getName(),
                    "Delete format",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                off.delete();
            }
        }
    }

    private void editOrderFile(OrderFileFormat off) {
        if (off != null) {
            EditOrderFileFormatDialog dialog = new EditOrderFileFormatDialog(DistributorsDialog.this, "Edit format", off);
            if (dialog.showDialog() == ICacheDialog.OK) {
                off.save();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        OrderFileFormat off = (OrderFileFormat) detailOrderFileFormatCb.getSelectedItem();
        if (off != null) {
            EditOrderFileFormatDialog dialog = new EditOrderFileFormatDialog(DistributorsDialog.this, "Edit format", off);
            if (dialog.showDialog() == ICacheDialog.OK) {
                off.save();
            }
        }
    }

}
