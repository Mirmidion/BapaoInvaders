package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Entities.*;
import com.mygdx.game.GameScreen;

public class BossFight extends BaseScreen {
    private final Boss ufoBoss;
    private final PlayerBoss player;
    private int backgroundPosY;
    private boolean drawHitboxes = false;
    private final Texture gameOverTexture = new Texture("gameOver.png");
    private final Sprite gameOverSprite = new Sprite(gameOverTexture);
    private float gameOverSpriteY;

    public BossFight(GameScreen gameScreen) {
        batch = new SpriteBatch();
        mainRenderScreen = gameScreen;
        player = new PlayerBoss();
        ufoBoss = new Boss(player);
        player.setBoss(ufoBoss);
        gameOverSprite.setPosition(Gdx.graphics.getWidth()/2f - gameOverSprite.getWidth()/2, Gdx.graphics.getHeight());
        gameOverSpriteY = Gdx.graphics.getHeight();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.enableBlending();

        player.setAdvancedMovement(mainRenderScreen.getAdvancedMovement());

        batch.begin();

        drawScrollingBackground();

        player.draw(batch);

        ufoBoss.draw(batch);

        if (player.getHealth() == 0) {
            gameOverSprite.draw(batch);
        }

        batch.end();

        //player dead
        if (player.getHealth() == 0) {
            if (gameOverSpriteY > Gdx.graphics.getHeight() / 2f) {
                gameOverSpriteY -= delta * 400f;
                System.out.println(gameOverSpriteY);
                gameOverSprite.setY(gameOverSpriteY);
            } else {
                if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
                    this.dispose();
                    mainRenderScreen.setInBossScene(false);
                    mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
                }
            }

        } else {
            update(delta);
        }
    }

    private void update(float delta) {
        ufoBoss.update(delta);
        player.update(delta);
        drawHitboxes();
    }

    public void drawHitboxes() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            drawHitboxes = !drawHitboxes;
            player.setDrawHitboxes(drawHitboxes);
            Missile.setDrawHitboxes(drawHitboxes);
            BigLaser.setDrawHitboxes(drawHitboxes);
            Laser.setDrawHitboxes(drawHitboxes);
        }
    }

    public void drawScrollingBackground() {
        backgroundPosY -= 2;
        if (backgroundPosY % mainRenderScreen.getHeight() == 0) {
            backgroundPosY = 0;
        }

        batch.draw(mainRenderScreen.getGameBackground(), 0, backgroundPosY + mainRenderScreen.getHeight(), mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        batch.draw(mainRenderScreen.getGameBackground(), 0, backgroundPosY, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
    }

    @Override
    public void show() {

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
        batch.dispose();
    }
}
