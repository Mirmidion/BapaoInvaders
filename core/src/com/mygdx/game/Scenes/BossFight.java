package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Entities.*;
import com.mygdx.game.GameScreen;
import org.w3c.dom.Text;

public class BossFight extends BaseScreen {
    private final Boss ufoBoss;
    private final PlayerBoss player;
    private int backgroundPosY;
    private boolean drawHitboxes = false;
    private final Texture gameOverTexture = new Texture("gameOver.png");
    private final Sprite gameOverSprite = new Sprite(gameOverTexture);
    private float gameOverSpriteY;
    private final Texture pressEscTexture = new Texture("press_esc.png");
    private final Sprite pressEscSprite = new Sprite(pressEscTexture);
    private float textTimer = 0;
    private boolean textAlpha = false;
    private boolean paused = false;
    private final Texture pauseTexture = new Texture("pause.png");
    private final Sprite pauseSprite = new Sprite(pauseTexture);

    public BossFight(GameScreen gameScreen) {
        batch = new SpriteBatch();
        mainRenderScreen = gameScreen;
        player = new PlayerBoss();
        ufoBoss = new Boss(player);
        player.setBoss(ufoBoss);
        gameOverSprite.setPosition(Gdx.graphics.getWidth() / 2f - gameOverSprite.getWidth() / 2, Gdx.graphics.getHeight() - 40f);
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
            if (gameOverSpriteY == Gdx.graphics.getHeight() / 2f) {
                pressEscSprite.draw(batch);
            }
        }

        if (paused) {
            pauseSprite.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 1.5f);
            pauseSprite.draw(batch);
            pressEscSprite.draw(batch);
        }

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;
        }

        if (player.getHealth() == 0) {
            gameOver(delta);
        } else if (paused) {
            textTimer += delta;
            blinkEsc();
            returnMainMenu();
        } else {
            update(delta);
        }
    }

    private void gameOver(float delta) {
        textTimer += delta;

        if (gameOverSpriteY > Gdx.graphics.getHeight() / 2f) {
            gameOverSpriteY -= delta * 400f;
            System.out.println(gameOverSpriteY);
            if (gameOverSpriteY - Gdx.graphics.getHeight() / 2f <= 10) {
                gameOverSpriteY = Gdx.graphics.getHeight() / 2f;
            }
            gameOverSprite.setY(gameOverSpriteY);
        } else {
            blinkEsc();
            returnMainMenu();
        }
    }

    private void blinkEsc() {
        float TEXT_BLINK = 1f;

        if (player.getHealth() == 0) {
            pressEscSprite.setPosition(Gdx.graphics.getWidth() / 2f - pressEscSprite.getWidth() / 2,
                    gameOverSpriteY - gameOverSprite.getHeight() / 2f - 20f);
        } else if (paused){
            pressEscSprite.setPosition(Gdx.graphics.getWidth() / 2f - pressEscSprite.getWidth() / 2,
                     Gdx.graphics.getHeight() / 2f - 20f);
        }

        if (textTimer >= TEXT_BLINK) {
            textTimer = 0;
            textAlpha = !textAlpha;
            if (textAlpha) {
                pressEscSprite.setAlpha(1f);
            } else {
                pressEscSprite.setAlpha(0);
            }
        }
    }

    private void returnMainMenu() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            this.dispose();
            mainRenderScreen.setInBossScene(false);
            mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
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
