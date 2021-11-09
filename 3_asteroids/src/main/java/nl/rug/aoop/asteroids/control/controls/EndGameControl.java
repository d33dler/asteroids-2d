package nl.rug.aoop.asteroids.control.controls;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.view.menus.EndgameMenu;

/**
 * Control used by controller to display End Game panel
 */
public class EndGameControl extends Control {

    private final static String GAME_OVER_BG = "images/game_over.png";

    /**
     * Constructor which only assigns the view controller to super class
     *
     * @param controller The view controller
     */
    public EndGameControl(ViewController controller) {
        super(controller);
    }

    @Override
    public void display() {
        EndgameMenu menu = new EndgameMenu(controller,
                controller.getGame().getUserSpaceship().getScore(),
                GAME_OVER_BG);
        super.display(menu);
    }
}
