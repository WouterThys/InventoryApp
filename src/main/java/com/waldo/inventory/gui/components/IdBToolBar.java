package com.waldo.inventory.gui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public class IdBToolBar extends JToolBar {

    public interface IdbToolBarListener {
        void onToolBarRefresh(IdBToolBar source);
        void onToolBarAdd(IdBToolBar source);
        void onToolBarDelete(IdBToolBar source);
        void onToolBarEdit(IdBToolBar source);
    }

    private Action refreshAction;
    private Action addAction;
    private Action deleteAction;
    private Action editAction;
    private IdbToolBarListener toolBarListener;

    private final boolean hasRefresh;
    private final boolean hasAdd;
    private final boolean hasDelete;
    private final boolean hasEdit;

    public IdBToolBar(IdbToolBarListener listener) {
        this(listener, JToolBar.HORIZONTAL);
    }

    public IdBToolBar(IdbToolBarListener listener, int orientation) {
        this(listener, orientation, true, true,true, true);
    }

    public IdBToolBar(IdbToolBarListener listener, boolean hasRefresh, boolean hasAdd, boolean hasDelete, boolean hasEdit) {
        this(listener, JToolBar.HORIZONTAL, hasRefresh, hasAdd, hasDelete, hasEdit);
    }

    public IdBToolBar(IdbToolBarListener listener, int orientation, boolean hasRefresh, boolean hasAdd, boolean hasDelete, boolean hasEdit) {
        super(orientation);
        this.hasRefresh = hasRefresh;
        this.hasAdd = hasAdd;
        this.hasDelete = hasDelete;
        this.hasEdit = hasEdit;
        init(listener);
    }


    private void init(IdbToolBarListener listener) {
        this.toolBarListener = listener;

        // Actions
        if (hasRefresh) {
            refreshAction = new AbstractAction("Refresh", imageResource.readIcon("Toolbar.Db.RefreshIcon")) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toolBarListener.onToolBarRefresh(IdBToolBar.this);
                }
            };
            refreshAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Refresh");
            add(refreshAction);
        }

        if (hasAdd) {
            addAction = new AbstractAction("Add", imageResource.readIcon("Toolbar.Db.AddIcon")) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toolBarListener.onToolBarAdd(IdBToolBar.this);
                }
            };
            addAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Add");

            if (hasRefresh) addSeparator();

            add(addAction);
        }

        if (hasDelete) {
            deleteAction = new AbstractAction("Delete", imageResource.readIcon("Toolbar.Db.DeleteIcon")) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toolBarListener.onToolBarDelete(IdBToolBar.this);
                }
            };
            deleteAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Delete");
            add(deleteAction);
        }

        if (hasEdit) {
            editAction = new AbstractAction("Update", imageResource.readIcon("Toolbar.Db.EditIcon")) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toolBarListener.onToolBarEdit(IdBToolBar.this);
                }
            };
            editAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Edit");

            if (hasRefresh || hasAdd) {
                addSeparator();
            }

            add(editAction);
        }

        setOpaque(false);
        setFloatable(false);
        super.setBorder(new EmptyBorder(5,5,5,5));
    }

    public void setBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);
        super.setBorder(titledBorder);
    }

    public void setRefreshActionEnabled(boolean enabled) {
        if (hasRefresh) {
            refreshAction.setEnabled(enabled);
        }
    }

    public void setAddActionEnabled(boolean enabled) {
        if (hasAdd) {
            addAction.setEnabled(enabled);
        }
    }

    public void setDeleteActionEnabled(boolean enabled) {
        if (hasDelete) {
            deleteAction.setEnabled(enabled);
        }
    }

    public void setEditActionEnabled(boolean enabled) {
        if (hasEdit) {
            editAction.setEnabled(enabled);
        }
    }

    public void addSeparateAction(Action action) {
        addSeparator();
        addAction(action);
    }

    public void addAction(Action action) {
        add(action);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setRefreshActionEnabled(enabled);
        setAddActionEnabled(enabled);
        setDeleteActionEnabled(enabled);
        setEditActionEnabled(enabled);
    }
}
