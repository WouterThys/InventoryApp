package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.gui.components.ICheckBoxList;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ICheckBox;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class FilterPanel<T> extends IPanel {

    private ILabel nameLbl;
    private ILabel statusLbl;
    private ICheckBoxList checkBoxList;
    private IActions.DeleteAction deleteSelectionAction;

    private String name;
    private List<T> listData;
    private List<T> selectedList = new ArrayList<>();

    public FilterPanel(String name, List<T> listData) {
        super(new BorderLayout());
        this.name = name;
        this.listData = listData;
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    private void updateEnabledComponents() {
        deleteSelectionAction.setEnabled(selectedList.size() > 0);
    }

    private void objectSelected(boolean selected, T object) {
        if (selected) {
            if (!selectedList.contains(object)) {
                selectedList.add(object);
            }
        } else {
            if (selectedList.contains(object)) {
                selectedList.remove(object);
            }
        }
        updateComponents();
    }

    public void clearSelection() {
        for(int i = 0; i< checkBoxList.getModel().getSize();i++){
            JCheckBox box = checkBoxList.getModel().getElementAt(i);
            if (box.isSelected()) {
                box.setSelected(false);
            }
        }
        checkBoxList.revalidate();
        checkBoxList.repaint();
    }

    public List<T> getSelected() {
        return selectedList;
    }

    @Override
    public void initializeComponents() {

        nameLbl = new ILabel(name);
        nameLbl.setFont(Font.BOLD);
        statusLbl = new ILabel();

        DefaultListModel<JCheckBox> listModel = new DefaultListModel<>();
        for (T t : listData) {
            listModel.addElement(new FilterCheckbox<>(t));
        }
        checkBoxList = new ICheckBoxList(listModel);

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

        JScrollPane scrollPane = new JScrollPane(checkBoxList);
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
        statusLbl.setText(selectedList.size() + " selected");
        updateEnabledComponents();


    }

    private class FilterCheckbox<F extends T> extends ICheckBox {

        FilterCheckbox(F object) {
            super(object.toString());
            addItemListener(e -> objectSelected(isSelected(), object));
        }
    }
}
