package com.waldo.inventory.gui.dialogs.kccomponentorderdialog;

import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.classes.kicad.KcComponent;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.kccomponentorderdialog.extras.KcOrderItemPanel;
import com.waldo.inventory.gui.dialogs.linkitemdialog.extras.LinkKcPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionListener;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class KcComponentOrderDialogLayout extends IDialog implements ActionListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     LinkKcPanel kcPanel;
     KcOrderItemPanel oiPanel;

     JButton addToOrderBtn;

     KcComponent selectedComponent;
     OrderItem selectedOrderItem;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    KcComponentOrderDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        addToOrderBtn.setEnabled(selectedComponent != null && selectedOrderItem != null);


    }

    void addTableListeners(ListSelectionListener kcListListener, ListSelectionListener orderListListener) {
        kcPanel.addListSelectionListener(kcListListener);
        oiPanel.addListSelectionListener(orderListListener);
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("Common.Order", 48));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Panels
        kcPanel = new LinkKcPanel(application);
        oiPanel = new KcOrderItemPanel(application);

        kcPanel.setSortByRefButtonVisible(false);

        // Button
        addToOrderBtn = new JButton(imageResource.readImage("Common.Order", 32));
        addToOrderBtn.setToolTipText("Add to order");
        addToOrderBtn.setEnabled(false);
        addToOrderBtn.addActionListener(this);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.X_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(addToOrderBtn);

        getContentPanel().add(kcPanel);
        getContentPanel().add(buttonPanel);
        getContentPanel().add(oiPanel);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,5,10,5));

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null && object instanceof List) {
            kcPanel.setItemList((List<KcComponent>)object);
        }
    }
}