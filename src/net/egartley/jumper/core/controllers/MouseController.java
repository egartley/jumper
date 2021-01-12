package net.egartley.jumper.core.controllers;

import net.egartley.jumper.core.input.MouseClicked;

import java.util.ArrayList;

public class MouseController {

    private static final ArrayList<MouseClicked> clickeds = new ArrayList<>();

    public static void onMouseClick(int button, int x, int y) {
        ((ArrayList<MouseClicked>) clickeds.clone()).forEach(mc -> mc.onClick(button, x, y));
    }

    public static void addMouseClicked(MouseClicked mc) {
        if (!clickeds.contains(mc)) {
            clickeds.add(mc);
        }
    }

    public static void removeMouseClicked(MouseClicked mc) {
        clickeds.remove(mc);
    }

}
