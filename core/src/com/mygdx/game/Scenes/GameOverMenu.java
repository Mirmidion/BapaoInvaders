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

public class GameOverMenu implements Screen {

    //Main screen
    private final GameScreen mainRenderScreen;

    //Different batches
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Stage stage;

    //All buttons
    private final TextButton tryAgain;
    private final TextButton registerScore;
    private final TextButton exit;
    private final TextButton otherName;

    //The selected button
    private int selectedButton = 1;

    //Time when last button has been switched
    private long prevSelect = 0;

    //The used font
    private final BitmapFont normalFont;

    //Name of player
    private String currentName;

    public GameOverMenu(GameScreen renderScreen) {
        mainRenderScreen = renderScreen;

        batch = mainRenderScreen.getSpriteBatch();
        shapeRenderer = mainRenderScreen.getShapeRenderer();
        stage = new Stage();

        registerScore = new TextButton("Register score", MainMenu.getButtonSkin());
        tryAgain = new TextButton("Try Again", MainMenu.getButtonSkin());
        exit = new TextButton("exit", MainMenu.getButtonSkin());
        otherName = new TextButton("generate name", MainMenu.getButtonSkin());

        otherName();

        otherName.setBounds(710, 460, 500, 100);
        tryAgain.setBounds(785, 340, 350, 100);
        registerScore.setBounds(710, 220, 500, 100);
        exit.setBounds(860, 100, 200, 100);

        stage.addActor(otherName);
        stage.addActor(tryAgain);
        stage.addActor(registerScore);
        stage.addActor(exit);

        normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));

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

        //The score the player has
        int score;
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

        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        switch (selectedButton) {
            case 0: {
                shapeRenderer.rect(otherName.getX(), otherName.getY(), otherName.getWidth(), otherName.getHeight());
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || mainRenderScreen.getRasp().is_pressed("up") || mainRenderScreen.getArduino().is_pressed("up"))) {
                    otherName();
                }
                break;
            }
            case 1: {
                shapeRenderer.rect(tryAgain.getX(), tryAgain.getY(), tryAgain.getWidth(), tryAgain.getHeight());
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || mainRenderScreen.getRasp().is_pressed("up") || mainRenderScreen.getArduino().is_pressed("up"))) {
                    mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
                }
                break;
            }
            case 2: {
                shapeRenderer.rect(registerScore.getX(), registerScore.getY(), registerScore.getWidth(), registerScore.getHeight());
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || mainRenderScreen.getRasp().is_pressed("up") || mainRenderScreen.getArduino().is_pressed("up"))) {
                    saveHighScore(score, currentName);
                    mainRenderScreen.setCurrentScene(GameScreen.scene.highScores);
                }
                break;
            }
            case 3: {
                shapeRenderer.rect(exit.getX(), exit.getY(), exit.getWidth(), exit.getHeight());
                if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || mainRenderScreen.getRasp().is_pressed("up") || mainRenderScreen.getArduino().is_pressed("up"))) {
                    dispose();
                    System.exit(0);
                }
                break;
            }
        }
        shapeRenderer.end();


        if ((Gdx.input.isKeyPressed(Input.Keys.UP) || mainRenderScreen.getRasp().is_pressed("left") || mainRenderScreen.getArduino().is_pressed("left")) && selectedButton > 0 && TimeUtils.millis() - prevSelect > 300) {
            selectedButton--;
            prevSelect = TimeUtils.millis();
        } else if ((Gdx.input.isKeyPressed(Input.Keys.DOWN) || mainRenderScreen.getRasp().is_pressed("right") || mainRenderScreen.getArduino().is_pressed("right")) && selectedButton < 3 && TimeUtils.millis() - prevSelect > 300) {
            selectedButton++;
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

    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        stage.dispose();
        mainRenderScreen.dispose();
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
}
