package com.waldo.inventory.gui.dialogs.linkitemdialog;

import com.waldo.inventory.Utils.parser.KiCad.KcComponent;
import com.waldo.inventory.Utils.parser.KiCad.KiCadParser;
import com.waldo.inventory.classes.KcItemMatch;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IObjectSearchPanel;
import com.waldo.inventory.gui.dialogs.linkitemdialog.extras.LinkItemPanel;
import com.waldo.inventory.gui.dialogs.linkitemdialog.extras.LinkKcPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;


public abstract class LinkItemDialogLayout extends IDialog implements
        IObjectSearchPanel.IObjectSearchListener, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LinkKcPanel kcPanel;
    LinkItemPanel itemPanel;

    JButton linkBtn;
    private JButton matchBtn;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    KiCadParser kiCadParser;

    KcComponent selectedComponent;
    KcItemMatch selectedMatchItem;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LinkItemDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        linkBtn.setEnabled(selectedComponent != null && selectedMatchItem != null);
        updateLinkBtn();
    }

    void updateLinkBtn() {
        if (selectedComponent != null) {
            if (selectedComponent.hasMatch()) {
                linkBtn.setIcon(imageResource.readImage("Common.RemoveLink", 32));
            } else {
                linkBtn.setIcon(imageResource.readImage("Common.NewLink", 32));
            }
        }
    }

    void addListeners(ListSelectionListener kcListListener, ListSelectionListener itemListListener) {
        kcPanel.addListSelectionListener(kcListListener);
        itemPanel.addListSelectionListener(itemListListener);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("Common.NewLink", 48));
        setTitleName(getTitle());

        // Panels
        kcPanel = new LinkKcPanel(application);

        itemPanel = new LinkItemPanel(application);
        itemPanel.addSearchListener(this);

        // Buttons
        linkBtn = new JButton(imageResource.readImage("Common.NewLink", 32));
        linkBtn.setToolTipText("Link to item");
        linkBtn.setEnabled(false);
        linkBtn.addActionListener(this);

        matchBtn = new JButton(imageResource.readImage("Common.Parse", 32));
        matchBtn.setToolTipText("Find match");
        matchBtn.setEnabled(false);
        matchBtn.addActionListener(this);

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.X_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(linkBtn);
        buttonPanel.add(matchBtn);

        getContentPanel().add(kcPanel);
        getContentPanel().add(buttonPanel);
        getContentPanel().add(itemPanel);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,5,10,5));

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            kiCadParser = (KiCadParser) object;
            kcPanel.updateComponents(kiCadParser);
        } else {
            //componentList.clear();
        }
    }
}