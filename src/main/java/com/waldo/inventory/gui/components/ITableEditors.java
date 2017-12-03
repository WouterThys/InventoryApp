package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.dialogs.importfromcsvdialog.TableObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.EventObject;

import static com.waldo.inventory.gui.Application.imageResource;

public class ITableEditors {

    public interface SpinnerChangedListener {
        void onValueSet(int value);
    }

    public abstract static class SpinnerEditor extends DefaultCellEditor implements SpinnerChangedListener {

        JSpinner spinner;
        JSpinner.DefaultEditor editor;
        JTextField textField;
        boolean valueSet;

        protected SpinnerEditor() {
            super(new JTextField());
            SpinnerModel model = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
            spinner = new JSpinner(model);
            editor = ((JSpinner.DefaultEditor) spinner.getEditor());
            textField = editor.getTextField();
            textField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        if (valueSet) {
                            textField.setCaretPosition(1);
                        }
                    });
                }

                @Override
                public void focusLost(FocusEvent e) {
                }
            });
            textField.addActionListener(e -> stopCellEditing());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (!valueSet) {
                spinner.setValue(value);
            }
            SwingUtilities.invokeLater(() -> textField.requestFocus());
            return spinner;
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof KeyEvent) {
                KeyEvent ke = (KeyEvent) anEvent;
                textField.setText(String.valueOf(ke.getKeyChar()));
                valueSet = true;
            } else {
                valueSet = false;
            }
            return true;
        }

        @Override
        public Object getCellEditorValue() {
            int value = (int) spinner.getValue();

            onValueSet(value);

            return value;
        }

        @Override
        public boolean stopCellEditing() {
            try {
                editor.commitEdit();
                spinner.commitEdit();
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid value, discarding");
            }
            return super.stopCellEditing();
        }
    }


    public static class AmountRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (column == 0) {

                ILabel lblIcon = null;
                ILabel lblText = null;

                if (value instanceof Item) {
                    Item item = (Item) value;
                    lblText = new ILabel(String.valueOf(item.getAmount()));
                    lblText.setForeground(Color.WHITE);
                    Font f = lblText.getFont();
                    lblText.setFont(new Font(f.getName(), Font.BOLD, f.getSize() - 5));

                    if (item.getOrderState() == Statics.ItemOrderStates.ORDERED) {
                        lblIcon = new ILabel(imageResource.readImage("Ball.blue"));
                    } else if (item.getOrderState() == Statics.ItemOrderStates.PLANNED) {
                        lblIcon = new ILabel(imageResource.readImage("Ball.yellow"));
                    } else {
                        if (item.getAmount() > 0) {
                            lblIcon = new ILabel(imageResource.readImage("Ball.green"));
                        } else {
                            lblIcon = new ILabel(imageResource.readImage("Ball.red"));
                        }
                    }
                } else if (value instanceof SetItem) {
                    SetItem item = (SetItem) value;
                    lblText = new ILabel(String.valueOf(item.getAmount()));
                    lblText.setForeground(Color.WHITE);
                    Font f = lblText.getFont();
                    lblText.setFont(new Font(f.getName(), Font.BOLD, f.getSize() - 5));

                    if (item.getAmount() > 0) {
                        lblIcon = new ILabel(imageResource.readImage("Ball.green"));
                    } else {
                        lblIcon = new ILabel(imageResource.readImage("Ball.red"));
                    }
                }

                if (lblIcon != null) {
                    // Colors
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    Color cbg = c.getBackground();

                    if (row % 2 == 1 || isSelected) {
                        lblIcon.setBackground(cbg);
                        lblText.setBackground(cbg);
                    } else {
                        lblIcon.setBackground(Color.WHITE);
                        lblText.setBackground(Color.WHITE);
                    }

                    lblIcon.setOpaque(true);
                    lblText.setOpaque(false);

                    lblIcon.setLayout(new GridBagLayout());
                    lblIcon.add(lblText);
                    return lblIcon;
                }
                return null;
            } else {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }
    }

    public static class LogTypeEditor extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (column == 0) {

                Log log = (Log) value;
                ILabel lblText = new ILabel();
                lblText.setForeground(Color.WHITE);
                Font f = lblText.getFont();
                lblText.setFont(new Font(f.getName(), Font.BOLD, f.getSize() - 5));
                ILabel lblIcon;
                switch (log.getLogType()) {
                    case Statics.LogTypes.INFO:
                        lblIcon = new ILabel(imageResource.readImage("Log.InfoS"));
                        break;
                    case Statics.LogTypes.DEBUG:
                        lblIcon = new ILabel(imageResource.readImage("Log.DebugS"));
                        break;
                    case Statics.LogTypes.WARN:
                        lblIcon = new ILabel(imageResource.readImage("Log.WarnS"));
                        break;
                    case Statics.LogTypes.ERROR:
                        lblIcon = new ILabel(imageResource.readImage("Log.ErrorS"));
                        break;
                    default:
                        lblIcon = new ILabel(imageResource.readImage("Log.LogS"));
                        break;
                }

                // Colors
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Color cbg =  c.getBackground();

                if (row %2 == 1 || isSelected) {
                    lblIcon.setBackground(cbg);
                    lblText.setBackground(cbg);
                } else {
                    lblIcon.setBackground(Color.WHITE);
                    lblText.setBackground(Color.WHITE);
                }

                lblIcon.setOpaque(true);
                lblText.setOpaque(false);

                lblIcon.setLayout(new GridBagLayout());
                lblIcon.add(lblText);
                return lblIcon;
            } else {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }
    }

    public static class CheckRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (column == 0) {

                TableObject object = (TableObject) value;
                ILabel lblText = new ILabel();
                lblText.setForeground(Color.WHITE);
                Font f = lblText.getFont();
                lblText.setFont(new Font(f.getName(), Font.BOLD, f.getSize() - 5));

                ILabel lblIcon;
                if (object.isValid()) {
                    lblIcon = new ILabel(imageResource.readImage("Ball.green"));
                } else {
                    if (object.getFound() == 0) {
                        lblIcon = new ILabel(imageResource.readImage("Ball.red"));
                    } else {
                        lblIcon = new ILabel(imageResource.readImage("Ball.yellow"));
                        lblText.setText(String.valueOf(object.getFound()));
                    }
                }

                // Colors
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Color cbg =  c.getBackground();

                if (row %2 == 1 || isSelected) {
                    lblIcon.setBackground(cbg);
                    lblText.setBackground(cbg);
                } else {
                    lblIcon.setBackground(Color.WHITE);
                    lblText.setBackground(Color.WHITE);
                }

                lblIcon.setOpaque(true);
                lblText.setOpaque(false);

                lblIcon.setLayout(new GridBagLayout());;
                lblIcon.add(lblText);
                return lblIcon;
            } else {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }
    }

    public static class OrderItemTooltipRenderer extends DefaultTableCellRenderer {

        private static final ImageIcon imageOk = imageResource.readImage("Orders.Table.Ok");
        private static final ImageIcon imageWarn = imageResource.readImage("Orders.Table.Warning");
        private static final ImageIcon imageError = imageResource.readImage("Orders.Table.Error");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (column == 0 && value != null && value instanceof OrderItem) {
                OrderItem orderItem = (OrderItem) value;
                ILabel label = new ILabel("", ILabel.CENTER);
                boolean amountOk = orderItem.getAmount() > 0;
                    boolean referenceOk = orderItem.getDistributorPartId() > DbObject.UNKNOWN_ID;
                    if (amountOk && referenceOk) {
                        label.setIcon(imageOk);
                        label.setToolTipText(null);
                    } else if (!referenceOk) {
                        label.setIcon(imageError);
                        label.setToolTipText("Reference is not set..");
                    } else {
                        label.setIcon(imageWarn);
                        label.setToolTipText("Amount is 0..");
                    }

                Color cbg = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).getBackground();

                if (row % 2 == 1 || isSelected) {
                    label.setBackground(cbg);
                } else {
                    label.setBackground(Color.WHITE);
                }
                label.setOpaque(true);
                return label;
            }
            return this;
        }
    }

    //                if (component.hasMatch()) {
//                    lblIcon = new ILabel(imageResource.readImage("Ball.green"));
//                } else {
//                    lblIcon = new ILabel(imageResource.readImage("Ball.red"));
//                    if (component.matchCount() > 0) {
//                        int highest = component.highestMatch();
//                        if (PcbItemParser.getInstance().getMatchCount(highest) == 3) {
//                            lblIcon = new ILabel(imageResource.readImage("Ball.green"));
//                        } else if (PcbItemParser.getInstance().getMatchCount(highest) == 2) {
//                            lblIcon = new ILabel(imageResource.readImage("Ball.yellow"));
//                        } else {
//                            lblIcon = new ILabel(imageResource.readImage("Ball.orange"));
//                        }
//                    } else {
//                        lblIcon = new ILabel(imageResource.readImage("Ball.red"));
//                    }
//}

    public static class PcbItemMatchRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (column == 0) {
                PcbItem component = (PcbItem) value;
                ILabel lblText = new ILabel();
                lblText.setForeground(Color.WHITE);
                Font f = lblText.getFont();
                lblText.setFont(new Font(f.getName(), Font.BOLD, f.getSize() - 5));
                lblText.setText(String.valueOf(component.getReferences().size()));

                // Colors
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Color cbg =  c.getBackground();

                ILabel lblIcon = new ILabel(imageResource.readImage("Ball.green"));

                if (row %2 == 1 || isSelected) {
                    lblIcon.setBackground(cbg);
                    lblText.setBackground(cbg);
                } else {
                    lblIcon.setBackground(Color.WHITE);
                    lblText.setBackground(Color.WHITE);
                }

                lblIcon.setOpaque(true);
                lblText.setOpaque(false);

                lblIcon.setLayout(new GridBagLayout());;
                lblIcon.add(lblText);
                return lblIcon;
            } else {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }
    }

    public static class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }

            if (value != null) {
                if (value instanceof String) {
                    setText(value.toString());
                }
                if (value instanceof ImageIcon) {
                    setIcon((ImageIcon) value);
                }
            }


            return this;
        }
    }

    public static class ButtonEditor extends AbstractCellEditor implements
            TableCellRenderer, TableCellEditor, ActionListener, MouseListener {

        private JTable table;
        private Action action;
        private int mnemonic;
        private Border originalBorder;
        private Border focusBorder;

        private JButton renderButton;
        private JButton editButton;
        private Object editorValue;
        private boolean isButtonColumnEditor;

        /**
         * Create the ButtonColumn to be used as a renderer and editor. The
         * renderer and editor will automatically be installed on the TableColumn
         * of the specified column.
         *
         * @param table  the table containing the button renderer/editor
         * @param action the Action to be invoked when the button is invoked
         * @param column the column to which the button renderer/editor is added
         */
        public ButtonEditor(JTable table, Action action, int column) {
            this.table = table;
            this.action = action;

            renderButton = new JButton();
            editButton = new JButton();
            editButton.setFocusPainted(false);
            editButton.addActionListener(this);
            originalBorder = editButton.getBorder();
            setFocusBorder(new LineBorder(Color.BLUE));

            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(column).setCellRenderer(this);
            columnModel.getColumn(column).setCellEditor(this);
            table.addMouseListener(this);
        }


        /**
         * Get foreground color of the button when the cell has focus
         *
         * @return the foreground color
         */
        public Border getFocusBorder() {
            return focusBorder;
        }

        /**
         * The foreground color of the button when the cell has focus
         *
         * @param focusBorder the foreground color
         */
        public void setFocusBorder(Border focusBorder) {
            this.focusBorder = focusBorder;
            editButton.setBorder(focusBorder);
        }

        public int getMnemonic() {
            return mnemonic;
        }

        /**
         * The mnemonic to activate the button when the cell has focus
         *
         * @param mnemonic the mnemonic
         */
        public void setMnemonic(int mnemonic) {
            this.mnemonic = mnemonic;
            renderButton.setMnemonic(mnemonic);
            editButton.setMnemonic(mnemonic);
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            if (value == null) {
                editButton.setText("");
                editButton.setIcon(null);
            } else if (value instanceof Icon) {
                editButton.setText("");
                editButton.setIcon((Icon) value);
            } else {
                editButton.setText(value.toString());
                editButton.setIcon(null);
            }

            this.editorValue = value;
            return editButton;
        }

        @Override
        public Object getCellEditorValue() {
            return editorValue;
        }

        //
        //  Implement TableCellRenderer interface
        //
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                renderButton.setForeground(table.getSelectionForeground());
                renderButton.setBackground(table.getSelectionBackground());
            } else {
                renderButton.setForeground(table.getForeground());
                renderButton.setBackground(UIManager.getColor("Button.background"));
            }

            if (hasFocus) {
                renderButton.setBorder(focusBorder);
            } else {
                renderButton.setBorder(originalBorder);
            }

            //		renderButton.setText( (value == null) ? "" : value.toString() );
            if (value == null) {
                renderButton.setText("");
                renderButton.setIcon(null);
            } else if (value instanceof Icon) {
                renderButton.setText("");
                renderButton.setIcon((Icon) value);
            } else {
                renderButton.setText(value.toString());
                renderButton.setIcon(null);
            }

            return renderButton;
        }

        //
        //  Implement ActionListener interface
        //
    /*
	 *	The button has been pressed. Stop editing and invoke the custom Action
	 */
        public void actionPerformed(ActionEvent e) {
            int row = table.convertRowIndexToModel(table.getEditingRow());
            fireEditingStopped();

            //  Invoke the Action

            ActionEvent event = new ActionEvent(
                    table,
                    ActionEvent.ACTION_PERFORMED,
                    "" + row);
            action.actionPerformed(event);
        }

        //
        //  Implement MouseListener interface
        //
	/*
	 *  When the mouse is pressed the editor is invoked. If you then then drag
	 *  the mouse to another cell before releasing it, the editor is still
	 *  active. Make sure editing is stopped when the mouse is released.
	 */
        public void mousePressed(MouseEvent e) {
            if (table.isEditing()
                    && table.getCellEditor() == this)
                isButtonColumnEditor = true;
        }

        public void mouseReleased(MouseEvent e) {
            if (isButtonColumnEditor
                    && table.isEditing())
                table.getCellEditor().stopCellEditing();

            isButtonColumnEditor = false;
        }

        public void mouseClicked(MouseEvent e) {
            renderButton.setForeground(table.getSelectionForeground());
            renderButton.setBackground(table.getSelectionBackground());
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }




}
