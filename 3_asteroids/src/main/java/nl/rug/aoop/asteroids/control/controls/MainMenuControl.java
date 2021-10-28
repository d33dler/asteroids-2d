package nl.rug.aoop.asteroids.control.controls;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.util.ReflectionUtils;
import nl.rug.aoop.asteroids.view.menus.main_menu.MainMenu;

import javax.swing.*;
import java.util.List;

/**
 * Control used by controller to display the main menu of the game
 */
public class MainMenuControl extends Control {

    private final static String MAIN_M_BG = "images/menu_bg.png";
    private final static String MAIN_M_PKG = "nl.rug.aoop.asteroids.control.menu_commands.main";
    private List<AbstractAction> mainMenuActions;

    /**
     * Assigns view controller to super class and initializes menu actions
     *
     * @param controller The view controller needed for display operations
     */
    public MainMenuControl(ViewController controller){
        super(controller);
        mainMenuActions = ReflectionUtils.getMenuCommands(controller, MAIN_M_PKG);
    }

    @Override
    public void display() {
        MainMenu menu = new MainMenu(controller, mainMenuActions, MAIN_M_BG);
        super.display(menu);
    }
}
