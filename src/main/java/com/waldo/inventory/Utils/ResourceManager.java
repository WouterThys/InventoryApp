package com.waldo.inventory.Utils;

import com.waldo.inventory.managers.LogManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ResourceManager {

    private static final LogManager LOG = LogManager.LOG(ResourceManager.class);
    private Properties properties;
    private InputStream resourceInput;
    private String resourceFileName;


    public ResourceManager(String propertiesUrl, String fileName) {
        properties = new Properties();
        try {
            resourceFileName = propertiesUrl + fileName;
            resourceInput = getClass().getClassLoader().getResourceAsStream(resourceFileName);
            properties.load(resourceInput);
        } catch (Exception ex) {
            LOG.error("Error creating ResourceManager", ex);
        }
    }


    /**
     * Reads int from the properties file
     * @param key
     * @return
     */
    public int readInteger(String key) {
        int val = 0;
        try {
            val = Integer.parseInt(properties.getProperty(key));

        } catch (Exception ex) {
            //ignore
        }
        return val;
    }

    public int readInteger(String key,int radix) {
        int val = 0;
        try {
            val = Integer.parseInt(properties.getProperty(key),radix);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return val;
    }

    /**
     * Reads long from the properties file
     * @param key
     * @return
     */
    public long readLong(String key) {
        long val = 0;
        try {
            val = Long.parseLong(properties.getProperty(key));
        } catch (Exception ex) {
            //ignore
        }
        return val;
    }

    /**
     * Reads the string from the properties file
     * @param key
     * @return
     */
    public String readString(String key) {
        return properties.getProperty(key);
    }

    /**
     *  Reads the icon path from the properties file and gets the icon
     * from the path retrieved
     * @param resourceURL
     * @return
     */
    public ImageIcon readImage(URL resourceURL) {
        Image img = Toolkit.getDefaultToolkit().createImage(resourceURL);
        if (img == null) {
            return readImage("Common.UnknownIcon32");
        } else {
            return new ImageIcon(img);
        }
    }

    public ImageIcon readImage(String key) {
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("icons/" + readString(key));
            return new ImageIcon(ImageIO.read(is));
        } catch (Exception e) {
            Status().setWarning("Error loading image icon " + key, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Status().setError("Error closing input stream", e);
                }
            }
        }
        return null;
    }

    public ImageIcon readImage(String key, int size) {
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("icons/" + readString(key));
            BufferedImage image = ImageIO.read(is);
            return getScaledImageIcon(image, size, size);
        } catch (Exception e) {
            Status().setWarning("Error loading image icon " + key, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Status().setError("Error closing input stream", e);
                }
            }
        }
        return null;
    }

    public ImageIcon readImage(URL resourceURL, int width, int height) throws Exception {
        return getScaledImageIcon(ImageIO.read(resourceURL), width, height);
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

    public ImageIcon readImage (String path, int width, int height) {
        File imageFile = new File(path);
        if (imageFile.exists() && imageFile.isFile()) {
            try {
                return readImage(imageFile.toURI().toURL(), width, height);
            } catch (Exception e) {
                LOG.warning("Error reading image at path: " + path, e);
            }
        }
        return readImage("Common.UnknownIcon48");
    }

    /**
     *
     * @param resourceURL
     * @return
     */
    public File readFile(URL resourceURL)  {
        File file = null;
        try {
            file =  new File(resourceURL.toURI());
        } catch (URISyntaxException ex) {
            LOG.error("Error reading file", ex);
        }
        return file;
    }
}
