package com.waldo.inventory.gui.dialogs.editlocationlabeldialog;

import com.waldo.inventory.Utils.Statics.LabelAnnotationType;
import com.waldo.inventory.classes.dbclasses.LabelAnnotation;
import com.waldo.inventory.classes.dbclasses.LocationLabel;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public abstract class AnnotationPanel extends IPanel implements
        LabelAnnotationPanel.AnnotationPanelListener,
        IdBToolBar.IdbToolBarListener,
        CacheChangedListener<LabelAnnotation> {

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
    AnnotationPanel(LocationLabel locationLabel) {
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
        }
    }

    void updateView() {
        validate();
        repaint();
    }

    void createNewAnnotation() {

        String message = "Create new annotation, set name and type";
        ITextField nameTf = new ITextField();
        IComboBox<LabelAnnotationType> typeCb = new IComboBox<>(LabelAnnotationType.values());

        Object[] obj = {message, nameTf, typeCb};

        int res = JOptionPane.showConfirmDialog(
                AnnotationPanel.this,
                obj,
                "New annotation",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (res == JOptionPane.YES_OPTION) {
            String name = nameTf.getText();
            LabelAnnotationType type = (LabelAnnotationType) typeCb.getSelectedItem();

            LabelAnnotation newAnnotation = new LabelAnnotation(locationLabel.getId(), name, type);
            newAnnotation.save();
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
        pane.setPreferredSize(new Dimension(400, 500));

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
        updateView();
        updateEnabledComponents();
    }

    // Listener

    @Override
    public void onClicked(MouseEvent e, LabelAnnotation annotation) {
        if (currentPanel != null) {
            currentPanel.setSelected(false);
        }
        currentPanel = (LabelAnnotationPanel) e.getSource();
        currentPanel.setSelected(true);

        updateView();
        updateEnabledComponents();
    }

    // Tool bar

    @Override
    public void onToolBarRefresh(IdBToolBar source) {

    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        SwingUtilities.invokeLater(this::createNewAnnotation);
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (currentPanel != null) {
            int res = JOptionPane.showConfirmDialog(
                    AnnotationPanel.this,
                    "Delete " + currentPanel.getAnnotation() + "?",
                    "Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (res == JOptionPane.YES_OPTION) {
                currentPanel.getAnnotation().delete();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {

    }



    // Cache changed
    @Override
    public void onInserted(LabelAnnotation annotation) {
        addPanel(new LabelAnnotationPanel(annotation));
        updateView();
        updateEnabledComponents();
    }

    @Override
    public void onDeleted(LabelAnnotation annotation) {
        updateComponents();
        updateEnabledComponents();
    }

    @Override
    public void onCacheCleared() {

    }
}
