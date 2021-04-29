package com.mygdx.game.Scenes;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.GameScreen;

public class WinMenu implements Screen {

    private GameScreen mainRenderScreen;
    SpriteBatch batch = new SpriteBatch();


    public WinMenu(GameScreen renderScreen){
        mainRenderScreen = renderScreen;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        // Background being drawn
        batch.begin();
        batch.draw(mainRenderScreen.getGameBackground(), 0, 0, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        mainRenderScreen.getNormalFont().draw(batch, "Final score: " + mainRenderScreen.getScore(), 630, 100);
        batch.end();


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
