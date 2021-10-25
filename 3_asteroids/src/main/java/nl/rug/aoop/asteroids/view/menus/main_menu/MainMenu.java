package nl.rug.aoop.asteroids.view.menus.main_menu;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.view.menus.MenuDefaultBlueprint;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainMenu extends MenuDefaultBlueprint {

    public final static int BUTTON_X = 500, BUTTON_Y = 1200, FRAME_W = 500, FRAME_H = 500;
    public final static Font font = new Font("Tahoma", Font.BOLD, 49);

    public MainMenu(ViewController v, List<AbstractAction> menuCommands, String image) {
        super(v);
        init(menuCommands);
        addBackground(image);
        render(FRAME_W, FRAME_H);
    }

    private void init(List<AbstractAction> menuCommands) {
        for (AbstractAction c : menuCommands) {
            addNewButton(c, BUTTON_X, BUTTON_Y,font);
        }
    }
}
