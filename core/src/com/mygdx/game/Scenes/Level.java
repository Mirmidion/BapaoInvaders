package com.mygdx.game.Scenes;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import com.badlogic.gdx.utils.TimeUtils;

//import com.mygdx.game.Controllers.Arduino;
import com.mygdx.game.Controllers.Arduino;
import com.mygdx.game.Controllers.raspController.RaspController;

import com.mygdx.game.Entities.*;
import com.mygdx.game.GameScreen;
import org.w3c.dom.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class Level implements Screen {

    //The spritebatch
    SpriteBatch batch;

    //The main gamescreen
    GameScreen mainRenderScreen;

    //Background position of the scrolling background
    private int backgroundPosY;

    //The player object
    private Player player;

    private int currentWaveOfPlanet = 1;

    private SolarSystem solarSystem;
    private Texture healthBar = new Texture("healthBar.png");


    private RaspController rasp;
    private Arduino arduino;


    public Level(GameScreen gameScreen){
        rasp = new RaspController("192.168.137.54");
        arduino = new Arduino();
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
        solarSystem = mainRenderScreen.getSolarSystem();
        mainRenderScreen.getMusic().dispose();
        mainRenderScreen.getMusic2().dispose();
        mainRenderScreen.getMusic3().play();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        if (batch.isDrawing()){
            batch.end();
        }


        batch.enableBlending();


        batch.begin();
        System.out.println(mainRenderScreen.getCurrentPlanet().getWaves());


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

            //player.draw(batch);
            //player.update(delta);

            batch.draw(player.getSprite(), player.getPosX(), player.getPosY());

        } else {
            mainRenderScreen.getSolarSystem().setPlanetListOfDifficulty(new LinkedList<Planet>());
            solarSystem.setGlobalDifficulty(0);
            mainRenderScreen.setSolarSystem(new SolarSystem(mainRenderScreen.getWidth(), mainRenderScreen.getHeight()));
            mainRenderScreen.setCurrentScene(GameScreen.scene.gameOver);
            player = new Player(mainRenderScreen.getWidth());
            Bullet.setAllBullets(new ArrayList<Bullet>());
            Planet.regenerateDefenses();
        }

        for (Enemy enemy : mainRenderScreen.getCurrentPlanet().getEnemyWaves()){
            if (enemy.getSprite() == null){
                enemy.refreshTextures();
            }
            //System.out.println(enemy.getPosX() + "\n" + enemy.getPosY() );
            //System.out.println(enemy.getTargetX() + "\n" + enemy.getTargetY() + "\n");
            enemy.update(player);
        }
        System.out.println("");

        if (mainRenderScreen.getCurrentPlanet() != null) {
            batch.draw(healthBar, 0, 0, player.getHealth() * 19.2f, 30);

            for (Iterator<Enemy> enemyIterator = mainRenderScreen.getCurrentPlanet().getEnemyWaves().iterator(); enemyIterator.hasNext(); ) {
                Enemy enemy = enemyIterator.next();
                if (enemy.getSprite() == null){
                    enemy.refreshTextures();
                }
                if (enemy.getHealth() != 0) {

                    batch.draw(enemy.getSprite(), enemy.getPosX(), enemy.getPosY());
                    batch.draw(healthBar,enemy.getPosX() + (enemy.getSprite().getWidth()-80f)/2f, enemy.getPosY()-10, 80f * enemy.getHealth()/enemy.getMaxHealth(), 10 );
                    mainRenderScreen.getNormalFont().draw(batch, enemy.getCurrentStateName(),enemy.getPosX()+20, enemy.getPosY()+enemy.getSprite().getHeight()+10);

                    for (Iterator<Bullet> bulletIterator = Bullet.getAllBullets().iterator(); bulletIterator.hasNext(); ) {
                        Bullet bullet = bulletIterator.next();

                        Rectangle enemyRectangle = new Rectangle((int) enemy.getPosX(), (int) enemy.getPosY(), 140, enemy.getSprite().getHeight());
                        Rectangle playerRectangle = new Rectangle((int)player.getPosX(), (int)player.getPosY(), 140, player.getSprite().getHeight());

                        Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), bullet.getLaser().getWidth(), bullet.getLaser().getHeight());
                        if (player.getHealth() != 0 && bullet.isExists() && overlaps(playerRectangle, bulletRectangle) && !bullet.getFriendly() && !player.isInvulnerable()) {
                            bullet.setExists(false);
                            player.setInvulnerable(true);
                            player.setHealth(-25);
                            bulletIterator.remove();
                        }
                        else if (player.getHealth() != 0 && bullet.isExists() && overlaps(playerRectangle, bulletRectangle) && !bullet.getFriendly() && player.isInvulnerable()) {
                            bullet.setExists(false);
                            bulletIterator.remove();
                        }
                        else if (enemy.getHealth() != 0 && bullet.isExists() && overlaps(bulletRectangle, enemyRectangle) && bullet.getFriendly() && enemy.isInvulnerable()) {

                            bullet.setExists(false);
                            enemy.setHealth(-50);
                            enemy.setGotHit();
                            bulletIterator.remove();
                        }
                        else if (enemy.getHealth() <= 0){
                            mainRenderScreen.setScore(50*enemy.getEnemyClass());
                            break;
                        }
                    }
                } else {
                    enemyIterator.remove();
                }
            }


            if (mainRenderScreen.getCurrentPlanet().getEnemyWaves().size() == 0 && mainRenderScreen.getCurrentPlanet().getWaves().size() != currentWaveOfPlanet+1){
                currentWaveOfPlanet++;
                mainRenderScreen.getCurrentPlanet().generateEnemies(currentWaveOfPlanet);
            }
            else if (mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().size() == 0 && Gdx.input.isKeyPressed(Input.Keys.R)){
                mainRenderScreen.setCurrentScene(GameScreen.scene.win);
            }
            else if ((mainRenderScreen.getCurrentPlanet().getWaves().size() == currentWaveOfPlanet+1 || Gdx.input.isKeyPressed(Input.Keys.R)) || mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().size() == 0){
                mainRenderScreen.getCurrentPlanet().setEnemyWaves(new ArrayList<Enemy>());
                mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().poll();
                if (mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().peek() == null){
                    mainRenderScreen.setCurrentScene(GameScreen.scene.win);
                }
                else {
                    mainRenderScreen.setCurrentScene(GameScreen.scene.map);
                }
                player.resetPosition(1920);
                Planet.regenerateDefenses();
                Bullet.setAllBullets(new ArrayList<Bullet>());
                //System.out.println(mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().size());
                //System.out.println("jup");
                solarSystem.setCurrentlevel(1);
                mainRenderScreen.saveSaveGame(MainMenu.getSelectedSaveGame());
            }


            if (mainRenderScreen.getCurrentPlanet().getEnemyWaves().size() == 0) {
                mainRenderScreen.addLevel();
                mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().poll();
                mainRenderScreen.setCurrentScene(GameScreen.scene.map);
                Bullet.setAllBullets(new ArrayList<Bullet>());
                player.resetPosition(mainRenderScreen.getWidth());
                mainRenderScreen.saveSaveGame(MainMenu.getSelectedSaveGame());
            }

            // Draw every bullet and move them up/down
            for (Iterator<Bullet> iter = Bullet.getAllBullets().iterator(); iter.hasNext();) {
                Bullet bullet = iter.next();
                if (bullet.isExists()) {
                    batch.draw(bullet.getLaser(), bullet.getPosX(), bullet.getPosY());
                }
                if (!GameScreen.isPaused()) {
                    bullet.setPosY(3f, mainRenderScreen.getHeight());
                }
                if (bullet.getPosX() > 1920 || bullet.getPosY() > 1080){
                    iter.remove();
                }
            }

            for (Planet planet : mainRenderScreen.getSolarSystem().getPlanets()) {
                if (planet.getDifficulty() == solarSystem.getGlobalDifficulty()) {
                    mainRenderScreen.setCurrentPlanet(planet);
                }
            }

            // draw all defenses present
            int defenseCount = 0;
            for (Iterator<Defense> iter = Planet.getDefenses().iterator(); iter.hasNext(); ) {
                Defense defense = iter.next();
                Rectangle defenseRectangle = new Rectangle(defense.getPosX(), defense.getPosY(), defense.getTexture().getWidth(), defense.getTexture().getHeight());
                for (Iterator<Bullet> iter2 = Bullet.getAllBullets().iterator(); iter2.hasNext();) {
                    Bullet bullet = iter2.next();
                    Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), (bullet.getLaser().getWidth()), (int) bullet.getLaser().getHeight());
                    if (defense.getHealth() != 0 && bullet.isExists() && overlaps(defenseRectangle, bulletRectangle)) {
                        iter.remove();
                        defense.setHealth(-50);
                    }
                }
                if (defense.getHealth() > 0) {
                    batch.draw(defense.getTexture(), defense.getPosX(), defense.getPosY());
                }
                else{
                    iter.remove();
                }
                defenseCount++;
            }





            // Change the position of the player depending on the keys pressed
            if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || rasp.is_pressed("left") || arduino.is_pressed("left"))&& !GameScreen.isPaused()) {
                player.setPosX(-2, mainRenderScreen.getWidth());
            } else if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || rasp.is_pressed("right") || arduino.is_pressed("right")) && !GameScreen.isPaused()) {
                player.setPosX(2, mainRenderScreen.getWidth());
            }
            if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) || rasp.is_pressed("up") || arduino.is_pressed("up") ) && !GameScreen.isPaused()) {

                player.shoot();
            }

            batch.end();


            // Enable using the ESCAPE KEY to pause the scene. Supported scenes for pausing are: level
            if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && TimeUtils.millis() - mainRenderScreen.getPauseDelay() > 500) {
                GameScreen.setPaused(!GameScreen.isPaused());
                mainRenderScreen.setPauseDelay(TimeUtils.millis());
            }

            // Draw the current score
            batch.begin();
            mainRenderScreen.getTitleFont().getData().setScale(1f);
            mainRenderScreen.getTitleFont().draw(batch, "Score: " + mainRenderScreen.getScore(), 80, 1000);
            mainRenderScreen.getTitleFont().getData().setScale(2f);
            batch.end();
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
