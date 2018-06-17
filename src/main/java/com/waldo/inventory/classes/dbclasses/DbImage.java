package com.waldo.inventory.classes.dbclasses;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.ImageType;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.waldo.inventory.database.ImageDbAccess.imDb;
import static com.waldo.inventory.gui.Application.scriptResource;

public class DbImage extends DbObject {

    private ImageType imageType;
    private ImageIcon imageIcon;


    private List<ProjectIDE> dbObjects;

    public DbImage(@NotNull ImageType imageType) {
        this(imageType, null, "");
    }

    public DbImage(@NotNull ImageType imageType, ImageIcon imageIcon, String name) {
        super(imageType.getFolderName());
        this.imageType = imageType;
        this.imageIcon = imageIcon;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DbImage dbImage = (DbImage) o;
        return getId() == dbImage.getId() &&
                Objects.equals(getName(), dbImage.getName()) &&
                (getImageType() == dbImage.getImageType());
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), getImageType());
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        statement.setString(ndx++, getName());
        statement.setInt(ndx++, getImageType().getId());
        SerialBlob b = new SerialBlob(imageToByteArray(imageIcon));
        statement.setBlob(ndx++, b);

        return ndx;
    }

    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert:
                break;
            case Delete:
                break;
        }
    }

    @Override
    public DbImage createCopy() {
        return createCopy(new DbImage(ImageType.Other));
    }

    @Override
    public DbImage createCopy(DbObject copyInto) {
        DbImage cpy = (DbImage) copyInto;
        copyBaseFields(cpy);
        cpy.imageType = imageType;
        cpy.imageIcon = imageIcon;
        return cpy;
    }


    @Override
    public void save() {
        if (canBeSaved) {
            if (id < 0 && !isInserted) {
                imDb().insert(this);
                isInserted = true;
            } else {
                imDb().update(this);
            }
        }
    }

    @Override
    public void delete() {
        if (canBeSaved) {
            if (id >= UNKNOWN_ID) {
                imDb().delete(this);
            }
        }
    }

    @Override
    public String getScript(String scriptName) {
        return scriptResource.readString(TABLE_NAME + "." + scriptName);
    }

    public String getScript(ImageType imageType, String scriptName) {
        return scriptResource.readString(imageType.getFolderName() + "." + scriptName);
    }



    public static ImageIcon blobToImage(Blob blob) {
        ImageIcon icon = null;

        if (blob != null) {
            try (InputStream is = blob.getBinaryStream(1, blob.length())) {
                BufferedImage bi = ImageIO.read(is);
                icon = new ImageIcon(bi);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }

        return icon;
    }

    public static byte[] imageToByteArray(ImageIcon imageIcon) {
        byte[] bytes = null;

        if (imageIcon != null) {
            BufferedImage bi = getBufferedImage(imageIcon.getImage());
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                ImageIO.write(bi, "png", os);
                bytes = os.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }

    public static BufferedImage getBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null),
                img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }



    public ImageType getImageType() {
        return imageType;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageType(int type) {
        this.imageType = ImageType.fromInt(type);
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }



    public void setDbObjects(List<ProjectIDE> objects) {
        this.dbObjects = objects;
    }

    @Override
    public void setId(long id) {
        super.setId(id);
        if (isInserted && dbObjects != null) {
            new Thread(() -> {
                for (DbObject object : dbObjects) {
                    object.setImageId(id);
                    object.save();
                }
            }).start();

        }
    }
}
