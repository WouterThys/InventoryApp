package com.waldo.inventory.gui.dialogs.resistordialog;

import com.waldo.inventory.classes.Resistor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class ResistorCacheDialog extends ResistorCacheDialogLayout {


    public ResistorCacheDialog(Window window, String title) {
        super(window, title, new Resistor());

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            SwingUtilities.invokeLater(() -> {
                resistor.setBandType(getResistorBandType());
                switch (getResistorBandType()) {
                    case FourBand:
                        resistor.setValueFromBands(getFirstBandValue(), getSecondBandValue(), getMultiplier(), getTolerance());
                        break;
                    case FiveBand:
                        resistor.setValueFromBands(getFirstBandValue(), getSecondBandValue(), getThirdBandValue(), getMultiplier(), getTolerance());
                        break;
                    case SixBand:
                        resistor.setValueFromBands(getFirstBandValue(), getSecondBandValue(), getThirdBandValue(), getMultiplier(), getTolerance(), getPpm());
                        break;
                }
                resistorValueLbl.setText(resistor.getValue().toString());
                resistorImage.updateComponents();
            });
        }
    }
}
