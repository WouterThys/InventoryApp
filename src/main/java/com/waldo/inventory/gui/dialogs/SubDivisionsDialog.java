package com.waldo.inventory.gui.dialogs;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.classes.Type;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialogPanel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledEditPanel;
import com.waldo.inventory.gui.components.ITitledPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.net.URL;

public class SubDivisionsDialog extends IDialogPanel {

    private static JDialog dialog;
    private static Application application;
    private ResourceManager resourceManager;

    public static void showDialog(Application parent) {
        SubDivisionsDialog.application = parent;
        dialog = new JDialog(parent, "Sub Divisions", true);
        dialog.getContentPane().add(new SubDivisionsDialog());
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
    }

    private JList<String> subDivisionList;
    private DefaultListModel<DbObject> detailListModel;
    private JList<DbObject> detailList;
    private JToolBar toolBar;
    private ITextField searchField;
    private ITitledPanel detailsPanel;

    private Action addAction;
    private Action deleteAction;
    private Action editAction;

    private SubDivisionsDialog() {
        super();

        URL url = Error.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());

        initActions();
        initComponents();
        initLayouts();
    }

    private void initActions() {
        addAction = new AbstractAction("Add", resourceManager.readImage("SubDivisionDialog.AddIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        deleteAction = new AbstractAction("Delete", resourceManager.readImage("SubDivisionDialog.DeleteIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        editAction = new AbstractAction("Edit", resourceManager.readImage("SubDivisionDialog.EditIcon")) {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
    }

    private void initComponents() {

        // Sub divisions list
        String[] subDivisions = new String[] {"Categories", "Products", "Types"};
        subDivisionList = new JList<>(subDivisions);
        subDivisionList.setFixedCellWidth(200);
        subDivisionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    JList list = (JList)e.getSource();
                    detailsPanel.setTitle((String) list.getSelectedValue());
                    int ndx = list.getSelectedIndex();
                    switch (ndx) {
                        case 0: // Categories
                            detailListModel.removeAllElements();
                            for(Category c : application.getCategoryList()) {
                                detailListModel.addElement(c);
                            }
                            break;

                        case 1: // Products
                            detailListModel.removeAllElements();
                            for(Product p : application.getProductList()) {
                                detailListModel.addElement(p);
                            }
                            break;

                        case 2: // Types
                            detailListModel.removeAllElements();
                            for(Type t : application.getTypeList()) {
                                detailListModel.addElement(t);
                            }
                            break;
                    }
                }
            }
        });

        // Search field
        searchField = new ITextField("Search");



        // Toolbar
        toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.add(addAction);
        toolBar.add(deleteAction);
        toolBar.add(editAction);

        // Details
        detailListModel = new DefaultListModel<>();
        detailList = new JList<>(detailListModel);
        detailsPanel = new ITitledPanel("Categories",
                new JComponent[] {new JScrollPane(detailList), toolBar},
                ITitledPanel.HORIZONTAL);

        subDivisionList.setSelectedIndex(0);

    }

    private void initLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.X_AXIS));

        getContentPanel().add(new ITitledPanel("Sub divisions",
                new JComponent[] {searchField, new JScrollPane(subDivisionList)}
        ));

        getContentPanel().add(detailsPanel);
    }
}
