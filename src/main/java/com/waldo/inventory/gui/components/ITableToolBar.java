package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.tablemodels.IAbstractTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Comparator;
import java.util.regex.PatternSyntaxException;

import static com.waldo.inventory.gui.Application.imageResource;

public class ITableToolBar<T extends DbObject> extends JToolBar implements GuiInterface {

    private ILabel sortLbl;

    private DefaultComboBoxModel<Comparator> sortCbModel;
    private JComboBox<Comparator> sortCb;

    private AbstractAction filterAa;
    private ITextField filterTf;
    private boolean hasFilter;

    private ITable<T> table;

    public ITableToolBar(ITable<T> table) {
        super(JToolBar.HORIZONTAL);

        this.table = table;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    private void applyTableFilter(ITable table) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        RowFilter<TableModel, Object> filter;
        try {
            filter = RowFilter.regexFilter("(?i)"+filterTf.getText());
        } catch (PatternSyntaxException exc) {
            return;
        }
        sorter.setRowFilter(filter);
    }

    public String getFilterText() {
        return  filterTf.getText();
    }

    public void addSortComparator(Comparator comparable) {
        sortCbModel.addElement(comparable);
    }

    @Override
    public void initializeComponents() {
        // JToolBar
        setOpaque(false);
        setFloatable(false);
        setBorder(new EmptyBorder(5,5,5,5));

        // Sort
        sortLbl = new ILabel(imageResource.readImage("Toolbar.Table.ApplySort"));
        sortCbModel = new DefaultComboBoxModel<>();
        sortCb = new JComboBox<>(sortCbModel);
        sortCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (table.getModel() instanceof IAbstractTableModel) {
                    IAbstractTableModel tm = (IAbstractTableModel) table.getModel();
                    tm.setSortOrder((Comparator) sortCb.getSelectedItem());
                    tm.sort();
                }
            }
        });

        // Filter
        filterAa = new AbstractAction("Filter", imageResource.readImage("Toolbar.Table.ApplyFilter")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterAa.putValue(AbstractAction.SMALL_ICON, imageResource.readImage("Toolbar.Table.ApplyFilter"));
                filterTf.setText("");
                hasFilter = false;
            }
        };
        filterTf = new ITextField();
        filterTf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (table != null) {
                    applyTableFilter(table);
                    if (!hasFilter) {
                        filterAa.putValue(AbstractAction.SMALL_ICON, imageResource.readImage("Toolbar.Table.RemoveFilter"));
                        hasFilter = true;
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (table != null) {
                    applyTableFilter(table);
                    if (!hasFilter) {
                        filterAa.putValue(AbstractAction.SMALL_ICON, imageResource.readImage("Toolbar.Table.RemoveFilter"));
                        hasFilter = true;
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (table != null) {
                    applyTableFilter(table);
                    if (!hasFilter) {
                        filterAa.putValue(AbstractAction.SMALL_ICON, imageResource.readImage("Toolbar.Table.RemoveFilter"));
                        hasFilter = true;
                    }
                }
            }
        });
    }

    @Override
    public void initializeLayouts() {
        add(filterTf);
        add(filterAa);
        addSeparator();
        add(sortLbl);
        add(sortCb);
    }

    @Override
    public void updateComponents(Object... args) {

    }
}
