import java.awt.*;
import java.awt.image.BufferedImage;
/**
 * @author Logan Karstetter
 * Date: 02/11/2018
 */
public class Ribbon
{
    /** The x-coordinate position of the ribbon */
    private int xPos = 0;
    /** The y-coordinate position of the ribbon */
    private int yPos = 0;

    /** The width of the ribbon */
    private int width;
    /** The height of the ribbon */
    private int height;

    /** The number of pixels the ribbon moves vertically each game update */
    private int step;

    /** The image displayed by this ribbon */
    private BufferedImage image;

    /**
     * Create a ribbon used to display and shift an image by a set interval each update.
     * @param image The image to be displayed and consequently shifted with each update.
     */
    public Ribbon(BufferedImage image, int step)
    {
        //Store the image and step value
        this.image = image;
        this.step = step;

        //Get the width and height of the image
        width = image.getWidth();
        height = image.getHeight();
    }

    /**
     * Update the ribbon by shifting the image by its step amount.
     */
    public void update()
    {
        //Move the image downwards, reset the position to zero once the entire image has cycled through
        yPos = (yPos + step) % height;
    }

    /**
     * Draw the image to the screen. When drawing the image will be in one of three different 'states'. The
     * first state being that the image's y-coordinate position is zero. This will result in the most of the
     * image being drawn, with the bottom cut off since the image is taller than the panel. The second case
     * is that the image has been shifted down from y-coordinate zero, but not past the height of the panel.
     * In this case, the bottom of the image will be drawn above the current top of the image. The last
     * case is that the entire top of the image is offscreen, so we will only draw the bottom.
     * @param dbGraphics The Graphics object used to draw the image.
     */
    public void draw(Graphics dbGraphics)
    {
        //Draw the image according to the yPos
        if (yPos == 0)
        {
            //Draw the head of the image
            dbGraphics.drawImage(image, 0, 0, AsteroidRunPanel.WIDTH, AsteroidRunPanel.HEIGHT, 0, 0, AsteroidRunPanel.WIDTH, AsteroidRunPanel.HEIGHT, null);
        }
        else if (yPos > 0 && yPos < AsteroidRunPanel.HEIGHT) //The top of the image is within the panel
        {
            //Draw the tail of the image
            dbGraphics.drawImage(image, 0, 0, AsteroidRunPanel.WIDTH, yPos, 0, height - yPos, AsteroidRunPanel.WIDTH, height, null);
            //Draw the head of the image
            dbGraphics.drawImage(image, 0, yPos, AsteroidRunPanel.WIDTH, AsteroidRunPanel.HEIGHT, 0, 0, AsteroidRunPanel.WIDTH, AsteroidRunPanel.HEIGHT - yPos, null);
        }
        else if (yPos >= AsteroidRunPanel.HEIGHT)
        {
            //Draw the tail of the image
            dbGraphics.drawImage(image, 0, 0, AsteroidRunPanel.WIDTH, yPos, 0, height - yPos, AsteroidRunPanel.WIDTH, height, null);
        }
    }
}
