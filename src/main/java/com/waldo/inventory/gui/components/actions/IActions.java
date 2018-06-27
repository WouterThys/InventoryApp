package com.waldo.inventory.gui.components.actions;

import com.waldo.inventory.classes.dbclasses.Set;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public class IActions {

    public static abstract class IAction extends IAbstractAction {
        protected IAction() {
            this("", null);
        }
        protected IAction(String name) {
            this(name, null);
        }
        protected IAction(String name, ImageIcon imageIcon) {
            super(name, imageIcon);
            setTooltip(name);
        }
    }

    public static abstract class AddAction extends IAbstractAction {
        protected AddAction() {
            super("Add", imageResource.readIcon("Add.SS"));
        }
    }

    public static abstract class EditAction extends IAbstractAction {
        protected EditAction() {
            super("Edit", imageResource.readIcon("Pen.SS"));
        }
        protected EditAction(String tooltip) {
            this();
            setTooltip(tooltip);
        }
    }

    public static abstract class DeleteAction extends IAbstractAction {
        protected DeleteAction() {
            super("Delete", imageResource.readIcon("Delete.SS"));
        }
        protected DeleteAction(ImageIcon imageIcon) {
            super("Delete", imageIcon);
        }
        protected DeleteAction(String tooltip) {
            this();
            setTooltip(tooltip);
        }
    }

    public static abstract class SaveAction extends IAbstractAction {
        protected SaveAction() {
            super("Save", imageResource.readIcon("Save.SS"));
            setTooltip("Save");
        }
        protected SaveAction(ImageIcon icon) {
            super("Save", icon);
            setTooltip("Save");
        }
    }

    public static abstract class SearchAction extends IAbstractAction {
        protected SearchAction() {
            super("Search", imageResource.readIcon("Search.SS"));
            setTooltip("Search");
        }
        protected SearchAction(ImageIcon imageIcon) {
            super("Search", imageIcon);
            setTooltip("Search");
        }
    }


    public static abstract class CopyAction extends IAbstractAction {
        protected CopyAction() {
            super("Copy", imageResource.readIcon("Copy.SS"));
        }
    }

    public static abstract class PasteAction extends IAbstractAction {
        protected PasteAction() {
            super("Paste", imageResource.readIcon("Paste.SS"));
        }
    }

    public static abstract class AddItemToSetAction extends IAbstractAction {

        private final Set set;

        protected AddItemToSetAction(Set set) {
            super(set.toString(), imageResource.readIcon("Components.SS"));
            this.set = set;
        }

        public abstract void onAddToSet(ActionEvent e, Set set);

        @Override
        public void actionPerformed(ActionEvent e) {
            onAddToSet(e, set);
        }
    }

    public static abstract class AutoCalculateUsedAction extends IAbstractAction {
        protected AutoCalculateUsedAction() {
            super("Auto set used count", imageResource.readIcon("Used.SS"));
            setTooltip("Auto set used count");
        }
    }

    public static abstract class BackToOrderedAction extends IAbstractAction {
        protected BackToOrderedAction() {
            super("Back to ordered", imageResource.readIcon("OrderStates.TruckOut.SS"));
            setTooltip("Back to ordered");
        }
    }

    public static abstract class BackToPlannedAction extends IAbstractAction {
        protected BackToPlannedAction() {
            super("Back to planned", imageResource.readIcon("OrderStates.CalendarOut.SS"));
            setTooltip("Back to planned");
        }
    }

    public static abstract class MoveToOrderedAction extends IAbstractAction {
        protected MoveToOrderedAction() {
            super("To ordered", imageResource.readIcon("OrderStates.TruckIn.SS"));
        }
    }

    public static abstract class MoveToReceivedAction extends IAbstractAction {
        protected MoveToReceivedAction() {
            super("To received", imageResource.readIcon("OrderStates.BoxIn.SS"));
        }
    }

    public static abstract class OrderDetailsAction extends IAbstractAction {
        protected OrderDetailsAction() {
            super("ItemOrder details", imageResource.readIcon("Gears.SS"));
            setTooltip("ItemOrder details");
        }
    }

    public static abstract class OrderItemAction extends IAbstractAction {
        protected OrderItemAction() {
            super("ItemOrder item", imageResource.readIcon("Order.SS"));
            setTooltip("ItemOrder item");
        }
    }

    public static abstract class LockAction extends IAbstractAction {
        private static final ImageIcon lockedIcon = imageResource.readIcon("Lock.SS");
        private static final ImageIcon unlockedIcon = imageResource.readIcon("Lock.Open.SS");

        private boolean locked;
        protected LockAction(boolean locked) {
            super("Lock", lockedIcon);
            this.locked = locked;
            setView(locked);
        }

        private void setView(boolean locked) {
            if (locked) {
                setIcon(lockedIcon);
                setName("Unlock");
            } else {
                setIcon(unlockedIcon);
                setName("Lock");
            }
        }

        public abstract void actionPerformed(ActionEvent e, boolean locked);

        @Override
        public void actionPerformed(ActionEvent e) {
            locked = !locked;
            setView(locked);
            actionPerformed(e, locked);
        }
    }


    public static abstract class OpenItemDataSheetLocalAction extends IAbstractAction {
        protected OpenItemDataSheetLocalAction() {
            super("Local data sheet", imageResource.readIcon("Folder.SS"));
        }
    }

    public static abstract class OpenItemDataSheetOnlineAction extends IAbstractAction {
        protected OpenItemDataSheetOnlineAction() {
            super("Online data sheet", imageResource.readIcon("Earth.SS"));
        }
    }


    public static abstract class BrowseFileAction extends IAbstractAction {
        protected BrowseFileAction() {
            super("Open", imageResource.readIcon("Browse.Folder.SS"));
        }
    }

    public static abstract class BrowseWebAction extends IAbstractAction {
        protected BrowseWebAction() {
            super("Browse", imageResource.readIcon("Earth.SS"));
        }
    }

    public static abstract class CheckItOutAction extends IAbstractAction {
        protected CheckItOutAction() {
            super("Check it out", imageResource.readIcon("View.S"));
        }
    }

    public static abstract class CreateSetItemSeriesAction extends IAbstractAction {
        public CreateSetItemSeriesAction() {
            super("Create series", imageResource.readIcon("Wizard.S"));
            setTooltip("Create series");
        }
    }

    public static abstract class DoItAction extends IAbstractAction {
        public DoItAction() {
            super("", imageResource.readIcon("Wizard.S"));
        }
    }

    public static abstract class GoAction extends IAbstractAction {
        public GoAction() {
            super("Go", imageResource.readIcon("Go.S"));
        }
    }

    public static abstract class EditReferenceAction extends IAbstractAction {
        protected EditReferenceAction() {
            super("Edit reference", imageResource.readIcon("Distributor.SS"));
            setTooltip("Edit reference");
        }
    }

    public static abstract class RenameAction extends IAbstractAction {
        protected RenameAction() {
            super("Rename", imageResource.readIcon("Refresh.Cloud.S"));
            setTooltip("Rename");
        }
    }

    public static abstract class ReplaceAction extends IAbstractAction {
        protected ReplaceAction() {
            super("Replace", imageResource.readIcon("Replace.S"));
            setTooltip("Replace");
        }
    }

    public static abstract class ShowItemHistoryAction extends IAbstractAction {
        protected ShowItemHistoryAction() {
            super("Show history", imageResource.readIcon("History.SS"));
        }
    }

    public static abstract class TableOptionsAction extends IAbstractAction {
        protected TableOptionsAction() {
            super("Table options", imageResource.readIcon("Sort.Descending.S"));
            setTooltip("Table options");
        }
    }

    public static abstract class UpdateSetItemLocationsAction extends IAbstractAction {
        public UpdateSetItemLocationsAction() {
            super("Create series", imageResource.readIcon("Location.S"));
        }
    }

    public static abstract class ViewAllLinksAction extends IAbstractAction {
        protected ViewAllLinksAction() {
            super("All links", imageResource.readIcon("Link.SS"));
            setTooltip("All links");
        }
    }

    public static abstract class WizardAction  extends IAbstractAction {
        protected WizardAction() {
            this("Wizard", imageResource.readIcon("Wizard.SS"));
        }
        protected WizardAction(String name) {
            this(name, imageResource.readIcon("Wizard.SS"));
        }
        protected WizardAction(String name, ImageIcon imageIcon) {
            super(name, imageIcon);
            setTooltip(name);
        }
    }

    public static abstract class PlusOneAction extends IAbstractAction {
        protected PlusOneAction() {
            super("Plus one", imageResource.readIcon("PlusOne.SS"));
        }
    }

    public static abstract class MinOneAction  extends IAbstractAction {
        protected MinOneAction() {
            super("Minus one", imageResource.readIcon("MinOne.SS"));
        }
    }

    public static abstract class UseAction extends IAbstractAction {
        protected UseAction() {
            super("Use this", imageResource.readIcon("Arrow.Down.SS"));
        }
        protected UseAction(ImageIcon icon) {
            super("Use this", icon);
            setTooltip("Use this");
        }
    }

    public static abstract class NotUsedAction extends IAbstractAction {
        protected NotUsedAction() {
            this(imageResource.readIcon("Forbidden.S"));
        }
        protected NotUsedAction(ImageIcon icon) {
            super("Not used", icon);
            setTooltip("Not used on PCB");
        }
    }

    public static abstract class TestAction extends IAbstractAction {
        protected TestAction() {
            super("Test", imageResource.readIcon("Actions.Test"));
        }
        protected TestAction(ImageIcon icon) {
            super("Test", icon);
            setTooltip("Test");
        }
    }
    public static abstract class AddToPendingOrderAction extends IAbstractAction {
        protected AddToPendingOrderAction() {
            super("AddPending", imageResource.readIcon("Order.Add.S"));
        }
        protected AddToPendingOrderAction(ImageIcon icon) {
            super("Add to pending", icon);
            setTooltip("Add to pending");
        }
    }

    public static abstract class RemoveAllAction extends IAbstractAction {
        protected RemoveAllAction() {
            super("Remove all", imageResource.readIcon("Arrow.Sort.S"));
        }
        protected RemoveAllAction(ImageIcon icon) {
            super("Remove all", icon);
            setTooltip("Remove all");
        }
    }

    public static abstract class NextAction extends IAbstractAction {
        protected NextAction() {
            super("Next", imageResource.readIcon("Arrow.Right.XXL"));
            setTooltip("Next");
        }
    }

    public static abstract class PreviousAction extends IAbstractAction {
        protected PreviousAction() {
            super("Previous", imageResource.readIcon("Arrow.Left.XXL"));
            setTooltip("Previous");
        }
    }

    public static abstract class InventoryAction extends IAbstractAction {
        protected InventoryAction() {
            super("Inventory", imageResource.readIcon("Count.S"));
            setTooltip("Inventory");
        }
    }

    public static abstract class NavigateUpAction extends IAbstractAction {
        protected NavigateUpAction() {
            super("Up", imageResource.readIcon("Arrow.Up.S"));
            setTooltip("Up");
        }
    }

    public static abstract class NavigateDownAction extends IAbstractAction {
        protected NavigateDownAction() {
            super("Down", imageResource.readIcon("Arrow.Down.S"));
            setTooltip("Down");
        }
    }

    public static abstract class ConnectAction extends IAbstractAction {
        protected ConnectAction() {
            super("Connect", imageResource.readIcon("Plug.Add.S"));
            setTooltip("Connect");
        }
    }

    public static abstract class DisconnectAction extends IAbstractAction {
        protected DisconnectAction() {
            super("Disconnect", imageResource.readIcon("Plug.Delete.S"));
            setTooltip("Disconnect");
        }
    }

    public static abstract class SendContentAction extends IAbstractAction {
        protected SendContentAction() {
            super("Send content", imageResource.readIcon("Folder.Network.S"));
            setTooltip("Send content");
        }
    }

    public static abstract class PrintAction extends IAbstractAction {
        protected PrintAction() {
            super("Print", imageResource.readIcon("Printer.SS"));
            //setTooltip("Send content");
        }
    }

    public static abstract class SolderedAction extends IAbstractAction {
        protected SolderedAction() {
            super("Set soldered", imageResource.readIcon("Solder.S"));
            setTooltip("Set soldered");
        }
    }

    public static abstract class DesolderedAction extends IAbstractAction {
        protected DesolderedAction() {
            super("Desolder", imageResource.readIcon("Solder.Out.S"));
            setTooltip("Desoldered");
        }
    }
}
