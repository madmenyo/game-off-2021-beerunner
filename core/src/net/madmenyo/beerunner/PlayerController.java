package net.madmenyo.beerunner;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import net.madmenyo.beerunner.gui.GuiStage;

public class PlayerController implements GestureDetector.GestureListener, InputProcessor {

    private Player player;
    private GuiStage gui;

    private boolean flying = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;



    public PlayerController(Player player, GuiStage gui) {
        this.player = player;
        this.gui = gui;
    }

    public void update(float delta){
        if (gui.isPaused()) return;

        if (flying) player.fly(delta);

        if (moveLeft) player.moveLeft(delta);

        if (moveRight) player.moveRight(delta);
    }

    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            moveLeft = true;
        }
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            moveRight = true;
        }
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W || keycode == Input.Keys.SPACE) {
            flying = true;
        }

        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE){
            if (gui.isPaused()) gui.pause(false);
            else gui.pause(true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A){
            moveLeft = false;
        }
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            moveRight = false;
        }
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W || keycode == Input.Keys.SPACE) {
            flying = false;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
