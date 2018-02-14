import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Logan Karstetter
 * Date: 02/11/2018
 */
public class AsteroidRunPanel extends JPanel implements Runnable
{
    /** The width of this AsteroidRunPanel */
    public static final int WIDTH = 700;
    /** The height of this AsteroidRunPanel */
    public static final int HEIGHT = 800;

    /** The thread that runs the animation loop */
    private Thread animator;
    /** Determines whether the animator thread is running */
    private volatile boolean isRunning;
    /** Determines whether the game is paused; */
    private volatile boolean isPaused;
    /** Determines whether the game is over */
    private volatile boolean gameOver;

    /** The desired FPS/UPS for the animation loop */
    private int FPS;
    /** The amount of time allocated for each cycle of the game loop (in nanos) */
    private long loopPeriod;

    /** The max number of times the animator thread can loop without sleeping
     * before it is forced to sleep/yield and let other threads execute */
    private final int NUM_DELAYS_FOR_YIELD = 16;
    /** The max number of times the loop can skip rendering/painting to the screen
     * before it is forced to render */
    private final int MAX_FRAMES_SKIPPED = 5;

    /** The Graphics used to double buffer/render the screen */
    private Graphics dbGraphics;
    /** The image that is created/rendered offscreen and later painted to the screen */
    private Image dbImage;

    /** The ImageLoader used to load the game images */
    private ImageLoader imageLoader;
    /** The KeyManager used to process key events */
    private KeyManager keyManager;

    /** The Spaceship controlled by the player in this game */
    private Spaceship spaceship;
    /** The AsteroidManager that manages the game's asteroids */
    private AsteroidManager asteroidManager;
    /** The Ribbon used to display the moving background */
    private Ribbon backgroundRibbon;


    /**
     * Create a new AsteroidRunPanel for playing AsteroidRun.
     * The panel is responsible for running the game loop which updates,
     * renders, and draws the game at the desired FPS/UPS.
     */
    public AsteroidRunPanel(int desiredFPS)
    {
        //Store the FPS and calculate the loop period
        FPS = desiredFPS;
        loopPeriod = 1000000000/FPS; //secs -> nanos

        //Set up the panel
        setBackground(Color.BLACK);
        setDoubleBuffered(false); //Active rendering is used anyways
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true); //Request focus to the panel
        requestFocus();

        //Create the ImageLoader and KeyManager
        imageLoader = new ImageLoader();
        imageLoader.loadImagesFromFile("ImagesConfig.txt");
        keyManager = new KeyManager(this);
        this.addKeyListener(keyManager);

        //Create the asteroidManager and spaceship
        asteroidManager = new AsteroidManager(5, imageLoader);
        spaceship = new Spaceship("Space Freighter", loopPeriod, asteroidManager,
                imageLoader, keyManager, this );

        //Create the backgroundRibbon
        backgroundRibbon = new Ribbon(imageLoader.getImage("Space Background"), 1);
    }

    /**
     * Notifies this component that it now has a parent component.
     * This method informs the AsteroidRunPanel that it has been added to a
     * parent container such as a JFrame. Once notified it starts the
     * game. This prevents the game starting before the user can see it.
     */
    public void addNotify()
    {
        super.addNotify();
        startGame();
    }

    /**
     * Initialize the animator thread and start the game.
     */
    private void startGame()
    {
        //If the game is not started
        if (animator == null || !isRunning)
        {
            //Initialize the animator thread
            animator = new Thread(this);
            animator.start();
        }
    }

    /**
     * Pause the game.
     */
    public void pauseGame()
    {
        isPaused = true;
    }

    /**
     * Resume the game.
     */
    public void resumeGame()
    {
        isPaused = false;
    }

    /**
     * Stop the game, set isRunning to false.
     */
    public void stopGame()
    {
        isRunning = false;
    }

    /**
     * Ends the game, sets gameOver to true.
     */
    public void gameOver()
    {
        gameOver = true;
    }

    /**
     * Repeatably update, render, paint, and sleep such that the game loop takes close to the amount of
     * time allotted by the desired FPS (loopPeriod).
     */
    public void run()
    {
        //The time before the current loop/cycle begins
        long beforeTime;
        //The current time after the gameUpdate, gameRender, and paintScreen methods execute
        long afterTime;
        //The time taken for the gameUpdate, gameRender, and paintScreen methods to execute
        long timeDifference;

        //The amount of time left in the current loopPeriod that the thread can sleep for
        //(loopPeriod - timeDifference) - overSleepTime
        long sleepTime;
        //The amount of time the thread overslept
        long overSleepTime = 0L;

        //The number of times the thread looped/cycled without sleeping (sleepTime was <= 0)
        int numDelays = 0;
        //The total amount of excess time the methods took to execute (overTime = actual - loopPeriod)
        long overTime = 0L;

        //Get the current time before the first loop
        beforeTime = System.nanoTime();

        //Game loop
        isRunning = true;
        while (isRunning)
        {
            //Update, render, and paint the screen
            gameUpdate();
            gameRender();
            paintScreen(); //active rendering

            //Get the current time after the methods executed
            afterTime = System.nanoTime();
            timeDifference = afterTime - beforeTime; //The time it took to update, render, and paint

            //Calculate how much time is left for sleeping in this loopPeriod (1000000000/FPS)
            sleepTime = (loopPeriod - timeDifference) - overSleepTime;

            //Sleep
            if (sleepTime > 0)
            {
                try
                {
                    Thread.sleep(sleepTime/1000000); //nanos -> ms
                }
                catch (InterruptedException e)
                {
                    //Do nothing
                }
                //Check if the animator overslept, overSleepTime will be deducted from the next sleepTime
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            }
            else //If we didn't get a chance to sleep this loopPeriod (sleepTime <= 0)
            {
                overTime = overTime - sleepTime; //Store the excess time (- because sleepTime is <= 0)
                overSleepTime = 0L; //Reset the oversleep time

                //See if the animator thread needs to yield
                if (++numDelays >= NUM_DELAYS_FOR_YIELD) //(it hasn't slept for NUM_DELAYS_FOR_YIELD cycles)
                {
                    Thread.yield();
                    numDelays = 0;
                }
            }

            //Get the beforeTime for the next cycle
            beforeTime = System.nanoTime();

            //If rendering and animation are taking too long, update the game without rendering it
            //This will get the UPS closer to the desired FPS
            int skips = 0;
            while ((overTime > loopPeriod) && (skips < MAX_FRAMES_SKIPPED))
            {
                //Update x times without rendering, won't be noticeable if MAX_SKIPPED_FRAMES is small
                overTime = overTime - loopPeriod;
                gameUpdate();
                skips++;
            }
        }
        //Running is false, so exit
        System.exit(0);
    }

    /**
     * Update the elements of the game as long as the game is not over or paused.
     */
    private void gameUpdate()
    {
        //If the game is not over or paused, update
        if (!gameOver && !isPaused)
        {
            //Update the game elements
            backgroundRibbon.update();
            keyManager.update();
            asteroidManager.update();
            spaceship.update();
        }
    }

    /**
     * Render the game using double buffering. If it does not already exist, this
     * method creates an Image the size of the AsteroidRunPanel and draws to it offscreen.
     * Drawing offscreen prevents flickering and then allows the paintScreen() method
     * to draw the entire screen as an image rather than in layers.
     */
    private void gameRender()
    {
        //If the dbImage (double buffered image) has not been created
        if (dbImage == null)
        {
            //Make an image the size of the panel
            dbImage = createImage(WIDTH, HEIGHT);
            if (dbImage == null)
            {
                return;
            }
            else
            {
                //Get the graphics context to draw to the dbImage
                dbGraphics = dbImage.getGraphics();
            }
        }

        //Draw the background image
        backgroundRibbon.draw(dbGraphics);

        //Render the game elements
        asteroidManager.draw(dbGraphics);
        spaceship.draw(dbGraphics);
    }

    /**
     * Actively render/draw the dbImage (created in gameRender()) onto the screen/AsteroidRunPanel.
     */
    private void paintScreen()
    {
        //Declare a graphics object
        Graphics g;

        try
        {
            //Get the graphics context from the AsteroidRunPanel so we can to draw to the panel
            g = this.getGraphics();

            if ((g != null) && (dbImage != null))
            {
                g.drawImage(dbImage, 0, 0, null);
            }
            Toolkit.getDefaultToolkit().sync(); //Sync the display (only applies to odd systems)
            g.dispose();

        }
        catch (NullPointerException e)
        {
            System.out.println("Graphics context error: " + e);
        }
    }


}
