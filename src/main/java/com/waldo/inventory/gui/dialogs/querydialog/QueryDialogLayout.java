package com.waldo.inventory.gui.dialogs.querydialog;

import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;

import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class QueryDialogLayout extends IDialog {

    private QueryPanel queryPanel;

    QueryDialogLayout(Application application, String title) {
        super(application, title);
    }


    @Override
    public void initializeComponents() {
        // Title
        getTitleIconLabel().setIcon(imageResource.readImage("QueryDialog.TitleIcon", 48));
        getTitleNameLabel().setText("Query");

        // Panel
        queryPanel = new QueryPanel(application);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(queryPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... object) {

    }
}
