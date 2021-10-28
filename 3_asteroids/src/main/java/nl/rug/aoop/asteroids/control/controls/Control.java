package nl.rug.aoop.asteroids.control.controls;

import nl.rug.aoop.asteroids.control.ViewController;

import javax.swing.*;

/**
 * This abstract class groups functionality for controls that are used to
 * display panels in the application
 */
public abstract class Control {

    protected ViewController controller;

    /**
     * Default constructor that only assigns the view controller, operations
     * that are specific to a certain control are delegated to specialized
     * constructors
     *
     * @param controller
     */
    public Control(ViewController controller){
        this.controller = controller;
    }

    /**
     * Method that executes operations needed in view controller
     */
    protected void display(JPanel panel){
        controller.removePanels();
        controller.validatePanel(panel);
    }

    /**
     * Method to be overridden that may call the specialized one
     */
    public abstract void display();
}
