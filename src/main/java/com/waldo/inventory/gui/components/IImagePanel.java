package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.imagedialogs.selectimagedialog.SelectImageDialog;
import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static com.waldo.inventory.gui.Application.imageResource;

public class IImagePanel extends IPanel implements ImageResource.ImageRequester {

    private JLabel imageLbl;
    private JToolBar imageToolbar;

    private boolean editable;
    private ImageType imageType;
    private String imageName;
    private Dimension imageDimension;

    private Window parent;
    private IEditedListener editedListener;

    public IImagePanel(ImageType imageType, Dimension imageDimension) {
        this(imageType, "", null, imageDimension);
    }

    public IImagePanel(ImageType imageType, String imageName, IEditedListener editedListener, Dimension imageDimension) {
        this(null, imageType, imageName, editedListener, imageDimension);
    }

    public IImagePanel(Window parent, ImageType imageType, String imageName, IEditedListener editedListener, Dimension imageDimension) {
        super(new BorderLayout());

        this.parent = parent;
        this.imageType = imageType;
        this.imageName = imageName;
        this.editedListener = editedListener;
        this.editable = editedListener != null;
        this.imageDimension = imageDimension;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }


    public void setImage(ImageIcon imageIcon) {
        if (imageIcon != null) {
            imageIcon = ImageResource.scaleImage(imageIcon, imageDimension);
        }
        imageLbl.setIcon(imageIcon);
    }

    public void setImage(String name) {
        setImageName(name);
        updateComponents();
    }


    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public ImageType getImageType() {
        if (imageType == null) {
            imageType = ImageType.Other;
        }
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public String getImageName() {
        if (imageName == null) {
            imageName = "";
        }
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
        setToolTipText(getImageName());
    }


    //
    // Gui
    //
    @Override
    public void initializeComponents() {
        imageLbl = new JLabel((ImageIcon)null, JLabel.CENTER);
        imageLbl.setPreferredSize(imageDimension);
        imageLbl.setMaximumSize(imageDimension);
        imageLbl.setMinimumSize(imageDimension);
        imageLbl.setOpaque(true);
        imageLbl.setBackground(Color.WHITE);

        IActions.EditAction editImageAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectImageDialog selectImageDialog = new SelectImageDialog(parent, false, imageType);
                if (selectImageDialog.showDialog() == IDialog.OK) {
                    String imageName = selectImageDialog.getImageName();
                    if (!imageName.equalsIgnoreCase(getImageName())) {
                        File imageFile = selectImageDialog.getSelectedFile();
                        imageResource.saveImage(imageFile, imageType, imageName);
                        setImage(imageName);

                        if (editedListener != null) {
                            DbObject object = (DbObject) editedListener.getGuiObject();
                            object.setIconPath(imageName);
                            editedListener.onValueChanged(IImagePanel.this, "iconPath", "", imageName);
                        }
                    }
                }
            }
        };

        IActions.DeleteAction deleteImageAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        ImageIcon edit = ImageResource.scaleImage(imageResource.readIcon("Actions.Edit"), new Dimension(10,10));
        ImageIcon delete = ImageResource.scaleImage(imageResource.readIcon("Actions.Delete"), new Dimension(10,10));
        editImageAction.setIcon(edit);
        deleteImageAction.setIcon(delete);

        imageToolbar = GuiUtils.createNewToolbar(editImageAction, deleteImageAction);
        imageToolbar.setOrientation(JToolBar.VERTICAL);
        imageToolbar.setVisible(editable);

        setToolTipText(getImageName());
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(imageLbl, BorderLayout.CENTER);
        panel.add(imageToolbar, BorderLayout.EAST);

        imageLbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(1,1,1,1)
        ));

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... objects) {
        if (!getImageName().isEmpty()) {
            imageResource.requestImage(this);
        } else {
            setImage(imageResource.getDefaultImage(getImageType()));
        }
    }

}
