package com.waldo.inventory.gui.dialogs.editlocationlabeldialog;

import com.waldo.inventory.classes.dbclasses.LabelAnnotation;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LabelAnnotationPanel extends IPanel implements IEditedListener, CacheChangedListener<LabelAnnotation>, MouseListener {

    interface AnnotationPanelListener {
        void onClicked(MouseEvent e, LabelAnnotation annotation);
    }

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
    private final Color backGround;
    private boolean selected = false;
    private AnnotationPanelListener listener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public LabelAnnotationPanel(LabelAnnotation annotation) {
        this.annotation = annotation;
        this.backGround = getBackground();
        addMouseListener(this);

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

    void addAnnotationListener(AnnotationPanelListener listener) {
        this.listener = listener;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setBackground(backGround.darker());
        } else {
            setBackground(backGround);
        }
    }

    public boolean isSelected() {
        return selected;
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

    private ILabel createLabel(String text) {
        ILabel lbl = new ILabel(text);
        lbl.setOpaque(true);
        return lbl;
    }

    @Override
    public void initializeLayouts() {

        JPanel startXPnl = new JPanel(new BorderLayout());
        startXPnl.setOpaque(true);
        startXPnl.add(createLabel("X:"), BorderLayout.WEST);
        startXPnl.add(startXSp, BorderLayout.CENTER);

        JPanel startYPnl = new JPanel(new BorderLayout());
        startYPnl.setOpaque(true);
        startYPnl.add(createLabel(" Y:"), BorderLayout.WEST);
        startYPnl.add(startYSp, BorderLayout.CENTER);

        Box startPosBox = Box.createHorizontalBox();
        startPosBox.setOpaque(true);
        startPosBox.add(startXPnl);
        startPosBox.add(startYPnl);


        Box textFontBox = Box.createHorizontalBox();
        textFontBox.setOpaque(true);
        textFontBox.add(textFontCb);
        textFontBox.add(textSizeSp);


        JPanel imageWPnl = new JPanel(new BorderLayout());
        imageWPnl.setOpaque(true);
        imageWPnl.add(createLabel("W:"), BorderLayout.WEST);
        imageWPnl.add(imageWSp, BorderLayout.CENTER);

        JPanel imageHPnl = new JPanel(new BorderLayout());
        imageHPnl.setOpaque(true);
        imageHPnl.add(createLabel(" H:"), BorderLayout.WEST);
        imageHPnl.add(imageHSp, BorderLayout.CENTER);

        Box imageWHBox = Box.createHorizontalBox();
        imageWHBox.setOpaque(true);
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

    // Mouse listener

    @Override
    public void mouseClicked(MouseEvent e) {
        if (listener != null) {
            e.setSource(this);
            listener.onClicked(e, annotation);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!selected) {
            setBackground(backGround.brighter());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!selected) {
            setBackground(backGround);
        }
    }
}
