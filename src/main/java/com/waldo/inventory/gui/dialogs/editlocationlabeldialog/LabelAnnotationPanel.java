package com.waldo.inventory.gui.dialogs.editlocationlabeldialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.LabelAnnotationLink;
import com.waldo.inventory.classes.dbclasses.LabelAnnotation;
import com.waldo.inventory.gui.components.INavigator;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.imagedialogs.selectimagedialog.SelectImageDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class LabelAnnotationPanel extends IPanel implements IEditedListener, MouseListener, INavigator.NavigatorListener {

    interface AnnotationPanelListener {
        void onClicked(MouseEvent e, LabelAnnotation annotation);
        void onMoved(LabelAnnotation annotation, INavigator.Direction direction);
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField nameTf;
    private INavigator startXYNavigator;
    private IComboBox<LabelAnnotationLink> linkCb;

    // Text
    private ITextField textTf;
    private IComboBox<Font> textFontCb;
    private ISpinner textSizeSp;

    // Image
    private ITextField imageNameTf;
    private ISpinner imageWSp;
    private ISpinner imageHSp;
    private IActions.BrowseFileAction editImageAction;

    // Save
    private IActions.SaveAction saveAction;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private LabelAnnotation annotation;

    private final Color backGround;
    private boolean selected = false;
    private AnnotationPanelListener listener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LabelAnnotationPanel(LabelAnnotation annotation) {
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
            setBackground(backGround.brighter());
        } else {
            setBackground(backGround);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    private void setAnnotationImage(String file, ImageIcon image) {
        annotation.setImagePath(file);
        annotation.setImageW(image.getIconWidth());
        annotation.setImageH(image.getIconHeight());

        imageNameTf.setText(file);
        imageWSp.setValue(image.getIconWidth());
        imageHSp.setValue(image.getIconHeight());

        saveAnnotation();
    }

    private void onEditImage() {
        SelectImageDialog dialog = new SelectImageDialog(null, false, Statics.ImageType.Other);
        if (dialog.showDialog() == IDialog.OK) {
            File imageFile = dialog.getSelectedFile();
            String name = dialog.getImageName();

            try {
                BufferedImage bufferedImage = ImageIO.read(imageFile);
                ImageIcon image = new ImageIcon(bufferedImage);

                // TODO save in db

                setAnnotationImage(imageFile.getAbsolutePath(), image);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to read image..",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }


    private void saveAnnotation() {
        annotation.save();
        saveAction.setEnabled(false);
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    // Layout
    @Override
    public void initializeComponents() {
        nameTf = new ITextField(this, "name");
        startXYNavigator = new INavigator(this);
        linkCb = new IComboBox<>(LabelAnnotationLink.values());
        linkCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                annotation.setLink((LabelAnnotationLink) linkCb.getSelectedItem());
                this.onValueChanged(linkCb, "link", 0, 0);
            }
        });

        textTf = new ITextField(this, "text");
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        textFontCb = new IComboBox<>();

        SpinnerNumberModel testSizeModel = new SpinnerNumberModel(10, 6, Integer.MAX_VALUE, 1);
        textSizeSp = new ISpinner(testSizeModel);
        textSizeSp.addEditedListener(this, "textFontSize", int.class);
        textSizeSp.setPreferredSize(new Dimension(60, 28));

        imageNameTf = new ITextField(this, "imagePath");
        imageNameTf.setEnabled(false);

        SpinnerNumberModel imageWModel = new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 1.0);
        imageWSp = new ISpinner(imageWModel);
        imageWSp.addEditedListener(this, "imageW", double.class);
        imageWSp.setPreferredSize(new Dimension(60, 28));

        SpinnerNumberModel imageHModel = new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 1.0);
        imageHSp = new ISpinner(imageHModel);
        imageHSp.addEditedListener(this, "imageH", double.class);
        imageHSp.setPreferredSize(new Dimension(60, 28));

        saveAction = new IActions.SaveAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> saveAnnotation());
            }
        };

        editImageAction = new IActions.BrowseFileAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onEditImage());
            }
        };
    }

    private ILabel createLabel(String text) {
        ILabel lbl = new ILabel(text);
        lbl.setOpaque(true);
        return lbl;
    }

    @Override
    public void initializeLayouts() {

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

        JPanel savePnl = new JPanel(new BorderLayout());
        savePnl.add(GuiUtils.createNewToolbar(saveAction), BorderLayout.EAST);
        JPanel navPnl = new JPanel(new BorderLayout());
        navPnl.add(savePnl, BorderLayout.NORTH);
        navPnl.add(startXYNavigator, BorderLayout.CENTER);

       JPanel panel = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel, 60);
        gbc.addLine("Name: ", nameTf);
        gbc.addLine("Link: ", linkCb);
        switch (annotation.getType()) {
            case Text:
                gbc.addLine("Text: ", textTf);
                gbc.addLine("", textFontBox);
                break;
            case Image:
                gbc.addLine("Image: ", GuiUtils.createComponentWithActions(imageNameTf, editImageAction));
                gbc.addLine("", imageWHBox);
                break;

            default:
                break;
        }


        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(navPnl, BorderLayout.EAST);

        setBorder(GuiUtils.createInlineTitleBorder(annotation.getType().toString()));
    }

    @Override
    public void updateComponents(Object... args) {

        nameTf.setText(annotation.getName());
        linkCb.setSelectedItem(annotation.getLink());
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
        if (listener != null) {
            saveAnnotation();
        }
    }

    @Override
    public Object getGuiObject() {
        return annotation;
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
            setBackground(backGround.darker());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!selected) {
            setBackground(backGround);
        }
    }

    // Navigator

    @Override
    public void onMoved(INavigator.Direction direction) {
        if (listener != null) {
            listener.onMoved(annotation, direction);
            saveAction.setEnabled(true);
        }
    }
}
