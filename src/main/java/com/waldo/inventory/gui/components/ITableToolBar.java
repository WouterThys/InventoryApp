package com.waldo.inventory.gui.components;

import com.waldo.inventory.gui.GuiInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.util.regex.PatternSyntaxException;

import static com.waldo.inventory.gui.Application.imageResource;

public class ITableToolBar extends JToolBar implements GuiInterface {

    private ILabel sortLbl;
    private JComboBox<String> sortCb;

    private AbstractAction filterAa;
    private ITextField filterTf;
    private boolean hasFilter;

    private ITable table;

    public ITableToolBar(ITable table) {
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

    @Override
    public void initializeComponents() {
        // JToolBar
        setOpaque(false);
        setFloatable(false);
        setBorder(new EmptyBorder(5,5,5,5));

        // Sort
        sortLbl = new ILabel(imageResource.readImage("Toolbar.Table.ApplySort"));
        sortCb = new JComboBox<>(new String[] {"Filter one", "Filter other"});

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
