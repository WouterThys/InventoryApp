package com.waldo.inventory.gui.dialogs.imagedialogs.sendfullcontentdialog;

import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.test.client.Client;
import com.waldo.test.client.SendFullContentTask;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

abstract class SendFullContentDialogLayout extends IDialog implements SendFullContentTask.SendFullContentListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILabel progressLbl;
    JProgressBar progressBar;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SendFullContentTask sendFullContentTask;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SendFullContentDialogLayout(Window window, Client client, File folder, ImageType imageType) {
        super(window, "Send folder");

        sendFullContentTask = new SendFullContentTask(client, this, folder, imageType);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        showTitlePanel(false);
        getButtonOK().setVisible(false);

        progressLbl = new ILabel();
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
    }

    @Override
    public void initializeLayouts() {

        JPanel panel = new JPanel(new BorderLayout());
//        Box box = Box.createVerticalBox();
//        box.add(progressLbl);
//        box.add(progressBar);

        panel.add(progressLbl, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        getContentPanel().add(panel);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {

    }


}