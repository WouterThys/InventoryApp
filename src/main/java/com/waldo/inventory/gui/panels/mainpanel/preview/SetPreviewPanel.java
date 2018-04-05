package com.waldo.inventory.gui.panels.mainpanel.preview;

import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDivisionPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.setitemswizaddialog.SetItemsWizardDialog;
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
    private ITextField setNameTf;
    private IDivisionPanel divisionPanel;
    private ITextField totalItemsTf;
    private IdBToolBar setTb;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private final Set rootSet;

    private Set selectedSet;


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public SetPreviewPanel(Application application, Set rootSet) {
        this.application = application;
        this.rootSet = rootSet;
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
            setNameTf.setText(set.toString());
            divisionPanel.updateComponents(set.getDivision());
            totalItemsTf.setText(String.valueOf(set.getSetItems().size()));
        }
    }

    public void addSet(Set set) {
        EditItemDialog dialog = new EditItemDialog<>(application, "Add set", new Set());
        dialog.setValuesForSet(set);
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
            SetItemsWizardDialog dialog = new SetItemsWizardDialog(application, "Set item magic", set);
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
        addSet(selectedSet);
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
        setNameTf = new ITextField(false, 12);
        setTb = new IdBToolBar(this);
    }

    @Override
    public void initializeLayouts() {
        GuiUtils.GridBagHelper gbc;

        JPanel totalItemsPnl = new JPanel();
        gbc = new GuiUtils.GridBagHelper(totalItemsPnl);
        gbc.addLine("Total items", imageResource.readIcon("Preview.Amount"), totalItemsTf);

        JPanel setNamePnl = new JPanel();
        gbc = new GuiUtils.GridBagHelper(setNamePnl);
        gbc.addLine("Set name", imageResource.readIcon("Items.Tree.Set"), setNameTf);

        JPanel infoPnl = new JPanel();
        infoPnl.setLayout(new BoxLayout(infoPnl, BoxLayout.X_AXIS));
        infoPnl.add(setNamePnl);
        infoPnl.add(totalItemsPnl);

        JPanel centerPnl = new JPanel(new BorderLayout());
        JPanel southPnl = new JPanel(new BorderLayout());

        centerPnl.add(infoPnl, BorderLayout.CENTER);
        centerPnl.add(divisionPanel, BorderLayout.SOUTH);
        southPnl.add(setTb, BorderLayout.WEST);

        setLayout(new BorderLayout());
        add(centerPnl, BorderLayout.CENTER);
        add(southPnl, BorderLayout.NORTH);
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