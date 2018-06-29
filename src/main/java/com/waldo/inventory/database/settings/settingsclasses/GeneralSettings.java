package com.waldo.inventory.database.settings.settingsclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;

public class GeneralSettings extends DbSettingsObject {

    private static final String TABLE_NAME = "generalsettings";

    private Statics.GuiDetailsView guiDetailsView;
    private String guiLookAndFeel;
    private boolean guiStartUpFullScreen;
    private boolean autoOrderEnabled;


    public GeneralSettings() {
        super(TABLE_NAME);
    }

    public GeneralSettings(String name) {
        this();
        setName(name);
    }


    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (obj instanceof GeneralSettings) {
                GeneralSettings ref = (GeneralSettings) obj;
                if ((ref.getGuiDetailsView().equals(getGuiDetailsView())) &&
                        (ref.getGuiLookAndFeel().equals(getGuiLookAndFeel())) &&
                        (ref.isAutoOrderEnabled() == (isAutoOrderEnabled())) &&
                        (ref.isGuiStartUpFullScreen() == (isGuiStartUpFullScreen())) ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public GeneralSettings createCopy(DbObject copyInto) {
        GeneralSettings copy = (GeneralSettings) copyInto;
        copyBaseFields(copy);

        copy.setGuiDetailsView(getGuiDetailsView());
        copy.setGuiLookAndFeel(getGuiLookAndFeel());
        copy.setGuiStartUpFullScreen(isGuiStartUpFullScreen());
        copy.setAutoOrderEnabled(isAutoOrderEnabled());

        return copy;
    }

    @Override
    public GeneralSettings createCopy() {
        return createCopy(new GeneralSettings());
    }

    @Override
    public void tableChanged(Statics.QueryType changedHow) {

    }

    public Statics.GuiDetailsView getGuiDetailsView() {
        if (guiDetailsView == null) {
            guiDetailsView = Statics.GuiDetailsView.VerticalSplit;
        }
        return guiDetailsView;
    }

    public void setGuiDetailsView(Statics.GuiDetailsView guiDetailsView) {
        this.guiDetailsView = guiDetailsView;
    }

    public void setGuiDetailsView(String guiDetailsView) {
        this.guiDetailsView = Statics.GuiDetailsView.fromString(guiDetailsView);
    }

    public String getGuiLookAndFeel() {
        if (guiLookAndFeel == null) {
            guiLookAndFeel = "Nimbus";
        }
        return guiLookAndFeel;
    }

    public void setGuiLookAndFeel(String guiLookAndFeel) {
        this.guiLookAndFeel = guiLookAndFeel;
    }

    public boolean isGuiStartUpFullScreen() {
        return guiStartUpFullScreen;
    }

    public void setGuiStartUpFullScreen(boolean guiStartUpFullScreen) {
        this.guiStartUpFullScreen = guiStartUpFullScreen;
    }

    public boolean isAutoOrderEnabled() {
        return autoOrderEnabled;
    }

    public void setAutoOrderEnabled(boolean autoOrderEnabled) {
        this.autoOrderEnabled = autoOrderEnabled;
    }
}
