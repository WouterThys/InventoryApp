package com.waldo.inventory.gui.panels.mainpanel.preview;

import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDivisionPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.IdBToolBar.IdbToolBarListener;
import com.waldo.inventory.gui.dialogs.editdivisiondialog.EditDivisionDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class DivisionPreviewPanel extends IPanel implements IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IDivisionPanel divisionPanel;
    private ITextField totalItemsTf;
    private ITextField totalPriceTf;
    private IdBToolBar divisionTb;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private final Division rootDivision;

    private Division selectedDivision;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public DivisionPreviewPanel(Application application, Division rootDivision) {
        super();
        this.application = application;
        this.rootDivision = rootDivision;
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        boolean enabled = selectedDivision != null && selectedDivision.canBeSaved();
        divisionTb.setEditActionEnabled(enabled);
        divisionTb.setDeleteActionEnabled(enabled);
    }

    private void setDetails(Division division) {
        if (division != null) {
            divisionPanel.updateComponents(division);
            totalPriceTf.setText(String.valueOf(division.getTotalItemPrice()));
            totalItemsTf.setText(String.valueOf(division.getItemList().size()));
        }
    }

    public void addDivision(Division division) {
        Division parent;
        if (division != null && division.canBeSaved()) {
            parent = division;
        } else {
            parent = rootDivision;
        }
        EditDivisionDialog dialog = new EditDivisionDialog(application, new Division(parent), parent);
        dialog.showDialog();
    }

    public void editDivision(Division division) {
        Division parent = null;
        if (division != null && division.canBeSaved()) {
            parent = division.getParentDivision();
        }
        if (parent == null) {
            parent = rootDivision;
        }
        EditDivisionDialog dialog = new EditDivisionDialog(application, division, parent);
        dialog.showDialog();
    }

    public void deleteDivision(Division division) {
        if (division != null && division.canBeSaved()) {
            int res = JOptionPane.showConfirmDialog(application, "Are you sure you want to delete " + division);
            if (res == JOptionPane.YES_OPTION) {
                division.delete();
            }
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
        setDetails(selectedDivision);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        addDivision(selectedDivision);
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        deleteDivision(selectedDivision);
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        editDivision(selectedDivision);
    }

    //
    // Gui
    //
    @Override
    public void initializeComponents() {
        divisionPanel = new IDivisionPanel(false);

        totalItemsTf = new ITextField(false, 8);
        totalPriceTf = new ITextField(false, 8);
        divisionTb = new IdBToolBar(this);
    }

    @Override
    public void initializeLayouts() {
        GuiUtils.GridBagHelper gbc;

        JPanel totalItemsPnl = new JPanel();
        gbc = new GuiUtils.GridBagHelper(totalItemsPnl);
        gbc.addLine("Total items", imageResource.readIcon("Preview.Amount"), totalItemsTf);

        JPanel totalPricePnl = new JPanel();
        gbc = new GuiUtils.GridBagHelper(totalPricePnl);
        gbc.addLine("Total price", imageResource.readIcon("Preview.Price"), totalPriceTf);

        JPanel infoPnl = new JPanel();
        infoPnl.setLayout(new BoxLayout(infoPnl, BoxLayout.X_AXIS));
        infoPnl.add(totalItemsPnl);
        infoPnl.add(totalPricePnl);

        JPanel centerPnl = new JPanel(new BorderLayout());
        JPanel southPnl = new JPanel(new BorderLayout());

        centerPnl.add(divisionPanel, BorderLayout.CENTER);
        centerPnl.add(infoPnl, BorderLayout.SOUTH);
        southPnl.add(divisionTb, BorderLayout.WEST);

        setLayout(new BorderLayout());
        add(centerPnl, BorderLayout.CENTER);
        add(southPnl, BorderLayout.NORTH);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length == 0 || args[0] == null) {
            setVisible(false);
            selectedDivision = null;
        } else {
            setVisible(true);
            if (args[0] instanceof Division) {
                selectedDivision = (Division) args[0];
            }
            setDetails(selectedDivision);
            updateEnabledComponents();
        }
    }
}
