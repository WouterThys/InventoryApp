package com.waldo.inventory.gui.panels.projectspanel.preview;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ProjectCode;
import com.waldo.inventory.gui.Application;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ProjectCodePreviewPanel extends ProjectPreviewPanel<ProjectCode> {

    private ITextField ideTf;
    private ITextField languageTf;

    public ProjectCodePreviewPanel(Application application) {
        super(application);
    }

    @Override
    void initializeInfoComponents() {
        ideTf = new ITextField(false);
        languageTf = new ITextField(false);
    }

    @Override
    JPanel createInfoPanel() {
        JPanel infoPnl = new JPanel();

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("IDE", imageResource.readIcon("Ides.Menu"), ideTf);
        gbc.addLine("Language", imageResource.readIcon("TextEdit.ListNumbers"), languageTf);

        return infoPnl;
    }

    @Override
    void updateInfoPanel(ProjectCode projectCode) {
        if (projectCode != null) {
            if (projectCode.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                ideTf.setText(projectCode.getProjectIDE().toString());
            } else {
                ideTf.setText("");
            }
            languageTf.setText(projectCode.getLanguage().toString());
        } else {
            ideTf.setText("");
            languageTf.setText("");
        }
    }
}
