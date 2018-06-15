package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.popups.CopyPastePopup;
import com.waldo.inventory.gui.dialogs.editimagedialog.EditImageDialog;
import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.waldo.inventory.gui.Application.imageResource;

public class IImagePanel extends IPanel implements ImageResource.ImageRequester, ClipboardOwner {

    private JLabel imageLbl;
    private JToolBar imageToolbar;

    private boolean editable;
    private ImageType imageType;
    private String imageName;
    private Dimension imageDimension;

    private DbObject dbObject;

    private Window parent;
    private IEditedListener editedListener;

    public IImagePanel(ImageType imageType, Dimension imageDimension) {
        this(imageType, "", null, imageDimension);
    }

    public IImagePanel(ImageType imageType, Dimension imageDimension, boolean editable) {
        this(null, imageType, "", editable, null, imageDimension);
    }

    public IImagePanel(ImageType imageType, String imageName, IEditedListener editedListener, Dimension imageDimension) {
        this(null, imageType, imageName, editedListener, imageDimension);
    }

    public IImagePanel(Window parent, ImageType imageType, String imageName, IEditedListener editedListener, Dimension imageDimension) {
        this(parent, imageType, imageName, false, editedListener, imageDimension);
    }

    public IImagePanel(Window parent, ImageType imageType, String imageName, boolean editable, IEditedListener editedListener, Dimension imageDimension) {
        super(new BorderLayout());

        this.parent = parent;
        this.imageType = imageType;
        this.imageName = imageName;
        this.editedListener = editedListener;
        this.editable = editable || editedListener != null;
        this.imageDimension = imageDimension;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    @Override
    public void setImage(ImageIcon imageIcon) {
        if (imageIcon != null) {
            imageIcon = ImageResource.scaleImage(imageIcon, imageDimension);
        }
        imageLbl.setIcon(imageIcon);
    }

    public void setImage(String name) {
        if (name == null || !name.equalsIgnoreCase(getImageName())) {
            setImageName(name);
            updateComponents();
        }
    }

    public DbObject getDbObject() {
        return dbObject;
    }

    public void setDbObject(DbObject dbObject) {
        this.dbObject = dbObject;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public ImageType getImageType() {
        if (imageType == null) {
            imageType = ImageType.Other;
        }
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    @Override
    public String getImageName() {
        if (imageName == null) {
            imageName = "";
        }
        return imageName;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
        if (imageName == null || imageName.isEmpty()) {
            setToolTipText(null);
        } else {
            setToolTipText(getImageName());
        }
    }


    private TransferHandler createNewTransferHandler() {
        return new TransferHandler() {
            @Override
            public boolean importData(JComponent component, Transferable transferable) {
                Object transferData = null;
                try {
                    transferData = transferable.getTransferData(DataFlavor.imageFlavor);
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
                if (transferData instanceof BufferedImage) {
                    BufferedImage image = (BufferedImage) transferData;

                    if (editedListener != null) {
                        DbObject object = (DbObject) editedListener.getGuiObject();
                        if (getImageName().isEmpty()) {
                            imageName = object.getName() + ".jpg";
                        }
                        object.setIconPath(imageName);
                        editedListener.onValueChanged(IImagePanel.this, "iconPath", "", imageName);
                    }

                    imageResource.saveImage(image, imageType, imageName);
                    setImage(imageName);
                }
                return true;
            }

            @Override
            public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
                return editable;
            }


        };
    }

    private void saveImage(String imageName, File imageFile) {
        if (imageFile != null && imageFile.exists() && !imageName.equalsIgnoreCase(getImageName())) {
            imageResource.saveImage(imageFile, imageType, imageName);
            setImage(imageName);

            if (editedListener != null) {
                DbObject object = (DbObject) editedListener.getGuiObject();
                object.setIconPath(imageName);
                editedListener.onValueChanged(IImagePanel.this, "iconPath", "", imageName);
            }
        }
    }

    private void deleteImage() {
        int res = JOptionPane.showConfirmDialog(
                parent,
                "Delete image " + getImageName() + "?",
                "Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (res == JOptionPane.YES_OPTION) {
            setImage((String) null);

            if (editedListener != null) {
                DbObject object = (DbObject) editedListener.getGuiObject();
                object.setIconPath("");
                editedListener.onValueChanged(IImagePanel.this, "iconPath", "", "");
            }
        }
    }

    private void copyToClipboard() {
        Icon icon = imageLbl.getIcon();
        if (icon != null) {
            //icon = new SafeIcon(icon);
            BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);

            TransferableImage trans = new TransferableImage(image);
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            c.setContents(trans, this);
        }
    }

    private void pasteFromClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (clipboard != null) {
            try {
                BufferedImage image = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
                if (image != null) {
                    if (editedListener != null) {
                        DbObject object = (DbObject) editedListener.getGuiObject();
                        if (getImageName().isEmpty()) {
                            imageName = object.getName() + ".jpg";
                        }
                        object.setIconPath(imageName);
                        editedListener.onValueChanged(IImagePanel.this, "iconPath", "", imageName);
                    }

                    imageResource.saveImage(image, imageType, imageName);
                    setImage(imageName);
                }
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
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

        if (editable) {
            imageLbl.setTransferHandler(createNewTransferHandler());

            imageLbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        JPopupMenu popupMenu = new CopyPastePopup(!getImageName().isEmpty(), true) {
                            @Override
                            public void onCopy() {
                                copyToClipboard();
                            }

                            @Override
                            public void onPaste() {
                                pasteFromClipboard();
                            }
                        };

                        popupMenu.show(imageLbl, e.getX(), e.getY());
                    }
                }
            });
        }

        IActions.EditAction editImageAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                SelectImageDialog selectImageDialog = new SelectImageDialog(parent, false, imageType);
//                if (selectImageDialog.showDialog() == IDialog.OK) {
//                    String imageName = selectImageDialog.getImageName();
//                    saveImage(imageName, selectImageDialog.getSelectedFile());
//                }
                EditImageDialog dialog = new EditImageDialog(parent);
                dialog.showDialog();
            }
        };

        IActions.DeleteAction deleteImageAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (imageLbl.getIcon() != null) {
                    deleteImage();
                }
            }
        };


        ImageIcon edit = ImageResource.scaleImage(imageResource.readIcon("Actions.Edit"), new Dimension(10,10));
        ImageIcon delete = ImageResource.scaleImage(imageResource.readIcon("Actions.Delete"), new Dimension(10,10));
        editImageAction.setIcon(edit);
        deleteImageAction.setIcon(delete);

        imageToolbar = GuiUtils.createNewToolbar(editImageAction, deleteImageAction);
        imageToolbar.setOrientation(JToolBar.VERTICAL);
        imageToolbar.setVisible(editable);

        if (!getImageName().isEmpty()) {
            setToolTipText(getImageName());
        }
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

        if (dbObject != null) {
            SwingUtilities.invokeLater(() -> {
                DbImage image = imageResource.getImage(imageType, dbObject.getImageId());
                if (image != null) {
                    setImage(image.getImageIcon());
                }
            });
            return;
        }

        // Old way
        if (!getImageName().isEmpty()) {
            imageResource.requestImage(this);
        } else {
            setImage(imageResource.getDefaultImage(getImageType()));
        }
    }

    @Override
    public long getImageId() {
        return 0;
    }
}
