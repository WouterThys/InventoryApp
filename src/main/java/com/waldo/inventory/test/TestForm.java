package com.waldo.inventory.test;

import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestForm {
    private JButton button1;
    private ITextField textField1;

    public TestForm() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}
