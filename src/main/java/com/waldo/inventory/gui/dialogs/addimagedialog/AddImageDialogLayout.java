package com.waldo.inventory.gui.dialogs.addimagedialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

abstract class AddImageDialogLayout extends iDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel imageLbl;

    private IActions.PasteAction pasteAction;
    private IActions.BrowseFileAction browseFileAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ImageIcon imageIcon;
    File imageFile;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    AddImageDialogLayout(Window window) {
        super(window, "New image");

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract void onBrowseImage();
    abstract void onPasteImage();
    abstract void onMouseRightClicked(MouseEvent e);

    void setImage(ImageIcon icon) {
        if (icon != null) {
            imageLbl.setIcon(ImageResource.scaleImage(icon, new Dimension(200, 200)));
        } else {
            imageLbl.setIcon(null);
        }
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {

        getButtonNeutral().setEnabled(false);
        getButtonNeutral().setText("OK");
        showTitlePanel(false);

        // Image
        imageLbl = new ILabel();
        imageLbl.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        imageLbl.setHorizontalAlignment(SwingConstants.CENTER);
        imageLbl.setPreferredSize(new Dimension(200, 200));
        imageLbl.setMaximumSize(new Dimension(200, 200));
        imageLbl.setMinimumSize(new Dimension(200, 200));
        imageLbl.setBackground(Color.WHITE);
        imageLbl.setOpaque(true);

        imageLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    SwingUtilities.invokeLater(() -> onMouseRightClicked(e));
                }
            }
        });

        // Actions
        pasteAction = new IActions.PasteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onPasteImage());
            }
        };

        browseFileAction = new IActions.BrowseFileAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onBrowseImage());
            }
        };
    }

    @Override
    public void initializeLayouts() {

        JToolBar toolBar = GuiUtils.createNewToolbar(browseFileAction, pasteAction);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(imageLbl, BorderLayout.CENTER);
        panel.add(toolBar, BorderLayout.SOUTH);

        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(panel);


        pack();
    }

    @Override
    public void updateComponents(Object... args) {

    }
}