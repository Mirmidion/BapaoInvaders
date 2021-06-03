package com.mygdx.game.Scenes;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Entities.*;
import com.mygdx.game.GameScreen;

import java.awt.*;
import java.util.Iterator;

public class BossFight extends BaseScreen {
    private Boss ufoBoss;
    private Player player;
    private int backgroundPosY;
    private final Texture healthBar = new Texture("healthBar.png");

    public BossFight(GameScreen gameScreen) {
        this.mainRenderScreen = gameScreen;
        player = new Player(mainRenderScreen.getWidth());
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
        ufoBoss.update(delta);

        if (!batch.isDrawing()) {
            batch.begin();
        }
        batch.enableBlending();

        drawScrollingBackground();

        if (player.getHealth() != 0) {
            player.draw(batch);
            player.update(delta);
            batch.draw(healthBar, 0, 0, player.getHealth() * 19.2f, 30);
        }

        ufoBoss.render(batch);

        mainRenderScreen.getTitleFont().getData().setScale(1f);
        mainRenderScreen.getTitleFont().draw(batch, "Score: " + mainRenderScreen.getScore(), 80, 1000);
        mainRenderScreen.getTitleFont().getData().setScale(2f);


        checkCollisions();
        updateEntities();
        handleInput();
        batch.end();
    }

    public boolean overlaps(Rectangle r, Rectangle r2) {
        return (r2.x < r.x + r.width && r2.x + r2.width > r.x && r2.y < r.y + r.height && r2.y + r2.height > r.y);
    }

    public void drawScrollingBackground(){
        backgroundPosY -= 2;
        if (backgroundPosY % mainRenderScreen.getHeight() == 0) {
            backgroundPosY = 0;
        }

        batch.draw(mainRenderScreen.getGameBackground(), 0, backgroundPosY + mainRenderScreen.getHeight(), mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        batch.draw(mainRenderScreen.getGameBackground(), 0, backgroundPosY, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
    }

    public void handleInput(){
        boolean raspLeftPressed = mainRenderScreen.getRasp().is_pressed("left");
        boolean raspRightPressed = mainRenderScreen.getRasp().is_pressed("right");
        boolean raspUpPressed = mainRenderScreen.getRasp().is_pressed("up");

        boolean ardLeftPressed = mainRenderScreen.getArduino().is_pressed("left");
        boolean ardRightPressed = mainRenderScreen.getArduino().is_pressed("right");
        boolean ardUpPressed = mainRenderScreen.getArduino().is_pressed("up");

        // Change the position of the player depending on the keys pressed
        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || raspLeftPressed || ardLeftPressed)) {
            player.setPosX(-5, mainRenderScreen.getWidth());
        } else if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || raspRightPressed || ardRightPressed)) {
            player.setPosX(5, mainRenderScreen.getWidth());
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) || raspUpPressed || ardUpPressed)) {
            player.shoot();
        }
    }

    public void updateEntities() {
        //Defenses
        for (Defense defense : Planet.getDefenses()) {
            if (defense.getHealth() > 0) {
                batch.draw(defense.getSprite(), defense.getPosX(), defense.getPosY());
            }
        }

        //Bullets
        for (Iterator<Bullet> iter = Bullet.getAllBullets().iterator(); iter.hasNext(); ) {
            Bullet bullet = iter.next();
            batch.draw(bullet.getLaser(), bullet.getPosX(), bullet.getPosY());
            bullet.setPosY(3f);
            if (bullet.getPosX() > 1920 || bullet.getPosY() > 1080) {
                iter.remove();
            }
        }
    }

    public void checkCollisions() {
        for (Iterator<Defense> iter = Planet.getDefenses().iterator(); iter.hasNext(); ) {
            Defense defense = iter.next();
            Rectangle defenseRectangle = new Rectangle(Math.round(defense.getPosX()), Math.round(defense.getPosY()), defense.getSprite().getWidth(), defense.getSprite().getHeight());
            for (Iterator<Bullet> iter2 = Bullet.getAllBullets().iterator(); iter2.hasNext(); ) {
                Bullet bullet = iter2.next();
                Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), (bullet.getLaser().getWidth()), bullet.getLaser().getHeight());
                if (defense.getHealth() != 0 && overlaps(defenseRectangle, bulletRectangle)) {
                    iter2.remove();
                }
            }

            if (defense.getHealth() <= 0) {
                iter.remove();
            }
        }
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
