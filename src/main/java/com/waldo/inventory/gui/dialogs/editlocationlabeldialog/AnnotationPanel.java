package com.waldo.inventory.gui.dialogs.editlocationlabeldialog;

import com.waldo.inventory.classes.dbclasses.LabelAnnotation;
import com.waldo.inventory.classes.dbclasses.LocationLabel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class AnnotationPanel extends IPanel implements LabelAnnotationPanel.AnnotationPanelListener, IdBToolBar.IdbToolBarListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel container;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final LocationLabel locationLabel;
    private LabelAnnotationPanel currentPanel;

    private IdBToolBar annotationTb;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public AnnotationPanel(LocationLabel locationLabel) {
        this.locationLabel = locationLabel;
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void updateEnabledComponents() {
        boolean enabled = currentPanel != null && currentPanel.isSelected();

        annotationTb.setDeleteActionEnabled(enabled);
        annotationTb.setEditActionEnabled(enabled);

    }

    void addPanel(LabelAnnotationPanel panel) {
        if (panel != null) {
            panel.addAnnotationListener(this);
            panel.setOpaque(true);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            container.add(panel, gbc, 0);

            validate();
            repaint();
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {

         container = new JPanel(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.weightx = 1;
         gbc.weighty = 1;
         container.add(new JPanel(), gbc);

         annotationTb = new IdBToolBar(this, false, true, true, false);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JScrollPane pane = new JScrollPane(container);
        pane.setPreferredSize(new Dimension(300, 500));

        add(pane, BorderLayout.CENTER);
        add(annotationTb, BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object... args) {
        initializeLayouts();
        List<LabelAnnotation> annotationList = locationLabel.getAnnotationList();
        for (LabelAnnotation la : annotationList) {
            addPanel(new LabelAnnotationPanel(la));
        }
    }

    // Listener

    @Override
    public void onClicked(MouseEvent e, LabelAnnotation annotation) {
        if (currentPanel != null) {
            currentPanel.setSelected(false);
        }
        currentPanel = (LabelAnnotationPanel) e.getSource();
        currentPanel.setSelected(true);

        updateEnabledComponents();
    }

    // Tool bar

    @Override
    public void onToolBarRefresh(IdBToolBar source) {

    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {

    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {

    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {

    }
}
