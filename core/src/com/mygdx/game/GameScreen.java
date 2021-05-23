package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.mygdx.game.Controllers.Arduino;
import com.mygdx.game.Controllers.raspController.RaspController;
import com.mygdx.game.Entities.*;

import com.mygdx.game.Enums.SaveGames;
import com.mygdx.game.Scenes.Level;
import com.mygdx.game.Scenes.MainMenu;
import com.mygdx.game.Scenes.Map;
import com.mygdx.game.Scenes.LoadingScreen;

import com.mygdx.game.SaveSystem.SerializeManager;

import com.mygdx.game.Scenes.*;

import java.util.ArrayList;
import java.util.LinkedList;


public class GameScreen implements Screen {

	//----- These are all the variables used in more than 1 scene ----//

	//Scene control
	public enum scene  {mainMenu, map, level, gameOver, win, loadingScreen, highScores, settingsMenu}
	private scene currentScene =  scene.loadingScreen;

	private static boolean fpsCounterCheck = false;

	//Fonts
	private final BitmapFont normalFont;
	private final BitmapFont titleFont;

	//Music
	private final Music music;
	private final Music music2;
	private final Music music3;



	//Camera
	private final OrthographicCamera camera;
	private final Viewport viewport;

	//Screen width & height
	private final int width = 1920;
	private final int height = 1080;

	//SpriteBatches
	private final SpriteBatch batch;

	//Background
	private final Texture gameBackground;

	//Solar Systems
	private Planet currentPlanet;



	private final SpriteBatch spriteBatch;
	private final ShapeRenderer shapeRenderer;

	private final SerializeManager serializeManager = new SerializeManager();

	private final SaveGames saveGame1 = SaveGames.saveGame1;
	private final SaveGames saveGame2 = SaveGames.saveGame2;
	private final SaveGames saveGame3 = SaveGames.saveGame3;
	private SaveGames currentSaveGame;

	//Different scenes used throughout the game
	private final MainMenu mainMenuScene;
	private final Level levelScene;
	private final Map mapScene;
	private final SettingsMenu settingsMenuScene;
	private final LoadingScreen loadingScreenScene;

	private final GameOverMenu gameOverScene;
	private final WinMenu winScene;
	private final HighScores highScoreScene;

	//FPS counter
	private int framesPerSecond;
	private long lastChecked;

	private final RaspController rasp;
	private final Arduino arduino;

	

	public GameScreen (){
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		rasp = new RaspController("192.168.2.4");
		arduino = new Arduino();

		//Inititializing SpriteBatches
		batch = new SpriteBatch();

		//Initializing Textures
		gameBackground = new Texture("gameBackground.png");

		music = Gdx.audio.newMusic(Gdx.files.internal("Theme.mp3"));
		music2 = Gdx.audio.newMusic(Gdx.files.internal("Theme2.mp3"));
		music3 = Gdx.audio.newMusic(Gdx.files.internal("Theme3.mp3"));

		//Initializing Fonts
		normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));
		titleFont = new BitmapFont(Gdx.files.internal("titleFontV2.fnt"));
		titleFont.getData().setScale(2);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);

		viewport = new StretchViewport(1920, 1080, camera);

		music.setLooping(true);
		music2.setLooping(true);
		music3.setLooping(true);

		mainMenuScene = new MainMenu(this);
		levelScene = new Level(this);
		mapScene = new Map(this);

		loadingScreenScene = new LoadingScreen(this);
		settingsMenuScene = new SettingsMenu(this);
		winScene = new WinMenu(this);
		gameOverScene = new GameOverMenu(this);
		highScoreScene = new HighScores(this);
	}



	@Override
	public void render (float delta) {
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glDisable(GL20.GL_BLEND);

		if (currentScene == scene.mainMenu) {
			mainMenuScene.render(delta);
		}

		// If on the map, draw the solar system
		else if (currentScene == scene.map) {
			try {
				mapScene.render(delta);
			}
			catch (NullPointerException e){
				currentScene = scene.win;
			}
		}

		else if (currentScene == scene.settingsMenu){
			settingsMenuScene.render(delta);
		}

		// If in a level, draw everything of that level
		else if (currentScene == scene.level) {
			levelScene.render(delta);
		}


		else if (currentScene == scene.loadingScreen){
			loadingScreenScene.render(delta);
		}

		else if (currentScene == scene.gameOver) {
			gameOverScene.render(delta);
		}

		else if (currentScene == scene.win) {
			winScene.render(delta);
		}

		else if (currentScene == scene.highScores){
			highScoreScene.render(delta);
		}

		if (TimeUtils.millis() - lastChecked >= 1000){
			framesPerSecond = Gdx.graphics.getFramesPerSecond();
			lastChecked = TimeUtils.millis();
		}

		if (fpsCounterCheck) {
			batch.begin();
			normalFont.getData().setScale(0.2f);
			normalFont.draw(batch, framesPerSecond + "", 10, 1070);
			batch.end();
		}

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		batch.setProjectionMatrix(camera.combined);

	}

	@Override
	public void show() {

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
	public void dispose () {
		batch.dispose();
		music.dispose();
		music2.dispose();
		music3.dispose();
	}

	public void saveSaveGame(int saveGame){
		try {
			if (saveGame == 1) {
				serializeManager.save(currentSaveGame.getSolarSystem().getPlanets(), "SaveGame1_", "Planets");
				serializeManager.save(currentSaveGame.getSolarSystem().getPlanetListOfDifficulty(), "SaveGame1_", "PlanetDifficulty");
				serializeManager.save(currentSaveGame.getScore(), "SaveGame1_", "Score");
				serializeManager.save(currentSaveGame.getSolarSystem().isPlayed(), "SaveGame1_", "Played");
			} else if (saveGame == 2) {
				serializeManager.save(currentSaveGame.getSolarSystem().getPlanets(), "SaveGame2_", "Planets");
				serializeManager.save(currentSaveGame.getSolarSystem().getPlanetListOfDifficulty(), "SaveGame2_", "PlanetDifficulty");
				serializeManager.save(currentSaveGame.getScore(), "SaveGame2_", "Score");
				serializeManager.save(currentSaveGame.getSolarSystem().isPlayed(), "SaveGame2_", "Played");
			} else if (saveGame == 3) {
				serializeManager.save(currentSaveGame.getSolarSystem().getPlanets(), "SaveGame3_", "Planets");
				serializeManager.save(currentSaveGame.getSolarSystem().getPlanetListOfDifficulty(), "SaveGame3_", "PlanetDifficulty");
				serializeManager.save(currentSaveGame.getScore(), "SaveGame3_", "Score");
				serializeManager.save(currentSaveGame.getSolarSystem().isPlayed(), "SaveGame3_", "Played");
			}
		}
		catch(Exception ignored){

		}
	}

	public void setCurrentSaveGame(int selected) {
		if (selected == 1){
			currentSaveGame = saveGame1;
		}
		else if (selected == 2){
			currentSaveGame = saveGame2;
		}
		else if (selected == 3){
			currentSaveGame = saveGame3;
		}
		currentSaveGame.getSolarSystem().setPlayed(true);
		saveSaveGame(selected);
	}

	public void newSaveGame(){
		this.currentSaveGame.setSolarSystem(new SolarSystem(1920,1080));
		this.currentSaveGame.setScore(0);
		if (currentSaveGame == saveGame1){
			saveSaveGame(1);
		}
		else if (currentSaveGame == saveGame2){
			saveSaveGame(2);
		}
		else if (currentSaveGame == saveGame3){
			saveSaveGame(3);
		}
	}

	public void setMusicVol(float value){

		if (music.getVolume() + value <= 0){
			music.setVolume(0);
		}
		else if (music.getVolume() + value >= 1){
			music.setVolume(1);
		}
		else{
			music.setVolume(music.getVolume() + value);
		}

		if (music2.getVolume() + value <= 0){
			music2.setVolume(0);
		}
		else if (music2.getVolume() + value >= 1){
			music2.setVolume(1);
		}
		else{
			music2.setVolume(music2.getVolume() + value);
		}

		if (music3.getVolume() + value <= 0){
			music3.setVolume(0);
		}
		else if (music3.getVolume() + value >= 1){
			music3.setVolume(1);
		}
		else{
			music3.setVolume(music3.getVolume() + value);
		}
	}

	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}

	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}

	public HighScores getHighScoreScene() {
		return highScoreScene;
	}

	public boolean isFirstLoad() {
		boolean firstLoad = true;
		return firstLoad;
	}

	public SolarSystem getSaveGame1SolarSystem() {
		return saveGame1.getSolarSystem();
	}

	public SolarSystem getSaveGame2SolarSystem() {
		return saveGame2.getSolarSystem();
	}

	public SolarSystem getSaveGame3SolarSystem() {
		return saveGame3.getSolarSystem();
	}

	public int getSaveGame1Score() {
		return saveGame1.getScore();
	}
	public int getSaveGame2Score() {
		return saveGame2.getScore();
	}
	public int getSaveGame3Score() {
		return saveGame3.getScore();
	}

	public static void setFpsCounterCheck(boolean fpsCounterCheck) {
		GameScreen.fpsCounterCheck = fpsCounterCheck;
	}

	public Arduino getArduino() {
		return arduino;
	}

	public RaspController getRasp() {
		return rasp;
	}

	public Music getMusic() {
		return music;
	}

	public Music getMusic2() {
		return music2;
	}

	public Music getMusic3() {
		return music3;
	}

	public void setPlayingMusic(int whichOne){
		if (whichOne == 1){
			music.play();
			music2.dispose();
			music3.dispose();
		}
		else if (whichOne == 2){
			music.dispose();
			music2.play();
			music3.dispose();
		}
		else if (whichOne == 3){
			music.dispose();
			music2.dispose();
			music3.play();
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Texture getGameBackground() {
		return gameBackground;
	}

	public BitmapFont getTitleFont() {
		return titleFont;
	}

	public void setCurrentScene(scene currentScene) {
		this.currentScene = currentScene;
	}

	public void setCurrentPlanet(Planet currentPlanet) {
		this.currentPlanet = currentPlanet;
	}

	public Planet getCurrentPlanet() {
		return currentPlanet;
	}

	public BitmapFont getNormalFont() {
		return normalFont;
	}

	public SolarSystem getSolarSystem() {
		return currentSaveGame.getSolarSystem();
	}

	public int getScore(){
		return currentSaveGame.getScore();
	}

	public void setScore(int score){
		this.currentSaveGame.setScore(score);
	}

	public void setSolarSystem(SolarSystem solarSystem) {
		this.currentSaveGame.setSolarSystem(solarSystem);
	}
}

