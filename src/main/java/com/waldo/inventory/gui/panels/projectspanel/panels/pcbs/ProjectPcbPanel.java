package com.waldo.inventory.gui.panels.projectspanel.panels.pcbs;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderLine;
import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.createpcbdialog.CreatePcbDialog;
import com.waldo.inventory.gui.dialogs.editordersdialog.EditOrdersDialog;
import com.waldo.inventory.gui.dialogs.editprojectpcbdialog.EditProjectPcbDialog;
import com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog.OrderPcbItemDialog;
import com.waldo.inventory.gui.panels.projectspanel.panels.ProjectObjectPanel;
import com.waldo.inventory.gui.panels.projectspanel.preview.ProjectPcbPreviewPanel;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;
import static com.waldo.inventory.managers.CacheManager.cache;

public class ProjectPcbPanel extends ProjectObjectPanel<ProjectPcb> {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private PcbItemPanel pcbItemPanel;
    private PcbOrderedPanel pcbOrderedPanel;
    private PcbCreatedPanel pcbCreatedPanel;

    private AbstractAction orderItemsAa;
    private AbstractAction orderPcbAa;

    private AbstractAction createPcbAa;
    private AbstractAction parseAa;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private GuiUtils.GuiInterface selectedPanel;


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectPcbPanel(Application application, ProjectObjectListener listener) {
        super(application, listener);
        cache().addListener(ProjectPcb.class, this);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected boolean selectProjectObject(ProjectPcb projectPcb) {
        if (super.selectProjectObject(projectPcb) && selectedPanel != null) {
            selectedPanel.updateComponents(selectedProjectObject);
        }
        return false;
    }

    @Override
    protected JPopupMenu showPopup(ProjectPcb projectObject) {
        JPopupMenu popupMenu = super.showPopup(projectObject);

        JMenu orderMenu = new JMenu("Order");
        orderMenu.add(orderItemsAa);
        orderMenu.add(orderPcbAa);

        popupMenu.addSeparator();
        popupMenu.add(orderMenu);
        popupMenu.add(parseAa);
        popupMenu.add(createPcbAa);

        return popupMenu;
    }

    private void onOrderItems() {
        if (selectedProjectObject != null) {
            if (selectedProjectObject.hasLinkedItems()) {
                OrderPcbItemDialog orderDialog = new OrderPcbItemDialog(
                        application,
                        "Order items",
                        selectedProjectObject);
                orderDialog.showDialog();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Items need to be linked with known item..",
                        "No linked items",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void onOrderPcb() {
        if (selectedProjectObject != null) {
            Order order = new Order(selectedProjectObject.getName());
            EditOrdersDialog dialog = new EditOrdersDialog(application, order, Statics.DistributorType.Pcbs, false);
            if (dialog.showDialog() == IDialog.OK) {
                OrderLine pcbOrderLine = new OrderLine(order, selectedProjectObject, 1);
                pcbOrderLine.save();
            }
        }
    }

    private void onCreatePcb() {
        if (selectedProjectObject != null) {
            CreatePcbDialog dialog = new CreatePcbDialog(application, "PCB", selectedProjectObject);
            dialog.showDialog();
        } else {
            JOptionPane.showMessageDialog(
                    application,
                    "No project pcb selected..",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onParse() {
        try {
            if (selectedProjectObject.parseAgain()) {
                //clearComponentTable();
                //setDetails();
                //updateComponentTable(projectPcb.getPcbItemList());
            }
        } catch (Exception ex) {
            Status().setError("Error parsing", ex);
            JOptionPane.showMessageDialog(
                    ProjectPcbPanel.this,
                    "Error parsing: " + ex,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /*
         *                  LISTENERS
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        super.initializeComponents();

        // PCB panels
        pcbItemPanel = new PcbItemPanel(application);
        pcbOrderedPanel = new PcbOrderedPanel();
        pcbCreatedPanel = new PcbCreatedPanel();


        // Preview panel
        previewPanel = new ProjectPcbPreviewPanel(application) {
            @Override
            public void onToolBarDelete(IdBToolBar source) {
                ProjectPcbPanel.this.onToolBarDelete(source);
            }

            @Override
            public void onToolBarEdit(IdBToolBar source) {
                ProjectPcbPanel.this.onToolBarEdit(source);
            }
        };

        // Actions
        orderItemsAa = new AbstractAction("Order items", imageResource.readIcon("Items.S.Title")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onOrderItems());
            }
        };

        orderPcbAa = new AbstractAction("Order PCB", imageResource.readIcon("Projects.Details.Pcb")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onOrderPcb());
            }
        };

        createPcbAa = new AbstractAction("Create", imageResource.readIcon("Projects.Pcb.UsedBtn")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onCreatePcb());
            }
        };

        parseAa = new AbstractAction("Parse", imageResource.readIcon("Projects.Pcb.ParseBtn")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onParse());
            }
        };
    }

    @Override
    public void initializeLayouts() {
        super.initializeLayouts();

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);

        tabbedPane.addChangeListener(e -> {
            if (!Application.isUpdating(application)) {
                Component c = tabbedPane.getSelectedComponent();
                selectedPanel = (GuiUtils.GuiInterface) c;
                selectedPanel.updateComponents(selectedProjectObject);
            }
        });

        tabbedPane.addTab("Pcb items ", imageResource.readIcon("Items.S.Title"), pcbItemPanel);
        tabbedPane.addTab("Ordered ", imageResource.readIcon("Projects.Pcb.Ordered"), pcbOrderedPanel);
        tabbedPane.addTab("Created ", imageResource.readIcon("Projects.Details.Pcb"), pcbCreatedPanel);

        bottomPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            Project project = null;
            if (object[0] instanceof Project) {
                project = (Project) object[0];
            }
            if (object[0] instanceof ProjectPcb) {
                selectedProjectObject = (ProjectPcb) object[0];
                project = selectedProjectObject.getProject();
            }

            if (project != null && !project.equals(selectedProject)) {
                selectedProject = project;
                gridPanel.drawTiles(selectedProject.getProjectPcbs());
            }
        } else {
            selectedProject = null;
        }
        previewPanel.updateComponents(selectedProject);
        updateEnabledComponents();
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (selectedProject != null) {
            ProjectPcb newProjectPcb = new ProjectPcb(selectedProject.getId());
            EditProjectPcbDialog dialog = new EditProjectPcbDialog(application, "Add pcb", newProjectPcb);
            if (dialog.showDialog() == IDialog.OK) {
                pcbItemPanel.updateComponents(newProjectPcb);
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedProjectObject != null) {
            EditProjectPcbDialog dialog = new EditProjectPcbDialog(application, "Edit " + selectedProjectObject.getName(), selectedProjectObject);
            if (dialog.showDialog() == IDialog.OK) {
                //selectedProjectObject.save();
                updateComponents(selectedProjectObject);
            }
        }
    }

    @Override
    public void onDeleted(ProjectPcb object) {
        super.onDeleted(object);
        pcbItemPanel.updateComponents((ProjectPcb)null);
    }

    //
    // Project code changed
    //
    @Override
    public void onUpdated(ProjectPcb object) {
        if (selectedProject == null) {
            selectedProject = object.getProject();
        }

        selectedProject.updateProjectPcbs();
//        gridPanel.redrawTiles();
//        gridPanel.selectTile(object);
//        updateEnabledComponents();
        updateComponents(object);
    }

    //
    // Text edit save action listener
    //
    @Override
    public void actionPerformed(ActionEvent e) {
//        DefaultStyledDocument doc = remarksTe.getStyledDocument();
//        if (selectedProjectObject.getRemarksFileName().isEmpty()) {
//            try {
//                selectedProjectObject.setRemarksFile(FileUtils.createTempFile(selectedProjectObject.createRemarksFileName()));
//            } catch (Exception e1) {
//                e1.printStackTrace();
//                return;
//            }
//        }
//        try (OutputStream fos = new FileOutputStream(selectedProjectObject.getRemarksFile());
//             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
//
//            oos.writeObject(doc);
//            selectedProjectObject.save();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }
}