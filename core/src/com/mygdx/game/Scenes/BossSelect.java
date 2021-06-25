package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.game.GameScreen;

import java.util.ArrayList;

public class BossSelect extends BaseScreen {
    private int backgroundPosY;
    private final BitmapFont titleFont;
    private final BitmapFont normalFont;
    private static Skin buttonSkin;

    Stage stage = new Stage();

    //Main Menu Layout and Objects
    private final Table buttonTable;

    private final ArrayList<TextButton> allButtons;

    private int buttonSelect = 1;
    private float switchTimer = 0;
    private long prevSelect = 0;
    private long select;

    public BossSelect(GameScreen gameScreen) {
        batch = new SpriteBatch();
        mainRenderScreen = gameScreen;
        normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));
        titleFont = new BitmapFont(Gdx.files.internal("titleFontV2.fnt"));
        buttonSkin = new Skin(Gdx.files.internal("Skin1.json"));

        buttonTable = new Table();
        buttonTable.setPosition(250, 500);
        buttonTable.left();

        allButtons = new ArrayList<>();

        TextButton selectDifficulty = new TextButton("Select Difficulty", buttonSkin);
//        TextButton bossmode = new TextButton("BossMode", buttonSkin);
//        TextButton settings = new TextButton("Settings", buttonSkin);
        TextButton exit = new TextButton("Back", buttonSkin);

        selectDifficulty.setTransform(true);
//        bossmode.setTransform(true);
//        settings.setTransform(true);
        exit.setTransform(true);

        buttonTable.row();
        buttonTable.add(selectDifficulty).padTop(225);
        buttonTable.row();
//        mainMenuTable.add(bossmode).padTop(10);
//        mainMenuTable.row();
//        mainMenuTable.add(settings).padTop(10);
//        mainMenuTable.row();
        buttonTable.add(exit).padTop(10);

        allButtons.add(selectDifficulty);
//        allBossSelectButtons.add(bossmode);
//        allBossSelectButtons.add(settings);
//        allBossSelectButtons.add(highScores);
        allButtons.add(exit);

        stage.addActor(buttonTable);

        shapeRenderer = mainRenderScreen.getShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.enableBlending();

        batch.begin();

        batch.draw(mainRenderScreen.getGameBackground(), 0, 0, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());

        titleFont.getData().setScale(2f);
        titleFont.draw(batch, "Bossmode", 200, 800);

        batch.end();

        stage.draw();
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        drawOutlines();

        switchTimer+=delta;

        handleButtonPress();
        handleButtonSelect();

    }

    public void handleButtonPress() {
        final float SWITCH_TIME = 0.5f;

        if (!shapeRenderer.isDrawing()) {
            shapeRenderer.begin();
        }

        boolean canPressButton = switchTimer >= SWITCH_TIME;

        switch (buttonSelect) {
            case 1: {
                if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) && canPressButton) {
                    switchTimer = 0;
                    mainRenderScreen.setCurrentScene(GameScreen.scene.selectDifficulty);
                }
                break;
            }
            case 2: {
                if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) && canPressButton) {
                    mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
                }
                break;
            }
//            case 3: {
//                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
//                    mainRenderScreen.setCurrentScene(GameScreen.scene.settingsMenu);
//                    SettingsMenu.setPrevChange();
//                    switchDelay = TimeUtils.millis();
//                }
//                break;
//            }
//            case 4: {
//                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
//                    mainRenderScreen.setCurrentScene(GameScreen.scene.highScores);
//                    HighScores.setPrevPress();
//                }
//                break;
//            }
//            case 5: {
//                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
//                    dispose();
//                    System.exit(0);
//                }
//                break;
//            }
        }
        shapeRenderer.end();
    }

    public void handleButtonSelect() {
        final float SWITCH_TIME = 0.3f;

        boolean canPressButton = switchTimer >= SWITCH_TIME;

        if ((Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) && canPressButton) {
            if (buttonSelect < 2) {
                buttonSelect++;
            }
            switchTimer = 0;
        }
        if ((Gdx.input.isKeyJustPressed(Input.Keys.UP)) && canPressButton) {
            if (buttonSelect > 1) {
                buttonSelect--;
            }
            switchTimer = 0;
        }
    }

    public void drawOutlines() {
        if (!shapeRenderer.isDrawing()) {
            shapeRenderer.begin();
        }

        TextButton selectedButton = allButtons.get(buttonSelect - 1);
        shapeRenderer.rect(selectedButton.getX() + buttonTable.getX(), selectedButton.getY() + buttonTable.getY(),
                selectedButton.getWidth(), selectedButton.getHeight());

        shapeRenderer.end();
    }


    @Override
    public void show() {

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
