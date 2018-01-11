package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialogLayout;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;

import static com.waldo.inventory.Utils.GuiUtils.createFieldConstraints;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class ComponentPanel<T extends Item> extends JPanel implements GuiInterface {

    public static final int TAB_BASIC = 0;
    public static final int TAB_DETAILS = 1;

    private final Application application;
    private final T selectedItem;

    // Listener
    private final IEditedListener editedListener;

    // Tabbed pane
    private JTabbedPane tabbedPane;

    // Basic info
    private GuiUtils.INameValuePanel nameValuePnl;
    private ITextField aliasTf;
    private ITextArea descriptionTa;
    private ITextField priceTf;
    private IComboBox<Category> categoryCb;
    private IComboBox<Product> productCb;
    private IComboBox<Type> typeCb;
    private ITextField localDataSheetTf;
    private JButton localDataSheetBtn;
    private GuiUtils.IBrowseWebPanel onlineDataSheetTf;

    // Details
    private GuiUtils.IPackagePanel packagePnl;
    private IComboBox<Manufacturer> manufacturerCb;
    private ILabel manufacturerIconLbl;
    private IStarRater starRater;
    private ICheckBox discourageOrderCb;
    private ITextEditor remarksTe;


    public ComponentPanel(Application application, T selectedItem, @NotNull IEditedListener listener) {
        this.application = application;
        this.selectedItem = selectedItem;
        this.editedListener = listener;
    }

    /*
     *                  PUBLIC METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void setValuesForSet(Set set) {
        selectedItem.setCategoryId(set.getCategoryId());
        categoryCb.setSelectedItem(set.getCategory());
        selectedItem.setProductId(set.getProductId());
        productCb.setSelectedItem(set.getProduct());
        selectedItem.setTypeId(set.getTypeId());
        typeCb.setSelectedItem(set.getType());

        // DATA SHEETS
        selectedItem.setLocalDataSheet(set.getLocalDataSheet());
        localDataSheetTf.setText(set.getLocalDataSheet());
        selectedItem.setOnlineDataSheet(set.getOnlineDataSheet());
        onlineDataSheetTf.setText(set.getOnlineDataSheet());

        // PACKAGE
        selectedItem.setPackageTypeId(set.getPackageTypeId());
        selectedItem.setPins(set.getPins());
        packagePnl.setPackageType(set.getPackageType(), set.getPins());

        // MANUFACTURER
        if (set.getManufacturerId() > DbObject.UNKNOWN_ID) { // Edit
            selectedItem.setManufacturerId(set.getManufacturerId());
            updateManufacturerCb(set.getManufacturer());
        }

        // REMARKS
        selectedItem.setRating(set.getRating());
        starRater.setRating(set.getRating());
        starRater.setSelection(0);
        discourageOrderCb.setSelected(set.isDiscourageOrder());
    }

    public void setSelectedTab(int tab) {
        if (tabbedPane != null) {
            tabbedPane.setSelectedIndex(tab);
        }
    }

    private void updateManufacturerCbValues() {
        if (manufacturerCb != null) {
            manufacturerCb.updateList(cache().getManufacturers());
            manufacturerCb.setSelectedItem(selectedItem.getManufacturer());
        }
    }

    private void updateManufacturerCb(Manufacturer manufacturer) {
        manufacturerCb.setSelectedItem(manufacturer);

        if (manufacturer != null) {
            try {
                String p = manufacturer.getIconPath();
                if (!p.isEmpty()) {
                    Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgManufacturersPath(), p);
                    manufacturerIconLbl.setIcon(path.toString(), 48, 48);
                } else {
                    manufacturerIconLbl.setIcon((ImageIcon) null);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void updateCategoryCbValues() {
        categoryCb.updateList();
        categoryCb.setSelectedItem(selectedItem.getCategory());
    }

    public void updateProductCbValues(long categoryId) {
        productCb.updateList(sm().findProductListForCategory(categoryId));
        productCb.setSelectedItem(selectedItem.getProduct());
    }

    public void updateTypeCbValues(long productId) {
        typeCb.updateList(sm().findTypeListForProduct(productId));
        typeCb.setSelectedItem(selectedItem.getType());
    }

    public boolean updateRemarks() {
        boolean changed = false;
        DefaultStyledDocument document = remarksTe.getStyledDocument();
        File file = selectedItem.getRemarksFile();
        if (document != null && (document.getLength() > 0 || file != null)) {
            if (file == null) {
                try {
                    file = FileUtils.createTempFile("remarks");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (file != null) {
                selectedItem.setRemarksFile(file);
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                        objectOutputStream.writeObject(document);
                        objectOutputStream.flush();
                        objectOutputStream.close();
                        changed = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not save file..",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
        return changed;
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void createCategoryCb() {
        categoryCb = new IComboBox<>(cache().getCategories(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        categoryCb.addEditedListener(editedListener, "categoryId");
        categoryCb.setSelectedItem(selectedItem.getCategory());
    }

    private void createProductCb() {
        java.util.List<Product> productList;
        if (selectedItem.getCategoryId() > DbObject.UNKNOWN_ID) {
            productList = sm().findProductListForCategory(selectedItem.getCategoryId());
        } else {
            productList = cache().getProducts();
        }

        productCb = new IComboBox<>(productList, new ComparatorUtils.DbObjectNameComparator<>(), true);
        productCb.addEditedListener(editedListener, "productId");
        productCb.setEnabled((selectedItem.getId() >= 0) && (selectedItem.getCategoryId() > DbObject.UNKNOWN_ID));
        productCb.setSelectedItem(selectedItem.getProduct());
    }

    private void createTypeCb() {
        java.util.List<Type> typeList;
        if (selectedItem.getCategoryId() > DbObject.UNKNOWN_ID) {
            typeList = sm().findTypeListForProduct(selectedItem.getProductId());
        } else {
            typeList = cache().getTypes();
        }

        typeCb = new IComboBox<>(typeList, new ComparatorUtils.DbObjectNameComparator<>(), true);
        typeCb.addEditedListener(editedListener, "typeId");
        typeCb.setEnabled((selectedItem.getId() >= 0) && (selectedItem.getProductId() > DbObject.UNKNOWN_ID));
        typeCb.setSelectedItem(selectedItem.getType());
    }

    private void createManufacturerCb() {
        manufacturerCb = new IComboBox<>(cache().getManufacturers(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        manufacturerCb.setSelectedItem(selectedItem.getManufacturer());
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
                    if (categoryCb.getSelectedItem() != null) {
                        updateProductCbValues(((Category) categoryCb.getSelectedItem()).getId());
                    }
                    if (productCb.getSelectedItem() != null) {
                        updateTypeCbValues(((Product) productCb.getSelectedItem()).getId());
                    }
                });
            }
        };
    }

    private ActionListener createAddProductListener() {
        return e -> {
            if (selectedItem.getCategoryId() > DbObject.UNKNOWN_ID) {
                Product newProduct = new Product(selectedItem.getCategoryId());
                SubDivisionsDialog subDivisionsDialog = new SubDivisionsDialog(application, "Add product", newProduct);
                if (subDivisionsDialog.showDialog() == IDialog.OK) {
                    newProduct.save();
                    SwingUtilities.invokeLater(() -> {
                        updateCategoryCbValues();
                        if (categoryCb.getSelectedItem() != null) {
                            updateProductCbValues(((Category) categoryCb.getSelectedItem()).getId());
                        }
                        if (productCb.getSelectedItem() != null) {
                            updateTypeCbValues(((Product) productCb.getSelectedItem()).getId());
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
            if (selectedItem.getCategoryId() > DbObject.UNKNOWN_ID && selectedItem.getProductId() > DbObject.UNKNOWN_ID) {
                Type newType = new Type(selectedItem.getProductId());
                SubDivisionsDialog subDivisionsDialog = new SubDivisionsDialog(application, "Add type", newType);
                if (subDivisionsDialog.showDialog() == IDialog.OK) {
                    newType.save();
                    SwingUtilities.invokeLater(() -> {
                        updateCategoryCbValues();
                        if (categoryCb.getSelectedItem() != null) {
                            updateProductCbValues(((Category) categoryCb.getSelectedItem()).getId());
                        }
                        if (productCb.getSelectedItem() != null) {
                            updateTypeCbValues(((Product) productCb.getSelectedItem()).getId());
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
        nameValuePnl = new GuiUtils.INameValuePanel(editedListener, "name", editedListener);
        aliasTf = new ITextField(editedListener, "alias");

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
        onlineDataSheetTf = new GuiUtils.IBrowseWebPanel("","onlineDataSheet", editedListener);
        //onlineDataSheetTf.addEditedListener(editedListener, "onlineDataSheet");
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
                new String[] {"Name: ", "Alias: "},
                new JComponent[] {nameValuePnl, aliasTf}
        ));

        basicPanel.add(new ITitledEditPanel(
                "Sub divisions",
                new String[] {"Category: ", "Product: ", "Type: "},
                new JComponent[] {
                        GuiUtils.createComboBoxWithButton(categoryCb, createAddCategoryListener()),
                        GuiUtils.createComboBoxWithButton(productCb, createAddProductListener()),
                        GuiUtils.createComboBoxWithButton(typeCb, createAddTypeListener())}
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
        //tabbedPane.addTab("Set items", createSetItemsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... object) {
        aliasTf.setText(selectedItem.getAlias().trim());
        nameValuePnl.setNameTxt(selectedItem.getName().trim());
        nameValuePnl.setValue(selectedItem.getValue());
        descriptionTa.setText(selectedItem.getDescription().trim());
        priceTf.setText(String.valueOf(selectedItem.getPrice()));

        // Combo boxes
        categoryCb.setSelectedItem(selectedItem.getCategory());
        productCb.setSelectedItem(selectedItem.getProduct());
        typeCb.setSelectedItem(selectedItem.getType());

        // DATA SHEETS
        localDataSheetTf.setText(selectedItem.getLocalDataSheet());
        onlineDataSheetTf.setText(selectedItem.getOnlineDataSheet());

        // PACKAGE
        packagePnl.setPackageType(selectedItem.getPackageType(), selectedItem.getPins());

        // MANUFACTURER
        if (selectedItem.getManufacturerId() > DbObject.UNKNOWN_ID) { // Edit
            updateManufacturerCb(selectedItem.getManufacturer());
        }

        // REMARKS
        starRater.setRating(selectedItem.getRating());
        starRater.setSelection(0);
        discourageOrderCb.setSelected(selectedItem.isDiscourageOrder());
        remarksTe.setDocument(selectedItem.getRemarksFile());
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
        return nameValuePnl.getNameText();
    }

    public void setNameFieldError(String error) {
        nameValuePnl.setError(error);
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
}
