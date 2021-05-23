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

import java.util.ArrayList;

public class SettingsMenu extends ScreenAdapter implements Screen {

    //The main settings screen
    private final GameScreen mainRenderScreen;

    //The spritebatch
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    //Settings menu Layout and Objects
    private final Table settingsTable;



    //The stage for drawing and getting input from buttons
    private final Stage stage = new Stage();

    private Skin volumeDisplayScreen;

    private final TextButton volumeUp;
    private final TextButton volumeDown;
    private final TextButton exit;
    private Label volumeDisplay;
    private final TextButton fpsCounterOn;
    private final TextButton fpsCounterOff;

    private ArrayList<TextButton> allButtons;

    public boolean fpsCounterCheck;

    private int buttonSelect = 1;
    private long prevSelect = 0;
    private static long prevChange = 0;

    public SettingsMenu(GameScreen gameScreen) {
        mainRenderScreen = gameScreen;
        batch = mainRenderScreen.getSpriteBatch();
        shapeRenderer = mainRenderScreen.getShapeRenderer();

        allButtons = new ArrayList<>();

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


        exit = new TextButton("Exit", buttonSkin);
        exit.setTransform(true);


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

        settingsTable.row();
        settingsTable.add(exit).padTop(50);

        stage.addActor(settingsTable);
        shapeRenderer.setAutoShapeType(true);

        allButtons.add(volumeDown);
        allButtons.add(volumeUp);
        allButtons.add(fpsCounterOn);
        allButtons.add(fpsCounterOff);
        allButtons.add(exit);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        mainRenderScreen.setPlayingMusic(1);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(mainRenderScreen.getGameBackground(), 0, 0, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        mainRenderScreen.getTitleFont().draw(batch, "Settings", 200, 800);
        batch.end();

        stage.draw();
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);


        drawOutlines();
        handleInput();
        handleButtonSelect();
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

    public void drawOutlines(){
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(allButtons.get(buttonSelect-1).getX() + settingsTable.getX(), allButtons.get(buttonSelect-1).getY() + settingsTable.getY(), allButtons.get(buttonSelect-1).getWidth(), allButtons.get(buttonSelect-1).getHeight());
        shapeRenderer.end();
    }

    public void handleInput(){

        boolean raspUpPressed = mainRenderScreen.getRasp().is_pressed("up");
        boolean ardUpPressed = mainRenderScreen.getArduino().is_pressed("up");
        boolean canPressButton = TimeUtils.millis() - prevChange > 200;

        switch (buttonSelect) {
            case 1: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    mainRenderScreen.setMusicVol(-0.1f);
                    volumeDisplay.setText(String.valueOf(Math.round(mainRenderScreen.getMusic().getVolume() * 100)));
                    prevChange = TimeUtils.millis();
                }
                break;
            }
            case 2: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    mainRenderScreen.setMusicVol(0.1f);
                    volumeDisplay.setText(String.valueOf(Math.round(mainRenderScreen.getMusic().getVolume() * 100)));
                    prevChange = TimeUtils.millis();
                }
                break;
            }
            case 3: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    GameScreen.setFpsCounterCheck(true);
                    prevChange = TimeUtils.millis();
                }
                break;
            }
            case 4: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    GameScreen.setFpsCounterCheck(false);
                    prevChange = TimeUtils.millis();
                }
                break;
            }
            case 5: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    MainMenu.setSwitchDelay(TimeUtils.millis());
                    mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
                    prevChange = TimeUtils.millis();
                }
                break;
            }
        }
    }

    public void handleButtonSelect(){
        boolean raspLeftPressed = mainRenderScreen.getRasp().is_pressed("left");
        boolean raspRightPressed = mainRenderScreen.getRasp().is_pressed("right");

        boolean ardLeftPressed = mainRenderScreen.getArduino().is_pressed("left");
        boolean ardRightPressed = mainRenderScreen.getArduino().is_pressed("right");
        boolean canSelectButton = TimeUtils.millis() - prevSelect > 500;

        if ((Gdx.input.isKeyPressed(Input.Keys.DOWN) || raspRightPressed || ardRightPressed) && canSelectButton){
            if (buttonSelect < 5){
                buttonSelect++;
            }
            prevSelect = TimeUtils.millis();
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.UP) || raspLeftPressed || ardLeftPressed) && canSelectButton){
            if (buttonSelect > 1){
                buttonSelect--;
            }
            prevSelect = TimeUtils.millis();
        }
    }
}
