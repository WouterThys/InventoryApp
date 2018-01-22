package com.waldo.inventory.gui.dialogs.editdistributorpartlinkdialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.DistributorPartLink;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

abstract class EditDistributorPartLinkDialogLayout extends IDialog implements IEditedListener, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IComboBox<Distributor> distributorCb;
    ITextField referenceTf;

    private IFormattedTextField priceTf;
    private JComboBox<Statics.PriceUnits> priceUnitsCb;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    DistributorPartLink distributorPartLink;
    DistributorPartLink originalPartLink;


    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditDistributorPartLinkDialogLayout(Window parent, String title) {
        super(parent, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateDistributorCb(DistributorPartLink link) {
        if (link != null && link.getItemId() > DbObject.UNKNOWN_ID) {
            List<Distributor> distributors = new ArrayList<>(cache().getDistributors());

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

    void setPrice(DistributorPartLink partLink) {
        if (partLink != null) {
            try {
                Object obj = priceTf.getValue();
                double val = Double.valueOf(obj.toString());
                Statics.PriceUnits unit = (Statics.PriceUnits) priceUnitsCb.getSelectedItem();
                partLink.setPrice(val, unit);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        showTitlePanel(false);

        // This
        distributorCb = new IComboBox<>(new ArrayList<>(), new ComparatorUtils.DbObjectNameComparator<>(), false);
        distributorCb.addEditedListener(this, "distributorId");

        referenceTf = new ITextField(this, "itemRef");

        NumberFormat format = DecimalFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(3);
        format.setRoundingMode(RoundingMode.HALF_UP);

        priceTf = new IFormattedTextField(format);
        priceUnitsCb = new JComboBox<>(Statics.PriceUnits.values());
        priceUnitsCb.setPreferredSize(new Dimension(60, 20));
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel pricePnl = new JPanel(new BorderLayout());
        pricePnl.add(priceTf, BorderLayout.CENTER);
        pricePnl.add(priceUnitsCb, BorderLayout.EAST);

        JPanel panel = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel);
        gbc.addLine("Distributor: ", GuiUtils.createComponentWithAddAction(distributorCb, this));
        gbc.addLine("Reference: ", referenceTf);
        gbc.addLine("Price: ", pricePnl);

        getContentPanel().add(panel, BorderLayout.CENTER);
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            distributorPartLink  = (DistributorPartLink) args[0];
            originalPartLink = distributorPartLink.createCopy();
            updateDistributorCb(distributorPartLink);

            distributorCb.setSelectedItem(distributorPartLink.getDistributor());
            referenceTf.setText(distributorPartLink.getItemRef());

            priceTf.setValue(distributorPartLink.getPrice().getValue());
            priceUnitsCb.setSelectedItem(distributorPartLink.getPrice().getPriceUnits());
        }
    }
}