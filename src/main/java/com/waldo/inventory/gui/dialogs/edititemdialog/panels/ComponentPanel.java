package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialogLayout;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;
import com.waldo.inventory.gui.dialogs.packagedialog.PackageTypeDialog;
import com.waldo.inventory.gui.dialogs.setitemdialog.SetItemDialog;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;

import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;
import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.database.SearchManager.sm;
import static com.waldo.inventory.gui.Application.imageResource;

public class ComponentPanel extends JPanel implements GuiInterface {

    private Application application;
    private Item newItem;

    // Listener
    private IEditedListener editedListener;

    // Tabbed pane
    private JTabbedPane tabbedPane;

    // Details
    private IComboBox<PackageType> packageTypeComboBox;
    private ISpinner packagePinsSp;
    private IFormattedTextField packageWidthTf, packageHeightTf;
    private DefaultComboBoxModel<Manufacturer> manufacturerModel;
    private IComboBox<Manufacturer> manufacturerComboBox;
    private ILabel iconLabel;
    private IStarRater starRater;
    private ICheckBox discourageOrderCb;
    private ITextArea remarksTa;
    private ICheckBox isSetCb;
    private JButton setValuesBtn;
    private DefaultComboBoxModel<DimensionType> dimensionCbModel;
    private IComboBox<DimensionType> dimensionCb;

    // Basic info
    private ITextField idTextField;
    private ITextField nameTextField;
    private ITextArea descriptionTextArea;
    private ITextField priceTextField;
    private DefaultComboBoxModel<Category> categoryCbModel;
    private DefaultComboBoxModel<Product> productCbModel;
    private DefaultComboBoxModel<Type> typeCbModel;
    private IComboBox<Category> categoryComboBox;
    private IComboBox<Product> productComboBox;
    private IComboBox<Type> typeComboBox;

    // Data sheet
    private ITextField localDataSheetTextField;
    private JButton localDataSheetButton;
    private ITextField onlineDataSheetTextField;

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
        manufacturerModel.removeAllElements();
        for (Manufacturer m : db().getManufacturers()) {
            manufacturerModel.addElement(m);
        }
        if (manufacturerComboBox != null) {
            manufacturerComboBox.setSelectedItem(newItem.getManufacturer());
        }
    }

    private void updateCategoryCbValues() {
        categoryCbModel.removeAllElements();
        categoryCbModel.addElement(Category.getUnknownCategory());
        for (Category c : db().getCategories()) {
            categoryCbModel.addElement(c);
        }

        categoryComboBox.setSelectedItem(newItem.getCategory());
    }

    public void updateProductCbValues(long categoryId) {
        productCbModel.removeAllElements();
        productCbModel.addElement(Product.getUnknownProduct()); // Add unknown
        for (Product p : db().getProductListForCategory(categoryId)) {
            productCbModel.addElement(p);
        }

        productComboBox.setSelectedItem(newItem.getProduct());
    }

    public void updateTypeCbValues(long productId) {
        typeCbModel.removeAllElements();
        typeCbModel.addElement(Type.getUnknownType()); // Add unknown
        for (Type t : db().getTypeListForProduct(productId)) {
            typeCbModel.addElement(t);
        }

        typeComboBox.setSelectedItem(newItem.getType());
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void createCategoryCb() {
        categoryCbModel = new DefaultComboBoxModel<>();
        for (Category c : db().getCategories()) {
            categoryCbModel.addElement(c);
        }
        categoryComboBox = new IComboBox<>(categoryCbModel);
        categoryComboBox.addEditedListener(editedListener, "categoryId");
        categoryComboBox.setSelectedItem(newItem.getCategory());
    }

    private void createProductCb() {
        productCbModel = new DefaultComboBoxModel<>();
        for (Product p: db().getProducts()) {
            productCbModel.addElement(p);
        }
        productComboBox = new IComboBox<>(productCbModel);
        productComboBox.addEditedListener(editedListener, "productId");
        productComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getCategoryId() > DbObject.UNKNOWN_ID));
        productComboBox.setSelectedItem(newItem.getProduct());
    }

    private void createTypeCb() {
        typeCbModel = new DefaultComboBoxModel<>();
        for (Type t: db().getTypes()) {
            typeCbModel.addElement(t);
        }
        typeComboBox = new IComboBox<>(typeCbModel);
        typeComboBox.addEditedListener(editedListener, "typeId");
        typeComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getProductId() > DbObject.UNKNOWN_ID));
        typeComboBox.setSelectedItem(newItem.getType());
    }

    private void createProductTypeId() {
        DefaultComboBoxModel<PackageType> packageTypeCbModel = new DefaultComboBoxModel<>();
        packageTypeCbModel.addElement((PackageType.createDummyPackageType()));
        for (PackageType pt : db().getPackageTypes()) {
            packageTypeCbModel.addElement(pt);
        }
        packageTypeComboBox = new IComboBox<>(packageTypeCbModel);
        packageTypeComboBox.addEditedListener(editedListener, "packageTypeId");
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

    private ActionListener createPackageTypeListener() {
        return e -> {
            PackageTypeDialog packageTypeDialog = new PackageTypeDialog(application, "Packages");
            if (packageTypeDialog.showDialog() == IDialog.OK) {
                updateDimensionPanel();
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
    
    public void updateDimensionPanel() {
        PackageType packageType = (PackageType) packageTypeComboBox.getSelectedItem();
        dimensionCbModel.removeAllElements();
        dimensionCbModel.addElement(DimensionType.createDummyDimensionType());
        if (packageType != null) {
            java.util.List<DimensionType> dimensionTypeList = sm().findDimensionTypesForPackageType(packageType.getId());
            for (DimensionType dt : dimensionTypeList) {
                dimensionCbModel.addElement(dt);
            }
            dimensionCb.setEnabled(dimensionTypeList.size() > 0);
        } else {
            dimensionCb.setEnabled(false);
        }

        DimensionType d = sm().findDimensionTypeById(newItem.getDimensionTypeId());
        if (d != null && !d.isUnknown()) {
            dimensionCb.setSelectedItem(d);
        }
    }

    private void initializeBasicComponents() {
        // Identification
        idTextField = new ITextField();
        idTextField.setEditable(false);
        idTextField.setEnabled(false);

        nameTextField = new ITextField();
        nameTextField.addEditedListener(editedListener, "name");
        nameTextField.setName(EditItemDialogLayout.COMP_NAME);
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
        onlineDataSheetTextField = new ITextField();
        onlineDataSheetTextField.addEditedListener(editedListener, "onlineDataSheet");
    }

    private void initializeDetailsComponents() {
        // Package
        createProductTypeId();
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        packagePinsSp = new ISpinner(spinnerModel);
        packagePinsSp.addEditedListener(editedListener, "pins");
        packageWidthTf = new IFormattedTextField(NumberFormat.getNumberInstance());
        packageWidthTf.addEditedListener(editedListener, "width", double.class);
        packageHeightTf = new IFormattedTextField(NumberFormat.getNumberInstance());
        packageHeightTf.addEditedListener(editedListener, "height", double.class);
        dimensionCbModel = new DefaultComboBoxModel<>();
        dimensionCb = new IComboBox<>(dimensionCbModel);
        dimensionCb.addEditedListener(editedListener, "dimensionTypeId", long.class);
        dimensionCb.setEnabled(false);

        // Manufacturer
        manufacturerModel = new DefaultComboBoxModel<>();
        updateManufacturerCbValues();
        manufacturerComboBox = new IComboBox<>(manufacturerModel);
        manufacturerComboBox.addEditedListener(editedListener, "manufacturerId");
        manufacturerComboBox.setName(EditItemDialogLayout.COMP_MANUFACTURER);
        manufacturerComboBox.addItemListener(e -> {
            Manufacturer m = (Manufacturer) e.getItem();
            if (m != null) {
                if (!m.getIconPath().isEmpty()) {
                    Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgManufacturersPath(), m.getIconPath());
                    iconLabel.setIcon(path.toString(), 100, 100);
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
                dialog.showDialog();
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
                new JComponent[] {idTextField, nameTextField}
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
        JPanel packagePanel = new JPanel(new GridBagLayout());
        JPanel manufacturerPanel = new JPanel(new GridBagLayout());
        JPanel remarksPanel = new JPanel(new GridBagLayout());
        JPanel setPanel = new JPanel(new BorderLayout());

        // Borders
        TitledBorder packageBorder = PanelUtils.createTitleBorder("Package");
        TitledBorder manufacturerBorder = PanelUtils.createTitleBorder("Manufacturer");
        TitledBorder remarksBorder = PanelUtils.createTitleBorder("Remarks");
        TitledBorder setBorder = PanelUtils.createTitleBorder("Set");

        // Labels
        ILabel typeLabel = new ILabel("Type: ");
        typeLabel.setHorizontalAlignment(ILabel.RIGHT);
        typeLabel.setVerticalAlignment(ILabel.CENTER);
        ILabel pinsLabel = new ILabel("Pins: ");
        pinsLabel.setHorizontalAlignment(ILabel.RIGHT);
        pinsLabel.setVerticalAlignment(ILabel.CENTER);
        ILabel dimLabel = new ILabel("Dimensions: ");
        dimLabel.setHorizontalAlignment(ILabel.RIGHT);
        dimLabel.setVerticalAlignment(ILabel.CENTER);
        ILabel dimTypeLabel = new ILabel("Type: ");
        dimTypeLabel.setHorizontalAlignment(ILabel.RIGHT);
        dimTypeLabel.setVerticalAlignment(ILabel.CENTER);

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // - extra
        JPanel dimPanel = new JPanel();
        dimPanel.add(packageWidthTf);
        dimPanel.add(packageHeightTf);

        // - type
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        packagePanel.add(typeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 3;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        packagePanel.add(PanelUtils.createComboBoxWithButton(packageTypeComboBox, createPackageTypeListener()), gbc);

        // - pins
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        packagePanel.add(pinsLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 3;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        packagePanel.add(packagePinsSp, gbc);

        // - dimensions
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        packagePanel.add(dimLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 3;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        packagePanel.add(dimPanel, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        packagePanel.add(dimTypeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 3;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        packagePanel.add(PanelUtils.createComboBoxWithButton(dimensionCb, createPackageTypeListener()), gbc);

        // - border
        packagePanel.setBorder(packageBorder);


        // MANUFACTURER
        ILabel manufacturerLabel = new ILabel("Name: ", ILabel.RIGHT);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Name
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        manufacturerPanel.add(manufacturerLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        manufacturerPanel.add(PanelUtils.createComboBoxWithButton(manufacturerComboBox, createManufacturerAddListener()), gbc);

        // Icon
        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        manufacturerPanel.add(iconLabel, gbc);

        // Border
        manufacturerPanel.setBorder(manufacturerBorder);


        // REMARKS
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

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

        // Border
        remarksPanel.setBorder(remarksBorder);

        // SET
        setPanel.add(isSetCb, BorderLayout.CENTER);
        setPanel.add(setValuesBtn, BorderLayout.EAST);
        setPanel.setBorder(setBorder);

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
        // Add tabs
        tabbedPane.addTab("Basic", createBasicPanel());
        tabbedPane.addTab("Details", createDetailsPanel());

        add(tabbedPane);
    }

    @Override
    public void updateComponents(Object object) {
        idTextField.setText(String.valueOf(newItem.getId()));
        nameTextField.setText(newItem.getName().trim());
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
        if (newItem.getPackage() != null) {
            PackageType p = sm().findPackageTypeById(newItem.getPackage().getPackageTypeId());
            if (p != null && !p.isUnknown()) {
                packageTypeComboBox.setSelectedItem(p);
            } else {
                packageTypeComboBox.setSelectedIndex(0);
            }
            packagePinsSp.setValue(newItem.getPackage().getPins());
            packageHeightTf.setText(String.valueOf(newItem.getPackage().getHeight()));
            packageWidthTf.setText(String.valueOf(newItem.getPackage().getWidth()));
        }

        // MANUFACTURER
        if (newItem.getManufacturerId() >= 0) {
            manufacturerComboBox.setSelectedItem(newItem.getManufacturer());

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
            if (manufacturerModel.getSize() > 0) {
                manufacturerComboBox.setSelectedIndex(0);
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
        return nameTextField.getText();
    }

    public void setNameFieldError(String error) {
        nameTextField.setError(error);
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

    public JComboBox getProductComboBox() {
        return productComboBox;
    }

    public JComboBox getTypeComboBox() {
        return typeComboBox;
    }

    public void updateRating(float rating) {
        starRater.setRating(rating);
        starRater.setSelection(0);
    }

    public IComboBox<PackageType> getPackageTypeCb() {
        return packageTypeComboBox;
    }
}
