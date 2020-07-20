package com.vagumlabs.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.vagumlabs.game.controller.Controller;

import static com.vagumlabs.game.Constants.MAP_PATH;
import static com.vagumlabs.game.Constants.PIXEL_PER_METER;
import static com.vagumlabs.game.Constants.SCALE;

public class SimpleGame extends ApplicationAdapter {

    private static final Logger logger = new Logger(SimpleGame.class.getCanonicalName());

    private static final float TIME_STEP = 1 / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private OrthographicCamera orthographicCamera;
    private Box2DDebugRenderer box2DDebugRenderer;
    private World world;
    private Player player;
    private SpriteBatch batch;
    private TextureAtlas runTextureAtlas;
    private TextureAtlas idleTextureAtlas;
    private TextureAtlas jumpTextureAtlas;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private TiledMap tiledMap;
    private TextureRegion region;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Controller controller;
    private boolean flip = false;
    private float stateTime = 0f;
    private static float vpW = 0f;
    private static float vpH = 0f;

    private static final float VELOCITY_Y = -9.85f;
    private static final float VELOCITY_X = 0f;

    @Override
    public void create() {
        vpW = Gdx.graphics.getWidth() / SCALE;
        vpH = Gdx.graphics.getHeight() / SCALE;

        orthographicCamera = new OrthographicCamera(vpW, vpH);

        world = new World(new Vector2(VELOCITY_X, VELOCITY_Y), false);
        box2DDebugRenderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();

        runTextureAtlas = new TextureAtlas(Gdx.files.internal(Player.PLAYER_RUN_ATLAS));
        idleTextureAtlas = new TextureAtlas(Gdx.files.internal(Player.PLAYER_IDLE_ATLAS));
        jumpTextureAtlas = new TextureAtlas(Gdx.files.internal(Player.PLAYER_JUMP_ATLAS));

        runAnimation = new Animation<TextureRegion>(0.15f, runTextureAtlas.getRegions(), Animation.PlayMode.LOOP);
        idleAnimation = new Animation<TextureRegion>(0.15f, idleTextureAtlas.getRegions(), Animation.PlayMode.LOOP);
        tiledMap = new TmxMapLoader().load(MAP_PATH);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        MapParser.parseMapLayers(world, tiledMap);

        player = new Player(world);
        controller = new Controller(batch);
        world.setContactListener(new WorldContactListener());
    }

    private void inputUpdate() {
        if (player.getState() == PlayerState.DEAD) {
            return;
        }

        int horizontalForce = 0;
        if (Gdx.input.isTouched()) {
            if (controller.isRightPressed()) {
                horizontalForce += 1;
                if (flip) {
                    flip = false;
                }
            }
            if (controller.isLeftPressed()) {
                horizontalForce -= 1;
                if (!flip) {
                    flip = true;
                }
            }

            if (controller.isUpPressed() && player.getState() != PlayerState.JUMPING) {
                player.getBody().applyForceToCenter(0, Player.JUMP_FORCE, false);
                player.setState(PlayerState.JUMPING);
            }

            if (player.getState() != PlayerState.JUMPING && (controller.isLeftPressed() || controller.isRightPressed())) {
                player.setState(PlayerState.RUNNING);
            }

            player.getBody().setLinearVelocity(horizontalForce * Player.RUN_FORCE, player.getBody().getLinearVelocity().y);
        } else if (player.getState() != PlayerState.JUMPING) {
            player.setState(PlayerState.IDLE);
        }

    }

    private void resolvePlayerKeyframes() {
        stateTime += Gdx.graphics.getDeltaTime();
        switch (player.getState()) {
            case DEAD:
                world.destroyBody(player.getBody());
                player = new Player(world);
                break;
            case JUMPING:
                float vy = player.getBody().getLinearVelocity().y;
                if (vy > 1f) {
                    region = jumpTextureAtlas.findRegion("jump1");
                } else if (vy <= 1f && vy > 0f) {
                    region = jumpTextureAtlas.findRegion("jump3");
                } else if (vy < 0f) {
                    region = jumpTextureAtlas.findRegion("jump4");
                } else if (vy == 0f) {
                    region = jumpTextureAtlas.findRegion("jump2");
                }
                break;
            case RUNNING:
                region = runAnimation.getKeyFrame(stateTime, true);
                break;
            case IDLE:
                region = idleAnimation.getKeyFrame(stateTime, true);
                break;
        }
    }

    private void update() {
        world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        inputUpdate();
        cameraUpdate();
        tiledMapRenderer.setView(orthographicCamera);
    }

    private void cameraUpdate() {
        Vector3 position = orthographicCamera.position;
        position.x = player.getBody().getPosition().x * PIXEL_PER_METER;
        position.y = (player.getBody().getPosition().y) * PIXEL_PER_METER;

        position.x = MathUtils.clamp(position.x, vpW / 2f, 1000);
        position.y = MathUtils.clamp(position.y, vpH / 2f, 1000);

        orthographicCamera.position.set(position);
        orthographicCamera.update();
    }

    @Override
    public void render() {
        update();
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiledMapRenderer.render();
        resolvePlayerKeyframes();

        batch.setProjectionMatrix(controller.getStage().getCamera().combined);
        controller.draw();

        batch.setProjectionMatrix(orthographicCamera.combined);
        batch.begin();
        batch.draw(region,
                player.getBody().getPosition().x * PIXEL_PER_METER - (region.getRegionWidth() / 2),
                player.getBody().getPosition().y * PIXEL_PER_METER - (region.getRegionHeight() / 2),
                region.getRegionWidth(),
                region.getRegionHeight(),
                region.getRegionWidth() * 2,
                region.getRegionHeight() * 2,
                flip ? -1f : 1f,
                1f,
                0f
        );
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        orthographicCamera.setToOrtho(false, width / SCALE, height / SCALE);
        controller.resize(width, height);
    }

    @Override
    public void dispose() {
        controller.dispose();
        runTextureAtlas.dispose();
        idleTextureAtlas.dispose();
        jumpTextureAtlas.dispose();
        batch.dispose();
        box2DDebugRenderer.dispose();
        world.dispose();
        tiledMapRenderer.dispose();
        tiledMap.dispose();
    }
}
