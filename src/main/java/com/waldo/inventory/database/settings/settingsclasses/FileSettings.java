package com.waldo.inventory.database.settings.settingsclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;

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

    public FileSettings(String name) {
        this();
        setName(name);
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
    public FileSettings createCopy(DbObject copyInto) {
        FileSettings copy = (FileSettings) copyInto;
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

    @Override
    public FileSettings createCopy() {
        return createCopy(new FileSettings());
    }

    @Override
    public void tableChanged(Statics.QueryType changedHow) {

    }

    public String getImgDistributorsPath() {
        if (imgDistributorsPath == null) {
            return "";
        }
        return imgDistributorsPath;
    }

    public void setImgDistributorsPath(String imgDistributorsPath) {
        this.imgDistributorsPath = imgDistributorsPath;
    }

    public String getImgDivisionsPath() {
        if (imgDivisionsPath == null) {
            imgDivisionsPath = "";
        }
        return imgDivisionsPath;
    }

    public void setImgDivisionsPath(String imgDivisionsPath) {
        this.imgDivisionsPath = imgDivisionsPath;
    }

    public String getImgIdesPath() {
        if (imgIdesPath == null) {
            imgIdesPath = "";
        }
        return imgIdesPath;
    }

    public void setImgIdesPath(String imgIdesPath) {
        this.imgIdesPath = imgIdesPath;
    }

    public String getImgItemsPath() {
        if (imgItemsPath == null) {
            imgItemsPath =  "";
        }
        return imgItemsPath;
    }

    public void setImgItemsPath(String imgItemsPath) {
        this.imgItemsPath = imgItemsPath;
    }

    public String getImgManufacturersPath() {
        if (imgManufacturersPath == null) {
            imgManufacturersPath = "";
        }
        return imgManufacturersPath;
    }

    public void setImgManufacturersPath(String imgManufacturersPath) {
        this.imgManufacturersPath = imgManufacturersPath;
    }

    public String getImgProjectsPath() {
        if (imgProjectsPath == null) {
            imgProjectsPath = "";
        }
        return imgProjectsPath;
    }

    public void setImgProjectsPath(String imgProjectsPath) {
        this.imgProjectsPath = imgProjectsPath;
    }

    public String getFileOrdersPath() {
        if (fileOrdersPath == null) {
            fileOrdersPath = "";
        }
        return fileOrdersPath;
    }

    public void setFileOrdersPath(String fileOrdersPath) {
        this.fileOrdersPath = fileOrdersPath;
    }
}
