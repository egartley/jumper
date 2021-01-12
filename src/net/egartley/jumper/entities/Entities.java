package net.egartley.jumper.entities;

/**
 * Commonly referenced entities, such as the player
 */
public class Entities {

    public static Player PLAYER;

    public static void initialize() {
        PLAYER = new Player();
    }

}
