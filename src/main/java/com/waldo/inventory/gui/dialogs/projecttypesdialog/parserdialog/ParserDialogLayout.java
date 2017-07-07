package com.waldo.inventory.gui.dialogs.projecttypesdialog.parserdialog;

import com.waldo.inventory.Utils.parser.ProjectParser;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ICheckBox;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ParserDialogLayout extends IDialog implements ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ICheckBox useParserCb;
    DefaultComboBoxModel<ProjectParser> parserCbModel;
    JComboBox<ProjectParser> parserCb;


    /*
    *                  VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    boolean useParser;
    ProjectParser projectParser;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ParserDialogLayout(Application application, String title, boolean useParser, ProjectParser projectParser) {
        super(application, title);

        this.useParser = useParser;
        this.projectParser = projectParser;

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

        parserCbModel = new DefaultComboBoxModel<>();
        parserCb = new JComboBox<>(parserCbModel);
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
        parserCbModel.removeAllElements();
        for (ProjectParser pp : Application.getParserList()) {
            parserCbModel.addElement(pp);
        }

        parserCb.setSelectedItem(projectParser);
        parserCb.setEnabled(useParser);
        useParserCb.setSelected(useParser);
    }
}