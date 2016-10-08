package com.rolandoislas.operationplatypus.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

/**
 * Created by Rolando on 8/23/2016.
 */
public class GameDialogBox {
	private String message;
	private ArrayList<ClickListener> confirmListeners = new ArrayList<ClickListener>();
	private ArrayList<ClickListener> cancelListeners = new ArrayList<ClickListener>();

	public void setMessage(String message) {
		this.message = message;
	}

	public void addConfirmAction(ClickListener clickListener) {
		confirmListeners.add(clickListener);
	}

	public void addCancelAction(ClickListener clickListener) {
		cancelListeners.add(clickListener);
	}

	String getMessage() {
		return message;
	}

	void confirm(InputEvent event, float x, float y) {
		for (ClickListener listener : confirmListeners)
			listener.clicked(event, x, y);
	}

	void cancel(InputEvent event, float x, float y) {
		for (ClickListener listener : cancelListeners)
			listener.clicked(event, x, y);
	}

	boolean hasConfirmAction() {
		return confirmListeners.size() > 0;
	}

	boolean hasCancelAction() {
		return cancelListeners.size() > 0;
	}
}
