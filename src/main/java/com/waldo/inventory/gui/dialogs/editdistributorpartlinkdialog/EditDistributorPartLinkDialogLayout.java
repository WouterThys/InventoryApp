package com.waldo.inventory.gui.dialogs.editdistributorpartlinkdialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.DistributorPartLink;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public abstract class EditDistributorPartLinkDialogLayout extends IDialog implements IEditedListener, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IComboBox<Distributor> distributorCb;
    ITextField referenceTf;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    DistributorPartLink distributorPartLink;


    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditDistributorPartLinkDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateComboBox(DistributorPartLink link) {
        if (link != null && link.getItemId() > DbObject.UNKNOWN_ID) {
            List<Distributor> distributors = new ArrayList<>(DbManager.db().getDistributors());

            if (link.getDistributorId() < DbObject.UNKNOWN_ID) {
                List<DistributorPartLink> knownLinks = SearchManager.sm().findDistributorPartLinksForItem(distributorPartLink.getItemId());
                if (knownLinks.size() > 0) {
                    for (DistributorPartLink l : knownLinks) {
                        if (l.getDistributorId() > DbObject.UNKNOWN_ID) {
                            distributors.remove(l.getDistributor());
                        }
                    }
                }
            }

            distributorCb.updateList(distributors);
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        showTitlePanel(false);

        // Combo box
        distributorCb = new IComboBox<>(new ArrayList<>(), new ComparatorUtils.DbObjectNameComparator<>(), false);
        distributorCb.addEditedListener(this, "distributorId");

        // Reference
        referenceTf = new ITextField(this, "itemRef");
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(panel);
        gbc.addLine("Distributor: ", PanelUtils.createComboBoxWithButton(distributorCb, this));
        gbc.addLine("Reference: ", referenceTf);

        getContentPanel().add(panel, BorderLayout.CENTER);
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            distributorPartLink  = (DistributorPartLink) args[0];
            updateComboBox(distributorPartLink);

            distributorCb.setSelectedItem(distributorPartLink.getDistributor());
            referenceTf.setText(distributorPartLink.getItemRef());
        }
    }
}