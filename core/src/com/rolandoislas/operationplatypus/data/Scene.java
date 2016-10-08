package com.rolandoislas.operationplatypus.data;

/**
 * Created by Rolando on 8/29/2016.
 */
public enum Scene {
	MAIN("MainScene"), MENU("Menu"), LEVEL_ONE("LevelOne"),
	LEVEL_TWO("LevelTwo"), LOADING("Loading");

	public final float width = 53.3f;
	public final float height = 30;
	public final String name;

	/**
	 * Scene name and viewport dimensions
	 *
	 * @param name   Name of scene
	 */
	Scene(String name) {
		this.name = name;
	}
}
