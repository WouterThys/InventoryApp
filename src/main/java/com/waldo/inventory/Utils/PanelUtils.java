package com.waldo.inventory.Utils;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;

public class PanelUtils {

    public static GridBagConstraints createLabelConstraints(int gridLocX, int gridLocY) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridLocX;
        constraints.gridy = gridLocY;
        constraints.anchor = WEST;
        constraints.insets = new Insets(2,2,2,2);
        return constraints;
    }

    public static GridBagConstraints createFieldConstraints(int gridLocX, int gridLocY) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridLocX;
        constraints.gridy = gridLocY;
        constraints.weightx = 3;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(2,2,2,2);
        constraints.fill = BOTH;
        return constraints;
    }

    public static GridBagConstraints createButtonConstraints(int gridLocX, int gridLocY) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridLocX;
        constraints.gridy = gridLocY;
        constraints.weightx = 1;
        constraints.fill = BOTH;
        constraints.insets = new Insets(2,2,2,2);
        return constraints;
    }

    public static JTextField getHintTextField(String hint) {
        return new HintTextField(hint);
    }

    public static HintFormattedTextField getHintFormattedTextField(String hint, NumberFormatter formatter) {
        return new HintFormattedTextField(hint, formatter);
    }

    public static JTextArea getHintTextArea(String hint) {
        return new HintTextArea(hint);
    }


    /**
     *
     *  HELPER CLASSES
     *
     */

    private static class HintTextField extends JTextField implements FocusListener {

        private final String hint;

        HintTextField(final String hint) {
            super(hint);
            this.hint = hint;
            super.addFocusListener(this);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if(this.getText().isEmpty()) {
                super.setText("");
            }
        }
        @Override
        public void focusLost(FocusEvent e) {
            if(this.getText().isEmpty()) {
                super.setText(hint);
            }
        }

        @Override
        public String getText() {
            if (super.getText().equals(hint)) {
                return "";
            } else {
                return super.getText();
            }
        }
    }

//        @Override
//        public void setText(String t) {
//            if (t == null || t.isEmpty() || t.equals(hint)) {
//                this.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
//                super.setText(hint);
//                showingHint = true;
//            } else {
//                this.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
//                super.setText(t);
//                showingHint = false;
//            }
//        }


    private static class HintTextArea extends JTextArea implements FocusListener {

        private final String hint;
        private boolean showingHint;

        HintTextArea(final String hint) {
            super(hint);
            this.hint = hint;
            this.showingHint = true;
            super.addFocusListener(this);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if(this.getText().isEmpty()) {
                super.setText("");
            }
        }
        @Override
        public void focusLost(FocusEvent e) {
            if(this.getText().isEmpty()) {
                super.setText(hint);
            }
        }

        @Override
        public String getText() {
            return showingHint ? "" : super.getText();
        }

        @Override
        public void setText(String t) {
            if (t == null || t.isEmpty() || t.equals(hint)) {
                this.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
                super.setText(hint);
                showingHint = true;
            } else {
                this.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                super.setText(t);
                showingHint = false;
            }
        }
    }

    private static class HintFormattedTextField extends JFormattedTextField implements FocusListener {

        private final String hint;
        private boolean showingHint;

        HintFormattedTextField(final String hint, NumberFormatter formatter) {
            super(formatter);
            this.setText(hint);
            this.hint = hint;
            this.showingHint = true;
            super.addFocusListener(this);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if(this.getText().isEmpty()) {
                super.setText("");
                showingHint = false;
            }
        }
        @Override
        public void focusLost(FocusEvent e) {
            if(this.getText().isEmpty()) {
                super.setText(hint);
                showingHint = true;
            }
        }

        @Override
        public String getText() {
            return showingHint ? "" : super.getText();
        }
    }

}
