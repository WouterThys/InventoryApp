package com.waldo.inventory.managers;

import com.waldo.inventory.classes.database.DbTable;
import com.waldo.inventory.classes.database.ForeignKey;
import com.waldo.inventory.classes.dbclasses.DbObject;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

public class ErrorManager {

    private static final ErrorManager INSTANCE = new ErrorManager();

    public static ErrorManager em() {
        return INSTANCE;
    }


    public static final int MYSQL_DELETE_FK_ERROR = 1451;
    public static final int MYSQL_UPDATE_FK_ERROR = 1452; // Key Id prolly does not exist
    public static final int MYSQL_ADD_FK_ERROR = 1452;

    private ErrorManager() {

    }


    public boolean handle(DbObject object, Throwable throwable, String sql) {
        boolean handled = false;

        if (throwable instanceof SQLException) {
            SQLException sqlException = (SQLException) throwable;

            switch (sqlException.getErrorCode()) {

                case MYSQL_DELETE_FK_ERROR: {
                    ForeignKey fk = handleFkDeleteError(sqlException);
                    if (fk != null) {
                        showFkDeleteError(object, fk);
                        handled = true;
                    }
                }
                break;

                case MYSQL_UPDATE_FK_ERROR: {
                    ForeignKey fk = handleFkAddUpdateError(sqlException);
                    if (fk != null) {
                        showFkAddUpdateError(object, fk);
                        handled = true;
                    }
                }
                break;

                default:
                    break;
            }
        }

        return handled;
    }

    private ForeignKey handleFkDeleteError(SQLException sqlException) {
        String msg = sqlException.getMessage();

        int first = msg.indexOf("(");
        int last = msg.lastIndexOf(")");

        String[] dummy;

        msg = msg.substring(first +1, last);
        dummy = msg.split(",");
        if (dummy.length > 1) {
            String tableData = dummy[0];
            String constraintData = dummy[1];

            dummy = tableData.split("\\.");
            if (dummy.length > 1) {
                String tableName = dummy[1].replace("`", "");

                // do stuff with tableName
                DbTable table = TableManager.dbTm().getDbTable(tableName);
                if (table != null) {
                    dummy = StringUtils.substringsBetween(constraintData, "`", "`");
                    if (dummy.length > 3) {
                        String fkConstraint = dummy[0];
                        return table.findForeignKey(fkConstraint);
                    }
                }

            }
        }
        return null;
    }

    private ForeignKey handleFkAddUpdateError(SQLException sqlException) {
        String msg = sqlException.getMessage();

        int first = msg.indexOf("(");
        int last = msg.lastIndexOf(")");

        String[] dummy;

        msg = msg.substring(first +1, last);
        dummy = msg.split(",");
        if (dummy.length > 1) {
            String tableData = dummy[0];
            String constraintData = dummy[1];

            dummy = tableData.split("\\.");
            if (dummy.length > 1) {
                String tableName = dummy[1].replace("`", "");

                // do stuff with tableName
                DbTable table = TableManager.dbTm().getDbTable(tableName);
                if (table != null) {
                    dummy = StringUtils.substringsBetween(constraintData, "`", "`");
                    if (dummy.length > 3) {
                        String fkConstraint = dummy[0];
                        return table.findForeignKey(fkConstraint);
                    }
                }

            }
        }
        return null;
    }

    //
    // Message boxes
    //
    private void showFkAddUpdateError(DbObject object, ForeignKey foreignKey) {

        String id = "";
        try {
            String variableName = Character.toUpperCase(foreignKey.getFromColumn().charAt(0)) + foreignKey.getFromColumn().substring(1);
            String getter = "get" + variableName;

            Method get = object.getClass().getDeclaredMethod(getter);
            id = String.valueOf(get.invoke(object));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String message = String.format(
                "Could not delete %s \"%s\" because the link %s (= %s) does not exist..",
                object.getClass().getSimpleName(),
                object.getName(),
                foreignKey.getFromColumn(),
                id);

        JOptionPane.showMessageDialog(
                null,
                message,
                "Add/update error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showFkDeleteError(DbObject object, ForeignKey foreignKey) {
        String message = String.format(
                "Could not delete %s \"%s\" because there are still %s connected to it..",
                object.getClass().getSimpleName(),
                object.getName(),
                foreignKey.getFromTable());

        Object[] options = {"Show affected", "Ok", "Cancel"};
        int res = JOptionPane.showOptionDialog(
                null,
                message,
                "Delete error",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]
        );

        if (res == JOptionPane.YES_OPTION) {
            List<DbObject> references = TableManager.dbTm().getForeignKeyReferences(object, foreignKey);

            StringBuilder m = new StringBuilder();
            for (DbObject obj : references) {
                m.append(" - ").append(obj.getName()).append("\r\n");
            }

            JLabel lbl = new JLabel("The following " + foreignKey.getFromTable() + " are linked to this " + object.getClass().getSimpleName() + ":");
            JTextArea textArea = new JTextArea(m.toString(), 10, 20);
            textArea.setEnabled(false);
            JScrollPane scrollPane = new JScrollPane(textArea);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(lbl, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(
                    null,
                    panel,
                    "Affected " + foreignKey.getFromTable(),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
