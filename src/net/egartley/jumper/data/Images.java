package net.egartley.jumper.data;

import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import javax.imageio.ImageIO;
import java.io.File;

public class Images {

    public final static byte PLAYER = 0;

    public static String path = "resources/images/";
    public static String entityPath = path + "entities/";

    /**
     * Returns the specified image
     */
    public static Image get(byte image) {
        switch (image) {
            case PLAYER:
                return get(entityPath + "player-default.png");
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
