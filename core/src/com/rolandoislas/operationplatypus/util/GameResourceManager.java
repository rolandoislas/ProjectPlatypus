package com.rolandoislas.operationplatypus.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.uwsoft.editor.renderer.resources.FontSizePair;
import com.uwsoft.editor.renderer.resources.ResourceManager;

import java.io.File;

/**
 * Created by Rolando on 8/23/2016.
 */
public class GameResourceManager extends ResourceManager {
	@Override
	public void loadFont(FontSizePair pair) {
		FileHandle fontFile = Gdx.files.internal(fontsPath + File.separator + pair.fontName + ".ttf");
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
		FreeTypeFontGenerator.setMaxTextureSize(FreeTypeFontGenerator.NO_MAXIMUM);
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = Math.round(pair.fontSize * resMultiplier);
		BitmapFont font = generator.generateFont(parameter);
		font.setUseIntegerPositions(false);
		bitmapFonts.put(pair, font);
	}
}
