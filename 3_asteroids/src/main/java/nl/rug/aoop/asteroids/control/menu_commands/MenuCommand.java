package nl.rug.aoop.asteroids.control.menu_commands;

import nl.rug.aoop.asteroids.view.ViewManager;

public interface MenuCommand {
    String getName();
    void execute(ViewManager manipulator);
}
