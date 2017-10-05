package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.managers.LogManager;
import com.waldo.inventory.database.interfaces.DbSettingsListener;
import com.waldo.inventory.database.settings.settingsclasses.FileSettings;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.concurrent.ExecutionException;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;

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

    private ITextField distributorsPathTf;
    private ITextField divisionsPathTF;
    private ITextField idesPathTf;
    private ITextField itemsPathTf;
    private ITextField manufacturersPathTf;
    private ITextField projectsPathTf;
    private ITextField orderFilesPathTf;

    private JButton distributorsPathBtn;
    private JButton divisionsPathBtn;
    private JButton idesPathBtn;
    private JButton itemsPathBtn;
    private JButton manufacturersPathBtn;
    private JButton projectsPathBtn;
    private JButton orderFilesPathBtn;

    private JButton saveBtn;
    private JButton useBtn;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;

    private FileSettings selectedFileSettings;
    private FileSettings originalFileSettings;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public FilesPanel(Application application) {
        this.application = application;

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
            distributorsPathTf.setEnabled(false);
            distributorsPathBtn.setEnabled(false);
            divisionsPathTF.setEnabled(false);
            divisionsPathBtn.setEnabled(false);
            idesPathTf.setEnabled(false);
            idesPathBtn.setEnabled(false);
            itemsPathTf.setEnabled(false);
            itemsPathBtn.setEnabled(false);
            manufacturersPathTf.setEnabled(false);
            manufacturersPathBtn.setEnabled(false);
            projectsPathTf.setEnabled(false);
            projectsPathBtn.setEnabled(false);
            orderFilesPathTf.setEnabled(false);
            orderFilesPathBtn.setEnabled(false);
            saveBtn.setEnabled(false);
        } else {
            distributorsPathTf.setEnabled(true);
            distributorsPathBtn.setEnabled(true);
            divisionsPathTF.setEnabled(true);
            divisionsPathBtn.setEnabled(true);
            idesPathTf.setEnabled(true);
            idesPathBtn.setEnabled(true);
            itemsPathTf.setEnabled(true);
            itemsPathBtn.setEnabled(true);
            manufacturersPathTf.setEnabled(true);
            manufacturersPathBtn.setEnabled(true);
            projectsPathTf.setEnabled(true);
            projectsPathBtn.setEnabled(true);
            orderFilesPathTf.setEnabled(true);
            orderFilesPathBtn.setEnabled(true);
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
            distributorsPathTf.setText(selectedFileSettings.getImgDistributorsPath());
            divisionsPathTF.setText(selectedFileSettings.getImgDivisionsPath());
            idesPathTf.setText(selectedFileSettings.getImgIdesPath());
            itemsPathTf.setText(selectedFileSettings.getImgItemsPath());
            manufacturersPathTf.setText(selectedFileSettings.getImgManufacturersPath());
            projectsPathTf.setText(selectedFileSettings.getImgProjectsPath());
            orderFilesPathTf.setText(selectedFileSettings.getFileOrdersPath());

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

    private void browseFiles(ITextField textField, String initialPath) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(initialPath));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showDialog(FilesPanel.this, "Open") == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            textField.fireValueChanged();
        }
    }

    private void saveSettings(FileSettings toSave) {
        new SwingWorker<FileSettings, Object>() {
            @Override
            protected FileSettings doInBackground() throws Exception {
                application.beginWait();
                try {
                    settings().saveSettings(toSave);
                } finally {
                    application.endWait();
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
                application.beginWait();
                try {
                    settings().selectNewSettings(toUse);
                } finally {
                    application.endWait();
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
                    application.beginWait();
                    try {
                        settings().deleteSetting(toDelete);
                    } finally {
                        application.endWait();
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
        distributorsPathTf = new ITextField();
        divisionsPathTF = new ITextField();
        idesPathTf = new ITextField();
        itemsPathTf = new ITextField();
        manufacturersPathTf = new ITextField();
        projectsPathTf = new ITextField();
        orderFilesPathTf = new ITextField();

        distributorsPathTf.addEditedListener(this, "imgDistributorsPath");
        divisionsPathTF.addEditedListener(this, "imgDivisionsPath");
        idesPathTf.addEditedListener(this, "imgIdesPath");
        itemsPathTf.addEditedListener(this, "imgItemsPath");
        manufacturersPathTf.addEditedListener(this, "imgManufacturersPath");
        projectsPathTf.addEditedListener(this, "imgProjectsPath");
        orderFilesPathTf.addEditedListener(this, "fileOrdersPath");

        distributorsPathBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        divisionsPathBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        idesPathBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        itemsPathBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        manufacturersPathBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        projectsPathBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        orderFilesPathBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));

        distributorsPathBtn.addActionListener(this);
        divisionsPathBtn.addActionListener(this);
        idesPathBtn.addActionListener(this);
        itemsPathBtn.addActionListener(this);
        manufacturersPathBtn.addActionListener(this);
        projectsPathBtn.addActionListener(this);
        orderFilesPathBtn.addActionListener(this);

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
        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(settingsPanel);

        ITextField[] iTextFields = new ITextField[]{distributorsPathTf, divisionsPathTF, idesPathTf, itemsPathTf, manufacturersPathTf, projectsPathTf, orderFilesPathTf};
        String[] iLabels = new String[] {
                "Distributor images: ", "Division images: ", "Ide images: ", "Item images: ","Manufacturer images: ","Project images: ", "Order files: "
        };
        JButton[] jButtons = new JButton[]{distributorsPathBtn, divisionsPathBtn, idesPathBtn, itemsPathBtn, manufacturersPathBtn, projectsPathBtn, orderFilesPathBtn};

        for (int i = 0; i < iTextFields.length; i++) {
            JPanel panel = PanelUtils.createFileOpenPanel(iTextFields[i], jButtons[i]);
            gbc.addLine(iLabels[i], panel);
        }

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
    public void updateComponents(Object object) {
        application.beginWait();
        try {
            fileSettingsCbModel.removeAllElements();
            for (FileSettings settings : settings().getFileSettingsList()) {
                fileSettingsCbModel.addElement(settings);
            }

            selectedFileSettings = (FileSettings) object;

            if (selectedFileSettings != null) {
                fileSettingsComboBox.setSelectedItem(selectedFileSettings);
                originalFileSettings = selectedFileSettings.createCopy();
                updateFieldValues();
            } else {
                originalFileSettings = null;
            }
            updateEnabledComponents();
        } finally {
            application.endWait();
        }
    }

    //
    // ITextFields changed
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        if (!application.isUpdating()) {
            updateEnabledComponents();
        }
    }

    @Override
    public DbObject getGuiObject() {
        if (!application.isUpdating()) {
            return selectedFileSettings;
        }
        return null;
    }

    //
    // File settings changed
    //
    @Override
    public void onSettingsChanged(FileSettings newSettings) {
        if (!application.isUpdating()) {
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
        } else if (source.equals(distributorsPathBtn)) {
            if (selectedFileSettings == null) {
                browseFiles(distributorsPathTf, "home/");
            } else {
                browseFiles(distributorsPathTf, selectedFileSettings.getImgDistributorsPath());
            }
        } else if (source.equals(divisionsPathBtn)) {
            if (selectedFileSettings == null) {
                browseFiles(divisionsPathTF, "home/");
            } else {
                browseFiles(divisionsPathTF, selectedFileSettings.getImgDivisionsPath());
            }
        } else if (source.equals(idesPathBtn)) {
            if (selectedFileSettings == null) {
                browseFiles(idesPathTf, "home/");
            } else {
                browseFiles(idesPathTf, selectedFileSettings.getImgIdesPath());
            }
        } else if (source.equals(itemsPathBtn)) {
            if (selectedFileSettings == null) {
                browseFiles(itemsPathTf, "home/");
            } else {
                browseFiles(itemsPathTf, selectedFileSettings.getImgItemsPath());
            }
        } else if (source.equals(manufacturersPathBtn)) {
            if (selectedFileSettings == null) {
                browseFiles(manufacturersPathTf, "home/");
            } else {
                browseFiles(manufacturersPathTf, selectedFileSettings.getImgManufacturersPath());
            }
        } else if (source.equals(projectsPathBtn)) {
            if (selectedFileSettings == null) {
                browseFiles(projectsPathTf, "home/");
            } else {
                browseFiles(projectsPathTf, selectedFileSettings.getImgProjectsPath());
            }
        } else if (source.equals(orderFilesPathBtn)) {
            if (selectedFileSettings == null) {
                browseFiles(orderFilesPathTf, "home/");
            } else {
                browseFiles(orderFilesPathTf, selectedFileSettings.getFileOrdersPath());
            }
        }

    }

    //
    // Combobox value changed
    //
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (!application.isUpdating()) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object o = e.getItem();
                if (o instanceof FileSettings) {
                    updateComponents(o);
                }
            }
        }
    }
}
    