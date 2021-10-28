package nl.rug.aoop.asteroids.control.controls;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.view.menus.pause_menu.PauseMenu;

/**
 * Control used by controller to manage pause menu
 */
public class PauseControl extends Control {

    private final static String PAUSE_M_PKG = "nl.rug.aoop.asteroids.control.menu_commands.pause";

    /**
     * Default constructor that only assigns the view controller
     *
     * @param controller
     */
    public PauseControl(ViewController controller) {
        super(controller);
    }

    @Override
    public void display() {
        PauseMenu pMenu = new PauseMenu(controller);
        controller.removePanels();
        controller.validatePanel(pMenu);
        pMenu.refresh();
    }
}
