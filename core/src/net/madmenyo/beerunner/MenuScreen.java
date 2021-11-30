package net.madmenyo.beerunner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen extends ScreenAdapter {
    private BeeRunner beeRunner;
    private Skin skin;

    private Viewport vp;
    private Stage stage;

    private Image background;

    private Table mainTable;

    private Vector2 tmp = new Vector2();

    public MenuScreen(BeeRunner beeRunner) {
        this.beeRunner = beeRunner;
        skin = beeRunner.assetManager.get(Assets.skin);

        vp = new ScreenViewport();
        stage = new Stage(vp);


    }

    @Override
    public void show() {
        background = new Image(beeRunner.assetManager.get(Assets.background));
        background.setFillParent(true);
        stage.addActor(background);

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        mainTable.add(createButtonTable()).padTop(200).padLeft(100);

        Gdx.input.setInputProcessor(stage);
    }
    private Table createButtonTable() {
        Table buttonTable = new Table();

        TextButton playButton = createButton("Play", buttonTable);
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(beeRunner));
            }
        });

        //createButton("Options", buttonTable);
        TextButton creditsButton = createButton("Credits", buttonTable);
        creditsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new CreditsScreen(beeRunner));
            }
        });

        TextButton exitButton = createButton("Exit", buttonTable);
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        return buttonTable;
    }


    /**
     *
     * @param text text of button
     * @param buttonTable the table this gets added to
     * @return returns tyhe button to add aditional listeners
     */
    private TextButton createButton(String text, Table buttonTable){
        Action arrowAction = Actions.forever(
                Actions.sequence(
                        Actions.moveBy(50, 0, .4f, Interpolation.pow2Out),
                        Actions.moveBy(-50, 0, .4f, Interpolation.pow2In)
                ));
        Image arrow = new Image(skin.getDrawable("arrow"));
        arrow.setVisible(false);
        arrow.addAction(arrowAction);

        TextButton button = new TextButton(text, skin, "main");
        button.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                arrow.setVisible(true);
                beeRunner.assetManager.get(Assets.click).play(.5f);

            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                arrow.setVisible(false);
            }
        });
        buttonTable.add(button).pad(5);
        buttonTable.add(arrow).padLeft(5).row();

        return button;
    }


    @Override
    public void render(float delta) {
        stage.act();

        ScreenUtils.clear(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        vp.update(width, height);

    }

}
