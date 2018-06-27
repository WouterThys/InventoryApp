package com.waldo.inventory.gui.panels.mainpanel.preview;

import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDivisionPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.setitemswizaddialog.SetItemsWizardCacheDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class SetPreviewPanel extends IPanel implements IdBToolBar.IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private IDivisionPanel divisionPanel;
    private ITextField totalItemsTf;
    private IdBToolBar setTb;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;

    private Set selectedSet;


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public SetPreviewPanel(Application application) {
        this.application = application;
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        boolean enabled = selectedSet != null;
        setTb.setEditActionEnabled(enabled);
        setTb.setDeleteActionEnabled(enabled);
    }

    private void setDetails(Set set) {
        if (set != null) {
            divisionPanel.updateComponents(set.getDivision());
            totalItemsTf.setText(String.valueOf(set.getSetItems().size()));
        }
    }

    public void addSet() {
        EditItemDialog dialog = new EditItemDialog<>(application, "Add set", new Set());
        dialog.showDialog();
    }

    public void editSet(Set set) {
        if (set != null) {
            EditItemDialog dialog = new EditItemDialog<>(application, "Edit set", set);
            dialog.setValuesForSet(set);
            dialog.showDialog();
        }
    }

    public void deleteSet(Set set) {
        if (set != null) {
            int res = JOptionPane.showConfirmDialog(application, "Are you sure you want to delete " + set);
            if (res == JOptionPane.YES_OPTION) {
                set.delete();
            }
        }
    }

    public void onSetWizard(Set set) {
        if (set != null && set.canBeSaved()) {
            // Add set items wizard dialog
            SetItemsWizardCacheDialog dialog = new SetItemsWizardCacheDialog(application, "Set item magic", set);
            dialog.showDialog();
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        setDetails(selectedSet);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        addSet();
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        deleteSet(selectedSet);
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        editSet(selectedSet);
    }

    //
    // Gui
    //
    @Override
    public void initializeComponents() {
        divisionPanel = new IDivisionPanel(false);
        totalItemsTf = new ITextField(false, 4);
        setTb = new IdBToolBar(this);
    }

    @Override
    public void initializeLayouts() {
        GuiUtils.GridBagHelper gbc;

        JPanel totalItemsPnl = new JPanel();
        gbc = new GuiUtils.GridBagHelper(totalItemsPnl);
        gbc.addLine("Total items", imageResource.readIcon("Amount.S"), totalItemsTf);

        JPanel centerPnl = new JPanel(new BorderLayout());
        JPanel southPnl = new JPanel(new BorderLayout());

        centerPnl.add(totalItemsPnl, BorderLayout.EAST);
        centerPnl.add(divisionPanel, BorderLayout.CENTER);
        southPnl.add(setTb, BorderLayout.WEST);

        setLayout(new BorderLayout());
        add(centerPnl, BorderLayout.CENTER);
        add(southPnl, BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length == 0 || args[0] == null) {
            setVisible(false);
            selectedSet = null;
        } else {
            setVisible(true);
            if (args[0] instanceof Set) {
                selectedSet = (Set) args[0];
            }
            setDetails(selectedSet);
            updateEnabledComponents();
        }
    }
}
