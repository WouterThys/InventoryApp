package com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ItemDetailPanelLayout extends JPanel implements GuiInterface {

    public interface OnItemDetailListener {
        void onShowDataSheet(Item item);
        void onShowDataSheet(Item item, boolean online);
        void onOrderItem(Item item);
        void onShowHistory(Item item);
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILabel iconLbl;

    ITextField nameTf;
    ITextField descriptionTa;

    ITextField categoryTf;
    ITextField productTf;
    ITextField typeTf;

    ITextField manufacturerTf;
    ITextField footprintTf;
    ITextField locationTf;

    IStarRater starRater;
    ICheckBox discourageOrderCb;
    ITextPane remarksTp;

    JButton dataSheetBtn;
    private JButton orderBtn;
    private JButton historyBtn;

    JPanel remarksPnl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Item selectedItem;
    private final OnItemDetailListener detailListener;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ItemDetailPanelLayout(OnItemDetailListener detailListener) {
        this.detailListener = detailListener;

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private JPanel createIconPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        iconLbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(2,2,2,2)
        ));

        panel.add(iconLbl, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }

    private JPanel createIdentificationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(descriptionTa);
        scrollPane.setBorder(null);

        panel.add(nameTf, BorderLayout.NORTH);
        panel.add(descriptionTa, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createComponentInfoPanel() {
        JPanel dataPnl = new JPanel();
        dataPnl.setLayout(new BoxLayout(dataPnl, BoxLayout.X_AXIS));

        GuiUtils.GridBagHelper gbc;

        JPanel divisionPanel = new JPanel();
        divisionPanel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        gbc = new GuiUtils.GridBagHelper(divisionPanel, 0);
        gbc.addLine("Category", imageResource.readImage("Items.Tree.Category"), categoryTf);
        gbc.addLine("Product", imageResource.readImage("Items.Tree.Product"), productTf);
        gbc.addLine("Type", imageResource.readImage("Items.Tree.Type"), typeTf);

        JPanel infoPnl = new JPanel();
        infoPnl.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        gbc = new GuiUtils.GridBagHelper(infoPnl);
        gbc.addLine("Manufacturers", imageResource.readImage("Manufacturers.Menu"), manufacturerTf);
        gbc.addLine("Footprint", imageResource.readImage("Packages.Menu"), footprintTf);
        gbc.addLine("Location", imageResource.readImage("Locations.Menu"), locationTf);

        //dataPnl.add(createDivisionPanel());
        dataPnl.add(infoPnl);
        dataPnl.add(divisionPanel);

        return dataPnl;
    }

    private JPanel createDivisionPanel() {
        JPanel divisionPnl = new JPanel();
        divisionPnl.setLayout(new BoxLayout(divisionPnl, BoxLayout.X_AXIS));

        JPanel cPnl = new JPanel(new BorderLayout());
        JPanel pPnl = new JPanel(new BorderLayout());
        JPanel tPnl = new JPanel(new BorderLayout());

        cPnl.add(new ILabel(imageResource.readImage("Items.Tree.Category")), BorderLayout.WEST);
        cPnl.add(categoryTf, BorderLayout.CENTER);
        pPnl.add(new ILabel(imageResource.readImage("Items.Tree.Product")), BorderLayout.WEST);
        pPnl.add(productTf, BorderLayout.CENTER);
        tPnl.add(new ILabel(imageResource.readImage("Items.Tree.Type")), BorderLayout.WEST);
        tPnl.add(typeTf, BorderLayout.CENTER);

        divisionPnl.add(cPnl);
        divisionPnl.add(pPnl);
        divisionPnl.add(tPnl);

        return divisionPnl;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        buttonsPanel.add(dataSheetBtn, gbc);

        gbc.gridy++;
        buttonsPanel.add(orderBtn, gbc);

        gbc.gridy++;
        buttonsPanel.add(historyBtn, gbc);

        return buttonsPanel;
    }

    private JPanel createRemarksPanel() {
        remarksPnl = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel(new BorderLayout());

        northPanel.add(starRater, BorderLayout.WEST);
        northPanel.add(discourageOrderCb, BorderLayout.EAST);

        remarksPnl.add(northPanel, BorderLayout.NORTH);
        remarksPnl.add(new JScrollPane(remarksTp), BorderLayout.CENTER);
        remarksPnl.setBorder(BorderFactory.createEmptyBorder(5,10,2,10));

        return remarksPnl;

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


        nameTf = new ITextField(false);
        manufacturerTf = new ITextField(false);
        footprintTf = new ITextField(false);
        locationTf = new ITextField(false);
        categoryTf = new ITextField(false);
        productTf = new ITextField(false);
        typeTf = new ITextField(false);

        descriptionTa = new ITextField(false);
//        descriptionTa.setBorder(nameTf.getBorder());
//        descriptionTa.setEnabled(false);
//        descriptionTa.setLineWrap(true);
//        descriptionTa.setWrapStyleWord(true);

        starRater = new IStarRater(5);
        starRater.setEnabled(false);
        discourageOrderCb = new ICheckBox("Discourage order ");
        discourageOrderCb.setEnabled(false);
        discourageOrderCb.setHorizontalAlignment(SwingConstants.RIGHT);
        remarksTp = new ITextPane();
        remarksTp.setEditable(false);
        remarksTp.setEnabled(false);

        dataSheetBtn = new JButton(imageResource.readImage("Items.Buttons.Datasheet"));
        orderBtn = new JButton(imageResource.readImage("Items.Buttons.Order"));
        historyBtn = new JButton(imageResource.readImage("Items.Buttons.History"));

        dataSheetBtn.addActionListener(e -> detailListener.onShowDataSheet(selectedItem));
        orderBtn.addActionListener(e -> detailListener.onOrderItem(selectedItem));
        historyBtn.addActionListener(e -> detailListener.onShowHistory(selectedItem));

        dataSheetBtn.setToolTipText("Data sheets");
        orderBtn.setToolTipText("Order");
        historyBtn.setToolTipText("History");

        remarksPnl = new JPanel(new BorderLayout());
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel idPanel = createIdentificationPanel();
        JPanel infoPanel = createComponentInfoPanel();
        JPanel remarksPanel = createRemarksPanel();

        JPanel panel1 = new JPanel(new BorderLayout());
        JPanel panel2 = new JPanel(new BorderLayout());

        panel1.add(idPanel, BorderLayout.NORTH);
        panel1.add(infoPanel, BorderLayout.CENTER);

        panel2.add(panel1, BorderLayout.CENTER);
        panel2.add(remarksPanel, BorderLayout.EAST);

        add(createIconPanel(), BorderLayout.WEST);
        add(panel2, BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.EAST);
    }
}
