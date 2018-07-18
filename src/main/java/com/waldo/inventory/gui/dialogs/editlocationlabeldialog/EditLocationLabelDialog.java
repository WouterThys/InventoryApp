package com.waldo.inventory.gui.dialogs.editlocationlabeldialog;

import com.waldo.inventory.classes.dbclasses.LabelAnnotation;
import com.waldo.inventory.classes.dbclasses.LocationLabel;
import com.waldo.inventory.gui.components.ILocationLabelPreview;
import com.waldo.inventory.gui.components.IObjectDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EditLocationLabelDialog extends IObjectDialog<LocationLabel> {

    private ILocationLabelPreview labelPreview;
    private ITextField imageNameTv;

    private JPanel annotationsPanel;


    public EditLocationLabelDialog(Window window, LocationLabel locationLabel) {
        super(window, "Edit " + locationLabel, locationLabel, LocationLabel.class);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }


    private LocationLabel getLocationLabel() {
        return getObject();
    }

    private void updateAnnotationPanel(List<LabelAnnotation> annotations) {
        annotationsPanel.removeAll();
        Box box = Box.createVerticalBox();
        for (LabelAnnotation a : annotations) {
            LabelAnnotationPanel lap = new LabelAnnotationPanel(a);
            box.add(lap);
        }
        annotationsPanel.add(box, BorderLayout.NORTH);
        annotationsPanel.revalidate();
        annotationsPanel.repaint();
    }


    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel headerPnl = new JPanel(new BorderLayout());
        headerPnl.add(new ILabel("Image: "), BorderLayout.WEST);
        headerPnl.add(imageNameTv, BorderLayout.CENTER);
        headerPnl.setBorder(GuiUtils.createInlineTitleBorder("Image"));

        JPanel centerPnl = new JPanel(new BorderLayout());
        //JScrollPane scrollPane = new JScrollPane();
        //scrollPane.add(annotationsPanel);
        //scrollPane.setPreferredSize(new Dimension(200, 300));
        //centerPnl.add(scrollPane, BorderLayout.CENTER);
        centerPnl.add(annotationsPanel);
        centerPnl.setBorder(GuiUtils.createInlineTitleBorder("Annotations"));

        panel.add(headerPnl, BorderLayout.NORTH);
        panel.add(centerPnl, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(labelPreview, BorderLayout.CENTER);

        return panel;
    }

    @Override
    public VerifyState verify(LocationLabel toVerify) {
        return null;
    }

    @Override
    public void initializeComponents() {
        setResizable(true);

        imageNameTv = new ITextField();

        labelPreview = new ILocationLabelPreview();

        annotationsPanel = new JPanel(new BorderLayout());
       // annotationsPanel.setPreferredSize(new Dimension(200, 300));
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel(new BorderLayout());


        panel.add(createPreviewPanel(), BorderLayout.CENTER);
        panel.add(createDetailPanel(), BorderLayout.EAST);


        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(panel);
    }

    @Override
    public void updateComponents(Object... objects) {

        updateAnnotationPanel(getLocationLabel().getAnnotationList());

    }
}
