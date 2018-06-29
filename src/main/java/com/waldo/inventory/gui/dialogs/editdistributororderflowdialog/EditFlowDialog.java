package com.waldo.inventory.gui.dialogs.editdistributororderflowdialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DistributorOrderFlow;
import com.waldo.inventory.gui.components.IObjectDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ISpinner;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

class EditFlowDialog extends IObjectDialog<DistributorOrderFlow> {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField nameTf;
    private ILabel iconLbl; // TODO
    private ISpinner sequenceNumberSp;
    private IComboBox<Statics.OrderStates> orderStateCb;
    private ITextField descriptionTf;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public EditFlowDialog(Window parent, DistributorOrderFlow originalObject) {
        super(parent, "ItemOrder flow", originalObject, DistributorOrderFlow.class);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }
    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public DistributorOrderFlow getOrderFlow() {
        return super.getObject();
    }

    @Override
    public VerifyState verify(DistributorOrderFlow toVerify) {
        VerifyState ok = VerifyState.Ok;

        String name = nameTf.getText();
        if (name == null || name.isEmpty()) {
            nameTf.setError("Name can not be empty..");
            ok = VerifyState.Error;
        } else {
//            if (getOrderFlow().getId() <= DbObject.UNKNOWN_ID) {
//                DistributorOrderFlow foundFlow = SearchManager.sm().findDistributorOrderFlow()
//            }
        }

        return ok;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        nameTf = new ITextField(this, "name");
        // Label...
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        sequenceNumberSp = new ISpinner(spinnerModel);
        sequenceNumberSp.addEditedListener(this, "sequenceNumber");
        sequenceNumberSp.setPreferredSize(new Dimension(40, 25));
        orderStateCb = new IComboBox<>(Statics.OrderStates.values());
        orderStateCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                getOrderFlow().setOrderState((Statics.OrderStates) orderStateCb.getSelectedItem());
                onValueChanged(orderStateCb, "orderState", null, null);
            }
        });
        descriptionTf = new ITextField(this, "description");
    }

    @Override
    public void initializeLayouts() {

        JPanel spinnerNamePnl = new JPanel(new BorderLayout());
        spinnerNamePnl.add(sequenceNumberSp, BorderLayout.WEST);
        spinnerNamePnl.add(nameTf, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        GuiUtils.GridBagHelper gbh = new GuiUtils.GridBagHelper(panel);
        gbh.addLine("Seq/Name: ", spinnerNamePnl);
        gbh.addLine("Description: ", descriptionTf);
        gbh.addLine("In state: ", orderStateCb);

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(panel, BorderLayout.CENTER);
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,10,5));

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (getOrderFlow() != null) {
            nameTf.setText(getOrderFlow().getName());
            descriptionTf.setText(getOrderFlow().getDescription());
            sequenceNumberSp.setTheValue(getOrderFlow().getSequenceNumber());
            orderStateCb.setSelectedItem(getOrderFlow().getOrderState());
        }
    }
}