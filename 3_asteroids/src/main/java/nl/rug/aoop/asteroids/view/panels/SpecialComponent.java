package nl.rug.aoop.asteroids.view.panels;

import javax.swing.*;
import java.awt.*;

public class SpecialComponent extends JComponent {
    private final JComponent component;

    public SpecialComponent(JComponent component) {
        this.component = component;
        setLayout(new BorderLayout());
        setOpaque(false);
        component.setOpaque(false);
        add(component);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(component.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}