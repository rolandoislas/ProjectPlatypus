package com.rolandoislas.operationplatypus.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rolandoislas.operationplatypus.OperationPlatypus;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Project Platypus";
		config.width = 1280;
		config.height = 720;
		config.addIcon("image/Icon128.png", Files.FileType.Internal);
		config.addIcon("image/Icon32.png", Files.FileType.Internal);
		config.addIcon("image/Icon16.png", Files.FileType.Internal);
		new LwjglApplication(new OperationPlatypus(), config);
	}
}
