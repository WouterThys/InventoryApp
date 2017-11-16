package com.waldo.inventory.gui.dialogs.linkitemdialog;

import com.waldo.inventory.Utils.parser.PcbItemParser;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class LinkPcbItemDialog extends LinkPcbItemDialogLayout {

    public LinkPcbItemDialog(Application application, String title, ProjectPcb projectPcb) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(projectPcb);

        addListeners(createPcbItemListListener(), createItemListListener());
        addDbObjectListeners();
    }

    private ListSelectionListener createPcbItemListListener() {
        return e -> {
            if (!e.getValueIsAdjusting()) {
                selectedPcbItem = pcbPanel.getSelectedComponent();
                if (selectedPcbItem != null) {
                    pcbPanel.updateSelectedValueData(selectedPcbItem);
                    itemPanel.setItemList(selectedPcbItem.getItemLinkList());
                    if (selectedPcbItem.hasMatch()) {
                        itemPanel.selectMatchItem(selectedPcbItem.getMatchedItemLink());
                    } else {
                        if (selectedPcbItem.getItemLinkList().size() > 0) {
                            itemPanel.selectMatchItem(selectedPcbItem.getItemLinkList().get(0));
                        } else {
                            selectedItemLink = null;
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
                selectedItemLink = itemPanel.getSelectedItem();
                itemPanel.updateSelectedValueData(selectedItemLink);
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
                    LinkPcbItemDialog.this,
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

    private void addDbObjectListeners() {
        cache().addOnPcbItemItemLinkChangedListener(new CacheChangedListener<PcbItemItemLink>() {
            @Override
            public void onInserted(PcbItemItemLink link) {}

            @Override
            public void onUpdated(PcbItemItemLink link) {
//                itemPanel.tableInitialize();
//                pcbPanel.tableInitialize();
            }

            @Override
            public void onDeleted(PcbItemItemLink link) {}

            @Override
            public void onCacheCleared() {}
        });

        cache().addOnItemsChangedListener(new CacheChangedListener<Item>() {
            @Override
            public void onInserted(Item item) {}

            @Override
            public void onUpdated(Item item) {
                if (selectedItemLink != null) {
                    List<PcbItemItemLink> links;
                    if (item.isSet()) {
                        links = PcbItemParser.getInstance().linkWithSetItem(selectedPcbItem, item);
                    } else {
                        links = PcbItemParser.getInstance().linkWithItem(selectedPcbItem, item);
                    }

                    for (PcbItemItemLink link : links) {
                        if (link.getItemId() == selectedItemLink.getItemId()) {
                            link.createCopy(selectedItemLink);
                            break;
                        }
                    }
                }

                pcbPanel.updateTable();
                itemPanel.updateTable();
                itemPanel.updateSelectedValueData(selectedItemLink);
            }

            @Override
            public void onDeleted(Item item) {}

            @Override
            public void onCacheCleared() {}
        });
    }

    //
    // Search stuff
    //
    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        List<PcbItemItemLink> itemMatches = new ArrayList<>();
        for (DbObject object : foundObjects) {
            int type = DbObject.getType(object);
            if (type == DbObject.TYPE_ITEM) {
                if (selectedPcbItem != null) {
                    List<PcbItemItemLink> matches = PcbItemParser.getInstance().linkWithItem(selectedPcbItem, (Item) object);
                    if (matches.size() > 0) {
                        itemMatches.addAll(matches);
                    } else {
                        PcbItemItemLink m = new PcbItemItemLink(0, selectedPcbItem, (Item)object);
                        itemMatches.add(m);
                    }
                } else {
                    PcbItemItemLink m = new PcbItemItemLink(0, null, (Item)object);
                    itemMatches.add(m);
                }
            } else if (type == DbObject.TYPE_SET_ITEM) {
                if (selectedPcbItem != null) {
                    List<PcbItemItemLink> matches = PcbItemParser.getInstance().linkWithSetItem(selectedPcbItem, ((SetItem) object).getItem());
                    if (matches.size() > 0) {
                        itemMatches.addAll(matches);
                    } else {
                        PcbItemItemLink m = new PcbItemItemLink(0, selectedPcbItem,  (SetItem)object);
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
        if (selectedPcbItem != null) {
            if (selectedPcbItem.hasMatch()) {
                itemPanel.selectMatchItem(selectedPcbItem.getMatchedItemLink());
            } else {
                selectedItemLink = null;
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
            if (selectedPcbItem != null && selectedItemLink != null) {
                if (selectedPcbItem.hasMatch()) { // Remove link
                    selectedPcbItem.setMatchedItem(null);
                    selectedItemLink.setMatched(false);
                    if (itemLinksToSave.contains(selectedItemLink)) {
                        itemLinksToSave.remove(selectedItemLink);
                    } else {
                        if (!itemLinksToDelete.contains(selectedItemLink)) {
                            itemLinksToDelete.add(selectedItemLink);
                        }
                    }
                } else { // Set link
                    selectedPcbItem.getItemLinkList().add(selectedItemLink);
                    selectedPcbItem.setMatchedItem(selectedItemLink);
                    if (itemLinksToDelete.contains(selectedItemLink)) {
                        itemLinksToDelete.remove(selectedItemLink);
                    } else {
                        if (!itemLinksToSave.contains(selectedItemLink)) {
                            itemLinksToSave.add(selectedItemLink);
                        }
                    }
                }
                pcbPanel.updateTable();
                updateEnabledComponents();
            }
        }
    }
}