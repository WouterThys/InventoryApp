package com.waldo.inventory.gui.dialogs.linkitemdialog;

import com.waldo.inventory.classes.KcItemLink;
import com.waldo.inventory.classes.kicad.KcComponent;
import com.waldo.inventory.Utils.parser.KiCad.KiCadParser;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.gui.Application;

import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class LinkItemDialog extends LinkItemDialogLayout {


    public LinkItemDialog(Application application, String title, KiCadParser parser) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(parser);

        addListeners(createKcListListener(), createItemListListener());
    }

    private ListSelectionListener createKcListListener() {
        return e -> {
            if (!e.getValueIsAdjusting()) {
                selectedComponent = kcPanel.getSelectedComponent();
                if (selectedComponent != null) {
                    kcPanel.updateSelectedValueData(selectedComponent);
                    itemPanel.setItemList(selectedComponent.getItemMatchMap());
                    if (selectedComponent.hasMatch()) {
                        itemPanel.selectMatchItem(selectedComponent.getMatchedItem());
                    } else {
                        selectedMatchItem = null;
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
    // Search stuff
    //
    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        List<KcItemLink> itemMatches = new ArrayList<>();
        for (DbObject object : foundObjects) {
            int type = DbObject.getType(object);
            if (type == DbObject.TYPE_ITEM) {
                if (selectedComponent != null) {
                    List<KcItemLink> matches = KcComponent.findInItem(selectedComponent, (Item) object);
                    if (matches.size() > 0) {
                        itemMatches.addAll(matches);
                    } else {
                        KcItemLink m = new KcItemLink(0, selectedComponent, (Item)object);
                        itemMatches.add(m);
                    }
                } else {
                    KcItemLink m = new KcItemLink(0, null, (Item)object);
                    itemMatches.add(m);
                }
            } else if (type == DbObject.TYPE_SET_ITEM) {
                if (selectedComponent != null) {
                    List<KcItemLink> matches = KcComponent.findInSet(selectedComponent, ((SetItem) object).getItem());
                    if (matches.size() > 0) {
                        itemMatches.addAll(matches);
                    } else {
                        KcItemLink m = new KcItemLink(0,selectedComponent,  (SetItem)object);
                        itemMatches.add(m);
                    }
                } else {
                    KcItemLink m = new KcItemLink(0, null, (SetItem) object);
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
                } else {
                    selectedComponent.setMatchedItem(selectedMatchItem);
                }
                updateLinkBtn();
                kcPanel.updateTable();
                updateLinkBtn();
            }
        }
    }
}