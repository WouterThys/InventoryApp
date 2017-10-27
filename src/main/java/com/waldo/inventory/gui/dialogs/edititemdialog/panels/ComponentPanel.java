package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.classes.DbObject.DbObjectNameComparator;
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
import static com.waldo.inventory.managers.SearchManager.sm;
import static com.waldo.inventory.gui.Application.imageResource;

public class ComponentPanel extends JPanel implements GuiInterface {

    private Application application;
    private Item newItem;

    // Listener
    private IEditedListener editedListener;

    // Tabbed pane
    private JTabbedPane tabbedPane;

    // Details
    private IComboBox<PackageType> packageTypeCb;
    private ISpinner packagePinsSp;
    private IFormattedTextField packageWidthTf, packageHeightTf;
    private IComboBox<Manufacturer> manufacturerCb;
    private ILabel iconLbl;
    private IStarRater starRater;
    private ICheckBox discourageOrderCb;
    private ITextArea remarksTa;
    private ICheckBox isSetCb;
    private JButton setValuesBtn;
    private IComboBox<DimensionType> dimensionCb;

    // Basic info
    private ITextField idTf;
    private ITextField nameTf;

    private ITextArea descriptionTa;
    private ITextField priceTf;
    private IComboBox<Category> categoryCb;
    private IComboBox<Product> productCb;
    private IComboBox<Type> typeCb;

    // Data sheet
    private ITextField localDataSheetTf;
    private JButton localDataSheetBtn;
    private ITextField onlineDataSheetTf;

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
            manufacturerCb.updateList();
            manufacturerCb.setSelectedItem(newItem.getManufacturer());
        }
    }

    private void updateCategoryCbValues() {
        categoryCb.updateList();
        categoryCb.setSelectedItem(newItem.getCategory());
    }

    public void updateProductCbValues(long categoryId) {
        productCb.updateList(sm().findProductListForCategory(categoryId));
        productCb.setSelectedItem(newItem.getProduct());
    }

    public void updateTypeCbValues(long productId) {
        typeCb.updateList(sm().findTypeListForProduct(productId));
        typeCb.setSelectedItem(newItem.getType());
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void createCategoryCb() {
        categoryCb = new IComboBox<>(db().getCategories(), new DbObjectNameComparator<>(), true);
        categoryCb.addEditedListener(editedListener, "categoryId");
        categoryCb.setSelectedItem(newItem.getCategory());
    }

    private void createProductCb() {
        java.util.List<Product> productList;
        if (newItem.getCategoryId() > DbObject.UNKNOWN_ID) {
            productList = sm().findProductListForCategory(newItem.getCategoryId());
        } else {
            productList = db().getProducts();
        }

        productCb = new IComboBox<>(productList, new DbObjectNameComparator<>(), true);
        productCb.addEditedListener(editedListener, "productId");
        productCb.setEnabled((newItem.getId() >= 0) && (newItem.getCategoryId() > DbObject.UNKNOWN_ID));
        productCb.setSelectedItem(newItem.getProduct());
    }

    private void createTypeCb() {
        java.util.List<Type> typeList;
        if (newItem.getCategoryId() > DbObject.UNKNOWN_ID) {
            typeList = sm().findTypeListForProduct(newItem.getProductId());
        } else {
            typeList = db().getTypes();
        }

        typeCb = new IComboBox<>(typeList, new DbObjectNameComparator<>(), true);
        typeCb.addEditedListener(editedListener, "typeId");
        typeCb.setEnabled((newItem.getId() >= 0) && (newItem.getProductId() > DbObject.UNKNOWN_ID));
        typeCb.setSelectedItem(newItem.getType());
    }

    private void createPackageTypeCb() {
        packageTypeCb = new IComboBox<>(db().getPackageTypes(), new DbObjectNameComparator<>(), true);
        packageTypeCb.addEditedListener(editedListener, "packageTypeId");
    }

    private void createDimensionTypeCb() {
//        java.util.List<DimensionType> dimensionTypes;
//        if (newItem.getPackageId() > DbObject.UNKNOWN_ID) {
//            dimensionTypes = sm().findDimensionTypesForPackageType(newItem.getPackage().getPackageTypeId());
//        } else {
//            dimensionTypes = db().getDimensionTypes();
//        }

        dimensionCb = new IComboBox<>(db().getDimensionTypes(), new DbObjectNameComparator<>(), true);
        dimensionCb.addEditedListener(editedListener, "dimensionTypeId", long.class);
        dimensionCb.setEnabled(false);
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
                updateProductCbValues(((Category) categoryCb.getSelectedItem()).getId());
                updateTypeCbValues(((Product) productCb.getSelectedItem()).getId());
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
        PackageType packageType = (PackageType) packageTypeCb.getSelectedItem();
        if (packageType != null) {
            java.util.List<DimensionType> dimensionTypeList = sm().findDimensionTypesForPackageType(packageType.getId());
            dimensionCb.updateList(dimensionTypeList);
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
        idTf = new ITextField();
        idTf.setEditable(false);
        idTf.setEnabled(false);

        nameTf = new ITextField();
        nameTf.addEditedListener(editedListener, "name");
        nameTf.setName(EditItemDialogLayout.COMP_NAME);
        descriptionTa = new ITextArea();
        descriptionTa.setLineWrap(true); // Go to next line when area is full
        descriptionTa.setWrapStyleWord(true); // Don't cut words in two
        descriptionTa.addEditedListener(editedListener, "description");
        descriptionTa.setName(EditItemDialogLayout.COMP_DESCRIPTION);

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(Double.MIN_VALUE);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true); // Commit on every key press
        priceTf = new ITextField();
        priceTf.addEditedListener(editedListener, "price", double.class);

        // Combo boxes
        createCategoryCb();
        createProductCb();
        createTypeCb();

        // Local data sheet
        localDataSheetTf = new ITextField();
        localDataSheetTf.addEditedListener(editedListener, "localDataSheet");
        localDataSheetBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        localDataSheetBtn.addActionListener(new AbstractAction() {
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
        onlineDataSheetTf = new ITextField();
        onlineDataSheetTf.addEditedListener(editedListener, "onlineDataSheet");
    }

    private void initializeDetailsComponents() {
        // Package
        createPackageTypeCb();
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        packagePinsSp = new ISpinner(spinnerModel);
        packagePinsSp.addEditedListener(editedListener, "pins");
        packageWidthTf = new IFormattedTextField(NumberFormat.getNumberInstance());
        packageWidthTf.addEditedListener(editedListener, "width", double.class);
        packageHeightTf = new IFormattedTextField(NumberFormat.getNumberInstance());
        packageHeightTf.addEditedListener(editedListener, "height", double.class);
        createDimensionTypeCb();

        // Manufacturer
        createManufacturerCb();
        manufacturerCb.addEditedListener(editedListener, "manufacturerId");
        manufacturerCb.setName(EditItemDialogLayout.COMP_MANUFACTURER);
        manufacturerCb.addItemListener(e -> {
            Manufacturer m = (Manufacturer) e.getItem();
            if (m != null) {
                if (!m.getIconPath().isEmpty()) {
                    Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgManufacturersPath(), m.getIconPath());
                    iconLbl.setIcon(path.toString(), 100, 100);
                }
            }
        });
        iconLbl = new ILabel("", ILabel.RIGHT);

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
        local.add(localDataSheetTf, constraints);
        constraints = createFieldConstraints(1,0);
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        local.add(localDataSheetBtn, constraints);

        basicPanel.add(new ITitledEditPanel(
                "Identification",
                new String[] {"Database ID: ", "Name: "},
                new JComponent[] {idTf, nameTf}
        ));

        basicPanel.add(new ITitledEditPanel(
                "Sub divisions",
                new String[] {"Category: ", "Product: ", "Type: "},
                new JComponent[] {
                        PanelUtils.createComboBoxWithButton(categoryCb, createDivisionListener()),
                        PanelUtils.createComboBoxWithButton(productCb, createDivisionListener()),
                        PanelUtils.createComboBoxWithButton(typeCb, createDivisionListener())}
        ));

        basicPanel.add(new ITitledEditPanel(
                "Data sheets",
                new String[] {"Local: ", "Online: "},
                new JComponent[] {local, onlineDataSheetTf}
        ));

        basicPanel.add(new ITitledEditPanel(
                "Info",
                new String[] {"Price: ", "Description: "},
                new JComponent[] {priceTf, new JScrollPane(descriptionTa)}
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

        packagePanel.setBorder(packageBorder);
        manufacturerPanel.setBorder(manufacturerBorder);
        remarksPanel.setBorder(remarksBorder);
        setPanel.setBorder(setBorder);

        // PACKAGE
        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(packagePanel);

        // - extra
        JPanel dimPanel = new JPanel();
        dimPanel.add(packageWidthTf);
        dimPanel.add(packageHeightTf);

        gbc.addLine("Type: ", PanelUtils.createComboBoxWithButton(packageTypeCb, createPackageTypeListener()));
        gbc.addLine("Pins: ", packagePinsSp);
        gbc.addLine("Dimensions: ", dimPanel);
        gbc.addLine("Type: ", PanelUtils.createComboBoxWithButton(dimensionCb, createPackageTypeListener()));

        // MANUFACTURER
        gbc = new PanelUtils.GridBagHelper(manufacturerPanel);
        gbc.addLine("Name: ", PanelUtils.createComboBoxWithButton(manufacturerCb, createManufacturerAddListener()));
        gbc.add(iconLbl, 2,0,1,1);

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
        // Add tabs
        tabbedPane.addTab("Basic", createBasicPanel());
        tabbedPane.addTab("Details", createDetailsPanel());

        add(tabbedPane);
    }

    @Override
    public void updateComponents(Object object) {
        idTf.setText(String.valueOf(newItem.getId()));
        nameTf.setText(newItem.getName().trim());
        descriptionTa.setText(newItem.getDescription().trim());
        priceTf.setText(String.valueOf(newItem.getPrice()));

        // Combo boxes
        categoryCb.setSelectedItem(newItem.getCategory());
        productCb.setSelectedItem(newItem.getProduct());
        typeCb.setSelectedItem(newItem.getType());

        // DATA SHEETS
        localDataSheetTf.setText(newItem.getLocalDataSheet());
        onlineDataSheetTf.setText(newItem.getOnlineDataSheet());

        // PACKAGE
        if (newItem.getPackage() != null) {
            PackageType p = sm().findPackageTypeById(newItem.getPackage().getPackageTypeId());
            if (p != null && !p.isUnknown()) {
                packageTypeCb.setSelectedItem(p);
            } else {
                packageTypeCb.setSelectedIndex(0);
            }
            updateDimensionPanel();
            packagePinsSp.setValue(newItem.getPackage().getPins());
            packageHeightTf.setText(String.valueOf(newItem.getPackage().getHeight()));
            packageWidthTf.setText(String.valueOf(newItem.getPackage().getWidth()));
        }

        // MANUFACTURER
        if (newItem.getManufacturerId() >= 0) {
            manufacturerCb.setSelectedItem(newItem.getManufacturer());

            // Set icon
            try {
                Manufacturer m = newItem.getManufacturer();
                if (m != null && !m.getIconPath().isEmpty()) {
                    Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgManufacturersPath(), m.getIconPath());
                    iconLbl.setIcon(path.toString(), 100, 100);
                } else {
                    iconLbl.setIcon(imageResource.readImage("Common.Unknown"));
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
        nameTf.requestFocus();
    }

    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void setCategoryChangedAction(ItemListener categoryChangedAction) {
        categoryCb.addItemListener(categoryChangedAction);
    }

    public void setProductChangedAction(ItemListener productChangedAction) {
        productCb.addItemListener(productChangedAction);
    }

    public String getNameFieldValue() {
        return nameTf.getText();
    }

    public void setNameFieldError(String error) {
        nameTf.setError(error);
    }

    public String getPriceFieldValue() {
        return priceTf.getText();
    }

    public void setPriceFieldError(String error) {
        priceTf.setError(error);
    }

    private void setLocalDataSheetFieldValue(String localDataSheetFieldValue) {
        localDataSheetTf.setText(localDataSheetFieldValue);
    }

    public IComboBox getProductCb() {
        return productCb;
    }

    public IComboBox getTypeCb() {
        return typeCb;
    }

    public void updateRating(float rating) {
        starRater.setRating(rating);
        starRater.setSelection(0);
    }

    public IComboBox<PackageType> getPackageTypeCb() {
        return packageTypeCb;
    }
}
