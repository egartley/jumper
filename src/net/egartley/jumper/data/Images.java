package net.egartley.jumper.data;

import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import javax.imageio.ImageIO;
import java.io.File;

public class Images {

    public static final byte LEFT_FALLING = 0;
    public static final byte RIGHT_FALLING = 1;
    public static final byte LEFT_RISING = 2;
    public static final byte RIGHT_RISING = 3;
    public static final byte SQUISH = 4;

    public static String path = "resources/images/";
    public static String entityPath = path + "entities/";

    /**
     * Returns the specified image
     */
    public static Image get(byte image) {
        switch (image) {
            case LEFT_FALLING:
                return get(entityPath + "fall-left.png");
            case RIGHT_FALLING:
                return get(entityPath + "fall-right.png");
            case LEFT_RISING:
                return get(entityPath + "rising-left.png");
            case RIGHT_RISING:
                return get(entityPath + "rising-right.png");
            case SQUISH:
                return get(entityPath + "squish.png");
            default:
                return get(path + "unknown.png");
        }
    }

    /**
     * Returns an image at the specified path
     */
    public static Image get(String path) {
        // Credit: https://stackoverflow.com/a/23613661
        try {
            Texture texture = BufferedImageUtil.getTexture("", ImageIO.read(new File(path)));
            Image image = new Image(texture.getImageWidth(), texture.getImageHeight());
            image.setTexture(texture);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
