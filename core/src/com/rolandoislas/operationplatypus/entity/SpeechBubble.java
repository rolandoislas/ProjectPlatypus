package com.rolandoislas.operationplatypus.entity;

import com.badlogic.gdx.Gdx;
import com.rolandoislas.operationplatypus.OperationPlatypus;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by Rolando on 8/29/2016.
 */
public class SpeechBubble extends AttachedEntity {
	private float visibleTime;

	SpeechBubble(GameEntity entity) {
		super(entity);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		updateVisibllity();
		updatePosition();
	}

	private void updatePosition() {
		ItemWrapper label = new ItemWrapper(getEntity()).getChild("text");
		ItemWrapper background = new ItemWrapper(getEntity()).getChild("background");
		LabelComponent labelComponent = label.getComponent(LabelComponent.class);
		DimensionsComponent backgroundDimensions = background.getComponent(DimensionsComponent.class);
		TransformComponent backgroundTransform = background.getComponent(TransformComponent.class);
		TransformComponent labelTransform = label.getComponent(TransformComponent.class);
		backgroundDimensions.width = labelComponent.getGlyphLayout().width;
		backgroundDimensions.height = labelComponent.getGlyphLayout().height * 2;
		backgroundTransform.x = (getBoundEntity().getCenterX() - backgroundDimensions.width / 2 *
				OperationPlatypus.viewport.getWorldWidth()) / OperationPlatypus.viewport.getWorldWidth();
		labelTransform.x = backgroundTransform.x;
	}

	private void updateVisibllity() {
		if (getVisibleTime() > 0)
			setVisibleTime(getVisibleTime() - Gdx.graphics.getDeltaTime());
		if (getVisibleTime() < 0)
			this.hide();
	}

	void setText(String text) {
		new ItemWrapper(getEntity()).getChild("text").getComponent(LabelComponent.class).setText(text);
	}

	void setVisibleTime(float visibleTime) {
		this.visibleTime = visibleTime;
	}

	private float getVisibleTime() {
		return visibleTime;
	}
}
