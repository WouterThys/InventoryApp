package com.waldo.inventory.gui.components.actions;

import static com.waldo.inventory.gui.Application.imageResource;

public class IActions {

    public static abstract class AddAction extends IAbstractAction {
        protected AddAction() {
            super("Add", imageResource.readImage("Actions.Add"));
        }
    }

    public static abstract class EditAction extends IAbstractAction {
        protected EditAction() {
            super("Edit", imageResource.readImage("Actions.Edit"));
        }
    }

    public static abstract class DeleteAction extends IAbstractAction {
        protected DeleteAction() {
            super("Delete", imageResource.readImage("Actions.Delete"));
        }
    }

}
