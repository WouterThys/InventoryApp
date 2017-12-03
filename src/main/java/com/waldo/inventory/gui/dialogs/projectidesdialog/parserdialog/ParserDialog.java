package com.waldo.inventory.gui.dialogs.projectidesdialog.parserdialog;

import com.waldo.inventory.Utils.parser.PcbParser;
import com.waldo.inventory.classes.dbclasses.ParserItemLink;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.projectidesdialog.parserdialog.editparseritemlinkdialog.EditParserItemLinkDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ParserDialog extends ParserDialogLayout implements CacheChangedListener<ParserItemLink> {


    public ParserDialog(Application application, String title, boolean useParser, PcbParser parser) {
        super(application, title, useParser, parser);

        initializeComponents();
        initializeLayouts();

        cache().addOnParserItemLinkChangedListener(this);

        updateComponents();
    }

    public boolean useParser() {
        return useParserCb.isSelected();
    }

    public PcbParser getParser() {
        return (PcbParser) parserCb.getSelectedItem();
    }

    //
    // User parser checkbox
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        parserCb.setEnabled(useParserCb.isSelected());
        updateEnabledComponents();
    }

    //
    // Combo box
    //
    @Override
    public void itemStateChanged(ItemEvent e) {
       if (e.getStateChange() == ItemEvent.SELECTED) {
           selectedParser = getParser();
           tableInitialize(selectedParser);
           updateEnabledComponents();
       }
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        tableInitialize(selectedParser);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (selectedParser != null) {
            ParserItemLink newLink = new ParserItemLink(selectedParser.getName());
            EditParserItemLinkDialog dialog = new EditParserItemLinkDialog(application, "Add link", newLink);

            if (dialog.showDialog() == IDialog.OK) {
                newLink.save();
            }
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedLink != null) { // TODO multiple delete
            int res = JOptionPane.showConfirmDialog(ParserDialog.this, "Are you sure you want to delete \"" + selectedLink.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedLink.delete();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedLink != null) {
            EditParserItemLinkDialog dialog = new EditParserItemLinkDialog(application, "Edit link", selectedLink);

            if (dialog.showDialog() == IDialog.OK) {
                selectedLink.save();
            }
        }
    }

    //
    // List item selected
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            selectedLink = tableGetSelected();
            updateEnabledComponents();
        }
    }

    //
    // Parser item link listener
    //
    @Override
    public void onInserted(ParserItemLink link) {
        tableAdd(link);
        selectedLink = link;
        tableSelect(link);
    }

    @Override
    public void onUpdated(ParserItemLink link) {
        tableUpdate();
        tableSelect(selectedLink);
    }

    @Override
    public void onDeleted(ParserItemLink link) {
        tableDelete(link);
        selectedLink = null;
        tableSelect(null);
    }

    @Override
    public void onCacheCleared() {

    }
}