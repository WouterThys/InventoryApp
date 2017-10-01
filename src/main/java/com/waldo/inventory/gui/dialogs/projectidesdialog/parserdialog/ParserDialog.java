package com.waldo.inventory.gui.dialogs.projectidesdialog.parserdialog;

import com.waldo.inventory.Utils.parser.PcbItemParser;
import com.waldo.inventory.gui.Application;

import java.awt.event.ActionEvent;

public class ParserDialog extends ParserDialogLayout {


    public ParserDialog(Application application, String title, boolean useParser, PcbItemParser parser) {
        super(application, title, useParser, parser);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);

    }

    public boolean useParser() {
        return useParserCb.isSelected();
    }

    public PcbItemParser getParser() {
        return (PcbItemParser) parserCb.getSelectedItem();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parserCb.setEnabled(useParserCb.isSelected());
    }
}