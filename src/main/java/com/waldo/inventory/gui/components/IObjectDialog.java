package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.IEditedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

@SuppressWarnings("unchecked")
public abstract class IObjectDialog <T extends DbObject> extends IDialog implements IEditedListener<T>, CacheChangedListener<T> {

    public enum VerifyState {
        Ok,
        Warning,
        Error
    }

    private final List<CacheChangedListener> cacheListenerList = new ArrayList<>();
    private T originalObject;
    private T copyObject;

    public IObjectDialog(Window parent, String title, T originalObject, Class<T> c) {
        super(parent, title);

        this.originalObject = originalObject;
        if (originalObject != null) {
            this.copyObject = (T) originalObject.createCopy();
        }

        assert copyObject != null;

        addCacheListener(c, this);

        // Layout
        setTitle(getTitle());
        if (originalObject != null) {
            ImageIcon icon;
            if (originalObject.getId() > DbObject.UNKNOWN_ID) {
                icon = imageResource.readIcon("Actions.L.Edit");
            } else {
                icon = imageResource.readIcon("Actions.L.Add");
            }
            if (icon != null) {
                setInfoIcon(icon);
                setIconImage(icon.getImage());
            }
        }
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);
        getButtonNeutral().setVisible(true);
    }

    public T getObject() {
        return copyObject;
    }

    public abstract VerifyState verify(T toVerify);

    public <L extends DbObject> void addCacheListener(Class<L> c, CacheChangedListener<L> listener) {
        if (!cacheListenerList.contains(listener)) {
            cacheListenerList.add(listener);
        }
        cache().addListener(c, listener);
    }

    protected void setOriginalObject(T originalObject) {
        this.originalObject = originalObject;
        if (originalObject != null) {
            this.copyObject = (T) originalObject.createCopy();
        }
        getButtonNeutral().setEnabled(false);
    }

    protected boolean hasChanged() {
        return originalObject != null && copyObject != null && !(copyObject.equals(originalObject));
    }

    protected void doSave() {
        copyObject.createCopy(originalObject);
        originalObject.setCanBeSaved(true);
        originalObject.save();
    }

    public void save(boolean closeAfter) {
        boolean canClose = true;
        switch (verify(copyObject)) {
            case Ok:
                doSave();
                break;
            case Warning:
                int res = JOptionPane.showConfirmDialog(
                        getParent(),
                        "There are warnings, are you sure you want to save?",
                        "Warnings",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (res == JOptionPane.YES_OPTION) {
                    doSave();
                }
                break;
            case Error:
                canClose = false;
                break;
        }

        if (closeAfter && canClose) {
            super.onOK();
        }
    }

    private void showSaveDialog() {
        String msg = copyObject + " is edited, do you want to save?";
        int res = JOptionPane.showConfirmDialog(
                this,
                msg,
                "Save",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (res == JOptionPane.YES_OPTION) {
            save(true);
        } else {
            super.onOK();
        }
    }

    //
    // Edited listener
    //
    @Override
    public void onValueChanged(Component component, String s, Object o, Object o1) {
        getButtonNeutral().setEnabled(hasChanged());
    }

    @Override
    public T getGuiObject() {
        if (isShown && !isUpdating()) {
            return copyObject;
        }
        return null;
    }

    //
    // Cache changed
    //
    @Override
    public void onInserted(T object) {
        copyObject = (T) object.createCopy();
        getButtonNeutral().setEnabled(false);
    }

    @Override
    public void onUpdated(T object) {
        copyObject = (T) object.createCopy();
        getButtonNeutral().setEnabled(false);
    }

    @Override
    public void onDeleted(T object) {
        originalObject = null;
        copyObject = null;
        getButtonNeutral().setEnabled(false);
    }

    @Override
    public void onCacheCleared() {
        // Don't care
    }

    //
    // Dialog events
    //
    @Override
    public void windowClosed(WindowEvent e) {
        for (CacheChangedListener listener : cacheListenerList) {
            cache().removeListener(listener);
        }
        super.windowClosed(e);
    }

    @Override
    protected void onOK() {
        if (hasChanged()) {
            showSaveDialog();
        } else {
            super.onOK();
        }
    }

    @Override
    protected void onNeutral() {
        save(false);
    }

    @Override
    protected void onCancel() {
        if (hasChanged()) {
            showSaveDialog();
        } else {
            super.onCancel();
        }
    }
}
