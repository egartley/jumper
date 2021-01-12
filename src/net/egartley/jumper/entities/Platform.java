package net.egartley.jumper.entities;

import net.egartley.jumper.Game;
import net.egartley.jumper.core.abstracts.StaticEntity;
import net.egartley.jumper.core.logic.interaction.BoundaryPadding;
import net.egartley.jumper.core.logic.interaction.EntityBoundary;
import net.egartley.jumper.gamestates.InGameState;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Platform extends StaticEntity {

    public static final int WIDTH = 72;
    public static final int HEIGHT = 12;
    public final Color color = Color.green.darker();

    public Platform(int x, int y) {
        super("Platform");
        setBoundaries();
        setPosition(x, y);
    }

    @Override
    public void render(Graphics graphics) {
        graphics.setColor(color);
        graphics.fillRect(x(), y(), WIDTH, HEIGHT);
        if (Game.debug) {
            defaultBoundary.draw(graphics);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (y() > Game.WINDOW_HEIGHT) {
            InGameState.board.destroy(this);
        }
    }

    @Override
    protected void setBoundaries() {
        defaultBoundary = new EntityBoundary(this, WIDTH, HEIGHT, new BoundaryPadding(0));
        boundaries.add(defaultBoundary);
    }

    @Override
    public void setCollisions() {

    }

}
