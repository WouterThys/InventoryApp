package com.waldo.inventory.gui.dialogs.editcreatedlinkspcbdialog;

import com.waldo.inventory.Utils.Statics.SolderItemState;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.components.IImagePanel;
import com.waldo.inventory.gui.components.IMenuBar;
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


abstract class EditCreatedPcbLinksDialogLayout extends ICacheDialog implements IEditedListener {

    private static final ImageIcon receivedIcon = imageResource.readIcon("Actions.Received");
    private static final ImageIcon inProgressIcon = imageResource.readIcon("Actions.Created");
    private static final ImageIcon doneIcon = imageResource.readIcon("Actions.Ok");
    private static final ImageIcon destroyedIcon = imageResource.readIcon("Actions.Destroyed");

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
    private IImagePanel pcbImagePanel;
    private JProgressBar solderProgressPb;
    private ILabel solderProgressLbl;

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
    private IActions.WizardAction solderWizardAction;
    private IActions.IAction editRemarksAa;
    private IActions.IAction copyLinkAction;
    private IActions.IAction solderInfoAction;
    private IActions.IAction selectAllAction;
    private IActions.IAction recreateSolderItemsAction;

    // Pcb item actions
    private IActions.EditAction editPcbItemAction;
    private IActions.EditAction editLinkedItemAction;

    // Created pcb actions
    private IActions.UseAction pcbDoneAction;
    private IActions.DeleteAction destroyPcbAction;
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
    EditCreatedPcbLinksDialogLayout(Window window, String title, ProjectPcb projectPcb, CreatedPcb createdPcb) {
        super(window, title);

        this.projectPcb = projectPcb;
        this.createdPcb = createdPcb;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract void onLinkTableDoubleClicked();
    abstract void onSolderTableDoubleClicked();

    abstract void onEditPcbItem(CreatedPcbLink link);
    abstract void onEditLinkedItem(CreatedPcbLink link);

    abstract void onSearchUsedItem(CreatedPcbLink link);
    abstract void onDeleteUsedItem();
    abstract void onCopyLink(List<SolderItem> selectedItems, CreatedPcbLink link, boolean overwriteNotUsed, boolean overwriteIfAlreadyHasItem, boolean logging);
    abstract void onNotUsed();
    abstract void onSetSoldered(List<SolderItem> solderItems, boolean decrementStock, boolean overWriteNotUsed, boolean logging);
    abstract void onDesoldered(boolean incrementStock);
    abstract void onSolderWizard(CreatedPcbLink link);
    abstract void onOrderInfo(SolderItem solderItem);
    abstract void onRecreateSolderItems(CreatedPcbLink link);
    abstract void onEditRemark(SolderItem solderItem);

    abstract void onSetPcbDone(CreatedPcb createdPcb);
    abstract void onDestroyPcb(CreatedPcb createdPcb);
    abstract void onRemoveAll(CreatedPcb createdPcb);
    abstract void onCalculatePrice(CreatedPcb createdPcb);

    void updateEnabledComponents() {
        boolean enabled = selectedLink != null;
        boolean hasLink = enabled && selectedLink.getPcbItemItemLink() != null;
        boolean isSoldered = createdPcb != null && createdPcb.isSoldered();
        boolean oneSelected = solderItemTable.getSelectedItem() != null;
        boolean moreSelected = solderItemTable.getSelectedItems().size() > 0;

        editPcbItemAction.setEnabled(enabled);
        editLinkedItemAction.setEnabled(hasLink);

        editRemarksAa.setEnabled(oneSelected);
        searchUsedItemAction.setEnabled(oneSelected || moreSelected);
        copyLinkAction.setEnabled((oneSelected || moreSelected) && hasLink);
        solderInfoAction.setEnabled(oneSelected);
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

        calculatePriceAction.setEnabled(isSoldered);
        removeAllAction.setEnabled(isSoldered);
    }

    private void initLinkTable(CreatedPcb createdPcb) {
        if (createdPcb != null) {
            List<CreatedPcbLink> linkList = createdPcb.getCreatedPcbLinks();
            linkTableModel.setItemList(linkList);
            // Select first
            selectedLink = null;
            if (linkList.size() > 0) {
                linkTable.selectItem(linkList.get(0));
            }
        } else {
            linkTableModel.clearItemList();
        }
    }

    void initSolderItemTable(CreatedPcbLink link) {
        if (link != null) {
            List<SolderItem> solderItemList = link.getSolderItems();
            solderTableModel.setItemList(solderItemList);
            // Select first
            if (solderItemList.size() > 0) {
                solderItemTable.selectItem(solderItemList.get(0));
            }
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
        updateSolderProgress();
        if (createdPcb != null) {
            pcbNameTf.setText(createdPcb.toString());
            pcbCreatedTf.setText(DateUtils.formatDateTime(createdPcb.getDateCreated()));
            if (!createdPcb.getIconPath().isEmpty()) {
                pcbImagePanel.setImage(createdPcb.getIconPath());
            } else {
                pcbImagePanel.setImage((ImageIcon)null);
            }
        } else {
            pcbNameTf.setText("");
            pcbCreatedTf.setText("");
            pcbImagePanel.setImage((ImageIcon)null);
        }
        updateLinkInfo(link);
    }

    void updateSolderProgress() {
        if (createdPcb != null) {
            solderProgressPb.setValue(createdPcb.getAmountDone());
            solderProgressPb.setString(createdPcb.getAmountDone() + " / " + createdPcb.getAmountOfSolderItems());

            String tooltip =
                    createdPcb.getAmountSoldered() + " items soldered, " +
                    createdPcb.getAmountNotUsed() + " items not used";

            solderProgressPb.setToolTipText(tooltip);

            if (createdPcb.isDestroyed()) {
                solderProgressLbl.setIcon(destroyedIcon);
            } else {
                int total = createdPcb.getAmountOfSolderItems();
                int done = createdPcb.getAmountDone();

                if (done == 0) {
                    solderProgressLbl.setIcon(receivedIcon);
                } else if (done < total) {
                    solderProgressLbl.setIcon(inProgressIcon);
                } else {
                    solderProgressLbl.setIcon(doneIcon);
                }
            }

        } else {
            solderProgressLbl.setIcon(null);
            solderProgressPb.setValue(0);
        }
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

        JPanel progressPnl = new JPanel(new BorderLayout());
        progressPnl.add(solderProgressPb, BorderLayout.CENTER);
        progressPnl.add(solderProgressLbl, BorderLayout.EAST);

        JPanel infoPnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("Project: ", projectPcbTf);
        gbc.addLine("PCB name: ", pcbNameTf);
        gbc.addLine("Created: ", pcbCreatedTf);
        gbc.addLine("Progress: ", progressPnl);

        JPanel mainPnl = new JPanel(new BorderLayout());
        JToolBar tb = GuiUtils.createNewToolbar(removeAllAction, calculatePriceAction);

        JPanel tbPanel = new JPanel(new BorderLayout());
        tbPanel.add(tb, BorderLayout.WEST);
        tbPanel.add(GuiUtils.createNewToolbar(pcbDoneAction, destroyPcbAction), BorderLayout.EAST);

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

    private IMenuBar createSolderMenu() {
        IMenuBar menuBar = new IMenuBar();

        menuBar.addGroup(" ", recreateSolderItemsAction, solderInfoAction);
        menuBar.addGroup("Solder state", solderedAction, desolderedAction);
        menuBar.addGroup("Used item", 2, copyLinkAction, searchUsedItemAction, deleteUsedItemAction, notUsedAction);
        menuBar.addGroup("Other", selectAllAction, solderWizardAction);

        return menuBar;
    }

    private JPanel createSolderInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(remarksTp);
        scrollPane.setPreferredSize(new Dimension(200, 100));

        JPanel remarksPnl = new JPanel(new BorderLayout());
        remarksPnl.add(GuiUtils.createNewToolbar(editRemarksAa), BorderLayout.NORTH);
        remarksPnl.add(scrollPane, BorderLayout.CENTER);

        //panel.add(toolbarPnl, BorderLayout.NORTH);
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
        gbc.addLine(pcbItemLbl, GuiUtils.createComponentWithActions(pcbItemTf, editPcbItemAction));
        gbc.addLine(linkedItemLbl, GuiUtils.createComponentWithActions(linkedItemTf, editLinkedItemAction));

        //panel.add(GuiUtils.createNewToolbar(solderAllWizardAction), BorderLayout.NORTH);
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

        JPanel toolbarPnl = new JPanel(new BorderLayout());
        toolbarPnl.add(createSolderMenu(), BorderLayout.EAST);

        // Tables
        JScrollPane linkScrollPane = new JScrollPane(linkTable);
        linkScrollPane.setPreferredSize(new Dimension(500, 500));

        JScrollPane solderScrollPane = new JScrollPane(solderItemTable);
        solderScrollPane.setPreferredSize(new Dimension(500, 500));

        // Together
        eastPnl.add(linkScrollPane, BorderLayout.CENTER);
        eastPnl.add(createLinkInfoPanel(), BorderLayout.SOUTH);

        westPnl.add(toolbarPnl, BorderLayout.NORTH);
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

        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        defaults.put("nimbusOrange", new Color(10, 180, 0));
        solderProgressPb = new JProgressBar();
        solderProgressPb.setStringPainted(true);
        solderProgressPb.setPreferredSize(new Dimension(100, 25));

        solderProgressLbl = new ILabel(receivedIcon, SwingConstants.CENTER);
        solderProgressLbl.setPreferredSize(new Dimension(24,24));

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
        editPcbItemAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditPcbItem(selectedLink);
            }
        };
        editLinkedItemAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditLinkedItem(selectedLink);
            }
        };

        // Soldered item actions
        recreateSolderItemsAction = new IActions.IAction("Recreate", imageResource.readIcon("Actions.M.Rename")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRecreateSolderItems(selectedLink);
            }
        };
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
        selectAllAction = new IActions.IAction("Select all", imageResource.readIcon("Actions.M.SelectAll")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                solderItemTable.selectAll();
            }
        };
        copyLinkAction = new IActions.IAction("Copy link", imageResource.readIcon("Actions.M.CopyLink")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCopyLink(getSelectedSolderItems(), selectedLink, true, true, false);
            }
        };
        solderInfoAction = new IActions.IAction("Solder info", imageResource.readIcon("Actions.M.Info")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOrderInfo(getSelectedSolderItem());
            }
        };
        solderWizardAction = new IActions.WizardAction("Solder wizard", imageResource.readIcon("Actions.M.Wizard")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSolderWizard(selectedLink);
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
                onSetSoldered(getSelectedSolderItems(), true,true, false);
            }
        };
        desolderedAction = new IActions.DesolderedAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDesoldered(true);
            }
        };

        // Created pcb action
        pcbDoneAction = new IActions.UseAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSetPcbDone(createdPcb);
            }
        };
        pcbDoneAction.setIcon(imageResource.readIcon("Actions.M.Ok"));
        pcbDoneAction.setTooltip("Set PCB soldered");

        destroyPcbAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDestroyPcb(createdPcb);
            }
        };
        destroyPcbAction.setIcon(imageResource.readIcon("Actions.M.Destroyed"));
        destroyPcbAction.setTooltip("Destroy PCB");

        editRemarksAa = new IActions.IAction("Edit remarks", imageResource.readIcon("Actions.EditRemark")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditRemark(solderItemTable.getSelectedItem());
            }
        };
        editRemarksAa.putValue(AbstractAction.LONG_DESCRIPTION, "Edit remarks");
        editRemarksAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Edit remarks");
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

        headerPanel.setBorder(GuiUtils.createInlineTitleBorder("PCB"));
        pcbItemsPanel.setBorder(GuiUtils.createInlineTitleBorder("Soldering"));

        getContentPanel().add(headerPanel, BorderLayout.NORTH);
        getContentPanel().add(pcbItemsPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        initLinkTable(createdPcb);
        updateInfo(projectPcb, createdPcb, selectedLink);

        // Progress
        if (createdPcb != null) {
            solderProgressPb.setMaximum(createdPcb.getAmountOfSolderItems());
            solderProgressPb.setMinimum(0);
            updateSolderProgress();
        } else {
            solderProgressPb.setVisible(false);
        }

        linkTable.resizeColumns();
        updateEnabledComponents();
    }
}




//    private IMenuBar createSolderMenu() {
//        JPanel tbPanel = new JPanel();
//
//        JPanel recreatePnl = new JPanel(new BorderLayout());
//        ILabel recreateLbl = new ILabel("  ", SwingConstants.CENTER);
//        recreateLbl.setFont(10, Font.ITALIC);
//        recreateLbl.setForeground(Color.gray);
//        JToolBar recreateTb = GuiUtils.createNewToolbar();
//        recreateTb.add((recreateSolderItemsAction));
//        recreatePnl.add(recreateLbl, BorderLayout.NORTH);
//        recreatePnl.add(recreateTb, BorderLayout.CENTER);
//
//        JButton test = new JButton(solderedAction);
//        test.setBorderPainted(false);
//
//        JPanel solderPnl = new JPanel(new BorderLayout());
//        ILabel solderLbl = new ILabel("Solder state", SwingConstants.CENTER);
//        solderLbl.setFont(10, Font.ITALIC);
//        solderLbl.setForeground(Color.gray);
//        JToolBar solderTb = GuiUtils.createNewToolbar();
//        solderTb.add((solderedAction));
//        solderTb.add((desolderedAction));
//        solderPnl.add(solderLbl, BorderLayout.NORTH);
//        solderPnl.add(solderTb, BorderLayout.CENTER);
//
//        JPanel usedPbl = new JPanel(new BorderLayout());
//        ILabel usedLbl = new ILabel("Used item", SwingConstants.CENTER);
//        usedLbl.setFont(10, Font.ITALIC);
//        usedLbl.setForeground(Color.gray);
//        JToolBar usedTb = GuiUtils.createNewToolbar();
//        usedTb.add((copyLinkAction));
//        usedTb.add((searchUsedItemAction));
//        usedTb.add((deleteUsedItemAction));
//        usedTb.add((notUsedAction));
//        usedPbl.add(usedLbl, BorderLayout.NORTH);
//        usedPbl.add(usedTb, BorderLayout.CENTER);
//
//        JPanel otherPnl = new JPanel(new BorderLayout());
//        ILabel otherLbl = new ILabel("Other", SwingConstants.CENTER);
//        otherLbl.setFont(10, Font.ITALIC);
//        otherLbl.setForeground(Color.gray);
//        JToolBar otherTb = GuiUtils.createNewToolbar();
//        otherTb.add((solderInfoAction));
//        otherTb.add((selectAllAction));
//        otherTb.add((solderWizardAction));
//        otherPnl.add(otherLbl, BorderLayout.NORTH);
//        otherPnl.add(otherTb, BorderLayout.CENTER);
//
//        Box box = Box.createHorizontalBox();
//        box.add(recreatePnl);
//        box.add(new JSeparator(JSeparator.VERTICAL));
//        box.add(solderPnl);
//        box.add(new JSeparator(JSeparator.VERTICAL));
//        box.add(usedPbl);
//        box.add(new JSeparator(JSeparator.VERTICAL));
//        box.add(otherPnl);
//
//        tbPanel.add(box);
//        return tbPanel;
//    }