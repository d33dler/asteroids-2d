package nl.rug.aoop.asteroids.view.menus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class MenuButton {

    /**
     * A class for simplifying the use of custom drawn strings
     * on a frame as buttons.
     */
    /**
     * The text of the button.
     */

    private String text;
    /**
     * The coordinates where it will be printed and
     * the height and width, used for testing whether
     * the button has been clicked.
     */

    private int x, y, width, height;
    /**
     * The metrics of the font, used for knowing the exact
     * position of the button on the panel.
     */
    private FontMetrics metrics;
    private AbstractAction action;
    /**
     * Initializes the button after which draws it.
     *
     * @param font the font used for computing some sizes.
     * @param rect for positioning on the panel.
     * @param g    the graphic component on which to draw.
     */
    public MenuButton(AbstractAction a, Rectangle rect, Font font, Graphics g) {
        this.action = a;
        this.text = (String) a.getValue("Name");
        metrics = g.getFontMetrics(font);
        x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        width = metrics.stringWidth(text);
        height = metrics.getHeight();
        drawMenuItem(text, font, g);
    }

    /**
     * Draws the menu item.
     *
     * @param text the text to be drawn.
     * @param font the font to be used.
     * @param g    the graphic component on which to draw.
     */
    private void drawMenuItem(String text, Font font, Graphics g) {
        g.setFont(font);
        g.drawString(text, x, y);
    }

    /**
     * Get the text contained in this menu item.
     *
     * @return the text of the menu item.
     */
    public String getText() {
        return this.text;
    }

    /**
     * Checks if the button has been clicked.
     *
     * @param e the mouse event containing the coordinates of the click.
     * @return true if it was clicked, false otherwise.
     */
    public void clicked(MouseEvent e) {
        int clickX = e.getX(), clickY = e.getY();
        if(clickX >= x && clickX <= (x + width)
                && clickY >= (y - height) && clickY <= y){
            action.actionPerformed(null);
        };
    }
}
