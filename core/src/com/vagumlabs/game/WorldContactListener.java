package com.vagumlabs.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by B0204046 on 20/10/18.
 */

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact cntct) {
        Fixture fa = cntct.getFixtureA();
        Fixture fb = cntct.getFixtureB();
        if (fa == null || fb == null) return;
        if (fa.getUserData() == null || fb.getUserData() == null) return;
        if (isGroundContact(fa, fb)) {
            Player player = (Player) fb.getUserData();
            player.setState(PlayerState.IDLE);
        }
        if (isDangerContact(fa, fb)) {
            Player player = (Player) fb.getUserData();
            player.setState(PlayerState.DEAD);
        }
    }

    @Override
    public void endContact(Contact cntct) {
        Fixture fa = cntct.getFixtureA();
        Fixture fb = cntct.getFixtureB();
        if (fa == null || fb == null) return;
        if (fa.getUserData() == null || fb.getUserData() == null) return;
        if (isGroundContact(fa, fb)) {
            Player player = (Player) fb.getUserData();
            player.setState(PlayerState.JUMPING);
        }
        if (isDangerContact(fa, fb)) {
            Player player = (Player) fb.getUserData();
            player.setState(PlayerState.JUMPING);
        }
    }

    private boolean isDangerContact(Fixture a, Fixture b) {
        return (a.getUserData() instanceof DangerZone && b.getUserData() instanceof Player);
    }

    private boolean isGroundContact(Fixture a, Fixture b) {
        return (a.getUserData() instanceof Ground && b.getUserData() instanceof Player);
    }

    @Override
    public void preSolve(Contact cntct, Manifold mnfld) {
    }

    @Override
    public void postSolve(Contact cntct, ContactImpulse ci) {
    }
}
