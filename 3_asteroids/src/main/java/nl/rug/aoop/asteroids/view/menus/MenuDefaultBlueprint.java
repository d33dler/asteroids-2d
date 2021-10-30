package nl.rug.aoop.asteroids.view.menus;

import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

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

/**
 * This class offers a potentially re-usable blueprint to create different menus in the game
 */
public abstract class MenuDefaultBlueprint extends JPanel implements MouseListener, MouseMotionListener, MenuBlueprint {

    public BufferedImage background;
    private Graphics g;
    protected int ITEMS_DIST_X = 0, ITEM_DIST_Y = 110;
    private Color defaultHlColor = MenuButton.defaultCol;

    /**
     * Adds the menu to mouse listeners
     */
    public MenuDefaultBlueprint() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private final List<Tuple.T3<AbstractAction, Rectangle, Font>> buttons = new ArrayList<>();
    private final List<MenuButton> menuButtons = new ArrayList<>();
    private final List<Tuple.T3<String, Integer, Integer>> labels = new ArrayList<>();
    private int buff_x = 0, buff_y = 0;

    /**
     * Adds new button to the panel and updates distance
     *
     * @param r The action object
     * @param w The width of the button
     * @param h The height of the button
     * @param f The font to be used for the text
     */
    public void addNewButton(AbstractAction r, int w, int h, Font f) {
        buttons.add(new Tuple.T3<>(r, new Rectangle(w + buff_x, h + buff_y), f));
        buff_x += ITEMS_DIST_X;
        buff_y += ITEM_DIST_Y;
    }

    /**
     * Loads the image from the given url to be used as background
     *
     * @param url The location of the image file
     */
    public void addBackground(String url) {
        try {
            background = ImageIO.read(Path.of(url).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a label to display text to the panel
     *
     * @param text Text to be displayed
     * @param x X-position of the label
     * @param y Y-position of the label
     */
    protected void addText(String text, int x, int y) {
        labels.add(new Tuple.T3<>(text, x, y));
    }

    /**
     * This method executes all the Swing operations needed to add components to the menu and finally renders it
     *
     * @param w Width of the panel
     * @param h Height of the panel
     */
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

    /**
     * Repaints panel when updated
     */
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
