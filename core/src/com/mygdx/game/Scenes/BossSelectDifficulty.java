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

public class BossSelectDifficulty extends BaseScreen {
    private final BitmapFont titleFont;

    private final Stage stage = new Stage();

    //Main Menu Layout and Objects
    private final Table buttonTable;

    private final ArrayList<TextButton> allButtons;

    private int buttonSelect = 1;
    private float switchTimer = 0;

    public BossSelectDifficulty(GameScreen gameScreen) {
        batch = new SpriteBatch();
        mainRenderScreen = gameScreen;
        titleFont = new BitmapFont(Gdx.files.internal("titleFontV2.fnt"));
        Skin buttonSkin = new Skin(Gdx.files.internal("Skin1.json"));

        buttonTable = new Table();
        buttonTable.setPosition(250, 500);
        buttonTable.left();

        allButtons = new ArrayList<>();

        TextButton easy = new TextButton("Easy", buttonSkin);
        TextButton medium = new TextButton("Medium", buttonSkin);
        TextButton hard = new TextButton("Hard", buttonSkin);
        TextButton back = new TextButton("Back", buttonSkin);

        easy.setTransform(true);
        medium.setTransform(true);
        hard.setTransform(true);
        back.setTransform(true);

        buttonTable.row();
        buttonTable.add(easy).padTop(225);
        buttonTable.row();
        buttonTable.add(medium).padTop(10);
        buttonTable.row();
        buttonTable.add(hard).padTop(10);
        buttonTable.row();
        buttonTable.add(back).padTop(10);

        allButtons.add(easy);
        allButtons.add(medium);
        allButtons.add(hard);
        allButtons.add(back);

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

        switchTimer += delta;

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
                    //easy
                    mainRenderScreen.setBossDifficulty(GameScreen.difficulty.EASY);
                    mainRenderScreen.setCurrentScene(GameScreen.scene.bossFight);
                    switchTimer = 0;
                }
                break;
            }
            case 2: {
                if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) && canPressButton) {
                    //medium
                    mainRenderScreen.setBossDifficulty(GameScreen.difficulty.MEDIUM);
                    mainRenderScreen.setCurrentScene(GameScreen.scene.bossFight);
                    switchTimer = 0;
                }
                break;
            }
            case 3: {
                if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) && canPressButton) {
                    //hard
                    mainRenderScreen.setBossDifficulty(GameScreen.difficulty.HARD);
                    mainRenderScreen.setCurrentScene(GameScreen.scene.bossFight);
                    switchTimer = 0;
                }
                break;
            }
            case 4: {
                if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) && canPressButton) {
                    mainRenderScreen.setCurrentScene(GameScreen.scene.bossSelect);
                }
                break;
            }
        }
        shapeRenderer.end();
    }

    public void handleButtonSelect() {
        final float SWITCH_TIME = 0.3f;

        boolean canPressButton = switchTimer >= SWITCH_TIME;

        if ((Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) && canPressButton) {
            if (buttonSelect < 4) {
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