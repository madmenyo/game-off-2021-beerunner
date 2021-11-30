package net.madmenyo.beerunner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

/**
 * It's endless... it's relentless... it's Beelendless!!!
 */
public class BeeRunner extends Game {

	public AssetManager assetManager;

	public MenuScreen menuScreen;
	public CreditsScreen creditsScreen;

	public Music menuMusic;
	public Music gameMusic;
	
	@Override
	public void create () {
		Assets assets= new Assets();
		assets.load();

		assetManager = assets.getAssetManager();

		menuMusic = assetManager.get(Assets.menuMusic);
		menuMusic.setVolume(.6f);
		menuMusic.setLooping(true);
		menuMusic.play();

		gameMusic = assetManager.get(Assets.gameMusic);
		gameMusic.setVolume(.6f);
		gameMusic.setLooping(true);

		menuScreen = new MenuScreen(this);
		creditsScreen = new CreditsScreen(this);

		//setScreen(new MenuScreen(assets.getAssetManager()));
		setScreen(menuScreen);
	}
}
