package com.waldo.inventory.Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class OfficeUtils {

    public static void test(BufferedImage bufferedImage) {
        Workbook workBook = new XSSFWorkbook();

        Sheet sheet = workBook.createSheet("Test");

        ByteArrayOutputStream baos;
        try {
            baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos );
            baos.flush();
            byte[] bytes = baos.toByteArray();

            // Add picture to work book
            int pictureNdx = workBook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            baos.close();

            //Returns an object that handles instantiating concrete classes
            CreationHelper helper = workBook.getCreationHelper();
            //Creates the top-level drawing patriarch.
            Drawing drawing = sheet.createDrawingPatriarch();

            //Create an anchor that is attached to the worksheet
            ClientAnchor anchor = helper.createClientAnchor();

            //create an anchor with upper left cell _and_ bottom right cell
            anchor.setCol1(1); //Column B
            anchor.setRow1(2); //Row 3
            anchor.setCol2(2); //Column C
            anchor.setRow2(3); //Row 4

            //Creates a picture
            Picture pict = drawing.createPicture(anchor, pictureNdx);

            //Reset the image to the original size
            //pict.resize(); //don't do that. Let the anchor resize the image!

            //Create the Cell B3
            Cell cell = sheet.createRow(2).createCell(1);

            //set width to n character widths = count characters * 256
            int widthUnits = 20*256;
            sheet.setColumnWidth(1, widthUnits);

            //set height to n points in twips = n * 20
            short heightUnits = 60*20;
            cell.getRow().setHeight(heightUnits);

            //Write the Excel file
            FileOutputStream fileOut;
            fileOut = new FileOutputStream("myFile.xlsx");
            workBook.write(fileOut);
            fileOut.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
