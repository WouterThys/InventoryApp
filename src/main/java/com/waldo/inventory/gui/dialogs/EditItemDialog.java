package com.waldo.inventory.gui.dialogs;

import com.waldo.inventory.Utils.ImageUtils;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialogPanel;
import com.waldo.inventory.gui.components.ITextArea;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledPanel;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Vector;

import static com.waldo.inventory.Utils.PanelUtils.*;

public class EditItemDialog extends IDialogPanel {

    // Local stuff
    private static JDialog dialog;
    private static Item newItem;
    private static Application parent;

    // Components
    private JTextField idTextField;
    private ITextField nameTextField;
    private JTextArea descriptionTextArea;
    private JTextField priceTextField;
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

    public static Item showDialog(Application parent) {
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

    public static Item showDialog(Application parent, Item item) {
        EditItemDialog.parent = parent;
        dialog = new JDialog(parent, "Create new Item", true);
        dialog.getContentPane().add(new EditItemDialog(item));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        return newItem;
    }

    private EditItemDialog(Item item) {
        super();
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
        productComboBox.setSelectedIndex(parent.getProductList().indexOf(p)); // TODO -> with products for category

        com.waldo.inventory.classes.Type t = parent.findTypeById(newItem.getType());
        typeComboBox.setSelectedIndex(parent.getTypeList().indexOf(t));

        localDataSheetTextField.setText(newItem.getLocalDataSheet());
        onlineDataSheetTextField.setText(newItem.getOnlineDataSheet());
    }

    private boolean verify() {
        String name = nameTextField.getText();
        if (name.isEmpty()) {
            nameTextField.setError("Name can not be empty");
            return false;
        }
        return true;
    }

    private void initComponents() {
        idTextField = new ITextField(String.valueOf(newItem.getId()));
        idTextField.setEditable(false);

        nameTextField = new ITextField();
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

                    newItem.setCategory(getCategoryId());
                    newItem.setProduct(getProductId());
                    newItem.setType(getTypeId());

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

        // Add all
        getContentPanel().add(new ITitledPanel(
                "Identification",
                new String[] {"Database ID: ", "Name: "},
                new JComponent[] {idTextField, nameTextField}
        ));

        getContentPanel().add(new ITitledPanel(
                "Sub divisions",
                new String[] {"Category: ", "Product: ", "Type: "},
                new JComponent[] {categoryComboBox, productComboBox, typeComboBox}
        ));

        getContentPanel().add(new ITitledPanel(
                "Data sheets",
                new String[] {"Local: ", "Online: "},
                new JComponent[] {local, onlineDataSheetTextField}
        ));

        getContentPanel().add(new ITitledPanel(
                "Info",
                new String[] {"Price: ", "Description: "},
                new JComponent[] {priceTextField, descriptionTextArea}
        ));

        // Buttons
        setPositiveButton(createButton);
        setNegativeButton(cancelButton);
    }

    private void createCategoryCb() {
        int selectedIndex = 0;
        Vector<String> categoryItems = new Vector<>();
        for (Category c : parent.getCategoryList()) {
            categoryItems.add(c.toString());
            if (newItem.getId() >= 0) { // Not a new item -> set combobox to value
                if (c.getId() == newItem.getCategory()) {
                    selectedIndex = parent.getCategoryList().indexOf(c);
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
                    long id = getCategoryId();
                    for (Product p : parent.getProductListForCategory(id)) {
                        productCbModel.addElement(p.toString());
                    }

                }
                if (typeComboBox != null) {
                    typeComboBox.setEnabled(cb.getSelectedIndex() > 0);
                }
            }
        });
        categoryComboBox.setSelectedIndex(selectedIndex);
    }

    private void createProductCb() {
        int selectedIndex = 0;
        Vector<String> productStrings = new Vector<>();
        for (Product p : parent.getProductList()) {
            productStrings.add(p.toString());
            if (newItem.getId() >= 0) { // Not a new item -> set combobox to value
                if (p.getId() == newItem.getProduct()) {
                    selectedIndex = parent.getProductList().indexOf(p);
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

    private void createTypeCb() {
        int selectedIndex = 0;
        Vector<String> typeStrings = new Vector<>();
        for (com.waldo.inventory.classes.Type t : parent.getTypeList()) {
            typeStrings.add(t.toString());
            if (newItem.getId() >= 0) { // Not a new item -> set combobox to value
                if (t.getId() == newItem.getType()) {
                    selectedIndex = parent.getTypeList().indexOf(t);
                }
            }
        }

        DefaultComboBoxModel<String> typeCbModel = new DefaultComboBoxModel<>(typeStrings);
        typeComboBox = new JComboBox<>(typeCbModel);
        typeComboBox.setEnabled((newItem.getId() >= 0) && (newItem.getProduct() > DbObject.UNKNOWN));
        typeComboBox.setSelectedIndex(selectedIndex);
    }

    private long getCategoryId() {
        int ndx = categoryComboBox.getSelectedIndex();
        if (ndx >= 0) {
            return parent.getCategoryList().get(ndx).getId();
        } else {
            return DbObject.UNKNOWN;
        }
    }

    private long getProductId() {
        if (productComboBox.isEnabled()) {
            int ndx = productComboBox.getSelectedIndex();
            if (ndx >= 0) {
                return parent.getProductList().get(ndx).getId();
            } else {
                return DbObject.UNKNOWN;
            }
        } else {
            return DbObject.UNKNOWN;
        }
    }

    private long getTypeId() {
        if (typeComboBox.isEnabled()) {
            int ndx = typeComboBox.getSelectedIndex();
            if (ndx >= 0) {
                return parent.getTypeList().get(ndx).getId();
            } else {
                return DbObject.UNKNOWN;
            }
        } else {
            return DbObject.UNKNOWN;
        }
    }
}
