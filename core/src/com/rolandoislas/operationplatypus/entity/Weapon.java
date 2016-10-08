package com.rolandoislas.operationplatypus.entity;

import com.badlogic.ashley.core.Entity;
import com.rolandoislas.operationplatypus.data.Direction;
import com.rolandoislas.operationplatypus.data.Item;
import com.rolandoislas.operationplatypus.util.EntityHelper;
import com.uwsoft.editor.renderer.components.ScriptComponent;

/**
 * Created by Rolando on 8/24/2016.
 */
class Weapon extends AttachedEntity {
	private boolean attack = false;
	private boolean reset = false;
	private float initialRotation;
	private float postAttackRotation;
	private boolean hasAttacked = false;
	private Direction initialDirection;

	Weapon(GameEntity entity) {
		super(entity);
	}

	@Override
	public void init(Entity entity) {
		this.disableHealthBar();
		super.init(entity);
		setInitialDirection(getDirection());
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		updateDirection();
		attackUpdate();
	}

	private void updateDirection() {
		if (!getInitialDirection().equals(getDirection())) {
			setInitialDirection(getDirection());
			setInitialRotation(getInitialRotation() * -1);
			setPostAttackRotation(getPostAttackRotation() * -1);
		}
	}

	private void attackUpdate() {
		// Attack
		if (getAttack()) {
			// Swing
			if (getAttackCooldown() < getAttackSpeed() / 2) {
				float rotation = 90 * (getAttackCooldown() / (getAttackSpeed() / 2));
				setRotation(getInitialRotation() + rotation * (getDirection().equals(Direction.RIGHT) ? -1 : 1));
			} else {
				setAttack(false);
				setReset(true);
				setPostAttackRotation(getRotation());
			}
		}
		// Reset
		if (getReset()) {
			if (getAttackCooldown() < getAttackSpeed()) {
				float direction = 90 * ((getAttackCooldown() - getAttackSpeed() / 2) / (getAttackSpeed() / 2));
				setRotation(getPostAttackRotation() + direction * (getDirection().equals(Direction.RIGHT) ? 1 : -1));
			} else {
				setReset(false);
				setRotation(getInitialRotation());
				setHasAttacked(false);
			}
		}
		// Damage
		if (getAttack() || getReset()) {
			Entity entity = EntityHelper.getClosestEntityWithTag(getEntity(), "enemy");
			if (entity != null && this.overlaps(entity) && !hasAttacked()) {
				this.forceAttackWithoutReset((GameEntity) entity.getComponent(ScriptComponent.class).scripts.get(0));
				setHasAttacked(true);
			}
		}
	}

	private boolean hasAttacked() {
		return hasAttacked;
	}

	void attack() {
		if (!attack && !reset) {
			this.setAttackCooldown(0);
			setAttack(true);
			setInitialRotation(getRotation());
			setInitialDirection(getDirection());
		}
	}

	private float getInitialRotation() {
		return initialRotation;
	}

	private void setPostAttackRotation(float postAttackRotation) {
		this.postAttackRotation = postAttackRotation;
	}

	private void setAttack(boolean attack) {
		this.attack = attack;
	}

	private void setReset(boolean reset) {
		this.reset = reset;
	}

	private boolean getAttack() {
		return attack;
	}

	private boolean getReset() {
		return reset;
	}

	private float getPostAttackRotation() {
		return postAttackRotation;
	}

	private void setInitialRotation(float initialRotation) {
		this.initialRotation = initialRotation;
	}

	private void setHasAttacked(boolean hasAttacked) {
		this.hasAttacked = hasAttacked;
	}

	private void setInitialDirection(Direction initialDirection) {
		this.initialDirection = initialDirection;
	}

	private Direction getInitialDirection() {
		return initialDirection;
	}

	void loadStatsFromItem(Item item) {
		setRelativeX(getBoundEntity().getWidth() * item.getItemPropertyAsFloat("x"));
		setRelativeY(getBoundEntity().getHeight() * item.getItemPropertyAsFloat("y"));
		setRotation(item.getItemPropertyAsFloat("rotation"));
		float scale = item.getItemPropertyAsFloat("scale");
		setScale(scale, scale);
		setAttackSpeed(item.getItemPropertyAsFloat("attackSpeed"));
		setAttackDamage(item.getItemPropertyAsFloat("attackDamage"));
	}
}
