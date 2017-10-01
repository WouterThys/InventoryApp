package com.waldo.inventory.gui.dialogs.projectidesdialog.parserdialog;

import com.waldo.inventory.Utils.parser.PcbItemParser;
import com.waldo.inventory.Utils.parser.PcbParser;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ICheckBox;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public abstract class ParserDialogLayout extends IDialog implements ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ICheckBox useParserCb;
    IComboBox<PcbParser> parserCb;


    /*
    *                  VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private boolean useParser;
    private PcbItemParser selectedParser;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ParserDialogLayout(Application application, String title, boolean useParser, PcbItemParser selectedParser) {
        super(application, title);

        this.useParser = useParser;
        this.selectedParser = selectedParser;

        showTitlePanel(false);
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        useParserCb = new ICheckBox("User parser", false);
        useParserCb.addActionListener(this);

        parserCb = new IComboBox<>(PcbItemParser.getInstance().getPcbParsers(), null, false);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(useParserCb, BorderLayout.NORTH);
        getContentPanel().add(parserCb, BorderLayout.CENTER);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));


        pack();
    }

    @Override
    public void updateComponents(Object object) {
        parserCb.setSelectedItem(selectedParser);
        parserCb.setEnabled(useParser);
        useParserCb.setSelected(useParser);
    }
}