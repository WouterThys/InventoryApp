package com.waldo.inventory.gui.dialogs.editeventdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics.EventIntervalField;
import com.waldo.inventory.classes.database.DbEvent;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.utils.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.sql.Date;

import static com.waldo.inventory.Utils.Statics.EventType;
import static com.waldo.inventory.gui.Application.imageResource;

abstract class EditEventDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField nameTf;
    private ITextField commentTf;

    private IComboBox<EventType> typeCb;
    private ITextField scheduleTf;

    private IQueryPane definitionQp;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    DbEvent selectedEvent;

    private EventType intervalType = EventType.OneTime;
    private int intervalValue;
    private EventIntervalField intervalField = EventIntervalField.Day;
    private Date intervalStartDate = null;
    private Date intervalEndDate = null;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditEventDialogLayout(Window parent, String title, DbEvent selectedEvent) {
        super(parent, title);

        this.selectedEvent = selectedEvent;

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateSchedule() {
        String scheduleTxt = "";
        EventType type = (EventType) typeCb.getSelectedItem();
        if (type != null) {
            switch (type) {
                case OneTime:
                    scheduleTxt = "Once at " + DateUtils.formatDateTime(selectedEvent.getExecuteAt());
                    break;
                case Recurring:
                    scheduleTxt = "Every " + intervalValue + " " + intervalField;
                    break;
            }
        }
        scheduleTf.setText(scheduleTxt);
    }

    private void editSchedule(DbEvent event) {
        EventType type = (EventType) typeCb.getSelectedItem();
        if (event != null && type != null) {
            JPanel panel = null;
            String message = "";
            switch (type) {
                default:
                case OneTime:
                    break;
                case Recurring:
                    panel = createRecurringPanel(intervalValue, intervalField);
                    message = "Set the recurring schedule: ";
                    break;
            }

            if (panel != null) {
                Object[] objects = new Object[] {message, panel};

                int res = JOptionPane.showConfirmDialog(
                        EditEventDialogLayout.this,
                        objects,
                        "Schedule",
                        JOptionPane.OK_CANCEL_OPTION
                );
                if (res == JOptionPane.OK_OPTION) {
                    updateSchedule();
                }
            }
        }
    }

    private JPanel createRecurringPanel(int intValue, EventIntervalField field) {
        JPanel panel = new JPanel();

        ILabel label = new ILabel("Every ");
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        final JSpinner spinner = new JSpinner(spinnerNumberModel);
        if (intValue >= 1) {
            spinner.setValue(intValue);
        }
        spinner.setPreferredSize(new Dimension(80, 25));
        spinner.addChangeListener(e -> {
            intervalValue = ((SpinnerNumberModel) spinner.getModel()).getNumber().intValue();
        });

        final JComboBox<EventIntervalField> comboBox = new JComboBox<>(EventIntervalField.values());
        comboBox.setSelectedItem(field);
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                SwingUtilities.invokeLater(() -> {
                    intervalField = (EventIntervalField) comboBox.getSelectedItem();
                });
            }
        });

        panel.add(label);
        panel.add(spinner);
        panel.add(comboBox);

        return panel;
    }

    void copyValues() {
        if (selectedEvent != null) {
            selectedEvent.setName(nameTf.getText());
            selectedEvent.setComment(commentTf.getText());
            selectedEvent.setType((EventType) typeCb.getSelectedItem());
//            selectedEvent.setExecuteAt();
            selectedEvent.setIntervalValue(intervalValue);
            selectedEvent.setIntervalField(intervalField);
            selectedEvent.setIntervalStarts(intervalStartDate);
            selectedEvent.setIntervalEnds(intervalEndDate);
            selectedEvent.setDefinition(definitionQp.getText());
        }
    }



    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("Settings.Title.Events"));
        setResizable(true);

        // This
        nameTf = new ITextField();
        commentTf = new ITextField();

        typeCb = new IComboBox<>(EventType.values());
        scheduleTf = new ITextField(false);

        definitionQp = new IQueryPane();
        definitionQp.setMargin(new Insets(5,5,5,5));
        definitionQp.setPreferredSize(new Dimension(400,300));
    }

    @Override
    public void initializeLayouts() {
        JPanel dataPanel = new JPanel();
        JPanel westPanel = new JPanel();
        JPanel definitionPanel = new JPanel(new BorderLayout());

        // Data
        JPanel schedulePnl = GuiUtils.createComponentWithActions(scheduleTf, new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSchedule(selectedEvent);
            }
        });

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(dataPanel);
        gbc.addLine("Name: ", nameTf);
        gbc.addLine("Comment: ", commentTf);
        gbc.addLine("Type: ", typeCb);
        gbc.addLine("Schedule: ", schedulePnl);
        westPanel.add(dataPanel);

        // Definition
        JScrollPane scrollPane = new JScrollPane(definitionQp);
        definitionPanel.add(scrollPane, BorderLayout.CENTER);

        // Borders
        dataPanel.setBorder(GuiUtils.createTitleBorder("Data"));
        definitionPanel.setBorder(GuiUtils.createTitleBorder("Query"));

        // Add
        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(westPanel, BorderLayout.WEST);
        getContentPanel().add(definitionPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (selectedEvent != null) {

            intervalType = selectedEvent.getType();
            intervalValue = selectedEvent.getIntervalValue();
            intervalField = selectedEvent.getIntervalField();
            intervalStartDate = selectedEvent.getIntervalStarts();
            intervalEndDate = selectedEvent.getIntervalEnds();

            if (selectedEvent.getName() != null && !selectedEvent.getName().isEmpty()) {
                nameTf.setText(selectedEvent.getName());
                nameTf.setEnabled(false);
            }
            commentTf.setText(selectedEvent.getComment());
            typeCb.setSelectedItem(selectedEvent.getType());
            definitionQp.setText(selectedEvent.getDefinition());
            updateSchedule();
        }
    }
}