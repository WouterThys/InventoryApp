package com.waldo.inventory.gui.components;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ITabbedPane extends JTabbedPane {

    private ITabRenderer tabRenderer = new DefaultTabRenderer();

    public ITabbedPane() {
        super();
    }

    public ITabbedPane(int tabPlacement) {
        super(tabPlacement);
    }

    public ITabbedPane(int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
    }

    public ITabRenderer getTabRenderer() {
        return tabRenderer;
    }

    public void setTabRenderer(ITabRenderer tabRenderer) {
        this.tabRenderer = tabRenderer;
    }

    @Override
    public void addTab(String title, Component component) {
        this.addTab(title, null, component, null);
    }

    @Override
    public void addTab(String title, Icon icon, Component component) {
        this.addTab(title, icon, component, null);
    }

    @Override
    public void addTab(String title, Icon icon, Component component, String tip) {
        super.addTab(title, icon, component, tip);
        int tabIndex = getTabCount() - 1;
        Component tab = tabRenderer.getTabRendererComponent(this, title, icon, tabIndex);
        super.setTabComponentAt(tabIndex, tab);
    }

    interface ITabRenderer {
        Component getTabRendererComponent(JTabbedPane tabbedPane, String text, Icon icon, int tabIndex);
    }

    public abstract class AbstractTabRenderer implements ITabRenderer {

        private String prototypeText = "";
        private Icon prototypeIcon = UIManager.getIcon("OptionPane.informationIcon");
        private int horizontalTextAlignment = SwingConstants.CENTER;
        private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

        public AbstractTabRenderer() {
            super();
        }

        public void setPrototypeText(String text) {
            String oldText = this.prototypeText;
            this.prototypeText = text;
            firePropertyChange("prototypeText", oldText, text);
        }

        public String getPrototypeText() {
            return prototypeText;
        }

        public Icon getPrototypeIcon() {
            return prototypeIcon;
        }

        public void setPrototypeIcon(Icon icon) {
            Icon oldIcon = this.prototypeIcon;
            this.prototypeIcon = icon;
            firePropertyChange("prototypeIcon", oldIcon, icon);
        }

        public int getHorizontalTextAlignment() {
            return horizontalTextAlignment;
        }

        public void setHorizontalTextAlignment(int horizontalTextAlignment) {
            this.horizontalTextAlignment = horizontalTextAlignment;
        }

        public PropertyChangeListener[] getPropertyChangeListeners() {
            return propertyChangeSupport.getPropertyChangeListeners();
        }

        public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
            return propertyChangeSupport.getPropertyChangeListeners(propertyName);
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }

        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
        }

        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            PropertyChangeListener[] listeners = getPropertyChangeListeners();
            for (int i = listeners.length - 1; i >= 0; i--) {
                listeners[i].propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
            }
        }
    }

    class DefaultTabRenderer extends AbstractTabRenderer implements PropertyChangeListener {

        private Component prototypeComponent;

        public DefaultTabRenderer() {
            super();
            prototypeComponent = generateRendererComponent(getPrototypeText(), getPrototypeIcon(), getHorizontalTextAlignment());
            addPropertyChangeListener(this);
        }

        private Component generateRendererComponent(String text, Icon icon, int horizontalTabTextAlignment) {
            JPanel rendererComponent = new JPanel(new GridBagLayout());
            rendererComponent.setOpaque(false);

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(2, 4, 2, 4);
            c.fill = GridBagConstraints.HORIZONTAL;
            rendererComponent.add(new JLabel(icon), c);

            c.gridx = 1;
            c.weightx = 1;
            rendererComponent.add(new JLabel(text, horizontalTabTextAlignment), c);

            return rendererComponent;
        }

        @Override
        public Component getTabRendererComponent(JTabbedPane tabbedPane, String text, Icon icon, int tabIndex) {
            Component rendererComponent = generateRendererComponent(text, icon, getHorizontalTextAlignment());
            int prototypeWidth;
            int prototypeHeight;

            prototypeWidth = prototypeComponent.getPreferredSize().width;
            prototypeHeight = prototypeComponent.getPreferredSize().height;

            rendererComponent.setPreferredSize(new Dimension(prototypeWidth, prototypeHeight));
            return rendererComponent;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if ("prototypeText".equals(propertyName) || "prototypeIcon".equals(propertyName)) {
                this.prototypeComponent = generateRendererComponent(getPrototypeText(), getPrototypeIcon(), getHorizontalTextAlignment());
            }
        }
    }

}
