package com.rolandoislas.operationplatypus.entity;

import com.badlogic.ashley.core.Entity;
import com.rolandoislas.operationplatypus.data.Item;
import com.rolandoislas.operationplatypus.data.Items;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.utils.CustomVariables;

/**
 * Created by Rolando on 8/29/2016.
 */
public class Pickup extends GravityGameEntity {
	@Override
	public void init(Entity entity) {
		this.disableHealthBar();
		super.init(entity);
	}

	Item getItem() {
		CustomVariables customVars = new CustomVariables();
		customVars.loadFromString(getEntity().getComponent(MainItemComponent.class).customVars);
		Item item = new Item();
		item.setType(Items.valueOf(customVars.getStringVariable("type").toUpperCase()));
		item.setEntityIdentifier(customVars.getStringVariable("id"));
		return item;
	}
}
