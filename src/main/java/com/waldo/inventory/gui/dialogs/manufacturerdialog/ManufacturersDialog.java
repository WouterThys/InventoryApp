package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.gui.components.IImagePanel;
import com.waldo.inventory.gui.components.IResourceDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class ManufacturersDialog extends IResourceDialog<Manufacturer> {

    private static final ImageIcon icon = imageResource.readIcon("Factory.L");

    private ITextField detailName;
    private GuiUtils.IBrowseWebPanel browsePanel;
    private IImagePanel detailLogo;
    private JList<Item> detailItemList;
    private DefaultListModel<Item> detailItemDefaultListModel;

    public ManufacturersDialog(Window parent) {
        super(parent, "Manufacturers", Manufacturer.class);
    }

    @Override
    protected List<Manufacturer> getAllResources() {
        return cache().getManufacturers();
    }

    @Override
    protected Manufacturer getNewResource() {
        return new Manufacturer();
    }

    @Override
    protected void updateEnabledComponents() {
        super.updateEnabledComponents();
        boolean enabled = getObject() != null && !getObject().getWebsite().isEmpty();
        browsePanel.setActionEnabled(enabled);
    }

    @Override
    protected void initializeDetailComponents() {
        setTitleIcon(icon);
        // Details
        detailName = new ITextField("Name");
        detailName.setEnabled(false);
        detailLogo = new IImagePanel(this, Statics.ImageType.ManufacturerImage, null, new Dimension(128, 128), this);
        browsePanel = new GuiUtils.IBrowseWebPanel("Web site", "website", this);

        detailItemDefaultListModel = new DefaultListModel<>();
        detailItemList = new JList<>(detailItemDefaultListModel);
    }

    @Override
    protected JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel infoPnl = new JPanel();
        com.waldo.utils.GuiUtils.GridBagHelper gbc = new com.waldo.utils.GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("Name: ", detailName);
        gbc.addLine("Web site: ", browsePanel);

        JPanel iconPnl = new JPanel(new BorderLayout());
        iconPnl.add(detailLogo, BorderLayout.EAST);

        JPanel itemsPnl = new JPanel(new BorderLayout());
        itemsPnl.add(new ILabel("Items: "));
        itemsPnl.add(new JScrollPane(detailItemList));
        itemsPnl.setBorder(GuiUtils.createInlineTitleBorder("Known items"));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(infoPnl, BorderLayout.NORTH);
        northPanel.add(iconPnl, BorderLayout.CENTER);
        northPanel.setBorder(GuiUtils.createInlineTitleBorder("Details"));

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(itemsPnl, BorderLayout.CENTER);

        return panel;
    }

    @Override
    protected void setDetails(Manufacturer manufacturer) {
        if (manufacturer != null) {
            detailName.setText(manufacturer.getName());
            browsePanel.setText(manufacturer.getWebsite());
            detailItemDefaultListModel.removeAllElements();
            for (Item item : SearchManager.sm().getItemsForManufacturer(manufacturer.getId())) {
                detailItemDefaultListModel.addElement(item);
            }
        } else {
            clearDetails();
        }
        detailLogo.updateComponents(manufacturer);
    }

    @Override
    protected void clearDetails() {
        detailName.setText("");
        browsePanel.clearText();
        detailLogo.updateComponents((DbObject)null);
        detailItemDefaultListModel.removeAllElements();
    }

    @Override
    public VerifyState verify(Manufacturer m) {
        VerifyState ok = VerifyState.Ok;
        if (detailName.getText().isEmpty()) {
            detailName.setError("Name can't be empty");
            ok = VerifyState.Error;
        } else {
            if (m.getId() < DbObject.UNKNOWN_ID) {
                if (SearchManager.sm().findManufacturerByName(detailName.getText()) != null) {
                    detailName.setError("Name already exists..");
                    ok = VerifyState.Error;
                }
            }
        }

        return ok;
    }
}