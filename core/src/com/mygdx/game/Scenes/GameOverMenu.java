package com.mygdx.game.Scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.GameScreen;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class GameOverMenu implements Screen {


    private GameScreen mainRenderScreen;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Stage stage;

    TextButton tryAgain;
    TextButton registerScore;
    TextButton exit;
    TextButton otherName;

    int score = 0;
    int selectedButton = 1;
    private long prevSelect = 0;

    BitmapFont normalFont;
    String currentName;

    public GameOverMenu(GameScreen renderScreen) {
        mainRenderScreen = renderScreen;
        batch = mainRenderScreen.getSpriteBatch();
        shapeRenderer = mainRenderScreen.getShapeRenderer();
        stage = new Stage();

        //Gdx.input.setInputProcessor(stage);
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
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);


        GameScreen mainRenderScreen;
        SpriteBatch batch;
        Stage stage;

        TextButton tryAgain;

        int score = 0;

        BitmapFont normalFont;
    }

//    public GameOverMenu(GameScreen renderScreen) {
//            mainRenderScreen = renderScreen;
//            batch = new SpriteBatch();
//            stage = new Stage();
//
//            Gdx.input.setInputProcessor(stage);
//
//
//            tryAgain = new TextButton("Try Again", MainMenu.getButtonSkin());
//
//            tryAgain.addListener(new ClickListener() {
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
//                }
//            });
//
//            tryAgain.setBounds(785, 300, 350, 100);
//            stage.addActor(tryAgain);
//
//            normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));
//        }

        @Override
        public void show () {

        }

        @Override
        public void render ( float delta){
            mainRenderScreen.getMusic().play();
            mainRenderScreen.getMusic2().dispose();
            mainRenderScreen.getMusic3().dispose();
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

            shapeRenderer.begin();
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.WHITE);

            if (selectedButton == 0) {
                shapeRenderer.rect(otherName.getX(), otherName.getY(), otherName.getWidth(), otherName.getHeight());
            } else if (selectedButton == 1) {
                shapeRenderer.rect(tryAgain.getX(), tryAgain.getY(), tryAgain.getWidth(), tryAgain.getHeight());
            } else if (selectedButton == 2) {
                shapeRenderer.rect(registerScore.getX(), registerScore.getY(), registerScore.getWidth(), registerScore.getHeight());
            } else if (selectedButton == 3) {
                shapeRenderer.rect(exit.getX(), exit.getY(), exit.getWidth(), exit.getHeight());
            }
            shapeRenderer.end();

            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                if (selectedButton == 0) {
                    otherName();
                } else if (selectedButton == 1) {
                    mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
                } else if (selectedButton == 2) {
                    saveHighScore(score, currentName);
                    mainRenderScreen.setCurrentScene(GameScreen.scene.highScores);
                } else if (selectedButton == 3) {
                    dispose();
                    System.exit(0);
                }

            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP) && selectedButton > 0 && TimeUtils.millis() - prevSelect > 300) {
                selectedButton--;
                prevSelect = TimeUtils.millis();
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && selectedButton < 3 && TimeUtils.millis() - prevSelect > 300) {
                selectedButton++;
                prevSelect = TimeUtils.millis();
            }
        }

        @Override
        public void resize ( int width, int height){

        }

        @Override
        public void pause () {

        }

        @Override
        public void resume () {

        }

        @Override
        public void hide () {

        }

        @Override
        public void dispose () {
            batch.dispose();
            shapeRenderer.dispose();
            stage.dispose();
            mainRenderScreen.dispose();
        }

        public void saveHighScore ( int score, String name){
            Process process = null;
            try {
                process = Runtime.getRuntime().exec("C:\\xampp\\xampp_start.exe");
            } catch (Exception e) {

            }
            String url = "jdbc:mysql://localhost/highScores";
            String username = "game", password = "admin";

            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();
                //ResultSet rs = statement.executeQuery("SELECT naam FROM medewerker");
                int gelukt = statement.executeUpdate("INSERT INTO scores (score, name) VALUES(" + score + ", \"" + name + "\")");
                //while(rs.next()){
                //	System.out.println(rs.getString(1));
                //}
                statement.close(); //sluit ook de resultset
                connection.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            if (process != null) {
                process.destroy();
            }
            mainRenderScreen.getHighScoreScene().getHighscores();
        }

        public void otherName () {
            int randomFirst = MathUtils.random(0, WinMenu.getFirstNames().length - 1);
            int randomLast = MathUtils.random(0, WinMenu.getLastNames().length - 1);
            this.currentName = WinMenu.getFirstNames()[randomFirst] + " " + WinMenu.getLastNames()[randomLast];
        }


}
