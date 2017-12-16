package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialogLayout;
import com.waldo.inventory.gui.dialogs.edititemdialog.panels.componentpaneltabs.SetItemPanel;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;
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

import static com.waldo.inventory.Utils.GuiUtils.createFieldConstraints;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class ComponentPanel extends JPanel implements GuiInterface {

    public static final int TAB_BASIC = 0;
    public static final int TAB_DETAILS = 1;
    public static final int TAB_SET_ITEMS = 2;

    private final Application application;
    private final Item newItem;

    // Listener
    private final IEditedListener editedListener;

    // Tabbed pane
    private JTabbedPane tabbedPane;

    // Basic info
    private ITextField idTextField;
    private GuiUtils.INameValuePanel nameValuePnl;
    private ITextArea descriptionTextArea;
    private ITextField priceTextField;
    private IComboBox<Category> categoryComboBox;
    private IComboBox<Product> productComboBox;
    private IComboBox<Type> typeComboBox;
    private ITextField localDataSheetTextField;
    private JButton localDataSheetButton;
    private GuiUtils.IBrowseWebPanel onlineDataSheetTextField;

    // Details
    private GuiUtils.IPackagePanel packagePnl;
    private IComboBox<Manufacturer> manufacturerCb;
    private ILabel manufacturerIconLbl;
    private IStarRater starRater;
    private ICheckBox discourageOrderCb;
    private ITextEditor remarksTe;

    // Sets
    private ICheckBox isSetCb;
    private SetItemPanel setItemPanel;



    public ComponentPanel(Application application, Item newItem, @NotNull IEditedListener listener) {
        this.application = application;
        this.newItem = newItem;
        this.editedListener = listener;
    }

    /*
     *                  PUBLIC METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void setSelectedTab(int tab) {
        if (tabbedPane != null) {
            tabbedPane.setSelectedIndex(tab);
        }
    }

    private void updateManufacturerCbValues() {
        if (manufacturerCb != null) {
            manufacturerCb.updateList(cache().getManufacturers());
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
        categoryComboBox = new IComboBox<>(cache().getCategories(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        categoryComboBox.addEditedListener(editedListener, "categoryId");
        categoryComboBox.setSelectedItem(newItem.getCategory());
    }

    private void createProductCb() {
        java.util.List<Product> productList;
        if (newItem.getCategoryId() > DbObject.UNKNOWN_ID) {
            productList = sm().findProductListForCategory(newItem.getCategoryId());
        } else {
            productList = cache().getProducts();
        }

        productComboBox = new IComboBox<>(productList, new ComparatorUtils.DbObjectNameComparator<>(), true);
        productComboBox.addEditedListener(editedListener, "productId");
        productComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getCategoryId() > DbObject.UNKNOWN_ID));
        productComboBox.setSelectedItem(newItem.getProduct());
    }

    private void createTypeCb() {
        java.util.List<Type> typeList;
        if (newItem.getCategoryId() > DbObject.UNKNOWN_ID) {
            typeList = sm().findTypeListForProduct(newItem.getProductId());
        } else {
            typeList = cache().getTypes();
        }

        typeComboBox = new IComboBox<>(typeList, new ComparatorUtils.DbObjectNameComparator<>(), true);
        typeComboBox.addEditedListener(editedListener, "typeId");
        typeComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getProductId() > DbObject.UNKNOWN_ID));
        typeComboBox.setSelectedItem(newItem.getType());
    }

    private void createManufacturerCb() {
        manufacturerCb = new IComboBox<>(cache().getManufacturers(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        manufacturerCb.setSelectedItem(newItem.getManufacturer());
        manufacturerCb.addEditedListener(editedListener, "manufacturerId");
    }

    private ActionListener createAddCategoryListener() {
        return e -> {
            Category newCategory = new Category();
            SubDivisionsDialog subDivisionsDialog = new SubDivisionsDialog(application, "Add category", newCategory);
            if (subDivisionsDialog.showDialog() == IDialog.OK) {
                newCategory.save();
                SwingUtilities.invokeLater(() -> {
                    updateCategoryCbValues();
                    if (categoryComboBox.getSelectedItem() != null) {
                        updateProductCbValues(((Category) categoryComboBox.getSelectedItem()).getId());
                    }
                    if (productComboBox.getSelectedItem() != null) {
                        updateTypeCbValues(((Product) productComboBox.getSelectedItem()).getId());
                    }
                });
            }
        };
    }

    private ActionListener createAddProductListener() {
        return e -> {
            if (newItem.getCategoryId() > DbObject.UNKNOWN_ID) {
                Product newProduct = new Product(newItem.getCategoryId());
                SubDivisionsDialog subDivisionsDialog = new SubDivisionsDialog(application, "Add product", newProduct);
                if (subDivisionsDialog.showDialog() == IDialog.OK) {
                    newProduct.save();
                    SwingUtilities.invokeLater(() -> {
                        updateCategoryCbValues();
                        if (categoryComboBox.getSelectedItem() != null) {
                            updateProductCbValues(((Category) categoryComboBox.getSelectedItem()).getId());
                        }
                        if (productComboBox.getSelectedItem() != null) {
                            updateTypeCbValues(((Product) productComboBox.getSelectedItem()).getId());
                        }
                    });
                }
            } else {
                JOptionPane.showMessageDialog(
                        ComponentPanel.this,
                        "Select a category first..",
                        "No category",
                        JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    private ActionListener createAddTypeListener() {
        return e -> {
            if (newItem.getCategoryId() > DbObject.UNKNOWN_ID && newItem.getProductId() > DbObject.UNKNOWN_ID) {
                Type newType = new Type(newItem.getProductId());
                SubDivisionsDialog subDivisionsDialog = new SubDivisionsDialog(application, "Add type", newType);
                if (subDivisionsDialog.showDialog() == IDialog.OK) {
                    newType.save();
                    SwingUtilities.invokeLater(() -> {
                        updateCategoryCbValues();
                        if (categoryComboBox.getSelectedItem() != null) {
                            updateProductCbValues(((Category) categoryComboBox.getSelectedItem()).getId());
                        }
                        if (productComboBox.getSelectedItem() != null) {
                            updateTypeCbValues(((Product) productComboBox.getSelectedItem()).getId());
                        }
                    });
                }
            } else {
                JOptionPane.showMessageDialog(
                        ComponentPanel.this,
                        "Select category and product first..",
                        "No category and/or product",
                        JOptionPane.ERROR_MESSAGE);
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

        nameValuePnl = new GuiUtils.INameValuePanel(editedListener, "name", editedListener);

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
        onlineDataSheetTextField = new GuiUtils.IBrowseWebPanel("","onlineDataSheet", editedListener);
        //onlineDataSheetTextField.addEditedListener(editedListener, "onlineDataSheet");
    }

    private void initializeDetailsComponents() {
        // Package
        packagePnl = new GuiUtils.IPackagePanel(application, editedListener, "packageTypeId", "pins");

        // Manufacturer
        createManufacturerCb();
        manufacturerCb.setName(EditItemDialogLayout.COMP_MANUFACTURER);
        manufacturerCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Manufacturer m = (Manufacturer) manufacturerCb.getSelectedItem();
                if (m != null) {
                    if (!m.getIconPath().isEmpty()) {
                        Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgManufacturersPath(), m.getIconPath());
                        manufacturerIconLbl.setIcon(path.toString(), 48, 48);
                    }
                }
            }
        });
        manufacturerIconLbl = new ILabel("", ILabel.RIGHT);

        // Remarks stuff
        starRater = new IStarRater(5, 0,0);
        starRater.addEditedListener(editedListener, "rating");
        starRater.setName(EditItemDialogLayout.COMP_RATING);
        discourageOrderCb = new ICheckBox("Discourage future orders");
        discourageOrderCb.addEditedListener(editedListener, "discourageOrder");
        discourageOrderCb.setAlignmentX(RIGHT_ALIGNMENT);
        discourageOrderCb.setName(EditItemDialogLayout.COMP_DISCOURAGE);
        remarksTe = new ITextEditor();
        remarksTe.setName(EditItemDialogLayout.COMP_REMARK);
        remarksTe.setPreferredSize(new Dimension(300, 100));

    }

    private void initializeSetComponents() {
        isSetCb = new ICheckBox("Is set", false);
        isSetCb.addEditedListener(editedListener, "set");
        isSetCb.addActionListener(e -> {
            setItemPanel.setEnabled(isSetCb.isSelected());
            if (isSetCb.isSelected()) {
                setItemPanel.updateComponents(newItem);
            }
        });
        setItemPanel = new SetItemPanel(application, newItem);
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
                        GuiUtils.createComboBoxWithButton(categoryComboBox, createAddCategoryListener()),
                        GuiUtils.createComboBoxWithButton(productComboBox, createAddProductListener()),
                        GuiUtils.createComboBoxWithButton(typeComboBox, createAddTypeListener())}
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


        // Borders
        TitledBorder packageBorder = GuiUtils.createTitleBorder("Package");
        TitledBorder manufacturerBorder = GuiUtils.createTitleBorder("Manufacturer");
        TitledBorder remarksBorder = GuiUtils.createTitleBorder("Remarks");

        packagePanel.setBorder(packageBorder);
        manufacturerPanel.setBorder(manufacturerBorder);
        remarksPanel.setBorder(remarksBorder);

        // PACKAGE
        packagePanel.add(packagePnl, BorderLayout.CENTER);

        GuiUtils.GridBagHelper gbc;

        // MANUFACTURER
        gbc = new GuiUtils.GridBagHelper(manufacturerPanel);
        gbc.addLine("Name: ", GuiUtils.createComboBoxWithButton(manufacturerCb, createManufacturerAddListener()));
        gbc.add(manufacturerIconLbl, 2,0,1,1);

        // REMARKS
        gbc = new GuiUtils.GridBagHelper(remarksPanel);

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
        remarksPanel.add(new JScrollPane(remarksTe), gbc);


        // Add to panel
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        gbc = new GuiUtils.GridBagHelper(panel);
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        panel.add(packagePanel, gbc); gbc.gridy++;
        panel.add(manufacturerPanel, gbc); gbc.gridy++;
        gbc.weighty = 1;
        gbc.fill = GuiUtils.GridBagHelper.BOTH;
        panel.add(remarksPanel, gbc);

        return panel;
    }

    private JPanel createSetItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel setPanel = new JPanel(new BorderLayout());

//        setPanel.add(isSetCb, BorderLayout.CENTER);
//        setPanel.add(setValuesBtn, BorderLayout.EAST);
//        setPanel.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));

        panel.add(isSetCb, BorderLayout.NORTH);
        panel.add(setItemPanel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        return panel;
    }


    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        //tabbedPane.addChangeListener(e -> /* update tab*/);

        initializeBasicComponents();
        initializeDetailsComponents();
        initializeSetComponents();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        // Add tabs
        tabbedPane.addTab("Basic", createBasicPanel());
        tabbedPane.addTab("Details", createDetailsPanel());
        tabbedPane.addTab("Set items", createSetItemsPanel());

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
                    manufacturerIconLbl.setIcon(path.toString(), 48, 48);
                } else {
                    manufacturerIconLbl.setIcon(imageResource.readImage("Common.Unknown"));
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
        remarksTe.setDocument(newItem.getRemarksFile());

        // SETS
        isSetCb.setSelected(newItem.isSet());
        setItemPanel.updateComponents(newItem); // TODO only do this when tab opens
        setItemPanel.setEnabled(newItem.isSet());

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
