package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DistributorPartLink;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.tablemodels.IDistributorPartTableModel;
import com.waldo.inventory.gui.dialogs.editdistributorpartlinkdialog.EditDistributorPartLinkDialog;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class EditItemOrderPanel<T extends Item> extends JPanel implements GuiInterface, IdBToolBar.IdbToolBarListener, CacheChangedListener<DistributorPartLink> {

    private IDistributorPartTableModel tableModel;
    private ITable<DistributorPartLink> linkTable;
    private IdBToolBar toolBar;

    private final T newItem;
    private boolean first = true;

    // Listener
    private final Application application;

    public EditItemOrderPanel(Application application, T newItem) {
        this.application = application;
        this.newItem = newItem;

        cache().addListener(DistributorPartLink.class, this);
    }

    private void updateEnabledComponents() {
        boolean enable = linkTable.getSelectedItem() != null;

        toolBar.setEditActionEnabled(enable);
        toolBar.setDeleteActionEnabled(enable);
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
        DistributorPartLink link = new DistributorPartLink(newItem.getId());
        EditDistributorPartLinkDialog dialog = new EditDistributorPartLinkDialog(application, "Add link", link);
        if (dialog.showDialog() == IDialog.OK) {
            link.save();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        DistributorPartLink link = linkTable.getSelectedItem();
        if (link != null) {
            int res = JOptionPane.showConfirmDialog(EditItemOrderPanel.this,
                    "Do you realy want to delete this link?",
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
            EditDistributorPartLinkDialog dialog = new EditDistributorPartLinkDialog(application, "Edit link", link);
            if (dialog.showDialog() == IDialog.OK) {
                link.save();
            }
        }
    }

    //
    // Db listener
    //
    @Override
    public void onInserted(DistributorPartLink link) {
        tableModel.addItem(link);
        linkTable.selectItem(link);
        updateEnabledComponents();
    }

    @Override
    public void onUpdated(DistributorPartLink link) {
        tableModel.updateTable();
        linkTable.selectItem(link);
        updateEnabledComponents();
    }

    @Override
    public void onDeleted(DistributorPartLink link) {
        linkTable.selectItem(null);
        tableModel.removeItem(link);
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
        toolBar  = new IdBToolBar(this, IdBToolBar.VERTICAL);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel distributorPanel = new JPanel(new BorderLayout());

        JScrollPane pane = new JScrollPane(linkTable);
        pane.setPreferredSize(new Dimension(300, 300));

        distributorPanel.add(pane, BorderLayout.CENTER);
        distributorPanel.add(toolBar, BorderLayout.EAST);
        distributorPanel.setBorder(GuiUtils.createTitleBorder("Distributor reference"));

        // Add to panel
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(distributorPanel);
    }

    @Override
    public void updateComponents(Object... object) {
        if (newItem != null) {
            if (first) {
                List<DistributorPartLink> linkList = SearchManager.sm().findDistributorPartLinksForItem(newItem.getId());
                tableModel.setItemList(linkList);
                first = false;
            }
        }
        updateEnabledComponents();
    }
}
