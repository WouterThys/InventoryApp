package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.tablemodels.IFoundItemsTableModel;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

abstract class AdvancedSearchDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField searchTf;
    private JButton searchBtn;

    private ILabel resultLbl;
    private AbstractAction nextResultAction;
    private AbstractAction prevResultAction;

    private IFoundItemsTableModel tableModel;
    private ITable<Item> foundItemTable;
    private FoundItemDetailPanel detailPanel;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    AdvancedSearchDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("Search.Title"));
        setTitleName(getTitle());

        // This
        searchTf = new ITextField();
        searchTf.setFont(30, Font.BOLD);
        searchBtn = new JButton(imageResource.readImage("Search.Go"));

        resultLbl = new ILabel("Results: ");

        nextResultAction = new AbstractAction("Next", imageResource.readImage("Search.Next")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                    }
                };
        prevResultAction = new AbstractAction("Previous", imageResource.readImage("Search.Previous")) {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        tableModel = new IFoundItemsTableModel();
        foundItemTable = new ITable<>(tableModel);
        detailPanel = new FoundItemDetailPanel(application);

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        JPanel searchPnl = new JPanel(new BorderLayout());
        searchPnl.add(searchTf, BorderLayout.CENTER);
        searchPnl.add(searchBtn, BorderLayout.EAST);

        JPanel infoPnl = new JPanel(new BorderLayout());
        JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        toolBar.add(nextResultAction);
        toolBar.add(prevResultAction);

        infoPnl.add(resultLbl, BorderLayout.WEST);
        infoPnl.add(toolBar, BorderLayout.EAST);

        JPanel foundPnl = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(foundItemTable);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        foundPnl.add(scrollPane, BorderLayout.CENTER);
        foundPnl.add(detailPanel, BorderLayout.EAST);

        JPanel resultPnl = new JPanel(new BorderLayout());
        resultPnl.add(infoPnl, BorderLayout.PAGE_START);
        resultPnl.add(foundPnl, BorderLayout.CENTER);

        getContentPanel().add(searchPnl, BorderLayout.NORTH);
        getContentPanel().add(resultPnl, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {

        } else {

        }
    }
}