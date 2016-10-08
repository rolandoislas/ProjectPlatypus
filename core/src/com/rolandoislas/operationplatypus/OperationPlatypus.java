package com.rolandoislas.operationplatypus;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.rolandoislas.operationplatypus.data.Scene;
import com.rolandoislas.operationplatypus.entity.*;
import com.rolandoislas.operationplatypus.ui.GameUI;
import com.rolandoislas.operationplatypus.util.Assets;
import com.rolandoislas.operationplatypus.util.EntityHelper;
import com.rolandoislas.operationplatypus.util.GameResourceManager;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

public class OperationPlatypus extends ApplicationAdapter {

	public static StretchViewport viewport;
	public static SceneLoader sceneLoader;
	public static Scene scene;
	private static Player player;
	public static float sceneWidth;
	private static GameUI ui;
	private ShapeRenderer shapeRenderer;
	private boolean debug = false;

	public static void loadScene(String warp) {
		scene = Scene.valueOf(warp);
		createScene();
	}

	public static void loadScene(Scene scene) {
		loadScene(scene.name());
	}

	private static void createScene() {
		loadSceneWithSceneLoader();
		if (isCurrentLevelGame())
			initializeGameEntities();
		else
			initializeMenuEntities();
	}

	private static void initializeMenuEntities() {

	}

	private static boolean isCurrentLevelGame() {
		return scene.name.contains("Level") || scene.name.equals("MainScene");
	}

	private static void initializeGameEntities() {
		// Initialize entities for game scenes
		if (scene == Scene.MENU)
			return;
		// Initialize/reset
		sceneWidth = 0;
		if (player != null) {
			EntityHelper.deSpawn(player.getEntity());
			player = null;
		}
		// Loop through entities for general initialization
		for (Entity entity : sceneLoader.engine.getEntities()) {
			MainItemComponent mainComponent = entity.getComponent(MainItemComponent.class);
			ItemWrapper item = new ItemWrapper(entity);
			// Floor
			if (mainComponent.tags.contains("floor")) {
				// Calculate scene width
				DimensionsComponent dimensionComponent = entity.getComponent(DimensionsComponent.class);
				if (dimensionComponent.width > sceneWidth)
					sceneWidth = dimensionComponent.width;
			}
			// Player
			if (mainComponent.itemIdentifier.equalsIgnoreCase("playerSpawn")) {
				TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
				item.getComponent(MainItemComponent.class).visible = false;
				player = new Player();
				item = new ItemWrapper(EntityHelper.spawn("playerPlatypus", transformComponent.x,
						transformComponent.y));
				item.addScript(player);
			}
			// Portal
			if (mainComponent.tags.contains("portal"))
				item.addScript(new Portal());
			// Fist
			if (mainComponent.tags.contains("fist"))
				item.addScript(new Fist());
			// Detector
			if (mainComponent.tags.contains("detector"))
				item.addScript(new Detector());
			// NPCs
			if (mainComponent.tags.contains("npc")) {
				if (mainComponent.itemIdentifier.equals("guide"))
					item.addScript(new Guide());
			}
			// Pickup
			if (mainComponent.tags.contains("pickup"))
				item.addScript(new Pickup());
		}
		// Check player was spawned
		assert player != null : "Level " + scene.name + " is missing a playerSpawn.";
		// Initialize UI
		ui = new GameUI();
	}

	private static void loadSceneWithSceneLoader() {
		viewport = new StretchViewport(scene.width, scene.height);
		GameResourceManager resourceManager = new GameResourceManager();
		resourceManager.initAllResources();
		sceneLoader = new SceneLoader(resourceManager);
		sceneLoader.loadScene(scene.name, viewport);
	}

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		loadScene(Scene.LOADING);
		createShapeRenderer();
		loadAssets();
	}

	private void loadAssets() {
		new ItemWrapper(sceneLoader.getRoot()).addScript(new Loading());
	}

	private void createShapeRenderer() {
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		sceneLoader.rayHandler.useCustomViewport(viewport.getScreenX(), viewport.getScreenY(),
				viewport.getScreenWidth(), viewport.getScreenHeight());
	}

	@Override
	public void render() {
		// Reset
		Gdx.gl.glClearColor(43/255f, 49/255f, 48/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Render scene
		sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
		// Update camera
		updateCamera();
		// Render UI
		renderGameUi();
		// Render debug shapes
		renderDebug();
	}

	private void renderGameUi() {
		if (!isCurrentLevelGame())
			return;
		ui.act();
		ui.draw();
	}

	private void renderDebug() {
		// Toggle debug
		if (Gdx.input.isKeyJustPressed(Input.Keys.F3))
			this.debug = !debug;
		if (!debug)
			return;
		// Render polygons
		Matrix4 mat = viewport.getCamera().combined.cpy();
		mat.setToOrtho2D(viewport.getCamera().position.x - (viewport.getWorldWidth() / 2), 0, scene.width, scene.height);
		shapeRenderer.setProjectionMatrix(mat);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		for (Entity entity : sceneLoader.engine.getEntities())
			shapeRenderer.polygon(EntityHelper.entityToPolygon(entity).getTransformedVertices());
		shapeRenderer.end();
	}

	private void updateCamera() {
		// Position camera to player
		if (!isCurrentLevelGame())
			return;
		// Update camera
		OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();
		camera.position.x = player.getCenterX();
		// Set camera bounds
		if (camera.position.x < viewport.getWorldWidth() / 2)
			camera.position.x = viewport.getWorldWidth() / 2;
		if (camera.position.x > sceneWidth - viewport.getWorldWidth() / 2)
			camera.position.x = sceneWidth - viewport.getWorldWidth() / 2;
	}

	@Override
	public void dispose() {
		Assets.dispose();
	}
}
