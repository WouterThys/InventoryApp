package com.waldo.inventory.gui.dialogs.editcreatedlinkspcbdialog;

import com.waldo.inventory.Utils.Statics.SolderItemState;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.components.IImagePanel;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.ICreatedPcbLinkTableModel;
import com.waldo.inventory.gui.components.tablemodels.ISolderItemTableModel;
import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.utils.DateUtils;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;


abstract class EditCreatedPcbLinksCacheDialogLayout extends ICacheDialog implements IEditedListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ICreatedPcbLinkTableModel linkTableModel;
    private ITable<CreatedPcbLink> linkTable;

    private ISolderItemTableModel solderTableModel;
    private ITable<SolderItem> solderItemTable;

    // Created pcb
    private ITextField projectPcbTf;
    private ITextField pcbNameTf;
    private ITextField pcbCreatedTf;
    private ITextField pcbSolderedTf;
    private IImagePanel pcbImagePanel;

    // Link
    private ITextField pcbItemTf;
    private ITextField linkedItemTf;

    // Soldered item
    private ITextPane remarksTp;

    private IActions.SearchAction searchUsedItemAction;
    private IActions.DeleteAction deleteUsedItemAction;
    private IActions.NotUsedAction notUsedAction;
    private IActions.SolderedAction solderedAction;
    private IActions.DesolderedAction desolderedAction;
    private AbstractAction editRemarksAa;

    // Pcb item actions
    private IActions.EditAction editItemAction;

    // Created pcb actions
    private IActions.UseAction createPcbAction;
    private IActions.DeleteAction destroyPcbAction;
    private IActions.WizardAction wizardAction;
    private IActions.RemoveAllAction removeAllAction;
    private IActions.DoItAction calculatePriceAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ProjectPcb projectPcb;
    CreatedPcb createdPcb;
    CreatedPcbLink selectedLink;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditCreatedPcbLinksCacheDialogLayout(Window window, String title, ProjectPcb projectPcb, CreatedPcb createdPcb) {
        super(window, title);

        this.projectPcb = projectPcb;
        this.createdPcb = createdPcb;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract void onLinkTableDoubleClicked();
    abstract void onSolderTableDoubleClicked();

    abstract void onEditItem(CreatedPcbLink link);
    abstract void onSearchUsedItem(CreatedPcbLink link);
    abstract void onDeleteUsedItem();
    abstract void onNotUsed();
    abstract void onSoldered();
    abstract void onDesoldered();
    abstract void onEditRemark(SolderItem solderItem);

    abstract void onCreatePcb(CreatedPcb createdPcb);
    abstract void onDestroyPcb(CreatedPcb createdPcb);
    abstract void onMagicWizard(CreatedPcb createdPcb);
    abstract void onRemoveAll(CreatedPcb createdPcb);
    abstract void onCalculatePrice(CreatedPcb createdPcb);

    void updateEnabledComponents() {
        boolean enabled = selectedLink != null;
        boolean hasLink = enabled && selectedLink.getPcbItemItemLink() != null;
        boolean isSoldered = createdPcb != null && createdPcb.isSoldered();
        boolean oneSelected = solderItemTable.getSelectedItem() != null;
        boolean moreSelected = solderItemTable.getSelectedItems().size() > 0;

        editItemAction.setEnabled(hasLink);

        editRemarksAa.setEnabled(oneSelected);
        searchUsedItemAction.setEnabled(oneSelected || moreSelected);
        deleteUsedItemAction.setEnabled(false);
        notUsedAction.setEnabled(false);
        solderedAction.setEnabled(false);
        desolderedAction.setEnabled(false);
        if (moreSelected) {
            for (SolderItem solderItem : getSelectedSolderItems()) {
                if (solderItem.getUsedItemId() > DbObject.UNKNOWN_ID) {
                    deleteUsedItemAction.setEnabled(true);
                }
                if (solderItem.getState() != SolderItemState.NotUsed) {
                    notUsedAction.setEnabled(true);
                }
                if (solderItem.getUsedItemId() > DbObject.UNKNOWN_ID && solderItem.getState() != SolderItemState.Soldered) {
                    solderedAction.setEnabled(true);
                }
                if (solderItem.getState() == SolderItemState.Soldered) {
                    desolderedAction.setEnabled(true);
                }
            }
        }

        createPcbAction.setEnabled(!isSoldered);
        destroyPcbAction.setEnabled(isSoldered);
        calculatePriceAction.setEnabled(isSoldered);
        removeAllAction.setEnabled(isSoldered);
    }

    private void initLinkTable(CreatedPcb createdPcb) {
        if (createdPcb != null) {
            linkTableModel.setItemList(createdPcb.getCreatedPcbLinks());
        } else {
            linkTableModel.clearItemList();
        }
    }

    private void initSolderItemTable(CreatedPcbLink link) {
        if (link != null) {
            solderTableModel.setItemList(link.getSolderItems());
        } else {
            solderTableModel.clearItemList();
        }
    }

    List<SolderItem> getSelectedSolderItems() {
        return solderItemTable.getSelectedItems();
    }

    SolderItem getSelectedSolderItem() {
        return solderItemTable.getSelectedItem();
    }

    void updateLinkTable() {
        linkTableModel.updateTable();
    }

    void updateSolderTable() {
        solderTableModel.updateTable();
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
                pcbImagePanel.setImage(createdPcb.getIconPath());
            } else {
                pcbImagePanel.setImage((ImageIcon)null);
            }
        } else {
            pcbNameTf.setText("");
            pcbCreatedTf.setText("");
            pcbSolderedTf.setText("");
            pcbImagePanel.setImage((ImageIcon)null);
        }
        updateLinkInfo(link);
    }

    void updateLinkInfo(CreatedPcbLink link) {
        pcbItemTf.setText("");
        linkedItemTf.setText("");
        if (link != null) {
            if (link.getPcbItemProjectLink() != null) {
                pcbItemTf.setText(link.getPcbItemProjectLink().getPrettyName());
            }
            if (link.getPcbItemItemLink() != null) {
                linkedItemTf.setText(link.getPcbItemItemLink().getLinkedItemName());
            }
        }
    }

    void updateSolderInfo(SolderItem solderItem) {
        if (solderItem != null) {
            remarksTp.setFile(solderItem.getRemarksFile());
        } else {
            remarksTp.setFile(null);
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

        panel.add(pcbImagePanel, BorderLayout.WEST);
        panel.add(mainPnl, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 1),
                BorderFactory.createEmptyBorder(2,5,2,5)
        ));

        return panel;
    }

    private JPanel createSolderInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JToolBar toolBar = GuiUtils.createNewToolbar();

        toolBar.add(solderedAction);
        toolBar.add(desolderedAction);
        toolBar.addSeparator();
        toolBar.add(searchUsedItemAction);
        toolBar.add(deleteUsedItemAction);
        toolBar.add(notUsedAction);

        JPanel toolbarPnl = new JPanel(new BorderLayout());
        toolbarPnl.add(toolBar, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(remarksTp);
        scrollPane.setPreferredSize(new Dimension(200, 100));

        JPanel remarksPnl = new JPanel(new BorderLayout());
        remarksPnl.add(GuiUtils.createNewToolbar(editRemarksAa), BorderLayout.NORTH);
        remarksPnl.add(scrollPane, BorderLayout.CENTER);

        panel.add(toolbarPnl, BorderLayout.NORTH);
        panel.add(remarksPnl, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(1,1,1,1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.lightGray, 1),
                        BorderFactory.createEmptyBorder(2,10,10,10)
                )
        ));

        return panel;
    }

    private JPanel createLinkInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        ILabel pcbItemLbl = new ILabel("Pcb item: ", imageResource.readIcon("Projects.Details.Pcb"), SwingConstants.CENTER);
        ILabel linkedItemLbl = new ILabel("Linked item: ", imageResource.readIcon("Projects.Pcb.Linked"), SwingConstants.CENTER);

        JPanel pnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(pnl);
        gbc.addLine(pcbItemLbl, pcbItemTf);
        gbc.addLine(linkedItemLbl, GuiUtils.createComponentWithActions(linkedItemTf, editItemAction));

        panel.add(pnl, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(1,1,1,1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.lightGray, 1),
                        BorderFactory.createEmptyBorder(2,10,10,10)
                )
        ));

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel eastPnl = new JPanel(new BorderLayout());
        JPanel westPnl = new JPanel(new BorderLayout());

        // Tables
        JScrollPane linkScrollPane = new JScrollPane(linkTable);
        linkScrollPane.setPreferredSize(new Dimension(500, 500));

        JScrollPane solderScrollPane = new JScrollPane(solderItemTable);
        solderScrollPane.setPreferredSize(new Dimension(500, 500));

        // Together
        eastPnl.add(linkScrollPane, BorderLayout.CENTER);
        eastPnl.add(createLinkInfoPanel(), BorderLayout.SOUTH);

        westPnl.add(solderScrollPane, BorderLayout.CENTER);
        westPnl.add(createSolderInfoPanel(), BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, eastPnl, westPnl);
        split.setResizeWeight(0.5);
        split.setOneTouchExpandable(true);

        panel.add(split, BorderLayout.CENTER);

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

        IEditedListener editedListener = new IEditedListener() {
            @Override
            public void onValueChanged(Component component, String s, Object o, Object o1) {
                if (createdPcb != null) {
                    createdPcb.save();
                }
            }
            @Override
            public Object getGuiObject() {
                return createdPcb;
            }
        };

        pcbImagePanel = new IImagePanel(this, ImageType.Other, "", editedListener, new Dimension(250, 170));

        linkTableModel = new ICreatedPcbLinkTableModel();
        linkTable = new ITable<>(linkTableModel);
        linkTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        linkTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedLink = linkTable.getSelectedItem();
                initSolderItemTable(selectedLink);
                updateLinkInfo(selectedLink);
                updateEnabledComponents();
            }
        });
        linkTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    onLinkTableDoubleClicked();
                }
            }
        });

        solderTableModel = new ISolderItemTableModel();
        solderItemTable = new ITable<>(solderTableModel);
        solderItemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSolderInfo(solderItemTable.getSelectedItem());
                updateEnabledComponents();
            }
        });
        solderItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    onSolderTableDoubleClicked();
                }
            }
        });

        pcbItemTf = new ITextField(false);
        linkedItemTf = new ITextField(false);

        remarksTp = new ITextPane();
        remarksTp.setEditable(false);

        // Pcb item actions
        editItemAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditItem(selectedLink);
            }
        };

        // Soldered item actions
        searchUsedItemAction = new IActions.SearchAction(imageResource.readIcon("Actions.M.Search")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSearchUsedItem(selectedLink);
            }
        };
        deleteUsedItemAction = new IActions.DeleteAction(imageResource.readIcon("Actions.M.Delete")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteUsedItem();
            }
        };
        notUsedAction = new IActions.NotUsedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNotUsed();
            }
        };
        solderedAction = new IActions.SolderedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSoldered();
            }
        };
        desolderedAction = new IActions.DesolderedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDesoldered();
            }
        };

        // Created pcb action
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
                onEditRemark(solderItemTable.getSelectedItem());
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

        JPanel headerPanel = createHeaderPanel();
        JPanel pcbItemsPanel = createMainPanel();

        getContentPanel().add(headerPanel, BorderLayout.NORTH);
        getContentPanel().add(pcbItemsPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        initLinkTable(createdPcb);
        updateInfo(projectPcb, createdPcb, selectedLink);
        linkTable.resizeColumns();
        updateEnabledComponents();
    }
}