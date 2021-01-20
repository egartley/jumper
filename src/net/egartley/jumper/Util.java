package net.egartley.jumper;

import net.egartley.jumper.core.abstracts.Entity;
import net.egartley.jumper.core.graphics.Sprite;
import net.egartley.jumper.core.logic.collision.Collisions;
import net.egartley.jumper.core.logic.collision.EntityEntityCollision;
import net.egartley.jumper.core.logic.events.EntityEntityCollisionEvent;
import net.egartley.jumper.core.logic.interaction.EntityBoundary;
import net.egartley.jumper.data.Images;
import org.newdawn.slick.*;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Util {

    /**
     * Builds an array of images to use for the frames of an animation
     *
     * @param sprite The sprite to get frames from
     * @return An array of images, each representing a single frame
     */
    public static Image[] getAnimationFrames(Sprite sprite) {
        Image[] frames = new Image[sprite.frames.size()];
        Object[] a = sprite.frames.toArray();
        for (int i = 0; i < sprite.frames.size(); i++) {
            frames[i] = (Image) a[i];
        }
        return frames;
    }

    /**
     * Returns a random integer between the minimum and maximum
     *
     * @param min The smallest value (inclusive)
     * @param max The largest value (exclusive)
     * @return An integer between the minimum and maximum
     */
    public static int randomInt(int min, int max) {
        if (max < min) {
            // if max/min were mixed up, switch them
            int actualmin = max;
            max = min;
            min = actualmin;
        }
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * Returns a random integer between the minimum and maximum
     *
     * @param min       The smallest value (inclusive)
     * @param max       The largest value
     * @param inclusive Whether or not the maximum value could be returned
     * @return An integer between the minimum and maximum
     */
    public static int randomInt(int min, int max, boolean inclusive) {
        if (inclusive) {
            return randomInt(min, max + 1);
        } else {
            return randomInt(min, max);
        }
    }

    /**
     * Returns true the given percentage of the time
     *
     * @param percent The percent chance that true will be returned
     * @return True the given percentage of the time, false otherwise
     */
    public static boolean percentChance(double percent) {
        // assert percent <= 1.0D;
        return randomInt(100, 1, true) <= percent * 100;
    }

    /**
     * Calls {@link #percentChance(double)} with an equal chance of either true or false
     *
     * @return True 50% of the time, false the other 50%
     */
    public static boolean fiftyFifty() {
        return percentChance(0.5D);
    }

    /**
     * Returns whether or not the specified point is "within bounds," or overlapping, the specified area
     *
     * @param pointX  The x-coordinate of the point
     * @param pointY  The y-coordinate of the point
     * @param boundsX The x-coordinate of the bounds
     * @param boundsY The y-coordinate of the bounds
     * @param width   The width of the bounds
     * @param height  The height of the bounds
     * @return Whether or not the point is located in the bounds
     */
    public static boolean isWithinBounds(int pointX, int pointY, int boundsX, int boundsY, int width, int height) {
        return pointX >= boundsX && pointX <= boundsX + width && pointY >= boundsY && pointY <= boundsY + height;
    }

}
