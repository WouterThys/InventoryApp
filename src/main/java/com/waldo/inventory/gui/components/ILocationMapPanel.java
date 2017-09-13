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

    private ILocationButton locBtn;
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
            for (Location location : locations) {
                ILocationButton button = new ILocationButton(location);
                final int finalR = location.getRow();
                final int finalC = location.getCol();
                addButtonActionListener(button, finalR, finalC);

                buttonList.add(button);

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        button.showPopup(e, application);
                    }
                });
            }
        }

        drawButtons(buttonList);
    }

    public void addButtonActionListener(ILocationButton button, final int r, final int c) {
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

    public void setHighlighted(Item item, Color color) {
        boolean isSet = item.isSet();
        boolean hasLocation = item.getLocationId() > DbObject.UNKNOWN_ID;
        boolean setHasLocations = false;
        if (isSet && hasLocation) {
            for (SetItem setItem : SearchManager.sm().findSetItemsByItemId(item.getId())) {
                if (setItem.getLocationId() > DbObject.UNKNOWN_ID) {
                    setHasLocations = true;
                    break;
                }
            }
        }

        if (!isSet && hasLocation) {
            setHighlighted(item.getLocationRow(), item.getLocationCol(), color);
        } else if (isSet && setHasLocations) {
            for (SetItem setItem : SearchManager.sm().findSetItemsByItemId(item.getId())) {
                if (setItem.getLocationId() > DbObject.UNKNOWN_ID) {
                    setHighlighted(setItem.getLocation().getRow(), setItem.getLocation().getCol(), color);
                }
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
}