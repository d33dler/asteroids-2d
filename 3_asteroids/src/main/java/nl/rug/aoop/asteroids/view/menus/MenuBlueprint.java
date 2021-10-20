package nl.rug.aoop.asteroids.view.menus;

import nl.rug.aoop.asteroids.control.menu_commands.MenuCommand;
import nl.rug.aoop.asteroids.view.ViewManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class MenuBlueprint extends JPanel {

    public BufferedImage background;
    public List<JButton> buttons = new ArrayList<>();
    private final ViewManager viewManager;

    public MenuBlueprint(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    public void addNewButton(AbstractAction r, int w, int h) {
        JButton button = new JButton(r) {
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(w,h);
            }
        };

        buttons.add(button);
    }

    public void addBackground(String url) {
        try {
            background = ImageIO.read(Path.of(url).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render(JPanel menu, int w, int h) {
        menu.setLayout(new BorderLayout());
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.PAGE_AXIS));
        menuPanel.setSize(w,h);

        for (JButton button : buttons) {
            menuPanel.add(button);
        }

        menu.add(menuPanel,BorderLayout.WEST);
        menu.setSize(w,h);
        menu.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
       
        g.drawImage(background,0,0,null);
    }
}
