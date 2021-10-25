package nl.rug.aoop.asteroids.control.menu_commands.pause;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.view.menus.pause_menu.PauseMenu;
import nl.rug.aoop.asteroids.view.panels.AsteroidsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PauseCommand extends AbstractAction {

    private final ViewController viewController;

    public PauseCommand(ViewController manager){
        super("Pause");
        this.viewController = manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
