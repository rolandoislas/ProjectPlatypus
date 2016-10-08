package com.rolandoislas.operationplatypus.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Rolando on 8/30/2016.
 */
public class Item {
	private Items type = Items.ITEM;
	private String entityIdentifier = "invisible";
	private JsonValue json;
	private int amount;

	public Item() {
		createJsonObject();
	}

	private void createJsonObject() {
		json = new JsonReader().parse(Gdx.files.internal("data/" + getType().name().toLowerCase() + ".json"))
				.get(getEntityIdentifier());
	}

	Items getType() {
		return type;
	}

	public String getEntityIdentifier() {
		return entityIdentifier;
	}

	public void setType(Items type) {
		this.type = type;
		createJsonObject();
	}

	public void setEntityIdentifier(String entityIdentifier) {
		this.entityIdentifier = entityIdentifier;
		createJsonObject();
	}

	public float getItemPropertyAsFloat(String key) {
		return json.getFloat(key);
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}