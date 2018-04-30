package com.waldo.inventory.database.settings.settingsclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;

import java.util.Objects;

public class ImageServerSettings extends DbSettingsObject {

    private static final String TABLE_NAME = "imageserversettings";

    private String imageServerName;
    private String connectAsName;

    public ImageServerSettings() {
        super(TABLE_NAME);
    }

    public ImageServerSettings(String name) {
        this();
        setName(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageServerSettings)) return false;
        if (!super.equals(o)) return false;
        ImageServerSettings that = (ImageServerSettings) o;
        return Objects.equals(getImageServerName(), that.getImageServerName()) &&
                Objects.equals(getConnectAsName(), that.getConnectAsName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getImageServerName(), getConnectAsName());
    }

    @Override
    public ImageServerSettings createCopy(DbObject copyInto) {
        ImageServerSettings copy = (ImageServerSettings) copyInto;
        copyBaseFields(copy);
        copy.setImageServerName(getImageServerName());
        copy.setConnectAsName(getConnectAsName());
        return copy;
    }

    @Override
    public ImageServerSettings createCopy() {
        return createCopy(new ImageServerSettings());
    }

    @Override
    public void tableChanged(Statics.QueryType changedHow) {

    }

    public String getImageServerName() {
        if (imageServerName == null) {
            imageServerName = "";
        }
        return imageServerName;
    }

    public void setImageServerName(String imageServerName) {
        this.imageServerName = imageServerName;
    }

    public String getConnectAsName() {
        if (connectAsName == null) {
            connectAsName = "";
        }
        return connectAsName;
    }

    public void setConnectAsName(String connectAsName) {
        this.connectAsName = connectAsName;
    }
}
