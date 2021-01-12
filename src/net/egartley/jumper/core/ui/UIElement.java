package net.egartley.jumper.core.ui;

import net.egartley.jumper.Util;
import net.egartley.jumper.core.abstracts.Renderable;
import net.egartley.jumper.core.interfaces.Tickable;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public abstract class UIElement extends Renderable implements Tickable {

    public int width;
    public int height;
    public boolean isEnabled;
    public Image image;

    public UIElement(Image image) {
        this(image, true);
    }

    public UIElement(Image image, boolean enabled) {
        this(image.getWidth(), image.getHeight(), enabled);
        this.image = image;
    }

    public UIElement(int size) {
        this(size, size);
    }

    public UIElement(int width, int height) {
        this(width, height, true);
    }

    public UIElement(int width, int height, boolean enabled) {
        this.width = width;
        this.height = height;
        this.isEnabled = enabled;
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics graphics) {
        if (image != null) {
            graphics.drawImage(image, x(), y());
        }
    }

    public boolean isClickInBounds(int cx, int cy) {
        return Util.isWithinBounds(cx, cy, x(), y(), width, height);
    }

}
