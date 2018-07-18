package com.waldo.inventory.gui.dialogs.editlocationlabeldialog;

import com.waldo.inventory.classes.dbclasses.LabelAnnotation;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;

public class LabelAnnotationPanel extends IPanel implements IEditedListener, CacheChangedListener<LabelAnnotation> {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField nameTf;
    private ISpinner startXSp;
    private ISpinner startYSp;

    // Text
    private ITextField textTf;
    private IComboBox<Font> textFontCb;
    private ISpinner textSizeSp;

    // Image
    private ITextField imageNameTf;
    private ISpinner imageWSp;
    private ISpinner imageHSp;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final LabelAnnotation annotation;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public LabelAnnotationPanel(LabelAnnotation annotation) {
        this.annotation = annotation;
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    LabelAnnotation getAnnotation() {
        return annotation;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    // Layout
    @Override
    public void initializeComponents() {
        nameTf = new ITextField(this, "name");

        SpinnerNumberModel startXModel = new SpinnerNumberModel(1.0, Double.MIN_VALUE, Double.MAX_VALUE, 0.1);
        startXSp = new ISpinner(startXModel);
        startXSp.addEditedListener(this, "startX", double.class);
        startXSp.setPreferredSize(new Dimension(60, 28));

        SpinnerNumberModel startYModel = new SpinnerNumberModel(1.0, Double.MIN_VALUE, Double.MAX_VALUE, 0.1);
        startYSp = new ISpinner(startYModel);
        startYSp.addEditedListener(this, "startY", double.class);
        startYSp.setPreferredSize(new Dimension(60, 28));

        textTf = new ITextField(this, "text");
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        textFontCb = new IComboBox<>();

        SpinnerNumberModel testSizeModel = new SpinnerNumberModel(10, 6, Integer.MAX_VALUE, 1);
        textSizeSp = new ISpinner(testSizeModel);
        textSizeSp.addEditedListener(this, "textFontSize", double.class);
        textSizeSp.setPreferredSize(new Dimension(60, 28));

        imageNameTf = new ITextField();

        SpinnerNumberModel imageWModel = new SpinnerNumberModel(16.0, 8.0, Double.MAX_VALUE, 1.0);
        imageWSp = new ISpinner(imageWModel);
        imageWSp.addEditedListener(this, "imageW", double.class);
        imageWSp.setPreferredSize(new Dimension(60, 28));

        SpinnerNumberModel imageHModel = new SpinnerNumberModel(16.0, 8.0, Double.MAX_VALUE, 1.0);
        imageHSp = new ISpinner(imageHModel);
        imageHSp.addEditedListener(this, "imageH", double.class);
        imageHSp.setPreferredSize(new Dimension(60, 28));
    }

    @Override
    public void initializeLayouts() {

        JPanel startXPnl = new JPanel(new BorderLayout());
        startXPnl.add(new ILabel("X:"), BorderLayout.WEST);
        startXPnl.add(startXSp, BorderLayout.CENTER);

        JPanel startYPnl = new JPanel(new BorderLayout());
        startYPnl.add(new ILabel(" Y:"), BorderLayout.WEST);
        startYPnl.add(startYSp, BorderLayout.CENTER);

        Box startPosBox = Box.createHorizontalBox();
        startPosBox.add(startXPnl);
        startPosBox.add(startYPnl);


        Box textFontBox = Box.createHorizontalBox();
        textFontBox.add(textFontCb);
        textFontBox.add(textSizeSp);


        JPanel imageWPnl = new JPanel(new BorderLayout());
        imageWPnl.add(new ILabel("W:"), BorderLayout.WEST);
        imageWPnl.add(imageWSp, BorderLayout.CENTER);

        JPanel imageHPnl = new JPanel(new BorderLayout());
        imageHPnl.add(new ILabel(" H:"), BorderLayout.WEST);
        imageHPnl.add(imageHSp, BorderLayout.CENTER);

        Box imageWHBox = Box.createHorizontalBox();
        imageWHBox.add(imageWPnl);
        imageWHBox.add(imageHPnl);



        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(this, 50);
        gbc.addLine("Name: ", nameTf);
        gbc.addLine(" ", startPosBox);

        switch (annotation.getType()) {
            case Text:
                gbc.addLine("Text: ", textTf);
                gbc.addLine("", textFontBox);
                break;
            case Image:
                gbc.addLine("Image: ", imageNameTf);
                gbc.addLine("", imageWHBox);
                break;

                default:
                    break;
        }


        setBorder(GuiUtils.createInlineTitleBorder(annotation.getType().toString()));
        //setPreferredSize(new Dimension(200, 60));
    }

    @Override
    public void updateComponents(Object... args) {

        nameTf.setText(annotation.getName());
        startXSp.setTheValue(annotation.getStartX());
        startYSp.setTheValue(annotation.getStartY());
        textTf.setText(annotation.getText());
        textFontCb.setSelectedItem(annotation.getTextFontName());
        textSizeSp.setTheValue(annotation.getTextFontSize());
        imageNameTf.setText(annotation.getImagePath());
        imageWSp.setTheValue(annotation.getImageW());
        imageHSp.setTheValue(annotation.getImageH());

    }


    // Edited
    @Override
    public void onValueChanged(Component component, String s, Object o, Object o1) {

    }

    @Override
    public Object getGuiObject() {
        return annotation;
    }

    // Cache changed
    @Override
    public void onInserted(LabelAnnotation annotation) {

    }

    @Override
    public void onUpdated(LabelAnnotation annotation) {

    }

    @Override
    public void onDeleted(LabelAnnotation annotation) {

    }

    @Override
    public void onCacheCleared() {

    }
}
