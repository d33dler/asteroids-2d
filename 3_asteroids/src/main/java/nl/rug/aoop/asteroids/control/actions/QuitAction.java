package nl.rug.aoop.asteroids.control.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * An action that represents when a user indicates that they wish to quit the application.
 */
public class QuitAction extends AbstractAction {
    /**
     * Construct a new quit action. This calls the parent constructor to give the action a name.
     */
    public QuitAction() {
        super("Quit");
    }

    /**
     * Invoked when an action occurs.
     *
     * @param event The event to be processed.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        System.exit(0);
    }
}
