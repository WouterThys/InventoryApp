package com.waldo.inventory.gui.panels.mainpanel.itempreviewpanel;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;
import com.waldo.inventory.gui.dialogs.historydialog.HistoryDialog;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;
import com.waldo.inventory.gui.panels.mainpanel.AbstractDetailPanel;
import com.waldo.utils.OpenUtils;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public abstract class ItemPreviewPanel extends AbstractDetailPanel implements IdBToolBar.IdbToolBarListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel iconLbl;

    private ITextField nameTf;
    private ILabel aliasLbl;
    private ITextArea descriptionTa;

    private ITextField manufacturerTf;
    private ITextField footprintTf;
    private ITextField locationTf;

    private ITextField categoryTf;
    private ITextField productTf;
    private ITextField typeTf;

    private IStarRater starRater;
    private ITextPane remarksTp;

    private AbstractAction dataSheetAa;
    private AbstractAction orderAa;
    private AbstractAction historyAa;

    private IdBToolBar dbToolbar;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final Application application;
    Item selectedItem;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ItemPreviewPanel(Application application) {
        this.application = application;
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateToolbar(Item item) {
        aliasLbl.setText(item.getAlias());

        if (item.getOnlineDataSheet().isEmpty() && item.getLocalDataSheet().isEmpty()) {
            dataSheetAa.setEnabled(false);
        } else {
            dataSheetAa.setEnabled(true);
        }
    }

    private void updateHeader(Item item) {
        try {
            if (!item.getIconPath().isEmpty()) {
                Path path = Paths.get(settings().getFileSettings().getImgItemsPath(), item.getIconPath());
                iconLbl.setIcon(imageResource.readImage(path));
            } else {
                iconLbl.setIcon(imageResource.readImage("Items.Edit.Title"));
            }
        } catch (Exception e) {
            Status().setError("Failed to set item icon");
        }
        nameTf.setText(item.toString());
        descriptionTa.setText(item.getDescription());
        starRater.setRating(item.getRating());
    }

    private void updateData(Item item) {
        if (item.getCategoryId() > DbObject.UNKNOWN_ID) {
            categoryTf.setText(item.getCategory().toString());
        } else {
            categoryTf.setText("");
        }

        if (item.getProductId() > DbObject.UNKNOWN_ID) {
            productTf.setText(item.getProduct().toString());
        } else {
            productTf.setText("");
        }

        if (item.getTypeId() > DbObject.UNKNOWN_ID) {
            typeTf.setText(item.getType().toString());
        } else {
            typeTf.setText("");
        }

        if (item.getManufacturerId() > DbObject.UNKNOWN_ID) {
            manufacturerTf.setText(item.getManufacturer().toString());
        } else {
            manufacturerTf.setText("");
        }

        if (item.getPackageTypeId() > DbObject.UNKNOWN_ID) {
            footprintTf.setText(item.getPackageType().getPrettyString());
        } else {
            footprintTf.setText("");
        }

        if (item.getLocationId() > DbObject.UNKNOWN_ID) {
            locationTf.setText(item.getLocation().getPrettyString());
        } else {
            locationTf.setText("");
        }
    }

    private void updateRemarks(Item item) {
        remarksTp.setFile(item.getRemarksFile());
    }

    private void openDataSheet(Item item) {
        if (item != null) {
            String local = item.getLocalDataSheet();
            String online = item.getOnlineDataSheet();
            if (local != null && !local.isEmpty() && online != null && !online.isEmpty()) {
                SelectDataSheetDialog.showDialog(application, online, local);
            } else if (local != null && !local.isEmpty()) {
                try {
                    OpenUtils.openPdf(local);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(application,
                            "Error opening the file: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else if (online != null && !online.isEmpty()) {
                try {
                    OpenUtils.browseLink(online);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(application,
                            "Error opening the file: " + e1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        }
    }

    private void orderItem(Item item) {
        int result = JOptionPane.YES_OPTION;
        if (item.isDiscourageOrder()) {
            result = JOptionPane.showConfirmDialog(
                    application,
                    "This item is marked to discourage new orders, \n do you really want to order it?",
                    "Discouraged to order",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
        }
        if (result == JOptionPane.YES_OPTION) {
            OrderItemDialog dialog = new OrderItemDialog(application, "Order " + item.getName(), item, true);
            dialog.showDialog();
        }
    }

    private void showHistory(Item item) {
        HistoryDialog dialog = new HistoryDialog(application, item);
        dialog.showDialog();
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

        iconLbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(2,2,2,2)
        ));

        JPanel raterPnl = new JPanel();
        raterPnl.add(starRater);

        JScrollPane scrollPane = new JScrollPane(descriptionTa);
        scrollPane.setBorder(null);

//        JPanel iconPnl = new JPanel(new BorderLayout());
//        iconPnl.add(iconLbl, BorderLayout.CENTER);
//        //iconPnl.add(raterPnl, BorderLayout.PAGE_END);
//
//        JPanel dataPnl = new JPanel(new BorderLayout());
//        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(dataPnl, 0);
//        gbc.addLine("", nameTf);
//        gbc.weightx = 1;
//        gbc.weighty = 1;
//        gbc.addLine("", new JScrollPane(descriptionTa), GridBagConstraints.BOTH);
//
//        headerPnl.add(iconPnl, BorderLayout.WEST);
//        headerPnl.add(dataPnl, BorderLayout.CENTER);
//        headerPnl.add(raterPnl, BorderLayout.PAGE_END);


        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(headerPnl);
        // Label
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        headerPnl.add(iconLbl, gbc);

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
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPnl.add(raterPnl, gbc);

        return headerPnl;
    }

//    private JPanel createDivisionPanel() {
//        JPanel divisionPnl = new JPanel();
//        divisionPnl.setLayout(new BoxLayout(divisionPnl, BoxLayout.X_AXIS));
//
//        JPanel cPnl = new JPanel(new BorderLayout());
//        JPanel pPnl = new JPanel(new BorderLayout());
//        JPanel tPnl = new JPanel(new BorderLayout());
//
//        cPnl.add(new ILabel(imageResource.readImage("Items.Tree.Category")), BorderLayout.WEST);
//        cPnl.add(categoryTf, BorderLayout.CENTER);
//        pPnl.add(new ILabel(imageResource.readImage("Items.Tree.Product")), BorderLayout.WEST);
//        pPnl.add(productTf, BorderLayout.CENTER);
//        tPnl.add(new ILabel(imageResource.readImage("Items.Tree.Type")), BorderLayout.WEST);
//        tPnl.add(typeTf, BorderLayout.CENTER);
//
//        divisionPnl.add(cPnl);
//        divisionPnl.add(pPnl);
//        divisionPnl.add(tPnl);
//
//        return divisionPnl;
//    }

    private JPanel createDataPanel() {
        JPanel dataPnl = new JPanel();
        dataPnl.setLayout(new BoxLayout(dataPnl, BoxLayout.Y_AXIS));

        GuiUtils.GridBagHelper gbc;

        JPanel divisionPanel = new JPanel();
        divisionPanel.setBorder(BorderFactory.createEmptyBorder(1,1,8,1));
        gbc = new GuiUtils.GridBagHelper(divisionPanel, 0);
        gbc.addLine("Category", imageResource.readImage("Items.Tree.Category"), categoryTf);
        gbc.addLine("Product", imageResource.readImage("Items.Tree.Product"), productTf);
        gbc.addLine("Type", imageResource.readImage("Items.Tree.Type"), typeTf);

        JPanel infoPnl = new JPanel();
        infoPnl.setBorder(BorderFactory.createEmptyBorder(8,1,1,1));
        gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("Manufacturers", imageResource.readImage("Manufacturers.Menu"), manufacturerTf);
        gbc.addLine("Footprint", imageResource.readImage("Packages.Menu"), footprintTf);
        gbc.addLine("Location", imageResource.readImage("Locations.Menu"), locationTf);

        //dataPnl.add(createDivisionPanel());
        dataPnl.add(divisionPanel);
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
        // Label
        iconLbl = new ILabel();
        iconLbl.setBackground(Color.WHITE);
        iconLbl.setOpaque(true);
        iconLbl.setHorizontalAlignment(ILabel.CENTER);
        iconLbl.setVerticalAlignment(ILabel.CENTER);
        iconLbl.setPreferredSize(new Dimension(150,150));
        iconLbl.setMaximumSize(new Dimension(150,150));
        iconLbl.setMinimumSize(new Dimension(150,150));

        // Data
        nameTf = new ITextField(false);
        aliasLbl = new ILabel();
        aliasLbl.setHorizontalAlignment(ILabel.CENTER);
        aliasLbl.setVerticalAlignment(ILabel.CENTER);
        aliasLbl.setFont(20, Font.BOLD);
        manufacturerTf = new ITextField(false);
        footprintTf = new ITextField(false);
        locationTf = new ITextField(false);
        categoryTf = new ITextField(false);
        productTf = new ITextField(false);
        typeTf = new ITextField(false);
        descriptionTa = new ITextArea(false);
        descriptionTa.setBorder(nameTf.getBorder());
        descriptionTa.setEnabled(false);
        descriptionTa.setLineWrap(true);
        descriptionTa.setWrapStyleWord(true);

        starRater = new IStarRater();
        starRater.setEnabled(false);

        remarksTp = new ITextPane();
        //remarksTp.setPreferredSize(new Dimension(300, 50));
        remarksTp.setEditable(false);
        remarksTp.setEnabled(false);

        dbToolbar = new IdBToolBar(this, false, false, true, true);

        // Actions
        dataSheetAa = new AbstractAction("Datasheet", imageResource.readImage("Items.Buttons.Datasheet")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null) {
                    openDataSheet(selectedItem);
                }
            }
        };
        dataSheetAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Data sheet");
        orderAa = new AbstractAction("Order", imageResource.readImage("Items.Buttons.Order")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null) {
                    orderItem(selectedItem);
                }
            }
        };
        orderAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Order");
        historyAa = new AbstractAction("History", imageResource.readImage("Items.Buttons.History")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null) {
                    showHistory(selectedItem);
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

//        JPanel mainPanel = new JPanel();
//        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//
//        mainPanel.add(toolbarsPanel);
//        mainPanel.add(headerPanel);
//        mainPanel.add(dataPanel);
//
//        panel.add(mainPanel, BorderLayout.CENTER);
//        panel.add(createRemarksPanel(), BorderLayout.SOUTH);
//
//        setMinimumSize(new Dimension(400, 400));
//        add(panel);

        setLayout(new BorderLayout());

        panel1.add(headerPanel, BorderLayout.NORTH);
        panel1.add(dataPanel, BorderLayout.CENTER);

        panel2.add(toolbarsPanel, BorderLayout.PAGE_START);
        panel2.add(panel1, BorderLayout.CENTER);

        add(panel2, BorderLayout.NORTH);
        add(remarksPanel, BorderLayout.CENTER);
        //setPreferredSize(new Dimension(400, 100));
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            Item newItem = (Item) args[0];

            if (isVisible() && selectedItem != null && newItem.equals(selectedItem)) {
                setVisible(false);
            } else {
                updateToolbar(newItem);
                updateHeader(newItem);
                updateData(newItem);
                updateRemarks(newItem);

                setVisible(true);
                selectedItem = newItem;
            }
        } else {
            setVisible(false);
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
