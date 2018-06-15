package com.waldo.inventory.gui.dialogs.editimagedialog;

import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ImagesPanel extends IPanel {

    interface ImageClickListener {
        void onImageClicked(MouseEvent e, DbImage image);
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel imagePanel;

    private ITextField imageNameTf;
    private ITextField imageTypeTf;
    private ITextField imageIdTf;
    private ITextField imageLocationTf;

    private IdBToolBar toolBar;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ImageType imageType;
    private ImageClickListener imageClickListener;
    private IdBToolBar.IdbToolBarListener toolBarListener;

    private ImageLabel selectedLabel;


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ImagesPanel(ImageType imageType, ImageClickListener imageClickListener, IdBToolBar.IdbToolBarListener toolBarListener) {

        this.imageType = imageType;
        this.imageClickListener = imageClickListener;
        this.toolBarListener = toolBarListener;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void updateEnabledComponents() {
        boolean selected = selectedLabel != null;

        toolBar.setDeleteActionEnabled(selected);
        toolBar.setEditActionEnabled(selected);
    }

    public void updateImages(List<DbImage> dbImageList) {
        imagePanel.removeAll();
        if (dbImageList != null) {
            for (DbImage image : dbImageList) {
                ImageLabel label = new ImageLabel(image);
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (imageClickListener != null) {
                            if (selectedLabel != null) {
                                selectedLabel.setSelected(false);
                            }
                            label.setSelected(true);
                            selectedLabel = label;
                            imageClickListener.onImageClicked(e, image);
                        }
                        setImageDetails(image);
                    }
                });
                imagePanel.add(label);
            }
        }
        //imagePanel.revalidate();
        //imagePanel.repaint();
    }

    public ImageType getImageType() {
        return imageType;
    }

    private void setImageDetails(DbImage image) {
        if (image != null) {
            imageNameTf.setText(image.getName());
            imageTypeTf.setText(image.getImageType().toString());
            imageIdTf.setText(String.valueOf(image.getId()));
            imageLocationTf.setText(image.getImageType().getFolderName());
        } else {
            imageNameTf.setText("");
            imageTypeTf.setText("");
            imageIdTf.setText("");
            imageLocationTf.setText("");
        }
    }

    private JPanel createImageDetailsPanel() {
        JPanel detailsPanel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel();

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(infoPanel);
        gbc.addLine("Name: ", imageNameTf);
        gbc.addLine("Id: ", imageIdTf);
        gbc.addLine("Type: ", imageTypeTf);
        gbc.addLine("Location: ", imageLocationTf);

        detailsPanel.add(toolBar, BorderLayout.NORTH);
        detailsPanel.add(infoPanel, BorderLayout.CENTER);

        return detailsPanel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        imagePanel = new JPanel(new GridLayout(0, 3));
        setPreferredSize(new Dimension(800, 600));
        //imagePanel = new JPanel(new FlowLayout());

        imageNameTf = new ITextField(false);
        imageTypeTf = new ITextField(false);
        imageIdTf = new ITextField(false);
        imageLocationTf = new ITextField(false);
        toolBar = new IdBToolBar(toolBarListener, true, true, true, false);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(imagePanel);
        scrollPane.setPreferredSize(new Dimension(600,400));
        scrollPane.getVerticalScrollBar().setUnitIncrement(32);

        add (createImageDetailsPanel(), BorderLayout.EAST);
        add (scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object ... args) {

    }
}
