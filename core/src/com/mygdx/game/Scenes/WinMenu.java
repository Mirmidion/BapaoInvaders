package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.game.GameScreen;

import java.awt.*;

public class WinMenu implements Screen {

    private GameScreen mainRenderScreen;
    SpriteBatch batch = new SpriteBatch();

    TextButton tryAgain = new TextButton("Try Again", MainMenu.getButtonSkin());

    private int score = 0;
    BitmapFont normalFont;


    public WinMenu(GameScreen renderScreen){
        mainRenderScreen = renderScreen;
        normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        try {
            score = mainRenderScreen.getScore();
        }
        catch (Exception e){
            score = 0;
        }

        // Background being drawn
        batch.begin();
        batch.draw(mainRenderScreen.getGameBackground(), 0, 0, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        normalFont.draw(batch, "Final score: " + score, 630, 100);
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
