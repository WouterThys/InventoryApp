package com.waldo.inventory.gui.dialogs.pcbitemdetails;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;

abstract class PcbItemDetailsDialogLayout extends IDialog implements IEditedListener {

    private static final ImageIcon greenBall = imageResource.readImage("Ball.green");
    private static final ImageIcon redBall = imageResource.readImage("Ball.red");

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
    private ITableLabel matchedItemLbl;
    private ITextField matchedItemTf;
    private IActions.EditAction editAction;
    private IActions.DeleteAction deleteAction;
    private IActions.ViewAllLinksAction viewAllLinksAction;

    private ITableLabel orderLbl;
    private ITextField orderTf;
    private IActions.OrderItemAction orderItemAction;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PcbItemProjectLink pcbItemProjectLink;
    PcbItemProjectLink originalProjectLink;

    int newItemAmount = -1;
    private int originalItemAmount = -1;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PcbItemDetailsDialogLayout(Window parent, String title, PcbItemProjectLink pcbItemProjectLink) {
        super(parent, title);

        this.pcbItemProjectLink = pcbItemProjectLink;
        this.originalProjectLink = pcbItemProjectLink.createCopy();
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract void onSelectNewItem();
    abstract void onSelectNewOrder();
    abstract void onDeleteMatchedItem();
    abstract void onViewAllItemLinks();

    void updateEnabledComponents() {
        if (pcbItemProjectLink != null) {
            deleteAction.setEnabled(pcbItemProjectLink.getPcbItemItemLink() != null);
            viewAllLinksAction.setEnabled(pcbItemProjectLink.getPcbItem().getKnownItemLinks().size() > 0);
        }
    }

    private void setProjectInfo(ProjectPcb pcbProject) {
        if (pcbProject != null) {
            Project project = pcbProject.getProject();
            if (project != null) {
                if (!project.getIconPath().isEmpty()) {
                    Path path = Paths.get(settings().getFileSettings().getImgProjectsPath(), project.getIconPath());
                    try {
                        projectIconLbl.setIcon(imageResource.readImage(path, 48,48));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            if (pcbItemProjectLink.hasMatchedItem()) {
                PcbItemItemLink itemLink = pcbItemProjectLink.getPcbItemItemLink();
                updateMatchedItemPanel(itemLink.getLinkedItemName(), itemLink.getLinkedItemAmount());
            }
            if (pcbItem.getOrderItem() != null) {
                orderTf.setText(pcbItem.getOrderItem().getOrder().toString());
            }
        }
    }

    void updateMatchedItemPanel(String name, int amount) {
        matchedItemTf.setText(name);
        setItemAmount(amount);
    }

    void clearMatchedItemPanel() {
        // TODO: what if already changed?
        //..
        matchedItemLbl.setText("");
        matchedItemLbl.setIcon((ImageIcon)null);
        matchedItemTf.setText("");

    }

    void setItemAmount(int amount) {
        // Set previous item amount
        if (originalItemAmount == -1) {
            newItemAmount = amount;
            originalItemAmount = amount;
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
        JToolBar itemTb = new JToolBar();
        itemTb.setFloatable(false);
        itemTb.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        itemTb.add(editAction);
        itemTb.add(deleteAction);
        itemTb.add(viewAllLinksAction);
        itemPnl.add(matchedItemLbl, BorderLayout.WEST);
        itemPnl.add(matchedItemTf, BorderLayout.CENTER);
        itemPnl.add(itemTb, BorderLayout.EAST);

        JPanel orderPnl = new JPanel(new BorderLayout());
        JButton orderBtn = new JButton(orderItemAction);
        orderBtn.setText("Order ");
        orderPnl.add(orderLbl, BorderLayout.WEST);
        orderPnl.add(orderTf, BorderLayout.CENTER);
        orderPnl.add(orderBtn, BorderLayout.EAST);

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel);
        gbc.addLine(imageResource.readImage("Projects.Pcb.Linked"), itemPnl);
        gbc.addLine(imageResource.readImage("Projects.Pcb.Ordered"), orderPnl);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setResizable(true);
        setModal(false);
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
        matchedItemLbl = new ITableLabel(matchedItemTf.getBackground());
        editAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSelectNewItem();
            }
        };
        deleteAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteMatchedItem();
            }
        };
        viewAllLinksAction = new IActions.ViewAllLinksAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onViewAllItemLinks();
            }
        };
        orderTf = new ITextField(false);
        orderLbl = new ITableLabel(orderTf.getBackground());
        orderItemAction = new IActions.OrderItemAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSelectNewOrder();
            }
        };
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        getContentPanel().add(createProjectHeader(), BorderLayout.NORTH);
        getContentPanel().add(createPcbItemInfo(), BorderLayout.CENTER);
        getContentPanel().add(createReferencesPanel(), BorderLayout.SOUTH);

        getContentPanel().setPreferredSize(new Dimension(600, getContentPanel().getPreferredSize().height));

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (pcbItemProjectLink != null) {
            setProjectInfo(pcbItemProjectLink.getProjectPcb());
            setPcbItemInfo(pcbItemProjectLink);
            setReferencesInfo(pcbItemProjectLink);
            updateEnabledComponents();
        }
    }
}