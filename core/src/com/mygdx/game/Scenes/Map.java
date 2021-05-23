package com.mygdx.game.Scenes;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Entities.Planet;
import com.mygdx.game.Entities.SolarSystem;
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
    private SolarSystem currentSolarSystem;

    public Map(GameScreen gameScreen){
        this.mainRenderScreen = gameScreen;
        batch = mainRenderScreen.getSpriteBatch();
        shapeRenderer = mainRenderScreen.getShapeRenderer();
        starTexture = new Texture(Gdx.files.internal("sun.png"), true);

        shapeRenderer.setAutoShapeType(true);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        Gdx.gl.glLineWidth(1);

        currentSolarSystem = mainRenderScreen.getSolarSystem();

        // Background being drawn
        batch.begin();
        batch.draw(mainRenderScreen.getGameBackground(), 0, 0, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.8f,0.8f,0.8f,1));

        drawOrbitsOfSun();

        // Star texture being drawn
        batch.begin();
        batch.draw(starTexture, mainRenderScreen.getSolarSystem().getPosXStar()-starTexture.getWidth()/2f/mapScale, (mainRenderScreen.getSolarSystem().getPosYStar()-starTexture.getHeight()/2f/mapScale+9), starTexture.getWidth()/mapScale, starTexture.getHeight()/mapScale);
        batch.end();
        shapeRenderer.begin();

        updateSolarSystem();
        currentPlanetOutline();
        handleInput();
        handleZoom();
        textRender();
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
        shapeRenderer.dispose();
    }

    public static void setSelect() {
        Map.select = TimeUtils.millis();
    }

    public void drawOrbitsOfSun(){

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
    }

    public void updateSolarSystem(){

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

        for (Planet planet : currentSolarSystem.getPlanets()){

            float planetPositionX = currentSolarSystem.getPosXStar() + planet.getPosX() / mapScale;
            float planetPositionY = currentSolarSystem.getPosYStar() + planet.getPosY() / mapScale;

            if (!shapeRenderer.isDrawing()){
                shapeRenderer.begin();
            }

            if (planet.getMoonList().size() > 0){

                int moonOrbit = planet.getRadius()+25;

                for (Planet moon : planet.getMoonList()){
                    shapeRenderer.set(ShapeRenderer.ShapeType.Line);
                    shapeRenderer.setColor(new Color(0.8f,0.8f,0.8f,1));
                    shapeRenderer.ellipse(planetPositionX - moonOrbit/2f,(planetPositionY - moonOrbit/2f), moonOrbit, moonOrbit);
                    shapeRenderer.end();

                    moon.setOrbit(moonOrbit/2);
                    moon.orbit();

                    float moonXPosition = moon.getPosX() + planetPositionX - moon.getPlanetTexture().getWidth()/2f;
                    float moonYPosition = moon.getPosY() + planetPositionY - moon.getPlanetTexture().getHeight()/2f;

                    batch.begin();
                    batch.draw(moon.getPlanetTexture(), moonXPosition, moonYPosition);
                    batch.end();
                    shapeRenderer.begin();

                    moonOrbit += 25;
                }
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(new Color(1f,0.95686f,0.2627f,1));
            }

            planet.orbit();

            if (shapeRenderer.isDrawing()){
                shapeRenderer.end();
            }

            batch.begin();
            batch.draw(planet.getPlanetTexture(), (planetPositionX - planet.getPlanetTexture().getWidth()/2f) ,  planetPositionY - planet.getPlanetTexture().getHeight()/2f );
            batch.end();
        }
    }

    public void currentPlanetOutline(){
        if (!shapeRenderer.isDrawing()){
            shapeRenderer.begin();
        }

        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(255,255,255,255));

        Planet peekPlanet = currentSolarSystem.getPlanetListOfDifficulty().peek();

        assert peekPlanet != null;
        if (peekPlanet.isMoon()) {

            float peekMoonRadius = peekPlanet.getRadius();
            float peekMoonPosX = (currentSolarSystem.getPosXStar() + peekPlanet.getOrbitPlanet().getPosX() / mapScale + peekPlanet.getPosX() - peekMoonRadius / 2f);
            float peekMoonPosY = (currentSolarSystem.getPosYStar() + peekPlanet.getOrbitPlanet().getPosY() / mapScale + peekPlanet.getPosY() - peekMoonRadius / 2f);

            shapeRenderer.ellipse(peekMoonPosX, peekMoonPosY, peekMoonRadius, peekMoonRadius);
        }
        else if (!peekPlanet.isMoon()){

            float peekPlanetRadius = peekPlanet.getRadius();
            float peekPlanetPosX = (currentSolarSystem.getPosXStar() + peekPlanet.getPosX() / mapScale - peekPlanetRadius / 2f);
            float peekPlanetPosY = (currentSolarSystem.getPosYStar() + peekPlanet.getPosY() / mapScale - peekPlanetRadius / 2f);

            shapeRenderer.ellipse(peekPlanetPosX, peekPlanetPosY , peekPlanetRadius, peekPlanetRadius);
        }
        shapeRenderer.end();
    }

    public void handleInput(){

        boolean raspUpPressed = mainRenderScreen.getRasp().is_pressed("up");
        boolean ardUpPressed = mainRenderScreen.getArduino().is_pressed("up");
        boolean buttonSelectDelay = TimeUtils.millis() - select > 500;

        if (buttonSelectDelay && (Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed)){
            mainRenderScreen.setCurrentScene(GameScreen.scene.level);
            mainRenderScreen.setCurrentPlanet(mainRenderScreen.getSolarSystem().getPlanetListOfDifficulty().peek());
        }
    }

    public void handleZoom(){

        boolean raspLeftPressed = mainRenderScreen.getRasp().is_pressed("left");
        boolean raspRightPressed = mainRenderScreen.getRasp().is_pressed("right");

        boolean ardLeftPressed = mainRenderScreen.getArduino().is_pressed("left");
        boolean ardRightPressed = mainRenderScreen.getArduino().is_pressed("right");

        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || raspLeftPressed || ardLeftPressed)){
            if (mapScale == 2 || mapScale + 0.01f >= 2){
                mapScale = 2;
            }
            else{
                mapScale += 0.01f;
            }
        }
        else if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || raspRightPressed || ardRightPressed)){
            if (mapScale == 1 || mapScale + 0.01f <= 1){
                mapScale = 1;
            }
            else{
                mapScale -= 0.01f;
            }
        }
    }

    public void textRender(){
        if (TimeUtils.millis() - blinkTime >= 600){
            blink = !blink;
            blinkTime = TimeUtils.millis();
        }

        batch.begin();
        mainRenderScreen.getTitleFont().getData().setScale(1f);
        mainRenderScreen.getTitleFont().draw(batch, "Score: " + mainRenderScreen.getScore(), 80, 1000);
        assert currentSolarSystem.getPlanetListOfDifficulty().peek() != null;
        mainRenderScreen.getTitleFont().draw(batch, "Level: " + (currentSolarSystem.getPlanetListOfDifficulty().peek().getDifficulty()+1), 80, 950);
        mainRenderScreen.getNormalFont().getData().setScale(0.7f);
        if (blink) {
            mainRenderScreen.getNormalFont().draw(batch, "Press ENTER to start the level", 630, 100);
        }
        batch.end();
    }
}