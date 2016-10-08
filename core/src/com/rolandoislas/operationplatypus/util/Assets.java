package com.rolandoislas.operationplatypus.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

/**
 * Created by Rolando on 8/23/2016.
 */
public class Assets {
	public static AssetManager manager;
	public static Sound hutSound;
	public static I18NBundle lang;

	public static void create() {
		manager = new AssetManager();
		load();
	}

	private static void load() {
		// Audio
		manager.load("audio/hurt.ogg", Sound.class);
		// Lang
		manager.load("lang/lang", I18NBundle.class);
	}

	public static void done() {
		// Audio
		hutSound = manager.get("audio/hurt.ogg", Sound.class);
		// Lang
		lang = manager.get("lang/lang", I18NBundle.class);
	}

	public static void dispose() {
		manager.dispose();
	}
}
