package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.popups.TableOptionsPopup;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ITable;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.regex.PatternSyntaxException;

import static com.waldo.inventory.gui.Application.imageResource;

public class ITableToolBar<T extends DbObject> extends JToolBar implements GuiUtils.GuiInterface {

    private Comparator sortOrder;
    private IActions.TableOptionsAction tableOptionsAction;
    private TableOptionsPopup tableOptionsPopup;

    private AbstractAction filterAa;
    private ITextField filterTf;
    private boolean hasFilter;
    private final boolean hasSortOption;

    private final ITable<T> table;

    public ITableToolBar(ITable<T> table, boolean hasSortOption) {
        super(JToolBar.HORIZONTAL);

        this.table = table;
        this.hasSortOption = hasSortOption;

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

    String getFilterText() {
        return  filterTf.getText();
    }

    void addSortComparator(Comparator comparable, String name, ImageIcon imageIcon) {
        tableOptionsPopup.addSortOption(comparable, name, imageIcon);
    }

    void addTableOptionsListener(TableOptionsPopup.TableOptionsListener tableOptionsListener) {
        tableOptionsPopup.addTableOptionsListener(tableOptionsListener);
    }

    @Override
    public void initializeComponents() {
        // JToolBar
        setOpaque(false);
        setFloatable(false);
        setBorder(new EmptyBorder(5,5,5,5));

        tableOptionsAction = new IActions.TableOptionsAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableOptionsPopup.show(ITableToolBar.this, 0,0);
            }
        };
        tableOptionsPopup = new TableOptionsPopup() {
            @Override
            public void onSortBy(Comparator comparator) {
                if (table.getModel() instanceof IAbstractTableModel) {
                    IAbstractTableModel tm = (IAbstractTableModel) table.getModel();
                    sortOrder = comparator;
                    tm.setSortOrder(comparator);
                    tm.sort();
                }
            }
        };

        // Filter
        filterAa = new AbstractAction("Filter", imageResource.readIcon("Filter.S")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterAa.putValue(AbstractAction.SMALL_ICON, imageResource.readIcon("Filter.S"));
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
                        filterAa.putValue(AbstractAction.SMALL_ICON, imageResource.readIcon("Filter.Remove.S"));
                        hasFilter = true;
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (table != null) {
                    applyTableFilter(table);
                    if (!hasFilter) {
                        filterAa.putValue(AbstractAction.SMALL_ICON, imageResource.readIcon("Filter.Remove.S"));
                        hasFilter = true;
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (table != null) {
                    applyTableFilter(table);
                    if (!hasFilter) {
                        filterAa.putValue(AbstractAction.SMALL_ICON, imageResource.readIcon("Filter.Remove.S"));
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
        if (hasSortOption) {
            addSeparator();
            add(tableOptionsAction);
        }
    }

    @Override
    public void updateComponents(Object... args) {

    }
}
