package com.waldo.inventory.gui.panels.projectspanel.preview;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.createpcbdialog.CreatePcbDialog;
import com.waldo.inventory.gui.dialogs.linkitemdialog.LinkPcbItemDialog;
import com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog.OrderPcbItemDialog;
import com.waldo.utils.DateUtils;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public abstract class ProjectPcbPreviewPanel extends ProjectPreviewPanel<ProjectPcb> {

    private ITextField pcbIdeTf;
    private ITextField itemsFromTf;
    private ITextField lastParsedTf;
    private ITextField numberOfItemsTf;

    private AbstractAction linkAa;
    private AbstractAction orderAa;
    private AbstractAction usedAa;
    private AbstractAction parseAa;

    public ProjectPcbPreviewPanel(Application application) {
        super(application);
    }

    private void updateEnabledComponents() {
        boolean enable = selectedProjectObject != null;
        linkAa.setEnabled(enable);
        parseAa.setEnabled(enable && selectedProjectObject.isValid());
        orderAa.setEnabled(enable);
        usedAa.setEnabled(enable);
    }

    private void onLink() {
        if (selectedProjectObject != null) {
            LinkPcbItemDialog dialog = new LinkPcbItemDialog(application, "Link items", selectedProjectObject);
            dialog.showDialog();
        }
    }

    private void onOrder() {
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

    private void onUsed() {
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
                    ProjectPcbPreviewPanel.this,
                    "Error parsing: " + ex,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public AbstractAction getLinkAa() {
        AbstractAction linkAction = new AbstractAction("Link", imageResource.readIcon("Actions.Link")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onLink());
            }
        };
        linkAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Link to known items");
        return linkAction;
    }

    public AbstractAction getOrderAa() {
        AbstractAction orderAction = new AbstractAction("Order", imageResource.readIcon("Actions.ItemOrder")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onOrder());
            }
        };
        orderAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Order linked items");
        return orderAction;
    }

    public AbstractAction getParseAa() {
        AbstractAction parseAction = new AbstractAction("Parse", imageResource.readIcon("Parse.S.Title")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onParse());
            }
        };
        parseAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Parse again");
        return parseAction;
    }

    public AbstractAction getUsedAa() {
        AbstractAction usedAction = new AbstractAction("Used", imageResource.readIcon("Projects.Pcb.Used")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onUsed());
            }
        };
        usedAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Used items");
        return usedAction;
    }

    @Override
    void initializeInfoComponents() {
        pcbIdeTf = new ITextField(false);
        itemsFromTf = new ITextField(false);
        lastParsedTf = new ITextField(false);
        numberOfItemsTf = new ITextField(false);

        lastParsedTf.setMaximumSize(new Dimension(60, 30));
        itemsFromTf.setMaximumSize(new Dimension(60, 30));

        // Actions
        linkAa = new AbstractAction("Link", imageResource.readIcon("Projects.Pcb.LinkBtn")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onLink());
            }
        };
        linkAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Link to known items");

        orderAa = new AbstractAction("Order", imageResource.readIcon("Projects.Pcb.OrderBtn")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onOrder());
            }
        };
        orderAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Order linked items");

        usedAa = new AbstractAction("Used", imageResource.readIcon("Projects.Pcb.UsedBtn")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onUsed());
            }
        };
        usedAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Used items");

        parseAa = new AbstractAction("Parse", imageResource.readIcon("Projects.Pcb.ParseBtn")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onParse());
            }
        };
        parseAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Parse again");

        actionToolBar.addSeparator();
        actionToolBar.add(linkAa);
        actionToolBar.add(orderAa);
        actionToolBar.add(usedAa);
        actionToolBar.add(parseAa);
    }

    @Override
    JPanel createInfoPanel() {
        JPanel infoPnl = new JPanel();

        JPanel itemInfoPnl = new JPanel();
        itemInfoPnl.setLayout(new BoxLayout(itemInfoPnl, BoxLayout.X_AXIS));
        itemInfoPnl.add(numberOfItemsTf);
        itemInfoPnl.add(new ILabel(" items from "));
        itemInfoPnl.add(itemsFromTf);

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("IDE", imageResource.readIcon("Ides.Menu"), pcbIdeTf);
        gbc.addLine("Last parsed", imageResource.readIcon("Parse.S.Title"), lastParsedTf);
        //gbc.addLine("Items", imageResource.readIcon("Projects.Tab.Pcb"), itemInfoPnl);

        return infoPnl;
    }

    @Override
    void updateInfoPanel(ProjectPcb projectPcb) {
        if (projectPcb != null) {
            if (projectPcb.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                pcbIdeTf.setText(projectPcb.getProjectIDE().toString());
            } else {
                pcbIdeTf.setText("");
            }
            itemsFromTf.setText(projectPcb.hasParsed() ? "file" : "database");
            lastParsedTf.setText(DateUtils.formatDateTime(projectPcb.getLastParsedDate()));
            numberOfItemsTf.setText(String.valueOf(projectPcb.numberOfComponents()));
        } else {
            pcbIdeTf.setText("");
            itemsFromTf.setText("");
            lastParsedTf.setText("");
            numberOfItemsTf.setText("");
        }
        updateEnabledComponents();
    }
}
