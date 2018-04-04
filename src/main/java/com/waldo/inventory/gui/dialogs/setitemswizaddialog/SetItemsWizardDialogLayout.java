package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

abstract class SetItemsWizardDialogLayout extends IDialog {

    private static final String PANEL_ITEMS = "PnlItems";
    private static final String PANEL_LOCS = "PnlLocs";
    private static final String PANEL_PARSE = "PnlParse";

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ProgressPanel progressPanel;
    private JPanel cardsPnl;
    private CardLayout cardLayout;

    private WizardItemsPanel wizardItemsPanel;
    private WizardLocationsPanel wizardLocationsPanel;
    private WizardParsePanel wizardParsePanel;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     private final Set selectedSet;
     private final WizardSettings wizardSettings;
     private String currentTab = PANEL_ITEMS;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SetItemsWizardDialogLayout(Application application, String title, @NotNull Set set) {
        super(application, title);
        this.selectedSet = set;
        this.wizardSettings = new WizardSettings(set);
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void next() {
        switch (currentTab) {
            case PANEL_ITEMS:
                // Update
                wizardItemsPanel.updateSettings(wizardSettings);

                // Gui
                progressPanel.updateComponents(ProgressPanel.Progress.LocationsBusy);
                currentTab = PANEL_LOCS;
                getButtonNeutral().setEnabled(true);
                getButtonOK().setText("Next");
                cardLayout.next(cardsPnl);

                wizardLocationsPanel.updateComponents();
                break;
            case PANEL_LOCS:
                // Update
                wizardLocationsPanel.updateSettings(wizardSettings);

                // Gui
                progressPanel.updateComponents(ProgressPanel.Progress.ParseBusy);
                currentTab = PANEL_PARSE;
                getButtonNeutral().setEnabled(true);
                getButtonOK().setText("Ok");
                cardLayout.next(cardsPnl);

                wizardParsePanel.updateComponents(wizardSettings);
                break;
            case PANEL_PARSE:
                break;
        }
    }

    void previous() {
        switch (currentTab) {
            case PANEL_ITEMS:
                break;
            case PANEL_LOCS:
                progressPanel.updateComponents(ProgressPanel.Progress.ItemsBusy);
                currentTab = PANEL_ITEMS;
                getButtonNeutral().setEnabled(false);
                getButtonOK().setText("Next");
                cardLayout.previous(cardsPnl);
                break;
            case PANEL_PARSE:
                progressPanel.updateComponents(ProgressPanel.Progress.LocationsBusy);
                currentTab = PANEL_LOCS;
                getButtonNeutral().setEnabled(true);
                getButtonOK().setText("Next");
                cardLayout.previous(cardsPnl);
                break;
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readIcon("SetItem.Series.Title"));
        getButtonOK().setText("Next");
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setEnabled(false);
        getButtonNeutral().setText("Back");
        setResizable(true);

        // This
        progressPanel = new ProgressPanel();
        cardLayout = new CardLayout();
        cardsPnl = new JPanel(cardLayout);

        wizardItemsPanel = new WizardItemsPanel(this, selectedSet);
        wizardLocationsPanel = new WizardLocationsPanel(this, selectedSet.getLocation());
        wizardParsePanel = new WizardParsePanel(this);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        cardsPnl.add(PANEL_ITEMS, wizardItemsPanel);
        cardsPnl.add(PANEL_LOCS, wizardLocationsPanel);
        cardsPnl.add(PANEL_PARSE, wizardParsePanel);

        cardLayout.show(cardsPnl, PANEL_ITEMS);
        getContentPanel().add(progressPanel, BorderLayout.PAGE_START);
        getContentPanel().add(cardsPnl, BorderLayout.CENTER);
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        pack();
    }

    @Override
    public void updateComponents(Object... args) {

    }
}