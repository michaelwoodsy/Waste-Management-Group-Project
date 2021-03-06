package org.seng302.project.service_layer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Singleton class for image processing
 */
@Component
public class ImageUtil {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class.getName());
    private static final int THUMBNAIL_WIDTH = 150; //Pixels


    /**
     * Initial attempt to try creating an image thumbnail.
     * Based on code from:
     * https://www.techcoil.com/blog/how-to-create-a-thumbnail-of-an-image-in-java-without-using-external-libraries/
     *  @param filepath The filepath of the image we are
     *                  trying to create a thumbnail for
     * @return the filepath where the thumbnail was saved
     */
    public String createThumbnail(String filepath) throws IOException {
        logger.info("Creating thumbnail for file: {}", filepath);
        var originalBufferedImage = readImageFromFile(filepath);
        var resizedBufferImage = scaleImage(originalBufferedImage);
        var thumbnailBufferedImage = cropImage(resizedBufferImage);

        //Splits filepath into "directory/image" bit and "jpg" bit
        String[] filepathBits = filepath.split("\\.(?=[a-z])");

        //Add "_thumbnail" to the end of image name
        filepathBits[0] = filepathBits[0] + "_thumbnail";
        var thumbnailPath = String.join(".", filepathBits);
        saveImage(thumbnailBufferedImage, thumbnailPath);

        return thumbnailPath.substring(thumbnailPath.indexOf("/media/"));
    }


    /**
     *
     * @param filepath The filepath of the image we are
     *                 trying to read
     * @return A BufferedImage object of the image
     * @throws IOException an exception that is thrown by the ImageIO reader
     */
    public BufferedImage readImageFromFile(String filepath) throws IOException {
        BufferedImage originalBufferedImage;
        try {
            originalBufferedImage = ImageIO.read(new File(filepath));
        }
        catch(IOException ioe) {
            logger.error("IO exception occurred while trying to read image.");
            throw ioe;
        }
        return originalBufferedImage;
    }

    /**
     * Returns a BufferedImage object obtained from a multipart file.
     *
     * @param file The multipart file to get image from.
     * @return BufferedImage of the multipart file.
     * @throws IOException an exception thrown for input/output error.
     */
    public BufferedImage readImageFromMultipartFile(MultipartFile file) throws IOException {
        BufferedImage image;
        try {
            var imageInputStream = file.getInputStream();
            image = ImageIO.read(imageInputStream);
            imageInputStream.close();
        } catch (IOException exception) {
            logger.error("IO exception occurred while trying to read image.");
            throw exception;
        }
        return image;
    }

    /**
     * Scales the original image down to 10% larger than the thumbnail width of 150px
     * @param originalBufferedImage the original image, before it has been altered
     * @return a scaled down image, ready to be cropped into a thumbnail
     */
    public BufferedImage scaleImage(BufferedImage originalBufferedImage) {

        int widthToScale;
        int heightToScale;
        if (originalBufferedImage.getWidth() > originalBufferedImage.getHeight()) {
            //Landscape
            heightToScale = (int)(1.1 * THUMBNAIL_WIDTH);
            widthToScale = (int)((heightToScale * 1.0) / originalBufferedImage.getHeight()
                    * originalBufferedImage.getWidth());

        } else {
            //Portrait
            widthToScale = (int)(1.1 * THUMBNAIL_WIDTH);
            heightToScale = (int)((widthToScale * 1.0) / originalBufferedImage.getWidth()
                    * originalBufferedImage.getHeight());
        }

        //Creates a new BufferedImage object to hold the scaled down image
        var resizedImage = new BufferedImage(widthToScale,
                heightToScale, originalBufferedImage.getType());
        Graphics2D g = resizedImage.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Draw a scaled version of the original image into the BufferedImage object
        g.drawImage(originalBufferedImage, 0, 0, widthToScale, heightToScale, null);
        g.dispose();

        return resizedImage;
    }


    /**
     * Crops the (scaled down) image into a thumbnail
     * @return the thumbnail image
     */
    public BufferedImage cropImage(BufferedImage resizedImage) {
        int x = (resizedImage.getWidth() - THUMBNAIL_WIDTH) / 2;
        int y = (resizedImage.getHeight() - THUMBNAIL_WIDTH) / 2;

        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Width of new thumbnail is bigger than original image");
        }

        return resizedImage.getSubimage(x, y, THUMBNAIL_WIDTH, THUMBNAIL_WIDTH);
    }


    /**
     *
     * @param image The image to be saved
     * @param filepath The file path for the image to be saved to
     * @throws IOException an exception that is thrown by the ImageIO writer
     */
    public void saveImage(BufferedImage image, String filepath) throws IOException {
        String format = filepath.split("\\.(?=([a-zA-Z]*)$)")[1].toUpperCase();
        try {
            ImageIO.write(image, format, new File(filepath));
        }
        catch (IOException ioe) {
            logger.error("Error writing image to file");
            throw ioe;
        }
    }

    /**
     * Deletes the image from the disk
     * @param filepath The file path where the image is to be deleted
     * @throws IOException an exception that is thrown by the ImageIO writer
     */
    public void deleteImage(String filepath) throws IOException {
        Path path = Paths.get(filepath);
        try {
            Files.delete(path);
        } catch (IOException ioe) {
            logger.error("Error deleting file");
            throw ioe;
        }
    }

}
