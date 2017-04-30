package com.waldo.inventory.classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrderFile {

    private Order order;
    private File orderFile;
    private boolean success = false;
    private List<String> errorMessages = new ArrayList<>();
    private int orderType;

    public OrderFile(Order order) {
        this.order = order;
    }

    public void loadOrderFile(String fileName) {
        if (order.getDistributor() != null && fileName != null && !fileName.isEmpty()) {
            orderType = (int) order.getDistributor().getId();
            orderFile = new File(".","OrderFiles/"+fileName);
            success = true;
        } else {
            success = false;
        }
    }

    public void createOrderFile() {
        if (!isClearToOrder()) {
            success = false;
            return;
        }

        orderType = (int)order.getDistributor().getId();

        switch (orderType) {
            case 2: // Mouser
                orderFile = createMouserOrderFile();
                success = true;
                break;

            case 3: // Farnell
                try {
                    orderFile = createFarnellOrderFile(order);
                    success = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    errorMessages.add(e.getMessage());
                    success = false;
                }
                break;
        }
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


    private File createMouserOrderFile() {
        return null;
    }

    private File createFarnellOrderFile(Order order) throws FileNotFoundException {
        File orderFile = new File(".","OrderFiles/"+createOrderFileName(order));
        PrintWriter pw = new PrintWriter(orderFile);
        StringBuilder sb = new StringBuilder();

        for (OrderItem oi : order.getOrderItems()) {
            sb.append(oi.getItemRef()).append(',');
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
