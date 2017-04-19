package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.classes.Type;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;
import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class IObjectSearchPanel extends JPanel implements GuiInterface {

    private ResourceManager resourceManager;
    private IObjectSearchListener objectSearchListener;

    private ITextField searchField;
    private JButton searchButton;
    private ILabel infoLabel;
    private ILabel advancedLabel;

    private boolean searched = false;
    private boolean inAdvanced = false;
    private boolean hasAdvancedSearchOption;
    private int[] searchOptions;

    private JToolBar advancedToolBar;
    private JComboBox<Category> advancedCategoryCb;
    private DefaultComboBoxModel<Product> productCbModel;
    private JComboBox<Product> advancedProductCb;
    private DefaultComboBoxModel<Type> typeCbModel;
    private JComboBox<Type> advancedTypeCb;
    private JCheckBox advancedCheckbox1;
    private JCheckBox advancedCheckbox2;
    private JCheckBox advancedCheckbox3;

    public IObjectSearchPanel(boolean hasAdvancedSearchOption, IObjectSearchListener objectSearchListener) {
        this.hasAdvancedSearchOption = hasAdvancedSearchOption;
        this.objectSearchListener = objectSearchListener;

        URL url = TopToolBar.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    public IObjectSearchPanel(boolean hasAdvancedSearchOption, IObjectSearchListener objectSearchListener, int... searchOptions) {
        this(hasAdvancedSearchOption, objectSearchListener);
        this.searchOptions = searchOptions;
    }

    public interface IObjectSearchListener {
        void onDbObjectFound(java.util.List<DbObject> foundObjects);
        void onSearchCleared();
    }

    public void setSearchOptions(int... options) {
        searchOptions = options;
    }

    public void clearSearch() {

        setSearched(false);
        clearLabel();

        if (objectSearchListener != null) {
            objectSearchListener.onSearchCleared();
        }
    }

    public void search(String searchWord) {
        List<DbObject> foundObjects = new ArrayList<>();

        // Search list
        if (searchOptions == null || searchOptions.length == 0) {
            try {
                foundObjects = searchAllKnownObjects(searchWord);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Should work with search options -> more specific search
            try {
                foundObjects = searchSpecific(searchOptions, searchWord);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Found!
        if (foundObjects.size() > 0) {
            setInfo(foundObjects.size() + " object(s) found!");
            Status().setMessage(foundObjects.size() + " object(s) found!");
            setSearched(true);
            if (objectSearchListener != null) {
                objectSearchListener.onDbObjectFound(foundObjects);
            }
        } else {
            setError("Nothing found..");
        }
    }

    private void setSearched(boolean searched) {
        this.searched = searched;
        if (searched) {
            searchButton.setIcon(resourceManager.readImage("Common.SearchDelete"));
        } else {
            searchButton.setIcon(resourceManager.readImage("Common.Search"));
        }
    }

    private void setError(String error) {
        infoLabel.setForeground(Color.RED);
        infoLabel.setText(error);
        Status().setError(error);
    }

    private void clearLabel() {
        infoLabel.setText("");
    }

    private void setInfo(String info) {
        infoLabel.setForeground(Color.BLACK);
        infoLabel.setText(info);
        Status().setMessage(info);
    }

    private List<DbObject> searchAllKnownObjects(String searchWord) throws SQLException {
        List<DbObject> foundList = new ArrayList<>();

        // Categories
        Status().setMessage("Searching for: Categories");
        foundList.addAll(searchForDbObject(new ArrayList<>(db().getCategories()), searchWord));

        // Products
        Status().setMessage("Searching for: Products");
        foundList.addAll(searchForDbObject(new ArrayList<>(db().getProducts()), searchWord));

        // Types
        Status().setMessage("Searching for: Types");
        foundList.addAll(searchForDbObject(new ArrayList<>(db().getTypes()), searchWord));

        // Orders
        Status().setMessage("Searching for: Orders");
        foundList.addAll(searchForDbObject(new ArrayList<>(db().getOrders()), searchWord));

        // Locations
        Status().setMessage("Searching for: Locations");
        foundList.addAll(searchForDbObject(new ArrayList<>(db().getLocations()), searchWord));

        // Manufacturers
        Status().setMessage("Searching for: Manufacturers");
        foundList.addAll(searchForDbObject(new ArrayList<>(db().getManufacturers()), searchWord));

        // Distributors
        Status().setMessage("Searching for: Distributors");
        foundList.addAll(searchForDbObject(new ArrayList<>(db().getDistributors()), searchWord));

        // Items
        Status().setMessage("Searching for: Items");
        foundList.addAll(searchForDbObject(new ArrayList<>(db().getItems()), searchWord));

        return foundList;
    }

    private List<DbObject> searchSpecific(int[] searchOptions, String searchWord) throws SQLException {
        List<DbObject> foundList = new ArrayList<>();
        for (int type : searchOptions) {
            switch (type) {
                case DbObject.TYPE_CATEGORY:
                    Status().setMessage("Searching for: Categories");
                    foundList.addAll(searchForDbObject(new ArrayList<>(db().getCategories()), searchWord));
                    break;
                case DbObject.TYPE_PRODUCT:
                    Status().setMessage("Searching for: Products");
                    foundList.addAll(searchForDbObject(new ArrayList<>(db().getProducts()), searchWord));
                    break;
                case DbObject.TYPE_TYPE:
                    Status().setMessage("Searching for: Types");
                    foundList.addAll(searchForDbObject(new ArrayList<>(db().getTypes()), searchWord));
                    break;
                case DbObject.TYPE_ORDER:
                    Status().setMessage("Searching for: Orders");
                    foundList.addAll(searchForDbObject(new ArrayList<>(db().getOrders()), searchWord));
                    break;
                case DbObject.TYPE_LOCATION:
                    Status().setMessage("Searching for: Locations");
                    foundList.addAll(searchForDbObject(new ArrayList<>(db().getLocations()), searchWord));
                    break;
                case DbObject.TYPE_MANUFACTURER:
                    Status().setMessage("Searching for: Manufacturers");
                    foundList.addAll(searchForDbObject(new ArrayList<>(db().getManufacturers()), searchWord));
                    break;
                case DbObject.TYPE_DISTRIBUTOR:
                    Status().setMessage("Searching for: Distributors");
                    foundList.addAll(searchForDbObject(new ArrayList<>(db().getDistributors()), searchWord));
                    break;
                case DbObject.TYPE_ITEM:
                    Status().setMessage("Searching for: Items");
                    foundList.addAll(searchForDbObject(new ArrayList<>(db().getItems()), searchWord));
                    break;
                default:
                    break;
            }
        }
        return foundList;
    }

    private List<DbObject> searchForDbObject(List<DbObject> listToSearch, String searchWord) {
        List<DbObject> foundList = new ArrayList<>();
        if (listToSearch == null || listToSearch.size() == 0) {
            return foundList;
        }

        searchWord = searchWord.toUpperCase();

        for (DbObject dbo : listToSearch) {
            if (dbo.hasMatch(searchWord)) {
                foundList.add(dbo);
            }
        }

        return foundList;
    }

    private void createCategoryCb() {
        DefaultComboBoxModel<Category> model = new DefaultComboBoxModel<>();
        for (Category c : DbManager.db().getCategories()) {
            if (!c.isUnknown()) {
                model.addElement(c);
            }
        }
        advancedCategoryCb = new JComboBox<>(model);
        advancedCategoryCb.insertItemAt(null, 0);
        advancedCategoryCb.setSelectedIndex(0);
        advancedCategoryCb.addActionListener(e -> {
            JComboBox jbc = (JComboBox) e.getSource();
            Category c = (Category) jbc.getSelectedItem();
            if (c == null) {
                advancedProductCb.setEnabled(false);
                advancedTypeCb.setEnabled(false);
            } else {
                advancedProductCb.setEnabled(true);
                updateProductCb(c);
            }
        });
    }

    private void createProductCb() {
        productCbModel = new DefaultComboBoxModel<>();
        advancedProductCb = new JComboBox<>(productCbModel);
        advancedProductCb.insertItemAt(null, 0);
        advancedProductCb.setEnabled(false);
        advancedProductCb.addActionListener(e -> {
            JComboBox jbc = (JComboBox) e.getSource();
            Product p = (Product) jbc.getSelectedItem();
            if (p == null) {
                advancedTypeCb.setEnabled(false);
            } else {
                advancedTypeCb.setEnabled(true);
                updateTypeCb(p);
            }
        });
    }

    private void createTypeCb() {
        typeCbModel = new DefaultComboBoxModel<>();
        advancedTypeCb = new JComboBox<>(typeCbModel);
        advancedTypeCb.setEnabled(false);
    }

    private void updateProductCb(Category category) {
        productCbModel.removeAllElements();
        for (Product p : DbManager.db().getProductListForCategory(category.getId())) {
            if (!p.isUnknown()) {
                productCbModel.addElement(p);
            }
        }
        advancedProductCb.insertItemAt(null, 0);
        advancedProductCb.setSelectedIndex(0);
    }

    private void updateTypeCb(Product product) {
        typeCbModel.removeAllElements();
        for (Type t : DbManager.db().getTypeListForProduct(product.getId())) {
            if (!t.isUnknown()) {
                typeCbModel.addElement(t);
            }
        }
        advancedTypeCb.insertItemAt(null, 0);
        advancedTypeCb.setSelectedIndex(0);
    }

    private void setAdvancedSearch(boolean advancedSearch) {
        inAdvanced = advancedSearch;
        advancedToolBar.setVisible(advancedSearch);
        Font f = advancedLabel.getFont();
        if (advancedSearch) {
            Font f1 = new Font(f.getName(), Font.ITALIC, f.getSize());
            advancedLabel.setFont(f1);
        } else {
            Font f1 = new Font(f.getName(), Font.PLAIN, f.getSize());
            advancedLabel.setFont(f1);
        }
    }

    private JPanel createAdvancedPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,10);

        // Categories
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(advancedCategoryCb, gbc);

        // Products
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(advancedProductCb, gbc);

        // Types
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(advancedTypeCb, gbc);

        // Checkbox
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(advancedCheckbox1, gbc);

        // Checkbox
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(advancedCheckbox2, gbc);

        // Checkox
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(advancedCheckbox3, gbc);

        return panel;
    }

    @Override
    public void initializeComponents() {
        // Search text field
        searchField = new ITextField("Search");
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                clearSearch();
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
            }
        });
        searchField.addActionListener(e -> {
            String searchWord = searchField.getText();
            if (searchWord == null || searchWord.isEmpty()) {
                setError("No input..");
            } else {
                clearLabel();
                search(searchWord);
            }
        });

        // Search button
        searchButton = new JButton(resourceManager.readImage("Common.Search"));
        searchButton.addActionListener(e -> {
            if (searched) {
                clearSearch();
            } else {
                String searchWord = searchField.getText();
                if (searchWord == null || searchWord.isEmpty()) {
                    setError("No input..");
                } else {
                    clearLabel();
                    search(searchWord);
                }
            }
        });

        // Advanced label
        advancedLabel = new ILabel("Advanced");
        Font f = advancedLabel.getFont();
        advancedLabel.setFont(new Font(f.getName(), f.getStyle(), 12));
        advancedLabel.setForeground(Color.blue);
        advancedLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setAdvancedSearch(!inAdvanced);
            }
        });

        // Info label
        infoLabel = new ILabel();
        infoLabel.setFont(new Font(f.getName(), f.getStyle(), 12));

        // Advanced toolbar
        advancedToolBar = new JToolBar();
        createCategoryCb();
        createProductCb();
        createTypeCb();
        advancedCheckbox1 = new JCheckBox("Checkbox 1: ");
        advancedCheckbox2 = new JCheckBox("Checkbox 2: ");
        advancedCheckbox3 = new JCheckBox("Checkbox 3: ");
        advancedToolBar.add(createAdvancedPanel());
    }


    @Override
    public void initializeLayouts() {

        setOpaque(false);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(searchField, gbc);

        gbc.gridx = 1; gbc.weightx = 0.1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(searchButton, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(infoLabel, gbc);

        if (hasAdvancedSearchOption) {
            gbc.gridx = 1; gbc.weightx = 0;
            gbc.gridy = 1; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(advancedLabel, gbc);

            advancedToolBar.setVisible(false);
            gbc.gridx = 0; gbc.weightx = 1;
            gbc.gridy = 2; gbc.weighty = 0.5;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridwidth = 2;
            add(advancedToolBar, gbc);
        }

    }

    @Override
    public void updateComponents(Object object) {}

    @Override
    public void setEnabled(boolean enabled) {
            searchField.setEnabled(enabled);
            searchButton.setEnabled(enabled);
            infoLabel.setEnabled(enabled);
            advancedLabel.setEnabled(enabled);
    }
}
