package com.mygdx.game.Scenes;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Entities.SolarSystem;
import com.mygdx.game.GameScreen;
import org.w3c.dom.Text;

import java.awt.*;

public class MainMenu implements Screen {

    //The spritebatch
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;

    //The stage for drawing and getting input from buttons
    Stage stage = new Stage();

    //The main gamescreen
    GameScreen mainRenderScreen;

    //Main Menu Layout and Objects
    private Table mainMenuTable;
    private TextButton start;
    private TextButton settings;
    private TextButton exit;
    private Text title;

    //Other
    private float[] bapaoY = new float[]{500, 14, 129, 1049, 280, 809, 102, 758, 640, 20, 70, 780, 420, 920, 320};
    private float[] bapaoX = new float[]{294, 0, 498, 928, 1359, 200, 800, 500, 1060, 12, 1500, 1400, 1600, 1800, 1900};
    private int[] bapaoSpeed = new int[]{300, 250, 200, 150, 100, 250, 450, 100, 230, 400, 500, 210, 500, 320, 340};

    //Sprites
    private Sprite[] bapaoSprites;
    private Sprite bapaoSprite;

    //Object visuals
    private static Skin buttonSkin;
    private TextureAtlas atlas;

    BitmapFont titleFont;
    BitmapFont normalFont;

    private static int selectedSaveGame = 1;
    private long previousSelected = 0;

    public MainMenu(GameScreen renderScreen){
        mainRenderScreen = renderScreen;

        normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));
        titleFont = new BitmapFont(Gdx.files.internal("titleFontV2.fnt"));

        //Initializing Sprites
        bapaoSprite = new Sprite(new Texture("Bapao1.png"));
        bapaoSprites = new Sprite[15];
        for(int i = 0; i<bapaoSprites.length; i++)
        {
            bapaoSprites[i] = new Sprite(new Texture("Bapao1.png"));
        }

        Gdx.input.setInputProcessor(stage);
        mainMenuTable = new Table();
        mainMenuTable.setPosition(250,600);
        mainMenuTable.left();

        buttonSkin = new Skin(Gdx.files.internal("Skin1.json"));

        start = new TextButton("Start", buttonSkin);
        settings = new TextButton("Settings",buttonSkin);
        
        mainRenderScreen.setSettingsMenuSwitch(false);

        exit = new TextButton("Exit",buttonSkin);

        start.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainRenderScreen.setSaveGameMenuSwitch(true);
            }
        });
        settings.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainRenderScreen.setSettingsMenuSwitch(!mainRenderScreen.isSettingsMenuSwitch());
            }

        });
        exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                System.exit(0);
            }

        });

        start.setTransform(true);
        settings.setTransform(true);
        exit.setTransform(true);

        mainMenuTable.row();
        mainMenuTable.add(start).padTop(225);
        mainMenuTable.row();
        mainMenuTable.add(settings).padTop(50);
        mainMenuTable.row();
        mainMenuTable.add(exit).padTop(50);

        stage.addActor(mainMenuTable);

        batch = mainRenderScreen.getSpriteBatch();
        shapeRenderer = mainRenderScreen.getShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {

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

        renderBapaos(delta);  //todo

        batch.begin();
        titleFont.getData().setScale(2f);
        titleFont.draw(batch, "Bapao Invaders", 200, 800);
        batch.end();


        stage.draw();
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        if (mainRenderScreen.isSettingsMenuSwitch()){
            batch.begin();
            batch.draw(mainRenderScreen.getSettingsMenu(), 200,200, 1520, 680);
            batch.end();
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                mainRenderScreen.setSettingsMenuSwitch(false);
            }
        }
        else if (mainRenderScreen.isSaveGameMenuSwitch()){
            shapeRenderer.setAutoShapeType(true);
            for (int i = 0; i < 3; i++) {
                batch.begin();
                batch.draw(mainRenderScreen.getSettingsMenu(), 200 + 560 * i, 300, 400, 600);
                normalFont.getData().setScale(1f);
                normalFont.draw(batch, "Savegame "+(i+1),225 + 560 * i, 880);
                try {
                    if (i == 0 && mainRenderScreen.getSaveGame1SolarSystem().isPlayed()) {
                        System.out.println("1");
                        titleFont.getData().setScale(1f);
                        titleFont.draw(batch, "Level: " + (mainRenderScreen.getSaveGame1SolarSystem().getPlanetListOfDifficulty().peek().getDifficulty() + 1), 280 + 560 * i, 700);
                        titleFont.draw(batch, "Score: " + mainRenderScreen.getSaveGame1Score(), 270 + 560 * i, 550);
                    } else if (i == 1 && mainRenderScreen.getSaveGame2SolarSystem().isPlayed()) {
                        System.out.println("2");
                        titleFont.getData().setScale(1f);
                        titleFont.draw(batch, "Level: " + (mainRenderScreen.getSaveGame2SolarSystem().getPlanetListOfDifficulty().peek().getDifficulty() + 1), 280 + 560 * i, 700);
                        titleFont.draw(batch, "Score: " + mainRenderScreen.getSaveGame2Score(), 270 + 560 * i, 550);
                    } else if (i == 2 && mainRenderScreen.getSaveGame3SolarSystem().isPlayed()) {
                        System.out.println("3");
                        titleFont.getData().setScale(1f);
                        titleFont.draw(batch, "Level: " + (mainRenderScreen.getSaveGame3SolarSystem().getPlanetListOfDifficulty().peek().getDifficulty() + 1), 280 + 560 * i, 700);
                        titleFont.draw(batch, "Score: " + mainRenderScreen.getSaveGame3Score(), 270 + 560 * i, 550);
                    }
                    else{
                        titleFont.getData().setScale(1f);
                        titleFont.draw(batch, "EMPTY", 300 + 560 * i, 620);
                    }

                }
                catch (Exception e){
                    System.out.println(e);
                    titleFont.getData().setScale(1f);
                    titleFont.draw(batch, "EMPTY", 300 + 560 * i, 620);
                }
                batch.end();

            }
            shapeRenderer.begin();
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(200 + 560 * (selectedSaveGame-1), 300,400,600);
            shapeRenderer.end();

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
                mainRenderScreen.setCurrentSaveGame(selectedSaveGame);
                mainRenderScreen.setCurrentScene(GameScreen.scene.map);
                mainRenderScreen.setSaveGameMenuSwitch(false);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                mainRenderScreen.setSaveGameMenuSwitch(false);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && selectedSaveGame > 1 && TimeUtils.millis() - previousSelected > 300){
                selectedSaveGame--;
                previousSelected = TimeUtils.millis();
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && selectedSaveGame < 3 && TimeUtils.millis() - previousSelected > 300){
                selectedSaveGame++;
                previousSelected = TimeUtils.millis();
            }
        }

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

    public void renderBapaos(float delta)
    {
        for(int i = 0; i<bapaoSprites.length; i++)
        {
            batch.begin();
            bapaoSprites[i].draw(batch);
            batch.end();
        }

        for(int i = 0; i<bapaoSprites.length; i++)
        {
            bapaoSprites[i].setPosition(bapaoX[i], bapaoY[i]);
            bapaoSprites[i].setRotation(bapaoY[i]);
            if(bapaoY[i] < -50)
            {
                bapaoY[i] = 1210;
                bapaoX[i] = (float) (Math.random()) * (1920);

            }
            bapaoY[i] -= (bapaoSpeed[i] * delta);
        }
    }

    public static int getSelectedSaveGame() {
        return selectedSaveGame;
    }

    public static Skin getButtonSkin() {
        return buttonSkin;
    }
}
