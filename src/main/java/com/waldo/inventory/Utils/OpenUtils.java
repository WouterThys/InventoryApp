package com.waldo.inventory.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

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

}
