package com.waldo.inventory.gui.dialogs.projectidesdialog.parserdialog;

import com.waldo.inventory.Utils.parser.PcbItemParser;
import com.waldo.inventory.Utils.parser.PcbParser;
import com.waldo.inventory.classes.ParserItemLink;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.IParserItemLinkTableModel;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ParserDialogLayout extends IDialog implements
        ActionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ItemListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ICheckBox useParserCb;
    IComboBox<PcbParser> parserCb;

    private IParserItemLinkTableModel tableModel;
    private ITable<ParserItemLink> parserItemLinkTable;

    private IdBToolBar tableToolBar;


    /*
    *                  VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private boolean useParser;
    PcbParser selectedParser;
    ParserItemLink selectedLink;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ParserDialogLayout(Application application, String title, boolean useParser, PcbParser selectedParser) {
        super(application, title);

        this.useParser = useParser;
        this.selectedParser = selectedParser;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean hasParser = selectedParser != null && useParserCb.isSelected();
        boolean hasLink = (hasParser) && (selectedLink != null);
        parserItemLinkTable.setEnabled(hasParser);

        if (hasParser) {
            tableToolBar.setEnabled(true);
            tableToolBar.setEditActionEnabled(hasLink);
            tableToolBar.setDeleteActionEnabled(hasLink);
        } else {
            tableToolBar.setEnabled(false);
        }
    }


    void tableInitialize(PcbParser parser) {
        if (parser != null) {
            tableModel.setItemList(SearchManager.sm().findParserItemLinksByParserName(parser.getName()));
        } else {
            tableModel.clearItemList();
        }
    }

    void tableUpdate() {
        tableModel.updateTable();
    }

    void tableAdd(ParserItemLink link) {
        List<ParserItemLink> links = new ArrayList<>();
        links.add(link);
        tableModel.addItems(links);
    }

    void tableDelete(ParserItemLink link) {
        List<ParserItemLink> links = new ArrayList<>();
        links.add(link);
        tableModel.removeItems(links);
    }

    void tableDelete(List<ParserItemLink> links) {
        tableModel.removeItems(links);
    }

    void tableSelect(ParserItemLink link) {
        parserItemLinkTable.selectItem(link);
    }

    ParserItemLink tableGetSelected() {
        int row = parserItemLinkTable.getSelectedRow();
        return (ParserItemLink) parserItemLinkTable.getValueAtRow(row);
    }

    List<ParserItemLink> tableGetSelectedList() {
        java.util.List<ParserItemLink> setItems = new ArrayList<>();
        int[] selectedRows = parserItemLinkTable.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int row : selectedRows) {
                ParserItemLink si = (ParserItemLink) parserItemLinkTable.getValueAtRow(row);
                if (si != null) {
                    setItems.add(si);
                }
            }
        }
        return setItems;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        showTitlePanel(false);
        setResizable(true);

        // Stuff
        useParserCb = new ICheckBox("Use parser", false);
        useParserCb.addActionListener(this);

        java.util.List<PcbParser> parsers = PcbItemParser.getInstance().getPcbParsers();
        parserCb = new IComboBox<>(parsers, new ParserComparator(), false);
        parserCb.addItemListener(this);

        tableModel = new IParserItemLinkTableModel();
        parserItemLinkTable = new ITable<>(tableModel);
        parserItemLinkTable.getSelectionModel().addListSelectionListener(this);
        tableToolBar = new IdBToolBar(this, IdBToolBar.VERTICAL);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel tablePanel = new JPanel(new BorderLayout());
        JScrollPane pane = new JScrollPane(parserItemLinkTable);

        tablePanel.add(pane, BorderLayout.CENTER);
        tablePanel.add(tableToolBar, BorderLayout.EAST);

        centerPanel.add(parserCb, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        getContentPanel().add(useParserCb, BorderLayout.NORTH);
        getContentPanel().add(centerPanel, BorderLayout.CENTER);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        parserCb.setSelectedItem(selectedParser);
        parserCb.setEnabled(useParser);
        useParserCb.setSelected(useParser);

        tableInitialize(selectedParser);
        updateEnabledComponents();
    }

    private class ParserComparator implements Comparator<PcbParser> {

        @Override
        public int compare(PcbParser p1, PcbParser p2) {
            return p1.getName().compareTo(p2.getName());
        }
    }
}