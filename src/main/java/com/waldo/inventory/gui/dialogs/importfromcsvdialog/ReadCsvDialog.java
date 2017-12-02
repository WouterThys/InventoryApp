package com.waldo.inventory.gui.dialogs.importfromcsvdialog;

import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.io.File;

public class ReadCsvDialog extends ReadCsvDialogLayout {

    public ReadCsvDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        initActions();
        updateComponents();
    }

    private void initActions() {

    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {

        String fileName = fileNameTf.getText();
        if (fileName != null && !fileName.isEmpty()) {
            dialogResult = OK;

            SwingUtilities.invokeLater(() -> {
                ImportCsvDialog dialog = new ImportCsvDialog(application, "Import csv",
                        new File(fileNameTf.getText()),
                        useHeaderCb.isSelected(),
                        (int)headerRowSp.getValue(),
                        (int)componentNameColSp.getValue());
                dialog.showDialog();
            });

            dispose();
        } else {
            fileNameTf.setError("File name can not be empty!");
        }

    }

}
