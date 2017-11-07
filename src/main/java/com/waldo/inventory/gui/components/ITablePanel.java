package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.tablemodels.IAbstractTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ITablePanel<T extends DbObject> extends JPanel implements GuiInterface {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IAbstractTableModel<T> tableModel;
    private ITable<T> table;

    // Tool bars
    private ITableToolBar tableToolBar;
    private IdBToolBar dBToolBar;
    private boolean dbToolBarAdded;

    // Title
    private boolean titleAdded;
    private boolean subTitleAdded;

    // Panels
    private JPanel centerPanel;
    private JPanel westPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ITablePanel(IAbstractTableModel<T> tableModel) {
        this(tableModel, null, null);
    }

    public ITablePanel(IAbstractTableModel<T> tableModel, ListSelectionListener listSelectionListener) {
        this(tableModel, listSelectionListener, null);
    }

    public ITablePanel(IAbstractTableModel<T> tableModel, ListSelectionListener listSelectionListener, TableCellRenderer tableCellRenderer) {
        super(new BorderLayout());

        this.tableModel = tableModel;

        initializeComponents();
        initializeLayouts();

        if (listSelectionListener != null) {
            addListSelectionListener(listSelectionListener);
        }
        addTableRenderer(tableCellRenderer);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    // Table
    public void addListSelectionListener(ListSelectionListener listSelectionListener) {
        table.getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    public void addTableRenderer(TableCellRenderer cellRenderer) {
        HighlightRenderer renderer = new HighlightRenderer(cellRenderer);
        table.setDefaultRenderer(Object.class, renderer);
    }

    public T getItemAtRow(int row) {
        return table.getValueAtRow(row);
    }

    public T getSelectedItem() {
        return table.getSelectedItem();
    }

    public void selectItem(T item) {
        table.selectItem(item);
    }

    public int getRowAtPoint(Point point) {
        return table.rowAtPoint(point);
    }

    public int getColumnAtPoint(Point point) {
        return table.columnAtPoint(point);
    }

    // Tool bars
    public void setDbToolBar(IdBToolBar.IdbToolBarListener listener) {
        addDbToolBar(new IdBToolBar(listener, IdBToolBar.HORIZONTAL));
    }

    public void addDbToolBar(IdBToolBar dBToolBar) {
        this.dBToolBar = dBToolBar;
        if (!dbToolBarAdded) {
            westPanel.add(dBToolBar);
            dbToolBarAdded = true;
        }
    }

    public void setDbToolBarEnabled(boolean enabled) {
        if (dBToolBar != null) {
            dBToolBar.setEnabled(enabled);
        }
    }

    public void setDbToolBarEditDeleteEnabled(boolean enabled) {
        if (dBToolBar != null) {
            dBToolBar.setDeleteActionEnabled(enabled);
            dBToolBar.setEditActionEnabled(enabled);
        }
    }

    // Titles
    public void setTableTitle(String title) {
        ILabel lbl = new ILabel(title, ILabel.CENTER);
        lbl.setFont(20, Font.BOLD);
        addTableTitle(lbl);
    }

    public void addTableTitle(ILabel titleLbl) {
        if (!titleAdded) {
            centerPanel.add(titleLbl, BorderLayout.CENTER);
            titleAdded = true;
        }
    }

    public void setTableSubTitle(String subTitle) {
        addTableSubTitle(new ILabel(subTitle));
    }

    public void addTableSubTitle(ILabel subTitleLbl) {
        if (!subTitleAdded) {
            centerPanel.add(subTitleLbl);
            subTitleAdded = true;
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        table = new ITable<>(tableModel);

        // Toolbar
        tableToolBar = new ITableToolBar(table);

        // Panels
        centerPanel = new JPanel(new BorderLayout());
        westPanel = new JPanel(new BorderLayout());
    }

    @Override
    public void initializeLayouts() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        headerPanel.add(tableToolBar, BorderLayout.EAST);
        headerPanel.add(centerPanel, BorderLayout.CENTER);
        headerPanel.add(westPanel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {

    }


    private class HighlightRenderer extends DefaultTableCellRenderer {

        private static final String HTML = "%s<span style='color:#000000; background-color:#FFFF00'>%s</span>";
        private TableCellRenderer originalRenderer;

        public HighlightRenderer(TableCellRenderer originalRenderer) {
            this.originalRenderer = originalRenderer;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String txt = Objects.toString(value, "").toLowerCase();
            String pattern = tableToolBar.getFilterText().toLowerCase();

            if (!pattern.isEmpty()) {
                Matcher matcher = Pattern.compile(pattern).matcher(txt);
                int pos = 0;
                StringBuilder buf = new StringBuilder("<html>");
                while (matcher.find(pos)) {
                    int start = matcher.start();
                    int end   = matcher.end();
                    buf.append(String.format(HTML, txt.substring(pos, start), txt.substring(start, end)));
                    pos = end;
                }
                buf.append(txt.substring(pos));
                txt = buf.toString();
            }
            if (originalRenderer == null) {
                super.getTableCellRendererComponent(table, txt, isSelected, hasFocus, row, column);
            } else {
                originalRenderer.getTableCellRendererComponent(table, txt, isSelected, hasFocus, row, column);
            }
            return this;
        }
    }
}