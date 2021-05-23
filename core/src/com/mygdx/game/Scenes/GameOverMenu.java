package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.GameScreen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

public class GameOverMenu implements Screen {

    //Main screen
    private final GameScreen mainRenderScreen;

    //Different batches
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Stage stage;

    private final ArrayList<TextButton> allButtons;

    //The selected button
    private int selectedButton = 1;

    //Time when last button has been switched
    private long prevSelect = 0;
    private static long prevPress = 0;

    //The used font
    private final BitmapFont normalFont;

    //Name of player
    private String currentName;
    private int score;

    public GameOverMenu(GameScreen gameScreen) {
        mainRenderScreen = gameScreen;

        allButtons = new ArrayList<>();

        batch = mainRenderScreen.getSpriteBatch();
        shapeRenderer = mainRenderScreen.getShapeRenderer();
        stage = new Stage();

        TextButton registerScore = new TextButton("Register score", MainMenu.getButtonSkin());
        TextButton tryAgain = new TextButton("Try Again", MainMenu.getButtonSkin());
        TextButton exit = new TextButton("exit", MainMenu.getButtonSkin());
        TextButton otherName = new TextButton("generate name", MainMenu.getButtonSkin());

        otherName();

        otherName.setBounds(710, 460, 500, 100);
        tryAgain.setBounds(785, 340, 350, 100);
        registerScore.setBounds(710, 220, 500, 100);
        exit.setBounds(860, 100, 200, 100);

        stage.addActor(otherName);
        stage.addActor(tryAgain);
        stage.addActor(registerScore);
        stage.addActor(exit);

        allButtons.add(otherName);
        allButtons.add(tryAgain);
        allButtons.add(registerScore);
        allButtons.add(exit);

        normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));

        shapeRenderer.setAutoShapeType(true);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        try {
            score = mainRenderScreen.getScore();
        } catch (Exception e) {
            score = 0;
        }

        batch.begin();
        batch.draw(mainRenderScreen.getGameBackground(), 0, 0, mainRenderScreen.getWidth(), mainRenderScreen.getHeight());
        mainRenderScreen.getTitleFont().draw(batch, "GAME OVER", 640, 750);
        normalFont.draw(batch, "Score: " + score, 850, 640);
        normalFont.draw(batch, "Name: " + currentName, 815 - currentName.length() * 13, 610);
        batch.end();

        stage.draw();

        drawOutlines();
        handleButtonPress();
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

    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        stage.dispose();
        mainRenderScreen.dispose();
    }

    public void drawOutlines(){
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(allButtons.get(selectedButton).getX(), allButtons.get(selectedButton).getY(), allButtons.get(selectedButton).getWidth(), allButtons.get(selectedButton).getHeight());
        shapeRenderer.end();
    }

    public void handleButtonSelect(){

        boolean ardUpPressed = mainRenderScreen.getArduino().is_pressed("up");
        boolean raspUpPressed = mainRenderScreen.getRasp().is_pressed("up");

        boolean canPressButton = TimeUtils.millis() - prevPress > 500;

        switch (selectedButton) {
            case 0: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    otherName();
                }
                break;
            }
            case 1: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
                }
                break;
            }
            case 2: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    saveHighScore(score, currentName);
                    mainRenderScreen.setCurrentScene(GameScreen.scene.highScores);
                }
                break;
            }
            case 3: {
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
                    dispose();
                    System.exit(0);
                }
                break;
            }
        }
    }

    public void handleButtonPress(){

        boolean raspLeftPressed = mainRenderScreen.getRasp().is_pressed("left");
        boolean raspRightPressed = mainRenderScreen.getRasp().is_pressed("right");

        boolean ardLeftPressed = mainRenderScreen.getArduino().is_pressed("left");
        boolean ardRightPressed = mainRenderScreen.getArduino().is_pressed("right");

        boolean canSelectButton = TimeUtils.millis() - prevSelect > 300;
        if ((Gdx.input.isKeyPressed(Input.Keys.UP) || raspLeftPressed || ardLeftPressed) && canSelectButton && selectedButton > 0) {
            selectedButton--;
            prevSelect = TimeUtils.millis();
        } else if ((Gdx.input.isKeyPressed(Input.Keys.DOWN) || raspRightPressed || ardRightPressed) && canSelectButton && selectedButton < 3 ) {
            selectedButton++;
            prevSelect = TimeUtils.millis();
        }
    }

    public void saveHighScore(int score, String name) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("C:\\xampp\\xampp_start.exe");
        } catch (Exception ignored) {

        }
        String url = "jdbc:mysql://localhost/highScores";
        String username = "game", password = "admin";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO scores (score, name) VALUES(" + score + ", \"" + name + "\")");
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (process != null) {
            process.destroy();
        }
        mainRenderScreen.getHighScoreScene().getHighscores();
    }

    public void otherName() {
        int randomFirst = MathUtils.random(0, WinMenu.getFirstNames().length - 1);
        int randomLast = MathUtils.random(0, WinMenu.getLastNames().length - 1);
        this.currentName = WinMenu.getFirstNames()[randomFirst] + " " + WinMenu.getLastNames()[randomLast];
    }

    public static void setPrevPress() {
        prevPress = TimeUtils.millis();
    }
}
