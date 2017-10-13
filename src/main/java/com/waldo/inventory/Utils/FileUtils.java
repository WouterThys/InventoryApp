package com.waldo.inventory.Utils;

import com.waldo.inventory.classes.ProjectIDE;
import org.apache.commons.io.IOUtils;

import javax.sql.rowset.serial.SerialBlob;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public final static String shell = "sh";


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

    public static String getLastPathPart(String path) {
        String result = "";
        if (!path.isEmpty()) {
            int ndx = path.lastIndexOf(File.separator);
            result = path.substring(ndx+1, path.length());
        }
        return result;
    }


    public static ImageFilter getImageFilter() {
        return new ImageFilter();
    }

    public static CsvFilter getCsvFilter() {
        return new CsvFilter();
    }

    public static ShellFilter getShellFilter() {
        return new ShellFilter();
    }

    public static DbFilter getDbFilter() {
        return new DbFilter();
    }

    public static IDEFilter getIDEFilter(List<ProjectIDE> projectIDEs) {
        return new IDEFilter(projectIDEs);
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

    private static class ShellFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = FileUtils.getExtension(f);
            if (extension != null) {
                if (extension.equals(FileUtils.shell)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return ".sh files";
        }
    }

    private static class IDEFilter extends  FileFilter {

        private List<ProjectIDE> ideList;

        IDEFilter(List<ProjectIDE> ideList) {
            this.ideList = ideList;
        }

        @Override
        public boolean accept(File f) {
            return false;
        }

        @Override
        public String getDescription() {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (ProjectIDE pi : ideList) {
                if (first) {
                    builder.append(pi.getName()).append(" (").append(pi.getExtension()).append(")");
                } else {
                    builder.append(", ").append(pi.getName()).append(" (").append(pi.getExtension()).append(")");
                }
                first = false;
            }
            return builder.toString();
        }
    }

    private static class IDEFileChooser extends JFileChooser {
        @Override
        public void approveSelection() {
            super.approveSelection();
        }
    }

    public static String getRawStringFromFile(File file) {
        StringBuilder result = new StringBuilder();
        if (file != null) {
            if (file.exists()) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line).append("\n");
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
        return result.toString();
    }


    public static List<File> findFileInFolder(File folder, String extension, boolean filesOnly) {
        List<File> files = new ArrayList<>();
        if (folder.exists() && folder.isDirectory()) {
            File[] containedFiles = folder.listFiles();
            if (containedFiles != null) {
                for (File f : containedFiles) {
                    if (f.isDirectory()) {
                        if (f.toString().endsWith(extension) && !filesOnly) {
                            files.add(f);
                        } else {
                            files.addAll(findFileInFolder(f, extension, filesOnly));
                        }
                    } else {
                        if (f.toString().endsWith(extension)) {
                            files.add(f);
                        }
                    }
                }
            }
        }
        return files;
    }

    public static boolean isOrContains(File folder, String extension) {
        return is(folder, extension) || contains(folder, extension);
    }

    public static boolean is(File folder, String extension) {
        boolean result = false;

        if (folder.exists()) {
            if (folder.toString().endsWith(extension)) {
                result = true;
            }
        }

        return result;
    }

    public static boolean contains(File folder, String extension) {
        boolean result = false;

        if (folder.exists() && folder.isDirectory()) {

                File[] containedFiles = folder.listFiles();
                if (containedFiles != null) {
                    for (File f : containedFiles) {
                        if (f.toString().endsWith(extension)) {
                            result = true;
                            break;
                        } else {
                            if (f.isDirectory() && contains(f, extension)) {
                                result = true;
                                break;
                            }
                        }
                    }
                }

        }

        return result;
    }

    public static List<File> containsGetParents(File folder, String extension) {
        List<File> files = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {

            File[] containedFiles = folder.listFiles();
            if (containedFiles != null) {
                for (File f : containedFiles) {
                    if (f.toString().endsWith(extension)) {
                        files.add(folder);
                    } else {
                        if (f.isDirectory()) {
                            files.addAll(containsGetParents(f, extension));
                        }
                    }
                }
            }

        }

        return files;
    }

    public static String formatFileNameString(String text) {
        String result = text;
        if (result.length() > 12) {
            String[] split = result.split("(?=\\p{Lu})|(?=\\.)|(?<=\\_)|(?<=\\-)");
            if (split.length > 1) {
                int middle = split.length / 2;
                StringBuilder first = new StringBuilder();
                StringBuilder second = new StringBuilder();
                for (int i = 0; i < middle; i++) {
                    first.append(split[i]);
                }
                for (int i = middle; i < split.length; i++) {
                    second.append(split[i]);
                }

                if (first.length() > 12) {
                    first = new StringBuilder(formatFileNameString(first.toString()));
                }

                if (second.length() > 12) {
                    second = new StringBuilder(formatFileNameString(second.toString()));
                }

                result = first + "\n" + second;
            }
        }

        return result;
    }

    public static String createIconPath(String initialPath, String iconPath) {
        String result = iconPath;
        if(iconPath.startsWith(initialPath) || iconPath.contains(initialPath)) {
            int ndx = iconPath.lastIndexOf(File.separator);
            result = iconPath.substring(ndx + 1, iconPath.length());
        }
        return result;
    }

    public static File blobToFile(Blob blob, String fileName) throws SQLException {
        File f = null;
        if (blob != null && blob.length() > 0) {
            try {
                f = createTempFile(fileName);
                try (InputStream in = blob.getBinaryStream();
                     OutputStream out = new FileOutputStream(f)) {
                    byte[] buff = new byte[4096];
                    int len;
                    while ((len = in.read(buff)) != -1) {
                        out.write(buff, 0, len);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    public static SerialBlob fileToBlob(File file) throws SQLException {
        SerialBlob blob = null;
        if (file != null && file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                byte[] bytes = IOUtils.toByteArray(inputStream);
                blob = new SerialBlob(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return blob;
    }

    public static File createTempFile(String fileName) throws IOException {
        File file = File.createTempFile(fileName, "");
        file.deleteOnExit();
        return file;
    }

}
