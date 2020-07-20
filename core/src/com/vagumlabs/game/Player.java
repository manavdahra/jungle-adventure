package com.vagumlabs.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 * Created by B0204046 on 19/10/18.
 */

public class Player {

    private static final int BOX_SIZE_W = 32;
    private static final int BOX_SIZE_H = 64;
    private static final float PLAYER_DENSITY = 1.0f;
    private static final float SWORD_DENSITY = 0.3f;
    public static final float RUN_FORCE = 5f;
    public static final float JUMP_FORCE = 650f;
    public static final float FRICTION_FORCE = 3f;
    public static final String PLAYER_RUN_ATLAS = "character/hero-run-weapon.atlas";
    public static final String PLAYER_IDLE_ATLAS = "character/hero-idle-weapon.atlas";
    public static final String PLAYER_JUMP_ATLAS = "character/hero-jump-weapon.atlas";
    public static final float PLAYER_START_X = 5f;
    public static final float PLAYER_START_Y = 25f;

    private Body body;
    private Body weapon;
    private PlayerState state;
    private RevoluteJoint joint;

    public Player(World world) {
        createBoxBody(world, PLAYER_START_X, PLAYER_START_Y);
        createSword(world, PLAYER_START_X, PLAYER_START_Y);
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.bodyA = this.getBody();
        revoluteJointDef.bodyB = this.getWeapon();
        revoluteJointDef.collideConnected = false;
        revoluteJointDef.localAnchorA.set(0, 0);
        revoluteJointDef.localAnchorB.set(0, 0);

        revoluteJointDef.enableMotor = true;
        revoluteJointDef.motorSpeed = 0f;
        revoluteJointDef.maxMotorTorque = 10f;

        revoluteJointDef.enableLimit = true;
        revoluteJointDef.lowerAngle = 1.2f;
        revoluteJointDef.upperAngle = 5;

        joint = (RevoluteJoint) world.createJoint(revoluteJointDef);
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
        fixtureDef.friction = FRICTION_FORCE;
        body = world.createBody(bdef);
        body.createFixture(fixtureDef).setUserData(this);
    }

    private void createSword(World world, float x, float y) {
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.position.set(x, y);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(20 / Constants.PIXEL_PER_METER / 2, 12 / Constants.PIXEL_PER_METER / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = SWORD_DENSITY;
        weapon = world.createBody(bDef);
        weapon.createFixture(fixtureDef).setUserData(this);
    }

    public Body getBody() {
        return body;
    }

    public Body getWeapon() {
        return weapon;
    }

    public PlayerState getState() {
        return state;
    }

    public synchronized void setState(PlayerState state) {
        this.state = state;
    }
}
