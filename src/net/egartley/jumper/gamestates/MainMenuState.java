package net.egartley.jumper.gamestates;

import net.egartley.jumper.Game;
import net.egartley.jumper.core.ui.MenuButton;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;

public class MainMenuState extends BasicGameState {

    public static final int ID = 0;

    private final Color backgroundColor = Color.lightGray;

    public ArrayList<MenuButton> buttons = new ArrayList<>();

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        buttons.add(new MenuButton("Play", true, (Game.WINDOW_WIDTH / 2) - 40, (Game.WINDOW_HEIGHT / 2) - 16, 80, 32) {
            @Override
            public void onClick() {
                game.enterState(InGameState.ID);
            }
        });
        buttons.add(new MenuButton("Quit", true, (Game.WINDOW_WIDTH / 2) - 60, (Game.WINDOW_HEIGHT / 2) - 16 + 52, 120, 32) {
            @Override
            public void onClick() {
                Game.quit();
            }
        });
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        for (MenuButton mb : buttons) {
            mb.registerClicked();
        }
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        for (MenuButton mb : buttons) {
            mb.deregisterClicked();
        }
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);

        for (MenuButton b : buttons) {
            b.render(graphics);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        for (MenuButton b : buttons) {
            b.tick();
        }
    }

    @Override
    public int getID() {
        return MainMenuState.ID;
    }

}
