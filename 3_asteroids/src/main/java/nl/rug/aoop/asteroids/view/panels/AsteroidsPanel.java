package nl.rug.aoop.asteroids.view.panels;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.game.GameResources;
import nl.rug.aoop.asteroids.model.gameobjects.gameui.InteractionHud;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ConcurrentModificationException;

/**
 * The panel at the center of the game's window which is responsible for the custom drawing of game objects.
 */
public class AsteroidsPanel extends JPanel implements GameUpdateListener {
    /**
     * The height of the energy bar in pixels
     */
    public static final int ENERGY_BAR_HEIGHT = 15;
    /**
     * The x- and y-coordinates of the score indicator.
     */
    private static final Point SCORE_INDICATOR_POSITION = new Point(20, 20);
    /**
     * The game model that this panel will draw to the screen.
     */
    private final Game game;
    private final GameResources resources;
    /**
     * Number of milliseconds since the last time the game's physics were updated. This is used to continue drawing all
     * game objects as if they have kept moving, even in between game ticks.
     */
    private long timeSinceLastTick = 0L;

    private InteractionHud interactionHud;
    @Setter
    @Getter
    private boolean paused;
    private final static String PAUSE_BG = "images/pause_bg.png";
    private BufferedImage pauseImg;

    private ViewController viewController;

    /**
     * Constructs a new game panel, based on the given model. Also starts listening to the game to check for updates, so
     * that it can repaint itself if necessary.
     *
     * @param game The model which will be drawn in this panel.
     */
    public AsteroidsPanel(ViewController controller, Game game) {
        this.viewController = controller;
        this.game = game;
        this.resources = game.getResources();
        this.interactionHud = new InteractionHud(game);
        game.addListener(this);
        loadPauseBg();
    }

    private void loadPauseBg() {
        try {
            pauseImg = ImageIO.read(Path.of(PAUSE_BG).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method provided by JPanel for 'painting' this component. It is overridden here so that this panel can define
     * some custom drawing. By default, a JPanel is just an empty rectangle.
     *
     * @param graphics The graphics object that exposes various drawing methods to use.
     */
    @Override
    public void paintComponent(Graphics graphics) {
		/* The parent method is first called. Here's an excerpt from the documentation stating why we do this:
		"...if you do not invoke super's implementation you must honor the opaque property, that is if this component is
		opaque, you must completely fill in the background in an opaque color. If you do not honor the opaque property
		you will likely see visual artifacts." Just a little FYI. */
        Graphics2D graphics2D = (Graphics2D) graphics;

        if (!paused) {
            super.paintComponent(graphics);

            // The Graphics2D class offers some more advanced options when drawing, so before doing any drawing, this is obtained simply by casting.

            // Set some key-value options for the graphics object. In this case, this just sets antialiasing to true.
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Since the game takes place in space, it is efficient to just lazily make the background black.
            setBackground(Color.BLACK);
            drawGameObjects(graphics2D);
            drawShipInformation(graphics2D);
        } else paintPauseMenu(graphics2D);
    }

    /**
     * Draws the ship's score and energy.
     *
     * @param graphics2D The graphics object that provides the drawing methods.
     */
    private void drawShipInformation(Graphics2D graphics2D) {
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString(
                String.valueOf(resources.getSpaceShip().getScore()),
                SCORE_INDICATOR_POSITION.x,
                SCORE_INDICATOR_POSITION.y
        );
        graphics2D.setColor(Color.GREEN);
        graphics2D.drawRect(SCORE_INDICATOR_POSITION.x, SCORE_INDICATOR_POSITION.y + 20,
                100, ENERGY_BAR_HEIGHT);
        graphics2D.fillRect(SCORE_INDICATOR_POSITION.x, SCORE_INDICATOR_POSITION.y + 20,
                (int) resources.getSpaceShip().getEnergyPercentage(), ENERGY_BAR_HEIGHT);
    }

    /**
     * Draws all the game's objects. Wraps each object in a view model, then uses that to draw the object.
     *
     * @param graphics2D The graphics object that provides the drawing methods.
     */
    private void drawGameObjects(Graphics2D graphics2D) {
        synchronized (game) {
            if (!game.isGameOver()) {
                resources.setDrawingDone(false);
                while (true) {
                    if (game.rendererDeepCloner.cycleDone) {
                        try {
                            game.rendererDeepCloner.clonedObjects
                                    .forEach(object
                                            -> object.getViewModel(object).drawObject(graphics2D, timeSinceLastTick));
                            resources.setDrawingDone(true);
                            break;
                        } catch (ConcurrentModificationException ignored) {
                        }
                    }
                }
                interactionHud.drawHud(graphics2D);
            }
        }

    }


    private void paintPauseMenu(Graphics2D graphics2D) {
        System.out.println("PAINTING pause");
        graphics2D.drawImage(pauseImg, 0, 0, null);
       viewController.getPMenu().paintOnCustomCanvas(graphics2D);
    }

    /**
     * Do something when the game has indicated that it is updated. For this panel, that means redrawing.
     *
     * @param timeSinceLastTick The number of milliseconds since the game's physics were updated. This is used to allow
     *                          objects to continue to appear animated between each game tick.
     *                          <p>
     *                          Note for your information: when repaint() is called, Swing does some internal stuff, and then paintComponent()
     *                          is called.
     */
    @Override
    public void onGameUpdated(long timeSinceLastTick) {
        this.timeSinceLastTick = timeSinceLastTick;
        repaint();
    }


    public final static Color blur_BG = new Color(0, 0, 0, 110);

    public class GlassPane extends JPanel {

        public GlassPane() {
            this.setOpaque(false);
            this.setBackground(blur_BG);
        }


        @Override
        public final void paint(Graphics g) {
            final Color old = blur_BG;
            g.setColor(getBackground());
            g.fillRect(0, 0, getSize().width, getSize().height);
            g.setColor(old);
            super.paintComponent(g);
        }

    }

    @Override
    public void onGameOver() {

    }
}
