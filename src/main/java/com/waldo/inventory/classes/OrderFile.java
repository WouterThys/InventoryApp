package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.waldo.inventory.gui.Application.startUpPath;

public class OrderFile {

    private static final Logger LOG = LoggerFactory.getLogger(OrderFile.class);
    public static final String SEPARATOR = ",";

    private Order order;
    private File orderFile;
    private boolean success = false;
    private List<String> errorMessages = new ArrayList<>();
    private int orderType;

    public OrderFile(Order order) {
        this.order = order;
    }

    public void loadOrderFile(String fileName) {
        success = false;
        if (order.getDistributor() != null && fileName != null && !fileName.isEmpty()) {
            orderType = (int) order.getDistributor().getId();
            orderFile = new File(startUpPath + "orderfiles" + File.separator+ fileName);
            if (orderFile.exists()) {
                success = true;
            }
        }
    }

    public void createOrderFile() {
        if (!isClearToOrder()) {
            success = false;
            return;
        }

        orderType = (int)order.getDistributor().getId();
        try {
            orderFile = createOrderFile(order);
            success = true;
        } catch (FileNotFoundException e) {
            LOG.error("File not found.", e);
            errorMessages.add(e.getMessage());
            success = false;
        }

//        switch (orderType) {
//            case 2: // Mouser
//                orderFile = createMouserOrderFile();
//                success = true;
//                break;
//
//            case 3: // Farnell
//                try {
//                    orderFile = createOrderFile(order);
//                    success = true;
//                } catch (FileNotFoundException e) {
//                    errorMessages.add(e.getMessage());
//                    success = false;
//                }
//                break;
//        }
    }

    private boolean isClearToOrder() {
        boolean ok = true;
        if (order.isOrdered()) {
            ok = false;
            errorMessages.add("Order is already ordered");
        }

        if (order.isReceived()) {
            ok = false;
            errorMessages.add("Order is already received");
        }

        if (order.getDistributor() == null) {
            ok = false;
            errorMessages.add("Distributor is empty");
        }

        List<OrderItem> missingRefs = order.missingOrderReferences();
        if (missingRefs.size() > 0) {
            ok = false;
            String msg = "Next orders don't have an order reference: \n";
            for (OrderItem oi : missingRefs) {
                msg += oi.getItem().getName() + " \n";
            }
            errorMessages.add(msg);
        }

        return ok;
    }


    public String getRawOrderString() {
        return FileUtils.getRawStringFromFile(orderFile);
    }

    private File createOrderFile(Order order) throws FileNotFoundException {
        File orderFile = new File(".","OrderFiles/"+createOrderFileName(order));
        PrintWriter pw = new PrintWriter(orderFile);
        StringBuilder sb = new StringBuilder();

        for (OrderItem oi : order.getOrderItems()) {
            sb.append(oi.getItemRef()).append(SEPARATOR);
            sb.append(oi.getAmount()).append('\n');
            //String description = oi.getItem().getDescription().replace(',', ' ');
            //sb.append(description).append('\n');
        }

        pw.write(sb.toString());
        pw.close();

        return orderFile;
    }

    private String createOrderFileName(Order order) {
        SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyyMMdd");
        String fileName = dateFormatShort.format(new Date(Calendar.getInstance().getTimeInMillis()));
        String name = order.getName().replace(' ', '_');
        fileName += "_" + name + "_"+order.getDistributor().getName()+"_OrderFile.csv";
        return fileName;
    }

    public File getOrderFile() {
        return orderFile;
    }

    public String getOrderFileName() {
        if (orderFile != null) {
            return orderFile.getName();
        }
        return "";
    }

    public String getOrderFilePath() {
        if (orderFile != null) {
            return orderFile.getPath();
        }
        return "";
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public Order getOrder() {
        return order;
    }

    public int getOrderType() {
        return orderType;
    }
}
