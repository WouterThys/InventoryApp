package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.gui.components.popups.TableOptionsPopup;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ITablePanel<T extends DbObject> extends JPanel implements GuiUtils.GuiInterface {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final IAbstractTableModel<T> tableModel;
    private ITable<T> table;

    // Tool bars
    private ITableToolBar<T> tableToolBar;
    private JToolBar toolBar;
    private boolean dbToolBarAdded;

    // Title
    private boolean titleAdded;
    private boolean subTitleAdded;

    // Panels
    private JPanel centerPanel;
    private JPanel westPanel;
    private JPanel detailsPanel;
    private JPanel headerPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final boolean hasSortOption;
    private Color headerPanelBackground = null;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public ITablePanel(IAbstractTableModel<T> tableModel, ListSelectionListener listSelectionListener) {
        this(tableModel, null, listSelectionListener, false);
    }

    public ITablePanel(IAbstractTableModel<T> tableModel, JPanel detailsPanel, ListSelectionListener listSelectionListener, boolean hasSortOption) {
        super(new BorderLayout());

        this.tableModel = tableModel;
        this.hasSortOption = hasSortOption;
        this.detailsPanel = detailsPanel;

        initializeComponents();
        initializeLayouts();

        if (listSelectionListener != null) {
            addListSelectionListener(listSelectionListener);
        }
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    // Table
    public void addListSelectionListener(ListSelectionListener listSelectionListener) {
        table.getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    public void setListSelectionMode(int selectionMode) {
        table.getSelectionModel().setSelectionMode(selectionMode);
    }

    public void print(String headerName) {
        MessageFormat header = new MessageFormat(headerName);
        MessageFormat footer = new MessageFormat(" - {0} - ");
        try {
            table.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    public void addTableRenderer(TableCellRenderer cellRenderer) {
        HighlightRenderer renderer = new HighlightRenderer(cellRenderer);
        table.setDefaultRenderer(Object.class, renderer);
    }

    public void addColumnCellEditor(int columnIndex, TableCellEditor editor) {
        TableColumn tableColumn = table.getColumnModel().getColumn(columnIndex);
        tableColumn.setCellEditor(editor);
    }

    public void addMouseListener(MouseListener listener) {
        table.addMouseListener(listener);
    }

    public void resizeColumns() {
        if (table != null) {
            table.resizeColumns();
        }
    }

    public String getColumnNameAtPoint(Point point) {
        int col = table.convertColumnIndexToModel(getColumnAtPoint(point));
        return tableModel.getColumnName(col);
    }

    public T getItemAtRow(int row) {
        return table.getValueAtRow(row);
    }

    public T getSelectedItem() {
        return table.getSelectedItem();
    }

    public List<T> getAllSelectedItems() {
        List<T> selectedItems = new ArrayList<>();
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int row : selectedRows) {
                T t = table.getValueAtRow(row);
                if (t != null) {
                    selectedItems.add(t);
                }
            }
        }
        return selectedItems;
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

    public void setExactColumnWidth(int column, int width) {
        table.setExactColumnWidth(column, width);
    }

    // Tool bars
    public void setDbToolBar(IdBToolBar.IdbToolBarListener listener) {
        addToolBar(new IdBToolBar(listener, true, true, true, true));
    }

    public void setDbToolBar(IdBToolBar.IdbToolBarListener listener, boolean hasRefresh, boolean hasAdd, boolean hasDelete, boolean hasEdit) {
        addToolBar(new IdBToolBar(listener, hasRefresh, hasAdd, hasDelete, hasEdit));
    }

    private void addToolBar(JToolBar toolBar) {
        this.toolBar = toolBar;
        if (!dbToolBarAdded) {
            westPanel.add(this.toolBar);
            dbToolBarAdded = true;
        }
    }

    public void setDbToolBarEnabled(boolean enabled) {
        if (toolBar != null) {
            toolBar.setEnabled(enabled);
        }
    }

    public void setDbToolBarEditDeleteEnabled(boolean enabled) {
        if (toolBar != null && toolBar instanceof IdBToolBar) {
            ((IdBToolBar)toolBar).setDeleteActionEnabled(enabled);
            ((IdBToolBar)toolBar).setEditActionEnabled(enabled);
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

    public void addSortOption(Comparator comparator, ImageIcon icon) {
        tableToolBar.addSortComparator(comparator, comparator.toString(), icon);
    }

    public void addTableOptionsListener(TableOptionsPopup.TableOptionsListener tableOptionsListener) {
        tableToolBar.addTableOptionsListener(tableOptionsListener);
    }

    public JPanel getTitlePanel() {
        return centerPanel;
    }

    public void setHeaderPanelBackground(Color background) {
        if (headerPanel != null) {
            if (headerPanelBackground == null) {
                headerPanelBackground = headerPanel.getBackground();
            }

            if (background != null) {
                headerPanel.setBackground(background);
                headerPanel.setOpaque(true);
            } else {
                headerPanel.setBackground(headerPanelBackground);
                headerPanel.setOpaque(false);
            }
        }
    }

    public void setDetailsPanel(JPanel detailsPanel) {
        this.detailsPanel = detailsPanel;
    }

    public void setHeaderPanelVisible(boolean visible) {
        if (headerPanel != null) {
            headerPanel.setVisible(visible);
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        table = new ITable<>(tableModel, false);
        table.setRowSorter(null);
        if (tableModel.hasTableCellRenderer()) {
            addTableRenderer(tableModel.getTableCellRenderer());
        }
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Toolbar
        tableToolBar = new ITableToolBar<>(table, hasSortOption);

        // Panels
        centerPanel = new JPanel(new BorderLayout());
        westPanel = new JPanel(new BorderLayout());
    }

    @Override
    public void initializeLayouts() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        centerPanel.setOpaque(false);
        westPanel.setOpaque(false);

        headerPanel.add(tableToolBar, BorderLayout.EAST);
        headerPanel.add(centerPanel, BorderLayout.CENTER);
        headerPanel.add(westPanel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.PAGE_START);
        add(new JScrollPane(table), BorderLayout.CENTER);
        if (detailsPanel != null) {
            add(detailsPanel, BorderLayout.EAST);
        }
    }

    @Override
    public void updateComponents(Object... args) {

    }


    private class HighlightRenderer extends DefaultTableCellRenderer {

        private static final String HTML = "%s<span style='color:#000000; background-color:#FFFF00'><nobr>%s</nobr></span>";
        private final TableCellRenderer originalRenderer;

        public HighlightRenderer(TableCellRenderer originalRenderer) {
            this.originalRenderer = originalRenderer;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component;
            Class c = table.getColumnClass(column);

            if (c == String.class) {
                String txt = Objects.toString(value, "");
                String pattern = tableToolBar.getFilterText();

                if (!pattern.isEmpty()) {
                    Matcher matcher = Pattern.compile(pattern.toLowerCase()).matcher(txt.toLowerCase());
                    int pos = 0;
                    StringBuilder buf = new StringBuilder("<html>");
                    while (matcher.find(pos)) {
                        int start = matcher.start();
                        int end = matcher.end();
                        buf.append(String.format(HTML, txt.substring(pos, start), txt.substring(start, end)));
                        pos = end;
                    }
                    buf.append(txt.substring(pos));
                    txt = buf.toString();
                    if (originalRenderer == null) {
                        component = super.getTableCellRendererComponent(table, txt, isSelected, hasFocus, row, column);
                    } else {
                        component = originalRenderer.getTableCellRendererComponent(table, txt, isSelected, hasFocus, row, column);
                    }
                } else {
                    if (originalRenderer == null) {
                        component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    } else {
                        component = originalRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    }
                }
            } else {
                if (originalRenderer == null) {
                    component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                } else {
                    component = originalRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            }
            return component;
        }
    }
}