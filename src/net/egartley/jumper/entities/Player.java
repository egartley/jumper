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
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class Player extends AnimatedEntity {

    private final byte RIGHT_FALLING = 0;
    private final byte LEFT_FALLING = 1;
    private final byte LEFT_RISING = 2;
    private final byte RIGHT_RISING = 3;
    private final byte SQUISH = 4;
    private final int ANIMATION_THRESHOLD = 100;

    public int fallSpeed = 0, maxFallSpeedUp = 4, maxFallSpeedDown = 6;
    private int landingPoint, jumpHeight = 72;
    private double downAcc = 0.0625D, upAcc = 0.05D, fallControl= 0D, maxControl = Math.PI / 2D;
    public boolean isFalling = true, isSquished = false;

    private Platform lastPlatform;
    public EntityBoundary boundary;

    public Player() {
        super("Player", new SpriteSheet(Images.get(Images.RIGHT_FALLING), 31, 27, 1, 1), false);
        sprites.add(new SpriteSheet(Images.get(Images.LEFT_FALLING), 31, 27, 1, 1).getSprite(0));
        sprites.add(new SpriteSheet(Images.get(Images.LEFT_RISING), 31, 27, 1, 1).getSprite(0));
        sprites.add(new SpriteSheet(Images.get(Images.RIGHT_RISING), 31, 27, 1, 1).getSprite(0));
        sprites.add(new SpriteSheet(Images.get(Images.SQUISH), 31, 27, 1, 6).getSprite(0));
        setAnimations();
        speed = 3D;
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
            // rising, so don't do anything
            return;
        }
        lastPlatform = platform;
        isSquished = true;
        animation.start();
        new DelayedEvent(0.095) {
            @Override
            public void onFinish() {
                isFalling = false;
                isSquished = false;
                animation.stop();
                landingPoint = platform.y();
                resetFallSpeed();
            }
        }.start();
    }

    @Override
    public void setAnimations() {
        animations.add(new Animation(Util.getAnimationFrames(sprites.get(SQUISH)), ANIMATION_THRESHOLD));
        animation = animations.get(0);
    }

    @Override
    public void setBoundaries() {
        boundary = new EntityBoundary(this, sprite, new BoundaryPadding(2, 0, 0, 0));
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
    public void render(Graphics graphics) {
        if (isSquished) {
            animation.draw(x(), y());
        } else {
            graphics.drawImage(sprite.asImage(), x(), y());
        }
        if (Game.debug) {
            drawDebug(graphics);
        }
    }

    @Override
    public void tick() {
        boolean left = Keyboard.isPressed(Input.KEY_A);
        boolean right = Keyboard.isPressed(Input.KEY_D);
        if (!isSquished) {
            if (!isFalling) {
                sprite = sprites.get(sprite.equals(sprites.get(LEFT_FALLING)) ? LEFT_RISING : RIGHT_RISING);
            } else {
                sprite = sprites.get(sprite.equals(sprites.get(LEFT_RISING)) ? LEFT_FALLING : RIGHT_FALLING);
            }
            if (left) {
                if (!sprite.equals(sprites.get(LEFT_FALLING)) && isFalling) {
                    sprite = sprites.get(LEFT_FALLING);
                }
                move(DIRECTION_LEFT);
            } else if (right) {
                if (!sprite.equals(sprites.get(LEFT_FALLING)) && isFalling) {
                    sprite = sprites.get(LEFT_FALLING);
                }
                move(DIRECTION_RIGHT);
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
        if (!isSquished) {
            y(y() + fallSpeed * i);
        } else {
            y(lastPlatform.y() - sprite.height);
        }
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
