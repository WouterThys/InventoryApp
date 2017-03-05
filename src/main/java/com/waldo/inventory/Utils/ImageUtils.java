package com.waldo.inventory.Utils;

import javax.swing.*;

public class ImageUtils {

    public static final int ICON_SIZE_16 = 16;
    public static final int ICON_SIZE_24 = 24;
    public static final int ICON_SIZE_36 = 36;
    public static final int ICON_SIZE_48 = 48;


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
}
