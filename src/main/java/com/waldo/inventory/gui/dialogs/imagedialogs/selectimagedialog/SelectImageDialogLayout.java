package com.waldo.inventory.gui.dialogs.imagedialogs.selectimagedialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics.ImageType;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ITextField;
import com.waldo.utils.icomponents.ITextFieldActionPanel;

import javax.swing.*;
import java.awt.*;

abstract class SelectImageDialogLayout extends IDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    IComboBox<ImageType> imageTypeCb;
    ITextField imageNameTf;
    ITextFieldActionPanel browsePnl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final boolean multiple;
    ImageType imageType;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SelectImageDialogLayout(Window window, boolean multiple, ImageType imageType) {
        super(window, "Select image");

        this.imageType = imageType;
        this.multiple = multiple;
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

        imageTypeCb = new IComboBox<>(ImageType.values());
        imageNameTf = new ITextField("Image name");
        if (multiple) {
            browsePnl = new GuiUtils.IBrowseFilePanel("File", ".", false);
        } else {
            browsePnl = new GuiUtils.IBrowseImagePanel(".", null, "");
        }
    }

    @Override
    public void initializeLayouts() {

        JPanel panel = new JPanel();
        com.waldo.utils.GuiUtils.GridBagHelper gbc = new com.waldo.utils.GuiUtils.GridBagHelper(panel);
        if (multiple) {
            gbc.addLine("Folder: ", browsePnl);
        } else {
            gbc.addLine("Image: ", browsePnl);
            gbc.addLine("Name: ", imageNameTf);
        }

        if (imageType == null) {
            gbc.addLine("Type: ", imageTypeCb);
        }

        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        getContentPanel().add(panel);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {

        } else {

        }
    }
}