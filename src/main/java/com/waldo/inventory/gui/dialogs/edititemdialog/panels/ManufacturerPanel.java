package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITitledEditPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import static com.waldo.inventory.database.DbManager.dbInstance;

public class ManufacturerPanel extends JPanel implements GuiInterface {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentPanel.class);

    private Item newItem;

    private JComboBox<Manufacturer> manufacturerComboBox;
    private DefaultComboBoxModel<Manufacturer> manufacturerCbModel;
    private ILabel iconLabel;

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
        iconLabel.setPreferredSize(new Dimension(48,48));
    }

    @Override
    public void initializeLayouts() {
//        add(new ITitledEditPanel("Manufacturer",
//                new String[] {"Manufcaturer: ", ""},
//                new JComponent[] {manufacturerComboBox, iconLabel}));
        add(manufacturerComboBox);
        add(iconLabel);
    }

    @Override
    public void updateComponents(Object object) {
        manufacturerCbModel.removeAllElements();
        try {
            for (Manufacturer m : dbInstance().getManufacturers()) {
                manufacturerCbModel.addElement(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (newItem != null) {
            if (newItem.getManufacturerId() >= 0) {
                // Set index
                try {
                    int ndx = dbInstance().findManufacturerIndex(newItem.getManufacturerId());
                    manufacturerComboBox.setSelectedIndex(ndx);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Set icon
                URL url = ManufacturerPanel.class.getResource("/settings/Settings.properties");
                ResourceManager resourceManager = new ResourceManager(url.getPath());
                try {
                    Manufacturer m = dbInstance().findManufacturerById(newItem.getManufacturerId());
                    if (m != null && !m.getIconPath().isEmpty()) {
                        url = new File(m.getIconPath()).toURI().toURL();
                        iconLabel.setIcon(resourceManager.readImage(url, 48, 48));
                    }
                } catch (SQLException | IOException e1) {
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
