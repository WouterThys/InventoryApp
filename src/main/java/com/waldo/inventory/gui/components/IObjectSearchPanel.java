package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.classes.Type;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.gui.GuiInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class IObjectSearchPanel extends JPanel implements GuiInterface {

    private SearchManager searchManager;
    private IObjectSearchListener objectSearchListener;
    private IObjectSearchBtnListener objectSearchBtnListener;

    private ITextField searchField;
    private JButton searchButton;
    private ILabel infoLabel;
    private ILabel advancedLabel;
    private JPanel btnPanel;
    private IImageButton previousBtn;
    private IImageButton nextBtn;

    private JToolBar advancedToolBar;
    private JComboBox<Category> advancedCategoryCb;
    private DefaultComboBoxModel<Product> productCbModel;
    private JComboBox<Product> advancedProductCb;
    private DefaultComboBoxModel<Type> typeCbModel;
    private JComboBox<Type> advancedTypeCb;
    private JCheckBox advancedCheckbox1;
    private JCheckBox advancedCheckbox2;
    private JCheckBox advancedCheckbox3;

    private IObjectSearchPanel(boolean hasAdvancedSearchOption) {
        searchManager = new SearchManager();
        searchManager.setHasAdvancedSearchOption(hasAdvancedSearchOption);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    public IObjectSearchPanel(boolean hasAdvancedSearchOption, int... searchOptions) {
        this(hasAdvancedSearchOption);
        searchManager.setSearchOptions(searchOptions);
    }

    public void addSearchListener(IObjectSearchListener listener) {
        this.objectSearchListener = listener;
    }

    public void addSearchBtnListener(IObjectSearchBtnListener listener) {
        this.objectSearchBtnListener = listener;
    }

    public void removeSearchListener() {
        this.objectSearchListener = null;
    }

    public interface IObjectSearchListener {
        void onDbObjectFound(java.util.List<DbObject> foundObjects);
        void onSearchCleared();
    }

    public interface IObjectSearchBtnListener {
        void nextSearchObject(DbObject next);
        void previousSearchObject(DbObject previous);
    }

    public void setSearchOptions(int... options) {
        searchManager.setSearchOptions(options);
    }

    public void setSearchList(List<DbObject> searchList) {
        searchManager.setSearchList(searchList);
    }

    public void clearSearch() {
        searchManager.clearSearch();
        setSearched(false);
        clearLabel();

        btnPanel.setVisible(false);

        if (objectSearchListener != null) {
            objectSearchListener.onSearchCleared();
        }
    }

    public void search(String searchWord) {
        List<DbObject> foundObjects = searchManager.search(searchWord);

        // Found!
        if (foundObjects.size() > 0) {
            setInfo(foundObjects.size() + " object(s) found!");
            Status().setMessage(foundObjects.size() + " object(s) found!");

            if (objectSearchBtnListener != null && foundObjects.size() > 1) {
                btnPanel.setVisible(true);
            }

            setSearched(true);
            if (objectSearchListener != null) {
                objectSearchListener.onDbObjectFound(foundObjects);
            }
        } else {
            setError("Nothing found..");
        }
    }

    private void setSearched(boolean searched) {
        searchManager.setSearched(searched);
        if (searched) {
            searchButton.setIcon(imageResource.readImage("Common.SearchDelete"));
        } else {
            searchButton.setIcon(imageResource.readImage("Common.Search"));
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
        searchManager.setInAdvanced(advancedSearch);
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

    private void getNextFoundItem() {
        if (objectSearchBtnListener != null && searchManager.getResultList().size() > 1) {
            objectSearchBtnListener.previousSearchObject(searchManager.getPreviousFoundObject());
        }
    }

    private void getPreviousFoundItem() {
        if (objectSearchBtnListener != null && searchManager.getResultList().size() > 1) {
            objectSearchBtnListener.nextSearchObject(searchManager.getNextFoundObject());
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
        searchButton = new JButton(imageResource.readImage("Common.Search"));
        searchButton.addActionListener(e -> {
            if (searchManager.isSearched()) {
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
                setAdvancedSearch(!searchManager.isInAdvanced());
            }
        });

        // Info label
        infoLabel = new ILabel();
        infoLabel.setFont(new Font(f.getName(), f.getStyle(), 12));

        // Next and prev buttons
        btnPanel = new JPanel();
        btnPanel.setVisible(false);
        previousBtn = new IImageButton(
                imageResource.readImage("Common.ArrowRight"),
                imageResource.readImage("Common.ArrowRightActive"),
                imageResource.readImage("Common.ArrowRightActive"),
                imageResource.readImage("Common.ArrowRightDisabled"));
        previousBtn.setBorder(BorderFactory.createEmptyBorder());
        previousBtn.setContentAreaFilled(false);
        nextBtn = new IImageButton(
                imageResource.readImage("Common.ArrowLeft"),
                imageResource.readImage("Common.ArrowLeftActive"),
                imageResource.readImage("Common.ArrowLeftActive"),
                imageResource.readImage("Common.ArrowLeftDisabled"));
        nextBtn.setBorder(BorderFactory.createEmptyBorder());
        nextBtn.setContentAreaFilled(false);

        previousBtn.addActionListener(e -> getPreviousFoundItem());
        nextBtn.addActionListener(e -> getNextFoundItem());

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

        btnPanel.add(nextBtn);
        btnPanel.add(previousBtn);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(btnPanel, gbc);

        if (searchManager.isHasAdvancedSearchOption()) {
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

    public void setSearchFieldText(String text) {
        if (searchField != null) {
            searchField.setText(text);
        }
    }
}
