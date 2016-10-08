package com.rolandoislas.operationplatypus;

import com.badlogic.ashley.core.Entity;
import com.rolandoislas.operationplatypus.data.Scene;
import com.rolandoislas.operationplatypus.util.Assets;
import com.uwsoft.editor.renderer.scripts.IScript;

/**
 * Created by Rolando on 8/29/2016.
 */
public class Loading implements IScript {
	@Override
	public void init(Entity entity) {
		Assets.create();
	}

	@Override
	public void act(float delta) {
		if (Assets.manager.update()) {
			Assets.done();
			OperationPlatypus.loadScene(Scene.LEVEL_ONE);
		}
	}

	@Override
	public void dispose() {

	}
}
