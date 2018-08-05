package com.waldo.inventory.Utils.resource;

import com.waldo.inventory.Utils.Statics.IconSize;
import com.waldo.inventory.Utils.Statics.ImageType;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.interfaces.ImageChangedListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static com.waldo.inventory.database.ImageDbAccess.imDb;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ImageResource extends Resource implements ImageChangedListener {

    private static final ImageResource INSTANCE = new ImageResource();

    public static ImageResource getInstance() {
        return INSTANCE;
    }

    private ImageResource() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            imDb().close();
        }));
    }

    private static final int DEFAULT = 1;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Map<String, ImageIcon> iconImageMap = new HashMap<>();

    private final Map<ImageType, Vector<DbImage>> imageMap = new EnumMap<>(ImageType.class);

    /*
     *                  INITIALIZE
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void initIcons(String propertiesUrl, String fileName) throws IOException {
        super.initProperties(propertiesUrl, fileName);
    }

    public void initServer() {
        try {
            imDb().init();
            imDb().addImageChangedListener(this);
            imDb().startImageWorkers();
            Status().updateConnectionStatus();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /*
     *                  ICONS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public ImageIcon getDefaultImage(ImageType imageType) {
        switch (imageType) {
            case ItemImage:
                return readIcon("Component.L");
            case IdeImage:
                return readIcon("Toolkit.L");
            case ProjectImage:
                return readIcon("BluePrint.L");
            case DistributorImage:
                return readIcon("Distributor.L");
            case ManufacturerImage:
                return readIcon("Factory.L");
            default:
                return readIcon("Unknown.L");
        }
    }

    public ImageIcon readIcon(String name, IconSize iconSize) {
        if ((name != null) && (!name.isEmpty())) {
            if (iconSize == null) {
                iconSize = IconSize.SS;
            }
            String propertiesKey = name + "." + iconSize;
            return readIcon(propertiesKey);
        }
        return null;
    }


    public ImageIcon readIcon(String key) {
        if (iconImageMap.containsKey(key)) {
            return iconImageMap.get(key);
        }

        ImageIcon icon = getIcon(key);
        if (icon == null) {
            icon = iconImageMap.get(DEFAULT);
        }
        if (icon != null) {
            iconImageMap.put(key, icon);
        }

        return icon;
    }

    private ImageIcon getIcon(String key) {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("icons/" + readString(key))) {
            return new ImageIcon(ImageIO.read(is));
        } catch (Exception e) {
            return null;
        }
    }

    public ImageIcon getImageFromDisc(String path) {

        if (iconImageMap.containsKey(path)) {
            return iconImageMap.get(path);
        }

        ImageIcon icon;
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("images/" + path)) {
            icon = new ImageIcon(ImageIO.read(is));
        } catch (Exception e) {
            icon = null;
        }

        if (icon != null) {
            iconImageMap.put(path, icon);
        }

        return icon;
    }

    /*
     *                  IMAGES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public DbImage getImage(ImageType type, long id) {
        DbImage image = null;
        if (type != null) {
            image = findImageForRequest(type, id);
            if (image == null) {
                image = imDb().fetch(type, id);
                onInserted(image); // Adds image
            }
        }
        return  image;
    }

    private DbImage findImageForRequest(ImageType type, long id) {
        if ((type != null) && (id > DbObject.UNKNOWN_ID)) {
            if (imageMap.containsKey(type)) {
                for (DbImage image : imageMap.get(type)) {
                    if (image.getId() == id) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    public List<DbImage> getAll(ImageType type) {
        if (!type.isAllFetched()) {
            imageMap.put(type, new Vector<>(imDb().fetchAllImages(type)));
            type.setAllFetched(true);
        }
        return imageMap.get(type);
    }



    //
    // Image db listener
    //
    @Override
    public void onInserted(DbImage image) {
        if (image != null) {
            if (!imageMap.containsKey(image.getImageType())) {
                imageMap.put(image.getImageType(), new Vector<>());
            }
            if (!imageMap.get(image.getImageType()).contains(image)) {
                imageMap.get(image.getImageType()).add(image);
            }
        }
    }

    @Override
    public void onUpdated(DbImage image) {

    }

    @Override
    public void onDeleted(DbImage image) {
        if (image != null) {
            if (imageMap.containsKey(image.getImageType())) {
                imageMap.get(image.getImageType()).remove(image);
            }
        }
    }


    /*
     *                  HELPERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static ImageIcon scaleImage(ImageIcon imageIcon, Dimension boundary) {
        if (imageIcon == null || boundary == null) {
            return null;
        }
        Dimension newScale = getScaledDimension(imageIcon.getIconWidth(), imageIcon.getIconHeight(), boundary);
        Image icon = imageIcon.getImage();
        Image scaledImage = icon.getScaledInstance(newScale.width, newScale.height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public static Dimension getScaledDimension(int originalWidth, int originalHeight, Dimension boundary) {

        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = originalWidth;
        int new_height = originalHeight;

        // first check if we need to scale width
        if (originalWidth > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * originalHeight) / originalWidth;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * originalWidth) / originalHeight;
        }

        return new Dimension(new_width, new_height);
    }
}
