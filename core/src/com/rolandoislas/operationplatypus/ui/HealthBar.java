package com.rolandoislas.operationplatypus.ui;

import com.badlogic.ashley.core.Entity;
import com.rolandoislas.operationplatypus.entity.Player;
import com.rolandoislas.operationplatypus.util.EntityHelper;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.ScriptComponent;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;
import com.uwsoft.editor.renderer.scripts.IActorScript;

import static com.rolandoislas.operationplatypus.OperationPlatypus.sceneLoader;

/**
 * Created by Rolando on 8/31/2016.
 */
public class HealthBar implements IActorScript {
	private CompositeActor actor;

	@Override
	public void init(CompositeActor compositeActor) {
		actor = compositeActor;
	}

	@Override
	public void act(float delta) {
		// Update health bar
		for (Entity entity : sceneLoader.engine.getEntities())
			if (entity.getComponent(MainItemComponent.class).tags.contains("player")) {
				Player player = ((Player) entity.getComponent(ScriptComponent.class).scripts.get(0));
				setHealth(player.getHealth() / player.getMaxHealth());
			}
	}

	/**
	 * Sets the size of the health indicator bar
	 * @param percent percent of bar to fill
	 */
	private void setHealth(float percent) {
		float healthBarMaxWidth = actor.getWidth() - 40;
		actor.getItem("health").setWidth(healthBarMaxWidth * percent);
	}

	@Override
	public void dispose() {

	}
}
