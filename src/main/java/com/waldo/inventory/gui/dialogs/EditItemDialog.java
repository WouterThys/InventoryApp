package com.waldo.inventory.gui.dialogs;

import com.waldo.inventory.Utils.ImageUtils;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;

import static com.waldo.inventory.Utils.PanelUtils.*;
import static com.waldo.inventory.database.DbManager.dbInstance;
import static javax.swing.SpringLayout.*;

public class EditItemDialog extends IDialogPanel {

    // Local stuff
    private static JDialog dialog;
    private static Item newItem;
    private static Application parent;

    // Components
    private ILabel titleIconLabel;
    private ILabel titleNameLabel;
    private JTextField idTextField;
    private ITextField nameTextField;
    private JTextArea descriptionTextArea;
    private ITextField priceTextField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> productComboBox;
    DefaultComboBoxModel<String> productCbModel;
    private JComboBox<String> typeComboBox;

    private String buttonText = "";

    // Data sheet
    private JTextField localDataSheetTextField;
    private JButton localDataSheetButton;
    private JFileChooser localDataSheetFileChooser;
    private JTextField onlineDataSheetTextField;

    private JButton cancelButton;
    private JButton createButton;

    public static Item showDialog(Application parent) throws SQLException {
        EditItemDialog.parent = parent;
        dialog = new JDialog(parent, "Create new Item", true);
        dialog.getContentPane().add(new EditItemDialog());
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        return newItem;
    }

    public static Item showDialog(Application parent, Item item) throws SQLException {
        EditItemDialog.parent = parent;
        dialog = new JDialog(parent, "Edit Item", true);
        dialog.getContentPane().add(new EditItemDialog(item));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        return newItem;
    }

    private EditItemDialog(Item item) throws SQLException {
        super();
        newItem = item;
        initComponents();
        initLayouts();
        updateValues();
        createButton.setText("Save");
    }

    private EditItemDialog() throws SQLException {
        this(new Item());
        createButton.setText("Create");
    }

    private void updateValues() throws SQLException {
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

        Category c = dbInstance().findCategoryById(newItem.getCategory());
        java.util.List<Category> cl = dbInstance().getCategories();
        int ndx = cl.indexOf(c);

        categoryComboBox.setSelectedIndex(ndx);

        Product p = dbInstance().findProductById(newItem.getProduct());
        productComboBox.setSelectedIndex(dbInstance().getProducts().indexOf(p)); // TODO -> with products for category

        com.waldo.inventory.classes.Type t = dbInstance().findTypeById(newItem.getType());
        typeComboBox.setSelectedIndex(dbInstance().getTypes().indexOf(t));

        localDataSheetTextField.setText(newItem.getLocalDataSheet());
        onlineDataSheetTextField.setText(newItem.getOnlineDataSheet());
    }

    private boolean verify() {
        boolean ok = true;

        String name = nameTextField.getText();
        if (name.isEmpty()) {
            nameTextField.setError("Name can not be empty");
            ok = false;
        }

        String price = priceTextField.getText();
        try {
            Double.valueOf(price);
        } catch (Exception e) {
            priceTextField.setError("This should be a number");
            ok = false;
        }
        return ok;
    }

    private void initComponents() throws SQLException {
        // Title
        titleIconLabel = new ILabel(resourceManager.readImage("Common.UnknownIcon48"));
        titleIconLabel.setPreferredSize(new Dimension(48,48));
        titleIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JLabel lbl = (JLabel)e.getSource();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "png", "jpeg");
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(filter);
                    fileChooser.setCurrentDirectory(new File("."));
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    if (fileChooser.showOpenDialog(EditItemDialog.this) == JFileChooser.APPROVE_OPTION) {
                        newItem.setIconPath(fileChooser.getSelectedFile().getAbsolutePath());
                        try {
                            URL url = fileChooser.getSelectedFile().toURI().toURL();
                            lbl.setIcon(resourceManager.readImage(url, 48,48));
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        });
        titleNameLabel = new ILabel("?");
        titleNameLabel.setFontSize(36);

        // Identification
        idTextField = new ITextField(String.valueOf(newItem.getId()));
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
        createCategoryCb();
        createProductCb();
        createTypeCb();

        // Local data sheet
        localDataSheetTextField = new ITextField();
        localDataSheetTextField.setToolTipText(localDataSheetTextField.getText());
        localDataSheetFileChooser = new JFileChooser();
        localDataSheetButton = new JButton(ImageUtils.loadImageIcon("folder"));
        localDataSheetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               localDataSheetFileChooser.setCurrentDirectory(new File("."));
               localDataSheetFileChooser.setDialogTitle("Select the data sheet");
               localDataSheetFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
               if (localDataSheetFileChooser.showOpenDialog(EditItemDialog.this) == JFileChooser.APPROVE_OPTION) {
                   localDataSheetTextField.setText(localDataSheetFileChooser.getSelectedFile().getAbsolutePath());
               }
            }
        });

        // Online data sheet
        onlineDataSheetTextField = new ITextField();

        // Dialog buttons
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newItem = null;
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        createButton = new JButton(buttonText);
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (verify()) {
                    // Create item
                    newItem.setName(nameTextField.getText());
                    newItem.setDescription(descriptionTextArea.getText());
                    String priceTxt = priceTextField.getText();
                    if (!priceTxt.isEmpty()) {
                        newItem.setPrice(Double.valueOf(priceTxt));
                    }

                    try {
                        newItem.setCategory(getCategoryId());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        newItem.setProduct(getProductId());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        newItem.setType(getTypeId());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                    newItem.setLocalDataSheet(localDataSheetTextField.getText());
                    newItem.setOnlineDataSheet(onlineDataSheetTextField.getText());

                    // Close dialog
                    dialog.setVisible(false);
                    dialog.dispose();
                }
            }
        });
    }

    private void initLayouts() {
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
        setPositiveButton(createButton);
        setNegativeButton(cancelButton);
    }

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
        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                if (productComboBox != null) {
                    productComboBox.setEnabled(cb.getSelectedIndex() > 0); // Bigger than "UNKNOWN"

                    productCbModel.removeAllElements();
                    long id = 0;
                    try {
                        id = getCategoryId();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        productCbModel.addElement(dbInstance().getProducts().get(0).toString()); // Add unknown
                        for (Product p : dbInstance().getProductListForCategory(id)) {
                            productCbModel.addElement(p.toString());
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                }
                if (typeComboBox != null) {
                    typeComboBox.setEnabled(cb.getSelectedIndex() > 0);
                }
            }
        });
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
        productComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (typeComboBox != null) {
                    JComboBox cb = (JComboBox) e.getSource();
                    typeComboBox.setEnabled((cb.getSelectedIndex() > 0)); // Bigger than "UNKNOWN"
                }
            }
        });
        productComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getCategory() > DbObject.UNKNOWN));
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

        DefaultComboBoxModel<String> typeCbModel = new DefaultComboBoxModel<>(typeStrings);
        typeComboBox = new JComboBox<>(typeCbModel);
        typeComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getProduct() > DbObject.UNKNOWN));
        typeComboBox.setSelectedIndex(selectedIndex);
    }

    private long getCategoryId() throws SQLException {
        int ndx = categoryComboBox.getSelectedIndex();
        if (ndx >= 0) {
            return dbInstance().getCategories().get(ndx).getId();
        } else {
            return DbObject.UNKNOWN;
        }
    }

    private long getProductId() throws SQLException {
        if (productComboBox.isEnabled()) {
            int ndx = productComboBox.getSelectedIndex();
            if (ndx >= 0) {
                return dbInstance().getProducts().get(ndx).getId();
            } else {
                return DbObject.UNKNOWN;
            }
        } else {
            return DbObject.UNKNOWN;
        }
    }

    private long getTypeId() throws SQLException {
        if (typeComboBox.isEnabled()) {
            int ndx = typeComboBox.getSelectedIndex();
            if (ndx >= 0) {
                return dbInstance().getTypes().get(ndx).getId();
            } else {
                return DbObject.UNKNOWN;
            }
        } else {
            return DbObject.UNKNOWN;
        }
    }
}
