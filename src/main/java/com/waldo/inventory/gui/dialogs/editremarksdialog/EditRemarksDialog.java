package com.waldo.inventory.gui.dialogs.editremarksdialog;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.io.*;

public class EditRemarksDialog extends EditRemarksDialogLayout {


    public EditRemarksDialog(Application application, String title, File file) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(file);

    }

    @Override
    void onSave()  {
        if (file == null) {
            try {
                file = FileUtils.createTempFile("remarks");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (file != null) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                    objectOutputStream.writeObject(textEditor.getStyledDocument());
                    objectOutputStream.flush();
                    objectOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(
                    EditRemarksDialog.this,
                    "Could not save file..",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public File getFile() {
        return file;
    }

    @Override
    protected void onOK() {
        onSave();
        super.onOK();
    }

    @Override
    protected void onNeutral() {
        onSave();
        super.onOK();
    }

    @Override
    protected void onCancel() {
        super.onCancel();
    }
}