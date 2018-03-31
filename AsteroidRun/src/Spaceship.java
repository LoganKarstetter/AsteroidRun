import java.awt.*;
import java.awt.image.BufferedImage;
/**
 * @author Logan Karstetter
 * Date: 02/11/2018
 */
public class Spaceship
{
    /** The x-coordinate position of this spaceship */
    private int xPos;
    /** The y-coordinate position of this spaceship */
    private int yPos;

    /** The width of this spaceship in pixels */
    private int width = 10; //10 by default
    /** The height of this spaceship in pixels */
    private int height = 10;

    /** The number of pixels this ship will move in the x-direction each update */
    private int step = 10;

    /** The ImageLoader used to load and store images for this game */
    private ImageLoader imageLoader;
    /** The SequencePlayer used to display the animations for this spaceship */
    private SequencePlayer sequencePlayer;
    /** The current image used to represent the spaceship */
    private BufferedImage image;

    /** The AsteroidManager that encloses all the asteroids */
    private AsteroidManager asteroidManager;
    /** The KeyManager used to control the spaceship's movement for the game */
    private KeyManager keyManager;
    /** The AsteroidRunPanel that updates and renders this spaceship */
    private AsteroidRunPanel asteroidRunPanel;

    /**
     * Create a new spaceship for a game of AsteroidRun. The inputted image name is used to load the
     * spaceships animation sequence. The sequence player is updated using the loopPeriod of the enclosing
     * AsteroidRunPanel. The AsteroidManager, ImageLoader, KeyManager, and AsteroidPanel are also passed
     * to load the images, determine the direction to move the ship, and end the game if the spaceship
     * collides with an asteroid.
     * @param imageName The name of the stored sequence of images used to animate this spaceship.
     * @param loopPeriod The loopPeriod of the enclosing AsteroidRunPanel, used to update the animation.
     * @param asteroidManager The AsteroidManager that encloses all the asteroids.
     * @param imageLoader The ImageLoader used to load images for this game.
     * @param keyManager The KeyManager used to process key events for this game.
     * @param asteroidRunPanel The AsteroidRunPanel that encloses the components of the game.
     */
    public Spaceship(String imageName, long loopPeriod, AsteroidManager asteroidManager,
                     ImageLoader imageLoader, KeyManager keyManager, AsteroidRunPanel asteroidRunPanel)
    {
        //Store the AsteroidManager, ImageLoader, KeyManager, and AsteroidRunPanel references
        this.asteroidManager = asteroidManager;
        this.imageLoader = imageLoader;
        this.keyManager = keyManager;
        this.asteroidRunPanel = asteroidRunPanel;

        //Create the SequencePlayer and store the current image
        sequencePlayer = new SequencePlayer(imageName, 1, loopPeriod, imageLoader);
        image = sequencePlayer.getCurrentImage();
        width = image.getWidth();
        height = image.getHeight();

        //Determine the initial position
        xPos = AsteroidRunPanel.WIDTH/2 - (width/2);
        yPos = AsteroidRunPanel.HEIGHT - height - 50;
    }

    /**
     * Update the position and animation of the spaceship and check if it has run into
     * any asteroids since the last update.
     */
    public void update()
    {
        //Check if the ship has hit an asteroid
        hasHitAsteroid(); //The asteroidManager will handle ending the game, if applicable

        //Update the sequencePlayer and move the ship
        sequencePlayer.update();
        move();
    }

    /**
     * Move the spaceship left, right, or not at all depending on the state of the KeyManager.
     */
    private void move()
    {
        //Move the spaceship according to its position in the panel and key presses
        if (keyManager.left) //Move left
        {
            //Make sure the ship does not move offscreen
            if (xPos - step >= 0)
            {
                xPos = xPos - step;
            }
            else //If the xStep would move the ship offscreen, set its xPos to zero
            {
                xPos = 0;
            }
        }
        else if (keyManager.right) //Move right
        {
            //Make sure the ship does not move offscreen to the right
            if (xPos + step <= AsteroidRunPanel.WIDTH - width)
            {
                xPos = xPos + step;
            }
            else //Move the ship as far to the right as possible
            {
                xPos = AsteroidRunPanel.WIDTH - width;
            }
        }
    }

    /**
     * Determine whether the spaceship has collided with any asteroids. If so the game is over and this
     * method returns true. If not, this method will return false.
     * @return True or false.
     */
    private boolean hasHitAsteroid()
    {
        //Check if the ship has hit any asteroids, give the player some wiggle room
        return asteroidManager.checkCollisions(new Rectangle(xPos +  25, yPos + 25, width - 25,
                height - 25), true);
    }

    /**
     * Draw the spaceship using its current image of animation. If the current image cannot be
     * found then a placeholder red square is draw instead.
     * @param dbGraphics The Graphics object used to draw the spaceship.
     */
    public void draw(Graphics dbGraphics)
    {
        //Get the image from the sequence player
        image = sequencePlayer.getCurrentImage();
        if (image != null)
        {
            dbGraphics.drawImage(image, xPos, yPos, null);
        }
        else //Draw the spaceship as a red square
        {
            dbGraphics.setColor(Color.RED);
            dbGraphics.fillRect(xPos, yPos, width, height);
        }
    }
}
