package net.madmenyo.beerunner.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


public class ProfilerOverlay extends Table {
    private GLProfiler profiler;

    private Label fpsLabel;
    private Label glCallsLabel;
    private Label glSwitches;
    private Label glBindsLabel;
    private Label glDrawCalls;

    private boolean profiling= true;

    public ProfilerOverlay(Skin skin) {
        super(skin);

        fpsLabel = new Label("", skin);
        glCallsLabel = new Label("", skin);
        glSwitches = new Label("", skin);
        glBindsLabel = new Label("", skin);
        glDrawCalls = new Label("", skin);

        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();

        createTable();
    }

    private void createTable() {
        left().bottom();
        add("FPS:");
        add(fpsLabel).row();
        add("GL Calls:");
        add(glCallsLabel).row();
        add("GL switches");
        add(glSwitches).row();
        add("GL Binds");
        add(glBindsLabel).row();
        add("GL Drawcalls");
        add(glDrawCalls).row();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        fpsLabel.setText(Gdx.graphics.getFramesPerSecond());
        glBindsLabel.setText(profiler.getTextureBindings());
        glSwitches.setText(profiler.getShaderSwitches());
        glCallsLabel.setText(profiler.getCalls());
        glDrawCalls.setText(profiler.getDrawCalls());

        profiler.reset();
    }

    public void setProfiling(boolean profiling) {
        this.profiling = profiling;

        if (profiling) profiler.enable();
        else profiler.disable();
    }
}
