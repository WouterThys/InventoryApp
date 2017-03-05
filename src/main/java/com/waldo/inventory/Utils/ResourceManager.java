package com.waldo.inventory.Utils;

import javax.swing.*;
import java.awt.*;
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
    private String propertiesFile;
    private String resourceURL;

    /**
     *
     * @param propertiesFile
     */
    public ResourceManager(String propertiesFile) {
        this.propertiesFile = propertiesFile;
        resourceURL = propertiesFile.substring(0, propertiesFile.lastIndexOf(File.separator)+1);
        properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException ex) {
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
     * from the path retieved
     * @param resourceURL
     * @return
     */
    public ImageIcon readImage(URL resourceURL) {
        Image img = Toolkit.getDefaultToolkit().createImage(resourceURL);
        return new ImageIcon(img);
    }

    public ImageIcon readImage(String key) {
        return new ImageIcon(Toolkit.getDefaultToolkit().createImage(resourceURL + readString(key)));
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
