package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.classes.DbTable;
import com.waldo.inventory.managers.TableManager;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class IQueryPane extends JTextPane {

    public IQueryPane() {
        super();

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

        List<DbTable> tableNames;
        StringBuilder tableNamesString = new StringBuilder();

            tableNames = TableManager.dbTm().getDbTableList();
            for(DbTable table : tableNames) {
                tableNamesString.append(table.getTableName().toUpperCase()).append("|");
            }
            tableNamesString = new StringBuilder(tableNamesString.substring(0, tableNamesString.length() - 2)); // remove last | again

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

        setDocument(doc);
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

}
