package com.waldo.inventory.Utils;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.dbclasses.Package;
import com.waldo.inventory.classes.dbclasses.PackageType;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialogLayout;
import com.waldo.inventory.gui.dialogs.filechooserdialog.ImageFileChooser;
import com.waldo.inventory.gui.dialogs.packagedialog.PackageTypeDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.OpenUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;
import com.waldo.utils.icomponents.*;

public class GuiUtils extends com.waldo.utils.GuiUtils {

    @Deprecated
    public static JPanel createComboBoxWithButton(JComboBox comboBox, ActionListener listener) {
        IActions.AddAction addAction = new IActions.AddAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.actionPerformed(e);
            }
        };
        return createComboBoxWithAction(comboBox, addAction);

    }

    public static JPanel createComponentWithAddAction(JComponent component, ActionListener listener) {
        IActions.AddAction addAction = new IActions.AddAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.actionPerformed(e);
            }
        };
        return createComponentWithActions(component, addAction);
    }


    public static class IBrowseFilePanel extends ITextFieldActionPanel implements ActionListener {

        private String defaultPath = "";
        private int fileType = JFileChooser.DIRECTORIES_ONLY;

        public IBrowseFilePanel() {
            this("", "");
        }

        public IBrowseFilePanel(String hint, String defaultPath) {
            this(hint, defaultPath, null, "");
        }

        public IBrowseFilePanel(String hint, String defaultPath, IEditedListener listener, String fieldName) {
            super(hint, fieldName, listener);
            this.defaultPath = defaultPath;
            this.setAction(new IActions.BrowseFileAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    IBrowseFilePanel.this.actionPerformed(e);
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();

            File openFile;
            if (getText().isEmpty()) {
                openFile = new File(defaultPath);
            } else {
                openFile = new File(getText());
                if (openFile.exists()) {
                    if (openFile.isFile()) {
                        openFile = openFile.getParentFile();
                    }
                } else {
                    openFile = new File(defaultPath);
                }
            }

            fileChooser.setCurrentDirectory(openFile);
            fileChooser.setFileSelectionMode(fileType);

            if (fileChooser.showDialog(IBrowseFilePanel.this, "Open") == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                textField.fireValueChanged();
            }
        }

        public void setError(String error) {
            textField.setError(error);
        }

        public void setWarning(String warning) {
            textField.setWarning(warning);
        }

        public void setDefaultPath(String defaultPath) {
            this.defaultPath = defaultPath;
        }

        public void setEditable(boolean editable) {
            textField.setEditable(editable);
        }
    }

    public static class IBrowseWebPanel extends ITextFieldActionPanel implements ActionListener {

        public IBrowseWebPanel(String hint, String fieldName, IEditedListener editedListener) {
            super(hint, fieldName, editedListener);
            this.setAction(new IActions.BrowseWebAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    IBrowseWebPanel.this.actionPerformed(e);
                }
            });
            setActionToolTip();
            setTextFieldToolTip();
        }

        private void setActionToolTip() {
            if (action != null) {
                String tooltip = "Browse ";
                if (!hint.isEmpty() && getText().isEmpty()) {
                    String firstChar = String.valueOf(hint.charAt(0));
                    if (firstChar.equals(firstChar.toUpperCase())) {
                        tooltip += firstChar.toLowerCase() + hint.substring(1, hint.length());
                    }
                } else {
                    tooltip += getText();
                }

                action.putValue(AbstractAction.SHORT_DESCRIPTION, tooltip);
            }
        }

        private void setTextFieldToolTip() {
            String tooltip = null;
            if (!getText().isEmpty()) {
                tooltip = getText();
            }
            textField.setToolTipText(tooltip);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!getText().isEmpty()) {
                try {
                    OpenUtils.browseLink(getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(
                            IBrowseWebPanel.this,
                            "Unable to browse: " + getText(),
                            "Browse error",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        }

        @Override
        public void setText(String text) {
            super.setText(text);
            setActionToolTip();
            setTextFieldToolTip();
        }
    }

    public static class IBrowseImagePanel extends ITextFieldActionPanel implements ActionListener {

        private String defaultPath = "";

        public IBrowseImagePanel(String defaultPath, IEditedListener listener, String fieldName) {
            super("", fieldName, listener);
            this.setAction(new IActions.BrowseFileAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    IBrowseImagePanel.this.actionPerformed(e);
                }
            });
            this.defaultPath = defaultPath;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = ImageFileChooser.getFileChooser();
            fileChooser.setCurrentDirectory(new File(defaultPath));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            File openFile;
            if (getText().isEmpty()) {
                openFile = new File(defaultPath);
            } else {
                openFile = new File(getText());
                if (openFile.exists()) {
                    if (openFile.isFile()) {
                        openFile = openFile.getParentFile();
                    }
                } else {
                    openFile = new File(defaultPath);
                }
            }

            if (fileChooser.showDialog(IBrowseImagePanel.this, "Open") == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getName());
                textField.fireValueChanged();
            }
        }

        public void setDefaultPath(String defaultPath) {
            this.defaultPath = defaultPath;
        }

        public void setEditable(boolean editable) {
            textField.setEditable(editable);
        }
    }

    public static class INameValuePanel extends JPanel implements GuiInterface, ActionListener {

        private ITextField nameTf;
        private IValuePanel valuePnl;
        private JButton toggleValuePnlBtn;

        public INameValuePanel(IEditedListener nameListener, String nameFieldName, IEditedListener valueListener) {
            super();

            initializeComponents();
            initializeLayouts();

            if (nameListener != null) {
                nameTf.addEditedListener(nameListener, nameFieldName);
            }

            if (valueListener != null) {
                valuePnl.addEditedListener(valueListener);
            }
        }

        // Name
        public String getNameText() {
            return nameTf.getText();
        }

        public void setNameTxt(String name) {
            nameTf.setText(name);
        }

        public void setError(String error) {
            nameTf.setError(error);
        }

        // Value
        public void setValue(Value v) {
            valuePnl.setValue(v);
            updateComponents();
        }

        public Value getValue() {
            return valuePnl.getValue();
        }

        public void addEditedListener(IEditedListener listener) {
            valuePnl.addEditedListener(listener);
        }

        public void setValuePanelVisible(boolean visible) {
            if (valuePnl != null) {
                valuePnl.setVisible(visible);

                if (visible) {
                    toggleValuePnlBtn.setText("");
                    toggleValuePnlBtn.setIcon(imageResource.readImage("Search.ArrowRightBlue"));
                } else {
                    updateComponents();
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setValuePanelVisible(!valuePnl.isVisible());
        }

        @Override
        public void initializeComponents() {
            // Name
            nameTf = new ITextField("Name");
            nameTf.setName(EditItemDialogLayout.COMP_NAME);

            // Value
            valuePnl = new IValuePanel();

            // Button
            toggleValuePnlBtn = new JButton(imageResource.readImage("Search.ArrowLeftBlue"));
            toggleValuePnlBtn.addActionListener(this);
            toggleValuePnlBtn.setToolTipText("Value");
        }

        @Override
        public void initializeLayouts() {
            setLayout(new BorderLayout());

            JPanel totalPanel = new JPanel(new BorderLayout());
            JPanel valuePanel = new JPanel(new BorderLayout());

            valuePanel.add(valuePnl, BorderLayout.CENTER);
            valuePanel.add(toggleValuePnlBtn, BorderLayout.EAST);

            totalPanel.add(nameTf, BorderLayout.CENTER);
            totalPanel.add(valuePanel, BorderLayout.EAST);

            add(totalPanel, BorderLayout.CENTER);
            valuePnl.setVisible(false);
        }

        @Override
        public void updateComponents(Object... args) {
            if (valuePnl.getValue().hasValue()) {
                toggleValuePnlBtn.setText(valuePnl.getValue().toString());
                toggleValuePnlBtn.setIcon(null);
            } else {
                toggleValuePnlBtn.setText("");
                toggleValuePnlBtn.setIcon(imageResource.readImage("Search.ArrowLeftBlue"));
            }
        }
    }

    public static class IValuePanel extends JPanel implements
            GuiInterface, ChangeListener, ItemListener {

        private SpinnerNumberModel valueModel;
        private JSpinner valueSp;

        private JComboBox<Statics.ValueMultipliers> multiplierCb;
        private JComboBox<Statics.ValueUnits> unitCb;

        private Value value;
        private IEditedListener listener;

        public IValuePanel() {
            this(null);
        }

        public IValuePanel(IEditedListener listener) {
            super();

            this.listener = listener;

            initializeComponents();
            initializeLayouts();
        }

        public void setValue(Value v) {
            this.value = v;
            updateComponents(value);
        }

        public Value getValue() {
            return value;
        }

        public void addEditedListener(IEditedListener listener) {
            this.listener = listener;
        }

        @Override
        public void initializeComponents() {
            valueModel = new SpinnerNumberModel(0.00, 0.00, 999.99, 0.01);
            valueSp = new JSpinner(valueModel);
            valueSp.addChangeListener(this);
            valueSp.setPreferredSize(new Dimension(80, 0));

            multiplierCb = new JComboBox<>(Statics.ValueMultipliers.values());
            multiplierCb.setSelectedItem(Statics.ValueMultipliers.x);
            multiplierCb.addItemListener(this);

            unitCb = new JComboBox<>(Statics.ValueUnits.values());
            unitCb.setSelectedIndex(0);
            unitCb.addItemListener(this);
        }

        @Override
        public void initializeLayouts() {
            setLayout(new BorderLayout());

            JPanel cbPanel = new JPanel(new BorderLayout());
            cbPanel.add(multiplierCb, BorderLayout.WEST);
            cbPanel.add(unitCb, BorderLayout.EAST);

            add(valueSp, BorderLayout.CENTER);
            add(cbPanel, BorderLayout.EAST);
        }

        @Override
        public void updateComponents(Object... object) {
            if (object.length != 0 && object[0] != null) {
                Value v = (Value) object[0];

                valueModel.setValue(v.getDoubleValue());
                multiplierCb.setSelectedItem(v.getMultiplier());
                unitCb.setSelectedItem(v.getUnit());
            } else {
                valueModel.setValue(0);
                multiplierCb.setSelectedIndex(5);
                unitCb.setSelectedIndex(0);
            }
        }

        //
        // Spinner value changed
        //
        @Override
        public void stateChanged(ChangeEvent e) {
            if (value != null) {
                if (e.getSource().equals(valueSp)) {
                    SwingUtilities.invokeLater(() -> {
                        double val = valueModel.getNumber().doubleValue();
                        value.setDoubleValue(val);
                        if (listener != null) {
                            listener.onValueChanged(this, "value:doubleValue", 0, val);
                        }
                    });
                }
            }
        }

        //
        // Combo box selection changed
        //
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (value != null) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    SwingUtilities.invokeLater(() -> {
                        if (e.getSource().equals(multiplierCb)) {
                            multiplierCbChanged();
                        } else {
                            unitCbChanged();
                        }
                    });
                }
            }
        }

        private void multiplierCbChanged() {
            value.setMultiplier((Statics.ValueMultipliers) multiplierCb.getSelectedItem());
            if (listener != null) {
                listener.onValueChanged(this, "value:multiplier", 0, 0);
            }

        }

        private void unitCbChanged() {
            value.setUnit((Statics.ValueUnits) unitCb.getSelectedItem());
            if (listener != null) {
                listener.onValueChanged(this, "value:unit", 0, 0);
            }
        }
    }

    public static class IPackagePanel extends JPanel implements GuiInterface, ActionListener, ItemListener {

        private final Window parent;

        private IComboBox<Package> packageCb;
        private IComboBox<PackageType> typeCb;
        private ISpinner pinsSp;

        private PackageType packageType;

        public IPackagePanel(Window parent) {
            this(parent, null, "", "");
        }

        public IPackagePanel(Window parent, IEditedListener listener, String typeField, String pinsField) {
            super();

            this.parent = parent;

            initializeComponents();
            initializeLayouts();
            if (listener != null) {
                typeCb.addEditedListener(listener, typeField);
                pinsSp.addEditedListener(listener, pinsField);
            }
        }

        public PackageType getPackageType() {
            return (PackageType) typeCb.getSelectedItem();
        }

        public int getPins() {
            return ((SpinnerNumberModel)pinsSp.getModel()).getNumber().intValue();
        }

        public void setPackageType(PackageType packageType, int pins) {
            updateComponents(packageType, pins);
        }

        private void packageCbChanged() {

                Package p = (Package) packageCb.getSelectedItem();
                if (p != null) {
                    typeCb.updateList(SearchManager.sm().findPackageTypesByPackageId(p.getId()));
                    typeCb.setEnabled(true);
                    pinsSp.setEnabled(false);
                }

        }

        private void typeCbChanged() {

                packageType = (PackageType) typeCb.getSelectedItem();
                if (packageType != null && !packageType.isUnknown()) {
                    pinsSp.setEnabled(packageType.isAllowOtherPinNumbers());
                    pinsSp.setTheValue(packageType.getDefaultPins());
                } else {
                    pinsSp.setEnabled(false);
                }

        }

        @Override
        public void initializeComponents() {
            // Package
            packageCb = new IComboBox<>(cache().getPackages(), new DbObjectNameComparator<>(), true);
            packageCb.addItemListener(this);

            // Package type
            typeCb = new IComboBox<>(new ArrayList<>(), new DbObjectNameComparator<>(), true);
            typeCb.setEnabled(false);
            typeCb.addItemListener(this);

            // Pins
            SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
            pinsSp = new ISpinner(spinnerModel);
            pinsSp.setEnabled(false);
        }

        @Override
        public void initializeLayouts() {
            setLayout(new BorderLayout());
            JPanel panel = new JPanel();

            GridBagHelper gbc = new GridBagHelper(panel);
            gbc.addLine("Package: ", GuiUtils.createComponentWithAddAction(packageCb, this));
            gbc.addLine("Type: ", GuiUtils.createComponentWithAddAction(typeCb, this));
            gbc.addLine("Pins: ", pinsSp);

            add(panel, BorderLayout.CENTER);
        }

        @Override
        public void updateComponents(Object... args) {
            PackageType pt = null;
            if (args.length > 0 && args[0] != null) {
                pt = (PackageType) args[0];
            } else {
                packageType = null;
            }

            if (packageType != null && packageType.getId() == pt.getId()) {
                return;
            } else {
                packageType = pt;
            }

            if (packageType != null && !packageType.isUnknown()) {
                Package p = packageType.getPackage();
                if (p != null) {
                        packageCb.selectItem(p);
                        typeCb.updateList(SearchManager.sm().findPackageTypesByPackageId(p.getId()));
                        if (packageType.isAllowOtherPinNumbers()) {
                            pinsSp.setEnabled(true);
                            int itemPins = (int) args[1];
                            if (itemPins > 0) {
                                pinsSp.setTheValue(itemPins);
                            } else {
                                pinsSp.setTheValue(packageType.getDefaultPins());
                            }
                        } else {
                            pinsSp.setTheValue(packageType.getDefaultPins());
                            pinsSp.setEnabled(false);
                        }
                        typeCb.selectItem(packageType);
                        typeCb.setEnabled(true);

                } else {
                    typeCb.setEnabled(false);
                    pinsSp.setEnabled(false);
                }
            } else {
                typeCb.setEnabled(false);
                pinsSp.setEnabled(false);
            }
        }

        //
        // Combo box value changed
        //
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                //if (!parent.isUpdating((Component)e.getSource())) {
                    SwingUtilities.invokeLater(() -> {
                        if (e.getSource().equals(packageCb)) {
                            packageCbChanged();
                        } else {
                            typeCbChanged();
                        }
                    });
                //}
            }
        }

        //
        // Edit package
        //
        @Override
        public void actionPerformed(ActionEvent e) {
            PackageTypeDialog dialog = new PackageTypeDialog(parent, "Packages");
            dialog.showDialog();

            // TODO update comboboxes
        }
    }
}
