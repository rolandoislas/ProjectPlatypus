package com.rolandoislas.operationplatypus.entity;

import com.badlogic.ashley.core.Entity;
import com.rolandoislas.operationplatypus.util.EntityHelper;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.scripts.IScript;

import static com.rolandoislas.operationplatypus.OperationPlatypus.viewport;

/**
 * Created by Rolando on 8/23/2016.
 */
public class Fist extends Enemy {
	@Override
	public void init(Entity entity) {
		super.init(entity);
		this.setAttackSpeed(1f);
		this.setSpeed(2);
		this.setHealth(3);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		ai();
	}

	private void ai() {
		if (!this.isOnFloor())
			return;
		Entity player = EntityHelper.getClosestEntityWithTag(getEntity(), "player",
				viewport.getCamera().viewportWidth / 2);
		if (player == null)
			return;
		TransformComponent playerTransform = player.getComponent(TransformComponent.class);
		if (playerTransform.x > getX())
			this.moveRight();
		if (playerTransform.x < getX())
			this.moveLeft();
	}
}
