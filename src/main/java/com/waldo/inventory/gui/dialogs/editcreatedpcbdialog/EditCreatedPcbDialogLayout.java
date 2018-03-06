package com.waldo.inventory.gui.dialogs.editcreatedpcbdialog;

import com.waldo.inventory.Utils.Statics.CreatedPcbLinkState;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.ICreatedPcbTableModel;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.DateUtils;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ISpinner;
import com.waldo.utils.icomponents.ITable;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;


abstract class EditCreatedPcbDialogLayout extends IDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ICreatedPcbTableModel tableModel;
    private ITable<CreatedPcbLink> createdPcbTable;

    // Created pcb
    private ITextField projectPcbTf;
    private ITextField pcbNameTf;
    private ITextField pcbDateTf;
    private ILabel pcbImageLbl;

    // Link panel
    private ILabel stateLbl;
    private ITextField pcbItemTf;
    private ITextField linkedItemTf;
    private ITextField usedItemTf;

    private SpinnerNumberModel usedAmountSpModel;
    ISpinner usedAmountSp;
    private IActions.AutoCalculateUsedAction autoCalculateUsedAction;

    private IActions.SaveAction saveAllAction;

    private IActions.EditAction editItemAction;
    private IActions.SearchAction searchUsedItemAction;
    private IActions.DeleteAction deleteUsedItemAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ProjectPcb projectPcb;
    private CreatedPcb createdPcb;
    private List<CreatedPcbLink> displayList = new ArrayList<>();

    private CreatedPcbLink selectedLink;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditCreatedPcbDialogLayout(Window window, String title, ProjectPcb projectPcb, CreatedPcb createdPcb) {
        super(window, title);

        this.projectPcb = projectPcb;
        this.createdPcb = createdPcb;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract void onAutoCalculateUsed(CreatedPcbLink link);
    abstract void onEditItem(CreatedPcbLink link);
    abstract void onSearchUsedItem(CreatedPcbLink link);
    abstract void onDeleteUsedItem(CreatedPcbLink link);
    abstract void onSaveAllAction(CreatedPcb createdPcb);

    void updateEnabledComponents() {
        boolean enabled = selectedLink != null;
        boolean hasLink = enabled && selectedLink.getPcbItemItemLink() != null;
        boolean hasUsed = enabled && selectedLink.getUsedItemId() > DbObject.UNKNOWN_ID;

        editItemAction.setEnabled(hasLink);
        searchUsedItemAction.setEnabled(enabled);
        deleteUsedItemAction.setEnabled(hasUsed);

        autoCalculateUsedAction.setEnabled(enabled);
        usedAmountSp.setEnabled(hasUsed);
    }

    void initTable() {
        if (projectPcb != null && createdPcb != null) {
            updateDisplayList(projectPcb, createdPcb);
            tableModel.setItemList(displayList);
        }
    }

    void updateTable() {
        tableModel.updateTable();
    }

    void updateInfo(ProjectPcb projectPcb, CreatedPcb createdPcb, CreatedPcbLink link) {
        if (projectPcb != null) {
            projectPcbTf.setText(projectPcb.toString());
        } else {
            projectPcbTf.setText("");
        }
        if (createdPcb != null) {
            pcbNameTf.setText(createdPcb.toString());
            pcbDateTf.setText(DateUtils.formatDateTime(createdPcb.getDateCreated()));
        } else {
            pcbNameTf.setText("");
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
        if (link != null) {
            CreatedPcbLinkState state = link.getState();
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

            usedAmountSp.setTheValue(link.getUsedAmount());
        }
    }

    private void updateDisplayList(ProjectPcb projectPcb, CreatedPcb createdPcb) {
        displayList.clear();
        List<PcbItemProjectLink> pcbItemList = projectPcb.getPcbItemList();
        List<CreatedPcbLink> createdPcbLinkList = new ArrayList<>(SearchManager.sm().findCreatedPcbLinks(projectPcb.getId(), createdPcb.getId()));

        for (PcbItemProjectLink pipl : pcbItemList) {
            CreatedPcbLink link = findPcbItem(createdPcbLinkList, pipl.getPcbItemId());
            if (link != null) {
                createdPcbLinkList.remove(link);
            } else {
                link = new CreatedPcbLink(pipl.getId(), createdPcb.getId(), 0);
                if (pipl.getPcbItemItemLinkId() > DbObject.UNKNOWN_ID) {
                    link.setUsedItemId(pipl.getPcbItemItemLink().getItemId());
                }
            }
            displayList.add(link);
        }
    }

    private CreatedPcbLink findPcbItem(List<CreatedPcbLink> searchList, long pcbItemId) {
        for (CreatedPcbLink cpl : searchList) {
            if (cpl.getPcbItemProjectLinkId() > DbObject.UNKNOWN_ID) {
                if (cpl.getPcbItemProjectLink().getPcbItemId() == pcbItemId) {
                    return cpl;
                }
            }
        }
        return null;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();

        JPanel infoPnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("Project: ", projectPcbTf);
        gbc.addLine("PCB name: ", pcbNameTf);
        gbc.addLine("Created: ", pcbDateTf);

        panel.add(infoPnl, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 1),
                BorderFactory.createEmptyBorder(2,5,2,5)
        ));

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel infoPnl = new JPanel(new BorderLayout());
        ILabel pcbItemLbl = new ILabel("Pcb item: ", imageResource.readImage("Projects.Details.Pcb"), SwingConstants.CENTER);
        ILabel linkedItemLbl = new ILabel("Linked item: ", imageResource.readImage("Projects.Pcb.Linked"), SwingConstants.CENTER);
        ILabel usedItemLbl = new ILabel("Used item: ", imageResource.readImage("Projects.Pcb.Used"), SwingConstants.CENTER);

        JPanel pnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(pnl);
        gbc.addLine(pcbItemLbl, pcbItemTf);
        gbc.addLine(linkedItemLbl, GuiUtils.createComponentWithActions(linkedItemTf, editItemAction));
        gbc.addLine(usedItemLbl, GuiUtils.createComponentWithActions(usedItemTf, searchUsedItemAction, deleteUsedItemAction));
        gbc.addLine("Amount: ", GuiUtils.createComponentWithActions(usedAmountSp, autoCalculateUsedAction));

        stateLbl.setBorder(BorderFactory.createEmptyBorder(3,5,3,5));

        infoPnl.add(stateLbl, BorderLayout.NORTH);
        infoPnl.add(pnl, BorderLayout.CENTER);

        panel.add(infoPnl, BorderLayout.NORTH);
        panel.add(pcbImageLbl, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPcbItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(createdPcbTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        panel.add(scrollPane);

        return panel;
    }

    private JPanel createPcbItemsHeader() {
        JPanel panel = new JPanel(new BorderLayout());

        ILabel nameLbl = new ILabel("PCB items: ");
        JToolBar toolBar = GuiUtils.createNewToolbar(saveAllAction);

        panel.add(nameLbl, BorderLayout.WEST);
        panel.add(toolBar, BorderLayout.EAST);

        panel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
        panel.setBackground(Color.gray);
        panel.setOpaque(true);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        setResizable(true);
        setTitleName(getTitle());
        if (projectPcb != null && projectPcb.getProject() != null && !projectPcb.getProject().getIconPath().isEmpty()) {
            Path path = Paths.get(settings().getFileSettings().getImgProjectsPath(), projectPcb.getProject().getIconPath());
            try {
                setTitleIcon(imageResource.readImage(path, 48,48));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        projectPcbTf = new ITextField(false);
        pcbNameTf = new ITextField(false);
        pcbDateTf = new ITextField(false);
        pcbImageLbl = new ILabel();
        pcbImageLbl.setPreferredSize(new Dimension(150, 90));
        pcbImageLbl.setBorder(BorderFactory.createLineBorder(Color.gray, 2));

        tableModel = new ICreatedPcbTableModel();
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
        saveAllAction = new IActions.SaveAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveAllAction(createdPcb);
            }
        };
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel infoPanel = createInfoPanel();
        JPanel headerPanel = createHeaderPanel();
        JPanel pcbItemsPanel = createPcbItemsPanel();
        JPanel pcbItemsHeader = createPcbItemsHeader();

        JPanel mainPanel = new JPanel(new BorderLayout());
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pcbItemsPanel, infoPanel);
        centerSplitPane.setOneTouchExpandable(true);

        mainPanel.add(pcbItemsHeader, BorderLayout.PAGE_START);
        mainPanel.add(centerSplitPane, BorderLayout.CENTER);

        getContentPanel().add(headerPanel, BorderLayout.NORTH);
        getContentPanel().add(mainPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        initTable();

        updateInfo(projectPcb, createdPcb, selectedLink);
        updateEnabledComponents();
    }
}