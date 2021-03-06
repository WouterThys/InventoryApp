package com.waldo.inventory.gui.dialogs.editremarksdialog;

import com.waldo.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class EditRemarksDialog extends EditRemarksDialogLayout {


    public EditRemarksDialog(Window window, String title, File file) {
        super(window, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(file);

    }

    @Override
    void onSave()  {
        if (textEditor.getStyledDocument().getLength() > 0) {
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
        } else {
            file = null;
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
    }

    @Override
    protected void onCancel() {
        super.onCancel();
    }
}