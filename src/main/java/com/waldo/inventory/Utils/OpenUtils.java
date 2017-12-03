package com.waldo.inventory.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;

public class OpenUtils {

    public static void openPdf(String path) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(path));
        }
    }

    public static void browseLink(String link) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI.create(link));
        }
    }

    public static boolean isValidIpAddress(String ip) {
        if (ip.contains(".")) {
            String parts[] = ip.split("\\.");
            if (parts.length == 4) {
                for (String part : parts) {
                    try {
                        if (part.contains(":")) {
                            int ndx = part.indexOf(":");
                            part = part.substring(0,ndx);
                        }
                        int i = Integer.valueOf(part);
                        if (i < 0 || i > 255) {
                            return false;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

}
