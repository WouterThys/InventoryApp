package com.waldo.inventory.gui.dialogs.logsdialog;


import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Log;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;

import static com.waldo.inventory.gui.Application.imageResource;

public class LogsDetailPanel extends JPanel implements GuiInterface {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

   private Log selectedLog;

   private ILabel iconLabel;
   private ILabel typeLabel;
   private ILabel timeLabel;
   private ILabel classLabel;

   private ITextArea messageTa;
   private ITextArea exceptionTa;

   private JPanel messagePanel;
   private JPanel exceptionPanel;

    LogsDetailPanel(Application application) {
        initializeComponents();
        initializeLayouts();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(iconLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(typeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(classLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(timeLabel, gbc);

        headerPanel.setBorder(BorderFactory.createEmptyBorder(5,10,2,10));
        return headerPanel;
    }

    private JPanel createInfoPanel() {
        JPanel msqPanel = new JPanel(new BorderLayout());

        JScrollPane msqPane = new JScrollPane(messageTa);
        JScrollPane exPane = new JScrollPane(exceptionTa);

       messagePanel.add(new ILabel("Message: ", ILabel.LEFT), BorderLayout.BEFORE_FIRST_LINE);
       messagePanel.add(msqPane, BorderLayout.CENTER);

       exceptionPanel.add(new ILabel("Exception: ", ILabel.LEFT), BorderLayout.BEFORE_FIRST_LINE);
       exceptionPanel.add(exPane, BorderLayout.CENTER);

       msqPanel.add(messagePanel, BorderLayout.NORTH);
       msqPanel.add(exceptionPanel, BorderLayout.CENTER);

        msqPanel.setBorder(BorderFactory.createEmptyBorder(2,10,5,10));
        return msqPanel;
    }

    @Override
    public void initializeComponents() {
        iconLabel = new ILabel("", ILabel.LEFT);
        typeLabel = new ILabel("", ILabel.LEFT);
        timeLabel = new ILabel("", ILabel.LEFT);
        classLabel = new ILabel("", ILabel.LEFT);

        messageTa = new ITextArea("Message", 3, 20);
        messageTa.setEditable(false);
        messageTa.setLineWrap(true); // Go to next line when area is full
        messageTa.setWrapStyleWord(true); // Don't cut words in two
        exceptionTa = new ITextArea("Exception", 20, 20);
        exceptionTa.setEditable(false);
        exceptionTa.setForeground(Color.red);
        exceptionTa.setLineWrap(true); // Go to next line when area is full
        exceptionTa.setWrapStyleWord(true); // Don't cut words in two
        exceptionTa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JOptionPane.showMessageDialog(
                            LogsDetailPanel.this,
                            selectedLog.getLogException(),
                            "Exception",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        messagePanel = new JPanel(new BorderLayout());
        exceptionPanel = new JPanel(new BorderLayout());
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createInfoPanel(), BorderLayout.CENTER);

    }

    @Override
    public void updateComponents(Object object) {
        if (object == null) {
            setVisible(false);
            selectedLog = null;
        } else {
            if (object instanceof Log) {
                selectedLog = (Log) object;

                switch (selectedLog.getLogType()) {
                    case Statics.LogTypes.INFO:
                        iconLabel.setIcon(imageResource.readImage("Log.InfoL"));
                        typeLabel.setText("Info");
                        break;
                    case Statics.LogTypes.DEBUG:
                        iconLabel.setIcon(imageResource.readImage("Log.DebugL"));
                        typeLabel.setText("Debug");
                        break;
                    case Statics.LogTypes.WARN:
                        iconLabel.setIcon(imageResource.readImage("Log.WarnL"));
                        typeLabel.setText("Warning");
                        break;
                    case Statics.LogTypes.ERROR:
                        iconLabel.setIcon(imageResource.readImage("Log.ErrorL"));
                        typeLabel.setText("Error");
                        break;
                }

                timeLabel.setText(sdf.format(selectedLog.getLogTime()));
                classLabel.setText(selectedLog.getLogClass());

                messageTa.setText(selectedLog.getLogMessage());

                if (!selectedLog.getLogException().isEmpty()) {
                    exceptionTa.setText(selectedLog.getLogException());
                    exceptionPanel.setVisible(true);
                } else {
                    exceptionPanel.setVisible(false);
                }

                setVisible(true);
            }
        }
    }
}