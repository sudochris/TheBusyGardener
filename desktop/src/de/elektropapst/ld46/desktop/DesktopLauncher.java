package de.elektropapst.ld46.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.elektropapst.ld46.LDGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "The Busy Gardener [Ludum Dare 46 - Keep it alive]";
		config.samples = 4;
		config.width = 1280; // 1280
		config.height = 768; //  640
		config.forceExit = false;
		config.resizable = false;
		new LwjglApplication(new LDGame(), config);
	}
}
