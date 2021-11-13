package net.madmenyo.beerunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.JsonReader;

import java.util.ArrayList;
import java.util.List;


public class Assets {

    public static final List<AssetDescriptor<Model>> trees = new ArrayList<>();

    private AssetManager assetManager = new AssetManager();


    public void load(){


        for (FileHandle file : Gdx.files.internal("models/trees").list()){

            if (file.file().getName().endsWith("png")) continue;

            AssetDescriptor<Model> treeDescriptor = new AssetDescriptor<>(file, Model.class);
            assetManager.load(treeDescriptor);
            trees.add(treeDescriptor);
        }
        assetManager.finishLoading();
    }



    public AssetManager getAssetManager() {
        return assetManager;
    }
}
