package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.GameScreen;

public class loadingScreen implements Screen {

    private final GameScreen mainRenderScreen;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    private final AssetManager manager = new AssetManager();
    float progress;
    long previousTime;


    public loadingScreen (GameScreen renderScreen){
        mainRenderScreen = renderScreen;
        previousTime = TimeUtils.millis();
        batch = mainRenderScreen.getSpriteBatch();
        shapeRenderer = mainRenderScreen.getShapeRenderer();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        mainRenderScreen.getMusic().play();
        mainRenderScreen.getMusic2().dispose();
        mainRenderScreen.getMusic3().dispose();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(mainRenderScreen.getGameBackground(), 0, 0, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        batch.end();


        if (manager.update() && TimeUtils.millis() - previousTime > 5000){
            mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
        }

        progress = manager.getProgress();

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GOLD);
        if (progress > ((TimeUtils.millis() - previousTime)/ 5000f)) {
            shapeRenderer.rect(300, 300, 1320 * ((TimeUtils.millis() - previousTime) / 5000f), 50);
        }
        else{
            shapeRenderer.rect(300, 300, 1320 * progress, 50);
        }
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
