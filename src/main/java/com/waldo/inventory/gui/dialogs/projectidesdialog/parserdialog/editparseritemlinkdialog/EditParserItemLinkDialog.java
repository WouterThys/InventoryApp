package com.waldo.inventory.gui.dialogs.projectidesdialog.parserdialog.editparseritemlinkdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.classes.dbclasses.ParserItemLink;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IObjectDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.selectdivisiondialog.SelectDivisionDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditParserItemLinkDialog extends IObjectDialog<ParserItemLink> {

    private ITextField linkTf;
    private ITextField divisionTf;
    private IActions.EditAction editDivisionAction;

    public EditParserItemLinkDialog(Window parent, ParserItemLink link) {
        super(parent, "Parser - Division link   ", link, ParserItemLink.class);

        initializeComponents();
        initializeLayouts();
        updateComponents(link);
    }

    @Override
    public VerifyState verify(ParserItemLink toVerify) {
        VerifyState ok = VerifyState.Ok;

        String link = linkTf.getText();
        if (link == null || link.isEmpty()) {
            linkTf.setError("Can not be empty..");
            ok = VerifyState.Error;
        } else {
            if (getObject().getId() <= DbObject.UNKNOWN_ID) {
                ParserItemLink foundLink = SearchManager.sm().findParserItemLinkByPcbItemName(link);
                if (foundLink != null) {
                    linkTf.setText("Link already exists..");
                    ok = VerifyState.Error;
                }
            }
        }

        if (divisionTf.getText().isEmpty()) {
            divisionTf.setText("Select a division..");
            ok = VerifyState.Error;
        }

        return ok;
    }

    @Override
    public void initializeComponents() {
        linkTf = new ITextField("Link");
        divisionTf = new ITextField(false);

        linkTf.addEditedListener(this, "pcbItemName");

        editDivisionAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectDivisionDialog dialog = new SelectDivisionDialog(EditParserItemLinkDialog.this, getObject().getDivision());
                if (dialog.showDialog() == IDialog.OK) {
                    Division newDivision = dialog.getSelectedDivision();
                    if (newDivision != null && !newDivision.equals(getObject().getDivision())) {
                        getObject().setDivisionId(newDivision.getId());
                        divisionTf.setText(newDivision.toString());
                        onValueChanged(null, "division", null, null);
                    }
                }
            }
        };
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel();

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel);
        gbc.addLine("Link: ", linkTf);
        gbc.addLine("Division: ", GuiUtils.createComponentWithActions(divisionTf, editDivisionAction));

        panel.setBorder(BorderFactory.createEmptyBorder(5,10,20,10));

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(panel, BorderLayout.CENTER);
        pack();
    }

    @Override
    public void updateComponents(Object... objects) {
        if (getObject() != null) {
            linkTf.setText(getObject().getPcbItemName());
            if (getObject().getDivisionId() > DbObject.UNKNOWN_ID) {
                divisionTf.setText(getObject().getDivision().toString());
            }
        }
    }
}