package nl.rug.aoop.asteroids.view.menus;

import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.control.ViewController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class MenuBlueprint extends JPanel implements MouseListener {



    public BufferedImage background;
    private final ViewController viewController;
    private Graphics g;
    protected int ITEMS_DIST_X = 0, ITEM_DIST_Y = 110;

    public MenuBlueprint(ViewController viewController) {
        this.viewController = viewController;
        addMouseListener(this);
    }

    private final List<Tuple.T3<AbstractAction, Rectangle, Font>> buttons = new ArrayList<>();
    private final List<MenuButton> menuButtons = new ArrayList<>();
    private int buff_x = 0, buff_y = 0;

    public void addNewButton(AbstractAction r, int w, int h, Font f) {
        buttons.add(new Tuple.T3<>(r, new Rectangle(w+buff_x, h+buff_y), f));
        buff_x+=ITEMS_DIST_X;
        buff_y+=ITEM_DIST_Y;
    }

    public void addBackground(String url) {
        try {
            background = ImageIO.read(Path.of(url).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render(int w, int h) {
        setBackground(new Color(118, 18, 18));
        setLayout(new BorderLayout());
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.PAGE_AXIS));
        menuPanel.setSize(w, h);
        add(menuPanel, BorderLayout.WEST);
        setSize(w, h);
        setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.g = g;
        g.drawImage(background, 0, 0, null);
        g.setColor(new Color(249, 252, 205, 224));
        for (Tuple.T3<AbstractAction, Rectangle, Font> t : buttons) {
            MenuButton menuButton = new MenuButton(t.a, t.b, t.c,g);
            menuButtons.add(menuButton);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        for (MenuButton button : menuButtons) {
            if(button.clicked(e)) {
                button.executeAction();
                break;
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

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
