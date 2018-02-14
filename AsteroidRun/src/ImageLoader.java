import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Logan Karstetter
 * Date: 02/11/2018
 */
public class ImageLoader
{
    /** The location of the file to load images from */
    private String directory = "Images/";

    /**
     * The HashMap used to store loaded images. The key is the image name as
     * it appeared in the file, and the object stored is an ArrayList of BufferedImages.
     */
    private HashMap<String, ArrayList<BufferedImage>> imagesMap;

    /** The graphics configuration describing the characteristics of the user's display */
    private GraphicsConfiguration graphicsConfiguration;

    /**
     * * Create an ImageLoader for loading images from a file located in the local Images/ directory.
     */
    public ImageLoader()
    {
        //Create the imagesMap and get the graphicsConfiguration
        imagesMap = new HashMap<>();
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsConfiguration = graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
    }
    /**
     * Create an ImageLoader for loading images from a file located in some directory.
     * @param directory The local directory to load files from.
     */
    public ImageLoader(String directory)
    {
        //Call the constructor
        this();

        //Set the directory
        this.directory = directory; //Set to Images/ by default
    }

    /**
     * Load a single or sequence of images located within the given file under the given directory
     * (Images/ by default). For a single image, the format of the file is as follows "image".
     * The key/name of the image in the imagesMap will be set to the "image" minus the .ext
     * (if it even has an extension). For a sequence of images, the format of the file is as follows
     * "[sequence name: image1, image2]". In this case, the key/name of the images will be set to
     * "sequence name". Lines that do not adhere to this format will be skipped. Lines
     * beginning with // will be regarded as comments and blank lines will also be skipped.
     * @param fileName The name of the file to load images from.
     */
    public void loadImagesFromFile(String fileName)
    {
        //Inform the user of the file reading
        System.out.println("Reading file: " + directory + fileName);
        try
        {
            //Created an InputStream and BufferedReader to read the file
            InputStream inputStream = this.getClass().getResourceAsStream(directory + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            //Loop until the end of the file is reached
            String line;
            while ((line = br.readLine()) != null)
            {
                //Determine what action to take based off the line read
                if (line.startsWith("//") || (line.length() == 0)) //This line is a comment or blank line
                {
                    continue;
                }
                else if (line.startsWith("[")) //This line is a sequence of images
                {
                    loadImageSequence(line);
                }
                else //The line is a single image
                {
                    loadSingleImage(line);
                }
            }

            //Close the BufferedReader
            br.close();

            //Inform the user the ImageLoader is done reading
            System.out.println("Finished reading file: " + directory + fileName);
        }
        catch (IOException e) {
            System.out.println("Error reading file: " + directory + fileName + " " + e);
        }
    }

    /**
     * Load the single specified image from the line, and store it in the imagesMap.
     * @param line The line containing the file to be loaded.
     * @return True or false (success or fail)
     */
    private boolean loadSingleImage(String line)
    {
        //Get the image name (remove an .extension, if any)
        String imageName = line; //Set the imageName to the line by default
        if (line.contains("."))
        {
            imageName = line.substring(0, line.indexOf('.'));
        }

        //Check that the imagesMap does not already contain this file or one using this name
        if (imagesMap.containsKey(imageName))
        {
            System.out.println("ImagesMap already contains: " + imageName);
            return false;
        }

        //Load the image
        BufferedImage image = loadImage(line);

        //Store the new image in the imagesMap if it is not null
        if (image != null)
        {
            //Add the new image to an new array list
            ArrayList<BufferedImage> imageList = new ArrayList<>();
            imageList.add(image);
            imagesMap.put(imageName, imageList);
            System.out.println("Stored " + imageName + " [" + line + "]");
            return true;
        }

        //Something went wrong
        return false;
    }

    /**
     * Load a sequence of images from the line and store it in the imagesMap.
     * @param line The line containing the sequence of images.
     * @return True or false (success or fail)
     */
    private boolean loadImageSequence(String line)
    {
        //Get the image name
        String imageName = line.substring(1, line.indexOf(':')); //Skip the first open bracket

        //Check that the imagesMap does not already contain this file or one using this name
        if (imagesMap.containsKey(imageName))
        {
            System.out.println("ImagesMap already contains: " + imageName);
            return false;
        }

        //Split the line into an array of strings using the commas
        line = line.substring(line.indexOf(":") + 1, line.indexOf("]"));
        line = line.trim();
        String[] lines = line.split(",");

        //Load the images
        ArrayList<BufferedImage> imageList = new ArrayList<>();
        BufferedImage loadedImage;
        for (int i = 0; i < lines.length; i++)
        {
            //Load the image and store it in the imageList
            loadedImage = loadImage(lines[i].trim()); //Trim whitespace off the lines

            //Check that the image is not null
            if (loadedImage != null)
            {
                imageList.add(loadedImage);
            }
            else //Do not load any more images if a single image was null
            {
                return false;
            }
        }

        //Put the image sequence in the imageMap
        imagesMap.put(imageName, imageList);
        System.out.println("Stored " + imageName + " [" + line + "]");
        return true;
    }

    /**
     * Loads the specified image from the line and ensures the image becomes a managed image
     * using the computer's graphics configuration.
     * @param line The line containing the file to be loaded.
     * @return The new managed BufferedImage, or null on failure.
     */
    private BufferedImage loadImage(String line)
    {
        try
        {
            //Read in the image and store it in a new BufferedImage
            BufferedImage readImage = ImageIO.read(getClass().getResource(directory + line));

            //Create a new copy of the image to ensure it becomes a managed image
            int transparency = readImage.getColorModel().getTransparency();
            BufferedImage copy = graphicsConfiguration.createCompatibleImage(readImage.getWidth(),
                    readImage.getHeight(), transparency);
            //Create a graphics context to draw the image onto
            Graphics2D g2d = copy.createGraphics();
            g2d.drawImage(readImage, 0, 0, null);
            g2d.dispose();

            //Return the new image
            return copy;
        }
        catch (IOException e)
        {
            System.out.println("Error loading image [" + line + "]");
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("Unable to find image [" + line + "]");
        }

        //Something went wrong
        return null;
    }


    /**
     * Get an image from the imagesMap using it's key/name.
     * @param key The key (name) of the image.
     * @return The BufferedImage associated with the passed key or null if it does not exist.
     */
    public BufferedImage getImage(String key)
    {
        //Get the image and check if its null before returning
        try
        {
            BufferedImage image = imagesMap.get(key).get(0);
            if (image == null)
            {
                System.out.println("No image found under '" + key + "'");
            }
            return image;
        }
        catch (IndexOutOfBoundsException | NullPointerException e)
        {
            System.out.println("No image found under '" + key + "'");
        }

        //Return null if no image was found
        return null;
    }

    /**
     * Get an image belonging to a sequence of images from the imagesMap using it's key/name
     * and the index of its location in the image sequence.
     * @param key The key (name) of the image.
     * @param index The index of the image in the sequence.
     * @return The BufferedImage associated with the passed key or null if it does not exist.
     */
    public BufferedImage getImage(String key, int index)
    {
        //Get the image and check if its null before returning
        try
        {
            BufferedImage image = imagesMap.get(key).get(index);
            if (image == null)
            {
                System.out.println("No image found under '" + key + "' with index '" + index + "'");
            }
            return image;
        }
        catch (IndexOutOfBoundsException | NullPointerException e)
        {
            System.out.println("No image found under '" + key + "' with index '" + index + "'");
        }

        //Return null if no image was found
        return null;
    }


    /**
     * Determine whether the imagesMap contains the given key.
     * @param imageName The key value to be checked.
     * @return True or false (if the key is mapped).
     */
    public boolean imageExists(String imageName)
    {
        //See if the imageName is a key used in the imagesMap
        return imagesMap.containsKey(imageName);
    }

    /**
     * Get the number of images stored for a particular image. If this amount is greater than one,
     * then this image is a sequence of images. If the amount is one, then this is a single image.
     * @param imageName The name of the image for which the number of images will be checked.
     * @return The number of images.
     */
    public int getNumberImages(String imageName)
    {
        return imagesMap.get(imageName).size();
    }
}
