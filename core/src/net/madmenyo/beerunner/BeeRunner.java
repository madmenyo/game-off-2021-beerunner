package net.madmenyo.beerunner;

import com.badlogic.gdx.Game;

public class BeeRunner extends Game {
	
	@Override
	public void create () {
		Assets assets= new Assets();

		assets.load();



		setScreen(new GameScreen(assets.getAssetManager()));
	}
}
