package com.waldo.inventory.gui.dialogs;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.classes.Type;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialogPanel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static com.waldo.inventory.database.DbManager.dbInstance;
import static javax.swing.SpringLayout.*;

public class SubDivisionsDialog extends IDialogPanel {

    public static void showDialog(Application parent) {
        SubDivisionsDialog.application = parent;
        dialog = new JDialog(parent, "Sub Divisions", true);
        dialog.getContentPane().add(new SubDivisionsDialog());
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(parent);
        //dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
    }

    private JList<String> subDivisionList;
    private DefaultListModel<DbObject> detailListModel;
    private JList<DbObject> detailList;
    private JToolBar toolBar;
    private ITextField searchField;
    private JButton searchButton;
    private ITitledPanel detailsPanel;
    private DefaultComboBoxModel<DbObject> selectionCbModel;
    private JComboBox<DbObject> selectionComboBox;
    private JLabel selectionLabel;
    private JLabel iconLabel;

    private Action addAction;
    private Action deleteAction;
    private Action editAction;
    private Action searchAction;

    private int selectedSubNdx = 0;
    private DbObject selectedObject;

    private SubDivisionsDialog() {
        super();
        initActions();
        initComponents();
        initLayouts();
    }

    private void initActions() {
        addAction = new AbstractAction("Add", resourceManager.readImage("SubDivisionDialog.AddIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                    switch (selectedSubNdx) {
                        case 0: // Categories
                            Category c = (Category) AddNewSubDivisionDialog.showDialog(application, selectedSubNdx);
                            if (c != null) {
                                try {
                                    c.save(DbManager.dbInstance());
                                    updateDetailListModel(selectedSubNdx, c.getId());
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            break;

                        case 1: // Products
                            Product p = (Product) AddNewSubDivisionDialog.showDialog(application, selectedSubNdx);
                            if (p != null) {
                                try {
                                    p.setCategoryId(((DbObject)selectionCbModel.getSelectedItem()).getId());
                                    p.save(DbManager.dbInstance());
                                    updateDetailListModel(selectedSubNdx, p.getCategoryId());
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            break;

                        case 2: // Types
                            Type t = (Type) AddNewSubDivisionDialog.showDialog(application, selectedSubNdx);
                            if (t != null) {
                                try {
                                    t.setProductId(((DbObject)selectionCbModel.getSelectedItem()).getId());
                                    t.save(DbManager.dbInstance());
                                    updateDetailListModel(selectedSubNdx, t.getProductId());
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            break;
                    }

            }
        };

        deleteAction = new AbstractAction("Delete", resourceManager.readImage("SubDivisionDialog.DeleteIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedObject != null) {
                    if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(SubDivisionsDialog.this,
                            "Are you sure you want to delete "+selectedObject.getName()+"?",
                            "Delete",
                            JOptionPane.YES_NO_OPTION)) {

                        try {
                            selectedObject.delete();
                            updateDetailListModel(selectedSubNdx, 1);
                        } catch (SQLException e1) {
                            JOptionPane.showMessageDialog(SubDivisionsDialog.this,
                                    "Failed to delete " + selectedObject.getName()+". "+e1.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            e1.printStackTrace();
                        }
                    }
                }
            }
        };

        editAction = new AbstractAction("Edit", resourceManager.readImage("SubDivisionDialog.EditIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (selectedSubNdx) {
                    case 0: // Categories
                        Category c = (Category) AddNewSubDivisionDialog.showDialog(application, selectedSubNdx, selectedObject);
                        if (c != null) {
                            try {
                                c.save(DbManager.dbInstance());
                                updateDetailListModel(selectedSubNdx, c.getId());
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                        break;

                    case 1: // Products
                        Product p = (Product) AddNewSubDivisionDialog.showDialog(application, selectedSubNdx, selectedObject);
                        if (p != null) {
                            try {
                                p.setCategoryId(((DbObject)selectionCbModel.getSelectedItem()).getId());
                                p.save(DbManager.dbInstance());
                                updateDetailListModel(selectedSubNdx, p.getCategoryId());
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                        break;

                    case 2: // Types
                        Type t = (Type) AddNewSubDivisionDialog.showDialog(application, selectedSubNdx, selectedObject);
                        if (t != null) {
                            try {
                                t.setProductId(((DbObject)selectionCbModel.getSelectedItem()).getId());
                                t.save(DbManager.dbInstance());
                                updateDetailListModel(selectedSubNdx, t.getProductId());
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                        break;
                }
            }
        };

        searchAction = new AbstractAction("Search", resourceManager.readImage("Common.Search")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchWord = searchField.getText();
                if (searchWord.isEmpty()) {
                    searchField.setError("Enter a search word");
                } else {
                    java.util.List<DbObject> foundList = search(searchWord);
                    if (foundList.size() > 0) {
                        if (foundList.size() == 1) {
                            DbObject obj = foundList.get(0);
                            try {
                                selectObject(obj);
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        } else {

                        }
                    }
                }
            }
        };
    }

    private void updateComponents(int selectedSubNdx) throws SQLException {
        this.selectedSubNdx = selectedSubNdx;
        setSelectedObject(null);
        updateDetailListModel(selectedSubNdx, 1);
        updateSelectionCbModel(selectedSubNdx);
        switch (selectedSubNdx) {
            case 0: // Categories
                selectionLabel.setVisible(false);
                selectionComboBox.setVisible(false);
                break;

            case 1: // Products
                selectionLabel.setText("Select a category");
                selectionLabel.setVisible(true);
                selectionComboBox.setVisible(true);
                break;

            case 2: // Types
                selectionLabel.setText("Select a product");
                selectionLabel.setVisible(true);
                selectionComboBox.setVisible(true);
                break;
        }
    }

    private void updateDetailListModel(int selectedSubNdx, long id) throws SQLException {
        switch (selectedSubNdx) {
            case 0:
                detailListModel.removeAllElements();
                for(Category c : dbInstance().getCategories()) {
                    detailListModel.addElement(c);
                }
                break;
            case 1:
                detailListModel.removeAllElements();
                for(Product p : dbInstance().getProductListForCategory(id)) {
                    detailListModel.addElement(p);
                }
                break;
            case 2:
                detailListModel.removeAllElements();
                for(Type t : dbInstance().getTypeListForProduct(id)) {
                    detailListModel.addElement(t);
                }
                break;
        }
    }

    private void updateSelectionCbModel(int selectedSubNdx) throws SQLException {
        switch (selectedSubNdx) {
            default:
            case 0:break;
            case 1:
                selectionCbModel.removeAllElements();
                for(Category c : dbInstance().getCategories()) {
                    selectionCbModel.addElement(c);
                }
                break;
            case 2:
                selectionCbModel.removeAllElements();
                for(Product p : dbInstance().getProducts()) {
                    selectionCbModel.addElement(p);
                }
                break;
        }
    }

    private void setSelectedObject(DbObject object) {
        selectedObject = object;
        if (selectedObject != null) {
            String iconPath = selectedObject.getIconPath();
            if (iconPath != null && !iconPath.isEmpty()) {
                try {
                    URL url = new File(selectedObject.getIconPath()).toURI().toURL();
                    iconLabel.setIcon(resourceManager.readImage(url, 48,48));
                } catch (Exception e) {
                    iconLabel.setIcon(resourceManager.readImage("Common.UnknownIcon32"));
                    e.printStackTrace();
                }
            } else {
                iconLabel.setIcon(resourceManager.readImage("Common.UnknownIcon32"));
            }
        } else {
            iconLabel.setIcon(null);
        }
    }

    private void selectObject(DbObject object) throws SQLException, ClassCastException {
        if (object instanceof Category) {
            selectedSubNdx = 0;
        } else if (object instanceof Product) {
            selectedSubNdx = 1;
        } else {
            selectedSubNdx = 2;
        }

        subDivisionList.setSelectedIndex(selectedSubNdx);
        switch (selectedSubNdx) {
            case 0: // Category
                break;
            case 1: { // Product
                int ndx = dbInstance().findProductIndex((Product)object);
                if (ndx >= 0) {
                    selectionComboBox.setSelectedIndex(ndx);
                }
                break;
            }
            case 2: { // Type
                int ndx = dbInstance().getTypeListForProduct(((Type) object).getProductId()).indexOf(object);
                selectionComboBox.setSelectedIndex(ndx);
                break;
            }
        }
        detailList.setSelectedValue(object, true);
        setSelectedObject(selectedObject);
    }

    private List<DbObject> search(String searchWord) {
        List<DbObject> foundList = new ArrayList<>();

        try {
            Category c = dbInstance().findCategoryByName(searchWord);
            if (c != null) {
                foundList.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Product p = dbInstance().findProductByName(searchWord);
            if (p != null) {
                foundList.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Type t= dbInstance().findTypeByName(searchWord);
            if (t != null) {
                foundList.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foundList;
    }

    private void initComponents() {
        // Sub divisions list
        String[] subDivisions = new String[] {"Categories", "Products", "Types"};
        subDivisionList = new JList<>(subDivisions);
        subDivisionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    JList list = (JList)e.getSource();
                    detailsPanel.setTitle((String) list.getSelectedValue());
                    try {
                        updateComponents(list.getSelectedIndex());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        // Search field
        searchField = new ITextField("Search");
        searchField.setMaximumSize(new Dimension(100,30));
        searchField.addActionListener(searchAction);

        // Search button
        searchButton = new JButton(resourceManager.readImage("Common.Search"));
        searchButton.addActionListener(searchAction);

        // Combo box
        selectionCbModel = new DefaultComboBoxModel<>();
        selectionComboBox = new JComboBox<>(selectionCbModel);
        selectionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                switch (selectedSubNdx) {
                    default:
                    case 0: break;
                    case 1: // Products
                        Category c = (Category) cb.getSelectedItem();
                        if (c != null) {
                            try {
                                updateDetailListModel(selectedSubNdx, c.getId());
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                        break;
                    case 2: // Types
                        Product p = (Product) cb.getSelectedItem();
                        if (p != null) {
                            try {
                                updateDetailListModel(selectedSubNdx, p.getId());
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                        break;
                }
            }
        });

        // Icon
        iconLabel = new JLabel(resourceManager.readImage("SubDivisionDialog.EditIcon"));

        // Toolbar
        toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.add(addAction);
        toolBar.add(deleteAction);
        toolBar.add(editAction);
        toolBar.setMaximumSize(new Dimension(toolBar.getPreferredSize()));

        // Details
        detailListModel = new DefaultListModel<>();
        detailList = new JList<>(detailListModel);
        detailList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    setSelectedObject((DbObject) ((JList)e.getSource()).getSelectedValue());
                }
            }
        });
        selectionLabel = new JLabel("Categories");
        detailsPanel = new ITitledPanel("Categories",
                new JComponent[] {createDetailsPanel()});

        subDivisionList.setSelectedIndex(0);

    }

    private void initLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(new ITitledPanel("Sub divisions",
                new JComponent[] {searchField, new JScrollPane(subDivisionList)}
        ), BorderLayout.WEST);

        getContentPanel().add(detailsPanel, BorderLayout.CENTER);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        setPositiveButton("Ok");
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel();
        JScrollPane list = new JScrollPane(detailList);

        SpringLayout layout = new SpringLayout();
        // Label
        layout.putConstraint(WEST, selectionLabel, 5, WEST, panel);
        layout.putConstraint(NORTH, selectionLabel, 5, NORTH, panel);

        // Combobox
        layout.putConstraint(WEST, selectionComboBox, 5, WEST, panel);
        layout.putConstraint(NORTH, selectionComboBox, 2, SOUTH, selectionLabel);
        layout.putConstraint(EAST, selectionComboBox, -5, EAST, panel);

        // List
        layout.putConstraint(WEST, list, 5, WEST, panel);
        layout.putConstraint(NORTH, list, 5, SOUTH, selectionComboBox);
        layout.putConstraint(SOUTH, list, -5, SOUTH, panel);
        layout.putConstraint(EAST, list, 0, WEST, toolBar);

        // Toolbar
        layout.putConstraint(EAST, toolBar, -5, EAST, panel);
        layout.putConstraint(NORTH, toolBar, 5, SOUTH, selectionComboBox);

        // Icon
        layout.putConstraint(EAST, iconLabel,-5, EAST, panel);
        layout.putConstraint(SOUTH, iconLabel, -5, SOUTH, panel);

        // Add stuff
        panel.add(selectionLabel);
        panel.add(selectionComboBox);
        panel.add(list);
        panel.add(toolBar);
        panel.add(iconLabel);
        panel.setPreferredSize(new Dimension(400,500));
        panel.setLayout(layout);

        return panel;
    }

}
