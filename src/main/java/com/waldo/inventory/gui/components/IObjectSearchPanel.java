package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.search.Search;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class IObjectSearchPanel<T extends DbObject> extends JPanel implements GuiUtils.GuiInterface, Search.SearchListener<T> {

    private final Search.DbObjectSearch<T> searchManager;

    private ITextField searchField;
    private JButton searchButton;
    private ILabel infoLabel;
    private JPanel btnPanel;
    private IImageButton previousBtn;
    private IImageButton nextBtn;

    private Search.SearchListener<T> searchListener;

    public IObjectSearchPanel(List<T> searchList, Search.SearchListener<T> searchListener) {
        this.searchManager = new Search.DbObjectSearch<>(searchList, this);
        this.searchListener = searchListener;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public IObjectSearchPanel(List<T> searchList) {
        this(searchList, null);
    }

    public void setSearchList(List<T> searchList) {
        searchManager.setSearchList(searchList);
    }

    public void addSearchListener(Search.SearchListener<T> searchListener) {
        this.searchListener = searchListener;
    }

    public void clearSearch() {
        setSearched(false);
        clearLabel();
        searchField.setText("");
        btnPanel.setVisible(false);

        searchManager.clearSearch();
    }

    public void search(String searchWord) {
        searchManager.search(searchWord);
    }

    private void setSearched(boolean searched) {
        searchManager.setSearched(searched);
        if (searched) {
            searchButton.setIcon(imageResource.readImage("Search.RemoveSearch"));
        } else {
            searchButton.setIcon(imageResource.readImage("Search.Search"));
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
        searchButton = new JButton(imageResource.readImage("Search.Search"));
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

        // Info label
        infoLabel = new ILabel();
        infoLabel.setFontSize(12);

        // Next and prev buttons
        btnPanel = new JPanel();
        btnPanel.setVisible(false);
        previousBtn = new IImageButton(
                imageResource.readImage("Search.ArrowRightBlue"),
                imageResource.readImage("Search.ArrowRightGreen"),
                imageResource.readImage("Search.ArrowRightGreen"),
                imageResource.readImage("Search.ArrowRightGray"));
        previousBtn.setBorder(BorderFactory.createEmptyBorder());
        previousBtn.setContentAreaFilled(false);
        nextBtn = new IImageButton(
                imageResource.readImage("Search.ArrowLeftBlue"),
                imageResource.readImage("Search.ArrowLeftGreen"),
                imageResource.readImage("Search.ArrowLeftGreen"),
                imageResource.readImage("Search.ArrowLeftGray"));
        nextBtn.setBorder(BorderFactory.createEmptyBorder());
        nextBtn.setContentAreaFilled(false);

        previousBtn.addActionListener(e -> searchManager.findPreviousObject());
        nextBtn.addActionListener(e -> searchManager.findNextObject());
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

    }

    @Override
    public void updateComponents(Object... object) {}

    @Override
    public void setEnabled(boolean enabled) {
            searchField.setEnabled(enabled);
            searchButton.setEnabled(enabled);
            infoLabel.setEnabled(enabled);
    }

    @Override
    public void onObjectsFound(List<T> foundObjects) {
        if (foundObjects.size() > 0) {
            if (foundObjects.size() == 1) {
                setInfo("1 object found!");
            } else {
                setInfo(foundObjects.size() + " object(s) found!");
            }

            if (foundObjects.size() > 1) {
                btnPanel.setVisible(true);
            }

            setSearched(true);
        } else {
            setError("Nothing found..");
        }
        if (searchListener != null) {
            searchListener.onObjectsFound(foundObjects);
        }
    }

    @Override
    public void onSearchCleared() {
        if (searchListener != null) {
            searchListener.onSearchCleared();
        }
    }

    @Override
    public void onNextSearchObject(T next) {
        if (searchListener != null) {
            searchListener.onNextSearchObject(next);
        }
    }

    @Override
    public void onPreviousSearchObject(T previous) {
        if (searchListener != null) {
            searchListener.onPreviousSearchObject(previous);
        }
    }
}
