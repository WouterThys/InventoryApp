package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;
import static com.waldo.inventory.database.DbManager.dbInstance;

public class IObjectSearchPanel extends JPanel implements GuiInterface {

    private ResourceManager resourceManager;
    private IObjectSearchListener objectSearchListener;

    private ITextField searchField;
    private JButton searchButton;
    private ILabel infoLabel;
    private ILabel advancedLabel;

    private boolean searched = false;
    private boolean hasAdvancedSearchOption;
    private int[] searchOptions;

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
    }

    private void clearLabel() {
        infoLabel.setText("");
    }

    private void setInfo(String info) {
        infoLabel.setForeground(Color.BLACK);
        infoLabel.setText(info);
    }

    private List<DbObject> searchAllKnownObjects(String searchWord) throws SQLException {
        List<DbObject> foundList = new ArrayList<>();

        // Categories
        foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getCategories()), searchWord));

        // Products
        foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getProducts()), searchWord));

        // Types
        foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getTypes()), searchWord));

        // Orders
        foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getOrders()), searchWord));

        // Locations
        foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getLocations()), searchWord));

        // Manufacturers
        foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getManufacturers()), searchWord));

        // Items
        foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getItems()), searchWord));

        return foundList;
    }

    private List<DbObject> searchSpecific(int[] searchOptions, String searchWord) throws SQLException {
        List<DbObject> foundList = new ArrayList<>();
        for (int type : searchOptions) {
            switch (type) {
                case DbObject.TYPE_CATEGORY:
                    foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getCategories()), searchWord));
                    break;
                case DbObject.TYPE_PRODUCT:
                    foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getProducts()), searchWord));
                    break;
                case DbObject.TYPE_TYPE:
                    foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getTypes()), searchWord));
                    break;
                case DbObject.TYPE_ORDER:
                    foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getOrders()), searchWord));
                    break;
                case DbObject.TYPE_LOCATION:
                    foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getLocations()), searchWord));
                    break;
                case DbObject.TYPE_MANUFACTURER:
                    foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getManufacturers()), searchWord));
                    break;
                case DbObject.TYPE_ITEM:
                    foundList.addAll(searchForDbObject(new ArrayList<>(dbInstance().getItems()), searchWord));
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
                super.mouseClicked(e);
                // Open advanced dialog
            }
        });

        // Info label
        infoLabel = new ILabel();
        infoLabel.setFont(new Font(f.getName(), f.getStyle(), 12));
    }

    @Override
    public void initializeLayouts() {

        setOpaque(false);
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = createFieldConstraints(0,0);
        constraints.gridwidth = 1;
        add(searchField, constraints);

        constraints = createFieldConstraints(1,0);
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        add(searchButton, constraints);

        constraints = createFieldConstraints(0,1);
        add(infoLabel, constraints);

        if (hasAdvancedSearchOption) {
            constraints = createFieldConstraints(1, 1);
            add(advancedLabel, constraints);
        }
    }

    @Override
    public void updateComponents(Object object) {

    }
}
