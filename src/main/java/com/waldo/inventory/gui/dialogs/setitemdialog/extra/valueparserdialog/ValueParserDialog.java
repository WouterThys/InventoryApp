package com.waldo.inventory.gui.dialogs.setitemdialog.extra.valueparserdialog;

import com.waldo.inventory.Utils.parser.SetItem.SetItemValueParser;
import com.waldo.inventory.classes.dbclasses.SetItem;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.List;

public class ValueParserDialog extends ValueParserDialogLayout {

    private List<SetItem> setItemList;

    public ValueParserDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    @Override
    protected void onOK() {
        int ans = Integer.parseInt(JOptionPane.showInputDialog(ValueParserDialog.this,
                "Amount per item",
                "Amounts",
                JOptionPane.QUESTION_MESSAGE));

        if (ans >= 0) {
            if (setItemList != null) {
                for (SetItem si : setItemList) {
                    si.setAmount(ans);
                }
            }
        }
        super.onOK();
    }

    public List<SetItem> getSetItems() {
        return setItemList;
    }

    public boolean checkForErrors() {
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
            if (max < min) {
                maxTf.setError("Max value can't be smaller than min value..");
                ok = false;
            }
        }

        return ok;
    }

    //
    // Parse button clicked
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (checkForErrors()) {

            String type = (String) typeCb.getSelectedItem();
            String series = (String) seriesCb.getSelectedItem();
            String minUnit = (String) minUnitCb.getSelectedItem();
            String maxUnit = (String) maxUnitCb.getSelectedItem();
            double min = Double.valueOf(minTf.getText());
            double max = Double.valueOf(maxTf.getText());
            int skip = Integer.valueOf(valueSkipSp.getValue().toString());

            SetItemValueParser parser = SetItemValueParser.getParser(type);
            if (parser != null) {
                parser.setMinValue(min, minUnit);
                parser.setMaxValue(max, maxUnit);

//                try {
//                    parser.parse(series);
//                    setItemList = parser.crop(skip);
//
//                    if (setItemList != null) {
//                        resultTa.clearText();
//
//                        for (SetItem si : setItemList) {
//                            resultTa.append(si.getValue() + ",  ");
//                        }
//                    }
//
//                } catch (ParseException | IOException e1) {
//                    IStatusStrip.Status().setError("Error parsing file", e1);
//                }
            }
        }
    }


    //
    // Combo box changed
    //
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (!application.isUpdating()) {
                Object source = e.getSource();

                if (source.equals(typeCb)) {
                    updateSeriesTb();
                    updateMinUnitCb();
                    updateMaxUnitCb();
                }
//            else if (source.equals(seriesCb)) {
//
//            } else if (source.equals(minUnitCb)) {
//
//            } else if (source.equals(maxUnitCb)) {
//
//            }
            }
        }
    }
}