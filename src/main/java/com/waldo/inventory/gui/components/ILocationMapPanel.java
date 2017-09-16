package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ILocationMapPanel extends JPanel implements GuiInterface {

    public interface LocationClickListener {
        void onClick(ActionEvent e, Location location);
    }

    public static final Color GREEN = new Color(19,182,46);
    public static final Color YELLOW = new Color(250,244,70);
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel buttonPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<ILocationButton> buttonList = new ArrayList<>();
    private Application application;
    private LocationClickListener locationClickListener;
    private LocationType locationType;


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ILocationMapPanel(Application application, LocationClickListener locationClickListener) {
        this.application = application;
        this.locationClickListener = locationClickListener;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void createInitialPanel(LocationType locationType) {
        buttonList.clear();

        if (locationType != null) {
            List<Location> locations = SearchManager.sm().findLocationsByTypeId(locationType.getId());
            createButtonsFromLocations(locations);
        }

        drawButtons(buttonList);
    }

    public void createButtonsFromLocations(List<Location> locationList) {
        buttonList.clear();
        for (Location location : locationList) {
            ILocationButton button = new ILocationButton(location);
            addButtonActionListener(button);

            buttonList.add(button);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    button.showPopup(e, application);
                }
            });
        }
    }

    public void addButtonActionListener(ILocationButton button) {
        button.addActionListener(e -> {
            if (locationClickListener != null) {
                locationClickListener.onClick(e, button.getTheLocation());
            }
        });
    }

    public void clearButtons() {
        buttonList.clear();
        buttonPanel.removeAll();
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void updateButtons() {
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void drawButtons() {
        drawButtons(buttonList);
    }

    public void drawButtons(List<ILocationButton> locationButtons) {
        buttonPanel.removeAll();

        if (!buttonList.equals(locationButtons)) {
            buttonList = locationButtons;
        }

        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(2,2,2,2);
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.weightx = 1;
        btnGbc.gridy = 0;

        GridBagConstraints pnlGbc = new GridBagConstraints();
        pnlGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.gridx = 0;

        int r = 0;
        List<ILocationButton> temp = new ArrayList<>(locationButtons);
        while (temp.size() > 0) {
            List<ILocationButton> btns = locationButtonsForRow(r, temp);

            JPanel rowPanel = new JPanel(new GridBagLayout());
            for (ILocationButton btn : btns) {
                btnGbc.gridx = btn.getCol();
                rowPanel.add(btn, btnGbc);
            }
            pnlGbc.gridy = r;
            buttonPanel.add(rowPanel, pnlGbc);

            temp.removeAll(btns);
            r++;
        }


        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public List<ILocationButton> locationButtonsForRow(int row, List<ILocationButton> locationButtons) {
        List<ILocationButton> buttons = new ArrayList<>();
            for (ILocationButton btn : locationButtons) {
                if (btn.getRow() == row) {
                    buttons.add(btn);
                }
            }

        return buttons;
    }

    public ILocationButton findButton(int row, int col) {
        for (ILocationButton button : buttonList) {
            if (button.getRow() == row && button.getCol() == col) {
                return button;
            }
        }
        return null;
    }

    public void setHighlighted(Location location, Color color) {
        if (location != null) {
            ILocationButton button = findButton(location.getRow(), location.getCol());
            if (button != null) {
                button.setBackground(color);
            }
        } else {
            clearHighlights();
        }
    }

    public void clearHighlights() {
        for (ILocationButton button : getLocationButtons()) {
            button.setBackground(null);
        }
    }

    public void setLocationsWithItemHighlighted(Color color) {
        for (ILocationButton button : buttonList) {
            if (button.getTheLocation().hasItems()) {
                button.setBackground(color);
            } else {
                button.setBackground(null);
            }
        }
    }

    public ILocationButton setHighlighted(int row, int col, Color color) {
        ILocationButton button = null;
        if (row >= 0 && col >= 0) {
            button = findButton(row, col);
            if (button != null) {
                if (color != null) {
                    button.setBackground(color);
                } else {
                    if (button.getItems().size() > 0) {
                        button.setBackground(YELLOW);
                    } else {
                        button.setBackground(null);
                    }
                }
            }
        }
        return button;
    }

    public List<ILocationButton> getLocationButtons() {
        return buttonList;
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
        //add(customizeBtn, BorderLayout.SOUTH);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 1),
                BorderFactory.createEmptyBorder(5,20,5,20)
        ));
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            LocationType type = (LocationType) object;

            if (!type.equals(locationType)) {
                createInitialPanel(type);
            }

            locationType = type.createCopy();
        }
    }

    public void setLocationType(LocationType type) {
        locationType = type.createCopy();
    }
}