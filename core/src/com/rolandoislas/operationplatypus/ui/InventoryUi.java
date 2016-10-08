package com.rolandoislas.operationplatypus.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.rolandoislas.operationplatypus.OperationPlatypus;
import com.rolandoislas.operationplatypus.data.Inventory;
import com.rolandoislas.operationplatypus.data.Item;
import com.rolandoislas.operationplatypus.entity.Player;
import com.rolandoislas.operationplatypus.util.EntityHelper;
import com.uwsoft.editor.renderer.components.ScriptComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;
import com.uwsoft.editor.renderer.scripts.IActorScript;

import static com.rolandoislas.operationplatypus.OperationPlatypus.sceneLoader;

/**
 * Created by Rolando on 8/31/2016.
 */
public class InventoryUi extends Table implements IActorScript {
	private Inventory inventory;
	private Actor[] entities;
	private CompositeActor actor;

	@Override
	public void init(CompositeActor compositeActor) {
		actor = compositeActor;
		inventory = ((Player)EntityHelper.getEntityWithTag("player").getComponent(ScriptComponent.class).scripts.get(0))
				.getInventory();
		entities = new Actor[inventory.getAll().length];
	}

	@Override
	public void act(float delta) {
		loadEntities();
	}

	private void loadEntities() {
		Item[] items = inventory.getAll();
		for (int i = 0; i< items.length; i++) {
			// The slot has no item or the wrong one
			if ((items[i] != null && entities[i] == null) || (items[i] != null && entities[i] != null &&
					!items[i].getEntityIdentifier().equals(entities[i].getName()))) {
				// Construct item
				CompositeItemVO vo = sceneLoader.loadVoFromLibrary(items[i].getEntityIdentifier());
				CompositeActor itemActor = new CompositeActor(vo, OperationPlatypus.sceneLoader.getRm());
				Actor slot = getSlot(i);
				// Calculate scale
				float slotSize = slot.getWidth();
				float itemSize = itemActor.getWidth() > itemActor.getHeight() ? itemActor.getWidth() :
						itemActor.getHeight();
				itemActor.setScale(slotSize / itemSize);
				// Set position
				itemActor.setX(slot.getX() + (slot.getWidth() - itemActor.getWidth() * itemActor.getScaleX()) / 2);
				itemActor.setY(slot.getY() + (slot.getHeight() - itemActor.getHeight() * itemActor.getScaleY()) / 2);
				itemActor.setName(items[i].getEntityIdentifier());
				// Add to array and table
				entities[i] = itemActor;
				actor.addActor(itemActor);
			}
			// Item should be reset to null
			else if (items[i] == null && entities[i] != null) {
				actor.getItem(items[i].getEntityIdentifier()).remove();
				entities[i] = null;
			}
		}
	}

	private Actor getSlot(int index) {
		return actor.getItem("slot" + index);
	}

	@Override
	public void dispose() {

	}
}
