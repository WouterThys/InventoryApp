package com.waldo.inventory.gui.dialogs.allaliasesdialog;

import com.waldo.inventory.managers.CacheManager;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

abstract class AllAliasesDialogLayout extends IDialog implements ListSelectionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<String> aliasList;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final String currentAlias;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    AllAliasesDialogLayout(Window parent, String title, String currentAlias) {
        super(parent, title);
        this.currentAlias = currentAlias;

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
        setResizable(true);
        showTitlePanel(false);
        getButtonOK().setEnabled(false);

        // This
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String alias : CacheManager.cache().getAliasList()) {
            listModel.addElement(alias);
        }
        aliasList = new JList<>(listModel);
        aliasList.addListSelectionListener(this);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(aliasList);
        scrollPane.setPreferredSize(new Dimension(120, 200));

        getContentPanel().add(scrollPane);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        aliasList.setSelectedValue(currentAlias,true);
    }
}