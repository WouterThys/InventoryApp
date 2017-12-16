package com.waldo.inventory.gui.dialogs;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class SelectDataSheetDialog extends JPanel {

    private static JDialog dialog;
    private final String onlineLink;
    private final String localLink;

    public static void showDialog(Application parent, String onlineLink, String localLink) {
        dialog = new JDialog(parent, "Create new Item");
        dialog.getContentPane().add(new SelectDataSheetDialog(onlineLink, localLink));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
    }

    private SelectDataSheetDialog(String onlineLink, String localLink) {
        super(new BorderLayout());
        this.onlineLink = onlineLink;
        this.localLink = localLink;
        initComponents();
    }

    private void initComponents() {
        add(new JLabel("There are two datasheets specified, which one do you want to open?"), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> close());

        final JButton onlineBtn = new JButton("Online");
        onlineBtn.addActionListener(e -> {
            try {
                OpenUtils.browseLink(onlineLink);
                close();
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(SelectDataSheetDialog.this, "Error opening the file: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            }
        });

        final JButton localBtn = new JButton("Local");
        localBtn.addActionListener(e -> {
            try {
                OpenUtils.openPdf(localLink);
                close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(SelectDataSheetDialog.this, "Error opening the file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(onlineBtn);
        buttonPanel.add(localBtn);

        add(buttonPanel, BorderLayout.SOUTH);

    }

    private void close() {
        dialog.setVisible(false);
        dialog.dispose();
    }

}
