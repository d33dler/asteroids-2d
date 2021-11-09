package nl.rug.aoop.asteroids.control.menu_commands.pause;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.control.controls.MainMenuControl;
import nl.rug.aoop.asteroids.model.game.Game;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * ViewStats - abstract action class attached to menu button; Activated upon click
 */
@MenuCommands(id = "return")
public class ReturnCommand extends AbstractAction {

    private final ViewController manager;

    public ReturnCommand(ViewController manager){
        super("MAIN MENU");
        this.manager = manager;
    }

    /**
     * Quits the game , creates a new one and updates the instance for
     * superior model classes. Finally, displays the main menu.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        manager.getGame().quit();
        Game newGame = new Game();
        manager.setGame(newGame);
        newGame.setViewController(manager);
        manager.getFrame().resetGame(newGame);
        manager.displayPane(new MainMenuControl(manager));
    }
}
