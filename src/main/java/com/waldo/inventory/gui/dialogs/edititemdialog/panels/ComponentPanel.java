package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.classes.DbObject.DbObjectNameComparator;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialogLayout;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;
import com.waldo.inventory.gui.dialogs.setitemdialog.SetItemDialog;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;

import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;
import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.SearchManager.sm;

public class ComponentPanel extends JPanel implements GuiInterface {

    private Application application;
    private Item newItem;

    // Listener
    private IEditedListener editedListener;

    // Tabbed pane
    private JTabbedPane tabbedPane;

    // Details
    private PanelUtils.IPackagePanel packagePnl;
    private IComboBox<Manufacturer> manufacturerCb;
    private ILabel iconLabel;
    private IStarRater starRater;
    private ICheckBox discourageOrderCb;
    private ITextArea remarksTa;
    private ICheckBox isSetCb;
    private JButton setValuesBtn;

    // Basic info
    private ITextField idTextField;
    private PanelUtils.INameValuePanel nameValuePnl;
    private ITextArea descriptionTextArea;
    private ITextField priceTextField;
    private IComboBox<Category> categoryComboBox;
    private IComboBox<Product> productComboBox;
    private IComboBox<Type> typeComboBox;

    // Data sheet
    private ITextField localDataSheetTextField;
    private JButton localDataSheetButton;
    private PanelUtils.IBrowseWebPanel onlineDataSheetTextField;

    public ComponentPanel(Application application, Item newItem, @NotNull IEditedListener listener) {
        this.application = application;
        this.newItem = newItem;
        this.editedListener = listener;
    }

    /*
     *                  PUBLIC METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void setSelectedTab(int index) {
        if (tabbedPane != null) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    private void updateManufacturerCbValues() {
        if (manufacturerCb != null) {
            manufacturerCb.updateList(DbManager.db().getManufacturers());
            manufacturerCb.setSelectedItem(newItem.getManufacturer());
        }
    }

    private void updateCategoryCbValues() {
        categoryComboBox.updateList();
        categoryComboBox.setSelectedItem(newItem.getCategory());
    }

    public void updateProductCbValues(long categoryId) {
        productComboBox.updateList(sm().findProductListForCategory(categoryId));
        productComboBox.setSelectedItem(newItem.getProduct());
    }

    public void updateTypeCbValues(long productId) {
        typeComboBox.updateList(sm().findTypeListForProduct(productId));
        typeComboBox.setSelectedItem(newItem.getType());
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void createCategoryCb() {
        categoryComboBox = new IComboBox<>(db().getCategories(), new DbObjectNameComparator<>(), true);
        categoryComboBox.addEditedListener(editedListener, "categoryId");
        categoryComboBox.setSelectedItem(newItem.getCategory());
    }

    private void createProductCb() {
        java.util.List<Product> productList;
        if (newItem.getCategoryId() > DbObject.UNKNOWN_ID) {
            productList = sm().findProductListForCategory(newItem.getCategoryId());
        } else {
            productList = db().getProducts();
        }

        productComboBox = new IComboBox<>(productList, new DbObjectNameComparator<>(), true);
        productComboBox.addEditedListener(editedListener, "productId");
        productComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getCategoryId() > DbObject.UNKNOWN_ID));
        productComboBox.setSelectedItem(newItem.getProduct());
    }

    private void createTypeCb() {
        java.util.List<Type> typeList;
        if (newItem.getCategoryId() > DbObject.UNKNOWN_ID) {
            typeList = sm().findTypeListForProduct(newItem.getProductId());
        } else {
            typeList = db().getTypes();
        }

        typeComboBox = new IComboBox<>(typeList, new DbObjectNameComparator<>(), true);
        typeComboBox.addEditedListener(editedListener, "typeId");
        typeComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getProductId() > DbObject.UNKNOWN_ID));
        typeComboBox.setSelectedItem(newItem.getType());
    }

    private void createManufacturerCb() {
        manufacturerCb = new IComboBox<>(db().getManufacturers(), new DbObjectNameComparator<>(), true);
        manufacturerCb.setSelectedItem(newItem.getManufacturer());
        manufacturerCb.addEditedListener(editedListener, "manufacturerId");
    }

    private ActionListener createDivisionListener() {
        return e -> {
            SubDivisionsDialog subDivisionsDialog = new SubDivisionsDialog(application, "Sub divisions");
            if (subDivisionsDialog.showDialog() == IDialog.OK) {
                updateCategoryCbValues();
                updateProductCbValues(((Category)categoryComboBox.getSelectedItem()).getId());
                updateTypeCbValues(((Product)productComboBox.getSelectedItem()).getId());
            }
        };
    }

    private ActionListener createManufacturerAddListener() {
        return e -> {
            ManufacturersDialog manufacturersDialog = new ManufacturersDialog(application, "Manufacturers");
            if (manufacturersDialog.showDialog() == IDialog.OK) {
                updateManufacturerCbValues();
            }
        };
    }

    private void initializeBasicComponents() {
        // Identification
        idTextField = new ITextField();
        idTextField.setEditable(false);
        idTextField.setEnabled(false);

        nameValuePnl = new PanelUtils.INameValuePanel(editedListener, "name", editedListener);

        descriptionTextArea = new ITextArea();
        descriptionTextArea.setLineWrap(true); // Go to next line when area is full
        descriptionTextArea.setWrapStyleWord(true); // Don't cut words in two
        descriptionTextArea.addEditedListener(editedListener, "description");
        descriptionTextArea.setName(EditItemDialogLayout.COMP_DESCRIPTION);

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(Double.MIN_VALUE);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true); // Commit on every key press
        priceTextField = new ITextField();
        priceTextField.addEditedListener(editedListener, "price", double.class);

        // Combo boxes
        createCategoryCb();
        createProductCb();
        createTypeCb();

        // Local data sheet
        localDataSheetTextField = new ITextField();
        localDataSheetTextField.addEditedListener(editedListener, "localDataSheet");
        localDataSheetButton = new JButton(imageResource.readImage("Common.BrowseIcon"));
        localDataSheetButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
                fileChooser.setDialogTitle("Select the data sheet");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (fileChooser.showOpenDialog(ComponentPanel.this) == JFileChooser.APPROVE_OPTION) {
                    setLocalDataSheetFieldValue(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        // Online data sheet
        onlineDataSheetTextField = new PanelUtils.IBrowseWebPanel("","onlineDataSheet", editedListener);
        //onlineDataSheetTextField.addEditedListener(editedListener, "onlineDataSheet");
    }

    private void initializeDetailsComponents() {
        // Package
        packagePnl = new PanelUtils.IPackagePanel(application, editedListener, "packageTypeId", "pins");

        // Manufacturer
        createManufacturerCb();
        manufacturerCb.setName(EditItemDialogLayout.COMP_MANUFACTURER);
        manufacturerCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Manufacturer m = (Manufacturer) manufacturerCb.getSelectedItem();
                if (m != null) {
                    if (!m.getIconPath().isEmpty()) {
                        Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgManufacturersPath(), m.getIconPath());
                        iconLabel.setIcon(path.toString(), 100, 100);
                    }
                }
            }
        });
        iconLabel = new ILabel("", ILabel.RIGHT);

        // Remarks stuff
        starRater = new IStarRater(5, 0,0);
        starRater.addEditedListener(editedListener, "rating");
        starRater.setName(EditItemDialogLayout.COMP_RATING);
        discourageOrderCb = new ICheckBox("Discourage future orders");
        discourageOrderCb.addEditedListener(editedListener, "discourageOrder");
        discourageOrderCb.setAlignmentX(RIGHT_ALIGNMENT);
        discourageOrderCb.setName(EditItemDialogLayout.COMP_DISCOURAGE);
        remarksTa = new ITextArea();
        remarksTa.setName(EditItemDialogLayout.COMP_REMARK);
        remarksTa.setLineWrap(true); // Go to next line when area is full
        remarksTa.setWrapStyleWord(true); // Don't cut words in two
        remarksTa.addEditedListener(editedListener, "remarks");

        // Set stuff
        isSetCb = new ICheckBox("Is set", false);
        isSetCb.addEditedListener(editedListener, "set");
        isSetCb.addActionListener(e -> setValuesBtn.setEnabled(isSetCb.isSelected()));
        setValuesBtn = new JButton("Set values");
        setValuesBtn.addActionListener(e -> {
            if (newItem != null && newItem.getId() > DbObject.UNKNOWN_ID) {
                SetItemDialog dialog = new SetItemDialog(application, "Set items", newItem);
                if (dialog.showDialog() == IDialog.OK) {
                    editedListener.onValueChanged(null, "", 0, 0);
                }
            } else {
                JOptionPane.showMessageDialog(ComponentPanel.this,
                        "Save item first!",
                        "Error creating set items",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    private JPanel createBasicPanel() {
        JPanel basicPanel = new JPanel();
        basicPanel.setLayout(new BoxLayout(basicPanel, BoxLayout.Y_AXIS));
        // Additional stuff
        JPanel local = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = createFieldConstraints(0,0);
        constraints.gridwidth = 1;
        local.add(localDataSheetTextField, constraints);
        constraints = createFieldConstraints(1,0);
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        local.add(localDataSheetButton, constraints);

        basicPanel.add(new ITitledEditPanel(
                "Identification",
                new String[] {"Database ID: ", "Name: "},
                new JComponent[] {idTextField, nameValuePnl}
        ));

        basicPanel.add(new ITitledEditPanel(
                "Sub divisions",
                new String[] {"Category: ", "Product: ", "Type: "},
                new JComponent[] {
                        PanelUtils.createComboBoxWithButton(categoryComboBox, createDivisionListener()),
                        PanelUtils.createComboBoxWithButton(productComboBox, createDivisionListener()),
                        PanelUtils.createComboBoxWithButton(typeComboBox, createDivisionListener())}
        ));

        basicPanel.add(new ITitledEditPanel(
                "Data sheets",
                new String[] {"Local: ", "Online: "},
                new JComponent[] {local, onlineDataSheetTextField}
        ));

        basicPanel.add(new ITitledEditPanel(
                "Info",
                new String[] {"Price: ", "Description: "},
                new JComponent[] {priceTextField, new JScrollPane(descriptionTextArea)}
        ));
        return basicPanel;
    }

    private JPanel createDetailsPanel() {
        JPanel packagePanel = new JPanel(new BorderLayout());
        JPanel manufacturerPanel = new JPanel(new GridBagLayout());
        JPanel remarksPanel = new JPanel(new GridBagLayout());
        JPanel setPanel = new JPanel(new BorderLayout());

        // Borders
        TitledBorder packageBorder = PanelUtils.createTitleBorder("Package");
        TitledBorder manufacturerBorder = PanelUtils.createTitleBorder("Manufacturer");
        TitledBorder remarksBorder = PanelUtils.createTitleBorder("Remarks");
        TitledBorder setBorder = PanelUtils.createTitleBorder("Set");

        packagePanel.setBorder(packageBorder);
        manufacturerPanel.setBorder(manufacturerBorder);
        remarksPanel.setBorder(remarksBorder);
        setPanel.setBorder(setBorder);

        // PACKAGE
        packagePanel.add(packagePnl, BorderLayout.CENTER);

        PanelUtils.GridBagHelper gbc;

        // MANUFACTURER
        gbc = new PanelUtils.GridBagHelper(manufacturerPanel);
        gbc.addLine("Name: ", PanelUtils.createComboBoxWithButton(manufacturerCb, createManufacturerAddListener()));
        gbc.add(iconLabel, 2,0,1,1);

        // REMARKS
        gbc = new PanelUtils.GridBagHelper(remarksPanel);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        remarksPanel.add(starRater, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        remarksPanel.add(discourageOrderCb, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        remarksPanel.add(new ILabel("Remarks: "), gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        remarksPanel.add(new JScrollPane(remarksTa), gbc);

        // SET
        setPanel.add(isSetCb, BorderLayout.CENTER);
        setPanel.add(setValuesBtn, BorderLayout.EAST);


        // Add to panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        panel.add(packagePanel);
        panel.add(manufacturerPanel);
        panel.add(remarksPanel);
        panel.add(setPanel);

        return panel;
    }


    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        initializeBasicComponents();
        initializeDetailsComponents();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        // Add tabs
        tabbedPane.addTab("Basic", createBasicPanel());
        tabbedPane.addTab("Details", createDetailsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... object) {
        idTextField.setText(String.valueOf(newItem.getId()));
        nameValuePnl.setNameTxt(newItem.getName().trim());
        nameValuePnl.setValue(newItem.getValue());
        descriptionTextArea.setText(newItem.getDescription().trim());
        priceTextField.setText(String.valueOf(newItem.getPrice()));

        // Combo boxes
        categoryComboBox.setSelectedItem(newItem.getCategory());
        productComboBox.setSelectedItem(newItem.getProduct());
        typeComboBox.setSelectedItem(newItem.getType());

        // DATA SHEETS
        localDataSheetTextField.setText(newItem.getLocalDataSheet());
        onlineDataSheetTextField.setText(newItem.getOnlineDataSheet());

        // PACKAGE
        packagePnl.setPackageType(newItem.getPackageType(), newItem.getPins());

        // MANUFACTURER
        if (newItem.getManufacturerId() >= 0) {
            manufacturerCb.setSelectedItem(newItem.getManufacturer());

            // Set icon
            try {
                Manufacturer m = newItem.getManufacturer();
                if (m != null && !m.getIconPath().isEmpty()) {
                    Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgManufacturersPath(), m.getIconPath());
                    iconLabel.setIcon(path.toString(), 100, 100);
                } else {
                    iconLabel.setIcon(imageResource.readImage("Common.Unknown"));
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            if (manufacturerCb.getModel().getSize() > 0) {
                manufacturerCb.setSelectedIndex(0);
            }
        }

        // REMARKS
        starRater.setRating(newItem.getRating());
        starRater.setSelection(0);
        discourageOrderCb.setSelected(newItem.isDiscourageOrder());
        remarksTa.setText(newItem.getRemarks());

        // SETS
        isSetCb.setSelected(newItem.isSet());
        setValuesBtn.setEnabled(newItem.isSet());

        // Focus
        //nameTextField.requestFocus();
    }

    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void setCategoryChangedAction(ItemListener categoryChangedAction) {
        categoryComboBox.addItemListener(categoryChangedAction);
    }

    public void setProductChangedAction(ItemListener productChangedAction) {
        productComboBox.addItemListener(productChangedAction);
    }

    public String getNameFieldValue() {
        return nameValuePnl.getNameText();
    }

    public void setNameFieldError(String error) {
        nameValuePnl.setError(error);
    }

    public String getPriceFieldValue() {
        return priceTextField.getText();
    }

    public void setPriceFieldError(String error) {
        priceTextField.setError(error);
    }

    private void setLocalDataSheetFieldValue(String localDataSheetFieldValue) {
        localDataSheetTextField.setText(localDataSheetFieldValue);
    }

    public IComboBox getProductComboBox() {
        return productComboBox;
    }

    public IComboBox getTypeComboBox() {
        return typeComboBox;
    }

    public void updateRating(float rating) {
        starRater.setRating(rating);
        starRater.setSelection(0);
    }
}
