package com.rolandoislas.operationplatypus.entity;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by Rolando on 8/24/2016.
 */
public class HealthBar extends AttachedEntity implements IScript {
	private float barWidth;
	private DimensionsComponent healthBarGreenDimensions;

	HealthBar(GameEntity entity) {
		super(entity);
	}

	@Override
	public void init(Entity entity) {
		this.disableHealthBar();
		super.init(entity);
		setBarWidth();
		setPosition();
	}

	private void setPosition() {
		this.setRelativeX(this.getBoundEntity().getWidth() * .15f);
		this.setRelativeY(this.getBoundEntity().getHeight());
	}

	private void setBarWidth() {
		this.barWidth = this.getBoundEntity().getWidth() * .7f;
		ItemWrapper entity = new ItemWrapper(getEntity());
		entity.getChild("red").getComponent(DimensionsComponent.class).width = barWidth;
		healthBarGreenDimensions = entity.getChild("green").getComponent(DimensionsComponent.class);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		updateHealth();
	}

	private void updateHealth() {
		float percent = getBoundEntity().getHealth() / getBoundEntity().getMaxHealth();
		healthBarGreenDimensions.width = barWidth * percent;
	}

	@Override
	public void dispose() {

	}
}
