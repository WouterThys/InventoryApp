package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.search.ObjectMatch;
import com.waldo.inventory.classes.search.SearchMatch;
import com.waldo.utils.icomponents.IImageButton;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class IObjectSearchPanel<T extends DbObject> extends JPanel implements GuiUtils.GuiInterface {

    private static final ImageIcon removeSearchIcon = imageResource.readImage("Search.RemoveSearch");
    private static final ImageIcon searchIcon = imageResource.readImage("Search.Search");

    public interface SearchListener<T extends DbObject> {
        void onObjectsFound(List<T> foundObjects);
        void onNextObjectSelected(T next);
        void onPreviousObjectSelected(T previous);
        void onSearchCleared();
    }

    private ITextField searchField;
    private JButton searchButton;
    private ILabel infoLabel;
    private JPanel btnPanel;
    private IImageButton previousBtn;
    private IImageButton nextBtn;

    private SearchListener<T> searchListener;
    private List<T> searchList;
    private List<T> foundList = new ArrayList<>();
    private T currentObject;

    private boolean immediateSearch = false;

    public IObjectSearchPanel(List<T> searchList) {
        this(searchList, null);
    }

    public IObjectSearchPanel(List<T> searchList, SearchListener<T> searchListener) {
        this(searchList, searchListener, false);
    }

    public IObjectSearchPanel(List<T> searchList, SearchListener<T> searchListener, boolean immediateSearch) {
        super();

        this.searchList = searchList;
        this.searchListener = searchListener;
        this.immediateSearch = immediateSearch;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }


    public void setSearchList(List<T> searchList) {
        this.searchList = searchList;
    }

    public void addSearchListener(SearchListener<T> searchListener) {
        this.searchListener = searchListener;
    }

    private void sendObjectsFound(List<T> foundList) {
        if (searchListener != null) {
            searchListener.onObjectsFound(foundList);
        }
    }

    private void sendNextObject(T next) {
        if (searchListener != null) {
            searchListener.onNextObjectSelected(next);
        }
    }

    private void sendPreviousObject(T previous) {
        if (searchListener != null) {
            searchListener.onPreviousObjectSelected(previous);
        }
    }

    private void sendSearchCleared() {
        if (searchListener != null) {
            searchListener.onSearchCleared();
        }
    }

    public void clearSearch() {
        setSearched(false);
        clearLabel();
        searchField.setText("");
        btnPanel.setVisible(false);

        sendSearchCleared();
    }

    public void search(String searchWord) {
        if (searchWord == null || searchWord.isEmpty()) {
            clearSearch();
            return;
        }
        if (searchList.size() > 0) {
            setSearched(false);
            clearLabel();
            btnPanel.setVisible(false);

            SwingUtilities.invokeLater(() -> {
                List<ObjectMatch<T>> foundObjects = new ArrayList<>();
                for (T t : searchList) {
                    List<SearchMatch> matches = t.searchByKeyWord(searchWord);
                    if (matches != null && matches.size() > 0) {
                        ObjectMatch<T> objectMatch = new ObjectMatch<>(t, matches);
                        foundObjects.add(objectMatch);
                    }
                }

                if (foundObjects.size() > 0) {
                    if (foundObjects.size() == 1) {
                        setInfo("1 object found!");
                    } else {
                        setInfo(foundObjects.size() + " object(s) found!");
                        btnPanel.setVisible(true);

                        foundObjects.sort(new ComparatorUtils.FoundMatchComparator());
                    }

                    foundList.clear();
                    for (ObjectMatch<T> om : foundObjects) {
                        foundList.add(om.getFoundObject());
                    }
                    sendObjectsFound(foundList);
                    setSearched(true);
                } else {
                    sendObjectsFound(null);
                    setError("Nothing found..");
                }
            });
        } else {
            setError("No data to search..");
        }
    }

    public T getCurrentObject() {
        return currentObject;
    }

    public void setCurrentObject(T object) {
        this.currentObject = object;
    }

    private T findPreviousObject() {
        T previous = null;
        if (foundList.size() > 0) {
            if (currentObject == null) {
                previous = foundList.get(0);
            } else {
                int ndx = foundList.indexOf(currentObject);
                if (ndx < 1) {
                    previous = foundList.get(foundList.size()-1);
                } else {
                    ndx--;
                    previous = foundList.get(ndx);
                }
            }
        }
        return previous;
    }

    private T findNextObject() {
        T next = null;
        if (foundList.size() > 0) {
            if (currentObject == null) {
                next = foundList.get(0);
            } else {
                int ndx = foundList.indexOf(currentObject);
                if (ndx >= foundList.size()-1) {
                    next = foundList.get(0);
                } else {
                    ndx++;
                    next = foundList.get(ndx);
                }
            }
        }
        return next;
    }

    private void setSearched(boolean searched) {
        if (searched) {
            searchButton.setIcon(removeSearchIcon);
        } else {
            foundList.clear();
            searchButton.setIcon(searchIcon);
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
        if (immediateSearch) {
            searchField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    search(searchField.getText());
                }
            });
        }

        // Search button
        searchButton = new JButton(imageResource.readImage("Search.Search"));
        searchButton.addActionListener(e -> {
            if (foundList.size() > 0) {
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
        nextBtn = new IImageButton(
                imageResource.readImage("Search.ArrowRightBlue"),
                imageResource.readImage("Search.ArrowRightGreen"),
                imageResource.readImage("Search.ArrowRightGreen"),
                imageResource.readImage("Search.ArrowRightGray"));
        nextBtn.setBorder(BorderFactory.createEmptyBorder());
        nextBtn.setContentAreaFilled(false);
        previousBtn = new IImageButton(
                imageResource.readImage("Search.ArrowLeftBlue"),
                imageResource.readImage("Search.ArrowLeftGreen"),
                imageResource.readImage("Search.ArrowLeftGreen"),
                imageResource.readImage("Search.ArrowLeftGray"));
        previousBtn.setBorder(BorderFactory.createEmptyBorder());
        previousBtn.setContentAreaFilled(false);

        previousBtn.addActionListener(e -> {
            T previous = findPreviousObject();
            setCurrentObject(previous);
            sendPreviousObject(previous);
        });
        nextBtn.addActionListener(e -> {
            T next = findNextObject();
            setCurrentObject(next);
            sendNextObject(next);
        });
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

        btnPanel.add(previousBtn);
        btnPanel.add(nextBtn);
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
}
