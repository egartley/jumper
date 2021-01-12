package net.egartley.jumper.entities;

import net.egartley.jumper.Game;
import net.egartley.jumper.Util;
import net.egartley.jumper.core.abstracts.AnimatedEntity;
import net.egartley.jumper.core.graphics.SpriteSheet;
import net.egartley.jumper.core.input.Keyboard;
import net.egartley.jumper.core.logic.Calculate;
import net.egartley.jumper.core.logic.collision.Collisions;
import net.egartley.jumper.core.logic.collision.EntityEntityCollision;
import net.egartley.jumper.core.logic.events.EntityEntityCollisionEvent;
import net.egartley.jumper.core.logic.interaction.BoundaryPadding;
import net.egartley.jumper.core.logic.interaction.EntityBoundary;
import net.egartley.jumper.core.threads.DelayedEvent;
import net.egartley.jumper.data.Images;
import net.egartley.jumper.gamestates.InGameState;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Input;

public class Player extends AnimatedEntity {

    private final byte LEFT_ANIMATION = 0;
    private final byte RIGHT_ANIMATION = 1;
    private final int ANIMATION_THRESHOLD = 165;

    public int fallSpeed = 0, maxFallSpeedUp = 4, maxFallSpeedDown = 6;
    private int landingPoint, jumpHeight = 72;
    private double downAcc = 0.0625D, upAcc = 0.05D, fallControl= 0D, maxControl = Math.PI / 2D;
    public boolean isFalling = true;

    public EntityBoundary boundary;

    public Player() {
        super("Player", new SpriteSheet(Images.get(Images.PLAYER), 30, 44, 2, 4));
        speed = 4D;
    }

    public void addPlatformCollision(Platform platform) {
        Collisions.add(new EntityEntityCollision(boundary, platform.defaultBoundary) {
            @Override
            public void start(EntityEntityCollisionEvent event) {
                if (event.collidedSide == EntityEntityCollisionEvent.TOP_SIDE) {
                    onPlatformLand(platform);
                }
            }
        });
    }

    private void resetFallSpeed() {
        fallSpeed = 0;
        fallControl = 0;
    }

    public void onInGameEnter() {
        setPosition(Calculate.getCenteredX(Entities.PLAYER.sprite.width), -sprite.height);
        animation.stop();
        resetFallSpeed();
    }

    public void onInGameLeave() {

    }

    public void onPlatformLand(Platform platform) {
        if (!isFalling) {
            return;
        }
        isFalling = false;
        landingPoint = platform.y();
        resetFallSpeed();
    }

    @Override
    public void setAnimations() {
        animations.add(new Animation(Util.getAnimationFrames(sprites.get(0)), ANIMATION_THRESHOLD));
        animations.add(new Animation(Util.getAnimationFrames(sprites.get(1)), ANIMATION_THRESHOLD));
        animation = animations.get(0);
    }

    @Override
    public void setBoundaries() {
        boundary = new EntityBoundary(this, sprite, new BoundaryPadding(3, 0, 1, 0));
        boundary.name = "Base";
        defaultBoundary = boundary;
        boundaries.add(boundary);
    }

    @Override
    public void setCollisions() {

    }

    private void nextFallSpeed() {
        int s = maxFallSpeedUp;
        if (isFalling) {
            s = maxFallSpeedDown;
        }
        fallSpeed = (int) Math.round(s * Math.sin(fallControl));
        if (fallControl < maxControl) {
            if (isFalling) {
                fallControl += downAcc;
            } else {
                fallControl += upAcc;
            }
        }
    }

    @Override
    public void tick() {
        boolean left = Keyboard.isPressed(Input.KEY_A);
        boolean right = Keyboard.isPressed(Input.KEY_D);
        if (left) {
            move(DIRECTION_LEFT);
            if (animation.equals(animations.get(RIGHT_ANIMATION))) {
                animation = animations.get(LEFT_ANIMATION);
                animation.stop();
            }
        } else if (right) {
            move(DIRECTION_RIGHT);
            if (animation.equals(animations.get(LEFT_ANIMATION))) {
                animation = animations.get(RIGHT_ANIMATION);
                animation.stop();
            }
        }
        if (isFalling) {
            if (fallSpeed < maxFallSpeedDown) {
                nextFallSpeed();
            }
        } else {
            if (fallSpeed < maxFallSpeedUp) {
                nextFallSpeed();
            } else if (y() < landingPoint - jumpHeight - sprite.height) {
                // reached "maximum" jump height, start falling again
                new DelayedEvent(0.1) {
                    @Override
                    public void onFinish() {
                        isFalling = true;
                        resetFallSpeed();
                    }
                }.start();
            }
        }
        int i = 1;
        if (!isFalling) {
            i = -1;
        }
        y(y() + fallSpeed * i);
        super.tick();
        if (y() + 1 > Game.WINDOW_HEIGHT) {
            // below visible, fell off
            InGameState.onPlayerDeath();
        }
    }

    @Override
    public void move(byte direction) {
        // modified move() from Entity to allow continuous horizontal movement
        EntityBoundary boundary = defaultBoundary;
        if (direction == -1)
            return;
        switch (direction) {
            case DIRECTION_LEFT:
                if (boundary.right <= 0) {
                    x(Game.WINDOW_WIDTH);
                } else {
                    x(x() - (int) speed);
                }
                break;
            case DIRECTION_RIGHT:
                if (boundary.left >= Game.WINDOW_WIDTH) {
                    x(-sprite.width);
                } else {
                    x(x() + (int) speed);
                }
                break;
            default:
                break;
        }
    }

}
