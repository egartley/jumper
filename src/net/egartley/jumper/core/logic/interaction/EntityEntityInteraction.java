package net.egartley.jumper.core.logic.interaction;

import net.egartley.jumper.core.abstracts.Entity;
import net.egartley.jumper.core.controllers.KeyboardController;
import net.egartley.jumper.core.input.KeyTyped;
import net.egartley.jumper.core.logic.collision.EntityEntityCollision;
import net.egartley.jumper.core.logic.events.EntityEntityCollisionEvent;
import org.newdawn.slick.Input;

public class EntityEntityInteraction {

    public Entity[] entities;
    public EntityEntityCollision collision;

    private static final int defaultKeyCode = Input.KEY_ENTER;

    private boolean isActive;
    private boolean didInteract;
    private final KeyTyped keyTyped;

    public EntityEntityInteraction(Entity e1, Entity e2) {
        this(e1.defaultBoundary, e2.defaultBoundary);
    }

    public EntityEntityInteraction(EntityBoundary b1, EntityBoundary b2) {
        entities = new Entity[]{b1.parent, b2.parent};
        collision = new EntityEntityCollision(b1, b2) {
            @Override
            public void end(EntityEntityCollisionEvent event) {
                didInteract = false;
            }
        };
        keyTyped = new KeyTyped(defaultKeyCode) {
            @Override
            public void onType() {
                if (collision.isCollided && !didInteract) {
                    interact();
                    didInteract = true;
                }
            }
        };
    }

    public void tick() {
        if (!isActive) {
            return;
        }
        collision.tick();
    }

    public void activate() {
        isActive = true;
        collision.activate();
        KeyboardController.addKeyTyped(keyTyped);
    }

    public void deactivate() {
        isActive = false;
        collision.deactivate();
        KeyboardController.removeKeyTyped(keyTyped);
    }

    public void interact() {

    }

}
