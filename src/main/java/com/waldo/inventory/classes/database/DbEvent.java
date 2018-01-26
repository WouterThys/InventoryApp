package com.waldo.inventory.classes.database;

import com.waldo.inventory.Utils.Statics.EventIntervalField;
import com.waldo.inventory.Utils.Statics.EventType;
import com.waldo.utils.DateUtils;

import java.sql.Date;
import java.sql.Timestamp;

public class DbEvent {

    private String name;
    private String definer;
    private String definition; // Sql to execute
    private String comment;
    private Date created; // Date time the event was created
    private Date altered; // Date time the event was altered
    private Date lastExecuted;
    private boolean enabled;

    private EventType type;
    private Date executeAt; // For one time only events
    private int intervalValue; // For recurring events
    private EventIntervalField intervalField;
    private Date intervalStarts;
    private Date intervalEnds;

    public DbEvent() {

    }

    public DbEvent(String name) {
        setName(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof DbEvent) {
            DbEvent e = (DbEvent) obj;

            if (!e.getName().equals(getName())) return false;
            if (!e.getComment().equals(getComment())) return false;
            if (!e.getDefiner().equals(getDefiner())) return false;

            return true;
        }
        return false;
    }

    public static String sqlCreate(DbEvent event) {
        StringBuilder sql = new StringBuilder();

        if (event != null) {
            sql.append("CREATE EVENT\n");
            sql.append("\t").append(event.getName()).append("\n");
            sql.append("ON SCHEDULE\n");
            switch (event.getType()) {
                default:
                case OneTime:
                    sql.append("\tAT ");
                    sql.append(DateUtils.formatMySqlDateTime(event.getExecuteAt()));
                    sql.append("\n");
                    break;
                case Recurring:
                    sql.append("\tEVERY ");
                    sql.append(String.valueOf(event.getIntervalValue())).append(" ");
                    sql.append(event.getIntervalField().toString().toUpperCase()).append("\n");
                    if (event.getIntervalStarts() != null) {
                        sql.append("\tSTARTS ").append(DateUtils.formatMySqlDateTime(event.getIntervalStarts())).append("\n");
                    }
                    if (event.getIntervalEnds() != null) {
                        sql.append("ENDS ").append(DateUtils.formatMySqlDateTime(event.getIntervalEnds())).append("\n");
                    }
                    break;
            }
            if (!event.getComment().isEmpty()) {
                sql.append("COMMENT\n");
                sql.append("\t'").append(event.getComment()).append("'\n");
            }
            sql.append("DO\n\t");
            sql.append(event.getDefinition());
        }

        return sql.toString();
    }

    public static String sqlAlter(DbEvent event) {
        StringBuilder sql = new StringBuilder();

        if (event != null) {
            sql.append("ALTER EVENT\n");
            sql.append("\t").append(event.getName()).append("\n");
            sql.append("ON SCHEDULE\n");
            switch (event.getType()) {
                default:
                case OneTime:
                    sql.append("\tAT '");
                    sql.append(DateUtils.formatMySqlDateTime(event.getExecuteAt()));
                    sql.append("'\n");
                    break;
                case Recurring:
                    sql.append("\tEVERY ");
                    sql.append(String.valueOf(event.getIntervalValue())).append(" ");
                    sql.append(event.getIntervalField().toString().toUpperCase()).append("\n");
                    if (event.getIntervalStarts() != null) {
                        sql.append("\tSTARTS '").append(DateUtils.formatMySqlDateTime(event.getIntervalStarts())).append("'\n");
                    }
                    if (event.getIntervalEnds() != null) {
                        sql.append("ENDS '").append(DateUtils.formatMySqlDateTime(event.getIntervalEnds())).append("'\n");
                    }
                    break;
            }
            if (!event.getComment().isEmpty()) {
                sql.append("COMMENT\n");
                sql.append("\t'").append(event.getComment()).append("'\n");
            }
            sql.append("DO\n\t");
            sql.append(event.getDefinition());
        }

        return sql.toString();
    }

    public static String sqlDelete(DbEvent event) {
        StringBuilder sql = new StringBuilder();

        if (event != null) {
            sql.append("DROP EVENT ").append(event.getName()).append(";");
        }

        return sql.toString();
    }

    public static String sqlEnable(DbEvent event, boolean enable) {
        StringBuilder sql = new StringBuilder();

        if (event != null) {
            sql.append("ALTER EVENT ").append(event.getName()).append(enable ? " ENABLE;" : " DISABLE;");
        }

        return sql.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefiner() {
        if (definer == null) {
            definer = "";
        }
        return definer;
    }

    public void setDefiner(String definer) {
        this.definer = definer;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getComment() {
        if (comment == null) {
            comment = "";
        }
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = new Date(created.getTime());
    }

    public Date getAltered() {
        return altered;
    }

    public void setAltered(Timestamp altered) {
        this.altered = new Date(altered.getTime());
    }

    public Date getLastExecuted() {
        return lastExecuted;
    }

    public void setLastExecuted(Timestamp lastExecuted) {
        this.lastExecuted = new Date(lastExecuted.getTime());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public EventType getType() {
        if (type == null) {
            type = EventType.OneTime;
        }
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = EventType.fromString(type);
    }

    public Date getExecuteAt() {
        return executeAt;
    }

    public void setExecuteAt(Timestamp executeAt) {
        if (executeAt != null) {
            this.executeAt = new Date(executeAt.getTime());
        }
    }

    public int getIntervalValue() {
        return intervalValue;
    }

    public void setIntervalValue(int intervalValue) {
        this.intervalValue = intervalValue;
    }

    public EventIntervalField getIntervalField() {
        return intervalField;
    }

    public void setIntervalField(EventIntervalField intervalField) {
        this.intervalField = intervalField;
    }

    public void setIntervalField(String intervalField) {
        this.intervalField = EventIntervalField.fromString(intervalField);
    }

    public Date getIntervalStarts() {
        return intervalStarts;
    }

    public void setIntervalStarts(Date intervalStarts) {
        this.intervalStarts = intervalStarts;
    }

    public void setIntervalStarts(Timestamp intervalStarts) {
        if (intervalStarts != null) {
            this.intervalStarts = new Date(intervalStarts.getTime());
        }
    }

    public Date getIntervalEnds() {
        return intervalEnds;
    }

    public void setIntervalEnds(Date intervalEnds) {
        this.intervalEnds = intervalEnds;
    }

    public void setIntervalEnds(Timestamp intervalEnds) {
        if (intervalEnds != null) {
            this.intervalEnds = new Date(intervalEnds.getTime());
        }
    }
}
