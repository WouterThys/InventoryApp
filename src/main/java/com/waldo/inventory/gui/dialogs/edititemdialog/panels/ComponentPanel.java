package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.waldo.inventory.Utils.ImageUtils;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.classes.Package;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Vector;

import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;
import static com.waldo.inventory.classes.DbObject.UNKNOWN_ID;
import static com.waldo.inventory.database.DbManager.db;

public class ComponentPanel extends JPanel implements GuiInterface {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentPanel.class);

    private Item newItem;

    // Tabbed pane
    private JTabbedPane tabbedPane;

    // Package info
    private JComboBox<PackageType> packageTypeComboBox;
    private JSpinner packagePinsSp;
    private JSpinner packageWidthSp, packageHeightSp;

    // Basic info
    private JTextField idTextField;
    private ITextField nameTextField;
    private JTextArea descriptionTextArea;
    private ITextField priceTextField;
    private JComboBox<Category> categoryComboBox;
    private JComboBox<Product> productComboBox;
    private DefaultComboBoxModel<Product> productCbModel;
    private DefaultComboBoxModel<Type> typeCbModel;
    private JComboBox<Type> typeComboBox;

    // Data sheet
    private JTextField localDataSheetTextField;
    private JButton localDataSheetButton;
    private JTextField onlineDataSheetTextField;

    public ComponentPanel(Item newItem) {
        this.newItem = newItem;
    }

    /*
     *                  PUBLIC METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public long getCbCategoryId()  {
        Category c = (Category) categoryComboBox.getSelectedItem();
        if (c != null) {
            return c.getId();
        }
        return UNKNOWN_ID;
    }

    public long getCbProductId() {
        if (productComboBox.isEnabled()) {
            Product p = (Product) productComboBox.getSelectedItem();
            if (p != null) {
                return p.getId();
            }
        }
        return UNKNOWN_ID;
    }

    public long getCbTypeId() {
        if (typeComboBox.isEnabled()) {
            Type t = (Type) typeComboBox.getSelectedItem();
            if (t != null) {
                return t.getId();
            }
        }
        return UNKNOWN_ID;

    }

    public void updateProductCbValues(long categoryId) {
        productCbModel.removeAllElements();
        productCbModel.addElement(db().getProducts().get(0)); // Add unknown
        for (Product p : db().getProductListForCategory(categoryId)) {
            productCbModel.addElement(p);
        }
    }

    public void updateTypeCbValues(long productId) {
        typeCbModel.removeAllElements();
        typeCbModel.addElement(db().getTypes().get(0)); // Add unknown
        for (Type t : db().getTypeListForProduct(productId)) {
            typeCbModel.addElement(t);
        }
    }

    public void setComponentValues() {
        // Basic
        newItem.setName(getNameFieldValue().trim());
        newItem.setDescription(getDescriptionFieldValue().trim());
        String priceTxt = getPriceFieldValue();
        if (!priceTxt.isEmpty()) {
            newItem.setPrice(Double.valueOf(priceTxt));
        }

        newItem.setCategoryId(getCbCategoryId());
        newItem.setProductId(getCbProductId());
        newItem.setTypeId(getCbTypeId());

        newItem.setLocalDataSheet(getLocalDataSheetFieldValue());
        newItem.setOnlineDataSheet(getOnlineDataSheetFieldValue());

        // TODO: remove here
        newItem.setLocationId(1);

        // Package
        Package p = getPackageInfo();
        if (p.getId() <= UNKNOWN_ID) {
            p.setId(-1); // Force save
        }
        p.save();
        newItem.setPackageId(p.getId());

    }

    public void setNameTextFieldTracker(JLabel label) {
        nameTextField.setTrackingField(label);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void createCategoryCb() {
        int selectedIndex = 0;
        Vector<Category> categoryItems = new Vector<>();
        for (Category c : db().getCategories()) {
            categoryItems.add(c);
            if (newItem.getId() >= 0) { // Not a new item -> set combobox to value
                if (c.getId() == newItem.getCategoryId()) {
                    selectedIndex = db().findCategoryIndex(c.getId());
                }
            }
        }

        DefaultComboBoxModel<Category> categoryCbModel = new DefaultComboBoxModel<>(categoryItems);
        categoryComboBox = new JComboBox<>(categoryCbModel);
        categoryComboBox.setSelectedIndex(selectedIndex);
    }

    private void createProductCb() {
        int selectedIndex = 0;
        Vector<Product> productStrings = new Vector<>();
        for (Product p : db().getProducts()) {
            productStrings.add(p);
            if (newItem.getId() >= 0) { // Not a new item -> set combobox to value
                if (p.getId() == newItem.getProductId()) {
                    selectedIndex = db().findProductIndex(p.getId());
                }
            }
        }

        productCbModel = new DefaultComboBoxModel<>(productStrings);
        productComboBox = new JComboBox<>(productCbModel);
        productComboBox.setSelectedIndex(selectedIndex);
    }

    private void createTypeCb() {
        int selectedIndex = 0;
        Vector<Type> typeStrings = new Vector<>();
        for (com.waldo.inventory.classes.Type t : db().getTypes()) {
            typeStrings.add(t);
            if (newItem.getId() >= 0) { // Not a new item -> set combobox to value
                if (t.getId() == newItem.getTypeId()) {
                    selectedIndex = db().findTypeIndex(t.getId());
                }
            }
        }

        typeCbModel = new DefaultComboBoxModel<>(typeStrings);
        typeComboBox = new JComboBox<>(typeCbModel);
        typeComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getProductId() > DbObject.UNKNOWN_ID));
        typeComboBox.setSelectedIndex(selectedIndex);
    }

    private void initializeBasicComponents() {
        // Identification
        idTextField = new ITextField();
        idTextField.setEditable(false);
        idTextField.setEnabled(false);

        nameTextField = new ITextField();
        descriptionTextArea = new ITextArea();
        descriptionTextArea.setLineWrap(true); // Go to next line when area is full
        descriptionTextArea.setWrapStyleWord(true); // Don't cut words in two

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(Double.MIN_VALUE);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true); // Commit on every key press
        priceTextField = new ITextField();

        // Combo boxes
        createCategoryCb();
        createProductCb();
        createTypeCb();

        // Local data sheet
        localDataSheetTextField = new ITextField();
        localDataSheetTextField.setToolTipText(localDataSheetTextField.getText());
        localDataSheetButton = new JButton(ImageUtils.loadImageIcon("folder"));
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
    }

    private void initializePackageComponents() {
        DefaultComboBoxModel<PackageType> packageTypeCbModel = new DefaultComboBoxModel<>();
        for (PackageType pt : db().getPackageTypes()) {
            packageTypeCbModel.addElement(pt);
        }
        packageTypeComboBox = new JComboBox<>(packageTypeCbModel);
        packageTypeComboBox.insertItemAt(PackageType.createDummyPackageType(), 0);
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        packagePinsSp = new JSpinner(spinnerModel);
        spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        packageWidthSp = new JSpinner(spinnerModel);
        spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        packageHeightSp = new JSpinner(spinnerModel);
        //packagePinsSp.addEditedListener(this);
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
                new JComponent[] {categoryComboBox, productComboBox, typeComboBox}
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

    private JPanel createPackagePanel() {
        JPanel packagePanel = new JPanel(new GridBagLayout());

        // Border
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Package");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

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

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // - extra
        JPanel dimPanel = new JPanel();
        dimPanel.add(packageWidthSp);
        dimPanel.add(packageHeightSp);

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
        packagePanel.add(packageTypeComboBox, gbc);

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

        // - dimension
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

        // - border
        packagePanel.setBorder(titledBorder);

        return packagePanel;
    }


    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        initializeBasicComponents();
        initializePackageComponents();
    }

    @Override
    public void initializeLayouts() {
        // Add tabs
        //tabbedPane.addTab("Basic  ", resourceManager.readImage("EditItem.InfoIcon"), componentPanel, "Component info");
        tabbedPane.addTab(null, createBasicPanel());
        tabbedPane.addTab(null, createPackagePanel());

        // Create vertical labels to render tab titles
        JLabel labTab1 = new JLabel("Basic ");
        labTab1.setUI(ILabel.createVerticalLabel(false)); // true/false to make it upwards/downwards
        tabbedPane.setTabComponentAt(0, labTab1); // For component1

        JLabel labTab2 = new JLabel("Package ");
        labTab2.setUI(ILabel.createVerticalLabel(false));
        tabbedPane.setTabComponentAt(1, labTab2); // For component2

        add(tabbedPane);
    }

    @Override
    public void updateComponents(Object object) {
        LOG.debug("Component panel: update components.");

        idTextField.setText(String.valueOf(newItem.getId()));
        nameTextField.setText(newItem.getName().trim());
        descriptionTextArea.setText(newItem.getDescription().trim());
        priceTextField.setText(String.valueOf(newItem.getPrice()));

        // Combo boxes
        int cNdx = db().findCategoryIndex(newItem.getCategoryId());
        int pNdx = db().findProductIndex(newItem.getProductId());
        int tNdx = db().findTypeIndex(newItem.getTypeId());
        if (cNdx >= 0) {
            categoryComboBox.setSelectedIndex(cNdx); // This should also set the product combo box values

            if (pNdx >= 0) {
                productComboBox.setSelectedIndex(pNdx);

                if (tNdx >= 0) {
                    typeComboBox.setSelectedIndex(tNdx);
                } else {
                    typeComboBox.setSelectedIndex(0);
                }
            } else {
                productComboBox.setSelectedIndex(0);
                typeComboBox.setSelectedIndex(0);
            }

        } else {
            categoryComboBox.setSelectedIndex(0); // Unknown
            productComboBox.setSelectedIndex(0);
            typeComboBox.setSelectedIndex(0);
        }

        // Data sheets
        localDataSheetTextField.setText(newItem.getLocalDataSheet());
        onlineDataSheetTextField.setText(newItem.getOnlineDataSheet());

        // Package
        Package p = db().findPackageByIndex(newItem.getPackageId());
        if (p != null && !p.isUnknown()) {
            packageTypeComboBox.setSelectedItem(p.getPackageType());
            packageWidthSp.setValue(p.getWidth());
            packageHeightSp.setValue(p.getHeight());
            packagePinsSp.setValue(p.getPins());
        } else {
            packageTypeComboBox.setSelectedIndex(0);
            packageWidthSp.setValue(0);
            packageHeightSp.setValue(0);
            packagePinsSp.setValue(0);
        }
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

    public String getDescriptionFieldValue() {
        return descriptionTextArea.getText();
    }

    public String getPriceFieldValue() {
        return priceTextField.getText();
    }

    public void setPriceFieldError(String error) {
        priceTextField.setError(error);
    }

    public String getLocalDataSheetFieldValue() {
        return localDataSheetTextField.getText();
    }

    public void setLocalDataSheetFieldValue(String localDataSheetFieldValue) {
        localDataSheetTextField.setText(localDataSheetFieldValue);
    }

    public String getOnlineDataSheetFieldValue() {
        return onlineDataSheetTextField.getText();
    }

    public JComboBox getProductComboBox() {
        return productComboBox;
    }

    public JComboBox getTypeComboBox() {
        return typeComboBox;
    }


    public Package getPackageInfo() {
        Package p = new Package();
        PackageType pt = (PackageType) packageTypeComboBox.getSelectedItem();
        if (pt != null) {
            p.setPackageType(pt);
        } else {
            p.setPackageType(PackageType.getUnknownPackageType());
        }
        p.setId(newItem.getPackageId());
        p.setWidth((Integer) packageWidthSp.getValue());
        p.setHeight((Integer) packageHeightSp.getValue());
        p.setPins((Integer) packagePinsSp.getValue());

        return p;
    }
}
