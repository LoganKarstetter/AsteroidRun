import java.awt.*;
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

    /**
     * Create an AsteroidManager to manage the updating, drawing, and interactions of asteroids.
     * @param numInitialAsteroids The desired initial number of asteroids present in the game.
     * @param imageLoader The ImageLoader used to load images for this game.
     */
    public AsteroidManager(int numInitialAsteroids, ImageLoader imageLoader)
    {
        //Create the array of asteroids
        asteroids = new Asteroid[MAX_ASTEROIDS];
        numAsteroids = numInitialAsteroids;

        //Populate the asteroids array with new asteroids
        for (int i = 0; i < numAsteroids; i++)
        {
            asteroids[i] = new Asteroid("Apple", imageLoader, this);
        }
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
     * @return True or false (true if the rectangles intersect, false otherwise).
     */
    public boolean checkCollisions(Rectangle rect)
    {
        //Determine if this rectangle has collided with any asteroids
        for (int i = 0; i < numAsteroids; i++)
        {
            //If the rectangles intersect and are not the exact same
            if (asteroids[i].getRectangle().intersects(rect))
            {
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
