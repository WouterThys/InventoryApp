package com.waldo.inventory.gui.dialogs.resistordialog;

import com.waldo.inventory.Utils.Statics.ResistorBandType;
import com.waldo.inventory.Utils.Statics.ResistorBandValue;
import com.waldo.inventory.classes.CircleIcon;
import com.waldo.inventory.classes.Resistor;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IResistorImage;
import com.waldo.inventory.gui.dialogs.resistorpreviewdialog.ResistorPreviewDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

abstract class ResistorDialogLayout extends IDialog implements ItemListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IComboBox<ResistorBandType> resistorTypeCb;
    private IComboBox<ResistorBandValue> firstBandCb;
    private IComboBox<ResistorBandValue> secondBandCb;
    private IComboBox<ResistorBandValue> thirdBandCb;
    private IComboBox<ResistorBandValue> multiplierBandCb;
    private IComboBox<ResistorBandValue> toleranceBandCb;
    private IComboBox<ResistorBandValue> ppmBandCb;

    IResistorImage resistorImage;
    ILabel resistorValueLbl;

    private JButton testBtn;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Resistor resistor;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ResistorDialogLayout(Window window, String title, Resistor resistor) {
        super(window, title);
        this.resistor = resistor;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateVisibleComponents() {
        switch (getResistorBandType()) {
            case FourBand:
                thirdBandCb.setVisible(false);
                ppmBandCb.setVisible(false);
                break;
            case FiveBand:
                thirdBandCb.setVisible(true);
                ppmBandCb.setVisible(false);
                break;
            case SixBand:
                thirdBandCb.setVisible(true);
                ppmBandCb.setVisible(true);
                break;
        }
    }

    ResistorBandType getResistorBandType() {
        return (ResistorBandType) resistorTypeCb.getSelectedItem();
    }

    ResistorBandValue getFirstBandValue() {
        return (ResistorBandValue) firstBandCb.getSelectedItem();
    }

    ResistorBandValue getSecondBandValue() {
        return (ResistorBandValue) secondBandCb.getSelectedItem();
    }

    ResistorBandValue getThirdBandValue() {
        return (ResistorBandValue) thirdBandCb.getSelectedItem();
    }

    ResistorBandValue getMultiplier() {
        return (ResistorBandValue) multiplierBandCb.getSelectedItem();
    }

    ResistorBandValue getTolerance() {
        return (ResistorBandValue) toleranceBandCb.getSelectedItem();
    }

    ResistorBandValue getPpm() {
        return (ResistorBandValue) ppmBandCb.getSelectedItem();
    }

    private JPanel createComboboxPanel() {
        JPanel panel = new JPanel();

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel);
        gbc.addLine("1st Band: ", firstBandCb);
        gbc.addLine("2st Band: ", secondBandCb);
        gbc.addLine("3st Band: ", thirdBandCb);
        gbc.addLine("Multiplier: ", multiplierBandCb);
        gbc.addLine("Tolerance: ", toleranceBandCb);
        gbc.addLine("PPM:", ppmBandCb);

        return panel;
    }
    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        setResizable(true);

        resistorValueLbl = new ILabel();

        resistorTypeCb = new IComboBox<>(ResistorBandType.values());
        resistorTypeCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                SwingUtilities.invokeLater(this::updateVisibleComponents);
            }
        });

        firstBandCb = new IComboBox<>(ResistorBandValue.getFirstBandValues(), null, false);
        secondBandCb = new IComboBox<>(ResistorBandValue.getSecondBandValues(), null, false);
        thirdBandCb = new IComboBox<>(ResistorBandValue.getThirdBandValues(), null, false);
        multiplierBandCb = new IComboBox<>(ResistorBandValue.getMultiplierBandValues(), null, false);
        toleranceBandCb = new IComboBox<>(ResistorBandValue.getToleranceBandValues(), null, false);
        ppmBandCb = new IComboBox<>();

        firstBandCb.addItemListener(this);
        secondBandCb.addItemListener(this);
        thirdBandCb.addItemListener(this);
        multiplierBandCb.addItemListener(this);
        toleranceBandCb.addItemListener(this);
        ppmBandCb.addItemListener(this);

        firstBandCb.setRenderer(new ValueBandRenderer());
        secondBandCb.setRenderer(new ValueBandRenderer());
        thirdBandCb.setRenderer(new ValueBandRenderer());
        multiplierBandCb.setRenderer(new MultiplierBandRenderer());
        toleranceBandCb.setRenderer(new ToleranceBandRenderer());

        resistorImage = new IResistorImage(resistor);
        resistorImage.setPreferredSize(new Dimension(resistorImage.getOriginalWidth()/4, resistorImage.getOriginalHeight() / 4));

        testBtn = new JButton("Test");
        testBtn.addActionListener(e -> {
            ResistorPreviewDialog dialog = new ResistorPreviewDialog(ResistorDialogLayout.this, "Export", resistorImage.createPrintablePanel());
            dialog.showDialog();
        });

    }

    @Override
    public void initializeLayouts() {

        getContentPanel().setLayout(new BorderLayout());

        JPanel comboboxPanel = createComboboxPanel();
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(resistorTypeCb, BorderLayout.WEST);
        topPanel.add(resistorValueLbl, BorderLayout.CENTER);
        topPanel.add(testBtn, BorderLayout.EAST);

        getContentPanel().add(topPanel, BorderLayout.NORTH);
        getContentPanel().add(comboboxPanel, BorderLayout.WEST);
        getContentPanel().add(resistorImage, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        resistorTypeCb.setSelectedIndex(0);
    }


    private abstract class BandRenderer extends JPanel implements ListCellRenderer<ResistorBandValue> {
        final ILabel name = new ILabel();
        final ILabel col1 = new ILabel();
        final ILabel col2 = new ILabel();

        BandRenderer() {
            setLayout(new BorderLayout());
            add(col1, BorderLayout.WEST);
            add(name, BorderLayout.CENTER);
            add(col2, BorderLayout.EAST);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(90, 20);
        }

        @Override
        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ResistorBandValue> list, ResistorBandValue value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(Color.gray);
                setForeground(Color.blue);
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            name.setText(" " + getNameText(value));
            col1.setIcon(new CircleIcon(value.getColor()));
            col2.setText(" " + getValueText(value) + " ");

            col2.setFont(Font.BOLD);

            return this;
        }

        String getNameText(ResistorBandValue value) {
            return value.toString();
        }
        abstract String getValueText(ResistorBandValue value);
    }

    private class ValueBandRenderer extends BandRenderer {
        @Override
        String getValueText(ResistorBandValue value) {
            return String.valueOf(value.getValue());
        }
    }

    private class MultiplierBandRenderer extends BandRenderer {
        @Override
        public Dimension getMinimumSize() {
            return new Dimension(120, 20);
        }
        @Override
        String getValueText(ResistorBandValue value) {
            return "";//value.getMultiplierString();
        }
    }

    private class ToleranceBandRenderer extends BandRenderer {
        @Override
        public Dimension getMinimumSize() {
            return new Dimension(140, 20);
        }
        @Override
        String getValueText(ResistorBandValue value) {
            return "\u00B1" + value.getTolerance() + "%";
        }
    }
}