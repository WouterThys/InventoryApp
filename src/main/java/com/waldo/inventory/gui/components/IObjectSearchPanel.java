package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.search.ObjectMatch;
import com.waldo.inventory.classes.search.SearchMatch;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.utils.icomponents.IImageButton;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class IObjectSearchPanel<T extends DbObject> extends JPanel implements GuiUtils.GuiInterface {

    private static final ImageIcon removeSearchIcon = imageResource.readIcon("Search.Delete.SS");
    private static final ImageIcon searchIcon = imageResource.readIcon("Search.SS");

    public interface SearchListener<T extends DbObject> {
        void onObjectsFound(List<T> foundObjects);
        void onNextObjectSelected(T next);
        void onPreviousObjectSelected(T previous);
        void onSearchCleared();
    }

    private ITextField searchField;
    private IActions.SearchAction searchAction;
    private ILabel infoLabel;
    private JPanel btnPanel;
    private IImageButton previousBtn;
    private IImageButton nextBtn;

    private SearchListener<T> searchListener;
    private List<T> searchList;
    private List<ObjectMatch<T>> result = new ArrayList<>();
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

    public void setSearchText(String searchText, boolean doSearch) {
        searchField.setText(searchText);
        if (doSearch) {
            search(searchText);
        }
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

    protected List<ObjectMatch<T>> doSearch(String searchWord) {
        List<ObjectMatch<T>> foundObjects = new ArrayList<>();
        for (T t : searchList) {
            List<SearchMatch> matches = t.searchByKeyWord(searchWord);
            if (matches != null && matches.size() > 0) {
                ObjectMatch<T> objectMatch = new ObjectMatch<>(t, matches);
                foundObjects.add(objectMatch);
            }
        }
        return foundObjects;
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
                result.clear();
                result = doSearch(searchWord);

                if (result.size() > 0) {
                    if (result.size() == 1) {
                        setInfo("1 object found!");
                    } else {
                        setInfo(result.size() + " object(s) found!");
                        btnPanel.setVisible(true);

                        result.sort(new ComparatorUtils.FoundMatchComparator());
                    }

                    foundList.clear();
                    for (ObjectMatch<T> om : result) {
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

    public List<ObjectMatch<T>> getResult() {
        return result;
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
            searchAction.setIcon(removeSearchIcon);
        } else {
            foundList.clear();
            searchAction.setIcon(searchIcon);
        }
    }

    private void setError(String error) {
        infoLabel.setForeground(Color.RED);
        infoLabel.setText(error);
        //Status().setError(error);
    }

    private void clearLabel() {
        infoLabel.setText("");
    }

    private void setInfo(String info) {
        infoLabel.setForeground(Color.BLACK);
        infoLabel.setText(info);
        //Status().setMessage(info);
    }

    @Override
    public void initializeComponents() {
        // Search text field
        searchField = new ITextField("Search");
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

        // Search action
        searchAction = new IActions.SearchAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        };

        // Info label
        infoLabel = new ILabel();
        infoLabel.setFontSize(12);

        // Next and prev buttons
        btnPanel = new JPanel();
        btnPanel.setVisible(false);
        nextBtn = new IImageButton(
                imageResource.readIcon("Arrow.Right.Blue.SS"),
                imageResource.readIcon("Arrow.Right.Green.SS"),
                imageResource.readIcon("Arrow.Right.Green.SS"),
                imageResource.readIcon("Arrow.Right.Gray.SS"));
        nextBtn.setBorder(BorderFactory.createEmptyBorder());
        nextBtn.setContentAreaFilled(false);
        previousBtn = new IImageButton(
                imageResource.readIcon("Arrow.Left.Blue.SS"),
                imageResource.readIcon("Arrow.Left.Green.SS"),
                imageResource.readIcon("Arrow.Left.Green.SS"),
                imageResource.readIcon("Arrow.Left.Gray.SS"));
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
        btnPanel.add(previousBtn);
        btnPanel.add(nextBtn);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(GuiUtils.createComponentWithActions(searchField, searchAction));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(infoLabel, BorderLayout.WEST);
        infoPanel.add(btnPanel, BorderLayout.EAST);

       setLayout(new BorderLayout());
       add(searchPanel, BorderLayout.CENTER);
       add(infoPanel, BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object... object) {}

    @Override
    public void setEnabled(boolean enabled) {
            searchField.setEnabled(enabled);
            searchAction.setEnabled(enabled);
            infoLabel.setEnabled(enabled);
    }
}
