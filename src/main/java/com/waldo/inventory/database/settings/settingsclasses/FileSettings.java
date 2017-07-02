package com.waldo.inventory.database.settings.settingsclasses;

public class FileSettings extends DbSettingsObject {

    private String imgDistributorsPath;
    private String imgDivisionsPath;
    private String imgIdesPath;
    private String imgItemsPath;
    private String imgManufacturersPath;
    private String imgProjectsPath;

    private String fileOrdersPath;

    @Override
    public DbSettingsObject creatCopy(DbSettingsObject original) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
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
