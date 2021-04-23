package com.mygdx.game.Scenes;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Entities.Planet;
import com.mygdx.game.GameScreen;

public class Map implements Screen {

    GameScreen mainRenderScreen;
    SpriteBatch batch = new SpriteBatch();
    private float mapScale = 1;

    private Texture starTexture;

    //Drawer of shapes
    private ShapeRenderer shapeRenderer;

    private boolean blink = true;
    private long blinkTime = 0;

    public Map(GameScreen gameScreen){
        this.mainRenderScreen = gameScreen;
        shapeRenderer = new ShapeRenderer();
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
        shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(0.8f,0.8f,0.8f,1));

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

        // Loop for going through every planet in the system
        for (Planet planet : mainRenderScreen.getSolarSystem().getPlanets()){

            // Calculating the planets position relative to the scale and star position
            float planetPositionY = mainRenderScreen.getSolarSystem().getPosYStar() + planet.getPosY() / mapScale;
            float planetPositionX = mainRenderScreen.getSolarSystem().getPosXStar() + planet.getPosX() / mapScale;

            // If there are moons around the planet, do a for-loop
            if (planet.getMoonList().size() != 0){

                // Calculating the moon orbit in a way it doesn't interfere with the planets size
                int moonOrbit = planet.getRadius()+25;

                // Loop for going through every moon
                for (Planet moon : planet.getMoonList()){
                     moon.setOrbit(moonOrbit/2);
                     shapeRenderer.set(ShapeRenderer.ShapeType.Line);

                     // Draw the orbit with a grey-ish colour
                     shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(0.8f,0.8f,0.8f,1));
                     shapeRenderer.ellipse(planetPositionX - moonOrbit/2f,(planetPositionY - moonOrbit/2f), moonOrbit, moonOrbit);
                     shapeRenderer.end();

                     // Calculate the position of the moon on the orbit
                     moon.setMoonOrbit(moon.getOrbit());

                     //Draw the moon with its own texture
                     batch.begin();
                     batch.draw(moon.getPlanetTexture(), moon.getPosX() + planetPositionX - moon.getPlanetTexture().getWidth()/2f, moon.getPosY() +planetPositionY - moon.getPlanetTexture().getHeight()/2f);
                     batch.end();
                     shapeRenderer.begin();

                     // If the moon is the next level you are going to play, outline this moon
                     if (moon == Planet.getPlanetListOfDifficulty().peek()){
                         mainRenderScreen.setCurrentPlanet(moon);
                         shapeRenderer.set(ShapeRenderer.ShapeType.Line);
                         shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(255,255,255,255));
                         shapeRenderer.ellipse((moon.getPosX() + planetPositionX - moon.getRadius()/2f), (moon.getPosY() + planetPositionY - moon.getRadius()/2f) , moon.getRadius(), moon.getRadius());
                     }
                     moonOrbit += 25;
                }
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(1f,0.95686f,0.2627f,1));
            }

            // Calculate the planets position in the orbit
            planet.orbit();
            batch.begin();
            batch.draw(planet.getPlanetTexture(), (planetPositionX - planet.getPlanetTexture().getWidth()/2f) ,  planetPositionY - planet.getPlanetTexture().getHeight()/2f );
            batch.end();

            // If the planet is the next level, outline the planet
            if (planet == Planet.getPlanetListOfDifficulty().peek()){
                shapeRenderer.set(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(255,255,255,255));
                shapeRenderer.ellipse((mainRenderScreen.getSolarSystem().getPosXStar() + planet.getPosX()/mapScale - planet.getRadius()/2f), (mainRenderScreen.getSolarSystem().getPosYStar() + planet.getPosY()/mapScale - planet.getRadius()/2f) , planet.getRadius(), planet.getRadius());
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            }
        }
        shapeRenderer.end();

        // If ENTER is pressed, start the next level
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)){
            mainRenderScreen.setCurrentScene(GameScreen.scene.level);
            mainRenderScreen.setCurrentPlanet(Planet.getPlanetListOfDifficulty().peek());
            Planet.getPlanetListOfDifficulty().remove();
        }

        // If the left or right arrow is pressed, zoom in/out
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            if (mapScale == 2 || mapScale + 0.01f >= 2){
                mapScale = 2;
            }
            else{
                mapScale += 0.01f;
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
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
        mainRenderScreen.getTitleFont().draw(batch, "Score: " + GameScreen.getScore(), 80, 1000);
        mainRenderScreen.getTitleFont().draw(batch, "Level: " + (Planet.getPlanetListOfDifficulty().peek().getDifficulty()+1), 80, 950);
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
}
