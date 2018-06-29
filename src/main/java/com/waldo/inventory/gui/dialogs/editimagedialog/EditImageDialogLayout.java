package com.waldo.inventory.gui.dialogs.editimagedialog;

import com.waldo.inventory.Utils.Statics.ImageType;
import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.iDialog;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

abstract class EditImageDialogLayout extends iDialog implements ImagesPanel.ImageClickListener, IdBToolBar.IdbToolBarListener {

    private static final ImageIcon itemTypeIcon = ImageResource.scaleImage(imageResource.getDefaultImage(ImageType.ItemImage), new Dimension(16,16));
    private static final ImageIcon distributorTypeIcon = ImageResource.scaleImage(imageResource.getDefaultImage(ImageType.DistributorImage), new Dimension(16,16));
    private static final ImageIcon manufacturerTypeIcon = ImageResource.scaleImage(imageResource.getDefaultImage(ImageType.ManufacturerImage), new Dimension(16,16));
    private static final ImageIcon ideTypeIcon = ImageResource.scaleImage(imageResource.getDefaultImage(ImageType.IdeImage), new Dimension(16,16));
    private static final ImageIcon projectTypeIcon = ImageResource.scaleImage(imageResource.getDefaultImage(ImageType.ProjectImage), new Dimension(16,16));
    private static final ImageIcon otherTypeIcon = ImageResource.scaleImage(imageResource.getDefaultImage(ImageType.Other), new Dimension(16,16));

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JTabbedPane tabbedPane;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    DbImage selectedImage;
    ImageType imageType;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditImageDialogLayout(Window window, String title, ImageType imageType) {
        super(window, title);
        this.imageType = imageType;

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        ((ImagesPanel)tabbedPane.getSelectedComponent()).updateEnabledComponents();
    }



    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        setResizable(true);

        getButtonOK().setText("Select");
        getButtonOK().setEnabled(false);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(e -> {
            selectedImage = null;
            updateEnabledComponents();
        });

        if (imageType == null) {
            tabbedPane.addTab("Items ",
                    itemTypeIcon,
                    new ImagesPanel(ImageType.ItemImage, this, this));
            tabbedPane.addTab("Distributors ",
                    distributorTypeIcon,
                    new ImagesPanel(ImageType.DistributorImage, this, this));
            tabbedPane.addTab("Manufacturers ",
                    manufacturerTypeIcon,
                    new ImagesPanel(ImageType.ManufacturerImage, this, this));
            tabbedPane.addTab("IDE's ",
                    ideTypeIcon,
                    new ImagesPanel(ImageType.IdeImage, this, this));
            tabbedPane.addTab("Projects ",
                    projectTypeIcon,
                    new ImagesPanel(ImageType.ProjectImage, this, this));
            tabbedPane.addTab("Others ",
                    otherTypeIcon,
                    new ImagesPanel(ImageType.Other, this, this));
        } else {
            switch (imageType) {
                case ItemImage:
                    tabbedPane.addTab("Items ",
                            itemTypeIcon,
                            new ImagesPanel(ImageType.ItemImage, this, this));
                    break;
                case DistributorImage:
                    tabbedPane.addTab("Distributors ",
                            distributorTypeIcon,
                            new ImagesPanel(ImageType.DistributorImage, this, this));
                    break;
                case ManufacturerImage:
                    tabbedPane.addTab("Manufacturers ",
                            manufacturerTypeIcon,
                            new ImagesPanel(ImageType.ManufacturerImage, this, this));
                    break;
                case IdeImage:
                    tabbedPane.addTab("IDE's ",
                            ideTypeIcon,
                            new ImagesPanel(ImageType.IdeImage, this, this));
                    break;
                case ProjectImage:
                    tabbedPane.addTab("Projects ",
                            projectTypeIcon,
                            new ImagesPanel(ImageType.ProjectImage, this, this));
                    break;
                case Other:
                    tabbedPane.addTab("Others ",
                            otherTypeIcon,
                            new ImagesPanel(ImageType.Other, this, this));
                    break;
            }
        }


        getContentPanel().add(tabbedPane, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        Application.beginWait(this);
        try {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Component component = tabbedPane.getComponent(i);
                if (component != null) {
                    ImagesPanel panel = (ImagesPanel) component;
                    List<DbImage> imageList = imageResource.getAll(panel.getImageType());
                    imageList.sort(Comparator.comparing(DbObject::getName));
                    panel.updateImages(imageList);
                }
            }
            updateEnabledComponents();
        } finally {
            Application.endWait(this);
        }
    }
}