package com.waldo.inventory.gui.dialogs.editcreatedlinkspcbdialog;

import com.waldo.inventory.Utils.Statics.CreatedPcbLinkState;
import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.CreatedPcbLink;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.ICreatedPcbLinkTableModel;
import com.waldo.utils.DateUtils;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.waldo.inventory.gui.Application.imageResource;


abstract class EditCreatedPcbLinksDialogLayout extends IDialog implements IEditedListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ICreatedPcbLinkTableModel tableModel;
    private ITable<CreatedPcbLink> createdPcbTable;

    // Created pcb
    private ITextField projectPcbTf;
    private ITextField pcbNameTf;
    private ITextField pcbCreatedTf;
    private ITextField pcbSolderedTf;
    ILabel pcbImageLbl;


    // Link panel
    private ILabel stateLbl;
    private ITextField pcbItemTf;
    private ITextField linkedItemTf;
    private ITextField usedItemTf;
    private SpinnerNumberModel usedAmountSpModel;
    ISpinner usedAmountSp;
    private ITextPane remarksTp;

    // Pcb actions
    private IActions.AutoCalculateUsedAction autoCalculateUsedAction;

    // Pcb item actions
    private IActions.EditAction editItemAction;
    private IActions.SearchAction searchUsedItemAction;
    private IActions.DeleteAction deleteUsedItemAction;
    private IActions.UseAction createPcbAction;
    private IActions.DeleteAction destroyPcbAction;
    private IActions.NotUsedAction notUsedAction;
    private AbstractAction editRemarksAa;
    private IActions.WizardAction wizardAction;
    private IActions.RemoveAllAction removeAllAction;
    private IActions.DoItAction calculatePriceAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectPcb projectPcb;
    CreatedPcb createdPcb;
    CreatedPcbLink selectedLink;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditCreatedPcbLinksDialogLayout(Window window, String title, ProjectPcb projectPcb, CreatedPcb createdPcb) {
        super(window, title);

        this.projectPcb = projectPcb;
        this.createdPcb = createdPcb;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract void onImageIconDoubleClicked(CreatedPcb createdPcb, ILabel imageLabel);
    abstract void onAutoCalculateUsed(CreatedPcbLink link);
    abstract void onEditItem(CreatedPcbLink link);
    abstract void onSearchUsedItem(CreatedPcbLink link);
    abstract void onDeleteUsedItem(CreatedPcbLink link);
    abstract void onNotUsed(CreatedPcbLink link);
    abstract void onEditRemark(CreatedPcbLink link);

    abstract void onCreatePcb(CreatedPcb createdPcb);
    abstract void onDestroyPcb(CreatedPcb createdPcb);
    abstract void onMagicWizard(CreatedPcb createdPcb);
    abstract void onRemoveAll(CreatedPcb createdPcb);
    abstract void onCalculatePrice(CreatedPcb createdPcb);

    void updateEnabledComponents() {
        boolean enabled = selectedLink != null;
        boolean hasLink = enabled && selectedLink.getPcbItemItemLink() != null;
        boolean hasUsed = enabled && selectedLink.getUsedItemId() > DbObject.UNKNOWN_ID;
        boolean isSoldered = createdPcb != null && createdPcb.isSoldered();

        editItemAction.setEnabled(hasLink);
        searchUsedItemAction.setEnabled(!isSoldered && enabled);
        deleteUsedItemAction.setEnabled(!isSoldered && hasUsed);
        notUsedAction.setEnabled(enabled && !isSoldered && !selectedLink.getState().equals(CreatedPcbLinkState.NotUsed));

        autoCalculateUsedAction.setEnabled(!isSoldered && hasUsed);
        usedAmountSp.setEnabled(!isSoldered && hasUsed);

        createPcbAction.setEnabled(!isSoldered);
        destroyPcbAction.setEnabled(isSoldered);
        calculatePriceAction.setEnabled(isSoldered);
        editRemarksAa.setEnabled(enabled);
        removeAllAction.setEnabled(isSoldered);
    }

    private void initTable() {
        tableModel.setItemList(createdPcb.getCreatedPcbLinks());
        createdPcbTable.setPreferredScrollableViewportSize(createdPcbTable.getPreferredSize());
    }

    void updateTable() {
        tableModel.updateTable();
    }

    CreatedPcbLink getSelectedLink() {
        return selectedLink;
    }

    private void updateInfo(ProjectPcb projectPcb, CreatedPcb createdPcb, CreatedPcbLink link) {
        if (projectPcb != null) {
            projectPcbTf.setText(projectPcb.toString());
        } else {
            projectPcbTf.setText("");
        }
        if (createdPcb != null) {
            pcbNameTf.setText(createdPcb.toString());
            pcbCreatedTf.setText(DateUtils.formatDateTime(createdPcb.getDateCreated()));
            pcbSolderedTf.setText(DateUtils.formatDateTime(createdPcb.getDateSoldered()));
            if (!createdPcb.getIconPath().isEmpty()) {
                try {
                    ImageIcon icon = imageResource.readImage(createdPcb.getIconPath());
                    if (icon != null) {
                        icon = ImageResource.scaleImage(icon, pcbImageLbl.getSize());//new Dimension(250,100));
                    }
                    pcbImageLbl.setIcon(icon);
                } catch (Exception e) {
                    //
                }
            } else {
                pcbImageLbl.setIcon(null);
            }
        } else {
            pcbNameTf.setText("");
            pcbCreatedTf.setText("");
            pcbSolderedTf.setText("");
        }
        updateLinkInfo(link);
    }

    void updateLinkInfo(CreatedPcbLink link) {
        pcbItemTf.setText("");
        linkedItemTf.setText("");
        usedItemTf.setText("");
        usedAmountSp.setTheValue(0);
        stateLbl.setText(" ");
        stateLbl.setIcon(null);
        remarksTp.setFile(null);
        if (link != null) {
            CreatedPcbLinkState state = link.getState();
            if (state != CreatedPcbLinkState.Ok) {
                stateLbl.setIcon(state.getImageIcon());
                stateLbl.setForeground(state.getMessageColor());
                StringBuilder builder = null;
                boolean first = true;
                for (String m : state.getMessages()) {
                    if (first) {
                        builder = new StringBuilder(m);
                        first = false;
                    } else {
                        builder.append(", ").append(m);
                    }
                }
                if (builder != null) {
                    stateLbl.setText(builder.toString());
                }
            }

            usedAmountSp.setTheValue(link.getUsedAmount());
            remarksTp.setFile(link.getRemarksFile());

            if (link.getPcbItemProjectLink() != null) {
                pcbItemTf.setText(link.getPcbItemProjectLink().getPrettyName());
            }
            if (link.getPcbItemItemLink() != null) {
                linkedItemTf.setText(link.getPcbItemItemLink().getLinkedItemName());
                usedAmountSpModel.setMaximum(link.getPcbItemItemLink().getItem().getAmount());
            }
            if (link.getUsedItem() != null) {
                usedItemTf.setText(link.getUsedItem().toString());
            }
        }
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel infoPnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("Project: ", projectPcbTf);
        gbc.addLine("PCB name: ", pcbNameTf);
        gbc.addLine("Created: ", pcbCreatedTf);
        gbc.addLine("Soldered: ", pcbSolderedTf);

        JPanel mainPnl = new JPanel(new BorderLayout());
        JToolBar tb = GuiUtils.createNewToolbar(removeAllAction, wizardAction, calculatePriceAction);

        JPanel tbPanel = new JPanel(new BorderLayout());
        tbPanel.add(tb, BorderLayout.WEST);
        tbPanel.add(GuiUtils.createNewToolbar(createPcbAction, destroyPcbAction), BorderLayout.EAST);

        mainPnl.add(infoPnl, BorderLayout.CENTER);
        mainPnl.add(tbPanel, BorderLayout.PAGE_START);

        panel.add(pcbImageLbl, BorderLayout.WEST);
        panel.add(mainPnl, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 1),
                BorderFactory.createEmptyBorder(2,5,2,5)
        ));

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel infoPnl = new JPanel(new BorderLayout());
        ILabel pcbItemLbl = new ILabel("Pcb item: ", imageResource.readIcon("Projects.Details.Pcb"), SwingConstants.CENTER);
        ILabel linkedItemLbl = new ILabel("Linked item: ", imageResource.readIcon("Projects.Pcb.Linked"), SwingConstants.CENTER);
        ILabel usedItemLbl = new ILabel("Used item: ", imageResource.readIcon("Projects.Pcb.Used"), SwingConstants.CENTER);

        JPanel pnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(pnl);
        gbc.addLine(pcbItemLbl, pcbItemTf);
        gbc.addLine(linkedItemLbl, GuiUtils.createComponentWithActions(linkedItemTf, editItemAction));
        gbc.addLine(usedItemLbl, GuiUtils.createComponentWithActions(usedItemTf, searchUsedItemAction, deleteUsedItemAction, notUsedAction));
        gbc.addLine("Amount: ", GuiUtils.createComponentWithActions(usedAmountSp, autoCalculateUsedAction));

        JPanel remarksPnl = new JPanel(new BorderLayout());
        remarksPnl.add(GuiUtils.createNewToolbar(editRemarksAa), BorderLayout.NORTH);
        remarksPnl.add(new JScrollPane(remarksTp), BorderLayout.CENTER);

        stateLbl.setBorder(BorderFactory.createEmptyBorder(3,5,3,5));

        infoPnl.add(stateLbl, BorderLayout.NORTH);
        infoPnl.add(pnl, BorderLayout.CENTER);

        panel.add(infoPnl, BorderLayout.NORTH);
        panel.add(remarksPnl, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPcbItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(createdPcbTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        panel.add(scrollPane);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        setResizable(true);
        showTitlePanel(false);

        projectPcbTf = new ITextField(false);
        pcbNameTf = new ITextField(false);
        pcbCreatedTf = new ITextField(false);
        pcbSolderedTf = new ITextField(false);
        pcbImageLbl = new ILabel();
        pcbImageLbl.setMinimumSize(new Dimension(200, 90));
        pcbImageLbl.setPreferredSize(new Dimension(250, 100));
        pcbImageLbl.setMaximumSize(new Dimension(300, 120));
        pcbImageLbl.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
        pcbImageLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onImageIconDoubleClicked(createdPcb, pcbImageLbl);
                }
            }
        });

        tableModel = new ICreatedPcbLinkTableModel();
        createdPcbTable = new ITable<>(tableModel);
        createdPcbTable.setPreferredScrollableViewportSize(createdPcbTable.getPreferredSize());
        createdPcbTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedLink = createdPcbTable.getSelectedItem();
                updateLinkInfo(selectedLink);
                updateEnabledComponents();
            }
        });

        stateLbl = new ILabel("Ok", CreatedPcbLinkState.Ok.getImageIcon(), SwingConstants.CENTER);
        stateLbl.setAlignmentX(CENTER_ALIGNMENT);
        stateLbl.setAlignmentY(CENTER_ALIGNMENT);
        pcbItemTf = new ITextField(false);
        linkedItemTf = new ITextField(false);
        usedItemTf = new ITextField(false);

        usedAmountSpModel = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
        usedAmountSp = new ISpinner(usedAmountSpModel);
        usedAmountSp.addEditedListener(this, "usedAmount");

        remarksTp = new ITextPane();
        remarksTp.setEditable(false);

        autoCalculateUsedAction = new IActions.AutoCalculateUsedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAutoCalculateUsed(selectedLink);
            }
        };
        editItemAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditItem(selectedLink);
            }
        };
        searchUsedItemAction = new IActions.SearchAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSearchUsedItem(selectedLink);
            }
        };
        deleteUsedItemAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteUsedItem(selectedLink);
            }
        };
        notUsedAction = new IActions.NotUsedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNotUsed(selectedLink);
            }
        };
        createPcbAction = new IActions.UseAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreatePcb(createdPcb);
            }
        };
        createPcbAction.setIcon(imageResource.readIcon("Actions.M.Created"));
        createPcbAction.setTooltip("Set PCB soldered");

        destroyPcbAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDestroyPcb(createdPcb);
            }
        };
        destroyPcbAction.setIcon(imageResource.readIcon("Actions.M.Destroyed"));
        destroyPcbAction.setTooltip("Destroy PCB");

        editRemarksAa = new AbstractAction("Edit remarks", imageResource.readIcon("Actions.EditRemark")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditRemark(selectedLink);
            }
        };
        editRemarksAa.putValue(AbstractAction.LONG_DESCRIPTION, "Edit remarks");
        editRemarksAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Edit remarks");
        wizardAction = new IActions.WizardAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onMagicWizard(createdPcb);
            }
        };
        wizardAction.setIcon(imageResource.readIcon("Actions.M.Wizard"));
        removeAllAction = new IActions.RemoveAllAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemoveAll(createdPcb);
            }
        };

        calculatePriceAction = new IActions.DoItAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCalculatePrice(createdPcb);
            }
        };
        calculatePriceAction.setTooltip("Calculate estimated price");
        calculatePriceAction.setIcon(imageResource.readIcon("Actions.M.Calculate"));
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel infoPanel = createInfoPanel();
        JPanel headerPanel = createHeaderPanel();
        JPanel pcbItemsPanel = createPcbItemsPanel();

        JPanel mainPanel = new JPanel(new BorderLayout());
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pcbItemsPanel, infoPanel);
        centerSplitPane.setResizeWeight(1);

        mainPanel.add(centerSplitPane, BorderLayout.CENTER);

        getContentPanel().add(headerPanel, BorderLayout.NORTH);
        getContentPanel().add(mainPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        initTable();

        updateInfo(projectPcb, createdPcb, selectedLink);
        createdPcbTable.resizeColumns();
        updateEnabledComponents();
    }
}