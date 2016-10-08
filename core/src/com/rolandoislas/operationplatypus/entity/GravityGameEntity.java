package com.rolandoislas.operationplatypus.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.rolandoislas.operationplatypus.OperationPlatypus;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.CustomVariables;

import static com.rolandoislas.operationplatypus.OperationPlatypus.sceneLoader;

/**
 * Created by Rolando on 8/21/2016.
 */
public abstract class GravityGameEntity extends GameEntity {

	@Override
	public void act(float delta) {
		super.act(delta);
		fall(delta);
	}

	@Override
	public void dispose() {

	}

	private void fall(float delta) {
		// Reset if entity fell out of world
		if (getY() < 0)
			setY(5);
		// No gravity while jumping. Yay!
		if (this.isJumping())
			return;
		// Check if entity is overlapping floor
		boolean onFloor = false;
		for (Entity entity : sceneLoader.engine.getEntities()) {
			MainItemComponent mainComponent = entity.getComponent(MainItemComponent.class);
			if (mainComponent.tags.contains("floor"))
				if (this.overlapsTop(entity))
					onFloor = true;
		}
		// Set onFloor entity state
		this.setOnFloor(onFloor);
		// Move entity down if not on floor
		assert sceneLoader.world.getGravity().y != 0 : "Level " + OperationPlatypus.scene.name + " does not have it's gravity set.";
		if (!onFloor)
			this.moveEntity(0, -(sceneLoader.world.getGravity().y + this.getGravity()) * delta);
	}
}
