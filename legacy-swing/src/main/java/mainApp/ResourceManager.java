package mainApp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Class: ResourceManager
 * @author Team 303
 * <br>Purpose: Manages loading of images and other resources from both classpath and file system
 * <br>Handles both development (file system) and deployment (CheerpJ) scenarios
 */
public class ResourceManager {
    
    /**
     * Load an image from resources or file system
     * @param fileName the image file name (without extension)
     * @param extension the file extension (e.g., ".PNG", ".png")
     * @return BufferedImage or null if not found
     */
    public static BufferedImage loadImage(String fileName, String extension) {
        BufferedImage img = null;
        
        // Try to load from classpath first (for JAR/resources/images)
        try {
            String resourcePath = "images/" + fileName + extension;
            InputStream inputStream = ResourceManager.class.getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream != null) {
                img = ImageIO.read(inputStream);
                inputStream.close();
                return img;
            }
        } catch (IOException e) {
            // Fall through to file system approach
        }
        
        // Fallback to file system (for development and CheerpJ)
        try {
            String filePath = "images/" + fileName + extension;
            File file = new File(filePath);
            if (file.exists()) {
                img = ImageIO.read(file);
                return img;
            }
        } catch (IOException e) {
            // Image not found
        }
        
        return null;
    }
    
    /**
     * Load an image with default PNG extension
     * @param fileName the image file name (without extension)
     * @return BufferedImage or null if not found
     */
    public static BufferedImage loadImage(String fileName) {
        // Try PNG first, then png
        BufferedImage img = loadImage(fileName, ".PNG");
        if (img == null) {
            img = loadImage(fileName, ".png");
        }
        return img;
    }
} 