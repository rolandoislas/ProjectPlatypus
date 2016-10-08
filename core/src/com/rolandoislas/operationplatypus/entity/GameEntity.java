package com.rolandoislas.operationplatypus.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.rolandoislas.operationplatypus.audio.SoundPlayer;
import com.rolandoislas.operationplatypus.data.Direction;
import com.rolandoislas.operationplatypus.util.Assets;
import com.rolandoislas.operationplatypus.util.EntityHelper;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.factory.EntityFactory;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;

/**
 * Created by Rolando on 8/21/2016.
 */
public abstract class GameEntity implements IScript {

	private TransformComponent transformComponent;
	private DimensionsComponent dimensionsComponent;
	private ArrayList<SpriteAnimationStateComponent> animationStates = new ArrayList<SpriteAnimationStateComponent>();
	private int jumpUpdateIncrement = 0;
	private int jumpCount = 0;
	private boolean isJumping;
	private float speed = 10;
	private float jumpSpeed = 10;
	private int jumpHeight = 25;
	private boolean onFloor = false;
	private boolean canDoubleJump = false;
	private float gravity = 0;
	private Entity entity;
	private float maxHealth = 10;
	private float health = maxHealth;
	private float immunity = 0;
	private float attackSpeed = 1;
	private float damage = 1;
	private float attackCooldownIncrement = 0;
	private float jumpDamage = 1;
	private ItemWrapper healthBar;
	private boolean enableHealthBar = true;
	private Direction direction = Direction.RIGHT;

	public void init(Entity entity) {
		this.entity = entity;
		// Load components
		transformComponent = entity.getComponent(TransformComponent.class);
		dimensionsComponent = entity.getComponent(DimensionsComponent.class);
		loadAnimations();
		// Set origin
		transformComponent.originX = getWidth() / 2;
		transformComponent.originY = getWidth() / 2;
		transformComponent.useOriginTransform = true;
		// Healthbar
		if (enableHealthBar)
			createHealthBar();
	}

	private void loadAnimations() {
		MainItemComponent mainComponent = entity.getComponent(MainItemComponent.class);
		// Loop through all children for the
		if (mainComponent.entityType == EntityFactory.COMPOSITE_TYPE) {
			NodeComponent nodeComponent = entity.getComponent(NodeComponent.class);
			for (Entity child : nodeComponent.children)
				animationStates.add(child.getComponent(SpriteAnimationStateComponent.class));
		}
		// Add base animation
		animationStates.add(this.getEntity().getComponent(SpriteAnimationStateComponent.class));
	}

	private void createHealthBar() {
		healthBar = new ItemWrapper(EntityHelper.spawn("uiHealthIndicatorSmall", 0, 0));
		healthBar.addScript(new HealthBar(this));
	}

	@Override
	public void act(float delta) {
		// Reset animation
		this.animate(false);
		// Jump update
		updateJump(delta);
		// Immunity update
		updateImmunity();
		// Update attack cooldown
		updateAttack();
	}

	private void updateAttack() {
		if (getAttackCooldown() < 10) // Assume no attack will be longer than 10 seconds
			setAttackCooldown(getAttackCooldown() + Gdx.graphics.getDeltaTime());
	}

	private void updateImmunity() {
		if (getImmunity() > 0)
			setImmunity(getImmunity() - .1f);
		if (getImmunity() < 0)
			setImmunity(0);
	}

	void moveEntity(float x, float y) {
		// Animate when moving
		this.animate(true);
		// Move
		setX(getX() + x);
		setY(getY() + y);
	}

	public float getCenterX() {
		return getX() + getOriginX();
	}

	/**
	 * Makes the entity jump
	 * @param delta stage delta
	 * @param update specify if it is a jump request or update
	 * @param force ignore limits and force jump
	 */
	private void jump(float delta, boolean update, boolean force) { // TODO floor that does not allow jumping through
		if (update) {
			// Reset jump count when on floor
			if (onFloor)
				jumpCount = 0;
			// Don't update if not jumping
			if (!isJumping)
				return;
		}
		// Player initiated jump
		else {
			// Player jumped again
			if (!onFloor) {
				if (force || (canDoubleJump && jumpCount < 2))
					jumpUpdateIncrement = 0;
				else
					return;
			}
			// Player jumped first time
			if (!isJumping)
				isJumping = true;
		}
		moveEntity(0, jumpSpeed * delta);
		jumpUpdateIncrement++;
		jumpCount++;
		if (jumpUpdateIncrement == jumpHeight) {
			isJumping = false;
			jumpUpdateIncrement = 0;
		}
	}

	private void updateJump(float delta) {
		jump(delta, true, false);
	}

	/**
	 * Normal jump
	 * @param delta
	 */
	void jump(float delta) {
		jump(delta, false, false);
	}

	/**
	 * Force a jump ignoring limits
	 * @param delta delta
	 * @param force should be forced
	 */
	void jump(float delta, boolean force) {
		jump(delta, false, force);
	}

	float getX() {
		return transformComponent.x;
	}

	float getY() {
		return transformComponent.y;
	}

	void setX(float x) {
		transformComponent.x = x;
	}

	void setY(float y) {
		transformComponent.y = y;
	}

	float getWidth() {
		return dimensionsComponent.width;
	}

	float getHeight() {
		return dimensionsComponent.height;
	}

	boolean overlaps(Entity entity) {
		Polygon shape = EntityHelper.entityToPolygon(getEntity());
		Polygon otherShape = EntityHelper.entityToPolygon(entity);
		return Intersector.overlapConvexPolygons(shape, otherShape);
	}

	private void animate(boolean animate) {
		for (SpriteAnimationStateComponent animationState : animationStates) {
			if (animationState != null) {
				if (animate)
					animationState.get().setPlayMode(Animation.PlayMode.LOOP);
				else
					animationState.get().setPlayMode(Animation.PlayMode.NORMAL);
			}
		}
	}

	/**
	 * Check if entity is overlapping the top of another entity
	 * @param entity entity to check against overlap top
	 * @return true if overlaps top
	 */
	boolean overlapsTop(Entity entity) {
		TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
		DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
		return overlaps(entity) && this.getY() <= transformComponent.y + dimensionsComponent.height &&
				this.getY() > transformComponent.y + dimensionsComponent.height * .5f;
	}

	public Entity getEntity() {
		return entity;
	}

	void damage(float damage) {
		// Ignore damage if already at 0
		if (health == 0)
			return;
		//Ignore damage if immune
		if (immunity > 0)
			return;
		// Reduce health
		health -= damage;
		if (health < 0)
			health = 0;
		// Play hurt sound
		SoundPlayer.play(Assets.hutSound);
		// Call for death
		if (health == 0)
			die();
	}

	public void die() {
		// Despawn healthbar after death
		if (enableHealthBar)
			EntityHelper.deSpawn(healthBar.getEntity());
	}

	public float getHealth() {
		return health;
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	void moveRight() {
		if (!direction.equals(Direction.RIGHT))
			flipX();
		this.moveEntity(speed * Gdx.graphics.getDeltaTime(), 0);
	}

	void moveLeft() {
		if (!direction.equals(Direction.LEFT))
			flipX();
		this.moveEntity(-speed * Gdx.graphics.getDeltaTime(), 0);
	}

	private void flipX() {
		float scale = 0;
		// Flip composite children
		if (getEntity().getComponent(MainItemComponent.class).entityType == EntityFactory.COMPOSITE_TYPE) {
			NodeComponent nodeComponent = getEntity().getComponent(NodeComponent.class);
			for (Entity child : nodeComponent.children) {
				TransformComponent transformComponent = child.getComponent(TransformComponent.class);
				transformComponent.scaleX *= -1;
				scale = transformComponent.scaleX;
			}
		}
		// Flip entity if not a composite
		else {
			this.setScale(this.getScaleX() * -1, this.getScaleY());
			scale = this.getScaleX();
		}
		// Set direction
		if (scale > 0)
			setDirection(Direction.RIGHT);
		else
			setDirection(Direction.LEFT);
	}

	void setImmunity(float time) {
		immunity = time;
	}

	void setHealth(float health) {
		this.maxHealth = health;
		this.health = maxHealth;
	}

	void setSpeed(int speed) {
		this.speed = speed;
	}

	boolean isOnFloor() {
		return onFloor;
	}

	boolean isJumping() {
		return isJumping;
	}

	void setOnFloor(boolean onFloor) {
		this.onFloor = onFloor;
	}

	float getGravity() {
		return gravity;
	}

	void setJumpSpeed(int jumpSpeed) {
		this.jumpSpeed = jumpSpeed;
	}

	void setJumpHeight(int jumpHeight) {
		this.jumpHeight = jumpHeight;
	}

	void setAttackSpeed(float attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	float getDamage() {
		return damage;
	}

	void attack(GameEntity entity, boolean force, boolean resetCooldown) {
		if (canAttack() || force) {
			entity.damage(getDamage());
			if (resetCooldown)
				setAttackCooldown(0);
		}
	}

	private boolean canAttack() {
		return getAttackCooldown() > getAttackSpeed();
	}

	void jumpAttack(GameEntity entity) {
		entity.damage(getJumpDamage());
	}

	float getJumpDamage() {
		return jumpDamage;
	}

	void disableHealthBar() {
		enableHealthBar = false;
	}

	void setRotation(float degree) {
		// Apply rotation
		transformComponent.rotation = degree;
	}

	void setScale(float scaleX, float scaleY) {
		transformComponent.scaleX = scaleX;
		transformComponent.scaleY = scaleY;
	}

	float getRotation() {
		return transformComponent.rotation;
	}

	float getScaleX() {
		return transformComponent.scaleX;
	}

	float getScaleY() {
		return transformComponent.scaleY;
	}

	void setAttackDamage(float attackDamage) {
		this.damage = attackDamage;
	}

	void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Direction getDirection() {
		return direction;
	}

	float getOriginX() {
		return transformComponent.originX;
	}

	float getOriginY() {
		return transformComponent.originY;
	}

	float getCenterY() {
		return getY() + getOriginY();
	}

	float getAttackCooldown() {
		return attackCooldownIncrement;
	}

	void setAttackCooldown(float attackCooldown) {
		this.attackCooldownIncrement = attackCooldown;
	}

	private float getImmunity() {
		return immunity;
	}

	float getAttackSpeed() {
		return attackSpeed;
	}

	@Override
	public void dispose() {

	}

	void forceAttackWithoutReset(GameEntity entity) {
		this.attack(entity, true, false);
	}

	void attack(GameEntity entity) {
		this.attack(entity, false, true);
	}

	void setJumpDamage(float jumpDamage) {
		this.jumpDamage = jumpDamage;
	}

	void hide() {
		entity.getComponent(MainItemComponent.class).visible = false;
	}

	void show() {
		entity.getComponent(MainItemComponent.class).visible = true;
	}
}
