package com.rolandoislas.operationplatypus.entity;

import com.rolandoislas.operationplatypus.data.Direction;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.factory.EntityFactory;

/**
 * Created by Rolando on 8/24/2016.
 */
class AttachedEntity extends GameEntity {
	private final GameEntity boundEntity;
	private float initialRotation;
	private float relativeX = 0;
	private float relativeY = 0;

	AttachedEntity(GameEntity entity) {
		this.boundEntity = entity;
		this.disableHealthBar();
		this.setSpeed(0);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		updatePosition();
		updateDirection();
		updateRotation();
	}

	private void updateRotation() {
		if (initialRotation == 0)
			return;
		if (getBoundEntity().getDirection().equals(Direction.RIGHT)) {
			if (initialRotation > 0)
				this.setRotation(Math.abs(this.getRotation()));
			else
				this.setRotation(-Math.abs(this.getRotation()));
		}
		else {
			if (initialRotation < 0)
				this.setRotation(Math.abs(this.getRotation()));
			else
				this.setRotation(-Math.abs(this.getRotation()));
		}
	}

	private void updateDirection() {
		if (getBoundEntity().getDirection().equals(Direction.RIGHT))
			setDirection(Direction.RIGHT);
		else
			setDirection(Direction.LEFT);
	}

	private void updatePosition() {
		// Position update
		setX(boundEntity.getX() + (getDirection().equals(Direction.LEFT) ?
			getBoundEntity().getWidth() - relativeX - getWidth() : relativeX));
		setY(boundEntity.getY() + relativeY);
	}

	@Override
	void setRotation(float degree) {
		super.setRotation(degree);
		if (initialRotation == 0)
			this.initialRotation = degree;
	}

	void setRelativeX(float relativeX) {
		this.relativeX = relativeX;
	}

	void setRelativeY(float relativeY) {
		this.relativeY = relativeY;
	}

	GameEntity getBoundEntity() {
		return boundEntity;
	}

	public float getRelativeX() {
		return relativeX;
	}
}
