package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.Location;
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
    private boolean showPopup;


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ILocationMapPanel(Application application, List<Location> locations, LocationClickListener locationClickListener, boolean showPopup) {
        this.application = application;
        this.locationClickListener = locationClickListener;
        this.showPopup = showPopup;

        initializeComponents();
        initializeLayouts();
        createButtonsFromLocations(locations, showPopup);
        updateComponents();
    }

    public ILocationMapPanel(Application application, LocationClickListener locationClickListener, boolean showPopup) {
        this.application = application;
        this.locationClickListener = locationClickListener;
        this.showPopup = showPopup;
        createButtonsFromLocations(null, showPopup);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void setLocations(List<Location> locations) {
        createButtonsFromLocations(locations, showPopup);
        drawButtons();
    }

    public void createButtonsFromLocations(List<Location> locationList, boolean showPopup) {
        buttonList.clear();
        if (locationList != null) {
            for (Location location : locationList) {
                if (!location.isUnknown()) {
                    ILocationButton button = new ILocationButton(location);
                    addButtonActionListener(button);
                    if (showPopup) {
                        button.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                button.showPopup(e, application);
                            }
                        });
                    }
                    buttonList.add(button);
                }
            }
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

    private void drawButtons() {
        drawButtons(buttonList);
    }

    private void drawButtons(List<ILocationButton> locationButtons) {
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

//        setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(Color.gray, 1),
//                BorderFactory.createEmptyBorder(5,20,5,20)
//        ));
        setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
    }

    @Override
    public void updateComponents(Object... object) {
        drawButtons();
    }
}