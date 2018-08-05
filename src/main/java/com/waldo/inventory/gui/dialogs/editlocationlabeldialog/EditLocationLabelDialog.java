package com.waldo.inventory.gui.dialogs.editlocationlabeldialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.LabelAnnotation;
import com.waldo.inventory.classes.dbclasses.LocationLabel;
import com.waldo.inventory.database.ImageDbAccess;
import com.waldo.inventory.database.interfaces.ImageChangedListener;
import com.waldo.inventory.gui.components.ILocationLabelPreview;
import com.waldo.inventory.gui.components.IObjectDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.imagedialogs.selectimagedialog.SelectImageDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ISpinner;
import com.waldo.utils.icomponents.ITextField;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.waldo.inventory.gui.Application.imageResource;

public class EditLocationLabelDialog extends IObjectDialog<LocationLabel> implements ImageChangedListener {

    private ILocationLabelPreview labelPreview;
    private ISpinner zoomSp;

    private IActions.BrowseFileAction editImageAction;
    private ITextField imageNameTv;

    private AnnotationPanel annotationPanel;


    public EditLocationLabelDialog(Window window, LocationLabel locationLabel) {
        super(window, "Edit " + locationLabel, locationLabel, LocationLabel.class);

        initializeComponents();
        initializeLayouts();
        updateComponents();

        addCacheListener(LabelAnnotation.class, annotationPanel);
        ImageDbAccess.imDb().addImageChangedListener(this);

    }


    private LocationLabel getLocationLabel() {
        return getObject();
    }

    private void onEditImage() {
        SelectImageDialog dialog = new SelectImageDialog(EditLocationLabelDialog.this, false, Statics.ImageType.Other);
        if (dialog.showDialog() == IDialog.OK) {
            File imageFile = dialog.getSelectedFile();
            String name = dialog.getImageName();

            try {
                BufferedImage bufferedImage = ImageIO.read(imageFile);
                ImageIcon image = new ImageIcon(bufferedImage);

                DbImage dbImage = new DbImage(Statics.ImageType.Other, image, name);
                dbImage.save();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        EditLocationLabelDialog.this,
                        "Failed to read image..",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }


    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel headerPnl = new JPanel(new BorderLayout());
        headerPnl.add(new ILabel("Image: "), BorderLayout.WEST);
        headerPnl.add(GuiUtils.createComponentWithActions(imageNameTv, editImageAction), BorderLayout.CENTER);
        headerPnl.setBorder(GuiUtils.createInlineTitleBorder("Image"));

        JPanel centerPnl = new JPanel(new BorderLayout());
        centerPnl.add(annotationPanel, BorderLayout.CENTER);
        centerPnl.setBorder(GuiUtils.createInlineTitleBorder("Annotations"));

        panel.add(headerPnl, BorderLayout.NORTH);
        panel.add(centerPnl, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel zoomPnl = new JPanel(new BorderLayout());
        zoomPnl.add(new ILabel("Zoom: (%)"), BorderLayout.WEST);
        zoomPnl.add(zoomSp, BorderLayout.CENTER);

        Box bottomBox = Box.createHorizontalBox();
        bottomBox.add(zoomPnl);


        panel.add(labelPreview, BorderLayout.CENTER);
        panel.add(bottomBox, BorderLayout.SOUTH);

        panel.setPreferredSize(new Dimension(600, 500));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(8,2,2,2),
                BorderFactory.createLineBorder(Color.lightGray, 1)
        ));

        return panel;
    }

    @Override
    public VerifyState verify(LocationLabel toVerify) {
        return VerifyState.Ok;
    }

    @Override
    public void initializeComponents() {
        setResizable(true);

        imageNameTv = new ITextField();
        editImageAction = new IActions.BrowseFileAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onEditImage());
            }
        };

        labelPreview = new ILocationLabelPreview(getLocationLabel());
        SpinnerNumberModel snm = new SpinnerNumberModel(0, 0, 50, 1);
        zoomSp = new ISpinner(snm);
        zoomSp.addChangeListener(e -> {
            double z = snm.getNumber().doubleValue();
            z /= 10;
            labelPreview.setZoom(z);
        });

        annotationPanel = new AnnotationPanel(getLocationLabel()) {
            @Override
            public void onUpdated(LabelAnnotation a) {
                labelPreview.updateAnnotations();
            }
        };
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
        if (getLocationLabel().getImageId() > DbObject.UNKNOWN_ID) {
            DbImage image = imageResource.getImage(Statics.ImageType.Other, getLocationLabel().getImageId());
            if (image != null) {
                imageNameTv.setText(image.getName());
            }
        }
    }

    // Image changed

    @Override
    public void onInserted(DbImage image) {
        getLocationLabel().setImageId(image.getId());
        imageNameTv.setText(image.getName());
        labelPreview.updateComponents();
        onValueChanged(imageNameTv, "imageId", 0, 0);
    }

    @Override
    public void onUpdated(DbImage image) {
        getLocationLabel().setImageId(image.getId());
        imageNameTv.setText(image.getName());
        labelPreview.updateComponents();
        onValueChanged(imageNameTv, "imageId", 0, 0);
    }

    @Override
    public void onDeleted(DbImage image) {
        if (image.getId() == getLocationLabel().getImageId()) {
            getLocationLabel().setImageId(0);
            imageNameTv.setText("");
            labelPreview.updateComponents();
            onValueChanged(imageNameTv, "imageId", 0, 0);
        }
    }
}
