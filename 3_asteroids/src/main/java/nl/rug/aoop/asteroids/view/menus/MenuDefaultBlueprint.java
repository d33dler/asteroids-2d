package nl.rug.aoop.asteroids.view.menus;

import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.control.ViewController;

import javax.imageio.ImageIO;
import javax.persistence.criteria.CriteriaBuilder;
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
    private final List<Tuple.T3<String, Integer, Integer>> labels = new ArrayList<>();
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

    protected void addText(String text, int x, int y) {
        labels.add(new Tuple.T3<>(text, x, y));
    }

    public void render(int w, int h) {
        for (Tuple.T3<AbstractAction, Rectangle, Font> t : buttons) {
            MenuButton menuButton = new MenuButton(this, t.a, t.b, t.c, defaultHlColor);
            menuButtons.add(menuButton);
        }
        setBackground(new Color(118, 18, 18));
        setLayout(new BorderLayout());
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.PAGE_AXIS));
        menuPanel.setSize(w, h);
        add(menuPanel, BorderLayout.WEST);
        setSize(w, h);
        setVisible(true);
    }
    public final static Font labelFont = new Font("Tahoma", Font.BOLD, 30);

    @Override
    protected void paintComponent(Graphics g) {
        this.g = g;
        g.drawImage(background, 0, 0, null);
        menuButtons.forEach(menuButtons -> menuButtons.refresh(g));
        g.setFont(labelFont);
        g.setColor(Color.WHITE);
        labels.forEach(l -> g.drawString(l.a, l.b, l.c));
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
    public synchronized void paintOnCustomCanvas(Graphics g) {
         paintComponent(g);
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
