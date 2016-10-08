package com.rolandoislas.operationplatypus.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.rolandoislas.operationplatypus.OperationPlatypus;
import com.rolandoislas.operationplatypus.util.Assets;
import com.rolandoislas.operationplatypus.util.EntityHelper;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.utils.CustomVariables;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;

/**
 * Created by Rolando on 8/29/2016.
 */
public class Npc extends GravityGameEntity {
	private ArrayList<String> dialogue = new ArrayList<String>();
	private boolean speak;
	private float speakTime = 0;
	private int dialogueIndex;
	private SpeechBubble speechBubble;

	@Override
	public void init(Entity entity) {
		this.disableHealthBar();
		super.init(entity);
		createDialogue();
		createSpeechBubble();
	}

	private void createSpeechBubble() {
		ItemWrapper item = new ItemWrapper(EntityHelper.spawn("uiSpeechBubble", 0, 0));
		speechBubble = new SpeechBubble(this);
		speechBubble.setRelativeX(0);
		speechBubble.setRelativeY(getHeight());
		item.addScript(speechBubble);
		speechBubble.hide();
	}

	private void createDialogue() {
		MainItemComponent mainItemComponent = getEntity().getComponent(MainItemComponent.class);
		CustomVariables customVars = new CustomVariables();
		customVars.loadFromString(mainItemComponent.customVars);
		String dialogueGroup = customVars.getStringVariable("dialogue");
		assert dialogueGroup != null : "NPC missing dialogue variable: " + mainItemComponent.itemIdentifier + " " +
				OperationPlatypus.scene.name;
		boolean foundDialogue = true;
		int index = 1;
		while (foundDialogue) {
			try {
				String dialogueString = Assets.lang.get(dialogueGroup + "." + index);
				getDialogue().add(dialogueString);
				index++;
			} catch (Exception e) {
				foundDialogue = false;
			}
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		speak();
	}

	private void speak() {
		// All dialogue exhausted
		if (getDialogueIndex() == getDialogue().size())
			return;
		// Wait for player to be near to start dialogue
		if (!getSpeak()) {
			Entity player = EntityHelper.getClosestEntityWithTag(getEntity(), "player",
					OperationPlatypus.viewport.getCamera().viewportWidth / 2);
			if (player != null)
				setSpeak(true);
			return;
		}
		setSpeakTime(getSpeakTime() + Gdx.graphics.getDeltaTime());
		String text = getDialogue().get(getDialogueIndex());
		if (getDialogueIndex() == 0 || getSpeakTime() > getReadSpeed(text)) {
			showSpeechBubble(text);
			setDialogueIndex(getDialogueIndex() + 1);
			setSpeakTime(0);
		}
	}

	private void showSpeechBubble(String text) {
		speechBubble.setText(text);
		speechBubble.setVisibleTime(getReadSpeed(text));
		speechBubble.show();
	}

	private float getReadSpeed(String text) {
		return text.split("\\w+").length * .5f + 1.5f;
	}

	private void setSpeak(boolean speak) {
		this.speak = speak;
	}

	private boolean getSpeak() {
		return speak;
	}

	private float getSpeakTime() {
		return speakTime;
	}

	private void setSpeakTime(float speakTime) {
		this.speakTime = speakTime;
	}

	private ArrayList<String> getDialogue() {
		return dialogue;
	}

	private int getDialogueIndex() {
		return dialogueIndex;
	}

	private void setDialogueIndex(int dialogueIndex) {
		this.dialogueIndex = dialogueIndex;
	}
}
