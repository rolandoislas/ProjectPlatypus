package com.rolandoislas.operationplatypus.data;

import com.rolandoislas.operationplatypus.event.InventoryChangeListener;

import java.util.ArrayList;

/**
 * Created by Rolando on 8/30/2016.
 */
public class Inventory {
	/**
	 * 0-1 primary and secondary weapon
	 * 2 buck hides
	 * 3-n main inventory
	 */
	private Item[] inventory = new Item[22];
	private ArrayList<InventoryChangeListener> eventListeners = new ArrayList<InventoryChangeListener>();

	public boolean addItem(Item item) {
		int slot = getFreeInventorySlot();
		// Check if weapon slot is free
		if (item.getType().equals(Items.WEAPON) && slot == 0)
			inventory[0] = item;
		// One whole buck
		else if (item.getEntityIdentifier().equals("itemHide")) {
			if (inventory[2] == null)
				inventory[2] = item;
			else
				inventory[2].setAmount(inventory[2].getAmount() + 1);
		}
		// Add to main  inventory
		else if (slot >= 3)
			inventory[slot] = item;
		// Don't addd
		else
			return false;
		alertChangeListeners(slot);
		return true;
	}

	private int getFreeInventorySlot() {
		for (int i = 0; i < inventory.length; i++)
			if (inventory[i] == null)
				return i;
		return -1;
	}

	private void alertChangeListeners(int slot) {
		for (InventoryChangeListener listener : eventListeners)
			listener.changed(slot);
	}

	public void addChangeListener(InventoryChangeListener listener) {
		eventListeners.add(listener);
	}

	public Item getWeapon() {
		return inventory[0];
	}

	public Item[] getAll() {
		return inventory;
	}
}
