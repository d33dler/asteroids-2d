package nl.rug.aoop.asteroids.view.menus;

import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.control.ViewController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class MenuDefaultBlueprint extends JPanel implements MouseListener, MouseMotionListener, MenuBlueprint {


    public BufferedImage background;
    private final ViewController viewController;
    private Graphics g;
    protected int ITEMS_DIST_X = 0, ITEM_DIST_Y = 110;
    private Color defaultHlColor = MenuButton.defaultCol;

    public MenuDefaultBlueprint(ViewController viewController) {
        this.viewController = viewController;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private final List<Tuple.T3<AbstractAction, Rectangle, Font>> buttons = new ArrayList<>();
    private final List<MenuButton> menuButtons = new ArrayList<>();
    private final List<JLabel> labels = new ArrayList<>();
    private int buff_x = 0, buff_y = 0;

    public void addNewButton(AbstractAction r, int w, int h, Font f, Color c) {
        this.defaultHlColor = c;
        buttons.add(new Tuple.T3<>(r, new Rectangle(w + buff_x, h + buff_y), f));
        buff_x += ITEMS_DIST_X;
        buff_y += ITEM_DIST_Y;
    }

    public void addNewButton(AbstractAction r, int w, int h, Font f) {
        buttons.add(new Tuple.T3<>(r, new Rectangle(w + buff_x, h + buff_y), f));
        buff_x += ITEMS_DIST_X;
        buff_y += ITEM_DIST_Y;
    }

    public void addBackground(String url) {
        try {
            background = ImageIO.read(Path.of(url).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void addText(String text) {
        labels.add(new JLabel(text));
    }

    public void render(int w, int h) {
        for (Tuple.T3<AbstractAction, Rectangle, Font> t : buttons) {
            MenuButton menuButton = new MenuButton(this, t.a, t.b, t.c, defaultHlColor);
            menuButtons.add(menuButton);
        }
        setBackground(new Color(118, 18, 18));
        setLayout(new BorderLayout());
        labels.forEach(this::add);
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
        menuButtons.forEach(menuButtons -> menuButtons.refresh(g));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        for (MenuButton button : menuButtons) {
            if (button.clicked(e)) {
                button.executeAction();
                break;
            }
        }
    }

    public synchronized void refresh() {
        repaint();
        revalidate();
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

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (MenuButton button : menuButtons) {
            if (button.clicked(e)) {
                button.highlight(true);
            } else {
                if (button.isHighlighted()) {
                    button.highlight(false);
                }
            }
        }
    }
}
