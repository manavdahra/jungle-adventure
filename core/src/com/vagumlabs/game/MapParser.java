package com.vagumlabs.game;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import static com.vagumlabs.game.Constants.PIXEL_PER_METER;

/**
 * Created by B0204046 on 19/10/18.
 */

public class MapParser {

    private static final String MAP_LAYER_NAME_GROUND = "ground";
    private static final String MAP_LAYER_NAME_BOUNDS = "bounds";
    private static final String MAP_LAYER_NAME_DANGERS = "dangers";

    public static void parseMapLayers(World world, TiledMap tiledMap) {
        for (MapLayer layer : tiledMap.getLayers()) {
            for (MapObject object : layer.getObjects()) {
                Shape shape;
                if (object instanceof PolygonMapObject) {
                    shape = createShape((PolygonMapObject) object);
                } else {
                    continue;
                }
                if (layer.getName().equals(MAP_LAYER_NAME_GROUND))
                    new Ground(world, shape);
                if (layer.getName().equals(MAP_LAYER_NAME_BOUNDS))
                    new Bounds(world, shape);
                if (layer.getName().equals(MAP_LAYER_NAME_DANGERS))
                    new DangerZone(world, shape);
            }
        }
    }

    private static Shape createShape(PolygonMapObject mapObject) {
        float[] vertices = mapObject.getPolygon().getTransformedVertices();
        Vector2[] worldVerticies = new Vector2[vertices.length / 2];
        for (int i = 0; i < worldVerticies.length; i++) {
            worldVerticies[i] = new Vector2(vertices[i * 2] / PIXEL_PER_METER,
                    vertices[i * 2 + 1] / PIXEL_PER_METER);
        }
        ChainShape cs = new ChainShape();
        cs.createChain(worldVerticies);
        return cs;
    }
}
