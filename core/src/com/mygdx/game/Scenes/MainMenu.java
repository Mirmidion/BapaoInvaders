package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.GameScreen;

import java.util.ArrayList;

public class MainMenu extends BaseScreen {

    private boolean saveGameMenuSwitch = false;

    //The stage for drawing and getting input from buttons
    Stage stage = new Stage();

    //Main Menu Layout and Objects
    private final Table mainMenuTable;

    private final ArrayList<TextButton> allMainButtons;

    //Other
    private final float[] bapaoY = new float[]{500, 14, 129, 1049, 280, 809, 102, 758, 640, 20, 70, 780, 420, 920, 320};
    private final float[] bapaoX = new float[]{294, 0, 498, 928, 1359, 200, 800, 500, 1060, 12, 1500, 1400, 1600, 1800, 1900};
    private final int[] bapaoSpeed = new int[]{300, 250, 200, 150, 100, 250, 450, 100, 230, 400, 500, 210, 500, 320, 340};

    //Sprites
    private final Sprite[] bapaoSprites;

    //New sign
    private final Sprite redSign = new Sprite(new Texture(Gdx.files.internal("red_circle.png")));
    private final Sprite redSignText = new Sprite(new Texture(Gdx.files.internal("red_circle_text.png")));
    private Vector2 redSignSize;
    private Vector2 redSignTextSize;
    private int redSignRotation;
    private double redSignDelta;

    //Object visuals
    private static Skin buttonSkin;

    private final BitmapFont titleFont;
    private final BitmapFont normalFont;

    private static int selectedSaveGame = 1;
    private long previousSelected = 0;

    private int buttonSelect = 1;
    private static long switchDelay = 0;
    private long prevSelect = 0;
    private long select;

    private final Texture saveGameTexture;

    public MainMenu(GameScreen gameScreen) {
        mainRenderScreen = gameScreen;

        allMainButtons = new ArrayList<>();

        normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));
        titleFont = new BitmapFont(Gdx.files.internal("titleFontV2.fnt"));
        saveGameTexture = new Texture("button.png");

        //Initializing Sprites
        bapaoSprites = new Sprite[15];
        for (int i = 0; i < bapaoSprites.length; i++) {
            bapaoSprites[i] = new Sprite(new Texture("Bapao1.png"));
        }

        Gdx.input.setInputProcessor(stage);
        mainMenuTable = new Table();
        mainMenuTable.setPosition(250, 500);
        mainMenuTable.left();

        buttonSkin = new Skin(Gdx.files.internal("Skin1.json"));

        TextButton start = new TextButton("Start", buttonSkin);
        TextButton bossmode = new TextButton("BossMode", buttonSkin);
        TextButton settings = new TextButton("Settings", buttonSkin);
        TextButton highScores = new TextButton("Highscores", buttonSkin);
        TextButton exit = new TextButton("Exit", buttonSkin);

        bossmode.setTransform(true);
        start.setTransform(true);
        settings.setTransform(true);
        highScores.setTransform(true);
        exit.setTransform(true);

        mainMenuTable.row();
        mainMenuTable.add(start).padTop(225);
        mainMenuTable.row();
        mainMenuTable.add(bossmode).padTop(10);
        mainMenuTable.row();
        mainMenuTable.add(settings).padTop(10);
        mainMenuTable.row();
        mainMenuTable.add(highScores).padTop(10);
        mainMenuTable.row();
        mainMenuTable.add(exit).padTop(10);

        allMainButtons.add(start);
        allMainButtons.add(bossmode);
        allMainButtons.add(settings);
        allMainButtons.add(highScores);
        allMainButtons.add(exit);

        redSign.setSize(100, 100);
        redSign.setPosition(mainMenuTable.getX() + 280, mainMenuTable.getY() - 20);
        redSignSize = new Vector2(redSign.getWidth(), redSign.getHeight());
        redSignRotation = 0;
        redSignDelta = 0;
        redSignText.setSize(80, 80);
        redSignText.setPosition(mainMenuTable.getX() + 290, mainMenuTable.getY()-5);
        redSignTextSize = new Vector2(redSignText.getWidth(), redSignText.getHeight());


        stage.addActor(mainMenuTable);

        batch = mainRenderScreen.getSpriteBatch();
        shapeRenderer = mainRenderScreen.getShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        batch.begin();
        batch.draw(mainRenderScreen.getGameBackground(), 0, 0, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        batch.end();

        renderBapaos(delta);

        batch.begin();
        titleFont.getData().setScale(2f);
        titleFont.draw(batch, "Bapao Invaders", 200, 800);
        batch.end();

        stage.draw();
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        drawOutlines();

        boolean raspLeftPressed = mainRenderScreen.getRasp().is_pressed("left");
        boolean raspRightPressed = mainRenderScreen.getRasp().is_pressed("right");
        boolean raspUpPressed = mainRenderScreen.getRasp().is_pressed("up");

        boolean ardLeftPressed = mainRenderScreen.getArduino().is_pressed("left");
        boolean ardRightPressed = mainRenderScreen.getArduino().is_pressed("right");
        boolean ardUpPressed = mainRenderScreen.getArduino().is_pressed("up");

        renderRedSign(delta);

        if (!saveGameMenuSwitch) {
            handleButtonPress(raspUpPressed, ardUpPressed);
            handleButtonSelect(raspRightPressed, ardRightPressed, raspLeftPressed, ardLeftPressed);
        } else {
            drawSaveGames();
            handleSaveGamePress(raspUpPressed, ardUpPressed);
            handleSaveGameSelect(raspRightPressed, ardRightPressed, raspLeftPressed, ardLeftPressed);
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
        stage.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }

    public void renderRedSign(float delta)
    {
        batch.begin();
        redSign.draw(batch);
        redSignText.draw(batch);

        redSignDelta += delta;

        redSignSize.x += 3*(float)Math.sin(redSignDelta*5);
        redSignSize.y += 3*(float)Math.sin(redSignDelta*5);

        redSignTextSize.x += 3*(float)Math.sin(redSignDelta*5);
        redSignTextSize.y += 3*(float)Math.sin(redSignDelta*5);

        redSign.setOrigin(redSignSize.x / 2, redSignSize.y / 2);
        redSign.setSize(redSignSize.x, redSignSize.y);
        redSign.setRotation(redSignRotation++);


        redSignText.setRotation(15);
        redSignText.setOrigin(redSignSize.x/2, redSignSize.y/2);
        redSignText.setSize(redSignTextSize.x, redSignTextSize.y);
        batch.end();

    }

    public void renderBapaos(float delta) {

        if (!batch.isDrawing()) {
            batch.begin();
        }

        for (Sprite sprite : bapaoSprites) {
            sprite.draw(batch);

        }
        batch.end();

        for (int i = 0; i < bapaoSprites.length; i++) {
            bapaoSprites[i].setPosition(bapaoX[i], bapaoY[i]);
            bapaoSprites[i].setRotation(bapaoY[i]);
            if (bapaoY[i] < -50) {
                bapaoY[i] = 1210;
                bapaoX[i] = (float) (Math.random()) * (1920);

            }
            bapaoY[i] -= (bapaoSpeed[i] * delta);
        }
    }

    public void handleSaveGamePress(boolean raspUpPressed, boolean ardUpPressed) {

        boolean canPressButton = TimeUtils.millis() - select > 500;
        if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
            mainRenderScreen.setCurrentSaveGame(selectedSaveGame);
            mainRenderScreen.setCurrentScene(GameScreen.scene.map);
            saveGameMenuSwitch = false;
            Map.setSelect();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            saveGameMenuSwitch = false;
        }
    }

    public void handleSaveGameSelect(boolean raspRightPressed, boolean ardRightPressed, boolean raspLeftPressed, boolean ardLeftPressed) {


        boolean canSelectButton = TimeUtils.millis() - previousSelected > 300;
        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || raspLeftPressed || ardLeftPressed) && selectedSaveGame > 1 && canSelectButton) {
            selectedSaveGame--;
            previousSelected = TimeUtils.millis();
        } else if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || raspRightPressed || ardRightPressed) && selectedSaveGame < 3 && canSelectButton) {
            selectedSaveGame++;
            previousSelected = TimeUtils.millis();
        }
    }

    public void drawSaveGames() {
        for (int i = 0; i < 3; i++) {
            batch.begin();
            batch.draw(saveGameTexture, 200 + 560 * i, 300, 400, 600);
            normalFont.getData().setScale(1f);
            normalFont.draw(batch, "Savegame " + (i + 1), 225 + 560 * i, 880);
            try {
                if (i == 0 && mainRenderScreen.getSaveGame1().getSolarSystem().isPlayed()) {
                    titleFont.getData().setScale(1f);
                    int saveGame1Level = mainRenderScreen.getSaveGame1().getSolarSystem().getPlanetListOfDifficulty().peek().getDifficulty() + 1;
                    titleFont.draw(batch, "Level: " + saveGame1Level, 280, 700);
                    titleFont.draw(batch, "Score: " + mainRenderScreen.getSaveGame1().getScore(), 270, 550);
                } else if (i == 1 && mainRenderScreen.getSaveGame2().getSolarSystem().isPlayed()) {
                    titleFont.getData().setScale(1f);
                    int saveGame2Level = mainRenderScreen.getSaveGame2().getSolarSystem().getPlanetListOfDifficulty().peek().getDifficulty() + 1;
                    titleFont.draw(batch, "Level: " + saveGame2Level, 280 + 560 * i, 700);
                    titleFont.draw(batch, "Score: " + mainRenderScreen.getSaveGame2().getScore(), 270 + 560 * i, 550);
                } else if (i == 2 && mainRenderScreen.getSaveGame3().getSolarSystem().isPlayed()) {
                    titleFont.getData().setScale(1f);
                    int saveGame3Level = mainRenderScreen.getSaveGame3().getSolarSystem().getPlanetListOfDifficulty().peek().getDifficulty() + 1;
                    titleFont.draw(batch, "Level: " + saveGame3Level, 280 + 560 * i, 700);
                    titleFont.draw(batch, "Score: " + mainRenderScreen.getSaveGame3().getScore(), 270 + 560 * i, 550);
                } else {
                    titleFont.getData().setScale(1f);
                    titleFont.draw(batch, "EMPTY", 300 + 560 * i, 620);
                }

            } catch (Exception e) {
                e.printStackTrace();
                titleFont.getData().setScale(1f);
                titleFont.draw(batch, "EMPTY", 300 + 560 * i, 620);
            }
            batch.end();
        }
    }

    public void drawOutlines() {
        if (!shapeRenderer.isDrawing()) {
            shapeRenderer.begin();
        }
        if (!saveGameMenuSwitch) {
            TextButton selectedButton = allMainButtons.get(buttonSelect - 1);
            shapeRenderer.rect(selectedButton.getX() + mainMenuTable.getX(), selectedButton.getY() + mainMenuTable.getY(), selectedButton.getWidth(), selectedButton.getHeight());
        } else {
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(200 + 560 * (selectedSaveGame - 1), 300, 400, 600);
        }
        shapeRenderer.end();
    }

    public void handleButtonSelect(boolean raspRightPressed, boolean ardRightPressed, boolean raspLeftPressed, boolean ardLeftPressed) {


        boolean canSelectButton = TimeUtils.millis() - prevSelect > 300;

        if ((Gdx.input.isKeyPressed(Input.Keys.DOWN) || raspRightPressed || ardRightPressed) && canSelectButton) {
            if (buttonSelect < 5) {
                buttonSelect++;
            }
            prevSelect = TimeUtils.millis();
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.UP) || raspLeftPressed || ardLeftPressed) && canSelectButton) {
            if (buttonSelect > 1) {
                buttonSelect--;
            }
            prevSelect = TimeUtils.millis();
        }
    }

    public void handleButtonPress(boolean raspUpPressed, boolean ardUpPressed) {
        if (!shapeRenderer.isDrawing()) {
            shapeRenderer.begin();
        }

        boolean canPressButton = TimeUtils.millis() - switchDelay > 300;

        switch (buttonSelect) {
            case 1: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    saveGameMenuSwitch = true;
                    switchDelay = TimeUtils.millis();
                    select = TimeUtils.millis();
                }
                break;
            }
            case 2: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    mainRenderScreen.setCurrentScene(GameScreen.scene.bossFight);
                }
                break;
            }
            case 3: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    mainRenderScreen.setCurrentScene(GameScreen.scene.settingsMenu);
                    SettingsMenu.setPrevChange();
                    switchDelay = TimeUtils.millis();
                }
                break;
            }
            case 4: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    mainRenderScreen.setCurrentScene(GameScreen.scene.highScores);
                    HighScores.setPrevPress();
                }
                break;
            }
            case 5: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    dispose();
                    System.exit(0);
                }
                break;
            }
        }
        shapeRenderer.end();
    }

    public static int getSelectedSaveGame() {
        return selectedSaveGame;
    }

    public static Skin getButtonSkin() {
        return buttonSkin;
    }

    public static void setSwitchDelay(long switchDelay) {

        MainMenu.switchDelay = switchDelay;
    }
}
