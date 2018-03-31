import java.awt.*;
import java.util.Random;

/**
 * @author Logan Karstetter
 * Date: 02/11/2018
 */
public class AsteroidManager
{
    /** The maximum number of asteroids */
    private static final int MAX_ASTEROIDS = 15;

    /** An array of asteroids */
    private Asteroid[] asteroids;
    /** The current number of asteroids in the game */
    private int numAsteroids;

    /** A random number generator used to determine the type of asteroid to create */
    private Random rng;

    /** A reference to the AsteroidRunPanel that runs the game */
    AsteroidRunPanel asteroidRunPanel;

    /**
     * Create an AsteroidManager to manage the updating, drawing, and interactions of asteroids.
     * @param numInitialAsteroids The desired initial number of asteroids present in the game.
     * @param imageLoader The ImageLoader used to load images for this game.
     * @param asteroidRunPanel The AsteroidRunPanel that runs the game.
     */
    public AsteroidManager(int numInitialAsteroids, ImageLoader imageLoader, AsteroidRunPanel asteroidRunPanel)
    {
        //Create the array of asteroids
        asteroids = new Asteroid[MAX_ASTEROIDS];
        numAsteroids = numInitialAsteroids;

        //Create the random number generator and an array of asteroid image names
        rng = new Random();
        String[] imagesNames = {"Asteroid", "Asteroid 2", "Asteroid 3"};

        //Populate the asteroids array with new asteroids
        for (int i = 0; i < numAsteroids; i++)
        {
            //Generate a random number to determine the type of asteroid, then create the asteroid
            asteroids[i] = new Asteroid(imagesNames[rng.nextInt(3)], "Explosion", imageLoader, this);
        }

        //Store the reference to the asteroidRunPanel
        this.asteroidRunPanel = asteroidRunPanel;
    }

    /**
     * Update all of the asteroids.
     */
    public void update()
    {
        //Update the asteroids
        for (int i = 0; i < numAsteroids; i++)
        {
            asteroids[i].update(i);
        }
    }

    /**
     * Determine whether the given rectangle intersects the collision rectangles of any of the asteroids.
     * @param rect The rectangle to check for collisions/intersections.
     * @param isSpaceship Determines whether the rectangle being checked is the spaceship's bounding box.
     * @return True or false (true if the rectangles intersect, false otherwise).
     */
    public boolean checkCollisions(Rectangle rect, boolean isSpaceship)
    {
        //Determine if this rectangle has collided with any asteroids
        for (int i = 0; i < numAsteroids; i++)
        {
            //If the rectangles intersect and are not the exact same
            if (asteroids[i].getRectangle().intersects(rect))
            {
                //If the spaceship hit an asteroid, deactivate the asteroid and remove a life
                if (isSpaceship)
                {
                    asteroids[i].hitShip();
                    asteroidRunPanel.lifeLost();
                }
                return true;
            }
        }

        //There were no collisions
        return false;
    }

    /**
     * Determine whether the given asteroid's rectangle intersects the collision rectangles of any of
     * the other asteroids. The index of the asteroid calling this method is passed so that the asteroid
     * does not check collisions with itself.
     * @param rect The rectangle to check for collisions/intersections.
     * @param index The index of the asteroid being compared.
     * @return True or false (true if the rectangles intersect, false otherwise).
     */
    public boolean checkAsteroidCollisions(Rectangle rect, int index)
    {
        //Determine if this rectangle has collided with any asteroids
        for (int i = 0; i < numAsteroids; i++)
        {
            //If the rectangles intersect and are not the exact same
            if (i != index && asteroids[i].getRectangle().intersects(rect))
            {
                return true;
            }
        }

        //There were no collisions
        return false;
    }

    /**
     * Draw all of the asteroids.
     * @param dbGraphics The Graphics object used to draw the asteroids.
     */
    public void draw(Graphics dbGraphics)
    {
        //Update the asteroids
        for (int i = 0; i < numAsteroids; i++)
        {
            asteroids[i].draw(dbGraphics);
        }
    }
}
