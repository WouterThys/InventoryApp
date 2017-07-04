package com.waldo.inventory.database.settings.settingsclasses;

public class FileSettings extends DbSettingsObject {

    private static final String TABLE_NAME = "filesettings";

    private String imgDistributorsPath;
    private String imgDivisionsPath;
    private String imgIdesPath;
    private String imgItemsPath;
    private String imgManufacturersPath;
    private String imgProjectsPath;

    private String fileOrdersPath;


    public FileSettings() {
        super(TABLE_NAME);
    }



    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (obj instanceof FileSettings) {
                FileSettings ref = (FileSettings) obj;
                if ((ref.getImgDistributorsPath().equals(getImgDistributorsPath())) &&
                        (ref.getImgDivisionsPath().equals(getImgDivisionsPath())) &&
                        (ref.getImgIdesPath().equals(getImgIdesPath())) &&
                        (ref.getImgItemsPath().equals(getImgItemsPath())) &&
                        (ref.getImgManufacturersPath().equals(getImgManufacturersPath())) &&
                        (ref.getImgProjectsPath().equals(getImgProjectsPath())) &&
                        (ref.getFileOrdersPath().equals(getFileOrdersPath()))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public FileSettings createCopy() {
        FileSettings copy = new FileSettings();
        copyBaseFields(copy);
        copy.setImgDistributorsPath(imgDistributorsPath);
        copy.setImgDivisionsPath(imgDivisionsPath);
        copy.setImgIdesPath(imgIdesPath);
        copy.setImgItemsPath(imgItemsPath);
        copy.setImgManufacturersPath(imgManufacturersPath);
        copy.setImgProjectsPath(imgProjectsPath);
        copy.setFileOrdersPath(fileOrdersPath);
        return copy;
    }


    public String getImgDistributorsPath() {
        return imgDistributorsPath;
    }

    public void setImgDistributorsPath(String imgDistributorsPath) {
        this.imgDistributorsPath = imgDistributorsPath;
    }

    public String getImgDivisionsPath() {
        return imgDivisionsPath;
    }

    public void setImgDivisionsPath(String imgDivisionsPath) {
        this.imgDivisionsPath = imgDivisionsPath;
    }

    public String getImgIdesPath() {
        return imgIdesPath;
    }

    public void setImgIdesPath(String imgIdesPath) {
        this.imgIdesPath = imgIdesPath;
    }

    public String getImgItemsPath() {
        return imgItemsPath;
    }

    public void setImgItemsPath(String imgItemsPath) {
        this.imgItemsPath = imgItemsPath;
    }

    public String getImgManufacturersPath() {
        return imgManufacturersPath;
    }

    public void setImgManufacturersPath(String imgManufacturersPath) {
        this.imgManufacturersPath = imgManufacturersPath;
    }

    public String getImgProjectsPath() {
        return imgProjectsPath;
    }

    public void setImgProjectsPath(String imgProjectsPath) {
        this.imgProjectsPath = imgProjectsPath;
    }

    public String getFileOrdersPath() {
        return fileOrdersPath;
    }

    public void setFileOrdersPath(String fileOrdersPath) {
        this.fileOrdersPath = fileOrdersPath;
    }
}
