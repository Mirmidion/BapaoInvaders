package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.GameScreen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class HighScores extends BaseScreen {

    private ArrayList<String> names;
    private ArrayList<Integer> scores;

    private final BitmapFont normalFont;

    private static long prevPress = 0;

    public HighScores(GameScreen gameScreen){
        this.mainRenderScreen = gameScreen;

        getHighscores();

        this.normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));

        batch = mainRenderScreen.getSpriteBatch();
        shapeRenderer = mainRenderScreen.getShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        getHighscores();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!batch.isDrawing()) {
            batch.begin();
        }


        batch.draw(mainRenderScreen.getGameBackground(), 0,0, 1920, 1080);
        drawLeaderBoard();
        batch.end();

        boolean raspUpPressed = mainRenderScreen.getRasp().is_pressed("up");
        boolean ardUpPressed = mainRenderScreen.getArduino().is_pressed("up");

        boolean canPressButton = TimeUtils.millis() - prevPress > 500;

        if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
        mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
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
    }

    public void drawLeaderBoard(){
        if (names != null) {
            normalFont.draw(batch, "Rank:",360,930);
            normalFont.draw(batch, "Name:",660,930);
            normalFont.draw(batch, "Score:",1310,930);
            for (String name : names) {
                normalFont.draw(batch, (names.indexOf(name)+1) + ".", 360, 900 - names.indexOf(name) * 30);
                normalFont.draw(batch, name, 660, 900 - names.indexOf(name) * 30);
            }
            for (int i = 0; i < scores.size(); i++){
                normalFont.draw(batch, String.valueOf((scores.get(i))), 1310, 900 - i * 30);
                //System.out.println(scores.get(i));
            }
        }
    }

    public void getHighscores() {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();
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
            statement.execute("USE highscores");
            ResultSet rs = statement.executeQuery("SELECT score, name FROM scores ORDER BY score DESC");

            while(rs.next()){
            	names.add(rs.getString(2));
            	scores.add(rs.getInt(1));
            }

            this.names = names;
            this.scores = scores;

            statement.close(); //sluit ook de resultset
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (process != null) {
            process.destroy();
        }
    }

    public static void setPrevPress() {
        HighScores.prevPress = TimeUtils.millis();
    }
}
