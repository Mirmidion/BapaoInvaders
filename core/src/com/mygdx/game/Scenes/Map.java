package com.mygdx.game.Scenes;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Entities.Planet;
import com.mygdx.game.GameScreen;

public class Map implements Screen {

    GameScreen mainRenderScreen;
    SpriteBatch batch;
    private float mapScale = 1;

    private final Texture starTexture;

    //Drawer of shapes
    private final ShapeRenderer shapeRenderer;

    private boolean blink = true;
    private long blinkTime = 0;
    private static long select;

    public Map(GameScreen gameScreen){
        this.mainRenderScreen = gameScreen;
        batch = mainRenderScreen.getSpriteBatch();
        shapeRenderer = mainRenderScreen.getShapeRenderer();
        starTexture = new Texture(Gdx.files.internal("sun.png"), true);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        mainRenderScreen.getMusic().dispose();
        mainRenderScreen.getMusic2().play();
        mainRenderScreen.getMusic3().dispose();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setAutoShapeType(true);

        // Background being drawn
        batch.begin();
        batch.draw(mainRenderScreen.getGameBackground(), 0, 0, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.8f,0.8f,0.8f,1));

        // Orbits around the sun being drawn, limited to the amount of planets present
        int localOrbitCounter = 0;
        for (int i : mainRenderScreen.getSolarSystem().getOrbitRings()){
            localOrbitCounter++;
            if (localOrbitCounter > mainRenderScreen.getSolarSystem().getPlanets().size()){
                break;
            }
            shapeRenderer.ellipse(mainRenderScreen.getSolarSystem().getPosXStar() - i/mapScale, mainRenderScreen.getSolarSystem().getPosYStar() -i/mapScale, i*2/mapScale, i*2/mapScale);
        }
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.end();

        // Star texture being drawn
        batch.begin();
        batch.draw(starTexture, mainRenderScreen.getSolarSystem().getPosXStar()-starTexture.getWidth()/2f/mapScale, (mainRenderScreen.getSolarSystem().getPosYStar()-starTexture.getHeight()/2f/mapScale+9), starTexture.getWidth()/mapScale, starTexture.getHeight()/mapScale);
        batch.end();
        shapeRenderer.begin();
        if (!mainRenderScreen.getSolarSystem().isFresh()) {
            for (Planet planet : mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty()) {
                planet.orbit();

                int moonOrbit = planet.getRadius() + 25;
                for (Planet moon : planet.getMoonList()) {

                    moon.setOrbit(moonOrbit / 2);
                    moon.setMoonOrbit(moon.getOrbit());
                    moonOrbit += 25;
                }
            }
        }

        // Loop for going through every planet in the system
        for (Planet planet : mainRenderScreen.getSolarSystem().getPlanets()){

            // Calculating the planets position relative to the scale and star position
            float planetPositionY = mainRenderScreen.getSolarSystem().getPosYStar() + planet.getPosY() / mapScale;
            float planetPositionX = mainRenderScreen.getSolarSystem().getPosXStar() + planet.getPosX() / mapScale;
            if (!shapeRenderer.isDrawing()){
                shapeRenderer.begin();
            }
            // If there are moons around the planet, do a for-loop
            if (planet.getMoonList().size() != 0){

                // Calculating the moon orbit in a way it doesn't interfere with the planets size
                int moonOrbit = planet.getRadius()+25;

                // Loop for going through every moon
                for (Planet moon : planet.getMoonList()){
                     moon.setOrbit(moonOrbit/2);

                     shapeRenderer.set(ShapeRenderer.ShapeType.Line);

                     // Draw the orbit with a grey-ish colour
                     shapeRenderer.setColor(new Color(0.8f,0.8f,0.8f,1));
                     shapeRenderer.ellipse(planetPositionX - moonOrbit/2f,(planetPositionY - moonOrbit/2f), moonOrbit, moonOrbit);
                     shapeRenderer.end();

                     // Calculate the position of the moon on the orbit
                     moon.setMoonOrbit(moon.getOrbit());
                     moon.setMoonOrbit(moon.getOrbit());
                     //Draw the moon with its own texture
                     batch.begin();
                     batch.draw(moon.getPlanetTexture(), moon.getPosX() + planetPositionX - moon.getPlanetTexture().getWidth()/2f, moon.getPosY() +planetPositionY - moon.getPlanetTexture().getHeight()/2f);
                     batch.end();
                     shapeRenderer.begin();

                     moonOrbit += 25;
                }
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(new Color(1f,0.95686f,0.2627f,1));
            }

            // Calculate the planets position in the orbit
            planet.orbit();
            if (mainRenderScreen.isFirstLoad()){
                //planet.orbit();
            }


            planet.checkList(mainRenderScreen.getSolarSystem());

            if (shapeRenderer.isDrawing()){
                shapeRenderer.end();
            }
            batch.begin();
            batch.draw(planet.getPlanetTexture(), (planetPositionX - planet.getPlanetTexture().getWidth()/2f) ,  planetPositionY - planet.getPlanetTexture().getHeight()/2f );
            batch.end();
        }

        for (Planet planet : mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty()) {
            if (!planet.isMoon()) {
                //planet.orbit();
            }
        }

        if (!shapeRenderer.isDrawing()){
            shapeRenderer.begin();
        }


        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(255,255,255,255));
        Planet peekPlanet = mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().peek();
        if (peekPlanet.isMoon()) {
            shapeRenderer.ellipse((mainRenderScreen.getSolarSystem().getPosXStar() + (peekPlanet.getOrbitPlanet().getPosX() / mapScale + peekPlanet.getPosX()) - peekPlanet.getRadius() / 2f), (mainRenderScreen.getSolarSystem().getPosYStar() + (peekPlanet.getOrbitPlanet().getPosY() / mapScale + peekPlanet.getPosY()) - peekPlanet.getRadius() / 2f), peekPlanet.getRadius(), peekPlanet.getRadius());
        }
        else if (!peekPlanet.isMoon()){
            shapeRenderer.ellipse((mainRenderScreen.getSolarSystem().getPosXStar() + peekPlanet.getPosX() / mapScale - peekPlanet.getRadius() / 2f), (mainRenderScreen.getSolarSystem().getPosYStar() + peekPlanet.getPosY() / mapScale - peekPlanet.getRadius() / 2f), peekPlanet.getRadius(), peekPlanet.getRadius());
        }
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.end();

        // If ENTER is pressed, start the next level
        if (TimeUtils.millis() - select > 500 && (Gdx.input.isKeyPressed(Input.Keys.ENTER) || mainRenderScreen.getRasp().is_pressed("up") || mainRenderScreen.getArduino().is_pressed("up"))){
            mainRenderScreen.setCurrentScene(GameScreen.scene.level);
            mainRenderScreen.setCurrentPlanet(mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().peek());
        }

        // If the left or right arrow is pressed, zoom in/out
        if ((Gdx.input.isKeyPressed(Input.Keys.UP) || mainRenderScreen.getRasp().is_pressed("left") || mainRenderScreen.getArduino().is_pressed("left"))){
            if (mapScale == 2 || mapScale + 0.01f >= 2){
                mapScale = 2;
            }
            else{
                mapScale += 0.01f;
            }
        }
        else if ((Gdx.input.isKeyPressed(Input.Keys.DOWN) || mainRenderScreen.getRasp().is_pressed("right") || mainRenderScreen.getArduino().is_pressed("right"))){
            if (mapScale == 1 || mapScale + 0.01f <= 1){
                mapScale = 1;
            }
            else{
                mapScale -= 0.01f;
            }
        }

        if (TimeUtils.millis() -blinkTime >= 600){
            blink = !blink;
            blinkTime = TimeUtils.millis();
        }

        // Draw the current score
        batch.begin();
        mainRenderScreen.getTitleFont().getData().setScale(1f);
        mainRenderScreen.getTitleFont().draw(batch, "Score: " + mainRenderScreen.getScore(), 80, 1000);
        mainRenderScreen.getTitleFont().draw(batch, "Level: " + (mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().peek().getDifficulty()+1), 80, 950);
        mainRenderScreen.getTitleFont().getData().setScale(2f);

        mainRenderScreen.getNormalFont().getData().setScale(0.7f);
        if (blink) {
            mainRenderScreen.getNormalFont().draw(batch, "Press ENTER to start the level", 630, 100);
        }
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

    public static void setSelect() {
        Map.select = TimeUtils.millis();
    }
}