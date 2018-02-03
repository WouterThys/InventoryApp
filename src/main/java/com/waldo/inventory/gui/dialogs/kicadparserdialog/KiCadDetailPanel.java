package com.waldo.inventory.gui.dialogs.kicadparserdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextArea;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.utils.DateUtils;

import javax.swing.*;
import java.awt.*;

public class KiCadDetailPanel extends JPanel implements GuiUtils.GuiInterface {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField refTf;
    private ITextField valueTf;
    private ITextField footprintTf;
    private ITextField lsLibTf;
    private ITextField lsPartTf;
    private ITextField tStampTf;
    private ITextArea  spNamesTa;
    private ITextArea  spTStampsTa;



    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private PcbItem selectedComponent;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public KiCadDetailPanel() {

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void setValues() {
        if (selectedComponent != null) {
            refTf.setText(selectedComponent.getRef());
            valueTf.setText(selectedComponent.getValue());
            footprintTf.setText(selectedComponent.getFootprint());
            lsLibTf.setText(selectedComponent.getLibrary());
            lsPartTf.setText(selectedComponent.getPartName());
            tStampTf.setText(DateUtils.formatDateTime(selectedComponent.gettStamp()));
            spNamesTa.clearText();
//            for (String name : selectedComponent.getPcbSheetName()) {
//                spNamesTa.append(name + "\n");
//            }
            spNamesTa.setText(selectedComponent.getSheetName());
            spTStampsTa.clearText();
//            for (Date date : selectedComponent.getSheetPath().gettStamps()) {
//                spTStampsTa.setText(sdf.format(date));
//            }
            spTStampsTa.setText(DateUtils.formatDateTime(selectedComponent.gettStamp()));
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        refTf = new ITextField();
        valueTf = new ITextField();
        footprintTf = new ITextField();
        lsLibTf = new ITextField();
        lsPartTf = new ITextField();
        tStampTf = new ITextField();
        spNamesTa = new ITextArea("", 2, 3);
        spTStampsTa = new ITextArea("", 2, 3);

        refTf.setEnabled(false);
        valueTf.setEnabled(false);
        footprintTf.setEnabled(false);
        lsLibTf.setEnabled(false);
        lsPartTf.setEnabled(false);
        tStampTf.setEnabled(false);
        spNamesTa.setEnabled(false);
        spTStampsTa.setEnabled(false);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        JComponent[] jComponents = new JComponent[] {
                refTf, valueTf, footprintTf, lsLibTf, lsPartTf, tStampTf, new JScrollPane(spNamesTa), new JScrollPane(spTStampsTa)
        };
        ILabel[] iLabels = new ILabel[] {
                new ILabel("Reference: ", ILabel.RIGHT), new ILabel("Value: ", ILabel.RIGHT),
                new ILabel("Footprint: ", ILabel.RIGHT), new ILabel("Library: ", ILabel.RIGHT),
                new ILabel("Part: ", ILabel.RIGHT), new ILabel("Last edited: ", ILabel.RIGHT),
                new ILabel("Sheet names: ", ILabel.RIGHT), new ILabel("Sheet date: ", ILabel.RIGHT)
        };

        for (int i = 0; i < jComponents.length; i++) {
            // Label
            gbc.gridx = 0; gbc.weightx = 0;
            gbc.gridy = i; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.EAST;
            add(iLabels[i], gbc);
            // Component
            gbc.gridx = 1; gbc.weightx = 1;
            gbc.gridy = i; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            add(jComponents[i], gbc);
        }

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 1),
                BorderFactory.createEmptyBorder(10,5,5,10)
        ));
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length == 0) {
            setVisible(false);
            selectedComponent = null;
        } else {
            if (object[0] instanceof PcbItem) {
                selectedComponent = (PcbItem) object[0];

                setValues();

                setVisible(true);
            }
        }
    }
}