package net.madmenyo.beerunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;

import java.util.ArrayList;
import java.util.List;


public class Assets {

    public static final List<AssetDescriptor<Model>> trees = new ArrayList<>();
    public static final List<AssetDescriptor<Model>> rocks = new ArrayList<>();
    public static final List<AssetDescriptor<Model>> flowers = new ArrayList<>();

    public static final AssetDescriptor<Texture> background = new AssetDescriptor<>("gui/background.png", Texture.class);
    public static final AssetDescriptor<Skin> skin = new AssetDescriptor<>("gui/guiskin.json", Skin.class, new SkinLoader.SkinParameter("gui/skin.atlas"));

    public static final AssetDescriptor<Sound> click = new AssetDescriptor<Sound>("sound/click.wav", Sound.class);
    public static final AssetDescriptor<Sound> bee = new AssetDescriptor<Sound>("sound/bee.wav", Sound.class);

    private AssetManager assetManager = new AssetManager();

    public static final AssetDescriptor<Music> menuMusic = new AssetDescriptor<>("sound/forest_loop.mp3", Music.class);
    public static final AssetDescriptor<Music> gameMusic = new AssetDescriptor<>("sound/musicloop.mp3", Music.class);


    public void load(){

        assetManager.load(background);
        assetManager.load(skin);

        assetManager.load(click);
        assetManager.load(bee);

        assetManager.load(menuMusic);
        assetManager.load(gameMusic);

        System.out.println("Adding tree models...");
        for (int i = 1; i <= 24; i++){
            System.out.println("Loading tree model...");
            AssetDescriptor<Model> treeDescriptor;
            if (i >= 10) {
                //treeDescriptor = new AssetDescriptor<>("models/trees/tree.0"+ i + ".g3dj", Model.class);
                treeDescriptor = new AssetDescriptor<>("models/combined/tree.0"+ i + ".g3dj", Model.class);

            } else {
                //treeDescriptor = new AssetDescriptor<>("models/trees/tree.00"+ i + ".g3dj", Model.class);
                treeDescriptor = new AssetDescriptor<>("models/combined/tree.00"+ i + ".g3dj", Model.class);
            }

            assetManager.load(treeDescriptor);
            trees.add(treeDescriptor);
        }

        System.out.println("Adding rock models...");
        for (int i = 1; i <= 7; i++){
            System.out.println("Loading rock model...");
            AssetDescriptor<Model> treeDescriptor;
            if (i >= 10) {
                //treeDescriptor = new AssetDescriptor<>("models/rocks/rock_"+ i + ".g3dj", Model.class);
                treeDescriptor = new AssetDescriptor<>("models/combined/rock_"+ i + ".g3dj", Model.class);
            } else {
                //treeDescriptor = new AssetDescriptor<>("models/rocks/rock_0"+ i + ".g3dj", Model.class);
                treeDescriptor = new AssetDescriptor<>("models/combined/rock_0"+ i + ".g3dj", Model.class);
            }

            assetManager.load(treeDescriptor);
            rocks.add(treeDescriptor);
        }


        System.out.println("Adding flower models...");
        for (int i = 1; i <= 3; i++){
            System.out.println("Loading flower model...");
            AssetDescriptor<Model> treeDescriptor;
            if (i >= 10) {
                treeDescriptor = new AssetDescriptor<>("models/combined/flower_"+ i + ".g3dj", Model.class);
            } else {
                treeDescriptor = new AssetDescriptor<>("models/combined/flower_0"+ i + ".g3dj", Model.class);
            }

            assetManager.load(treeDescriptor);
            flowers.add(treeDescriptor);
        }


        assetManager.finishLoading();
    }



    public AssetManager getAssetManager() {
        return assetManager;
    }
}
