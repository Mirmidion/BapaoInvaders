package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.mygdx.game.Entities.*;
import com.mygdx.game.Scenes.Level;
import com.mygdx.game.Scenes.MainMenu;
import com.mygdx.game.Scenes.Map;
import com.mygdx.game.Scenes.loadingScreen;
import com.mygdx.game.Scenes.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


public class GameScreen implements Screen {

	//----- These are all the variables used in more than 1 scene ----//

	//Scene control

	public enum scene  {mainMenu, map, level, gameOver, win, loadingScreen, settings}

	private scene currentScene =  scene.loadingScreen;
	private int level = 0;


	//Fonts
	private BitmapFont normalFont;
	private BitmapFont titleFont;

	//Music
	private Music music;
	private Music music2;
	private Music music3;

	//Settings Menu
	private SettingsMenu settingsScene;
	private boolean settingsMenuSwitch;

	//Camera
	private OrthographicCamera camera;
	private Viewport viewport;

	//Screen width & height
	private int width = 1920;
	private int height = 1080;

	//SpriteBatches
	private SpriteBatch batch;

	//Background
	private Texture gameBackground;

	//Solar Systems
	private SolarSystem solarSystem;
	private Planet currentPlanet;

	//Paused variables
	private static boolean paused = false;
	private long pauseDelay = 0;

	//The score
	private static int score;

	//Different scenes used throughout the game
	MainMenu mainMenuScene;
	Level levelScene;
	Map mapScene;

	loadingScreen loadingScreenScene;

	GameOverMenu gameOverScene;
	WinMenu winScene;

	//FPS counter
	int framesPerSecond;
	long lastChecked;

	InputMultiplexer inputMultiplexer;


	public GameScreen () {


		//Inititializing SpriteBatches
		batch = new SpriteBatch();
		inputMultiplexer = new InputMultiplexer();
		
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

		solarSystem = new SolarSystem(width, height);

		music.setLooping(true);
		music2.setLooping(true);
		music3.setLooping(true);

		settingsScene = new SettingsMenu(this);
		mainMenuScene = new MainMenu(this);


		levelScene = new Level(this);
		mapScene = new Map(this);

		loadingScreenScene = new loadingScreen(this);

		winScene = new WinMenu(this);
		gameOverScene = new GameOverMenu(this);
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
			mapScene.render(delta);
		}

		// If in a level, draw everything of that level
		else if (currentScene == scene.level) {
			levelScene.render(delta);
		}
else if (currentScene == scene.settings){
	settingsScene.render(delta);
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

		if (TimeUtils.millis() - lastChecked >= 1000){
			framesPerSecond = Gdx.graphics.getFramesPerSecond();
			lastChecked = TimeUtils.millis();
		}

if(settingsScene.fpsCounterCheck = true) {
	batch.begin();
	normalFont.getData().setScale(0.4f);
	normalFont.draw(batch, framesPerSecond + "", 10, 1070);
	batch.end();
} else{
	batch.begin();
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





	public Music getMusic() {
		return music;
	}

	public Music getMusic2() {
		return music2;
	}

	public Music getMusic3() {
		return music3;
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



	public boolean isSettingsMenuSwitch() {
		return settingsMenuSwitch;
	}

	public void setSettingsMenuSwitch(boolean settingsMenuSwitch) {
		this.settingsMenuSwitch = settingsMenuSwitch;
	}

	public BitmapFont getTitleFont() {
		return titleFont;
	}

	public void setCurrentScene(scene currentScene) {
		this.currentScene = currentScene;
	}

	public SolarSystem getSolarSystem() {
		return solarSystem;
	}

	public void setCurrentPlanet(Planet currentPlanet) {
		this.currentPlanet = currentPlanet;
	}

	public int getLevel() {
		return level;
	}

	public static boolean isPaused() {
		return paused;
	}

	public void setSolarSystem(SolarSystem solarSystem) {
		this.solarSystem = solarSystem;
	}

	public Planet getCurrentPlanet() {
		return currentPlanet;
	}

	public static void setPaused(boolean paused) {
		GameScreen.paused = paused;
	}

	public long getPauseDelay() {
		return pauseDelay;
	}

	public void setPauseDelay(long pauseDelay) {
		this.pauseDelay = pauseDelay;
	}

	public static int getScore() {
		return score;
	}

	public static void setScore(int score) {
		GameScreen.score += score;
	}

	public void addLevel(){
		this.level++;
	}

	public scene getCurrentScene() {
		return currentScene;
	}

	public BitmapFont getNormalFont() {
		return normalFont;
	}

	public InputMultiplexer getInputMultiplexer() {
		return inputMultiplexer;
	}

	public void setMusic1Vol(float value){

		if (music.getVolume() + value <= 0){
			music.setVolume(0);
		}
		else if (music.getVolume() + value >= 1){
			music.setVolume(1);
		}
		else{
			music.setVolume(music.getVolume() + value);
		}
	}
	public void setMusic2Vol(float value){
		if (music2.getVolume() + value <= 0){
			music2.setVolume(0);
		}
		else if (music2.getVolume() + value >= 1){
			music2.setVolume(1);
		}
		else{
			music2.setVolume(music2.getVolume() + value);
		}
	}
	public void setMusic3Vol(float value){
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
}

