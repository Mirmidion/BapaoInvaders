package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.GameScreen;
import org.w3c.dom.Text;

public class SettingsMenu implements Screen {

    //Settings menu Layout and Objects
    private Table settingsTable;

    //The spritebatch
    SpriteBatch batch = new SpriteBatch();

    //The stage for drawing and getting input from buttons
    Stage stage = new Stage();

    private Skin buttonSkin;
    private Skin volumeDisplayScreen;

    //The main settings screen
    GameScreen mainRenderScreen;

    private TextButton volumeUp;
    private TextButton volumeDown;
    private TextButton exit;
    private Label volumeDisplay;
    private Text title;
    private SelectBox fpsCounter;

    public SettingsMenu(GameScreen renderScreen) {
        mainRenderScreen = renderScreen;

        Gdx.input.setInputProcessor(stage);
        settingsTable = new Table();
        settingsTable.setPosition(250, 600);
        settingsTable.left();

        buttonSkin = new Skin(Gdx.files.internal("Skin1.json"));
        volumeDisplayScreen = new Skin(Gdx.files.internal("vhs-ui.json"));

        volumeDisplay = new Label(String.valueOf(Math.round(mainRenderScreen.getMusic().getVolume() * 100)), volumeDisplayScreen);


        volumeUp = new TextButton("+", volumeDisplayScreen);
        volumeDown = new TextButton("-", volumeDisplayScreen);
        fpsCounter = new SelectBox(buttonSkin);
        fpsCounter.setItems("Top-Left", "Top-Right", "Bottom-Left", "Bottom-Right");

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

        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
            }

        });


        //Implementation buttons
        settingsTable.row();
        settingsTable.add(volumeDown).padTop(50);
        settingsTable.add(volumeDisplay).padTop(50);
        settingsTable.add(volumeUp).padTop(50);
        settingsTable.row();
        settingsTable.add(fpsCounter).padTop(50);
        exit.setTransform(true);
        settingsTable.row();
        settingsTable.add(exit).padTop(50);

        stage.addActor(settingsTable);
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
