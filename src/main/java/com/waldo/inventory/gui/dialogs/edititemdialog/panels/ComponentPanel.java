package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.SearchManager;
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
import static com.waldo.inventory.database.SearchManager.*;
import static com.waldo.inventory.gui.Application.imageResource;

public class ComponentPanel extends JPanel implements GuiInterface {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentPanel.class);

    private Item newItem;

    // Listener
    private IEditedListener editedListener;

    // Tabbed pane
    private JTabbedPane tabbedPane;

    // Details
    private IComboBox<PackageType> packageTypeComboBox;
    private ISpinner packagePinsSp;
    private IFormattedTextField packageWidthTf, packageHeightTf;
    private IComboBox<Manufacturer> manufacturerComboBox;
    private ILabel iconLabel;

    // Basic info
    private ITextField idTextField;
    private ITextField nameTextField;
    private ITextArea descriptionTextArea;
    private ITextField priceTextField;
    private IComboBox<Category> categoryComboBox;
    private IComboBox<Product> productComboBox;
    private DefaultComboBoxModel<Product> productCbModel;
    private DefaultComboBoxModel<Type> typeCbModel;
    private IComboBox<Type> typeComboBox;

    // Data sheet
    private ITextField localDataSheetTextField;
    private JButton localDataSheetButton;
    private ITextField onlineDataSheetTextField;

    public ComponentPanel(Item newItem, @NotNull IEditedListener listener) {
        this.newItem = newItem;
        this.editedListener = listener;
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
                    selectedIndex = sm().findCategoryIndex(c.getId());
                }
            }
        }

        DefaultComboBoxModel<Category> categoryCbModel = new DefaultComboBoxModel<>(categoryItems);
        categoryComboBox = new IComboBox<>(categoryCbModel);
        categoryComboBox.addEditedListener(editedListener, "categoryId");
        categoryComboBox.setSelectedIndex(selectedIndex);
    }

    private void createProductCb() {
        int selectedIndex = 0;
        Vector<Product> productStrings = new Vector<>();
        for (Product p : db().getProducts()) {
            productStrings.add(p);
            if (newItem.getId() >= 0) { // Not a new item -> set combobox to value
                if (p.getId() == newItem.getProductId()) {
                    selectedIndex = sm().findProductIndex(p.getId());
                }
            }
        }

        productCbModel = new DefaultComboBoxModel<>(productStrings);
        productComboBox = new IComboBox<>(productCbModel);
        productComboBox.addEditedListener(editedListener, "productId");
        productComboBox.setSelectedIndex(selectedIndex);
    }

    private void createTypeCb() {
        int selectedIndex = 0;
        Vector<Type> typeStrings = new Vector<>();
        for (com.waldo.inventory.classes.Type t : db().getTypes()) {
            typeStrings.add(t);
            if (newItem.getId() >= 0) { // Not a new item -> set combobox to value
                if (t.getId() == newItem.getTypeId()) {
                    selectedIndex = sm().findTypeIndex(t.getId());
                }
            }
        }

        typeCbModel = new DefaultComboBoxModel<>(typeStrings);
        typeComboBox = new IComboBox<>(typeCbModel);
        typeComboBox.addEditedListener(editedListener, "typeId");
        typeComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getProductId() > DbObject.UNKNOWN_ID));
        typeComboBox.setSelectedIndex(selectedIndex);
    }

    private void initializeBasicComponents() {
        // Identification
        idTextField = new ITextField();
        idTextField.setEditable(false);
        idTextField.setEnabled(false);

        nameTextField = new ITextField();
        nameTextField.addEditedListener(editedListener, "name");
        descriptionTextArea = new ITextArea();
        descriptionTextArea.setLineWrap(true); // Go to next line when area is full
        descriptionTextArea.setWrapStyleWord(true); // Don't cut words in two
        descriptionTextArea.addEditedListener(editedListener, "description");

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(Double.MIN_VALUE);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true); // Commit on every key press
        priceTextField = new ITextField();
        priceTextField.addEditedListener(editedListener, "price");

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
        DefaultComboBoxModel<PackageType> packageTypeCbModel = new DefaultComboBoxModel<>();
        for (PackageType pt : db().getPackageTypes()) {
            packageTypeCbModel.addElement(pt);
        }
        packageTypeComboBox = new IComboBox<>(packageTypeCbModel);
        packageTypeComboBox.addEditedListener(editedListener, "packageTypeId");
        packageTypeComboBox.insertItemAt(PackageType.createDummyPackageType(), 0);
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        packagePinsSp = new ISpinner(spinnerModel);
        packagePinsSp.addEditedListener(editedListener, "pins");

        packageWidthTf = new IFormattedTextField(NumberFormat.getNumberInstance());
        packageWidthTf.addEditedListener(editedListener, "width");
        packageHeightTf = new IFormattedTextField(NumberFormat.getNumberInstance());
        packageHeightTf.addEditedListener(editedListener, "height");

        // Manufacturer
        DefaultComboBoxModel<Manufacturer> model = new DefaultComboBoxModel<>();
        for (Manufacturer m : db().getManufacturers()) {
            model.addElement(m);
        }
        manufacturerComboBox = new IComboBox<>(model);
        manufacturerComboBox.addEditedListener(editedListener, "manufacturerId");
        manufacturerComboBox.addItemListener(e -> {
            Manufacturer m = (Manufacturer) e.getItem();
            if (m != null) {
                if (!m.getIconPath().isEmpty()) {
                    iconLabel.setIcon(m.getIconPath(), 100, 100);
                }
            }
        });

        iconLabel = new ILabel();
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

    private JPanel createDetailsPanel() {
        JPanel packagePanel = new JPanel(new GridBagLayout());
        JPanel manufacturerPanel = new JPanel(new GridBagLayout());

        // Borders
        TitledBorder packageBorder = BorderFactory.createTitledBorder("Package");
        packageBorder.setTitleJustification(TitledBorder.RIGHT);
        packageBorder.setTitleColor(Color.gray);
        TitledBorder manufacturerBorder = BorderFactory.createTitledBorder("Manufacturer");
        manufacturerBorder.setTitleJustification(TitledBorder.RIGHT);
        manufacturerBorder.setTitleColor(Color.gray);

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
        packagePanel.setBorder(packageBorder);

        // Manufacturer
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
        manufacturerPanel.add(manufacturerComboBox, gbc);

        // Icon
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        manufacturerPanel.add(iconLabel, gbc);

        // Border
        manufacturerPanel.setBorder(manufacturerBorder);

        // Add to panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        panel.add(packagePanel, BorderLayout.NORTH);
        panel.add(manufacturerPanel, BorderLayout.CENTER);

        return panel;
    }


    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        initializeBasicComponents();
        initializeDetailsComponents();
    }

    @Override
    public void initializeLayouts() {
        // Add tabs
        //tabbedPane.addTab("Basic  ", imageResource.readImage("EditItem.InfoIcon"), componentPanel, "Component info");
        tabbedPane.addTab("Basic", createBasicPanel());
        tabbedPane.addTab("Details", createDetailsPanel());

//        // Create vertical labels to render tab titles
//        JLabel labTab1 = new JLabel("Basic ");
//        labTab1.setUI(ILabel.createVerticalLabel(false)); // true/false to make it upwards/downwards
//        tabbedPane.setTabComponentAt(0, labTab1); // For component1
//
//        JLabel labTab2 = new JLabel("Package ");
//        labTab2.setUI(ILabel.createVerticalLabel(false));
//        tabbedPane.setTabComponentAt(1, labTab2); // For component2

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
        int cNdx = sm().findCategoryIndex(newItem.getCategoryId());
        int pNdx = sm().findProductIndex(newItem.getProductId());
        int tNdx = sm().findTypeIndex(newItem.getTypeId());
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
        PackageType p = sm().findPackageTypeById(newItem.getPackageTypeId());
        if (p != null && !p.isUnknown()) {
            packageTypeComboBox.setSelectedItem(p);
        } else {
            packageTypeComboBox.setSelectedIndex(0);
        }
        packagePinsSp.setValue(newItem.getPins());
        packageHeightTf.setText(String.valueOf(newItem.getHeight()));
        packageWidthTf.setText(String.valueOf(newItem.getWidth()));

        // Manufacturer
        if (newItem.getManufacturerId() >= 0) {
            // Set index
            int ndx = sm().findManufacturerIndex(newItem.getManufacturerId());
            manufacturerComboBox.setSelectedIndex(ndx);

            // Set icon
            try {
                Manufacturer m = sm().findManufacturerById(newItem.getManufacturerId());
                if (m != null && !m.getIconPath().isEmpty()) {
                    iconLabel.setIcon(m.getIconPath(), 100,100);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
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

    public PackageType getPackageType() {
        return (PackageType) packageTypeComboBox.getSelectedItem();
    }

    public int getPinsFieldValue() {
        return (int) packagePinsSp.getValue();
    }

    public String getWidthFieldValue() {
        return packageWidthTf.getText();
    }

    public String getHeightFieldValue() {
        return packageHeightTf.getText();
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
