package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ProjectIDE;
import com.waldo.inventory.classes.dbclasses.ProjectObject;
import com.waldo.utils.FileUtils;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import static com.waldo.inventory.gui.Application.imageResource;

public class ITileView<IT extends ProjectObject> extends JPanel implements GuiUtils.GuiInterface, MouseListener /*, ActionListener */ {

    public interface TileClickListener<IT extends ProjectObject> {
        void onTileClick(MouseEvent e, IT projectObject);
        void onTileRightClick(MouseEvent e, IT projectObject);
    }

    private JButton iconBtn;
    private JTextPane nameTp;

    private final String name;
    private final IT projectObject;
    private TileClickListener<IT> listener;

    private boolean isSelected = false;
    private Color background;

    public ITileView(IT projectObject) {
        this.projectObject = projectObject;
        this.name = projectObject.getName();

        this.addMouseListener(this);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public void addTileClickListener(TileClickListener<IT> listener) {
        this.listener = listener;
    }

    public IT getProjectObject() {
        return projectObject;
    }

    private String createName(String text) {
        return FileUtils.formatFileNameString(text);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        if (selected) {
            this.setBackground(Color.gray);
        } else {
            this.setBackground(background);
        }
    }


    @Override
    public void initializeComponents() {
        iconBtn = new JButton();
        iconBtn.setPreferredSize(new Dimension(70,70));
        iconBtn.setMinimumSize(new Dimension(70,70));
        iconBtn.setMaximumSize(new Dimension(70,70));
        iconBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
        iconBtn.addMouseListener(this);
        //iconBtn.addActionListener(this);

        nameTp = new JTextPane();

        StyledDocument doc = nameTp.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        nameTp.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameTp.setAlignmentY(Component.CENTER_ALIGNMENT);
        nameTp.setFocusable(false);
        nameTp.setOpaque(false);
        nameTp.setBackground(new Color(0,0,0,0));
        nameTp.setBorder(null);
        nameTp.setEditable(false);
        nameTp.addMouseListener(this);

        background = this.getBackground();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(iconBtn);
        add(nameTp);

        setPreferredSize(new Dimension(128, 128));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    }

    @Override
    public void updateComponents(Object... object) {
        ProjectIDE ide = projectObject.getProjectIDE();

        if (ide != null && ide.getImageId() > DbObject.UNKNOWN_ID) {
            DbImage dbImage = imageResource.getImage(Statics.ImageType.IdeImage, ide.getImageId());
            if (dbImage != null) {
                setImage(dbImage.getImageIcon());
            }
        } else {
            String extension = FileUtils.getExtension(new File(projectObject.getDirectory()));
            String iconPath = "";

            if (extension != null) {
                switch (extension) {
                    case "ods":
                        iconPath += "ods.png";
                        break;
                    case "pdf":
                        iconPath += "pdf.png";
                        break;
                    case "avi":
                        iconPath += "avi-icon.png";
                        break;
                    case "csv":
                        iconPath += "csv-icon.png";
                        break;
                    case "xls":
                        iconPath += "excel-xls-icon.png";
                        break;
                    case "jpg":
                        iconPath += "jpg-icon.png";
                        break;
                    case "mp3":
                        iconPath += "mp3-icon.png";
                        break;
                    case "png":
                        iconPath += "png-icon.png";
                        break;
                    case "ppt":
                        iconPath += "ppt-icon.png";
                        break;
                    case "rar":
                        iconPath += "rar-icon.png";
                        break;
                    case "txt":
                        iconPath += "txt-icon.png";
                        break;
                    case "zip":
                        iconPath += "zip-icon.png";
                        break;
                    default:
                        break;
                }
            }
            setIcon(iconPath, projectObject.isValid());
            //imageResource.requestImage(this);
        }


        nameTp.setText(createName(name));
    }

    private void setIcon(String path, boolean isValid) {
        if (!path.isEmpty()) {
            try {
                ImageIcon ideIcon = ImageResource.scaleImage(imageResource.getImageFromDisc(path), new Dimension(60,60));
                if (isValid) {
                    iconBtn.setIcon(ideIcon);
                } else {
                    ImageIcon warnIcon = imageResource.readIcon("ErrorProvider.WarningIcon");
                    iconBtn.setIcon(new CombinedIcon(ideIcon, warnIcon));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            iconBtn.setIcon(imageResource.readIcon("Unknown.L"));
        }
    }

//    @Override
//    public void actionPerformed(ActionEvent e) {
//        if (listener != null) {
//            listener.onTileClick(projectObject);
//        }
//    }

    @Override
    public String getName() {
        return name;
    }


    class CombinedIcon implements Icon {
        private final Icon center;
        private final Icon error;

        public CombinedIcon(Icon center, Icon error) {
            this.center = center;
            this.error = error;
        }

        public int getIconHeight() {
            //return 60;
            return center.getIconHeight();// + (error.getIconHeight()/2);
        }

        public int getIconWidth() {
            //return 60;
            return center.getIconWidth();// + (error.getIconWidth()/2);
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            center.paintIcon(c, g, x, y);
            int errorX = 40;//x + center.getIconWidth() - (error.getIconWidth()/3);
            int errorY = 5;//y - (error.getIconWidth()/3);
            error.paintIcon(c, g, errorX, errorY);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (listener != null) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                listener.onTileClick(e, projectObject);
            } else {
                listener.onTileRightClick(e, projectObject);
            }
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
        setBackground(Color.gray.brighter());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setSelected(isSelected);
    }

    public void setImage(ImageIcon image) {
        ImageIcon ideIcon = ImageResource.scaleImage(image, new Dimension(60,60));
        iconBtn.setIcon(ideIcon);
    }

}

