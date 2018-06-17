package com.waldo.inventory.Utils.resource;

import com.waldo.inventory.Utils.Statics.ImageType;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.interfaces.ImageChangedListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // Local images
    private final Map<String, ImageIcon> iconImageMap = new HashMap<>();

    private final Map<ImageType, Vector<DbImage>> imageMap = new EnumMap<>(ImageType.class);

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

    public ImageIcon getDefaultImage(ImageType imageType) {
        switch (imageType) {
            case ItemImage:
                return readIcon("Items.Edit.Title");
            case IdeImage:
                return readIcon("Ides.Title");
            case ProjectImage:
                return readIcon("Projects.Icon");
            case DistributorImage:
                return readIcon("Distributors.Title");
            case ManufacturerImage:
                return readIcon("Manufacturers.Title");
            default:
                return readIcon("Common.UnknownIcon48");
        }
    }


    public void dummy(ImageType type) {
        switch (type) {
            case ItemImage:
                break;
            case DistributorImage:
                break;
            case ManufacturerImage:
                break;
            case IdeImage:
                break;
            case ProjectImage:
                break;
            case Other:
                break;
            default:
                break;
        }
    }


    /**
     * Make this call on a different thread!!
     */
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



















    // OLD ->





    public ImageIcon fetchImage(String imagePath) {
        ImageIcon icon = null;
        Path path = Paths.get(imagePath);
        try {
            URL url = path.toUri().toURL();
            icon = new ImageIcon(ImageIO.read(url));
        } catch (Exception e) {
            //
        }
        return icon;
    }

    public static ImageIcon scaleImage(ImageIcon imageIcon, Dimension boundary) {
        if (imageIcon == null || boundary == null) {
            return null;
        }
        Dimension newScale = getScaledDimension(imageIcon, boundary);
        Image icon = imageIcon.getImage();
        Image scaledImage = icon.getScaledInstance(newScale.width, newScale.height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private static Dimension getScaledDimension(ImageIcon icon, Dimension boundary) {

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
            //
        }
        //
        return null;
    }


}
