package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import com.mygdx.game.Entities.*;
import com.mygdx.game.GameScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Level extends BaseScreen {

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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!batch.isDrawing()) {
            batch.begin();
        }
        batch.enableBlending();

        drawScrollingBackground();

        // Draw the player sprite with the correct position
        if (player.getHealth() != 0) {
            player.draw(batch);
            player.update(delta);
            batch.draw(healthBar, 0, 0, player.getHealth() * 19.2f, 30);
        }

        for (Enemy enemy : mainRenderScreen.getCurrentPlanet().getEnemyWaves()) {
            enemy.update(player);
        }

        levelEndCheck();

        for (Planet planet : mainRenderScreen.getSolarSystem().getPlanets()) {
            if (planet.getDifficulty() == mainRenderScreen.getSolarSystem().getGlobalDifficulty()) {
                mainRenderScreen.setCurrentPlanet(planet);
            }
        }

        checkCollisions();
        updateEntities();
        handleInput();

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
            player.setPosX(-2, mainRenderScreen.getWidth());
        } else if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || raspRightPressed || ardRightPressed)) {
            player.setPosX(2, mainRenderScreen.getWidth());
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) || raspUpPressed || ardUpPressed)) {
            player.shoot();
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
                    defense.setHealth(-50);
                }
            }

            if (defense.getHealth() <= 0) {
                iter.remove();
            }
        }

        for (Iterator<Enemy> enemyIterator = mainRenderScreen.getCurrentPlanet().getEnemyWaves().iterator(); enemyIterator.hasNext(); ) {
            Enemy enemy = enemyIterator.next();
            if (enemy.getHealth() != 0) {
                for (Iterator<Bullet> bulletIterator = Bullet.getAllBullets().iterator(); bulletIterator.hasNext(); ) {
                    Bullet bullet = bulletIterator.next();

                    Rectangle enemyRectangle = new Rectangle((int) enemy.getPosX(), (int) enemy.getPosY(), 140, enemy.getSprite().getHeight());
                    Rectangle playerRectangle = new Rectangle((int) player.getPosX(), (int) player.getPosY(), 140, player.getSprite().getHeight());
                    Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), bullet.getLaser().getWidth(), bullet.getLaser().getHeight());

                    boolean bulletIsFriendly = bullet.getFriendly();

                    if (overlaps(playerRectangle, bulletRectangle) && !bulletIsFriendly && !player.isInvulnerable()) {
                        player.setInvulnerable(true);
                        player.setHealth(bullet.getDamage() * -1);
                        bulletIterator.remove();
                    } else if (overlaps(playerRectangle, bulletRectangle) && !bulletIsFriendly && player.isInvulnerable()) {
                        bulletIterator.remove();
                    } else if (overlaps(bulletRectangle, enemyRectangle) && bulletIsFriendly && enemy.isInvulnerable()) {
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

        //Enemies
        for (Enemy enemy : mainRenderScreen.getCurrentPlanet().getEnemyWaves()) {
            if (enemy.getSprite() == null) {
                enemy.refreshTextures();
            }
            if (enemy.getHealth() != 0) {
                float healthBarPosX = enemy.getPosX() + (enemy.getSprite().getWidth() - 80f) / 2f;
                float healthBarPosY = enemy.getPosY() - 10;
                float healthBarWidth = 80f * enemy.getHealth() / enemy.getMaxHealth();

                batch.draw(enemy.getSprite(), enemy.getPosX(), enemy.getPosY());
                batch.draw(healthBar, healthBarPosX, healthBarPosY, healthBarWidth, 10);
            }
        }
    }

    public void levelEndCheck() {

        boolean rKeyIsPressed = Gdx.input.isKeyPressed(Input.Keys.R);
        boolean noLevelsRemaining = mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().size() == 0;
        boolean WavesRemaining = mainRenderScreen.getCurrentPlanet().getWaves().size() != currentWaveOfPlanet + 1;
        boolean noEnemiesAlive = mainRenderScreen.getCurrentPlanet().getEnemyWaves().size() == 0;
        boolean playerIsDead = player.getHealth() <= 0;

        if (playerIsDead){
            mainRenderScreen.setSolarSystem(new SolarSystem(mainRenderScreen.getWidth(), mainRenderScreen.getHeight()));
            mainRenderScreen.setCurrentScene(GameScreen.scene.gameOver);
            GameOverMenu.setPrevPress();
            player = new Player(mainRenderScreen.getWidth());
            Bullet.removeAllBullets();
            Planet.regenerateDefenses();
        }
        else if (noEnemiesAlive && WavesRemaining) {
            currentWaveOfPlanet++;
            mainRenderScreen.getCurrentPlanet().generateEnemies(currentWaveOfPlanet);
        } else if (!WavesRemaining || rKeyIsPressed || noLevelsRemaining) {
            mainRenderScreen.getCurrentPlanet().setEnemyWaves(new ArrayList<Enemy>());
            mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().poll();
            if (noLevelsRemaining) {
                mainRenderScreen.setCurrentScene(GameScreen.scene.win);
            } else {
                mainRenderScreen.setCurrentScene(GameScreen.scene.map);
            }
            player.resetPosition(1920);
            Planet.regenerateDefenses();
            Bullet.removeAllBullets();
            mainRenderScreen.saveSaveGame(MainMenu.getSelectedSaveGame());
        }
    }
}
