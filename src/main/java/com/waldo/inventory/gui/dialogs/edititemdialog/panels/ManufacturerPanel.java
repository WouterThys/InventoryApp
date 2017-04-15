package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.waldo.inventory.database.DbManager.db;

public class ManufacturerPanel extends JPanel implements GuiInterface {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentPanel.class);

    private Item newItem;

    private JComboBox<Manufacturer> manufacturerComboBox;
    private DefaultComboBoxModel<Manufacturer> manufacturerCbModel;
    private ILabel iconLabel;
    private ITextField manufacturerItemNr;

    public ManufacturerPanel(Item newItem) {
        this.newItem = newItem;
    }

    @Override
    public void initializeComponents() {
        manufacturerCbModel = new DefaultComboBoxModel<>();
        manufacturerComboBox = new JComboBox<>(manufacturerCbModel);
        manufacturerComboBox.addItemListener(e -> {
            Manufacturer m = (Manufacturer) e.getItem();
            if (m != null) {
                if (!m.getIconPath().isEmpty()) {
                    iconLabel.setIcon(m.getIconPath());
                }
            }
        });

        iconLabel = new ILabel();
        manufacturerItemNr = new ITextField("Mfr. No");
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        ILabel manufacturerLabel = new ILabel("Name: ", ILabel.RIGHT);
        ILabel manufacturerIdLabel = new ILabel("Mfr. No: ", ILabel.RIGHT);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Name
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(manufacturerLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(manufacturerComboBox, gbc);

        // Mfr. No
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(manufacturerIdLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(manufacturerItemNr, gbc);

        // Icon
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(iconLabel, gbc);

        // Add to panel
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(panel, BorderLayout.NORTH);
    }

    @Override
    public void updateComponents(Object object) {
        manufacturerCbModel.removeAllElements();
        for (Manufacturer m : db().getManufacturers()) {
            manufacturerCbModel.addElement(m);
        }


        if (newItem != null) {
            if (newItem.getManufacturerId() >= 0) {
                // Set index
                int ndx = db().findManufacturerIndex(newItem.getManufacturerId());
                manufacturerComboBox.setSelectedIndex(ndx);

                // Set icon
                URL url = ManufacturerPanel.class.getResource("/settings/Settings.properties");
                ResourceManager resourceManager = new ResourceManager(url.getPath());
                try {
                    Manufacturer m = db().findManufacturerById(newItem.getManufacturerId());
                    if (m != null && !m.getIconPath().isEmpty()) {
                        url = new File(m.getIconPath()).toURI().toURL();
                        iconLabel.setIcon(resourceManager.readImage(url, 48, 48));
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    public Manufacturer getSelectedManufacturer() {
        return (Manufacturer) manufacturerComboBox.getSelectedItem();
    }

    public long getSelectedManufacturerId() {
        Manufacturer m = getSelectedManufacturer();
        if (m != null) {
            return m.getId();
        } else {
            return -1;
        }
    }
}
