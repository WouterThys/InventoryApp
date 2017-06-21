package com.waldo.inventory.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceManager {

    private Properties properties;
    private String resourceURL;


    public ResourceManager(String propertiesUrl, String fileName) {
        this.resourceURL = propertiesUrl;
        properties = new Properties();

        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(propertiesUrl + fileName));
        } catch (Exception ex) {
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.println(val);
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
        return new ImageIcon(Toolkit.getDefaultToolkit().createImage(resourceURL + readString(key)));
    }

    public ImageIcon readImage(URL resourceURL, int width, int height) throws IOException {
        Image img = ImageIO.read(resourceURL);
        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
//        if (img != null) {
//            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//            Graphics2D graphics2D = bufferedImage.createGraphics();
//
//            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//            graphics2D.drawImage(img, 0,0,width, height, null);
//            graphics2D.dispose();
//
//            return new ImageIcon(bufferedImage);
//        }
//        return null;
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
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }
}
