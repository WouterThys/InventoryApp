package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class ILocationCustomDialog extends IDialog {

    public interface LocationMapToolbarListener {
        void onLocationChanged(int row, int col);
        int onRowChanged(int row);
        int onColChanged(int col);
        int onWidthChanged(int width);
        int onHeightChanged(int height);
    }

    private LocationMapToolbarListener listener;

    private ILabel nameLbl;

    private SpinnerModel selRModel;
    private SpinnerNumberModel selCModel;
    private SpinnerNumberModel rModel;
    private SpinnerNumberModel cModel;
    private SpinnerNumberModel wModel;
    private SpinnerNumberModel hModel;

    private JSpinner selRSpinner;
    private JSpinner selCSpinner;
    private JSpinner rSpinner;
    private JSpinner cSpinner;
    private JSpinner wSpinner;
    private JSpinner hSpinner;

    private int rows;
    private int cols;

    private int selR, selC; // Selected
    private int r, c, w, h;


    public ILocationCustomDialog(Application application, LocationMapToolbarListener listener, int firstR, int firstC, int rows, int cols) {
        super(application, "");

        this.listener = listener;
        this.rows = rows;
        this.cols = cols;
        this.selR = firstR;
        this.selC = firstC;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if (listener != null) {
            listener.onLocationChanged(selR, selC);
        }
    }

    @Override
    public void initializeComponents() {
        // Dialog
        showTitlePanel(false);

        // Fields
        nameLbl = new ILabel("*");

        selRModel = new SpinnerListModel(Arrays.copyOfRange(Statics.Alphabet, 0, rows));
        selCModel = new SpinnerNumberModel(0,0,cols-1, 1);
        rModel = new SpinnerNumberModel(0, 0, 10, 1);
        cModel = new SpinnerNumberModel(0, 0, 10, 1);
        wModel = new SpinnerNumberModel(1, 1, 10, 1);
        hModel = new SpinnerNumberModel(1, 1, 10, 1);

        selRSpinner = new JSpinner(selRModel);
        selCSpinner = new JSpinner(selCModel);
        rSpinner = new JSpinner(rModel);
        cSpinner = new JSpinner(cModel);
        wSpinner = new JSpinner(wModel);
        hSpinner = new JSpinner(hModel);

        selRSpinner.addChangeListener(e -> {
            String rTxt = selRModel.getValue().toString();
            selR = Statics.indexOfAlphabet(rTxt);

            if (listener != null) {
                listener.onLocationChanged(selR, selC);
            }
        });
        selCSpinner.addChangeListener(e -> {
            selC = selCModel.getNumber().intValue();
            if (listener != null) {
                listener.onLocationChanged(selR, selC);
            }
        });

        rSpinner.addChangeListener(e -> {
            r = rModel.getNumber().intValue();
            if (listener != null) {
                int newR = listener.onRowChanged(r);
                rModel.setValue(newR);
            }
        });
        cSpinner.addChangeListener(e -> {
            c = cModel.getNumber().intValue();
            if (listener != null) {
                int newC = listener.onColChanged(c);
                cModel.setValue(newC);
            }
        });
        wSpinner.addChangeListener(e -> {
            w = wModel.getNumber().intValue();
            if (listener != null) {
                int newW = listener.onWidthChanged(w);
                wModel.setValue(newW);
            }
        });
        hSpinner.addChangeListener(e -> {
            h = hModel.getNumber().intValue();
            if (listener != null) {
                int newH = listener.onHeightChanged(h);
                hSpinner.setValue(newH);
            }
        });

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel namePanel = new JPanel();
        JPanel northPanel = new JPanel(new GridBagLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());

        namePanel.add(nameLbl);
        northPanel.setBorder(BorderFactory.createEmptyBorder(5,5,10,5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // - Name
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 2;
        northPanel.add(namePanel, gbc);

        // - Selected row
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        northPanel.add(selRSpinner, gbc);

        // - Selected col
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        northPanel.add(selCSpinner, gbc);



        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // - Row
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        centerPanel.add(new ILabel("r: ", ILabel.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 5;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        centerPanel.add(rSpinner, gbc);

        // - Col
        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        centerPanel.add(new ILabel("c: ", ILabel.RIGHT), gbc);
        gbc.gridx = 3; gbc.weightx = 5;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        centerPanel.add(cSpinner, gbc);

        // - Width
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        centerPanel.add(new ILabel("w: ", ILabel.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 5;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        centerPanel.add(wSpinner, gbc);

        // - Height
        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        centerPanel.add(new ILabel("h: ", ILabel.RIGHT), gbc);
        gbc.gridx = 3; gbc.weightx = 5;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        centerPanel.add(hSpinner, gbc);


        getContentPanel().add(northPanel, BorderLayout.NORTH);
        getContentPanel().add(centerPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object object) {

    }
}
