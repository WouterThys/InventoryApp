package com.waldo.inventory.gui.panels.orderspanel;

import com.waldo.inventory.classes.dbclasses.AbstractOrderLine;
import com.waldo.inventory.classes.dbclasses.DistributorPartLink;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.editdistributorpartlinkdialog.EditDistributorPartLinkDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.panels.mainpanel.ItemDetailListener;
import com.waldo.inventory.gui.panels.mainpanel.preview.ItemPreviewPanel;
import com.waldo.inventory.gui.panels.projectspanel.preview.ProjectPcbPreviewPanel;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public class OrderLineDetailsPanel extends IPanel implements ItemDetailListener {

    private static final String ITEM_PREVIEW = "ItemsPreview";
    private static final String PCB_PREVIEW = "PcbPreview";

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ItemPreviewPanel itemPreviewPanel;
    private ProjectPcbPreviewPanel pcbPreviewPanel;
    private JPanel previewPanel;
    private CardLayout cardLayout;

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

    private void editOrderLine(AbstractOrderLine orderLine) {
        if (orderLine != null && orderLine.getLine() != null) {
            if (orderLine.getLine() instanceof Item) {
                EditItemDialog dialog = new EditItemDialog<>(application, "Item", (Item)orderLine.getLine());
                dialog.showDialog();
            } else {
                // TODO edit pcb
            }
        }
    }

    private void deleteOrderLine(AbstractOrderLine orderLine) {
        if (orderLine != null) {
            int res = JOptionPane.showConfirmDialog(
                    application,
                    "Delete " + orderLine.getLine() + " from order?",
                    "Delete",
                    JOptionPane.YES_NO_OPTION
            );
            if (res == JOptionPane.YES_OPTION) {
                orderLine.delete();
            }
        }
    }

    private void editDistributorLink() {
        if (selectedOrderLine != null && !selectedOrderLine.isLocked()) {
            DistributorPartLink link = selectedOrderLine.getDistributorPartLink();
            if (link == null) {
                long id = selectedOrderLine.getLineId();
                link = new DistributorPartLink(selectedOrderLine.getOrder().getDistributor(), id);
            }
            EditDistributorPartLinkDialog dialog = new EditDistributorPartLinkDialog(application, link);
            dialog.enableDistributor(false);
            if (dialog.showDialog() == IDialog.OK) {
                link.save();
            }
        }
    }

    private void plusOne() {
        if (selectedOrderLine != null && !selectedOrderLine.isLocked()) {
            selectedOrderLine.setAmount(selectedOrderLine.getAmount() + 1);
            selectedOrderLine.save();
        }
    }

    private void minusOne() {
        if (selectedOrderLine != null && !selectedOrderLine.isLocked()) {
            selectedOrderLine.setAmount(selectedOrderLine.getAmount() - 1);
            selectedOrderLine.save();
        }
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
                SwingUtilities.invokeLater(() -> deleteOrderLine(selectedOrderLine));
            }

            @Override
            public void onToolBarEdit(IdBToolBar source) {
                SwingUtilities.invokeLater(() -> editOrderLine(selectedOrderLine));
            }
        };
        pcbPreviewPanel = new ProjectPcbPreviewPanel(application, true) {
            @Override
            public void onToolBarDelete(IdBToolBar source) {
                SwingUtilities.invokeLater(() -> deleteOrderLine(selectedOrderLine));
            }

            @Override
            public void onToolBarEdit(IdBToolBar source) {
                SwingUtilities.invokeLater(() -> editOrderLine(selectedOrderLine));
            }
        };

        previewPanel = new JPanel();
        cardLayout = new CardLayout();
        previewPanel.setLayout(cardLayout);
        previewPanel.add(ITEM_PREVIEW, itemPreviewPanel);
        previewPanel.add(PCB_PREVIEW, pcbPreviewPanel);


        editPriceAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> editDistributorLink());
            }
        };
        plusOneAction = new IActions.PlusOneAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> plusOne());
            }
        };
        minOneAction = new IActions.MinOneAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> minusOne());
            }
        };
        editReferenceAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> editDistributorLink());
            }
        };
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPanel);
        gbc.addLine("Amount", imageResource.readIcon("Amount.S"), GuiUtils.createComponentWithActions(amountTf, plusOneAction, minOneAction));
        gbc.addLine("Price", imageResource.readIcon("Value.S"), GuiUtils.createComponentWithActions(priceTf, editPriceAction));
        gbc.addLine("Distributor reference", imageResource.readIcon("Dictionary.SS"), GuiUtils.createComponentWithActions(referenceTf, editReferenceAction));

        add(previewPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);

    }

    @Override
    public void updateComponents(Object... objects) {
        if (objects != null && objects.length > 0) {
            selectedOrderLine = (AbstractOrderLine) objects[0];
        }

        if (selectedOrderLine != null) {
            amountTf.setText(String.valueOf(selectedOrderLine.getAmount()));
            priceTf.setText(selectedOrderLine.getPrice().toString());
            if (selectedOrderLine.getDistributorPartLink() != null) {
                referenceTf.setText(selectedOrderLine.getDistributorPartLink().getReference());
            } else {
                referenceTf.setText("");
            }

            if (selectedOrderLine.getLine() != null) {
                if (selectedOrderLine.getLine() instanceof Item) {
                    itemPreviewPanel.updateComponents((Item) selectedOrderLine.getLine());
                    cardLayout.show(previewPanel, ITEM_PREVIEW);
                } else {
                    pcbPreviewPanel.updateComponents((ProjectPcb) selectedOrderLine.getLine());
                    cardLayout.show(previewPanel, PCB_PREVIEW);
                }
            }

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
