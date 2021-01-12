package net.egartley.jumper.core.abstracts;

import net.egartley.jumper.Debug;
import net.egartley.jumper.Game;
import net.egartley.jumper.Util;
import net.egartley.jumper.core.graphics.Sprite;
import net.egartley.jumper.core.graphics.SpriteSheet;
import net.egartley.jumper.core.interfaces.Tickable;
import net.egartley.jumper.core.logic.collision.Collisions;
import net.egartley.jumper.core.logic.collision.EntityEntityCollision;
import net.egartley.jumper.core.logic.events.EntityEntityCollisionEvent;
import net.egartley.jumper.core.logic.interaction.EntityBoundary;
import org.newdawn.slick.*;

import java.util.ArrayList;

/**
 * Anything that is rendered with a {@link Sprite} at a specified location
 *
 * @see AnimatedEntity
 * @see StaticEntity
 */
public abstract class Entity extends Renderable implements Tickable {

    public static final byte DIRECTION_UP = 1;
    public static final byte DIRECTION_DOWN = 2;
    public static final byte DIRECTION_LEFT = 3;
    public static final byte DIRECTION_RIGHT = 4;

    /**
     * The entity's sprites
     */
    protected ArrayList<Sprite> sprites = new ArrayList<>();
    /**
     * The entity's sprite sheets
     */
    protected ArrayList<SpriteSheet> sheets = new ArrayList<>();
    /**
     * Collection of the entity's boundaries
     */
    public ArrayList<EntityBoundary> boundaries = new ArrayList<>();

    /**
     * The sprite to use while rendering
     */
    public Sprite sprite;
    /**
     * The buffered image to use when rendering (default)
     */
    protected Image image;
    /**
     * The most recent collision that has occurred for the entity. If no
     * collisions have occurred within the entity's lifetime, this will be
     * null
     */
    public EntityEntityCollision lastCollision = null;
    /**
     * The most recent collision event to have occurred. This will be null
     * if no collision event has yet to take place
     */
    public EntityEntityCollisionEvent lastCollisionEvent = null;
    /**
     * The boundary to use if not specified
     */
    public EntityBoundary defaultBoundary = null;
    /**
     * Less-than-one change in the x-coordinate, used for non-integer speeds
     */
    public double deltaX;
    /**
     * Less-than-one change in the y-coordinate, used for non-integer speeds
     */
    public double deltaY;
    /**
     * The entity's speed (magnitude of its location change when moving)
     */
    public double speed;
    /**
     * The entity's unique identification number. Use {@link #name} for
     * user-friendly identification
     */
    public int uuid;
    /**
     * Whether or not the entity is animated
     */
    public boolean isAnimated;
    /**
     * Whether or not the entity is currently collided with another entity
     */
    public boolean isCollided;
    /**
     * Whether or not the entity is "allowed" to collide with others
     */
    public boolean canCollide = true;
    /**
     * Human-readable identifier for the entity (not unique)
     */
    public String name;

    private static final Font debugFont = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11), true);
    private static final Color nameTagBackgroundColor = new Color(0, 0, 0, 128);

    private int nameTagWidth;
    /**
     * Whether or not font metrics have been initialized. Since {@link
     * #render(Graphics)} is called about 60 times a second, and the
     * resulting font metrics object will always be the same, there is no
     * need to keep re-computing it each time {@link #render(Graphics)} is
     * called
     */
    private boolean setFontMetrics;

    Entity(String name) {
        this(name, (Sprite) null);
    }

    Entity(String name, Sprite sprite) {
        uuid = Util.randomInt(999999, 100000, true);
        this.name = name;
        if (sprite != null) {
            setSprite(sprite);
            setBoundaries();
        }
        speed = 1.0;
        setCollisions();
    }

    Entity(String name, SpriteSheet sheet) {
        this(name, sheet.sprites.get(0));
        sprites = sheet.sprites;
        sheets.add(sheet);
    }

    /**
     * Renders the entity, using {@link #image}, at ({@link #x()}, {@link #y()})
     */
    public void render(Graphics graphics) {
        graphics.drawImage(image, x(), y());
        if (Game.debug) {
            drawDebug(graphics);
        }
    }

    /**
     * Renders debug information, such as the entity's boundaries and "name tag"
     */
    public void drawDebug(Graphics graphics) {
        drawBoundaries(graphics);
        drawNameTag(graphics);
    }

    /**
     * Draws the entity's "name tag", which is {@link #toString()}
     */
    private void drawNameTag(Graphics graphics) {
        if (!setFontMetrics) {
            nameTagWidth = debugFont.getWidth(toString()) + 8;
            setFontMetrics = true;
        }
        int nameX = (x() + (sprite.width / 2)) - nameTagWidth / 2;
        int nameY = y() - 18;

        graphics.setColor(nameTagBackgroundColor);
        graphics.setFont(debugFont);

        graphics.fillRect(nameX, nameY, nameTagWidth, 18);
        graphics.setColor(Color.white);
        graphics.drawString(toString(), nameX + 5, nameY + 16 - debugFont.getLineHeight());
    }

    /**
     * Draws all of the entity's boundaries
     */
    private void drawBoundaries(Graphics graphics) {
        boundaries.forEach(boundary -> boundary.draw(graphics));
    }

    /**
     * Ticks boundaries and interactions
     *
     * @see #boundaries
     */
    @Override
    public void tick() {
        boundaries.forEach(EntityBoundary::tick);
    }

    /**
     * Sets the entity's x and y coordinates, as well as updating the coordinate deltas
     *
     * @param x The new x-coordinate
     * @param y The new y-coordinate
     * @see #deltaX
     * @see #deltaY
     */
    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        deltaX = x;
        deltaY = y;
    }

    @Override
    public void x(int x) {
        x(x, true);
    }

    @Override
    public void y(int y) {
        y(y, true);
    }

    public void x(int x, boolean setDelta) {
        super.x(x);
        if (setDelta) {
            deltaX = x;
        }
    }

    public void y(int y, boolean setDelta) {
        super.y(y);
        if (setDelta) {
            deltaY = y;
        }
    }

    /**
     * Updates the entity's location by {@link #speed}, unless the
     * specified boundary is outside of the game's window
     */
    private void move(byte direction, EntityBoundary boundary) {
        if (direction == -1)
            return;
        switch (direction) {
            case DIRECTION_UP:
                if (boundary.top <= 0) {
                    break; // top of window
                }
                deltaY -= speed;
                y(y() - (int) Math.abs(deltaY - y()), false);
                break;
            case DIRECTION_DOWN:
                if (boundary.bottom >= Game.WINDOW_HEIGHT) {
                    break; // bottom of window
                }
                deltaY += speed;
                y(y() + (int) Math.abs(deltaY - y()), false);
                break;
            case DIRECTION_LEFT:
                if (boundary.left <= 0) {
                    break; // left side of window
                }
                deltaX -= speed;
                x(x() - (int) Math.abs(deltaX - x()), false);
                break;
            case DIRECTION_RIGHT:
                if (boundary.right >= Game.WINDOW_WIDTH) {
                    break; // right side of window
                }
                deltaX += speed;
                x(x() + (int) Math.abs(deltaX - x()), false);
                break;
            default:
                break;
        }
    }

    /**
     * Changes the entity's location ({@link #x()} and {@link #y()}) at a rate
     * of {@link #speed} per call. Since there is no boundary parameter,
     * the entity's {@link #defaultBoundary} will be used
     */
    protected void move(byte direction) {
        move(direction, defaultBoundary);
    }

    /**
     * Returns whether or not the entity is to the "right" of the other
     * entity
     */
    private boolean isRightOf(Entity e) {
        return x() > e.x();
    }

    /**
     * Returns whether or not the entity is to the "left" of the other
     * entity
     */
    private boolean isLeftOf(Entity e) {
        return !isRightOf(e);
    }

    /**
     * Returns whether or not the entity is "above" the other entity
     */
    private boolean isAbove(Entity e) {
        return y() < e.y();
    }

    /**
     * Returns whether or not the entity is "below" the other entity
     */
    private boolean isBelow(Entity e) {
        return !isAbove(e);
    }

    /**
     * Sets the entity's boundaries
     *
     * @see #boundaries
     */
    protected abstract void setBoundaries();

    /**
     * Sets the entity's collisions
     *
     * @see #boundaries
     */
    public abstract void setCollisions();

    private void onSpriteChanged() {
        image = sprite.asImage();
        if (this instanceof AnimatedEntity) {
            ((AnimatedEntity) this).animations.clear();
            ((AnimatedEntity) this).setAnimations();
        }
        boundaries.clear();
        setBoundaries();
        Collisions.endWith(this);
        setCollisions();
    }

    /**
     * Sets the current sprite
     *
     * @param index The index in {@link #sprites}
     * @see #sprite
     */
    public void setSprite(int index, boolean update) {
        if (index < sprites.size()) {
            sprite = sprites.get(index);
            if (update) {
                onSpriteChanged();
            }
        } else {
            Debug.warning("Tried to get a sprite for \"" + this + "\" at an valid index, " + index + " (must be less than " + sprites.size() + ")");
        }
    }

    public void setSprite(Sprite sprite) {
        if (sprite == null) {
            Debug.warning("Tried to set the sprite for " + this + " to null");
            return;
        }
        this.sprite = sprite;
        sprites.clear();
        sprites.add(sprite);
        image = sprite.asImage();
    }

    /**
     * Sets {@link #sprites} to {@link SpriteSheet#sprites} from {@link #sheets} at the given index
     *
     * @param index The index in {@link #sheets}
     * @see #sheets
     * @see #sprites
     */
    public void setSpriteSheet(int index) {
        if (index < sheets.size()) {
            sprites = sheets.get(index).sprites;
            setSprite(0, true);
        } else {
            Debug.warning("Tried to get a sprite for \"" + this + "\" at an valid index, " + index + " (must be less than " + sprites.size() + ")");
        }
    }

    /**
     * Returns the entity as a string, in the format <code>id#uuid</code>
     *
     * @see #name
     * @see #uuid
     */
    public String toString() {
        return name + "#" + uuid;
    }

}
