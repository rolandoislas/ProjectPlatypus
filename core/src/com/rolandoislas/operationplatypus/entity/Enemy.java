package com.rolandoislas.operationplatypus.entity;

import com.badlogic.ashley.core.Entity;
import com.rolandoislas.operationplatypus.util.EntityHelper;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.ScriptComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.rolandoislas.operationplatypus.OperationPlatypus.sceneLoader;

/**
 * Created by Rolando on 8/23/2016.
 */
public class Enemy extends GravityGameEntity {
	@Override
	public void init(Entity entity) {
		super.init(entity);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		meleeAttack();
	}

	private void meleeAttack() {
		// Check if player is in range to attack
		Player player = (Player) EntityHelper.getClosestEntityWithTag(getEntity(), "player")
				.getComponent(ScriptComponent.class).scripts.get(0);
		if (player.overlaps(getEntity()))
			this.attack(player);
	}

	@Override
	public void die() {
		super.die();
		Entity hide = EntityHelper.spawn("itemHide", getCenterX(), getY() + getHeight());
		new ItemWrapper(hide).addScript(new Pickup());
		EntityHelper.deSpawn(getEntity());
	}
}
