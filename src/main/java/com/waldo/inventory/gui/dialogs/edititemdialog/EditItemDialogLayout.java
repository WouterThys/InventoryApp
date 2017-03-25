package com.waldo.inventory.gui.dialogs.edititemdialog;

import com.waldo.inventory.Utils.ImageUtils;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Vector;

import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;
import static com.waldo.inventory.database.DbManager.dbInstance;
import static javax.swing.SpringLayout.*;
import static javax.swing.SpringLayout.HORIZONTAL_CENTER;
import static javax.swing.SpringLayout.VERTICAL_CENTER;

public class EditItemDialogLayout extends IDialogPanel implements GuiInterface {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel titleIconLabel;
    private ILabel titleNameLabel;
    private JTextField idTextField;
    ITextField nameTextField;
    JTextArea descriptionTextArea;
    ITextField priceTextField;
    JComboBox<String> categoryComboBox;
    JComboBox<String> productComboBox;
    DefaultComboBoxModel<String> productCbModel;
    DefaultComboBoxModel<String> typeCbModel;
    JComboBox<String> typeComboBox;

    // Data sheet
    JTextField localDataSheetTextField;
    private JButton localDataSheetButton;
    JFileChooser localDataSheetFileChooser;
    JTextField onlineDataSheetTextField;

    Action createAction;
    Action cancelAction;
    Action localDataSheetAction;
    MouseAdapter titleIconDoubleClicked;
    ItemListener categoryChangedAction;
    ItemListener productChangedAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    static Item newItem;
    boolean isNew = false;

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void createCategoryCb() throws SQLException {
        int selectedIndex = 0;
        Vector<String> categoryItems = new Vector<>();
        for (Category c : dbInstance().getCategories()) {
            categoryItems.add(c.toString());
            if (newItem.getId() >= 0) { // Not a new item -> set combobox to value
                if (c.getId() == newItem.getCategory()) {
                    selectedIndex = dbInstance().getCategories().indexOf(c);
                }
            }
        }

        DefaultComboBoxModel<String> categoryCbModel = new DefaultComboBoxModel<>(categoryItems);
        categoryComboBox = new JComboBox<>(categoryCbModel);
        categoryComboBox.addItemListener(categoryChangedAction);
        categoryComboBox.setSelectedIndex(selectedIndex);
    }

    private void createProductCb() throws SQLException {
        int selectedIndex = 0;
        Vector<String> productStrings = new Vector<>();
        for (Product p : dbInstance().getProducts()) {
            productStrings.add(p.toString());
            if (newItem.getId() >= 0) { // Not a new item -> set combobox to value
                if (p.getId() == newItem.getProduct()) {
                    selectedIndex = dbInstance().getProducts().indexOf(p);
                }
            }
        }

        productCbModel = new DefaultComboBoxModel<>(productStrings);
        productComboBox = new JComboBox<>(productCbModel);
        productComboBox.addItemListener(productChangedAction);
        productComboBox.setSelectedIndex(selectedIndex);
    }

    private void createTypeCb() throws SQLException {
        int selectedIndex = 0;
        Vector<String> typeStrings = new Vector<>();
        for (com.waldo.inventory.classes.Type t : dbInstance().getTypes()) {
            typeStrings.add(t.toString());
            if (newItem.getId() >= 0) { // Not a new item -> set combobox to value
                if (t.getId() == newItem.getType()) {
                    selectedIndex = dbInstance().getTypes().indexOf(t);
                }
            }
        }

        typeCbModel = new DefaultComboBoxModel<>(typeStrings);
        typeComboBox = new JComboBox<>(typeCbModel);
        typeComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getProduct() > DbObject.UNKNOWN));
        typeComboBox.setSelectedIndex(selectedIndex);
    }


    @Override
    public void initializeComponents() {
        // Title
        titleIconLabel = new ILabel(resourceManager.readImage("Common.UnknownIcon48"));
        titleIconLabel.setPreferredSize(new Dimension(48,48));
        titleIconLabel.addMouseListener(titleIconDoubleClicked);
        titleNameLabel = new ILabel("?");
        titleNameLabel.setFontSize(36);

        // Identification
        idTextField = new ITextField();
        idTextField.setEditable(false);
        idTextField.setEnabled(false);

        nameTextField = new ITextField();
        nameTextField.setTrackingField(titleNameLabel);
        descriptionTextArea = new ITextArea();

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(Double.MIN_VALUE);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true); // Commit on every key press
        priceTextField = new ITextField();

        // Combo boxes
        try {
            createCategoryCb();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            createProductCb();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            createTypeCb();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Local data sheet
        localDataSheetTextField = new ITextField();
        localDataSheetTextField.setToolTipText(localDataSheetTextField.getText());
        localDataSheetFileChooser = new JFileChooser();
        localDataSheetButton = new JButton(ImageUtils.loadImageIcon("folder"));
        localDataSheetButton.addActionListener(localDataSheetAction);

        // Online data sheet
        onlineDataSheetTextField = new ITextField();
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));

        // Additional stuff
        JPanel local = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = createFieldConstraints(0,0);
        constraints.gridwidth = 1;
        local.add(localDataSheetTextField, constraints);
        constraints = createFieldConstraints(1,0);
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        local.add(localDataSheetButton, constraints);

        // Title
        JPanel titlePanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        layout.putConstraint(WEST, titleIconLabel, 5, WEST, titlePanel);
        layout.putConstraint(NORTH, titleIconLabel, 5, NORTH, titlePanel);
        layout.putConstraint(SOUTH, titleIconLabel, -5, SOUTH, titlePanel);

        layout.putConstraint(HORIZONTAL_CENTER, titleNameLabel, 0, HORIZONTAL_CENTER, titlePanel);
        layout.putConstraint(VERTICAL_CENTER, titleNameLabel, 0, VERTICAL_CENTER, titlePanel);

        titlePanel.add(titleIconLabel, BorderLayout.WEST);
        titlePanel.add(titleNameLabel, BorderLayout.CENTER);
        titlePanel.setPreferredSize(new Dimension(200, 60));
        titlePanel.setLayout(layout);

        // Add all
        getContentPanel().add(titlePanel);

        getContentPanel().add(new ITitledEditPanel(
                "Identification",
                new String[] {"Database ID: ", "Name: "},
                new JComponent[] {idTextField, nameTextField}
        ));

        getContentPanel().add(new ITitledEditPanel(
                "Sub divisions",
                new String[] {"Category: ", "Product: ", "Type: "},
                new JComponent[] {categoryComboBox, productComboBox, typeComboBox}
        ));

        getContentPanel().add(new ITitledEditPanel(
                "Data sheets",
                new String[] {"Local: ", "Online: "},
                new JComponent[] {local, onlineDataSheetTextField}
        ));

        getContentPanel().add(new ITitledEditPanel(
                "Info",
                new String[] {"Price: ", "Description: "},
                new JComponent[] {priceTextField, descriptionTextArea}
        ));

        // Buttons
        String txt  = isNew ? "Create" : "Save";
        setPositiveButton(txt).addActionListener(createAction);
        setNegativeButton("Cancel").addActionListener(cancelAction);
    }

    @Override
    public void updateComponents(Object object) {
        if (!newItem.getIconPath().isEmpty()) {
            try {
                URL url = new File(newItem.getIconPath()).toURI().toURL();
                titleIconLabel.setIcon(resourceManager.readImage(url, 48,48));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        titleNameLabel.setText(newItem.getName());

        idTextField.setText(String.valueOf(newItem.getId()));
        nameTextField.setText(newItem.getName());
        descriptionTextArea.setText(newItem.getDescription());
        priceTextField.setText(String.valueOf(newItem.getPrice()));

        Category c = null;
        try {
            c = dbInstance().findCategoryById(newItem.getCategory());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        java.util.List<Category> cl = null;
        try {
            cl = dbInstance().getCategories();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int ndx = (cl != null) ? cl.indexOf(c) : 0;

        categoryComboBox.setSelectedIndex(ndx);

        Product p = null;
        try {
            p = dbInstance().findProductById(newItem.getProduct());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            productComboBox.setSelectedIndex(dbInstance().getProducts().indexOf(p)); // TODO -> with products for category
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Type t = null;
        try {
            t = dbInstance().findTypeById(newItem.getType());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            typeComboBox.setSelectedIndex(dbInstance().getTypes().indexOf(t));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        localDataSheetTextField.setText(newItem.getLocalDataSheet());
        onlineDataSheetTextField.setText(newItem.getOnlineDataSheet());
    }
}
