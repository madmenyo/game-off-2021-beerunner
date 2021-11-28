package net.madmenyo.beerunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;

import java.util.ArrayList;
import java.util.List;


public class Assets {

    public static final List<AssetDescriptor<Model>> trees = new ArrayList<>();
    public static final List<AssetDescriptor<Model>> rocks = new ArrayList<>();
    public static final List<AssetDescriptor<Model>> flowers = new ArrayList<>();
    
    private AssetManager assetManager = new AssetManager();


    public void load(){

        for (FileHandle file : Gdx.files.internal("models/trees").list()){

            //if (file.file().getName().endsWith("png")) continue;

            try {

                AssetDescriptor<Model> treeDescriptor = new AssetDescriptor<>(file, Model.class);
                assetManager.load(treeDescriptor);
                trees.add(treeDescriptor);
            } catch (GdxRuntimeException e){
                System.out.println(e.getMessage());
            }
        }

        for (FileHandle file : Gdx.files.internal("models/rocks").list()){

            //if (file.file().getName().endsWith("png")) continue;

            try {

                AssetDescriptor<Model> rockDescriptor = new AssetDescriptor<>(file, Model.class);
                assetManager.load(rockDescriptor);
                rocks.add(rockDescriptor);
            } catch (GdxRuntimeException e){
                System.out.println(e.getMessage());
            }
        }

        for (FileHandle file : Gdx.files.internal("models/flowers").list()){

            //if (file.file().getName().endsWith("png")) continue;
            try {
                AssetDescriptor<Model> rockDescriptor = new AssetDescriptor<>(file, Model.class);
                assetManager.load(rockDescriptor);
                flowers.add(rockDescriptor);
            } catch (GdxRuntimeException e){
                System.out.println(e.getMessage());
            }
        }


        assetManager.finishLoading();
    }



    public AssetManager getAssetManager() {
        return assetManager;
    }
}
