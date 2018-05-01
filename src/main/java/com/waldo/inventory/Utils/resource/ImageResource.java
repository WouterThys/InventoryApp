package com.waldo.inventory.Utils.resource;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ImageResource extends Resource implements Client.ImageClientListener {

    public interface ImageRequester {
        ImageType getImageType();
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

    public static final String DEFAULT = "default";

    // Local images
    private final Map<String, ImageIcon> iconImageMap = new HashMap<>();

    // Cache for server images
    private final Map<String, ImageIcon> itemImageMap = new HashMap<>();
    private final Map<String, ImageIcon> distributorImageMap = new HashMap<>();
    private final Map<String, ImageIcon> manufacturerImageMap = new HashMap<>();
    private final Map<String, ImageIcon> ideImageMap = new HashMap<>();
    private final Map<String, ImageIcon> projectImageMap = new HashMap<>();
    private final Map<String, ImageIcon> otherImageMap = new HashMap<>();

    // Requests for image
    private ArrayList<ImageRequester> imageRequests = new ArrayList<>();

    private Client client;

    public void init(String propertiesUrl, String fileName) throws IOException {
        super.initProperties(propertiesUrl, fileName);

        iconImageMap.put(DEFAULT, readIcon("Common.UnknownIcon32"));
        itemImageMap.put(DEFAULT, readIcon("Items.Edit.Title"));
        distributorImageMap.put(DEFAULT, readIcon("Distributors.Title"));
        manufacturerImageMap.put(DEFAULT, readIcon("Manufacturers.Title"));
        ideImageMap.put(DEFAULT, readIcon("Ides.Title"));
        projectImageMap.put(DEFAULT, readIcon("Projects.Icon"));
        otherImageMap.put(DEFAULT, readIcon("Common.UnknownIcon48 "));

        updateImageServerConnection();
    }

    public ImageIcon getDefaultImage(ImageType imageType) {
        switch (imageType) {
            case ItemImage: return itemImageMap.get(DEFAULT);
            case IdeImage: return ideImageMap.get(DEFAULT);
            case ProjectImage: return projectImageMap.get(DEFAULT);
            case DistributorImage: return distributorImageMap.get(DEFAULT);
            case ManufacturerImage: return manufacturerImageMap.get(DEFAULT);
            //case DivisionImage: return .get(DEFAULT);
            default: return otherImageMap.get(DEFAULT);
        }
    }

    public void requestImage(final ImageRequester imageRequester) {

            if (imageRequester != null) {
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

                    default:
                        break;
                }
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
        String name = requester.getImageName();
        ImageType imageType = requester.getImageType();
        if ((name != null) && (!name.isEmpty())) {
            if (map.containsKey(name)) {
                requester.setImage(map.get(name));
                imageRequests.remove(requester);
            } else {
                if (!imageRequests.contains(requester)) {
                    imageRequests.add(requester);
                    SwingUtilities.invokeLater(() -> fetchImage(imageType, name));
                }
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


    //
    // Image server client
    //

    public Client getClient() {
        return client;
    }

    public boolean serverConnected() {
        return client != null && client.isConnected();
    }

    public void updateImageServerConnection() {
        ImageServerSettings imageServerSettings = SettingsManager.settings().getImageServerSettings();
        String clientName = imageServerSettings.getConnectAsName();
        if (client == null || !client.getClientName().equalsIgnoreCase(clientName) || !client.isConnected()) {

            if (client != null) {
                client.disconnectClient(true);
            }

            client = new Client(imageServerSettings.getImageServerName(), imageServerSettings.getConnectAsName());
            client.addImageClientListener(this);
            client.connectClient(imageServerSettings.getConnectAsName());
        }
    }

    private void fetchImage(ImageType imageType, String name) {
        try {
            client.getImage(name, imageType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ImageIcon saveImage(BufferedImage image, ImageType imageType, String imageName) {
        ImageIcon imageIcon = null;
        try {
            if (image != null) {
                client.sendImage(image, imageName, imageType);
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
        return imageIcon;
    }

    public ImageIcon saveImage(File file, ImageType imageType, String imageName) {
        ImageIcon imageIcon = null;
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
        return imageIcon;
    }

    private void checkRequests(ImageIcon icon, ImageType imageType, String name) {

        List<ImageRequester> copyOfList = new ArrayList<>(imageRequests);

        for (ImageRequester requester : copyOfList) {
            // Request again
            requestImage(requester);
        }
    }

    @Override
    public void onConnected(String clientName) {
        System.out.println("Client " + clientName + " connected");
    }

    @Override
    public void onDisconnected(String clientName) {
        System.out.println("Client " + clientName + " disconnected");
    }

    @Override
    public void onImageTransmitted(String imageName, ImageType imageType) {
        System.out.println("Image " + imageName + " transmitted");
    }

    @Override
    public void onImageReceived(BufferedImage bufferedImage, String imageName, ImageType imageType) {
        System.out.println("Image "+ imageName +" received");

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
        }
    }
}
