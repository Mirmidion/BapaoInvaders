package com.mygdx.game.Scenes;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Entities.*;
import com.mygdx.game.GameScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Level  implements Screen {

    SpriteBatch batch = new SpriteBatch();
    GameScreen mainRenderScreen;
    private int backgroundPosY;
    private Player player;


    public Level(GameScreen gameScreen){
        this.mainRenderScreen = gameScreen;
        player = new Player(mainRenderScreen.getWidth());
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        mainRenderScreen.getMusic().dispose();
        mainRenderScreen.getMusic2().dispose();
        mainRenderScreen.getMusic3().play();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();


        // Move the background down
        backgroundPosY -= (!GameScreen.isPaused()) ? 2 : 0;
        if (backgroundPosY % mainRenderScreen.getHeight() == 0) {
            backgroundPosY = 0;
        }

        // Draw a background texture on the posY and posX, and one above that
        batch.draw(mainRenderScreen.getGameBackground(), 0, backgroundPosY + mainRenderScreen.getHeight(), mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        batch.draw(mainRenderScreen.getGameBackground(), 0, backgroundPosY, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());

        // Draw the player sprite with the correct position
        if (player.getHealth() != 0) {
            batch.draw(player.getPlayerSprite(), player.getPosX(), player.getPosY());
        } else {
            Planet.setPlanetListOfDifficulty(new LinkedList<Planet>());
            Planet.setGlobalDifficulty(0);
            mainRenderScreen.setCurrentPlanet(null);
            mainRenderScreen.setSolarSystem(new SolarSystem(mainRenderScreen.getWidth(), mainRenderScreen.getHeight()));
            mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
            player = new Player(mainRenderScreen.getWidth());
            Bullet.setAllBullets(new ArrayList<Bullet>());
            Planet.setDefenses(new Defense[]{new Defense(233, 300), new Defense(570, 300), new Defense(904, 300), new Defense(1241, 300), new Defense(1578, 300)});
        }
        if (mainRenderScreen.getCurrentPlanet() != null) {
            batch.draw(new Texture("healthBar.png"), 0, 0, player.getHealth() * 19.2f, 30);

            for (Iterator<Enemy> enemyIterator = mainRenderScreen.getCurrentPlanet().getEnemyWaves().iterator(); enemyIterator.hasNext(); ) {
                Enemy enemy = enemyIterator.next();
                System.out.println(enemy.getPosY() + "\t" + enemy.getPosX());
                if (enemy.getHealth() != 0) {
                    enemy.moveEnemy();
                    batch.draw(enemy.getEnemySprite(), enemy.getPosX(), enemy.getPosY());
                    for (Iterator<Bullet> bulletIterator = Bullet.getAllBullets().iterator(); bulletIterator.hasNext(); ) {
                        Bullet bullet = bulletIterator.next();
                        Rectangle enemyRectangle = new Rectangle((int) enemy.getPosX(), (int) enemy.getPosY(), 140, enemy.getEnemySprite().getHeight());
                        Rectangle playerRectangle = new Rectangle(player.getPosX(), player.getPosY(), 140, player.getPlayerSprite().getHeight());
                        Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), bullet.getLaser().getWidth(), bullet.getLaser().getHeight());
                        if (player.getHealth() != 0 && bullet.isExists() && overlaps(playerRectangle, bulletRectangle) && !bullet.getFriendly()) {
                            bullet.setExists(false);
                            player.setHealth(-25);
                            bulletIterator.remove();
                        } else if (enemy.getHealth() != 0 && bullet.isExists() && overlaps(bulletRectangle, enemyRectangle) && bullet.getFriendly()) {
                            bullet.setExists(false);
                            enemy.setHealth(-50);
                            bulletIterator.remove();
                            GameScreen.setScore(50);
                        }
                    }
                } else {
                    enemyIterator.remove();
                }
            }

            if (mainRenderScreen.getCurrentPlanet().getEnemyWaves().size() == 0) {
                mainRenderScreen.addLevel();
                mainRenderScreen.setCurrentScene(GameScreen.scene.map);
                Bullet.setAllBullets(new ArrayList<Bullet>());
                player.resetPosition(mainRenderScreen.getWidth());
                System.out.println(mainRenderScreen.getLevel());
            }

            // Draw every bullet and move them up/down
            for (Bullet bullet : Bullet.getAllBullets()) {
                if (bullet.isExists()) {
                    batch.draw(bullet.getLaser(), bullet.getPosX(), bullet.getPosY());
                }
                if (!GameScreen.isPaused()) {
                    bullet.setPosY(5f, mainRenderScreen.getHeight());
                }
            }

            for (Planet planet : mainRenderScreen.getSolarSystem().getPlanets()) {
                if (planet.getDifficulty() == Planet.getGlobalDifficulty()) {
                    mainRenderScreen.setCurrentPlanet(planet);
                }
            }

            // draw all defenses present
            int defenseCount = 0;
            for (Defense defense : Planet.getDefenses()) {
                Rectangle defenseRectangle = new Rectangle(defense.getPosX(), defense.getPosY(), defense.getTexture().getWidth(), defense.getTexture().getHeight());
                for (Bullet bullet : Bullet.getAllBullets()) {
                    Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), (bullet.getLaser().getWidth()), (int) bullet.getLaser().getHeight());
                    if (defense.getHealth() != 0 && bullet.isExists() && overlaps(defenseRectangle, bulletRectangle)) {
                        bullet.setExists(false);
                        defense.setHealth(-50);
                    }
                }
                if (defense.getHealth() > 0) {
                    batch.draw(defense.getTexture(), defense.getPosX(), defense.getPosY());
                }
                defenseCount++;
            }


            // Change the position of the player depending on the keys pressed
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !GameScreen.isPaused()) {
                player.setPosX(-2, mainRenderScreen.getWidth());
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !GameScreen.isPaused()) {
                player.setPosX(2, mainRenderScreen.getWidth());
            }
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !GameScreen.isPaused()) {
                player.shoot();
            }

            batch.end();


            // Enable using the ESCAPE KEY to pause the scene. Supported scenes for pausing are: level
            if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && TimeUtils.millis() - mainRenderScreen.getPauseDelay() > 500) {
                if (!GameScreen.isPaused()) {
                    GameScreen.setPaused(true);
                } else {
                    GameScreen.setPaused(false);
                }
                mainRenderScreen.setPauseDelay(TimeUtils.millis());

            }
        }
    }
            @Override
            public void resize ( int width, int height){

            }

            @Override
            public void pause () {

            }

            @Override
            public void resume () {

            }

            @Override
            public void hide () {

            }

            @Override
            public void dispose () {

            }

            public boolean overlaps (Rectangle r, Rectangle r2){
                return (r2.x < r.x + r.width && r2.x + r2.width > r.x && r2.y < r.y + r.height && r2.y + r2.height > r.y);
            }

}
