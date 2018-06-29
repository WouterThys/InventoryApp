package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.DistributorPartLink;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.IDistributorPartTableModel;
import com.waldo.inventory.gui.dialogs.advancedsearchdialog.AdvancedSearchDialog;
import com.waldo.inventory.gui.dialogs.editdistributorpartlinkdialog.EditDistributorPartLinkDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils.GridBagHelper;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.managers.CacheManager.cache;

public class EditItemOrderPanel<T extends Item> extends JPanel implements GuiUtils.GuiInterface, IdBToolBar.IdbToolBarListener, CacheChangedListener<DistributorPartLink> {

    // Distributor
    private IDistributorPartTableModel tableModel;
    private ITable<DistributorPartLink> linkTable;
    private IdBToolBar toolBar;

    // Linked items
    private ICheckBox discourageOrderCb;
    private ITextField replacementItemTf;
    private ITextField relatedItemTf;
    private ICheckBox autoOrderCb;

    private DefaultComboBoxModel<Distributor> autoOrderByModel;
    private IComboBox<Distributor> autoOrderByCb;

    private IActions.SearchAction searchReplacementItemAa;
    private IActions.EditAction editReplacementItemAa;
    private IActions.DeleteAction deleteReplacementItemAa;

    private IActions.SearchAction searchRelatedItemAa;
    private IActions.EditAction editRelatedItemAa;
    private IActions.DeleteAction deleteRelatedItemAa;

    private final T newItem;
    private boolean first = true;

    // Listener
    private final Window parent;
    private final IEditedListener editedListener;

    public EditItemOrderPanel(Window parent, T newItem, @NotNull IEditedListener listener) {
        this.parent = parent;
        this.newItem = newItem;
        this.editedListener = listener;

        cache().addListener(DistributorPartLink.class, this);
    }

    private void updateEnabledComponents() {
        boolean enable = linkTable.getSelectedItem() != null;

        toolBar.setEditActionEnabled(enable);
        toolBar.setDeleteActionEnabled(enable);

        boolean hasReplacement = (newItem != null) && (newItem.getReplacementItemId() > DbObject.UNKNOWN_ID);
        boolean hasRelated = (newItem != null) && (newItem.getRelatedItemId() > DbObject.UNKNOWN_ID);

        editRelatedItemAa.setEnabled(hasRelated);
        deleteRelatedItemAa.setEnabled(hasRelated);

        editReplacementItemAa.setEnabled(hasReplacement);
        deleteReplacementItemAa.setEnabled(hasReplacement);

        boolean canAutoUpdate = (settings().getGeneralSettings().isAutoOrderEnabled()) && (tableModel.getItemList().size() > 0);
        autoOrderCb.setEnabled(canAutoUpdate);
        autoOrderByCb.setEnabled(canAutoUpdate && autoOrderCb.isSelected());
    }

    private JPanel createDistributorPanel() {
        JPanel distributorPanel = new JPanel(new BorderLayout());

        JScrollPane pane = new JScrollPane(linkTable);
        distributorPanel.add(pane, BorderLayout.CENTER);
        distributorPanel.add(toolBar, BorderLayout.EAST);

        distributorPanel.setBorder(GuiUtils.createInlineTitleBorder("Distributor reference"));
        return  distributorPanel;
    }

    private JPanel createOrderRulesPanel() {
        JPanel linksPanel = new JPanel(new BorderLayout());
        JPanel helperPnl = new JPanel();
        GridBagHelper gbc = new GridBagHelper(helperPnl, 150);

        gbc.addLine("Discourage orders: ", discourageOrderCb);
        gbc.addLine("Replaced with: ", GuiUtils.createComponentWithActions(replacementItemTf, searchReplacementItemAa, editReplacementItemAa, deleteReplacementItemAa));
        gbc.addLine("Related to: ", GuiUtils.createComponentWithActions(relatedItemTf, searchRelatedItemAa, editRelatedItemAa, deleteRelatedItemAa));
        gbc.addLine("Automatic orders: ", autoOrderCb);
        gbc.addLine("Auto order by: ", autoOrderByCb);

        linksPanel.add(helperPnl, BorderLayout.NORTH);
        linksPanel.setBorder(GuiUtils.createInlineTitleBorder("ItemOrder rules"));
        return linksPanel;
    }

    //
    // Local
    //
    private void onSearchReplacement() {
        if (newItem != null) {
            AdvancedSearchDialog searchDialog = new AdvancedSearchDialog(parent, false);
            if (searchDialog.showDialog() == IDialog.OK) {
                Item newReplacement = searchDialog.getSelectedItem();

                if (newReplacement != null) {
                    int res = JOptionPane.YES_OPTION;
                    if (newItem.getReplacementItemId() > DbObject.UNKNOWN_ID) {
                        res = JOptionPane.showConfirmDialog(
                                parent,
                                "There was already an item selected, replace it?",
                                "Replace",
                                JOptionPane.YES_NO_OPTION
                        );
                    }
                    if (res == JOptionPane.YES_OPTION) {
                        newItem.setReplacementItemId(newReplacement.getId());
                        replacementItemTf.setText(newReplacement.toString());
                        editedListener.onValueChanged(replacementItemTf, "replacementItemId", 0, 0);
                    }
                }
            }
            updateEnabledComponents();
        }
    }

    private void onEditReplacement() {
        if ((newItem != null) && (newItem.getReplacementItemId() > DbObject.UNKNOWN_ID)) {
            EditItemDialog editItemDialog = new EditItemDialog<>(parent, "Edit item", newItem.getReplacementItem());
            if (editItemDialog.showDialog() == IDialog.OK) {
                replacementItemTf.setText(newItem.getReplacementItem().toString());
            }
            updateEnabledComponents();
        }
    }

    private void onDeleteReplacement() {
        if ((newItem != null) && (newItem.getReplacementItemId() > DbObject.UNKNOWN_ID)) {
            int res = JOptionPane.showConfirmDialog(
                    EditItemOrderPanel.this,
                    "Delete replacement item?",
                    "Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (res == JOptionPane.YES_OPTION) {
                newItem.setReplacementItemId(0);
                replacementItemTf.setText("");
                editedListener.onValueChanged(replacementItemTf, "replacementItemId", 0, 0);
            }
            updateEnabledComponents();
        }
    }


    private void onSearchRelated() {
        if (newItem != null) {
            AdvancedSearchDialog searchDialog = new AdvancedSearchDialog(parent, false);
            if (searchDialog.showDialog() == IDialog.OK) {
                Item newRelated = searchDialog.getSelectedItem();

                if (newRelated != null) {
                    int res = JOptionPane.YES_OPTION;
                    if (newItem.getRelatedItemId() > DbObject.UNKNOWN_ID) {
                        res = JOptionPane.showConfirmDialog(
                                parent,
                                "There was already an item selected, replace it?",
                                "Replace",
                                JOptionPane.YES_NO_OPTION
                        );
                    }
                    if (res == JOptionPane.YES_OPTION) {
                        newItem.setRelatedItemId(newRelated.getId());
                        relatedItemTf.setText(newRelated.toString());
                        editedListener.onValueChanged(relatedItemTf, "relatedItemId", 0, 0);
                    }
                }
            }
            updateEnabledComponents();
        }
    }

    private void onEditRelated() {
        if ((newItem != null) && (newItem.getRelatedItemId() > DbObject.UNKNOWN_ID)) {
            EditItemDialog editItemDialog = new EditItemDialog<>(parent, "Edit item", newItem.getRelatedItem());
            if (editItemDialog.showDialog() == IDialog.OK) {
                relatedItemTf.setText(newItem.getRelatedItem().toString());
            }
            updateEnabledComponents();
        }
    }

    private void onDeleteRelated() {
        if ((newItem != null) && (newItem.getRelatedItemId() > DbObject.UNKNOWN_ID)) {
            int res = JOptionPane.showConfirmDialog(
                    EditItemOrderPanel.this,
                    "Delete related item?",
                    "Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (res == JOptionPane.YES_OPTION) {
                newItem.setRelatedItemId(0);
                relatedItemTf.setText("");
                editedListener.onValueChanged(relatedItemTf, "relatedItemId", 0, 0);
            }
            updateEnabledComponents();
        }
    }


    private void updateAutoOrderByCb() {
        if (newItem != null) {
            autoOrderByModel.removeAllElements();

            autoOrderByModel.addElement(Distributor.getUnknownDistributor());
            for (DistributorPartLink link : tableModel.getItemList()) {
                autoOrderByModel.addElement(link.getDistributor());
            }

            if (newItem.getAutoOrderById() > DbObject.UNKNOWN_ID) {
                autoOrderByCb.setSelectedItem(newItem.getAutoOrderBy());
            } else {
                autoOrderByCb.setSelectedIndex(0);
            }
        }
    }

    private void onAutoOrderBy(Distributor distributor) {
        if (newItem != null) {
            if (distributor != null) {
                newItem.setAutoOrderById(distributor.getId());
            } else {
                newItem.setAutoOrderById(0);
            }
            editedListener.onValueChanged(autoOrderByCb, "autoOrderById", 0, 0);
        }
    }

    //
    // Toolbar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        first = true;
        updateComponents();
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        DistributorPartLink link = new DistributorPartLink(newItem);
        EditDistributorPartLinkDialog dialog = new EditDistributorPartLinkDialog(parent, link, Statics.DistributorType.Items);
        dialog.showDialog();
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        DistributorPartLink link = linkTable.getSelectedItem();
        if (link != null) {
            int res = JOptionPane.showConfirmDialog(EditItemOrderPanel.this,
                    "Do you really want to delete this link?",
                    "Delete link",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                link.delete();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        DistributorPartLink link = linkTable.getSelectedItem();
        if (link != null) {
            EditDistributorPartLinkDialog dialog = new EditDistributorPartLinkDialog(parent, link);
            dialog.showDialog();
        }
    }

    //
    // Db listener
    //
    @Override
    public void onInserted(DistributorPartLink link) {
        tableModel.addItem(link);
        linkTable.selectItem(link);
        updateAutoOrderByCb();
        updateEnabledComponents();
    }

    @Override
    public void onUpdated(DistributorPartLink link) {
        tableModel.updateTable();
        linkTable.selectItem(link);
        updateAutoOrderByCb();
        updateEnabledComponents();
    }

    @Override
    public void onDeleted(DistributorPartLink link) {
        tableModel.removeItem(link);
        linkTable.selectItem(null);

        if (newItem != null) {
            if (newItem.getAutoOrderById() == link.getDistributorId()) {
                onAutoOrderBy(Distributor.getUnknownDistributor());
            }
        }

        updateAutoOrderByCb();
        updateEnabledComponents();
    }

    @Override
    public void onCacheCleared() {

    }

    //
    // Gui
    //
    @Override
    public void initializeComponents() {
        // Table
        tableModel = new IDistributorPartTableModel();
        linkTable = new ITable<>(tableModel);
        linkTable.getSelectionModel().addListSelectionListener(e -> updateEnabledComponents());

        // Toolbar
        toolBar = new IdBToolBar(this, IdBToolBar.VERTICAL);

        //
        discourageOrderCb = new ICheckBox();
        discourageOrderCb.addEditedListener(editedListener, "discourageOrder");

        replacementItemTf = new ITextField(false);
        relatedItemTf = new ITextField(false);

        autoOrderCb = new ICheckBox();
        autoOrderCb.addEditedListener(editedListener, "autoOrder");
        autoOrderCb.addActionListener(e -> updateEnabledComponents());

        autoOrderByModel = new DefaultComboBoxModel<>();
        autoOrderByCb = new IComboBox<>(autoOrderByModel);
        autoOrderByCb.addItemListener(e -> {
            SwingUtilities.invokeLater(() -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    onAutoOrderBy((Distributor)autoOrderByCb.getSelectedItem());
                }
            });
        });

        searchReplacementItemAa = new IActions.SearchAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSearchReplacement();
            }
        };
        editReplacementItemAa = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditReplacement();
            }
        };
        deleteReplacementItemAa = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteReplacement();
            }
        };

        searchRelatedItemAa = new IActions.SearchAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSearchRelated();
            }
        };
        editRelatedItemAa = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditRelated();
            }
        };
        deleteRelatedItemAa = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteRelated();
            }
        };
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel distributorPanel = createDistributorPanel();
        JPanel orderRulesPanel = createOrderRulesPanel();

        // Add to panel
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(distributorPanel);
        add(orderRulesPanel);
    }

    @Override
    public void updateComponents(Object... object) {
        if (newItem != null) {
            if (first) {
                List<DistributorPartLink> linkList = SearchManager.sm().findDistributorPartLinksForItem(newItem.getId());
                tableModel.setItemList(linkList);
                first = false;
            }

            // ItemOrder rules
            discourageOrderCb.setSelected(newItem.isDiscourageOrder());
            autoOrderCb.setSelected(newItem.isAutoOrder());

            if (newItem.getReplacementItemId() > DbObject.UNKNOWN_ID) {
                replacementItemTf.setText(newItem.getReplacementItem().toString());
            }

            if (newItem.getRelatedItemId() > DbObject.UNKNOWN_ID) {
                relatedItemTf.setText(newItem.getRelatedItem().toString());
            }

            updateAutoOrderByCb();
        }

        updateEnabledComponents();
    }
}
