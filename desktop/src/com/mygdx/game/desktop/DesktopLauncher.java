package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.BapoaInvaders;

public class DesktopLauncher {
	public static void main(String[] args) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Bapao Invaders";
		config.addIcon("Icon.png", Files.FileType.Internal);
		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		config.samples = 3;
		config.x = 0;
		config.y = 0;

		new LwjglApplication(new BapoaInvaders(), config);
	}
}
