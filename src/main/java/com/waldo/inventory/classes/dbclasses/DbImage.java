package com.waldo.inventory.classes.dbclasses;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.Statics;
import com.waldo.test.ImageSocketServer.ImageType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbImage extends DbObject {

    private ImageType imageType;
    private ImageIcon image;

    public DbImage(@NotNull ImageType imageType) {
        this(imageType, null, "");
    }

    public DbImage(@NotNull ImageType imageType, ImageIcon image, String name) {
        super(imageType.getFolderName());
        this.imageType = imageType;
        this.image = image;
        this.name = name;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        statement.setString(ndx++, getName());
        statement.setInt(ndx++, getImageType().getDimension().width);
        statement.setInt(ndx++, getImageType().getDimension().height);
        statement.setInt(ndx++, getImageType().getId());
        statement.setBytes(ndx++, imageToByteArray(image));

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
        cpy.imageType = imageType;
        cpy.image = image;
        return cpy;
    }


    public static ImageIcon blobToImage(Blob blob) {
        ImageIcon icon = null;

        if (blob != null) {
            try (InputStream is = blob.getBinaryStream(0, blob.length())) {
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

    public ImageIcon getImage() {
        return image;
    }
}
