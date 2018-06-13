package com.waldo.inventory.gui.dialogs.messageprogressdialog;

import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.utils.icomponents.ITextPane;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

abstract class MessageProgressDialogLayout extends iDialog {

    private final Color textColor = Color.DARK_GRAY;
    private final Color infoColor = Color.BLUE;
    private final Color warnColor = Color.ORANGE;
    private final Color errorColor = Color.RED;

    private final ImageIcon infoIcon = imageResource.readIcon("");
    private final ImageIcon warnIcon = ImageResource.scaleImage(imageResource.readIcon("Orders.Table.Warning"), new Dimension(10,10));
    private final ImageIcon errorIcon = ImageResource.scaleImage(imageResource.readIcon("Orders.Table.Error"), new Dimension(10,10));

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel progressBarPanel;
    private JProgressBar mainProgressBar;
    private JProgressBar subProgressBar;

    private ITextPane messagePane;
    private Style style;
    private StyledDocument messageDoc;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private boolean showIcons = true;
    private int mainProgressMax = 10;
    private int subProgressMax = 10;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    MessageProgressDialogLayout(Window window) {
        super(window, "Progress");

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void appendHeader(String header) {
        StyleConstants.setForeground(style, textColor);
        StyleConstants.setBold(style, true);
        try {
            if (!header.isEmpty() && !header.endsWith("\n")) {
                header += "\n";
            }
            if(!header.isEmpty() && !header.startsWith(" ")) {
                header = " " + header;
            }
            if (showIcons) messageDoc.insertString(messageDoc.getLength(), header, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void appendText(String text) {
        StyleConstants.setForeground(style, textColor);
        StyleConstants.setBold(style, false);
        try {
            if (!text.isEmpty() && !text.endsWith("\n")) {
                text += "\n";
            }
            if(!text.isEmpty() && !text.startsWith(" ")) {
                text = " " + text;
            }
            if (showIcons) messageDoc.insertString(messageDoc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void appendInfo(String info) {
        StyleConstants.setForeground(style, infoColor);
        StyleConstants.setBold(style, false);
        try {
            if (!info.isEmpty() && !info.endsWith("\n")) {
                info += "\n";
            }
            if(!info.isEmpty() && !info.startsWith(" ")) {
                info = " " + info;
            }
            if (showIcons) messageDoc.insertString(messageDoc.getLength(), info, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void appendWarning(String warning) {
        StyleConstants.setForeground(style, warnColor);
        StyleConstants.setBold(style, false);
        try {
            if (!warning.isEmpty() && !warning.endsWith("\n")) {
                warning += "\n";
            }
            if(!warning.isEmpty() && !warning.startsWith(" ")) {
                warning = " " + warning;
            }
            if (showIcons) messagePane.insertIcon(warnIcon);
            messageDoc.insertString(messageDoc.getLength(), warning, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void appendError(String error) {
        StyleConstants.setForeground(style, errorColor);
        StyleConstants.setBold(style, false);
        try {
            if (!error.isEmpty() && !error.endsWith("\n")) {
                error += "\n";
            }
            if(!error.isEmpty() && !error.startsWith(" ")) {
                error = " " + error;
            }
            if (showIcons) messagePane.insertIcon(errorIcon);
            messageDoc.insertString(messageDoc.getLength(), error, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void initMainProgress(int min, int max) {
        mainProgressMax = max;
        mainProgressBar.setMinimum(min);
        mainProgressBar.setMaximum(max);
    }

    public void setMainProgress(int progress) {
        mainProgressBar.setValue(progress);
        mainProgressBar.setString(progress + " / " + mainProgressMax);
    }

    public void initSubProgress(int min, int max) {
        subProgressMax = max;
        subProgressBar.setMinimum(min);
        subProgressBar.setMaximum(max);
    }

    public void setSubProgress(int progress) {
        subProgressBar.setValue(progress);
        subProgressBar.setString(progress + " / " + subProgressMax);
    }

    public void setDone(boolean closeDialog) {
        getButtonOK().setEnabled(true);
        if (closeDialog) {
            onOK();
        }
    }

    public void showIcons(boolean showIcons) {
        this.showIcons = showIcons;
    }

    public void showProgressBars(boolean showProgressBars) {
        progressBarPanel.setVisible(showProgressBars);
    }

    public void showSubProgressBar(boolean show) {
        subProgressBar.setVisible(show);
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        setModal(false);
        getButtonCancel().setVisible(false);
        getButtonOK().setEnabled(false);

        //UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        //defaults.put("nimbusOrange", defaults.get("nimbusFocus"));

        mainProgressBar = new JProgressBar();
        mainProgressBar.setStringPainted(true);
        mainProgressBar.setPreferredSize(new Dimension(500, 25));

        subProgressBar = new JProgressBar();
        subProgressBar.setStringPainted(true);
        subProgressBar.setPreferredSize(new Dimension(450, 20));

        progressBarPanel = new JPanel(new BorderLayout());

        messagePane = new ITextPane();
        messagePane.setEditable(false);
        style = messagePane.addStyle("MessageStyle", messagePane.getLogicalStyle());
        messageDoc = messagePane.getStyledDocument();
    }

    @Override
    public void initializeLayouts() {

        progressBarPanel.add(mainProgressBar, BorderLayout.CENTER);
        progressBarPanel.add(subProgressBar, BorderLayout.SOUTH);
        progressBarPanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        JScrollPane scrollPane = new JScrollPane(messagePane);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        getContentPanel().add(progressBarPanel, BorderLayout.NORTH);
        getContentPanel().add(scrollPane, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {

    }
}