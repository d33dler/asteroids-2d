package nl.rug.aoop.asteroids.view.menus.main_menu;

import nl.rug.aoop.asteroids.view.ViewManager;
import nl.rug.aoop.asteroids.view.menus.MenuBlueprint;

import javax.swing.*;
import java.util.List;

public class MainMenu extends MenuBlueprint {

    public final static int BUTTON_W = 90, BUTTON_H = 30, FRAME_W = 500, FRAME_H = 500;

    public MainMenu(ViewManager v, List<AbstractAction> menuCommands, String image) {
        super(v);
        init(menuCommands);
        addBackground(image);
        render(this, FRAME_W, FRAME_H);
    }

    private void init(List<AbstractAction> menuCommands) {
        for (AbstractAction c : menuCommands) {
            addNewButton(c, BUTTON_W, BUTTON_H);
        }
    }
}
