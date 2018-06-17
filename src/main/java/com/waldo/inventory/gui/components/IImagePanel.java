package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Statics.ImageType;
import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.interfaces.ImageChangedListener;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.popups.CopyPastePopup;
import com.waldo.inventory.gui.dialogs.editimagedialog.EditImageDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.waldo.inventory.gui.Application.imageResource;

public class IImagePanel extends IPanel implements ClipboardOwner, ImageChangedListener {

    private JLabel imageLbl;
    private JToolBar imageToolbar;

    private boolean editable;
    private ImageType imageType;
    private Dimension imageDimension;
    private DbObject dbObject;

    private Window parent;
    private IEditedListener editedListener;



    public IImagePanel(Window parent, ImageType imageType, DbObject dbObject, Dimension imageDimension) {
        this(parent, imageType, dbObject, imageDimension, null);
    }


    public IImagePanel(Window parent, ImageType imageType, DbObject dbObject, Dimension imageDimension, IEditedListener editedListener) {
        super(new BorderLayout());

        this.parent = parent;
        this.imageType = imageType;
        this.dbObject = dbObject;
        this.editedListener = editedListener;
        this.editable = editedListener != null;
        this.imageDimension = imageDimension;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public DbObject getDbObject() {
        return dbObject;
    }

    public void setDbObject(DbObject dbObject) {
        this.dbObject = dbObject;
        updateComponents();
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
        updateComponents();
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //
    }

    private void setImage(ImageIcon imageIcon) {
        if (imageIcon != null) {
            imageIcon = ImageResource.scaleImage(imageIcon, imageDimension);
        }
        imageLbl.setIcon(imageIcon);
    }

    private void updateWithNewIcon(ImageIcon imageIcon) {
        if(imageIcon != null) {
            if (dbObject != null) {
                DbImage image = new DbImage(imageType, imageIcon, dbObject.getName());
                image.save();
            }
        }
        setImage(imageIcon);
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
                    ImageIcon icon = new ImageIcon(image);
                    updateWithNewIcon(icon);
                }
                return true;
            }

            @Override
            public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
                return editable;
            }


        };
    }

    private void deleteImage() {
        int res = JOptionPane.showConfirmDialog(
                parent,
                "Delete image?",
                "Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (res == JOptionPane.YES_OPTION) {
            setImage(imageResource.getDefaultImage(imageType));
            if (dbObject != null) {
                dbObject.setImageId(0);
            }
            if (editedListener != null) {
                editedListener.onValueChanged(IImagePanel.this, "imageId", 0, 0);
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
        if (editable) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard != null) {
                try {
                    BufferedImage image = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
                    if (image != null) {
                        updateWithNewIcon(new ImageIcon(image));
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //
    // Images changed
    //
    @Override
    public void onInserted(DbImage image) {
        if (editable && dbObject != null && image != null) {
            dbObject.setImageId(image.getId());

            if (editedListener != null) {
                editedListener.onValueChanged(this, "imageId", 0, image.getId());
            }
        }
    }

    @Override
    public void onUpdated(DbImage image) {
        if (editable && dbObject != null && image != null) {

            if (dbObject.getImageId() == image.getId()) {
                dbObject.setImageId(image.getId());

                if (editedListener != null) {
                    editedListener.onValueChanged(this, "imageId", 0, image.getId());
                }
            }
        }
    }

    @Override
    public void onDeleted(DbImage image) {
        if (editable && dbObject != null && image != null) {

            if (dbObject.getImageId() == image.getId()) {
                dbObject.setImageId(0);

                if (editedListener != null) {
                    editedListener.onValueChanged(this, "imageId", 0, image.getId());
                }
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
                        JPopupMenu popupMenu = new CopyPastePopup(imageLbl.getIcon() != null, true) {
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
                EditImageDialog dialog = new EditImageDialog(parent, imageType);
                if (dialog.showDialog() == IDialog.OK) {
                    DbImage image = dialog.getSelectedImage();
                    if (image != null) {
                        setImage(image.getImageIcon());
                        if (dbObject != null) {
                            dbObject.setImageId(image.getId());
                        }
                        if (editedListener != null) {
                            editedListener.onValueChanged(IImagePanel.this, "imageId", 0, image.getId());
                        }
                    }
                }
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

        if(objects != null && objects.length > 0) {
            dbObject = (DbObject) objects[0];
        }

        if (dbObject != null) {
            SwingUtilities.invokeLater(() -> {
                DbImage image = imageResource.getImage(imageType, dbObject.getImageId());
                if (image != null) {
                    setImage(image.getImageIcon());
                } else {
                    setImage(imageResource.getDefaultImage(getImageType()));
                }
            });
        } else {
            setImage(imageResource.getDefaultImage(getImageType()));
        }
    }
}
