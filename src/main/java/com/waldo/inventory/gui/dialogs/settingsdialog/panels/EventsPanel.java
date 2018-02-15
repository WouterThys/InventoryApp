package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.database.DbEvent;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.database.interfaces.DbExecuteListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.tablemodels.IEventTableModel;
import com.waldo.inventory.gui.dialogs.editeventdialog.EditEventDialog;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class EventsPanel extends JPanel implements
        GuiUtils.GuiInterface,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        DbExecuteListener {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IdBToolBar toolBar;
    private IEventTableModel tableModel;
    private ITable<DbEvent> eventTable;

    private ITextField nameTf;
    private ITextField definerTf;
    private ITextField commentTf;
    private ITextField typeTf;
    private ITextArea definitionTa;
    private ITextField createdTf;
    private ITextField alteredTf;
    private ITextField lastExecutedTf;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Window parent;
    private DbEvent selectedEvent;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public EventsPanel(Window parent) {
        this.parent = parent;
        DatabaseAccess.db().addExecuteListener(this);

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        boolean enabled = !(selectedEvent == null);

        toolBar.setDeleteActionEnabled(enabled);
        toolBar.setEditActionEnabled(enabled);
    }

    private void setEventDetails(DbEvent event) {
        if (event != null) {
            nameTf.setText(event.getName());
            definerTf.setText(event.getDefiner());
            commentTf.setText(event.getComment());
            typeTf.setText(event.getType().toString());
            definitionTa.setText(event.getDefinition());
            createdTf.setText(DateUtils.formatDateTime(event.getCreated()));
            alteredTf.setText(DateUtils.formatDateTime(event.getAltered()));
            lastExecutedTf.setText(DateUtils.formatDateTime(event.getLastExecuted()));
        } else {
            nameTf.clearText();
            definerTf.clearText();
            commentTf.clearText();
            typeTf.clearText();
            definitionTa.clearText();
            createdTf.clearText();
            alteredTf.clearText();
            lastExecutedTf.clearText();
        }
    }

    //
    // Table
    //
    private void tableInitialize() {
        List<DbEvent> dbEventList = cache().getDbEvents();

        // sort and stuff

        tableModel.setItemList(dbEventList);
    }

    //
    //
    //
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        GuiUtils.GridBagHelper gbc;

        JPanel panel1 = new JPanel();
        gbc = new GuiUtils.GridBagHelper(panel1);
        gbc.addLine("Name: ", nameTf);
        gbc.addLine("Definer: ", definerTf);
        gbc.addLine("Type: ", typeTf);
        gbc.addLine("Comment: ", commentTf);

        JPanel panel2 = new JPanel();
        gbc = new GuiUtils.GridBagHelper(panel2);
        gbc.addLine("Created: ", createdTf);
        gbc.addLine("Altered: ", alteredTf);
        gbc.addLine("Executed: ", lastExecutedTf);
        gbc.addLine("", new ILabel());

        panel.add(panel1);
        panel.add(panel2);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Tool bar
        toolBar = new IdBToolBar(this);

        // Table
        tableModel = new IEventTableModel();
        eventTable = new ITable<>(tableModel);
        eventTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventTable.getSelectionModel().addListSelectionListener(this);
        eventTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && selectedEvent != null) {
                    int row = eventTable.rowAtPoint(e.getPoint());
                    int col = eventTable.columnAtPoint(e.getPoint());
                    int realCol = eventTable.convertColumnIndexToModel(col);
                    if (row >= 0 && realCol == 0) {
                        boolean enabled = selectedEvent.isEnabled();
                        String sql = DbEvent.sqlEnable(selectedEvent, !enabled);
                        selectedEvent.setEnabled(!enabled);
                        DatabaseAccess.db().execute(sql);
                    }
                }
            }
        });

        // Details
        nameTf = new ITextField(false, 10);
        definerTf = new ITextField(false, 10);
        commentTf = new ITextField(false, 10);
        typeTf = new ITextField(false, 10);
        definitionTa = new ITextArea(false);
        createdTf = new ITextField(false, 10);
        alteredTf = new ITextField(false, 10);
        lastExecutedTf = new ITextField(false, 10);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        JPanel eventsPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(toolBar, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        JScrollPane scrollPane = new JScrollPane(eventTable);

        JPanel detailPanel = createDetailPanel();


        eventsPanel.add(headerPanel, BorderLayout.NORTH);
        eventsPanel.add(scrollPane, BorderLayout.CENTER);
        eventsPanel.add(detailPanel, BorderLayout.SOUTH);

        add(eventsPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        Application.beginWait(EventsPanel.this);
        try {
            tableInitialize();
            if (selectedEvent == null && tableModel.getItemList().size() > 0) {
                eventTable.selectItem(tableModel.getItemList().get(0));
            } else {
                eventTable.selectItem(selectedEvent);
            }
            updateEnabledComponents();
        } finally {
            Application.endWait(EventsPanel.this);
        }
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        if (source.equals(toolBar)) {
            cache().getDbEvents().clear();
            updateComponents();
        }
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        DbEvent newEvent = new DbEvent();
        EditEventDialog eventDialog = new EditEventDialog(parent, "Add event", newEvent);
        if (eventDialog.showDialog() == IDialog.OK) {
            String sql = DbEvent.sqlCreate(newEvent);
            DatabaseAccess.db().execute(sql);
            tableModel.addItem(newEvent);
            eventTable.selectItem(newEvent);
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedEvent != null) {
            int res = JOptionPane.showConfirmDialog(EventsPanel.this,
                    "Are you sure you want to delete " + selectedEvent.getName() + "?",
                    "Delete event",
                    JOptionPane.YES_NO_OPTION);

            if (res == JOptionPane.YES_OPTION) {
                String sql = DbEvent.sqlDelete(selectedEvent);
                if (!sql.isEmpty()) {
                    DatabaseAccess.db().execute(sql);
                    tableModel.removeItem(selectedEvent);
                    selectedEvent = null;
                }
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedEvent != null) {
            EditEventDialog eventDialog = new EditEventDialog(parent, "Edit " + selectedEvent.getName(), selectedEvent);
            if (eventDialog.showDialog() == IDialog.OK) {
                String sql = DbEvent.sqlAlter(selectedEvent);
                DatabaseAccess.db().execute(sql);
                tableModel.updateTable();
            }
        }
    }

    //
    // Table
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Application.beginWait(EventsPanel.this);
            try {
                selectedEvent = eventTable.getSelectedItem();
                setEventDetails(selectedEvent);
                updateEnabledComponents();
            } finally {
                Application.endWait(EventsPanel.this);
            }
        }
    }

    //
    // Command executed
    //
    @Override
    public void onExecuted(String sql) {
        updateComponents();
    }

    @Override
    public void onExecuteError(String sql, Throwable throwable) {
        JOptionPane.showMessageDialog(
                EventsPanel.this,
                "Error executing command: " + throwable,
                "Execute error",
                JOptionPane.ERROR_MESSAGE
        );
        updateComponents();
    }
}