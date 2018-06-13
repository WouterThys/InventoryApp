package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.components.IDivisionPanel;
import com.waldo.inventory.gui.components.IImagePanel;
import com.waldo.inventory.gui.components.IRemarksPanel;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.allaliasesdialog.AllAliasesDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialogLayout;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;
import com.waldo.inventory.gui.dialogs.selectdivisiondialog.SelectDivisionDialog;
import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.NumberFormat;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class ComponentPanel<T extends Item> extends JPanel implements GuiUtils.GuiInterface {

    public static final int TAB_BASIC = 0;
    public static final int TAB_DETAILS = 1;

    private final Window parent;
    private final T selectedItem;

    // Listener
    private final IEditedListener editedListener;

    // Tabbed pane
    private JTabbedPane tabbedPane;

    // Basic info
    private GuiUtils.INameValuePanel nameValuePnl;
    private ITextFieldActionPanel aliasPnl;
    private ITextArea descriptionTa;
    private IDivisionPanel divisionPnl;
    private IActions.EditAction editDivisionAction;
    private GuiUtils.IBrowseFilePanel localDataSheetPnl;
    private GuiUtils.IBrowseWebPanel onlineDataSheetPnl;

    // Details
    private GuiUtils.IPackagePanel packagePnl;
    private IComboBox<Manufacturer> manufacturerCb;
    private IImagePanel manufacturerIconLbl;
    private IStarRater starRater;
    private IRemarksPanel remarksPnl;

    public ComponentPanel(Window parent, T selectedItem, @NotNull IEditedListener listener) {
        this.parent = parent;
        this.selectedItem = selectedItem;
        this.editedListener = listener;
    }

    /*
     *                  PUBLIC METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void setValuesForSet(Set set) {
        // DIVISION
        selectedItem.setDivisionId(set.getDivisionId());
        divisionPnl.updateComponents(selectedItem.getDivision());

        // DATA SHEETS
        selectedItem.setLocalDataSheet(set.getLocalDataSheet());
        localDataSheetPnl.setText(set.getLocalDataSheet());
        selectedItem.setOnlineDataSheet(set.getOnlineDataSheet());
        onlineDataSheetPnl.setText(set.getOnlineDataSheet());

        // PACKAGE
        selectedItem.setPackageTypeId(set.getPackageTypeId());
        selectedItem.setPins(set.getPins());
        packagePnl.setPackageType(set.getPackageType(), set.getPins());

        // MANUFACTURER
        if (set.getManufacturerId() > DbObject.UNKNOWN_ID) { // Edit
            selectedItem.setManufacturerId(set.getManufacturerId());
            updateManufacturerCb(set.getManufacturer());
        }

        // REMARKS
        selectedItem.setRating(set.getRating());
        starRater.setRating(set.getRating());
        starRater.setSelection(0);
    }

    public void setSelectedTab(int tab) {
        if (tabbedPane != null) {
            tabbedPane.setSelectedIndex(tab);
        }
    }

    private void updateManufacturerCbValues() {
        if (manufacturerCb != null) {
            manufacturerCb.updateList(cache().getManufacturers());
            manufacturerCb.setSelectedItem(selectedItem.getManufacturer());
        }
    }

    private void updateManufacturerCb(Manufacturer manufacturer) {
        manufacturerCb.setSelectedItem(manufacturer);

        if (manufacturer != null) {
            manufacturerIconLbl.setImage(manufacturer.getIconPath());
        } else {
            manufacturerIconLbl.setImage(imageResource.getDefaultImage(ImageType.ManufacturerImage));
        }
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void createManufacturerCb() {
        manufacturerCb = new IComboBox<>(cache().getManufacturers(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        manufacturerCb.setSelectedItem(selectedItem.getManufacturer());
        manufacturerCb.addEditedListener(editedListener, "manufacturerId");
    }

    private ActionListener createManufacturerAddListener() {
        return e -> {
            ManufacturersDialog manufacturersDialog = new ManufacturersDialog(parent);
            if (manufacturersDialog.showDialog() == ICacheDialog.OK) {
                updateManufacturerCbValues();
            }
        };
    }

    private void initializeBasicComponents() {
        // Identification
        nameValuePnl = new GuiUtils.INameValuePanel(editedListener, "name", editedListener);
        nameValuePnl.requestFocus();
        aliasPnl = new ITextFieldActionPanel("Alias", "alias", editedListener, new IActions.SearchAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AllAliasesDialog dialog = new AllAliasesDialog(parent, "Alias", aliasPnl.getText());
                if (dialog.showDialog() == ICacheDialog.OK) {
                    String selectedAlias = dialog.getSelectedAlias();
                    if (selectedAlias != null && !selectedAlias.isEmpty()) {
                        aliasPnl.setText(selectedAlias);
                        aliasPnl.fireValueChanged();
                    }
                }
            }
        });

        descriptionTa = new ITextArea();
        descriptionTa.setLineWrap(true); // Go to next line when area is full
        descriptionTa.setWrapStyleWord(true); // Don't cut words in two
        descriptionTa.addEditedListener(editedListener, "description");
        descriptionTa.setName(EditItemDialogLayout.COMP_DESCRIPTION);

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(Double.MIN_VALUE);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true); // Commit on every key press

        // Division
        divisionPnl = new IDivisionPanel();
        editDivisionAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectDivisionDialog dialog = new SelectDivisionDialog(parent, selectedItem.getDivision());
                if (dialog.showDialog() == ICacheDialog.OK) {
                    Division newDivision = dialog.getSelectedDivision();
                    if (newDivision != null && !newDivision.equals(selectedItem.getDivision())) {
                        selectedItem.setDivisionId(newDivision.getId());
                        divisionPnl.updateComponents(newDivision);
                        editedListener.onValueChanged(null, "divisionId", null, null);
                    }
                }
            }
        };

        // Data sheets
        localDataSheetPnl = new GuiUtils.IBrowseFilePanel("", "/home", editedListener, "localDataSheet");
        onlineDataSheetPnl = new GuiUtils.IBrowseWebPanel("","onlineDataSheet", editedListener);
    }

    private void initializeDetailsComponents() {
        // Package
        packagePnl = new GuiUtils.IPackagePanel(parent, editedListener, "packageTypeId", "pins");

        // Manufacturer
        createManufacturerCb();
        manufacturerCb.setName(EditItemDialogLayout.COMP_MANUFACTURER);
        manufacturerCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Manufacturer m = (Manufacturer) manufacturerCb.getSelectedItem();
                if (m != null) {
                    manufacturerIconLbl.setImage(m.getIconPath());
                } else {
                    manufacturerIconLbl.setImage(imageResource.getDefaultImage(ImageType.ManufacturerImage));
                }
            }
        });
        manufacturerIconLbl = new IImagePanel(ImageType.ManufacturerImage, new Dimension(48,48));

        // Remarks stuff
        starRater = new IStarRater(5, 0,0);
        starRater.addEditedListener(editedListener, "rating");
        starRater.setName(EditItemDialogLayout.COMP_RATING);

        remarksPnl = new IRemarksPanel(parent, newFile -> {
           selectedItem.setRemarksFile(newFile);
           editedListener.onValueChanged(remarksPnl, "remarksFile", null, null);
        });
        remarksPnl.setPreferredSize(new Dimension(400, 100));
    }

    private JPanel createBasicPanel() {
        JPanel basicPanel = new JPanel();
        basicPanel.setLayout(new BoxLayout(basicPanel, BoxLayout.Y_AXIS));

        GuiUtils.GridBagHelper gbc;

        JPanel idPnl = new JPanel();
        idPnl.setBorder(GuiUtils.createInlineTitleBorder("Identification"));
        gbc = new GuiUtils.GridBagHelper(idPnl);
        gbc.addLine("Name: ", nameValuePnl);
        gbc.addLine("Alias: ", aliasPnl);
        gbc.addLine("Description: ", new JScrollPane(descriptionTa));

        JPanel divPnl = new JPanel(new BorderLayout());
        divPnl.setBorder(GuiUtils.createInlineTitleBorder("Division"));
        divPnl.add(divisionPnl, BorderLayout.CENTER);
        divPnl.add(GuiUtils.createNewToolbar(editDivisionAction), BorderLayout.EAST);

        JPanel dsPnl = new JPanel();
        dsPnl.setBorder(GuiUtils.createInlineTitleBorder("Data sheets"));
        gbc = new GuiUtils.GridBagHelper(dsPnl);
        gbc.addLine("Local: ", localDataSheetPnl);
        gbc.addLine("Online: ", onlineDataSheetPnl);

        basicPanel.add(idPnl);
        basicPanel.add(divPnl);
        basicPanel.add(dsPnl);

        return basicPanel;
    }

    private JPanel createDetailsPanel() {
        JPanel packagePanel = new JPanel(new BorderLayout());
        JPanel manufacturerPanel = new JPanel(new GridBagLayout());
        JPanel remarksPanel = new JPanel(new BorderLayout());

        // Borders
        Border packageBorder = GuiUtils.createInlineTitleBorder("Package");
        Border manufacturerBorder = GuiUtils.createInlineTitleBorder("Manufacturer");
        Border remarksBorder = GuiUtils.createInlineTitleBorder("Remarks");

        packagePanel.setBorder(packageBorder);
        manufacturerPanel.setBorder(manufacturerBorder);
        remarksPanel.setBorder(remarksBorder);

        // PACKAGE
        packagePanel.add(packagePnl, BorderLayout.CENTER);

        GuiUtils.GridBagHelper gbc;

        // MANUFACTURER
        gbc = new GuiUtils.GridBagHelper(manufacturerPanel);
        gbc.addLine("Name: ", GuiUtils.createComponentWithAddAction(manufacturerCb, createManufacturerAddListener()));
        gbc.add(manufacturerIconLbl, 2,0,0,0);

        // REMARKS
        JPanel starPnl = new JPanel(new BorderLayout());
        starPnl.add(starRater, BorderLayout.CENTER);
        remarksPnl.addSomethingToHeader(starPnl);
        remarksPanel.add(remarksPnl, BorderLayout.CENTER);

        // Add to panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        Box box = Box.createVerticalBox();
        box.add(packagePanel);
        box.add(manufacturerPanel);

        panel.add(box, BorderLayout.NORTH);
        panel.add(remarksPanel, BorderLayout.CENTER);

        return panel;
    }


    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        initializeBasicComponents();
        initializeDetailsComponents();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel basicPnl = createBasicPanel();
        JPanel detailPnl = createDetailsPanel();

        JPanel mainPnl = new JPanel(new BorderLayout());
        mainPnl.add(basicPnl, BorderLayout.NORTH);
        mainPnl.add(detailPnl, BorderLayout.CENTER);

        add(mainPnl, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... object) {
        aliasPnl.setText(selectedItem.getAlias().trim());
        nameValuePnl.setNameTxt(selectedItem.getName().trim());
        if (selectedItem.getDivision() != null && selectedItem.getDivision().isCanHaveValue()) {
            nameValuePnl.setValue(selectedItem.getValue());
            nameValuePnl.displayValue(true);
            nameValuePnl.setValuePanelVisible(false);
        } else {
            nameValuePnl.displayValue(false);
        }

        descriptionTa.setText(selectedItem.getDescription().trim());

        // DIVISION
        divisionPnl.updateComponents(selectedItem.getDivision());

        // DATA SHEETS
        localDataSheetPnl.setText(selectedItem.getLocalDataSheet());
        onlineDataSheetPnl.setText(selectedItem.getOnlineDataSheet());

        // PACKAGE
        packagePnl.setPackageType(selectedItem.getPackageType(), selectedItem.getPins());

        // MANUFACTURER
        if (selectedItem.getManufacturerId() > DbObject.UNKNOWN_ID) { // Edit
            updateManufacturerCb(selectedItem.getManufacturer());
        }

        // REMARKS
        starRater.setRating(selectedItem.getRating());
        starRater.setSelection(0);
        remarksPnl.updateComponents(selectedItem.getRemarksFile());
    }

    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public String getNameFieldValue() {
        return nameValuePnl.getNameText();
    }

    public void setNameFieldError(String error) {
        nameValuePnl.setError(error);
    }

    public void updateRating(float rating) {
        starRater.setRating(rating);
        starRater.setSelection(0);
    }
}
