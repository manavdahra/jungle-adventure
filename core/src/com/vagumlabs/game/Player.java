package com.vagumlabs.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by B0204046 on 19/10/18.
 */

public class Player {

    private static final int BOX_SIZE_W = 32;
    private static final int BOX_SIZE_H = 64;
    private static final float PLAYER_DENSITY = 1.0f;
    public static final float JUMP_FORCE = 650f;
    public static final float RUN_FORCE = 5f;
    public static final String PLAYER_RUN_ATLAS = "character/hero.atlas";
    public static final String PLAYER_IDLE_ATLAS = "character/hero-idle.atlas";
    public static final String PLAYER_JUMP_ATLAS = "character/hero-jump.atlas";
    public static final float PLAYER_START_X = 5f;
    public static final float PLAYER_START_Y = 25f;

    private Body body;
    private PlayerState state;

    public Player(World world) {
        createBoxBody(world, PLAYER_START_X, PLAYER_START_Y);
    }

    private void createBoxBody(World world, float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.fixedRotation = true;
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x, y);
        state = PlayerState.IDLE;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(BOX_SIZE_H / Constants.PIXEL_PER_METER / 2, BOX_SIZE_W / Constants.PIXEL_PER_METER / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = PLAYER_DENSITY;
        fixtureDef.friction = 3f;
        body = world.createBody(bdef);
        body.createFixture(fixtureDef).setUserData(this);
    }

    public Body getBody() {
        return body;
    }

    public PlayerState getState() {
        return state;
    }

    public synchronized void setState(PlayerState state) {
        this.state = state;
    }
}
