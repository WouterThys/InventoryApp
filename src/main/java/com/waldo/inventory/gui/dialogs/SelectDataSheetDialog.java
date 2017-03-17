package com.waldo.inventory.gui.dialogs;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.waldo.inventory.Utils.PanelUtils.createButtonConstraints;
import static com.waldo.inventory.Utils.PanelUtils.createLabelConstraints;

public class SelectDataSheetDialog extends JPanel {

    private final String onlineLink;
    private final String localLink;
    private static Application parent;

    public static void showDialog(Application parent, String onlineLink, String localLink) {
        SelectDataSheetDialog.parent = parent;
        JDialog dialog = new JDialog(parent, "Create new Item", true);
        dialog.getContentPane().add(new SelectDataSheetDialog(onlineLink, localLink));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(500,400);
        //dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private SelectDataSheetDialog(String onlineLink, String localLink) {
        super(new GridBagLayout());
        this.onlineLink = onlineLink;
        this.localLink = localLink;
        initComponents();
    }

    private void initComponents() {
        add(new JLabel("Local: "), createLabelConstraints(0,0));
        add(new JLabel(localLink), createLabelConstraints(1,0));
        Button localBtn = new Button("Open");
        localBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        add(localBtn, createButtonConstraints(1,1));

        add(new JLabel("Online: "), createLabelConstraints(0,2));
        add(new JLabel(localLink), createLabelConstraints(1,2));
        Button onlineBtn = new Button("Open");
        onlineBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        add(onlineBtn, createButtonConstraints(2,2));
    }

}
