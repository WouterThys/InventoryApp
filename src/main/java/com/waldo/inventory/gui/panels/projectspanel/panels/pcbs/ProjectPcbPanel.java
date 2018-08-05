package com.waldo.inventory.gui.panels.projectspanel.panels.pcbs;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.addtoorderdialog.AddToOrderDialog;
import com.waldo.inventory.gui.dialogs.createpcbdialog.SelectPcbDialog;
import com.waldo.inventory.gui.dialogs.editcreatedlinkspcbdialog.EditCreatedPcbLinksDialog;
import com.waldo.inventory.gui.dialogs.editprojectpcbdialog.EditProjectPcbDialog;
import com.waldo.inventory.gui.dialogs.ordersearchitemdialog.OrderSearchItemsDialog;
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
                OrderSearchItemsDialog dialog = new OrderSearchItemsDialog(application, selectedProjectObject);
                dialog.showDialog();
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
            //PcbOrder pcbOrder = OrderManager.createNewPcbOrder(selectedProjectObject.getName());
            //OrderManager.addLineToOrder(selectedProjectObject, pcbOrder);
            AddToOrderDialog<ProjectPcb> dialog = new AddToOrderDialog<ProjectPcb>(application, selectedProjectObject, true);
            if (dialog.showDialog() == IDialog.OK) {
                // Create new link
                AbstractOrder order = dialog.getSelectedOrder();
                DistributorPartLink link = new DistributorPartLink(order.getDistributor(), selectedProjectObject.getId());
                link.save();
            }
        }
    }

    private void onSelectForCreation() {
        if (selectedProjectObject != null) {
            SelectPcbDialog dialog = new SelectPcbDialog(application, "PCB", selectedProjectObject);
            if (dialog.showDialog() == IDialog.OK) {
                CreatedPcb pcb = dialog.getCreatedPcb();
                if (pcb != null) {
                   createPcb(pcb);
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                    application,
                    "No project pcb selected..",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void createPcb(CreatedPcb pcb) {
        if (pcb != null) {
            EditCreatedPcbLinksDialog linksDialog = new EditCreatedPcbLinksDialog(
                    application,
                    "Edit pcb",
                    selectedProjectObject,
                    pcb
            );
            linksDialog.showDialog();
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
        pcbCreatedPanel = new PcbCreatedPanel() {
            @Override
            public void createPcb(CreatedPcb pcb) {
                ProjectPcbPanel.this.createPcb(pcb);
            }
        };

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
        orderItemsAa = new AbstractAction("Order items", imageResource.readIcon("Component.SS")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onOrderItems());
            }
        };

        orderPcbAa = new AbstractAction("Order PCB", imageResource.readIcon("Pcb.SS")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onOrderPcb());
            }
        };

        createPcbAa = new AbstractAction("Create", imageResource.readIcon("Used.SS")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onSelectForCreation());
            }
        };

        parseAa = new AbstractAction("Parse", imageResource.readIcon("Parse.SS")) {
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

        tabbedPane.addTab("Pcb items ", imageResource.readIcon("Component.SS"), pcbItemPanel);
        tabbedPane.addTab("Created ", imageResource.readIcon("Pcb.SS"), pcbCreatedPanel);

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

            if (selectedProject == null || !selectedProject.equals(project)) {
                selectedProject = project;
                if (selectedProject != null) {
                    if (selectedProjectObject == null && selectedProject.getProjectPcbs().size() > 0) {
                        selectedProjectObject = selectedProject.getProjectPcbs().get(0);
                    }
                    gridPanel.drawTiles(selectedProject.getProjectPcbs());
                    gridPanel.selectTile(selectedProjectObject);
                }
            } else {
                selectedProject = null;
            }
        } else {
            selectedProject = null;
        }
        previewPanel.updateComponents(selectedProjectObject);
        if (selectedPanel == null) {
            selectedPanel = pcbItemPanel;
        }
        selectedPanel.updateComponents(selectedProjectObject);
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