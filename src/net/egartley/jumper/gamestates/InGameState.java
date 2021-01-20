package net.egartley.jumper.gamestates;

import net.egartley.jumper.Debug;
import net.egartley.jumper.Game;
import net.egartley.jumper.core.controllers.KeyboardController;
import net.egartley.jumper.core.input.KeyTyped;
import net.egartley.jumper.core.logic.Calculate;
import net.egartley.jumper.core.logic.collision.Collisions;
import net.egartley.jumper.entities.Entities;
import net.egartley.jumper.ingame.Board;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;

public class InGameState extends BasicGameState {

    public static boolean isDead;

    public static final int ID = 1;

    private Font deathFont = new TrueTypeFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 36), true);
    private Font scoreFont = new TrueTypeFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 21), true);
    private Color deathBackgroundColor = Color.black, deathTextColor = Color.red, scoreColor = Color.white;
    private String deathText = "You died";

    public static Board board;

    private static final ArrayList<KeyTyped> keyTypeds = new ArrayList<>();

    public static void onPlayerDeath() {
        isDead = true;
        Collisions.nuke();
        board.highScore = Math.max(board.highScore, board.score);
    }

    private void deathScreen(Graphics graphics) {
        graphics.setColor(deathBackgroundColor);
        // graphics.fillRect(0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        graphics.fillRect(Calculate.getCenteredX(400), Calculate.getCenteredY(300), 400, 300);
        graphics.setFont(deathFont);
        graphics.setColor(deathTextColor);
        graphics.drawString(deathText, Calculate.getCenteredX(graphics.getFont().getWidth(deathText)), Calculate.getCenteredY(graphics.getFont().getLineHeight()) - 100);
        graphics.setFont(scoreFont);
        graphics.setColor(scoreColor);
        String s = "Score: " + board.score;
        graphics.drawString(s, Calculate.getCenteredX(graphics.getFont().getWidth(s)), Calculate.getCenteredY(graphics.getFont().getLineHeight()) - 35);
        s = "High Score: " + board.highScore;
        graphics.drawString(s, Calculate.getCenteredX(graphics.getFont().getWidth(s)), Calculate.getCenteredY(graphics.getFont().getLineHeight()));
        s = "Press enter to retry";
        graphics.drawString(s, Calculate.getCenteredX(graphics.getFont().getWidth(s)), Calculate.getCenteredY(graphics.getFont().getLineHeight()) + 75);
    }

    private void drawScore(Graphics graphics) {
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, 64, 32);
        graphics.setColor(Color.white);
        graphics.setFont(scoreFont);
        graphics.drawString(String.valueOf(board.score), 8, 2);
    }

    private void playAgain() {
        isDead = false;
        board.score = 0;
        board.populate();
        Entities.PLAYER.onInGameEnter();
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        keyTypeds.add(new KeyTyped(Input.KEY_F3) {
            @Override
            public void onType() {
                Game.debug = !Game.debug;
            }
        });
        keyTypeds.add(new KeyTyped(Input.KEY_ENTER) {
            @Override
            public void onType() {
                if (isDead) {
                    playAgain();
                }
            }
        });
        board = new Board();
        board.populate();
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        for (KeyTyped kt : keyTypeds) {
            KeyboardController.addKeyTyped(kt);
        }
        Entities.PLAYER.onInGameEnter();
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        Entities.PLAYER.onInGameLeave();
        for (KeyTyped kt : keyTypeds) {
            KeyboardController.removeKeyTyped(kt);
        }
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
        board.render(graphics);
        if (isDead) {
            deathScreen(graphics);
        } else {
            drawScore(graphics);
        }
        if (Game.debug) {
            Debug.render(graphics);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        if (!isDead) {
            board.tick();
            Collisions.tick();
        }
    }

    @Override
    public int getID() {
        return InGameState.ID;
    }

}
