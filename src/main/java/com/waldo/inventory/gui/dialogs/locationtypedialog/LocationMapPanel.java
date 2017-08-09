package com.waldo.inventory.gui.dialogs.locationtypedialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LocationMapPanel extends JPanel implements GuiInterface {

    public interface LocationClickListener {
        void onClick(int row, int column);
    }
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel buttonPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<JButton> buttonList = new ArrayList<>();
    private Application application;
    private LocationClickListener locationClickListener;

    private int rows = 0;
    private int columns = 0;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public LocationMapPanel(Application application, LocationClickListener locationClickListener) {
        this.application = application;
        this.locationClickListener = locationClickListener;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateButtonPanel(int rows, int columns) {
        buttonPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                String location = Statics.Alphabet[r] + String.valueOf(c);
                JButton button = new JButton(location);
                button.setOpaque(false);
                final int finalR = r;
                final int finalC = c;
                button.addActionListener(e -> locationClickListener.onClick(finalR, finalC));
                gbc.gridx = c;
                gbc.gridy = r;
                buttonPanel.add(button, gbc);
            }
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        add(new JScrollPane(buttonPanel), BorderLayout.CENTER);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 1),
                BorderFactory.createEmptyBorder(5,20,5,20)
        ));
    }

    @Override
    public void updateComponents(Object object) {
        int c = 0;
        int r = 0;
        if (object != null) {
            LocationType type = (LocationType) object;
            c = type.getColumns();
            r = type.getRows();
        }

        if (c != columns || r != rows) {
            columns = c;
            rows = r;
            updateButtonPanel(rows, columns);
        }

    }
}