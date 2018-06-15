package com.waldo.inventory.Utils.resource;

import com.waldo.inventory.Main;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.interfaces.ImageChangedListener;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.database.settings.settingsclasses.ImageServerSettings;
import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.test.client.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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

public class ImageResource extends Resource implements Client.ImageClientListener, ImageChangedListener {

    public interface ImageRequester {
        ImageType getImageType();
        long getImageId();
        String getImageName();
        void setImage(ImageIcon image);
    }

    private static final ImageResource INSTANCE = new ImageResource();

    public static ImageResource getInstance() {
        return INSTANCE;
    }

    private ImageResource() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (client != null) {
                client.disconnectClient(true);
            }
        }));
    }

    private static final String DEFAULT = "default";

    // Local images
    private final Map<String, ImageIcon> iconImageMap = new HashMap<>();

    // Cache for server images
    private final Map<String, ImageIcon> itemImageMap = new HashMap<>();
    private final Map<String, ImageIcon> distributorImageMap = new HashMap<>();
    private final Map<String, ImageIcon> manufacturerImageMap = new HashMap<>();
    private final Map<String, ImageIcon> ideImageMap = new HashMap<>();
    private final Map<String, ImageIcon> projectImageMap = new HashMap<>();
    private final Map<String, ImageIcon> otherImageMap = new HashMap<>();

    // NEW CACHE FOR DB IMAGES
    private final Map<Long, DbImage> dbItemImageList = new HashMap<>();
    private final Map<Long, DbImage> dbDistributorList = new HashMap<>();
    private final Map<Long, DbImage> dbManufacturerImageList = new HashMap<>();
    private final Map<Long, DbImage> dbIdeImageList = new HashMap<>();
    private final Map<Long, DbImage> dbProjectImageList = new HashMap<>();
    private final Map<Long, DbImage> dbOtherImageList = new HashMap<>();

    // Requests for image
    private List<ImageRequest> imageRequests = new ArrayList<>();

    private Client client;

    public void initIcons(String propertiesUrl, String fileName) throws IOException {
        super.initProperties(propertiesUrl, fileName);

        iconImageMap.put(DEFAULT, readIcon("Common.UnknownIcon32"));
        itemImageMap.put(DEFAULT, readIcon("Items.Edit.Title"));
        distributorImageMap.put(DEFAULT, readIcon("Distributors.Title"));
        manufacturerImageMap.put(DEFAULT, readIcon("Manufacturers.Title"));
        ideImageMap.put(DEFAULT, readIcon("Ides.Title"));
        projectImageMap.put(DEFAULT, readIcon("Projects.Icon"));
        otherImageMap.put(DEFAULT, readIcon("Common.UnknownIcon48"));
    }

    public void initServer() {
        updateImageServerConnection();
        try {
            imDb().init();
            imDb().addImageChangedListener(this);
            imDb().startImageWorkers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ImageIcon getDefaultImage(ImageType imageType) {
        switch (imageType) {
            case ItemImage:
                return itemImageMap.get(DEFAULT);
            case IdeImage:
                return ideImageMap.get(DEFAULT);
            case ProjectImage:
                return projectImageMap.get(DEFAULT);
            case DistributorImage:
                return distributorImageMap.get(DEFAULT);
            case ManufacturerImage:
                return manufacturerImageMap.get(DEFAULT);
            //case DivisionImage: return .get(DEFAULT);
            default:
                return otherImageMap.get(DEFAULT);
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
        DbImage foundImage = null;
            if ((type != null) && (id > DbObject.UNKNOWN_ID)) {
                switch (type) {
                    case ItemImage:
                        foundImage = dbItemImageList.get(id);
                        break;
                    case DistributorImage:
                        foundImage = dbDistributorList.get(id);
                        break;
                    case ManufacturerImage:
                        foundImage = dbManufacturerImageList.get(id);
                        break;
                    case IdeImage:
                        foundImage = dbIdeImageList.get(id);
                        break;
                    case ProjectImage:
                        foundImage = dbProjectImageList.get(id);
                        break;
                    case Other:
                        foundImage = dbOtherImageList.get(id);
                        break;
                    default:
                        break;
                }

        }
        return foundImage;
    }

    public List<DbImage> getAll(ImageType type) {
        List<DbImage> imageList = new ArrayList<>();
        switch (type) {
            case ItemImage:
                imageList.addAll(imDb().fetchItemImages());
                break;
            case DistributorImage:
                imageList.addAll(dbDistributorList.values());
                break;
            case ManufacturerImage:
                imageList.addAll(dbManufacturerImageList.values());
                break;
            case IdeImage:
                imageList.addAll(dbIdeImageList.values());
                break;
            case ProjectImage:
                imageList.addAll(dbProjectImageList.values());
                break;
            case Other:
                imageList.addAll(dbOtherImageList.values());
                break;
            default:
                break;
        }
        return imageList;
    }



    //
    // Image db listener
    //
    @Override
    public void onInserted(DbImage image) {
        switch (image.getImageType()) {
            case ItemImage:
                dbItemImageList.put(image.getId(), image);
                break;
            case DistributorImage:
                dbDistributorList.put(image.getId(), image);
                break;
            case ManufacturerImage:
                dbManufacturerImageList.put(image.getId(), image);
                break;
            case IdeImage:
                dbIdeImageList.put(image.getId(), image);
                break;
            case ProjectImage:
                dbProjectImageList.put(image.getId(), image);
                break;
            case Other:
                dbOtherImageList.put(image.getId(), image);
                break;
                default:
                    break;

        }
    }

    @Override
    public void onUpdated(DbImage image) {

    }

    @Override
    public void onDeleted(DbImage image) {
        switch (image.getImageType()) {
            case ItemImage:
                dbItemImageList.remove(image.getId());
                break;
            case DistributorImage:
                dbDistributorList.remove(image.getId());
                break;
            case ManufacturerImage:
                dbManufacturerImageList.remove(image.getId());
                break;
            case IdeImage:
                dbIdeImageList.remove(image.getId());
                break;
            case ProjectImage:
                dbProjectImageList.remove(image.getId());
                break;
            case Other:
                dbOtherImageList.remove(image.getId());
                break;
            default:
                break;

        }
    }



















    // OLD ->



    public void requestImage(final ImageRequester imageRequester) {
        if (imageRequester != null) {
            //SwingUtilities.invokeLater(() -> {
                switch (imageRequester.getImageType()) {
                    case ItemImage:
                        fromMap(itemImageMap, imageRequester);
                        break;
                    case IdeImage:
                        fromMap(ideImageMap, imageRequester);
                        break;
                    case ProjectImage:
                        fromMap(projectImageMap, imageRequester);
                        break;
                    case DistributorImage:
                        fromMap(distributorImageMap, imageRequester);
                        break;
                    case ManufacturerImage:
                        fromMap(manufacturerImageMap, imageRequester);
                        break;
                    case DivisionImage:
                        //fromMap(divisio)
                        break;
                    case Other:
                        fromMap(otherImageMap, imageRequester);
                        break;
                    default:
                        break;
                }
            //});
        }
    }

    public ImageIcon readIdeIcon(String name) {
        return fromMap(ideImageMap, name, ImageType.IdeImage);
    }

    private ImageIcon fromMap(Map<String, ImageIcon> map, String name, ImageType imageType) {
        ImageIcon icon = null;
        if ((name != null) && (!name.isEmpty())) {
            if (map.containsKey(name)) {
                icon = map.get(name);
            } else {
                SwingUtilities.invokeLater(() -> fetchImage(imageType, name));
            }
        }
        if (icon == null) {
            icon = map.get(DEFAULT);
        }
        map.put(name, icon);
        return icon;
    }

    private void fromMap(Map<String, ImageIcon> map, ImageRequester requester) {
        String imageName = requester.getImageName();
        if ((imageName != null) && (!imageName.isEmpty())) {
            if (map.containsKey(imageName)) {
                requester.setImage(map.get(imageName));
            } else {
                createRequest(requester);
            }
        }
    }

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


    //
    // Image server client
    //

    public Client getClient() {
        return client;
    }

    public boolean serverConnected() {
        return Main.IMAGE_SERVER && client != null && client.isConnected();
    }

    public void updateImageServerConnection() {
        if (Main.IMAGE_SERVER) {
            ImageServerSettings imageServerSettings = SettingsManager.settings().getImageServerSettings();
            String clientName = imageServerSettings.getConnectAsName();
            if (client == null || !client.getClientName().equalsIgnoreCase(clientName) || !client.isConnected()) {

                if (client != null) {
                    client.disconnectClient(true);
                }

                client = new Client(imageServerSettings.getImageServerName(), imageServerSettings.getConnectAsName());
                client.addImageClientListener(this);
                client.connectClient();
            }
        }
    }

    private void fetchImage(ImageType imageType, String name) {
        try {
            client.getImage(name, imageType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveImage(BufferedImage image, ImageType imageType, String imageName) {
        ImageIcon imageIcon;
        try {
            if (image != null) {
                if (serverConnected()) {
                    client.sendImage(image, imageName, imageType);
                }
                imageIcon = new ImageIcon(image);
                switch (imageType) {
                    case ItemImage:
                        itemImageMap.put(imageName, imageIcon);
                        break;
                    case DistributorImage:
                        distributorImageMap.put(imageName, imageIcon);
                        break;
                    case ManufacturerImage:
                        manufacturerImageMap.put(imageName, imageIcon);
                        break;
                    case IdeImage:
                        ideImageMap.put(imageName, imageIcon);
                        break;
                    case ProjectImage:
                        projectImageMap.put(imageName, imageIcon);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveImage(File file, ImageType imageType, String imageName) {
        ImageIcon imageIcon;
        try {
            BufferedImage image = client.sendImage(file, imageType, imageName);
            if (image != null) {
                imageIcon = new ImageIcon(image);
                switch (imageType) {
                    case ItemImage:
                        itemImageMap.put(imageName, imageIcon);
                        break;
                    case DistributorImage:
                        distributorImageMap.put(imageName, imageIcon);
                        break;
                    case ManufacturerImage:
                        manufacturerImageMap.put(imageName, imageIcon);
                        break;
                    case IdeImage:
                        ideImageMap.put(imageName, imageIcon);
                        break;
                    case ProjectImage:
                        projectImageMap.put(imageName, imageIcon);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnected(String clientName) {
        if (Main.DEBUG_MODE) System.out.println("Client " + clientName + " connected");
        Status().updateConnectionStatus();
    }

    @Override
    public void onDisconnected(String clientName) {
        if (Main.DEBUG_MODE) System.out.println("Client " + clientName + " disconnected");
        Status().updateConnectionStatus();
    }

    @Override
    public void onImageTransmitted(String imageName, ImageType imageType) {
        System.out.println("Image " + imageName + " transmitted");
    }

    @Override
    public void onImageReceived(BufferedImage bufferedImage, String imageName, ImageType imageType) {
        if (bufferedImage != null) {
            ImageIcon icon = new ImageIcon(bufferedImage);
            switch (imageType) {
                case ItemImage:
                    itemImageMap.put(imageName, icon);
                    break;
                case DistributorImage:
                    distributorImageMap.put(imageName, icon);
                    break;
                case ManufacturerImage:
                    manufacturerImageMap.put(imageName, icon);
                    break;
                case IdeImage:
                    ideImageMap.put(imageName, icon);
                    break;
                case ProjectImage:
                    projectImageMap.put(imageName, icon);
                    break;
            }

            checkRequests(icon, imageType, imageName);
        } else {
            if (Main.DEBUG_MODE) System.out.println("IR: onImageReceived - Empty image received for " + imageType + ", " + imageName);
        }
    }

    private void checkRequests(ImageIcon icon, ImageType imageType, String imageName) {
        ImageRequest request = findImageRequest(imageType, imageName);
        if (request != null) {
            for (ImageRequester requester : request.getImageRequesters()) {
                if (requester != null) {
                    // Is requester still interested?
                    if (requester.getImageType().equals(imageType) && request.getImageName().equals(imageName)) {
                        requester.setImage(icon);
                        if (Main.DEBUG_MODE)
                            System.out.println("IR: checkRequests - set image " + imageType + ", " + imageName);
                    } else {
                        if (Main.DEBUG_MODE)
                            System.out.println("IR: checkRequests - no requester changed his mind " + imageType + ", " + imageName);
                    }
                } else {
                    if (Main.DEBUG_MODE)
                        System.out.println("IR: checkRequests - no requester found for " + imageType + ", " + imageName);
                }
            }
            imageRequests.remove(request);

        } else {
            if (Main.DEBUG_MODE) System.out.println("IR: checkRequests - no request found for " + imageType + ", " + imageName);
        }
    }

    private ImageRequest findImageRequest(ImageType imageType, String imageName) {

        for (ImageRequest imageRequest : imageRequests) {
            if (imageRequest.isThis(imageType, imageName)) {
                return imageRequest;
            }
        }

        return null;
    }

    private void createRequest(final ImageRequester requester) {
        if (serverConnected() && requester != null) {
            SwingUtilities.invokeLater(() -> {
                ImageRequest request = findImageRequest(requester.getImageType(), requester.getImageName());
                if (request == null) {
                    request = new ImageRequest(requester.getImageName(), requester.getImageType(), requester);

                    imageRequests.add(request);
                    fetchImage(request.getImageType(), request.getImageName());
                } else {
                    request.addRequester(requester);
                }
            });
        }
    }

    private class ImageRequest {

        private String imageName;
        private ImageType imageType;
        private List<ImageRequester> imageRequesters;

        ImageRequest(String imageName, ImageType imageType, ImageRequester imageRequest) {
            this.imageName = imageName;
            this.imageType = imageType;
            this.imageRequesters = new ArrayList<>();
            this.imageRequesters.add(imageRequest);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ImageRequest)) return false;
            ImageRequest that = (ImageRequest) o;
            return Objects.equals(getImageName(), that.getImageName()) &&
                    getImageType() == that.getImageType();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getImageName(), getImageType());
        }

        boolean isThis(ImageType imageType, String imageName) {
            return imageType != null && imageName != null && getImageType().equals(imageType) && getImageName().equals(imageName);
        }

        void addRequester(ImageRequester requester) {
            if (!imageRequesters.contains(requester)) {
                imageRequesters.add(requester);
            }
        }

        String getImageName() {
            return imageName;
        }

        ImageType getImageType() {
            return imageType;
        }

        List<ImageRequester> getImageRequesters() {
            return imageRequesters;
        }
    }

}
