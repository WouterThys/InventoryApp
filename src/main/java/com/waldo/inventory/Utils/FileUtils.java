package com.waldo.inventory.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class FileUtils {

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

    public final static String csv = "csv";

    public final static String db = "db";


    public static ImageIcon loadImageIcon(final String name) {
        try {
            return new ImageIcon(FileUtils.class.getResource("/data/icons/" + name + "/" + name + "16" + ".png"));
        } catch (Exception e) {
            Status().setError("Error loading image icon " + name, e);
        }
        return null;
    }

    public static ImageIcon loadImageIcon(final String name, final int size) {
        try {
            return new ImageIcon(FileUtils.class.getResource("/data/icons/" + name + "/" + name + String.valueOf(size) + ".png"));
        } catch (Exception e) {
            Status().setError("Error loading image icon " + name, e);
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
        URL imgURL = FileUtils.class.getResource(path);
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

    public static CsvFilter getCsvFilter() {
        return new CsvFilter();
    }

    public static DbFilter getDbFilter() {
        return new DbFilter();
    }

    private static class ImageFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = FileUtils.getExtension(f);
            if (extension != null) {
                if (extension.equals(FileUtils.jpeg) || extension.equals(FileUtils.jpg) || extension.equals(FileUtils.png) || extension.equals(FileUtils.gif)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Jpeg, jpg, png and gif images";
        }
    }

    private static class DbFilter extends  FileFilter {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = FileUtils.getExtension(f);
            if (extension != null) {
                if (extension.equals(FileUtils.db)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "db files";
        }
    }

    private static class CsvFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = FileUtils.getExtension(f);
            if (extension != null) {
                if (extension.equals(FileUtils.csv)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return ".csv files";
        }
    }

    public static String getRawStringFromFile(File file) {
        String result = "";
        if (file != null) {
            if (file.exists()) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line + "\n";
                    }
                } catch (IOException e) {
                    Status().setError("Error getting raw string from file.", e);
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            Status().setError("Error closing buffered reader.", e);
                        }
                    }
                }
            }
        }
        return result;
    }
}
