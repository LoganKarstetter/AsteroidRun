import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author Logan Karstetter
 * Date: 02/11/2018
 */
public class Asteroid
{
    /** The x-coordinate position of this asteroid */
    private int xPos;
    /** The y-coordinate position of this asteroid */
    private int yPos;

    /** The width of this asteroid's collision box in pixels */
    private int width = 10;
    /** The width of this asteroid's collision box in pixels */
    private int height = 10;

    /** The number of pixels this asteroid will move in the x-direction each update */
    private int xStep;
    /** The number of pixels this asteroid will move in the y-direction each update */
    private int yStep;

    /** Determines whether this asteroid is currently active (should be updated).
     * This field is used to keep the asteroid from immediately resetting after hitting the ship */
    boolean isActive;

    /** The BufferedImage used to display this asteroid on the screen */
    private BufferedImage image;
    /** The BufferedImage used to display an explosion on the screen */
    private BufferedImage explosionImage;
    /** The AsteroidManager that manages this asteroid */
    private AsteroidManager asteroidManager;

    /**
     * Create an Asteroid for a game of AsteroidRun. Asteroids are obstacles floating in space that the
     * player must dodge in their spaceship.
     * @param imageName The name of the image used to display the asteroid on screen.
     * @param explosionImageName The name of the image used to display asteroid explosion on screen.
     * @param imageLoader A reference to the imageLoader for this game.
     * @param asteroidManager A reference to the AsteroidManager that interfaces with the enclosing AsteroidRunPanel.
     */
    public Asteroid(String imageName, String explosionImageName, ImageLoader imageLoader, AsteroidManager asteroidManager)
    {
        //Store the asteroidManager
        this.asteroidManager = asteroidManager;

        //Load the asteroid's image and the explosion image
        image = imageLoader.getImage(imageName);
        explosionImage = imageLoader.getImage(explosionImageName);

        //Calculate the width and height of the asteroid
        width = image.getWidth();
        height = image.getHeight();

        //Set the initial position and steps of the asteroid
        isActive = true;
        reset();
    }

    /**
     * Update the asteroid. Checks if the asteroid has gone offscreen and moves the asteroid by its
     * step amounts.
     * @param index The asteroids index within the AsteroidManager's asteroids array.
     */
    public void update(int index)
    {
        //Only update the asteroid if it is active
        if (isActive)
        {
            //Check if the asteroid has gone off the screen
            if (hasGoneOffScreen())
            {
                //Reset the position and steps of the asteroid
                reset();
            }

            //Check if the asteroid has collided with another asteroid
            if (hasHitAsteroid(index))
            {
                xStep = -xStep; //Invert the direction
                xPos = xPos + xStep; //Give the asteroid an extra push to separate the asteroids
            }

            //Move the asteroid
            xPos = xPos + xStep;
            yPos = yPos + yStep;
        }
        else //Make the asteroid active, this will update it on the next tick
        {
            isActive = true;
            reset();
        }


    }

    /**
     * Determine whether the asteroid has gone outside the bounds of the screen
     * @return True or false (true if the asteroid has gone offscreen, false otherwise).
     */
    private boolean hasGoneOffScreen()
    {
        //Check if the asteroid has gone off the left, right, or bottom of the screen
        if (xPos + width <= 0 || xPos >= AsteroidRunPanel.WIDTH || yPos >= AsteroidRunPanel.HEIGHT)
        {
            return true;
        }

        //The asteroid is still on the screen
        return false;
    }

    /**
     * Determine whether this asteroid has collided with any other asteroids.
     * @param index The asteroids index within the AsteroidManager's asteroids array.
     * @return True or false (true if the asteroid has collided, false otherwise).
     */
    private boolean hasHitAsteroid(int index)
    {
        //Check for an collisions
        return asteroidManager.checkAsteroidCollisions(new Rectangle(xPos, yPos, width, height), index);
    }

    /**
     * Reset the position and step values of the asteroid.
     */
    public void reset()
    {
        //Create a Random number generator
        Random rng = new Random();

        //Calculate a new position
        xPos = rng.nextInt(AsteroidRunPanel.WIDTH - width);
        yPos = -rng.nextInt(height);

        //Calculate new step values
        if (Math.random() >= 0.5)
        {
            xStep = -1 * rng.nextInt(5);
        }
        else //Move the asteroid right
        {
            xStep = rng.nextInt(5);
        }

        //Add a value so that this cannot be zero
        yStep = 5 + rng.nextInt(10);
    }

    /**
     * Get a rectangle object representing this asteroids collision box using its position and dimensions.
     * @return A rectangle roughly representing this asteroid.
     */
    public Rectangle getRectangle()
    {
        //Give the player some wiggle room
        return new Rectangle(xPos, yPos, width, height);
    }

    /**
     * Draw the asteroid using its given image. If the image is null, the draw a placeholder
     * black circle in its place.
     * @param dbGraphics The Graphics object used to draw the asteroid.
     */
    public void draw(Graphics dbGraphics)
    {
        //Draw the asteroid if the image is not null
        if (image != null)
        {
            //Draw the asteroid if is active
            if (isActive)
            {
                dbGraphics.drawImage(image, xPos, yPos, null);
            }
            else //Draw the explosion image
            {
                dbGraphics.drawImage(explosionImage, xPos, yPos, null);
            }
        }
        else //Draw a GREEN circle
        {
            dbGraphics.setColor(Color.GREEN);
            dbGraphics.fillOval(xPos, yPos, width, height);
        }
    }

    /**
     * Deactivate the asteroid.
     */
    public void hitShip()
    {
        isActive = false;
    }
}
