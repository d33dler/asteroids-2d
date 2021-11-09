package nl.rug.aoop.asteroids.control.controls;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.view.panels.AsteroidsPanel;

/**
 * Control used by controller to display the game
 */
public class GameControl extends Control {

    /**
     * Default constructor that only assigns the view controller
     *
     * @param controller The view Controller
     */
    public GameControl(ViewController controller) {
        super(controller);

    }

    @Override
    public void display() {
        AsteroidsPanel pane = new AsteroidsPanel(controller, controller.getGame());
        controller.setGraphicOut(pane);
        super.display(pane);
        controller.getFrame().setFocusable(true);
    }
}
