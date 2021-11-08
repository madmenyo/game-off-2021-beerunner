package net.madmenyo.beerunner;

import com.badlogic.gdx.Game;

public class BeeRunner extends Game {
	
	@Override
	public void create () {
		setScreen(new GameScreen(null));
	}
}
