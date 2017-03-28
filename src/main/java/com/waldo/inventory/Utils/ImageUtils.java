package com.waldo.inventory.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.net.URL;

public class ImageUtils {

    public static final int ICON_SIZE_16 = 16;
    public static final int ICON_SIZE_24 = 24;
    public static final int ICON_SIZE_36 = 36;
    public static final int ICON_SIZE_48 = 48;

    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";


    public static ImageIcon loadImageIcon(final String name) {
        try {
            return new ImageIcon(ImageUtils.class.getResource("/icons/" + name + "/" + name + "16" + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ImageIcon loadImageIcon(final String name, final int size) {
        try {
            return new ImageIcon(ImageUtils.class.getResource("/icons/" + name + "/" + name + String.valueOf(size) + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    public static ImageIcon createImageIcon(String path) {
        URL imgURL = ImageUtils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }


    public static ImageFilter getImageFilter() {
        return new ImageFilter();
    }

    private static class ImageFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = ImageUtils.getExtension(f);
            if (extension != null) {
                if (extension.equals(ImageUtils.jpeg) || extension.equals(ImageUtils.jpg) || extension.equals(ImageUtils.png)) {
                    return true;
                }
            } else {
                return false;
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Jpeg, jpg and png images";
        }
    }
}
