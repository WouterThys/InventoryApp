package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ILocationMapPanel extends JPanel implements GuiUtils.GuiInterface {

    public interface LocationClickListener {
        void onLocationClicked(ActionEvent e, Location location);
    }

    public static final Color GREEN = new Color(19,182,46);
    public static final Color YELLOW = new Color(250,244,70);
    public static final Color BLUE = Color.BLUE;
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel buttonPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<ILocationButton> buttonList = new ArrayList<>();
    private final Window parent;
    private final LocationClickListener locationClickListener;
    private final boolean showPopup;


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ILocationMapPanel(Window parent, List<Location> locations, LocationClickListener locationClickListener, boolean showPopup) {
        this.parent = parent;
        this.locationClickListener = locationClickListener;
        this.showPopup = showPopup;

        initializeComponents();
        initializeLayouts();
        createButtonsFromLocations(locations, showPopup);
        updateComponents();
    }

    public ILocationMapPanel(Window parent, LocationClickListener locationClickListener, boolean showPopup) {
        this.parent = parent;
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

    private void createButtonsFromLocations(List<Location> locationList, boolean showPopup) {
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
                                button.showPopup(e, parent);
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
                locationClickListener.onLocationClicked(e, button.getTheLocation());
            }
        });
    }

    public void clear() {
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

    private void updateLocations(List<ILocationButton> locations, String input) {
        String rows[] = input.split("\\r?\\n");

        if (rows.length == locations.size()) {
            for (int i=0; i < rows.length; i++) {
                try {
                    String row = rows[i];
                    Location loc = locations.get(i).getTheLocation();

                    row = row.replace(" ", "");

                    String[] params = valueBetween(row, "(", ")").split(",");
                    int c = Integer.valueOf(params[0]); // X = column
                    int r = Integer.valueOf(params[1]); // Y = row
                    int w = Integer.valueOf(params[2]); // Width
                    int h = Integer.valueOf(params[3]); // Height
                    int wx = 0;
                    int wy = 0;

                    if (params.length > 4) {
                        wx = Integer.valueOf(params[4]);
                    }
                    if (params.length > 5) {
                        wy = Integer.valueOf(params[5]);
                    }


                    loc.setLayout(c, r, w, h, wx, wy);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String valueBetween(String s, String first, String last) {
        s = s.substring(s.indexOf(first) + 1);
        s = s.substring(0, s.indexOf(last));

        return s;
    }



    private void drawButtons(List<ILocationButton> locationButtons) {
        if (locationButtons != null) {
            buttonPanel.removeAll();

            if (!buttonList.equals(locationButtons)) {
                buttonList = locationButtons;
            }

            boolean hasDefinition = false;
            if (locationButtons.size() > 0) {
                hasDefinition = locationButtons.get(0).getTheLocation().getLocationType().hasLayoutDefinition();
            }

            if (hasDefinition) {
                updateLocations(locationButtons, locationButtons.get(0).getTheLocation().getLocationType().getLayoutDefinition());
                buttonPanel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.BOTH;

//                gbc.gridx = 0; gbc.gridy = 0;
//                gbc.weightx = 0; gbc.weighty = 0;
//                gbc.gridwidth = 1; gbc.gridheight = 2;
//                buttonPanel.add(new JButton("A0"), gbc);
//
//                gbc.gridx = 1; gbc.gridy = 0;
//                gbc.weightx = 0; gbc.weighty = 0;
//                gbc.gridwidth = 1; gbc.gridheight = 2;
//                buttonPanel.add(new JButton("A1"), gbc);
//
//                gbc.gridx = 2; gbc.gridy = 0;
//                gbc.weightx = 1; gbc.weighty = 0;
//                gbc.gridwidth = 2; gbc.gridheight = 2;
//                buttonPanel.add(new JButton("A2"), gbc);
//
//                gbc.gridx = 0; gbc.gridy = 2;
//                gbc.weightx = 1; gbc.weighty = 0;
//                gbc.gridwidth = 3; gbc.gridheight = 2;
//                buttonPanel.add(new JButton("A3"), gbc);
//
//                gbc.gridx = 3; gbc.gridy = 2;
//                gbc.weightx = 0; gbc.weighty = 0;
//                gbc.gridwidth = 1; gbc.gridheight = 2;
//                buttonPanel.add(new JButton("A4"), gbc);

                for (ILocationButton btn : locationButtons) {
                    Location.LocationLayout layout = btn.getBtnLayout();

                    gbc.gridx = layout.x;
                    gbc.gridy = layout.y;
                    gbc.gridwidth = layout.w;
                    gbc.gridheight = layout.h;
                    gbc.weightx = layout.wx;
                    gbc.weighty = layout.wy;

                    buttonPanel.add(btn, gbc);
                }
            } else {
                GridBagConstraints btnGbc = new GridBagConstraints();
                btnGbc.insets = new Insets(2, 2, 2, 2);
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
            }

            buttonPanel.revalidate();
            buttonPanel.repaint();
        }
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
        setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
    }

    @Override
    public void updateComponents(Object... object) {
        drawButtons();
    }
}