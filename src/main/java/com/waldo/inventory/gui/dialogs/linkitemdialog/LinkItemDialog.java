package com.waldo.inventory.gui.dialogs.linkitemdialog;

import com.waldo.inventory.Utils.parser.PcbItemParser;
import com.waldo.inventory.Utils.parser.PcbParser;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.PcbItemItemLink;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class LinkItemDialog extends LinkItemDialogLayout implements DbObjectChangedListener<PcbItemItemLink> {


    public LinkItemDialog(Application application, String title, PcbParser parser) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(parser);

        addListeners(createKcListListener(), createItemListListener());
        DbManager.db().addOnKcItemLinkChangedListener(this);
    }

    private ListSelectionListener createKcListListener() {
        return e -> {
            if (!e.getValueIsAdjusting()) {
                selectedComponent = kcPanel.getSelectedComponent();
                if (selectedComponent != null) {
                    kcPanel.updateSelectedValueData(selectedComponent);
                    itemPanel.setItemList(selectedComponent.getItemLinkList());
                    if (selectedComponent.hasMatch()) {
                        itemPanel.selectMatchItem(selectedComponent.getMatchedItem());
                    } else {
                        if (selectedComponent.getItemLinkList().size() > 0) {
                            itemPanel.selectMatchItem(selectedComponent.getItemLinkList().get(0));
                        } else {
                            selectedMatchItem = null;
                        }
                    }
                }
                updateEnabledComponents();
            }
        };
    }

    private ListSelectionListener createItemListListener() {
        return e -> {
            if (!e.getValueIsAdjusting()) {
                selectedMatchItem = itemPanel.getSelectedItem();
                itemPanel.updateSelectedValueData(selectedMatchItem);
                updateEnabledComponents();
            }
        };
    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        if (itemLinksToSave.size() > 0) {
            int result = JOptionPane.showConfirmDialog(
                    LinkItemDialog.this,
                    "There are unsaved links, do you realy want to close this dialog?",
                    "Unsaved links",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                super.onOK();
            }
        } else {
            super.onOK();
        }
    }

    @Override
    protected void onNeutral() {
        for (PcbItemItemLink link : itemLinksToSave) {
            link.save();
        }
        itemLinksToSave.clear();
        getButtonNeutral().setEnabled(false);
    }

    //
    // Db listener
    //


    @Override
    public void onInserted(PcbItemItemLink link) {}

    @Override
    public void onUpdated(PcbItemItemLink link) {}

    @Override
    public void onDeleted(PcbItemItemLink link) {}

    @Override
    public void onCacheCleared() {}

    //
    // Search stuff
    //
    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        List<PcbItemItemLink> itemMatches = new ArrayList<>();
        for (DbObject object : foundObjects) {
            int type = DbObject.getType(object);
            if (type == DbObject.TYPE_ITEM) {
                if (selectedComponent != null) {
                    List<PcbItemItemLink> matches = PcbItemParser.getInstance().linkWithItem(selectedComponent, (Item) object);
                    if (matches.size() > 0) {
                        itemMatches.addAll(matches);
                    } else {
                        PcbItemItemLink m = new PcbItemItemLink(0, selectedComponent, (Item)object);
                        itemMatches.add(m);
                    }
                } else {
                    PcbItemItemLink m = new PcbItemItemLink(0, null, (Item)object);
                    itemMatches.add(m);
                }
            } else if (type == DbObject.TYPE_SET_ITEM) {
                if (selectedComponent != null) {
                    List<PcbItemItemLink> matches = PcbItemParser.getInstance().linkWithSetItem(selectedComponent, ((SetItem) object).getItem());
                    if (matches.size() > 0) {
                        itemMatches.addAll(matches);
                    } else {
                        PcbItemItemLink m = new PcbItemItemLink(0,selectedComponent,  (SetItem)object);
                        itemMatches.add(m);
                    }
                } else {
                    PcbItemItemLink m = new PcbItemItemLink(0, null, (SetItem) object);
                    itemMatches.add(m);
                }
            }
        }

        itemPanel.setItemList(itemMatches);
    }

    @Override
    public void onSearchCleared() {
        if (selectedComponent != null) {
            if (selectedComponent.hasMatch()) {
                itemPanel.selectMatchItem(selectedComponent.getMatchedItem());
            } else {
                selectedMatchItem = null;
            }
        } else {
            itemPanel.clearItemList();
        }
        updateEnabledComponents();
    }

    //
    // Buttons clicked
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(linkBtn)) {
            if (selectedComponent != null && selectedMatchItem != null) {
                if (selectedComponent.hasMatch()) {
                    selectedComponent.setMatchedItem(null);
                    selectedMatchItem.setMatched(false);
                    if (itemLinksToSave.contains(selectedMatchItem)) {
                        itemLinksToSave.remove(selectedMatchItem);
                    } else {
                        if (!itemLinksToDelete.contains(selectedMatchItem)) {
                            itemLinksToDelete.add(selectedMatchItem);
                        }
                    }
                } else {
                    selectedComponent.getItemLinkList().add(selectedMatchItem);
                    selectedComponent.setMatchedItem(selectedMatchItem);
                    if (itemLinksToDelete.contains(selectedMatchItem)) {
                        itemLinksToDelete.remove(selectedMatchItem);
                    } else {
                        if (!itemLinksToSave.contains(selectedMatchItem)) {
                            itemLinksToSave.add(selectedMatchItem);
                        }
                    }
                }
                kcPanel.updateTable();
                updateEnabledComponents();
            }
        }
    }
}