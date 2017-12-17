package com.waldo.inventory.gui.dialogs.pcbitemdetails;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.actions.EditItemAction;
import com.waldo.inventory.gui.components.actions.OrderItemAction;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;

abstract class PcbItemDetailsDialogLayout extends IDialog implements IEditedListener {

    private static final ImageIcon greenBall = imageResource.readImage("Ball.green");
    private static final ImageIcon redBall = imageResource.readImage("Ball.red");
    private static final ImageIcon yellowBall = imageResource.readImage("Ball.yellow");

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    // Project
    private ILabel projectIconLbl;
    private ITextField projectTf;
    private ITextField pcbProjectTf;

    private ITextField valueTf;
    private ITextField footprintTf;
    private ITextField libraryTf;
    private ITextField partNameTf;
    private ITextField sheetNameTf;

    private DefaultListModel<String> refListModel;
    private JList<String> referencesList; // TODO: make real pcb items?

    // References
    private ITableIcon matchedItemLbl;
    private ITextField matchedItemTf;
    private EditItemAction editItemAction;

    private ITableIcon orderLbl;
    private ITextField orderTf;
    private OrderItemAction orderItemAction;

    SpinnerNumberModel spinnerNumberModel;
    ISpinner usedSpinner;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PcbItemProjectLink pcbItemProjectLink;
    PcbItemProjectLink originalProjectLink;

    int newItemAmount = -1;
    int originalItemAmount = -1;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PcbItemDetailsDialogLayout(Application application, String title, PcbItemProjectLink pcbItemProjectLink) {
        super(application, title);

        this.pcbItemProjectLink = pcbItemProjectLink;
        this.originalProjectLink = pcbItemProjectLink.createCopy();
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {

    }

    private void setProjectInfo(ProjectPcb pcbProject) {
        if (pcbProject != null) {
            Project project = pcbProject.getProject();
            if (project != null) {
                if (!project.getIconPath().isEmpty()) {
                    Path path = Paths.get(settings().getFileSettings().getImgProjectsPath(), project.getIconPath());
                    projectIconLbl.setIcon(path.toString(), 48, 48);
                }
                projectTf.setText(project.toString());
            }
            pcbProjectTf.setText(pcbProject.toString());
        }
    }

    private void setPcbItemInfo(PcbItemProjectLink projectLink) {
        if (projectLink != null) {
            PcbItem pcbItem = projectLink.getPcbItem();
            valueTf.setText(projectLink.getValue());
            footprintTf.setText(pcbItem.getFootprint());
            libraryTf.setText(pcbItem.getLibrary());
            partNameTf.setText(pcbItem.getPartName());
            sheetNameTf.setText(projectLink.getPcbSheetName());

            for (String ref : projectLink.getReferences()) {
                refListModel.addElement(ref);
            }
        }
    }

    private void setReferencesInfo(PcbItemProjectLink pcbItemProjectLink) {
        if (pcbItemProjectLink != null) {
            PcbItem pcbItem = pcbItemProjectLink.getPcbItem();
            if (pcbItem.hasMatch()) {
                PcbItemItemLink itemLink = pcbItem.getMatchedItemLink();
                updateMatchedItemPanel(itemLink.getLinkedItemName(), itemLink.getLinkedItemAmount());
            } else {
                usedSpinner.setEnabled(false);
            }
            if (pcbItem.getOrderItem() != null) {
                orderTf.setText(pcbItem.getOrderItem().getOrder().toString());
            }

            usedSpinner.setTheValue(pcbItemProjectLink.getUsedCount());
        }
    }

    void updateMatchedItemPanel(String name, int amount) {
        matchedItemTf.setText(name);
        setItemAmount(amount);
    }

    void setItemAmount(int amount) {
        // Set previous item amount
        if (originalItemAmount == -1) {
            newItemAmount = amount;
            originalItemAmount = amount;
            spinnerNumberModel.setMaximum(amount+1);
        }
        matchedItemLbl.setText(String.valueOf(amount));
        if (amount > 0) {
            matchedItemLbl.setIcon(greenBall);
        } else {
            matchedItemLbl.setIcon(redBall);
        }
    }

    private JPanel createProjectHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(GuiUtils.createTitleBorder("Project"));

        JPanel infoPnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("Project: ", projectTf);
        gbc.addLine("Pcb: ", pcbProjectTf);

        panel.add(projectIconLbl, BorderLayout.EAST);
        panel.add(infoPnl, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPcbItemInfo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(GuiUtils.createTitleBorder("Pcb item"));

        // Info
        JPanel infoPnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("Name: ", partNameTf);
        gbc.addLine("Value: ", valueTf);
        gbc.addLine("Footprint: ", footprintTf);
        gbc.addLine("Library: ", libraryTf);
        gbc.addLine("Sheet: ", sheetNameTf);

        // List
        JPanel listPnl = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(referencesList);
        listPnl.add(new ILabel("References"), BorderLayout.PAGE_START);
        listPnl.add(scrollPane, BorderLayout.CENTER);

        // Add
        panel.add(infoPnl, BorderLayout.CENTER);
        panel.add(listPnl, BorderLayout.EAST);

        return panel;
    }

    private JPanel createReferencesPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(GuiUtils.createTitleBorder("References"));

        JPanel itemPnl = new JPanel(new BorderLayout());
        JButton itemBtn = new JButton(editItemAction);
        itemBtn.setText("Select");
        itemPnl.add(matchedItemLbl, BorderLayout.WEST);
        itemPnl.add(matchedItemTf, BorderLayout.CENTER);
        itemPnl.add(itemBtn, BorderLayout.EAST);

        JPanel orderPnl = new JPanel(new BorderLayout());
        JButton orderBtn = new JButton(orderItemAction);
        orderBtn.setText("Order ");
        orderPnl.add(orderLbl, BorderLayout.WEST);
        orderPnl.add(orderTf, BorderLayout.CENTER);
        orderPnl.add(orderBtn, BorderLayout.EAST);

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel);
        gbc.addLine(imageResource.readImage("Projects.Pcb.Linked"), itemPnl);
        gbc.addLine(imageResource.readImage("Projects.Pcb.Ordered"), orderPnl);
        gbc.addLine(imageResource.readImage("Projects.Pcb.Used"), usedSpinner);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("Projects.Pcb.Title"));
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setEnabled(false);

        // This
        // Project
        projectIconLbl = new ILabel();
        projectTf = new ITextField(false);
        pcbProjectTf = new ITextField(false);

        // PcbItem
        valueTf = new ITextField(false);
        footprintTf = new ITextField(false);
        libraryTf = new ITextField(false);
        partNameTf = new ITextField(false);
        sheetNameTf = new ITextField(false);

        refListModel = new DefaultListModel<>();
        referencesList = new JList<>(refListModel);
        referencesList.setEnabled(false);

        // References
        matchedItemTf = new ITextField(false);
        matchedItemLbl = new ITableIcon(matchedItemTf.getBackground());
        editItemAction = new EditItemAction() {
            @Override
            public void onEditItem() {
                AdvancedSearchDialog dialog = new AdvancedSearchDialog(application, "Search item");
                dialog.showDialog();
            }
        };
        orderTf = new ITextField(false);
        orderLbl = new ITableIcon(orderTf.getBackground());
        orderItemAction = new OrderItemAction() {
            @Override
            public void onOrderItem() {
                // TODO: add item to order
            }
        };
        spinnerNumberModel = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
        usedSpinner = new ISpinner(spinnerNumberModel);
        usedSpinner.addEditedListener(this, "usedCount");
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        getContentPanel().add(createProjectHeader(), BorderLayout.NORTH);
        getContentPanel().add(createPcbItemInfo(), BorderLayout.CENTER);
        getContentPanel().add(createReferencesPanel(), BorderLayout.SOUTH);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (pcbItemProjectLink != null) {
            setProjectInfo(pcbItemProjectLink.getProjectPcb());
            setPcbItemInfo(pcbItemProjectLink);
            setReferencesInfo(pcbItemProjectLink);
        }
    }
}