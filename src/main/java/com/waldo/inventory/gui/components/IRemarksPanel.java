package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.editremarksdialog.EditRemarksDialog;
import com.waldo.utils.FileUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import static com.waldo.inventory.gui.Application.imageResource;

public class IRemarksPanel extends IPanel {

    public interface FileChangedListener {
        void onFileChanged(File newFile);
    }

    private final Window parent;
    private final FileChangedListener fileChangedListener;

    private ITextPane remarksTp;
    private AbstractAction editRemarksAa;
    private IActions.DeleteAction deleteAction;

    private File remarksFile;

    public IRemarksPanel(Window parent, FileChangedListener fileChangedListener) {
        this.parent = parent;
        this.fileChangedListener = fileChangedListener;

        initializeComponents();
        initializeLayouts();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        editRemarksAa.setEnabled(enabled);
        deleteAction.setEnabled(enabled);
    }

    @Override
    public void initializeComponents() {
        remarksTp = new ITextPane();
        remarksTp.setEditable(false);

        editRemarksAa = new AbstractAction("Edit remarks", imageResource.readIcon("Actions.EditRemark")) {
            @Override
            public void actionPerformed(ActionEvent e) {

                File copy = null;

                if (remarksFile != null) {
                    try {
                        copy = FileUtils.createTempFile("RemarksCopyTmp");
                        FileUtils.copyContent(remarksFile, copy);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                EditRemarksDialog dialog = new EditRemarksDialog(parent, "Edit project remarks", copy);
                if (dialog.showDialog() == IDialog.OK) {
                    copy = dialog.getFile();

                    if (copy != null && remarksFile == null) {
                        fileChangedListener.onFileChanged(copy);
                    } else if (copy == null && remarksFile != null) {
                        fileChangedListener.onFileChanged(null);
                    } else if (copy != null && remarksFile != null) {
                        if (!FileUtils.contentEquals(copy, remarksFile)) {
                            fileChangedListener.onFileChanged(copy);
                        }
                    }
                    updateComponents(copy);
                }
            }
        };
        editRemarksAa.putValue(AbstractAction.LONG_DESCRIPTION, "Edit remarks");
        editRemarksAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Edit remarks");

        deleteAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showConfirmDialog(
                        parent,
                        "Are you sure you want to delete the remarks file?",
                        "Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (res == JOptionPane.YES_OPTION) {
                    if (remarksFile != null) {
                        remarksFile = null;
                        fileChangedListener.onFileChanged(null);
                        updateComponents(remarksFile);
                    }
                }
            }
        };
        deleteAction.setIcon(imageResource.readIcon("Actions.Delete"));
        deleteAction.setName("Delete remarks");
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel headerPnl = new JPanel(new BorderLayout());

        //JToolBar toolBar = GuiUtils.createNewToolbar();
        //JButton b = toolBar.add(editRemarksAa);
        //b.setText("Edit remarks ");
        //b.setVerticalTextPosition(SwingConstants.CENTER);
        //b.setHorizontalTextPosition(SwingConstants.RIGHT);


        headerPnl.add(new ILabel("Remarks: "), BorderLayout.WEST);
        headerPnl.add(GuiUtils.createNewToolbar(deleteAction, editRemarksAa), BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(remarksTp);
        panel.add(headerPnl, BorderLayout.PAGE_START);
        panel.add(scrollPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            remarksFile = (File) args[0];
        } else {
            remarksFile = null;
        }
        remarksTp.setFile(remarksFile);
    }
}
