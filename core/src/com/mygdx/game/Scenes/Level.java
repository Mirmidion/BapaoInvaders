package com.mygdx.game.Scenes;

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

public class Level implements Screen {

    //The spritebatch
    private final SpriteBatch batch;

    //The main gamescreen
    private final GameScreen mainRenderScreen;

    //Background position of the scrolling background
    private int backgroundPosY;

    //The player object
    private Player player;

    private int currentWaveOfPlanet = 1;

    private final Texture healthBar = new Texture("healthBar.png");


    public Level(GameScreen gameScreen) {
        this.mainRenderScreen = gameScreen;
        player = new Player(mainRenderScreen.getWidth());
        Planet.regenerateDefenses();
        batch = mainRenderScreen.getSpriteBatch();
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        SolarSystem solarSystem = mainRenderScreen.getSolarSystem();
        mainRenderScreen.getMusic().dispose();
        mainRenderScreen.getMusic2().dispose();
        mainRenderScreen.getMusic3().play();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (batch.isDrawing()) {
            batch.end();
        }
        batch.enableBlending();

        batch.begin();
        // Move the background down
        backgroundPosY -= (GameScreen.isPaused()) ? 2 : 0;
        if (backgroundPosY % mainRenderScreen.getHeight() == 0) {
            backgroundPosY = 0;
        }

        // Draw a background texture on the posY and posX, and one above that
        batch.draw(mainRenderScreen.getGameBackground(), 0, backgroundPosY + mainRenderScreen.getHeight(), mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        batch.draw(mainRenderScreen.getGameBackground(), 0, backgroundPosY, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());

        // Draw the player sprite with the correct position
        if (player.getHealth() != 0) {
            player.draw(batch);
            player.update(delta);
        } else {
            mainRenderScreen.getSolarSystem().setPlanetListOfDifficulty(new LinkedList<Planet>());
            solarSystem.setGlobalDifficulty(0);
            mainRenderScreen.setSolarSystem(new SolarSystem(mainRenderScreen.getWidth(), mainRenderScreen.getHeight()));
            mainRenderScreen.setCurrentScene(GameScreen.scene.gameOver);
            player = new Player(mainRenderScreen.getWidth());
            Bullet.setAllBullets(new ArrayList<Bullet>());
            Planet.regenerateDefenses();
        }

        for (Enemy enemy : mainRenderScreen.getCurrentPlanet().getEnemyWaves()) {
            enemy.update(player);
        }


            batch.draw(healthBar, 0, 0, player.getHealth() * 19.2f, 30);

            for (Iterator<Enemy> enemyIterator = mainRenderScreen.getCurrentPlanet().getEnemyWaves().iterator(); enemyIterator.hasNext(); ) {
                Enemy enemy = enemyIterator.next();
                if (enemy.getSprite() == null) {
                    enemy.refreshTextures();
                }
                if (enemy.getHealth() != 0) {

                    batch.draw(enemy.getSprite(), enemy.getPosX(), enemy.getPosY());
                    batch.draw(healthBar, enemy.getPosX() + (enemy.getSprite().getWidth() - 80f) / 2f, enemy.getPosY() - 10, 80f * enemy.getHealth() / enemy.getMaxHealth(), 10);
                    //mainRenderScreen.getNormalFont().draw(batch, enemy.getCurrentStateName(),enemy.getPosX()+20, enemy.getPosY()+enemy.getSprite().getHeight()+10);

                    for (Iterator<Bullet> bulletIterator = Bullet.getAllBullets().iterator(); bulletIterator.hasNext(); ) {
                        Bullet bullet = bulletIterator.next();

                        Rectangle enemyRectangle = new Rectangle((int) enemy.getPosX(), (int) enemy.getPosY(), 140, enemy.getSprite().getHeight());
                        Rectangle playerRectangle = new Rectangle((int) player.getPosX(), (int) player.getPosY(), 140, player.getSprite().getHeight());
                        Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), bullet.getLaser().getWidth(), bullet.getLaser().getHeight());

                        if (player.getHealth() != 0 && overlaps(playerRectangle, bulletRectangle) && !bullet.getFriendly() && !player.isInvulnerable()) {
                            player.setInvulnerable(true);
                            player.setHealth(bullet.getDamage() * -1);
                            bulletIterator.remove();
                        } else if (player.getHealth() != 0 && overlaps(playerRectangle, bulletRectangle) && !bullet.getFriendly() && player.isInvulnerable()) {
                            bulletIterator.remove();
                        } else if (enemy.getHealth() != 0 && overlaps(bulletRectangle, enemyRectangle) && bullet.getFriendly() && enemy.isInvulnerable()) {
                            enemy.setHealth(bullet.getDamage() * -1);
                            enemy.setGotHit();
                            bulletIterator.remove();
                        } else if (enemy.getHealth() <= 0) {
                            mainRenderScreen.setScore(50 * enemy.getEnemyClass());
                            break;
                        }
                    }
                } else {
                    enemyIterator.remove();
                }
            }


            if (mainRenderScreen.getCurrentPlanet().getEnemyWaves().size() == 0 && mainRenderScreen.getCurrentPlanet().getWaves().size() != currentWaveOfPlanet + 1) {
                currentWaveOfPlanet++;
                mainRenderScreen.getCurrentPlanet().generateEnemies(currentWaveOfPlanet);
            } else if (mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().size() == 0 && Gdx.input.isKeyPressed(Input.Keys.R)) {
                mainRenderScreen.setCurrentScene(GameScreen.scene.win);
            } else if ((mainRenderScreen.getCurrentPlanet().getWaves().size() == currentWaveOfPlanet + 1 || Gdx.input.isKeyPressed(Input.Keys.R)) || mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().size() == 0) {
                mainRenderScreen.getCurrentPlanet().setEnemyWaves(new ArrayList<Enemy>());
                mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().poll();
                if (mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().peek() == null) {
                    mainRenderScreen.setCurrentScene(GameScreen.scene.win);
                } else {
                    mainRenderScreen.setCurrentScene(GameScreen.scene.map);
                }
                player.resetPosition(1920);
                Planet.regenerateDefenses();
                Bullet.setAllBullets(new ArrayList<Bullet>());
                mainRenderScreen.saveSaveGame(MainMenu.getSelectedSaveGame());
            }

            // Draw every bullet and move them up/down
            for (Iterator<Bullet> iter = Bullet.getAllBullets().iterator(); iter.hasNext(); ) {
                Bullet bullet = iter.next();
                batch.draw(bullet.getLaser(), bullet.getPosX(), bullet.getPosY());
                if (GameScreen.isPaused()) {
                    bullet.setPosY(3f);
                }
                if (bullet.getPosX() > 1920 || bullet.getPosY() > 1080) {
                    iter.remove();
                }
            }

            for (Planet planet : mainRenderScreen.getSolarSystem().getPlanets()) {
                if (planet.getDifficulty() == solarSystem.getGlobalDifficulty()) {
                    mainRenderScreen.setCurrentPlanet(planet);
                }
            }

            // draw all defenses present
            /*for (Iterator<Defense> iter = Planet.getDefenses().iterator(); iter.hasNext(); ) {
                Defense defense = iter.next();
                Rectangle defenseRectangle = new Rectangle(defense.getPosX(), defense.getPosY(), defense.getTexture().getWidth(), defense.getTexture().getHeight());
                for (Iterator<Bullet> iter2 = Bullet.getAllBullets().iterator(); iter2.hasNext(); ) {
                    Bullet bullet = iter2.next();
                    Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), (bullet.getLaser().getWidth()), bullet.getLaser().getHeight());
                    if (defense.getHealth() != 0 && bullet.isExists() && overlaps(defenseRectangle, bulletRectangle)) {
                        iter2.remove();
                        defense.setHealth(-50);
                    }
                }
                if (defense.getHealth() > 0) {
                    batch.draw(defense.getTexture(), defense.getPosX(), defense.getPosY());
                } else {
                    iter.remove();
                }
            }*/


            // Change the position of the player depending on the keys pressed
            if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || mainRenderScreen.getRasp().is_pressed("left") || mainRenderScreen.getArduino().is_pressed("left")) && GameScreen.isPaused()) {
                player.setPosX(-2, mainRenderScreen.getWidth());
            } else if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || mainRenderScreen.getRasp().is_pressed("right") || mainRenderScreen.getArduino().is_pressed("right")) && GameScreen.isPaused()) {
                player.setPosX(2, mainRenderScreen.getWidth());
            }
            if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) || mainRenderScreen.getRasp().is_pressed("up") || mainRenderScreen.getArduino().is_pressed("up")) && GameScreen.isPaused()) {
                player.shoot();
            }

            batch.end();


            // Draw the current score
            batch.begin();
            mainRenderScreen.getTitleFont().getData().setScale(1f);
            mainRenderScreen.getTitleFont().draw(batch, "Score: " + mainRenderScreen.getScore(), 80, 1000);
            mainRenderScreen.getTitleFont().getData().setScale(2f);
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

    public boolean overlaps(Rectangle r, Rectangle r2) {
        return (r2.x < r.x + r.width && r2.x + r2.width > r.x && r2.y < r.y + r.height && r2.y + r2.height > r.y);
    }

    public void checkCollisions(){
        for (Iterator<Defense> iter = Planet.getDefenses().iterator(); iter.hasNext(); ) {
            Defense defense = iter.next();
            Rectangle defenseRectangle = new Rectangle(defense.getPosX(), defense.getPosY(), defense.getTexture().getWidth(), defense.getTexture().getHeight());
            for (Iterator<Bullet> iter2 = Bullet.getAllBullets().iterator(); iter2.hasNext(); ) {
                Bullet bullet = iter2.next();
                Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), (bullet.getLaser().getWidth()), bullet.getLaser().getHeight());
                if (defense.getHealth() != 0 && overlaps(defenseRectangle, bulletRectangle)) {
                    iter2.remove();
                    defense.setHealth(-50);
                }
            }

            if (defense.getHealth() <= 0){
                iter.remove();
            }
        }
    }

    public void updateEntities(){
        for (Defense defense : Planet.getDefenses()){
            if (defense.getHealth() > 0) {
                batch.draw(defense.getTexture(), defense.getPosX(), defense.getPosY());
            }
        }


    }

}
