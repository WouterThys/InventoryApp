package com.waldo.inventory.gui.dialogs.querydialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.DatabaseAccess;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

class QueryPanel extends JPanel {

    private JTextPane queryTextArea;
    private JTextField messageTextField;

    private Action executeAction;
    private Action executeAllAction;
    private Action clearAction;

    QueryPanel() {
        this.setLayout(new BorderLayout());
        initActions();
        initComponents();
    }

    private void initActions() {
        executeAction = new AbstractAction("Execute", imageResource.readIcon("QueryDialog.Execute")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String queryTxt = queryTextArea.getText();
                int cursorPosition = queryTextArea.getCaretPosition();
                handleQueryText(queryTxt, cursorPosition);
            }
        };

        executeAllAction = new AbstractAction("Execute all", imageResource.readIcon("QueryDialog.ExecuteAll")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String queryTxt = queryTextArea.getText();
                handleQueryText(queryTxt, -1);
            }
        };

        clearAction = new AbstractAction("Clear", imageResource.readIcon("QueryDialog.Clear")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryTextArea.setText("");
            }
        };
    }

    private void initComponents() {
        //queryTextArea = new JEditorPane();
        messageTextField = new JTextField();
        JToolBar queryToolBar = new JToolBar();

        final SimpleAttributeSet blueSet = new SimpleAttributeSet();
        StyleConstants.setForeground(blueSet, Color.BLUE);
        StyleConstants.setBold(blueSet, true);

        final SimpleAttributeSet darkGreenSet = new SimpleAttributeSet();
        StyleConstants.setForeground(darkGreenSet, new Color(0,102,0));
        StyleConstants.setBold(darkGreenSet, true);

        final SimpleAttributeSet lightGreenSet = new SimpleAttributeSet();
        StyleConstants.setForeground(lightGreenSet,new Color(76,153,0));
        StyleConstants.setBold(lightGreenSet, true);

        final SimpleAttributeSet blackSet = new SimpleAttributeSet();
        StyleConstants.setForeground(blackSet, Color.BLACK);
        StyleConstants.setBold(blackSet, false);

        List<String> tableNames;
        StringBuilder tableNamesString = new StringBuilder();
//        try {
//            tableNames = DatabaseAccess.db().getTableNames();
//            for(String name : tableNames) {
//                tableNamesString.append(name.toUpperCase()).append("|");
//            }
//            tableNamesString = new StringBuilder(tableNamesString.substring(0, tableNamesString.length() - 2)); // remove last | again
//        } catch (SQLException e) {
//            Status().setError("Error initializing.", e);
//        }

        final String finalTableNamesString = tableNamesString.toString();
        DefaultStyledDocument doc = new DefaultStyledDocument() {
            @Override
            public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offset, str, a);
                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offset);
                if (before < 0) before = 0;
                int after = findFirstNonWordChar(text, offset + str.length());
                int wordL = before;
                int wordR = before;

                while (wordR <= after) {
                    if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                        if (text.substring(wordL, wordR).toUpperCase().matches("(\\W)*("+ Statics.sqlKeyWords+")")) {
                            setCharacterAttributes(wordL, wordR - wordL, blueSet, false); // SQL words
                        } else if (text.substring(wordL, wordR).toUpperCase().matches("(\\W)*("+ finalTableNamesString+")")) {
                            setCharacterAttributes(wordL, wordR - wordL, darkGreenSet, false); // Table names
                        } else {
                            setCharacterAttributes(wordL, wordR - wordL, blackSet, false);
                        }
                        wordL = wordR;
                    }
                    wordR++;
                }
            }

            public void remove (int offs, int len) throws BadLocationException {
                super.remove(offs, len);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offs);
                if (before < 0) before = 0;
                int after = findFirstNonWordChar(text, offs);

                if (text.substring(before, after).matches("(\\W)*("+ Statics.sqlKeyWords+")")) {
                    setCharacterAttributes(before, after - before, blueSet, false);
                } else {
                    setCharacterAttributes(before, after - before, blackSet, false);
                }
            }
        };

        queryTextArea = new JTextPane(doc);
        queryTextArea.setMargin(new Insets(5,5,5,5));
        queryTextArea.setPreferredSize(new Dimension(600,400));

        messageTextField.setEditable(false);
        messageTextField.setText("");

        JMenuItem executeMenuItem = new JMenuItem();
        executeMenuItem.setAction(executeAction);
        JMenuItem executeAllMenuItem = new JMenuItem();
        executeAllMenuItem.setAction(executeAllAction);
        JMenuItem clearMenuItem = new JMenuItem();
        clearMenuItem.setAction(clearAction);

        queryToolBar.add(executeMenuItem);
        queryToolBar.add(executeAllMenuItem);
        queryToolBar.add(clearMenuItem);
        queryToolBar.setOrientation(JToolBar.VERTICAL);
        queryToolBar.setFloatable(false);

        //add(new JLabel("SQL query: "), BorderLayout.WEST);
        add(new JScrollPane(queryTextArea), BorderLayout.CENTER);
        add(messageTextField, BorderLayout.SOUTH);
        add(queryToolBar, BorderLayout.EAST);
    }

    private void handleQueryText(String queryText, int cursorPosition) {
        if (queryText != null && !queryText.isEmpty()) {
            String[] queries = queryText.split(";");

            if (queries.length > 0) {
                if (cursorPosition >= 0) { // Execute only one
                    executeQuery(findQueryFromCursor(queries, cursorPosition));
                } else {
                    for (String q : queries) { // Execute all
                        executeQuery(q);
                    }
                }
            }
        } else {
            setError("Query field is empty...");
        }
    }

    private String findQueryFromCursor(String[] queries, int cursorPosition) {
        for (String s : queries) {
            int l = s.length();
            if (cursorPosition < l) {
                return s;
            } else {
                cursorPosition -= l;
            }
        }
        return "";
    }

    private void executeQuery(String query) {
        try {
            try (Connection connection = DatabaseAccess.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                System.out.println("QUERY: "+query);
                while (resultSet.next()) {
                    System.out.println(resultSet.getObject(2));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to execute query: " + query, e);
            setError("Failed to execute query: " + query);
        }
    }

    private int findLastNonWordChar (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int findFirstNonWordChar (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }

    private void setError(String error) {
        messageTextField.setText(error);
        messageTextField.setForeground(Color.RED);
        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                messageTextField.setText("");
                messageTextField.setForeground(Color.BLACK);
            }
        }, 3000);
    }
}