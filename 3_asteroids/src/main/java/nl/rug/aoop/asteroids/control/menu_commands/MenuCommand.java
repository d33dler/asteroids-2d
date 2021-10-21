package nl.rug.aoop.asteroids.control.menu_commands;

import nl.rug.aoop.asteroids.control.ViewController;

public interface MenuCommand {
    String getName();
    void execute(ViewController manipulator);
}
