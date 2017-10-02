package com.waldo.inventory.gui.dialogs.linkitemdialog;

import com.waldo.inventory.Utils.parser.PcbParser;
import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.classes.PcbItemItemLink;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IObjectSearchPanel;
import com.waldo.inventory.gui.components.tablemodels.ILinkKiCadTableModel;
import com.waldo.inventory.gui.dialogs.linkitemdialog.extras.LinkItemPanel;
import com.waldo.inventory.gui.dialogs.linkitemdialog.extras.LinkPcbPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;


public abstract class LinkPcbItemDialogLayout extends IDialog implements
        IObjectSearchPanel.IObjectSearchListener, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LinkPcbPanel pcbPanel;
    LinkItemPanel itemPanel;

    JButton linkBtn;
    private JButton matchBtn;

    PcbItem selectedComponent;
    PcbItemItemLink selectedMatchItem;

    List<PcbItemItemLink> itemLinksToSave = new ArrayList<>();
    List<PcbItemItemLink> itemLinksToDelete = new ArrayList<>();

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LinkPcbItemDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        linkBtn.setEnabled(selectedComponent != null && selectedMatchItem != null);

        if (selectedComponent != null) {
            if (selectedComponent.hasMatch()) {
                linkBtn.setIcon(imageResource.readImage("Projects.Link.AddLinkBtn"));
            } else {
                linkBtn.setIcon(imageResource.readImage("Projects.Link.RemoveLinkBtn"));
            }
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
        setTitleIcon(imageResource.readImage("Projects.Link.Title"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Panels
        pcbPanel = new LinkPcbPanel(application, ILinkKiCadTableModel.LINK_COMPONENTS);

        itemPanel = new LinkItemPanel(application);
        itemPanel.addSearchListener(this);

        // Buttons
        linkBtn = new JButton(imageResource.readImage("Projects.Link.AddLinkBtn"));
        linkBtn.setToolTipText("Link to item");
        linkBtn.setEnabled(false);
        linkBtn.addActionListener(this);

        matchBtn = new JButton(imageResource.readImage("Projects.Link.ParseBtn"));
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
    public void updateComponents(Object object) {
        if (object != null) {
            PcbParser parser = (PcbParser) object;
            pcbPanel.updateComponents(parser);
        } else {
            //componentList.clear();
        }
    }
}