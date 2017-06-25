package com.waldo.inventory.gui.dialogs.importfromcsvdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class TableObjectPanel extends JPanel implements
        GuiInterface,
        IObjectSearchPanel.IObjectSearchListener,
        ListSelectionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel leftPanel, rightPanel;

    private ITextField objectReferenceTf;
    private IObjectSearchPanel searchPanel;

    private ITable itemTable;
    private IItemTableModel tableModel;

    private ITextField foundItemNameTf;
    private ITextArea foundItemDescriptionTa;
    private JButton foundItemOrderBtn;
    private JButton foundItemSelectBtn;

    private IdBToolBar toolBar;

    public interface IItemSelectedListener {
        void onItemSelected(TableObject tableObject, Item item);
        void onOrderPressed();
    }
    private IItemSelectedListener selectedListener;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Item foundItem;
    private TableObject tableObject;
    private Application application;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public TableObjectPanel(IItemSelectedListener selectedListener, Application application) {
        this(selectedListener, application, null);
    }

    public TableObjectPanel(IItemSelectedListener selectedListener, Application application, TableObject firstObject) {
        this.selectedListener = selectedListener;
        this.application = application;
        initializeComponents();
        initializeLayouts();
        updateComponents(firstObject);
    }


    /*
     *                  PUBLIC METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void updateEnabledComponents() {
        if (foundItem != null && tableObject.isValid()) {
            foundItemOrderBtn.setEnabled(true);
            toolBar.setDeleteActionEnabled(true);
        } else {
            foundItemOrderBtn.setEnabled(false);
            toolBar.setDeleteActionEnabled(false);
        }
        toolBar.setEditActionEnabled(foundItem != null);
        foundItemSelectBtn.setEnabled(foundItem != null);
    }

    public void updateVisibleComponents() {

    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void createLeftPanel() {

        // Labels
        ILabel refLabel = new ILabel("Item reference: ");
        refLabel.setHorizontalAlignment(ILabel.RIGHT);
        refLabel.setVerticalAlignment(ILabel.CENTER);
        ILabel itemLabel = new ILabel("Item name: ");
        itemLabel.setHorizontalAlignment(ILabel.RIGHT);
        itemLabel.setVerticalAlignment(ILabel.CENTER);
        ILabel descLabel = new ILabel("Item description: ");
        descLabel.setHorizontalAlignment(ILabel.RIGHT);
        descLabel.setVerticalAlignment(ILabel.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(refLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(objectReferenceTf, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(itemLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(foundItemNameTf, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 4; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(descLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 5; gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(new JScrollPane(foundItemDescriptionTa), gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 6; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(new JScrollPane(foundItemSelectBtn), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 6; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        leftPanel.add(new JScrollPane(foundItemOrderBtn), gbc);


        leftPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    }

    private void createRightPanel() {

        JPanel top = new JPanel(new BorderLayout());
        top.add(searchPanel, BorderLayout.EAST);
        top.add(toolBar, BorderLayout.WEST);


        rightPanel.add(top, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        rightPanel.setPreferredSize(new Dimension(500,100));

    }

    private ActionListener selectFoundItem() {
        return e -> {
            if (foundItem != null && tableObject != null) {
                selectedListener.onItemSelected(tableObject, foundItem);
            }
        };
    }

    private ActionListener orderFoundItem() {
        return e -> {
            selectedListener.onOrderPressed();
        };
    }

    private void updateFoundItem(Item item) {
        this.foundItem = item;

        if (foundItem != null) {
            foundItemNameTf.setText(foundItem.getName());
            foundItemDescriptionTa.setText(foundItem.getDescription());
        } else {
            foundItemNameTf.setText("");
            foundItemDescriptionTa.setText("");
        }
        updateEnabledComponents();
    }

    private IdBToolBar.IdbToolBarListener toolbarListener() {
        return new IdBToolBar.IdbToolBarListener() {

            @Override
            public void onToolBarRefresh() {
                //
            }

            @Override
            public void onToolBarAdd() {
                EditItemDialog dialog = new EditItemDialog(application, "Add item");
                if (dialog.showDialog() == IDialog.OK) {
                    foundItem = dialog.getItem();
                    searchPanel.search(foundItem.getName());
                }
            }

            @Override
            public void onToolBarDelete() {
                if (tableObject != null && tableObject.isValid()) {
                    foundItem = null;
                    selectedListener.onItemSelected(tableObject, null);
                }
            }

            @Override
            public void onToolBarEdit() {
                if (tableObject != null && tableObject.isValid()) {
                    EditItemDialog dialog = new EditItemDialog(application, "Edit item", tableObject.getItem());
                    if (dialog.showDialog() == IDialog.OK) {
                        foundItem = dialog.getItem();
                        searchPanel.search(foundItem.getName());
                    }
                }
            }
        };
    }

    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //
    // Gui
    //
    @Override
    public void initializeComponents() {
        // Panels
        leftPanel = new JPanel(new GridBagLayout());
        //cardLayout = new CardLayout();
        rightPanel = new JPanel(new BorderLayout());

        // Left
        objectReferenceTf = new ITextField("Object reference");
        objectReferenceTf.setEnabled(false);

        foundItemNameTf = new ITextField("Item name");
        foundItemDescriptionTa = new ITextArea("Item description");
        foundItemDescriptionTa.setLineWrap(true); // Go to next line when area is full
        foundItemDescriptionTa.setWrapStyleWord(true); // Don't cut words in two
        foundItemOrderBtn = new JButton("Order");
        foundItemOrderBtn.addActionListener(orderFoundItem());
        foundItemSelectBtn = new JButton("Select");
        foundItemSelectBtn.addActionListener(selectFoundItem());

        // Right
        tableModel = new IItemTableModel();
        itemTable = new ITable(tableModel);
        itemTable.getSelectionModel().addListSelectionListener(this);
        itemTable.addMouseListener(mouseDoubleClicked());
        itemTable.setAutoResizeMode(ITable.AUTO_RESIZE_ALL_COLUMNS);
        itemTable.setDefaultRenderer(ILabel.class, new ITableEditors.AmountRenderer());

        searchPanel = new IObjectSearchPanel(false, DbObject.TYPE_ITEM);
        searchPanel.addSearchListener(this);

        toolBar = new IdBToolBar(toolbarListener());
        toolBar.setFloatable(false);

    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        createLeftPanel();
        add(leftPanel, BorderLayout.WEST);
        createRightPanel();
        add(rightPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        tableObject = (TableObject) object;

        if (tableObject != null) {
            searchPanel.clearSearch();
            objectReferenceTf.setText(tableObject.getItemReference());
            searchPanel.setSearchFieldText(tableObject.getItemReference());

            if (tableObject.isValid()) {
                updateFoundItem(tableObject.getItem());
            } else {
                updateFoundItem(null);
            }

            searchPanel.search(tableObject.getItemReference());
        }
        updateEnabledComponents();
    }

    //
    // Search
    //
    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        List<Item> foundItems = new ArrayList<>(foundObjects.size());
        for (DbObject object : foundObjects) {
            foundItems.add((Item) object);
        }
        tableModel.setItemList(foundItems);

        if (foundItems.size() == 1) {
            updateFoundItem(foundItems.get(0));
        }
    }

    @Override
    public void onSearchCleared() {
        tableModel.setItemList(new ArrayList<>());
    }

    //
    // Search result item selected
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = itemTable.getSelectedRow();
            Item item;
            if (row >= 0) {
                item = tableModel.getItem(row);
            } else {
                item = null;
            }
            updateFoundItem(item);
        }
    }

    //
    // Mouse double clicked in table
    //
    private MouseListener mouseDoubleClicked() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (foundItem != null) {
                        EditItemDialog dialog = new EditItemDialog(application, "Edit item", foundItem);
                        if (dialog.showDialog() == IDialog.OK) {
                            foundItem = dialog.getItem();
                            searchPanel.search(foundItem.getName());
                        }
                    }
                }
            }
        };
    }

}
