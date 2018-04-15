package com.waldo.inventory.gui.dialogs.editdistributorpartlinkdialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.DistributorPartLink;
import com.waldo.inventory.gui.components.IObjectDialog;
import com.waldo.inventory.gui.dialogs.distributorsdialog.DistributorsDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.IFormattedTextField;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class EditDistributorPartLinkDialog extends IObjectDialog<DistributorPartLink> implements ActionListener {

    private IComboBox<Distributor> distributorCb;
    private ITextField referenceTf;

    private IFormattedTextField priceTf;
    private JComboBox<Statics.PriceUnits> priceUnitsCb;

    private final DistributorType type;


    public EditDistributorPartLinkDialog(Window parent, DistributorPartLink distributorPartLink) {
        super(parent, "Distributor link", distributorPartLink, DistributorPartLink.class);

        if (distributorPartLink != null && distributorPartLink.getDistributorId() > DbObject.UNKNOWN_ID) {
            this.type = distributorPartLink.getDistributor().getDistributorType();
        } else {
            this.type = null;
        }

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public EditDistributorPartLinkDialog(Window parent, DistributorPartLink distributorPartLink, DistributorType type) {
        super(parent, "Distributor link", distributorPartLink, DistributorPartLink.class);

        this.type = type;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public DistributorPartLink getDistributorPartLink() {
        return getObject();
    }

    @Override
    public VerifyState verify(DistributorPartLink toVerify) {
        VerifyState ok = VerifyState.Ok;
        if (getDistributorPartLink().getDistributorId() <= DbObject.UNKNOWN_ID) {
            JOptionPane.showMessageDialog(this,
                    "Distributor can not be empty..",
                    "Empty distributor",
                    JOptionPane.ERROR_MESSAGE);
            ok = VerifyState.Error;
        }

        if (ok != VerifyState.Error) {
            if (getDistributorPartLink().getReference().isEmpty()) {
                referenceTf.setError("Reference can not be empty..");
                ok = VerifyState.Error;
            }
        }

        return ok;
    }

    @Override
    public void initializeComponents() {
        // Dialog
        showTitlePanel(false);

        // This
        distributorCb = new IComboBox<>(new ArrayList<>(), new ComparatorUtils.DbObjectNameComparator<>(), false);
        distributorCb.addEditedListener(this, "distributorId");

        referenceTf = new ITextField(this, "reference");

        NumberFormat format = DecimalFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(3);
        format.setRoundingMode(RoundingMode.HALF_UP);

        priceTf = new IFormattedTextField(format);
        priceUnitsCb = new JComboBox<>(Statics.PriceUnits.values());
        priceUnitsCb.setPreferredSize(new Dimension(60, 20));

        priceTf.addEditedListener(this, "price", double.class);

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
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        updateDistributorCb(getDistributorPartLink());

        distributorCb.setSelectedItem(getDistributorPartLink().getDistributor());
        referenceTf.setText(getDistributorPartLink().getReference());

        priceTf.setValue(getDistributorPartLink().getPrice().getValue());
        priceUnitsCb.setSelectedItem(getDistributorPartLink().getPrice().getPriceUnits());

    }

    public void enableDistributor(boolean enable) {
        distributorCb.setEnabled(enable);
    }


    //
    // Add distributor
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        DistributorsDialog distributorsDialog = new DistributorsDialog(EditDistributorPartLinkDialog.this);
        distributorsDialog.showDialog();

        updateDistributorCb(getDistributorPartLink());
    }

    private void updateDistributorCb(DistributorPartLink link) {
        if (link != null) {
            java.util.List<Distributor> distributors = new ArrayList<>(SearchManager.sm().findDistributorsByType(type));
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
}