package com.waldo.inventory.gui.dialogs.projectidesdialog.parserdialog;

import com.waldo.inventory.Utils.parser.ProjectParser;
import com.waldo.inventory.gui.Application;

import java.awt.event.ActionEvent;

public class ParserDialog extends ParserDialogLayout {


    public ParserDialog(Application application, String title, boolean useParser, ProjectParser parser) {
        super(application, title, useParser, parser);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);

    }

    public boolean useParser() {
        return useParserCb.isSelected();
    }

    public ProjectParser getParser() {
        return (ProjectParser) parserCb.getSelectedItem();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parserCb.setEnabled(useParserCb.isSelected());
    }
}