package com.waldo.inventory.gui.panels.projectspanel.preview;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.utils.DateUtils;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ProjectPcbPreviewPanel extends ProjectPreviewPanel<ProjectPcb> {

    private ITextField pcbIdeTf;
    private ITextField itemsFromTf;
    private ITextField lastParsedTf;
    private ITextField numberOfItemsTf;

    public ProjectPcbPreviewPanel(Application application) {
        super(application);
    }

    private void updateEnabledComponents() {

    }

    @Override
    void initializeInfoComponents() {
        pcbIdeTf = new ITextField(false);
        itemsFromTf = new ITextField(false);
        lastParsedTf = new ITextField(false);
        numberOfItemsTf = new ITextField(false);

        lastParsedTf.setMaximumSize(new Dimension(60, 30));
        itemsFromTf.setMaximumSize(new Dimension(60, 30));
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
