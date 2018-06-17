package com.waldo.inventory.gui.panels.orderspanel;

import com.waldo.inventory.classes.dbclasses.AbstractOrderLine;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.panels.mainpanel.ItemDetailListener;
import com.waldo.inventory.gui.panels.mainpanel.preview.ItemPreviewPanel;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class OrderLineDetailsPanel extends IPanel implements ItemDetailListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ItemPreviewPanel itemPreviewPanel;
    // TODO private PcbPreviewPanel pcbPreviewPanel;

    // Shared for PCB and ITEM orders
    private ITextField amountTf;
    private ITextField priceTf;
    private ITextField referenceTf;

    private IActions.EditAction editPriceAction;
    private IActions.PlusOneAction plusOneAction;
    private IActions.MinOneAction minOneAction;
    private IActions.EditAction editReferenceAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private AbstractOrderLine selectedOrderLine;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderLineDetailsPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        boolean enabled = selectedOrderLine != null;
        boolean locked = enabled && selectedOrderLine.isLocked();


        editPriceAction.setEnabled(!locked);
        plusOneAction.setEnabled(!locked);
        minOneAction.setEnabled(!locked);
        editReferenceAction.setEnabled(!locked);
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {

        amountTf = new ITextField(false);
        priceTf = new ITextField(false);
        referenceTf = new ITextField(false);


        itemPreviewPanel = new ItemPreviewPanel(this, true) {
            @Override
            public void onToolBarDelete(IdBToolBar source) {

            }

            @Override
            public void onToolBarEdit(IdBToolBar source) {

            }
        };

        // TODO pcbPreviewPanel


        editPriceAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
        plusOneAction = new IActions.PlusOneAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
        minOneAction = new IActions.MinOneAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
        editReferenceAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
         // TODO cards layout to switch between item and pcb layout

        JPanel infoPanel = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPanel);
        gbc.addLine("Amount: ", GuiUtils.createComponentWithActions(amountTf, plusOneAction, minOneAction));
        gbc.addLine("Price: ", GuiUtils.createComponentWithActions(priceTf, editPriceAction));
        gbc.addLine("Reference: ", GuiUtils.createComponentWithActions(referenceTf, editReferenceAction));


        // TODO add(cardLayout, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);

    }

    @Override
    public void updateComponents(Object... objects) {
        if (objects != null && objects.length > 0) {
            selectedOrderLine = (AbstractOrderLine) objects[0];
        }

        if (selectedOrderLine != null) {


            updateEnabledComponents();
            setVisible(true);
        } else {
            setVisible(false);
        }
    }


    @Override
    public void onShowDataSheet(Item item) {
        application.openDataSheet(item);
    }

    @Override
    public void onOrderItem(Item item) {
        // TODO: with OrderManager.orderItem
        application.orderItem(item);
    }

    @Override
    public void onShowHistory(Item item) {
        application.showHistory(item);
    }
}
