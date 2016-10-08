package com.rolandoislas.operationplatypus.entity;

import com.badlogic.ashley.core.Entity;
import com.rolandoislas.operationplatypus.OperationPlatypus;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.ScriptComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.CustomVariables;

import static com.rolandoislas.operationplatypus.OperationPlatypus.sceneLoader;

/**
 * Created by Rolando on 8/22/2016.
 */
public class Portal extends GameEntity {
	@Override
	public void init(Entity entity) {
		this.disableHealthBar();
		super.init(entity);
	}

	@Override
	public void act(float delta) {
		// Check if player is overlaping
		for (Entity entity : sceneLoader.engine.getEntities()) {
			if (entity.getComponent(MainItemComponent.class).tags.contains("player")) {
				if (this.overlaps(entity)) {
					// Activate portal if player is overlapping
					activatePortal();
					// Warp if player is not on floor
					Player player = (Player) entity.getComponent(ScriptComponent.class).scripts.get(0);
					if (player.isJumping()) {
						CustomVariables portalCustomVars = new CustomVariables();
						portalCustomVars.loadFromString(getEntity().getComponent(MainItemComponent.class).customVars);
						OperationPlatypus.loadScene(portalCustomVars.getStringVariable("warp"));
					}
				}
			}
		}
	}

	private void activatePortal() {

	}

	@Override
	public void dispose() {

	}
}
