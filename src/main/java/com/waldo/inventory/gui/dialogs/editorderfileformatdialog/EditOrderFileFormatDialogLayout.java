package com.waldo.inventory.gui.dialogs.editorderfileformatdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.OrderFileFormat;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.ITextField;

import java.awt.*;

abstract class EditOrderFileFormatDialogLayout extends IDialog implements IEditedListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField nameTf;
    ITextField separatorTf;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderFileFormat orderFileFormat;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditOrderFileFormatDialogLayout(Window parent, String title) {
        super(parent, title);
        showTitlePanel(false);
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        nameTf = new ITextField("name");
        nameTf.addEditedListener(this, "name");

        separatorTf = new ITextField("Separator");
        separatorTf.addEditedListener(this, "separator");
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new GridBagLayout());

        GuiUtils.GridBagHelper gbh = new GuiUtils.GridBagHelper(getContentPanel());
        gbh.addLine("Name: ", nameTf);
        gbh.addLine("Separator: ", separatorTf);

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null && object[0] instanceof OrderFileFormat) {
            orderFileFormat = (OrderFileFormat) object[0];

            nameTf.setText(orderFileFormat.getName());
            separatorTf.setText(String.valueOf(orderFileFormat.getSeparator()));
        }
    }
}