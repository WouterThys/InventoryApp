package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.database.DbEvent;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.database.interfaces.DbExecuteListener;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.IEventTableModel;
import com.waldo.inventory.gui.dialogs.editeventdialog.EditEventDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class EventsPanel extends JPanel implements
        GuiInterface,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        DbExecuteListener {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IdBToolBar toolBar;
    private ILabel currentEventLbl;
    private IToggleButton enableBtn;

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
    private IDialog parent;
    private DbEvent selectedEvent;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public EventsPanel(IDialog parent) {
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
        enableBtn.setEnabled(enabled);
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
            enableBtn.setSelected(event.isEnabled());
        } else {
            nameTf.clearText();
            definerTf.clearText();
            commentTf.clearText();
            typeTf.clearText();
            definitionTa.clearText();
            createdTf.clearText();
            alteredTf.clearText();
            lastExecutedTf.clearText();
            enableBtn.setSelected(false);
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
        // Lbl
        currentEventLbl = new ILabel();
        currentEventLbl.setAlignmentX(CENTER_ALIGNMENT);
        currentEventLbl.setForeground(Color.gray);
        Font f = currentEventLbl.getFont();
        Font newFont = new Font(f.getName(), Font.BOLD, f.getSize() + 3);
        currentEventLbl.setFont(newFont);

        // Tool bar
        toolBar = new IdBToolBar(this);

        // Enable
        enableBtn = new IToggleButton("On", "Off");
        enableBtn.addActionListener(e -> {
            if (selectedEvent != null && !parent.isUpdating()) {
                parent.beginWait();
                try {
                    String sql = DbEvent.sqlEnable(selectedEvent, enableBtn.isSelected());
                    DatabaseAccess.db().execute(sql);
                    selectedEvent.setEnabled(enableBtn.isSelected());
                } finally {
                    parent.endWait();
                }
            }
        });

        // Table
        tableModel = new IEventTableModel();
        eventTable = new ITable<>(tableModel);
        eventTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventTable.getSelectionModel().addListSelectionListener(this);

        // Details
        nameTf = new ITextField(false);
        definerTf = new ITextField(false);
        commentTf = new ITextField(false);
        typeTf = new ITextField(false);
        definitionTa = new ITextArea(false);
        createdTf = new ITextField(false);
        alteredTf = new ITextField(false);
        lastExecutedTf = new ITextField(false);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        JPanel eventsPanel = new JPanel(new BorderLayout());

        JPanel currentPanel = new JPanel(new BorderLayout());
        currentPanel.add(new ILabel("Current event: "), BorderLayout.NORTH);
        currentPanel.add(currentEventLbl, BorderLayout.CENTER);
        currentPanel.setBorder(BorderFactory.createEmptyBorder(2,15,2,15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(enableBtn, BorderLayout.WEST);
        headerPanel.add(currentPanel, BorderLayout.CENTER);
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
        parent.beginWait();
        try {
            tableInitialize();
            if (selectedEvent == null && tableModel.getItemList().size() > 0) {
                eventTable.selectItem(tableModel.getItemList().get(0));
            } else {
                eventTable.selectItem(selectedEvent);
            }
            updateEnabledComponents();
        } finally {
            parent.endWait();
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
            parent.beginWait();
            try {
                selectedEvent = eventTable.getSelectedItem();
                setEventDetails(selectedEvent);
                if (selectedEvent != null) {
                    currentEventLbl.setText(selectedEvent.getName());
                } else {
                    currentEventLbl.setText("");
                }
                updateEnabledComponents();
            } finally {
                parent.endWait();
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