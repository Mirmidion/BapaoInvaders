package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

    //Other
    private final float[] bapaoY = new float[]{500, 14, 129, 1049, 280, 809, 102, 758, 640, 20, 70, 780, 420, 920, 320};
    private final float[] bapaoX = new float[]{294, 0, 498, 928, 1359, 200, 800, 500, 1060, 12, 1500, 1400, 1600, 1800, 1900};
    private final int[] bapaoSpeed = new int[]{300, 250, 200, 150, 100, 250, 450, 100, 230, 400, 500, 210, 500, 320, 340};

    //Sprites
    private final Sprite[] bapaoSprites;

    public HighScores(GameScreen gameScreen){
        this.mainRenderScreen = gameScreen;

        getHighscores();

        this.normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));

        bapaoSprites = new Sprite[15];
        for (int i = 0; i < bapaoSprites.length; i++) {
            bapaoSprites[i] = new Sprite(new Texture("Bapao1.png"));
        }

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
        renderBapaos(delta);
        drawLeaderBoard();
        batch.end();

        boolean raspUpPressed = mainRenderScreen.getRasp().is_pressed("up");
        boolean ardUpPressed = mainRenderScreen.getArduino().is_pressed("up");

        boolean canPressButton = TimeUtils.millis() - prevPress > 500;

        if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || raspUpPressed || ardUpPressed) && canPressButton) {
        mainRenderScreen.setCurrentScene(GameScreen.scene.mainMenu);
        MainMenu.setSwitchDelay(TimeUtils.millis());
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

    public void renderBapaos(float delta) {


        for (Sprite sprite : bapaoSprites) {
            sprite.draw(batch);

        }


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
