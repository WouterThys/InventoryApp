package com.waldo.inventory.gui.dialogs;

import com.waldo.inventory.Utils.ImageUtils;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.Utils.validators.NotEmptyValidator;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Vector;

import static com.waldo.inventory.Utils.PanelUtils.*;

public class EditItemDialog extends JPanel {

    // Local stuff
    private static JDialog dialog;
    private static Item newItem;
    private static Application parent;

    // Components
    private JTextField idTextField;
    private JTextField nameTextField;
    private JTextArea descriptionTextArea;
    private JFormattedTextField priceTextField;
    private JComboBox<String> categoryComboBox;
    private DefaultComboBoxModel<String> categoryCbModel;
    private JComboBox<String> productComboBox;
    private DefaultComboBoxModel<String> productCbModel;
    private JComboBox<String> typeComboBox;
    private DefaultComboBoxModel<String> typeCbModel;

    private String buttonText = "";

    // Data sheet
    private JTextField localDataSheetTextField;
    private JButton localDataSheetButton;
    private JFileChooser localDataSheetFileChooser;
    private JTextField onlineDataSheetTextField;

    private JButton cancelButton;
    private JButton createButton;

    public static Item showDialog(Application parent) {
        EditItemDialog.parent = parent;
        dialog = new JDialog(parent, "Create new Item", true);
        dialog.getContentPane().add(new EditItemDialog());
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(500,400);
        dialog.setResizable(false);
        dialog.setVisible(true);
        return newItem;
    }

    public static Item showDialog(Application parent, Item item) {
        EditItemDialog.parent = parent;
        dialog = new JDialog(parent, "Create new Item", true);
        dialog.getContentPane().add(new EditItemDialog(item));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(400,500);
        dialog.setResizable(false);
        dialog.setVisible(true);
        return newItem;
    }

    private EditItemDialog(Item item) {
        super(new GridBagLayout());
        newItem = item;
        buttonText = "Save";
        initComponents();
        initLayouts();
        updateValues();
    }

    private EditItemDialog() {
        this(new Item());
        buttonText = "Create";
    }

    private void updateValues() {
        idTextField.setText(String.valueOf(newItem.getId()));
        nameTextField.setText(newItem.getName());
        descriptionTextArea.setText(newItem.getDescription());
        priceTextField.setText(String.valueOf(newItem.getPrice()));

        Category c = parent.findCategoryById(newItem.getCategory());
        categoryComboBox.setSelectedIndex(parent.getCategoryList().indexOf(c));

        Product p = parent.findProductById(newItem.getProduct());
        productComboBox.setSelectedIndex(parent.getProductList().indexOf(p));

        Type t = parent.findTypeById(newItem.getType());
        typeComboBox.setSelectedIndex(parent.getTypeList().indexOf(t));

        localDataSheetTextField.setText(newItem.getLocalDataSheet());
        onlineDataSheetTextField.setText(newItem.getOnlineDataSheet());
    }

    private void initComponents() {
        idTextField = new JTextField(String.valueOf(newItem.getId()));
        idTextField.setEditable(false);

        nameTextField = PanelUtils.getHintTextField("Component name");
        //nameTextField.setInputVerifier(new NotEmptyValidator(nameTextField));
        descriptionTextArea = PanelUtils.getHintTextArea("Component description");
        //descriptionTextArea.setInputVerifier(new NotEmptyValidator(descriptionTextArea));

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(Double.MIN_VALUE);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true); // Commit on every key press
        priceTextField = PanelUtils.getHintFormattedTextField("Price", formatter);

        // Combo boxes
        createCategoryCb();
        createProductCb();
        createTypeCb();

        // Local data sheet
        localDataSheetTextField = new JTextField();
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
        onlineDataSheetTextField = new JTextField();

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
                // Create item
                newItem.setName(nameTextField.getText());
                newItem.setDescription(descriptionTextArea.getText());
                String priceTxt = priceTextField.getText();
                if (!priceTxt.isEmpty()) {
                    newItem.setPrice(Double.valueOf(priceTxt));
                }

                newItem.setCategory(categoryComboBox.getSelectedIndex());
                newItem.setProduct(productComboBox.getSelectedIndex());
                newItem.setType(typeComboBox.getSelectedIndex());

                newItem.setLocalDataSheet(localDataSheetTextField.getText());
                newItem.setOnlineDataSheet(onlineDataSheetTextField.getText());

                // Close dialog
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
    }

    private void initLayouts() {
        // Id
        add(new JLabel("Database ID: "), createLabelConstraints(0,0));
        add(idTextField, createFieldConstraints(1,0));

        // Name
        add(new JLabel("Name: "), createLabelConstraints(0,1));
        add(nameTextField, createFieldConstraints(1,1));

        // Description
        add(new JLabel("Description: "), createLabelConstraints(0,2));
        GridBagConstraints constraints = createFieldConstraints(1,2);
        constraints.weighty = 1;
        add(descriptionTextArea, constraints);

        // Price
        add(new JLabel("Price: "), createLabelConstraints(0,3));
        add(priceTextField, createFieldConstraints(1,3));

        // Category
        add(new JLabel("Category: "), createLabelConstraints(0,4));
        add(categoryComboBox, createFieldConstraints(1,4));

        // Product
        add(new JLabel("Product: "), createLabelConstraints(0,5));
        add(productComboBox, createFieldConstraints(1,5));

        // Type
        add(new JLabel("Type: "), createLabelConstraints(0,6));
        add(typeComboBox, createFieldConstraints(1,6));

        // Local data sheet
        add(new JLabel("Local data sheet: "), createLabelConstraints(0,7));
        constraints = createFieldConstraints(1, 7);
        constraints.gridwidth = 1;
        add(localDataSheetTextField, constraints);
        constraints = createFieldConstraints(2, 7);
        constraints.weightx = 0.1;
        constraints.gridwidth = 1;
        add(localDataSheetButton, constraints);

        // Online data sheet
        add(new JLabel("Online data sheet: "), createLabelConstraints(0,8));
        add(onlineDataSheetTextField, createFieldConstraints(1,8));

        // Buttons
        JPanel buttons = new JPanel();
        buttons.add(cancelButton);
        buttons.add(createButton);
        constraints = createButtonConstraints(0,9);
        constraints.gridwidth = 3;
        add(buttons, constraints);
    }

    private void createCategoryCb() {
        Vector<String> categoryItems = new Vector<>();
        for (Category c : parent.getCategoryList()) {
            categoryItems.add(c.toString());
        }

        categoryCbModel = new DefaultComboBoxModel<>(categoryItems);
        categoryComboBox = new JComboBox<>(categoryCbModel);
        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                categorySelected((JComboBox)e.getSource());
            }
        });
    }

    private void createProductCb() {
        Vector<String> productStrings = new Vector<>();
        for (Product p : parent.getProductList()) {
            productStrings.add(p.toString());
        }

        productCbModel = new DefaultComboBoxModel<>(productStrings);
        productComboBox = new JComboBox<>(productCbModel);
        productComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productSelected((JComboBox)e.getSource());
            }
        });
    }

    private void createTypeCb() {
        Vector<String> typeStrings = new Vector<>();
        for (Type t : parent.getTypeList()) {
            typeStrings.add(t.toString());
        }

        typeCbModel = new DefaultComboBoxModel<>(typeStrings);
        typeComboBox = new JComboBox<>(typeCbModel);
        typeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                typeSelected((JComboBox)e.getSource());
            }
        });
    }

    private void categorySelected(JComboBox cb) {
        int index = cb.getSelectedIndex();
        if (index >= 0) {
            Category category;
            if (parent.getCategoryList().get(index).getId() == DbObject.NEW) {
                category = createNewCategory();
                categoryComboBox.setSelectedItem(category.toString());
            }
        }
    }

    private Category createNewCategory() {
        Category category = null;
        String newCategory = JOptionPane.showInputDialog(EditItemDialog.this, "Add new category name: ",
                "New category", JOptionPane.PLAIN_MESSAGE);
        if (newCategory != null && !newCategory.isEmpty()) {
            category = new Category(newCategory);
            try {
                category.save();
                parent.getCategoryList().add(category);
                categoryCbModel.addElement(category.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(EditItemDialog.this, "Error saving new category: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return category;
    }

    private void productSelected(JComboBox cb) {
        int index = cb.getSelectedIndex();
        if (index >= 0) {
            Product product;
            if (parent.getProductList().get(index).getId() == DbObject.NEW) {
                product = createNewProduct();
            } else {
                product = parent.getProductList().get(index);
            }

            if (newItem != null) {
                newItem.setCategory(product.getId());
            }
        }
    }

    private Product createNewProduct() {
        Product product = null;
        String newProduct = JOptionPane.showInputDialog(EditItemDialog.this, "Add new product name: ",
                "New product", JOptionPane.PLAIN_MESSAGE);
        if (newProduct != null && !newProduct.isEmpty()) {
            product = new Product(newProduct);
            try {
                product.save();
                parent.getProductList().add(product);
                productCbModel.addElement(product.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(EditItemDialog.this, "Error saving new category: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return product;
    }

    private void typeSelected(JComboBox cb) {
        int index = cb.getSelectedIndex();
        if (index >= 0) {
            Type type;
            if (parent.getTypeList().get(index).getId() == DbObject.NEW) {
                type = createNewType();
            } else {
                type = parent.getTypeList().get(index);
            }

            if (newItem != null) {
                newItem.setCategory(type.getId());
            }
        }
    }

    private Type createNewType() {
        Type type = null;
        String newType = JOptionPane.showInputDialog(EditItemDialog.this, "Add new type name: ",
                "New type", JOptionPane.PLAIN_MESSAGE);
        if (newType != null && !newType.isEmpty()) {
            type = new Type(newType);
            try {
                type.save();
                parent.getTypeList().add(type);
                typeCbModel.addElement(type.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(EditItemDialog.this, "Error saving new type: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return type;
    }
}
