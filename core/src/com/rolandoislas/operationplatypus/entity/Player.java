package com.rolandoislas.operationplatypus.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.rolandoislas.operationplatypus.OperationPlatypus;
import com.rolandoislas.operationplatypus.data.Inventory;
import com.rolandoislas.operationplatypus.data.Item;
import com.rolandoislas.operationplatypus.data.Items;
import com.rolandoislas.operationplatypus.data.Scene;
import com.rolandoislas.operationplatypus.event.InventoryChangeListener;
import com.rolandoislas.operationplatypus.ui.GameDialogBox;
import com.rolandoislas.operationplatypus.ui.GameUI;
import com.rolandoislas.operationplatypus.util.EntityHelper;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.ScriptComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.rolandoislas.operationplatypus.OperationPlatypus.sceneLoader;
import static com.rolandoislas.operationplatypus.OperationPlatypus.sceneWidth;

/**
 * Created by Rolando on 8/21/2016.
 */
public class Player extends GravityGameEntity {

	private boolean acceptingInput = true;
	private Weapon weapon;
	private Inventory inventory = new Inventory();

	@Override
	public void init(Entity entity) {
		this.disableHealthBar();
		super.init(entity);
		this.setSpeed(10);
		this.setJumpSpeed(8);
		this.setJumpHeight(35);
		this.setHealth(10);
		this.setJumpDamage(1f);
		addInventoryChangeListener();
		// TODO load inventory from save
		setWeapon();
	}

	private void addInventoryChangeListener() {
		inventory.addChangeListener(new InventoryChangeListener() {
			@Override
			public void changed(int slot) {
				if (slot == 0)
					setWeapon();
			}
		});
	}

	private void setWeapon() {
		Item weaponItem = inventory.getWeapon();
		if (weaponItem == null) {
			weaponItem = new Item();
			weaponItem.setType(Items.WEAPON);
			weaponItem.setEntityIdentifier("invisible");
		}
		if (weapon != null)
			EntityHelper.deSpawn(weapon.getEntity());
		weapon = new Weapon(this);
		Entity entity = EntityHelper.spawn(weaponItem.getEntityIdentifier(), 0, 0);
		entity.getComponent(ZIndexComponent.class).setZIndex(EntityHelper.getLargestZIndex() + 1);
		new ItemWrapper(entity).addScript(weapon);
		weapon.loadStatsFromItem(weaponItem);
	}

	@Override
	public void die() {
		super.die();
		acceptingInput = false;
		GameDialogBox dialog = new GameDialogBox();
		dialog.setMessage("You have died. Level failed. Returning to level select.");
		dialog.addConfirmAction(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				OperationPlatypus.loadScene(Scene.MAIN);
			}
		});
		GameUI.showDialogBox(dialog);
	}

	@Override
	public void act(float delta) {
		if (!acceptingInput)
			return;
		super.act(delta);
		checkMovement();
		checkJumpAttack();
		checkPickup();
		checkOpenInventory();
	}

	private void checkOpenInventory() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
			CompositeActor inventoryUi = GameUI.getInventory();
			inventoryUi.setVisible(!inventoryUi.isVisible());
		}
	}

	private void checkPickup() {
		Entity entity = EntityHelper.getClosestEntityWithTag(getEntity(), "pickup", getWidth());
		if (entity != null) {
			Pickup pickup = (Pickup) entity.getComponent(ScriptComponent.class).scripts.get(0);
			if (inventory.addItem(pickup.getItem()))
				EntityHelper.deSpawn(pickup.getEntity());
		}
	}

	private void checkJumpAttack() {
		// Jump on top of enemy
		if (!isJumping())
			for (Entity entity : sceneLoader.getEngine().getEntities())
				if (entity.getComponent(MainItemComponent.class).tags.contains("enemy"))
					if (overlapsTop(entity)) {
						this.jump(Gdx.graphics.getDeltaTime(), true);
						this.setImmunity(.1f);
						this.jumpAttack(((Enemy)entity.getComponent(ScriptComponent.class).scripts.get(0)));
					}
	}

	private void checkMovement() {
		// Move Left
		if ((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) && getX() > 0)
			this.moveLeft();
		// Move Right
		if ((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) &&
				getX() + getWidth() < sceneWidth)
			this.moveRight();
		// Attack
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
			weapon.attack();
		// Jump
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W) ||
				Gdx.input.isKeyJustPressed(Input.Keys.UP))
			this.jump(Gdx.graphics.getDeltaTime());
	}

	public Inventory getInventory() {
		return inventory;
	}
}
