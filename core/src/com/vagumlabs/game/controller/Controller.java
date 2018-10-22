package com.vagumlabs.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.vagumlabs.game.Constants.SCALE;

/**
 * Created by B0204046 on 21/10/18.
 */

public class Controller {
    private Viewport viewport;
    private Stage stage;
    private Table directionsTable;
    private Table actionsTable;
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    private static float vpW = 0f;
    private static float vpH = 0f;

    public Stage getStage() {
        return stage;
    }

    public Controller(SpriteBatch batch) {
        vpW = Gdx.graphics.getWidth() / SCALE;
        vpH = Gdx.graphics.getHeight() / SCALE;

        OrthographicCamera orthographicCamera = new OrthographicCamera(vpW, vpH);

        viewport = new FitViewport(vpW, vpH, orthographicCamera);
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        directionsTable = new Table();
        directionsTable.setDebug(true);
        directionsTable.bottom().left();

        actionsTable = new Table();
        actionsTable.setDebug(true);
        actionsTable.bottom().right();

        Image upImg = new Image(new Texture("controller/up_arrow.png"));
        upImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });

        Image lefImg = new Image(new Texture("controller/left_arrow.png"));
        lefImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });

        Image rightImg = new Image(new Texture("controller/right_arrow.png"));
        rightImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        actionsTable.add(upImg).padRight(20);
        actionsTable.row().pad(5, 5, 5, 5);
        actionsTable.setFillParent(true);

        directionsTable.add(lefImg).padLeft(20).padRight(20);
        directionsTable.add(rightImg);
        directionsTable.row().pad(5, 5, 5, 5);

        directionsTable.setFillParent(true);

        stage.addActor(directionsTable);
        stage.addActor(actionsTable);
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public void setUpPressed(boolean upPressed) {
        this.upPressed = upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public void setDownPressed(boolean downPressed) {
        this.downPressed = downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    public void dispose() {
        stage.dispose();
    }
}
