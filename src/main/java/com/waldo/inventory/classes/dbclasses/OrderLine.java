package com.waldo.inventory.classes.dbclasses;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.classes.Price;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class OrderLine extends DbObject {

    public static final String TABLE_NAME = "orderlines";

    // Variables
    private long orderId;
    private Order order;

    // If order type = Item
    private long itemId;
    private Item item;

    // If order type = ProjectPcb
    private long projectPcbId;
    private ProjectPcb projectPcb;

    private DistributorPartLink distributorPartLink;

    protected int amount;

    public OrderLine() {
        super(TABLE_NAME);
    }

    public OrderLine(@NotNull Order order, @NotNull Item item, int amount) {
        this();
        this.order = order;
        this.orderId = order.getId();

        this.item = item;
        this.itemId = item.getId();

        this.projectPcb = null;
        this.projectPcbId = UNKNOWN_ID;

        this.amount = amount;
    }

    public OrderLine(@NotNull Order order, @NotNull ProjectPcb projectPcb, int amount) {
        this();

        this.order = order;
        this.orderId = order.getId();

        this.item = null;
        this.itemId = UNKNOWN_ID;

        this.projectPcb = projectPcb;
        this.projectPcbId = projectPcb.getId();

        this.amount = amount;
    }

    @Override
    public String toString() {
        return getName() + super.toString();
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;//addBaseParameters(statement);

        statement.setInt(ndx++, getAmount());

        statement.setLong(ndx++, getOrderId());
        statement.setLong(ndx++, getItemId());
        statement.setLong(ndx++, getPcbId());

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
    public OrderLine createCopy() {
        return createCopy(new OrderLine());
    }

    @Override
    public OrderLine createCopy(DbObject copyInto) {
        OrderLine cpy = (OrderLine) copyInto;
        copyBaseFields(cpy);
        cpy.setOrderId(getOrderId());
        cpy.setAmount(getAmount());
        cpy.setItemId(getItemId());
        cpy.setPcbId(getPcbId());
        return cpy;
    }


    public Price getPrice() {
        if (getDistributorPartLink() != null) {
            return distributorPartLink.getPrice();
        }
        return new Price();
    }

    public Price getTotalPrice() {
        return Price.multiply(getPrice(), getAmount());
    }

    public boolean isLocked() {
        return getOrder() != null && order.isLocked();
    }

    public void updateLineAmount(boolean increment) {
        if (getOrder() != null) {
            switch (order.getDistributorType()) {
                case Items:
                    if (getItem() != null) {
                        int current = item.getAmount();
                        if (increment) {
                            item.setAmount(current + getAmount());
                        } else {
                            item.setAmount(current - getAmount());
                            if (item.getAmount() < 0) {
                                item.setAmount(0);
                            }
                        }
                        item.save();
                    }
                    break;

                case Pcbs:
                    if (getPcb() != null) {
                        int current = projectPcb.getAmount();
                        if (increment) {
                            projectPcb.setAmount(current + getAmount());
                        } else {
                            projectPcb.setAmount(current - getAmount());
                            if (projectPcb.getAmount() < 0) {
                                projectPcb.setAmount(0);
                            }
                        }
                        projectPcb.save();
                    }
                    break;
            }
        }
    }

    public DistributorPartLink getDistributorPartLink() {
        if (distributorPartLink == null && getOrder() != null) {
            switch (order.getDistributorType()) {
                case Items:
                    if (getItem() != null) {
                        distributorPartLink = SearchManager.sm().findDistributorPartLink(order.getDistributorId(), item);
                    }
                    break;

                case Pcbs:
                    if (getPcb() != null) {
                        distributorPartLink = SearchManager.sm().findDistributorPartLink(order.getDistributorId(), projectPcb);
                    }
                    break;
            }
        }
        return distributorPartLink;
    }

    public void updateOrderState() {
        if (getOrder() != null) {
            switch (order.getDistributorType()) {
                case Items:
                    if (getItem() != null) {
                        item.updateOrderState();
                    }
                    break;

                case Pcbs:
                    if (getPcb() != null) {
                        projectPcb.updateOrderState();
                    }
                    break;
            }
        }
    }

    public boolean isItemOrderType() {
        return getOrder() != null && getOrder().getDistributorType().equals(DistributorType.Items);
    }

    //
    // Getters, setters
    //
    public long getOrderId() {
        if (orderId <= UNKNOWN_ID) {
            orderId = UNKNOWN_ID;
        }
        return orderId;
    }

    public Order getOrder() {
        if (order == null && orderId > UNKNOWN_ID) {
            order = SearchManager.sm().findOrderById(getOrderId());
        }
        return order;
    }

    public void setOrderId(long orderId) {
        if (order != null && order.getId() != orderId) {
            order = null;
        }
        this.orderId = orderId;
    }


    public long getItemId() {
        if (itemId < UNKNOWN_ID) {
            itemId = UNKNOWN_ID;
        }
        return itemId;
    }

    public Item getItem() {
        if (item == null && getItemId() > UNKNOWN_ID) {
            item = SearchManager.sm().findItemById(itemId);
        }
        return item;
    }

    public void setItemId(long itemId) {
        if (getOrder() != null && order.getDistributorType() == DistributorType.Items) {
            if (item != null && item.getId() != itemId) {
                item = null;
            }
            this.itemId = itemId;
        }
    }



    public long getPcbId() {
        if (projectPcbId < UNKNOWN_ID) {
            projectPcbId = UNKNOWN_ID;
        }
        return projectPcbId;
    }

    public ProjectPcb getPcb() {
        if (projectPcb == null && getPcbId() > UNKNOWN_ID) {
            projectPcb = SearchManager.sm().findProjectPcbById(projectPcbId);
        }
        return projectPcb;
    }

    public void setPcbId(long pcbId) {
        if (getOrder() != null && order.getDistributorType() == DistributorType.Pcbs) {
            if (projectPcb != null && projectPcb.getId() != pcbId) {
                projectPcb = null;
            }
            this.projectPcbId = pcbId;
        }
    }



    public long getDistributorPartId() {
        if (distributorPartLink == null) {
            distributorPartLink = getDistributorPartLink();
        }
        if (distributorPartLink != null) {
            return distributorPartLink.getId();
        }
        return UNKNOWN_ID;
    }

    public void updateDistributorPart() {
        distributorPartLink = null;
    }


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String getName() {
        if (getOrder() != null) {
            switch (order.getDistributorType()) {
                case Items:
                    if (getItem() != null) {
                        return getItem().getName();
                    }
                case Pcbs:
                    if (getPcb() != null) {
                        return getPcb().getName();
                    }
            }
        }
        return "";
    }
}