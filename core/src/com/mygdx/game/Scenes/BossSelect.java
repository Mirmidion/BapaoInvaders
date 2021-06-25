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
    private final BitmapFont titleFont;

    private final Stage stage = new Stage();

    //Main Menu Layout and Objects
    private final Table buttonTable;

    private final ArrayList<TextButton> allButtons;

    private int buttonSelect = 1;
    private float switchTimer = 0;

    public BossSelect(GameScreen gameScreen) {
        batch = new SpriteBatch();
        mainRenderScreen = gameScreen;
        titleFont = new BitmapFont(Gdx.files.internal("titleFontV2.fnt"));
        Skin buttonSkin = new Skin(Gdx.files.internal("Skin1.json"));

        buttonTable = new Table();
        buttonTable.setPosition(250, 500);
        buttonTable.left();

        allButtons = new ArrayList<>();

        TextButton selectDifficulty = new TextButton("Select Difficulty", buttonSkin);
        TextButton exit = new TextButton("Back", buttonSkin);

        selectDifficulty.setTransform(true);
        exit.setTransform(true);

        buttonTable.row();
        buttonTable.add(selectDifficulty).padTop(225);
        buttonTable.row();
        buttonTable.add(exit).padTop(10);

        allButtons.add(selectDifficulty);
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
