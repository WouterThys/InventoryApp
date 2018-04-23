package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.trees.IDivisionCheckBoxTree;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class DivisionFilterPanel extends IPanel {

    private ILabel nameLbl;
    private ILabel statusLbl;
    private IDivisionCheckBoxTree checkBoxTree;
    private IActions.DeleteAction deleteSelectionAction;

    private String name;

    public DivisionFilterPanel(String name) {
        super(new BorderLayout());
        this.name = name;
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    private void updateEnabledComponents() {
        deleteSelectionAction.setEnabled(checkBoxTree.getCheckedPaths().length > 0);
    }

    public void clearSelection() {
        checkBoxTree.uncheckAll();
        updateComponents();
    }

    public List<Division> getSelected() {
        return checkBoxTree.getCheckedDivisions();
    }

    public void setSelected(Division selected) {
       checkBoxTree.setSelectedItem(selected);
    }

    @Override
    public void initializeComponents() {

        nameLbl = new ILabel(name);
        nameLbl.setFont(Font.BOLD);
        statusLbl = new ILabel();

        List<Division> rootDivisions = SearchManager.sm().findDivisionsWithoutParent();
        rootDivisions.sort(new ComparatorUtils.DbObjectNameComparator<>());
        Division rootDivision = Division.createDummyDivision("Dummy", rootDivisions);
        checkBoxTree = new IDivisionCheckBoxTree(rootDivision);
        //            TreePath[] paths = checkBoxTree.getCheckedPaths();
//            for (TreePath tp : paths) {
//                for (Object pathPart : tp.getPath()) {
//                    System.out.print(pathPart + ",");
//                }
//                System.out.println();
//            }
        checkBoxTree.addCheckChangeEventListener(this::updateComponents);

        deleteSelectionAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearSelection();
            }
        };
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(checkBoxTree);
        //scrollPane.setPreferredSize(new Dimension(80, 100));
        scrollPane.setMinimumSize(new Dimension(60, 100));

        panel.add(nameLbl, BorderLayout.PAGE_START);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(GuiUtils.createComponentWithActions(statusLbl, deleteSelectionAction), BorderLayout.PAGE_END);

        panel.setBorder(BorderFactory.createEmptyBorder(2,5, 5, 5));

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... objects) {
        statusLbl.setText(checkBoxTree.getCheckedPaths().length + " selected");
        updateEnabledComponents();
    }
}
