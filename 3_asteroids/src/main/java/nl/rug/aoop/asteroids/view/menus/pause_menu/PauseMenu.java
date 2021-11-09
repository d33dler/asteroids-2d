package nl.rug.aoop.asteroids.view.menus.pause_menu;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.menu_commands.pause.ReturnCommand;
import nl.rug.aoop.asteroids.view.menus.MenuDefaultBlueprint;

import java.awt.*;

public class PauseMenu extends MenuDefaultBlueprint {
    public final static int BUTTON_X = 1000, BUTTON_Y = 1000, FRAME_W = 500, FRAME_H = 500;
    public final static Font font = new Font("Tahoma", Font.BOLD, 35);

    public final static int TXT_X = 1200, TXT_Y = 1200;
    /**
     * This constructor assigns the view controller, adds a return button and renders the score panel
     *
     * @param viewController Controller needed for button actions
     */


    public PauseMenu(ViewController viewController) {

        super();
        addNewButton(new ReturnCommand(viewController), BUTTON_X, BUTTON_Y, font);
        addText("PAUSED", 450,400);
        render(FRAME_W, FRAME_H);
    }
}
