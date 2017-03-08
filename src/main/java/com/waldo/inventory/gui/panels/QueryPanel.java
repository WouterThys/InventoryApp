package com.waldo.inventory.gui.panels;

import com.waldo.inventory.Utils.ImageUtils;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimerTask;

public class QueryPanel extends JPanel {

    private Application app;

    private JEditorPane queryTextArea;
    private JTextField messageTextField;
    private JToolBar queryToolBar;

    private Action executeAction;
    private Action executeAllAction;
    private Action clearAction;

    public QueryPanel(Application app) {
        this.setLayout(new BorderLayout());
        this.app = app;
        initActions();
        initComponents();
    }

    private void initActions() {
        executeAction = new AbstractAction("Execute", ImageUtils.loadImageIcon("execute")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String queryTxt = queryTextArea.getText();
                int cursorPosition = queryTextArea.getCaretPosition();
                handleQueryText(queryTxt, cursorPosition);
            }
        };

        executeAllAction = new AbstractAction("Execute all", ImageUtils.loadImageIcon("execute_all")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String queryTxt = queryTextArea.getText();
                handleQueryText(queryTxt, -1);
            }
        };

        clearAction = new AbstractAction("Clear", ImageUtils.loadImageIcon("clear")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryTextArea.setText("");
            }
        };
    }

    private void initComponents() {
        queryTextArea = new JEditorPane();
        messageTextField = new JTextField();
        queryToolBar = new JToolBar();

        queryTextArea.setMargin(new Insets(5,5,5,5));
//        queryTextArea.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                System.out.println("Insert update");
//                checkSqlKeyWords(queryTextArea.getText());
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {}
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                System.out.println("Changed update");
//                checkSqlKeyWords(queryTextArea.getText());
//            }
//        });


        //queryTextArea.setText("select * from items");
        //queryTextArea.setEditorKit(new HTMLEditorKit());
        //checkSqlKeyWords(queryTextArea.getText());

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

        add(new JLabel("SQL query: "), BorderLayout.WEST);
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
            try (Connection connection = DbManager.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                System.out.println("QUERY: "+query);
                while (resultSet.next()) {
                    System.out.println(resultSet.getObject(2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            setError("Failed to execute query: " + query);
        }
    }

    private void checkSqlKeyWords(String text) {
        if (text.toUpperCase().contains("SELECT")) {
            text = text.replace("select", "<b>select</b>");
            System.out.println(text);
        }
        final String edited = text;

        Runnable setBold = new Runnable() {
            @Override
            public void run() {
                queryTextArea.setText(edited);
            }
        };
        SwingUtilities.invokeLater(setBold);
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


//public class Bold extends JTextPane {
//
//    public Bold(){
//        super();
//
//        setEditorKit(new HTMLEditorKit());
//        setText("<html><h1>Example</h1><p>Just a test</p></html>");
//        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK), "bold");
//        getActionMap().put("bold", new AbstractAction(){
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JTextPane bold = (JTextPane) e.getSource();
//                int start = bold.getSelectionStart();
//                int end = bold.getSelectionEnd();
//                String txt = bold.getSelectedText();
//                if(end != start)
//                    try {
//                        bold.getDocument().remove(start, end-start);
//                        HTMLEditorKit htmlkit = (HTMLEditorKit) bold.getEditorKit();
//                        htmlkit.insertHTML((HTMLDocument) bold.getDocument(), start, "<b>"+txt+"</b>", 0, 0, HTML.Tag.B);
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//            }
//
//        });
//    }