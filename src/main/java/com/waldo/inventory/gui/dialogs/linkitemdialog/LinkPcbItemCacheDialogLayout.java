package com.waldo.inventory.gui.dialogs.linkitemdialog;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.components.IObjectSearchPanel;
import com.waldo.inventory.gui.components.tablemodels.ILinkPcbItemTableModel;
import com.waldo.inventory.gui.dialogs.linkitemdialog.extras.LinkItemPanel;
import com.waldo.inventory.gui.dialogs.linkitemdialog.extras.LinkPcbPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;


abstract class LinkPcbItemCacheDialogLayout extends ICacheDialog implements
        IObjectSearchPanel.SearchListener<Item>, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LinkPcbPanel pcbPanel;
    LinkItemPanel itemPanel;

    JButton linkBtn;
    private JButton matchBtn;

    PcbItem selectedPcbItem;
    PcbItemItemLink selectedItemLink;

    final List<PcbItemItemLink> itemLinksToSave = new ArrayList<>();
    final List<PcbItemItemLink> itemLinksToDelete = new ArrayList<>();

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LinkPcbItemCacheDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        linkBtn.setEnabled(selectedPcbItem != null && selectedItemLink != null);

        if (selectedPcbItem != null) {
            // TODO #24
//            if (selectedPcbItem.hasMatchedItem()) {
//                linkBtn.setIcon(imageResource.readIcon("Projects.Link.RemoveLinkBtn"));
//            } else {
//                linkBtn.setIcon(imageResource.readIcon("Projects.Link.AddLinkBtn"));
//            }
        }

        if (itemLinksToSave.size() > 0 || itemLinksToDelete.size() > 0) {
            getButtonNeutral().setEnabled(true);
        } else {
            getButtonNeutral().setEnabled(false);
        }
    }

    void addListeners(ListSelectionListener kcListListener, ListSelectionListener itemListListener) {
        pcbPanel.addListSelectionListener(kcListListener);
        itemPanel.addListSelectionListener(itemListListener);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setResizable(true);
        setTitleIcon(imageResource.readIcon("Link.Edit.L"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Panels
        pcbPanel = new LinkPcbPanel(ILinkPcbItemTableModel.LINK_COMPONENTS);

        itemPanel = new LinkItemPanel(this);
        //itemPanel.addSearchListener(this);

        // Buttons
        linkBtn = new JButton(imageResource.readIcon("Link.New.M"));
        linkBtn.setToolTipText("Link to item");
        linkBtn.setEnabled(false);
        linkBtn.addActionListener(this);

        matchBtn = new JButton(imageResource.readIcon("Parse.M"));
        matchBtn.setToolTipText("Find match");
        matchBtn.setEnabled(false);
        matchBtn.addActionListener(this);

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.X_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(linkBtn);
        buttonPanel.add(matchBtn);

        getContentPanel().add(pcbPanel);
        getContentPanel().add(buttonPanel);
        getContentPanel().add(itemPanel);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,5,10,5));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            pcbPanel.updateComponents(object);
        } else {
            //componentList.clear();
        }
    }
}