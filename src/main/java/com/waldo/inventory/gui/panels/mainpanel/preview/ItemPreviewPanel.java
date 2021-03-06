package com.waldo.inventory.gui.panels.mainpanel.preview;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics.ImageType;
import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.components.IDivisionPanel;
import com.waldo.inventory.gui.components.IImagePanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.panels.mainpanel.AbstractDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.ItemDetailListener;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ItemPreviewPanel extends AbstractDetailPanel implements IdBToolBar.IdbToolBarListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IImagePanel imagePanel;

    private ITextField nameTf;
    private ILabel aliasLbl;
    private ITextArea descriptionTa;
    private ITextField manufacturerTf;
    private ITextField footprintTf;
    private ITextField locationTf;
    private IStarRater starRater;
    private ITextPane remarksTp;
    private IDivisionPanel divisionPnl;

    private AbstractAction dataSheetAa;
    private AbstractAction orderAa;
    private AbstractAction historyAa;

    private IdBToolBar dbToolbar;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Item selectedItem;
    private final ItemDetailListener itemDetailListener;
    private boolean simple = false;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected ItemPreviewPanel(ItemDetailListener itemDetailListener) {
        this(itemDetailListener, false);
    }

    protected ItemPreviewPanel(ItemDetailListener itemDetailListener, boolean simple) {
        this.itemDetailListener = itemDetailListener;
        this.simple = simple;
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateToolbar(Item item) {
        if (item != null) {
            aliasLbl.setText(item.getAlias());
            dataSheetAa.setEnabled(!item.getOnlineDataSheet().isEmpty() || !item.getLocalDataSheet().isEmpty());
        } else {
            aliasLbl.setText("");
            dataSheetAa.setEnabled(false);
        }
    }

    private void updateHeader(Item item) {
        if (item != null) {
            nameTf.setText(item.toString());
            descriptionTa.setText(item.getDescription());
            starRater.setRating(item.getRating());
        } else {
            nameTf.setText("");
            descriptionTa.setText("");
            starRater.setRating(0);
        }
        imagePanel.updateComponents(item);
    }

    private void updateData(Item item) {
        if (item != null) {
            divisionPnl.updateComponents(item.getDivision());

            if (item.getManufacturer() != null) {
                manufacturerTf.setText(item.getManufacturer().toString());
            } else {
                manufacturerTf.setText("");
            }

            if (item.getPackageType() != null) {
                footprintTf.setText(item.getPackageType().getPrettyString());
            } else {
                footprintTf.setText("");
            }

            if (item.getLocation() != null) {
                locationTf.setText(item.getLocation().getPrettyString());
            } else {
                locationTf.setText("");
            }

        } else {
            divisionPnl.updateComponents((Division)null);
            manufacturerTf.setText("");
            footprintTf.setText("");
            locationTf.setText("");
        }
    }

    private void updateRemarks(Item item) {
        if (item != null) {
            remarksTp.setFile(item.getRemarksFile());
        } else {
            remarksTp.setText("");
        }
    }

    private JPanel createToolBarPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JToolBar eastTb = GuiUtils.createNewToolbar(dataSheetAa, orderAa, historyAa);

        panel.add(dbToolbar, BorderLayout.WEST);
        panel.add(aliasLbl, BorderLayout.CENTER);
        panel.add(eastTb, BorderLayout.EAST);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPnl = new JPanel(new BorderLayout());

        JPanel raterPnl = new JPanel();
        raterPnl.add(starRater);

        JScrollPane scrollPane = new JScrollPane(descriptionTa);
        scrollPane.setBorder(null);

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(headerPnl);
        // Label
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        headerPnl.add(imagePanel, gbc);

        // Name
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPnl.add(nameTf, gbc);

        // Name
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        headerPnl.add(scrollPane, gbc);

        // Rater
        if (!simple) {
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.gridy = 2;
            gbc.weighty = 0;
            gbc.gridwidth = 2;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            headerPnl.add(raterPnl, gbc);
        }

        return headerPnl;
    }

    private JPanel createDataPanel() {
        JPanel dataPnl = new JPanel();
        dataPnl.setLayout(new BoxLayout(dataPnl, BoxLayout.Y_AXIS));

        GuiUtils.GridBagHelper gbc;

        JPanel divisionPanel = new JPanel();
        divisionPanel.setBorder(BorderFactory.createEmptyBorder(1,1,8,1));
        divisionPanel.setLayout(new BorderLayout());
        divisionPanel.add(divisionPnl, BorderLayout.CENTER);

        JPanel infoPnl = new JPanel();
        infoPnl.setBorder(BorderFactory.createEmptyBorder(8,1,1,1));
        gbc = new GuiUtils.GridBagHelper(infoPnl);
        if (!simple) gbc.addLine("Manufacturers", imageResource.readIcon("Factory.SS"), manufacturerTf);
        gbc.addLine("Footprint", imageResource.readIcon("Chip.SS"), footprintTf);
        gbc.addLine("Location", imageResource.readIcon("Location.SS"), locationTf);

        if (!simple) dataPnl.add(divisionPanel);
        dataPnl.add(infoPnl);

        return dataPnl;
    }

    private JPanel createRemarksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(remarksTp);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

     /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        // Image
        imagePanel = new IImagePanel(null, ImageType.ItemImage, selectedItem, new Dimension(150,150));

        // Data
        nameTf = new ITextField(false);
        aliasLbl = new ILabel();
        aliasLbl.setHorizontalAlignment(ILabel.CENTER);
        aliasLbl.setVerticalAlignment(ILabel.CENTER);
        aliasLbl.setFont(20, Font.BOLD);
        manufacturerTf = new ITextField(false);
        footprintTf = new ITextField(false);
        locationTf = new ITextField(false);
        divisionPnl = new IDivisionPanel();
        descriptionTa = new ITextArea(false);
        descriptionTa.setBorder(nameTf.getBorder());
        descriptionTa.setEnabled(false);
        descriptionTa.setLineWrap(true);
        descriptionTa.setWrapStyleWord(true);

        starRater = new IStarRater();
        starRater.setEnabled(false);

        remarksTp = new ITextPane();
        remarksTp.setEditable(false);
        //remarksTp.setEnabled(false);

        dbToolbar = new IdBToolBar(this, false, false, true, true);

        // Actions
        dataSheetAa = new AbstractAction("Datasheet", imageResource.readIcon("Datasheet.M")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null && itemDetailListener != null) {
                    itemDetailListener.onShowDataSheet(selectedItem);
                }
            }
        };
        dataSheetAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Data sheet");
        orderAa = new AbstractAction("ItemOrder", imageResource.readIcon("Order.M")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null && itemDetailListener != null) {
                    itemDetailListener.onOrderItem(selectedItem);
                }
            }
        };
        orderAa.putValue(AbstractAction.SHORT_DESCRIPTION, "ItemOrder");
        historyAa = new AbstractAction("History", imageResource.readIcon("History.M")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null && itemDetailListener != null) {
                    itemDetailListener.onShowHistory(selectedItem);
                }
            }
        };
        historyAa.putValue(AbstractAction.SHORT_DESCRIPTION, "History");
    }

    @Override
    public void initializeLayouts() {
        JPanel panel1 = new JPanel(new BorderLayout());
        JPanel panel2 = new JPanel(new BorderLayout());

        JPanel toolbarsPanel = createToolBarPanel();
        JPanel headerPanel = createHeaderPanel();
        JPanel dataPanel = createDataPanel();
        JPanel remarksPanel = createRemarksPanel();

        setLayout(new BorderLayout());

        panel1.add(headerPanel, BorderLayout.NORTH);
        panel1.add(dataPanel, BorderLayout.CENTER);

        panel2.add(toolbarsPanel, BorderLayout.PAGE_START);
        panel2.add(panel1, BorderLayout.CENTER);

        add(panel2, BorderLayout.NORTH);
        if (!simple) add(remarksPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length == 0 || args[0] == null) {
            setVisible(false);
            selectedItem = null;
        } else {
            setVisible(true);
            if (args[0] instanceof Item) {
                selectedItem = (Item) args[0];
                updateToolbar(selectedItem);
                updateHeader(selectedItem);
                updateRemarks(selectedItem);
            }

            updateData(selectedItem);
        }
    }

    //
    // Toolbar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {

    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {

    }
}
