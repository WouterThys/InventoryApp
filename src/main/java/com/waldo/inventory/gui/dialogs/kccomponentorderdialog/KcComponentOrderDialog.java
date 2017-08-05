package com.waldo.inventory.gui.dialogs.kccomponentorderdialog;

import com.waldo.inventory.classes.kicad.KcComponent;
import com.waldo.inventory.gui.Application;

import java.awt.event.ActionEvent;
import java.util.List;

public class KcComponentOrderDialog extends KcComponentOrderDialogLayout {


    public KcComponentOrderDialog(Application application, String title, List<KcComponent> componentList) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(componentList);

    }


    //
    // Dialog stuff
    //
    @Override
    protected void onOK() {
        super.onOK();
    }

    @Override
    protected void onCancel() {
        super.onCancel();
    }

    //
    // Button press
    //
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}