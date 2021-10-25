package nl.rug.aoop.asteroids.view.menus;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class MenuButton implements InterfaceMotions {

    /**
     * A class for simplifying the use of custom drawn strings
     * on a frame as buttons.
     */
    /**
     * The text of the button.
     */
    public static Color highlightCol = new Color(126, 232, 236, 200);
    public static Color defaultCol = new Color(231, 170, 14, 180);
    private final String text;
    /**
     * The coordinates where it will be printed and
     * the height and width, used for testing whether
     * the button has been clicked.
     */

    private int x;
    private int y;
    private int width;
    private int height;

    private Color customColor;
    private boolean setCustom = false;
    @Getter
    @Setter
    private boolean highlighted = false;
    /**
     * The metrics of the font, used for knowing the exact
     * position of the button on the panel.
     */
    private FontMetrics metrics;
    private final AbstractAction action;
    @Getter
    private int currentAlpha;

    public Color currentColor = defaultCol;
    /**
     * Initializes the button after which draws it.
     *
     * @param font the font used for computing some sizes.
     * @param rect for positioning on the panel.
     * @param g    the graphic component on which to draw.
     */

    private final MenuBlueprint blueprint;
    private final Rectangle rectangle;
    private Font font;

    public MenuButton(MenuBlueprint blueprint, AbstractAction a, Rectangle rect, Font font) {
        this.rectangle = rect;
        this.blueprint = blueprint;
        this.action = a;
        this.text = (String) a.getValue("Name");
        this.font = font;
    }

    public MenuButton(MenuBlueprint blueprint, AbstractAction a, Rectangle rect, Font font, Color customHlColor) {
        this(blueprint, a, rect, font);
        this.customColor = customHlColor;
        setCustom = true;
    }


    /**
     * Draws the menu item.
     *
     * @param text the text to be drawn.
     * @param font the font to be used.
     * @param g    the graphic component on which to draw.
     */
    private void drawItem(String text, Font font, Graphics g) {
        g.setFont(font);
        g.drawString(text, x, y);
    }

    public void refresh(Graphics g) {
        if (metrics == null) {
            metrics = g.getFontMetrics(font);
            x = rectangle.x + (rectangle.width - metrics.stringWidth(text)) / 2;
            y = rectangle.y + ((rectangle.height - metrics.getHeight()) / 2) + metrics.getAscent();
            width = metrics.stringWidth(text);
            height = metrics.getHeight();
        }
        g.setFont(font);
        g.setColor(currentColor);
        g.drawString(text, x, y);
    }

    public void resetHl() {
        setCustom = false;
    }

    public void setHlColor(Color color) {
        customColor = color;
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
    public boolean clicked(MouseEvent e) {
        int clickX = e.getX(), clickY = e.getY();
        return (clickX >= x && clickX <= (x + width)
                && clickY >= (y - height) && clickY <= y);
    }

    public void executeAction() {
        action.actionPerformed(null);
    }


    public void highlight(boolean hl) {
        if (hl) currentColor = highlightCol;
        else currentColor = defaultCol;
        highlighted = hl;
        blueprint.refresh();
    }


}
