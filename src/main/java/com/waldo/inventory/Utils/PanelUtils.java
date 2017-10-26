package com.waldo.inventory.Utils;

import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITextFieldButtonPanel;
import com.waldo.inventory.gui.dialogs.filechooserdialog.ImageFileChooser;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import static com.waldo.inventory.gui.Application.imageResource;
import static java.awt.GridBagConstraints.BOTH;

public class PanelUtils {

    public static GridBagConstraints createFieldConstraints(int gridLocX, int gridLocY) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridLocX;
        constraints.gridy = gridLocY;
        constraints.weightx = 3;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(2,2,2,2);
        constraints.fill = BOTH;
        return constraints;
    }

    public static JPanel createFileOpenPanel(ITextField fileTf, JButton openBtn) {
        JPanel iconPathPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = createFieldConstraints(0,0);
        constraints.gridwidth = 1;
        iconPathPanel.add(fileTf, constraints);
        constraints = createFieldConstraints(1,0);
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        iconPathPanel.add(openBtn, constraints);
        return iconPathPanel;
    }

    public static JPanel createComboBoxWithButton(JComboBox comboBox, ActionListener listener) {
        JPanel boxPanel = new JPanel(new BorderLayout());
        JButton button = new JButton(imageResource.readImage("Toolbar.AddIcon", 16));
        button.addActionListener(listener);
        boxPanel.add(comboBox, BorderLayout.CENTER);
        boxPanel.add(button, BorderLayout.EAST);
        return boxPanel;
    }

    public static TitledBorder createTitleBorder(String name) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(name);
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);
        return titledBorder;
    }

    public static TitledBorder createTitleBorder(ImageIcon icon) {
        return new TitledBorder("") {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
            {
                super.paintBorder(c, g, x, y, width, height);

                // Now use the graphics context to draw whatever needed
                g.drawImage(icon.getImage(), 0, 0, icon.getIconWidth(), icon.getIconHeight(), (img, infoflags, x1, y1, width1, height1) -> true);
            }
        };
    }



    public static class GridBagHelper extends GridBagConstraints {

        private JPanel panel;

        public GridBagHelper(JPanel panel) {
            this.panel = panel;
            this.panel.setLayout(new GridBagLayout());

            insets = new Insets(2,2,2,2);
            anchor = GridBagConstraints.EAST;

            gridx = 0;
            gridy = 0;

            panel.setOpaque(false);
        }

        public void addLineVertical(String labelText, JComponent component) {
            addLineVertical(labelText, component, GridBagConstraints.HORIZONTAL);
        }

        public void addLineVertical(String labelText, JComponent component, int fill) {
            int oldGw = gridwidth;
            int oldGh = gridheight;
            int oldAnchor = anchor;

            weightx = 0; weighty = 0;
            gridwidth = 1; anchor = GridBagConstraints.WEST;
            this.fill = GridBagConstraints.NONE;
            panel.add(new ILabel(labelText, ILabel.LEFT), this);

            gridwidth = oldGw;
            gridheight = oldGh;
            gridy += 1; weightx = 1;
            this.fill = fill;
            if (component != null) {
                panel.add(component, this);
            }

            anchor = oldAnchor;
            gridy += 1;
        }

        public void addLine(String labelText, JComponent component) {
            addLine(labelText, component, GridBagConstraints.HORIZONTAL);
        }

        public void addLine(String labelText, JComponent component, int fill) {
            int oldGw = gridwidth;
            int oldGh = gridheight;

            weightx = 0; weighty = 0;
            gridwidth = 1;
            this.fill = GridBagConstraints.NONE;
            panel.add(new ILabel(labelText, ILabel.RIGHT), this);

            gridwidth = oldGw;
            gridheight = oldGh;
            gridx = 1; weightx = 1;
            this.fill = fill;
            if (component != null) {
                panel.add(component, this);
            }


            gridx = 0; gridy++;
        }

        public void add(JComponent component, int x, int y) {
            add(component, x, y, 1, weighty);
        }

        public void add(JComponent component, int x, int y, double weightX, double weightY) {
            int oldX = gridx;
            int oldY = gridy;
            double oldWeightX = weightx;
            double oldWeightY = weighty;

            gridx = x; weightx = weightX;
            gridy = y; weighty = weightY;
            panel.add(component, this);

            gridx = oldX; weightx = oldWeightX;
            gridy = oldY; weighty = oldWeightY;

            gridx = 0;
            gridy++;
        }



        public JPanel getPanel() {
            return panel;
        }

        public void setPanel(JPanel panel) {
            this.panel = panel;
        }
    }

    public static class IBrowseFilePanel extends ITextFieldButtonPanel implements ActionListener {

        private String defaultPath = "";
        private int fileType = JFileChooser.DIRECTORIES_ONLY;

        public IBrowseFilePanel() {
            super("", imageResource.readImage("Common.FileBrowse", 20));
            this.defaultPath = "";
            addButtonActionListener(this);
        }

        public IBrowseFilePanel(String hint, String defaultPath) {
            super(hint, imageResource.readImage("Common.FileBrowse", 20));
            this.defaultPath = defaultPath;
            addButtonActionListener(this);
        }

        public IBrowseFilePanel(String hint, String defaultPath, IEditedListener listener, String fieldName) {
            super(hint, fieldName, listener, imageResource.readImage("Common.FileBrowse", 20));
            this.defaultPath = defaultPath;
            addButtonActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();

            File openFile;
            if (getText().isEmpty()) {
                openFile = new File(defaultPath);
            } else {
                openFile = new File(getText());
                if (openFile.exists()) {
                    if (openFile.isFile()) {
                        openFile = openFile.getParentFile();
                    }
                } else {
                    openFile = new File(defaultPath);
                }
            }

            fileChooser.setCurrentDirectory(openFile);
            fileChooser.setFileSelectionMode(fileType);

            if (fileChooser.showDialog(IBrowseFilePanel.this, "Open") == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                textField.fireValueChanged();
            }
        }

        public void setError(String error) {
            textField.setError(error);
        }

        public void setWarning(String warning) {
            textField.setWarning(warning);
        }

        public void setFileType(int fileType) {
            this.fileType = fileType;
        }

        public void setDefaultPath(String defaultPath) {
            this.defaultPath = defaultPath;
        }

        public void setEditable(boolean editable) {
            textField.setEditable(editable);
        }
    }

    public static class IBrowseWebPanel extends ITextFieldButtonPanel implements ActionListener {

        public IBrowseWebPanel(String hint, String fieldName, IEditedListener editedListener) {
            super(hint, fieldName, editedListener, imageResource.readImage("Common.WebBrowse", 20));

            addButtonActionListener(this);
            setButtonToolTip();
            setTextFieldToolTip();
        }

        private void setButtonToolTip() {
            String tooltip = "Browse ";
            if (!hint.isEmpty() && getText().isEmpty()) {
                String firstChar = String.valueOf(hint.charAt(0));
                if (firstChar.equals(firstChar.toUpperCase())) {
                    tooltip += firstChar.toLowerCase() + hint.substring(1, hint.length());
                }
            } else {
                tooltip += getText();
            }
            button.setToolTipText(tooltip);
        }

        private void setTextFieldToolTip() {
            String tooltip = null;
            if (!getText().isEmpty()) {
                tooltip = getText();
            }
            textField.setToolTipText(tooltip);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!getText().isEmpty()) {
                try {
                    OpenUtils.browseLink(getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(
                            IBrowseWebPanel.this,
                            "Unable to browse: " + getText(),
                            "Browse error",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        }

        @Override
        public void setText(String text) {
            super.setText(text);
            setButtonToolTip();
            setTextFieldToolTip();
        }
    }

    public static class IBrowseImagePanel extends ITextFieldButtonPanel implements ActionListener {

        private String defaultPath = "";

        public IBrowseImagePanel(String defaultPath, IEditedListener listener, String fieldName) {
            super("", fieldName, listener, imageResource.readImage("Common.FileBrowse", 20));
            this.defaultPath = defaultPath;
            addButtonActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = ImageFileChooser.getFileChooser();
            fileChooser.setCurrentDirectory(new File(defaultPath));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            File openFile;
            if (getText().isEmpty()) {
                openFile = new File(defaultPath);
            } else {
                openFile = new File(getText());
                if (openFile.exists()) {
                    if (openFile.isFile()) {
                        openFile = openFile.getParentFile();
                    }
                } else {
                    openFile = new File(defaultPath);
                }
            }

            if (fileChooser.showDialog(IBrowseImagePanel.this, "Open") == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getName());
                textField.fireValueChanged();
            }
        }

        public void setDefaultPath(String defaultPath) {
            this.defaultPath = defaultPath;
        }

        public void setEditable(boolean editable) {
            textField.setEditable(editable);
        }
    }

    public static class IValuePanel extends JPanel implements GuiInterface {

        private SpinnerNumberModel valueModel;
        private JSpinner valueSp;

        private JComboBox<String> multiplierCb;
        private JComboBox<String> unitCb;

        public IValuePanel() {
            super();

            initializeComponents();
            initializeLayouts();
            updateComponents();
        }

        @Override
        public void initializeComponents() {

        }

        @Override
        public void initializeLayouts() {

        }

        @Override
        public void updateComponents(Object... object) {

        }
    }
}
