package net.egartley.jumper.ingame;

import net.egartley.jumper.Game;
import net.egartley.jumper.Util;
import net.egartley.jumper.core.abstracts.Renderable;
import net.egartley.jumper.core.interfaces.Tickable;
import net.egartley.jumper.core.logic.collision.Collisions;
import net.egartley.jumper.entities.Entities;
import net.egartley.jumper.entities.Platform;
import net.egartley.jumper.gamestates.InGameState;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;

public class Board extends Renderable implements Tickable {

    public int score;
    public int highScore;

    private final int maxPlatforms = 17;
    private final int spacing = 48;
    private final int scoreIncrement = 10;

    private final Color backgroundColor = new Color(230, 230, 230);
    private ArrayList<Platform> toRemove = new ArrayList<>();
    private ArrayList<Platform> platforms = new ArrayList<>();

    public void populate() {
        platforms.clear();
        for (int line = -1; line < maxPlatforms - 1; line++) {
            add(new Platform(randomX(), line * spacing));
        }
    }

    public void add(Platform platform) {
        platforms.add(platform);
        Entities.PLAYER.addPlatformCollision(platform);
    }

    public void destroy(Platform platform) {
        toRemove.add(platform);
        Collisions.removeWith(platform);
        score += scoreIncrement;
    }

    private int randomX() {
        return Util.randomInt(24, Game.WINDOW_WIDTH - 24 - Platform.WIDTH);
    }

    @Override
    public void render(Graphics graphics) {
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        platforms.forEach(p -> p.render(graphics));
        if (!InGameState.isDead) {
            Entities.PLAYER.render(graphics);
        }
    }

    @Override
    public void tick() {
        Entities.PLAYER.tick();
        for (Platform p : platforms) {
            p.y(p.y() + 1);
        }
        platforms.forEach(Platform::tick);
        if (!toRemove.isEmpty()) {
            toRemove.forEach(p -> platforms.remove(p));
            toRemove.clear();
        }
        while (platforms.size() < maxPlatforms) {
            add(new Platform(randomX(), -1 * Platform.HEIGHT));
        }
    }

}
