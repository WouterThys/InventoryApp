package com.waldo.inventory.gui.dialogs.imagedialogs.sendfullcontentdialog;

import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.test.client.Client;
import com.waldo.test.client.SendFullContentTask;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;

public class SendFullContentDialog extends SendFullContentDialogLayout {


    public SendFullContentDialog(Window window, Client client, File folder, ImageType imageType) {
        super(window, client, folder, imageType);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }


    //
    // Sending content listener
    //
    @Override
    public void onError(Exception e) {
        JOptionPane.showMessageDialog(
                SendFullContentDialog.this,
                "Error: " + e,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void onUpdateState(int i, String s, String... strings) {
        progressLbl.setText(s);
        int progress = 0;
        int total = 0;
        switch (i) {
            case SendFullContentTask.STATE_READ1:
                progress = 0;
                total = Integer.valueOf(strings[0]);
                break;
            case SendFullContentTask.STATE_READ2:
                progress = Integer.valueOf(strings[0]);
                break;
            case SendFullContentTask.STATE_CONVERT1:
                progress = 0;
                total = Integer.valueOf(strings[0]);
                break;
            case SendFullContentTask.STATE_CONVERT2:
                progress = Integer.valueOf(strings[0]);
                break;
            case SendFullContentTask.STATE_SEND1:
                progress = 0;
                total = Integer.valueOf(strings[0]);
                break;
            case SendFullContentTask.STATE_SEND2:
                progress = Integer.valueOf(strings[0]);
                break;
        }

        progressBar.setMaximum(total);
        progressBar.setValue(progress);

        if (s.equalsIgnoreCase("DONE")) {
            progressBar.setValue(total);

            getButtonOK().setVisible(true);
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
        super.windowActivated(e);

        sendFullContentTask.startReading();
    }
}