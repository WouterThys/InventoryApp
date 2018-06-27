package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class ProgressPanel extends JPanel implements GuiUtils.GuiInterface {

    public enum Progress {
        ItemsBusy,
        ItemsDone,
        LocationsBusy,
        LocationsDone,
        ParseBusy,
        ParseDone
    }

    private final ImageIcon itemsMIcon = imageResource.readIcon("Component.S");
    private final ImageIcon locationsMIcon = imageResource.readIcon("Location.S");
    private final ImageIcon parseMIcon = imageResource.readIcon("Parse.S");

    private final ImageIcon itemsSIcon = imageResource.readIcon("Component.SS");
    private final ImageIcon locationsSIcon = imageResource.readIcon("Location.SS");
    private final ImageIcon parseSIcon = imageResource.readIcon("Parse.SS");

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel itemsLbl;
    private ILabel locationsLbl;
    private ILabel parseLbl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProgressPanel() {
        initializeComponents();
        initializeLayouts();
        updateComponents(Progress.ItemsBusy);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        itemsLbl = new ILabel(itemsMIcon);
        locationsLbl = new ILabel(locationsSIcon);
        parseLbl = new ILabel(parseSIcon);
    }

    @Override
    public void initializeLayouts() {
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(this);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GuiUtils.GridBagHelper.HORIZONTAL;

        add(itemsLbl, gbc); gbc.gridx++;
        add(locationsLbl, gbc); gbc.gridx++;
        add(parseLbl, gbc);

//        setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createEmptyBorder(1,1,1,1),
//                BorderFactory.createLineBorder(Color.darkGray, 1)
//        ));
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            switch ((Progress) args[0]) {
                case ItemsBusy:
                    itemsLbl.setIcon(itemsMIcon);
                    locationsLbl.setIcon(locationsSIcon);
                    parseLbl.setIcon(parseSIcon);
                    break;
                case ItemsDone:
                case LocationsBusy:
                    itemsLbl.setIcon(itemsSIcon);
                    locationsLbl.setIcon(locationsMIcon);
                    parseLbl.setIcon(parseSIcon);
                    break;
                case LocationsDone:
                case ParseBusy:
                    itemsLbl.setIcon(itemsSIcon);
                    locationsLbl.setIcon(locationsSIcon);
                    parseLbl.setIcon(parseMIcon);
                    break;
                case ParseDone:
                    itemsLbl.setIcon(itemsSIcon);
                    locationsLbl.setIcon(locationsSIcon);
                    parseLbl.setIcon(parseMIcon);
                    break;
            }
        }
    }
}