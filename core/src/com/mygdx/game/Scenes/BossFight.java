package com.mygdx.game.Scenes;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Entities.*;
import com.mygdx.game.GameScreen;

public class BossFight extends BaseScreen {
    private final Boss ufoBoss;
    private final PlayerBoss player;
    private int backgroundPosY;
    private final Texture healthBar = new Texture("healthBar.png");

    public BossFight(GameScreen gameScreen) {
        mainRenderScreen = gameScreen;
        player = new PlayerBoss();
        shapeRenderer = mainRenderScreen.getShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        ufoBoss = new Boss(player);
        Planet.regenerateDefenses();
        batch = mainRenderScreen.getSpriteBatch();
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.enableBlending();
        ufoBoss.update(delta);
        player.setAdvancedMovement(mainRenderScreen.getAdvancedMovement());

        if (!batch.isDrawing()) {
            batch.begin();
        }

        drawScrollingBackground();

        if (player.getHealth() != 0) {
            player.draw(batch);
            player.update(delta);
            batch.draw(healthBar, 0, 0, player.getHealth() * 19.2f, 30);
        }

        ufoBoss.render(batch);

        batch.end();

    }

    public void drawScrollingBackground(){
        backgroundPosY -= 2;
        if (backgroundPosY % mainRenderScreen.getHeight() == 0) {
            backgroundPosY = 0;
        }

        batch.draw(mainRenderScreen.getGameBackground(), 0, backgroundPosY + mainRenderScreen.getHeight(), mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        batch.draw(mainRenderScreen.getGameBackground(), 0, backgroundPosY, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
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
