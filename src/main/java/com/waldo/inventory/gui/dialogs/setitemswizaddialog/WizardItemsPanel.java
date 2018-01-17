package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.parser.SetItem.SetItemValueParser;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.actions.CheckItOutAction;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class WizardItemsPanel extends JPanel implements GuiInterface, ItemListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ICheckBox keepOldValuesCb;
    private ICheckBox replaceValuesCb;

    // Values
    private DefaultComboBoxModel<String> typeCbModel;
    private DefaultComboBoxModel<String> seriesCbModel;
    private DefaultComboBoxModel<String> minUnitCbModel;
    private DefaultComboBoxModel<String> maxUnitCbModel;

    private JComboBox<String> typeCb;
    private JComboBox<String> seriesCb;
    private JComboBox<String> minUnitCb;
    private JComboBox<String> maxUnitCb;

    private CheckItOutAction checkItOutAction;

    private ITextField minTf;
    private ITextField maxTf;

    private ISpinner valueSkipSp;

    // Items
    private GuiUtils.IPackagePanel packagePnl;
    private IComboBox<Manufacturer> manufacturerCb;
    private ISpinner amountSpinner;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private final IDialog parent;
    private SetItemValueParser parser;

    private Set selectedSet;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    WizardItemsPanel(Application application, IDialog parent, Set selectedSet) {
        this.application = application;
        this.parent = parent;
        this.selectedSet = selectedSet;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateSettings(WizardSettings settings) {
        if (settings != null) {
            settings.setManufacturer((Manufacturer) manufacturerCb.getSelectedItem());
            settings.setPackageType(packagePnl.getPackageType());
            settings.setPins(packagePnl.getPins());
            settings.setAmount(((SpinnerNumberModel)amountSpinner.getModel()).getNumber().intValue());
            settings.setTypeName((String) typeCb.getSelectedItem());
            settings.setKeepOldSetItems(keepOldValuesCb.isSelected());
            settings.setReplaceValues(replaceValuesCb.isSelected());
            settings.setItems(createItemsFromSettings(settings));
        }
    }


    /*
     *                  METHODS PRIVATE
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateTypeCb() {
        typeCbModel.removeAllElements();
        typeCbModel.addElement(SetItemValueParser.R);
        typeCbModel.addElement(SetItemValueParser.C);
        typeCbModel.addElement(SetItemValueParser.L);
        typeCb.setSelectedIndex(0);
    }

    private void updateSeriesTb() {
        seriesCbModel.removeAllElements();
        switch ((String) typeCbModel.getSelectedItem()) {
            case SetItemValueParser.R:
                for (String e : SetItemValueParser.R_SERIES) {
                    seriesCbModel.addElement(e);
                }
                break;
            case SetItemValueParser.C:
                for (String e : SetItemValueParser.R_SERIES) {
                    seriesCbModel.addElement(e);
                }
                break;
            case SetItemValueParser.L:
                seriesCbModel.addElement("TODO L");
                break;
        }
        seriesCb.setSelectedIndex(2);
    }

    private void updateMinUnitCb() {
        minUnitCbModel.removeAllElements();
        switch ((String) typeCbModel.getSelectedItem()) {
            case SetItemValueParser.R:
                for (int i = 3; i < 10; i++) {
                    minUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                minUnitCb.setSelectedIndex(2);
                break;
            case SetItemValueParser.C:
                for (int i = 0; i < 6; i++) {
                    minUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                minUnitCbModel.setSelectedItem(Statics.UnitMultipliers.p);
                break;
            case SetItemValueParser.L:
                for (int i = 0; i < 6; i++) {
                    minUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                minUnitCbModel.setSelectedItem(Statics.UnitMultipliers.m);
                break;
        }
        minUnitCb.setSelectedIndex(1);
    }

    private void updateMaxUnitCb() {
        maxUnitCbModel.removeAllElements();
        switch ((String) typeCbModel.getSelectedItem()) {
            case SetItemValueParser.R:
                for (int i = 3; i < 10; i++) {
                    maxUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                maxUnitCbModel.setSelectedItem(Statics.UnitMultipliers.M);
                break;
            case SetItemValueParser.C:
                for (int i = 0; i < 6; i++) {
                    maxUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                maxUnitCbModel.setSelectedItem(Statics.UnitMultipliers.M);
                break;
            case SetItemValueParser.L:
                for (int i = 0; i < 6; i++) {
                    maxUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                maxUnitCbModel.setSelectedItem(Statics.UnitMultipliers.x);
                break;
        }
        maxUnitCb.setSelectedIndex(2);
    }

    private void updateValues() {
        if (selectedSet != null) {
            manufacturerCb.setSelectedItem(selectedSet.getManufacturer());
            packagePnl.setPackageType(selectedSet.getPackageType(), selectedSet.getPins());
        }
    }

    private void showSelectedSeries() {
        String type = (String) typeCb.getSelectedItem();
        String series = (String) seriesCb.getSelectedItem();
        String error = null;
        if (series != null && type != null) {
            parser = SetItemValueParser.getParser(type);
            if (parser != null) {
                try {
                    List<String> values = parser.getParseValues(series);
                    StringBuilder sb = new StringBuilder();
                    int cnt = 0;
                    for (String s : values) {
                        sb.append(s).append(", ");
                        if (cnt >= 10) {
                            cnt = 0;
                            sb.append("\n");
                        } else {
                            cnt++;
                        }
                    }
                    JOptionPane.showMessageDialog(
                            WizardItemsPanel.this,
                            sb.toString(),
                            "Values for " + series,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (IOException e) {
                    error = "Could not find series file for " + series + "..";
                }
            } else {
                error = "Could not initialize parser..";
            }
        } else {
            error = "Select valid values..";
        }

        if (error != null) {
            JOptionPane.showMessageDialog(
                    WizardItemsPanel.this,
                    "Could not show values..",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateManufacturerCbValues() {
        if (manufacturerCb != null) {
            manufacturerCb.updateList(cache().getManufacturers());
        }
    }

    private ActionListener createManufacturerListener() {
        return e -> {
            ManufacturersDialog manufacturersDialog = new ManufacturersDialog(application, "Manufacturers");
            if (manufacturersDialog.showDialog() == IDialog.OK) {
                updateManufacturerCbValues();
            }
        };
    }

    private boolean checkForErrors() {
        boolean ok = true;

        double min = 0;
        double max = 0;

        try {
            min = Double.valueOf(minTf.getText());
        } catch (Exception e) {
            e.printStackTrace();
            minTf.setError("This should be a number..");
            ok = false;
        }
        try {
            max = Double.valueOf(maxTf.getText());
        } catch (Exception e) {
            e.printStackTrace();
            maxTf.setError("This should be a number..");
            ok = false;
        }

        if (ok) {
            double dMin = min * Math.pow(10, Statics.UnitMultipliers.toMultiplier(String.valueOf(minUnitCb.getSelectedItem())));
            double dMax = max * Math.pow(10, Statics.UnitMultipliers.toMultiplier(String.valueOf(maxUnitCb.getSelectedItem())));
            if (dMax < dMin) {
                maxTf.setError("Max value can't be smaller than min value..");
                ok = false;
            }
        }

        return ok;
    }

    //
    // Parse button clicked
    //
    private List<Value> createValues() {
        List<Value> valueList = new ArrayList<>();
        if (checkForErrors()) {

            String type = (String) typeCb.getSelectedItem();
            String series = (String) seriesCb.getSelectedItem();
            String minUnit = (String) minUnitCb.getSelectedItem();
            String maxUnit = (String) maxUnitCb.getSelectedItem();
            double min = Double.valueOf(minTf.getText());
            double max = Double.valueOf(maxTf.getText());
            int skip = Integer.valueOf(valueSkipSp.getValue().toString());

            parser = SetItemValueParser.getParser(type);
            if (parser != null) {
                parser.setMinValue(min, minUnit);
                parser.setMaxValue(max, maxUnit);

                try {
                    parser.parse(series);
                    valueList = parser.crop(skip);
                    valueList.sort(new ComparatorUtils.ValueComparator());
                } catch (ParseException | IOException e1) {
                    JOptionPane.showMessageDialog(
                            WizardItemsPanel.this,
                            "Error parsing for values..",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
        return valueList;
    }

    private Item findByValue(List<Item> itemList, Value value) {
        if (itemList != null && value != null) {
            for (Item item : itemList) {
                if (item.getValue().equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    private List<Item> createItemsFromSettings(WizardSettings settings) {
        List<Item> newSetItems = new ArrayList<>();
        if (settings != null) {

            if (settings.isKeepOldSetItems()) {
                newSetItems.addAll(settings.getSelectedSet().getSetItems());
            }

            int nameCnt = 0;
            for (Value value : createValues()) {
                Item item;
                nameCnt++;
                if (settings.isKeepOldSetItems() && settings.isReplaceValues()) {
                    item = findByValue(newSetItems, value);
                    if (item != null) {
                        if (settings.isOverWriteLocations()) {
                            item.setLocationId(settings.getLocation(item).getId());
                        }
                        continue;
                    }
                }
                String name = createItemName(settings, nameCnt);
                item = new Item(
                        name,
                        settings.getTypeName(),
                        value,
                        settings.getManufacturer(),
                        settings.getPackageType(),
                        settings.getPins(),
                        settings.getAmount(),
                        null,
                        settings.getSelectedSet());


                newSetItems.add(item);
            }
        }
        newSetItems.sort(new ComparatorUtils.ItemValueComparator());
        return newSetItems;
    }

    private String createItemName(WizardSettings settings, int count) {
        return settings.getSelectedSet().getName() + " - " + settings.getTypeName() + String.valueOf(count);
    }

    //
    // Panels
    //
    private JPanel createOldValuesPnl() {
        JPanel oldValuesPnl = new JPanel();

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(oldValuesPnl, 140);
        gbc.addLine("Keep old values: ", keepOldValuesCb);
        gbc.addLine("Replace values: ", replaceValuesCb);

        return oldValuesPnl;
    }

    private JPanel createValuesPanel() {
        JPanel valuesPnl = new JPanel();

        JPanel minPanel = new JPanel(new BorderLayout());
        minPanel.add(minTf, BorderLayout.CENTER);
        minPanel.add(minUnitCb, BorderLayout.EAST);

        JPanel maxPanel = new JPanel(new BorderLayout());
        maxPanel.add(maxTf, BorderLayout.CENTER);
        maxPanel.add(maxUnitCb, BorderLayout.EAST);

        JPanel skipPanel = new JPanel(new BorderLayout());
        skipPanel.add(valueSkipSp, BorderLayout.CENTER);
        skipPanel.add(new ILabel("th value", ILabel.LEFT), BorderLayout.EAST);

        JPanel seriesPanel = new JPanel(new BorderLayout());
        JToolBar toolBar = GuiUtils.createNewToolbar();
        toolBar.add(checkItOutAction);
        seriesPanel.add(seriesCb, BorderLayout.CENTER);
        seriesPanel.add(toolBar, BorderLayout.EAST);

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(valuesPnl);
        gbc.addLine("Type: ", typeCb);
        gbc.addLine("Series: ", seriesPanel);
        gbc.addLine("Min value: ", minPanel);
        gbc.addLine("Max value: ", maxPanel);
        gbc.addLine("Take every ", skipPanel);

        return valuesPnl;
    }

    private JPanel createItemPanel() {
        JPanel itemPanel = new JPanel();

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(itemPanel);
        gbc.addLine("Manufacturer: ", GuiUtils.createComboBoxWithButton(manufacturerCb, createManufacturerListener()));
        gbc.addLine("Amount / item: ", amountSpinner);

        return itemPanel;
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (!parent.isUpdating()) {
                Object source = e.getSource();

                if (source.equals(typeCb)) {
                    updateSeriesTb();
                    updateMinUnitCb();
                    updateMaxUnitCb();
                }
            }
        }
    }

    @Override
    public void initializeComponents() {
        // Values
        typeCbModel = new DefaultComboBoxModel<>();
        seriesCbModel = new DefaultComboBoxModel<>();
        minUnitCbModel = new DefaultComboBoxModel<>();
        maxUnitCbModel = new DefaultComboBoxModel<>();

        typeCb = new JComboBox<>(typeCbModel);
        seriesCb = new JComboBox<>(seriesCbModel);
        minUnitCb = new JComboBox<>(minUnitCbModel);
        maxUnitCb = new JComboBox<>(maxUnitCbModel);

        typeCb.addItemListener(this);
        seriesCb.addItemListener(this);
        minUnitCb.addItemListener(this);
        maxUnitCb.addItemListener(this);

        checkItOutAction = new CheckItOutAction() {
            @Override
            public void onCheckItOut() {
               showSelectedSeries();
            }
        };

        minTf = new ITextField("Min value");
        minTf.setText("1");
        maxTf = new ITextField("Max value");
        maxTf.setText("1");

        SpinnerModel valueSpModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        valueSkipSp = new ISpinner(valueSpModel);

        keepOldValuesCb = new ICheckBox("", true);
        keepOldValuesCb.addActionListener(e -> replaceValuesCb.setEnabled(keepOldValuesCb.isSelected()));
        replaceValuesCb = new ICheckBox("", false);

        // Items
        packagePnl = new GuiUtils.IPackagePanel(application);
        manufacturerCb = new IComboBox<>(cache().getManufacturers(), new ComparatorUtils.DbObjectNameComparator<>(), true);

        SpinnerModel amountSpModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        amountSpinner = new ISpinner(amountSpModel);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel oldValuesPnl = createOldValuesPnl();
        oldValuesPnl.setBorder(GuiUtils.createTitleBorder("Old set items"));

        JPanel valuesPnl = createValuesPanel();
        valuesPnl.setBorder(GuiUtils.createTitleBorder("New set items"));

        JPanel itemPnl = createItemPanel();
        itemPnl.setBorder(GuiUtils.createTitleBorder("Items"));

        packagePnl.setBorder(GuiUtils.createTitleBorder("Package"));

        add(oldValuesPnl);
        add(valuesPnl);
        add(itemPnl);
        add(packagePnl);

        setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
    }

    @Override
    public void updateComponents(Object... object) {
        parent.beginWait();
        try {
            updateTypeCb();
            updateSeriesTb();
            updateMinUnitCb();
            updateMaxUnitCb();

            updateValues();
        } finally {
            parent.endWait();
        }
    }



}
