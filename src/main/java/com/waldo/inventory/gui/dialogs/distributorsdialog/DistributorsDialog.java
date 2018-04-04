package com.waldo.inventory.gui.dialogs.distributorsdialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.OrderFileFormat;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IResourceDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.editorderfileformatdialog.EditOrderFileFormatDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class DistributorsDialog extends IResourceDialog<Distributor> {

    private static final ImageIcon icon = imageResource.readIcon("Distributors.Title");

    private ITextField detailName;
    private GuiUtils.IBrowseWebPanel browseDistributorPanel;
    private GuiUtils.IBrowseWebPanel browseOrderLinkPanel;
    private ILabel detailLogo;
    private IComboBox<OrderFileFormat> detailOrderFileFormatCb;
    private IdBToolBar detailOrderFileFormatTb;

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
        detailName = new ITextField("Name");
        detailName.setEnabled(false);
        detailLogo = new ILabel();
        detailLogo.setHorizontalAlignment(SwingConstants.RIGHT);

        browseDistributorPanel = new GuiUtils.IBrowseWebPanel("Web site", "website", this);
        browseOrderLinkPanel = new GuiUtils.IBrowseWebPanel("Order link", "orderLink", this);

        detailOrderFileFormatCb = new IComboBox<>(cache().getOrderFileFormats(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        detailOrderFileFormatCb.addEditedListener(this, "orderFileFormatId");
        detailOrderFileFormatTb = new IdBToolBar(new IdBToolBar.IdbToolBarListener() {
            @Override
            public void onToolBarRefresh(IdBToolBar source) {
                detailOrderFileFormatCb.updateList();
            }

            @Override
            public void onToolBarAdd(IdBToolBar source) {
                EditOrderFileFormatDialog dialog = new EditOrderFileFormatDialog(DistributorsDialog.this, "Add format", new OrderFileFormat());
                if (dialog.showDialog() == IDialog.OK) {
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

            @Override
            public void onToolBarEdit(IdBToolBar source) {
                OrderFileFormat off = (OrderFileFormat) detailOrderFileFormatCb.getSelectedItem();
                if (off != null) {
                    EditOrderFileFormatDialog dialog = new EditOrderFileFormatDialog(DistributorsDialog.this, "Edit format", off);
                    if (dialog.showDialog() == IDialog.OK) {
                        off.save();
                    }
                }
            }
        });
        detailOrderFileFormatTb.setAlignmentX(RIGHT_ALIGNMENT);
        detailOrderFileFormatCb.updateList();
    }

    @Override
    protected JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));

        // Panels
        JPanel textFieldPanel = new JPanel();
        JPanel orderPanel = new JPanel(new GridBagLayout());

        // - Text fields
        GuiUtils.GridBagHelper gbh = new GuiUtils.GridBagHelper(textFieldPanel);
        gbh.addLine("Name: ", detailName);
        gbh.addLine("Web site: ", browseDistributorPanel);
        gbh.add(detailLogo, 1, 2, 1, 1);

        // - Order stuff
        gbh = new GuiUtils.GridBagHelper(orderPanel);
        gbh.addLine("Order link: ", browseOrderLinkPanel);
        gbh.gridwidth = 2;
        gbh.addLine("File format: ", detailOrderFileFormatCb, GridBagConstraints.BOTH);
        gbh.gridwidth = 1;
        gbh.add(detailOrderFileFormatTb, 1, 2);

        // Add all
        panel.add(textFieldPanel, BorderLayout.NORTH);
        panel.add(orderPanel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void updateEnabledComponents() {
        super.updateEnabledComponents();

        if (getObject() != null) {
            detailOrderFileFormatTb.setRefreshActionEnabled(true);
            detailOrderFileFormatTb.setAddActionEnabled(true);

            OrderFileFormat off = getObject().getOrderFileFormat();
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

    @Override
    protected void setDetails(Distributor distributor) {
        detailName.setText(distributor.getName());
        browseDistributorPanel.setText(distributor.getWebsite());
        detailLogo.setIcon(ImageResource.scaleImage(
                imageResource.readDistributorIcon(distributor.getIconPath()), new Dimension(48,48)));

        // Orders
        browseOrderLinkPanel.setText(distributor.getOrderLink());
        detailOrderFileFormatCb.setSelectedItem(distributor.getOrderFileFormat());
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
        detailLogo.setIcon(null);
        // List
        browseOrderLinkPanel.clearText();
        detailOrderFileFormatCb.setSelectedItem(null);
    }

}
