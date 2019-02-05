package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.LabelAnnotationLink;
import com.waldo.inventory.Utils.Statics.LabelAnnotationType;
import com.waldo.inventory.managers.SearchManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class LabelAnnotation extends DbObject {

    public static final String TABLE_NAME = "labelannotations";

    private long locationLabelId;
    private LocationLabel locationLabel;

    private LabelAnnotationType type;
    private LabelAnnotationLink link;
    private double startX;
    private double startY;

    // For type text
    private String text;
    private String textFontName;
    private int textFontSize;

    // For image
    private ImageIcon image;
    private String imagePath;
    private double imageW;
    private double imageH;

    // Runtime
    private Image previewImage;

    public LabelAnnotation(long locationLabelId) {
        super(TABLE_NAME);
        setLocationLabelId(locationLabelId);
    }

    public LabelAnnotation(long locationLabelId, String name, LabelAnnotationType type) {
        this(locationLabelId);
        setName(name);
        setType(type);
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        statement.setString(ndx++, getName());

        statement.setLong(ndx++, getLocationLabelId());
        statement.setInt(ndx++, getType().getIntValue());
        statement.setInt(ndx++, getLink().getIntValue());
        statement.setDouble(ndx++, getStartX());
        statement.setDouble(ndx++, getStartY());

        // Text
        statement.setString(ndx++, getText());
        statement.setString(ndx++, getTextFontName());
        statement.setInt(ndx++, getTextFontSize());

        // Image
        statement.setString(ndx++, getImagePath());
        statement.setDouble(ndx++, getImageW());
        statement.setDouble(ndx++, getImageH());

        return ndx;
    }

    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                cache().add(this);
                break;
            }
            case Delete: {
                cache().remove(this);
                break;
            }
        }
    }

    @Override
    public LabelAnnotation createCopy() {
        return createCopy(new LabelAnnotation(getLocationLabelId()));
    }

    @Override
    public LabelAnnotation createCopy(DbObject copyInto) {
        LabelAnnotation cpy = new LabelAnnotation(getLocationLabelId());
        copyBaseFields(cpy);

        cpy.setType(getType());
        cpy.setLink(getLink());
        cpy.setStartX(getStartX());
        cpy.setStartY(getStartY());

        cpy.setText(getText());
        cpy.setTextFontName(getTextFontName());
        cpy.setTextFontSize(getTextFontSize());

        cpy.setImagePath(getImagePath());
        cpy.setImageW(getImageW());
        cpy.setImageH(getImageH());

        return cpy;
    }


    public long getLocationLabelId() {
        if (locationLabelId < UNKNOWN_ID) {
            locationLabelId = UNKNOWN_ID;
        }
        return locationLabelId;
    }

    public void setLocationLabelId(long locationLabelId) {
        if (locationLabel != null && locationLabel.getId() != locationLabelId) {
            locationLabel = null;
        }
        this.locationLabelId = locationLabelId;
    }

    public LocationLabel getLocationLabel() {
        if (locationLabel == null && getLocationLabelId() > UNKNOWN_ID) {
            locationLabel = SearchManager.sm().findLocationLabelById(locationLabelId);
        }
        return locationLabel;
    }



    public LabelAnnotationType getType() {
        if (type == null) {
            type = LabelAnnotationType.Unknown;
        }
        return type;
    }

    public void setType(int type) {
        this.type = LabelAnnotationType.fromInt(type);
    }

    public void setType(LabelAnnotationType type) {
        this.type = type;
    }


    public LabelAnnotationLink getLink() {
        if (link == null) {
            link = LabelAnnotationLink.Unknown;
        }
        return link;
    }

    public void setLink(LabelAnnotationLink link) {
        this.link = link;
    }

    public void setLink(int link) {
        this.link = LabelAnnotationLink.fromInt(link);
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        if (startX < 0) {
            startX = 0;
        }
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        if (startY < 0) {
            startY = 0;
        }
        this.startY = startY;
    }

    public String getText() {
        if (text == null) {
            text = "";
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextFontName() {
        if (textFontName == null) {
            textFontName = Font.SANS_SERIF;
        }
        return textFontName;
    }

    public void setTextFontName(String textFontName) {
        this.textFontName = textFontName;
    }

    public int getTextFontSize() {
        return textFontSize;
    }

    public void setTextFontSize(int textFontSize) {
        this.textFontSize = textFontSize;
    }

    public ImageIcon getImage() {
        if (image == null && !getImagePath().isEmpty()) {
            File file = new File(getImagePath());
            if (file.exists() && file.isFile()) {
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    image = new ImageIcon(bufferedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return image;
    }

    public String getImagePath() {
        if (imagePath == null) {
            imagePath = "";
        }
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        if (imagePath != null && !imagePath.equalsIgnoreCase(getImagePath())) {
            image = null;
            previewImage = null;
        }
        this.imagePath = imagePath;
    }

    public double getImageW() {
        return imageW;
    }

    public void setImageW(double imageW) {
        if (imageW < 0) {
            imageW = 0;
        }
        if (imageW != this.imageW) {
            previewImage = null;
        }
        this.imageW = imageW;
    }

    public double getImageH() {
        return imageH;
    }

    public void setImageH(double imageH) {
        if (imageH < 0) {
            imageH = 0;
        }
        if (imageH != this.imageH) {
            previewImage = null;
        }
        this.imageH = imageH;
    }

    public Image getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(Image previewImage) {
        this.previewImage = previewImage;
    }
}