package nl.rug.aoop.asteroids.view.menus.main_menu;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.view.menus.MenuDefaultBlueprint;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Implementation of Jpanel to display the main menu of the game from which the user can
 * access the game modes or other menus
 */
public class MainMenu extends MenuDefaultBlueprint {

    /**
     * Style constants
     */
    public final static int BUTTON_X = 500, BUTTON_Y = 1200, FRAME_W = 500, FRAME_H = 500;
    public final static Font font = new Font("Tahoma", Font.BOLD, 49);

    /**
     * This constructor initializes menu commands, sets the background and finally renders the menu
     *
     * @param menuCommands The commands to be added to the menu
     * @param image The background image of the menu
     */
    public MainMenu(List<AbstractAction> menuCommands, String image) {
        super();
        init(menuCommands);
        addBackground(image);
        render(FRAME_W, FRAME_H);
    }

    /**
     * Adds the given commands to the panel
     *
     * @param menuCommands The menu commands to be added
     */
    private void init(List<AbstractAction> menuCommands) {
        for (AbstractAction c : menuCommands) {
            addNewButton(c, BUTTON_X, BUTTON_Y,font);
        }
    }
}
