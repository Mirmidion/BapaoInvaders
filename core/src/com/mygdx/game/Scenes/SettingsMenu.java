package com.mygdx.game.Scenes;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.GameScreen;

public class SettingsMenu extends ScreenAdapter implements Screen {

    //Settings menu Layout and Objects
    private final Table settingsTable;

    //The spritebatch
    SpriteBatch batch = new SpriteBatch();
    ShapeRenderer shapeRenderer = new ShapeRenderer();

    //The stage for drawing and getting input from buttons
    Stage stage = new Stage();

    private Skin volumeDisplayScreen;

    //The main settings screen
    GameScreen mainRenderScreen;

    private final TextButton volumeUp;
    private final TextButton volumeDown;
    private final TextButton exit;
    private Label volumeDisplay;
    private final TextButton fpsCounterOn;
    private final TextButton fpsCounterOff;
    public boolean fpsCounterCheck;

    private int buttonSelect = 1;
    private long prevSelect = 0;
    private static long prevChange = 0;

    public SettingsMenu(GameScreen renderScreen) {
        mainRenderScreen = renderScreen;

        settingsTable = new Table();
        settingsTable.setPosition(250, 600);
        settingsTable.left();

        Skin buttonSkin = new Skin(Gdx.files.internal("Skin1.json"));
        try {
            volumeDisplayScreen = new Skin(Gdx.files.internal("vhs-ui.json"));
        }
        catch (Exception e){
            e.printStackTrace();
        }


        volumeDisplay = new Label(String.valueOf(Math.round(mainRenderScreen.getMusic().getVolume() * 100)), volumeDisplayScreen);


        TextButton volume = new TextButton("Volume", volumeDisplayScreen);
        volumeUp = new TextButton("+", volumeDisplayScreen);
        volumeDown = new TextButton("-", volumeDisplayScreen);
        TextButton fpsCounter = new TextButton("FPS-counter", volumeDisplayScreen);
        fpsCounterOn = new TextButton("On", volumeDisplayScreen);
        fpsCounterOff = new TextButton("Off", volumeDisplayScreen);


        mainRenderScreen.setSettingsMenuSwitch(false);

        exit = new TextButton("Exit", buttonSkin);



        //Buttons
        volumeUp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainRenderScreen.getMusic().setVolume((float) (mainRenderScreen.getMusic().getVolume() + 0.1)) ;
                mainRenderScreen.getMusic2().setVolume((float) (mainRenderScreen.getMusic2().getVolume() + 0.1)) ;
                mainRenderScreen.getMusic3().setVolume((float) (mainRenderScreen.getMusic3().getVolume() + 0.1)) ;
                volumeDisplay = new Label(String.valueOf(Math.round(mainRenderScreen.getMusic().getVolume() * 100)), volumeDisplayScreen);
            }

        });

        volumeDown.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainRenderScreen.getMusic().setVolume((float) (mainRenderScreen.getMusic().getVolume() - 0.1)) ;
                mainRenderScreen.getMusic2().setVolume((float) (mainRenderScreen.getMusic2().getVolume() - 0.1)) ;
                mainRenderScreen.getMusic3().setVolume((float) (mainRenderScreen.getMusic3().getVolume() - 0.1)) ;
                volumeDisplay = new Label(String.valueOf(Math.round(mainRenderScreen.getMusic().getVolume() * 100)), volumeDisplayScreen);
            }
        });

        fpsCounterOn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fpsCounterCheck = false;
            }

        });

        fpsCounterOff.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fpsCounterCheck = true;
            }

        });

        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
            }

        });


        //Implementation buttons
        settingsTable.row();
        settingsTable.add(volume).padTop(50);
        settingsTable.add(volumeDown).padTop(50);
        settingsTable.add(volumeDisplay).padTop(50);
        settingsTable.add(volumeUp).padTop(50);
        settingsTable.row();
        settingsTable.add(fpsCounter).padTop(50);
        settingsTable.add(fpsCounterOn).padTop(50);
        settingsTable.add(fpsCounterOff).padTop(50);
        exit.setTransform(true);
        settingsTable.row();
        settingsTable.add(exit).padTop(50);

        stage.addActor(settingsTable);
        shapeRenderer.setAutoShapeType(true);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        mainRenderScreen.getMusic().play();
        mainRenderScreen.getMusic2().dispose();
        mainRenderScreen.getMusic3().dispose();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(mainRenderScreen.getGameBackground(), 0, 0, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        batch.end();

        batch.begin();
        mainRenderScreen.getTitleFont().draw(batch, "Settings", 200, 800);
        batch.end();

        stage.draw();
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);


        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        switch (buttonSelect) {
            case 1: {
                shapeRenderer.rect(volumeDown.getX() + settingsTable.getX(), volumeDown.getY() + settingsTable.getY(), volumeDown.getWidth(), volumeDown.getHeight());
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || mainRenderScreen.getRasp().is_pressed("up") || mainRenderScreen.getArduino().is_pressed("up")) && TimeUtils.millis() - prevChange > 200) {
                    mainRenderScreen.setMusic1Vol(-0.1f);
                    mainRenderScreen.setMusic2Vol(-0.1f);
                    mainRenderScreen.setMusic3Vol(-0.1f);
                    volumeDisplay.setText(String.valueOf(Math.round(mainRenderScreen.getMusic().getVolume() * 100)));
                    prevChange = TimeUtils.millis();
                }
                break;
            }
            case 2: {
                shapeRenderer.rect(volumeUp.getX() + settingsTable.getX(), volumeUp.getY() + settingsTable.getY(), volumeUp.getWidth(), volumeUp.getHeight());
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || mainRenderScreen.getRasp().is_pressed("up") || mainRenderScreen.getArduino().is_pressed("up")) && TimeUtils.millis() - prevChange > 200) {
                    mainRenderScreen.setMusic1Vol(0.1f);
                    mainRenderScreen.setMusic2Vol(0.1f);
                    mainRenderScreen.setMusic3Vol(0.1f);
                    volumeDisplay.setText(String.valueOf(Math.round(mainRenderScreen.getMusic().getVolume() * 100)));
                    prevChange = TimeUtils.millis();
                }
                break;
            }
            case 3: {
                shapeRenderer.rect(fpsCounterOn.getX() + settingsTable.getX(), fpsCounterOn.getY() + settingsTable.getY(), fpsCounterOn.getWidth(), fpsCounterOn.getHeight());
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || mainRenderScreen.getRasp().is_pressed("up") || mainRenderScreen.getArduino().is_pressed("up")) && TimeUtils.millis() - prevChange > 200) {
                    GameScreen.setFpsCounterCheck(true);
                    prevChange = TimeUtils.millis();
                }
                break;
            }
            case 4: {
                shapeRenderer.rect(fpsCounterOff.getX() + settingsTable.getX(), fpsCounterOff.getY() + settingsTable.getY(), fpsCounterOff.getWidth(), fpsCounterOff.getHeight());
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || mainRenderScreen.getRasp().is_pressed("up") || mainRenderScreen.getArduino().is_pressed("up")) && TimeUtils.millis() - prevChange > 200) {
                    GameScreen.setFpsCounterCheck(false);
                    prevChange = TimeUtils.millis();
                }
                break;
            }
            case 5: {
                shapeRenderer.rect(exit.getX() + settingsTable.getX(), exit.getY() + settingsTable.getY(), exit.getWidth(), exit.getHeight());
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || mainRenderScreen.getRasp().is_pressed("up") || mainRenderScreen.getArduino().is_pressed("up")) && TimeUtils.millis() - prevChange > 200) {
                    MainMenu.setSwitchDelay(TimeUtils.millis());
                    mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
                    prevChange = TimeUtils.millis();
                }
                break;
            }
        }
        shapeRenderer.end();

        if ((Gdx.input.isKeyPressed(Input.Keys.DOWN) || mainRenderScreen.getRasp().is_pressed("right") || mainRenderScreen.getArduino().is_pressed("right")) && TimeUtils.millis() - prevSelect > 500){
            if (buttonSelect < 5){
                buttonSelect++;
            }
            prevSelect = TimeUtils.millis();
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.UP) || mainRenderScreen.getRasp().is_pressed("left") || mainRenderScreen.getArduino().is_pressed("left")) && TimeUtils.millis() - prevSelect > 500){
            if (buttonSelect > 1){
                buttonSelect--;
            }
            prevSelect = TimeUtils.millis();
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
        settingsTable.setTouchable(Touchable.disabled);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }

    public static void setPrevChange() {
        SettingsMenu.prevChange = TimeUtils.millis();
    }
}
