package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.interfaces.DbSettingsListener;
import com.waldo.inventory.database.settings.settingsclasses.DbSettingsObject;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.actions.IActions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;

public abstract class SettingsPnl<T extends DbSettingsObject> extends JPanel implements
        GuiUtils.GuiInterface,
        IEditedListener,
        DbSettingsListener<T>,
        ItemListener,
        IdBToolBar.IdbToolBarListener
{
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IdBToolBar toolBar;
    private ILabel currentSettingLbl;
    private IComboBox<T> settingsCb;
    private IActions.SaveAction saveAction;
    private IActions.UseAction useAction;
    JToolBar footerTb;

    final JPanel contentPanel = new JPanel();

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    T selectedSettings;
    T originalSettings;
    T currentSettings;

    protected final IDialog parent;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SettingsPnl(IDialog parent, T currentSettings) {
        this.parent = parent;
        this.currentSettings = currentSettings;
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected abstract List<T> getAllSettingsList();
    protected abstract T getSettingsByName(String name);
    protected abstract T createNew(String name);
    protected abstract T refreshSettings();

    protected boolean updateEnabledComponents() {
        boolean enabled = selectedSettings != null && !selectedSettings.isDefault();

        toolBar.setDeleteActionEnabled(enabled);
        toolBar.setEditActionEnabled(false);
        saveAction.setEnabled(selectedSettings != null && (!selectedSettings.isSaved() || !selectedSettings.equals(originalSettings)));
        useAction.setEnabled(selectedSettings != null && !selectedSettings.getName().equals(currentSettings.getName()));

        return enabled;
    }

    protected void updateFieldValues(T selectedSettings) {
        if (currentSettings != null) {
            currentSettingLbl.setText(currentSettings.getName());
        } else {
            currentSettingLbl.setText("");
        }
    }

    void cbSelectSettings(T settings) {
        settingsCb.setSelectedItem(settings);
    }

    private void cbAddSetting(T settings) {
        ComboBoxModel<T> m = settingsCb.getModel();
        if (m != null && m instanceof DefaultComboBoxModel) {
            DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) m;
            model.addElement(settings);
        }
    }

    private void addNewSettings() {
        String newName = JOptionPane.showInputDialog(
                this,
                "New settings name?");

        if (newName != null && !newName.isEmpty()) {
            addNewSettings(newName);
        }
    }

    private void addNewSettings(String newName) {
        if (getSettingsByName(newName) == null) {
            T t = createNew(newName);
            getAllSettingsList().add(t);
            cbAddSetting(t);
            updateComponents(t);
        } else {
            JOptionPane.showMessageDialog(
                    parent,
                    "Name " + newName + " already exists..",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void saveSettings(T toSave) {
        new SwingWorker<T, Object>() {
            @Override
            protected T doInBackground() throws Exception {
                parent.beginWait();
                try {
                    settings().saveSettings(toSave);
                } finally {
                    parent.endWait();
                }
                return toSave;
            }

            @Override
            protected void done() {
                try {
                    updateComponents(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void useSettings(T toUse) {
        new SwingWorker<T, Object>() {
            @Override
            protected T doInBackground() throws Exception {
                parent.beginWait();
                try {
                    settings().selectNewSettings(toUse);
                } finally {
                    parent.endWait();
                }
                return toUse;
            }

            @Override
            protected void done() {
                try {
                    currentSettings = get();
                    updateComponents(currentSettings);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void deleteSetting(T toDelete) {
        int res = JOptionPane.showConfirmDialog(parent,
                "Are you sure you want to delete " + toDelete.getName() + "?",
                "Delete setting",
                JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            new SwingWorker<T, Object>() {
                @Override
                protected T doInBackground() throws Exception {
                    parent.beginWait();
                    try {
                        settings().deleteSetting(toDelete);
                    } finally {
                        parent.endWait();
                    }
                    return toDelete;
                }

                @Override
                protected void done() {
                    try {
                        updateComponents();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Label
        currentSettingLbl = new ILabel();
        currentSettingLbl.setAlignmentX(CENTER_ALIGNMENT);
        currentSettingLbl.setForeground(Color.gray);
        Font f = currentSettingLbl.getFont();
        Font newFont = new Font(f.getName(), Font.BOLD, f.getSize() + 3);
        currentSettingLbl.setFont(newFont);

        // Combo box
        settingsCb = new IComboBox<>(getAllSettingsList(), null, false);
        settingsCb.addItemListener(this);
        settingsCb.setPreferredSize(new Dimension(120, 30));

        // Actions
        toolBar = new IdBToolBar(this, true, true, true, false);
        saveAction = new IActions.SaveAction(imageResource.readImage("Actions.M.Save")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings(selectedSettings);
            }
        };
        useAction = new IActions.UseAction(imageResource.readImage("Actions.M.Use")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedSettings.isSaved()) {
                    useSettings(selectedSettings);
                } else {
                    JOptionPane.showMessageDialog(
                            parent,
                            "Save settings first!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        footerTb = GuiUtils.createNewToolbar();
    }

    @Override
    public void initializeLayouts() {
        JPanel headerPnl = new JPanel(new BorderLayout());
        JPanel footerPnl = new JPanel(new BorderLayout());

        JPanel currentPanel = new JPanel(new BorderLayout());
        currentPanel.add(new ILabel("Current file setting: "), BorderLayout.NORTH);
        currentPanel.add(currentSettingLbl, BorderLayout.CENTER);
        currentPanel.setBorder(BorderFactory.createEmptyBorder(2, 15, 2, 15));

        headerPnl.add(settingsCb, BorderLayout.WEST);
        headerPnl.add(currentPanel, BorderLayout.CENTER);
        headerPnl.add(toolBar, BorderLayout.EAST);
        headerPnl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        footerTb.add(useAction);
        footerTb.add(saveAction);
        footerPnl.add(footerTb, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(headerPnl, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPnl, BorderLayout.SOUTH);
    }


    //
    // Edited listener
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        if (!parent.isUpdating()) {
            updateEnabledComponents();
        }
    }

    @Override
    public DbObject getGuiObject() {
        if (parent.isShowing() && !parent.isUpdating()) {
            return selectedSettings;
        }
        return null;
    }


    //
    // Settings changed
    //
    @Override
    public void onSettingsChanged(T newSettings) {
        if (!parent.isUpdating()) {
            updateComponents(newSettings);
        }
    }

    //
    // Combo box
    //
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            updateComponents(settingsCb.getSelectedItem());
        }
    }


    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        refreshSettings();
        updateComponents(selectedSettings);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        addNewSettings();
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedSettings != null) {
            if (selectedSettings.isDefault()) {
                JOptionPane.showMessageDialog(parent,
                        "Can't remove default settings..",
                        "Can not delete",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                deleteSetting(selectedSettings);
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {

    }
}
