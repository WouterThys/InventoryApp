package com.waldo.inventory.gui.dialogs.filechooserdialog;

import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.utils.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import static com.waldo.inventory.gui.Application.imageResource;

public class ImageFileChooser {

    public static JFileChooser getFileChooser() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.addChoosableFileFilter(FileUtils.getImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);

        fileChooser.setFileView(new ImageFileView());
        fileChooser.setAccessory(new ImagePreview(fileChooser));

        return fileChooser;
    }

    private static class ImageFileView extends FileView {
        final ImageIcon jpgIcon = ImageResource.scaleImage(imageResource.readIcon("IFC.Jpg"), new Dimension(32, 32));
        final ImageIcon gifIcon = ImageResource.scaleImage(imageResource.readIcon("IFC.Gif"), new Dimension(32, 32));
        final ImageIcon tiffIcon = ImageResource.scaleImage(imageResource.readIcon("IFC.Tiff"), new Dimension(32, 32));
        final ImageIcon pngIcon = ImageResource.scaleImage(imageResource.readIcon("IFC.Png"), new Dimension(32, 32));

        public String getName(File f) {
            return null; //let the L&F FileView figure this out
        }

        public String getDescription(File f) {
            return null; //let the L&F FileView figure this out
        }

        public Boolean isTraversable(File f) {
            return null; //let the L&F FileView figure this out
        }

        public String getTypeDescription(File f) {
            String extension = FileUtils.getExtension(f);
            String type = null;

            if (extension != null) {
                switch (extension) {
                    case FileUtils.jpeg:
                    case FileUtils.jpg:
                        type = "JPEG Image";
                        break;
                    case FileUtils.gif:
                        type = "GIF Image";
                        break;
                    case FileUtils.tiff:
                    case FileUtils.tif:
                        type = "TIFF Image";
                        break;
                    case FileUtils.png:
                        type = "PNG Image";
                        break;
                }
            }
            return type;
        }

        public Icon getIcon(File f) {
            String extension = FileUtils.getExtension(f);
            Icon icon = null;

            if (extension != null) {
                switch (extension) {
                    case FileUtils.jpeg:
                    case FileUtils.jpg:
                        icon = jpgIcon;
                        break;
                    case FileUtils.gif:
                        icon = gifIcon;
                        break;
                    case FileUtils.tiff:
                    case FileUtils.tif:
                        icon = tiffIcon;
                        break;
                    case FileUtils.png:
                        icon = pngIcon;
                        break;
                }
            }
            return icon;
        }
    }

    private static class ImagePreview extends JComponent implements PropertyChangeListener {
        ImageIcon thumbnail = null;
        File file = null;

        public ImagePreview(JFileChooser fc) {
            setPreferredSize(new Dimension(100, 50));
            fc.addPropertyChangeListener(this);
        }

        public void loadImage() {
            if (file == null) {
                thumbnail = null;
                return;
            }

            //Don't use createImageIcon (which is a wrapper for getResource)
            //because the image we're trying to load is probably not one
            //of this program's own resources.
            ImageIcon tmpIcon = new ImageIcon(file.getPath());
            if (tmpIcon.getIconWidth() > 90) {
                thumbnail = new ImageIcon(tmpIcon.getImage().
                        getScaledInstance(90, -1,
                                Image.SCALE_DEFAULT));
            } else { //no need to miniaturize
                thumbnail = tmpIcon;
            }
        }

        public void propertyChange(PropertyChangeEvent e) {
            boolean update = false;
            String prop = e.getPropertyName();

            //If the directory changed, don't show an image.
            if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
                file = null;
                update = true;

                //If a file became selected, find out which one.
            } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
                file = (File) e.getNewValue();
                update = true;
            }

            //Update the preview accordingly.
            if (update) {
                thumbnail = null;
                if (isShowing()) {
                    loadImage();
                    repaint();
                }
            }
        }

        protected void paintComponent(Graphics g) {
            if (thumbnail == null) {
                loadImage();
            }
            if (thumbnail != null) {
                int x = getWidth()/2 - thumbnail.getIconWidth()/2;
                int y = getHeight()/2 - thumbnail.getIconHeight()/2;

                if (y < 0) {
                    y = 0;
                }

                if (x < 5) {
                    x = 5;
                }
                thumbnail.paintIcon(this, g, x, y);
            }
        }
    }
}
