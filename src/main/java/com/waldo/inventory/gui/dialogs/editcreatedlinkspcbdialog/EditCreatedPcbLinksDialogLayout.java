package com.waldo.inventory.gui.dialogs.editcreatedlinkspcbdialog;

import com.waldo.inventory.Utils.Statics.CreatedPcbLinkState;
import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.CreatedPcbLink;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.ICreatedPcbTableModel;
import com.waldo.utils.DateUtils;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;


abstract class EditCreatedPcbLinksDialogLayout extends IDialog implements IEditedListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ICreatedPcbTableModel tableModel;
    private ITable<CreatedPcbLink> createdPcbTable;

    // Created pcb
    private ITextField projectPcbTf;
    private ITextField pcbNameTf;
    private ITextField pcbDateTf;
    ILabel pcbImageLbl;


    // Link panel
    private ILabel stateLbl;
    private ITextField pcbItemTf;
    private ITextField linkedItemTf;
    private ITextField usedItemTf;
    private SpinnerNumberModel usedAmountSpModel;
    ISpinner usedAmountSp;
    private ITextPane remarksTp;

    // Actions
    private IActions.AutoCalculateUsedAction autoCalculateUsedAction;
    private IActions.SaveAction saveAllAction;
    private IActions.EditAction editItemAction;
    private IActions.SearchAction searchUsedItemAction;
    private IActions.DeleteAction deleteUsedItemAction;
    private IActions.UseAction createPcbAction;
    private AbstractAction editRemarksAa;
    private IActions.WizardAction wizardAction;
    private IActions.RemoveAllAction removeAllAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ProjectPcb projectPcb;
    CreatedPcb createdPcb;

    private CreatedPcbLink selectedLink;

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
    abstract void onSaveAll(CreatedPcb createdPcb);
    abstract void onCreatePcb(CreatedPcb createdPcb);
    abstract void onEditRemark(CreatedPcbLink link);
    abstract void onMagicWizard(CreatedPcb createdPcb);
    abstract void onRemoveAll(CreatedPcb createdPcb);

    void updateEnabledComponents() {
        boolean enabled = selectedLink != null;
        boolean hasLink = enabled && selectedLink.getPcbItemItemLink() != null;
        boolean hasUsed = enabled && selectedLink.getUsedItemId() > DbObject.UNKNOWN_ID;
        boolean isCreated = createdPcb != null && createdPcb.isCreated();

        editItemAction.setEnabled(hasLink);
        searchUsedItemAction.setEnabled(!isCreated && enabled);
        deleteUsedItemAction.setEnabled(!isCreated && hasUsed);

        autoCalculateUsedAction.setEnabled(!isCreated && hasUsed);
        usedAmountSp.setEnabled(!isCreated && hasLink);

        createPcbAction.setEnabled(!isCreated);
        editRemarksAa.setEnabled(enabled);
        removeAllAction.setEnabled(isCreated);
    }

    private void initTable() {
        tableModel.setItemList(createdPcb.getCreatedPcbLinks());
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
            pcbDateTf.setText(DateUtils.formatDateTime(createdPcb.getDateCreated()));
            if (!createdPcb.getIconPath().isEmpty()) {
                Path path = Paths.get(createdPcb.getIconPath());
                try {
                    URL url = path.toUri().toURL();
                    pcbImageLbl.setIcon(imageResource.readImage(url));
                } catch (Exception e) {
                    //
                }
            } else {
                pcbImageLbl.setIcon(null);
            }
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
        gbc.addLine("Created: ", pcbDateTf);

        JPanel mainPnl = new JPanel(new BorderLayout());
        JToolBar tb = GuiUtils.createNewToolbar(saveAllAction, removeAllAction, wizardAction, createPcbAction);

        mainPnl.add(infoPnl, BorderLayout.CENTER);
        mainPnl.add(tb, BorderLayout.PAGE_START);

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
        ILabel pcbItemLbl = new ILabel("Pcb item: ", imageResource.readImage("Projects.Details.Pcb"), SwingConstants.CENTER);
        ILabel linkedItemLbl = new ILabel("Linked item: ", imageResource.readImage("Projects.Pcb.Linked"), SwingConstants.CENTER);
        ILabel usedItemLbl = new ILabel("Used item: ", imageResource.readImage("Projects.Pcb.Used"), SwingConstants.CENTER);

        JPanel pnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(pnl);
        gbc.addLine(pcbItemLbl, pcbItemTf);
        gbc.addLine(linkedItemLbl, GuiUtils.createComponentWithActions(linkedItemTf, editItemAction));
        gbc.addLine(usedItemLbl, GuiUtils.createComponentWithActions(usedItemTf, searchUsedItemAction, deleteUsedItemAction));
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
        setModal(false);
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
        pcbImageLbl.setPreferredSize(new Dimension(200, 90));
        pcbImageLbl.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
        pcbImageLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onImageIconDoubleClicked(createdPcb, pcbImageLbl);
                }
            }
        });

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
        saveAllAction = new IActions.SaveAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveAll(createdPcb);
            }
        };
        saveAllAction.setIcon(imageResource.readImage("Actions.M.Save"));
        createPcbAction = new IActions.UseAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreatePcb(createdPcb);
            }
        };
        createPcbAction.setIcon(imageResource.readImage("Actions.M.Use"));
        editRemarksAa = new AbstractAction("Edit remarks", imageResource.readImage("Actions.EditRemark")) {
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
        wizardAction.setIcon(imageResource.readImage("Actions.M.Wizard"));
        removeAllAction = new IActions.RemoveAllAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemoveAll(createdPcb);
            }
        };
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel infoPanel = createInfoPanel();
        JPanel headerPanel = createHeaderPanel();
        JPanel pcbItemsPanel = createPcbItemsPanel();

        JPanel mainPanel = new JPanel(new BorderLayout());
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pcbItemsPanel, infoPanel);
        centerSplitPane.setOneTouchExpandable(true);

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
        createdPcbTable.resizeColumns();
    }
}