package net.madmenyo.beerunner.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import net.madmenyo.beerunner.BeeRunner;

public class DesktopLauncher {
	public static void main (String[] arg) {

		//PackGui();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;

		//config.foregroundFPS = 0;
		//config.backgroundFPS = 0;
		//config.vSyncEnabled = false;

		config.forceExit = false;
		new LwjglApplication(new BeeRunner(), config);
	}

	private static void PackGui(){
		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.flattenPaths = true;
		settings.fast = true;

		String input = "../../images/gui";
		String output = "gui";
		String filename = "skin.atlas";

		TexturePacker.process(settings, input, output, filename);
	}
}
