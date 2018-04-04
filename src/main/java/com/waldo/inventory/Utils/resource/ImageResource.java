package com.waldo.inventory.Utils.resource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.waldo.inventory.database.settings.SettingsManager.settings;

public class ImageResource extends Resource{

    private static final ImageResource INSTANCE = new ImageResource();
    public static ImageResource getInstance() {
        return INSTANCE;
    }
    private ImageResource() { }

    public static final String DEFAULT = "default";

    private Map<String, ImageIcon> iconImageMap = new HashMap<>();
    private Map<String, ImageIcon> itemImageMap = new HashMap<>();
    private Map<String, ImageIcon> distributorImageMap = new HashMap<>();
    private Map<String, ImageIcon> manufacturerImageMap = new HashMap<>();
    private Map<String, ImageIcon> ideImageMap = new HashMap<>();
    private Map<String, ImageIcon> projectImageMap = new HashMap<>();

    public void init(String propertiesUrl, String fileName ) throws IOException {
        super.initProperties(propertiesUrl, fileName);

        iconImageMap.put(DEFAULT, readIcon("Common.UnknownIcon32"));
        itemImageMap.put(DEFAULT, readIcon("Items.Edit.Title"));
        distributorImageMap.put(DEFAULT, readIcon("Distributors.Title"));
        manufacturerImageMap.put(DEFAULT, readIcon("Manufacturers.Title"));
        ideImageMap.put(DEFAULT, readIcon("Ides.Title"));
        projectImageMap.put(DEFAULT, readIcon("Projects.Icon"));
    }

    public ImageIcon readItemIcon(String name) {
        return fromMap(itemImageMap, name, settings().getFileSettings().getImgItemsPath());
    }

    public ImageIcon readDistributorIcon(String name) {
        return fromMap(distributorImageMap, name, settings().getFileSettings().getImgDistributorsPath());
    }

    public ImageIcon readManufacturerIcon(String name) {
        return fromMap(manufacturerImageMap, name, settings().getFileSettings().getImgManufacturersPath());
    }

    public ImageIcon readIdeIcon(String name) {
        return fromMap(ideImageMap, name, settings().getFileSettings().getImgIdesPath());
    }

    public ImageIcon readProjectIcon(String name) {
        return fromMap(projectImageMap, name, settings().getFileSettings().getImgProjectsPath());
    }


//    public ImageIcon readImage(Path path) throws IOException {
//        URL url = path.toUri().toURL();
//        return new ImageIcon(ImageIO.read(url));
//    }
//
//    public ImageIcon readImage(Path path, int width, int height) throws Exception {
//        URL url = path.toUri().toURL();
//        return getScaledImageIcon(ImageIO.read(url), width, height);
//    }

    private ImageIcon fromMap(Map<String, ImageIcon> map, String name, String imagePath) {
        ImageIcon icon = null;
        if ((name != null) && (!name.isEmpty())) {
            if (map.containsKey(name)) {
                icon = map.get(name);
            } else {
                Path path = Paths.get(imagePath, name);
                try {
                    URL url = path.toUri().toURL();
                    icon = new ImageIcon(ImageIO.read(url));
                } catch (Exception e) {
                    //
                }
            }
        }
        if (icon == null) {
            icon = map.get(DEFAULT);
        }
        map.put(name, icon);
        return icon;
    }

    public static ImageIcon scaleImage(ImageIcon imageIcon, Dimension boundary) {
        Dimension newScale = getScaledDimension(imageIcon, boundary);
        Image icon = imageIcon.getImage();
        Image scaledImage = icon.getScaledInstance(newScale.width, newScale.height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public static Dimension getScaledDimension(ImageIcon icon, Dimension boundary) {

        int original_width = icon.getIconWidth();
        int original_height = icon.getIconHeight();
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    private ImageIcon getScaledImageIcon(BufferedImage image, int width, int height) {
        int newWidth;
        int newHeight;
        double targetRatio = (double) width / height;
        double originalRatio = (double) image.getWidth() / image.getHeight();
        if (originalRatio >= targetRatio) {
            newWidth = width;
            newHeight = newWidth;
        } else {
            newHeight = height;
            newWidth = newHeight;
        }
        Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public ImageIcon readIcon(String key) {
        if (iconImageMap.containsKey(key)) {
            return iconImageMap.get(key);
        }

        ImageIcon icon = getIcon(key);
        if (icon == null) {
            icon = iconImageMap.get(DEFAULT);
        }
        iconImageMap.put(key, icon);

        return icon;
    }

    private ImageIcon getIcon(String key) {
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("icons/" + readString(key));
            return new ImageIcon(ImageIO.read(is));
        } catch (Exception e) {
            //
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return null;
    }

}
