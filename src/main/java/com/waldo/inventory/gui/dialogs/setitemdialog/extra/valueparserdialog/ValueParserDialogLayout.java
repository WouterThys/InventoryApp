package com.waldo.inventory.gui.dialogs.setitemdialog.extra.valueparserdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.parser.SetItem.SetItemValueParser;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ValueParserDialogLayout extends IDialog implements
        ActionListener, ItemListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private DefaultComboBoxModel<String> typeCbModel;
    private DefaultComboBoxModel<String> seriesCbModel;
    private DefaultComboBoxModel<String> minUnitCbModel;
    private DefaultComboBoxModel<String> maxUnitCbModel;

    JComboBox<String> typeCb;
    JComboBox<String> seriesCb;
    JComboBox<String> minUnitCb;
    JComboBox<String> maxUnitCb;

    ITextField minTf;
    ITextField maxTf;

    ISpinner valueSkipSp;

    ITextArea resultTa;

    private JButton parseBtn;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ValueParserDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateTypeCb() {
        typeCbModel.removeAllElements();
        typeCbModel.addElement(SetItemValueParser.R);
        typeCbModel.addElement(SetItemValueParser.C);
        typeCbModel.addElement(SetItemValueParser.L);
        typeCb.setSelectedIndex(0);
    }

    void updateSeriesTb() {
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

    void updateMinUnitCb() {
        minUnitCbModel.removeAllElements();
        switch ((String) typeCbModel.getSelectedItem()) {
            case SetItemValueParser.R:
                for (int i = 3; i < 10; i++) {
                    minUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                break;
            case SetItemValueParser.C:
                for (int i = 0; i < 6; i++) {
                    minUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                break;
            case SetItemValueParser.L:
                for (int i = 0; i < 6; i++) {
                    minUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                break;
        }
        minUnitCb.setSelectedIndex(1);
    }

    void updateMaxUnitCb() {
        maxUnitCbModel.removeAllElements();
        switch ((String) typeCbModel.getSelectedItem()) {
            case SetItemValueParser.R:
                for (int i = 3; i < 10; i++) {
                    maxUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                break;
            case SetItemValueParser.C:
                for (int i = 0; i < 6; i++) {
                    maxUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                break;
            case SetItemValueParser.L:
                for (int i = 0; i < 6; i++) {
                    maxUnitCbModel.addElement(Statics.UnitMultipliers.get(i));
                }
                break;
        }
        maxUnitCb.setSelectedIndex(2);
    }

    private JPanel createWestPanel() {
        JPanel westPanel = new JPanel(new GridBagLayout());

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(westPanel);

        JPanel minPanel = new JPanel(new BorderLayout());
        minPanel.add(minTf, BorderLayout.CENTER);
        minPanel.add(minUnitCb, BorderLayout.EAST);

        JPanel maxPanel = new JPanel(new BorderLayout());
        maxPanel.add(maxTf, BorderLayout.CENTER);
        maxPanel.add(maxUnitCb, BorderLayout.EAST);

        JPanel skipPanel = new JPanel(new BorderLayout());
        skipPanel.add(valueSkipSp, BorderLayout.CENTER);
        skipPanel.add(new ILabel("th value", ILabel.LEFT), BorderLayout.EAST);

        String[] labels = new String[] {
                "Type: ",
                "Series: ",
                "Min value: ",
                "Max value: ",
                "Take every ",
                "Parse: "
        };

        JComponent component[] = new JComponent[] {
                typeCb,
                seriesCb,
                minPanel,
                maxPanel,
                skipPanel,
                parseBtn
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.addLine(labels[i], component[i]);
        }

        return westPanel;
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("SetItem.Series.Title"));
        setTitleName(getTitle());

        // Components
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

        minTf = new ITextField("Min value");
        minTf.setText("1");
        maxTf = new ITextField("Max value");
        maxTf.setText("1");

        resultTa = new ITextArea();
        resultTa.setEnabled(false);
        resultTa.setLineWrap(true);
        resultTa.setWrapStyleWord(true);

        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        valueSkipSp = new ISpinner(spinnerModel);

        parseBtn = new JButton("Parse");
        parseBtn.addActionListener(this);
    }

    @Override
    public void initializeLayouts() {

        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(createWestPanel(), BorderLayout.WEST);
        getContentPanel().add(new JScrollPane(resultTa), BorderLayout.CENTER);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        application.beginWait();
        try {
            updateTypeCb();
            updateSeriesTb();
            updateMinUnitCb();
            updateMaxUnitCb();
        } finally {
            application.endWait();
        }
    }
}