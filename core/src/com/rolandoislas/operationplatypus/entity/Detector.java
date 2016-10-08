package com.rolandoislas.operationplatypus.entity;

import com.badlogic.ashley.core.Entity;
import com.rolandoislas.operationplatypus.util.EntityHelper;

/**
 * Created by Rolando on 8/25/2016.
 */
public class Detector extends GravityGameEntity {
	private int r = 0;

	@Override
	public void init(Entity entity) {
		this.disableHealthBar();
		super.init(entity);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (r > -1) {
			if (getRotation() == 360)
				setRotation(0);
			setRotation(getRotation()+1);
			r = 0;
		}
		r++;
		if (this.overlaps(EntityHelper.getClosestEntityWithTag(getEntity(), "player")))
			this.jump(delta);
	}
}
