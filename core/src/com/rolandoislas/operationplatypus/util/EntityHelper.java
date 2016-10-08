package com.rolandoislas.operationplatypus.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.rolandoislas.operationplatypus.data.Direction;
import com.rolandoislas.operationplatypus.entity.GameEntity;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;

import java.util.ArrayList;
import java.util.Iterator;

import static com.rolandoislas.operationplatypus.OperationPlatypus.sceneLoader;

/**
 * Created by Rolando on 8/23/2016.
 */
public class EntityHelper {
	/**
	 * Spawn entity in world
	 * @param id VO library name
	 * @param x x position
	 * @param y y position
	 * @return entity
	 */
	public static Entity spawn(String id, float x, float y) {
		CompositeItemVO vo = sceneLoader.loadVoFromLibrary(id);
		assert vo != null : "Could not load VO from library: " + id;
		//vo.layerName = "Foreground";
		vo.x = x;
		vo.y = y;
		Entity entity = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), vo);
		sceneLoader.entityFactory.initAllChildren(sceneLoader.engine, entity, vo.composite);
		sceneLoader.engine.addEntity(entity);
		return entity;
	}

	/**
	 * Get the largest z-index of entities in the world
	 * @return z-index
	 */
	public static int getLargestZIndex() {
		int zIndex = -1;
		for (int i = 0; i < sceneLoader.getEngine().getEntities().size(); i++) {
			Entity entity = sceneLoader.getEngine().getEntities().get(i);
			ZIndexComponent zIndexComponent = entity.getComponent(ZIndexComponent.class);
			if (zIndex < zIndexComponent.getZIndex())
				zIndex = zIndexComponent.getZIndex();
		}
		return zIndex;
	}

	public static void deSpawn(Entity entity) {
		sceneLoader.engine.removeEntity(entity);
	}

	/**
	 * Get closest entity that has provided tag
	 * @param originEntity Entity to start the search from
	 * @param tag tag needed on found entities
	 * @param radius radius to search
	 * @return return a found entity or null if none with the provided criteria found
	 */
	public static Entity getClosestEntityWithTag(Entity originEntity, String tag, float radius) {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for (Entity entity : sceneLoader.engine.getEntities())
			if (entity.getComponent(MainItemComponent.class).tags.contains(tag))
				entities.add(entity);
		return getClosestEntity(originEntity, entities, radius);
	}

	public static Entity getClosestEntityWithTag(Entity originEntity, String tag) {
		return getClosestEntityWithTag(originEntity, tag, -1);
	}

	/**
	 * Get nearest entity
	 * @param originEntity entity that will be the origin of search
	 * @param entities a list of entities to search through
	 * @param radius radius the entity must reside
	 * @return Return entity found or null
	 */
	private static Entity getClosestEntity(Entity originEntity, ArrayList<Entity> entities, float radius) {
		TransformComponent originTransform = originEntity.getComponent(TransformComponent.class);
		Entity closest = null;
		double distance = Integer.MAX_VALUE;
		for (Entity entity : entities) {
			TransformComponent transform = entity.getComponent(TransformComponent.class);
			double dist = Math.sqrt(Math.pow(transform.x - originTransform.x, 2) +
					Math.pow(transform.y - originTransform.y, 2));
			if (dist < distance && (radius < 0 || dist <= radius)) {
				distance = dist;
				closest = entity;
			}
		}
		return closest;
	}

	private static Entity getClosestEntity(Entity originEntity, ArrayList<Entity> entities) {
		return getClosestEntity(originEntity, entities, -1);
	}

	/**
	 * Converts entity to a polygon
	 * @param entity entity to convert
	 * @return Polygon from vertices or polygon from bounding box
	 */
	public static Polygon entityToPolygon(Entity entity) {
		PolygonComponent polygonComponent = entity.getComponent(PolygonComponent.class);
		TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
		DimensionsComponent dimensionComponent = entity.getComponent(DimensionsComponent.class);
		Polygon polygon = new Polygon();
		// Entity does not have a polygon component return rectangle
		if (polygonComponent == null) {
			polygon.setVertices(new float[] {0, 0, dimensionComponent.width, 0, dimensionComponent.width,
					dimensionComponent.height, 0, dimensionComponent.height});
			polygon.setPosition(transformComponent.x, transformComponent.y);
		}
		// Map polygon component to libgdx polygon
		else {
			ArrayList<Float> vertices = new ArrayList<Float>();
			for (int i = 0; i < polygonComponent.vertices.length; i++)
				for (int j = 0; j < polygonComponent.vertices[i].length; j++) {
					vertices.add(polygonComponent.vertices[i][j].x);
					vertices.add(polygonComponent.vertices[i][j].y);
				}
			float[] primitiveArray = new float[vertices.size()];
			for (int i = 0; i < vertices.size(); i++)
				primitiveArray[i] = vertices.get(i);
			polygon.setVertices(primitiveArray);
			polygon.setPosition(transformComponent.x, transformComponent.y);
		}
		// Flip polygon
		Array<IScript> scripts = entity.getComponent(ScriptComponent.class).scripts;
		int scaleSign = 1;
		if (scripts.size > 0 && ((GameEntity)scripts.get(0)).getDirection().equals(Direction.LEFT))
			scaleSign = -1;
		// Add translations
		polygon.setOrigin(transformComponent.originX, transformComponent.originY);
		polygon.setRotation(transformComponent.rotation);
		polygon.setScale(transformComponent.scaleX * scaleSign, transformComponent.scaleY);
		polygon.dirty();
		return polygon;
	}

	public static Entity getEntityWithTag(String tag) {
		for (Entity entity : sceneLoader.getEngine().getEntities())
			if (entity.getComponent(MainItemComponent.class).tags.contains(tag))
				return entity;
		return null;
	}
}
