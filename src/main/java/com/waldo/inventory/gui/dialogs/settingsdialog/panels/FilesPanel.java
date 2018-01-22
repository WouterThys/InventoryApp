package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.interfaces.DbSettingsListener;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.database.settings.settingsclasses.FileSettings;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.managers.LogManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.ExecutionException;

import static com.waldo.inventory.database.settings.SettingsManager.settings;

public class FilesPanel extends JPanel implements
        GuiInterface,
        IEditedListener,
        ItemListener,
        IdBToolBar.IdbToolBarListener,
        DbSettingsListener<FileSettings>,
        ActionListener {

    private static final LogManager LOG = LogManager.LOG(FilesPanel.class);
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IdBToolBar toolBar;
    private ILabel currentSettingLbl;

    private DefaultComboBoxModel<FileSettings> fileSettingsCbModel;
    private JComboBox<FileSettings> fileSettingsComboBox;

    private GuiUtils.IBrowseFilePanel distributorsPathPnl;
    private GuiUtils.IBrowseFilePanel divisionsPathPnl;
    private GuiUtils.IBrowseFilePanel idesPathPnl;
    private GuiUtils.IBrowseFilePanel itemsPathPnl;
    private GuiUtils.IBrowseFilePanel manufacturersPathPnl;
    private GuiUtils.IBrowseFilePanel projectsPathPnl;
    private GuiUtils.IBrowseFilePanel orderFilesPathPnl;

    private JButton saveBtn;
    private JButton useBtn;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private FileSettings selectedFileSettings;
    private FileSettings originalFileSettings;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public FilesPanel() {
        initializeComponents();
        initializeLayouts();

        settings().addFileSettingsListener(this);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        if (selectedFileSettings == null || selectedFileSettings.isDefault()) {
            toolBar.setDeleteActionEnabled(false);
            toolBar.setEditActionEnabled(false);
            distributorsPathPnl.setEnabled(false);
            divisionsPathPnl.setEnabled(false);
            idesPathPnl.setEnabled(false);
            itemsPathPnl.setEnabled(false);
            manufacturersPathPnl.setEnabled(false);
            projectsPathPnl.setEnabled(false);
            orderFilesPathPnl.setEnabled(false);
            saveBtn.setEnabled(false);
        } else {
            divisionsPathPnl.setEnabled(true);
            idesPathPnl.setEnabled(true);
            itemsPathPnl.setEnabled(true);
            manufacturersPathPnl.setEnabled(true);
            projectsPathPnl.setEnabled(true);
            orderFilesPathPnl.setEnabled(true);
            saveBtn.setEnabled(!selectedFileSettings.isSaved() || !selectedFileSettings.equals(originalFileSettings));
        }

        if (selectedFileSettings != null) {
            boolean isCurrent = selectedFileSettings.getName().equals(settings().getSelectedFileSettingsName());
            boolean isSaved = selectedFileSettings.isSaved();
            useBtn.setEnabled(!isCurrent && isSaved);
        }
    }

    private void updateFieldValues() {
        if (selectedFileSettings != null) {
            distributorsPathPnl.setText(selectedFileSettings.getImgDistributorsPath());
            divisionsPathPnl.setText(selectedFileSettings.getImgDivisionsPath());
            idesPathPnl.setText(selectedFileSettings.getImgIdesPath());
            itemsPathPnl.setText(selectedFileSettings.getImgItemsPath());
            manufacturersPathPnl.setText(selectedFileSettings.getImgManufacturersPath());
            projectsPathPnl.setText(selectedFileSettings.getImgProjectsPath());
            orderFilesPathPnl.setText(selectedFileSettings.getFileOrdersPath());

            currentSettingLbl.setText(settings().getSelectedFileSettingsName());
        }
    }

    private void addNewFileSettings() {
        String newName = JOptionPane.showInputDialog(
                FilesPanel.this,
                "New settings name?");

        if (newName != null && !newName.isEmpty()) {
            addNewFileSettings(newName);
        }
    }

    private void addNewFileSettings(String newName) {
        if (settings().getFileSettingsByName(newName) == null) {
            FileSettings fileSettings = new FileSettings(newName);
            settings().getFileSettingsList().add(fileSettings);
            updateComponents(fileSettings);
        } else {
            JOptionPane.showMessageDialog(
                    FilesPanel.this,
                    "Name " + newName + " already exists..",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void saveSettings(FileSettings toSave) {
        new SwingWorker<FileSettings, Object>() {
            @Override
            protected FileSettings doInBackground() throws Exception {
                Application.beginWait(FilesPanel.this);
                try {
                    settings().saveSettings(toSave);
                } finally {
                    Application.endWait(FilesPanel.this);
                }
                return toSave;
            }

            @Override
            protected void done() {
                try {
                    updateComponents(get());
                } catch (InterruptedException | ExecutionException e) {
                    LOG.error("Error updating components", e);
                }
            }
        }.execute();
    }

    private void useSettings(FileSettings toUse) {
        new SwingWorker<FileSettings, Object>() {
            @Override
            protected FileSettings doInBackground() throws Exception {
                Application.beginWait(FilesPanel.this);
                try {
                    settings().selectNewSettings(toUse);
                } finally {
                    Application.endWait(FilesPanel.this);
                }
                return settings().getFileSettings();
            }

            @Override
            protected void done() {
                try {
                    updateComponents(get());
                } catch (InterruptedException | ExecutionException e) {
                    LOG.error("Error updating components", e);
                }
            }
        }.execute();
    }

    private void deleteFileSetting( FileSettings toDelete) {
        int res = JOptionPane.showConfirmDialog(FilesPanel.this,
                "Are you sure you want to delete " + toDelete.getName() + "?",
                "Delete file setting",
                JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            new SwingWorker<FileSettings, Object>() {
                @Override
                protected FileSettings doInBackground() throws Exception {
                    Application.beginWait(FilesPanel.this);
                    try {
                        settings().deleteSetting(toDelete);
                    } finally {
                        Application.endWait(FilesPanel.this);
                    }
                    return settings().getFileSettings();
                }

                @Override
                protected void done() {
                    try {
                        updateComponents(get());
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.error("Error updating components", e);
                    }
                }
            }.execute();
        }
    }

    private void setDefaultPaths() {
        if (selectedFileSettings == null) {
            distributorsPathPnl.setDefaultPath("home/");
            divisionsPathPnl.setDefaultPath("home/");
            idesPathPnl.setDefaultPath("home/");
            itemsPathPnl.setDefaultPath("home/");
            manufacturersPathPnl.setDefaultPath("home/");
            projectsPathPnl.setDefaultPath("home/");
            orderFilesPathPnl.setDefaultPath("home/");
        } else {
            distributorsPathPnl.setDefaultPath(selectedFileSettings.getImgDistributorsPath());
            divisionsPathPnl.setDefaultPath(selectedFileSettings.getImgDivisionsPath());
            idesPathPnl.setDefaultPath(selectedFileSettings.getImgIdesPath());
            itemsPathPnl.setDefaultPath(selectedFileSettings.getImgItemsPath());
            manufacturersPathPnl.setDefaultPath(selectedFileSettings.getImgManufacturersPath());
            projectsPathPnl.setDefaultPath(selectedFileSettings.getImgProjectsPath());
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
        fileSettingsCbModel = new DefaultComboBoxModel<>();
        fileSettingsComboBox = new JComboBox<>(fileSettingsCbModel);
        fileSettingsComboBox.setAlignmentX(RIGHT_ALIGNMENT);
        fileSettingsComboBox.addItemListener(this);
        fileSettingsComboBox.setPreferredSize(new Dimension(120, 30));

        // Text fields
        distributorsPathPnl = new GuiUtils.IBrowseFilePanel();
        divisionsPathPnl = new GuiUtils.IBrowseFilePanel();
        idesPathPnl = new GuiUtils.IBrowseFilePanel();
        itemsPathPnl = new GuiUtils.IBrowseFilePanel();
        manufacturersPathPnl = new GuiUtils.IBrowseFilePanel();
        projectsPathPnl = new GuiUtils.IBrowseFilePanel();
        orderFilesPathPnl = new GuiUtils.IBrowseFilePanel();

        distributorsPathPnl.addFieldEditedListener(this, "imgDistributorsPath");
        divisionsPathPnl.addFieldEditedListener(this, "imgDivisionsPath");
        idesPathPnl.addFieldEditedListener(this, "imgIdesPath");
        itemsPathPnl.addFieldEditedListener(this, "imgItemsPath");
        manufacturersPathPnl.addFieldEditedListener(this, "imgManufacturersPath");
        projectsPathPnl.addFieldEditedListener(this, "imgProjectsPath");
        orderFilesPathPnl.addFieldEditedListener(this, "fileOrdersPath");

        // Buttons
        saveBtn = new JButton("Save");
        saveBtn.setEnabled(false);
        saveBtn.setAlignmentX(RIGHT_ALIGNMENT);
        saveBtn.addActionListener(this);

        useBtn = new JButton("Use this");
        useBtn.setEnabled(false);
        useBtn.setAlignmentX(LEFT_ALIGNMENT);
        useBtn.addActionListener(this);

        // Toolbar
        toolBar = new IdBToolBar(this);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(saveBtn);
        buttonsPanel.add(useBtn);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel currentPanel = new JPanel(new BorderLayout());
        currentPanel.add(new ILabel("Current file setting: "), BorderLayout.NORTH);
        currentPanel.add(currentSettingLbl, BorderLayout.CENTER);
        currentPanel.setBorder(BorderFactory.createEmptyBorder(2, 15, 2, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(fileSettingsComboBox, BorderLayout.WEST);
        headerPanel.add(currentPanel, BorderLayout.CENTER);
        headerPanel.add(toolBar, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        // - Add to panel
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(settingsPanel, 160);

        gbc.addLine("Distributor images: ", distributorsPathPnl);
        gbc.addLine("Division images: ", divisionsPathPnl);
        gbc.addLine("IDE images: ", idesPathPnl);
        gbc.addLine("Item images: ", itemsPathPnl);
        gbc.addLine("Manufacturer images: ", manufacturersPathPnl);
        gbc.addLine("Project images: ", projectsPathPnl);
        gbc.addLine("Order file images: ", orderFilesPathPnl);

        TitledBorder titledBorder = BorderFactory.createTitledBorder("File options");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Add to panel
        add(headerPanel, BorderLayout.NORTH);
        add(settingsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object... object) {
        Application.beginWait(FilesPanel.this);
        try {
            fileSettingsCbModel.removeAllElements();
            for (FileSettings settings : settings().getFileSettingsList()) {
                fileSettingsCbModel.addElement(settings);
            }

            selectedFileSettings = ((SettingsManager) object[0]).getFileSettings();

            if (selectedFileSettings != null) {
                fileSettingsComboBox.setSelectedItem(selectedFileSettings);
                originalFileSettings = selectedFileSettings.createCopy();
                updateFieldValues();
            } else {
                originalFileSettings = null;
            }
            setDefaultPaths();
            updateEnabledComponents();
        } finally {
            Application.endWait(FilesPanel.this);
        }
    }

    //
    // ITextFields changed
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        if (!Application.isUpdating(FilesPanel.this)) {
            updateEnabledComponents();
        }
    }

    @Override
    public DbObject getGuiObject() {
        if (!Application.isUpdating(FilesPanel.this)) {
            return selectedFileSettings;
        }
        return null;
    }

    //
    // File settings changed
    //
    @Override
    public void onSettingsChanged(FileSettings newSettings) {
        if (!Application.isUpdating(FilesPanel.this)) {
            updateComponents(newSettings);
        }
    }

    //
    // Toolbar listener
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        settings().updateFileSettings();
        settings().updateSelectedSettings();
        updateComponents(settings().getFileSettings());
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        addNewFileSettings();
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedFileSettings != null) {
            if (selectedFileSettings.isDefault()) {
                JOptionPane.showMessageDialog(FilesPanel.this,
                        "Can't remove default settings..",
                        "Can not delete",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                deleteFileSetting(selectedFileSettings);
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {

    }

    //
    // Button clicked
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(saveBtn)) {
            // Save
            saveSettings(selectedFileSettings);
        } else if (source.equals(useBtn)) {
            // Use
            if (selectedFileSettings.isSaved()) {
                useSettings(selectedFileSettings);
            } else {
                JOptionPane.showMessageDialog(
                        FilesPanel.this,
                        "Save settings first!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    //
    // Combo box value changed
    //
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (!Application.isUpdating(FilesPanel.this)) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object o = e.getItem();
                if (o instanceof FileSettings) {
                    updateComponents(o);
                }
            }
        }
    }
}
    