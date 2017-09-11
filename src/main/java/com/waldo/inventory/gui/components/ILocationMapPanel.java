package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.classes.SetItem;
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

public class ILocationMapPanel extends JPanel implements GuiInterface, ILocationCustomDialog.LocationMapToolbarListener {

    public interface LocationClickListener {
        void onClick(ActionEvent e, List<DbObject> items, int row, int column);
    }

    public static final Color GREEN = new Color(19,182,46);
    public static final Color YELLOW = new Color(250,244,70);
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel buttonPanel;
    private JButton customizeBtn;

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
    private void createInitialPanel(int rows, int columns) {
        buttonPanel.removeAll();
        buttonList.clear();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                ILocationButton button = new ILocationButton(r, c);
                final int finalR = r;
                final int finalC = c;
                button.addActionListener(e -> {
                    if (locationClickListener != null) {
                        locationClickListener.onClick(e, button.getItems(), finalR, finalC);
                    }
                });

                gbc.gridx = c;
                gbc.gridy = r;
                buttonPanel.add(button, gbc);
                buttonList.add(button);

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        button.showPopup(e);
                    }
                });
            }
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void drawButtons(List<ILocationButton> locationButtons) {
        buttonPanel.removeAll();

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

    private List<ILocationButton> locationButtonsForRow(int row, List<ILocationButton> locationButtons) {
        List<ILocationButton> buttons = new ArrayList<>();
            for (ILocationButton btn : locationButtons) {
                if (btn.getRow() == row) {
                    buttons.add(btn);
                }
            }

        return buttons;
    }

//    public void drawButtons(List<ILocationButton> locationButtons) {
//        buttonPanel.removeAll();
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(2,2,2,2);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        for (ILocationButton lb : locationButtons) {
//            gbc.gridx = lb.getCol();
//            gbc.gridy = lb.getRow();
//            gbc.gridwidth = lb.getW();
//            gbc.gridheight = lb.getH();
//            buttonPanel.add(lb, gbc);
//        }
//
//        buttonPanel.revalidate();
//        buttonPanel.repaint();
//    }

    private ILocationButton findButton(int row, int col) {
        for (ILocationButton button : buttonList) {
            if (button.getRow() == row && button.getCol() == col) {
                return button;
            }
        }
        return null;
    }

    public void setItems(List<Item> items) {
        for(Item item : items) {
            if (item.isSet()) {
                for (SetItem setItem : SearchManager.sm().findSetItemsByItemId(item.getId())) {
                    if (setItem.getLocationId() > DbObject.UNKNOWN_ID) {
                        ILocationButton btn = setHighlighted(
                                setItem.getLocation().getRow(),
                                setItem.getLocation().getCol(),
                                YELLOW);
                        if (btn != null) {
                            btn.addItem(application, setItem);
                        }
                    }
                }
            } else {
                if (item.getLocation() != null) {
                    ILocationButton btn = setHighlighted(item.getLocation().getRow(), item.getLocation().getCol(), YELLOW);
                    if (btn != null) {
                        btn.addItem(application, item);
                    }
                }
            }
        }
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

    private void setHighlighted(ILocationButton button, Color color) {
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

    public void enableAllButtons(boolean enabled) {
        for (ILocationButton button : buttonList) {
            button.setEnabled(enabled);
        }
    }

    public ILocationButton buttonLeftOf(ILocationButton button) {
        if (button.getCol() > 0) {
            return findButton(button.getRow(), button.getCol()-1);
        }
        return null;
    }

    public ILocationButton buttonRightOf(ILocationButton button) {
        if (button.getCol() + button.getW() < locationType.getColumns()) {
            return findButton(button.getRow(), button.getCol()+button.getW());
        }
        return null;
    }

    public ILocationButton buttonUpOf(ILocationButton button) {
        if (button.getRow() > 0) {
            return findButton(button.getRow() - 1, button.getCol());
        }
        return null;
    }

    public ILocationButton buttonDownOf(ILocationButton button) {
        if (button.getCol() < locationType.getRows()-1) {
            return findButton(button.getRow() + 1, button.getCol());
        }
        return null;
    }

    public int setLocationButtonWidth(int width) {
        if (locBtn != null) {
            if (locBtn.getW() != width) {
                if (width > locBtn.getW()) {
                    while (width > locBtn.getW()) {
                        ILocationButton rightBtn = buttonRightOf(locBtn);
                        if (rightBtn != null) {
                            buttonList.remove(rightBtn);
                            locBtn.setW(locBtn.getW() + 1);
                        } else {
                            break;
                        }
                    }
                    drawButtons(buttonList);
                } else {
                    while (locBtn.getW() != width && locBtn.getW() > 0) {

                        ILocationButton fillBtn = new ILocationButton(locBtn.getRow(), locBtn.getW() + locBtn.getCol() - 1);
                        fillBtn.setEnabled(false);
                        buttonList.add(fillBtn);

                        locBtn.setW(locBtn.getW() - 1);
                    }
                    drawButtons(buttonList);
                }
            }
            return locBtn.getW();
        }
        return 0;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        customizeBtn = new JButton("Custom");
        customizeBtn.addActionListener(e -> {
            enableAllButtons(false);
            ILocationCustomDialog dialog = new ILocationCustomDialog(
                    application,
                    this,
                    0,
                    0,
                    locationType.getRows(),
                    locationType.getColumns());
            dialog.showDialog();
            enableAllButtons(true);
        });
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
                createInitialPanel(type.getRows(), type.getColumns());
            }

            locationType = type.createCopy();
        }
    }

    //
    // Custom tool bar
    //

    @Override
    public void onLocationChanged(int row, int col) {
        if (locBtn != null) {
            setHighlighted(locBtn, null);
            locBtn.setEnabled(false);
        }
        locBtn = findButton(row, col);
        if (locBtn != null) {
            locBtn.setEnabled(true);
            setHighlighted(locBtn, GREEN);
        }
    }

    @Override
    public int onRowChanged(int row) {
        return row;
    }

    @Override
    public int onColChanged(int col) {
        return col;
    }

    @Override
    public int onWidthChanged(int width) {
        return setLocationButtonWidth(width);
    }

    @Override
    public int onHeightChanged(int height) {
        return height;
    }
}