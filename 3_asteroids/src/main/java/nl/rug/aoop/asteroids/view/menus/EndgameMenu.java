package nl.rug.aoop.asteroids.view.menus;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.menu_commands.pause.ExitCommand;
import nl.rug.aoop.asteroids.control.menu_commands.pause.ReturnCommand;

import java.awt.*;

/**
 * This class represents the panel that will be displayed when a game ends
 */
public class EndgameMenu extends MenuBlueprint {

    public final static int BUTTON_X = 250, BUTTON_Y = 250, FRAME_W = 500, FRAME_H = 500;
    public final static Font font = new Font("Tahoma", Font.BOLD, 19);

    /**
     * This constructor assigns the view controller, adds a return button and renders the score panel
     *
     * @param viewController Controller needed for button actions
     */
    public EndgameMenu(ViewController viewController, int score) {
        super(viewController);
        addNewButton(new ReturnCommand(viewController), BUTTON_X, BUTTON_Y, font);
        addText("YOUR SCORE IS: " + score);
        render(FRAME_W, FRAME_H);
    }

}
